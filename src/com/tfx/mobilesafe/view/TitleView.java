package com.tfx.mobilesafe.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tfx.mobilesafe.R;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-3
 * @desc      自定义标题

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: TitleView.java 35 2016-09-10 13:47:42Z tfx $
 */

public class TitleView extends RelativeLayout {

	private ImageView iv_goback;
	private TextView tv_desc;

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initData(attrs);
		initEvent((Activity) context);
	}
	
	//初始化标题描述信息
	private void initData(AttributeSet attrs) {
		String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.tfx.mobilesafe","title");
		tv_desc.setText(desc);
	}

	//点击箭头返回上个Activity 就是结束当前Activity
	private void initEvent(final Activity context) {
		iv_goback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Activity的上下文才能finish当前Activity
				context.finish();
			}
		});
	}

	private void initView() {
		//找到布局 控件
		View rootView = View.inflate(getContext(), R.layout.view_title, this);
		iv_goback = (ImageView) rootView.findViewById(R.id.title_iv_goback);
		tv_desc = (TextView) rootView.findViewById(R.id.title_tv_desc);
	}
	
	public TitleView(Context context) {
		this(context, null);
	}
}
