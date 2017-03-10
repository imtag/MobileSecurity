package com.tfx.mobilesafe.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-14
 * @desc      字符串加密算法 

 * @version   $Rev: 29 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-15 22:46:04 +0800 (星期一, 15 八月 2016) $
 * @id        $Id: EncodeUtils.java 29 2016-08-15 14:46:04Z tfx $
 */

public class EncodeUtils {
	/**
	 * @param str 需要加密的字符串
	 * @param seed 加密种子
	 * @return 加密后的字符串
	 */
	public static String encode(String str,byte seed){
		try {
			
			/*
			 * 加密:对字符串每个字节进行异或^运算
			 * 解密:对加密过的字符串再进行一次加密  两次异或就是原字符串
			 * 例如  2^3 ^3 = 2  两个异或结果是自己
			 */
			
			//1.字符串转byte数组
			byte[] bytes = str.getBytes("gbk");
			//2.遍历byte数组,使每个字节异或seed
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] ^= seed;
			}
			//3.返回异或后的字符串
			return new String(bytes,"gbk");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
