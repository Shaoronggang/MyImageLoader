package com.detao.myimageloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.detao.myimageloader.activity.WebPicturesActivity;
import com.detao.myimageloader.adapter.ImageAdapter;
import com.detao.myimageloader.bean.WebLink;
import com.detao.myimageloader.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {   //功能清单文件中，如果只是Application 的主题，而给活动不添加其他的主题，app的主题就可以了，但是如果使用了AppcompatActivity的则需要再添加一个主题

    private TextView tv_main_info;
    private GridView gv_main;
    private List<WebLink> data;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_main = (GridView) findViewById(R.id.gv_main);
        initData();

        adapter = new ImageAdapter(this,data);
        gv_main.setAdapter(adapter);
        gv_main.setOnItemClickListener(this);
    }

    private void initData() {
        data = new ArrayList<WebLink>();
        data.add(new WebLink("图片天堂", R.drawable.i1, "www.ivsky.com/"));
        data.add(new WebLink("硅谷教育", R.drawable.i2, "www.atguigu.com/"));
        data.add(new WebLink("新闻图库", R.drawable.i3, "www.cnsphoto.com/"));
        data.add(new WebLink("MOKO美空", R.drawable.i4,"www.moko.cc/"));
        data.add(new WebLink("114啦", R.drawable.i5, "www.114la.com/mm/index.htm/"));
        data.add(new WebLink("动漫之家", R.drawable.i6, "www.donghua.dmzj.com/"));
        data.add(new WebLink("7k7k", R.drawable.i7, "www.7k7k.com/"));
        data.add(new WebLink("嘻嘻哈哈", R.drawable.i8, "www.xxhh.com/"));
        data.add(new WebLink("有意思吧", R.drawable.i9, "www.u148.net/"));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,WebPicturesActivity.class);
        intent.putExtra("url",data.get(i).url); //将相应的对象中的url传入到点击的相应的item中
        startActivity(intent);
    }

    //2s内两次按回退键, 退出应用, 并清理内存缓存
    private long firstTime ; //第一次按下back键的时间

    /**
     * 点击退出键的相关的功能
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    if(keyCode == KeyEvent.KEYCODE_BACK) {
//         long currentTime = System.currentTimeMillis();
//        if(currentTime - firstTime > 2000) { //当前是第一次
//            //提示
//            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
//            //记录时间
//            firstTime = currentTime;
//            return true;
//        }else {
//            //清楚缓存(内存)
//            x.image().clearMemCache();
//            //清楚缓存(文件)
////            x.image().clearCacheFiles();
//        }

        //退出时弹出一个相应的对话框
        new AlertDialog.Builder(this)
                .setMessage("你确认退出网络抓图吗？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    finish();
                    }
                })
                .setNeutralButton("最小化", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppUtils.showToast("暂时没有开启最小化功能");
                    }
                })
                .setNegativeButton("取消",null)
//                .setCancelable(false)  //设置点击可取消
                .show();
    }
        return false;
    }

}
