package com.tfx.mobilesafe.receiver;

import java.io.IOException;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-25
 * @desc      短信拦截的广播接收者

 * @version   $Rev: 21 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-01 22:24:36 +0800 (星期一, 01 八月 2016) $
 * @id        $Id: LostFindSmsReceiver.java 21 2016-08-01 14:24:36Z tfx $
 */

public class LostFindSmsReceiver extends BroadcastReceiver {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	boolean isPlaying = false ; //音乐是否正在播放的标记
	private LocationManager lm;

	//当接收到广播时回调
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		//初始化设备管理的对象
		mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//定位管理者
		lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		Object[] smsDatas = (Object[]) intent.getExtras().get("pdus"); //获得多条短信内容 pdus短信格式
		for (Object data : smsDatas) {
			//获得SmsMessage实例 就是每一条短信对象
			SmsMessage sms = SmsMessage.createFromPdu((byte[])data);
			String body = sms.getMessageBody(); //短信内容 
			//根据短信内容进行拦截   
			if(body.equals("#*music*#")){
				//如果接收到一条短信 音乐会播放 播放途中 又收到短信 就会两个音乐都播放 所以要判断是否播放中
				if(!isPlaying){ //音乐不在播放中
					MediaPlayer player = MediaPlayer.create(context, R.raw.m);
					try {
						player.prepare();//准备
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.start();//开始播放
					isPlaying = true;
					player.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							// 音乐播放完成回调
							isPlaying = false;
						}   
					}); 
				}
				abortBroadcast(); //停止广播传递  就是拦截短信
				
			}else if(body.equals("#*gps*#")){
				//定位当前位置 并将位置发送给安全号码
				getLocation(context);
				abortBroadcast(); //停止广播传递  
				
			}else if(body.equals("#*lockscreen*#")){
				mDPM.resetPassword("110", 0); //重置锁屏解锁密码
				mDPM.lockNow(); //锁屏
				abortBroadcast(); // 停止广播传递

			} else if (body.equals("#*wipedata*#")) {
				mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE); // 清除sd卡数据
				abortBroadcast(); // 停止广播传递
			}

		}
	}

	private void getLocation(final Context context) {
		// TODO Auto-generated method stub
		//定位api 
		
		//provider  定位方式, minTime 多久定位一次(0时刻更新) , minDistance 定位距离(0最详细位置) , listener 如果位置发生改变就自动监听
		lm.requestLocationUpdates("gps", 0, 0, new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}
			
			//当位置发生改变 回调该方法
			@Override
			public void onLocationChanged(Location location) {
				//位置发生改变  监听
				float accuracy = location.getAccuracy(); //精确度
				double longitude = location.getLongitude(); //经度 
				double latitude = location.getLatitude(); //纬度
				double altitude = location.getAltitude(); //海拔
				
				String mess = "accuracy:"+accuracy+"\n"+"longitude:"+longitude+"\n"+"latitude:"+latitude+"\n"+"altitude:"+altitude+"\n";
				//发送当前位置信息给安全号码 
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(SPUtils.getString(context, MyConstants.SAFENUMBER, ""), null, mess, null, null);
				
				//发一条短信只定位一次 不然如果位置一直改变  安全号码将会一直收到短信  
				//停止监控
				lm.removeUpdates(this);
			}
		});
	}
}