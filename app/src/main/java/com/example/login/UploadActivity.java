package com.example.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class UploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    private static String Checkbox_info = "checkbox";
    private static String autoLogin_info = "autoLogin";
    private Spinner spinner;
    private Button chooseFile_bt;
    private Button upload_bt;
    private TextView fileName_tv;
    private String uploadFilePathName = "";
    private String teacherName = "";
    final Data data=(Data)getApplication();
    private String WEBURL=data.WEBURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_upload);


        fileName_tv = (TextView) findViewById(R.id.fileName_tv);
        initSpinner();
        initFileChooseBt();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.upload_layout);
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

    public void initSpinner() {
        spinner = (Spinner) findViewById(R.id.teacher_spinner);
        String[] arr = getTeacherList(Utils.getUserList(UploadActivity.this).get(0).getId());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        spinner.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                teacherName = spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initFileChooseBt() {
        chooseFile_bt = (Button) findViewById(R.id.chooseFile_bt);
        chooseFile_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    public void initUploadBt() {
        upload_bt = (Button) findViewById(R.id.upload_bt);
        upload_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "开始上传", Toast.LENGTH_SHORT).show();
                File file = new File(uploadFilePathName);
                try {
                    uploadFromBySocket(teacherName, file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.upload_layout);
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
            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.notice) {
            Toast.makeText(getApplicationContext(), "课程通知",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadActivity.this, CourseNotice.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.upload) {
            Toast.makeText(getApplicationContext(), "上传作业",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.download) {
            Toast.makeText(getApplicationContext(), "下载资源",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadActivity.this, DownloadActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about) {
            Toast.makeText(getApplicationContext(), "关于",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadActivity.this, About.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.upload_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitApp() {
        AlertDialog.Builder isExit = new AlertDialog.Builder(this).setTitle("确认退出？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        ExitApplication.getInstance().exit(UploadActivity.this);
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
                        Intent intent = new Intent(UploadActivity.this, LoginActivity.class);
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

    /**
     * 上传文件
     */
    public void uploadFromBySocket(final String TeacherName, final File uploadFile) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String BOUNDARY = "------WebKitFormBoundarydReYwO1znlcqcjfA";
                String urlStr = WEBURL+"UploadServlet";
                StringBuilder sb = new StringBuilder();
                /**
                 * 普通的表单数据
                 */
                String[] params = {"TID", "select", "notes"};
                //1 要这么写才能正确发送第一个数据
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("\r\n");

                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + params[0]
                        + "\"" + "\r\n");
                sb.append("\r\n");
                sb.append(TeacherName + "_work" + "\r\n");

                //2
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + params[1]
                        + "\"" + "\r\n");
                sb.append("\r\n");
                sb.append("select" + "\r\n");

                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + params[2]
                        + "\"" + "\r\n");
                sb.append("\r\n");
                sb.append("notes" + "\r\n");

                /**
                 * 上传文件的头
                 */
                String fileFormName = "upload_file";
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + fileFormName
                        + "\"; filename=\"" + uploadFile.getName() + "\"" + "\r\n");
                sb.append("\r\n");
                try {
                    byte[] headerInfo = sb.toString().getBytes("UTF-8");
                    byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

                    URL url = new URL(urlStr);
                    Socket socket = new Socket(url.getHost(), url.getPort());
                    OutputStream os = socket.getOutputStream();
                    PrintStream ps = new PrintStream(os, true, "UTF-8");

                    // 写出请求头
                    ps.println("POST " + urlStr + " HTTP/1.1");
                    ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
                    ps.println("Content-Length: "
                            + String.valueOf(headerInfo.length + uploadFile.length()
                            + endInfo.length));
                    ps.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

                    InputStream in = new FileInputStream(uploadFile);
                    // 写出数据
                    os.write(headerInfo);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1)
                        os.write(buf, 0, len);

                    os.write(endInfo);

                    InputStreamReader inReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inReader);
                    String readline = null;
                    readline = bufferedReader.readLine();
                    Log.i("upload", readline);
                    final String statusCode = readline;
                    UploadActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (statusCode.equals("HTTP/1.1 200 OK")) {
                                Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    while ((readline=bufferedReader.readLine())!=null){
//                        Log.i("upload",readline);
//                    }

                    in.close();
                    os.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    public void openFileChooser() {
        int FILE_SELECT_CODE = 0X111;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
//           String uploadUri=uri.getPath().toString();
            String org = uploadFilePathName = getPath(getApplicationContext(), uri);
//            fileName_tv.setText(uploadFilePathName);
            int cut = uploadFilePathName.lastIndexOf("/");
            String uploadFileName = uploadFilePathName.substring(cut + 1);
            fileName_tv.setText(org + "\n\n" + uploadFileName);
            initUploadBt();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                /**此处适配华为P7*/
                else if ("9016-4EF8".equalsIgnoreCase(type)) {
                    String s = "/storage/sdcard1";
                    return s + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
