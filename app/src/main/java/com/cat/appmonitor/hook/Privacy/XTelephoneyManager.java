package com.cat.appmonitor.hook.Privacy;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XTelephoneyManager{
    private static final String className = "android.telephony.TelephonyManager";
    private static XTelephoneyManager xTelephoneyManager;

    public static XTelephoneyManager getInstance() {
        if (xTelephoneyManager == null) {
            xTelephoneyManager = new XTelephoneyManager();
        }
        return xTelephoneyManager;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getDeviceId",
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){
//                        param.setResult("356357045618430");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.telephony.TelephonyManager.getDeviceId()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getLine1Number", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.setResult("13826290651");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.telephony.TelephonyManager.getLine1Number()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getSubscriberId", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){
                        param.setResult("460006203280876");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.telephony.TelephonyManager.getSubscriberId()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getNetworkOperatorName", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.getResult();
                        param.setResult("46001");
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.telephony.TelephonyManager.getNetworkOperatorName()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }

                });
    }

}
