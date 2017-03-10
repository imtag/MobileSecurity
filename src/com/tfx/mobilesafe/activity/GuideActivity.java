package com.tfx.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

public class GuideActivity extends Activity {

	private ViewPager guide_vp;
	private List<ImageView> mPagerList;
	private Context context;
	private Button guide_center_bt; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//1.初始化view
		initView();
		
		//2.初始化viewpager
		initPage(); 
		
		//3.给viewpaper设置数据适配器
		initData();
		
		//4.设置viewpager的监听
		inintEvent();
		
	}
	
	//5.点击开始体验按钮跳转到splashactivity页面
	public void enterSystem(View v) {
		Intent intent = new Intent(this,SplashActivity.class);
		startActivity(intent);
		finish();
		//点击了按钮  设置是否第一次进入 为false
		SPUtils.putBoolean(context, MyConstants.IS_FIRST_USE, false);
	}
	
	private void inintEvent() {
		guide_vp.setOnPageChangeListener(new OnPageChangeListener() {
			//当页面被选择,调用该方法  
			@Override
			public void onPageSelected(int position) {
				//如果当前页面是最后一页 则显示立即体验按钮 否则按钮不可见
				if(position == mPagerList.size()-1){
					guide_center_bt.setVisibility(View.VISIBLE);
				}else{
					guide_center_bt.setVisibility(View.GONE);
				}
			}
			//当页面滚动 回调该方法
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			//当页面滚动状态改变
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	private void initData() {
		guide_vp.setAdapter(new PagerAdapter() {
			//判断当前的对象是不是视图,因为有可能是片段
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			//设置展示数据的数量 
			@Override
			public int getCount() {
				return mPagerList == null ? 0 : mPagerList.size();
			}
			//销毁position位置视图
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(mPagerList.get(position)); //删除视图
			}
			//实例化position位置视图
			//container:viewgroup(viewpager)   position:当前视图索引	 返回值:当前添加的视图项对象
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(mPagerList.get(position)); //向viewpager添加视图
				return mPagerList.get(position);
			}
		});
	}

	private void initView() {
		setContentView(R.layout.activity_guide);
		context = this;
		guide_vp = (ViewPager) findViewById(R.id.guide_vp);
		guide_center_bt = (Button) findViewById(R.id.guide_center_bt);
	}

	

	//初始化视图页数据
	private void initPage() {
		//2.1 初始化listview集合
		mPagerList = new ArrayList<ImageView>();
		//2.2集合添加imageview
		ImageView imageView1 = new ImageView(context);
		imageView1.setBackgroundResource(R.drawable.guide_1);
		mPagerList.add(imageView1);
		
		ImageView imageView2 = new ImageView(context);
		imageView2.setBackgroundResource(R.drawable.guide_2);
		mPagerList.add(imageView2);
		
		ImageView imageView3 = new ImageView(context);
		imageView3.setBackgroundResource(R.drawable.guide_3);
		mPagerList.add(imageView3);
	}
}
