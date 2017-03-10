package com.tfx.mobilesafe.view;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MyToast implements OnTouchListener{
	private WindowManager mWM;
	private LayoutParams mParams;
	private View mView;
	private Context mContext;
	private TextView tv_location;
	private float downX;
	private float downY;
	
	public MyToast(Context context){
		mContext = context;
		//1.窗口管理者
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		// 2.参数 params 设置属性   把toast原始动画去除 
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; //设置type为优先于phone
		mParams.setTitle("Toast");
		
		//设置Gravity  设置坐标的原点
		mParams.gravity = Gravity.LEFT | Gravity.TOP ; //原点为左上角  此时该点坐标为0,0 往下y变大  往右x变大
		mParams.x = SPUtils.getInt(mContext, MyConstants.TOASTX, 0);
		mParams.y = SPUtils.getInt(mContext, MyConstants.TOASTY, 0);
		// 3.view
	}
	
	public void show(String location){
		//每次show时获取view 动态赋值  因为removeView时view是空的
		mView = View.inflate(mContext, R.layout.view_mytoast, null);
		
		//设置背景主题为选中保存到sp中的
		int index = SPUtils.getInt(mContext, MyConstants.LOCATIONSELECTEDINDEX,0);
		mView.setBackgroundResource(ShowLocationStyleDialog.bgColors[index]);
		
		tv_location = (TextView) mView.findViewById(R.id.tv_mytoast);
		tv_location.setText(location);
		mView.setOnTouchListener(this);
		mWM.addView(mView, mParams);
	}
	
	//设置背景方法
	public void setBackground(int index){
		
	}
	
	public void hide(){
		//如果有view在显示,先要将其删除
		 if (mView != null) {
			//防止mView赋值了 但是没有mWM.addView(mView, mParams);
             if (mView.getParent() != null) { 
                 mWM.removeView(mView);
             }
             mView = null;
         }
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// 触摸事件
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://手按下
			//获得按下的坐标
			downX = event.getRawX();
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE://移动
			//移动后的坐标
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			//移动的长度
			float dx = moveX - downX;
			float dy = moveY - downY;
			
			//改变toast的参数  坐标  让toast跟着手走
			mParams.x += dx ;
			mParams.y += dy ;
			
			//判断是否越界
			if(mParams.x < 0){//左边越界
				mParams.x = 0;
			}else if(mParams.x > mWM.getDefaultDisplay().getWidth()){//右边越界
				mParams.x = mWM.getDefaultDisplay().getWidth() - mView.getWidth(); //记得要减去toast本身宽度
			}
			if(mParams.y < 0){//左边越界
				mParams.y = 0;
			}else if(mParams.y > mWM.getDefaultDisplay().getHeight()){//右边越界
				mParams.y = mWM.getDefaultDisplay().getHeight() - mView.getHeight(); //记得要减去toast本身宽度
			}
			//改变view的位置 
			//改变了参数要调用该方法改变view的位置
			mWM.updateViewLayout(mView, mParams);
			
			//设置移动后的坐标为新的起始位置
			downX = moveX ;
			downY = moveY ;
			
			break;
		case MotionEvent.ACTION_UP://手抬起
			//保存位置 下次直接显示保存的位置
			SPUtils.putInt(mContext, MyConstants.TOASTX, mParams.x);
			SPUtils.putInt(mContext, MyConstants.TOASTY, mParams.y);
			break;
		default:
			break;
		}
		return true;
	}
}
