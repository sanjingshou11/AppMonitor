package com.cat.appmonitor.hook;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.cat.appmonitor.BuildConfig;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook extends XC_MethodHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public static XSharedPreferences sPrefs;

    public void initZygote(StartupParam startupParam) throws Throwable {
        sPrefs = new XSharedPreferences("com.cat.appmonitor", "pkgs");
        sPrefs.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                if (context != null) {
                    loadPackageParam.classLoader = context.getClassLoader();
                    try {
                        //XposedBridge.log("invokeHandleHookMethod before");
                        invokeHandleHookMethod(
                                context, BuildConfig.APPLICATION_ID,
                                BuildConfig.APPLICATION_ID + ".hook.HookerDispatcher",
                                "handleLoadPackage", loadPackageParam);
                        //XposedBridge.log("invokeHandleHookMethod After");
                    } catch (Throwable error) {
                        error.printStackTrace();
                    }
                }
            }
        });
    }

    private void invokeHandleHookMethod(
            Context context,
            String modulePackageName,
            String handleHookClass,
            String handleHookMethod,
            XC_LoadPackage.LoadPackageParam loadPackageParam
    ) throws Throwable {
        // 原来的两种方式不是很好,改用这种新的方式
        File apkFile = findApkFile(context, modulePackageName);
        if (apkFile == null) {
            throw new RuntimeException("Cannot find the module APK.");
        }
        //XposedBridge.log("apkfile " + apkFile.getAbsolutePath());
        filterNotify(loadPackageParam);
        // 加载指定的hook逻辑处理类，并调用它的handleHook方法
        PathClassLoader pathClassLoader =
                new PathClassLoader(apkFile.getAbsolutePath(), ClassLoader.getSystemClassLoader());
        Class<?> cls = Class.forName(handleHookClass, true, pathClassLoader);
        Object instance = cls.newInstance();
        Method method = cls.getDeclaredMethod(handleHookMethod,
                Context.class, XC_LoadPackage.LoadPackageParam.class);
        method.invoke(instance, context, loadPackageParam);
    }

    private void filterNotify(XC_LoadPackage.LoadPackageParam lpparam)
            throws ClassNotFoundException {
        if("de.robv.android.xposed.installer".equals(lpparam.packageName)){
            XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("de.robv.android.xposed.installer.util.NotificationUtil"),
                    "showModulesUpdatedNotification", new XC_MethodHook() {
                        @Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(new Object());
                        }

                        @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
        }
    }
    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     *
     * @param context           context参数
     * @param modulePackageName 当前模块包名
     * @return return apk file
     */
    private File findApkFile(Context context, String modulePackageName) {
        if (context == null) {
            return null;
        }
        try {
            Context moduleContext = context.createPackageContext(
                    modulePackageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            String apkPath = moduleContext.getPackageCodePath();
            return new File(apkPath);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}



//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if (param.hasThrowable()){
//                    return;
//                }
//
//                Class<?> cls = (Class<?>) param.getResult();
//                String name = cls.getName();
//                if (targetCls.equals(name)){
//                    findAndHookMethod(cls, targerMethod, String.class, new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                        }
//
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//
//                        }
//                    });
//                }
//            }
//        });