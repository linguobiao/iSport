package com.lingb.global;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.lingb.helper.SpHelper;

/**
 * Created by LGB on 2017/10/15.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isKeepOn = SpHelper.getBoolean(Global.KEY_KEEP_SCREEN_ON, false);
        if (isKeepOn) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
