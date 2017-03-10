package com.tfx.mobilesafe.service;


import com.tfx.mobilesafe.receiver.LostFindSmsReceiver;

import android.app.Service; 
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-1
 * @desc      防盗服务

 * @version   $Rev: 21 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-01 22:24:36 +0800 (星期一, 01 八月 2016) $
 * @id        $Id: LostFindService.java 21 2016-08-01 14:24:36Z tfx $
 */

public class LostFindService extends Service {

	private LostFindSmsReceiver smsReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
  
	//当服务初始化   注册短信拦截的广播接收者   使用代码注册  方便服务销毁时取消注册
	@Override
	public void onCreate() {
		super.onCreate();
		//注册短信拦截广播 
		smsReceiver = new LostFindSmsReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE); //设置优先级位最大
		registerReceiver(smsReceiver, filter); 
	}
	
	//当服务销毁   取消短信拦截广播接收者
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}
	
}
