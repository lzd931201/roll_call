package com.example.xiao.attendance;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class mylist_attend extends AppCompatActivity {

    public class Student {
        public String xuehao;
        public String xingming;
        public String mac;
        public Student(){}
        public Student(String m,String n,String s){
            xuehao=m;
            xingming=n;
            mac=s;
        }
    }
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver broadcastReceiver_2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                String student_tmp=m_findnum_by_mac(bluetoothDevice.getAddress());
                if (student_tmp!=null) {
                    if(m_arraylist_inattend.remove(student_tmp)){
                        n1++;
                        globalapp appG = ((globalapp)getApplicationContext());
                        m_arraylist.add(student_tmp);
                        appG.remove_AL(student_tmp);
                        appG.change_ALS(student_tmp, 0, 1);
                        n=appG.getSomeVariable();
                        n--;
                        appG.setSomeVariable(n);
                        a = "目前已到人数: "+Integer.toString(n1);
                        m_number.setText(a);
                        adapter.add(m_findname_by_num(student_tmp));
                    }
                }
            }
        }
    };
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myrefresh();
        }
    };
    private View.OnClickListener listener1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent1 = new Intent();
            intent1.setClass(mylist_attend.this, export.class);
            startActivity(intent1);
        }
    };
    private View.OnClickListener listener2=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent1 = new Intent();
            intent1.setClass(mylist_attend.this, mylist.class);
            startActivity(intent1);
            mylist_attend.this.finish();
            unregisterReceiver(broadcastReceiver_2);
        }
    };

    private SQLiteDatabase db;
    private String m_classnum;
    private ListView listview;
    private ArrayAdapter adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private int n1=0;
    private int n=0;
    private String a;
    private TextView m_number;
    private ArrayList<String> m_arraylist=new ArrayList<String>();
    private ArrayList<String> m_arraylist_inattend=new ArrayList<String>();
    private ArrayList<String> m_arraylist_2=new ArrayList<String>();
    private ArrayList<globalapp.student_state> m_arraylist_state=new ArrayList<globalapp.student_state>();
    private Button m_refresh;
    private Button m_end;
    private Button m_unattend;


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
    protected void dialog(final String student_num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择签到情况");
        final globalapp appG = ((globalapp)getApplicationContext());
        final int checkedItem=appG.find_state(student_num);
        final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
        builder.setSingleChoiceItems(R.array.check_att, checkedItem, choiceListener);
        builder.setPositiveButton("确认",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                int choiceWhich;
                if(choiceListener.check()) {
                    choiceWhich = choiceListener.getWhich();
                }
                else
                    choiceWhich=checkedItem;
                if(choiceWhich!=1){
                    appG.add_AL(student_num);
                    if(m_arraylist.remove(student_num)){
                        n1--;
                        n=appG.getSomeVariable();
                        n++;
                        appG.setSomeVariable(n);
                        a = "目前已到人数: "+Integer.toString(n1);
                        m_number.setText(a);
                    }
                    adapter.remove(m_findname_by_num(student_num));
                }
                String StuStr =student_num+getResources().getStringArray(R.array.check_att)[choiceWhich];
                appG.change_ALS(student_num,checkedItem,choiceWhich);
                Toast.makeText(getApplicationContext(),StuStr,
                        Toast.LENGTH_SHORT).show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();
            intent1.setClass(mylist_attend.this, mytime.class);
            startActivity(intent1);
            mylist_attend.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public int m_getnumber(){
        globalapp appG = ((globalapp)getApplicationContext());
        m_arraylist_state=appG.get_ALS();
        int x=0;
       for(globalapp.student_state tmp:m_arraylist_state) {
           if (tmp.getState()== 1) x++;
       }
        return x;
    }
    public void m_init_number(){
        n1 = m_getnumber();
        a = "目前已到人数: "+Integer.toString(n1);
        m_number.setText(a);
    }
    public String m_findnum_by_mac(String mac){
        Cursor c=db.query(m_classnum, new String[]{"xuehao"}, "mac=?", new String[]{mac}, null, null, null);
        String x=null;
        if(c.moveToFirst()) {
            x = c.getString(c.getColumnIndex("xuehao"));
            c.close();
        }
        return x;
    }
    public String m_findname_by_num(String num){
        Cursor c=db.query(m_classnum,new String[]{"xingming"},"xuehao=?",new String[]{num},null,null,null);
        c.moveToFirst();
        String x=c.getString(c.getColumnIndex("xingming"));
        c.close();
        return x;
    }
    public String m_findnum_by_name(String name){
        Cursor c=db.query(m_classnum,new String[]{"xuehao"},"xingming=?",new String[]{name},null,null,null);
        c.moveToFirst();
        String x=c.getString(c.getColumnIndex("xuehao"));
        c.close();
        return x;
    }
    public void m_init_query() {
        globalapp appG = ((globalapp)getApplicationContext());
        m_arraylist_state=appG.get_ALS();
        for(globalapp.student_state tmp:m_arraylist_state) {
            if (tmp.getState()== 1)
                m_arraylist.add(tmp.getStu_num());
            else
                m_arraylist_inattend.add(tmp.getStu_num());
        }
    }
    public ArrayList<String> m_arraylist_trans(ArrayList<String>s0){
        ArrayList<String> s1=new ArrayList<String>();
        String a0;
        for (String tmp : s0){
            a0=m_findname_by_num(tmp);
            s1.add(a0);
        }
        return s1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist_attend);

        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        m_number=(TextView)findViewById(R.id.my_number2);
        a = "目前已到人数: "+Integer.toString(n);
        m_number.setText(a);
        db=openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);

        listview = (ListView) findViewById(R.id.listView2);
        m_refresh=(Button)findViewById(R.id.my_refresh2);
        m_refresh.setOnClickListener(listener);
        m_end=(Button)findViewById(R.id.my_end2);
        m_end.setOnClickListener(listener1);
        m_unattend=(Button)findViewById(R.id.my_unattend);
        m_unattend.setOnClickListener(listener2);
        // init();
        m_init_query();
        m_arraylist_2= m_arraylist_trans(m_arraylist);
        adapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1,m_arraylist_2);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String a=((TextView) view).getText().toString();
                dialog(m_findnum_by_name(a));
            }
        });
        m_init_number();
        mLocation();
        enable_BT();


        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_2, filter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_mylist_attend);
        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);
        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        listview = (ListView) findViewById(R.id.listView2);
        m_refresh=(Button)findViewById(R.id.my_refresh2);
        m_refresh.setOnClickListener(listener);
        m_end=(Button)findViewById(R.id.my_end2);
        m_end.setOnClickListener(listener1);
        m_number=(TextView)findViewById(R.id.my_number2);
        a = "目前已到人数: "+Integer.toString(n1);
        m_number.setText(a);
        m_arraylist_2=m_arraylist_trans(m_arraylist);
        adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                m_arraylist_2 );
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String a=((TextView) view).getText().toString();
                dialog(m_findnum_by_name(a));
            }
        });
        discoverDevices();
        // Register the BroadcastReceiver for ACTIONn_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_2, filter);
    }

    //刷新按钮对应的函数
    public void myrefresh(){
        discoverDevices();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_2, filter);
    }

    //允许获取地理位置的函数
    public void mLocation(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //enable Bluetooth 函数
    public void enable_BT(){
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            discoverDevices();
            makeDiscoverable();
        }
    }

    protected void makeDiscoverable(){
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    //发现蓝牙设备
    protected void discoverDevices(){
        // To scan for remote Bluetooth devices
        if (mBluetoothAdapter.startDiscovery()) {
            Toast.makeText(getApplicationContext(), "Discovering other bluetooth devices...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Discovery failed to start.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

