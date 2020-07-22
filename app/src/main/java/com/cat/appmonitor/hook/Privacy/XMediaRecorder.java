package com.cat.appmonitor.hook.Privacy;

import android.media.MediaRecorder;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XMediaRecorder{

    private static final String className = MediaRecorder.class.getName();
    private static XMediaRecorder xMediaRecorder;

    public static XMediaRecorder getInstance() {
        if (xMediaRecorder == null) {
            xMediaRecorder = new XMediaRecorder();
        }
        return xMediaRecorder;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "start", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

                String callRef = Stack.getCallRef();
                String log = Logger.Tolog("android.media.MediaRecorder.start()", "", callRef);
                Logger.log(log, packageParam.packageName);
            }
        });
    }
}
