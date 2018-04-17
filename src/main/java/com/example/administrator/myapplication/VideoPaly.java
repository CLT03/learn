package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/2/23.
 */
public class VideoPaly extends Activity {


    VideoView video;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏（电量那栏）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_paly);
        //全屏播放
        video= (VideoView) findViewById(R.id.video);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        video.setLayoutParams(layoutParams);

        try {
            final Intent intent=getIntent();
            final Bundle bundle=intent.getExtras();
            //解决URI有空格Tomcat识别不了的问题
            String temp1= URLEncoder.encode(bundle.getString("learn_data_name"), "utf-8");
            //Log.i("objectTs", temp1);
            String temp2=temp1.replaceAll("\\+", "%20");
            //Log.i("objectT", temp2);
            Uri uri = Uri.parse(this.getString(R.string.link)+"/file/" + temp2);
            //String Path="/storage/sdcard1/1/0006.优酷网-欺诈游戏 06-0001.flv";
            video.setMediaController(new MediaController(this));
            video.setVideoURI(uri);
        video.start();

       /* mHomeWatcher = new HomeWatcher(this);
            mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
                @Override
                public void onHomePressed() {
                    Log.e(TAG, "onHomePressed");
                    video.suspend();
                }

                @Override
                public void onHomeLongPressed() {
                    Log.e(TAG, "onHomeLongPressed");
                    video.suspend();
                }
            });
            mHomeWatcher.startWatch();*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

   /* @Override
    protected void onPause() {
        super.onPause();
        mHomeWatcher.stopWatch();// 在onPause中停止监听，不然会报错的。
    }
    @Override
    protected void onRestart(){
        video.resume();
    }*/
//Android应用横竖屏切换时Activity重启问题
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    @Override
    protected void onPause(){
        super.onPause();
        video.pause();
        position=video.getCurrentPosition();
        Log.d("lifeCycle", "MainActivity: 我是onPause方法");
        //System.out.print(position);

    }

    @Override
    protected void onResume(){

        super.onResume();
       // System.out.print(position);
        video.start();
        if(position>0){video.seekTo(position);}
        Log.d("lifeCycle", "MainActivity: 我是 onResume方法");
    }

}
