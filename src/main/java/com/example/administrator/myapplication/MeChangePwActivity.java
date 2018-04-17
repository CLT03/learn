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
 * Created by Administrator on 2018/3/9.
 */
public class MeChangePwActivity extends Activity {
    private EditText oldpw,newpw1,newpw2;
    private Button submit;
    private JSONObject object;
    private Handler handler;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_change_pw);
        oldpw = (EditText)findViewById(R.id.password1);
        newpw1 = (EditText)findViewById(R.id.password2);
        newpw2 = (EditText)findViewById(R.id.password3);
        back=(ImageView)findViewById(R.id.imageView);
        submit =(Button)findViewById(R.id.submit);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MeChangePwActivity.this.finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newpw1.getText().toString().equals(newpw2.getText().toString())) {
                    Toast.makeText(MeChangePwActivity.this, "两次新密码不一样！", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        object = new JSONObject();
                        object.put("user_id", bundle.getString("user_id"));
                        object.put("oldpw", oldpw.getText().toString());
                        object.put("newpw", newpw1.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {
                        @Override
                        public void run() {

                            super.run();
                            changepw();
                        }
                    }.start();
                }
            }
        });
   }

    public void changepw(){
        try {

            URL url = new URL(this.getString(R.string.link)+"/MeChangePwServlet");
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                Toast.makeText(MeChangePwActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else{

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                Toast.makeText(MeChangePwActivity.this, "原密码错误！", Toast.LENGTH_SHORT).show();

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
