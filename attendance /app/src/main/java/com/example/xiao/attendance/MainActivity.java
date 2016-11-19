package com.example.xiao.attendance;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    public SQLiteDatabase db;
    private EditText m_id,m_password;
    private String myid,mypassword;
    private void copyDataBaseToPhone() {
        DataBaseUtil util = new DataBaseUtil(this);
        // 判断数据库是否存在
        boolean dbExist = util.checkDataBase();

        if (dbExist) {
            Log.i("tag", "The database is exist.");
        } else {// 不存在就把raw里的数据库写入手机
            try {
                util.copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard){
            copyDataBaseToPhone();
        }else{
            Toast.makeText(getApplicationContext(), "未检测到SDCard",
                    Toast.LENGTH_SHORT).show();
        }
        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);

        button1=(Button)findViewById(R.id.my_login);
        button1.setOnClickListener(listener);

        m_id = (EditText) findViewById(R.id.my_id);
        m_password = (EditText) findViewById(R.id.my_password);

    }
    private OnClickListener listener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            //authenticated user information
            String m_userid="panxiao";
            String m_userpass="1252618";

            myid = m_id.getText().toString();
            mypassword = m_password.getText().toString();
            if(myid.equals(m_userid)) {
                if(mypassword.equals(m_userpass)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, mytime.class);
                    startActivity(intent1);
                }
                else {
                    Toast.makeText(getApplicationContext(), "密码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "用户不存在！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
