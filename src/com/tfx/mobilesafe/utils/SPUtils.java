package com.tfx.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

	private static SharedPreferences mSp; // 定义SharedPreferences的实例

	// 获得SharedPreferences实例
	private static SharedPreferences getPreferences(Context context) {
		if (mSp == null) {
			mSp = context.getSharedPreferences(MyConstants.SP_FILENAME , Context.MODE_PRIVATE); // 文件名,权限
		}
		return mSp;
	}

	// 保存布尔数据  
	public static void putBoolean(Context context, String key ,boolean value) {
		//获取SharedPreferences实例,往里面添加布尔数据
		SharedPreferences sp = getPreferences(context);
		sp.edit().putBoolean(key, value).commit();
	}

	// 获取布尔数据   如果该key不存在 默认使用传过来的boolean 否则获取key的boolean
	public static boolean getBoolean(Context context ,String key, boolean defValue) {
		SharedPreferences sp = getPreferences(context);
		return sp.getBoolean(key, defValue);
	}
	
	// 保存字符串数据  
	public static void putString(Context context, String key ,String value) {
		//获取SharedPreferences实例,往里面添加布尔数据
		SharedPreferences sp = getPreferences(context);
		sp.edit().putString(key, value).commit();
	}

	// 获取字符串数据 
	public static String getString(Context context ,String key, String defValue) {
		SharedPreferences sp = getPreferences(context);
		return sp.getString(key, defValue);
	}
	
	public static void putInt(Context context, String key ,int value) {
		//获取SharedPreferences实例,往里面添加布尔数据
		SharedPreferences sp = getPreferences(context);
		sp.edit().putInt(key, value).commit();
	}
	
	// 获取字符串数据 
	public static int getInt(Context context ,String key, int defValue) {
		SharedPreferences sp = getPreferences(context);
		return sp.getInt(key, defValue);
	}
	public static void putLong(Context context, String key ,long value) {
		//获取SharedPreferences实例,往里面添加布尔数据
		SharedPreferences sp = getPreferences(context);
		sp.edit().putLong(key, value).commit();
	}
	
	public static long getLong(Context context ,String key, long defValue) {
		SharedPreferences sp = getPreferences(context);
		return sp.getLong(key, defValue);
	}
}
