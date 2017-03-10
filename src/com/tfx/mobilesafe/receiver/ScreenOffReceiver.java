package com.tfx.mobilesafe.receiver;

import java.util.List;

import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.TaskInfoUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//当锁屏时清理进程
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<AppInfoBean> allRunningAppInfo = TaskInfoUtils.getAllRunningAppInfo(context);
		for (AppInfoBean appInfoBean : allRunningAppInfo) {
			am.killBackgroundProcesses(appInfoBean.getPackName());
		}
	}
}
