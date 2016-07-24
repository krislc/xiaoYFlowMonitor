package com.cqu.android.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cqu.android.allservice.monitoring.FloatService;
import com.cqu.android.db.DatabaseAdapter;


public class setting extends Activity{
	
	private View View_quota;
	private View View_left;
	private View View_clear;
	private View View_date;
	private EditText dt;
	private EditText dt1;
	private EditText dt2;
	private DatabaseAdapter dbAdapter;
	public static String mLeft,mCountDate,mLimit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		setTitle("小Y系统设置");
		dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
     
	}
	public void onResume() {
		super.onResume();
		setContentView(R.layout.setup);
		ToggleButton isWindow;
		ToggleButton isWarn;
		/**
		 * Displays checked/unchecked states as a button
		 * with a "light" indicator and by default accompanied with the text "ON" or "OFF".
		 */
        //悬浮窗口
		isWindow = (ToggleButton) findViewById(R.id.ToggleButton1);
		
		String window=getSharedPreferences("Settings", 0).getString(
				"Window", "0");
		final TextView is_Window = (TextView)findViewById(R.id.is_statist);
		
		
		if(window.equals("1"))
		{
			is_Window.setText("悬浮窗口已开启");
		}
		else
		{
			is_Window.setText("悬浮窗口已关闭");
		}
		
		// 设置悬浮窗口功能是否启   Boxstate检测是否为1
		isWindow.setChecked(this.BoxState(window));
		isWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("Window","1");
							editor.commit();
		                    Toast.makeText(setting.this,"悬浮窗口已开启",Toast.LENGTH_SHORT).show();
							is_Window.setText("悬浮窗口已开启");
							Intent service = new Intent();
			        		service.setClass(setting.this, FloatService.class);		
			        		startService(service);
						} else {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("Window","0");
							editor.commit();
		                    Toast.makeText(setting.this,"悬浮窗口已关闭",Toast.LENGTH_SHORT).show();
							is_Window.setText("悬浮窗口已关闭");
							Intent serviceStop = new Intent();
			        		serviceStop.setClass(setting.this, FloatService.class);
			        		stopService(serviceStop);
						}
					}
				});

		
		
		//上网流量警示
		 isWarn = (ToggleButton) findViewById(R.id.ToggleButton2);
		// 设置流量警示功能是否开启
		final TextView is_warn = (TextView)findViewById(R.id.is_warn);
		String mWarn = getSharedPreferences("Settings", 0).getString(
					"mWarn", "0");
		//初始化is_warn的值
		if(mWarn.equals("1"))
		{
			is_warn.setText("流量警示功能已开启");
		}
		else
		{
			is_warn.setText("流量警示功能已关闭");
		}
		
		isWarn.setChecked(this.BoxState(mWarn));
		isWarn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
				
						// TODO Auto-generated method stub
						if (isChecked) {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("mWarn","1");
							editor.commit();
		                    Toast.makeText(setting.this,"流量警示功能已开启",Toast.LENGTH_SHORT).show();
							is_warn.setText("流量警示功能已开启");
						} else {
							
							final Editor editor = getSharedPreferences("Settings", 0).edit();
							editor.putString("mWarn","0");
							editor.commit();
							
							is_warn.setText("流量警示功能已关闭");
						}
					}
				});
        //每月流量限额
		ImageView imgView1 = (ImageView) this.findViewById(R.id.imageButton_quota);
		final TextView limit_flow = (TextView)findViewById(R.id.limit_flow);
		//每月剩余流量提醒
		ImageView imgView4 = (ImageView) this.findViewById(R.id.imageButton_left);
		final TextView flow_remind = (TextView)findViewById(R.id.flow_remind);
		//每月结算日
		ImageView imgView2 = (ImageView) this.findViewById(R.id.imageButton_date);
		final TextView count_date = (TextView)findViewById(R.id.count_date);
		//清空数据
		ImageView imgView5 = (ImageView) this.findViewById(R.id.imageButton_clear);
		final TextView clear_data = (TextView)findViewById(R.id.clear_data);
		
		
		
		
		
		
		//初始化limit_flow的值
		
		mLimit = getSharedPreferences("Settings", 0).getString(
				"mLimit", "0");
		if(mLimit.equals("0")){
		   limit_flow.setText("每月流量限额为"+30+"MB");
		}else{
			limit_flow.setText("每月流量限额为"+mLimit+"MB");
		}
        // 设置每月流量限额
		imgView1.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
			    /*
				 * Instantiates a layout XML file into its corresponding {@link android.view.View}
				 * objects. It is never used directly. */
                //导入xml文件
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_quota = factory.inflate(R.layout.setup_quota, null);

				new AlertDialog.Builder(setting.this).setTitle("每月流量限额").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_quota)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										
										final Editor editor = getSharedPreferences("Settings", 0).edit();
										
										//获取添加的限额
										dt = (EditText) View_quota
												.findViewById(R.id.quota12);
										String limit = dt.getText().toString();
										//保存在mLimit文件中
										editor.putString("mLimit",limit);
										editor.commit();
										limit_flow.setText("每月流量限额为"+limit+"MB");
									}

								}).setNegativeButton("取消", null).show();
			}
		});

		
		//初始化count_date的值
		mCountDate = getSharedPreferences("Settings", 0).getString(
				"mDate", "0");
		if(mCountDate.equals("0")){
		  count_date.setText("月结算日为该月"+1+"日,在当前情况下无需更改且不支持更改");
		}else{
		  count_date.setText("月结算日为该月"+mCountDate+"日");
		}
		//设置月结算日
		imgView2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_date = factory.inflate(R.layout.setup_date, null);

				new AlertDialog.Builder(setting.this).setTitle("月结算日").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_date)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									
