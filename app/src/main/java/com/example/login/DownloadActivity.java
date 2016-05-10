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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class DownloadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private static String Checkbox_info = "checkbox";
    private static String autoLogin_info = "autoLogin";
    final Data data = (Data) getApplication();
    private String WEBURL = data.WEBURL;
    private Spinner t_spinner;
    private Spinner d_spinner;
    private Button download_bt;
    private Button update_res_bt;
    private String teacherName = "";
    private String resName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_download);

        intin_t_Spinner();
        init_update_res_bt();
        init_down_bt();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.download_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTranslucentForDrawerLayout(this, drawer);
        sp = this.getSharedPreferences(Checkbox_info, Context.MODE_APPEND);
        ExitApplication.getInstance().addActivity(this);
    }

    public void init_update_res_bt(){
        update_res_bt=(Button)findViewById(R.id.update_res_bt);
        update_res_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showResList(teacherName);
            }
        });
    }

    public void init_down_bt(){
        download_bt=(Button)findViewById(R.id.download_bt);
        download_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"开始下载",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"下载文件存于/sdcard/bysjRes",Toast.LENGTH_SHORT).show();
                downRes(teacherName,resName);
            }
        });
    }

    public void intin_t_Spinner() {
        t_spinner = (Spinner) findViewById(R.id.teacher_spinner_d);
        String[] arr = getTeacherList(Utils.getUserList(DownloadActivity.this).get(0).getId());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        t_spinner.setAdapter(arrayAdapter);
        t_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        t_spinner.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                teacherName = t_spinner.getItemAtPosition(position).toString();
                showResList(teacherName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void inint_d_Spinner(String[] arr){
        d_spinner = (Spinner) findViewById(R.id.res_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        d_spinner.setAdapter(arrayAdapter);
        d_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        d_spinner.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                resName = d_spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.download_layout);
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
            Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.notice) {
            Toast.makeText(getApplicationContext(), "课程通知",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DownloadActivity.this, CourseNotice.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.upload) {
            Toast.makeText(getApplicationContext(), "上传作业",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DownloadActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.download) {
            Toast.makeText(getApplicationContext(), "下载资源",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "关于",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DownloadActivity.this, About.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.download_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitApp() {
        AlertDialog.Builder isExit = new AlertDialog.Builder(this).setTitle("确认退出？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        ExitApplication.getInstance().exit(DownloadActivity.this);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        isExit.show();
    }

    public void logOutAccout() {
        AlertDialog.Builder isLogout = new AlertDialog.Builder(this).setTitle("是否注销？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(autoLogin_info);
                        editor.putBoolean(autoLogin_info, false);
                        editor.commit();
                        finish();
                        Intent intent = new Intent(DownloadActivity.this, LoginActivity.class);
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ExitApplication.getInstance().exit(this);
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

    public String[] getTeacherList(String user) {
        String[] getResult;
        HashSet<String> result = new HashSet<String>();
        ArrayList<String> temp = Utils.getClassTable(user);
        for (int i = 0; i < temp.size(); i++) {
            String[] courseStrings = temp.get(i).split(" ");
            if (courseStrings.length == 7) {
                result.add(courseStrings[6]);
            }
        }

        getResult = new String[result.size()];
        int index = 0;
        for (Iterator it = result.iterator(); it.hasNext(); ) {
            getResult[index] = it.next().toString();
            index++;
        }
        return getResult;
    }

    public void showResList(final String TID){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String strURL = WEBURL+"DownloadServlet"+"?TID="+TID;
                URL url = null;
                try {
                    url = new URL(strURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setReadTimeout(5000);
                    InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(in);
                    ArrayList<String> temp_result =new ArrayList<String>();
                    String readLine = null;
                    while ((readLine = bufferedReader.readLine()) != null) {
                        temp_result.add(readLine);
                    }
                    in.close();
                    httpURLConnection.disconnect();
                    String[] ts=new String[temp_result.size()];
                    for(int i=0;i<temp_result.size();i++){
                        ts[i]=temp_result.get(i);
                    }
                    final String[] resList=ts;
                    DownloadActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inint_d_Spinner(resList);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void downRes(final String TID,final String RES){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                String strURL = WEBURL+"doDownloadServlet"+"?TID="+TID+"&RESNAME="+RES;
                URL url = null;
                try {
                    url = new URL(strURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setReadTimeout(5000);
                    InputStream in = httpURLConnection.getInputStream();
                    String DIR = "/sdcard/bysjRes/";
                    File file=new File(DIR);
                    if (!file.exists() && !file.isDirectory()) {
                        // 创建目录
                        file.mkdir();
                    }
                    String filename=RES.substring(RES.lastIndexOf("\\")+1);
                    Log.i("download", filename);
                    FileOutputStream out = new FileOutputStream(DIR  + filename);
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while ((len=in.read(buffer))>0){
                        out.write(buffer,0,len);
                    }
                    in.close();
                    out.close();
                    httpURLConnection.disconnect();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
