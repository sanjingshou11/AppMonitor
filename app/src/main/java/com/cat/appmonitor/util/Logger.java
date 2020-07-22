package com.cat.appmonitor.util;

import android.util.Log;

import org.json.JSONObject;


public class Logger {


    public static final String TAG = "AppMonitor";


    public static void log(String str) {
        Log.d(TAG, str);

    }

    public static void log(String str, String pakeage) {
        Log.d(TAG, str);
        Utils.writeLog(pakeage, str);
    }

    public static String Tolog(String method, String params, String callRef) {
        try {
            String time = Utils.getSystemTime();
            JSONObject json = new JSONObject();
            json.put("time", time);
            json.put("fuction", method);
            json.put("param", params);
            json.put("callback", callRef);
            return json.toString();

        }catch (Exception e){

        }
        return "";
    }

    public static String Tolog(String method, String params, String result, String callRef) {
        try {
            String time = Utils.getSystemTime();
            JSONObject json = new JSONObject();
            json.put("time", time);
            json.put("fuction", method);
            json.put("param", params);
            json.put("result", result);
            json.put("callback", callRef);
            return json.toString();

        }catch (Exception e){

        }
        return "";
    }


}
