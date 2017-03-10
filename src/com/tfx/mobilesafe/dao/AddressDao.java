package com.tfx.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tfx.mobilesafe.domain.ServicePhoneType;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-7
 * @desc      归属地查询dao

 * @version   $Rev: 27 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-12 22:27:41 +0800 (星期五, 12 八月 2016) $
 * @id        $Id: AddressDao.java 27 2016-08-12 14:27:41Z tfx $
 */

public class AddressDao {
	public static final String PHONEDBPATH = "/data/data/com.tfx.mobilesafe/files/address.db";
	public static final String SERVICEDBPATH = "/data/data/com.tfx.mobilesafe/files/commonnum.db";
	
	/**
	 * @param 服务类型   
	 * @return 服务类型的具体数据
	 */
	public static List<NumberAndName> getServiceNumberAndName(ServicePhoneType type){
		List<NumberAndName> list = new ArrayList<NumberAndName>();
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(SERVICEDBPATH, null, SQLiteDatabase.OPEN_READONLY); 
		//查询classlist表 获得所有类型
		Cursor cursor = db.rawQuery("select name,number from table" + type.getOut_id(),null);
		NumberAndName bean = null;
		while(cursor.moveToNext()){
			bean = new NumberAndName();
			bean.setName(cursor.getString(0));
			bean.setNumber(cursor.getString(1));
			list.add(bean);
		}
		return list;
	}
	
	/**
	 * @return 返回封装了所有服务电话类型的list集合
	 */
	public static List<ServicePhoneType> getAllServiceTypes(){
		List<ServicePhoneType> list = new ArrayList<ServicePhoneType>();
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(SERVICEDBPATH, null, SQLiteDatabase.OPEN_READONLY); 
		//查询classlist表 获得所有类型
		Cursor cursor = db.rawQuery("select name,idx from classlist",null);
		ServicePhoneType type = null;
		while(cursor.moveToNext()){
			type = new ServicePhoneType();
			type.setName(cursor.getString(0));
			type.setOut_id(cursor.getInt(1));
			list.add(type);
		}
		return list;
	}
	
	/**
	 * @param phone 用户输入的号码 , 进行手机号正则表达式,符合就是手机号,不符合就是固定电话
	 * @return 归属地信息
	 */
	public static String getLocation(String phone){
		String location = "未知";   
		if(phone.length() < 3){ //号码位数必须大于等于3位
			return location;
		}else{
			//手机号正则表达式
			String pattern = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(phone);
			boolean b = m.matches();
			if(b){
				//匹配成功  是手机号
				location = getMobileLocation(phone.substring(0,7));//截取前七位进行查询
			}else{
				//匹配不成功  	固定号
				if(phone.charAt(1) == '1' || phone.charAt(1) == '2'){ //数据库区号两位的只有1和2开头,数据了去除了区号第一位0,索引从1开始
					location = getPhoneLocation(phone.substring(1,3));
				}else if(phone.length() > 3){
					location = getPhoneLocation(phone.substring(1,4));//索引1是3开头 不是1和2
				}else{
					return location;
				}
			}
			return location.substring(0,location.length()-2); //截取查询结果的后两位(运营商)
		}
	}
	
	/**
	 * 根据区号查询归属地
	 * @param phoneNumber 电话号码区号
	 * @return 归属地
	 */
	public static String getPhoneLocation(String phoneNumber){
		String location = "未知11";
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PHONEDBPATH, null, SQLiteDatabase.OPEN_READONLY); 
		//根据区号查询归属地
		Cursor cursor = db.rawQuery("select location from data2 where area=?",new String[]{phoneNumber});
		if(cursor.moveToNext()){
			//获取归属地
			location = cursor.getString(0);
		}
		return location;
	}
	
	/**
	 * 根据手机号码查询归属地
	 * @param mobileNumber 手机号码
	 * @return 归属地
	 */
	public static String getMobileLocation(String mobileNumber){
		String location = "未知11";
		//获取数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PHONEDBPATH, null, SQLiteDatabase.OPEN_READONLY); 
		//根据手机号查询归属地
		Cursor cursor = db.rawQuery("select location from data2 where id = (select outkey from data1 where id =?);", new String[]{mobileNumber});
		if(cursor.moveToNext()){
			//获取归属地
			location = cursor.getString(0);
		}
		return location;
	}
}
