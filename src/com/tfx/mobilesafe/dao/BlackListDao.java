package com.tfx.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tfx.mobilesafe.db.BlackListDB;
import com.tfx.mobilesafe.domain.BlackBean;

/**
 * @author Tfx
 * @comp GOD
 * @date 2016-7-27
 * @desc 对黑名单数据的操作
 * 
 * @version $Rev: 21 $
 * @auther $Author: tfx $
 * @date $Date: 2016-08-01 22:24:36 +0800 (星期一, 01 八月 2016) $
 * @id $Id: BlackListDao.java 21 2016-08-01 14:24:36Z tfx $
 */

public class BlackListDao {
	private BlackListDB mBlackListDB; // 黑名单数据库

	/**
	 * 初始化黑名单数据库实例
	 * 
	 * @param context 上下文,数据库构造器需要
	 */
	public BlackListDao(Context context) {
		mBlackListDB = new BlackListDB(context);
	}

	/**
	 * 添加黑名单数据
	 * 
	 * @param phone 黑名单电话
	 * @param mode 拦截方式
	 */
	public void add(String phone, int mode) {
		// 1.获取数据库
		SQLiteDatabase db = mBlackListDB.getWritableDatabase();
		// 2.设置数据
		ContentValues values = new ContentValues();
		values.put(BlackListDB.PHONE, phone);
		values.put(BlackListDB.MODE, mode);
		// 3.插入数据
		db.insert(BlackListDB.BLACKTB, null, values);
		// 4.关闭数据库
		db.close();
	}
	
	/**
	 * 添加黑名单数据
	 * 
	 * @param bean 黑名单对象
	 */
	public void add(BlackBean bean){
		add(bean.getPhone(),bean.getMode());
	}

	/**
	 * 根据号码删除某个黑名单数据
	 * 
	 * @param phone 要删除的黑名单号码
	 * @return true删除成功 false 删除失败
	 */
	public boolean delete(String phone) {
		// 1.获取数据库
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		// 2.根据号码删除数据 返回删除行数
		int count = db.delete(BlackListDB.BLACKTB, BlackListDB.PHONE + "=?", new String[]{phone});
		// 3.关闭数据库
		db.close();
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 修改黑名单数据的拦截模式 
	 * 
	 * @param phone 修改的黑名单电话
	 * @param mode 新的拦截模式
	 */
	public void update(String phone,int mode){
		// 1.先删除要修改拦截模式的黑名单电话
		delete(phone);
		// 2.重新添加黑名单
		add(phone,mode);
	}
	
	
	/**
	 * 修改黑名单数据拦截模式
	 * 
	 * @param bean 黑名单数据对象
	 */
	public void update(BlackBean bean){
		update(bean.getPhone(),bean.getMode());
	}
	
	/**
	 * 查询所有黑名单数据
	 * 
	 * @return 返回黑名单对象的list集合
	 */
	public List<BlackBean> findAll(){
		List<BlackBean> list = new ArrayList<BlackBean>();
		// 1.获取数据库
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		// 2.查询所有
//		Cursor cursor = db.rawQuery("select " + BlackListDB.PHONE + " , " + BlackListDB.MODE + " form " + BlackListDB.BLACKTB + " order by _id desc", null);
		Cursor cursor = db.rawQuery("select phone,mode from blacklist order by _id desc", null);
		while(cursor.moveToNext()){
			// 3.将获取的黑名单数据封装到blackbean
			BlackBean bean = new BlackBean();
			bean.setPhone(cursor.getString(0));
			bean.setMode(cursor.getInt(1));
			list.add(bean);
		}
		db.close();
		return list ;
	}
	
	/**
	 * @param pageNumber 当前页码
	 * @param showCount 一页显示的黑名单数量
	 * @return 当前页显示的数据
	 */
	public List<BlackBean> getPageData(int pageNumber,int showCount){
		int startIndex = (pageNumber - 1) * showCount ; //开始位置算法  第一条数据索引是0开始
		
		List<BlackBean> list = new ArrayList<BlackBean>();
		// 1.获取数据库
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacklist order by _id desc limit ?,?",new String[]{startIndex + "",showCount+""}); //limit 0,8 索引从0开始  取8条
		BlackBean bean = null;
		while(cursor.moveToNext()){
			// 3.将获取的黑名单数据封装到blackbean
			bean = new BlackBean();
			bean.setPhone(cursor.getString(0));
			bean.setMode(cursor.getInt(1));
			list.add(bean);
		}
		return list;
	}
	
	/**
	 * @return 获取黑名单数据总条数
	 */
	public int getTotalRows(){
		int total = 0;
		List<BlackBean> list = new ArrayList<BlackBean>();
		// 1.获取数据库
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(1) from blacklist", null); // count(1)常量查询比* 速度快
		if(cursor.moveToNext()){
			total = cursor.getInt(0);
		}
		return total;
	}

	/**
	 * @param startIndex 开始索引
	 * @param showCount 分配加载的数据数量
	 * @return 新加载的数据
	 */
	public List<BlackBean> loadMore(int startRowIndex,int showCount){
		List<BlackBean> list = new ArrayList<BlackBean>();
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacklist order by _id desc limit ?,?",new String[]{startRowIndex + "",showCount+""}); //limit 0,8 索引从0开始  取8条
		BlackBean bean = null;
		while(cursor.moveToNext()){
			// 3.将获取的黑名单数据封装到blackbean
			bean = new BlackBean();
			bean.setPhone(cursor.getString(0));
			bean.setMode(cursor.getInt(1));
			list.add(bean);
		}
		return list;
	}
	
	/**
	 * 通过号码获得拦截模式
	 * @param phone 号码
	 * @return 黑名单数据里该号码的拦截模式
	 */
	public int getMode(String phone){
		int mode = 0;
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacklist where phone = ?",new String[]{phone}); //查询mode 根据phone
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		cursor.close();
		return mode;
	}
}
