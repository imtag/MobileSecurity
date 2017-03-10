package com.tfx.mobilesafe.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-22
 * @desc      md5加密工具类

 * @version   $Rev: 34 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-01 22:21:45 +0800 (星期四, 01 九月 2016) $
 * @id        $Id: MD5Utils.java 34 2016-09-01 14:21:45Z tfx $
 */

public class MD5Utils {
	/**
	 * 获取文件的md5值
	 * @param filePath 文件路径
	 * @return 该文件的md5值
	 */
	public static String getFileMd5(String filePath){
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			//读取文件
			FileInputStream fis = new FileInputStream(new File(filePath));
			byte[] buffer = new byte[1024 * 10];
			int len = fis.read(buffer);
			while(len != -1){
				md.update(buffer,0,len); //不停读取文件内容
				len = fis.read(buffer);//继续读取
			}
			
			//读取文件完毕
			//md5加密后的字节数组
			byte[] digest = md.digest();
			//把字节数组转成字符串 1 byte=8bit 一个字符=2byte=16bit
			for (byte b : digest) {
				///把每个字节转成字符(16进制)
				int d = b & 0x000000ff;
				String s = Integer.toHexString(d);
				if(s.length() == 1){
					//转为16进制后只有一位，前面加0
					s = "0" + s;
				}
				sb.append(s);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb + "";
	}
	
	/**
	 * @param str	需要加密的字符串 
	 * @return	字符串加密后的md5值
	 */
	public static String encode(String str){
		String res = "";
		String s = "";
		try {
			//获得md5加密实例
			MessageDigest md = MessageDigest.getInstance("md5");
			//md5加密后的字节数组
			byte[] digest = md.digest(str.getBytes());
			//把字节数组转成字符串 1 byte=8bit 一个字符=2byte=16bit
			for (byte b : digest) {
				///把每个字节转成字符(16进制)
				int d = b & 0x000000ff;
				s = Integer.toHexString(d);
				if(s.length() == 1){
					//转为16进制后只有一位，前面加0
					s = "0" + s;
				}
				res = res + s ;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
