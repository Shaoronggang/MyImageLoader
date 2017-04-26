package com.detao.myimageloader.bean;

/**
 * Created by shaoronggang on 2017/2/15.
 * 推荐网站的实体类
 */

public class WebLink {
        public String name;//网站名
        public int image; //图片标识
        public String url; //网址

    public WebLink(String name,int image,  String url) {
        this.image = image;
        this.name = name;
        this.url = url;
    }
}
