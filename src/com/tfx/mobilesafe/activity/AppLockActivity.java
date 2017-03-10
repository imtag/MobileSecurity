package com.tfx.mobilesafe.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.LockedDao;
import com.tfx.mobilesafe.db.LockedDB;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.view.AppLockFragment;
import com.tfx.mobilesafe.view.AppLockHeader;
import com.tfx.mobilesafe.view.AppLockHeader.OnLockChangeListener;
import com.tfx.mobilesafe.view.AppUnlockFragment;

public class AppLockActivity extends FragmentActivity{
	private AppLockHeader alh_applock;
	private FrameLayout fl_applock;
	private AppLockFragment appLockFragment;
	private AppUnlockFragment appUnlockFragment;
	private LockedDao lockedDao;
	private List<String> allLockedPackage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initFragment();
		initData();
	}

	private void initData() {
		selectFragment(false);
	}

	private void initFragment() {
		lockedDao = new LockedDao(getApplicationContext());
		
		appLockFragment = new AppLockFragment();
		appUnlockFragment = new AppUnlockFragment();
		
		appLockFragment.setLockDao(lockedDao);
		appUnlockFragment.setLockDao(lockedDao);
		
		//所有app信息
		List<AppInfoBean> allInstalledAppInfo = AppInfoUtils.getAllInstalledAppInfo(getApplicationContext());
		//移除自己
		for (AppInfoBean appInfoBean : allInstalledAppInfo) {
			if(appInfoBean.getPackName() == getPackageName()){
				allInstalledAppInfo.remove(appInfoBean);
			}
		}
		
		//allInstalledAppInfo进行排序，做系统软件和用户软件分类
		Collections.sort(allInstalledAppInfo, new Comparator<AppInfoBean>() {
			@Override
			public int compare(AppInfoBean lhs, AppInfoBean rhs) {
				if(rhs.isSystem()){
					return -1;
				}else{
					return 2;
				}
			}
		});
		
		appLockFragment.setAllInstalledAppInfo(allInstalledAppInfo);
		appUnlockFragment.setAllInstalledAppInfo(allInstalledAppInfo);
		
		allLockedPackage = lockedDao.getAll();
		appLockFragment.setAllLokedPackage(allLockedPackage);
		appUnlockFragment.setAllLokedPackage(allLockedPackage);
		
		//注册内容观察者
		ContentObserver observer = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {	
				super.onChange(selfChange);
				//内容发生改变时
				allLockedPackage.clear();
				//重新加载数据
				allLockedPackage.addAll(lockedDao.getAll());
			}
		};
		getContentResolver().registerContentObserver(LockedDB.uri, true, observer);
	}

	private void initEvent() {
		alh_applock.setOnLockChangListener(new OnLockChangeListener() {
			@Override
			public void onLockChanged(boolean isLock) {
				selectFragment(isLock);
			}
		});
	}
	
	//选择fragment 
	private void selectFragment(boolean isLock) {
		//初始化两个fragment 替换到framlayout内容
		
		//1.fragmentmanager
		FragmentManager fm = getSupportFragmentManager();
		//2.开启事务
		FragmentTransaction transaction = fm.beginTransaction();
		//3.把fl替换成fragment
		if(isLock){
			transaction.replace(R.id.fl_applock, appLockFragment,"lock");
		}else{
			transaction.replace(R.id.fl_applock, appUnlockFragment,"unlock");
		}
		//4.提交事务
		transaction.commit();
	}

	private void initView() {
		setContentView(R.layout.activity_applock);
		alh_applock = (AppLockHeader) findViewById(R.id.alh_applock);
		fl_applock = (FrameLayout) findViewById(R.id.fl_applock);
	}
}
