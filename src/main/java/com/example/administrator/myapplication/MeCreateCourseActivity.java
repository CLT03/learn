package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class MeCreateCourseActivity extends Activity {
    private EditText mEditText1,mEditText2;
    private Button mButton1,mButton2;
    private ImageView mImageView,back;
    private JSONObject object;
    private Handler handler;
   // private String result;
    private String path="";//封面文件路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_create_course);
        mEditText1=(EditText)findViewById(R.id.editText1);
        mEditText2=(EditText)findViewById(R.id.editText2);
        mButton1=(Button)findViewById(R.id.button1);
        mButton2=(Button)findViewById(R.id.button2);
        mImageView=(ImageView)findViewById(R.id.imageView1);
        back=(ImageView)findViewById(R.id.imageView);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeCreateCourseActivity.this.finish();
            }
        });
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                //intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEditText1.getText().toString().equals("")) {
                    try {
                        object = new JSONObject();
                        object.put("course_name", mEditText1.getText().toString());
                        object.put("introduction", mEditText2.getText().toString());
                        object.put("teacher", bundle.getString("teacher"));
                        object.put("department", bundle.getString("department"));
                        if(!path.equals(""))
                        object.put("cover",path.substring(path.lastIndexOf("/") + 1));
                        else object.put("cover","me_green.png");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();
                            create_course();

                        }
                    }.start();
                }else Toast.makeText(MeCreateCourseActivity.this, "课程名不能为空！", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //选择文件函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
           // Log.i("objectT", uri.toString());
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
               // Log.i("objectT",path);
                mImageView.setImageURI(uri);
                //tv.setText(path);
                //Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void create_course(){

        try {

            URL url = new URL(this.getString(R.string.link)+"/CreateCourseServlet");
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
                    //Log.i("objectT", path);
                    if(!path.equals("")){
                    uploadFile();}
                    if(objectT.getString("ifsuccess").equals("true")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MeCreateCourseActivity.this, "课程创建成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MeCreateCourseActivity.this, MainActivity.class);
                                setResult(2, intent);
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
           // result = br.readLine();


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
