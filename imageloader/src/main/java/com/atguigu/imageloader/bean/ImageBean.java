package com.atguigu.imageloader.bean;

import java.io.Serializable;

/**
 * Created by shkstart on 2016/5/7.
 */
public class ImageBean implements Serializable{ //ImageBean实现序列化，方便在intent存储，传输到另外的Activity上
    private String url;//图片的路径
    private boolean checked;//是否被选中

    public ImageBean(String url) {
        this.url = url;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * 回头需要显示的当前网站的图片是不是重复的，需要调用如下的equals()/hashCode()
     * 只需要判断url即可
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageBean imageBean = (ImageBean) o;

        return !(url != null ? !url.equals(imageBean.url) : imageBean.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
