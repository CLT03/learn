package com.example.administrator.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
/**
 * Created by Administrator on 2018/3/3.
 */
public class Test2Activity extends Activity {

    private Button read;
    private Button write;
    private EditText ed1;
    private EditText ed2;
    private EditText ed3;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test2);
        read = (Button) findViewById(R.id.read);
        write = (Button) findViewById(R.id.write);
        delete = (Button) findViewById(R.id.delete);
        ed3 = (EditText) findViewById(R.id.ed3);
        ed2 = (EditText) findViewById(R.id.ed2);
        ed1 = (EditText) findViewById(R.id.ed1);
        write.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ed1.getText().toString();
                if (!str.equals("")) {
                    write(str);
                }

            }
        });
        read.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                read();

            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ed3.getText().toString();
                if (!str.equals("")) {
                    deleteFiles(str);
                } else {
                    ed3.setText(str + ":该文件输入错误或不存在!");
                }

            }
        });

    }

    private void write(String content) {
        try {
            // 以追加的方式打开文件输出流
            FileOutputStream fileOut = this.openFileOutput("test.txt",
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
            ed2.setText("");
            // 打开文件输入流
            FileInputStream fileInput = this.openFileInput("test.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    fileInput));
            String str = null;
            StringBuilder stb = new StringBuilder();
            while ((str = br.readLine()) !=null ) {
                stb.append(str);
            }
            ed2.setText(stb);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //删除指定的文件
    private void deleteFiles(String fileName) {
        try {
            // 获取data文件中的所有文件列表
            List<String> name = Arrays.asList(this.fileList());
            if (name.contains(fileName)) {
                this.deleteFile(fileName);
                ed3.setText(fileName + ":该文件成功删除！");
            } else
                ed3.setText(fileName + ":该文件输入错误或不存在!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
