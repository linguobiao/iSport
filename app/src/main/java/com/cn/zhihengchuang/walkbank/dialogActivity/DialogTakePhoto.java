package com.cn.zhihengchuang.walkbank.dialogActivity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.isport.trackernew.R;
import com.cn.zhihengchuang.walkbank.util.BitmapUtil;
import com.cn.zhihengchuang.walkbank.util.Constants;
import com.cn.zhihengchuang.walkbank.util.ToastUtil;
import com.cn.zhihengchuang.walkbank.util.Tools;

public class DialogTakePhoto extends Activity {
	private TextView take_photo, browsing;
	private Button cancle;
	private static final int IMAGE_REQUEST_CODE = 1;// 请求码
	private static final int CAMERA_REQUEST_CODE = 2;
	private static final int RESULT_REQUEST_CODE = 3;
	private ToastUtil toast_util;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_profile_photo);
		init();
	}

	private void init() {
		toast_util = new ToastUtil(this);
		take_photo = (TextView) findViewById(R.id.user_info_take_photo);
		browsing = (TextView) findViewById(R.id.user_info_browsing);
		cancle = (Button) findViewById(R.id.user_info_take_photo_cancle);
		take_photo.setOnClickListener(new OnClickListenerImpl());
		browsing.setOnClickListener(new OnClickListenerImpl());
		cancle.setOnClickListener(new OnClickListenerImpl());
	}
	
	private class OnClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.user_info_take_photo:
				// 判断存储卡是否可以用，可用进行存储
				if (Tools.hasSdcard()) {
					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 下面这句指定调用相机拍照后的照片存储的路径
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera",Constants.SAVEUSERIMAGE)));
					startActivityForResult(intentFromCapture,CAMERA_REQUEST_CODE);
					break;
				}
				break;
			case R.id.user_info_browsing:
				Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
				intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
				startActivityForResult(intentFromGallery, 1);
				break;
			case R.id.user_info_take_photo_cancle:
				DialogTakePhoto.this.finish();
				break;
			}	
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (IMAGE_REQUEST_CODE == requestCode) {
			if (data != null) {
				startPhotoZoom(data.getData());
				System.out.println(data.getData()+"data");
			}
		} else if (CAMERA_REQUEST_CODE == requestCode) {
			if (resultCode == 0) {
			} else {
				if (Tools.hasSdcard()) {
					File temp = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera" + Constants.SAVEUSERIMAGE);
					startPhotoZoom(Uri.fromFile(temp));
				} else {
					toast_util.getToast(DialogTakePhoto.this.getResources().getString(R.string.user_info_no_sd_card));
				}
			}
		} else if (RESULT_REQUEST_CODE == requestCode) {
			if (data != null) {
				getImageToView(data);
			}
		}
	}
	
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 206);
		intent.putExtra("outputY", 206);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			BitmapUtil.save(bitmap, getApplicationContext().getFilesDir().getAbsolutePath() + Constants.SAVEUSERIMAGE);
			File file = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera"+Constants.SAVEUSERIMAGE);
			if(file.exists()){
				//file.delete();
			}
			setResult(200, data);
			DialogTakePhoto.this.finish();
		}
	}
	
}