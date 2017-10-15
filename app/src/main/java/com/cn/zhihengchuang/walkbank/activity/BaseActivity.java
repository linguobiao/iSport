package com.cn.zhihengchuang.walkbank.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class BaseActivity extends FragmentActivity {
   

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        long beginTime = System.currentTimeMillis();
//        BusProvider.getInstance().register(this);
        Log.e("wuzhenlin", "注册耗时" + (System.currentTimeMillis() - beginTime));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        long beginTime = System.currentTimeMillis();
//        BusProvider.getInstance().unregister(this);
        Log.e("wuzhenlin", "注销耗时" + (System.currentTimeMillis() - beginTime));
    }

   
    
    // 更新回调接口
    public interface UpdateCallBack {
    	public void onFail();
    	public void onSuccess();
    }
    
   

    public void onResultReceive(int requestCode, int resultCode, Bundle resultData) {

    }
}
