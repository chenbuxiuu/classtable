package com.example.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class Utils {

    private static final String FILENAME = "userinfo.json";
    private static final String DIR = "/sdcard/bysj/";
    private static final String TAG = "Utils";

    static {
        File dirFirstFolder = new File(DIR);
        if (!dirFirstFolder.exists()) {
            dirFirstFolder.mkdirs();
        }
    }


    public static void saveUserList(Context context, ArrayList<User> users)
            throws Exception {

        Log.i(TAG, "正在保存");
        Writer writer = null;
        OutputStream out = null;
        JSONArray array = new JSONArray();
        for (User user : users) {
            array.put(user.toJSON());
        }
        try {
            out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            Log.i(TAG, "json:" + array.toString());
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }

    }


    public static ArrayList<User> getUserList(Context context) {

        FileInputStream in = null;
        ArrayList<User> users = new ArrayList<User>();
        try {

            in = context.openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            JSONArray jsonArray = new JSONArray();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            Log.i(TAG, jsonString.toString());
            jsonArray = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue(); // 把字符串转换成JSONArray对象
            for (int i = 0; i < jsonArray.length(); i++) {
                User user = new User(jsonArray.getJSONObject(i));
                users.add(user);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void saveClassTable(String user, ArrayList<String> list) throws Exception {
        Log.i(TAG, "正在保存");
        OutputStreamWriter writer = null;
        OutputStream out = null;
        try {
//            out = context.openFileOutput(DIR + user, Context.MODE_PRIVATE);
            out=new FileOutputStream(DIR+user);
            writer = new OutputStreamWriter(out,"UTF-8");
            for (int i = 1; i < list.size(); i++) {
                writer.write(list.get(i));
                writer.write("\n");
            }
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static ArrayList<String> getClassTable(String user){
        FileInputStream in = null;
        ArrayList<String> classTable=new ArrayList<String>();
        try {

//            in = context.openFileInput(DIR+user);
            in=new FileInputStream(DIR+user);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
               classTable.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  classTable;
    }

    public static void saveClassNotice(String user, ArrayList<String> list) throws Exception {
        Log.i(TAG, "正在保存");
        OutputStreamWriter writer = null;
        OutputStream out = null;
        try {
//            out = context.openFileOutput(DIR + user, Context.MODE_PRIVATE);
            out=new FileOutputStream(DIR+user+"_notice");
            writer = new OutputStreamWriter(out,"UTF-8");
            for (int i = 1; i < list.size(); i++) {
                String s=list.get(i);
                String[] strings=s.split(" ");
                if(!strings[strings.length-1].equals("null")&&!strings[strings.length-1].equals(" ")){
                    writer.write(s);
                    writer.write("\n");
                }
            }
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static ArrayList<String> getClassNotice(String user){
        FileInputStream in = null;
        ArrayList<String> classTable=new ArrayList<String>();
        try {

//            in = context.openFileInput(DIR+user);
            in=new FileInputStream(DIR+user+"_notice");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                classTable.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  classTable;
    }
}
