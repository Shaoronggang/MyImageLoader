package com.detao.myimageloader.util;

/**
 * Created by shaoronggang on 2017/2/16.
 */

public class Constants {
    public static  final String SAVE_DIR = AppUtils.getSDCardPath() + "/mypics"; //图片保存路径

    public static final int S_WEB = 0; //查看网络图片
    public static final int S_SDCARD = 1; //查看下载好的本地图片
    public static int state = Constants.S_WEB; //当前状态

}
