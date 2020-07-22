package com.cat.appmonitor.hook.Privacy;

import android.media.AudioRecord;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XAudioRecord{

    private static final String className = AudioRecord.class.getName();
    private static XAudioRecord xAudioRecord;

    public static XAudioRecord getInstance() {
        if (xAudioRecord == null) {
            xAudioRecord = new XAudioRecord();
        }
        return xAudioRecord;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "startRecording", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("android.media.AudioRecord.startRecording()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
