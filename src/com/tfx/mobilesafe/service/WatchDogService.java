package com.tfx.mobilesafe.service;

import java.util.List;

import com.tfx.mobilesafe.activity.EnterLockPassActivity;
import com.tfx.mobilesafe.dao.LockedDao;
import com.tfx.mobilesafe.db.LockedDB;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-8
 * @desc      看门狗 线程监控

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: WatchDogService.java 35 2016-09-10 13:47:42Z tfx $
 */

public class WatchDogService extends Service {
	LockedDao mLockedDao;
	private ShuRenReceiver receiver;
	private String shuren;
	private boolean isRunning ;
	private ActivityManager mAm;
	private List<String> allLockedPackageName;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mLockedDao = new LockedDao(getApplicationContext());
		allLockedPackageName = mLockedDao.getAll();//获取所有加锁包
		//注册熟人广播
		receiver = new ShuRenReceiver();
		IntentFilter filter = new IntentFilter("tfx.shuren");//action名字 tfx.shuren
		registerReceiver(receiver, filter );
		
		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		//开启看门狗线程
		startWatchDogThread();
		
		//注册内容观察者
		ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {	
				super.onChange(selfChange);
				//重新加载数据
				allLockedPackageName.addAll(mLockedDao.getAll());
			}
		};
		getContentResolver().registerContentObserver(LockedDB.uri, true, observer);
	}
	
	//看门狗线程
	private void startWatchDogThread() {
		new Thread(){
			public void run() {
				List<RunningTaskInfo> list;
				RunningTaskInfo runningTaskInfo = null;
				String packageName = null;
				isRunning = true;
				while(isRunning){ //只要服务开启了 isRunning为true 这个线程无限循环
					//1监控任务栈
					list = mAm.getRunningTasks(1);
					//2获得最新打开的(最前面的task)任务栈
					runningTaskInfo = list.get(0);
					//3 获得任务栈的顶部Activity 包名
					packageName = runningTaskInfo.topActivity.getPackageName();
					
					//4 判断是否加锁了
					//原本(mLockedDao.isLocked(packName) 这样查询数据库 需要到硬盘查询很慢  oncreate中把所有加锁信息加载到内存 这里进行内存判断就效率高
					if(allLockedPackageName.contains(packageName)){
						//判断是否是熟人 输入过口令 多次进入的是同一个应用  就直接放行 不用再次输入密码   注意：熟人只有一个 
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
					SystemClock.sleep(200);
				}
			};
		}.start();
	}
	
	@Override
	public void onDestroy() {
		//销毁熟人广播
		unregisterReceiver(receiver);
		//停止服务
		isRunning = false;
		super.onDestroy();
	}
	
	//熟人广播
	private class ShuRenReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			shuren = intent.getStringExtra("shuren");
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
