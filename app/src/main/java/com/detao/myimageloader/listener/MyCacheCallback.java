package com.detao.myimageloader.listener;

import org.xutils.common.Callback;

/**
 * Created by shkstart on 2016/5/7.
 *
 *
 * 当访问url地址，得到html代码字符串时，可以考虑使用CommonCallback接口，实现数据的响应处理：
 *   onSuccess() / onError()
 *
 * 当访问图片的url地址，得到的是图片资源时，建议使用CacheCallback接口，实现数据的响应处理：
 *   回调方法中，除了有CommonCallback接口中的onSuccess() / onError()以外，还有：
 *   onCache().
 *   1.涉及到图片的一级、二级缓存，将相应的代码声明在此方法。
 *   2.此方法的返回值如果是true:当下次访问已有路径的图片时，不会重新联网，而是从缓存中读取
 *     此方法的返回值如果是false:当下次访问已有路径的图片时，重新联网。
 */
public class MyCacheCallback<RequestType> implements Callback.CacheCallback<RequestType> {
    @Override
    public boolean onCache(RequestType result) {//涉及图片的缓存
        return false;
    }

    @Override
    public void onSuccess(RequestType result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
