package com.tfx.mobilesafe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tfx.mobilesafe.domain.AppInfoBean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-14
 * @desc      封装获取app信息的工具   内存 app图表 ......

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: AppInfoUtils.java 35 2016-09-10 13:47:42Z tfx $
 */

public class AppInfoUtils {
	
	/**
	 * @return sd卡总共空间
	 */
	public static long getSdTotalMemory(){
		return Environment.getExternalStorageDirectory().getTotalSpace();
	}
	
	/**
	 * @return sd卡剩余空间
	 */
	public static long getSdFreeMemory(){
		return Environment.getExternalStorageDirectory().getFreeSpace();
	}
	
	/**
	 * @return 手机总共空间
	 */
	public static long getPhoneTotalMemory(){
		return Environment.getDataDirectory().getTotalSpace();
	}
	
	/**
	 * @return 手机剩余空间
	 */
	public static long getPhoneFreeMemory(){
		return Environment.getDataDirectory().getFreeSpace();
	}
	
	public static List<AppInfoBean> getAllInstalledAppInfo(Context context){
		//存放app信息bean的容器
		List<AppInfoBean> datas = new ArrayList<AppInfoBean>();
		//获得包管理者
		PackageManager pm = context.getPackageManager();
		//获得所有安卓的app集合
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		List<ApplicationInfo> appList = pm.getInstalledApplications(0); //额外参数  写0
		
		AppInfoBean bean = null;
		for (ApplicationInfo app : appList) {
			bean = new AppInfoBean();
			try {
				PackageInfo packageInfo = pm.getPackageInfo(app.packageName, 0); //获得包信息
				bean.setVersion(packageInfo.versionName+packageInfo.versionCode);//版本信息
				
				bean.setPackName(app.packageName);// app包名

				// 根据包名封装其他信息
				AppInfoUtils.getAppInfo(context, bean);

				// 把bean添加到容器中
				datas.add(bean);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return datas;
	}
	
	/**
	 * 一定要先获取包名，封装了包名，再根据当前包名封装别的信息
	 * @param context
	 * @param bean 当前封装的bean
	 * @throws NameNotFoundException
	 */
	public static void getAppInfo(Context context , AppInfoBean bean) throws NameNotFoundException{
		PackageManager pm = context.getPackageManager();//包管理者
		ApplicationInfo app = pm.getApplicationInfo(bean.getPackName(), 0); //根据包名获得应用信息
		bean.setIcon(app.loadIcon(pm));// 图标
		bean.setAppName(app.loadLabel(pm) + "");// app名
		bean.setSourceDir(app.sourceDir);// 安装路径
		bean.setSize(new File(app.sourceDir).length());// 安装包占用的大小
		bean.setUid(app.uid);//uid
		
		if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
			//系统app
			bean.setSystem(true);
		}
		
		if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
			//安装在sd卡中
			bean.setSD(true);
		}
	}
}
