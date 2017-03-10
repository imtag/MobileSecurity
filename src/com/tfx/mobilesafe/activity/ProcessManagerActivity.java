package com.tfx.mobilesafe.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.service.ScreenOffClearTaskService;
import com.tfx.mobilesafe.utils.DensityUtils;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;
import com.tfx.mobilesafe.utils.ServiceUtils;
import com.tfx.mobilesafe.utils.TaskInfoUtils;
import com.tfx.mobilesafe.view.SettingCenterItem;
import com.tfx.mobilesafe.view.SettingCenterItem.OnToggleChangeListener;
import com.tfx.mobilesafe.view.TextProgressView;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-19
 * @desc      进程管家页面

 * @version   $Rev: 34 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-01 22:21:45 +0800 (星期四, 01 九月 2016) $
 * @id        $Id: ProcessManagerActivity.java 34 2016-09-01 14:21:45Z tfx $
 */

public class ProcessManagerActivity extends Activity {
	private static final int LOADING = 1;
	private static final int FINISH = 2;
	private TextView tv_userappcount;
	private TextProgressView tpv_processmanager;
	private LinearLayout ll_progressbar;  
	private ListView lv_processmanager;
	private long totalMemory;
	private long availableMemory;
	private List<AppInfoBean> allRunningAppInfo = new Vector<AppInfoBean>();
	private List<AppInfoBean> userAppInfoCount = new Vector<AppInfoBean>();
	private List<AppInfoBean> systemAppInfoCount = new Vector<AppInfoBean>();
	private MyAdapter mAdapter;
	private ActivityManager mAM;
	private SlidingDrawer sd_progressmanager;
	private ImageView iv_arrowdown;
	private ImageView iv_arrowup;
	private AlphaAnimation am1;
	private AlphaAnimation am2;
	private SettingCenterItem sci_showsystem;
	private SettingCenterItem sci_screenout;
    private int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
		initAnimation();
		showUp();
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}
	
	private void initAnimation() {
		am1 = new AlphaAnimation(1.0f, 0.5f);
		am1.setDuration(500);
		am1.setRepeatCount(Animation.INFINITE);
		
		am2 = new AlphaAnimation(0.5f, 1.0f);
		am2.setDuration(500);   
		am2.setRepeatCount(Animation.INFINITE);
	}

	//抽屉的向下的箭头
	public void showDown(){
		iv_arrowdown.setImageResource(R.drawable.drawer_arrow_down);
		iv_arrowup.setImageResource(R.drawable.drawer_arrow_down);
		iv_arrowdown.clearAnimation();
		iv_arrowup.clearAnimation();
	};
	
	//抽屉向上的箭头
	public void showUp(){
		iv_arrowdown.setImageResource(R.drawable.drawer_arrow_up);
		iv_arrowup.setImageResource(R.drawable.drawer_arrow_up);
		iv_arrowup.setAnimation(am1);
		iv_arrowdown.setAnimation(am2);
	}
	
	// 全选按钮
	public void checkall(View v) {
		for (AppInfoBean bean : userAppInfoCount) {
			bean.setCheckstate(true);
		}
		for (AppInfoBean bean : systemAppInfoCount) {
			bean.setCheckstate(true);
		}

		// 更新界面
		mAdapter.notifyDataSetChanged();
	}
	
	//清理按钮
	public void clearprocess(View v){
		int clearCount =0;
		long clearSize = 0;
		//清理用户进程
		for (int i = 0; i < userAppInfoCount.size(); i++) {
			AppInfoBean bean = userAppInfoCount.get(i);
			if (bean.isCheckstate() && !(bean.getPackName().equals(getPackageName()))) {
				clearCount++;
				clearSize += bean.getMemorySize();
				// 清理进程
				mAM.killBackgroundProcesses(bean.getPackName());
				// 界面清理
				userAppInfoCount.remove(i--); //删除当前条目 下一个条目会瞬移到当前位置 所以要-- 再++又会到当前位置
			}
		}
		//清理系统进程
		for (int i = 0; i < systemAppInfoCount.size();i++) {
			AppInfoBean bean = systemAppInfoCount.get(i);
			if(bean.isCheckstate()){
				clearCount++;
				clearSize += bean.getMemorySize();
				//清理进程
				mAM.killBackgroundProcesses(bean.getPackName());
				//界面清理
				systemAppInfoCount.remove(i--);  
			}
		}
		
		if(clearCount == 0){
			Toast.makeText(getApplicationContext(), "请勾选要清理的进程", 0).show();
			return;
		}else{
			//记录下清理的时间
			if(userAppInfoCount.size() + systemAppInfoCount.size() < 6){
				SPUtils.putLong(getApplicationContext(), MyConstants.CLEARTIME, System.currentTimeMillis());
			}
			Toast.makeText(getApplicationContext(), "清理了"+clearCount+"个进程,释放了"+ 
			Formatter.formatFileSize(getApplicationContext(), clearSize) +"内存", 0).show();
			initData();
		}
		
	}
	
	private void initEvent() {
		//显示系统进程开关
		sci_showsystem.setOnToggleChangeListener(new OnToggleChangeListener() {
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				SPUtils.putBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMPROCESS, isOpen);
				mAdapter.notifyDataSetChanged();
			}
		});
		//锁屏清理开关
		sci_screenout.setOnToggleChangeListener(new OnToggleChangeListener() {
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				if (isOpen) {
					//注册锁屏清理服务
					Intent service = new Intent(getApplicationContext(),ScreenOffClearTaskService.class);
					startService(service);
				}else{
					//注销锁屏清理服务
					Intent service = new Intent(getApplicationContext(),ScreenOffClearTaskService.class);
					stopService(service);
				}
			}
		});
		//监听抽屉的打开或关闭
		sd_progressmanager.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				showUp();
			}
		});
		sd_progressmanager.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				showDown();
			}
		});
		//单击事件
		lv_processmanager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == userAppInfoCount.size()){
					//系统标签位置
					return;
				}
				
				//点击条目，改变checkbox状态
				AppInfoBean item = (AppInfoBean) lv_processmanager.getItemAtPosition(position);
				item.setCheckstate(!item.isCheckstate()); //取反改变状态
				
				if(item.getPackName().equals(getPackageName())){
					//是自己
					item.setCheckstate(false);
				}
				
				//更新界面
				mAdapter.notifyDataSetChanged();	
			}
		});
		//滑动事件
		lv_processmanager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem > userAppInfoCount.size()){
					tv_userappcount.setText("系统进程(" + systemAppInfoCount.size() + " / " + allRunningAppInfo.size()+")"); 
				}else{
					tv_userappcount.setText("用户进程(" + userAppInfoCount.size() + " / " +allRunningAppInfo.size()+")"); 
				}
			}
		});
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				lv_processmanager.setVisibility(View.GONE); 
				tv_userappcount.setVisibility(View.GONE);
				ll_progressbar.setVisibility(View.VISIBLE);
				break;
			case FINISH:
				lv_processmanager.setVisibility(View.VISIBLE);
				tv_userappcount.setVisibility(View.VISIBLE);
				ll_progressbar.setVisibility(View.GONE);
				
				//设置内存信息
				String total = Formatter.formatFileSize(getApplicationContext(), totalMemory);
				String used = Formatter.formatFileSize(getApplicationContext(), totalMemory - availableMemory);
				tpv_processmanager.setMemoryuse(used +" / " + total);
				tpv_processmanager.setProgress((totalMemory - availableMemory) * 1.0 / totalMemory);
				
				//显示进程信息
				tv_userappcount.setText("用户进程(" + userAppInfoCount.size() + " / " +allRunningAppInfo.size()+")"); 
				count = userAppInfoCount.size() + systemAppInfoCount.size();
				//刷新页面
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	private void initData() {
		//初始化锁屏清理进程按钮
		sci_screenout.setToggleState(ServiceUtils.isServiceRunning(getApplicationContext(), "com.tfx.mobilesafe.service.ScreenOffClearTaskService"));
		//初始化显示系统进程按钮
		sci_showsystem.setToggleState(SPUtils.getBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMPROCESS, true));
		new Thread(){
			@Override
			public void run() {
				//发加载数据消息
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 加载数据
				allRunningAppInfo = TaskInfoUtils.getAllRunningAppInfo(getApplicationContext());
				userAppInfoCount.clear();
				systemAppInfoCount.clear();
				for (AppInfoBean bean : allRunningAppInfo) {
					if (bean.isSystem()) {
						systemAppInfoCount.add(bean);
					} else {
						userAppInfoCount.add(bean);
					}
				}
				
				totalMemory = TaskInfoUtils.getTotalMemory();
				availableMemory = TaskInfoUtils.getAvailableMemory(getApplicationContext());
				
				//加载完成消息
				mHandler.obtainMessage(FINISH).sendToTarget();
				
			}
		}.start();
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(SPUtils.getBoolean(getApplicationContext(), MyConstants.SHOWSYSTEMPROCESS, true)){
				return count + 1;
			} else {
				return userAppInfoCount.size();
			}
		}

		@Override
		public AppInfoBean getItem(int position) {
			AppInfoBean bean = new AppInfoBean();
			if(position < userAppInfoCount.size()){
				//用户app
				bean = userAppInfoCount.get(position);
			}else{
				//系统app
				bean = systemAppInfoCount.get(position - (userAppInfoCount.size() + 1));
			}
			return bean;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//给系统app添加标签
			if(position == userAppInfoCount.size()){
				TextView tv_systemAppTag = new TextView(getApplicationContext());
				tv_systemAppTag.setPadding(DensityUtils.dip2px(getApplicationContext(),14),DensityUtils.dip2px(getApplicationContext(),5)
						, 0,DensityUtils.dip2px(getApplicationContext(),5));
				tv_systemAppTag.setTextColor(Color.GRAY); 
				int color = getResources().getColor(R.color.appmanager_memory_text);
				tv_systemAppTag.setBackgroundColor(color);
				tv_systemAppTag.setTextSize(15);
				tv_systemAppTag.setText("系统进程(" + systemAppInfoCount.size() + " / " + allRunningAppInfo.size()+")"); 
				return tv_systemAppTag;
			}
			
			ViewHolder holder = null;
			if(convertView != null && !(convertView instanceof TextView)){
				//有缓存
				holder = (ViewHolder) convertView.getTag();
			}else{
				//没有缓存
				convertView = View.inflate(getApplicationContext(), R.layout.item_processmanager_lv, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_item_processmanager_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_processmanager_name);
				holder.tv_memory = (TextView) convertView.findViewById(R.id.tv_item_processmanager_memory);
				holder.cb_clear = (CheckBox) convertView.findViewById(R.id.cb_item_processmanager_clear); 
				//设置标记
				convertView.setTag(holder);
			}
			//显示信息
			final AppInfoBean bean = getItem(position);
			holder.iv_icon.setImageDrawable(bean.getIcon());
			holder.tv_name.setText(bean.getAppName());
			holder.tv_memory.setText(Formatter.formatFileSize(getApplicationContext(), bean.getMemorySize()));
			
			//复选框的状态监听
			holder.cb_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					bean.setCheckstate(isChecked);
				}
			});
			
			//显示复选框选中信息
			holder.cb_clear.setChecked(bean.isCheckstate());
			
			if(bean.getPackName().equals(getPackageName())){ 
				//是自己
				holder.cb_clear.setVisibility(View.GONE); 
			}else{
				holder.cb_clear.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

	private static class ViewHolder{
		TextView tv_name;
		TextView tv_memory;
		ImageView iv_icon;
		CheckBox cb_clear;
	}
	
	private void initView() {
		setContentView(R.layout.activity_processmanager);
		tv_userappcount = (TextView) findViewById(R.id.tv_processmanager_userappcount);
		tpv_processmanager = (TextProgressView) findViewById(R.id.tpv_processmanager);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		lv_processmanager = (ListView) findViewById(R.id.lv_processmanager);
		mAdapter = new MyAdapter();
		lv_processmanager.setAdapter(mAdapter);
		mAM = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		
		sd_progressmanager = (SlidingDrawer) findViewById(R.id.sd_progressmanager);
		iv_arrowup = (ImageView) findViewById(R.id.iv_process_arrowup);
		iv_arrowdown = (ImageView) findViewById(R.id.iv_process_arrowdown);
		sci_showsystem = (SettingCenterItem) findViewById(R.id.sci_process_showsystem);
		sci_screenout = (SettingCenterItem) findViewById(R.id.sci_process_screenout);
	}
}
