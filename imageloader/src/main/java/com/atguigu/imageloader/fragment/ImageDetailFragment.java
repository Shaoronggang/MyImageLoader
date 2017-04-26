package com.atguigu.imageloader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.util.AppUtils;

import org.xutils.x;

/**
 * Created by shkstart on 2016/5/10.
 */
public class ImageDetailFragment extends Fragment {
    private String imagePath;//图片路径：①联网状态：url  ②本地状态：本地图片的路径
    private static final String KEY = "path";
    private ImageView imageView;

    public static ImageDetailFragment getInstance(String path){
        ImageDetailFragment fragment = new ImageDetailFragment();

        Bundle args = new Bundle();
        args.putString(KEY,path);
        fragment.setArguments(args);

        return fragment;



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            imagePath = (String) bundle.get(KEY);//初始化imagePath
        }
    }

    //每一个Fragment都对应一个布局文件。需要重写onCreateView()
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局文件：
        //方式一：
        //View.inflate();
        //方式二：
        View view = inflater.inflate(R.layout.fragment_photoview, container, false);
        imageView = (ImageView) view.findViewById(R.id.pv_fragment);

        //如果是联网状态的图片：会联网下载指定imagePath路径的图片，并设置到imageView上
        //如果是本地状态的图片：会加载本地imagePath路径的图片，并设置到imageView上
        x.image().bind(imageView,imagePath, AppUtils.bigImageOptions);
        return view;
    }
}
