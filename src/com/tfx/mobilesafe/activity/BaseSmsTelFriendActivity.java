package com.tfx.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.domain.ContactBean;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.ShowToastUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-24
 * @desc      显示所有联系人的界面

 * @version   $Rev: 19 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-29 23:17:19 +0800 (星期五, 29 七月 2016) $
 * @id        $Id: BaseSmsTelFriendActivity.java 19 2016-07-29 15:17:19Z tfx $
 */   

public abstract class BaseSmsTelFriendActivity extends ListActivity {
	protected static final int LOADING = 0;
	protected static final int FINISH = 1;
	private ListView lv;
	private mAdapter adapter;
	List<ContactBean> mDatas = new ArrayList<ContactBean>(); //默认大小 是10个数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化界面
		initView();
		//数据
		initData();
		//事件
		initEvent();
	}

	private void initEvent() {
		//listview的item点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//获取当前位置的item
				ContactBean contact = (ContactBean) lv.getItemAtPosition(position);
				Intent data = new Intent();
				data.putExtra(MyConstants.SAFENUMBER, contact.getPhone()); //携带数据
				setResult(1, data); 
				finish();
			}
		});
	}

	private Handler mHandler = new Handler(){
		private ProgressDialog dialog;
		//主线程
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				//对话框显示加载数据
				dialog = new ProgressDialog(BaseSmsTelFriendActivity.this);
				dialog.setTitle("注意");
				dialog.setMessage("正在玩命加载数据......");
				dialog.show();
				break;
			case FINISH:
				//关闭对话框 
				dialog.dismiss();
				if(mDatas.isEmpty()){
					ShowToastUtils.showToast(BaseSmsTelFriendActivity.this, "数据为空,请返回上一个界面");
					return;
				}
				//更新数据
				adapter.notifyDataSetChanged(); //通知界面刷新数据
			default:
				break;
			}
			
		};
	};
	
	private void initData() {
		new Thread(new Runnable(){
			//数据加载
			@Override
			public void run() {
				//1.提醒用户正在加载数据
				mHandler.obtainMessage(LOADING).sendToTarget(); //一句代码直接发送
				//2.加载数据 
				mDatas = getDatas();
				//模拟耗时
				SystemClock.sleep(500);
				//3.数据加载完成 
				//发送数据加载完成消息 关闭对话框...
				mHandler.obtainMessage(FINISH).sendToTarget(); 
			}
		}){
		}.start();
	}
	
	public abstract List<ContactBean> getDatas();

	//获取联系人数据的适配器
	private class mAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mDatas.size();
		}

		//当前位置的item对象(ContactBean)
		@Override
		public ContactBean getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}   
 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder  = null;
			if(convertView == null){
				//没有缓存 
				//布局转成view
				convertView = View.inflate(getApplicationContext(), R.layout.item_contact_lv, null);
				//使用ViewHolder的目的是避免反复findviewbyid  getCount数目有多少 该方法就会执行多少次 该方法执行多少次 findviewbyid就会执行多少次
				holder = new ViewHolder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_contact_name);
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_item_contact_phone);
				convertView.setTag(holder); //设置标记给convertView
			}else{
				//有缓存
				//取出标记
				holder = (ViewHolder)convertView.getTag();
			}
			//获取数据
//			ContactBean contact = mDatas.get(position);
			ContactBean contact = getItem(position); //使用自己写的getItem方法获取当前位置的contactbean对象
			//设置数据
			holder.tv_name.setText(contact.getName());
			holder.tv_phone.setText(contact.getPhone());
			
			return convertView;
		}
	}
	
	private static class ViewHolder{
		TextView tv_name;
		TextView tv_phone;
	}
	
	private void initView() {
		lv = getListView();
		//设置适配器
		adapter = new mAdapter();
		lv.setAdapter(adapter);
	}
}
