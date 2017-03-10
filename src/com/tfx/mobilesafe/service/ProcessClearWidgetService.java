package com.tfx.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.receiver.ProcessClearWidgetProvider;
import com.tfx.mobilesafe.utils.TaskInfoUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class ProcessClearWidgetService extends Service {

	private AppWidgetManager mAM;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		//获得桌面小工具管理者
		mAM = AppWidgetManager.getInstance(getApplicationContext());
		
		//开启定时任务 监控进程信息
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateWidgetMessage();
			}
		};
		timer.schedule(task, 0, 2000); //计划任务  0：当前时间  2000：多久执行一次
		super.onCreate();
	}
	
	private void updateWidgetMessage() {
		ComponentName provider = new ComponentName(getApplicationContext(), ProcessClearWidgetProvider.class);
		//获取要更新内容所在的view布局
		RemoteViews views = new RemoteViews(getPackageName(),R.layout.process_widget); 
		//更新信息
		views.setTextViewText(R.id.tv_process_count	,"运行中的软件: "+TaskInfoUtils.getAllRunningAppInfo(getApplicationContext()).size());
		views.setTextViewText(R.id.tv_process_memory,"可用内存: "+Formatter.formatFileSize(getApplicationContext(), TaskInfoUtils.getAvailableMemory(getApplicationContext())));
		
		//清理进程广播  广播必须清单文件注册的
		Intent intent = new Intent();
		intent.setAction("widget.clear.process");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
		views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
		mAM.updateAppWidget(provider, views); //provider：要更新的内容所组件    viws:远程view
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
