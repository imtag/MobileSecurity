package com.tfx.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.service.BlackListService;
import com.tfx.mobilesafe.service.IncomingLocationService;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;
import com.tfx.mobilesafe.utils.ServiceUtils;
import com.tfx.mobilesafe.view.SettingCenterItem;
import com.tfx.mobilesafe.view.SettingCenterItem.OnToggleChangeListener;
import com.tfx.mobilesafe.view.ShowLocationStyleDialog;

public class SettingCenterActivity extends Activity {   
	private SettingCenterItem sci_autoupdate;
	private SettingCenterItem sci_blackintercept; 
	private OnToggleChangeListener listener;
	private SettingCenterItem sci_incominglocation;
	private SettingCenterItem sci_locationstyle;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initData();
	}
	
	private void initData() { 
		// 初始化黑名单拦截开关的状态 判断服务是否开启,开启了就设置为开,
		sci_blackintercept.setToggleState(ServiceUtils.isServiceRunning(this, "com.tfx.mobilesafe.service.BlackListService"));
		//初始化自动更新开关的状态
		sci_autoupdate.setToggleState(SPUtils.getBoolean(getApplicationContext(), MyConstants.AUTO_UPDATE, false));
		//初始化来电归属地的开关状态
		sci_incominglocation.setToggleState(ServiceUtils.isServiceRunning(this, "com.tfx.mobilesafe.service.IncomingLocationService"));
		//初始化来电归属地风格
		int index = SPUtils.getInt(getApplicationContext(), MyConstants.LOCATIONSELECTEDINDEX, 0);
		sci_locationstyle.setText("归属地显示风格(" + ShowLocationStyleDialog.styleNames[index] + ")");
	}
         
	private void initEvent() {
		listener = new OnToggleChangeListener() {
			@Override 
			public void onToggleChange(View v,boolean isOpen) {
				switch (v.getId()) {
				case R.id.sci_autoupdate: {
					//自动更新单击事件
					SPUtils.putBoolean(getApplicationContext(), MyConstants.AUTO_UPDATE, isOpen);
					break;
				}
				case R.id.sci_blackintercept: {
					//黑名单拦截事件
					if(isOpen){ //状态为开
						//开启黑名单拦截服务服务
						Intent service = new Intent(SettingCenterActivity.this,BlackListService.class);
						startService(service);
					}else{
						//关闭服务 
						Intent service = new Intent(SettingCenterActivity.this,BlackListService.class);
						stopService(service);
						SPUtils.putBoolean(getApplicationContext(), MyConstants.IS_FIRST_USE_BLACKLIST, false);
					}
					break;
				}
				case R.id.sci_incoming_phonelocation:{
					if(isOpen){//开关打开
						//开启归属地显示服务
						Intent service = new Intent(SettingCenterActivity.this,IncomingLocationService.class);
						startService(service);
					}else{
						//关闭归属地显示服务
						Intent service = new Intent(SettingCenterActivity.this,IncomingLocationService.class);
						stopService(service);
					}
					break;
				}
				case R.id.sci_locationstyle:{
					//来电归属地风格 
					//显示自定义对话框
					ShowLocationStyleDialog dialog = new ShowLocationStyleDialog(SettingCenterActivity.this,sci_locationstyle);
					dialog.show();
					break;
				}
				default:
					break;
				}
			}
		}; 
		//自动更新添加事件
		sci_autoupdate.setOnToggleChangeListener(listener);
		sci_blackintercept.setOnToggleChangeListener(listener);
		sci_incominglocation.setOnToggleChangeListener(listener);
		sci_locationstyle.setOnToggleChangeListener(listener);
	}

	private void initView() {
		setContentView(R.layout.activity_settingcenter);
	
		sci_autoupdate = (SettingCenterItem) findViewById(R.id.sci_autoupdate);
		sci_blackintercept = (SettingCenterItem) findViewById(R.id.sci_blackintercept);
		sci_incominglocation = (SettingCenterItem) findViewById(R.id.sci_incoming_phonelocation);
		sci_locationstyle = (SettingCenterItem) findViewById(R.id.sci_locationstyle);
	
	}
}
