package com.tfx.mobilesafe.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.itheima11.refreshlistview_lib.RefreshListView;
import com.itheima11.refreshlistview_lib.RefreshListView.OnRefreshingDataListener;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.BlackListDao;
import com.tfx.mobilesafe.db.BlackListDB;
import com.tfx.mobilesafe.domain.BlackBean;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.ShowToastUtils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-27
 * @desc      通讯卫士页面
    
 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: RefreshListBlackListActivity.java 35 2016-09-10 13:47:42Z tfx $
 */

public class RefreshListBlackListActivity extends Activity {
	private Button iv_blacklist_add;
	private RefreshListView lv_blacklist_showdata;
	private TextView tv_blacklist_nodata;
	private LinearLayout ll_progressbar_root;
	private BlackListDao mBlackListDao;
	private List<BlackBean> mBlackBeans ;
	private ImageView iv_item_blacklist_delete;
	private MyAdapter adapter;
	private static final int LOADING = 1;
	private static final int FINISH = 2;
	private PopupWindow pw;
	private View contentView;
	private ScaleAnimation scaleAnimation;
	private EditText et_phone;
	private AlertDialog mAlertDialog;
	
	private boolean isAddNewData; //判断是否添加了新的数据
	
	private int showCount = 10; //分配加载数据数量
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(); //界面
		initData(); //数据 可能执行多次
		initEvent(); //事件
		initPopupWindow(); //弹出窗口
		initAddBlacklistDialog(); //添加黑名单数据对话框
	}

	//手动添加联系人对话框
	private void initAddBlacklistDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(RefreshListBlackListActivity.this);
		//自定义view
		View mAlertView = View.inflate(getApplicationContext(), R.layout.dialog_addblacklist, null);
		
		et_phone = (EditText) mAlertView.findViewById(R.id.et_dialog_addblacklist_phone);
		final CheckBox cb_phonemode = (CheckBox) mAlertView.findViewById(R.id.cb_dialog_addblacklist_phonemode);
		final CheckBox cb_smsmode = (CheckBox) mAlertView.findViewById(R.id.cb_dialog_addblacklist_smsmode);
		Button bt_add = (Button) mAlertView.findViewById(R.id.bt_dialog_addblacklist_add);
		Button bt_cancel = (Button) mAlertView.findViewById(R.id.bt_dialog_addblacklist_cancel);
		
		//添加按钮单击事件 
		bt_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				//1.黑名单号码不能为空
				String phone = et_phone.getText().toString().trim();
				
				if(TextUtils.isEmpty(phone)){
					ShowToastUtils.showToast(RefreshListBlackListActivity.this, "请输入电话号码");
					return;
				}
				//2.拦截模式不能为空
				if(!cb_phonemode.isChecked() && !cb_smsmode.isChecked()){
					ShowToastUtils.showToast(RefreshListBlackListActivity.this, "请选择拦截模式");
					return;
				}
				//3.添加黑名单数据
				BlackBean bean = new BlackBean();
				bean.setPhone(phone);
				//4.判断选择的拦截模式
				int mode = 0;
				/*
				if(cb_phonemode.isChecked()){
					mode = BlackListDB.PHONE_MODE;
				}
				if(cb_smsmode.isChecked()){
					mode = BlackListDB.SMS_MODE;
				}
				if(cb_smsmode.isChecked() && cb_phonemode.isChecked()){
					mode = BlackListDB.ALL_MODE;
				}
				*/
				if(cb_phonemode.isChecked()){
					mode |= BlackListDB.PHONE_MODE; //mode = mode | BlackListDB.PHONE_MODE  当sms和phone都选中时  mode = sms | phone = allmode
				}
				if(cb_smsmode.isChecked()){
					mode |= BlackListDB.SMS_MODE; //mode = mode | BlackListDB.SMS_MODE
				}
				bean.setMode(mode);
				mBlackListDao.update(bean); //将黑名单添加到数据库
				//5.显示最新添加的黑名单数据
				 isAddNewData = true;
				initData();
				//6.记得关闭对话框
				mAlertDialog.dismiss();
			}
		});
		
		//取消按钮单击事件
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭对话框
				mAlertDialog.dismiss();   
			}  
		});
		
		//设置对话框显示的内容
		ab.setView(mAlertView);
		//创建对话框
		mAlertDialog = ab.create();
	}  

	//弹出窗口
	private void initPopupWindow() {
		//弹出框显示的内容
		contentView = View.inflate(this , R.layout.popupwindow_addblacklist, null);
		//初始化弹出窗口
//		pw = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pw = new PopupWindow(contentView, -2, -2); //-2包裹内容
		//获取焦点
		pw.setFocusable(true);
		//设置背景 必须
		ColorDrawable color = new ColorDrawable(0xffffff);
		pw.setBackgroundDrawable(color); //设置背景
		//点击弹出窗口的外部关闭弹出窗口
		pw.setOutsideTouchable(true);
		//弹出框动画
		scaleAnimation = new ScaleAnimation(
				1.0f, 1.0f, 
				0, 1.0f,             
				Animation.RELATIVE_TO_SELF,0.5f,
				Animation.RELATIVE_TO_SELF, 0
				);
		scaleAnimation.setDuration(300);
		
		pw.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		
		//获取弹出框布局文件的子控件 做监听事件
		TextView tv_addblack_manual = (TextView) contentView.findViewById(R.id.popupwindow_addblacklist_Manual);
		TextView tv_addblack_sms = (TextView) contentView.findViewById(R.id.popupwindow_addblacklist_sms);
		TextView tv_addblack_phone = (TextView) contentView.findViewById(R.id.popupwindow_addblacklist_phone);
		TextView tv_addblack_friend = (TextView) contentView.findViewById(R.id.popupwindow_addblacklist_friend);
		
		//四个按钮的监听
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.popupwindow_addblacklist_Manual:
					manualAdd();
					break;
					
				case R.id.popupwindow_addblacklist_sms:
					smsLogAdd(); 
					break;
					
				case R.id.popupwindow_addblacklist_phone:
					callLogAdd();
					break;
					
				case R.id.popupwindow_addblacklist_friend:
					friendAdd();
					break;

				default:
					break;
				}
				//关闭弹出框
				pw.dismiss();
			}
		};
				
		//给四个按钮设置同一个监听事件
		tv_addblack_manual.setOnClickListener(listener);
		tv_addblack_sms.setOnClickListener(listener);
		tv_addblack_phone.setOnClickListener(listener);
		tv_addblack_friend.setOnClickListener(listener);
	}

	private void showAddDialog(String phone){
		mAlertDialog.show();
		et_phone.setText(phone);
	}
	
	protected void friendAdd() {
		Intent friend = new Intent(getApplicationContext(), FriendsActivity.class);
		startActivityForResult(friend, 0);
	}

	protected void callLogAdd() {
		Intent call = new Intent(getApplicationContext(), CallLogActivity.class);
		startActivityForResult(call, 0);
	}

	protected void smsLogAdd() {
		Intent sms = new Intent(getApplicationContext(), SmsLogActivity.class);
		startActivityForResult(sms, 0);
	}

	protected void manualAdd() {
		showAddDialog("");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//获取选择的号码
		if(data != null){
			String phone = data.getStringExtra(MyConstants.SAFENUMBER);
			showAddDialog(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//事件
	private void initEvent() {
		//添加黑名单按钮事件
		iv_blacklist_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 显示弹出窗口在该按钮下面
//				pw.showAsDropDown(v);
				
				pw.showAtLocation(lv_blacklist_showdata, Gravity.CENTER, 0, 20);
				// 开始动画
				contentView.startAnimation(scaleAnimation);
				
				//设置当前窗体背景为透明0.2  记得关闭弹出窗体要设置当前窗体不透明
				LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.5f;
				getWindow().setAttributes(lp);
				
				//设置弹出窗体背景颜色
				ColorDrawable color = new ColorDrawable(0xe5e5e5);
				pw.setBackgroundDrawable(color);
				
			}
		});
		
		//refreshlistview的动作事件
		lv_blacklist_showdata.setOnRefreshingDataListener(new OnRefreshingDataListener() {
			
			@Override
			public void onHeadRefreshing() {
				//下拉刷新头  显示最新数据
				isAddNewData = true;
				initData();
			}
			
			@Override
			public void onFooterFreshing() {
				//上拉刷新尾  显示更多数据
				initData();
			}
		});
	}

	//黑名单数据适配器
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mBlackBeans.size();
		}

		@Override
		public BlackBean getItem(int position) {
			return mBlackBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){ //判断是否有缓存
				//没缓存
				convertView = View.inflate(getApplicationContext(), R.layout.item_blacklist_lv, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_logo = (ImageView) convertView.findViewById(R.id.iv_item_blacklist_logo);
				viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_item_blacklist_phone);
				viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_item_blacklist_mode);
				viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_item_blacklist_delete);
				//设置标签
				convertView.setTag(viewHolder);
			}else{
				//有缓存
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//1.取值
			final BlackBean bean = getItem(position);
			//2.显示值
			viewHolder.tv_phone.setText(bean.getPhone());
			switch (bean.getMode()) {
			case BlackListDB.SMS_MODE:
				viewHolder.tv_mode.setText("拦截短信");
				viewHolder.iv_logo.setImageResource(R.drawable.blacklist_sms);
				break;

			case BlackListDB.PHONE_MODE:
				viewHolder.iv_logo.setImageResource(R.drawable.blacklist_phone);
				viewHolder.tv_mode.setText("拦截来电");
				break;

			case BlackListDB.ALL_MODE:
				viewHolder.iv_logo.setImageResource(R.drawable.blacklist_all);
				viewHolder.tv_mode.setText("拦截全部");
				break;
			default:
				break;
			}
			//3.删除按钮事件
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//删除数据
					
					//1.本地删除
					mBlackBeans.remove(bean);
					//2.数据库删除
					mBlackListDao.delete(bean.getPhone());
					//3.通知更新新界面
					adapter.notifyDataSetChanged();
					initData();
				}
			});
			return convertView;
		}
		
	}
	
	private static class ViewHolder{
		TextView tv_phone ;
		TextView tv_mode ;
		ImageView iv_delete ;
		ImageView iv_logo;
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING://加载数据
				//1.显示加载数据进度条
				ll_progressbar_root.setVisibility(View.GONE);
				//2.隐藏显示数据的listview和没有数据的图片  
				lv_blacklist_showdata.setVisibility(View.VISIBLE);
				tv_blacklist_nodata.setVisibility(View.GONE);
				break;
			case FINISH: //加载数据完成
				lv_blacklist_showdata.finishRefreshing();//关闭刷新状态
				//2.判断是否有黑名单数据
				if(mBlackBeans.isEmpty()){
					//没有数据  隐藏listview 显示没有数据的图片
					lv_blacklist_showdata.setVisibility(View.GONE);
					tv_blacklist_nodata.setVisibility(View.VISIBLE);
				}else{
					//有数据 显示listview 隐藏没有数据的图片 
					lv_blacklist_showdata.setVisibility(View.VISIBLE);
					tv_blacklist_nodata.setVisibility(View.GONE);
					//刷新界面
					adapter.notifyDataSetChanged();
					//添加了新数据就要回滚到0位置
					if(isAddNewData){
						lv_blacklist_showdata.smoothScrollToPosition(0);
						isAddNewData = false;
					}
				}
				break;
			default:
				break;
			}
		};
	};

	
	private void initData() {
		// 数据过大 耗时操作 子线程访问
		new Thread() {
			public void run() {
				// 1.发送正在获取数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				//添加了新的数据 要先清除数据  再加载数据
				if(isAddNewData){
					mBlackBeans.clear();
				}
				// 2.加载数据
				//添加新增的数据  每加载一批数据  把数据添加到blackbeans
				mBlackBeans.addAll(mBlackListDao.loadMore(mBlackBeans.size(), showCount)); //如果第一页显示10条数据  最后一条数据索引是9  所以下一批开始的索引刚好是当前页的数据size
				// 3.发送数据加载完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}

	private void initView() {
		//加载布局
		setContentView(R.layout.activity_blacklist_refreshlist);
		
		//初始化控件
		iv_blacklist_add = (Button) findViewById(R.id.iv_blacklist_add);
		tv_blacklist_nodata = (TextView) findViewById(R.id.tv_blacklist_nodata);
		iv_item_blacklist_delete = (ImageView) findViewById(R.id.iv_item_blacklist_delete);
		ll_progressbar_root = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		lv_blacklist_showdata = (com.itheima11.refreshlistview_lib.RefreshListView) findViewById(R.id.lv_blacklist_showdata);
		// 启用下拉刷新头
//		lv_blacklist_showdata.isEnableRefreshHead(true);
		// 启用上拉刷新尾
		lv_blacklist_showdata.isEnableRefreshFoot(true);

		// 初始化黑名单数据
		mBlackBeans = new Vector<BlackBean>();
		
		//绑定适配器
		adapter = new MyAdapter();
		lv_blacklist_showdata.setAdapter(adapter);
		
		mBlackListDao = new BlackListDao(this);
		
	}
}
