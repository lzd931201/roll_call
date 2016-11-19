package com.example.xiao.attendance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class mytime extends AppCompatActivity {
    private SQLiteDatabase db;
    private Button button0,button1,button2,button3,button_export;
    private String m_classnum;
    private boolean m_isChoose_class;
    private int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private TextView mydate;
    private TextView acd_year, acd_week, acd_class,t_button0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytime);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS );
        }
        db=openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);
        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";
        button0=(Button)findViewById(R.id.my_classname);
        button0.setOnClickListener(listener_classname);

        t_button0=(TextView)findViewById(R.id.my_classname);

        if(appG.getClass_choose()!=-1)
            t_button0.setText(m_findname_by_num(appG.getClass_num()));

        button1=(Button)findViewById(R.id.my_startnew);
        button1.setOnClickListener(listener_mylist_new);

        button2=(Button)findViewById(R.id.my_start);
        button2.setOnClickListener(listener_mylist);

        button3=(Button)findViewById(R.id.my_record);
        button3.setOnClickListener(listener_record);

        button_export=(Button)findViewById(R.id.my_export);
        button_export.setOnClickListener(listener_export);

        //获取当前系统时间
        Calendar c = Calendar.getInstance();
        //设置学期开始的第一天的前一天，并且计算当前日期和这一天之间的天数，以及星期几
        Calendar start_c = new GregorianCalendar(2016, 1, 29);
        set_time(c);//设置时间显示
        set_academic(c);//设置学年学期显示
        set_week(start_c, c);//设置第几周星期几显示
        set_class(c);//设置第几节课显示
        //test_time(start_c,2016,5,16);//测试显示时间功能是否正常
    }

    //
    private View.OnClickListener listener_classname=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog_class();
            dialog_changeclass();
        }
    };

    private View.OnClickListener listener_mylist_new=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            globalapp appG = ((globalapp)getApplicationContext());
            m_isChoose_class=appG.getIs_class_choose();
            if (m_isChoose_class) {
                dialog();
            } else
                dialog_start();
        }
    };

    private View.OnClickListener listener_mylist=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            globalapp appG = ((globalapp)getApplicationContext());
            m_isChoose_class=appG.getIs_class_choose();
            if (m_isChoose_class) {
                Intent intent2 = new Intent();
                intent2.setClass(mytime.this, mylist.class);
                startActivity(intent2);
                mytime.this.finish();
            } else
                dialog_start();
        }
    };

    private View.OnClickListener listener_record=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            globalapp appG = ((globalapp)getApplicationContext());
            m_isChoose_class=appG.getIs_class_choose();
            if (m_isChoose_class) {
                Intent intent2 = new Intent();
                intent2.setClass(mytime.this, record.class);
                startActivity(intent2);
                mytime.this.finish();
            } else
                dialog_start();
        }
    };

    private View.OnClickListener listener_export=new View.OnClickListener() {
        @Override
        public void onClick(View v){
            globalapp appG = ((globalapp)getApplicationContext());
            m_isChoose_class=appG.getIs_class_choose();
            if (m_isChoose_class) {
               try {
                   saveTheFile();

               }
               catch(IOException e){
                String b =e.getMessage();
               }
            } else
                dialog_start();
        }
    };

    void saveTheFile()throws IOException {
    File myFile;
    Calendar cal = Calendar.getInstance();
    globalapp appG = ((globalapp)getApplicationContext());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String TimeStampDB = m_findname_by_num(appG.getClass_num())+"-"+sdf.format(cal.getTime());

    try {
        /*myFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/abc.csv");*/
        myFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/"+TimeStampDB+".csv");
        myFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut,"GB2312");
        myOutWriter.append(m_getdata());
        //myOutWriter.append("\n");
        myOutWriter.close();
        fOut.close();

        Intent i = new Intent();
        i.setAction(android.content.Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(myFile), "text/csv");
        startActivity(i);
    } catch (SQLiteException se)
    {
        Log.e(getClass().getSimpleName(), "Could not create or Open the database");
    }

    finally {

       // sampleDB.close();

    }

    }

    String m_getdata(){
        String a="";
        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";
        Cursor c = db.query(m_classnum, null, null, null, null, null, null);
        int column_num = c.getColumnCount();
        for (int i = 0; i < column_num-1; i++) {
            a += c.getColumnName(i) + ",";
        }
            a += c.getColumnName(column_num-1);
            a += "\n";
        while (c.moveToNext()) {
            for (int i = 0; i < column_num-1; i++) {
                a += c.getString(c.getColumnIndex(c.getColumnName(i))) + ",";
            }
            a += c.getString(c.getColumnIndex(c.getColumnName(column_num-1)));
            a += "\n";
        }
        return a;
    }

    //弹窗
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mytime.this);
        builder.setMessage("确认开始新的点名吗？缓存的点名记录将被清空！");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                globalapp appG = ((globalapp) getApplicationContext());
                appG.clearall();
                //mytime.this.finish();
                Intent intent1 = new Intent();
                intent1.setClass(mytime.this, mylist.class);
                startActivity(intent1);
                mytime.this.finish();
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

    private class ChoiceOnClickListener implements DialogInterface.OnClickListener {
        private boolean flag=false;
        private int which = 0;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            this.which = which;
            flag=true;
        }
        public boolean check(){
            return flag;
        }
        public int getWhich() {
            return which;
        }
    }

    protected void dialog_class() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择课程");
        final globalapp appG = ((globalapp)getApplicationContext());
        final int checkedItem=appG.getClass_choose();
        final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
        builder.setSingleChoiceItems(R.array.class_choose, checkedItem, choiceListener);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int choiceWhich;
                if (choiceListener.check()) {
                    choiceWhich = choiceListener.getWhich();
                    appG.clearall();
                } else
                    choiceWhich = checkedItem;
                if (choiceWhich != -1) {
                    String ClaStr = getResources().getStringArray(R.array.class_choose)[choiceWhich];
                    appG.setClass_num(m_findnum_by_name(ClaStr));
                    appG.setClass_choose(appG.find_choose(ClaStr));
                    m_isChoose_class = true;
                    appG.setIs_class_choose(m_isChoose_class);
                    t_button0.setText(ClaStr);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void dialog_changeclass(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mytime.this);
        builder.setMessage(" 更换课程将清空当前课程缓存的点名记录");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    protected void dialog_start() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mytime.this);
        builder.setMessage("请先选择课程！");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public String m_findnum_by_name(String name){
        Cursor c=db.query("teacher_name",new String[]{"classnum"},"classname=?",new String[]{name},null,null,null);
        c.moveToFirst();
        String x=c.getString(c.getColumnIndex("classnum"));
        c.close();
        return x;
    }
    public String m_findname_by_num(String num){
        Cursor c=db.query("teacher_name",new String[]{"classname"},"classnum=?",new String[]{num},null,null,null);
        c.moveToFirst();
        String x=c.getString(c.getColumnIndex("classname"));
        c.close();
        return x;
    }

    //设置当前系统时间
    public void set_time(Calendar x){
        int year = x.get(Calendar.YEAR);
        int month = x.get(Calendar.MONTH);
        int day = x.get(Calendar.DATE);
        String date = Integer.toString(year) + '/' + Integer.toString(month + 1) + '/' + Integer.toString(day);
        mydate = (TextView) findViewById(R.id.my_date);
        mydate.setText(date);
    }

    //计算当前日期的学年学期
    public void set_academic(Calendar x) {
        //默认上半年的学年，为第二学期
        String year0 = Integer.toString(x.get(Calendar.YEAR) - 1);
        String year1 = Integer.toString(x.get(Calendar.YEAR));
        int semester = 2;

        //如果是8月或以后，则为第二学期，学年也相应改变
        if (x.get(Calendar.MONTH) >= 7) {
            year0 = Integer.toString(x.get(Calendar.YEAR));
            year1 = Integer.toString(x.get(Calendar.YEAR) + 1);
            semester = 1;
        }
        //将数字转化成显示的文字
        String year_a = year0 + '/' + year1 + "学年" + "第" + Integer.toString(semester) + "学期 ";

        //设置对应
        acd_year = (TextView) findViewById(R.id.my_academic);
        acd_year.setText(year_a);
    }

    //计算当前日期是第几周星期几
    public void set_week(Calendar start, Calendar end) {
        int days = safeLongToInt(daysBetween(start, end));
        int day_of_week = day_of_week(end);
        int week = get_week(start, end, days);
        String week_a= "第" + Integer.toString(week) + "周" + "星期" + Integer.toString(day_of_week);
        acd_week = (TextView) findViewById(R.id.my_week);
        acd_week.setText(week_a);
    }

    //以下的三个函数用来判断当前的课是第几节课
    public void set_class(Calendar x){
        String m_class=null;
        acd_class=(TextView)findViewById(R.id.my_class_time);
        switch (time_range(x)) {
            case 1:
                m_class = "今天课程还未开始";break;
            case 2:
                m_class = "第1节课";break;
            case 3:
                m_class = "课间休息";break;
            case 4:
                m_class = "第2节课";break;
            case 5:
                m_class = "课间休息";break;
            case 6:
                m_class = "第3节课";break;
            case 7:
                m_class = "课间休息";break;
            case 8:
                m_class = "第4节课";break;
            case 9:
                m_class = "午间休息";break;
            case 10:
                m_class = "第5节课";break;
            case 11:
                m_class = "课间休息";break;
            case 12:
                m_class = "第6节课";break;
            case 13:
                m_class = "课间休息";break;
            case 14:
                m_class = "第7节课";break;
            case 15:
                m_class = "课间休息";break;
            case 16:
                m_class = "第8节课";break;
            case 17:
                m_class = "晚间休息";break;
            case 18:
                m_class = "第9节课";break;
            case 19:
                m_class = "课间休息";break;
            case 20:
                m_class = "第10节课";break;
            case 21:
                m_class = "课间休息";break;
            case 22:
                m_class = "第11节课";break;
            case 23:
                m_class = "今天全部课程已结束";break;
        }
        acd_class.setText(m_class);
    }//判断当前是第几节课
    public int time_range(Calendar x){
        int a=secondOfDay(x.get(Calendar.HOUR_OF_DAY),x.get(Calendar.MINUTE), x.get(Calendar.SECOND));
        int result=0;
        if(a<secondOfDay(8,0,0)) result=1;
        else if(a<secondOfDay(8, 45, 0)) result=2;
        else if(a<secondOfDay(8, 55, 0)) result=3;
        else if(a<secondOfDay(9, 40, 0)) result=4;
        else if(a<secondOfDay(10, 0, 0)) result=5;
        else if(a<secondOfDay(10, 45, 0)) result=6;
        else if(a<secondOfDay(10, 55, 0)) result=7;
        else if(a<secondOfDay(11, 40, 0)) result=8;
        else if(a<secondOfDay(13, 30, 0)) result=9;
        else if(a<secondOfDay(14, 15, 0)) result=10;
        else if(a<secondOfDay(14, 20, 0)) result=11;
        else if(a<secondOfDay(15, 5, 0)) result=12;
        else if(a<secondOfDay(15, 25, 0)) result=13;
        else if(a<secondOfDay(16, 10, 0)) result=14;
        else if(a<secondOfDay(16, 15, 0)) result=15;
        else if(a<secondOfDay(17, 0, 0)) result=16;
        else if(a<secondOfDay(18, 30, 0)) result=17;
        else if(a<secondOfDay(19, 15, 0)) result=18;
        else if(a<secondOfDay(19, 25, 0)) result=19;
        else if(a<secondOfDay(20, 10, 0)) result=20;
        else if(a<secondOfDay(20, 20, 0)) result=21;
        else if(a<secondOfDay(21, 5, 0)) result=22;
        else result=23;

        return result;
    }//判断当前所属时间段
    public int secondOfDay(int hour,int minute,int second){
        int x;
        x = hour* 3600 + minute*60+second;// 起始时间的分钟数
        return x;
    }//判断当前时间


    //计算两个日期之间的天数
    public static long daysBetween(Calendar startDate, Calendar endDate) {
        return TimeUnit.MILLISECONDS.toDays(
                endDate.getTimeInMillis() - startDate.getTimeInMillis());
    }
    //将Long转化为int
    public static int safeLongToInt(long l) {
        int i = (int) l;
        if ((long) i != l) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return i;
    }
    //计算星期几
    public int day_of_week(Calendar x) {
        int d = x.get(Calendar.DAY_OF_WEEK);
        d = d - 1;
        if (d == 0)
            d = 7;
        return d;
    }
    //计算第几周
    public int get_week(Calendar x, Calendar x1, int days) {
        int week = (days + day_of_week(x) - day_of_week(x1) + 1) / 7 + 1;
        return week;
    }
}
