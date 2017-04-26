package com.detao.myimageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.detao.myimageloader.R;
import com.detao.myimageloader.bean.WebLink;

import java.util.List;

/**
 * Created by shaoronggon 2017/2/15.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<WebLink> dada;

    public ImageAdapter(Context context, List<WebLink> list) {
        this.context = context;
        this.dada = list;
    }

    @Override
    public int getCount() {
        return dada.size();
    }

    @Override
    public Object getItem(int position) {
        return dada.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder ;
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.image_item,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_img);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        }else {
           holder = (ViewHolder) convertView.getTag();
        }
        WebLink link = dada.get(position);
        holder.imageView.setImageResource(link.image);
        holder.textView.setText(link.name);
        return convertView;
    }

     static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
