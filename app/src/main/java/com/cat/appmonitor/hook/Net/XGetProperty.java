package com.cat.appmonitor.hook.Net;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/*
Hook getProperty和setProperty 将app代理设置为空
 */
public class XGetProperty{

    public static XGetProperty xGetProperty = null;

    public static XGetProperty getInstance() {
        if (xGetProperty == null) {
            xGetProperty = new XGetProperty();
        }
        return xGetProperty;
    }

    //Hook手机设置代理
    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(System.class.getName(), packageParam.classLoader,"getProperty",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String arg = (String) param.args[0];
                        if (arg.equals("http.proxyHost")){
                            param.setResult("");
                        }else if (arg.equals("http.proxyPort")){
                            param.setResult("-1");
                        }

                    }
                });


        XposedHelpers.findAndHookMethod(System.class.getName(), packageParam.classLoader,"setProperty",
                String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String arg = (String) param.args[0];
                        if (arg.equals("http.proxyHost")){
                            param.args[1] = "";
                        }else if (arg.equals("http.proxyPort")){
                            param.args[1] = "-1";
                        }
                    }
                });
    }

}
