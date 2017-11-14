package com.robin.bean;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robin.backtracking.R;

public class Topbar extends RelativeLayout{
	
	private String text,leftText,rightText;
	private int textColor,leftColor;
	private int righColor;
	private int textsize , rightTextsize;
	float	leftTextsize;
	private Drawable rightDraw;
	private Drawable leftDraw;
	
	private TextView title;
	private TextView leftTextView,rightTextView;
	
	public topbarClickListen clickListen;
	
	
	
	private LayoutParams titeParams,leftParams,rightParams;

	public Topbar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	public Topbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub.
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
		text = ta.getString(R.styleable.Topbar_maintitle);
		leftText = ta.getString(R.styleable.Topbar_lefttext);
		rightText = ta.getString(R.styleable.Topbar_righttext);
		textColor = ta.getColor(R.styleable.Topbar_titlemainTextColor, 0);
		leftColor = ta.getColor(R.styleable.Topbar_leftTextColor, 0);
		righColor = ta.getColor(R.styleable.Topbar_rightTextColor, 0);
		textsize = ta.getDimensionPixelSize(R.styleable.Topbar_titleTextSize, 18);
		leftDraw = ta.getDrawable(R.styleable.Topbar_leftBackgroud);
		rightDraw = ta.getDrawable(R.styleable.Topbar_rightBackgroud);
		
		
		leftTextsize =  ta.getDimensionPixelSize(R.styleable.Topbar_leftTextSize, 18);
		rightTextsize =  ta.getDimensionPixelSize(R.styleable.Topbar_rightTextSize, 16);
		ta.recycle();
		
		title = new TextView(context);
		title.setText(text);
		title.setTextColor(textColor);
		title.setTextSize(textsize);
		
		titeParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		titeParams.addRule(RelativeLayout.CENTER_HORIZONTAL,TRUE);
		
		leftTextView = new TextView(context);
		leftTextView.setClickable(true);
		leftTextView.setText(leftText);
		leftTextView.setTextColor(leftColor);
		leftTextView.setTextSize(leftTextsize);
		leftTextView.setPadding(5, 5, 5, 5);
		leftTextView.setCompoundDrawablesWithIntrinsicBounds(leftDraw, null, null, null);
		leftParams =  new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,TRUE);
		
		
		rightTextView = new TextView(context);
		rightTextView.setText(rightText);
		rightTextView.setTextColor(righColor);
		rightTextView.setBackground(rightDraw);
		rightTextView.setTextSize(rightTextsize);
		rightTextView.setPadding(5, 5, 5, 5);
		rightParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
		
		addView(leftTextView, leftParams);
		addView(title, titeParams);
		addView(rightTextView, rightParams);
		
		leftTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickListen.onleftClick();
			}
		});
		
		rightTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickListen.onrightClick();
			}
		});
		
	}
	
	public interface topbarClickListen{
		public void onleftClick();
		public void onrightClick();
	}
	
	/*
	 * 设置标题栏 字体
	 * param 0 : title 的字体
	 * param 1 : 右边标题的字体
	 * */
	public void setTitle(String titletext, String righttext){
		title.setText(titletext);
		rightTextView.setText(righttext);
	}
	
	

	
	/**    
	 * @创建者   ：Robin   
	 * @创建时间 ：2015-11-26 上午9:25:29  
	 * @方法说明 ： 设置标题栏的可见   
	 * param1 : 设置标题栏左边
	 * param2 : 设置标题栏右边
	 */
	public void setvisibility(boolean left, boolean right) {
		// TODO Auto-generated method stub
		if(!left){
			leftTextView.setVisibility(View.INVISIBLE);
		}
		
		if(!right){
			rightTextView.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setLeftvisibility(boolean left) {
		// TODO Auto-generated method stub
		setvisibility(left, true);
	}
	
	/**    
	 * @创建者   ：Robin   
	 * @创建时间 ：2015-11-25 下午3:08:34  
	 * @方法说明 ： 设置标题栏的背景   
	 */
	@SuppressLint("NewApi")
	public void setbarBackgroup(int membersBtnAdd) {
		// TODO Auto-generated method stub
		rightTextView.setBackgroundResource(membersBtnAdd);
	}
	
	public void setTopbarClickListen(topbarClickListen listen){
		clickListen = listen;
	}

}
