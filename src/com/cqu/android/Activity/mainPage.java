package com.cqu.android.Activity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.cqu.android.allservice.monitoring.Speed;
import com.cqu.android.allservice.monitoring.TrafficMonitoring;
import com.cqu.android.db.DatabaseAdapter;



public class mainPage extends Activity {
	
	private GridView toolbarGrid;
	public static TextView upRate,downRate;
	
	private Calendar currentCa;
	private DatabaseAdapter dbAdapter;
	private final int TOOLBAR_ITEM_KEEPINTIME = 0;
	private final int TOOLBAR_ITEM_NETLIST = 1;
	private final int TOOLBAR_ITEM_STATIST = 2;
	private final int TOOLBAR_ITEM_SETTING = 3;
	private final int TOOLBAR_ITEM_ABOUT = 4;
	
	

	/** 底部按钮图片 **/
	int[] menu_toolbar_image_array = { R.drawable.shishi2,R.drawable.menu_debug,
			R.drawable.tongji,
			R.drawable.shezhi, R.drawable.women,
			};
	/** 底部按钮文字 **/
	String[] menu_toolbar_name_array = { "实时监控","联网控制" ,"流量统计", "系统设置", "关于我们",};
    
	private Handler handler = new Handler();
	
	    
	private Runnable runnable = new Runnable() {
	        public void run() {
	        
	            this.update();	            
	            handler.postDelayed(this,1500);// 间隔1.5秒  
	           
	        }
	        void update() {
	            //刷新msg的内容
	        	currentCa =  Calendar.getInstance();
	    		int year = currentCa.get(Calendar.YEAR);
	    		int month = currentCa.get(Calendar.MONTH)+1;
	    		int day = currentCa.get(Calendar.DATE);
	    		//仅在此处使用  设为局部变量
	    		TextView today3G = (TextView) findViewById(R.id.Today_3G);
	    		TextView todayWifi = (TextView) findViewById(R.id.Today_WIFI);
	    		TextView month3G = (TextView) findViewById(R.id.Month_3G);
	    		TextView monthWifi = (TextView) findViewById(R.id.Month_WIFI);
	    		TextView remain3G = (TextView) findViewById(R.id.Remain);
	    		TextView limit = (TextView) findViewById(R.id.limit);
//	  
	    		String month3GTraffic;
	    		String day3GTraffic;
	    		String dayWIFITraffic;
	    		String monthWIFITraffic;
               
	    		//从数据库读出数据
	
	    		// 显示本月已用3G流量
	    		//public static String convertTraffic(long traffic)流量单位转换
	    		//public long calculateForMonth(int year, int Month, int netType) 
	    		month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
	    		month3G.setText(month3GTraffic);

	    		// 本日已用3G流量;从数据库中获取
	    		day3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculate(year, month, day, 1));
	    		today3G.setText(day3GTraffic);
	    		

	    		// 本日已用WIFI流量;从数据库中获取
	    		dayWIFITraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculate(year, month, day, 0));
	    		todayWifi.setText(dayWIFITraffic);
	    		
