package com.cqu.android.allservice.monitoring;

import com.cqu.android.Activity.R;
import com.cqu.android.bean.Api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class BootAndShutdownBroadcast extends BroadcastReceiver {
	//static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(final Context context, final Intent intent) {
		//启动监听器  开机重启或者第一次安装 点击启动    自动开启服务
		String new_stall=context.getSharedPreferences("newStall", 0).getString(
				"stall", "0");
		//启动监听器  开机重启或者第一次安装 点击启动    自动开启服务
		if ((Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
				|| this.isStart(new_stall)) {
			
			final Editor editor = context.getSharedPreferences("newStall", 0).edit();
			editor.putString("stall","1");
			editor.commit();
			Intent sayHelloIntent = new Intent(context, MonitoringService.class);
			context.startService(sayHelloIntent);
		}
		//保存ipatbles规则
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			if (Api.isEnabled(context)) {
				final Handler toaster = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.arg1 != 0)
							Toast.makeText(context, msg.arg1,
									Toast.LENGTH_SHORT).show();
					}
				};
				// Start a new thread to enable the firewall - this prevents ANR
				new Thread() {
					@Override
					public void run() {
						if (!Api.applySavedIptablesRules(context, false)) {
							// Error enabling firewall on boot
							final Message msg = new Message();
							msg.arg1 = R.string.toast_error_enabling;
							toaster.sendMessage(msg);
							Api.setEnabled(context, false);
						}
					}
				}.start();
			}
		} else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
			new Thread() {
				@Override
				public void run() {
					Api.storageTraffic(context);
				}
			}.start();
		}
	}
	 //判断是否为安装程序启动
		public boolean isStart(String s) {
			if (Integer.parseInt(s) == 1) {
				return true;
			} else {
				return false;
			}
		}
}
