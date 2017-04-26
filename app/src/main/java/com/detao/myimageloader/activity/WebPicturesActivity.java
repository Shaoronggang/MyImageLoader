package com.detao.myimageloader.activity;

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

import com.detao.myimageloader.R;
import com.detao.myimageloader.adapter.WebPictureAdapter;
import com.detao.myimageloader.bean.HistoryUrl;
import com.detao.myimageloader.bean.ImageBean;
import com.detao.myimageloader.dao.HistoryUrlDao;
import com.detao.myimageloader.listener.MyCacheCallback;
import com.detao.myimageloader.util.AppUtils;
import com.detao.myimageloader.util.Constants;

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

/**
 * Created by shaoronggang on 2017/2/15.
 */
public class WebPicturesActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_pictures_info;//显示文本信息
    private CheckBox cb_pictures_select; //勾选图片
    private ImageView iv_pictures_download; //下载或删除图片
    private Button btn_pictures_stop; //停止抓取图片
    private ProgressBar pb_pictures_loading; //深度抓取进度
    private GridView gv_pictures_pictures; //抓取图片列表
    private WebPictureAdapter adapter;
    private String url;
    private List<ImageBean> imageBeens = new ArrayList<ImageBean>();//所有图片url的集合
    private HashSet<ImageBean> imageUrlSet = new HashSet<>(); //所有图片url 的set集合，用于过滤
    public static boolean isEdit = false;//标识是否是编辑模式
    private HistoryUrlDao historyUrlDao;
    private List<HistoryUrl> historyUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.webpictures_activity);
        init();
    }

    private void init() {
        tv_pictures_info = (TextView) findViewById(R.id.tv_pictures_info);
        cb_pictures_select = (CheckBox) findViewById(R.id.cb_pictures_select);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        btn_pictures_stop = (Button) findViewById(R.id.btn_pictures_stop);
        pb_pictures_loading = (ProgressBar) findViewById(R.id.pb_pictures_loading);
        gv_pictures_pictures = (GridView) findViewById(R.id.gv_pictures_pictures);

        //设置actionbar返回home的可操作性，同时生成一个返回图标
        getActionBar().setDisplayHomeAsUpEnabled(true);
        historyUrlDao = new HistoryUrlDao();
        historyUrls = historyUrlDao.getAll();


        //显示列表
        adapter = new WebPictureAdapter(this);
        gv_pictures_pictures.setAdapter(adapter); //此时集合中没有数据
        url = getIntent().getStringExtra("url");
        url = AppUtils.checkPreUrl(url);
        getHttpImages(url);


        gv_pictures_pictures.setOnItemClickListener(this);
        gv_pictures_pictures.setOnItemLongClickListener(this);
        cb_pictures_select.setOnCheckedChangeListener(this);
    }

    /**
     * 根据网页的url显示所包含的图片
     *
     * @param url
     */
    private void getHttpImages(final String url) {
        //保存当前状态
        Constants.state = Constants.S_WEB;
        //保存到请求历史记录
        addToHistory(url);

        //显示pd
        showProgressDialog("正在抓取" + url + "网页上的图片", false);
        //发送请求
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            /**
             * 请求成功
             * @param html 网页文本
             */
            @Override
            public void onSuccess(String html) {
                //先清理之前的集合数据
                imageBeens.clear();
                imageUrlSet.clear();
                Log.e("Sucess", html);
                //显示网页中包含的所有的图片
                showImageFromHtml(url, html);
                //移除pd
                pd.dismiss();
                //显示深度抓取提醒
                showDeepDialog(html);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                AppUtils.showToast("拾取图片失败!");
                pd.dismiss();
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
     * 添加到历史记录表中
     *
     * @param url
     */
    private void addToHistory(String url) {
        HistoryUrl historyUrl = new HistoryUrl(-1, url);
        if (!historyUrls.contains(historyUrl)) {
            //保存到数据库
            historyUrlDao.save(historyUrl);
            //保存到内存集合中
            historyUrls.add(historyUrl);
        }
    }

    private SearchView searchView;//搜索操作对应的类的对象

    /**
     * 搜索功能实现如下
     *
     * @param menu
     * @return
     */
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
                url = AppUtils.checkPreUrl(url);
                //联网操作
                getHttpImages(url);

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

    /**
     * 查看历史记录/下载图片功能
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_download:
                showDownloadImages();
                break;
            case R.id.item_menu_history:
                showHistory();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    /**
     * 显示拾取页面的历史记录
     */
    private void showHistory() {
        final String[] urls = new String[historyUrls.size()];
        for (int i = 0; i < historyUrls.size(); i++) {
            urls[i] = historyUrls.get(i).getUrl();
        }

        //显示列表并且绑定相应的点击事件操作
        new AlertDialog.Builder(this)
                .setTitle("历史记录")
                .setItems(urls, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getHttpImages(urls[which]);
                    }
                })
                .show();
    }

    /**
     * 显示下载的图片列表
     */
    private void showDownloadImages() {
        //声明当前的状态为本地状态
        Constants.state = Constants.S_SDCARD;

        //修改页面的图片的显示
        iv_pictures_download.setImageResource(R.drawable.op_del_press);
        iv_pictures_download.setVisibility(View.GONE);
        cb_pictures_select.setChecked(false);
        cb_pictures_select.setVisibility(View.GONE);

        isEdit = false;//改变为查看状态

        //获取本地图片
        imageBeens = getDownloadImages(Constants.SAVE_DIR);
        adapter.setList(imageBeens);
        adapter.notifyDataSetChanged();

        tv_pictures_info.setText("下载文件夹共有" + imageBeens.size() + "张图片");
        selectCount = 0;//重置选中项
    }

    /**
     * 获取文件中的所有图片的绝对路径
     *
     * @param saveDir
     * @return
     */
    private List<ImageBean> getDownloadImages(String saveDir) {
        List<ImageBean> list = new ArrayList<ImageBean>();
        File file = new File(saveDir);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                list.add(new ImageBean(files[i].getAbsolutePath()));//绝对路径
            }
        }

        return list;
    }

    /**
     * 显示深度抓取提醒
     *
     * @param html
     */
    private void showDeepDialog(final String html) {
        new AlertDialog.Builder(WebPicturesActivity.this)
                .setTitle("请确认")
                .setMessage(url + "的首页已经抓取完毕,是否进行深度抓取?(请确认是否有Wifi环境)")
                .setPositiveButton("深度抓取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deepSearch(html);
                    }
                })
                .setNegativeButton("下回吧", null)
                .show();

    }

    /**
     * 深度抓取图片
     *
     * @param html
     */
    private void deepSearch(String html) {
        btn_pictures_stop.setVisibility(View.VISIBLE);
        Document doc = Jsoup.parse(html); //解析html页面

        //获取页面的所有链接
        Elements links = doc.select("a[href]");
        List<String> useLinks = getUseableLinks(links);  //过滤

        pb_pictures_loading.setVisibility(View.VISIBLE);
        pb_pictures_loading.setMax(useLinks.size());

        Log.e("TAG", "useLinks=" + useLinks.size());

        for (int i = 0; i < useLinks.size(); i++) {
            final String href = useLinks.get(i);
            x.http().get(new RequestParams(href), new MyCacheCallback<String>() {//此时得到的是二级页的html代码字符串，
                @Override
                public boolean onCache(String result) {
                    return super.onCache(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (stopDeep) {
                        return;
                    }
                    updateProgress();
                }

                @Override
                public void onSuccess(String result) {
                    if (stopDeep) {
                        return;
                    }
                    showImageFromHtml(href, result);
                    updateProgress();
                }
            });

        }

    }

    /**
     * 更新抓取图片的进度条进度
     */
    private void updateProgress() {
        tv_pictures_info.setText("已经抓取了" + imageBeens.size() + "张图片");
        pb_pictures_loading.incrementProgressBy(1);
        if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
            tv_pictures_info.setText("全部抓取完成，总共抓取了" + imageBeens.size() + "张图片");
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
        //用于保存有效url的集合，(url保证是不重复的)
        List<String> listLinks = new ArrayList<String>();

        //遍历所有的links，过滤，保存有效链接
        for (Element link : links) {
            String href = link.attr("href"); // <a href='opensource.shtml' target="_blank"></a>
            //设置过滤条件
            if (href.equals("")) {
                continue; //跳过
            }
            if (href.equals(url)) {
                continue; //跳过
            }
            if (href.startsWith("javascript")) {
                continue;//跳过
            }
            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set.contains(href)) {
                set.add(href);//将有效链接保存到哈希表中
                listLinks.add(href);
            }
        }

        return listLinks;
    }

    /**
     * 点击停止抓取
     *
     * @param v
     */
    public void stopSearch(View v) {
        stopDeep = true;
        pb_pictures_loading.setVisibility(View.GONE);
        btn_pictures_stop.setVisibility(View.GONE);
        tv_pictures_info.setText("当前，总共抓取了" + imageBeens.size() + "张图片");
    }

    private int selectCount = 0; //设置选中的item的个数

    /**
     * 解析html请求图片显示
     *
     * @param url
     * @param html
     */
    private void showImageFromHtml(String url, String html) {
        List<ImageBean> list = parseHtml(url, html);
        imageBeens.addAll(list);
        adapter.setList(imageBeens); //将数据设置到相应的adapter中去，将html页面中的所有的图片对象添加到整体的imageBeans集合中
        selectCount = 0;
        adapter.notifyDataSetChanged();
    }

    /**
     * 解析网页文本, 找出其中所有图片信息对象的集合
     *
     * @param url
     * @param html
     * @return
     */
    private List<ImageBean> parseHtml(String url, String html) {
        List<ImageBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(html); //解析html代码
        List<Element> imgs = doc.getElementsByTag("img");   //得到图片文件的集合
        for (Element img : imgs) {//举例：element：<img src="images/ddhover.jpg" />
            String src = img.attr("src");//"images/ddhover.jpg"
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);
                if (!imageUrlSet.contains(imageBean) && src.indexOf("/../") == -1) {
                    imageUrlSet.add(imageBean);
                    list.add(imageBean);
                }
            }
        }

        return list;
    }

    /**
     * 检查<img>中src属性值
     *
     * @param url
     * @param src
     * @return
     */
    private String checkSrc(String url, String src) {
        if (src.startsWith("http")) {
            url = src;
        } else {
            if (src.startsWith("/")) {
                url = url + src;
            } else {
                url = url + "/" + src;
            }
        }

        return url;
    }


    private ProgressDialog pd;

    private void showProgressDialog(String msg, boolean isHorizon) {
        pd = new ProgressDialog(this);
        if (isHorizon) {
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//显示水平进度条
        } else {
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);//显示圆形进度条
        }
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("提示信息");
        pd.setMessage(msg);
        pd.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isEdit = false;//改回查看状态
    }

    /**
     * 点击相应的item进入到相应的页面
     *
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (isEdit) { //编辑状态
            boolean check = adapter.getImageChecked(position);
            selectCount += check ? -1 : 1;
            tv_pictures_info.setText(selectCount + "/" + imageBeens.size());
            adapter.changeChecked(position, check);
            adapter.notifyDataSetChanged();
        } else { //查看状态
            Intent intent = new Intent(this, DragImageActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("imageBeans", (ArrayList) imageBeens);
            startActivity(intent);
        }
    }

    /**
     * 长按相应的图片item进行的相应的操作
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isEdit) { //查看状态
            isEdit = true; //编辑状态
            cb_pictures_select.setVisibility(View.VISIBLE);
            iv_pictures_download.setVisibility(View.VISIBLE);
        }
        boolean Checked = adapter.getImageChecked(i); //选中状态
        selectCount += Checked ? -1 : 1; //如果是未被选中，则选中相应地item
        tv_pictures_info.setText(selectCount + "/" + imageBeens.size());
        adapter.changeChecked(i, Checked);
        adapter.notifyDataSetChanged();
        return true;
    }

    /**
     * 全选CheckBox的选中状态改变时的监听方法
     *
     * @param compoundButton
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        adapter.checkOrDisCheckAll(compoundButton.isChecked());
        if (compoundButton.isChecked()) {
            selectCount = adapter.getList().size();
        } else {
            selectCount = 0;
        }
        tv_pictures_info.setText(selectCount + "/" + imageBeens.size());
    }

    /**
     * 编辑状态下，下载/删除图片
     *
     * @param view
     */
    public void downloadImages(View view) {
        if (Constants.state == Constants.S_WEB) { //网络图片，批量下载
            showProgressDialog("开始批量下载", true);
            pd.setTitle("批量下载");
            pd.setMax(selectCount);
            for (ImageBean imageBean : imageBeens) {
                if (imageBean.isChecked()) {
                    //当前图片需要下载
                    downloadImage(imageBean.getUrl()); //  子线程
                }
            }

        } else if (Constants.state == Constants.S_SDCARD) { //本地图片
            for (int i = 0; i < imageBeens.size(); i++) {
                ImageBean imageBean = imageBeens.get(i);
                if (imageBean.isChecked()) {
                    //当前图片需要删除
                    File file = new File(imageBean.getUrl());
                    if (file.exists()) {
                        file.delete();
                    }
                    imageBeens.remove(i);
                    i--;
                }
            }
            adapter.notifyDataSetChanged();
            AppUtils.showToast("所选图片删除完毕!");
            cb_pictures_select.setVisibility(View.GONE);
            cb_pictures_select.setChecked(false);
            iv_pictures_download.setVisibility(View.GONE);
        }

        isEdit = false;
        tv_pictures_info.setText("本地总共有" + imageBeens.size() + "张图片");
        selectCount = 0;
    }

    /**
     * 根据相应的url下载图片
     *
     * @param url
     */
    private void downloadImage(final String url) {
        //下载文件夹
        File fdir = new File(Constants.SAVE_DIR);
        //创建文件夹
        if (!fdir.exists()) {
            fdir.mkdirs();
        }

        final String filePath = Constants.SAVE_DIR + "/" + System.currentTimeMillis() + AppUtils.cutImagePath(url);
        RequestParams params = new RequestParams(url);
        x.http().get(params, new MyCacheCallback<File>() {
            //从内存缓存中保存图片file
            @Override
            public boolean onCache(File result) {
                FileUtil.copy(result.getAbsolutePath(), filePath);//类似于二级缓存的操作
                updateDownImageProgress();
                return true;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                AppUtils.showToast("图片" + url + "下载失败");
                updateDownImageProgress();
            }


            @Override
            public void onSuccess(File result) {
                super.onSuccess(result);
                FileUtil.copy(result.getAbsolutePath(), filePath);
                updateDownImageProgress();
            }
        });


    }

    /**
     * 更新下载进度
     */
    private void updateDownImageProgress() {
        pd.incrementProgressBy(1);
        if (pd.getProgress() == pd.getMax()) {
            pd.dismiss();
            AppUtils.showToast("所有图片下载完毕!");

            adapter.checkOrDisCheckAll(false);
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setVisibility(View.GONE);
        }
    }

    /**
     * 点击
     */
    @Override
    public void onBackPressed() {
        if (isEdit) {//编辑状态
            isEdit = false;
            selectCount = 0;
            tv_pictures_info.setText("请在搜索框中输入网站网址");
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setVisibility(View.GONE);
            adapter.checkOrDisCheckAll(false);
            adapter.notifyDataSetChanged();
        } else {//查看状态
            super.onBackPressed();
        }

    }
}
