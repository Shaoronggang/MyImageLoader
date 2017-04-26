package com.detao.myimageloader.base;

import android.app.Application;

import com.detao.myimageloader.BuildConfig;
import com.detao.myimageloader.util.AppUtils;

import org.xutils.x;

/**
 * Created by shaoronggang on 2017/2/15.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Xutils
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        //初始化保存context对象
        AppUtils.initContext(this);
    }
}
