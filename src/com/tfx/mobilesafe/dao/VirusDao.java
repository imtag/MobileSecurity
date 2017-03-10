package com.tfx.mobilesafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-24
 * @desc      病毒数据库的dao层封装

 * @version   $Rev: 34 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-01 22:21:45 +0800 (星期四, 01 九月 2016) $
 * @id        $Id: VirusDao.java 34 2016-09-01 14:21:45Z tfx $
 */

public class VirusDao {
	//数据库路径
	public static final String ANTIVIRUSDBPATH = "/data/data/com.tfx.mobilesafe/files/antivirus.db";
	
	/**
	 * 判断文件是否是病毒
	 * @param md5 文件的md5值
	 * @return 是否是病毒文件
	 */
	public static boolean isVirus(String md5){
		boolean isVirus = false;
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(ANTIVIRUSDBPATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select 1 from datable where md5 = ?", new String[]{md5});
		if(cursor.moveToNext()){
			isVirus = true;
		}
		cursor.close();
		db.close();
		return isVirus;
	}
	
	/**
	 * 获取当前病毒数据库的版本号
	 * @return 病毒数据库版本号
	 */
	public static int getCurrentVirusVersion(){
		int versionCode = -1;
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(ANTIVIRUSDBPATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select subcnt from version",null);
		if(cursor.moveToNext()){
			versionCode = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return versionCode;
	}
	
	/**
	 * 更新病毒数据库的版本号
	 * @param newVersion 新版本号
	 */
	public static void updataVirusVersion(int newVersion){
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(ANTIVIRUSDBPATH, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("subcnt", newVersion);
		db.update("version", values, null, null);
		db.close();
	}
	
	
	/**
	 * 更新病毒库，插入新的病毒
	 * @param md5　病毒md5值
	 * @param desc 病毒的描述信息
	 */
	public static void updataVirus(String md5,String desc){
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(ANTIVIRUSDBPATH, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("type", 6);
		values.put("md5", md5);
		values.put("name", "Android.Troj.AirAD.a");
		values.put("desc", desc);
		db.insert("datable", null, values);
		db.close();
	}
}
