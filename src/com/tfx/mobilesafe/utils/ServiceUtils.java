package com.tfx.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-24
 * @desc      服务的工具类

 * @version   $Rev: 16 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-24 23:30:04 +0800 (星期日, 24 七月 2016) $
 * @id        $Id: ServiceUtils.java 16 2016-07-24 15:30:04Z tfx $
 */

public class ServiceUtils {
	
	/**
	 * 该方法用来检测服务是否运行中
	 * @param context	上下文 用来得到ActivityManager
	 * @param servieceName	需要检测的服务名
	 * @return	servieceName是否运行中 运行返回true 否则false
	 */
	public static boolean isServiceRunning(Context context,String servieceName){
		boolean res = false;
		
		//ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中所有运行的服务
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			res = runningServiceInfo.service.getClassName().equals(servieceName);
			if (res) //res为true servieceName该服务正在运行 终止方法
				break;
		}
		return res;
	}
}
