package com.cat.appmonitor.hook.File;

import android.util.Log;

import com.cat.appmonitor.Config;
import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Utils;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XFile {
    private static final String className = File.class.getName();
    private static XFile xFile;

    public static XFile getInstance() {
        if (xFile == null) {
            xFile = new XFile();
        }
        return xFile;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        // file.delete()
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "delete", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                File file = (File) param.thisObject;
                String callRef = Stack.getCallRef();

                if (!file.exists() || file.isDirectory()){
                    return;
                }


                if (file.getName().endsWith(".bak"))
                    return;
                ContentInfoUtil util = new ContentInfoUtil();
                ContentInfo info = util.findMatch(file);
                if (info != null){
                    Log.d(Config.TAG, "writeFile /data/data/" + packageParam.packageName + "/Appmonitor/" + file.getAbsolutePath());
                    Utils.writeFile(packageParam.packageName, file.getAbsolutePath());
                }

                String log = Logger.Tolog("java.io.File.delete()", "filePath : " + file.getAbsolutePath(), callRef);
                Logger.log(log, packageParam.packageName);

            }
        });

//    //private void open(String name)
//    XposedHelpers.findAndHookMethod(FileInputStream.class.getName(), packageParam.classLoader,
//        "open", String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String name = (String) param.args[0];
//
//                Log.d(Global.TAG, "afterHookedMethod: open " + name);
//                File file = new File(name);
//                FileType fileType = FileTypeJudge.getType(file.getAbsolutePath());
//                if (fileType == FileType.dex || fileType==FileType.zip){
//                    Utils.writeFile(packageParam.packageName, name);
//                }
//                //super.afterHookedMethod(param);
//            }
//        });


    }
}
