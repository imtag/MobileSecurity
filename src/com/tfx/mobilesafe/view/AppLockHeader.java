package com.tfx.mobilesafe.view;

import com.tfx.mobilesafe.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppLockHeader extends RelativeLayout {

	private TextView tv_unlock;
	private TextView tv_lock;

	public AppLockHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initEvent();
	}
	private OnLockChangeListener mOnLockChangeListener;

	//按钮的点击事件 方法
	public void setOnLockChangListener(OnLockChangeListener listener){
		this.mOnLockChangeListener = listener;
	}
	
	//按钮的回调接口
	public interface OnLockChangeListener{
		/**
		 * @param isLock  true:已加锁 false:未加锁
		 */
		void onLockChanged(boolean isLock); //这个方法里做自己的事
	}
	
	private void initEvent() {
		OnClickListener listener = new OnClickListener() {
			boolean isLock = false;
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_applock_lock:
					tv_lock.setBackgroundResource(R.drawable.tab_left_pressed);
					tv_lock.setTextColor(Color.WHITE);
					tv_unlock.setBackgroundResource(R.drawable.tab_right_default);
					tv_unlock.setTextColor(Color.GRAY);
					isLock = true;
					break;
					
				case R.id.tv_applock_unlock:
					tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
					tv_unlock.setTextColor(Color.WHITE);
					tv_lock.setBackgroundResource(R.drawable.tab_right_default);
					tv_lock.setTextColor(Color.GRAY);
					isLock = false;
					break;

				default:
					break;
				}
				//处理回调 把数据状态回调给用户
				if(mOnLockChangeListener != null){
					mOnLockChangeListener.onLockChanged(isLock);
				}
			}
		};
		tv_lock.setOnClickListener(listener);
		tv_unlock.setOnClickListener(listener);
	}

	private void initView() {
		View view = View.inflate(getContext(), R.layout.view_applocked_header,
				this);
		tv_lock = (TextView) view.findViewById(R.id.tv_applock_lock);
		tv_unlock = (TextView) view.findViewById(R.id.tv_applock_unlock);
	}

	public AppLockHeader(Context context) {
		this(context, null);
	}
}
