package com.detao.myimageloader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.detao.myimageloader.R;
import com.detao.myimageloader.util.AppUtils;

import org.xutils.x;

/**
 * Created by shaoronggang on 2017/2/24.
 */

public class ImgDetailFragment extends Fragment {

    public static final String KEY = "image";
    private ImageView imageView;
    private String imagePath; // 网络/远程（url）

    public static ImgDetailFragment newInstance(String imgResourceId){
        //创建当前类对象
        ImgDetailFragment fragment = new ImgDetailFragment();
        //保存参数
        Bundle arg = new Bundle();
        arg.putString(KEY,imgResourceId);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从fragment中的参数中取出保存的参数（参数标识），并保存为成员变量
        if(getArguments() != null) {
            imagePath = getArguments().getString(KEY);//取参
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_detail,container,false);
        imageView = (ImageView) view.findViewById(R.id.iv_big_img);
        //加载图片显示(可能是远程的, 也可能是网络的)
        x.image().bind(imageView,imagePath, AppUtils.bigImageOptions);
        return view;
    }
}
