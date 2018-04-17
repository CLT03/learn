package com.example.administrator.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/1/13.
 */
public class CourseList  extends RelativeLayout {
    private ImageView mImgView1 = null;
    private TextView mTextView1 = null;
    private TextView mTextView2 = null;
    private TextView mTextView3 = null;
    private TextView mTextView4 = null;
    private Context mContext;


    public CourseList(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.course_list, this, true);
        mContext = context;
        mImgView1 = (ImageView)findViewById(R.id.imageView);
        mTextView1 = (TextView)findViewById(R.id.textView1);
        mTextView2 = (TextView)findViewById(R.id.textView2);
        mTextView3 = (TextView)findViewById(R.id.textView3);
        mTextView4 = (TextView)findViewById(R.id.textView4);



    }



    /*设置图片接口*/
    public void setImageResource1(int resId){
        mImgView1.setImageResource(resId);
    }


    /*设置文字接口*/
    public void setText1(String str){
        mTextView1.setText(str);
    }
    public void setText2(String str){
        mTextView2.setText(str);
    }
    public void setText3(String str){
        mTextView3.setText(str);
    }
    public void setText4(String str){
        mTextView3.setText(str);
    }



//     /*设置触摸接口*/
//    public void setOnTouch(OnTouchListener listen){
//        mImgView.setOnTouchListener(listen);
//        //mTextView.setOnTouchListener(listen);
//    }


}
