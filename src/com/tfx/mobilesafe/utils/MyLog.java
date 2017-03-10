package com.tfx.mobilesafe.utils;

import android.util.Log;

/**
 * @author Tfx
 * @comp GOD
 * @date 2016-9-9
 * @desc 封装自己的日志工具
 * 
 * @version $Rev: 35 $
 * @auther $Author: tfx $
 * @date $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id $Id: MyLog.java 35 2016-09-10 13:47:42Z tfx $
 */

public class MyLog {
	private static final boolean isOpen = true;

	public static void p(String msg) {
		if (isOpen) {
			System.out.println(msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isOpen) {
			Log.d(tag, msg);
		}
	}
}
