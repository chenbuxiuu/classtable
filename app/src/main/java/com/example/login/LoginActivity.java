package com.example.login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener,
        OnItemClickListener, OnDismissListener {

    protected static final String TAG = "LoginActivity";
    private LinearLayout mLoginLinearLayout; // 登录内容的容器
    private LinearLayout mUserIdLinearLayout; // 将下拉弹出窗口在此容器下方显示
    private Animation mTranslate; // 位移动画
    private Dialog mLoginingDlg; // 显示正在登录的Dialog
    private EditText mIdEditText; // 登录ID编辑框
    private EditText mPwdEditText; // 登录密码编辑框
    private ImageView mMoreUser; // 下拉图标
    private Button mLoginButton; // 登录按钮
    private ImageView mLoginMoreUserView; // 弹出下拉弹出窗的按钮
    private String mIdString;
    private String mPwdString;
    private ArrayList<User> mUsers; // 用户列表
    private ListView mUserIdListView; // 下拉弹出窗显示的ListView对象
    private MyAapter mAdapter; // ListView的监听器
    private PopupWindow mPop; // 下拉弹出窗
    private CheckBox remPwd;
    private CheckBox autoLogin;
    private SharedPreferences sp;
    private static String app_info = "app_info";
    private static String rempwd_info = "rem_pwd";
    private static String autoLogin_info = "autoLogin";
    final Data data=(Data)getApplication();
    private String WEBURL=data.WEBURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_login);

        initView();
        setListener();
        mLoginLinearLayout.startAnimation(mTranslate); // Y轴水平移动

		/* 获取已经保存好的用户密码 */
        mUsers = Utils.getUserList(LoginActivity.this);

        if (mUsers.size() > 0) {
            /* 将列表中的第一个user显示在编辑框 */
            mIdEditText.setText(mUsers.get(0).getId());
            mPwdEditText.setText(mUsers.get(0).getPwd());
        }

        LinearLayout parent = (LinearLayout) getLayoutInflater().inflate(
                R.layout.userifo_listview, null);
        mUserIdListView = (ListView) parent.findViewById(android.R.id.list);
        parent.removeView(mUserIdListView); // 必须脱离父子关系,不然会报错
        mUserIdListView.setOnItemClickListener(this); // 设置点击事
        mAdapter = new MyAapter(mUsers);
        mUserIdListView.setAdapter(mAdapter);
        ExitApplication.getInstance().addActivity(this);

        //是否自动登录并且验证账号密码
        if (sp.getBoolean(rempwd_info, false)) {
            if (sp.getBoolean(autoLogin_info, false)) {
                doWeb(mUsers.get(0).getId(), mUsers.get(0).getPwd());
            }
        }
    }

    /*登录*/
    public void doWeb(final String user, final String pwd) {
        showLoginingDlg();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String strURL = WEBURL+"stulogin?LoginName=" +
                        user + "&" + "LoginPassWord=" + pwd+ "&" + "Request=ClassTable";
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
                    final ArrayList<String> result=temp_result;

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (result.get(0).equals("success")) {
                                try {
                                    Utils.saveClassTable(user,result);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                closeLoginingDlg();
                            }
                        }
                    });
                }catch (Exception e) {
                    closeLoginingDlg();
                    e.printStackTrace();
                }
            }
        });
        try {
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ListView的适配器 */
    class MyAapter extends ArrayAdapter<User> {

        public MyAapter(ArrayList<User> users) {
            super(LoginActivity.this, 0, users);
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.listview_item, null);
            }

            TextView userIdText = (TextView) convertView
                    .findViewById(R.id.listview_userid);
            userIdText.setText(getItem(position).getId());

            ImageView deleteUser = (ImageView) convertView
                    .findViewById(R.id.login_delete_user);
            deleteUser.setOnClickListener(new OnClickListener() {
                // 点击删除deleteUser时,在mUsers中删除选中的元素
                @Override
                public void onClick(View v) {

                    if (getItem(position).getId().equals(mIdString)) {
                        // 如果要删除的用户Id和Id编辑框当前值相等，则清空
                        mIdString = "";
                        mPwdString = "";
                        mIdEditText.setText(mIdString);
                        mPwdEditText.setText(mPwdString);
                        if (mUsers.size() == 1) {
                            mPop.dismiss();
                        }
                    }
                    mUsers.remove(getItem(position));
                    mAdapter.notifyDataSetChanged(); // 更新ListView
                }
            });
            return convertView;
        }

    }

    private void setListener() {
        mIdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mIdString = s.toString();
                mPwdEditText.setText("");
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mPwdEditText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mPwdString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mLoginButton.setOnClickListener(this);
        mLoginMoreUserView.setOnClickListener(this);
    }

    private void initView() {
        mIdEditText = (EditText) findViewById(R.id.login_edtId);
        mPwdEditText = (EditText) findViewById(R.id.login_edtPwd);
        mMoreUser = (ImageView) findViewById(R.id.login_more_user);
        mLoginButton = (Button) findViewById(R.id.login_btnLogin);
        mLoginMoreUserView = (ImageView) findViewById(R.id.login_more_user);
        mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
        mUserIdLinearLayout = (LinearLayout) findViewById(R.id.userId_LinearLayout);
        remPwd = (CheckBox) findViewById(R.id.rem_pwd);
        autoLogin = (CheckBox) findViewById(R.id.auto_login);
        sp = this.getSharedPreferences(app_info, Context.MODE_APPEND);
        autoLogin.setChecked(true);
        remPwd.setChecked(true);
        if (sp.getBoolean(rempwd_info, false)) {
            remPwd.setChecked(true);
        } else {
            remPwd.setChecked(false);
        }
        if (sp.getBoolean(autoLogin_info, false)) {
            autoLogin.setChecked(true);
        } else {
            autoLogin.setChecked(false);
        }
        mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // 初始化动画对象
        initLoginingDlg();
    }

    public void initPop() {
        int width = mUserIdLinearLayout.getWidth() - 4;
        int height = LayoutParams.WRAP_CONTENT;
        mPop = new PopupWindow(mUserIdListView, width, height, true);
        mPop.setOnDismissListener(this);// 设置弹出窗口消失时监听器

        // 注意要加这句代码，点击弹出窗口其它区域才会让窗口消失
        mPop.setBackgroundDrawable(new ColorDrawable(0xffffffff));

    }

    /* 初始化正在登录对话框 */
    private void initLoginingDlg() {

        mLoginingDlg = new Dialog(this, R.style.loginingDlg);
        mLoginingDlg.setContentView(R.layout.logining_dlg);

        Window window = mLoginingDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        // 获取和mLoginingDlg关联的当前窗口的属性，从而设置它在屏幕中显示的位置

        // 获取屏幕的高宽
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int cxScreen = dm.widthPixels;
        int cyScreen = dm.heightPixels;

        int height = (int) getResources().getDimension(
                R.dimen.loginingdlg_height);// 高42dp
        int lrMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_lr_margin); // 左右边沿10dp
        int topMargin = (int) getResources().getDimension(
                R.dimen.loginingdlg_top_margin); // 上沿20dp

        params.y = (-(cyScreen - height) / 2) + topMargin; // -199
        /* 对话框默认位置在屏幕中心,所以x,y表示此控件到"屏幕中心"的偏移量 */

        params.width = cxScreen;
        params.height = height;
        // width,height表示mLoginingDlg的实际大小

        mLoginingDlg.setCanceledOnTouchOutside(true); // 设置点击Dialog外部任意区域关闭Dialog
    }

    /* 显示正在登录对话框 */
    private void showLoginingDlg() {
        if (mLoginingDlg != null)
            mLoginingDlg.show();

    }

    /* 关闭正在登录对话框 */
    private void closeLoginingDlg() {
        if (mLoginingDlg != null && mLoginingDlg.isShowing())
            mLoginingDlg.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btnLogin:
                // 启动登录
                Log.i(TAG, mIdString + "  " + mPwdString);
                if (mIdString == null || mIdString.equals("")) { // 账号为空时
                    Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT)
                            .show();
                } else if (mPwdString == null || mPwdString.equals("")) {// 密码为空时
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
                            .show();
                } else {// 账号和密码都不为空时
                    showLoginingDlg();

                    boolean mIsSave = true;
                    if (autoLogin.isChecked()) {
                        saveCheckboxInfo(autoLogin_info, true);
                    } else {
                        saveCheckboxInfo(autoLogin_info, false);
                    }
                    try {
                        Log.i(TAG, "保存用户列表");
                        for (int i = 0; i < mUsers.size(); i++) { // 判断本地文档是否有此ID用户
                            User user = mUsers.get(i);
                            if (user.getId().equals(mIdString)) {
                                if (user.getPwd().equals("")) {
                                    mUsers.remove(i);
                                    mIsSave = true;
                                    break;
                                } else {
                                    mIsSave = false;
                                    break;
                                }
                            }
                        }
                        if (mIsSave) { // 将新用户加入users
                            User user;
                            if (remPwd.isChecked()) {
                                user = new User(mIdString, mPwdString);
                                mUsers.add(0, user);
                                saveCheckboxInfo(rempwd_info, true);
                            } else {
                                user = new User(mIdString, "");
                                mUsers.add(0, user);
                                saveCheckboxInfo(rempwd_info, false);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    doWeb(mIdString, mPwdString);
                }
                break;
            case R.id.login_more_user: // 当点击下拉栏
                if (mPop == null) {
                    initPop();
                }
                if (!mPop.isShowing() && mUsers.size() > 0) {
                    // Log.i(TAG, "切换为角向上图标");
                    mMoreUser.setImageResource(R.drawable.login_more_down); // 切换图标
                    mPop.showAsDropDown(mUserIdLinearLayout, 2, 1); // 显示弹出窗口
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mIdEditText.setText(mUsers.get(position).getId());
        mPwdEditText.setText(mUsers.get(position).getPwd());
        mUsers.add(0, mUsers.get(position));
        mUsers.remove(position + 1);
        mPop.dismiss();
    }

    /* PopupWindow对象dismiss时的事件 */
    @Override
    public void onDismiss() {
        // Log.i(TAG, "切换为角向下图标");
        mMoreUser.setImageResource(R.drawable.login_more_up);
    }

    /* 退出此Activity时保存users */
    @Override
    public void onPause() {
        super.onPause();
        try {
            Utils.saveUserList(LoginActivity.this, mUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.addCategory(Intent.CATEGORY_HOME);
//            startActivity(home);
            ExitApplication.getInstance().exit(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void saveCheckboxInfo(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putBoolean(key, value);
        editor.commit();
    }
}

