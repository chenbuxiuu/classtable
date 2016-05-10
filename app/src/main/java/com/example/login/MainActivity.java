package com.example.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private static String app_info="app_info";
    private static String autoLogin_info="autoLogin";
    private Button[] button;
    private Button week_set;
    private ArrayList<String> classTable;
//    private static String s="18011130 入侵检测原理与技术 12 一 9-12 A主0410";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        classTable= Utils.getClassTable(Utils.getUserList(MainActivity.this).get(0).getId());
        initButton();

        setTranslucentForDrawerLayout(this, drawer);
        sp=this.getSharedPreferences(app_info, Context.MODE_APPEND);

        String myWeek=sp.getString("week","1");
        week_set=(Button)findViewById(R.id.week_set);
        week_set.setText(myWeek);
        for(int i=0;i<classTable.size();i++){
            showClassTable(myWeek,classTable.get(i));
        }
        setWeekListener();
        ExitApplication.getInstance().addActivity(this);
    }

    public void initButton(){
        button=new Button[42];
        Integer[] id= new Integer[]{
                R.id.lesson11,R.id.lesson21,R.id.lesson31,R.id.lesson41,R.id.lesson51,R.id.lesson61,
                R.id.lesson12,R.id.lesson22,R.id.lesson32,R.id.lesson42,R.id.lesson52,R.id.lesson62,
                R.id.lesson13,R.id.lesson23,R.id.lesson33,R.id.lesson43,R.id.lesson53,R.id.lesson63,
                R.id.lesson14,R.id.lesson24,R.id.lesson34,R.id.lesson44,R.id.lesson54,R.id.lesson64,
                R.id.lesson15,R.id.lesson25,R.id.lesson35,R.id.lesson45,R.id.lesson55,R.id.lesson65,
                R.id.lesson16,R.id.lesson26,R.id.lesson36,R.id.lesson46,R.id.lesson56,R.id.lesson66,
                R.id.lesson17,R.id.lesson27,R.id.lesson37,R.id.lesson47,R.id.lesson57,R.id.lesson67,
        };

        for(int i=0;i<42;i++){
            button[i]=(Button)findViewById(id[i]);

//            button[i].setText(String.valueOf(i));
        }
    }

    public void setWeekListener(){
        week_set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] s=new String[20];
                for(int i=0;i<20;i++){
                    s[i]=String.valueOf(i+1);
                }
//                Toast.makeText(MainActivity.this,"设置周数",Toast.LENGTH_SHORT).show();
                int choosedWeek=Integer.parseInt(sp.getString("week","1"))-1;
                new AlertDialog.Builder(MainActivity.this).setTitle("选择周数").setSingleChoiceItems(s,choosedWeek,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //记录当前周数
                                saveWeekInfo("week",String.valueOf(which+1));
                                //清空当前课表
                                for(int i=0;i<42;i++){
                                    button[i].setText("");
                                }
                                //刷新课程
                                week_set.setText(String.valueOf(which+1));
                                for(int i=0;i<classTable.size();i++){
                                    showClassTable(String.valueOf(which+1),classTable.get(i));
                                }
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.classtable) {
            Toast.makeText(getApplicationContext(), "课程表",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.notice) {
            Toast.makeText(getApplicationContext(), "课程通知",
                    Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,CourseNotice.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.upload){
            Toast.makeText(getApplicationContext(), "上传作业",
                    Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,UploadActivity.class);
            startActivity(intent);
            finish();
        }else if(id==R.id.download){
            Toast.makeText(getApplicationContext(), "下载资源",
                    Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,DownloadActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "关于",
                    Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,About.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            Toast.makeText(getApplicationContext(), "注销",
                    Toast.LENGTH_SHORT).show();
            logOutAccout();
        } else if (id == R.id.exit) {
            Toast.makeText(getApplicationContext(), "退出",
                    Toast.LENGTH_SHORT).show();
           exitApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitApp(){
        AlertDialog.Builder isExit=new AlertDialog.Builder(this).setTitle("确认退出？")
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        ExitApplication.getInstance().exit(MainActivity.this);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        isExit.show();
    }

    public void logOutAccout(){
        AlertDialog.Builder isLogout=new AlertDialog.Builder(this).setTitle("是否注销？")
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        SharedPreferences.Editor editor=sp.edit();
                        editor.remove(autoLogin_info);
                        editor.putBoolean(autoLogin_info,false);
                        editor.commit();
                        finish();
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        isLogout.show();
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ExitApplication.getInstance().exit(MainActivity.this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // 设置抽屉布局属性
            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
            vg.setFitsSystemWindows(false);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }

    public void showClassTable(String week,String str){
        String[] courseStrings=str.split(" ");
        String cid=courseStrings[0];
        String cname=courseStrings[1];
        String cweek=courseStrings[2];
        String cday=courseStrings[3];
        String ctime=courseStrings[4];
        String caddr=courseStrings[5];
        String ct="";
//        Log.i("length",String.valueOf(courseStrings.length));
        if(courseStrings.length==7){
            ct=courseStrings[6];
        }

        int d=0;
        switch (cday){
            case "一":d=1;
                break;
            case "二":d=2;
                break;
            case "三":d=3;
                break;
            case "四":d=4;
                break;
            case "五":d=5;
                break;
            case "六":d=6;
                break;
            case "日":d=7;
                break;
        }
        int b;
        int e;
        String[] s=ctime.split("-");
        b=Integer.parseInt(s[0]);
        e=Integer.parseInt(s[1]);

        b=(d-1)*6+b/2;
        e=(d-1)*6+e/2;
        String string;
        if(!caddr.equals("")) {
            string=cname+"@"+caddr;
        }
        else {
            string=cname;
        }
        String[] iweek=cweek.split(",");
        boolean flag=false;
        for(int i=0;i<iweek.length;i++){
            if(iweek[i].equals(week)){
                flag=true;
                break;
            }
        }
        for(int i=b;i<e;i++){
            if(flag){
                button[i].setText(string);
                String[] ts=new String[5];
                ts[0]="课程名  "+cname;
                ts[1]="上课周数 "+cweek;
                ts[2]="节数   "+cday+" "+ctime;
                ts[3]="地点   "+caddr;
                ts[4]="老师   "+ct;
                final String[] cs=ts;
                button[i].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MainActivity.this).setTitle("课程信息").setItems(
                                cs, null).show();
//                        Toast.makeText(MainActivity.this,cs, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    public void saveWeekInfo(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.commit();
    }

}
