package com.tfx.mobilesafe.test;

import java.util.List;

import com.tfx.mobilesafe.dao.BlackListDao;
import com.tfx.mobilesafe.dao.ContactsDao;
import com.tfx.mobilesafe.dao.LockedDao;
import com.tfx.mobilesafe.db.LockedDB;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.domain.BlackBean;
import com.tfx.mobilesafe.domain.ContactBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.utils.TaskInfoUtils;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	public void testFindAll(){
		BlackListDao dao = new BlackListDao(getContext());
		List<BlackBean> all = dao.findAll();
		for (BlackBean blackBean : all) {
			System.out.println(blackBean);
		}
	}
	public void testAdd(){
		BlackListDao dao = new BlackListDao(getContext());
		for (int i = 1; i < 100; i++) {
			dao.add("1375532"+i,1);
		}
	}
	public void testDao(){
		List<ContactBean> smsLog = ContactsDao.getCallLog(getContext());
		System.out.println(smsLog);
	}
	public void test1(){
		System.out.println(1 << 0); //1
		System.out.println(1 << 1); //2
		System.out.println(2 & 3); 
		System.out.println(1 | 2); 
	}
	public void test2(){
//		SmsUtils.smsBack(new ProgressDialog(getContext()),getContext());
	}
	public void test4(){
		double c = 1024*1024*1000;
		double a = AppInfoUtils.getPhoneTotalMemory();
		double b = AppInfoUtils.getPhoneFreeMemory();
		System.out.println("可用"+(b)+"--总共"+a);
	}
	
	public void testLocked(){
		LockedDao dao = new LockedDao(getContext());
		dao.add("abc");
		dao.delete("abc");
		boolean b = dao.isLocked("abc");
		System.out.println(b);
//		dao.getAll();
//		dao.delete("abc");
	}
}
