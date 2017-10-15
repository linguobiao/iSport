package com.cn.zhihengchuang.walkbank.dialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.Utils;
import com.cn.zhihengchuang.walkbank.view.TosAdapterView;
import com.cn.zhihengchuang.walkbank.view.TosGallery;
import com.cn.zhihengchuang.walkbank.view.WheelView;

public class DialogSetHeight extends Activity {
	private Button mPickerok = null;
	private Button mPickeresc = null;
	String[] mData ;
	String[] mData2 ;
	String src = "120";
	String src2 = "0";
    private int selectPosition;
    private int selectPosition2;
	private WheelView np = null;
	private WheelView np2 = null;
	private SharedPreferences share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light1);
		int divierId = this.getResources().getIdentifier("android:id/titleDivider", null, null);  
	    View divider = findViewById(divierId);  
	    divider.setBackgroundColor(Color.parseColor("#754c50")); 
		mPickerok = (Button) findViewById(R.id.numberPickerok);
		mPickeresc = (Button) findViewById(R.id.numberPickercanle);
		np = (WheelView) findViewById(R.id.numberPicker);
		np2 = (WheelView) findViewById(R.id.numberPicker2);
		
		
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		if(share.getInt("metric", 0)==0){
			
			mData= new String[102];
			String heightstr = getIntent().getStringExtra("height").replace(".", ",");
			String weight[]=heightstr.split(",");
			int weighth=Integer.parseInt(weight[0]);
			int weightm = 0;
			if (weight.length > 1) {
				weightm=Integer.parseInt(weight[1]);
			}
			for(int i=120;i<221;i++){
				if(weighth==i){
					selectPosition=i-120;
				}
				mData[i-120]=i+".";
				
			}
			src =mData[selectPosition];
			mData2= new String[]{"0 cm","1 cm","2 cm","3 cm","4 cm","5 cm","6 cm","7 cm","8 cm","9 cm"};
			selectPosition2=weightm;
			src2 =mData2[selectPosition2];
			
//			int height = getIntent().getIntExtra("height", 170);
			
//			np.setMaxValue(220);
//			np.setMinValue(120);
//			np.setValue(height);
//			np.setLable("  " + getResources().getString(R.string.complete_user_info_default_height_unit));
		}else{
			mData= new String[41];
			String weightstr = getIntent().getStringExtra("height").replace(".", ",");
			String weight[]=weightstr.split(",");
			int weighth=Integer.parseInt(weight[0]);
			int weightm = 0;
			if (weight.length > 1) {
				weightm=Integer.parseInt(weight[1]);
			}
			
			for(int i=47;i<88;i++){
				if(weighth==i){
					selectPosition=i-47;
				}
				Log.i("test", "********* i = " + i);
				mData[i-47]=i+".";
			}
			src =mData[selectPosition];
			mData2= new String[]{"0 inch","1 inch","2 inch","3 inch","4 inch","5 inch","6 inch","7 inch","8 inch","9 inch"};
			selectPosition2=weightm;
			src2 =mData2[selectPosition2];
			
//			int height = getIntent().getIntExtra("height", 67);
//			np.setMaxValue(87);
//			np.setMinValue(47);
//			np.setValue(height);
//			np.setLable("  " + "inch");
		}
		np.setScrollCycle(true);
		np.setAdapter(new NumberAdapter());
		np.setSelection(selectPosition);
		np.setOnItemSelectedListener(mListener);
		np2.setScrollCycle(true);
		np2.setAdapter(new NumberAdapter2());
		np2.setSelection(selectPosition2);
		np2.setOnItemSelectedListener(mListener1);
//		np.setFocusable(true);
//		np.setFocusableInTouchMode(true);
//		np.setMinimumWidth(100);
		
		mPickerok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.putExtra("height",  src+src2.replace(" cm", "").replace(" inch", ""));
				
				setResult(202, intent);
				// 关闭掉这个Activity
				finish();
				
//				src = np.getValue();
//				Intent intent = new Intent();
//				intent.putExtra("height", "" + src);
//				setResult(202, intent);
//				// 关闭掉这个Activity
//				finish();

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
    private TosAdapterView.OnItemSelectedListener mListener1 = new TosAdapterView.OnItemSelectedListener() {
    	@Override
    	public void onItemSelected(TosAdapterView<?> parent, View view, int position, long id) {
    		// formatData();
    		int pos1 = np2.getSelectedItemPosition();
    		src2 =mData2[pos1];
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
            mHeight = (int) Utils.pixelToDp(DialogSetHeight.this, mHeight);
        }

        @Override
        public int getCount() {
        	Log.i("test", "count = " + (mData.length));
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
                convertView = new TextView(DialogSetHeight.this);
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
    /**
     * 瀵嗙爜鏍廰dapter
     * @author zr
     *
     */
    private class NumberAdapter2 extends BaseAdapter {
        int mHeight = 50;

        public NumberAdapter2() {
            mHeight = (int) Utils.pixelToDp(DialogSetHeight.this, mHeight);
        }

        @Override
        public int getCount() {
            return (null != mData2) ? mData2.length : 0;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtView = null;

            if (null == convertView) {
                convertView = new TextView(DialogSetHeight.this);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));

                txtView = (TextView) convertView;
                txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                txtView.setTextColor(Color.BLACK);
                txtView.setGravity(Gravity.CENTER);
            }

            String text = String.valueOf(mData2[position]);
            if (null == txtView) {
                txtView = (TextView) convertView;
            }

            txtView.setText(text);

            return convertView;
        }
    }
    
}