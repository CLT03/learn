package com.example.administrator.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/3/4.
 */
public class FindListItem {
    public Bitmap mBitmap;
    public String text1;
    public String text2;
    public String text3;
    public String text4;
    public String text5;
    public float mFloat;

    public FindListItem(Bitmap m,String t1,String t2,String
            t3,String t4,String t5,float f) {
        mBitmap = m;
        text1=t1;
        text2=t2;
        text3=t3;
        text4=t4;
        text5=t5;
        mFloat=f;
    }
}
