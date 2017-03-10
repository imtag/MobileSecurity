package com.tfx.mobilesafe.utils;
public interface MyConstants {
	//把常量都定义在接口中,因为接口不能实例化,接口的字段都是static final修饰
	
	 String SP_FILENAME = "config"; //sharedpereference文件名
	 String IS_FIRST_USE = "IS_FIRST_USE"; //第一次进入向导页面
	 String AUTO_UPDATE = "auto_update"; //自动更新
	 String PASSWORD = "password"; //手机防盗密码
	 String ISSETUPGUIDEFINISH = "is_setup_guide_finish"; //向导页面是否设置完成
	 String SIMNUMBER = "simnumber"; //sim卡序列号
	 String SAFENUMBER = "safenumber"; //安全号码
	 String BOOTCOMPLETE = "bootcomplete"; //重启完成
	 String TOASTX = "toastx";
	 String TOASTY = "toasty";
	 String LOCATIONSELECTEDINDEX = "locationselectedindex";
	 byte SEED = 100;
	 String CLEARTIME = "cleartime";
	 String SHOWSYSTEMPROCESS = "showsystemprocess";
	 String IS_FIRST_USE_BLACKLIST = "IS_FIRST_USE_BLACKLIST";
}
 