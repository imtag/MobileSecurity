package com.tfx.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.ServiceUtils;
import com.tfx.mobilesafe.utils.SmsUtils;
import com.tfx.mobilesafe.utils.SmsUtils.SmsBackupRestoreListener;
import com.tfx.mobilesafe.view.SettingCenterItem;
import com.tfx.mobilesafe.view.SettingCenterItem.OnToggleChangeListener;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-14
 * @desc      高级工具界面

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: AToolsActivity.java 35 2016-09-10 13:47:42Z tfx $
 */

public class AToolsActivity extends Activity {
	private SettingCenterItem sci_mobilelocation;
	private SettingCenterItem sci_servicelocation;
	private SettingCenterItem sci_smsbackup;
	private SettingCenterItem sci_smsrestore;
	private NumberProgressBar npb_progress;
	private SettingCenterItem sci_applock;
	private SettingCenterItem sci_watchdog1;
	private SettingCenterItem sci_watchdog2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initData();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initData() {
		//初始化accesibility看门狗数据
		sci_watchdog1.setToggleState(ServiceUtils.isServiceRunning(getApplicationContext(), "com.tfx.mobilesafe.service.MyAccessibilityService"));
		//初始化线程监控 看门狗
		sci_watchdog2.setToggleState(ServiceUtils.isServiceRunning(getApplicationContext(), "com.tfx.mobilesafe.service.WatchDogService"));
	}

	private void initEvent() {
		OnToggleChangeListener listener = new OnToggleChangeListener() {
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				switch (v.getId()) {
				case R.id.sci_mobilelocation:
					Intent phoneLaction = new Intent(getApplicationContext(),PhoneLocationActivity.class);
					startActivity(phoneLaction);
					break;
				case R.id.sci_servicelocation:
					Intent serviceLaction = new Intent(getApplicationContext(),ServiceNumberActivity.class);
					startActivity(serviceLaction);
					break;
				case R.id.sci_smsbackup://短信备份
					smsBackup();
					break;
				case R.id.sci_smsrestore://短信还原
					smsRestore();
					break;
					
				case R.id.sci_applock:
					Intent appLock = new Intent(getApplicationContext(),AppLockActivity.class);
					startActivity(appLock);
					break;
				case R.id.sci_watchdog2:
					if(isOpen){
						//开启看门狗服务 线程版
						Intent service = new Intent(getApplicationContext(),com.tfx.mobilesafe.service.WatchDogService.class);
						startService(service);
					}else{
						//停止看门狗服务
						Intent service = new Intent(getApplicationContext(),com.tfx.mobilesafe.service.WatchDogService.class);
						stopService(service);
					}
					break;
				case R.id.sci_watchdog1:
					if(isOpen){
						//开启看门狗服务
						Intent service = new Intent(getApplicationContext(),com.tfx.mobilesafe.service.MyAccessibilityService.class);
						startService(service);
					}else{
						//停止看门狗服务
						Intent service = new Intent(getApplicationContext(),com.tfx.mobilesafe.service.MyAccessibilityService.class);
						stopService(service);
					}
					break;
				default:
					break;
				}
			}
		};
		sci_mobilelocation.setOnToggleChangeListener(listener );
		sci_servicelocation.setOnToggleChangeListener(listener);
		sci_smsbackup.setOnToggleChangeListener(listener);
		sci_smsrestore.setOnToggleChangeListener(listener);
		sci_applock.setOnToggleChangeListener(listener);
		sci_watchdog2.setOnToggleChangeListener(listener);
		sci_watchdog1.setOnToggleChangeListener(listener);
	}
	
	//短信还原
	private void smsRestore() {
		ProgressDialog pd = new ProgressDialog(AToolsActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		SmsUtils.smsRestore(pd , AToolsActivity.this);
	}

	//短信备份
	private void smsBackup() {
		final ProgressDialog pd = new ProgressDialog(AToolsActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		SmsUtils.smsBackup(new SmsBackupRestoreListener() {
			
			@Override
			public void show() {
//				pd.show();
				npb_progress.setProgress(0);
				npb_progress.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void setProgress(int progress) {
//				pd.setProgress(progress);
				npb_progress.setProgress(progress);
			}
			
			@Override
			public void setMax(int max) {
//				pd.setMax(max);
				npb_progress.setMax(max);
			}
			
			@Override
			public void dismiss() {
//				pd.dismiss();
				npb_progress.setVisibility(View.GONE);
			}
		},AToolsActivity.this);
	}
	
	private void initView() {
		setContentView(R.layout.activity_atools);
		sci_mobilelocation = (SettingCenterItem)findViewById(R.id.sci_mobilelocation);
		sci_servicelocation = (SettingCenterItem)findViewById(R.id.sci_servicelocation);
		sci_smsbackup = (SettingCenterItem) findViewById(R.id.sci_smsbackup);
		sci_smsrestore = (SettingCenterItem) findViewById(R.id.sci_smsrestore);
		npb_progress = (NumberProgressBar) findViewById(R.id.npb_progress);
		sci_applock = (SettingCenterItem) findViewById(R.id.sci_applock);
		sci_watchdog1 = (SettingCenterItem) findViewById(R.id.sci_watchdog1);
		sci_watchdog2 = (SettingCenterItem) findViewById(R.id.sci_watchdog2);
	}
}
