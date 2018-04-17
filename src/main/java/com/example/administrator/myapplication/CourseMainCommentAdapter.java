package com.example.administrator.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/3/5.
 */
public class CourseMainCommentAdapter extends BaseAdapter {
    private List<CourseMainCommentItem> list = null;

    private Context context = null;

    private LayoutInflater inflater = null;

    public CourseMainCommentAdapter(List<CourseMainCommentItem> list, Context context1) {
        this.list = list;
        this.context = context1;
        // 布局装载器对象
        inflater = LayoutInflater.from(context);
    }

    // 适配器中数据集中数据的个数
    @Override
    public int getCount() {
        return list.size();
    }

    // 获取数据集中与指定索引对应的数据项
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // 获取指定行对应的ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 获取每一个Item显示的内容
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.course_main_comment_list, null);
            viewHolder.i = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.t1= (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.t2= (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.t3= (TextView) convertView.findViewById(R.id.textView3);
            viewHolder.r = (RatingBar)convertView.findViewById(R.id.ratingBar);
            convertView.setTag(viewHolder);// 通过setTag将ViewHolder和convertView绑定
        }  else {
            viewHolder = (ViewHolder) convertView.getTag(); // 获取，通过ViewHolder找到相应的控件
        }
        CourseMainCommentItem courseMainCommentItem = list.get(position);
        viewHolder.i.setImageDrawable(courseMainCommentItem.mBitmap);
        viewHolder.t1.setText(courseMainCommentItem.text1);
        viewHolder.t2.setText(courseMainCommentItem.text2);
        viewHolder.t3.setText(courseMainCommentItem.text3);
        viewHolder.r.setRating(courseMainCommentItem.mFloat);
        return convertView;
    }

    class ViewHolder {
        ImageView i;
        TextView t1;
        TextView t2;
        TextView t3;
        RatingBar r;

    }
}
