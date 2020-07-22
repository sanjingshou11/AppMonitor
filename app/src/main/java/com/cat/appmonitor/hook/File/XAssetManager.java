package com.cat.appmonitor.hook.File;

import android.content.res.AssetManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XAssetManager{
    private static final String className = AssetManager.class.getName();
    private static XAssetManager xAssetManager;

    public static XAssetManager getInstance() {
        if (xAssetManager == null) {
            xAssetManager = new XAssetManager();
        }
        return xAssetManager;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "open",
                String.class, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        String fileName = (String) param.args[0];
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.content.res.AssetManager.open()", fileName, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
