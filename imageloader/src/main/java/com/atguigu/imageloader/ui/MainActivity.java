package com.atguigu.imageloader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.adapter.MainAdapter;
import com.atguigu.imageloader.bean.WebLink;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView gv_main;
    private MainAdapter adapter;
    private List<WebLink> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gv_main = (GridView) findViewById(R.id.gv_main);
        //初始化集合数据

        initData();
        adapter = new MainAdapter(this,data);
        //显示列表数据
        gv_main.setAdapter(adapter);

        //设置GridView的item的点击事件的监听
        gv_main.setOnItemClickListener(this);
    }

    /**
     * 初始化集合数据
     */
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

    /**
     * 点击item时的回调方法
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this,WebPicturesActivity.class);
        intent.putExtra("url",data.get(position).getUrl());
        startActivity(intent);

    }

    /**
     * 2s内两次按回退键, 退出应用, 并清理内存缓存
     * 方式一：发送延迟消息
     * 方式二：如下：
     */
    private long startTime = 0;//初始化时间
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            long currTime = System.currentTimeMillis();
            if(currTime - startTime >= 2000){

                startTime = currTime;
                Toast.makeText(this,"再点一次退出应用",Toast.LENGTH_SHORT).show();
                return true;
            }else{
               //清理缓存
                x.image().clearMemCache();//清理内存的缓存
                //xutils可以清理缓存文件
//              x.image().clearCacheFiles();//清理缓存文件
            }
        }

        return super.onKeyUp(keyCode, event);
    }
}
