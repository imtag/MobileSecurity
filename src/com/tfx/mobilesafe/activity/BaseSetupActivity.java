package com.tfx.mobilesafe.activity;

import com.tfx.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	
	private GestureDetector mGD;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		
		initEvent();
		
		initData();
		
		initGuesture();
	}
	
	/**
	 * 添加手势识别器(触摸事件)
	 */
	@SuppressWarnings("deprecation")
	private void initGuesture() {
		mGD = new GestureDetector(new MyOnGuestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				/**
				 * 滑动 触摸时执行该方法
				 * 
				 * e1:按下的点 
				 * e2:松开的点
				 * velocityX:x方向的速度  单位px/s
				 * velocityY:y方向的速度
				 */
				
				//如果横向移动长度 比 纵向移动长度更长 就是横向移动
				if(Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())){
					//是横向滑动
					//判断速度
					if(Math.abs(velocityY) > 50){//如果速度小于50 则不纳入事件
						if(velocityX > 0){
							//往右滑
							prePage(null);
						}else{
							//往左滑
							nextPage(null);
						}
					}
				}
				return true;
			}
		});
	}
	
	//手势调用
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mGD != null){
			mGD.onTouchEvent(event);
			return true; //消费事件  handler
		}
		return super.onTouchEvent(event);
	}

	//下一步按钮的单击事件
	public void nextPage(View v){
		//下一个页面 抽象方法 让子类实现  调用指定方法跳到指定页面
		startNext();
		//位移动画  页面未跳转 动画不会执行
		nextPageAnimation();
	}
	
	//下一步按钮的单击事件
	public void prePage(View v){
		//上一个跳转
		startPrev();
		//位移动画
		prevPageAnimation();
	}

	/**
	 * 子类必须重写该方法  跳到指定页面
	 */
	protected abstract void startNext(); //抽象 必须实现
	protected abstract void startPrev();
	
	//进入下一页时的平移动画
	private void nextPageAnimation() {
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);
	}
	
	//进入上一页时的动画
	private void prevPageAnimation() {
		overridePendingTransition(R.anim.prev_enter_anim, R.anim.prev_exit_anim);
	}
	
	/**
	 * 页面跳转的代码实现,子类调用该方法,跳到指定页面
	 * @param type 要跳转的页面的class
	 */
	public void startPage(Class type){
		Intent intent = new Intent(this,type);
		startActivity(intent);
		finish();//关闭自己
	}

	/**
	 *  让子类重写该方法完成数据初始化  访问权限不能私有  
	 */
	protected void initData() {
	}

	/**
	 *  让子类重写该方法完成事件初始化  访问权限不能私有  
	 */
	protected void initEvent() {
	}

	/**
	 *  让子类重写该方法完成界面初始化  访问权限不能私有  
	 */
	protected void initView() {
	}
	
	private class MyOnGuestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
}
