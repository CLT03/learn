package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * Created by Administrator on 2018/3/13.
 */
public class CorrectHomeworkActivity extends Activity {
    private TextView mTextView1,mTextView2,mTextView3;
    private Button mButton1;
    private EditText mEditText1,mEditText2;
    private ImageView back;
    private JSONObject object;
    private Handler handler;
    private String user;//文件路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.correct_homework);
        mTextView1=(TextView)findViewById(R.id.textView1);
        mTextView2=(TextView)findViewById(R.id.textView2);
        mTextView3=(TextView)findViewById(R.id.textView3);
        back=(ImageView)findViewById(R.id.imageView);
        mButton1=(Button)findViewById(R.id.button);
        mEditText1=(EditText)findViewById(R.id.editText1);
        mEditText2=(EditText)findViewById(R.id.editText2);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        //mTextView1.setText(bundle.getString("user"));
        user=bundle.getString("user");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CorrectHomeworkActivity.this.finish();
            }
        });
        mTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileIsExists(mTextView3.getText().toString())) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/我爱学习/download/" + mTextView3.getText().toString());
                    Intent intent = new Intent("android.intent.action.VIEW");
                    Uri path = Uri.fromFile(file);
                    intent.setDataAndType(path, "application/msword");
                    startActivity(intent);
                } else
                    showNormalDialog(mTextView3.getText().toString());

            }


        });
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditText1.getText().toString().equals("")) {
                    try {

                        object = new JSONObject();
                        object.put("homework_id",bundle.getString("homework_id"));
                        object.put("user_id",bundle.getString("user_id"));
                        object.put("score",mEditText1.getText().toString());
                        object.put("teacher_comment",mEditText2.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();
                            intialization("correcthomework");

                        }
                    }.start();
                }else Toast.makeText(CorrectHomeworkActivity.this, "评分不能为空！", Toast.LENGTH_SHORT).show();

            }
        });
        try {
            object = new JSONObject();
            object.put("homework_id",bundle.getString("homework_id"));
            object.put("user_id",bundle.getString("user_id",""));
            //object.put("message",mEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread() {

            @Override
            public void run() {

                super.run();

                intialization("chushihua");
            }
        }.start();


    }



    public void intialization(String mark){
        try {

            URL url = new URL(this.getString(R.string.link)+"/CorrectHomeworkServlet");
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
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(in.readLine(), "UTF-8");
                    final JSONObject objectT = new JSONObject(jsonStr);
                    //Log.i("objectT", objectT.getString("ifpass"));
                    if(objectT.getString("ifsuccess").equals("true")){
                        if(mark.equals("chushihua")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mTextView1.setText(user);
                                        mEditText2.setText(objectT.getString("teacher_comment"));
                                        mTextView2.setText(objectT.getString("message"));
                                        mTextView3.setText(objectT.getString("file"));
                                        if(objectT.getString("score").equals("0"))
                                            mEditText1.setText("");
                                        else
                                            mEditText1.setText(objectT.getString("score"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                        if(mark.equals("correcthomework")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CorrectHomeworkActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                in.close();
                conn.disconnect();
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

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
                new AlertDialog.Builder(CorrectHomeworkActivity.this);

        //normalDialog.setTitle("提示");
        normalDialog.setMessage("确定下载这个文件吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String temp1 = URLEncoder.encode(filename, "utf-8");
                            String temp2 = temp1.replaceAll("\\+", "%20");
                            String uri = CorrectHomeworkActivity.this.getString(R.string.link)+"/file/" + temp2;
                            //创建下载任务,downloadUrl就是下载链接
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri));
                            //指定下载路径和下载文件名
                            //request.setDestinationInExternalPublicDir("/download/", fileName);下到SD卡中
                            request.setDestinationInExternalPublicDir("我爱学习/download/",filename);
                            //获取下载管理器
                            DownloadManager downloadManager= (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                            //将下载任务加入下载队列，否则不会进行下载
                            downloadManager.enqueue(request);
                            Toast.makeText(CorrectHomeworkActivity.this, "调用系统下载器开始下载!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CorrectHomeworkActivity.this, "下载完成!", Toast.LENGTH_LONG).show();
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }



}
