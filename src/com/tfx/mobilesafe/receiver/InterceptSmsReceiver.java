
package com.tfx.mobilesafe.receiver;

import com.tfx.mobilesafe.dao.BlackListDao;
import com.tfx.mobilesafe.db.BlackListDB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-1
 * @desc      拦截短信的广播

 * @version   $Rev: 21 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-01 22:24:36 +0800 (星期一, 01 八月 2016) $
 * @id        $Id: InterceptSmsReceiver.java 21 2016-08-01 14:24:36Z tfx $
 */

public class InterceptSmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		BlackListDao dao = new BlackListDao(context);
		
		Object[] smsDatas = (Object[]) intent.getExtras().get("pdus"); //获得多条短信内容 pdus短信格式
		for (Object data : smsDatas) {
			//获得SmsMessage实例 就是每一条短信对象
			SmsMessage sms = SmsMessage.createFromPdu((byte[])data);
			String phone = sms.getOriginatingAddress(); //号码
			int mode = dao.getMode(phone);
			//是短信拦截或者全部拦截
			if((mode & BlackListDB.SMS_MODE) != 0){ //!=0   说明mode要么是短信拦截(1) 或者全部拦截(3) 结果才不为0  因为 1&1=1 1&3(11)=1 而1$2(10)=0;
				abortBroadcast(); //终止广播运行  拦截短信
			}
		}
	}
}
