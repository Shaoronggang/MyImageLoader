package com.atguigu.imageloader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.atguigu.imageloader.R;

import org.xutils.image.ImageOptions;

/**
 * 当前应用的工具类
 * Created by shkstart on 2016/5/6.
 */
public class AppUtils {

    private static Context context;

    public static void initContext(Context context){
        AppUtils.context = context;
    }

    public static ImageOptions smallImageOptions;//缩略图
    public static ImageOptions bigImageOptions;//展开图

    static {
        smallImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER) //等比例放大/缩小到充满长/宽居中显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
//                        .setConfig(Bitmap.Config.RGB_565)//默认行为
                .build();

        bigImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)//等比例缩小到充满长/宽居中显示, 或原样显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    /**
     * 检查联网前的url是否符合规则：前缀是http://
     * @param url
     * @return
     */
    public static String checkUrlPre(String url){
        if(!url.startsWith("http")){
            url = "http://" + url;
        }

        return url;
    }

    public static String getUrlImageName(String url) {//http://www.atguigu.com/courses/abc.png
        String name = "";
        name = url.substring(url.lastIndexOf("/") + 1);

        return name;//abc.png
    }
}
