package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */
public class HomeworkListActivity extends Activity {
    private ImageView back;
    private ListView mListView;
    private JSONObject object;
    private Handler handler;
    private ArrayList<String>  homework_id= new ArrayList<String> ();
    private ArrayList<String>  homework_name = new ArrayList<String> ();
    private ArrayList<String>  information = new ArrayList<String> ();
    private ArrayList<String>  submit_number= new ArrayList<String> ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_list);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back=(ImageView)findViewById(R.id.imageView);
        mListView=(ListView)findViewById(R.id.listView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               HomeworkListActivity.this.finish();
            }
        });

        try {
            object = new JSONObject();
            object.put("course_name",bundle.getString("course_name"));
            object.put("user_id",bundle.getString("user_id"));
            object.put("teacher",bundle.getString("teacher"));
            object.put("identity",bundle.getString("identity"));
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
        final List<String> list = new ArrayList<>();
        try {

            URL url = new URL(this.getString(R.string.link)+"/HomeworkListServlet");
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
                BufferedReader inn = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(inn.readLine(), "UTF-8");
                    final JSONObject objectT = new JSONObject(jsonStr);
                    //Log.i("objectT", objectT.getString("ifsuccess"));
                    if(objectT.getString("ifsuccess").equals("true")){
                        for (int i = 1; i <=Integer.parseInt(objectT.getString("row")); i++) {//判断有几个课程
                            list.add(objectT.getString("homework_name"+i)+"--"+objectT.getString("create_time"+i).substring(0,10));
                            homework_id.add(objectT.getString("homework_id"+i));
                            homework_name.add(objectT.getString("homework_name"+i));
                            information.add(objectT.getString("information"+i));
                            submit_number.add(objectT.getString("submit_number"+i));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        HomeworkListActivity.this, android.R.layout.simple_list_item_1, list);
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                                        Bundle bundle1 = new Bundle();
                                        bundle1.putString("homework_name", homework_name.get(arg2));
                                        bundle1.putString("homework_id",homework_id.get(arg2));
                                        bundle1.putString("information",information.get(arg2));
                                        bundle1.putString("submit_number",submit_number.get(arg2));
                                        SharedPreferences pref = HomeworkListActivity.this.getSharedPreferences("data",0);
                                        Intent intent = new Intent(HomeworkListActivity.this, SubmitHomeworkActivity.class);
                                        if(pref.getString("identity","").equals("教师")){
                                        intent = new Intent(HomeworkListActivity.this, HomeworkSubmitListActivity.class);
                                        }
                                        intent.putExtras(bundle1);
                                        startActivity(intent);

                                    }
                                });

                            }
                        });
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
}
