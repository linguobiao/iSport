package com.cn.zhihengchuang.walkbank.view;

import com.cn.zhihengchuang.walkbank.numberpi.Scroller;
import android.content.Context;
import android.view.animation.Interpolator;
public class ViewPagerScroller extends Scroller {
	private int mScrollDuration = 25000;// 滑动速度

	public ViewPagerScroller(Context context) {
		super(context);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public ViewPagerScroller(Context context, Interpolator interpolator,
			boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, mScrollDuration);
	}
}
