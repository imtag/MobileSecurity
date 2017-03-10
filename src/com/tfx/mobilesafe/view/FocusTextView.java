package com.tfx.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-20
 * @desc      自定义view 滚动(聚焦)TextView

 * @version   $Rev: 12 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-21 08:50:59 +0800 (星期四, 21 七月 2016) $
 * @id        $Id: FocusTextView.java 12 2016-07-21 00:50:59Z tfx $
 */

public class FocusTextView extends TextView {

	/**
	 * 配置文件中 反射调用
	 * @param context
	 * @param attrs 属性
	 */
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 代码中实例化调用
	 * @param context
	 */
	public FocusTextView(Context context) {
		super(context);
	}
	
	/* (non-Javadoc) 永远获得焦点
	 * @see android.view.View#isFocused()
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
}