	    		// 本月已用WIFI流量；从数据库中获取
	    		monthWIFITraffic =TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 0));
	    		monthWifi.setText(monthWIFITraffic);
	    		
	    		// 显示流量限额，从mLimit文件中读取
	    		try{
	    		String mLimit=getSharedPreferences("Settings", 0).getString(
	    					"mLimit", "0");
	    		double Limit,iLimit;
	    		//默认是30，若为用户自己设定，则为其他
	    		if(mLimit.equals("0")){
	    			Limit=30.0;
	    			limit.setText("30.0MB");
	    		}else{
	    			Limit=Double.valueOf(mLimit);
	    			limit.setText(mLimit+"MB");
	    		}
	    		
	    		//流量警告值

	    		  String mWarn = getSharedPreferences("Settings", 0).getString(
    					"mWarn", "0"); 		
	    			
	    			
	    			//iLimit=限制流量, 用户设定了限额则为限额  否则默认30
	    			if(mWarn.equals("0")){
	    				iLimit = 27.0;
	    			}else{
	    				iLimit = Double.valueOf(mWarn);
	    			}
	    			//弄到这里了-----------------------------------------------
	    			double remain;
	    			String tempString[];// 临时存储3G流量
	    			if (month3GTraffic.contains("KB")) {
	    				tempString = month3GTraffic.split("KB");
	    				double temp = Double.valueOf(tempString[0]);
	    				//iLimit转化为kb  减去已使用的流量  再除1000 精度两位  舍去
	    				remain = new BigDecimal(Limit * 1000 - temp).divide(new BigDecimal(1000),2,1).doubleValue();
	    			} else if (month3GTraffic.contains("MB")) {
	    				tempString = month3GTraffic.split("MB");
	    				double temp = Double.valueOf(tempString[0]);
	    				remain = Limit - temp;
	    			} else {
	    				tempString = month3GTraffic.split("GB");
	    				double temp = Double.valueOf(tempString[0]);
	    				remain = new BigDecimal(Limit * 1000 - temp).doubleValue();
	    			}
	    			remain3G.setText(remain + "MB");
	    			
	    			String warn = getSharedPreferences("Settings", 0).getString(
	    					"mWarn", "0");
	    			if(warn.equals("1")){
	    				if(remain>(Limit-iLimit)){
	    				   remain3G.setTextColor(Color.WHITE);
	    				}else{
	    				   remain3G.setTextColor(Color.RED);
	    				}
	    				
	    			}else{
	    				remain3G.setTextColor(Color.WHITE);
	    			}
	    			
	    		} catch (Exception ex) {
	    			Toast.makeText(mainPage.this,"出错了",Toast.LENGTH_SHORT).show();
	    		}
	    		
	        }
	        
	    }; 
	   
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle("小Y流量监控与统计系统");
		dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        downRate=(TextView) findViewById(R.id.downRate);
	    upRate=(TextView) findViewById(R.id.upRate);
        //启动界面更新
      	handler.postDelayed(runnable,500);
      	//启动瞬时速度
      	Intent service = new Intent();
		service.setClass(mainPage.this, Speed.class);		
		startService(service);
      	
       
    
        //菜单
        
        toolbarGrid = (GridView)findViewById(R.id.GridView_toolbar);
        /**
         * Set a Drawable that should be used to highlight the currently selected item.
         *
         * @param resID A Drawable resource to use as the selection highlight.
         *
         * @attr ref android.R.styleable#AbsListView_listSelector
         * 
         */
        //设置点击的按钮的背景颜色
		toolbarGrid.setSelector(R.drawable.toolbar_menu_item);
		// 设置背景
		toolbarGrid.setBackgroundResource(R.drawable.menu_bg2);
		// 设置每行列数
		toolbarGrid.setNumColumns(5);
		// 位置居中
		toolbarGrid.setGravity(Gravity.CENTER);
		toolbarGrid.setVerticalSpacing(20);// 垂直间隔
		toolbarGrid.setHorizontalSpacing(7);// 水平间隔
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array,
				menu_toolbar_image_array));// 设置菜单Adapter
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
			//void onItemClick(AdapterView<?> parent, View view, int position, long id);
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case TOOLBAR_ITEM_KEEPINTIME:
					Intent intent1 = new Intent();
					intent1.setClass(mainPage.this, keepInTime.class);
//					外部类在内部类中调用方法
					mainPage.this.startActivity(intent1);

					break;
				case TOOLBAR_ITEM_NETLIST:
					Intent intent5 = new Intent();
					intent5.setClass(mainPage.this, MainActivity.class);
					mainPage.this.startActivity(intent5);

					break;
				case TOOLBAR_ITEM_STATIST:
					Intent intent2 = new Intent();
					intent2.setClass(mainPage.this, statist.class);
					mainPage.this.startActivity(intent2);

					break;
				case TOOLBAR_ITEM_SETTING:
					Intent intent3 = new Intent();
					intent3.setClass(mainPage.this, setting.class);
					mainPage.this.startActivity(intent3);

					break;
				case TOOLBAR_ITEM_ABOUT:
					Intent intent4 = new Intent();
					intent4.setClass(mainPage.this, aboutus.class);
					mainPage.this.startActivity(intent4);

					break;

			
				}
			}
		});
		
    }
    public void onResume(){
    	super.onResume();
    }


	
	 private SimpleAdapter getMenuAdapter(String[] menuNameArray,
				int[] imageResourceArray) {
			ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < menuNameArray.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemImage", imageResourceArray[i]);
				map.put("itemText", menuNameArray[i]);
				data.add(map);
			}
			SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
					R.layout.item_menu, new String[] { "itemImage", "itemText" },
					new int[] { R.id.item_image, R.id.item_text });
			return simperAdapter;
		}
	 
	 static long waitTime = 2000;  
	 static long touchTime = 0;  
	   
	 @Override  
	 //再按一次退出
	 public boolean onKeyDown(int keyCode, KeyEvent event) {  
		 /**
		     * {@link #getAction} value: the key has been pressed down.
		     */
		 /** Key code constant: Back key. */
	     if(keyCode == KeyEvent.KEYCODE_BACK) {  
	         long currentTime = System.currentTimeMillis();  
	         if((currentTime-touchTime)>=waitTime) {  
	        	 
	             Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();  
	             touchTime = currentTime;  
	         }else { 
	        	 Intent serviceStop = new Intent();
				 serviceStop.setClass(mainPage.this,Speed.class);
				  stopService(serviceStop);
	              Intent out=new Intent(Intent.ACTION_MAIN);
	              out.addCategory(Intent.CATEGORY_HOME);
	              mainPage.this.startActivity(out);
	             
	         }  
	        
	     }  
	    return true;
	     //return super.onKeyDown(keyCode, event);  
	 }  
	


}
