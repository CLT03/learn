package com.example.administrator.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    private Handler handler;
    private FragmentManager manager ; //fragment事务
    private FragmentTransaction transaction ;//fragment管理者
    //2个frament

    private MeFragment meFragment;
    private FindFragment findFragment;
    //下面导航栏的元素
    private FrameLayout fl_page_find, fl_page_me;
    private Button bt_page_find, bt_page_me;
    private TextView tv_page_find,tv_page_me;
    //private TextView tv_top_title;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();//初始化控件
        initEvents();//初始化事件
        manager = getSupportFragmentManager();//初始化fragment事务

        onClick(fl_page_find);//显示第一页fragment
    }


     //初始化控件函数
    private void initViews() {
        fl_page_find = (FrameLayout) findViewById(R.id.fl_page_find);
        fl_page_me = (FrameLayout) findViewById(R.id.fl_page_me);
        //底部的按钮
        bt_page_find = (Button) findViewById(R.id.bt_page_find);
        bt_page_me = (Button) findViewById(R.id.bt_page_me);
        //按钮对应文字
        tv_page_find = (TextView) findViewById(R.id.tv_page_find);
        tv_page_me = (TextView) findViewById(R.id.tv_page_me);
    }


     //初始化事件
    private void initEvents() {
        fl_page_find.setOnClickListener(this);
        fl_page_me.setOnClickListener(this);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
    }

    @Override
    public void onClick(View v) {
        resetImgAndTextColorAndButton();//重置导航栏元素
        transaction = manager.beginTransaction();//初始化fragment管理者
        hideFragment(transaction);//隐藏所有fragment
        switch (v.getId()) {
            case R.id.fl_page_find:
                //如果findfragment是null的话,就创建一个
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    //加入事务
                    transaction.add(R.id.fragment_container, findFragment);
                } else {
                    //如果findfragment不为空就显示出来
                    transaction.show(findFragment);
                }
                bt_page_find.setBackgroundResource(R.drawable.find_green);//并将按钮颜色点亮
                tv_page_find.setTextColor(Color.rgb(26,250,41));
                break;

            case R.id.fl_page_me:
                //如果mefragment是null的话,就创建一个
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    //加入事务
                    transaction.add(R.id.fragment_container, meFragment);
                } else {
                    //如果mefragment不为空就显示出来
                    transaction.show(meFragment);
                }
                bt_page_me.setBackgroundResource(R.drawable.me_green);
                tv_page_me.setTextColor(Color.rgb(26,250,41));
                break;
            default:
                break;
        }
        transaction.commit();//提交事务
    }

//隐藏所有fragment
    private void hideFragment(FragmentTransaction fragmentTransaction) {
        //如果此fragment不为空的话就隐藏起来
        if (findFragment != null) {
            fragmentTransaction.hide(findFragment);
        }
        if (meFragment != null) {
            fragmentTransaction.hide(meFragment);
        }


    }


            @Override
    protected void onRestart() {
        super.onRestart();

    }


    private void resetImgAndTextColorAndButton() {
        bt_page_find.setBackgroundResource(R.drawable.find);
        bt_page_me.setBackgroundResource(R.drawable.me);

        tv_page_find.setTextColor(Color.rgb(51, 51, 51));
        tv_page_me.setTextColor(Color.rgb(51, 51, 51));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息,2秒之内再按一次退出程序
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
//刷新Meframgment
    public void RefreshMeFragment(){
        transaction = manager.beginTransaction();
        transaction.remove(meFragment).commit();
        meFragment=new MeFragment();
        transaction.add(R.id.fragment_container, meFragment);
        transaction.show(meFragment);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
              super.onActivityResult(requestCode, resultCode, data);

              if (requestCode ==0&& resultCode==0) {
                  //Log.i("objectT", "kfjdk");

                       transaction = manager.beginTransaction();
                       transaction.remove(meFragment).commit();
                       meFragment=new MeFragment();
                       transaction.add(R.id.fragment_container, meFragment);
                       transaction.show(meFragment);

              }
        if (requestCode ==2&& resultCode==3) {
           // Log.i("objectT", "kfjdk");

            transaction = manager.beginTransaction();
            transaction.remove(findFragment).commit();
            findFragment=new FindFragment();
            transaction.add(R.id.fragment_container, findFragment);
            transaction.hide(findFragment);

        }
        if (requestCode ==1&& resultCode==1) {
            //Log.i("objectT", "kfjdk");

            transaction = manager.beginTransaction();
            transaction.remove(findFragment);
            transaction.remove(meFragment);
            transaction.commit();
            findFragment=new FindFragment();
            meFragment=new MeFragment();
            transaction.add(R.id.fragment_container, meFragment);
            transaction.add(R.id.fragment_container, findFragment);
            //transaction.hide(findFragment);
            transaction.hide(meFragment);

        }
        if (requestCode ==2&& resultCode==2) {
            //Log.i("objectT", "kfjdk");

            transaction = manager.beginTransaction();
            transaction.remove(findFragment);
            transaction.remove(meFragment);
            transaction.commit();
            findFragment=new FindFragment();
            meFragment=new MeFragment();
            transaction.add(R.id.fragment_container, findFragment);
            transaction.add(R.id.fragment_container, meFragment);
            transaction.hide(findFragment);
            //transaction.hide(meFragment);
        }

          }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
