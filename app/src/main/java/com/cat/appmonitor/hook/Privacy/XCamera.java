package com.cat.appmonitor.hook.Privacy;

import android.hardware.Camera;
import android.os.Build;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by CKCat on 2019/4/3.
 */

public class XCamera {
    //private static final String className = Camera.class.getName();
    private static XCamera xCamera;

    /*
    public final void takePicture (Camera.ShutterCallback shutter,
                Camera.PictureCallback raw,
                Camera.PictureCallback postview,
                Camera.PictureCallback jpeg)

    public final void takePicture (Camera.ShutterCallback shutter,
                Camera.PictureCallback raw,
                Camera.PictureCallback jpeg)

    public static Camera open ()
    public static Camera open (int cameraId)
     */
    public static XCamera getInstance() {
        if (xCamera == null) {
            xCamera = new XCamera();
        }
        return xCamera;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        String className = Camera.class.getName();
        if (Build.VERSION.SDK_INT < 21)
        {
            className = "android.graphics.Camera";
        }
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "open",
                int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        //写入log文件
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.graphics.Camera.open()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "open", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {

                        //写入log文件
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.graphics.Camera.open()", "", callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }
}
