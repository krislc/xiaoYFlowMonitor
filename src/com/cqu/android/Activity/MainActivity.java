

package com.cqu.android.Activity;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.cqu.android.bean.Api;
import com.cqu.android.bean.Api.DroidApp;

public class MainActivity extends Activity implements OnCheckedChangeListener,
		OnClickListener {

	// Menu options
	// 防火墙禁用
	private static final int MENU_DISABLE = 0;
	//日志禁用
	private static final int MENU_TOGGLELOG = 1;
	//保存规则
//	private static final int MENU_APPLY = 2;
	//退出
//	private static final int MENU_EXIT = 3;
	//当月流量
//	private static final int MENU_TRAFFIC = 4;
	//帮助
//	private static final int MENU_HELP = 5;
	//显示日志
	private static final int MENU_SHOWLOG = 6;
	//显示规则
//	private static final int MENU_SHOWRULES = 7;
	 //清除日志
	private static final int MENU_CLEARLOG = 8;
	//设置密码
//	private static final int MENU_SETPWD = 9;

	/** progress dialog instance */
	private ListView listview;
	public ActivityManager am;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		//检查参数文件
		checkPreferences();
		setContentView(R.layout.main_wall);
		//黑白名单选择  监听器
		this.findViewById(R.id.label_mode).setOnClickListener(this);
        //确认2进制文件已在缓存目录中安装
		Api.assertBinaries(this, true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Force re-loading the application list
		//强制进行重新载入应用列表
		Log.d("联网监控", "onStart() - Forcing APP list reload!");
		Api.applications = null;
	}
    //再次进入Activity是检测是否设置了密码
	@Override
	protected void onResume() {
		super.onResume();
		if (this.listview == null) {
			this.listview = (ListView) this.findViewById(R.id.listview);
		}
		refreshHeader();
		//检查是否设置密码
//		final String pwd = getSharedPreferences(Api.PREFS_NAME, 0).getString(
//				Api.PREF_PASSWORD, "");
//		if (pwd.length() == 0) {
			// No password lock----直接调用方法加载应用程序列表
			showOrLoadApplications();
//		} else {
			// Check the password
//			requestPassword(pwd);
//		}
	}
    //Activity暂停  失去显示  将listview清空
	@Override
	protected void onPause() {
		super.onPause();
		this.listview.setAdapter(null);
	}

	/**
	 * Check if the stored preferences are OK
	 * 查看配置文件看其是否正确
	 */
	private void checkPreferences() {
		/**
	     * File creation mode: the default mode, where the created file can only
	     * be accessed by the calling application (or all applications sharing the
	     * same user ID).
	     * @see #MODE_WORLD_READABLE
	     * @see #MODE_WORLD_WRITEABLE
	     */
	    //public static final int MODE_PRIVATE = 0x0000;
	    /**
	     * Retrieve and hold the contents of the preferences file 'name', returning
	     * a SharedPreferences through which you can retrieve and modify its
	     * values.  Only one instance of the SharedPreferences object is returned
	     * to any callers for the same name, meaning they will see each other's
	     * edits as soon as they are made.
	     *
	     * @param name Desired preferences file. If a preferences file by this name
	     * does not exist, it will be created when you retrieve an
	     * editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).*/
		final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
		final Editor editor = prefs.edit();
		boolean changed = false;
		 /**
	     * Retrieve a String value from the preferences.
	     * 
	     * @param key The name of the preference to retrieve.
	     * @param defValue Value to return if this preference does not exist.
	     * 
	     * @return Returns the preference value if it exists, or defValue.  Throws
	     * ClassCastException if there is a preference with this name that is not
	     * a String.
	     * 
	     * @throws ClassCastException
	     */
		if (prefs.getString(Api.PREF_MODE, "").length() == 0) {
			//初试状态为白名单
			editor.putString(Api.PREF_MODE, Api.MODE_WHITELIST);
			changed = true;
		}
		/* delete the old preference names 
		 * 删除允许运行软件的UID
		 * */
		if (prefs.contains("AllowedUids")) {
			editor.remove("AllowedUids");
			changed = true;
		}
		if (prefs.contains("Interfaces")) {
			editor.remove("Interfaces");
			changed = true;
		}
		//参数文件发生变化则执行保存变化
		if (changed)
			editor.commit();
	}

	/**
	 * Refresh informative header
	 */
	private void refreshHeader() {
		final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
		final String mode = prefs.getString(Api.PREF_MODE, Api.MODE_WHITELIST);
		final TextView labelmode = (TextView) this
				.findViewById(R.id.label_mode);
		final Resources res = getResources();
		int resid = (mode.equals(Api.MODE_WHITELIST) ? R.string.mode_whitelist
				: R.string.mode_blacklist);
		//更换模式label
		labelmode.setText(res.getString(R.string.mode_header,
				res.getString(resid)));
		//判断防火墙模式是开启还是关闭的
		resid = (Api.isEnabled(this) ? R.string.title_enabled
				: R.string.title_disabled);
		//显示开启关闭状态并标出版本
		setTitle(res.getString(resid, Api.VERSION));
	}

	/**
	 * Displays a dialog box to select the operation mode (black or white list)
	 * 黑白名单选择
	 */
	private void selectMode() {
		/**
		 * Class for accessing an application's resources.  This sits on top of the
		 * asset manager of the application (accessible through {@link #getAssets}) and
		 * provides a high-level API for getting typed data from the assets.
		 */
		final Resources res = getResources();
		//模式更换提示框
		 /**
         * Set a list of items to be displayed in the dialog as the content, you will be notified of the
         * selected item via the supplied listener.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         *
            public Builder setItems(CharSequence[] items, final OnClickListener listener) {
            P.mItems = items;
            P.mOnClickListener = listener;
            return this;
        }*/
		new AlertDialog.Builder(this)
				.setItems(
						new String[] { res.getString(R.string.mode_whitelist),
								res.getString(R.string.mode_blacklist) },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								final String mode = (which == 0 ? Api.MODE_WHITELIST
										: Api.MODE_BLACKLIST);
								final Editor editor = getSharedPreferences(
										Api.PREFS_NAME, 0).edit();
								editor.putString(Api.PREF_MODE, mode);
								editor.commit();
								//更新视图头
								refreshHeader();
							}
						}).setTitle("Select mode:").show();
	}

	/**
	 * Set a new password lock
	 * 
	 * @param pwd
	 *            new password (empty to remove the lock)
	 */
