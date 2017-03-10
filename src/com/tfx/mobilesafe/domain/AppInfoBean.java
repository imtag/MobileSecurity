package com.tfx.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * @author Tfx
 * @comp GOD
 * @date 2016-8-14
 * @desc app基本信息封装
 * 
 * @version $Rev: 35 $
 * @auther $Author: tfx $
 * @date $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id $Id: AppInfoBean.java 35 2016-09-10 13:47:42Z tfx $
 */

public class AppInfoBean {
	private boolean checkstate = true;
	private Drawable icon;// 图标
	private String appName;// app名字
	private boolean isSystem;// 是否是系统软件
	private boolean isSD;// 是否安装在sd卡中
	private String packName;// app包名
	private long size;// 占用的大小
	private String sourceDir;// 安装路径
	private String version;
	private long memorySize;
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public boolean isSD() {
		return isSD;
	}

	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public boolean isCheckstate() {
		return checkstate;
	}

	public void setCheckstate(boolean checkstate) {
		this.checkstate = checkstate;
	}

}
