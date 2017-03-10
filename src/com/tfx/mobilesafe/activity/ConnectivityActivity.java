package com.tfx.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-8
 * @desc      流量统计Activity

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: ConnectivityActivity.java 35 2016-09-10 13:47:42Z tfx $
 */

public class ConnectivityActivity extends Activity{
	private Context mContext;
	private ListView lv;
	private List<ConnectivityBean> list = new Vector<ConnectivityBean>();
	private static final int START = 1;
	private static final int FINISH = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		initData();
		super.onCreate(savedInstanceState);
	}

	private Handler mHandler = new Handler(){
		private ProgressDialog pd;
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START:
				//显示加载数据对话框
				pd = new ProgressDialog(mContext);
				pd.setTitle("注意");
				pd.setMessage("正在加载流量信息");
				pd.show();
				break;
				
			case FINISH:
				//关闭对话框
				pd.dismiss();
				mAdapter.notifyDataSetChanged();
				break;
				
			default:
				break;
			}
		};
	};
	private ConnectivityManager mCm;
	private mAdapter mAdapter;
	
	/**
	 * 读取文件信息
	 * @param path 文件路径
	 */
	private long readFile(String path){
		long size = 0;
		try {
			//创建文件输入流
			FileInputStream fis = new FileInputStream(new File(path));
			//带缓冲字符输出流
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = br.readLine();//读一行
			size = Long.parseLong(str);
			fis.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return size;
	}
	
	private void initData() {
		new Thread(){
			public void run() {
				//发送加载数据信息
				mHandler.obtainMessage(START).sendToTarget();
				
				//加载数据
				//获取所有app信息
				List<AppInfoBean> allInstalledAppInfo = AppInfoUtils.getAllInstalledAppInfo(mContext);
				for (AppInfoBean appInfoBean : allInstalledAppInfo) {
					//app的数量发送和接收信息 的文件路径
					String rcvPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_rcv";
					String sndPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_snd";
					//判断是否有流量信息  需要读取这个文件
					long rcvSize = readFile(rcvPath);
					long sedSize = readFile(sndPath);
//					if(rcvSize == 0 && sedSize == 0){
//						//没有流量信息
//					}else{
						//有流量信息 封装流量信息的bean
						ConnectivityBean bean = new ConnectivityBean();
						bean.icon = appInfoBean.getIcon();
						bean.sedSize = sedSize;
						bean.rcvSize = rcvSize;
						bean.type = mCm.getActiveNetworkInfo().getTypeName();
						list.add(bean);
//					}
				}
				
				//发送加载数据完成消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}
	
	private class ConnectivityBean{
		public long rcvSize;
		public long sedSize;
		public Drawable icon;
		public String type;
	}

	private class mAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView != null){
				holder = (ViewHolder) convertView.getTag();
			}else{
				convertView = View.inflate(mContext, R.layout.item_connectivity_lv, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_item_connectivity_icon);
				holder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_connectivity_type);
				holder.tv_sedSize = (TextView) convertView.findViewById(R.id.tv_item_connectivity_sedSize);
				holder.tv_rcvSize = (TextView) convertView.findViewById(R.id.tv_item_connectivity_revSize);
				convertView.setTag(holder);
			}
			//赋值
			ConnectivityBean bean = list.get(position);
			holder.iv_icon.setImageDrawable(bean.icon);
			holder.tv_type.setText(bean.type);
			holder.tv_rcvSize.setText("接收: "+Formatter.formatFileSize(mContext, bean.rcvSize));
			holder.tv_sedSize.setText("发送: "+Formatter.formatFileSize(mContext, bean.sedSize));
			return convertView;
		}
	}
	
	private class ViewHolder{
		ImageView iv_icon;
		TextView tv_rcvSize;
		TextView tv_sedSize;
		TextView tv_type;
	}
	
	private void initView() {
		setContentView(R.layout.activity_connectivity);
		mContext = this;
		lv = (ListView) findViewById(R.id.lv_connectivity);
		mAdapter = new mAdapter();
		lv.setAdapter(mAdapter);
		//ConnectivityManager
		mCm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}
}
