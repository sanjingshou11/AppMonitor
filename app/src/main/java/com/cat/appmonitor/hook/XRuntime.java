package com.cat.appmonitor.hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XRuntime extends XHook {

    private static final String className = "java.lang.Runtime";
    private static XRuntime classLoadHook;

    public static XRuntime getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XRuntime();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "exec",
                String[].class, String[].class, File.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String[] prog = (String[]) param.args[0];
                        String cmd = "";

                        for (String str : prog) {
                            cmd += str;
                            cmd += " ";
                        }

                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.Runtime.exec()", "cmd : " + cmd, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