/*	private void setPassword(String pwd) {
		final Resources res = getResources();
		final Editor editor = getSharedPreferences(Api.PREFS_NAME, 0).edit();
		editor.putString(Api.PREF_PASSWORD, pwd);
		String msg;
		if (editor.commit()) {
			if (pwd.length() > 0) {
				msg = res.getString(R.string.passdefined);
			} else {
				msg = res.getString(R.string.passremoved);
			}
		} else {
			msg = res.getString(R.string.passerror);
		}
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}*/

	/**
	 * Request the password lock before displayed the main screen.
	 */
/*	private void requestPassword(final String pwd) {
		new PassDialog(this, false, new android.os.Handler.Callback() {
			public boolean handleMessage(Message msg) {
				if (msg.obj == null) {
					MainActivity.this.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					return false;
				}
				if (!pwd.equals(msg.obj)) {
					requestPassword(pwd);
					return false;
				}
				// Password correct
				showOrLoadApplications();
				return false;
			}
		}).show();
	}*/

	/**
	 * Toggle iptables log enabled/disabled
	 */
	private void toggleLogEnabled() {
		final SharedPreferences prefs = getSharedPreferences(Api.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(Api.PREF_LOGENABLED, false);
		final Editor editor = prefs.edit();
		//反设置参数文件
		editor.putBoolean(Api.PREF_LOGENABLED, enabled);
		editor.commit();
		if (Api.isEnabled(this)) {
			/**
			 * Purge and re-add all saved rules (not in-memory ones). This is much
			 * faster than just calling "applyIptablesRules", since it don't need to
			 * read installed applications.
			 * 
			 * @param ctx
			 *            application context (mandatory)
			 * @param showErrors
			 *            indicates if errors should be alerted
			 */
			Api.applySavedIptablesRules(this, true);
		}
		Toast.makeText(
				MainActivity.this,
				(enabled ? R.string.log_was_enabled : R.string.log_was_disabled),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * If the applications are cached, just show them, otherwise load and show
	 */
	public void showOrLoadApplications() {
		final Resources res = getResources();
		if (Api.applications == null) {
			// The applications are not cached.. so lets display the progress
			// dialog
			//public static ProgressDialog show(Context context, CharSequence title,
            //CharSequence message, boolean indeterminate)
			//不明确就是滚动条的当前值自动在最小到最大值之间来回移动，形成这样一个动画效果，
			//这个只是告诉别人“我正在工作”，但不能提示工作进度到哪个阶段。
			//主要是在进行一些无法确定操作时间的任务时作为提示。而“明确”就是根据你的进度可以设置现在的进度值。
			
			//显示进度条
			final ProgressDialog progress = ProgressDialog.show(this,
					res.getString(R.string.working),
					res.getString(R.string.reading_apps), true);
			//界面更新
			final Handler handler = new Handler() {
				/**
			     * Subclasses must implement this to receive messages.
			     */
				public void handleMessage(Message msg) {
					try {
						//取消进度条
						progress.dismiss();
					} catch (Exception ex) {
					}
					//显示列表
					showApplications();
				}
			};
			//开启新线程
			new Thread() {
				public void run() {
					/**
					 * @param ctx
					 *            application context (mandatory)
					 * @return a list of applications
					 */
					//public static DroidApp[] getApps(Context ctx)
					//加载应用列表
					Api.getApps(MainActivity.this);
					//启动界面更新handler
					handler.sendEmptyMessage(0);
				}
			}.start();
		} else {
			// the applications are cached, just show the list
			showApplications();
		}
	}

	/**
	 * Show the list of applications
	 */
	private void showApplications() {
		//新线程以加载过，此处直接获得应用程序列表
		final DroidApp[] apps = Api.getApps(this);
		// Sort applications - selected first, then alphabetically
		//重排应用列表  选中的优先
		/**
	     * Sorts the specified array using the specified {@code Comparator}. All elements
	     * must be comparable to each other without a {@code ClassCastException} being thrown.
	     *
	     * @param array
	     *            the {@code Object} array to be sorted.
	     * @param comparator
	     *            the {@code Comparator}.
	     * @throws ClassCastException
	     *                if elements in the array cannot be compared to each other
	     *                using the {@code Comparator}.
	     */
		Arrays.sort(apps, new Comparator<DroidApp>() {
			@Override
			//按照返回值 排定两个元素的顺序
			public int compare(DroidApp o1, DroidApp o2) {
				//如果两个元素都是被选中的  按名字母顺序排列  
				if ((o1.selected_wifi | o1.selected_3g) == (o2.selected_wifi | o2.selected_3g)) {
					 /**
				     * A comparator ignoring the case of the characters.
				     */
					return String.CASE_INSENSITIVE_ORDER.compare(o1.names[0],
							o2.names[0]);
				}
				//若二者中只有一个被选中   01排在前边  
				if (o1.selected_wifi || o1.selected_3g) 
					return -1;
				//二者均未被选中 
				return 1;
			}
		});
		final LayoutInflater inflater = getLayoutInflater();
		/**
	     * Constructor
	     *
	     * @param context The current context.
	     * @param resource The resource ID for a layout file containing a layout to use when
	     *                 instantiating views.
	     * @param textViewResourceId The id of the TextView within the layout resource to be populated
	     * @param objects The objects to represent in the ListView.
	     *
	    public ArrayAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
	       init(context, resource, textViewResourceId, Arrays.asList(objects));
	    }*/

		final ListAdapter adapter = new ArrayAdapter<DroidApp>(this,
				R.layout.listitem, R.id.app_text, apps) {
			@Override
			//间接调用createviewfromresource()
			public View getView(int position, View convertView, ViewGroup parent)
			{
				//自定义的类
				ListEntry entry;
				if (convertView == null) {
					// Inflate a new view
					convertView = inflater.inflate(R.layout.listitem, parent,
							false);
					entry = new ListEntry();
					entry.box_wifi = (CheckBox) convertView
							.findViewById(R.id.itemcheck_wifi);
					entry.box_3g = (CheckBox) convertView
							.findViewById(R.id.itemcheck_3g);
					entry.app_Icon=(ImageView) convertView.findViewById(R.id.item_icon);
					entry.app_text = (TextView) convertView
							.findViewById(R.id.app_text);
					entry.upload = (TextView) convertView
							.findViewById(R.id.upload);
					entry.download = (TextView) convertView
							.findViewById(R.id.download);
					/**
				     * Sets the tag associated with this view. A tag can be used to mark
				     * a view in its hierarchy and does not have to be unique within the
				     * hierarchy. Tags can also be used to store data within a view without
				     * resorting to another data structure.
				     * public void setTag(final Object tag)*/
					
					convertView.setTag(entry);
					entry.box_wifi
							.setOnCheckedChangeListener(MainActivity.this);
					entry.box_3g.setOnCheckedChangeListener(MainActivity.this);
				} else {
					// Convert an existing view
					entry = (ListEntry) convertView.getTag();
				}
				
				final DroidApp app = apps[position];
				 
				
				//entry.app_text.setText(app.toString());
				//改为不加入uid
				entry.app_text.setText(app.names[0]);
				//添加并设置颜色
				convertAndSetColor(TrafficStats.getUidTxBytes(app.uid),
						entry.upload);
				convertAndSetColor(TrafficStats.getUidRxBytes(app.uid),
						entry.download);
				entry.app_Icon.setImageDrawable(app.icon);
				
				final CheckBox box_wifi = entry.box_wifi;
				box_wifi.setTag(app);
				box_wifi.setChecked(app.selected_wifi);
				final CheckBox box_3g = entry.box_3g;
				box_3g.setTag(app);
				box_3g.setChecked(app.selected_3g);
				 // 添加点击事件处理机制，以支持弹出菜单
	            convertView.setOnClickListener(new OnClickListener() {

	                @Override
	                public void onClick(View view) {
	                    MiscUtil.getTaskMenuDialog(MainActivity.this,app).show();
	                }
	                
	            });
				return convertView;
			}

			/*
			 * 添加应用程序流量信息并设置颜色		 */
			private void convertAndSetColor(long count, TextView text) {
				String value = null;
				//不支持统计
				if (count == -1) {
					//value = "N/A ";
					value="    0";
					text.setText(value);
					// gray55  =   0xFF919191,
					text.setTextColor(0xff919191);
					return;
				} else {
					//转换单位
					value = unitHandler(count);
				}
				text.setText(value);
				 //red =   0xFF0000FF
				text.setTextColor(0xff0000ff);
			}
		};
		
		
		//adapter外部
		this.listview.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//public MenuItem add(int groupId, int itemId, int order, int titleRes);
		/* <b>expanded menus</b> (only available if six or more menu items are visible,
		 * reached via the 'More' item in the icon menu) do not show item icons, and
		 * item check marks are discouraged.
		 * <li><b>Sub menus</b>: Do not support item icons, or nested sub menus.
		 * </ol>
		 */
		//调用系统图标
		menu.add(0, MENU_DISABLE, 0, R.string.fw_enabled).setIcon(
				android.R.drawable.button_onoff_indicator_on);
		menu.add(0, MENU_TOGGLELOG, 0, R.string.log_enabled).setIcon(
				android.R.drawable.button_onoff_indicator_on);
		//自己的图标
		/*menu.add(0, MENU_APPLY, 0, R.string.applyrules).setIcon(
				R.drawable.apply);
		//系统图标
		menu.add(0, MENU_EXIT, 0, R.string.exit).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(0, MENU_TRAFFIC, 0, R.string.traffic).setIcon(
				android.R.drawable.ic_menu_info_details);
		menu.add(0, MENU_HELP, 0, R.string.help).setIcon(
				android.R.drawable.ic_menu_help);*/
		
		//自己图标
		menu.add(0, MENU_SHOWLOG, 0, R.string.show_log)
				.setIcon(R.drawable.show);
		/*menu.add(0, MENU_SHOWRULES, 0, R.string.showrules).setIcon(
				R.drawable.show);*/
		//系统图标
		menu.add(0, MENU_CLEARLOG, 0, R.string.clear_log).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		/*menu.add(0, MENU_SETPWD, 0, R.string.setpwd).setIcon(
				android.R.drawable.ic_lock_lock);*/

		return true;
	}
    //菜单逻辑规则
	/*onCreateOptionsMenu：
	只会调用一次，他只会在Menu显示之前去调用一次，之后就不会在去调用。

	onPrepareOptionsMenu：

	onPrepareOptionsMenu是每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。*/
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final MenuItem item_onoff = menu.getItem(MENU_DISABLE);
		//final MenuItem item_apply = menu.getItem(MENU_APPLY);
		//查看防火墙是否开启
		final boolean enabled = Api.isEnabled(this);
		if (enabled) {
			item_onoff.setIcon(android.R.drawable.button_onoff_indicator_on);
			//防火墙已开启
			item_onoff.setTitle(R.string.fw_enabled);
			//应用规则
			//item_apply.setTitle(R.string.applyrules);
		} else {
			item_onoff.setIcon(android.R.drawable.button_onoff_indicator_off);
			item_onoff.setTitle(R.string.fw_disabled);
			//item_apply.setTitle(R.string.saverules);
		}
		//日志
		final MenuItem item_log = menu.getItem(MENU_TOGGLELOG);
		//查看参数配置文件获取系统日志功能是否开启
		final boolean logenabled = getSharedPreferences(Api.PREFS_NAME, 0)
				.getBoolean(Api.PREF_LOGENABLED, false);
		if (logenabled) {
			//日志已启用
			item_log.setIcon(android.R.drawable.button_onoff_indicator_on);
			item_log.setTitle(R.string.log_enabled);
		} else {
			//日志已禁用
			item_log.setIcon(android.R.drawable.button_onoff_indicator_off);
			item_log.setTitle(R.string.log_disabled);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DISABLE:
			//应用或禁用防火墙----广播通知statusWidget提供数据支持
			disableOrEnable();
			return true;
		case MENU_TOGGLELOG:
			// 启用或禁用日志
			toggleLogEnabled();
			return true;
		/*case MENU_APPLY:
			// 应用或保存规则
			applyOrSaveRules();
			return true;
		case MENU_EXIT:
			//结束Activity并推出系统
			finish();
			System.exit(0);
			return true;
		case MENU_TRAFFIC:
			//显示流量
			showTrafficDialog();
			return true;
		case MENU_HELP:
			//帮助
			new HelpDialog(this).show();
			return true;
		case MENU_SETPWD:
			//设置密码
			setPassword();
			return true;*/
		case MENU_SHOWLOG:
			showLog();
			return true;
		/*case MENU_SHOWRULES:
			showRules();
			return true;*/
		case MENU_CLEARLOG:
			clearLog();
			return true;
		}
		return false;
	}

	/*private void showTrafficDialog() {
		String message = null;
		long[] trafficArray = Api.showTraffic(this);
		if (trafficArray != null) {
			message = "2G/3G总流量:" + unitHandler(trafficArray[0]) + "\n上行流量:"
					+ unitHandler(trafficArray[1]) + "\n下行流量:"
					+ unitHandler(trafficArray[2]);
		} else {
			message = "无流量信息";
		}
		Api.alert(this, message);
	}*/
   //流量单位转换
	private String unitHandler(long count) {
		String value = null;
		long temp = count;
		float floatnum = count;
		if ((temp = temp / 1000) < 1) {
			value = count + "B";
		} else if ((floatnum = (float)temp / 1000) < 1) {
			value = temp + "KB";
		} else {
			DecimalFormat format = new DecimalFormat("0.#");
			value = format.format(floatnum) + "MB";
		}
		return value;
	}

	/**
	 * Enables or disables the firewall
	 */
	private void disableOrEnable() {
		final boolean enabled = !Api.isEnabled(this);
		Log.d("小Y", "Changing enabled status to: " + enabled);
		//点击之后  设置防火墙为反-----statusWidget提供数据支持
		Api.setEnabled(this, enabled);
		if (enabled) {
			//上方显示         应用并保存规则
			applyOrSaveRules();
		} else {
			//清除规则
			purgeRules();
		}
		//更新界面头
		refreshHeader();
	}

	/**
	 * Set a new lock password
	 */
//	private void setPassword() {
		/**
		 * Creates the dialog
		 * @param context context
		 * @param setting if true, indicates that we are setting a new password instead of requesting it.
		 * @param callback callback to receive the password entered (null if canceled)
		 */
	      //public PassDialog(Context context, boolean setting, Callback callback)
//	   new PassDialog(this, true, new android.os.Handler.Callback() {
//	   public boolean handleMessage(Message msg) {
				/**obj----
			     * An arbitrary object to send to the recipient.  */
//				if (msg.obj != null) {
//					setPassword((String) msg.obj);
//				}
//				return false;
//			}
//		}).show();
//	}

	/**
	 * Show iptable rules on a dialog
	 */
/*	private void showRules() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this,
				res.getString(R.string.working),
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (!Api.hasRootAccess(MainActivity.this, true))
					return;
				Api.showIptablesRules(MainActivity.this);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}*/

	/**
	 * Show logs on a dialog
	 */
	private void showLog() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this,
				res.getString(R.string.working),
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				/**
				 * Display logs*/
				Api.showLog(MainActivity.this);
			}
		};
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	/**
	 * Clear logs
	 */
	private void clearLog() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this,
				res.getString(R.string.working),
				res.getString(R.string.please_wait), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (!Api.hasRootAccess(MainActivity.this, true))
					return;
				if (Api.clearLog(MainActivity.this)) {
					Toast.makeText(MainActivity.this, R.string.log_cleared,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	/**
	 * Apply or save iptable rules, showing a visual indication
	 */
	private void applyOrSaveRules() {
		final Resources res = getResources();
		final boolean enabled = Api.isEnabled(this);
		//public static ProgressDialog show(Context context, CharSequence title,
	    //      CharSequence message, boolean indeterminate)
		// title 工作中...  message 保存规则
		final ProgressDialog progress = ProgressDialog.show(this, res
				.getString(R.string.working), res
				.getString(enabled ? R.string.applying_rules
						: R.string.saving_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					//移除进度条
					progress.dismiss();
				} catch (Exception ex) {
				}
				if (enabled) {
					Log.d("小Y", "Applying rules.");
					//检查是否有root权限    是否清除并重新添加所有规则
					if (Api.hasRootAccess(MainActivity.this, true)
							&& Api.applyIptablesRules(MainActivity.this, true)) {
						Toast.makeText(MainActivity.this,
								R.string.rules_applied, Toast.LENGTH_SHORT)
								.show();
					} else {
						Log.d("小Y", "Failed - Disabling firewall.");
						//向系统广播未获得root权限
						Api.setEnabled(MainActivity.this, false);
					}
				} else {
					Log.d("小Y", "Saving rules.");
					//关闭防火墙的时候保存规则
					Api.saveRules(MainActivity.this);
					Toast.makeText(MainActivity.this, R.string.rules_saved,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		 //0.1秒后启动应用保存规则
		handler.sendEmptyMessageDelayed(0, 100);
	}

	/**
	 * Purge iptable rules, showing a visual indication
	 */
	private void purgeRules() {
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this,
				res.getString(R.string.working),
				res.getString(R.string.deleting_rules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					progress.dismiss();
				} catch (Exception ex) {
				}
				//有root权限直接清除  没有权限则直接返回
				if (!Api.hasRootAccess(MainActivity.this, true))
					return;
				if (Api.purgeIptables(MainActivity.this, true)) {
					Toast.makeText(MainActivity.this, R.string.rules_deleted,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}

	/**
	 * Called an application is check/unchecked
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final DroidApp app = (DroidApp) buttonView.getTag();
		if (app != null) {
			switch (buttonView.getId()) {
			case R.id.itemcheck_wifi:
				app.selected_wifi = isChecked;
				break;
			case R.id.itemcheck_3g:
				app.selected_3g = isChecked;
				break;
			}
		}
	}
   //填充list的封装类
	private static class ListEntry {
		private ImageView app_Icon;
		private CheckBox box_wifi;
		private CheckBox box_3g;
		private TextView app_text;
		private TextView upload;
		private TextView download;
	}

	@Override
	//改名黑白名单label
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.label_mode:
			selectMode();
			break;
		}
	}
}
