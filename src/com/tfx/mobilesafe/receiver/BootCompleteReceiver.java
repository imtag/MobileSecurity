package com.tfx.mobilesafe.receiver;

import com.tfx.mobilesafe.service.LostFindService;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-24
 * @desc      手机重启完成的广播接收者

 * @version   $Rev: 18 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-28 23:01:11 +0800 (星期四, 28 七月 2016) $
 * @id        $Id: BootCompleteReceiver.java 18 2016-07-28 15:01:11Z tfx $
 */

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//手机重启完成
		
		//1.检测sim卡是否变更
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber();
		if(!simSerialNumber.equals(SPUtils.getString(context, MyConstants.SIMNUMBER, ""))){
			//sim卡不一致  发送短信给安全号码
			SmsManager sm = SmsManager.getDefault(); //得到短信管理者 
			sm.sendTextMessage(SPUtils.getString(context, MyConstants.SAFENUMBER, ""), null ,"sim card changed,maybe your mibele lost", null, null);
		}
		//2.重启完成就启动防盗服务
		if(SPUtils.getBoolean(context, MyConstants.BOOTCOMPLETE, false)){ //如果重启完成
			Intent service = new Intent(context,LostFindService.class); //启动防盗服务
			context.startService(service );
		}
	}

}
