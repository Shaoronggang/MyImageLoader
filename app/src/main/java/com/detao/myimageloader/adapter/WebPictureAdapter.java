package com.detao.myimageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.detao.myimageloader.R;
import com.detao.myimageloader.activity.WebPicturesActivity;
import com.detao.myimageloader.bean.ImageBean;
import com.detao.myimageloader.util.AppUtils;

import org.xutils.x;

import java.util.List;


/**
 * Created by shaoronggang on 2017/2/16.
 */

public class WebPictureAdapter extends BaseAdapter {
    private Context context;
    private List<ImageBean> list; //加载要显示的图片

    public WebPictureAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<ImageBean> list) {
        this.list = list;
    } //使用这个方法来添加数据

    public List<ImageBean> getList() {
        return list;
    }

    public void addList(List<ImageBean> list) {
        this.list.addAll(list); //尾部追加
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return (list == null) ? null : list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_web_pictures, null);
            viewHolder = new ViewHolder();
            viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.iv_img);
            viewHolder.selectIv = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(viewHolder); //将相应的存储类设置进去
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //得到当前项的数据对象
        ImageBean bean = list.get(i);
        //显示图片
        x.image().bind(viewHolder.iconIv, bean.getUrl(), AppUtils.smallImageOptions);
        if (WebPicturesActivity.isEdit) { //编辑模式
            viewHolder.selectIv.setVisibility(View.VISIBLE);
            if (bean.isChecked()) {// 选中
                viewHolder.selectIv.setImageResource(R.drawable.blue_selected); //选中时的状态
            } else {// 不选中
                viewHolder.selectIv.setImageResource(R.drawable.blue_unselected); //未选中时的状态
            }
        } else {//查看模式
            viewHolder.selectIv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    /**
     * 获取当前imageBean的选中状态
     * @param position
     * @return
     */
    public boolean getImageChecked(int position) {
        return list.get(position).isChecked();
    }

    /**
     * 修改当前的position位置的imagebean的状态：选中--->不选中 ，反之则反
     * @param isChecked
     */
    public void changeChecked(int position,boolean isChecked) {
        list.get(position).setChecked(!isChecked);
    }

    public void checkOrDisCheckAll(boolean checked) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(checked);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        ImageView iconIv; //图片
        ImageView selectIv; //标识是否"选中"
    }


}