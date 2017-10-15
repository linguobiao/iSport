package com.cn.zhihengchuang.walkbank.dialogActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

public class DialogSetWeight extends Activity {
	private Button mPickerok = null;
	private Button mPickeresc = null;
	String[] mData ;
	String[] mData2 ;
	String src = "30";
	String src2 = "0";
	WheelView np = null;
    private int selectPosition;
    private int selectPosition2;
	private SharedPreferences share;
	private WheelView np2=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light1);
		share = super.getSharedPreferences(Constants.SHARE_FILE_NAME, Activity.MODE_PRIVATE); // 指定操作的文件名
		if(share.getInt("metric", 0)==0){
			mData= new String[120];
			String weightstr = getIntent().getStringExtra("weight").replace(".", ",");
			String weight[]=weightstr.split(",");
			int weighth=Integer.parseInt(weight[0]);
			int weightm= 0;
			if (weight.length > 1) {
				weightm = Integer.parseInt(weight[1]);
			}
			
			for(int i=30;i<150;i++){
				if(weighth==i){
					selectPosition=i-30;
				}
				mData[i-30]=i+".";
				
			}
			src =mData[selectPosition];
			mData2= new String[]{"0 kg","1 kg","2 kg","3 kg","4 kg","5 kg","6 kg","7 kg","8 kg","9 kg"};
			selectPosition2=weightm;
			src2 =mData2[selectPosition2];
			
		}else{
			mData= new String[264];
			String weightstr = getIntent().getStringExtra("weight").replace(".", ",");
			String weight[]=weightstr.split(",");
			int weighth=Integer.parseInt(weight[0]);
			int weightm= 0;
			if (weight.length > 1) {
				weightm = Integer.parseInt(weight[1]);
			}
			for(int i=66;i<330;i++){
				if(weighth==i){
					selectPosition=i-66;
				}
				mData[i-66]=i+".";
			}
			src =mData[selectPosition];
			mData2= new String[]{"0 lb","1 lb","2 lb","3 lb","4 lb","5 lb","6 lb","7 lb","8 lb","9 lb"};
			selectPosition2=weightm;
			src2 =mData2[selectPosition2];
		}
		
		
		int divierId = this.getResources().getIdentifier("android:id/titleDivider", null, null);  
	    View divider = findViewById(divierId);  
	    divider.setBackgroundColor(Color.parseColor("#754c50")); 
		mPickerok = (Button) findViewById(R.id.numberPickerok);
		mPickeresc = (Button) findViewById(R.id.numberPickercanle);
		np = (WheelView) findViewById(R.id.numberPicker);
		np2 = (WheelView) findViewById(R.id.numberPicker2);
		np.setScrollCycle(true);
		np.setAdapter(new NumberAdapter());
		np.setSelection(selectPosition);
		
		np.setOnItemSelectedListener(mListener);
		np2.setScrollCycle(true);
		np2.setAdapter(new NumberAdapter2());
		
		np2.setSelection(selectPosition2);
		
		np2.setOnItemSelectedListener(mListener1);
		
		mPickerok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("weight",  src+src2.replace(" kg", "").replace(" lb", ""));
				
				setResult(203, intent);
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
            mHeight = (int) Utils.pixelToDp(DialogSetWeight.this, mHeight);
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
                convertView = new TextView(DialogSetWeight.this);
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
            mHeight = (int) Utils.pixelToDp(DialogSetWeight.this, mHeight);
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
                convertView = new TextView(DialogSetWeight.this);
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