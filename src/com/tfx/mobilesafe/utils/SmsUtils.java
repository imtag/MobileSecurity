package com.tfx.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tfx.mobilesafe.utils.SmsUtils.SmsJsonData.Sms;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-12
 * @desc      短信的工具类

 * @version   $Rev: 34 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-01 22:21:45 +0800 (星期四, 01 九月 2016) $
 * @id        $Id: SmsUtils.java 34 2016-09-01 14:21:45Z tfx $
 */
public class SmsUtils 
{
	//将json文件转换为字符串
	public static String stream2String(InputStream is){
		//1.字符串变量
		StringBuilder sb = new StringBuilder();
		//2.字符缓冲流
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));//InputStreamReader字节流转字符流
		try {
			//3.读取数据
			String line = reader.readLine();//读一行
			while(line != null){ //判断是否读完了
				sb.append(line); //将读取的数据追加到字符串变量
				line = reader.readLine(); //继续读
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * @param str 包含特殊字符的字符串
	 * @return 原字符串 包含json格式字符 []{}"
	 */
	private static String convert2Source(String str){
		String res = "";
		for(int i = 0 ;i < str.length() ; i++){
			res += convert2Source(str.charAt(i));
		}
		return res;
	}
	
	/**
	 * @param c 特殊字符
	 * @return 原字符 json格式字符 []{};
	 */
	private static char convert2Source(char c){
		//json字符转特殊字符 {卍      }卐       [¤   ]♋      "♍       ,¶   :§
		
		char res = '\u0000'; //char默认\u0000
		switch (c) {
		case '卍':
			res = '{';
			break;
		case '卐':
			res = '}';
			break;
		case '¤':
			res = '[';
			break;
		case '♋':
			res = ']';
			break;
		case '♍':
			res = '"';
			break;
		case '¶':
			res = ',';
			break;
		case '§':
			res = ':';
			break;

		default:
			res = c;
			break;
		}
		return res;
	}
	
	/**
	 * 如果原字符串里有json格式字符   则把里面的json字符转为特殊字符  
	 * @param str  原字符串 包含json格式字符 []{}"
	 * @return   包含特殊字符的字符串
	 */
	private static String convert2Special(String str){
		String res = "";
		for(int i = 0 ;i < str.length() ; i++){
			res += convert2Special(str.charAt(i));
		}
		return res;
	}
	
	/**
	 * @param c json字符 如[]"{}
	 * @return 特殊字符 用户无法输入
	 */
	private static char convert2Special(char c){
		//json字符转特殊字符 {卍      }卐       [¤   ]♋      "♍       ,¶   :§
		
		char res = '\u0000'; //char默认\u0000
		switch (c) {
		case '{':
			res = '卍';
			break;
		case '}':
			res = '卐';
			break;
		case '[':
			res = '¤';
			break;
		case ']':
			res = '♋';
			break;
		case '"':
			res = '♍';
			break;
		case ',':
			res = '¶';
			break;
		case ':':
			res = '§';
			break;

		default:
			res = c;
			break;
		}
		return res;
	}
	
	//定义回调接口
	public interface SmsBackupRestoreListener{
		void show();
		void dismiss();
		void setMax(int max);
		void setProgress(int progress);
	}
	
	/**
	 * 短信备份
	 * @param context
	 */
	public static void smsBackup(final SmsBackupRestoreListener pd , final Activity context){
		class Data{
			int progress = 0;
		}
		final Data data = new Data();
		File file = null;
		//将短信数据存入sd卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ 
			//sd卡已挂载
			if(Environment.getExternalStorageDirectory().getFreeSpace() < 1024 * 1024 * 5){
				//剩余空间小于5m
				throw new RuntimeException("sd卡空间不足");
			}else{
				//正常 创建短信备份的json文件
				file = new File(Environment.getExternalStorageDirectory(),"smses.json");
			}
		}else{
			throw new RuntimeException("sd卡未挂载");
		}
		try {
			//1.文件输出流
			final PrintWriter out = new PrintWriter(file);
			//短信json格式  {"smses":[{"address":"132333","date":"322143432432","body":"hello","type":"1"},{"":"","":""}]}
			out.println("{\"smses\":["); //写json头
			//2.短信数据库 查询短信记录
			Uri uri = Uri.parse("content://sms/");
			final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","body","type"}, null, null, null);
			
			//判断是否有短信
			if(cursor.getCount() == 0){
				Toast.makeText(context, "没有短信可备份", 0).show();
				return;
			}
			
			pd.setMax(cursor.getCount());
			pd.show();
			//3.子线程拷贝数据
			new Thread(){
				public void run() {
					String sms = null;
					while(cursor.moveToNext()){
						data.progress++;
						//游标循环一条数据 写一条数据
						//取一条短信{"address":"132333","date":"322143432432","body":"hello","type":"1"},
						sms = "{";
						sms += "\"address\":\"" + cursor.getString(0) + "\",";
						sms += "\"date\":\"" + cursor.getString(1) + "\",";
						sms += "\"body\":\"" + convert2Special(EncodeUtils.encode(cursor.getString(2), MyConstants.SEED))+ "\",";
						sms += "\"type\":\"" + cursor.getString(3) + "\"}";
						
						//判断是否是最后一条数据
						if(cursor.isLast()){
							sms += "]}"; //最后一条
						}else{
							sms += ",";
						}
						
						//把当前curosr遍历的数据写入到文件中
						out.println(sms);
						out.flush(); //刷新
						
						//设置当前进度
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								pd.setProgress(data.progress);
							}
						});
						SystemClock.sleep(300);
					}
					//备份结束
					//关闭输出流和游标
					out.close();
					cursor.close();
					
					//关闭进度条
					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.dismiss();
						}
					});
				};
			}.start();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * 短信还原
	 * @param context
	 */
	public static void smsRestore(final ProgressDialog pd , final Activity context){
		class Data{
			int progress;
		}
		final Data data = new Data();
		File file = null;
		//将短信数据存入sd卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){ 
			// 正常 获得短信备份的json文件
			file = new File(Environment.getExternalStorageDirectory(),"smses.json");
		}else{
			throw new RuntimeException("sd卡未挂载");
		}
		
		//备份文件存在  还原
		
		//1.获得短信json数据
		String smsJson = null;
		try {
			smsJson = stream2String(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//2. 创建class类 属性
		Gson gson = new Gson();
		//3.解析属性
		final SmsJsonData jsonData = gson.fromJson(smsJson, SmsJsonData.class);
	
		//判断json文件是否为空
		if(jsonData == null){
			Toast.makeText(context, "备份文件为空", 0).show();
			return;
		}
		//设置进度总大小
		pd.setMax(jsonData.smses.size());
		pd.show();
		
		//4.子线程  遍历jsonData 将每条短信写入数据库
		final Uri uri = Uri.parse("content://sms/");
		new Thread(){
			public void run() {
				//循环遍历每一条短信数据
				for (Sms sms : jsonData.smses) {
					//查询当前这条数据  是否存在  date时间是唯一标识短信是否唯一的
					Cursor cursor = context.getContentResolver().query(uri, new String[]{"date"}, "date=?"  , new String[]{sms.date+""}, null);
					
					SystemClock.sleep(300);
					data.progress++;
					//设置当前进度
					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							pd.setProgress(data.progress);
						}
					});
					
					if(!cursor.moveToFirst()){ 
						//如果短信里不存在这条信息 则还原这条数据
						ContentValues values = new ContentValues();
						values.put("address", sms.address);
						values.put("date", sms.date);
						values.put("type", sms.type);
						values.put("body", convert2Source(EncodeUtils.encode((sms.body),MyConstants.SEED)));
						//如果该短信不存在还原该短信
						context.getContentResolver().insert(uri, values);
					}
					cursor.close();
				}
				//关闭对话框
				SystemClock.sleep(200);
				context.runOnUiThread(new Runnable() {  
					@Override
					public void run() {
						pd.dismiss();
					}
				});
			};
		}.start();
	}
	
	public class SmsJsonData{
		public List<Sms> smses;
		public class Sms{
			public String address;
			public String body; 
			public long date; //数据库字段设计的类型是Integer  在sqlite中Integer可以放int long ... 
			public int type; //数据库字段设计 integer
		}
	}
}
