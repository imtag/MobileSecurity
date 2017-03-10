package com.tfx.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View; 
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.AddressDao;

public class PhoneLocationActivity extends Activity {
	private EditText et_number;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initEvent();
		super.onCreate(savedInstanceState);    
	}
	
   //输入框文字改变监听
	private void initEvent() {
		et_number.addTextChangedListener(new TextWatcher() {
			//文本变化
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				query(null);
			}
			//变化前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			//变化后
			@Override   
			public void afterTextChanged(Editable s) {
			}
		});
	}

	//点击查询按钮
	public void query(View v) {  
		//获取用户输入的号码 判断是否为空
		String phone = et_number.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			//抖动效果
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
			//震动效果
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(300); //震300停100 类推
			return;
			
		}
		try{
			tv_result.setText("归属地:"+AddressDao.getLocation(phone));
		}catch(Exception e){
		}
	}
	
	private void initView() {
		setContentView(R.layout.activity_phone_laction);
		et_number = (EditText) findViewById(R.id.et_phonelaction_number);
		tv_result = (TextView) findViewById(R.id.tv_phonelaction_result);
	}
}
