package com.tfx.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tfx.mobilesafe.db.LockedDB;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-6
 * @desc      程序锁的dao

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: LockedDao.java 35 2016-09-10 13:47:42Z tfx $
 */

public class LockedDao {
	private LockedDB mLockedDB = null;
	private Context mContext;
	//实例化数据库对象
	public LockedDao(Context context){
		mContext = context;
		mLockedDB = new LockedDB(context);
	}
	
	/**
	 * 往程序锁数据库添加数据
	 * @param packageName app包名
	 */
	public void add(String packageName){
		//数据库操作对象
		SQLiteDatabase db = mLockedDB.getWritableDatabase();
		//插入数据
		ContentValues values = new ContentValues();
		values.put(LockedDB.packName, packageName);
		db.insert(LockedDB.tbName, null, values);
		db.close();
		
		//此方法执行 数据发生改变 发送通知给内容观察者
		mContext.getContentResolver().notifyChange(LockedDB.uri, null);
	}
	
	/**
	 * 根据包名删除程序锁数据
	 * @param packageName app包名
	 */
	public void delete(String packageName){
		//数据库操作对象
		SQLiteDatabase db = mLockedDB.getWritableDatabase();
		//删除数据
		db.delete(LockedDB.tbName, LockedDB.packName + "=?", new String[]{packageName});
		db.close();
		
		//此方法执行 数据发生改变 发送通知给内容观察者
		mContext.getContentResolver().notifyChange(LockedDB.uri, null);
	}
	
	
	/**
	 * 获取所有加锁数据
	 * @return 所有数据集合
	 */
	public List getAll(){
		List<String> list = new ArrayList<String>();
		//数据库操作对象
		SQLiteDatabase db = mLockedDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + LockedDB.packName + " from " + LockedDB.tbName, null);
		while(cursor.moveToNext()){
			list.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return list;
	}
	
	/**
	 * 根据包名 判断app是否已加锁
	 * @param packageName 包名
	 * @return true 易佳锁 false 没有加锁
	 */
	public boolean isLocked(String packageName){
		boolean res = false;
		SQLiteDatabase db = mLockedDB.getReadableDatabase();
		//查询当前包是否加锁
		Cursor cursor = db.rawQuery("select 1 from " + LockedDB.tbName + " where " + LockedDB.packName + " =? ", new String[]{packageName});
		if(cursor.moveToFirst()){
			res = true;
		}
		cursor.close();
		db.close();
		return res;
	}
}
