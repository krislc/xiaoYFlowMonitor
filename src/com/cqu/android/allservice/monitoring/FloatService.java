package com.cqu.android.allservice.monitoring;

import com.cqu.android.Activity.MyApplication;
import com.cqu.android.Activity.R;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	private TextView tx;
	private ImageView iv;
	private float StartX;
	private float StartY;
	private static long old_totalRx;
	private static long old_totalTx;
	private static long totalRx=0;
	private static long totalTx=0;
	@Override
	public void onCreate() {
		Log.d("FloatService", "onCreate");
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);
		tx = (TextView) view.findViewById(R.id.updata);
		iv = (ImageView) view.findViewById(R.id.img2);
		iv.setVisibility(View.GONE);
		createView();
		handler.postDelayed(task,1000);
	}

	private void createView() {
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		wmParams = ((MyApplication) getApplication()).getWindowParams();	
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;	
		wm.addView(view, wmParams);
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				x = event.getRawX();
				y = event.getRawY() - 25; 
				Log.i("currP", "currX" + x + "====currY" + y);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);// 璋冭瘯淇℃伅
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;

					updateViewPosition();
					showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Editor editor = getSharedPreferences("Settings", 0).edit();
				editor.putString("Window","0");
				editor.commit();
				Intent serviceStop = new Intent();
				serviceStop.setClass(FloatService.this, FloatService.class);
				stopService(serviceStop);
				
			}
		});

	}

	public void showImg() {
		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5
				&& !iv.isShown()) {
			iv.setVisibility(View.VISIBLE);
		} else if (iv.isShown()) {
			iv.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			dataRefresh();
			wm.updateViewLayout(view, wmParams);
		}
	};

	public void dataRefresh() {
		
        //更新瞬时上下行
		totalRx = TrafficStats.getTotalRxBytes();
		totalTx = TrafficStats.getTotalTxBytes();
		long mrx = totalRx - old_totalRx;
		long mtx = totalTx - old_totalTx;
		mrx = (long) ((float) (Math.round(mrx * 100.0)) / 100);
		mtx = (long) ((float) (Math.round(mtx * 100.0)) / 100);
		tx.setText(TrafficMonitoring.convertTraffic(mrx+mtx));	
		old_totalRx = TrafficStats.getTotalRxBytes();
        old_totalTx = TrafficStats.getTotalTxBytes();
        handler.postDelayed(task,1000);
	}

	private void updateViewPosition() {
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		
//		setForeground(true);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(task);
		Log.d("FloatService", "onDestroy");
		wm.removeView(view);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	
}
