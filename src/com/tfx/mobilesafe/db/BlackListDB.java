package com.tfx.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-27
 * @desc      黑名单数据库

 * @version   $Rev: 21 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-01 22:24:36 +0800 (星期一, 01 八月 2016) $
 * @id        $Id: BlackListDB.java 21 2016-08-01 14:24:36Z tfx $
 */

public class BlackListDB extends SQLiteOpenHelper{

	private static final int VERSION = 1; //版本
	public static final String BLACKTB = "blacklist"; //表名	
	public static final String PHONE = "phone"; //黑名单电话
	public static final String MODE = "mode"; //拦截模式
	public static final int SMS_MODE = 1 << 0; //01 = 1  短信拦截
	public static final int PHONE_MODE = 1 << 1; //10 = 2  电话拦截
	public static final int ALL_MODE = SMS_MODE | PHONE_MODE; //1 | 2 = 01 | 10 = 11 = 3 全部拦截  
	
	public BlackListDB(Context context) {
		super(context, "blacklist.db", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//初始化 创建表
		db.execSQL("create table blacklist(_id integer primary key autoincrement,phone text,mode integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//版本号改变回调
		db.execSQL("drop table blacklist"); //不是逻辑删除 是直接删除 再调用oncreate
		onCreate(db);
	}
}
