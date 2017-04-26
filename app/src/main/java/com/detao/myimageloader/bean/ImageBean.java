package com.detao.myimageloader.bean;

import java.io.Serializable;

/**
 * Created by shaoronggang on 2017/2/16.
 */

public class ImageBean implements Serializable {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private   String url;
    private   boolean checked; //标识界面的图片是否需要选中

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ImageBean(String url) {
        this.url = url;
    }

    public boolean isChecked(){
        return checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageBean imageBean = (ImageBean) o;

        return url != null ? url.equals(imageBean.url) : imageBean.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
