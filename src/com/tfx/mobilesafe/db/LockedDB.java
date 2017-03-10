package com.tfx.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-6
 * @desc      程序锁数据库

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: LockedDB.java 35 2016-09-10 13:47:42Z tfx $
 */

public class LockedDB extends SQLiteOpenHelper {

	public static final String packName = "packageName";
	public static final String tbName = "locked_tb";
	public static final Uri uri = Uri.parse("content://tfx.locked");
	
	public LockedDB(Context contex) {
		super(contex,"locked.db",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表
		db.execSQL("create table  locked_tb(_id integer primary key autoincrement,packageName text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//版本升级
		db.execSQL("drop table locked_tb");
		onCreate(db);
	}
}
