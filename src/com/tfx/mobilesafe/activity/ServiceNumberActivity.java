package com.tfx.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.AddressDao;
import com.tfx.mobilesafe.dao.NumberAndName;
import com.tfx.mobilesafe.domain.ServicePhoneType;
import com.tfx.mobilesafe.utils.DensityUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-9
 * @desc      服务号码查询页面

 * @version   $Rev: 25 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-08-11 10:28:51 +0800 (星期四, 11 八月 2016) $
 * @id        $Id: ServiceNumberActivity.java 25 2016-08-11 02:28:51Z tfx $
 */

public class ServiceNumberActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ExpandableListView elv_showservicenumber;
	private LinearLayout ll_progressbar;

	private List<ServicePhoneType> mTypes = new ArrayList<ServicePhoneType>();//存放所有服务电话类型
	private List<List<NumberAndName>> mPhones = new ArrayList<List<NumberAndName>>();//存放每个电话类型的具体数据
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initData();
		initEvent();
		super.onCreate(savedInstanceState);
	}

	private void initEvent() {
		//设置组的子view的点击事件
		elv_showservicenumber.setOnChildClickListener(new OnChildClickListener() {
			//点击当前条目跳转到拨号界面
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//获得当前点击的条目的号码
				String number = mPhones.get(groupPosition).get(childPosition).getNumber();
				//店家条目进入拨号界面
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:"+number));
				startActivity(call);
				return true;
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING://正在加载数据
				elv_showservicenumber.setVisibility(View.GONE);
				ll_progressbar.setVisibility(View.VISIBLE);
				break;
			case FINISH://数据加载完成
				elv_showservicenumber.setVisibility(View.VISIBLE);
				ll_progressbar.setVisibility(View.GONE);
				//刷新界面
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};


	private mAdapter mAdapter;	private void initData() {
		//子线程加载数据
		new Thread(){
			@Override
			public void run() { 
				//1.发送取数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				//2.加载数据
				List<ServicePhoneType> types = AddressDao.getAllServiceTypes();
				mTypes = types;
				//获取数据
				for (ServicePhoneType servicePhoneType : types) {
					//获取当前类型的具体信息
					List<NumberAndName> serviceNumberAndName = AddressDao.getServiceNumberAndName(servicePhoneType);
					//将信息添加到容器中
					mPhones.add(serviceNumberAndName);
				}
				//3.加载数据完成消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			}
		}.start();
	}

	private class mAdapter extends BaseExpandableListAdapter{
		//组数量
		@Override
		public int getGroupCount() {
			return mTypes.size();
		}

		//组的子数量
		@Override
		public int getChildrenCount(int groupPosition) {
			return mPhones.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		//获得每条子数据对象
		@Override
		public NumberAndName getChild(int groupPosition, int childPosition) {
			return mPhones.get(groupPosition).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		//组的显示
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = null;
			//判断是否有可复用view   
			if(convertView == null){ 
				//没有 初始化textview 
				tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLACK);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setPadding(DensityUtils.dip2px(getApplicationContext(), 30),DensityUtils.dip2px(getApplicationContext(), 10) , 
						0, DensityUtils.dip2px(getApplicationContext(), 10));
			}else{
				//复用view
				tv = (TextView) convertView;
			}
			//设置textview显示内容
			tv.setText(mTypes.get(groupPosition).getName());
			return tv;
		}

		//组的子view的显示
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = null;
			//判断是否有可复用view   
			if(convertView == null){ 
				//没有 初始化textview 
				tv = new TextView(getApplicationContext());
				//给textviw设置图片
				Drawable drawable= getResources().getDrawable(R.drawable.call_selector);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tv.setCompoundDrawables(null, null, drawable, null);
				
				tv.setTextSize(16);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setBackgroundResource(R.drawable.dialog_button_selector);
				tv.setTextColor(Color.WHITE);
				//设置padding 用到了pd转px工具
				tv.setPadding(DensityUtils.dip2px(getApplicationContext(), 15),DensityUtils.dip2px(getApplicationContext(), 12) , 
						DensityUtils.dip2px(getApplicationContext(), 15), DensityUtils.dip2px(getApplicationContext(), 12));
			}else{
				//复用view
				tv = (TextView) convertView;
			}
			//设置textview显示内容
			NumberAndName child = getChild(groupPosition,childPosition);
			tv.setText(child.getName()+" "+child.getNumber());
			return tv;
		}

	}
	
	private void initView() {
		setContentView(R.layout.activity_service_number);
		elv_showservicenumber = (ExpandableListView) findViewById(R.id.elv_servicenumber_show);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		mAdapter = new mAdapter();
		elv_showservicenumber.setAdapter(mAdapter);
	}
}
