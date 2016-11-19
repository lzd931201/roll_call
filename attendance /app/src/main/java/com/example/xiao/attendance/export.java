package com.example.xiao.attendance;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;

public class export extends AppCompatActivity {

    public class Student {
        public String mac;
        public String name;
        public Student(){}
        public Student(String m,String n){
            mac=m;
            name=n;
        }
    }
    public SQLiteDatabase db;
    private String m_classnum;
    private ArrayList<globalapp.student_state> m_arraylist_state=new ArrayList<globalapp.student_state>();
    private Button b_save,b_cancel;
    private View.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog_save();
        }
    };
    private View.OnClickListener listener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog();
        }
    };
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(export.this);
        builder.setMessage("确认清空缓存吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                globalapp appG = ((globalapp)getApplicationContext());
                appG.clearall();
                Intent intent1 = new Intent();
                intent1.setClass(export.this, mytime.class);
                startActivity(intent1);
                export.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    protected void dialog_save(){
        AlertDialog.Builder builder = new AlertDialog.Builder(export.this);
        builder.setMessage("确认保存记录吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_save(m_getTime());
                globalapp appG = ((globalapp)getApplicationContext());
                appG.clearall();
                dialog_show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    protected void dialog_show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(export.this);
        builder.setMessage("已经保存当前点名记录！");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent1 = new Intent();
                intent1.setClass(export.this, mytime.class);
                startActivity(intent1);
                export.this.finish();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void m_save(String name){
        db.execSQL("ALTER TABLE" + m_classnum + "ADD COLUMN"+name+"VARCHAR DEFAULT '0'");
        globalapp appG = ((globalapp)getApplicationContext());
        m_arraylist_state=appG.get_ALS();
        ContentValues contentValues = new ContentValues();
        for(globalapp.student_state tmp:m_arraylist_state) {
            String tmp_state="";
            switch (tmp.getState()){
                case 0:
                    tmp_state="×";break;
                case 1:
                    tmp_state="√";break;
                case 2:
                    tmp_state="迟到";break;
                case 3:
                    tmp_state="请假";
            }
            contentValues.put(name, tmp_state);
            String[] selectionArgs = {tmp.getStu_num()};
            db.update(m_classnum, contentValues, "xuehao=?", selectionArgs);
        }
    }
    public String m_getTime(){
        Calendar x=Calendar.getInstance();
        int year = x.get(Calendar.YEAR);
        int month = x.get(Calendar.MONTH);
        int day = x.get(Calendar.DATE);
        int hour=x.get(Calendar.HOUR_OF_DAY);
        int minute=x.get(Calendar.MINUTE);
        String date = Integer.toString(year) +"/"+Integer.toString(month + 1) + "/"+Integer.toString(day)+ "-"+Integer.toString(hour)+":"+ Integer.toString(minute);
        return "'"+date+"'";
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);

        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        b_save=(Button)findViewById(R.id.m_save);
        b_save.setOnClickListener(listener1);
        b_cancel=(Button)findViewById(R.id.m_clear);
        b_cancel.setOnClickListener(listener2);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_export);

        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);
        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        b_save=(Button)findViewById(R.id.m_save);
        b_save.setOnClickListener(listener1);
        b_cancel=(Button)findViewById(R.id.m_clear);
        b_cancel.setOnClickListener(listener2);
    }
}
