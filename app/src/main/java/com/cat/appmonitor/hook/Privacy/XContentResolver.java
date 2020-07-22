package com.cat.appmonitor.hook.Privacy;

import android.content.ContentValues;
import android.net.Uri;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Utils;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XContentResolver {

    private static final String className = "android.content.ContentResolver";
    private static final String[] privacyUris = {
            "content://com.android.contacts",
            "content://sms",
            "content://mms-sms",
            "content://contacts/",
            "content://com.android.contacts",
            "content://call_log",
            "content://telephony",
            "content://browser/bookmarks"};
    private static List<String> logList = null;

    private static XContentResolver xContentResolver;

    public static XContentResolver getInstance() {
        if (xContentResolver == null) {
            xContentResolver = new XContentResolver();
        }
        return xContentResolver;
    }

    private boolean isUriAvailable(String uri) {
        String url = uri.toLowerCase();
        for (int i = 0; i < privacyUris.length; i++) {
            if (url.contains(privacyUris[i])) {
                return true;
            }
        }
        return false;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        logList = new ArrayList<String>();
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "query",
                Uri.class, String[].class, String.class, String[].class,
                String.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String uri = param.args[0].toString();
                        String selection = "";
                        if (param.args[2] != null) {
                            selection = (String) param.args[2];
                        }
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.content.ContentResolver.query()", "Uri : " + uri + ", selection : " + selection, callRef);
                        Logger.log(log, packageParam.packageName);


                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "insert",
                Uri.class, ContentValues.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String uri = (String) param.args[0];
                        String value = (String) param.args[1];
                        String callRef = Stack.getCallRef();
                        String time = Utils.getSystemTime();

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("Uri : " + uri)
                                .append(", value : " + value);

                        String log = Logger.Tolog("android.content.ContentResolver.insert()", logsb.toString(), callRef);
                        Logger.log(log, packageParam.packageName);

                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "delete",
                Uri.class, String.class, String[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String uri = (String) param.args[0];
                        String where = (String) param.args[1];
                        String selection = (String) param.args[2];
                        String callRef = Stack.getCallRef();

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("Uri : " + uri)
                                .append(", where : " + where)
                                .append(", selectionArgs : " + selection);
                        String log = Logger.Tolog("android.content.ContentResolver.delete()", logsb.toString(), callRef);
                        Logger.log(log, packageParam.packageName);

                    }
                });
    }

}
