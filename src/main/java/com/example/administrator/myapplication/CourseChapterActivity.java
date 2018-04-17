package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/3/5.
 */
public class CourseChapterActivity extends Activity {
    private TextView chapter_name,introduction;
    private ListView learn_data;
    private ImageView back,more;
    private JSONObject object;
    private Handler handler;
    private String position,type,chapter_id;
    ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_chapter);
        chapter_name=(TextView)findViewById(R.id.textView3);
        introduction=(TextView)findViewById(R.id.textView2);
        back=(ImageView)findViewById(R.id.imageView);
        more=(ImageView)findViewById(R.id.imageView2);
        learn_data=(ListView)findViewById(R.id.listView1);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        chapter_name.setText(bundle.getString("chapter_name"));
        position=bundle.getString("position");
        type=bundle.getString("type");
        try {
            object = new JSONObject();
            object.put("chapter_name",bundle.getString("chapter_name"));
            object.put("course_name",bundle.getString("course_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        learn_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Bundle bundle = new Bundle();
                TextView learn_data_name = (TextView) arg1.findViewById(R.id.textView3);
                String learn_data_name1 = learn_data_name.getText().toString().substring(learn_data_name.getText().toString().lastIndexOf(".") + 1);
                if (learn_data_name1.equals("mp4") || learn_data_name1.equals("flv") || learn_data_name1.equals("rmvb")
                        || learn_data_name1.equals("avi") || learn_data_name1.equals("wmv") || learn_data_name1.equals("mkv")
                        || learn_data_name1.equals("mpg") || learn_data_name1.equals("rm")) {
                    bundle.putString("learn_data_name", learn_data_name.getText().toString());
                    Intent intent = new Intent(CourseChapterActivity.this, VideoPaly.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if(fileIsExists(learn_data_name.getText().toString())){
                        File file=new File(Environment.getExternalStorageDirectory()+"/我爱学习/download/"+learn_data_name.getText().toString());
                        Intent intent = new Intent("android.intent.action.VIEW");
                        Uri path = Uri.fromFile(file);
                        intent.setDataAndType(path, "application/msword");
                        startActivity(intent);
                    }
                    else
                        showNormalDialog(learn_data_name.getText().toString());

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseChapterActivity.this.finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮就创建并显示一个popupMenu
                final SharedPreferences pref = CourseChapterActivity.this.getSharedPreferences("data",0);
                if(type.equals("2")&&pref.getString("identity","").equals("教师"))
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
    }

    //初始化listview的函数
    public void intialization(String mark){
        //Log.i("objectT","hhhh ");
        //用于接收多个课程的数据
        final String introduction1;

        try {

            URL url = new URL(this.getString(R.string.link)+"/CourseChapterServlet");//设置连接的url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();
            //传送标记给服务器
            object.put("mark", mark);
            out.write(URLEncoder.encode(object.toString(), "UTF-8").getBytes());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {//返回正确

                //获取服务器上的数据
                BufferedReader inn = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(inn.readLine(), "UTF-8");
                    //通过JSONObject保存获取的数据
                    final JSONObject objectT = new JSONObject(jsonStr);
                    if(objectT.getString("ifsuccess").equals("true")){//判断服务器返回的值是否正确
                        try{
                            if(mark.equals("chushihua")) {
                            introduction1=objectT.getString("introduction");
                            chapter_id=objectT.getString("chapter_id");
                            for (int i = 1; i <=Integer.parseInt(objectT.getString("learn_data_row")); i++) {//判断有几个课程
                                HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
                                String learn_data_name = objectT.getString("learn_data_name" + i);
                                String houzhuiming = learn_data_name.substring(learn_data_name.lastIndexOf(".") + 1);
                                if (houzhuiming.equals("doc") || houzhuiming.equals("docx")) {
                                    tempHashMap.put("imageView", R.drawable.word);
                                } else if (houzhuiming.equals("xls") || houzhuiming.equals("xlsx")) {
                                    tempHashMap.put("imageView", R.drawable.excel);
                                } else if (houzhuiming.equals("ppt") || houzhuiming.equals("pptx")) {
                                    tempHashMap.put("imageView", R.drawable.ppt);
                                } else if (houzhuiming.equals("txt")) {
                                    tempHashMap.put("imageView", R.drawable.txt);
                                } else if (houzhuiming.equals("pdf")) {
                                    tempHashMap.put("imageView", R.drawable.pdf);
                                } else {
                                    tempHashMap.put("imageView", R.drawable.video);
                                }

                                //Log.i("objectT", objectT.getString("cover"+i));
                                //用于接收一个课程的数据
                                tempHashMap.put("textView1", objectT.getString("learn_data_name" + i));
                                tempHashMap.put("textView2", objectT.getString("create_time" + i).substring(0, 10));
                                arrayList.add(tempHashMap);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        introduction.setText(introduction1);
                                        SimpleAdapter adapter = new SimpleAdapter(CourseChapterActivity.this, arrayList, R.layout.course_chapter_list,
                                                new String[]{"imageView", "textView1", "textView2"},
                                                new int[]{R.id.imageView, R.id.textView3, R.id.textView2,});
                                        learn_data.setAdapter(adapter);
                                    }
                                });

                            }
                            if(mark.equals("update_introduction")) {
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
                            if(mark.equals("delete_chapter")){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CourseChapterActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CourseChapterActivity.this, CourseMainActivity.class);
                                        intent.putExtra("position", Integer.parseInt(position));
                                        setResult(2, intent);
                                        finish();
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    }// intialization()函数结束

    //判断文件是否存在函数
    public boolean fileIsExists(String filename){
        try{
            File f=new File(Environment.getExternalStorageDirectory()+"/我爱学习/download/"+filename);
            if(!f.exists()){
                return false;
            }

        }catch (Exception e) {

            return false;
        }
        return true;
    }
    //确认下载的提示函数
    private void showNormalDialog(final String filename){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CourseChapterActivity.this);

        //normalDialog.setTitle("提示");
        normalDialog.setMessage("确定下载这个文件吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String temp1 = URLEncoder.encode(filename, "utf-8");
                            String temp2 = temp1.replaceAll("\\+", "%20");
                            String uri = CourseChapterActivity.this.getString(R.string.link)+"/file/" + temp2;
                            //创建下载任务,downloadUrl就是下载链接
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
                            //指定下载路径和下载文件名
                            //request.setDestinationInExternalPublicDir("/download/", fileName);下到SD卡中
                            request.setDestinationInExternalPublicDir("我爱学习/download/",filename);
                            //获取下载管理器
                            DownloadManager downloadManager= (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                            //将下载任务加入下载队列，否则不会进行下载
                            downloadManager.enqueue(request);
                            Toast.makeText(CourseChapterActivity.this, "调用系统下载器开始下载!", Toast.LENGTH_LONG).show();
                            long Id = downloadManager.enqueue(request);
                            listener(Id);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
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

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CourseChapterActivity.this);

        //normalDialog.setTitle("提示");
        normalDialog.setMessage("确定删除该章节吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                intialization("delete_chapter");
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
    private BroadcastReceiver broadcastReceiver;
    //监听下载完成的函数
    private void listener(final long Id) {

        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(CourseChapterActivity.this, "下载完成!", Toast.LENGTH_LONG).show();
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void showPopmenu(View view){
        PopupMenu popupMenu = new PopupMenu(CourseChapterActivity.this,view);
            popupMenu.getMenuInflater().inflate(R.menu.chapter_headmenu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.update_introduction:
                                showInputDialog();
                        break;
                    case R.id.upload_data:
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("chapter_id",chapter_id);

                        Intent intent = new Intent(CourseChapterActivity.this, UploadLearnDataActivity.class);
                        intent.putExtras(bundle1);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.delete_chapter:
                        showNormalDialog();
                        break;
                    case R.id.delete_learn_data:
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("chapter_id",chapter_id);
                        Intent intent1 = new Intent(CourseChapterActivity.this, DeleteLearnDataActivity.class);
                        intent1.putExtras(bundle2);
                        startActivityForResult(intent1, 0);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showInputDialog() {
    /*@setView 装入一个EditView
     */
        final EditText editText = new EditText(CourseChapterActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(CourseChapterActivity.this);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==0&& resultCode==2) {
            arrayList.clear();
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