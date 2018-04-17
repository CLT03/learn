package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
public class RegisterActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button tologin;
    private Button register;
    private RadioButton mRadioButton_m;
    private RadioButton mRadioButton_w;
    private JSONObject object;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        tologin =(Button)findViewById(R.id.tologin);
        register =(Button)findViewById(R.id.register);
        mRadioButton_m = (RadioButton)findViewById(R.id.radioButton_m);
        mRadioButton_w = (RadioButton)findViewById(R.id.radioButton_w);
        handler=new Handler();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    object = new JSONObject();
                    object.put("username",username.getText().toString());
                    object.put("password",password.getText().toString());
                    if(mRadioButton_m.isChecked()){
                        object.put("sex","男");
                    }
                    else{
                        object.put("sex","女");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {

                        super.run();
                        register();
                    }
                }.start();
            }
        });
        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent2);

            }
        });
    }
    public void register(){
        try {

            URL url = new URL(this.getString(R.string.link)+"/HelloApp/RegisterServlet");
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();

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
    @Override
    protected void onPause(){
        super.onPause();
        this.finish();

    }
}
