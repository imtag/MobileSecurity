package com.tfx.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tfx.mobilesafe.R;

/**
 * @author Tfx
 * @comp GOD
 * @date 2016-8-3
 * @desc 设置中心条目的自定义view
 * 
 * @version $Rev: 27 $
 * @auther $Author: tfx $
 * @date $Date: 2016-08-12 22:27:41 +0800 (星期五, 12 八月 2016) $
 * @id $Id: SettingCenterItem.java 27 2016-08-12 14:27:41Z tfx $
 */

public class SettingCenterItem extends RelativeLayout {

	private View rootView;
	private ImageView iv_toggle;
	private TextView tv_desc;
	private boolean isOpen = false;//开关状态默认关

	/**
	 * 布局文件中初始化调用
	 * 
	 * @param context
	 *            上下文
	 * @param attrs
	 *            attrs 属性
	 */
	public SettingCenterItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initData(attrs);
		initEvent(); 
	}
	
	public void setText(String desc){
		tv_desc.setText(desc);
	}

	//定义监听器接口 暴露给需要调用该功能的程序员
	public interface OnToggleChangeListener{
		void onToggleChange(View v,boolean isOpen);
	}
	//监听器对象
	private OnToggleChangeListener mOnToggleChangeListener;
	//获得监听器的方法
	public void setOnToggleChangeListener(OnToggleChangeListener listener){
		this.mOnToggleChangeListener = listener;
	}
	//初始化开关状态的方法 调用该方法来设置开关状态
	public void setToggleState(boolean isOpen){
		//保存当前状态
		this.isOpen = isOpen;
		if(isOpen){ //isOpen为true 设置状态为开启
			iv_toggle.setImageResource(R.drawable.on); 
		}else{ //isOpen为false,设置状态为关闭
			iv_toggle.setImageResource(R.drawable.off); 
		}
	}
	
	private void initEvent() {  
		//給rootview添加点击事件
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isOpen = !isOpen; 
				//当点击开的时候,isOpen取反则为true,if(isOpen)成立,则设置状态为打开
				//当点击关的时候,isOpen则true取反为false,if(isOpen)不成立,所以设置为关
				if(isOpen){ 
					//设置开关为打开状态
					iv_toggle.setImageResource(R.drawable.on);
				}else{
					//设置为关闭状态
					iv_toggle.setImageResource(R.drawable.off);
				}
				//如果监听器对象不为空,设置了监听器,就把isOpen值传给onToggleChange方法
				if(mOnToggleChangeListener != null){
					//设置了监听器  回调
					mOnToggleChangeListener.onToggleChange(SettingCenterItem.this,isOpen);
				}
			}
		});
	}

	private void initData(AttributeSet attrs) {
		// 解析布局文件属性
		String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.tfx.mobilesafe","desc");
		String bgtype = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.tfx.mobilesafe","bgselector");
		boolean isdisabletoggle = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.tfx.mobilesafe","isdisabletoggle", false);
		//如果设置了isdisabletoggle为true属性,则隐藏toggle开关
		if(isdisabletoggle){
			iv_toggle.setVisibility(View.GONE);
		}
		
		// 设置属性
		tv_desc.setText(desc);//描述
		//根据bgtype设置背景选择器
		switch (Integer.parseInt(bgtype)) {
		case 0:
			rootView.setBackgroundResource(R.drawable.settingcenter_iv_first_selector);
			break;
		case 1:
			rootView.setBackgroundResource(R.drawable.settingcenter_iv_middle_selector);
			break;
		case 2:
			rootView.setBackgroundResource(R.drawable.settingcenter_iv_last_selector);
			break;
		default:
			break;
		}
	}

	private void initView() {
		// 自定义控件,将view添加到relativelayout中
		// 解析布局文件为view
		rootView = View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_desc = (TextView) findViewById(R.id.tv_setting_item_desc);
		iv_toggle = (ImageView) findViewById(R.id.iv_setting_item_toggle);
	}

	/**
	 * 代码中初始化调用
	 * 
	 * @param context
	 */
	public SettingCenterItem(Context context) {
		this(context, null);
	}

}
