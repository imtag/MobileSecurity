package com.tfx.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.BlackListDao;
import com.tfx.mobilesafe.dao.ContactsDao;
import com.tfx.mobilesafe.db.BlackListDB;
import com.tfx.mobilesafe.receiver.InterceptSmsReceiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-1
 * @desc      黑名单拦截的服务

 * @version   $Rev: 34 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-01 22:21:45 +0800 (星期四, 01 九月 2016) $
 * @id        $Id: BlackListService.java 34 2016-09-01 14:21:45Z tfx $
 */

public class BlackListService extends Service {

	private InterceptSmsReceiver mSmsReceiver;
	private PhoneStateListener listener;
	private TelephonyManager tm;
	private BlackListDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	//服务初始化
	@Override
	public void onCreate() {
		//提高服务级别为前台服务 软件就不容易被杀死
		startPriority();
		dao = new BlackListDao(getApplicationContext());
		//注册拦截短信广播
		registSmsIntercept();
		//注册电话拦截
		registTelIntercept();
		super.onCreate();
	}

	private void startPriority() {
		//通知
		Notification notification = new Notification(R.drawable.ic_logo_small,"手机安全中心",0);
		//打开安全卫士的意图  显示意图用于开启同一个程序的页面 隐士意图用于开启不同软件
		//隐士意图打开卫士主页面
		Intent intent = new Intent(); 
		intent.setAction("www.mobilesafe.com");
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent , 0);
		//通知内容
		notification.setLatestEventInfo(getApplicationContext(), "手机安全中心实时保护中","进入主页面", contentIntent );
		//设置为前台线程
		startForeground(1, notification);
	}

	//注册电话拦截
	private void registTelIntercept() {
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new PhoneStateListener(){
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				//响铃状态
				if(state == TelephonyManager.CALL_STATE_RINGING){
					phoneIntercept(incomingNumber);//电话拦截
				}
				super.onCallStateChanged(state, incomingNumber);
			}

			private void phoneIntercept(String incomingNumber) {
				int mode = dao.getMode(incomingNumber);
				if((mode & BlackListDB.PHONE_MODE) != 0){ //!=0  说明是电话拦截和全部拦截模式 2&1=0(短信) 2&2=2(电话) 2&3=2(全部)
					//注册内容观察者  当有通话日志时删除日志
					deleteCallLog(incomingNumber);
					//电话拦截
					endCall();
				}
			}
			
			//删除通话记录
			private void deleteCallLog(final String incomingNumber) {
				//注册内容观察者
				Uri uri = Uri.parse("content://call_log/calls"); //观察的uri 通话记录  
				getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
					@Override
					public void onChange(boolean selfChange) {
						//日志发生变化
						ContactsDao.deleteCallLog(getApplicationContext(), incomingNumber);
						//删除日志完成  取消注册内容观察者 以免一直后台运行占用资源
						getContentResolver().unregisterContentObserver(this); 
						super.onChange(selfChange);
					}
				});
			}
			
			//挂断电话
			private void endCall() {
				/*
				 * ITelephony调用endCall方法,getITelephony方法私有的无法调用,需要反射,endCall方法也被隐藏,要通过aidl调用该方法
				 */
				try {
					//1.得到TelephonyManager的class
					Class<TelephonyManager> clazz = TelephonyManager.class; 
					//2.获得getITelephony方法  该方法是私有的
					Method getITelephonyMethod = clazz.getDeclaredMethod("getITelephony", (Class[]) null);
					//3.设置方法属性 可进入
					getITelephonyMethod.setAccessible(true);
					//4.调用getITelephony方法得到ITelephony对象
					ITelephony iTelephony = (ITelephony)getITelephonyMethod.invoke(tm,  (Object[])null);
					//5.拦截电话
					iTelephony.endCall();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		};
		tm.listen(listener , PhoneStateListener.LISTEN_CALL_STATE); //flags:LISTEN_CALL_STATE 监听电话状态
	}
 
	//注册短信拦截广播
	private void registSmsIntercept() {
		mSmsReceiver = new InterceptSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE); //设置优先级为最大
		registerReceiver(mSmsReceiver, filter );
	}

	//服务销毁
	@Override
	public void onDestroy() {
		//取消电话状态监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE); //不监听
		//取消广播注册
		unregisterReceiver(mSmsReceiver);
		super.onDestroy();
	}

}
