package com.tfx.mobilesafe.activity;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.utils.DensityUtils;
import com.tfx.mobilesafe.view.TextProgressView;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-17
 * @desc      软件管家界面  
 * 			    使用了开源的StickyListHeaders 系统软件标签会把用户软件标签顶上去  **********

 * @version   $Rev: 32 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-20 23:10:06 +0800 (星期六, 20 八月 2016) $
 * @id        $Id: AppManagerActivity_StickyListHeaders.java 32 2016-08-20 15:10:06Z tfx $
 */

public class AppManagerActivity_StickyListHeaders extends Activity {
	private final int LOADING = 1;
	private final int FINISH = 2;
	private StickyListHeadersListView lv;
	private LinearLayout ll_progressbar;
	private int allAppCount;
	private AppInfoBean clickedAppBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initData();
		initEvent();
		initPopupWindow();
		
		super.onCreate(savedInstanceState);
	}

	private void initPopupWindow() {
		//1.加载布局
		View mPopupViewRoot = View.inflate(getApplicationContext(), R.layout.popupwindow_appmanager, null);
		//2.实例化弹出窗体
		mPW = new PopupWindow(mPopupViewRoot, -2, -2);
		//3.获取焦点 保证里面的组件可以被点击
		mPW.setFocusable(true);
		//4.必须设置背景
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //透明
		//5.设置点击外部关闭窗体
		mPW.setOutsideTouchable(true);
		//6.动画
		mPW.setAnimationStyle(R.style.MyPopupWindow);
		
		//找到控件
		pw_setting = (LinearLayout) mPopupViewRoot.findViewById(R.id.pw_appmanager_setting);
		pw_share = (LinearLayout) mPopupViewRoot.findViewById(R.id.pw_appmanager_share);
		pw_uninstall = (LinearLayout) mPopupViewRoot.findViewById(R.id.pw_appmanager_uninstall);
		pw_startup = (LinearLayout) mPopupViewRoot.findViewById(R.id.pw_appmanager_startup);
		//点击事件
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.pw_appmanager_uninstall://卸载
					uninstall();
					break;
				case R.id.pw_appmanager_share: //共享
					share();
					break;
				case R.id.pw_appmanager_startup://启动app
					startup();
					break;
				case R.id.pw_appmanager_setting://设置
					setting();
					break;

				default:
					break;
				}
				//关闭弹出窗体
				mPW.dismiss();
			}
		};
		pw_setting.setOnClickListener(listener);
		pw_share.setOnClickListener(listener);
		pw_uninstall.setOnClickListener(listener);
		pw_startup.setOnClickListener(listener);
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("分享的标题");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}
	
	protected void share() {
		//分享到公众平台  使用集成
		showShare();
	}
	
	protected void setting() {
		// START u0 {act=android.settings.APPLICATION_DETAILS_SETTINGS
		// dat=package:com.android.email flg=0x10800000
		// cmp=com.android.settings/.applications.InstalledAppDetails} from pid
		// 1441
		//把应用拉倒appifo 根据日志提供的action和data进行写代码
		Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.parse("package:"+ clickedAppBean.getPackName()));
		startActivity(intent);
	}

	//启动app
	protected void startup() {
		//1.获得包管理者 getPackageManager用来获取静态资源
		PackageManager pm = getPackageManager();
		//2.通过app的包名来获得app的启动意图（配置为lanucher的activity）
		Intent launchIntentForPackage = pm.getLaunchIntentForPackage(clickedAppBean.getPackName());
		try {//抛异常 防止无法打开的系统app导致程序崩溃
			// 3.启动程序
			startActivity(launchIntentForPackage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//卸载软件
	protected void uninstall() {
		if (clickedAppBean.isSystem()) {  
			// 卸载系统软件
			try {
				RootTools.sendShell("mount -o remount rw /system", 5000); // 修改权限为读写  超时时间为5秒
				RootTools.sendShell("rm -r " + clickedAppBean.getSourceDir(),5000); // 删除软件
				RootTools.sendShell("mount -o remount r /system", 5000); // 改回权限为读
				initData();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RootToolsException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			initData();
		}else{
			//卸载应用软件
			Intent intent = new Intent("android.intent.action.DELETE"); 
			intent.setData(Uri.parse("package:" + clickedAppBean.getPackName())); 
			startActivityForResult(intent,0);//卸载意图		
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){
			initData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void initEvent() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//显示弹出窗体 因为该方法是显示在view的下面 所以要往上移负的宽度
				mPW.showAsDropDown(view, DensityUtils.dip2px(getApplicationContext(),60), -(view.getHeight()));
				
				//获得当前点击位置的appbean
				clickedAppBean = allAppInfoList.get(position);
			}
		});
		
		//滑动事件
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//如果弹出窗体在显示 则关闭
				if(mPW != null && mPW.isShowing()){
					mPW.dismiss();
				}
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				ll_progressbar.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
				tpv_appmanager.setVisibility(View.GONE);
				break;
			case FINISH:
				ll_progressbar.setVisibility(View.GONE);
				tpv_appmanager.setVisibility(View.VISIBLE);
				lv.setVisibility(View.VISIBLE);
				
				//已使用空间
				long sdUsedMemory = sdTotalMemory - sdFreeMemory;
				
				//设置内存使用情况和进度条
				String totalMemory = Formatter.formatFileSize(getApplicationContext(), + sdTotalMemory);
				String usedMemory = Formatter.formatFileSize(getApplicationContext(), + sdUsedMemory);
				tpv_appmanager.setMemoryuse(usedMemory + " / " + totalMemory);
				tpv_appmanager.setProgress((sdTotalMemory - sdFreeMemory)*1.0 / sdTotalMemory);
				
				//更新listview数据
				adapter.notifyDataSetChanged();
				
				break;
			default:
				break; 
			}
		};
	}; 

	private long sdFreeMemory;
	private long sdTotalMemory;
	private MyAdapter adapter;
	private List<AppInfoBean> systemAppList = new Vector<AppInfoBean>();
	private List<AppInfoBean> userAppList = new Vector<AppInfoBean>();
	private TextProgressView tpv_appmanager;
	private List<AppInfoBean> allAppInfoList = new Vector<AppInfoBean>();
	private PopupWindow mPW;
	private LinearLayout pw_setting;
	private LinearLayout pw_share;
	private LinearLayout pw_uninstall;
	private LinearLayout pw_startup;

	private void initData() {
		new Thread() {
			@Override 
			public void run() {
				// 1.发送加载数据消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				// 2.加载数据
				sdFreeMemory = AppInfoUtils.getSdFreeMemory();
				sdTotalMemory = AppInfoUtils.getSdTotalMemory();
				
				allAppInfoList.clear();
				systemAppList.clear();
				userAppList.clear();
				
				//获得所有软件集合
				allAppInfoList = AppInfoUtils.getAllInstalledAppInfo(getApplicationContext());
				for (AppInfoBean appInfoBean : allAppInfoList) {
					if(appInfoBean.isSystem()){
						//系统软件 添加到系统软件的容器
						systemAppList.add(appInfoBean);
					}else{
						//用户软件 添加到用户软件的容器
						userAppList.add(appInfoBean);
					}
				}  
				
				//对数据进行排序 供标签显示
				allAppInfoList.clear();
				allAppInfoList.addAll(userAppList);
				allAppInfoList.addAll(systemAppList);
				
				// 3.发送加载数据完成消息
				mHandler.obtainMessage(FINISH).sendToTarget();
				super.run();
			}
		}.start();
	}

	//list适配器
	private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter{

		@Override
		public int getCount() {
			return allAppInfoList.size();//2: 
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//1.需要给系统软件和用户软件加标签
			/*if(position == 0){
				//如果position为0  则给用户软件加标签
				TextView tv_userAppTag = new TextView(getApplicationContext());
				tv_userAppTag.setPadding(DensityUtils.dip2px(getApplicationContext(),14),DensityUtils.dip2px(getApplicationContext(),5)
						, 0,DensityUtils.dip2px(getApplicationContext(),5));
				tv_userAppTag.setTextColor(Color.GRAY); 
				int color = getResources().getColor(R.color.appmanager_memory_text);
				tv_userAppTag.setBackgroundColor(Color.GRAY);
				tv_userAppTag.setTextSize(15);
				tv_userAppTag.setText("用户软件("+ userAppList.size() +")");
				 
				return tv_userAppTag;//显示标签
			}else */
			
			//给系统软件加标签
			/*if(position == userAppList.size()){
				//给系统软件加标签
				TextView tv_systemAppTag = new TextView(getApplicationContext());
				tv_systemAppTag.setPadding(DensityUtils.dip2px(getApplicationContext(),12), DensityUtils.dip2px(getApplicationContext(), 5)
						, 0,DensityUtils.dip2px(getApplicationContext(), 5));
				tv_systemAppTag.setTextColor(Color.GRAY); 
				int color = getResources().getColor(R.color.appmanager_memory_text);
				tv_systemAppTag.setBackgroundColor(color);
				tv_systemAppTag.setTextSize(15);
				tv_systemAppTag.setText("系统软件(" + systemAppList.size() + " / " + allAppCount + ")");
				
				return tv_systemAppTag;//显示标签
			}*/
			//2.缓存view
			ViewHolder holder = null;
			//要判断当前viw是不是textview(标记),只有不是定义的标记才能进行缓存
			if(convertView != null){ 
				//有缓存 取出
				holder = (ViewHolder)convertView.getTag();
			}else{
				//没有缓存
				convertView = View.inflate(getApplicationContext(), R.layout.item_appmanager_lv, null);
				holder = new ViewHolder();
				holder.iv_appicon = (ImageView)convertView.findViewById(R.id.iv_item_appmanager_icon);
				holder.tv_appname = (TextView)convertView.findViewById(R.id.tv_item_appmanager_name);
				holder.tv_appsize = (TextView)convertView.findViewById(R.id.tv_item_appmanager_size);
				holder.tv_appversion = (TextView)convertView.findViewById(R.id.tv_item_appmanager_version);
				//设置标记 
				convertView.setTag(holder);
			}
			//3.取值赋值
			AppInfoBean bean = null;
			bean = allAppInfoList.get(position); 
			//4.显示数据
			holder.iv_appicon.setImageDrawable(bean.getIcon());
			holder.tv_appname.setText(bean.getAppName());
			//使用formatter进行格式化，会自动格式化成字符串，变成mb大小
			String appSize = Formatter.formatFileSize(getApplicationContext(), bean.getSize());
			holder.tv_appsize.setText(appSize+"");
			holder.tv_appversion.setText("版本: "+bean.getVersion());
			
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView tv_userAppTag = new TextView(getApplicationContext());
			tv_userAppTag.setPadding(DensityUtils.dip2px(getApplicationContext(),14),DensityUtils.dip2px(getApplicationContext(),5)
					, 0,DensityUtils.dip2px(getApplicationContext(),5));
			tv_userAppTag.setTextColor(Color.GRAY); 
			int color = getResources().getColor(R.color.appmanager_memory_text);
			tv_userAppTag.setBackgroundColor(color);
			tv_userAppTag.setTextSize(15);
			//获取所有app 判断是系统软件还是用户软件 来做显示
			AppInfoBean appInfoBean = allAppInfoList.get(position);
			if(!appInfoBean.isSystem()){
				tv_userAppTag.setText("用户软件("+userAppList.size() + " / " + allAppInfoList.size() + ")");
			}else{
				tv_userAppTag.setText("系统软件("+ systemAppList.size() + " / " + allAppInfoList.size() + ")");
			}
			return tv_userAppTag;//显示标签
		}

		@Override
		public long getHeaderId(int position) {
			AppInfoBean appInfoBean = allAppInfoList.get(position);
			if (!appInfoBean.isSystem()) {
				return 1;
			} else {
				return 2;
			}
		}
	}
	
	private static class ViewHolder{
		TextView tv_appname;
		TextView tv_appversion;
		TextView tv_appsize;
		ImageView iv_appicon;
	}
	
	private void initView() {
		setContentView(R.layout.activity_appmanager_stickylistheader);
		lv = (StickyListHeadersListView) findViewById(R.id.lv_appmanager);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		tpv_appmanager = (TextProgressView) findViewById(R.id.tpv_appmanager);
		
		adapter = new MyAdapter(); 
		lv.setAdapter(adapter);
	}
}
