package com.tfx.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.utils.DensityUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-4
 * @desc      缓存清理界面

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: CacheClearActivity.java 35 2016-09-10 13:47:42Z tfx $
 */

public class CacheClearActivity extends Activity {
	private TextView tv_scaning;
	private ProgressBar pb_scanpro;
	private LinearLayout ll_completed;
	private Button bt_clear;
	private RotateAnimation mRa;
	private Context mContext;
	private ImageView iv_animation;
	private static final int START = 1;
	private static final int SCAN = 2;
	private static final int FINISH = 3;
	private List<CacheInfo> list = new Vector<CacheInfo>();//存放所有缓存信息bean的集合
	private TextView tv_nodata;
	private ScrollView sv;
	private long cacheCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initAnimation();
		startScan();
	}
	
	public void clear(View v) {
		clearCache();
	}

	//清理所有缓存的方法
	private void clearCache(){
		//清理所有缓存
		// public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
				
		//1.packageManager
		PackageManager mPm = getPackageManager();
		//2.反射
		try {
			//清理结果回调
			IPackageDataObserver.Stub stub = new IPackageDataObserver.Stub() {
				@Override
				public void onRemoveCompleted(String packageName, boolean succeeded)throws RemoteException {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ll_completed.removeAllViews();
							ll_completed.setVisibility(View.GONE);
							tv_nodata.setVisibility(View.VISIBLE);
							tv_nodata.setText("清理了"+Formatter.formatFileSize(mContext, cacheCount)+"缓存");
						}
					});
				}
			};
			
			//2.1 class
			Class type = mPm.getClass();
			//2.2 method  参数1：方法名  参数二：可变参数 参数类型
			Method method = type.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
			//2.3 invoke调用   参数一 方法所在的类  方法二 包名  参数三  aidl回调结果
			method.invoke(mPm,Long.MAX_VALUE,stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//更新界面 handler
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START:
				//开始扫描
				sv.setPadding(0, 0, 0, 0);
				iv_animation.startAnimation(mRa); //开始动画
				tv_scaning.setVisibility(View.GONE);
				tv_nodata.setVisibility(View.GONE);
				bt_clear.setVisibility(View.GONE);
				break;
				
			case SCAN:{
				tv_scaning.setVisibility(View.VISIBLE);
				//正在扫描
				ScanInfo info = (ScanInfo) msg.obj;
				tv_scaning.setText("正在扫描: " + info.appName);
				pb_scanpro.setMax(info.max);
				pb_scanpro.setProgress(info.progress);
				
				//在ll中显示已经扫描的条目
				View view = View.inflate(mContext, R.layout.item_cachescan_ll, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.item_iv_cache_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.item_tv_cache_appname);
				//显示数据
				iv_icon.setImageDrawable(info.icon);
				tv_name.setText(info.appName);
				//把view添加到ll中
				ll_completed.addView(view,0);
				break;
			}
				
			case FINISH:
				//扫描完成
				sv.setPadding(0, 0, 0, DensityUtils.dip2px(mContext, 50));
				bt_clear.setVisibility(View.VISIBLE);
				ll_completed.removeAllViews();
				iv_animation.clearAnimation(); //清除动画
				tv_scaning.setText("扫描完成");
				
				if(list.size() > 0){
					for (final CacheInfo cacheInfo : list) {
						cacheCount += cacheInfo.size;
						
						//条目布局
						View view = View.inflate(mContext, R.layout.item_cacheclear_ll, null);
						ImageView iv_icon = (ImageView) view.findViewById(R.id.item_iv_cache_icon);
						TextView tv_name = (TextView) view.findViewById(R.id.item_tv_cache_appname);
						TextView tv_size = (TextView)view.findViewById(R.id.item_tv_cache_size);
						//显示数据
						iv_icon.setImageDrawable(cacheInfo.icon);
						tv_name.setText(cacheInfo.appName);
						tv_size.setText(Formatter.formatFileSize(mContext, cacheInfo.size));
						view.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								//点击条目跳转到app info页面
								Intent setting = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
								setting.setData(Uri.parse("package:"+cacheInfo.packageName));
								startActivity(setting);
							}
						});
						
						//把view添加到ll中
						ll_completed.addView(view,0);
					}
					
					//软件缓存标签
					TextView tv = new TextView(mContext);
					tv.setPadding(DensityUtils.dip2px(getApplicationContext(),16),DensityUtils.dip2px(getApplicationContext(),5)
							, 16,DensityUtils.dip2px(getApplicationContext(),5));
					tv.setTextColor(Color.GRAY); 
					tv.setTextSize(14);
					tv.setText("软件缓存");
					ll_completed.addView(tv,0);
				}else{
					tv_nodata.setVisibility(View.VISIBLE);
				}
				break;
				
			default:
				break;
			}
		};
	};
	
	//扫描缓存 子线程
	private void startScan() {
		new Thread(){
			public void run() {
				//1 开始扫描
				mHandler.obtainMessage(START).sendToTarget();
				
				//2 扫描所有安装app
				int progress = 0;
				List<AppInfoBean> allInstalledAppInfo = AppInfoUtils.getAllInstalledAppInfo(mContext);
				for (AppInfoBean appInfoBean : allInstalledAppInfo) {
					getCacheInfo(appInfoBean);
					progress++;
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.max = allInstalledAppInfo.size();
					scanInfo.progress = progress;
					scanInfo.appName = appInfoBean.getAppName();
					scanInfo.icon = appInfoBean.getIcon();
					//发送正在扫描的消息
					Message msg = mHandler.obtainMessage(SCAN);
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
					
					SystemClock.sleep(100);
				}
				
				//3 扫描完成
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}
	
	private class ScanInfo{
		int max;
		int progress;
		String appName;
		Drawable icon;
	}

	//扫描旋转动画
	private void initAnimation() {
		mRa = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRa.setDuration(2000);
		mRa.setRepeatCount(-1);
		//动画插入器 匀速
		mRa.setInterpolator(new Interpolator() {
			@Override
			public float getInterpolation(float x) {
				// TODO Auto-generated method stub
				return 2*x;
			}
		});
	}
	
	private class CacheInfo{
		Drawable icon;
		long size;
		String packageName;
		String appName;
	}
	
	
	//获取缓存信息
	private void getCacheInfo(AppInfoBean bean) {
		/*
		 * 1.调用该方法获得缓存信息 这个方法被官方隐藏 需要通过反射调用
			mPm.getPackageSizeInfo(packageName, mBackgroundHandler.mStatsObserver); 
			2.封装了回调结果
			final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub(); 
		*/

		//1.packageManager
		PackageManager mPm = getPackageManager();
		//2.反射
		try {
			//回调结果
			class MyIPackageStatsObserver extends IPackageStatsObserver.Stub{
				private AppInfoBean infoBean;
				//构造方法
				public MyIPackageStatsObserver(AppInfoBean bean){
					this.infoBean = bean;
				}
				@Override
				public void onGetStatsCompleted(PackageStats pStats,boolean succeeded) throws RemoteException {
					//缓存信息 pStats.cacheSize
					//封装缓存信息bean
					if(pStats.cacheSize > 0){
						CacheInfo info = new CacheInfo();
						info.size = pStats.cacheSize;
						info.appName = infoBean.getAppName();
						info.icon = infoBean.getIcon();
						info.packageName = infoBean.getPackName();
						list.add(info);
					}
				}
			};
			
			//2.1 class
			Class type = mPm.getClass();
			//2.2 method  参数1：方法名  参数二：可变参数 参数类型
			Method method = type.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			//2.3 invoke调用   参数一 方法所在的类  方法二 包名  参数三  aidl回调结果
			method.invoke(mPm,bean.getPackName(),new MyIPackageStatsObserver(bean));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		setContentView(R.layout.activity_cacheclear);
		mContext = this;
		tv_scaning = (TextView) findViewById(R.id.tv_cache_scaning);
		pb_scanpro = (ProgressBar) findViewById(R.id.pb_cache_scanprogress);
		ll_completed = (LinearLayout) findViewById(R.id.ll_cache_completed);
		bt_clear = (Button) findViewById(R.id.bt_cache_clearall);
		iv_animation = (ImageView) findViewById(R.id.iv_scan_animation);
		tv_nodata = (TextView) findViewById(R.id.tv_cache_nodata);
		sv = (ScrollView) findViewById(R.id.sv_cache);
	}
}
