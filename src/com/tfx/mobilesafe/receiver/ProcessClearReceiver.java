package com.tfx.mobilesafe.receiver;

import java.util.List;

import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.TaskInfoUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProcessClearReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//桌面小工具清理的广播
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<AppInfoBean> allRunningAppInfo = TaskInfoUtils.getAllRunningAppInfo(context);
		for (AppInfoBean appInfoBean : allRunningAppInfo) {
			am.killBackgroundProcesses(appInfoBean.getPackName());
		}
	}
}
