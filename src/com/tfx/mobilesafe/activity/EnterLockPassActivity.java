package com.tfx.mobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tfx.mobilesafe.R;

public class EnterLockPassActivity extends Activity {
	private ImageView iv_icon;
	private Button bt_confirm;
	private EditText et_password;
	private String packName;
	private HomeReceiver homeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
		initHomeReceiver();
	}
	
	//监听home键 不能使用onKeyDown方法监听 因为home键系统设计人员考虑频繁使用 
	//需要使用广播监听
	private class HomeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//此时可能有多个意图同时执行 所以需要判断是否包含home键动作
			if(intent.getAction().contains(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
				intoMain();//进入主页面
			}
		}
	}
	
	private void initHomeReceiver() {
		//注册home键广播
		homeReceiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homeReceiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(homeReceiver);
	}
	
	//监听键盘
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//监听返回键
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//进入手机主界面
			intoMain();
		}
		return super.onKeyDown(keyCode, event);
	}
 
	private void intoMain() {
		/*
		 * 查看上层源码 主页面(lanuch)的过滤器
		 *   <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.MONKEY"/>
            </intent-filter>
		 */
		//隐士意图打开主页面
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		
		//关闭自己
		finish();
	}

	private void initEvent() {
		bt_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				if(TextUtils.isEmpty(password)){
					Toast.makeText(getApplicationContext(), "口令不能为空", 0).show();
					return ;
				}
				if(password.equals("1")){
					//告诉看门狗是熟人 知道口令 放行 
					Intent intent = new Intent("tfx.shuren"); //参数 action
					intent.putExtra("shuren", packName);//把包名传递过去
					sendBroadcast(intent);//发送广播
					
					finish();// 关闭输入口令Activity
				}else{
					Toast.makeText(getApplicationContext(), "请输入正确口令", 0).show();
					return ;
				}
			}
		});
	}

	private void initData() {
		//获取额外参数 packname  用来获取应用icon
		packName = getIntent().getStringExtra("packname");
		PackageManager pm = getPackageManager();
		try {
			//设置图标为当前应用的icon  
			iv_icon.setImageDrawable(pm.getApplicationIcon(packName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		setContentView(R.layout.activity_enterlockpass);
		iv_icon = (ImageView) findViewById(R.id.iv_enterlockpass);
		bt_confirm = (Button) findViewById(R.id.bt_enterlockpass);
		et_password = (EditText) findViewById(R.id.et_enterlockpass);
		
	}
}
