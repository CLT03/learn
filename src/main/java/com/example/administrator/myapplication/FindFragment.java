package com.example.administrator.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chase on 2017/2/6.
 */

public class FindFragment extends Fragment {
    private JSONObject object;
    private Handler handler;
    private ListView listView;
    private AutoCompleteTextView auto;
    private String[] a;
    private ImageView mImageView;
    private FindListAdapter adapter = null;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        handler=new Handler();
        object = new JSONObject();
        View tab01 = inflater.inflate(R.layout.find, container, false);
        listView = (ListView) tab01.findViewById(R.id.listView);
        //读取文件
        write("");
        read();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, a);
        // AutoCompleteTextView
        auto = (AutoCompleteTextView)tab01.findViewById(R.id.autoCompleteTextView);
        auto.setAdapter(adapter);
        //右上角按键
        mImageView= (ImageView)tab01.findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮就创建并显示一个popupMenu
                showPopmenu(mImageView);
            }
        });
        //新建子线程连接网络获取listview数据并更新
        new Thread() {

            @Override
            public void run() {

                super.run();

                intialization("chushihua");
            }
        }.start();

        //软键盘搜索键响应函数
        auto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //判断搜索框文字在文件中存不存在，不存在就写入。
                    boolean ifwrite = true;
                    try {
                        for (int i = 0; i < a.length; i++) {
                            if (a[i].equals(auto.getText().toString())) {
                                ifwrite = false;
                            }
                        }
                        if (ifwrite) {
                            //把搜索框文字存入文件
                            write(auto.getText().toString() + " ");
                        }
                        object = new JSONObject();
                        object.put("value", auto.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Thread() {

                        @Override
                        public void run() {

                            super.run();

                            intialization("sousuo");
                        }
                    }.start();
                    return true;
                }
                return false;
             }
        });

     //返回视图
        return tab01;
    }

    private void showPopmenu(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.getMenuInflater().inflate(R.menu.find_headmenu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.time:
                        new Thread() {

                            @Override
                            public void run() {

                                super.run();

                                intialization("time");
                            }
                        }.start();
                        break;
                    case R.id.score:
                        new Thread() {

                            @Override
                            public void run() {

                                super.run();

                                intialization("score");
                            }
                        }.start();
                        break;
                    case R.id.number:
                        new Thread() {

                            @Override
                            public void run() {

                                super.run();

                                intialization("number");
                            }
                        }.start();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
    //新建子线程函数

//初始化listview的函数
    public void intialization(String mark){
        //Log.i("objectT","hhhh ");
     //用于接收多个课程的数据
        final List<FindListItem> list = new ArrayList<>();

        try {

            URL url = new URL(this.getString(R.string.link)+"/FindServlet");//设置连接的url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();
            //传送标记给服务器
            object.put("mark", mark);
            out.write(URLEncoder.encode(object.toString(), "UTF-8").getBytes());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {//返回正确

                //获取服务器上的数据
                BufferedReader inn = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                try {
                    //解码
                    String jsonStr = URLDecoder.decode(inn.readLine(), "UTF-8");
                    //通过JSONObject保存获取的数据
                    final JSONObject objectT = new JSONObject(jsonStr);
                    if(objectT.getString("ifsuccess").equals("true")){//判断服务器返回的值是否正确
                              try{
                                for (int i = 1; i <=Integer.parseInt(objectT.getString("row")); i++) {//判断有几个课程
                                    //获取课程的封面，要新开启一个连接
                                    Bitmap bitmap = null;
                                    try{
                                        //解决URI有空格Tomcat识别不了的问题
                                        String temp1= URLEncoder.encode(objectT.getString("cover" + i), "utf-8");
                                        String temp2=temp1.replaceAll("\\+", "%20");
                                        //形成封面图片的url
                                        URL httpUrl = new URL(this.getString(R.string.link)+"/file/"+temp2);

                                        HttpURLConnection conn1 = (HttpURLConnection) httpUrl.openConnection();
                                        conn1.setConnectTimeout(15000);
                                        conn1.setDoInput(true);
                                        conn1.setUseCaches(false);

                                        InputStream in = conn1.getInputStream();

                                        bitmap = BitmapFactory.decodeStream(in);
                                        in.close();
                                        conn1.disconnect();
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    list.add(new FindListItem(bitmap,objectT.getString("course_name"+i),objectT.getString("department"+i),
                                            objectT.getString("teacher"+i),"学生数："+objectT.getString("student_numbers"+i)
                                    ,objectT.getString("create_time"+i).substring(0,10),Float.parseFloat(objectT.getString("score" + i))));

                                }
                                  //异步更新listview的数据
                                  handler.post(new Runnable() {
                                      @Override
                                      public void run() {
                                          adapter = new FindListAdapter(list, getActivity());


                                          //设置listview的adapter
                                          listView.setAdapter(adapter);
                                          //设置listview的按键事件
                                          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                                  Bundle bundle = new Bundle();
                                                  TextView course_name = (TextView) arg1.findViewById(R.id.textView1);
                                                  TextView teacher = (TextView) arg1.findViewById(R.id.textView3);
                                                  bundle.putString("type","1");
                                                  bundle.putString("course_name", course_name.getText().toString());
                                                  bundle.putString("teacher", teacher.getText().toString());
                                                  Intent intent = new Intent(getActivity(), CourseMainActivity.class);
                                                  intent.putExtras(bundle);
                                                  getActivity().startActivityForResult(intent, 1);
                                              }
                                          });
                                          read();
                                          ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                                                  android.R.layout.simple_dropdown_item_1line, a);
                                          auto.setAdapter(adapter1);

                                      }
                                  });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    }// intialization()函数结束
    //读取文件autoString作为AutoCompleteTextView的adapter
    private void read() {

        try {
            //ed2.setText("");
            // 打开文件输入流
            FileInputStream fileInput = getActivity().openFileInput("autoString.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fileInput));
            String str = null;
            StringBuilder stb = new StringBuilder();
            while ((str = br.readLine()) !=null ) {
                stb.append(str);
            }
            a=stb.toString().split(" ");
            //ed2.setText(stb);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void write(String content) {
        try {
            // 以追加的方式打开文件输出流
            FileOutputStream fileOut = getActivity().openFileOutput("autoString.txt",
                    getActivity().MODE_APPEND);
            // 写入数据
            fileOut.write(content.getBytes());
            // 关闭文件输出流
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
