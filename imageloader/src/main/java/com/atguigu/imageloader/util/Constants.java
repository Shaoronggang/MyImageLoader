package com.atguigu.imageloader.util;

import android.os.Environment;

/**
 * 当前应用中使用到的常量类
 * Created by shkstart on 2016/5/9.
 */
public class Constants {
    public static final int STATE_WEB = 0;//联网状态
    public static final int STATE_SD = 1;//本地状态

    public static int state = STATE_WEB;//当前的状态

    public static final String SAVE_DIR = Environment.getExternalStorageDirectory() + "/imageloader";
}
