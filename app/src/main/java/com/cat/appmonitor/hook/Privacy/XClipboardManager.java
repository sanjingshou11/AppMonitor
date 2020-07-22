package com.cat.appmonitor.hook.Privacy;

import android.content.ClipData;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XClipboardManager{

    private static final String className = "android.content.ClipboardManager";
    private static XClipboardManager xClipboardManager = null;

    public static XClipboardManager getInstance() {
        if (xClipboardManager == null) {
            xClipboardManager = new XClipboardManager();
        }
        return xClipboardManager;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        //setPrimaryClip(ClipData clip)
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
            "setPrimaryClip", ClipData.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    ClipData clipData = (ClipData)param.args[0];
                    String text = clipData.getItemAt(0).getText().toString();

                    //写入log文件
                    String callRef = Stack.getCallRef();
                    String log = Logger.Tolog("android.content.ClipboardManager.setPrimaryClip()", "Text : " + text, callRef);
                    Logger.log(log, packageParam.packageName);
                }
            });

        //getPrimaryClip()
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getPrimaryClip", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        ClipData clipData = (ClipData)param.getResult();
                        String text = clipData.getItemAt(0).getText().toString();

                        //写入log文件
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.content.ClipboardManager.getPrimaryClip()", "Text : " + text, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

    }
}
