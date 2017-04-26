package com.atguigu.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.WebLink;

import java.util.List;

/**
 * Created by shkstart on 2016/5/6.
 */
public class MainAdapter extends BaseAdapter {
    private Context context;
    private List<WebLink> data;

    public MainAdapter() {
    }

    public MainAdapter(Context context, List<WebLink> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_main,null);

            viewHolder.iv_item_img = (ImageView) convertView.findViewById(R.id.iv_item_img);
            viewHolder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取集合中指定位置的数据
        WebLink webLink = data.get(position);
        viewHolder.iv_item_img.setImageResource(webLink.getIcon()); //本地的图片数据
        viewHolder.tv_item_name.setText(webLink.getName());

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_item_img;
        TextView tv_item_name;
    }
}
