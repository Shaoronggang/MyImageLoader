package com.detao.myimageloader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.detao.myimageloader.R;
import com.detao.myimageloader.bean.ImageBean;
import com.detao.myimageloader.fragment.ImgDetailFragment;
import com.detao.myimageloader.util.AppUtils;
import com.detao.myimageloader.util.Constants;

import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shaoronggang on 2017/2/17.
 */
public class DragImageActivity extends FragmentActivity {

    private TextView tv_drag_name, tv_drag_pageno;
    private ViewPager vp_drag_images;
    private ImageView iv_drag_download, iv_drag_share;
    private int position;//当前显示的图片的下标
    private ArrayList<ImageBean> imageBeans; //所有可以显示的图片信息的对象的集合
    private PageChangeAdapter pageChangeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragimage_activity);
        init();
    }

    private void init() {
        tv_drag_name = (TextView)findViewById(R.id.tv_drag_name);
        tv_drag_pageno = (TextView)findViewById(R.id.tv_drag_pageno);
        vp_drag_images = (ViewPager)findViewById(R.id.vp_drag_images);
        iv_drag_download = (ImageView)findViewById(R.id.iv_drag_download);
        iv_drag_share = (ImageView)findViewById(R.id.iv_drag_share);
        getActionBar().hide();//隐藏ActionBar

        //获取从上个页面获取的数据
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        imageBeans = (ArrayList<ImageBean>) intent.getSerializableExtra("imageBeans");

        tv_drag_name.setText(imageBeans.get(position).getUrl());
        tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());

        if(Constants.state == Constants.S_WEB) {
            iv_drag_download.setImageResource(R.drawable.icon_s_download_press);
            iv_drag_share.setVisibility(View.GONE);
        }else {
            iv_drag_download.setImageResource(R.drawable.garbage_media_cache);
            iv_drag_share.setVisibility(View.VISIBLE);
        }

        pageChangeAdapter = new PageChangeAdapter(getSupportFragmentManager());
        vp_drag_images.setAdapter(pageChangeAdapter);

        vp_drag_images.addOnPageChangeListener(onPageChangeListener);
        vp_drag_images.setCurrentItem(position);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当翻到一个新的页面的时回调
          * @param position
         */
        @Override
        public void onPageSelected(int position) {
            updateInfo(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 更新指定下标及对应的信息
     * @param position
     */
    private void updateInfo(int position) {
    this.position = position;
    tv_drag_name.setText(imageBeans.get(position).getUrl());
    tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());
    }


    /**
     * FragmentStatePagerAdapter : 会适时的销毁Frament及其视图, 适合于多item
     *  FragmentPagerAdapter : 不会销毁创建的Frament及其视图, 适合少个数item
     */
    class PageChangeAdapter extends FragmentStatePagerAdapter {

        public PageChangeAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return  ImgDetailFragment.newInstance(imageBeans.get(position).getUrl());
        }

        @Override
        public int getCount() {
            return imageBeans.size();
        }
    }

    /**
     * 设置壁纸
     * @param view
     */
    public void setWallpaper(View view) {
        if(Constants.state == Constants.S_WEB) {
            //下载网络图片
          downloadImage(imageBeans.get(position).getUrl());
        } else {
            //得到对应的图片对象
            Bitmap bitmap = BitmapFactory.decodeFile(imageBeans.get(position).getUrl());
            try {
                setWallpaper(bitmap);
                AppUtils.showToast("壁纸设置成功");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DragImageActivity.this, "壁纸设置失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 下载图片
     * @param url
     */
    private void downloadImage(String url) {
        //下载文件夹
        File fdir = new File(Constants.SAVE_DIR);
        //创建文件夹
        if(!fdir.exists()) {
            fdir.mkdirs();
        }
        //文件的完整路径
        final String filePath = Constants.SAVE_DIR + "/" + System.currentTimeMillis()  + AppUtils.cutImagePath(url);
        //创建指定图片文件路径的请求参数对象
        RequestParams requestParams = new RequestParams(url);
        //发送对图片的get请求
        x.http().get(requestParams, new Callback.CacheCallback<File>() {
          @Override
            public boolean onCache(File result) {
                AppUtils.showToast("图片下载成功地址:" + result.getAbsolutePath());
                FileUtil.copy(result.getAbsolutePath(),filePath);
                return true;//信任此缓存, 不会尝试去发请求
            }

            @Override
            public void onSuccess(File result) {
                AppUtils.showToast("图片下载成功地址" + result.getAbsolutePath());
                FileUtil.copy(result.getAbsolutePath(),filePath);
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
        });
    }

    /**
     * 分享图片
     * @param view
     */
    public void shareImage(View view){
        File file = new File(imageBeans.get(position).getUrl());
        if(file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),"image/*");
            startActivity(intent);
        }
    }
}
