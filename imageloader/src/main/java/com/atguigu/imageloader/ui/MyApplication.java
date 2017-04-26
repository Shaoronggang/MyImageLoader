package com.atguigu.imageloader.ui;

import android.app.Application;

import com.atguigu.imageloader.util.AppUtils;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by shkstart on 2016/5/6.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //xutils框架的初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        //初始化AppUtils类中的context属性
        AppUtils.initContext(this);
    }
}
