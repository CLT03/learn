package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
 * Created by Administrator on 2018/3/16.
 */
public class UploadLearnDataActivity extends Activity {
    private Button mButton1, mButton2;
    private TextView mTextView,mTextView2;
    private ProgressBar mProgressBar;
    private ImageView back;
    private JSONObject object;
    private Handler handler;
    private String path,chapter_id;//文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_learn_data);
        mButton1=(Button)findViewById(R.id.button1);
        mButton2=(Button)findViewById(R.id.button2);
        mTextView=(TextView)findViewById(R.id.textView);
        mTextView2=(TextView)findViewById(R.id.textView2);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        back=(ImageView)findViewById(R.id.imageView);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        chapter_id=bundle.getString("chapter_id");
        path="";
        Log.i("objectT",chapter_id);
        handler = new Handler();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadLearnDataActivity.this.finish();
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
       final String uri=this.getString(R.string.link) + "/UploadServlet";
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!path.equals("")) {
                    try {
                        object = new JSONObject();
                        object.put("chapter_id",chapter_id);

                        object.put("learn_data_name", path.substring(path.lastIndexOf("/") + 1));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MyTask mTask = new MyTask();
                    mTask.execute(path,uri);
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();
                            upload();

                        }
                    }.start();
                } else
                    Toast.makeText(UploadLearnDataActivity.this, "您还没选文件呢！", Toast.LENGTH_SHORT).show();

            }
        });


    }

    //选择文件函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            // Log.i("objectT", uri.toString());
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                 Log.i("objectT", path);
                //mImageView.setImageURI(uri);
                mTextView.setText(path);
                //Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    public void upload() {

        try {

            URL url = new URL(this.getString(R.string.link) + "/UploadLearnDataServlet");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();

            Log.i("objectT", object.toString());

            out.write(URLEncoder.encode(object.toString(), "UTF-8").getBytes());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //获取服务器上的数据
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(in.readLine(), "UTF-8");
                    final JSONObject objectT = new JSONObject(jsonStr);
                    //Log.i("objectT", path);



                    if (objectT.getString("ifsuccess").equals("true")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(UploadLearnDataActivity.this, CourseChapterActivity.class);
                                intent.putExtra("learn_data_name",path.substring(path.lastIndexOf("/") + 1));
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


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
/*public abstract class AsyncTask<Params, Progress, Result>
Params：doInBackground方法的参数类型；
Progress：AsyncTask所执行的后台任务的进度类型；
Result：后台任务的返回结果类型。
onPreExecute() //此方法会在后台任务执行前被调用，用于进行一些准备工作
doInBackground(Params… params) //此方法中定义要执行的后台任务，在这个方法中可以调用publishProgress来更新任务进度（publishProgress内部会调用onProgressUpdate方法）
onProgressUpdate(Progress… values) //由publishProgress内部调用，表示任务进度更新
onPostExecute(Result result) //后台任务执行完毕后，此方法会被调用，参数即为后台任务的返回结果
onCancelled() //此方法会在后台任务被取消时被调用
以上方法中，除了doInBackground方法由AsyncTask内部线程池执行外，其余方法均在主线程中执行。
 */
    private class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String result) {
            //最终结果的显示
            //Toast.makeText(UploadLearnDataActivity.this, result, Toast.LENGTH_SHORT).show();
            mTextView2.setText(result);

        }

        @Override
        protected void onPreExecute() {
            //开始前的准备工作
            mTextView2.setText("loading...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //显示进度
            mProgressBar.setProgress(values[0]);
            mTextView2.setText("loading..." + values[0] + "%");
            if(values[0]==100)
            mTextView2.setText("等待服务器写入硬盘，时间可能会很久！");
        }

        @Override
        protected String doInBackground(String... params) {
            //这里params[0]和params[1]是execute传入的两个参数
            String filePath = params[0];
            String uploadUrl = params[1];
            //下面即手机端上传文件的代码
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";
            try {
                URL url = new URL(uploadUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url
                        .openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(6*1000);
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(httpURLConnection
                        .getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos
                        .writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                                + encode(filePath.substring(filePath.lastIndexOf("/") + 1))
                                + "\"" + end);
                dos.writeBytes(end);

                //获取文件总大小
                FileInputStream fis = new FileInputStream(filePath);
                long total = fis.available();//获取文件的长度（字节数）
                byte[] buffer = new byte[8192]; // 8k
                int count = 0;
                int length = 0;
                while ((count = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, count);
                    //获取进度，调用publishProgress()
                    length += count;
                    publishProgress((int) ((length / (float) total) * 100));
                    //这里是测试时为了演示进度,休眠500毫秒，正常应去掉
                   // Thread.sleep(500);
                }
                fis.close();
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();

                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String result = br.readLine();
                dos.close();
                is.close();
                return result;
            }catch (Exception e) {
                e.printStackTrace();
                return "上传失败";
            }
        }
    }

    private String encode(String value) throws Exception{
        return URLEncoder.encode(value, "utf-8");
    }

}
