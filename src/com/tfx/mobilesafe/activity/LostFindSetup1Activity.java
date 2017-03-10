package com.tfx.mobilesafe.activity;

import com.tfx.mobilesafe.R;


public class LostFindSetup1Activity extends BaseSetupActivity {

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		super.initEvent();
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_lostfind_setup1);
		super.initView();
	}

	@Override
	protected void startNext() {
		startPage(LostFindSetup2Activity.class);
	}

	@Override
	protected void startPrev() {
		
	}
	
}
