package com.tfx.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.receiver.MyDeviceAdminReceiver;
import com.tfx.mobilesafe.service.LostFindService;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;
import com.tfx.mobilesafe.utils.ServiceUtils;
import com.tfx.mobilesafe.utils.ShowToastUtils;

public class LostFindSetup4Activity extends BaseSetupActivity {
	private CheckBox cb_isopenlostfind;
	private TextView tv_showstate;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_lostfind_setup4);
		cb_isopenlostfind = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
		tv_showstate = (TextView) findViewById(R.id.tv_setup4_showstate);
		//初始化设备管理的对象
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this, MyDeviceAdminReceiver.class);
	}
	
	@Override
	protected void initData() {
		//初始化复选框的状态
		boolean serviceState = ServiceUtils.isServiceRunning(getApplicationContext(),"com.tfx.mobilesafe.service.LostFindService"); //判断服务是否正在运行
		if(serviceState){
			cb_isopenlostfind.setChecked(true);
		}else{
			cb_isopenlostfind.setChecked(false);
		}
	}
	
	@Override
	protected void initEvent() {
		//复选框事件
		cb_isopenlostfind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){//复选框为勾选
					//强制开启设备管理器才能开启防盗保护
					if(!mDPM.isAdminActive(mDeviceAdminSample)){ //设备管理器没激活
						activateDevice(); //进入激活界面
					}else{ 
						//激活了设备管理器
						//复选框选中状态  开启防盗服务
						Intent service = new Intent(getApplicationContext(),LostFindService.class);
						startService(service );
						tv_showstate.setText("防盗保护已经开启");
					}
					
					//服务是否开启的状态不能保存的sp中  因为用户可以在应用管理中关闭服务  所以需要动态判断服务状态
					//使用ActivityManager动态判断服务是否开启
					
				}else{
					//关闭
					//复选框关闭状态  关闭防盗服务
					Intent service = new Intent(getApplicationContext(),LostFindService.class);
					stopService(service );
					tv_showstate.setText("防盗保护已经关闭");
				}
			}
		});
	}
	
	//激活设备管理的界面关闭时回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(mDPM.isAdminActive(mDeviceAdminSample)){ //激活了
			//开启防盗服务
			Intent service = new Intent(LostFindSetup4Activity.this,LostFindService.class);
			startService(service );
			//设置显示状态为开启
			tv_showstate.setText("手机防盗已经开启");
		}else{ //没激活
			//设置复选框为未勾选
			cb_isopenlostfind.setChecked(false);
			ShowToastUtils.showToast(LostFindSetup4Activity.this, "先激活,才能开启防盗服务");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 打开激活设备管理器的界面
	public void activateDevice() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mDeviceAdminSample);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启激活设备管理");
		startActivityForResult(intent, 1);
	}

	//完成设置向导,跳到手机防盗页面时
	@Override
	protected void startNext() {
		if(!cb_isopenlostfind.isChecked()){
			//没勾选开启防盗 提醒必须开启
			ShowToastUtils.showToast(LostFindSetup4Activity.this, "请先勾选开启防盗保护");
		}else{
			//保存设置向导完成的状态
			SPUtils.putBoolean(getApplicationContext(), MyConstants.ISSETUPGUIDEFINISH, true);
			//只要勾选了复选框  就添加手机重启  自动开启手机防盗服务
			SPUtils.putBoolean(getApplicationContext(), MyConstants.BOOTCOMPLETE, true);
			//跳到手机防盗页面
			startPage(LostFindActivity.class);
		}
	}

	@Override
	protected void startPrev() {
		startPage(LostFindSetup3Activity.class);
	}
}
