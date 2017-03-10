package com.tfx.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Process;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-8
 * @desc      一个apk对应一个application，在所有功能执行之前先执行application

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: MyApplication.java 35 2016-09-10 13:47:42Z tfx $
 */

public class MyApplication extends Application {
	//把错误信息写到文件 p1：错误信息 p2：文件路径
	private void writeExceptionMessage2File(String mess,String path) {
		try {
			File file = new File(path);
			PrintWriter pw = new PrintWriter(file);
			pw.println(mess);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate() {
		//在所有功能执行之前执行  
		//监控任务异常的状态
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				StringBuilder mess = new StringBuilder();
				//该方法 捕获任何线程抛出的异常
				
				//反射求出机型信息   build类
				Class type = Build.class;//1.class
				Field[] fields = type.getDeclaredFields(); //2.属性
				for (Field field : fields) {
					try {
						String name = field.getName();//属性名
						Object value = field.get(null);//属性值
						mess.append(name+":"+value+"\n");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				//将异常信息保存到文件中 放到sdcard 便于用户发送
				writeExceptionMessage2File(mess + ex.toString(),"/sdcard/mobilesafe_error.txt");
				
				//应用崩溃   获取启动app的意图  重新启动
				Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
				startActivity(launchIntentForPackage);
				
				//杀进程 让应用更快死掉  友好体验
				Process.killProcess(Process.myPid());
			}
		});
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
}
