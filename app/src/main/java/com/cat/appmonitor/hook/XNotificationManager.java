package com.cat.appmonitor.hook;

import android.app.Notification;
import android.app.NotificationManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
public class XNotificationManager extends XHook {

    private static final String className = NotificationManager.class.getName();
    private static XNotificationManager classLoadHook;

    public static XNotificationManager getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XNotificationManager();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "notify",
                int.class, Notification.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String notificationName = param.args[1].toString();
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.app.NotificationManager.notify()", "Notification :" + notificationName, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
