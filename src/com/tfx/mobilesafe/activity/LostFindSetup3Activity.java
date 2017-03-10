package com.tfx.mobilesafe.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;
import com.tfx.mobilesafe.utils.ShowToastUtils;

public class LostFindSetup3Activity extends BaseSetupActivity {
	private EditText setup3_et_safenumber;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_lostfind_setup3);
		setup3_et_safenumber = (EditText) findViewById(R.id.setup3_et_safenumber);
	}
	
	//点击按钮跳到所有联系人界面   点击某个好友  关闭Activity 在编辑框显示选择的好友的号码
	public void setup3_bt_selectsafenumber(View v) {
		Intent intent = new Intent(this,FriendsActivity.class);
		startActivityForResult(intent, 0);//开启一个携带结果的Activity
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//获取意图携带的数据  所选择的好友
		if(data != null){ //选择了好友才回显  
			String safeNum = data.getStringExtra(MyConstants.SAFENUMBER);
			setup3_et_safenumber.setText(safeNum);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void initData() {
		//初始化数据 设置安全号码输入框的号码
		String safenumber = SPUtils.getString(this, MyConstants.SAFENUMBER, "");
		setup3_et_safenumber.setText(safenumber); //设置输入的安全号码
		setup3_et_safenumber.setSelection(setup3_et_safenumber.getText().toString().trim().length()); //设置光标停留的位置
	}
	

	@Override
	protected void startNext() {
		String safeNumber = setup3_et_safenumber.getText().toString().trim();
		if(TextUtils.isEmpty(safeNumber)){
			//安全号码为空 不允许进入下一页
			ShowToastUtils.showToast(this, "安全号码不能为空");
		}else{
			//将安全号码保存到sp
			SPUtils.putString(this, MyConstants.SAFENUMBER,safeNumber);
			//安全号码不为空 可以下一步
			startPage(LostFindSetup4Activity.class);
		}
	}
	
	@Override
	protected void startPrev() {
		startPage(LostFindSetup2Activity.class);
	}
}
