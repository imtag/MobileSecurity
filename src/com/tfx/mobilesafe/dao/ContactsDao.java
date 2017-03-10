package com.tfx.mobilesafe.dao;

import java.util.ArrayList; 
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.tfx.mobilesafe.domain.ContactBean;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-29
 * @desc      获取联系人的dao

 * @version   $Rev: 22 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-02 23:18:58 +0800 (星期二, 02 八月 2016) $
 * @id        $Id: ContactsDao.java 22 2016-08-02 15:18:58Z tfx $
 */

public class ContactsDao {
	//删除通话记录 删除条件来电号码
	public static void deleteCallLog(Context context,String number){
		Uri uri = Uri.parse("content://call_log/calls");//uri协议+主机名+表名
		context.getContentResolver().delete(uri, "number=?", new String[]{number});
	}
	
	//获取通话记录
	public static List<ContactBean> getCallLog(Context context){
		List<ContactBean> list = new ArrayList<ContactBean>();
		Uri uri = Uri.parse("content://call_log/calls");//uri协议+主机名+表名
		
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"number","name"}, null, null, null);
		ContactBean contactBean = null;
		while(cursor.moveToNext()){
			contactBean = new ContactBean();
			contactBean.setName(cursor.getString(1));
			contactBean.setPhone(cursor.getString(0));
			list.add(contactBean);
		}
		cursor.close();
		return list;
	}
	
	//获取短信记录数据
	public static List<ContactBean> getSmsLog(Context context){
		List<ContactBean> list = new ArrayList<ContactBean>();
		Uri uri = Uri.parse("content://sms");//uri协议+主机名+表名
		
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);
		ContactBean contactBean = null;
		while(cursor.moveToNext()){
			contactBean = new ContactBean();
			contactBean.setName("sms");
			contactBean.setPhone(cursor.getString(0));
			list.add(contactBean);
		}
		cursor.close();
		return list;
	}
	
	//获取联系人数据
	public static List<ContactBean> getContacts(Context context){
		//[0]创建一个放contactbean的集合
		List<ContactBean> list = new ArrayList<ContactBean>();
		//[1]先查询contacts表 的name_raw_contact_id列 我们就知道一共有几条有效联系人   
		Uri uri = Uri.parse("content://com.android.contacts/contacts");//uri协议+主机名+表名
		Uri uri2 = Uri.parse("content://com.android.contacts/data");//uri协议+主机名+表名
		
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"name_raw_contact_id"}, null, null, null);
		while(cursor.moveToNext()){ 
			//获取id  contact_id列是引用contact表的id列 
			String _id = cursor.getString(0); 
			if(_id != null){
				//如果id不为空,创建contact对象 并设置id
				ContactBean contact = new ContactBean();
				//[2]根据id去查询data表  查询data1列(名字,电话,邮箱...)和mimetype列(数据类型) 注意:contacts表的name_raw_contact_id列的有效联系人就是data表的raw_contact_id列
				//切记根据查询到的id去查data表其实查的是view_data视图,(mimetypes表和data1),view_data视图把多表结合起来了
				Cursor cursor2 = context.getContentResolver(). query(uri2, new String[]{"mimetype","data1"}, "raw_contact_id=?", new String[]{_id}, null);//根据id去查数据类型(mimetype)和数据(data1)
				while(cursor2.moveToNext()){
					String mimetype = cursor2.getString(0);
					String data1 = cursor2.getString(1);   
					//[3]根据mimetype区分data1的数据类型 
					if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
						contact.setPhone(data1);
					}else if("vnd.android.cursor.item/name".equals(mimetype)){
						contact.setName(data1);
					}
				}   
				//将contact对象添加到list集合中 
				list.add(contact);
				//记得关闭游标
				cursor2.close();
			}
		}
		cursor.close();
		return list;
	}
}