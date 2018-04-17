package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/15.
 */
public class CourseMainActivity extends Activity {
    private TextView course_name,introduction;
    private ListView chapter,comment;
    private ImageView back,more;
    private JSONObject object;
    private Handler handler;
    private CourseMainCommentAdapter adapter = null;
    private String type,course_name_,teacher=null;
    private ArrayList<String> chapter_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_main);
        course_name=(TextView)findViewById(R.id.textView1);
        introduction=(TextView)findViewById(R.id.textView2);
        back=(ImageView)findViewById(R.id.imageView);
        more=(ImageView)findViewById(R.id.imageView2);
        chapter=(ListView)findViewById(R.id.listView1);
        comment=(ListView)findViewById(R.id.listView2);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        final SharedPreferences pref = CourseMainActivity.this.getSharedPreferences("data",0);
        type=bundle.getString("type");
        course_name_=bundle.getString("course_name");
        teacher=bundle.getString("teacher");
        course_name.setText(bundle.getString("course_name"));
        try {
            object = new JSONObject();
            object.put("course_name",bundle.getString("course_name"));
            object.put("teacher",bundle.getString("teacher"));
            object.put("user_id",pref.getString("user_id",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮就创建并显示一个popupMenu
                showPopmenu(more);
            }
        });
        new Thread() {

            @Override
            public void run() {

                super.run();
                intialization("chushihua");
            }
        }.start();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseMainActivity.this.finish();
            }
        });

        chapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Bundle bundle1 = new Bundle();
                TextView chapter_name = (TextView)arg1;
                bundle1.putString("type",type);
                bundle1.putString("position",Integer.toString(arg2));
                bundle1.putString("course_name",bundle.getString("course_name"));
                bundle1.putString("chapter_name",chapter_name.getText().toString());
                Intent intent = new Intent(CourseMainActivity.this, CourseChapterActivity.class);
                intent.putExtras(bundle1);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void intialization(String mark){
        final String introduction1;
        final List<CourseMainCommentItem> comment_list = new ArrayList<>();

        try {

            URL url = new URL(this.getString(R.string.link)+"/CourseMainServlet");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();

            //Log.i("objectT", object.toString());
            object.put("mark", mark);
            out.write(URLEncoder.encode(object.toString(), "UTF-8").getBytes());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //获取服务器上的数据
                BufferedReader inn = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(inn.readLine(), "UTF-8");
                    final JSONObject objectT = new JSONObject(jsonStr);
                   // Log.i("objectT", objectT.getString("ifsuccess"));
                    if(objectT.getString("ifsuccess").equals("true")){
                        if(mark.equals("chushihua")) {
                            introduction1 = objectT.getString("introduction");
                            //Log.i("objectT", introduction1);
                            for (int i = 1; i <= Integer.parseInt(objectT.getString("chapter_row")); i++) {
                                chapter_list.add(objectT.getString("chapter_name" + i));
                            }
                            for (int i = 1; i <= Integer.parseInt(objectT.getString("comment_row")); i++) {//判断有几个课程
                                //获取用户的头像，要新开启一个连接
                                Bitmap bitmap = null;
                                try {
                                    //解决URI有空格Tomcat识别不了的问题
                                    String temp1 = URLEncoder.encode(objectT.getString("avatar" + i), "utf-8");
                                    String temp2 = temp1.replaceAll("\\+", "%20");
                                    //形成封面图片的url
                                    URL httpUrl = new URL(this.getString(R.string.link) + "/file/" + temp2);

                                    HttpURLConnection conn1 = (HttpURLConnection) httpUrl.openConnection();
                                    conn1.setConnectTimeout(15000);
                                    conn1.setDoInput(true);
                                    conn1.setUseCaches(false);

                                    InputStream in = conn1.getInputStream();

                                    bitmap = BitmapFactory.decodeStream(in);
                                    in.close();
                                    conn1.disconnect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //转换圆形头像
                                RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                circleDrawable.getPaint().setAntiAlias(true);
                                circleDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()));

                                comment_list.add(new CourseMainCommentItem(circleDrawable, objectT.getString("username" + i), objectT.getString("information" + i)
                                        , objectT.getString("create_time" + i).substring(0, 10), Float.parseFloat(objectT.getString("score" + i))));

                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    introduction.setText(introduction1);
                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                                            CourseMainActivity.this, android.R.layout.simple_list_item_1, chapter_list);
                                    chapter.setAdapter(adapter1);
                                    adapter = new CourseMainCommentAdapter(comment_list, CourseMainActivity.this);


                                    //设置listview的adapter
                                    comment.setAdapter(adapter);
                                }
                            });
                        }
                        if(mark.equals("join_course")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                            Toast.makeText(CourseMainActivity.this, "加入成功！", Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(CourseMainActivity.this, MainActivity.class);
                              setResult(1, intent);

                                }
                            });
                           }
                        if(mark.equals("delete_course")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CourseMainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CourseMainActivity.this, MainActivity.class);
                                    setResult(2, intent);
                                    finish();
                                }
                            });
                        }
                        if(mark.equals("quit_course")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CourseMainActivity.this, "退出成功！", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CourseMainActivity.this, MainActivity.class);
                                    setResult(2, intent);
                                    finish();
                                }
                            });
                        }
                        if(mark.equals("update_introduction")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    introduction.setText(objectT.getString("introduction"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                }
                            });
                        }
                        if(mark.equals("new_chapter")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        chapter_list.add(objectT.getString("chapter_name"));
                                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                                                CourseMainActivity.this, android.R.layout.simple_list_item_1, chapter_list);
                                        chapter.setAdapter(adapter1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                inn.close();
                conn.disconnect();
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showPopmenu(View view){
        PopupMenu popupMenu = new PopupMenu(CourseMainActivity.this,view);
        final SharedPreferences pref = CourseMainActivity.this.getSharedPreferences("data",0);
        if(type.equals("1")&&pref.getString("identity","").equals("学生"))
           popupMenu.getMenuInflater().inflate(R.menu.course_main_headmenu1,popupMenu.getMenu());
        if(type.equals("2")&&pref.getString("identity","").equals("学生"))
            popupMenu.getMenuInflater().inflate(R.menu.course_main_headmenu2,popupMenu.getMenu());
        if(type.equals("2")&&pref.getString("identity","").equals("教师"))
            popupMenu.getMenuInflater().inflate(R.menu.course_main_headmenu3,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.join_course:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                intialization("join_course");
                            }
                        }.start();
                        break;
                    case R.id.delete_course:
                        showNormalDialog("delete_course");
                        break;
                    case R.id.quit_course:
                        showNormalDialog("quit_course");
                        break;

                    case R.id.arrange_homework:
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id",pref.getString("user_id",""));
                        bundle.putString("course_name",course_name_);
                        Intent intent = new Intent(CourseMainActivity.this,ArrangeHomeworkActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        break;
                    case R.id.look_homework:
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("identity",pref.getString("identity",""));
                        bundle1.putString("user_id",pref.getString("user_id",""));
                        bundle1.putString("course_name",course_name_);
                        bundle1.putString("teacher",teacher);
                        Intent intent1 = new Intent(CourseMainActivity.this,HomeworkListActivity.class);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);

                        break;
                    case R.id.upadte_introduction:
                         showInputDialog("update_introduction");
                        break;
                    case R.id.new_chapter:
                        showInputDialog("new_chapter");
                        break;
                    case R.id.update_cover:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("course_name",course_name_);
                        bundle2.putString("teacher",teacher);
                        bundle2.putString("mark_","cover");
                        Intent intent2 = new Intent(CourseMainActivity.this,UpdateCoverAvatarActivity.class);
                        intent2.putExtras(bundle2);
                        CourseMainActivity.this.startActivityForResult(intent2, 0);
                        break;
                    case R.id.new_comment:
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("user_id",pref.getString("user_id",""));
                        bundle3.putString("course_name",course_name_);
                        bundle3.putString("teacher",teacher);
                        Intent intent3 = new Intent(CourseMainActivity.this,NewCommentActivity.class);
                        intent3.putExtras(bundle3);
                        CourseMainActivity.this.startActivityForResult(intent3, 0);
                        break;
                    case R.id.look_student:
                        Bundle bundle4 = new Bundle();
                        bundle4.putString("course_name",course_name_);
                        bundle4.putString("teacher",teacher);
                        Intent intent4 = new Intent(CourseMainActivity.this,StudentListActivity.class);
                        intent4.putExtras(bundle4);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showInputDialog(String mark_) {
    /*@setView 装入一个EditView
     */
        final EditText editText = new EditText(CourseMainActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(CourseMainActivity.this);
        if(mark_.equals("update_introduction")) {
            editText.setText(introduction.getText());
            inputDialog.setTitle("请输入新的简介：").setView(editText);
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                object.put("introduction", editText.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    intialization("update_introduction");
                                }
                            }.start();

                        }
                    }).show();
        }
        if(mark_.equals("new_chapter")) {
            inputDialog.setTitle("请输入章节名字：").setView(editText);
            inputDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                object.put("chapter_name", editText.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    intialization("new_chapter");
                                }
                            }.start();

                        }
                    }).show();
        }

    }

    private void showNormalDialog(final String mark){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CourseMainActivity.this);
        if(mark.equals("delete_course"))
        //normalDialog.setTitle("提示");
        normalDialog.setMessage("确定删除该课程吗?");
        else
            normalDialog.setMessage("确定退出该课程吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                intialization(mark);
                            }
                        }.start();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==1&& resultCode==2) {
            Log.i("objectT", "fff");
            int postion = data.getIntExtra("position", 0);
            chapter_list.remove(postion);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                    CourseMainActivity.this, android.R.layout.simple_list_item_1, chapter_list);
            chapter.setAdapter(adapter1);
        }
        if (requestCode ==0&& resultCode==3) {
            Intent intent = new Intent(CourseMainActivity.this, MainActivity.class);
            setResult(3, intent);
        }
        if (requestCode ==0&& resultCode==0) {
            chapter_list.clear();
            new Thread() {

                @Override
                public void run() {

                    super.run();
                    intialization("chushihua");
                }
            }.start();
        }
    }

}



