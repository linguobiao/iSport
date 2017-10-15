package com.cn.zhihengchuang.walkbank.dialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.util.Utils;
import com.cn.zhihengchuang.walkbank.view.TosAdapterView;
import com.cn.zhihengchuang.walkbank.view.TosGallery;
import com.cn.zhihengchuang.walkbank.view.WheelView;

public class DialogSetTargetActivity extends Activity {
	private Button mPickerok = null;
	private Button mPickeresc = null;
	int[] mData ;
	int src = 0;
	WheelView np = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light2);
		mData=new int[300];
		for(int i=1;i<=300;i++){
			mData[i-1]=i*100;
		}
		int divierId = this.getResources().getIdentifier("android:id/titleDivider", null, null);  
	    View divider = findViewById(divierId);  
	    divider.setBackgroundColor(Color.parseColor("#754c50")); 
		mPickerok = (Button) findViewById(R.id.numberPickerok);
		mPickeresc = (Button) findViewById(R.id.numberPickercanle);
		np = (WheelView) findViewById(R.id.numberPicker);
		np.setScrollCycle(true);
		np.setAdapter(new NumberAdapter());
		int age = getIntent().getIntExtra("age", 7000);
		for(int i=0;i<mData.length;i++){
			if(mData[i]==age){
				np.setSelection(i);
			}
		}
		np.setOnItemSelectedListener(mListener);
		
		/*np.setMaxValue(10);
		np.setMinValue(5);
		np.setValue(age);
		np.setFocusable(true);
		np.setFocusableInTouchMode(true);
		np.setMinimumWidth(100);
		np.setLable("  " + getResources().getString(R.string.complete_user_info_default_day_target_step));*/
		mPickerok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.putExtra("age", "" + src);
				setResult(200, intent);
				// 关闭掉这个Activity
				finish();

			}
		});
		mPickeresc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(50, intent); // 50 为空
				// 关闭掉这个Activity
				finish();
			}
		});
	}
	 private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
	           // formatData();
	        	 int pos1 = np.getSelectedItemPosition();
	        	 src =mData[pos1];
	        }

	        @Override
	        public void onNothingSelected(TosAdapterView<?> parent) {
	        }
	    };
	 /**
     * 瀵嗙爜鏍廰dapter
     * @author zr
     *
     */
    private class NumberAdapter extends BaseAdapter {
        int mHeight = 50;

        public NumberAdapter() {
            mHeight = (int) Utils.pixelToDp(DialogSetTargetActivity.this, mHeight);
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtView = null;

            if (null == convertView) {
                convertView = new TextView(DialogSetTargetActivity.this);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));

                txtView = (TextView) convertView;
                txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                txtView.setTextColor(Color.BLACK);
                txtView.setGravity(Gravity.CENTER);
            }

            String text = String.valueOf(mData[position]);
            if (null == txtView) {
                txtView = (TextView) convertView;
            }

            txtView.setText(text);

            return convertView;
        }
    }

}
