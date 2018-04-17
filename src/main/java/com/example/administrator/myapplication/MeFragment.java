package com.example.administrator.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

public class MeFragment extends Fragment {
    private JSONObject object;
    private Handler handler;
    private ImageView mImageView,avatar;
    private TextView user_id1,department,profession,username;
    private ListView mListView;
    private Bitmap bitmap = null;
    private String user_id,identity=null;
    ArrayList<String>  course_name = new ArrayList<String> ();
    ArrayList<String>  teacher = new ArrayList<String> ();
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        handler=new Handler();
        object = new JSONObject();
        View tab01 = inflater.inflate(R.layout.me,container,false);
        avatar= (ImageView)tab01.findViewById(R.id.imageView2);
        user_id1=(TextView)tab01.findViewById(R.id.textView1);
        department=(TextView)tab01.findViewById(R.id.textView2);
        profession=(TextView)tab01.findViewById(R.id.textView3);
        username=(TextView)tab01.findViewById(R.id.textView4);
        mListView=(ListView)tab01.findViewById(R.id.listView);
        mImageView= (ImageView)tab01.findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击按钮就创建并显示一个popupMenu
                showPopmenu(mImageView);
            }
        });

        SharedPreferences pref = getActivity().getSharedPreferences("data",0);
        user_id = pref.getString("user_id","");//第二个参数为默认值
        if(!user_id.equals("")){
            try{
            object.put("user_id",user_id);
            } catch (JSONException e) {
            e.printStackTrace();
            }
            new Thread() {

                @Override
                public void run() {

                    super.run();

                    intialization();
                }
            }.start();
        }
        return tab01;
    }
    private void showPopmenu(View view){
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        final SharedPreferences pref = getActivity().getSharedPreferences("data",0);
        if(pref.getString("identity","").equals("教师")){
            popupMenu.getMenuInflater().inflate(R.menu.me_headmenu1,popupMenu.getMenu());
        }
        else popupMenu.getMenuInflater().inflate(R.menu.me_headmenu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.login:
                        SharedPreferences pref = getActivity().getSharedPreferences("data",0);
                        String user_id = pref.getString("user_id","");//第二个参数为默认值
                        if(user_id.equals("")){
                        //Log.i("objectT", getActivity().toString());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivityForResult(intent, 0);}
                        else
                            Toast.makeText(getActivity(), "您已经登录了。", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.exlogin:
                        SharedPreferences pref1 = getActivity().getSharedPreferences("data", 0);
                        SharedPreferences.Editor editor = pref1.edit();
                        editor.putString("user_id","");
                        editor.commit();
                        MainActivity activity= (MainActivity) getActivity();
                        activity.RefreshMeFragment();
                        break;
                    case R.id.changepw:
                        if(user_id1.getText().toString().equals("—"))
                            Toast.makeText(getActivity(), "请先登录。", Toast.LENGTH_SHORT).show();
                        else{
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", user_id1.getText().toString());
                            Intent intent = new Intent(getActivity(),MeChangePwActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                    case R.id.new_course:
                        Bundle bundle = new Bundle();
                        bundle.putString("teacher", user_id1.getText().toString());
                        bundle.putString("department", department.getText().toString());
                        Intent intent = new Intent(getActivity(),MeCreateCourseActivity.class);
                        intent.putExtras(bundle);
                        //startActivity(intent);
                        getActivity().startActivityForResult(intent, 2);
                        break;
                    case R.id.change_avatar:
                        if(user_id1.getText().toString().equals("—"))
                            Toast.makeText(getActivity(), "请先登录。", Toast.LENGTH_SHORT).show();
                        else{
                        SharedPreferences pref2 = getActivity().getSharedPreferences("data",0);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("user_id",pref2.getString("user_id",""));
                        bundle1.putString("mark_","avatar");
                        Intent intent1 = new Intent(getActivity(),UpdateCoverAvatarActivity.class);
                        intent1.putExtras(bundle1);
                        //startActivity(intent);
                        getActivity().startActivityForResult(intent1, 0);}
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void intialization(){
        //Log.i("objectT","hhhh ");
        //用于接收多个课程的数据
        final List<String> list = new ArrayList<>();

        try {

            URL url = new URL(this.getString(R.string.link)+"/MeServlet");//设置连接的url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");  //请求方法
            conn.setConnectTimeout(15000);  //设置连接超时
            conn.setReadTimeout(10000);  //设置读取超时
            conn.connect();  //建立连接
            OutputStream out = conn.getOutputStream();
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
                                list.add(objectT.getString("course_name"+i)+"-"+objectT.getString("teacher"+i));
                                course_name.add(objectT.getString("course_name"+i));
                                teacher.add(objectT.getString("teacher"+i));
                            }
                            //将用户身份存入SharePreference
                            SharedPreferences pref1 = getActivity().getSharedPreferences("data", 0);
                            SharedPreferences.Editor editor = pref1.edit();
                            editor.putString("identity",objectT.getString("identity"));
                            editor.commit();
                            //获取头像，要新开启一个连接
                            try{
                                //解决URI有空格Tomcat识别不了的问题
                                String temp1= URLEncoder.encode(objectT.getString("avatar"), "utf-8");
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
                            //异步更新数据
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                            android.R.layout.simple_dropdown_item_1line, list);
                                    mListView.setAdapter(adapter);
                                    //转换圆形头像
                                    RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                    circleDrawable.getPaint().setAntiAlias(true);
                                    circleDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()));
                                    avatar.setImageDrawable(circleDrawable);
                                    try {
                                        user_id1.setText(user_id);
                                        department.setText(objectT.getString("department"));
                                        if(objectT.getString("identity").equals("教师"))
                                        profession.setText(objectT.getString("username"));
                                        else{
                                            profession.setText(objectT.getString("profession"));
                                            username.setText(objectT.getString("username"));
                                        }
                                    } catch (JSONException e) {
                                       e.printStackTrace();
                                    }
                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                            Bundle bundle1 = new Bundle();
                                            bundle1.putString("type","2");
                                            bundle1.putString("course_name",course_name.get(arg2));
                                            bundle1.putString("teacher",teacher.get(arg2));
                                            Intent intent = new Intent(getActivity(), CourseMainActivity.class);
                                            intent.putExtras(bundle1);
                                            getActivity().startActivityForResult(intent, 2);
                                        }
                                    });
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



}