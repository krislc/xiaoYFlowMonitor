package com.cqu.android.allservice.monitoring;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.cqu.android.Activity.R;
import com.cqu.android.Activity.mainPage;
import com.cqu.android.db.DatabaseAdapter;

public class MonitoringService extends Service {

	private DatabaseAdapter dbAdapter;
	private Handler handler = new Handler() ;
	private long mobileRx = 0 , mobileTx = 0  ,wifiRx = 0 ,wifiTx = 0;
	private static long old_mobileRx = 0 ,old_mobileTx = 0  ,old_wifiRx = 0, old_wifiTx= 0,totalRx = 0 , totalTx = 0 ;
	private long mrx = 0,mtx = 0 , wrx = 0 ,wtx = 0 ;
	private static long mobileRx_all= 0 ,mobileTx_all= 0 ,wifiRx_all = 0,wifiTx_all = 0 ;
	int threadNum; // 线程数
	private static int count = 1;
	public static final String TAG="TRAFFIC";
	NetworkInfo nwi;
	Notification notification;
	Intent notificationIntent;
	PendingIntent pendingIntent;
	private ConnectivityManager connManager; 
	private Calendar currentCa;
	private static boolean float_open=false;
	//bindServicer()把这个 Service 和调用 Service 的客户类绑起来，如果调用这个客户类被销毁，
	//Service 也会被销毁。用这个方法的一个好处是，bindService()
	//方法执行后 Service 会回调上边提到的 onBind() 方发，
	//你可以从这里返回一个实现了 IBind 接口的类，在客户端操作这个类就能和这个服务通信了
	//，比如得到 Service 运行的状态或其他操作。如果 Service 还没有运行，
	//使用这个方法启动 Service 就会 onCreate() 方法而不会调用 onStart()。
	public IBinder onBind(Intent intent) {
		return null;
	}
	//ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); 

