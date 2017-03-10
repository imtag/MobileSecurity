package com.tfx.mobilesafe.activity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

public class LostFindSetup2Activity extends BaseSetupActivity {
	private ImageView iv_bindstate;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_lostfind_setup2);
		iv_bindstate = (ImageView) findViewById(R.id.setup2_iv_bindstate);
	}
	
	@Override
	protected void initData() {
		//初始化sim卡是否绑定的状态
		String simNumber = SPUtils.getString(this, MyConstants.SIMNUMBER, "");
		if(TextUtils.isEmpty(simNumber)){
			//没绑定sim卡  
			iv_bindstate.setImageResource(R.drawable.unlock); //更改状态图标
		}else{
			//绑定了sim卡
			iv_bindstate.setImageResource(R.drawable.lock); //更改状态图标
		}
	}

	@Override
	protected void startNext() {
		String simNumber = SPUtils.getString(this, MyConstants.SIMNUMBER, "");
		if(TextUtils.isEmpty(simNumber)){
			//没绑定sim卡  不进行下一页
			Toast.makeText(this, "请先绑定sim卡", 0).show();
			return;
		}else{
			//进入下一页
			startPage(LostFindSetup3Activity.class);
		}
	}

	@Override
	protected void startPrev() {
		startPage(LostFindSetup1Activity.class);
	}
	
	//点击锁定/解锁按钮
	public void bindSim(View v) {
		//判断是否具绑定了sim卡
		String simNumber = SPUtils.getString(this, MyConstants.SIMNUMBER, null);
		if(TextUtils.isEmpty(simNumber)){
			//没绑定sim卡  进行sim卡的绑定
			//获得电话管理者
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String number = tm.getSimSerialNumber(); //获得sim卡序列号
			SPUtils.putString(this, MyConstants.SIMNUMBER, number); //把sim卡序列号存到sp里
			iv_bindstate.setImageResource(R.drawable.lock); //更改状态图标
		}else{
			//绑定了sim卡  点击该按钮进行sim卡解绑
			SPUtils.putString(this, MyConstants.SIMNUMBER, ""); //清空sim卡序列号
			iv_bindstate.setImageResource(R.drawable.unlock); //更改状态图标
		}
	}
}
