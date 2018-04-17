package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RatingBar;

/*
*
*1.VideoView本身就是对SurfaceView和MediaPlayer做了一个封装
*2.实现视频列表播放
*
* 如果读取本地文件，和网络的话 需要添加权限
*
* */
public class Test3Activity extends Activity {
    // 可交互的两个 ratingBar
    private RatingBar ratingBar1 = null;
    private RatingBar ratingBar2 = null;

    // 不可交互的两个 ratingBar
    private RatingBar ratingBarOne = null;
    private RatingBar ratingBarTwo = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test3);
        initView();
    }
    // 初始化函数
    public void initView() {
        // 通过findViewById分别获得控件
        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBarOne = (RatingBar) findViewById(R.id.ratingBarOne);
        ratingBarTwo = (RatingBar) findViewById(R.id.ratingBarTwo);

        // 为ratingBar1添加 OnRatingBarChangeListener
        // 当用户交互改变分值时，触发该事件
        ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // 该方法可以获取到 3个参数
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating, boolean paramBoolean) {
                // 第一个参数 当前评分修改的 ratingBar
                System.out.println(ratingBar);
                // 第二个参数 当前评分分数，范围 0~星星数量
                System.out.println(rating);
                // 第三个参数 如果评分改变是由用户触摸手势或方向键轨迹球移动触发的，则返回true
                System.out.println(paramBoolean);

                //将不可交互的展示型ratingBarOne的评分分数通过setRating
                //设置成onRatingChanged方法获得的ratingBar1的分值参数rating
                ratingBarOne.setRating(rating);
            }
        });

        // 为ratingBar2添加 OnRatingBarChangeListener
        // 当用户交互改变分值时，触发该事件
        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar,
                                        float rating, boolean paramBoolean) {
                //将不可交互的展示型ratingBarTwo的评分分数通过setRating
                //设置成onRatingChanged方法获得的ratingBar2的分值参数rating
                ratingBarTwo.setRating(rating);
            }
        });
    }
}

