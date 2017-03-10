package com.tfx.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.tfx.mobilesafe.domain.AppInfoBean;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-19
 * @desc      进程信息的封装

 * @version   $Rev: 33 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-23 22:47:34 +0800 (星期二, 23 八月 2016) $
 * @id        $Id: TaskInfoUtils.java 33 2016-08-23 14:47:34Z tfx $
 */

public class TaskInfoUtils {

	/**
	 * @param context
	 * @return 可用内存空间
	 */
	public static long getAvailableMemory(Context context){
		//Activity管理者
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo); // 把内存的信息写到outInfo对象中
		//可用内存
		return outInfo.availMem;
	}
	
	/**
	 * /proc/meminfo文件的第一行  就是总内存大小
	 * 读取这个文件获取总内存
	 * @return 总内存
	 */
	public static long getTotalMemory(){
		/*
		//需要api16版本以上 所以不用这个方法
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.totalMem;
		*/
		File file = new File("/proc/meminfo");
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String string = br.readLine(); //读取第一行
			String str = string.substring(string.indexOf(':')+1, string.length()-2).trim();  //MemTotal:  510976 kB
			return Long.parseLong(str)*1024; //kb转b
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @param context
	 * @return 所有运行中的进程信息
	 * @throws NameNotFoundException 
	 */
	public static List<AppInfoBean> getAllRunningAppInfo(Context context){
		List<AppInfoBean> list = new ArrayList<AppInfoBean>();
		//1.Activity管理者
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//2.所有运行中的进程
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		AppInfoBean bean = null;
		//3.封装信息
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			bean = new AppInfoBean();
			//占用内存大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			bean.setMemorySize(processMemoryInfo[0].getTotalPrivateDirty() * 1024);
			//包名
			bean.setPackName(runningAppProcessInfo.processName);
			//需要抛异常  因为有没有名字的进程
			try {
				AppInfoUtils.getAppInfo(context, bean);
				//正常添加
				list.add(bean);
			} catch (NameNotFoundException e) {
				//没有名字
				e.printStackTrace();
			}
		}
		return list;
	}
}
