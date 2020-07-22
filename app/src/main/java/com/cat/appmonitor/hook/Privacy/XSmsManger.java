package com.cat.appmonitor.hook.Privacy;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Utils;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XSmsManger{
    private static final String className = SmsManager.class.getName();
    private static XSmsManger xSmsManger;

    public static XSmsManger getInstance() {
        if (xSmsManger == null) {
            xSmsManger = new XSmsManger();
        }
        return xSmsManger;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "sendTextMessage", String.class, String.class, String.class,
                PendingIntent.class, PendingIntent.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        String number = param.args[0].toString();
                        String body = param.args[2].toString();
                        String callRef = Stack.getCallRef();

//                        param.args[0] = "10086";
//                        param.args[2] = "101";


//                        if (MainHook.context != null) {
//                            showBox(MainHook.context, packageParam.appInfo.name, number, body, packageParam.appInfo.icon);
//                        }
                        String log = Logger.Tolog("android.telephony.SmsManager.sendTextMessage()", "Address : " + number + ", Body : " + body, callRef);
                        Logger.log(log, packageParam.packageName);
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "sendMultipartTextMessage", String.class, String.class,
                ArrayList.class, ArrayList.class, ArrayList.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Utils.getSystemTime();
                        String number = param.args[0].toString();
                        ArrayList<String> body = (ArrayList) param.args[2];
                        String callRef = Stack.getCallRef();
                        String msg = "";
                        for (String str : body) {
                            msg += str;
                        }

//                        param.args[0] = "10086";
//                        param.args[2] = "101";



//                        if (MainHook.context != null) {
//                            showBox(MainHook.context, packageParam.appInfo.name, number, msg, packageParam.appInfo.icon);
//                        }
                        String log = Logger.Tolog("android.telephony.SmsManager.sendMultipartTextMessage()", "Address : " + number + ", Body : " + body, callRef);
                        Logger.log(log, packageParam.packageName);
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "sendDataMessage", String.class, String.class, short.class,
                byte[].class, PendingIntent.class, PendingIntent.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        String number = param.args[0].toString();
                        byte[] body = (byte[]) param.args[3];
                        String msg = new String(body);
                        String callRef = Stack.getCallRef();

//                        param.args[0] = "10086";
//                        param.args[3] = "101".getBytes();

//                        if (MainHook.context != null) {
//                            showBox(MainHook.context, packageParam.appInfo.name, number, new String(body), packageParam.appInfo.icon);
//                        }
                        String log = Logger.Tolog("android.telephony.SmsManager.sendDataMessage()", "Address : " + number + ", Body : " + msg, callRef);
                        Logger.log(log, packageParam.packageName);

                    }

                });

    }


    // TODO test this method
    public static void showBox(Context ctx, String appName, String number, String content, int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setMessage(String.valueOf(appName) + "试图向号码" + number + "发送短信[" + content + "], 是否阻止？").setCancelable(
                false).setIcon(icon).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(2003);
        dialog.show();
    }
}
