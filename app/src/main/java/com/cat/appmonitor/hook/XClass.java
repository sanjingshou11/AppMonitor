package com.cat.appmonitor.hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XClass extends XHook {
    private static final String className = "java.lang.Class";
    private static XClass classLoadHook;

    public static XClass getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XClass();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "forName",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        String callRef = Stack.getCallRef();

                        //写入log文件
                        String log = Logger.Tolog("java.lang.Class.forName()", "name : " + name, callRef);
                        Logger.log(log, packageParam.packageName);

                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getMethod",
                String.class, Class[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];

                        Object obj = param.args[1];
                        String classname = "";
                        if (obj instanceof Class) {
                            classname = ((Class) obj).getName();
                        } else if (obj instanceof Class[] && ((Class[])obj).length > 0) {
                            classname = ((Class[])obj)[0].getName();
                        }
                        String callRef = Stack.getCallRef();


                        //写入log文件
                        String log = Logger.Tolog("java.lang.Class.getMethod()", "MethodName : " + name + ", ClassName : " + classname, callRef);
                        Logger.log(log, packageParam.packageName);

                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getDeclaredMethod",
                String.class, Class[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        Object obj = param.args[1];
                        String classname = "";
                        if (obj instanceof Class) {
                            classname = ((Class) obj).getName();
                        } else if (obj instanceof Class[] && ((Class[])obj).length > 0) {
                            classname = ((Class[])obj)[0].getName();
                        }

                        String callRef = Stack.getCallRef();
                        //写入log文件
                        String log = Logger.Tolog("java.lang.Class.getDeclaredMethod()", "MethodName : " + name + ", ClassName : " + classname, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
