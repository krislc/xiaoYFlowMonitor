package com.cqu.android.allservice.monitoring;

import com.cqu.android.Activity.mainPage;

import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;

public class Speed extends Service {
	
	private static long old_totalRx;
	private static long old_totalTx;
	private static long totalRx=0;
	private static long totalTx=0;
	
	private Handler speed = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			 //更新瞬时上下行
			totalRx = TrafficStats.getTotalRxBytes();
			totalTx = TrafficStats.getTotalTxBytes();
			long mrx = totalRx - old_totalRx;
			long mtx = totalTx - old_totalTx;
			mrx = (long) ((float) (Math.round(mrx * 100.0)) / 100);
			mtx = (long) ((float) (Math.round(mtx * 100.0)) / 100);
			mainPage.upRate.setText(TrafficMonitoring.convertTraffic(mtx));
			mainPage.downRate.setText(TrafficMonitoring.convertTraffic(mrx));	
			old_totalRx = TrafficStats.getTotalRxBytes();
	        old_totalTx = TrafficStats.getTotalTxBytes();
			speed.postDelayed(task,1000);
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		speed.postDelayed(task,1000);
	}

}
