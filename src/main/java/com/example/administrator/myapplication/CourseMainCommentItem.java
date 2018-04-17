package com.example.administrator.myapplication;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Created by Administrator on 2018/3/5.
 */
public class CourseMainCommentItem {
    public RoundedBitmapDrawable mBitmap;
    public String text1;
    public String text2;
    public String text3;
    public float mFloat;

    public CourseMainCommentItem(RoundedBitmapDrawable m,String t1,String t2,String
            t3,float f) {

        mBitmap = m;
        text1=t1;
        text2=t2;
        text3=t3;
        mFloat=f;
    }
}
