package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2018/3/13.
 */
public class SubmitHomeworkActivity extends Activity {
    private TextView mTextView1,mTextView2,mTextView3,mTextView4,mTextView5;
    private Button mButton1,mButton2;
    private EditText mEditText;
    private ImageView back;
    private JSONObject object;
    private Handler handler;
    private String path="";//文件路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_homework);
        mTextView1=(TextView)findViewById(R.id.textView1);
        mTextView2=(TextView)findViewById(R.id.textView2);
        mTextView3=(TextView)findViewById(R.id.textView3);
        mTextView4=(TextView)findViewById(R.id.textView4);
        mTextView5=(TextView)findViewById(R.id.textView5);
        mButton1=(Button)findViewById(R.id.button1);
        mButton2=(Button)findViewById(R.id.button2);
        back=(ImageView)findViewById(R.id.imageView);
        mEditText=(EditText)findViewById(R.id.editText);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        mTextView1.setText(bundle.getString("homework_name"));
        mTextView2.setText(bundle.getString("information"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitHomeworkActivity.this.finish();
            }
        });
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("image/*");//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditText.getText().toString().equals("")) {
                    try {
                        SharedPreferences pref = SubmitHomeworkActivity.this.getSharedPreferences("data",0);
                        object = new JSONObject();
                        object.put("homework_id",bundle.getString("homework_id"));
                        object.put("user_id",pref.getString("user_id",""));
                        object.put("message",mEditText.getText().toString());
                        if(!path.equals(""))
                            object.put("file",path.substring(path.lastIndexOf("/") + 1));
                        else object.put("file","");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();
                            intialization("submithomework");

                        }
                    }.start();
                }else Toast.makeText(SubmitHomeworkActivity.this, "留言不能为空！", Toast.LENGTH_SHORT).show();

            }
        });
        try {
            SharedPreferences pref = SubmitHomeworkActivity.this.getSharedPreferences("data",0);
            object = new JSONObject();
            object.put("homework_id",bundle.getString("homework_id"));
            object.put("user_id",pref.getString("user_id",""));
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

    //选择文件函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            // Log.i("objectT", uri.toString());
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                // Log.i("objectT",path);
                //mImageView.setImageURI(uri);
                mTextView3.setText(path);
                //Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void intialization(String mark){
        try {

            URL url = new URL(this.getString(R.string.link)+"/SubmitHomeworkServlet");
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
                    if(!path.equals("")&&mark.equals("submithomework")){
                        uploadFile();}
                    if(objectT.getString("ifsuccess").equals("true")){
                        if(mark.equals("chushihua")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mEditText.setText(objectT.getString("message"));
                                        mTextView3.setText(objectT.getString("file"));
                                        if(objectT.getString("score").equals("0"))
                                        mTextView4.setText("");
                                        else
                                        mTextView4.setText(objectT.getString("score"));
                                        mTextView5.setText(objectT.getString("teacher_comment"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                        if(mark.equals("submithomework")){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SubmitHomeworkActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
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


    private void uploadFile()
    {

        String uploadUrl = this.getString(R.string.link)+"/UploadServlet";
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try
        {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection
                    .getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos
                    .writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                            +encode(path.substring(path.lastIndexOf("/") + 1))
                            + "\"" + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(path);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);

            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            //result = br.readLine();


            dos.close();
            is.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            setTitle(e.getMessage());
        }

    }

    private String encode(String value) throws Exception{
        return URLEncoder.encode(value, "utf-8");
    }
}
