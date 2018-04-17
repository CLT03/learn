package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
public class HomeworkSubmitListActivity extends Activity {
    private ImageView back;
    private ListView mListView;
    private TextView mTextView;
    private JSONObject object;
    private Handler handler;
    private ArrayList<String>  user_id= new ArrayList<String> ();
    private ArrayList<String>  profession = new ArrayList<String> ();
    private ArrayList<String>  username = new ArrayList<String> ();
    private String homework_id,homework_name,information,submit_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_submit_list);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back=(ImageView)findViewById(R.id.imageView);
        mTextView=(TextView)findViewById(R.id.textView);
        mListView=(ListView)findViewById(R.id.listView);
        homework_id=bundle.getString("homework_id");
        homework_name=bundle.getString("homework_name");
        information=bundle.getString("information");
        submit_number=bundle.getString("submit_number");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeworkSubmitListActivity.this.finish();
            }
        });

        try {
            object = new JSONObject();
            object.put("homework_id",bundle.getString("homework_id"));
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

            URL url = new URL(this.getString(R.string.link)+"/HomeworkSubmitListServlet");
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
                            list.add(objectT.getString("user_id"+i)+"-"+objectT.getString("profession"+i)+"-"+objectT.getString("username"+i));
                            user_id.add(objectT.getString("user_id" + i));
                            username.add(objectT.getString("username"+i));
                            profession.add(objectT.getString("profession" + i));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText("作业提交人数："+submit_number);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        HomeworkSubmitListActivity.this, android.R.layout.simple_list_item_1, list);
                                mListView.setAdapter(adapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                                        Bundle bundle1 = new Bundle();
                                        TextView user=(TextView)arg1;
                                        bundle1.putString("homework_name", homework_name);
                                        bundle1.putString("homework_id",homework_id);
                                        bundle1.putString("information",information);
                                        bundle1.putString("user_id",user_id.get(arg2));
                                        bundle1.putString("user",user.getText().toString());
                                        Intent intent = new Intent(HomeworkSubmitListActivity.this, CorrectHomeworkActivity.class);
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
