package com.atguigu.imageloader.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.adapter.ImageAdapter;
import com.atguigu.imageloader.bean.HistoryUrl;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.dao.HistoryUrlDao;
import com.atguigu.imageloader.listener.MyCacheCallback;
import com.atguigu.imageloader.util.AppUtils;
import com.atguigu.imageloader.util.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebPicturesActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView tv_pictures_top;//显示文本信息
    private CheckBox cb_pictures_select; //勾选图片
    private ImageView iv_pictures_download; //下载或删除图片
    private Button btn_pictures_stop; //停止抓取图片
    private ProgressBar pb_pictures_loading; //深度抓取进度
    private GridView gv_pictures_pics; //抓取图片列表

    private ImageAdapter adapter;


    public static boolean isEdit = false;//是否处于编辑状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pictures);

//        Intent intent = getIntent();
//        String url = intent.getStringExtra("url");
//        Log.e("TAG",url);

        //保持屏幕常亮
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        init();


    }

    private String url;//当前页面显示的网站的地址

    private HistoryUrlDao historyUrlDao;//用于操作历史记录的dao
    private List<HistoryUrl> historyUrlList;//内存中保存历史记录的集合

    private void init() {
        //视图对象的初始化
        tv_pictures_top = (TextView) findViewById(R.id.tv_pictures_top);
        cb_pictures_select = (CheckBox) findViewById(R.id.cb_pictures_select);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        btn_pictures_stop = (Button) findViewById(R.id.btn_pictures_stop);
        pb_pictures_loading = (ProgressBar) findViewById(R.id.pb_pictures_loading);
        gv_pictures_pics = (GridView) findViewById(R.id.gv_pictures_pics);


        //进行初始化历史记录相关的类
        historyUrlDao = new HistoryUrlDao();
        historyUrlList = historyUrlDao.getAll();

        //设置actionbar返回home的可操作性，同时生成一个返回图标
        getActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ImageAdapter(this);
        //显示列表
        gv_pictures_pics.setAdapter(adapter);//此时集合中是没有数据

        //联网获取指定网站（url)的数据:html
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        url = AppUtils.checkUrlPre(url);

        //联网操作
        getHttpImage(url);

        //给GridView设置OnItemLongClickListener
        gv_pictures_pics.setOnItemLongClickListener(this);

        //给GridView设置OnItemClickListener
        gv_pictures_pics.setOnItemClickListener(this);

        //设置CheckBox的点击状态改变的监听
        cb_pictures_select.setOnCheckedChangeListener(this);


    }

    private ProgressDialog dialog;
    private List<ImageBean> imageBeans = new ArrayList<ImageBean>();//用户保存通过jsoup解析的所有图片的对象
    private Set<ImageBean> imageSet = new HashSet<>();//用于过滤重复的图片

    /**
     * 联网的具体操作
     *
     * @param url
     */
    private void getHttpImage(final String url) {
        //显示正在联网的PorgressDialog
        showProgressDialog("正在抓取" + url + "网站的图片", true);

        //将当前的url添加到历史记录里
        addToHistoryUrl(url);

        Constants.state = Constants.STATE_WEB;//联网状态

        //联网操作
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            /**
             * 正确的获取了响应数据
             * @param html
             */
            @Override
            public void onSuccess(String html) {
                Log.e("TAG", html);
                Log.e("相应的数据是：",url);
                //保证请求之前集合数据是空的
                imageBeans.clear();
                imageSet.clear();

                showImagesFromHtml(url, html);

                dialog.dismiss();

                //提示是否需要深度抓取
                showDeepSearchDialog(url, html);


            }

            /**
             * 获取数据失败
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(WebPicturesActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private SearchView searchView;//搜索操作对应的类的对象

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_web_pictures, menu);//加载menu的布局文件
        MenuItem item = menu.findItem(R.id.item_menu_search);
        searchView = (SearchView) item.getActionView();

        //设置searchView
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("请输入网址");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 当点击确认操作时的回调方法
             * @param url
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String url) {
                url = AppUtils.checkUrlPre(url);
                //联网操作
                getHttpImage(url);

                searchView.clearFocus();//清除焦点，隐藏软键盘
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_history://查看历史记录

                showHistory();

                break;
            case R.id.item_menu_download://查看本地图片

                Constants.state = Constants.STATE_SD;//改变为本地状态

                //调整页面的视图对象的状态
                iv_pictures_download.setImageResource(R.drawable.op_del_press);
                iv_pictures_download.setVisibility(View.GONE);
                cb_pictures_select.setChecked(false);
                cb_pictures_select.setVisibility(View.GONE);
                selectCount = 0;

                isEdit = false;//改变为查看状态

                //查询本地保存的下载的图片
                imageBeans = showDownloadImages();
                adapter.setList(imageBeans);
                adapter.notifyDataSetChanged();//更新列表

                tv_pictures_top.setText("在本地总共有" + imageBeans.size() + "张图片");

                break;

            case android.R.id.home://左边的应用图标
                finish();
                break;
        }

        return true;
    }

    /**
     * 点击页面上的imageview的回调方法
     * @param v
     */
   public void downloadOrDelImages(View v) {
       int state = Constants.state;
       if(state == Constants.STATE_WEB){//网络状态：下载选中的图片

           //提供一个ProgressDialog
           showProgressDialog("正在下载选中的图片",false);
           //设置最大值
           dialog.setMax(selectCount);

           File fileDir = new File(Constants.SAVE_DIR);
           if(!fileDir.exists()){//第一次下载时，此目录就不存在
               fileDir.mkdirs();
           }
           for (int i = 0;i < imageBeans.size();i++) {
               final ImageBean imageBean = imageBeans.get(i);
               if (imageBean.isChecked()) {
//
                   RequestParams requestParams = new RequestParams(imageBean.getUrl());
                   requestParams.setConnectTimeout(5000);
                   x.http().get(requestParams,new MyCacheCallback<File>(){
                       @Override
                       public boolean onCache(File result) {
                            String filePath = Constants.SAVE_DIR + "/" + System.currentTimeMillis() + AppUtils.getUrlImageName(imageBean.getUrl());
                            FileUtil.copy(result.getAbsolutePath(), filePath);//类似于二级缓存的操作
                            updateDownloadImage();
                            return true;//如果返回值是true，则下次显示内存层面的图片的话，直接可以调用内存的缓存
                       }

                       @Override
                       public void onSuccess(File result) {
                           String filePath = Constants.SAVE_DIR + "/" + System.currentTimeMillis() + AppUtils.getUrlImageName(imageBean.getUrl());
                           FileUtil.copy(result.getAbsolutePath(), filePath);//类似于二级缓存的操作
                           updateDownloadImage();

                       }

                       @Override
                       public void onError(Throwable ex, boolean isOnCallback) {
                           Toast.makeText(WebPicturesActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                           updateDownloadImage();
                       }
                   });


               }
           }

       }else if(state == Constants.STATE_SD){//本地状态：删除选中的图片

           //通过遍历，获取选中项的图片对应的本地图片，将其删除：①存储中删除 ②内存中删除
           for (int i = 0;i < imageBeans.size();i++){
               ImageBean imageBean = imageBeans.get(i);
               if(imageBean.isChecked()){
                   String url = imageBean.getUrl();//例如：url:storage/sdcard/imageloader/xxxx.png
                   File file = new File(url);
                   if(file.exists()){
                       file.delete();//删除此文件
                   }
                   imageBeans.remove(i);
                   i--;
               }
           }
           Toast.makeText(WebPicturesActivity.this, "选中的" + selectCount + "张图片已经删除", Toast.LENGTH_SHORT).show();
           //更新列表
           adapter.notifyDataSetChanged();

           //修改视图对象
           cb_pictures_select.setVisibility(View.GONE);
           cb_pictures_select.setChecked(false);

           iv_pictures_download.setVisibility(View.GONE);
           isEdit = false;
           selectCount = 0;
           tv_pictures_top.setText("本地总共有" + imageBeans.size() + "张图片");

       }
   }

    /**
     * 针对于返回键，当在编辑状态时，点击返回，进入查看状态
     * 在查看状态时，点击返回，退出当前界面
     */
    @Override
    public void onBackPressed() {
        if(isEdit){//编辑状态
            isEdit = false;

            tv_pictures_top.setText("请在搜索框输入网址");
            cb_pictures_select.setChecked(false);
            cb_pictures_select.setVisibility(View.GONE);
            iv_pictures_download.setVisibility(View.GONE);

            selectCount = 0;
            adapter.changeAllOrNot(false);


        }else{
            super.onBackPressed();//内部执行返回键的退出当前Activity的功能

        }


    }

    /**
     * 更新下载的进度
     */
    private void updateDownloadImage() {
        dialog.incrementProgressBy(1);
        if(dialog.getProgress() == dialog.getMax()){//达到最大值，意味着下载结束
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setChecked(false);
            cb_pictures_select.setVisibility(View.GONE);
            dialog.dismiss();//消失
            selectCount = 0;
            isEdit = false;//查看状态
            tv_pictures_top.setText("请在搜索框中输入网址");
            adapter.changeAllOrNot(false);

            Toast.makeText(WebPicturesActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查看本地已经下载的图片
     */
    private List<ImageBean> showDownloadImages() {

        List<ImageBean> list = new ArrayList<>();
        File fileDir = new File(Constants.SAVE_DIR);//下载图片对应的文件目录

//        if(!fileDir.exists()){
//
//        }

        File[] files = fileDir.listFiles();//所有的图片
        if (files != null) {
            //将本地图片的图片地址保存到集合中
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getAbsolutePath();
                ImageBean imageBean = new ImageBean(fileName);
                list.add(imageBean);
            }
        }

        return list;
    }

    private void showHistory() {
        final String[] urls = new String[historyUrlList.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = historyUrlList.get(i).getUrl();
        }
        new AlertDialog.Builder(this)
                .setTitle("历史记录")
                .setItems(urls, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //访问指定网址的链接
                        getHttpImage(urls[which]);
                    }
                })
                .show();
    }


    /**
     * 将url添加到历史记录(内存/存储)里,同时过滤掉重复的地址
     *
     * @param url
     */
    private void addToHistoryUrl(String url) {
        HistoryUrl historyUrl = new HistoryUrl(-1, url);
        if (!historyUrlList.contains(historyUrl)) {
            historyUrlDao.save(historyUrl);
            historyUrlList.add(historyUrl);
        }
    }

    /**
     * 提示深度抓取的Dialog
     *
     * @param html
     */
    private void showDeepSearchDialog(final String url, final String html) {
        new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage("首页图片已经抓取完成，是否需要深度抓取" + url + "站点的图片")
                .setPositiveButton("深度抓取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //深度抓取
                        deepSearch(html);
                    }
                })
                .setNegativeButton("下回吧", null)
                .show();


    }

    /**
     * 深度抓取的方法
     *
     * @param html
     */
    private void deepSearch(String html) {
        btn_pictures_stop.setVisibility(View.VISIBLE);

        Document doc = Jsoup.parse(html);// 解析HTML页面

        // 获取页面中的所有链接
        Elements links = doc.select("a[href]");
        List<String> useLinks = getUseableLinks(links);// 过滤

        pb_pictures_loading.setVisibility(View.VISIBLE);// 显示
        pb_pictures_loading.setMax(useLinks.size());
        Log.e("TAG", "useLinks=" + useLinks.size());


        for (int i = 0; i < useLinks.size(); i++) {
            final String href = useLinks.get(i);
            RequestParams params = new RequestParams(href);
            x.http().get(params, new MyCacheCallback<String>() {//此时得到的是二级页的html代码字符串，
                //不需要重写onCache()
                @Override
                public void onSuccess(String html) {
                    if (stopDeep) {
                        return;
                    }
                    showImagesFromHtml(href, html);//与首页的联网获取图片的url的方式完全相同
                    updateProgress();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (stopDeep) {
                        return;
                    }
                    Log.e("TAG", "抓取" + href + "失败");
                    updateProgress();
                }
            });
        }

    }

    private void updateProgress() {
        tv_pictures_top.setText("已经抓取了" + imageBeans.size() + "张图片");
        pb_pictures_loading.incrementProgressBy(1);
        if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
            tv_pictures_top.setText("全部抓取完成，总共抓取了" + imageBeans.size() + "张图片");
            pb_pictures_loading.setVisibility(View.GONE);
            btn_pictures_stop.setVisibility(View.GONE);
        }

    }

    private boolean stopDeep = false;//是否停止抓取

    /**
     * 过滤links,得到有效的链接地址（去除了重复的url地址）
     *
     * @param links
     * @return
     */
    private List<String> getUseableLinks(Elements links) {
        //用于过滤重复url的集合
        HashSet<String> set = new HashSet<String>();
        //用于保存有效url的集合（url保证是不重复的）
        List<String> lstLinks = new ArrayList<String>();

        //遍历所有links,过滤,保存有效链接
        for (Element link : links) {
            String href = link.attr("href");// <a href='opensource.shtml' target="_blank"></a>
            //Log.i("spl","过滤前,链接:"+href);
            // 设置过滤条件
            if (href.equals("")) {
                continue;// 跳过
            }
            if (href.equals(url)) {
                continue;// 跳过
            }
            if (href.startsWith("javascript")) {
                continue;// 跳过
            }

            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set.contains(href)) {
                set.add(href);// 将有效链接保存至哈希表中
                lstLinks.add(href);
            }

            Log.i("spl", "有效链接:" + href);
        }
        return lstLinks;

    }

    /**
     * 解析Html代码
     *
     * @param url
     * @param html
     */
    private void showImagesFromHtml(String url, String html) {
        List<ImageBean> list = parseHtml(url, html);
        imageBeans.addAll(list);//将html页面中的所有的图片对象添加到整体的imageBeans集合中
        adapter.setList(imageBeans);//
//        adapter.addList(imageBeans);//别用此方法

        adapter.notifyDataSetChanged();//更新数据

    }

    /**
     * 使用Jsoup解析Html代码
     *
     * @param url
     * @param html
     */
    private List<ImageBean> parseHtml(String url, String html) {
        List<ImageBean> list = new ArrayList<>();//用于盛装html代码中的图片的url地址对应的imagebean对象
        Document doc = Jsoup.parse(html);

        List<Element> imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {//举例：element：<img src="images/ddhover.jpg" />
            String src = img.attr("src");//"images/ddhover.jpg"
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);//
                //过滤掉重复的数据
                if (!imageSet.contains(imageBean) && src.indexOf("/../") == -1) {
                    imageSet.add(imageBean);
                    list.add(imageBean);
                }
            }
        }
        return list;

    }

    /**
     * 根据解析到的src,转换为一个绝对路径表示的url地址
     *
     * @param url
     * @param src
     * @return
     */
    private String checkSrc(String url, String src) {
        if (src.startsWith("http")) {
            url = src;
        } else if (src.startsWith("/")) {
            url = url + src;
        } else {
            url = url + "/" + src;
        }

        return url;
    }

    private void showProgressDialog(String message, boolean flag) {
        dialog = new ProgressDialog(this);
        if (flag) {//圆形的进度条
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//圆形
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平进度条
        }

        dialog.setTitle("显示信息");
        dialog.setMessage(message);
        dialog.show();//显示
    }

    /**
     * 点击“停止抓取”按钮的回调方法
     *
     * @param v
     */
    public void stopSearch(View v) {
        btn_pictures_stop.setVisibility(View.GONE);
        pb_pictures_loading.setVisibility(View.GONE);
        stopDeep = true;//设置停止的标识
        tv_pictures_top.setText("当前，总共抓取了" + imageBeans.size() + "张图片");
    }

    int selectCount = 0;//设置选中的item的个数

    /**
     * 长按item监听对应的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isEdit) {//查看状态
            isEdit = true;
            //设置相应视图对象的可见性
            iv_pictures_download.setVisibility(View.VISIBLE);
            cb_pictures_select.setVisibility(View.VISIBLE);
        }
        boolean isChecked = adapter.getItemChecked(position);
        //1.修改文本的显示
//        selectCount = (isChecked)? selectCount - 1 : selectCount + 1;
        selectCount += (isChecked) ? -1 : 1;
        tv_pictures_top.setText(selectCount + "/" + imageBeans.size());

        //2.修改集合中ImageBean的checked状态
        adapter.changeChecked(position, isChecked);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isEdit = false;//改为查看状态
    }

    /**
     * 点击item监听对应的回调方法
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isEdit) {//编辑状态
            boolean isChecked = adapter.getItemChecked(position);
            //1.修改文本的显示
//        selectCount = (isChecked)? selectCount - 1 : selectCount + 1;
            selectCount += (isChecked) ? -1 : 1;
            tv_pictures_top.setText(selectCount + "/" + imageBeans.size());

            //2.修改集合中ImageBean的checked状态
            adapter.changeChecked(position, isChecked);
            adapter.notifyDataSetChanged();

        } else {//查看状态

            Intent intent = new Intent(this, DragImageActivity.class);
            //携带数据
            intent.putExtra("position", position);
            intent.putExtra("imageBeans", (ArrayList) imageBeans);
            //启动新的Activity
            startActivity(intent);
        }
    }

    /**
     * 当CheckBox状态改变时的回调方法
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        adapter.changeAllOrNot(isChecked);
        if (isChecked) {//全选状态
            selectCount = imageBeans.size();
        } else {//全不选
            selectCount = 0;
        }
        tv_pictures_top.setText(selectCount + "/" + imageBeans.size());
    }
}
