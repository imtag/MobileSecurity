package com.tfx.mobilesafe.utils;

import android.content.Context;

public class DensityUtils {
	//dp转px
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density; //获得屏幕密度
		return (int) (dpValue * scale + 0.5f);
	}

	//px转dp
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
