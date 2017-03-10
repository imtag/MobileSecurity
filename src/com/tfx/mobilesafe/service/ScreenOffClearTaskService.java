package com.tfx.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.tfx.mobilesafe.receiver.ScreenOffReceiver;

public class ScreenOffClearTaskService extends Service {

	private ScreenOffReceiver screenOffReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		System.out.println("服务初始化");
		//服务初始化注册锁屏清理广播
		screenOffReceiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenOffReceiver, filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		System.out.println("服务销毁");
		//当服务销毁取消锁屏清理广播
		unregisterReceiver(screenOffReceiver);
		super.onDestroy();
	}
	
}
