package com.cn.zhihengchuang.walkbank.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.isport.trackernew.R;

public class TasksCompletedView extends View {
	// 画实心圆的画笔
	private Paint mCirclePaint2;
	// 画内圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画圆环背景的画笔
	private Paint mRingPaint2;
	// 圆形颜色
	// private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	private int mAllProgress;
	public static long TIME = 1;
	private int i;

	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius,
				80);
		mStrokeWidth = typeArray.getDimension(
				R.styleable.TasksCompletedView_strokeWidth, 10);
		// mCircleColor =
		// typeArray.getColor(R.styleable.TasksCompletedView_circleColor,
		// 0xFFFFFF);
		mRingColor = typeArray.getColor(
				R.styleable.TasksCompletedView_ringColor, 0xFFFFFF);
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		// mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setColor(mRingColor);
		mCirclePaint.setStyle(Paint.Style.FILL);// STROKE为空心，fill为实心
		mCirclePaint.setStrokeWidth(0);

		mCirclePaint2 = new Paint();
		mCirclePaint2.setAntiAlias(true);
		mCirclePaint2.setColor(Color.parseColor("#ffffff"));
		mCirclePaint2.setStyle(Paint.Style.FILL);// STROKE为空心，fill为实心
		mCirclePaint2.setStrokeWidth(0);

		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		// LinearGradient mShader = new LinearGradient(50, 50, 100, 150, new
		// int[] { Color.GRAY,Color.RED,Color.GRAY, Color.BLUE,Color.YELLOW
		// },null, Shader.TileMode.MIRROR);
		// mRingPaint.setShader(mShader);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);

		mRingPaint2 = new Paint();
		mRingPaint2.setAntiAlias(true);
		mRingPaint2.setColor(Color.parseColor("#eaeaea"));
		mRingPaint2.setStyle(Paint.Style.STROKE);
		mRingPaint2.setStrokeWidth(mStrokeWidth);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		float a = ((float) mProgress / mTotalProgress) * 360;
		if (a >= 360) {
			a = 360;
		}
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		if (mProgress >= 0) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			// canvas.drawCircle(mXCenter, mYCenter, mRingRadius,mRingPaint2);
			int n = 360;
			for (int i = 0; i < n; i += 3) {
				canvas.drawArc(oval, -90 + i, 2, false, mRingPaint2);
			}
			float m = (1.0f * mProgress / mTotalProgress) * 360;
			for (int i = 0; i < m; i += 3) {
				canvas.drawArc(oval, -90 + i, 2, false, mRingPaint);
			}
		}
		/*
		 * float x ,y ; x = (float) (mXCenter +
		 * (mRadius+mStrokeWidth/2)*Math.sin(a*Math.PI/180)); y = (float)
		 * (mYCenter - (mRadius+mStrokeWidth/2)*Math.cos(a*Math.PI/180));
		 * if(a>0) canvas.drawCircle(x, y, mStrokeWidth*1.7f,mCirclePaint);
		 * if(mProgress != 0){ if(mProgress < 10)
		 * mCirclePaint2.setTextSize(16*dp); else if(mProgress < 100)
		 * mCirclePaint2.setTextSize(14*dp); else
		 * mCirclePaint2.setTextSize(12*dp); float n =
		 * mCirclePaint2.measureText(mProgress+"%")/2;
		 * canvas.drawText(mProgress+"%", x-n, y+5*dp, mCirclePaint2); }
		 */
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				if (i <= mAllProgress) {
					handler.postDelayed(this, TIME);
					mProgress = i;
					postInvalidate();
					i++;
				} else {
					i = mAllProgress;
					handler.removeCallbacks(this);
				}
//				System.out.println("do...");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				System.out.println("exception...");
			}
		}
	};

	public void setProgress(int progress) {
		mAllProgress = progress;
		
		handler.postDelayed(runnable, TIME);
	}
}