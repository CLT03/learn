package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2018/3/2.
 */
public class TestActivity extends Activity {
    String[] a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        String[] autoString = new String[]
                { "数据" };
        for(int i=0;i<autoString.length;i++)
        {
            write(autoString[i]+" ");
        }
        read();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, a);

        // AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);     // 绑定adapter
    }

    private void write(String content) {
        try {
            // 以追加的方式打开文件输出流
            FileOutputStream fileOut = this.openFileOutput("autoString.txt",
                    this.MODE_APPEND);
            // 写入数据
            fileOut.write(content.getBytes());
            // 关闭文件输出流
            fileOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {

        try {
            //ed2.setText("");
            // 打开文件输入流
            FileInputStream fileInput = this.openFileInput("autoString.txt");
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
}
