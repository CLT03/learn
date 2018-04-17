package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/18.
 */
public class StudentListActivity extends Activity{
    private TextView mTextView;
    private ListView mListView;
    private ImageView back;
    private JSONObject object;
    private Handler handler;
    private ArrayList<String> student_list = new ArrayList<>();
    private int student_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_list);
        mTextView=(TextView)findViewById(R.id.textView1);
        mListView=(ListView)findViewById(R.id.listView);
        back=(ImageView)findViewById(R.id.imageView);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentListActivity.this.finish();
            }
        });
        try {
            object = new JSONObject();
            object.put("course_name",bundle.getString("course_name"));
            object.put("teacher",bundle.getString("teacher"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {

                super.run();
                intialization();
            }
        }.start();

    }
    public void intialization(){
        try {

            URL url = new URL(this.getString(R.string.link)+"/StudentListServlet");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();

            //Log.i("objectT", object.toString());

            out.write(URLEncoder.encode(object.toString(), "UTF-8").getBytes());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //获取服务器上的数据
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(in.readLine(), "UTF-8");
                    final JSONObject objectT = new JSONObject(jsonStr);
                    //Log.i("objectT", objectT.getString("ifsuccess"));
                    if(objectT.getString("ifsuccess")=="true"){
                        student_number=Integer.parseInt(objectT.getString("row"));
                        for (int i = 1; i <= Integer.parseInt(objectT.getString("row")); i++) {
                            student_list.add(objectT.getString("user_id"+i)+"-"+objectT.getString("department"+i)+"-"+
                                    objectT.getString("profession"+i)+"-"+objectT.getString("username"+i)
                                            );
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("学生人数："+student_number);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        StudentListActivity.this, android.R.layout.simple_list_item_1, student_list);
                                mListView.setAdapter(adapter);


                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                in.close();
                conn.disconnect();
            }


        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