	public void onCreate() {
		 connManager = (ConnectivityManager)
					this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    //3G状态下总流量
		old_mobileRx = TrafficStats.getMobileRxBytes();
		old_mobileTx = TrafficStats.getMobileTxBytes();

		// 获取全部网络接收、发送数据总量
		totalRx = TrafficStats.getTotalRxBytes();
		totalTx = TrafficStats.getTotalTxBytes();
		if(isWifiConnected()){
		// 计算WiFi网络接收、发送数据总量
		old_wifiRx = totalRx - old_mobileRx;
		old_wifiTx = totalTx - old_mobileTx;	
		}else{
			old_wifiRx = 0;
			old_wifiTx = 0;
			
		}
		
		handler.postDelayed(thread, 1000);//这个应该是开启这个线程的哦？
		
		
		
	    notification = new Notification(R.drawable.notificatonicon, getText(R.string.service_notification),
		      System.currentTimeMillis());
	    /* notificationIntent = new Intent(this, mainPage.class);
		 pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		 notification=new Notification.Builder(this).addAction(R.drawable.notificatonicon,getText(R.string.service_notification), pendingIntent)
		 .setShowWhen(true).setWhen(System.currentTimeMillis()).build();
		 notification.notify();*/
		
		notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.service_notification),
		        getText(R.string.service_notification), pendingIntent);
		startForeground(Notification.FLAG_ONGOING_EVENT, notification);
		
		super.onCreate();
	        
	}
	 public void toggleGprs(boolean isEnable) throws Exception {  
	        Class<?> cmClass = connManager.getClass();  
	        Class<?>[] argClasses = new Class[1];  
	        argClasses[0] = boolean.class;  
	  
	        // 反射ConnectivityManager中hide的方法setMobileDataEnabled，可以开启和关闭GPRS网络  
	        Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);  
	        method.invoke(connManager, isEnable);  
	    } 
	    public boolean isMobileConnected() {  
			  
	        NetworkInfo mMobile = connManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	  
	        if (mMobile != null) {  
	            return mMobile.isConnected();  
	        }  
	        return false;  
	    }
	    public boolean isWifiConnected() {  
	    	  
	        NetworkInfo mWifi = connManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	  
	        if (mWifi != null) {  
	            return mWifi.isConnected();  
	        }  
	  
	        return false;  
	    }  

	


		
		
	
    Runnable thread = new Runnable(){

		public void run() {
			
			dbAdapter = new DatabaseAdapter(MonitoringService.this);
			dbAdapter.open();		
			//将要超出流量限额时自动断网,检测是否为2G、3G状态
			if(isMobileConnected()){
			 currentCa =  Calendar.getInstance();
			int year = currentCa.get(Calendar.YEAR);
			int month = currentCa.get(Calendar.MONTH)+1;
			String month3GTraffic = TrafficMonitoring.convertTraffic(dbAdapter.calculateForMonth(year, month, 1));
			double remain,Limit;
			String tempString[];// 临时存储3G流量
			String mLimit=getSharedPreferences("Settings", 0).getString(
					"mLimit", "0");
			if(mLimit.equals("0")){
    			Limit=30.0;
    			
    		}else{
    			Limit=Double.valueOf(mLimit);
    		}
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
			if(remain<0.5){
				try {
					toggleGprs(false);
				} catch (Exception e) {
					Log.i("resume","出错");
				}				
			  }
			}
			//联网状态下的计算才有意义
			if (isMobileConnected()) {
				// 截至启动机器
				// 获取移动网络接收、发送数据总量，单位为byte，以下同上
				mobileRx = TrafficStats.getMobileRxBytes();
				mobileTx = TrafficStats.getMobileTxBytes();
				mrx = (mobileRx - old_mobileRx); // 得到瞬时GPRS流量
				mtx = (mobileTx - old_mobileTx); // 得到瞬时
				// 保留小数点后两位
				mrx = (long) ((float) (Math.round(mrx * 100.0)) / 100);
				mtx = (long) ((float) (Math.round(mtx * 100.0)) / 100);
				mobileRx_all += mrx; // 求同一天的数据之和
				mobileTx_all += mtx; // 求同一天的数据之和
			}
		    
		 	if(isWifiConnected()){
		 		// 获取全部网络接收、发送数据总量
				totalRx = TrafficStats.getTotalRxBytes();
				totalTx = TrafficStats.getTotalTxBytes();
				// 获取移动网络接收、发送数据总量，单位为byte，以下同上
				mobileRx = TrafficStats.getMobileRxBytes();
				mobileTx = TrafficStats.getMobileTxBytes();

			    // 计算WiFi网络接收、发送数据总量
			    wifiRx = totalRx - mobileRx;
			    wifiTx = totalTx - mobileTx;
			 
				wrx = (wifiRx - old_wifiRx); // 得到瞬时wifi流量
				wtx = (wifiTx - old_wifiTx); // 得到瞬时wifi流量
				wrx = (long) ((float) (Math.round(wrx * 100.0)) / 100);// 保留两位小数
				wtx = (long) ((float) (Math.round(wtx * 100.0)) / 100);
				wifiTx_all += wtx; // 求同一天的数据之和
				wifiRx_all += wrx; // 求同一天的数据之和
			
		    }
			
		
			//执行5次更新一次数据库，5秒更新一次数据库
			if(count==5){
				Date date = new Date() ;		
				//如果存在该天GPRS流量的记录则跟新本条记录
				if(mobileTx_all!=0||mobileRx_all!=0){
					Cursor checkMobile = dbAdapter.check(1, date);//1 为 GPRS流量类型
				  if(checkMobile.moveToNext()){
					long up = dbAdapter.getProFlowUp(1, date);
					long dw = dbAdapter.getProFlowDw(1, date);
					mobileTx_all += up ;
					mobileRx_all += dw ;
					dbAdapter.updateData(mobileTx_all, mobileRx_all, 1, date);
					Log.i(TAG,"upmobile"+up);
					mobileTx_all=0;
					mobileRx_all=0;			
					}
				  else{					
						dbAdapter.insertData(mobileTx_all, mobileRx_all, 1, date);	
						Log.i(TAG, "insert");
					}
				  
				}
				
				if(wifiTx_all!=0 ||wifiRx_all!=0){
					Cursor checkWifi = dbAdapter.check(0, date);//0为 wifi流量类型
					long up = dbAdapter.getProFlowUp(0, date);
					long dw = dbAdapter.getProFlowDw(0, date);
					if(checkWifi.moveToNext()){
					wifiTx_all += up ;
					wifiRx_all += dw ;
					dbAdapter.updateData(wifiTx_all, wifiRx_all, 0, date);
					wifiTx_all = 0 ;
					wifiRx_all = 0 ;				
					}
					else{					
						dbAdapter.insertData(wifiTx_all, wifiRx_all, 0, date);
						Log.i(TAG, "insert wifi");				
					}
				}
				count = 1 ;
			}
			count++;
			dbAdapter.close();
			handler.postDelayed(thread, 1000);
			old_mobileRx = TrafficStats.getMobileRxBytes();
			old_mobileTx = TrafficStats.getMobileTxBytes();
			// 获取全部网络接收、发送数据总量
			totalRx = TrafficStats.getTotalRxBytes();
			totalTx = TrafficStats.getTotalTxBytes();
			old_wifiRx = totalRx - old_mobileRx;
			old_wifiTx = totalTx - old_mobileTx;
		

		
	   }
    	
    };
    
    public int onStartCommand(Intent intent, int flags, int startId) {
		// service已经启动情况下  其他程序再起启动service  会执行onStart（）不会执行onCreate（）
		handler.postDelayed(thread,1000);
		/*notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification=new Notification.Builder(this).addAction(R.drawable.notificatonicon,getText(R.string.service_notification), pendingIntent)
		.setShowWhen(true).setWhen(System.currentTimeMillis()).build();
		notification.notify();*/
		
		notification = new Notification
		(R.drawable.notificatonicon, getText(R.string.service_notification),
		        System.currentTimeMillis());
		notificationIntent = new Intent(this, mainPage.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.service_notification),
		       getText(R.string.service_notification), pendingIntent);
		startForeground(Notification.FLAG_ONGOING_EVENT, notification);
		return super.onStartCommand(intent, flags, startId);
	}
	public static long monitoringEachApplicationReceive(int uid) {
		long   receive=TrafficStats.getUidRxBytes(uid);
		if(receive==-1)receive=0;
	  return receive;
}

    public static long monitoringEachApplicationSend(int uid) {
	long   send=TrafficStats.getUidRxBytes(uid);
		if(send==-1)send=0;
	  return send;
}
    public int getNetType() {
		if(nwi != null){
			String net = nwi.getTypeName();
			if(net.equals("WIFI")){
				return 0;
			}else {
				return 1;
			}
		}else {
			return -1;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(thread);
		stopForeground(true);
		Log.v("CountService", "on destroy");
	}
}




