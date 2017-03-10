package com.tfx.mobilesafe.receiver;

import com.tfx.mobilesafe.service.ProcessClearWidgetService;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class ProcessClearWidgetProvider extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		//第一次创建执行
		
		//注册监控进程状态服务
		Intent service = new Intent(context,ProcessClearWidgetService.class);
		context.startService(service);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		//删除最后一次执行
		
		//注销监控服务
		Intent service = new Intent(context,ProcessClearWidgetService.class);
		context.stopService(service);
		super.onDisabled(context);
	}

}
