package com.tfx.mobilesafe.activity;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.dao.VirusDao;
import com.tfx.mobilesafe.domain.AppInfoBean;
import com.tfx.mobilesafe.utils.AppInfoUtils;
import com.tfx.mobilesafe.utils.DensityUtils;
import com.tfx.mobilesafe.utils.MD5Utils;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-8-29
 * @desc      病毒查杀页面

 * @version   $Rev: 35 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-09-10 21:47:42 +0800 (星期六, 10 九月 2016) $
 * @id        $Id: AntivirusActivity.java 35 2016-09-10 13:47:42Z tfx $
 */

public class AntivirusActivity extends Activity {
	private ArcProgress ap_scanFinish;
	private ArcProgress ap_scanProgress;
	private LinearLayout ll_scanCompleted;
	private Button bt_scanAgain;
	private TextView tv_scanDesc;
	private LinearLayout ll_animation;
	private ImageView iv_rightImage;
	private ImageView iv_leftImage;
	private AnimatorSet mAsClose;
	private AnimatorSet mAsOpen;
	private FrameLayout fl_antivirus;
	private Button bt_finish;
	private Context mContext;
	private static final int STARTSCAN = 1;
	private static final int SCANFINISH = 2;
	private static final int SCANING = 3;
	private long startTime = 0;
	private long finishTime = 0;
	private boolean isInitAnimation = true;
	private boolean interruptScan = false;//终止扫描
	private boolean isShowPoint = true;//显示点的线程
	private List<AppInfoBean> allInstalledAppInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		checkVersion();//检测病毒库版本
//		initStartScan();//开始扫描
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//当Activity销毁   则终止扫描病毒的线程
		interruptScan = true;
		isShowPoint = false;
	}

	public void finish(View v) {
		finish();
	}
	
	private void checkVersion() {
		//创建对话框
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("注意");
		ad.setMessage("正在连接服务器...");
		final AlertDialog dialog = ad.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		//点动态增加  正在连接服务器. .. ... ....
		showDynamicPoint(dialog);
		
		//请求网络检测版本
		HttpUtils utils = new HttpUtils();
		utils.configTimeout(3000);
		String url = getResources().getString(R.string.virusversionurl);
		//请求    方式、url、回调
		utils.send(HttpMethod.GET,url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dialog.dismiss();//关闭对话框
				Toast.makeText(mContext, "服务器连接失败", 1).show();
				//主线程中执行
				initStartScan();//开始扫描
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//请求成功 
				dialog.dismiss();//关闭对话框
				final int serverVersion = Integer.parseInt(arg0.result);
				int currentVersion = VirusDao.getCurrentVirusVersion();
				//对比当前版本和服务器版本 判断是否有新版本 有新的病毒 
				if(serverVersion != currentVersion){
					//新版本 有新的病毒要下载
					AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
					ad.setTitle("有新病毒");
					ad.setMessage("是否下载更新");
					ad.setPositiveButton("立即更新", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadNewVirus(serverVersion);//下载新病毒
						}
					});
					ad.setNegativeButton("以后再说", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							initStartScan();//开始扫描
						}
					});
					AlertDialog dialog = ad.create();
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}else{
					//没有新病毒
					Toast.makeText(mContext, "病毒库当前最新", 1).show();
					//主线程中执行
					initStartScan();//开始扫描
				}
			}
		}); 
	}
	
	//显示动态增加的点
	private void showDynamicPoint(final AlertDialog dialog) {
		new Thread(){
			public void run() {
				class Data{
					int number = 1;
				}
				final Data data = new Data();
				
				while(isShowPoint){
				runOnUiThread(new Runnable() {
					public void run() {
						dialog.setMessage("正在连接服务器" + getPointNumber(data.number++ % 7)); //当data.number等于6时 ++再余7就又是1 
					}
				});
				SystemClock.sleep(500);
				}
			};
		}.start();
	}

	//获取点的个数
	public String getPointNumber(int number){
		String res = "";
		for (int i = 0; i < number; i++) {
			res += ".";
		}
		return res;
	};
	
	//下载新的病毒库
	private void downloadNewVirus(final int newVersion) {
		HttpUtils utils = new HttpUtils();
		utils.configTimeout(3000);
		String url = getResources().getString(R.string.virusdatas);
		//请求    方式、url、回调
		utils.send(HttpMethod.GET,url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(mContext, "病毒库更新失败", 1).show();
				initStartScan();//开始扫描
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String jsonData = arg0.result;
				try {
					JSONObject jsonObject = new JSONObject(jsonData);
					String md5 = jsonObject.getString("md5");
					String desc = jsonObject.getString("desc");
					VirusDao.updataVirus(md5, desc);
					VirusDao.updataVirusVersion(newVersion);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Toast.makeText(mContext, "病毒库更新成功", 1).show();
				initStartScan();//开始扫描
			}
		}); 
	};
	
	//重新扫描
	public void scanAgain(View v) {
		mAsOpen.start();
	}
	
	private Handler mHandler = new Handler(){
		private int virusCount = 0; //统计病毒软件数量
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STARTSCAN://开始扫描
				ap_scanFinish.setVisibility(View.GONE);
				ap_scanProgress.setVisibility(View.VISIBLE);
				bt_scanAgain.setVisibility(View.GONE);
				tv_scanDesc.setVisibility(View.GONE);
				ll_animation.setVisibility(View.GONE);
				bt_finish.setVisibility(View.GONE);
				
				//清空已经完成扫描的软件信息
				ll_scanCompleted.removeAllViews();
				
				antivirus_sv.setPadding(0, 0, 0, 0);
				startTime = System.currentTimeMillis();//当期时间
				break;
				
			case SCANING: //正在扫描
				
				tv_scanDesc.setVisibility(View.VISIBLE);
				//取出结果
				ScanAppInfo scanAppInfo = (ScanAppInfo) msg.obj;
				//进度的显示
				ap_scanProgress.setProgress((int)Math.round((scanAppInfo.currentProgress * 100.0 / scanAppInfo.maxProgress)));
				
				//扫描信息的显示
				tv_scanDesc.setText("正在扫描："+scanAppInfo.name); //正在扫描的软件
				
				//显示已完成扫描的软件信息 显示到LinearLayout
				View view = View.inflate(mContext, R.layout.item_antivirus_ll, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.item_iv_antivirus_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.item_tv_antivirus_name);
				ImageView iv_virusState = (ImageView) view.findViewById(R.id.item_iv_antivirus_virusState);
				iv_icon.setImageDrawable(scanAppInfo.icon);
				tv_name.setText(scanAppInfo.name);
				if(scanAppInfo.isVirus){
					//是病毒
					virusCount++;
					iv_virusState.setImageResource(R.drawable.antivirusperm_prompt);
				}else{
					iv_virusState.setImageResource(R.drawable.antivirusperm_grant);
				}
				//把view显示到linearLayout
				ll_scanCompleted.addView(view,0);
				break;
				
			case SCANFINISH://扫描结束
				//设置srocllview的padding 让button显示
				antivirus_sv.setPadding(0, 0, 0, DensityUtils.dip2px(mContext, 50));
				
				//扫描报告
				finishTime = System.currentTimeMillis();
				TextView scanReport = new TextView(mContext);
				scanReport.setPadding(DensityUtils.dip2px(getApplicationContext(),16),DensityUtils.dip2px(getApplicationContext(),12)
						, 16,DensityUtils.dip2px(getApplicationContext(),12));
				scanReport.setTextColor(Color.GRAY); 
				scanReport.setTextSize(18);
				int useTimme = (int)Math.round((finishTime - startTime) / 1000.0);
				if(virusCount == 0){
					ap_scanFinish.setProgress(100);
					scanReport.setText("扫描"+allInstalledAppInfo.size()+"项，用时" + useTimme + "秒，未发现病毒软件");
				}else{
					ap_scanFinish.setProgress(100 - virusCount*10);
					scanReport.setText("扫描"+allInstalledAppInfo.size()+"项，用时" + useTimme + "秒，发现病毒软件" + virusCount +"个，查看下面记录");
				}
				ll_scanCompleted.addView(scanReport, 0);
				
				//拍照
				fl_antivirus.setDrawingCacheEnabled(true); //开启缓存绘制当前图片 提高绘制速度
				fl_antivirus.setDrawingCacheQuality(ArcProgress.DRAWING_CACHE_QUALITY_HIGH); //缓存绘制质量 高
				Bitmap progressImage = fl_antivirus.getDrawingCache(); //获得缓存的图片 前提要开启缓存  
				//绘制两半边的图片
				Bitmap leftImage = getLeftImage(progressImage); //左半边
				Bitmap rightImage = getRightImage(progressImage); //右半边
				//设置图片
				iv_leftImage.setImageBitmap(leftImage);
				iv_rightImage.setImageBitmap(rightImage);
				
				//定义个boolean变量 这样初始化代码只执行一次
				if(isInitAnimation){
					//初始化动画
					initCloseScanProgressAnimation();
					mAsClose.start();
					initOpenScanProgressAnimation();
					isInitAnimation = false;
				}else{
					mAsClose.start();
				}
				
				ap_scanFinish.setVisibility(View.VISIBLE);
				ap_scanProgress.setVisibility(View.GONE);
				bt_scanAgain.setVisibility(View.VISIBLE);
				tv_scanDesc.setVisibility(View.GONE);
				bt_finish.setVisibility(View.VISIBLE);
				
				//显示动画
				ll_animation.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	};
	private ScrollView antivirus_sv;
	
	//初始化关闭扫描进度的动画
	private void initCloseScanProgressAnimation() {
		fl_antivirus.measure(0, 0);// 布局参数随意测量
		int width = fl_antivirus.getMeasuredWidth() / 2;
		//1.创建属性动画集
		mAsClose = new AnimatorSet();
		//2.渐变动画
		ObjectAnimator  leftAlpha = ObjectAnimator.ofFloat(iv_leftImage, "alpha",1.0f,0f);
		ObjectAnimator  rightAlpha = ObjectAnimator.ofFloat(iv_rightImage, "alpha",1.0f,0f);
		//3.平移动画
		ObjectAnimator  leftTranslation = ObjectAnimator.ofFloat(iv_leftImage, "translationX", 0, -width);
		ObjectAnimator  rightTranslation = ObjectAnimator.ofFloat(iv_rightImage, "translationX", 0, width);
		//4.扫描完成的渐变动画
		ObjectAnimator  scanFinishAlpha = ObjectAnimator.ofFloat(ap_scanFinish, "alpha",0f,1.0f);
		ObjectAnimator  scanButtonAlpha = ObjectAnimator.ofFloat(bt_scanAgain, "alpha",0f,1.0f);
		
		mAsClose.playTogether(leftAlpha,rightAlpha,leftTranslation,rightTranslation,scanFinishAlpha,scanButtonAlpha);
		mAsClose.setDuration(2000);
	}
	
	//初始化打开扫描进度的动画
	private void initOpenScanProgressAnimation() {
		fl_antivirus.measure(0, 0);// 布局参数随意测量
		int width = fl_antivirus.getMeasuredWidth() / 2;
		//1.创建属性动画集
		mAsOpen = new AnimatorSet();
		//2.渐变动画
		ObjectAnimator  leftAlpha = ObjectAnimator.ofFloat(iv_leftImage, "alpha",0f,1.0f);
		ObjectAnimator  rightAlpha = ObjectAnimator.ofFloat(iv_rightImage, "alpha",0f,1.0f);
		//3.平移动画
		ObjectAnimator  leftTranslation = ObjectAnimator.ofFloat(iv_leftImage, "translationX", -width ,0);
		ObjectAnimator  rightTranslation = ObjectAnimator.ofFloat(iv_rightImage, "translationX",width, 0 );
		//4.扫描完成的渐变动画
		ObjectAnimator  scanFinishAlpha = ObjectAnimator.ofFloat(ap_scanFinish, "alpha",1.0f,0f);
		ObjectAnimator  scanButtonAlpha = ObjectAnimator.ofFloat(bt_scanAgain, "alpha",1.0f,0f);
		
		mAsOpen.playTogether(leftAlpha,rightAlpha,leftTranslation,rightTranslation,scanFinishAlpha,scanButtonAlpha);
		mAsOpen.setDuration(2000);
		
		//打开扫描进度动画的监听事件 
		mAsOpen.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				//动画结束开始扫描
				initStartScan();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
	}
	
	private Bitmap getLeftImage(Bitmap progressImage) {
		//创建空白的画纸  宽 高 
		int width = progressImage.getWidth() / 2;
		int height = progressImage.getHeight();
		Bitmap leftImage = Bitmap.createBitmap(width,height, progressImage.getConfig());
		//创建画板
		Canvas canvas = new Canvas(leftImage); //把画纸放到画板上
		
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		canvas.drawBitmap(progressImage, matrix, paint);
		
		return leftImage;
	}
	
	private Bitmap getRightImage(Bitmap progressImage) {
		//创建空白的画纸  宽 高 
		int width = progressImage.getWidth() / 2;
		int height = progressImage.getHeight();
		Bitmap rightImage = Bitmap.createBitmap(width,height, progressImage.getConfig());
		//创建画板
		Canvas canvas = new Canvas(rightImage); //把画纸放到画板上
		
		Matrix matrix = new Matrix();
		matrix.setTranslate(-width, 0);// 画原图的右半部分
		Paint paint = new Paint();
		canvas.drawBitmap(progressImage, matrix, paint);
		
		return rightImage;
	};
	
	private void initStartScan() {
		new Thread(){
			@Override
			public void run() {
				//1.发送开始扫描消息
				mHandler.obtainMessage(STARTSCAN).sendToTarget();
				
				//2.获取所有安装的app 并扫描是否有病毒
				int progress = 0; //记录扫描进度
				allInstalledAppInfo = AppInfoUtils.getAllInstalledAppInfo(mContext);
				for (AppInfoBean appInfoBean : allInstalledAppInfo) {
					if(interruptScan){//如果终止扫描为true 则终止方法
						return;
					}
					progress++;
					//获得app目录
					String sourceDir = appInfoBean.getSourceDir(); 
					//获得app包的md5值
					String md5 = MD5Utils.getFileMd5(sourceDir);
					//判断是否是病毒
					boolean isVirus = VirusDao.isVirus(md5);
					
					//封装软件扫描结果  图标 名字  是否病毒 进度
					ScanAppInfo scanAppInfo = new ScanAppInfo();
					scanAppInfo.icon = appInfoBean.getIcon();
					scanAppInfo.name = appInfoBean.getAppName();
					scanAppInfo.isVirus = isVirus;
					scanAppInfo.maxProgress = allInstalledAppInfo.size();
					scanAppInfo.currentProgress = progress;
					
					//发送app扫描结果对象给handler做界面更新
					Message msg = mHandler.obtainMessage(SCANING);//发送正在扫描的消息
					msg.obj = scanAppInfo;
					mHandler.sendMessage(msg);//发送扫描结果
					
					SystemClock.sleep(100);
				}
				
				//3.发送扫描完成消息
				mHandler.obtainMessage(SCANFINISH).sendToTarget();
			};
		}.start();
	}

	private class ScanAppInfo{
		Drawable icon;
		String name;
		boolean isVirus;
		int maxProgress;
		int currentProgress;
	}
	
	private void initView() {
		setContentView(R.layout.activity_antivirus);
		mContext = this;
		ap_scanFinish = (ArcProgress) findViewById(R.id.ap_antivirus_scanfinish);
		ap_scanProgress = (ArcProgress) findViewById(R.id.ap_antivirus_scanProgress);
		ll_scanCompleted = (LinearLayout) findViewById(R.id.ll_antivirus_scancompleted);
		bt_scanAgain = (Button) findViewById(R.id.bt_antivirus_scanAgain);
		tv_scanDesc = (TextView) findViewById(R.id.tv_antivirus_scandesc);
		ll_animation = (LinearLayout) findViewById(R.id.ll_antivirus_animation);
		iv_leftImage = (ImageView) findViewById(R.id.iv_antivirus_leftImage);
		iv_rightImage = (ImageView) findViewById(R.id.iv_antivirus_rightImage);
		fl_antivirus = (FrameLayout) findViewById(R.id.fl_antivirus);
		bt_finish = (Button)findViewById(R.id.bt_antivirus_finish);
		antivirus_sv = (ScrollView) ll_scanCompleted.getParent();
	}
}
