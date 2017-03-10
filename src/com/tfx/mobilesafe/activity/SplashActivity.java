package com.tfx.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

public class SplashActivity extends Activity {

	private static final int LOADMAIN = 1 ;

	private static final int NEWVERSION = 2;
	
	private TextView splash_vesion_tv;
	private PackageInfo mPi;
	private Context context;
	private VersionInfo mVersionInfo;
	private int mVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		context = this;
		
		//界面    
		initView();
		
		//数据
		initData();
		
		//自动更新
		autoUpdate();
		
	}

	/**
	 * 复制文件 从assert目录复制到data目录
	 * @param dbFileName assert目录下的文件名
	 * @throws IOException
	 */
	private void copyDB(String dbFileName) throws IOException{
		File filesDir = getFilesDir(); // data/data/files
		File file = new File(filesDir,dbFileName);
		if(file.exists()){ //文件已经存在
			return;
		}
		//输出流
		FileOutputStream fos = new FileOutputStream(file);
		//获得资产管理者 打开文件 输入流
		AssetManager assets = getAssets();
		InputStream inputStream = assets.open(dbFileName); 
		
		//文件复制
		byte [] buffer =new byte[1024*5]; //定义字节数组
		int len; 
		while((len = inputStream.read(buffer))!= -1){ //把文件读到len里  每次读buffer大小  !=-1有文件
			fos.write(buffer, 0, len); //一次写buffer大小,从0开始写,
			fos.flush(); //
		}
		//关闭流
		inputStream.close();
		fos.close();
	}
	
	private void autoUpdate() {
		//判断进入版本更新还是进入HomeActivity 
		if(SPUtils.getBoolean(context, MyConstants.AUTO_UPDATE, false)){
			//是自动更新  检测版本更新
			checkVersion();
		} else {
			// 进入HomeActivity
			new Thread() {
				public void run() {
					SystemClock.sleep(2000);
					startHome();
				}
			}.start();
		}
	}

	//跳到homeActivity
	protected void startHome() {
		Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
		startActivity(intent);
		finish();
	}

	//检测版本更新
	protected void checkVersion() {
		//请求网络子线程中执行
		new Thread(){
			public void run() {
				//访问网络url 读取版本数据
				readUrlData();
			};
		}.start();
	}

	protected void readUrlData() {
		Message msg = mHandler.obtainMessage(); //handler消息
		long startTime = System.currentTimeMillis(); //开始请求url的时间
		//url http://192.168.1.103:8080/mobilesafe_version.json
		//数据格式json   {"versionname":"冬瓜版","versioncode":"3","desc":"增加了手机防盗,添加异性好友等","downloadurl":"http://192.168.1.103:8080/xx.apk"}
		try {
			//1.创建url
			URL url = new URL(getResources().getString(R.string.versionurl));
			//2.连接
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			//3.设置属性
			con.setConnectTimeout(2000);
			con.setRequestMethod("GET");
			//4.判断请求是否成功
			int code = con.getResponseCode();
			if(code == 200){
				//请求成功 把请求到的流数据转换成json数据(String) 
				String json = stream2json(con.getInputStream());
				//解析json
				mVersionInfo = parseJson(json);
				if(mVersionCode == mVersionInfo.versioncode){ // 清单文件配置的版本号等于服务器json数据版本号
					//版本一致 不更新  更新ui 进入主界面 要在主线程更新
					msg.what = LOADMAIN ; //消息的标记 一个常量
				}else{
					//有新版本 提醒用户是否更新
					//是 下载--提醒用户是否更新
					//否 进入主界面
					
					//显示是否更新的对话框
					msg.what = NEWVERSION ;
				}
			}else{
				//请求不成功 非200  如404 500
				msg.what = code;
			}
		} catch (MalformedURLException e) {
			//url错误
			msg.what = 10087; //URLException
			e.printStackTrace();
		} catch (NotFoundException e) {
			//找不到url
			msg.what = 10088; //NotFoundException
			e.printStackTrace();
		} catch (IOException e) {
			// io异常
			msg.what = 10089; //IOException
			e.printStackTrace();
		} catch (JSONException e) {
			// json异常
			msg.what = 10090; //JSONException
			e.printStackTrace();
		} finally { //怎样都会处理
			//延时处理
			long endTime = System.currentTimeMillis(); //请求完url的时间
			if(endTime - startTime < 1000){ //如果请求时间小于2秒 sleep一下 让界面停留两秒
				SystemClock.sleep(1000 - (endTime - startTime));
			}
			//统一发消息
			mHandler.sendMessage(msg);
		}
	}
	
	/**
	 * 是否下载的对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//		dialog.setCancelable(false); 设置对话框不可取消 只能点击按钮
		dialog.setTitle("提醒");
		dialog.setMessage("有新版本是否下载:\n新版本功能: "+mVersionInfo.desc);
		dialog.setPositiveButton("下载", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载apk
				downloadNewApk();
			}
		});
		dialog.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//主界面
				startHome(); 
			} 
		});
		
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// 取消对话框(不点击任何一个按钮)
				startHome();
			}
		});
		//记得展示对话框 
		dialog.show();  
	} 

	//使用xutils框架进行下载
	protected void downloadNewApk() {
		//判断sd卡是否被挂载
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
			HttpUtils httpUtils = new HttpUtils();
			//参数一:下载链接     二:存放路径 放在缓存目录    三:下载回调
			final String fielPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MobileSafeCenter.apk";
			httpUtils.download(mVersionInfo.downloadurl, fielPath ,new RequestCallBack<File>() {
				//下载成功
				@Override  
				public void onSuccess(ResponseInfo<File> arg0) {
					//主线程 
					//下载成功  安装程序  
					//参考上层源码 packageinstall  配置清单  得到安装应用的<intent-filter>
					/* <intent-filter>
	                <action android:name="android.intent.action.VIEW" />
	                <category android:name="android.intent.category.DEFAULT" />
	                <data android:scheme="content" />
	                <data android:scheme="file" />
	                <data android:mimeType="application/vnd.android.package-archive" />
	            </intent-filter>*/
					
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setDataAndType(Uri.fromFile(new File(fielPath)), "application/vnd.android.package-archive"); // data和type必须同时设置
					startActivityForResult(intent, 1); // 开启一个带结果码的意图  
					finish();
				} 
				//下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					//主线程
					System.out.println("下载失败"+arg0);
					startHome();
				}
				
				//下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					splash_download_pb.setVisibility(View.VISIBLE);
					splash_download_pb.setMax((int) total);
					splash_download_pb.setProgress((int) current);
					super.onLoading(total, current, isUploading);
				}
			});
		}else{
			Toast.makeText(getApplicationContext(), "sd卡未挂载", Toast.LENGTH_LONG).show();
		}
	}

	//监听打开的activity的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//进入主界面
		if(requestCode == 1){ 
			startHome(); //取消安装  就是执行完了安装界面 就跳到主界面
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//定义handler消息处理 
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//handlerMessage方法做设置ui,主线程中
			switch (msg.what) {
			case LOADMAIN:
				Toast.makeText(getApplicationContext(), "已经是最新版", 1).show();
				startHome(); //进入主界面
				break;
			case NEWVERSION:
				showDownloadDialog(); //新版本显示对话框
				break;
			default:
				//异常
				switch (msg.what) {
				case 404:
					Toast.makeText(getApplicationContext(), "网络资源不存在", 1).show();
					break;
				case 500:
					Toast.makeText(getApplicationContext(), "网络服务器内部错误", 1).show();
					break;
				case 10089:
					Toast.makeText(getApplicationContext(), "没有网络", 1).show();
					break;
				case 10090:
					Toast.makeText(getApplicationContext(), "json格式错误", 1).show();
					break;
				default:
					break;
				}
				startHome(); //还是要进入主界面
				break;
			}
		};
	};

	private ProgressBar splash_download_pb;
	
	//封装版本信息的bean对象
	private class VersionInfo{
		int versioncode;
		String versionname;
		String desc;
		String downloadurl;
	}
	
	//解析json 返回一个封装了版本信息的bean对象
	private VersionInfo parseJson(String json) throws JSONException {
		VersionInfo info = new VersionInfo();
		//解析json
		JSONObject jsonObject = new JSONObject(json);
		
		info.versionname = jsonObject.getString("versionname");
		info.desc = jsonObject.getString("desc");
		info.downloadurl = jsonObject.getString("downloadurl");
		info.versioncode = jsonObject.getInt("versioncode");
		
		return info;
	}

	//流转json(String)
	private String stream2json(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder(); //字符串缓冲区
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); //缓冲流
		String line = reader.readLine();//读一行
		while(line != null){
			//把每行信息拼接一起
			sb.append(line);
			//继续读取
			line = reader.readLine();
		}
		inputStream.close();
		reader.close();
		return sb+"";
	}

	private void initData() {
		//文件的拷贝
		copyFileThread();
		
		//获取清单文件配置的版本名和版本号设置到splash_vesion_tv
		//packageManager 静态数据   activityManager 动态数据,内存的使用
		
		PackageManager mPg = getPackageManager(); //1.获得应用程序包管理者
		try {
			mPi = mPg.getPackageInfo(getPackageName(), 0); //2.获得当前程序包信息,参数二可选写0
			mVersionCode = mPi.versionCode; //3.获得版本名和版本号
			String versionName = mPi.versionName;
			splash_vesion_tv.setText("v "+versionName); //4.设置到splash_vesion_tv
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
	}

	//复制文件的线程  耗时操作需要开启子线程
	private void copyFileThread() {
		new Thread(){
			@Override
			public void run() {
				try {
					copyDB("address.db");
					copyDB("commonnum.db");
					copyDB("antivirus.db");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void initView() {
		setContentView(R.layout.activity_splash); //加载布局
		splash_vesion_tv = (TextView) findViewById(R.id.splash_vesion_tv); //版本信息
		splash_download_pb = (ProgressBar) findViewById(R.id.splash_download_pb);
	}
}
