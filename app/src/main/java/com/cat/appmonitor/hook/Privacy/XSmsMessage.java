package com.cat.appmonitor.hook.Privacy;

import android.telephony.SmsMessage;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XSmsMessage{
    private static final String className = "android.telephony.SmsMessage";
    private static XSmsMessage xSmsMessage;

    public static XSmsMessage getInstance() {
        if (xSmsMessage == null) {
            xSmsMessage = new XSmsMessage();
        }
        return xSmsMessage;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "createFromPdu", byte[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        SmsMessage smsMessage = (SmsMessage) param.getResult();
                        String addr = smsMessage.getDisplayOriginatingAddress();
                        String body = smsMessage.getDisplayMessageBody();
                        String callRef = Stack.getCallRef();


                        String log = Logger.Tolog("android.telephony.SmsManager.createFromPdu()", "Address : " + addr + ", Body : " + body, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
