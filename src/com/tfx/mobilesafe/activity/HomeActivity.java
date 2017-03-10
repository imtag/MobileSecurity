package com.tfx.mobilesafe.activity;

import java.util.List;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.VirusDao;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.service.BlackListService;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.utils.DensityUtils;
import com.tfx.mobilesafe.utils.MD5Utils;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-20
 * @desc      主页面Activity

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: HomeActivity.java 35 2016-09-10 13:47:42Z tfx $  
 */

public class HomeActivity extends Activity{

	private ImageView home_iv_logo; 
	private ImageView home_iv_setting;
	private GridView home_gv;
	private AlertDialog mAD;
	
	private static final String[] names = new String[] {"防盗丢失","骚扰拦截","软件管家","手机加速","缓存清理","高级工具"};
	private static final String[] desc = new String[] {"手机丢失好找","防骚扰防监听","方便管理软件","保持手机通畅","手机快步如飞","特性处理更好"};
	private static final int[] icon = new int[] {R.drawable.home_logo1,R.drawable.home_logo2,R.drawable.home_logo3,R.drawable.home_logo4,R.drawable.home_logo5,R.drawable.home_logo6};
	private ArcProgress home_ap;
	private FrameLayout home_fl;
	private int itemHeight;
	private int hheight;
	private ImageView home_iv_flow;
	private TextView home_tv_desc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView(); //初始化view
//		initLogoAnimation(); //logo 360度旋转动画   
		initDate(); //初始化黑名单拦截服务
		initVirusScan(); //检测病毒
		initEvent(); //监听事件
	}

	private void initEvent() {
		//动态获取ArcProgress的父控件FrameLayout  动态设置ARCProgress的宽高
		ViewTreeObserver vto = home_fl.getViewTreeObserver(); //注册控件观察者
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { //添加全局布局监听
			@Override   
		    public void onGlobalLayout() { 
		    home_fl.getViewTreeObserver().removeGlobalOnLayoutListener(this); //每次监听前删除上次监听 以免重复监听
	    	int height = home_fl.getHeight() * 3/5; 
	    	//布局参数
	    	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(height, height,Gravity.CENTER);
	    	//设置arcprogress宽高
	    	home_ap.setLayoutParams(params);
		    }   
		});  
		
		//按钮单击事件
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				//流量统计
				case R.id.home_iv_flow:{
					Intent intent = new Intent(getApplicationContext(),ConnectivityActivity.class);
					startActivity(intent);
					break;
				}
				
				//设置中心
				case R.id.home_iv_setting:{
					Intent intent = new Intent(getApplicationContext(),SettingCenterActivity.class);
					startActivity(intent);
					break;
				}
				
				//progress
				case R.id.ap_home:{
					Intent intent = new Intent(getApplicationContext(),AntivirusActivity.class);
					startActivity(intent);
					break;
				}

				default:
					break;
				}
			}
		};
		home_iv_flow.setOnClickListener(listener );
		home_iv_setting.setOnClickListener(listener);
		home_ap.setOnClickListener(listener);
		
		//gridview条目点击事件
		home_gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: //手机防盗
					//首次使用 显示设置密码的对话框  设置过密码,显示输入密码对话框
					String password = SPUtils.getString(getApplicationContext(), MyConstants.PASSWORD, null);
					if(TextUtils.isEmpty(password)){
						//首次使用 显示设置密码的对话框  
						showSetPasswordDialog();
					}else{
						//设置过密码,显示输入密码对话框
						showEnterPasswordDialog();
					}
					break;
					
				case 1: //通讯卫士
				{   
					Intent intent = new Intent(getApplicationContext(),RefreshListBlackListActivity.class);
					startActivity(intent);
					break;  
				}
				case 2: //软件管家
				{   
					Intent intent = new Intent(getApplicationContext(),AppManagerActivity_StickyListHeaders.class);
					startActivity(intent);
					break;  
				}
				case 3: //进程管家
				{  
					long clearTime = SPUtils.getLong(getApplicationContext(), MyConstants.CLEARTIME, 0);
					if(System.currentTimeMillis() - clearTime < 10000){
						Toast.makeText(getApplicationContext(), "您的手机非常干净，无需清理", 1).show();
						return;
					}
					Intent intent = new Intent(getApplicationContext(),ProcessManagerActivity.class);
					startActivity(intent);
					break;  
				}
				case 4: //缓存清理
				{  
					Intent intent = new Intent(getApplicationContext(),CacheClearActivity.class);
					startActivity(intent);
					break;  
				}
			
				case 5://高级工具
				{
					Intent intent = new Intent(getApplicationContext(),AToolsActivity.class);
					startActivity(intent);
					break;
				}
				default:
					break;  
				}
			}
		});
	}

	//再次进入 输入密码
	protected void showEnterPasswordDialog() {
		showPasswordDialog(false);
	}

	//首次进入 设置密码
	protected void showSetPasswordDialog() {
		showPasswordDialog(true);
	}

	//设置密码和输入密码通用对话框
	/**
	 * @param isSetPassword
	 *     true 设置密码
	 *     false 输入密码
	 */
	private void showPasswordDialog(final boolean isSetPassword) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_setpassword, null); // 布局文件转换成view

		// 获取view的子控件
		final EditText et_pass1 = (EditText) view.findViewById(R.id.dialog_et_password1);
		final EditText et_pass2 = (EditText) view.findViewById(R.id.dialog_et_password2);
		TextView tv_Title = (TextView) view.findViewById(R.id.dialog_tv_title);
		
		final Button bt_confirm = (Button) view.findViewById(R.id.dialog_bt_confirm);
		final Button bt_cancel = (Button) view.findViewById(R.id.dialog_bt_cancel);
		
		if(!isSetPassword){
			//判断是否是输入密码 隐藏pass2 修改标题
			et_pass2.setVisibility(View.GONE); 
			tv_Title.setText("输入密码");
		}
		
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == bt_confirm) {
					// 点击的是确认按钮
					// 取出输入的两个密码  
					String pass1 = et_pass1.getText().toString().trim();
					String pass2 = et_pass2.getText().toString().trim();
					
					if(!isSetPassword){
						pass2 = "aaa"; //输入密码  输入框只有密码一 为了下面验证不为空 加个密码 
					}
					
					if (TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
						// 验证密码是否为空
						Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
						return;
					}
					
					if(!isSetPassword){
						//输入密码   判断输入的密码是否和保存的密码一致   
						//密码验证是md5的比较
						if(MD5Utils.encode(pass1).equals(SPUtils.getString(getApplicationContext(), MyConstants.PASSWORD, ""))){
							//密码验证成功  跳到手机防盗界面
							Intent intent = new Intent(getApplicationContext(), LostFindActivity.class);
							startActivity(intent);
						}else{
							Toast.makeText(getApplicationContext(), "密码错误", 0).show();
							return;
						}
						
					}else{
						//设置密码
						if (pass1.equals(pass2)) {
							// 两次密码一样  保存密码  先对密码md5加密 
							SPUtils.putString(getApplicationContext(),MyConstants.PASSWORD, MD5Utils.encode(pass1));
							Toast.makeText(getApplicationContext(), "密码设置成功", 0).show();
						} else {
							// 两次密码不一样
							Toast.makeText(getApplicationContext(), "两次密码不一样", 0).show();
							return;
						}
					}
					
					mAD.dismiss();
				} else {
					// 点击的取消按钮 关闭对话框
					mAD.dismiss();
				}
			}

		};

		// 给两个按钮设置同一个监听事件
		bt_confirm.setOnClickListener(listener);
		bt_cancel.setOnClickListener(listener);
	
		dialog.setView(view); // 将view设置给dialog
		mAD = dialog.create(); // 创建对话框变为成员变量 用来关闭对话框使用
		mAD.show();
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1://开始检测
				home_tv_desc.setText("检测中");
				break;
			case 2://每扫描到一个病毒  显示一次当前得分
				int virusCount = (Integer) msg.obj;
				home_ap.setProgress(100 - virusCount*10);
				break;
			case 3://检测完成
				home_tv_desc.setText("点击杀毒");
				break;
			default:
				break;
			}
		};
	};
	
	//初始化病毒扫描
	private void initVirusScan() {
		new Thread(){
			@Override
			public void run() {
				//1.发送正在检测消息
				mHandler.obtainMessage(1).sendToTarget();
				
				int virusCount = 0;
				//2.获取所有安装的app 并扫描是否有病毒
				List<AppInfoBean> allInstalledAppInfo = AppInfoUtils.getAllInstalledAppInfo(getApplicationContext());
				for (AppInfoBean appInfoBean : allInstalledAppInfo) {
					//获得app目录
					String sourceDir = appInfoBean.getSourceDir(); 
					//获得app包的md5值
					String md5 = MD5Utils.getFileMd5(sourceDir);
					//判断是否是病毒
					if(VirusDao.isVirus(md5)){
						virusCount++;
						
						//发送病毒数量给handler做分数更新 
						Message msg = mHandler.obtainMessage(2);
						msg.obj = virusCount; //发送对象
						mHandler.sendMessage(msg);//发送扫描结果
					}
				}
			
				//3.发送扫描完成消息
				mHandler.obtainMessage(3).sendToTarget();
			};
		}.start();
	}
	
	private void initDate() {
		//初始化黑名单拦截服务
		if(SPUtils.getBoolean(getApplicationContext(), MyConstants.IS_FIRST_USE_BLACKLIST, true)){
			Intent service = new Intent(HomeActivity.this,BlackListService.class);
			startService(service);
		}
	}

	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// 展示的数量
			return names.length;
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

		//getCount有多少条  该方法就会执行多少次
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout view = null;
			if(convertView != null){
				//复用view
				view = (LinearLayout) convertView ;
			}else{
				//界面转换成view
				view = (LinearLayout) View.inflate(getApplicationContext(), R.layout.item_home_gv, null); 
			}
			//动态设置gridview条目宽高
			int height = (home_gv.getHeight() - DensityUtils.dip2px(getApplicationContext(), home_gv.getNumColumns())) / 2;
			//布局参数
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,height);
			//设置给arcprogress
			view.setLayoutParams(param);
			
			//找子控件
			ImageView item_icon = (ImageView) view.findViewById(R.id.item_home_gv_icon); 
			TextView item_iv = (TextView) view.findViewById(R.id.item_home_gv_title);
			
			//赋值
			item_icon.setImageResource(icon[position]); //把数据设置到控件上
			item_iv.setText(names[position]);
			return view;
		}
	}
	
	private void initLogoAnimation() {
		//导入nineoldandroids来兼容低于11版本显示属性动画
		
		//属性动画:对属性变化过程一系列的操作组成动画  
		
		//使用属性动画来完成logo的旋转  propertyName setXXX 把set后面单词首字符改成小写 
		ObjectAnimator oa = ObjectAnimator.ofFloat(home_iv_logo, "rotationY", 0,60,120,180,240,300,360);
		oa.setDuration(2000); //一次动画完成时间
		oa.setRepeatCount(ObjectAnimator.INFINITE); //-1 无限重复
		oa.start();
	}

	private void initView() {
		setContentView(R.layout.activity_home);
		home_iv_logo = (ImageView) findViewById(R.id.home_iv_logo);
		home_iv_setting = (ImageView) findViewById(R.id.home_iv_setting);
		home_iv_flow = (ImageView) findViewById(R.id.home_iv_flow);
		home_gv = (GridView) findViewById(R.id.home_gv);
		home_ap = (ArcProgress) findViewById(R.id.ap_home);
		home_fl = (FrameLayout) findViewById(R.id.fl_home);
		home_tv_desc = (TextView) findViewById(R.id.tv_home_scanVirus);
		
		home_gv.setAdapter(new MyAdapter());
	}
}
