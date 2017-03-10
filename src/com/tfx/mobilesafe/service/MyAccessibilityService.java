package com.tfx.mobilesafe.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import com.tfx.mobilesafe.activity.EnterLockPassActivity;
import com.tfx.mobilesafe.dao.LockedDao;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-8
 * @desc      看门狗 accessibility版（辅助功能）

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: MyAccessibilityService.java 35 2016-09-10 13:47:42Z tfx $
 */

public class MyAccessibilityService extends AccessibilityService {  
	LockedDao mLockedDao;
	private ShuRenReceiver receiver;
	private String shuren;
	
	//熟人广播
	private class ShuRenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			shuren = intent.getStringExtra("shuren");
		}
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		//当前event是 窗口状态改变
		if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
			String packageName = event.getPackageName()+"";
			//判断是否加锁了
			if(mLockedDao.isLocked(packageName)){
				//判断是否是熟人 输入过口令 就直接放行 不用再次输入密码
				if(packageName.equals(shuren)){
					//熟人 放行
				}else{
					//加锁了  弹出输入密码界面
					Intent intent = new Intent(getApplicationContext(),EnterLockPassActivity.class);
					//广播或者服务里开启Activity需要设置flags为new task
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//添加额外参数 包名
					intent.putExtra("packname", packageName);
					startActivity(intent);
				}
			}else{
				//没加锁 放行
			}
		}
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		mLockedDao = new LockedDao(getApplicationContext());
		//注册熟人广播
		receiver = new ShuRenReceiver();
		IntentFilter filter = new IntentFilter("tfx.shuren");//action名字 tfx.shuren
		registerReceiver(receiver, filter );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//销毁熟人广播
		unregisterReceiver(receiver);
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}
}
