package com.tfx.mobilesafe.activity;

import com.tfx.mobilesafe.R;

import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	private ImageView welcome_iv;
	private AnimationSet set; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//如果第一次进入则跳到欢迎界面 否则进入splashactivity
		if(SPUtils.getBoolean(WelcomeActivity.this,MyConstants.IS_FIRST_USE,true)){
			//1.找到imageview控件
			initView();
			//2.初始化动画 
			initAnimation();
			//3.设置动画监听
			initAnimationListener();
		}else{
			Intent intent = new Intent(WelcomeActivity.this,SplashActivity.class);
			startActivity(intent);
			finish();//关闭当前activity
		}
	}

	private void initView() {
		setContentView(R.layout.activity_welcome); //加载布局
		welcome_iv = (ImageView) findViewById(R.id.welcome_iv);
	}

	private void initAnimation() {
		//2.设置多动画合集
		set = new AnimationSet(false); //false 每种动画用自己的动画插入器
		//2.1透明动画
		AlphaAnimation aa = new AlphaAnimation(
				0, 1.0f); //从0看不见到1完全看见
		
		//2.2缩放
		ScaleAnimation sa = new ScaleAnimation(
				0, 1.0f, //x和y从看不到放大到图片大小
				0, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f, //缩放的中心点是图片自身的x和y轴的一半(图片中心点)
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		//2.3旋转动画
		RotateAnimation ra = new RotateAnimation(
				0, 360, //从当前位置开始旋转一圈
				Animation.RELATIVE_TO_SELF, 0.5f, //旋转的中心点是图片的中心点(x和y轴的一半)
				Animation.RELATIVE_TO_SELF, 0.5f);
		//3.把动画添加到set集合
		set.addAnimation(aa);
		set.addAnimation(ra);
		set.addAnimation(sa);
		//4.设置动画合集执行时长
		set.setDuration(2000);
		//停留在动画结束位置
		set.setFillAfter(true);
		//5.开启iv图片控件的动画
		welcome_iv.startAnimation(set); 
	}
	
	private void initAnimationListener() {
		//6.设置动画的监听
		set.setAnimationListener(new AnimationListener() {
			//动画开始回调
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			//动画重复
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			//动画结束回调
			@Override
			public void onAnimationEnd(Animation animation) {
				//当动画结束,进入导航页面
				Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
				startActivity(intent);
				finish();//关闭当前activity
			}
		});
	}
}
