package com.tfx.mobilesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tfx.mobilesafe.R;
import com.tfx.mobilesafe.utils.MyConstants;
import com.tfx.mobilesafe.utils.SPUtils;

public class ShowLocationStyleDialog extends Dialog {

	public static final  String[] styleNames = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	public static final  int[]  bgColors = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange
			,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	private ListView lv_locationstyle;
	public SettingCenterItem sci_locationstyle;
	
	public ShowLocationStyleDialog(Context context, int theme) {
		super(context, theme);
	}

	//方便调用 一般调用时使用这个构造方法  方便  
	public ShowLocationStyleDialog(Context context,SettingCenterItem sci_locationstyle) {
		//调用ShowLocationStyleDialog(Context context, int theme) 0为默认样式
		this(context,R.style.LocationDialogStyle); 
		
		this.sci_locationstyle = sci_locationstyle;
		
		//设置对话框显示的位置
		//1.获得window 
		Window window = getWindow();
		//2.获取窗口属性
		LayoutParams lp = window.getAttributes();
		//3.设置显示位置 靠下
		lp.gravity = Gravity.BOTTOM;
		//4.把属性设置给window
		window.setAttributes(lp);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initView();
		intEvent();
		super.onCreate(savedInstanceState);
	}

	private void intEvent() {
		//listview点击事件
		lv_locationstyle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//保存点击的位置
				SPUtils.putInt(getContext(), MyConstants.LOCATIONSELECTEDINDEX, position);
				
				//设置文本  当前选中的主题
				int index = SPUtils.getInt(getContext(), MyConstants.LOCATIONSELECTEDINDEX, 0);
				sci_locationstyle.setText("归属地显示风格(" + styleNames[index] + ")");
				//关闭对话框
				dismiss();
				//设置文本  当前所选中的主题
			}
		});
	}

	private class mAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return styleNames.length;
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
			ViewHolder holder = null;
			if(convertView == null){
				//没有缓存 
				//布局转成view
				convertView = View.inflate(getContext(), R.layout.item_locationstyle_lv, null);
				//使用ViewHolder的目的是避免反复findviewbyid
				holder = new ViewHolder();
				holder.iv_bg = (ImageView) convertView.findViewById(R.id.iv_locationstyle);
				holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_locationstyle);
				holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_locationstyle_select);
				convertView.setTag(holder);
			}else{
				//有缓存 取出
				holder = (ViewHolder) convertView.getTag();
			}
			
			//设置颜色和描述信息
			holder.iv_bg.setBackgroundResource(bgColors[position]);
			holder.tv_desc.setText(styleNames[position]);
			
			//获取sp中保存的选中的颜色 
			if(position == SPUtils.getInt(getContext(), MyConstants.LOCATIONSELECTEDINDEX, 0)){
				holder.iv_select.setVisibility(View.VISIBLE);
			}else{
				holder.iv_select.setVisibility(View.GONE);
			}
			
			return convertView;
		}
	}
	
	private static class ViewHolder{
		ImageView iv_bg;
		TextView tv_desc;
		ImageView iv_select;
	}
	
	private void initView() {
		setContentView(R.layout.dialog_locationstyle);
		lv_locationstyle = (ListView) findViewById(R.id.lv_locationstyle_dialog_content);
		mAdapter adapter = new mAdapter(); 
		lv_locationstyle.setAdapter(adapter);
	}
}
