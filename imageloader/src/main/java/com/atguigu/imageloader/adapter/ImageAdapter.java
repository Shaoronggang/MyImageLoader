package com.atguigu.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.atguigu.imageloader.R;
import com.atguigu.imageloader.bean.ImageBean;
import com.atguigu.imageloader.ui.WebPicturesActivity;
import com.atguigu.imageloader.util.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by shkstart on 2016/5/7.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<ImageBean> list;

    public List<ImageBean> getList() {
        return list;
    }

    public void setList(List<ImageBean> list) {
        this.list = list;
    }

    public void addList(List<ImageBean> data){
        list.addAll(data);
    }

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return (list == null)? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return (list == null)? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_pictures,null);
            holder = new ViewHolder();

            holder.iv_item_pic = (ImageView) convertView.findViewById(R.id.iv_item_pic);
            holder.iv_item_select = (ImageView) convertView.findViewById(R.id.iv_item_select);


            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //装配数据
        ImageBean imageBean = list.get(position);
        //仍然会联网，将指定url地址对应的图片资源下载下来，并显示到holder.iv_item_pic上
        x.image().bind(holder.iv_item_pic,imageBean.getUrl(), AppUtils.smallImageOptions);

        //考虑iv_item_select的设置
        if(WebPicturesActivity.isEdit){//编辑状态
            holder.iv_item_select.setVisibility(View.VISIBLE);
            if(imageBean.isChecked()){//设置iv_item_select的选中状态
                holder.iv_item_select.setImageResource(R.drawable.blue_selected);
            }else{
                holder.iv_item_select.setImageResource(R.drawable.blue_unselected);
            }

        }else{//查看状态
            holder.iv_item_select.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 获取当前position位置的imagebean的选中状态
     * @param position
     */
    public boolean getItemChecked(int position) {
        return list.get(position).isChecked();
    }

    /**
     * 修改当前的position位置的imagebean的状态：选中--->不选中 ，反之则反
     * @param isChecked
     */
    public void changeChecked(int position,boolean isChecked) {
        list.get(position).setChecked(!isChecked);
    }




    public static class ViewHolder{
        ImageView iv_item_pic;
        ImageView iv_item_select;
    }

    /**
     * 设置所有的item的选中/不选中的状态
     * @param isChecked
     */
    public void changeAllOrNot(boolean isChecked) {
        for(int i = 0;i < list.size();i++){//修改每一项的选中状态
            list.get(i).setChecked(isChecked);
        }
        //更新操作
        notifyDataSetChanged();

    }

}
