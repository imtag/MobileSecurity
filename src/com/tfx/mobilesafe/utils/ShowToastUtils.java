package com.tfx.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-23
 * @desc      对toast的封装 不管是主线程还是子线程都能直接调用

 * @version   $Rev: 19 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-29 23:17:19 +0800 (星期五, 29 七月 2016) $
 * @id        $Id: ShowToastUtils.java 19 2016-07-29 15:17:19Z tfx $
 */

public class ShowToastUtils {
	public static void showToast(final Activity context, final String msg) {
		// runOnUiThread方法的源码已经进行了是否是主线程的判断 
		// 子线程:使用handler发消息到主线程显示toast  主线程:直接toast
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, 1).show();
			}
		});
	}
}
