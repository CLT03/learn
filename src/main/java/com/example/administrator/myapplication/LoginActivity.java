package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * Created by Administrator on 2018/2/26.
 */
public class LoginActivity extends Activity{
    private EditText username;
    private EditText password;
    private Button login;
    private JSONObject object;
    private Handler handler;
    private ImageView back;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    username = (EditText)findViewById(R.id.username);
    password = (EditText)findViewById(R.id.password);
    back=(ImageView)findViewById(R.id.imageView);
    login =(Button)findViewById(R.id.login);
     handler=new Handler();
     final Intent intent=getIntent();
    final Bundle bundle=intent.getExtras();
         back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 LoginActivity.this.finish();
             }
         });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    object = new JSONObject();
                    object.put("username", username.getText().toString());
                    object.put("password", password.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {

                        super.run();
                        login();
                    }
                }.start();
            }
        });

    }

    public void login(){
        try {

        URL url = new URL(this.getString(R.string.link)+"/LoginServlet");
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
                if(objectT.getString("ifpass").equals("true")){
                    SharedPreferences pref =LoginActivity.this.getSharedPreferences("data",0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user_id",username.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    setResult(0, intent);
                     finish();
                }else{

                    handler.post(new Runnable() {
                    @Override
                    public void run() {


                            Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();

                    }
                });
                    //
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