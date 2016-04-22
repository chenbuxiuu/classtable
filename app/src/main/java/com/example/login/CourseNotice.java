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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class CourseNotice extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private static String Checkbox_info="checkbox";
    private static String autoLogin_info="autoLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_course_notice);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.coursenotice_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTranslucentForDrawerLayout(this, drawer);
        sp=this.getSharedPreferences(Checkbox_info, Context.MODE_APPEND);
        ExitApplication.getInstance().addActivity(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.coursenotice_layout);
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
            Intent intent=new Intent(CourseNotice.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.notice) {
            Toast.makeText(getApplicationContext(), "课程通知",
                    Toast.LENGTH_SHORT).show();
        }else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "关于",
                    Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(CourseNotice.this,About.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.coursenotice_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitApp(){
        AlertDialog.Builder isExit=new AlertDialog.Builder(this).setTitle("确认退出？")
                .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        ExitApplication.getInstance().exit(CourseNotice.this);
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
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        SharedPreferences.Editor editor=sp.edit();
                        editor.remove(autoLogin_info);
                        editor.putBoolean(autoLogin_info,false);
                        editor.commit();
                        finish();
                        Intent intent=new Intent(CourseNotice.this,LoginActivity.class);
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
                ExitApplication.getInstance().exit(CourseNotice.this);
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
}
