package com.example.xiao.attendance;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class record extends AppCompatActivity {
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

    private final BroadcastReceiver broadcastReceiver_1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String student_tmp=m_findmac_by_num(bluetoothDevice.getName());
                if (student_tmp!="no record") {
                    if (student_tmp==null) {
                        m_arraylist.remove(bluetoothDevice.getName());
                        adapter.remove(m_findname_by_num(bluetoothDevice.getName()));
                        n--;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("mac", bluetoothDevice.getAddress());
                    String[] selectionArgs ={bluetoothDevice.getName()};
                            db.update(m_classnum, contentValues, "xuehao=?", selectionArgs);
                    a = "还未登记人数: " + Integer.toString(n);
                    m_number.setText(a);
                }
            }
        }
    };
    private View.OnClickListener listener1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           /* Intent intent1 = new Intent();
            intent1.setClass(record.this, mytime.class);
            startActivity(intent1);*/
            dialog();
        }
    };
    public SQLiteDatabase db;
    private ListView listview;
    private ArrayAdapter adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private int n=0;
    private String a;
    private String m_classnum;
    private TextView m_number;
    private ArrayList<String> m_arraylist=new ArrayList<String>();
    private ArrayList<String> m_arraylist_2=new ArrayList<String>();
    private Button m_recrefresh;
    private Button m_recend;
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myrefresh();
        }
    };
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(record.this);
        builder.setMessage("确认退出吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                unregisterReceiver(broadcastReceiver_1);
                Intent intent1 = new Intent();
                intent1.setClass(record.this, mytime.class);
                startActivity(intent1);
                record.this.finish();
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
    public void m_init_query(){
        Cursor c = db.query(m_classnum, null, null, null, null, null, null);
        while(c.moveToNext()) {
            Student s = new Student();
            s.xuehao = c.getString(c.getColumnIndex("xuehao"));
            s.xingming = c.getString(c.getColumnIndex("xingming"));
            String m_mac=c.getString(c.getColumnIndex("mac"));
            if(m_mac==null) {
                adapter.add(s.xingming);
                m_arraylist.add(s.xuehao);
                n++;
            }
        }
        a = "还未登记人数: "+Integer.toString(n);
        m_number.setText(a);
        c.close();
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
    public String m_findmac_by_num(String num){
        String x = "no record";
        if(num!=null) {
            Cursor c = db.query(m_classnum, new String[]{"mac"}, "xuehao=?", new String[]{num}, null, null, null);
            if (c.moveToFirst()) {
                x = c.getString(c.getColumnIndex("mac"));
                c.close();
            }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);

        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        listview = (ListView) findViewById(R.id.listView_rec);
        m_recrefresh=(Button)findViewById(R.id.my_recrefresh);
        m_recrefresh.setOnClickListener(listener);
        m_recend=(Button)findViewById(R.id.my_recend);
        m_recend.setOnClickListener(listener1);
        adapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);

        mLocation();
        enable_BT();
        m_number=(TextView)findViewById(R.id.my_recnumber);
        a = "还未登记人数: "+Integer.toString(n);
        m_number.setText(a);
        //m_arraylist.add("A");
        m_init_query();

        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_1, filter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_record);
        db = openOrCreateDatabase("students_origin.db", Context.MODE_PRIVATE, null);

        globalapp appG = ((globalapp)getApplicationContext());
        m_classnum="'"+appG.getClass_num()+"'";

        listview = (ListView) findViewById(R.id.listView_rec);
        m_recrefresh=(Button)findViewById(R.id.my_recrefresh);
        m_recrefresh.setOnClickListener(listener);
        m_recend=(Button)findViewById(R.id.my_recend);
        m_recend.setOnClickListener(listener1);
        m_number=(TextView)findViewById(R.id.my_recnumber);
        a = "还未登记人数: "+Integer.toString(n);
        m_number.setText(a);
        m_arraylist_2=m_arraylist_trans(m_arraylist);
        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                m_arraylist_2 );
        listview.setAdapter(adapter);
        discoverDevices();
        // Register the BroadcastReceiver for ACTIONn_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_1, filter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //刷新按钮对应的函数
    public void myrefresh(){
        discoverDevices();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver_1, filter);
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
