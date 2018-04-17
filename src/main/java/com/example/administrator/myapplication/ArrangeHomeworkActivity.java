package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

/**
 * Created by Administrator on 2018/3/12.
 */
public class ArrangeHomeworkActivity extends Activity {
   private EditText mEditText1,mEditText2;
   private Button mButton;
   private ImageView back;
    private JSONObject object;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrange_homework);
        mEditText1=(EditText)findViewById(R.id.editText1);
        mEditText2=(EditText)findViewById(R.id.editText2);
        mButton=(Button)findViewById(R.id.button2);
        back=(ImageView)findViewById(R.id.imageView);
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        handler=new Handler();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrangeHomeworkActivity.this.finish();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText1.getText().toString().equals("")||mEditText2.getText().toString().equals("")) {
                    Toast.makeText(ArrangeHomeworkActivity.this, "标题和内容不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                try {
                    object = new JSONObject();
                    object.put("user_id", bundle.getString("user_id"));
                    object.put("course_name", bundle.getString("course_name"));
                    object.put("homework_name",mEditText1.getText().toString());
                    object.put("information",mEditText2.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread() {

                    @Override
                    public void run() {

                        super.run();
                        arrangehomework();
                    }
                }.start();
                }
            }
        });
    }

    public void arrangehomework(){
        try {

            URL url = new URL(this.getString(R.string.link)+"/ArrangeHomeworkServlet");
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
                    //Log.i("objectT", objectT.getString("ifpass"));
                    if(objectT.getString("ifsuccess").equals("true")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ArrangeHomeworkActivity.this, "布置成功！", Toast.LENGTH_SHORT).show();
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
