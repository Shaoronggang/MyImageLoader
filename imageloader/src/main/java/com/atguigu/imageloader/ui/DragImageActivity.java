package com.atguigu.imageloader.ui;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.fragment.ImageDetailFragment;
import com.atguigu.imageloader.listener.MyCacheCallback;
import com.atguigu.imageloader.util.AppUtils;
import com.atguigu.imageloader.util.Constants;

import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragImageActivity extends FragmentActivity {

    private TextView tv_drag_name, tv_drag_pageno;
    private ViewPager vp_drag_images;
    private ImageView iv_drag_download, iv_drag_share;

    private int position;//当前显示的图片的位置
    private List<ImageBean> imageBeans;//发送过来的集合数据

    private PicturesSlidePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image);

        init();
    }

    private void init() {
        //页面中视图对象的初始化
        tv_drag_name = (TextView) findViewById(R.id.tv_drag_name);
        tv_drag_pageno = (TextView) findViewById(R.id.tv_drag_pageno);
        vp_drag_images = (ViewPager) findViewById(R.id.vp_drag_images);
        iv_drag_download = (ImageView) findViewById(R.id.iv_drag_download);
        iv_drag_share = (ImageView) findViewById(R.id.iv_drag_share);


        //1.隐藏actionbar
        getActionBar().hide();

        //2.获取前一个界面发送过来的数据
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        imageBeans = (List<ImageBean>) intent.getSerializableExtra("imageBeans");
        Log.e("TAG", "position = " + position + ",imageBeans = " + imageBeans.toString());

        //3.设置视图对象的显示
        tv_drag_name.setText(imageBeans.get(position).getUrl());
        tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());

        int state = Constants.state;
        if (state == Constants.STATE_WEB) {//联网状态
            iv_drag_download.setImageResource(R.drawable.icon_s_download_press);
            iv_drag_share.setVisibility(View.GONE);

        } else if (state == Constants.STATE_SD) {//本地状态
            iv_drag_download.setImageResource(R.drawable.garbage_media_cache);
            iv_drag_share.setVisibility(View.VISIBLE);
        }

        //4.设置ViewPager的显示
        adapter = new PicturesSlidePagerAdapter(getSupportFragmentManager());
        vp_drag_images.setAdapter(adapter);//

        //5.ViewPager的设置
        vp_drag_images.setCurrentItem(position);//设置当前显示的位置
        vp_drag_images.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DragImageActivity.this.position = position;//更新要显示的imagebean的位置
                tv_drag_name.setText(imageBeans.get(position).getUrl());
                tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 页面上"下载/设置壁纸"的imageView的回调方法
     * @param v
     */
    public void download(View v) {
        int state = Constants.state;
        if(state == Constants.STATE_WEB){//联网状态：下载当前显示的图片

            downloadImage(imageBeans.get(position).getUrl());

        }else if(state == Constants.STATE_SD){//本地状态：设置为壁纸

            Bitmap bitmap = BitmapFactory.decodeFile(imageBeans.get(position).getUrl());

            try {
                setWallpaper(bitmap);
                Toast.makeText(DragImageActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DragImageActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
            }

        }

    }

    /**
     * 下载指定路径的图片
     * @param url
     */
    private void downloadImage(final String url) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.setConnectTimeout(5000);
        x.http().get(requestParams,new MyCacheCallback<File>(){
            @Override
            public boolean onCache(File result) {

                File filesDir = new File(Constants.SAVE_DIR);
                if(!filesDir.exists()){
                    filesDir.mkdirs();//创建指定的文件目录
                }
                String fileName = filesDir.getAbsolutePath() + "/" + System.currentTimeMillis() + AppUtils.getUrlImageName(url);
                //将指定路径的图片下载到本地指定的路径下文件中
                FileUtil.copy(result.getAbsolutePath(),fileName);

                Toast.makeText(DragImageActivity.this, "下载图片成功", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onSuccess(File result) {
                File filesDir = new File(Constants.SAVE_DIR);
                if(!filesDir.exists()){
                    filesDir.mkdirs();//创建指定的文件目录
                }
                String fileName = filesDir.getAbsolutePath() + "/" + System.currentTimeMillis() + AppUtils.getUrlImageName(url);
                //将指定路径的图片下载到本地指定的路径下文件中
                FileUtil.copy(result.getAbsolutePath(),fileName);

                Toast.makeText(DragImageActivity.this, "下载图片成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(DragImageActivity.this, "下载图片失败", Toast.LENGTH_SHORT).show();

            }
        });


    }

    /**
     * 点击分享的ImageView的回调方法
     * @param v
     */
    public void share(View v) {
        File file = new File(imageBeans.get(position).getUrl());
        if(file.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),"image/*");
            startActivity(intent);

        }


    }
    /**
     * FragmentStatePagerAdapter:当要显示的item较多时，会适时的清除已有的Fragment和其视图对象
     * <p/>
     * FragmentPagerAdapter：当要显示的item较少时，不会清除已有的Fragment和其视图对象
     */
    class PicturesSlidePagerAdapter extends FragmentStatePagerAdapter {

        public PicturesSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {//类似于BaseAdapter中的getView();
            return ImageDetailFragment.getInstance(imageBeans.get(position).getUrl());
        }

        @Override
        public int getCount() {
            return imageBeans.size();
        }
    }
}
