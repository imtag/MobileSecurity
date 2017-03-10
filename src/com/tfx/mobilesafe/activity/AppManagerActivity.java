package com.tfx.mobilesafe.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

 * @version   $Rev: 30 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-17 22:06:25 +0800 (星期三, 17 八月 2016) $
 * @id        $Id: AppManagerActivity.java 30 2016-08-17 14:06:25Z tfx $
 */

public class AppManagerActivity extends Activity {
	private final int LOADING = 1;
	private final int FINISH = 2;
	
	private ListView lv;
	private LinearLayout ll_progressbar;
	private int allAppCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initData();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initEvent() {
		//滑动事件
		lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//如果第一个条目索引为用户软件大小+1
				if(firstVisibleItem >= userAppList.size() + 1){
					tv_userappcount.setText("系统软件(" + systemAppList.size() + " / " + allAppCount + ")");
				}else{
					tv_userappcount.setText("用户软件("+userAppList.size() + " / " + allAppCount + ")");
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
				tv_userappcount.setVisibility(View.GONE);
				tpv_appmanager.setVisibility(View.GONE);
				break;
			case FINISH:
				ll_progressbar.setVisibility(View.GONE);
				tpv_appmanager.setVisibility(View.VISIBLE);
				lv.setVisibility(View.VISIBLE);
				tv_userappcount.setVisibility(View.VISIBLE);
				
				DecimalFormat df1 = new DecimalFormat("0.0");//小数格式
				String size = df1.format(allAppSize*1.0 / 1073741824) + " GB";
				allAppCount = systemAppList.size() + userAppList.size(); 
				
				//显示软件类型和个数
				tv_userappcount.setText("用户软件("+userAppList.size() + " / " + allAppCount + ")");
				
				//显示内存信息 b转gb 保留两个小数 如1.22GB
				DecimalFormat df = new DecimalFormat("0.00"); //格式化小数点 保留两位
				String totalMemory = df.format(sdTotalMemory*1.0 / 1073741824) + " GB";
				String usedMemory = df.format((sdTotalMemory - sdFreeMemory)*1.0 / 1073741824) + " GB";
				
				//设置内存使用情况和进度条
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
	private List<AppInfoBean> systemAppList = new ArrayList<AppInfoBean>();
	private List<AppInfoBean> userAppList = new ArrayList<AppInfoBean>();
	private long allAppSize = 0;
	private TextView tv_userappcount;
	private LinearLayout ll_showroot;
	private TextProgressView tpv_appmanager;

	private void initData() {
		new Thread() {

			@Override 
			public void run() {
				// 1.发送加载数据消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				// 2.加载数据
				sdFreeMemory = AppInfoUtils.getSdFreeMemory();
				sdTotalMemory = AppInfoUtils.getSdTotalMemory();
				
				List<AppInfoBean> appInfoList = AppInfoUtils.getAllInstalledAppInfo(getApplicationContext());
				for (AppInfoBean appInfoBean : appInfoList) {
					allAppSize += appInfoBean.getSize();
					if(appInfoBean.isSystem()){
						//系统软件 添加到系统软件的容器
						systemAppList.add(appInfoBean);
					}else{
						//用户软件 添加到用户软件的容器
						userAppList.add(appInfoBean);
					}
				}  
				
				// 3.发送加载数据完成消息
				mHandler.obtainMessage(FINISH).sendToTarget();
				super.run();
			}
		}.start();
	}

	//list适配器
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return systemAppList.size() + userAppList.size() + 1;//2: 
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
			if(position == userAppList.size()){
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
			}
			//2.缓存view
			ViewHolder holder = null;
			//要判断当前viw是不是textview(标记),只有不是定义的标记才能进行缓存
			if(convertView != null && !(convertView instanceof TextView)){ 
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
			if(position < userAppList.size()){
				//用户软件
				bean = userAppList.get(position); 
			}else{
				//系统软件
				bean = systemAppList.get(position - userAppList.size() - 1);//系统软件的位置 要减去用户软件大小再减去两个标签
			}
			//4.显示数据
			holder.iv_appicon.setImageDrawable(bean.getIcon());
			holder.tv_appname.setText(bean.getAppName());
			//使用formatter进行格式化，会自动格式化成字符串，变成mb大小
			String appSize = Formatter.formatFileSize(getApplicationContext(), bean.getSize());
			holder.tv_appsize.setText(appSize+"");
			holder.tv_appversion.setText("版本: "+bean.getVersion());
			
			return convertView;
		}
	}
	
	private static class ViewHolder{
		TextView tv_appname;
		TextView tv_appversion;
		TextView tv_appsize;
		ImageView iv_appicon;
	}
	
	private void initView() {
		setContentView(R.layout.activity_appmanager);
		lv = (ListView) findViewById(R.id.lv_appmanager);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		ll_showroot = (LinearLayout) findViewById(R.id.ll_appmanager_showmemoryroot);
		tv_userappcount = (TextView) findViewById(R.id.tv_appmanager_userappcount);
		
		tpv_appmanager = (TextProgressView) findViewById(R.id.tpv_appmanager);
		
		adapter = new MyAdapter(); 
		lv.setAdapter(adapter);
	}
}
