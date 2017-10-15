package com.cn.zhihengchuang.walkbank.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.androidex.appformwork.bus.BusProvider;

public class BaseFragment extends Fragment  {
    

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

   

}