//										final Editor editor = getSharedPreferences("Settings", 0).edit();
//										dt2 = (EditText) View_date
//												.findViewById(R.id.setup_date);
//										String date = dt2.getText().toString();
//										if(Integer.valueOf(date)<1||Integer.valueOf(date)>31)
//										{
//											Toast.makeText(setting.this, "请输入1~31的数字！", Toast.LENGTH_SHORT).show();
//											
//										}
//										else{
//																				
//					                    editor.putString("mDate",date);
//										editor.commit();
//										count_date.setText("月结算日为该月"+date+"日");
//										}
									}

								}).setNegativeButton("取消", null).show();
			}
		});

		
		//初始化每月剩余流量提醒
		mLeft = getSharedPreferences("Settings", 0).getString(
				"mLeft", "0");
		if(mLeft.equals("0")){
			flow_remind.setText("每月剩余流量为"+27+"MB时会提醒");	
		}else{
		flow_remind.setText("每月剩余流量为"+mLeft+"MB时会提醒");
		}
		//设置每月剩余流量提醒
		imgView4.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_left = factory.inflate(R.layout.setup_left, null);

				new AlertDialog.Builder(setting.this).setTitle("每月剩余流量提醒").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_left)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										
										// TODO Auto-generated method stub
									
										final Editor editor = getSharedPreferences("Settings", 0).edit();
										dt1 = (EditText) View_left
												.findViewById(R.id.setup_left);
										String strLeft = dt1.getText().toString();
										mLimit=getSharedPreferences("Settings", 0).getString(
												"mLimit", "0");
										//剩余流量的设置不能da于流量限额
										if(Integer.valueOf(strLeft) > Integer.valueOf(mLimit))
										{
											Toast.makeText(setting.this, "剩余流量的设置不能大于流量限额", Toast.LENGTH_SHORT).show();
											
										//	finish();
										}
										else{
									    editor.putString("mLeft",strLeft);
										editor.commit();
										flow_remind.setText("每月剩余流量为"+strLeft+"MB时会提醒");
										}
									}

								}).setNegativeButton("取消", null).show();
			}
		});
		
		
		//清空所有统计数据
		imgView5.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater.from(setting.this);
				View_clear = factory.inflate(R.layout.setup_clear, null);

				new AlertDialog.Builder(setting.this).setTitle("您确定要清楚所有统计记录吗").setIcon(
						android.R.drawable.ic_dialog_info).setView(View_clear)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										
										// 删除表
										dbAdapter.clear();
										clear_data.setText("数据已清空");
										Toast.makeText(setting.this,"数据已清空",Toast.LENGTH_SHORT).show();
									}

								}).setNegativeButton("取消", null).show();
			}
		});
		
		
		
		



	}
    //检测字符串是否为1
	public boolean BoxState(String s) {
		if (s.equals("")) {
			return false;
		} else if (Integer.parseInt(s) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 按下键盘上返回按钮

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent1 = new Intent();
			intent1.setClass(setting.this, mainPage.class);
			startActivity(intent1);
			setting.this.finish();
		}
		return true;
	}			
	
}

