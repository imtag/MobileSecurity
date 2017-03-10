package com.tfx.mobilesafe.service;

import com.tfx.mobilesafe.dao.AddressDao;
import com.tfx.mobilesafe.view.MyToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-10
 * @desc      来电归属地显示服务

 * @version   $Rev: 25 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-11 10:28:51 +0800 (星期四, 11 八月 2016) $
 * @id        $Id: IncomingLocationService.java 25 2016-08-11 02:28:51Z tfx $
 */

public class IncomingLocationService extends Service {

	private TelephonyManager mTM;
	private PhoneStateListener listener;
	private MyToast myToast;
	private OutCallReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	//服务初始化
	@Override
	public void onCreate() {
		//注册来电状态监听
		registPhoneState();
		//注册外拨电话广播
		registOutCall();
		  
		myToast = new MyToast(getApplicationContext());
		super.onCreate();
	}
	
	//外拨电话广播
	private class OutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//获得外拨的电话
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			//获得归属地
			String location = AddressDao.getLocation(number);
			myToast.show(location);
			
			//记得权限PROCESS_OUTGOING_CALLS
		}
	}
	

	private void registOutCall() {
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL); //过滤信息
		registerReceiver(receiver, filter);
	}
	
	private void registPhoneState() {
		//1.获得电话管理者
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//2.监听电话状态
		listener = new PhoneStateListener(){
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://空闲 停止
					myToast.hide();
					break;
				case TelephonyManager.CALL_STATE_RINGING://响铃
					//显示来电归属地
					showLocation(incomingNumber);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://通话
					myToast.hide();
					break;
				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}

		};
		mTM.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	//显示归属地
	private void showLocation(String incomingNumber) {
		//获取归属地
		String location = AddressDao.getLocation(incomingNumber);
		myToast.show(location);
	}

	//服务销毁
	@Override
	public void onDestroy() {
		//取消电话状态监听
		mTM.listen(listener, PhoneStateListener.LISTEN_NONE);
		//取消注册外拨电话广播
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}
	
}
