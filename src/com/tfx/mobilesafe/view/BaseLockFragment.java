package com.tfx.mobilesafe.view;

import java.util.List;
import java.util.Vector;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.LockedDao;
import com.tfx.mobilesafe.domain.AppInfoBean;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-9-7
 * @desc      基类fragment 代码都写这里

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: BaseLockFragment.java 35 2016-09-10 13:47:42Z tfx $
 */

public class BaseLockFragment extends Fragment {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private mAdapter adapter;
	private List<AppInfoBean> mDatas = new Vector<AppInfoBean>();

	//这里获取不到上下文 需要传过来
    private LockedDao lockDao;
	public void setLockDao(LockedDao lockDao){
		this.lockDao = lockDao;
	}
	
	//把applockactivity初始化的allInstalledAppInfo传过来 就不用在子线程加载数据 浪费时间
	List<AppInfoBean> allInstalledAppInfo;
	public void setAllInstalledAppInfo(List<AppInfoBean> allInstalledAppInfo){
		this.allInstalledAppInfo = allInstalledAppInfo;
	}
	
	//获取所有加锁包的操作在初始化Activity时完成 传过来 为了不耗时
	List<String> allLockedPackage;
	public void setAllLokedPackage(List<String> allLockedPackage){
		this.allLockedPackage =  allLockedPackage;
	}
	
	private Handler mHandler = new Handler(){
		private ProgressDialog pd;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				//显示加载数据对话框
				pd = new ProgressDialog(getActivity());
				pd.setTitle("注意");
				pd.setMessage("正在玩命加载数据中");
				pd.show();
				break;
			case FINISH:
				//加载数据完成 关闭对话框
				if(pd != null && pd.isShowing()){
					pd.dismiss();
					pd = null; //以免pd存在 下次又new
				}
				//显示数据
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	private void initData() {
		new Thread(){
			public void run() {
				mHandler.obtainMessage(LOADING).sendToTarget();//加载数据消息
				//加载数据
			    //未加锁和已加锁页面做统一处理 
			    mDatas.clear();
			    for (AppInfoBean appInfoBean : allInstalledAppInfo) {
					if(BaseLockFragment.this instanceof AppLockFragment && allLockedPackage.contains(appInfoBean.getPackName())){
						//如果当前fragment是加锁  把已加锁的app添加到list容器做已加锁页面数据显示
						mDatas.add(appInfoBean);
					}else if(BaseLockFragment.this instanceof AppUnlockFragment && !allLockedPackage.contains(appInfoBean.getPackName())){
						//如果当前fragment是未加锁  把未加锁的app添加到list容器做未加锁页面数据显示
						mDatas.add(appInfoBean);
					}else{
						//不作处理
					}
				}
				mHandler.obtainMessage(FINISH).sendToTarget();//加载数据完成消息
			};
		}.start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	/*	
	    TextView tv = new TextView(getActivity());
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);
		if(this instanceof AppLockFragment){
			tv.setText("加锁");
		}else{
			tv.setText("未加锁");
		}
	*/
		//加载控件 listview
		StickyListHeadersListView view =(StickyListHeadersListView) View.inflate(getActivity(), R.layout.fragment_lock_view, null);
		if(adapter == null ){
			adapter = new mAdapter();
		}
		//每次都要做适配器
		view.setAdapter(adapter);
		return view;
	}
	
	private class mAdapter extends BaseAdapter implements StickyListHeadersAdapter{

		@Override
		public int getCount() {
			return mDatas.size();
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
			ViewHolder holder = null;
			if(convertView == null){
				//布局转view
				convertView = View.inflate(getActivity(), R.layout.item_applocked_lv, null);
				//缓存view
				holder = new ViewHolder();
				holder.icon = (ImageView)convertView.findViewById(R.id.item_iv_applock_icon);
				holder.packName = (TextView)convertView.findViewById(R.id.item_tv_applock_name);
				holder.lockState = (ImageView)convertView.findViewById(R.id.item_iv_applock_lockState);
				//标签
				convertView.setTag(holder);
			}else{
				//取出
				holder = (ViewHolder) convertView.getTag();
			}
			//赋值
			if(position < 0 || position >= mDatas.size()){
				//这个判断 防止数组越界
				return convertView;
			}
			final AppInfoBean bean = mDatas.get(position);
			holder.icon.setImageDrawable(bean.getIcon());
			holder.packName.setText(bean.getAppName());
			//判断当前是未加锁页面还是加锁页面 设置相应的icon
			if(BaseLockFragment.this instanceof AppLockFragment){
				holder.lockState.setImageResource(R.drawable.unlock);
			}else{
				holder.lockState.setImageResource(R.drawable.lock);
			}
			
			final View RootView = convertView;
			//点击加锁 解锁事件
			holder.lockState.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(BaseLockFragment.this instanceof AppLockFragment){
						//解锁业务
						lockDao.delete(bean.getPackName());
						//解锁动画
						RootView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.locked_translate));
					}else{
						//加锁业务
						lockDao.add(bean.getPackName());
						//加锁动画
						RootView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.unlocked_translate));
					}
					new Thread(){
						public void run() {
							SystemClock.sleep(500);//动画执行需要500毫秒 所以要sleep
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//都要删除这个bean 如果加锁 则未加锁的容器中要移除这个bean 如果解锁 则已加锁的容器中要移除这个bean
									mDatas.remove(bean);
									//更新界面
									adapter.notifyDataSetChanged();
								}
							});
						};
					}.start();
				}
			});
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView tv = new TextView(getActivity());
			tv.setTextSize(16);
			tv.setTextColor(Color.WHITE);
			tv.setBackgroundColor(Color.GRAY);
			AppInfoBean appInfoBean = mDatas.get(position);
			if(appInfoBean.isSystem()){
				tv.setText("系统软件");
			}else{
				tv.setText("用户软件");
			}
			return tv;
		}

		@Override
		public long getHeaderId(int position) {
			AppInfoBean appInfoBean = mDatas.get(position);
			if(appInfoBean.isSystem()){
				return 1;
			}else{
				return 2;
			}
		}
	}
	
	private class ViewHolder{
		ImageView icon;
		TextView packName;
		ImageView lockState;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//初始化数据
		initData();
	}
}
