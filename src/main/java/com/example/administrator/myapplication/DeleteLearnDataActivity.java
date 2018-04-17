package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/20.
 */
public class DeleteLearnDataActivity extends Activity {
    private ListView mListView;
    private Button mButton;
    private JSONObject object;
    private Handler handler;
    private ImageView back;
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_learn_data);
        mListView = (ListView)findViewById(R.id.listView);
        mButton = (Button)findViewById(R.id.button);
        back=(ImageView)findViewById(R.id.imageView);
        handler=new Handler();
        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteLearnDataActivity.this.finish();
            }
        });
        try {
            object = new JSONObject();
            object.put("chapter_id",bundle.getString("chapter_id"));
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


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    object = new JSONObject();
                    object.put("chapter_id",bundle.getString("chapter_id"));
                    object.put("row",names.size());
                    for(int i=0;i<names.size();i++)
                    object.put("learn_data_name"+i,names.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread() {

                    @Override
                    public void run() {

                        super.run();
                        intialization("delete");
                    }
                }.start();

            }
        });



    }

    public void intialization(String mark){

        try {

            URL url = new URL(this.getString(R.string.link)+"/DeleteLearnDataServlet");//设置连接的url
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
                            if(mark.equals("chushihua")) {

                                for (int i = 1; i <=Integer.parseInt(objectT.getString("row")); i++) {//判断有几个课程
                                    Map<String, Object> listitem = new HashMap<String, Object>();
                                    listitem.put("learn_data_name", objectT.getString("learn_data_name" + i));
                                    listitem.put("isSelected",false);
                                    items.add(listitem);
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SimpleAdapter adapter = new SimpleAdapter(DeleteLearnDataActivity.this, items, R.layout.delete_learn_data_list, new String[]{"isSelected", "learn_data_name"}, new int[]

                                                {R.id.checkBox, R.id.textView}) {

                                            @Override
                                            public View getView(final int position, View convertView, ViewGroup parent) {
                                                View view = super.getView(position, convertView, parent);
                                                @SuppressWarnings("unchecked")
                                                final HashMap<String, Object> map = (HashMap<String, Object>) this.getItem(position);
                                                //获取相应View中的Checkbox对象
                                                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                                                checkBox.setChecked((Boolean) map.get("isSelected"));
                                                //添加单击事件,在map中记录状态
                                                //通过判断checkbox是否被选中来确定联系人是否被放在names和numbers两个数组中。

                                                checkBox.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        map.put("isSelected", ((CheckBox) view).isChecked());
                                                        if (((CheckBox) view).isChecked()) {
                                                            //Toast.makeText(DeleteLearnDataActivity.this, "选中了" + map.get("name"), Toast.LENGTH_SHORT).show();
                                                            names.add((String) map.get("learn_data_name"));
                                                        } else {
                                                            names.remove(map.get("learn_data_name"));
                                                        }

                                                    }
                                                });
                                                return view;
                                            }

                                        };
                                        mListView.setAdapter(adapter);
                                    }
                                });

                            }
                            if(mark.equals("delete")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DeleteLearnDataActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DeleteLearnDataActivity.this, CourseChapterActivity.class);
                                        setResult(2, intent);
                                        finish();
                                    }
                                });
                            }


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


}
