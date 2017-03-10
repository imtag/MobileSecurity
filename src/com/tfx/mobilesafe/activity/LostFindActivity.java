package com.tfx.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-22
 * @desc      手机防盗丢失界面

 * @version   $Rev: 16 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-24 23:30:04 +0800 (星期日, 24 七月 2016) $
 * @id        $Id: LostFindActivity.java 16 2016-07-24 15:30:04Z tfx $ 
 */

public class LostFindActivity extends Activity {

	private TextView tv_safenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 直接进行逻辑判断 是否设置向导完成
		if (SPUtils.getBoolean(getApplicationContext(),MyConstants.ISSETUPGUIDEFINISH, false)) {
			// 设置向导完成 
			initView();
			
			initData();
		} else {
			// 设置向导未完成,进入第一个设置向导页面
			Intent intent = new Intent(this,LostFindSetup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	//重新进入设置向导页面按钮
	public void intosetup(View v) {
		Intent intent = new Intent(this,LostFindSetup1Activity.class);
		startActivity(intent);
		finish();
	}
	
	private void initData() {
		//显示安全号码
		tv_safenumber.setText(SPUtils.getString(getApplicationContext(), MyConstants.SAFENUMBER, null));
	}
	
	private void initView() {
		setContentView(R.layout.activity_lostfind);
		tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
	}
}
