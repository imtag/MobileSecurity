package com.tfx.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tfx.mobilesafe.R;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-17
 * @desc      自定义view

 * @version   $Rev: 30 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-17 22:06:25 +0800 (星期三, 17 八月 2016) $
 * @id        $Id: TextProgressView.java 30 2016-08-17 14:06:25Z tfx $
 */

public class TextProgressView extends LinearLayout {

	private TextView tv_showmemory;
	private ProgressBar pb_progress;

	public TextProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		View rootView = View.inflate(getContext(), R.layout.view_memory_progress, this);
		tv_showmemory = (TextView) rootView.findViewById(R.id.tv_show_memoryuse);
		pb_progress = (ProgressBar) rootView.findViewById(R.id.pb_memory_progress);
	}
	
	/**
	 * @param progressScale 百分比 如10% 0.1
	 */
	public void setProgress(double progressScale){ 
		pb_progress.setProgress((int)(Math.round(progressScale * 100))); //math.round方法四舍五入
	}
	
	public void setMemoryuse(String memoryuse){
		tv_showmemory.setText(memoryuse);
	}

	public TextProgressView(Context context) { //只传上下文对象 适合用于代码实例化该view
		this(context,null);
	}
	
}
