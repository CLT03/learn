package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/3/17.
 */
public class NewCommentActivity extends Activity {
    private Button mButton1;
    private EditText mEditText;
    private RatingBar mRatingBar;
    private ImageView back;
    private JSONObject object;
    private Handler handler;
    private float score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_commet);
        mButton1=(Button)findViewById(R.id.button);
        back=(ImageView)findViewById(R.id.imageView);
        mEditText=(EditText)findViewById(R.id.editText);
        mRatingBar=(RatingBar)findViewById(R.id.ratingBar);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCommentActivity.this.finish();
            }
        });
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // 该方法可以获取到 3个参数
            public void onRatingChanged(RatingBar ratingBar,
                            float rating, boolean paramBoolean) {
              score=rating;
            }
        });

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditText.getText().toString().equals("")) {
                    try {
                        object = new JSONObject();
                        object.put("information",mEditText.getText().toString());
                         object.put("user_id", bundle.getString("user_id"));
                         object.put("teacher", bundle.getString("teacher"));
                        object.put("course_name", bundle.getString("course_name"));
                        object.put("score",score);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();
                            intialization("comment");

                        }
                    }.start();
                }else Toast.makeText(NewCommentActivity.this, "评语不能为空哦！", Toast.LENGTH_SHORT).show();

            }
        });
        try {
            object = new JSONObject();
            object.put("user_id", bundle.getString("user_id"));
            object.put("teacher", bundle.getString("teacher"));
            object.put("course_name", bundle.getString("course_name"));
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

            URL url = new URL(this.getString(R.string.link)+"/NewCommentServlet");
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
                    //Log.i("objectT", path);

                    if(objectT.getString("ifsuccess").equals("true")){
                       if(mark.equals("comment"))
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewCommentActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(NewCommentActivity.this, CourseMainActivity.class);
                                setResult(0, intent);

                            }
                        });
                        if(mark.equals("chushihua"))
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mEditText.setText(objectT.getString("information"));

                                      
                                            mRatingBar.setRating(Float.parseFloat(objectT.getString("score")));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

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
        catch (Exception e) {
            e.printStackTrace();
        }


    }
}
