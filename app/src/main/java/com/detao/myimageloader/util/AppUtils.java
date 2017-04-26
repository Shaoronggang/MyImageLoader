package com.detao.myimageloader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.detao.myimageloader.R;

import org.xutils.image.ImageOptions;

/**
 * Created by shaoronggang on 2017/2/15.
 */

public class AppUtils {
    public static Context context;
    //位数不同的图片的设置
    public static ImageOptions smallImageOptions;
    public static ImageOptions bigImageOptions;

    public static void initContext(Context context) {
        AppUtils.context = context;
    }

    static { //静态初始化块
        smallImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)//等比例放大缩小到充满长/宽居中
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
//                .setConfig(Bitmap.Config.RGB_565)
                .build();

        bigImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)//等比例缩小到充满长/宽居中显示，或原样显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    /**
     * 检查url
     *
     * @param url
     * @return
     */
    public static String checkPreUrl(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }

    public static void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取SD卡根路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取网址中的图片名称
     *
     * @param url
     * @return
     */
    public static String cutImagePath(String url) {
        String res = "";
        int start = url.lastIndexOf("/") + 1;
        res = url.substring(start);
        return res;
    }
}
