package com.cat.appmonitor.hook;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XApplicationPackageManager extends XHook {

    private static final String className = "android.app.ApplicationPackageManager";
    private static XApplicationPackageManager classLoadHook;

    public static XApplicationPackageManager getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XApplicationPackageManager();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "setComponentEnabledSetting", ComponentName.class, int.class,
                int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        int state = (Integer) param.args[1];
                        //当state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED 则隐藏图标
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.app.ApplicationPackageManager.setComponentEnabledSetting()", "state :" + state, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });


        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getInstalledPackages", Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        Object obj = param.getResult();
                        if (obj !=null && obj instanceof List) {
                            List<PackageInfo> list = (List<PackageInfo>) obj;
                            List<PackageInfo> tmp = (List<PackageInfo>) obj;
                            for (PackageInfo info : list) {

                                if (info.packageName.contains("xposed")
                                        || info.packageName.contains("com.cat.appmonitor")) {
                                    tmp.remove(info);
                                }
                            }
                            param.setResult(tmp);
                        }

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.app.ApplicationPackageManager.getInstalledPackages()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });


        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getInstalledApplications", Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        Object obj = param.getResult();
                        if (obj !=null && obj instanceof List) {
                            List<ApplicationInfo> list = (List<ApplicationInfo>) obj;
                            List<ApplicationInfo> tmp = (List<ApplicationInfo>) obj;

                            for (ApplicationInfo info : list) {
                                if (info.packageName.contains("xposed")
                                        || info.packageName.contains("com.cat.appmonitor")) {
                                    tmp.remove(info);
                                }
                            }
                            param.setResult(tmp);
                        }

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.app.ApplicationPackageManager.getInstalledApplications()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }
}
