package com.cat.appmonitor.hook;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.cat.appmonitor.Config;
import com.cat.appmonitor.hook.File.XAssetManager;
import com.cat.appmonitor.hook.File.XFile;
import com.cat.appmonitor.hook.Net.XGetProperty;
import com.cat.appmonitor.hook.Net.XHttpHook;
import com.cat.appmonitor.hook.Net.XWebView;
import com.cat.appmonitor.hook.Privacy.XAudioRecord;
import com.cat.appmonitor.hook.Privacy.XCamera;
import com.cat.appmonitor.hook.Privacy.XClipboardManager;
import com.cat.appmonitor.hook.Privacy.XContentResolver;
import com.cat.appmonitor.hook.Privacy.XMediaRecorder;
import com.cat.appmonitor.hook.Privacy.XSmsManger;
import com.cat.appmonitor.hook.Privacy.XSmsMessage;
import com.cat.appmonitor.hook.Privacy.XTelephoneyManager;
import com.cat.appmonitor.util.FileIOUtils;
import com.cat.appmonitor.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.cat.appmonitor.Config.SD_SETTING_FILE;
import static com.cat.appmonitor.Config.SETTING_FILE;

public class HookerDispatcher{
    public Context context;

    public void handleLoadPackage(Context context, XC_LoadPackage.LoadPackageParam lpparam) {
            StartHook(lpparam);
    }


    public void StartHook(XC_LoadPackage.LoadPackageParam loadPackageParam){
        //Log.d(Config.TAG, "StartHook: " + loadPackageParam.packageName);
        //忽略的应用
        ArrayList<String> ignoreApplicationList = new ArrayList<>();
        ignoreApplicationList.add("com.cat.appmonitor");
        ignoreApplicationList.add("de.robv.android.xposed.installer");
        if (ignoreApplicationList.contains(loadPackageParam.packageName)) {
            return;
        }

        //加载配置文件
        File xposedSettingsFile = null;
        Set appList = null;
        do {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查/data/local/tmp/
                xposedSettingsFile = new File(SETTING_FILE);
                if (xposedSettingsFile.exists() && xposedSettingsFile.canRead()){
                    XposedBridge.log(SETTING_FILE + " can read!");
                    break;
                }
            }else {
                appList = new XSharedPreferences("com.cat.appmonitor", "pkgs").getStringSet("pkgs", null);
                break;
            }

            // 检查sd卡存储
            xposedSettingsFile = new File(SD_SETTING_FILE);
            if (xposedSettingsFile.exists() && xposedSettingsFile.canRead()){
                XposedBridge.log(SETTING_FILE + " can read!");
                break;
            }
            xposedSettingsFile = null;
        } while (false);

        XposedBridge.log(loadPackageParam.packageName);

        if (appList == null){
            if (xposedSettingsFile == null) {
                Log.e(Config.TAG, "handleLoadPackage: xposedSettingsFile is null");
                return;
            }
            //读取配置文件
            String xposedSettingsFileContent = null;
            try {
                xposedSettingsFileContent = FileIOUtils.readFile2String(xposedSettingsFile);
                XposedBridge.log("Hook app " + xposedSettingsFileContent);
            } catch (Throwable throwable) {
                Log.e(Config.TAG, "handleLoadPackage: " + throwable.getMessage(), throwable);
                return;
            }
            if (xposedSettingsFileContent == null || xposedSettingsFileContent.isEmpty()) {
                Log.w(Config.TAG, "handleLoadPackage: xposedSettingsFileContent == null");
                return;
            }
            //判断是否需要hook
            appList = new HashSet<>();
            String[] pkgs = xposedSettingsFileContent.split("\n");
            for (String app: pkgs){
                appList.add(app);
            }
        }
        XposedBridge.log("pkgs " + appList.toString() + " " + loadPackageParam.packageName);
        if (!appList.contains(loadPackageParam.packageName)) {
            return;
        }

        Log.d(Config.TAG, "handleLoadPackage Hook " + loadPackageParam.packageName);

        XposedBridge.log(loadPackageParam.packageName);

        //创建存储log信息的文件夹和文件
        String packageDir = "/data/data/"+ loadPackageParam.packageName + "/Appmonitor";
        if(!FileUtils.createOrExistsDir(packageDir)){
            Log.d(Config.TAG, "handleLoadPackage: create " + packageDir + " failed!");
            return;
        }
        String logfile = packageDir + File.separator + Config.LOG_FILE;
        if (!FileUtils.createOrExistsFile(logfile)){
            Log.d(Config.TAG, "handleLoadPackage: create " + logfile + " failed!");
            return;
        }

//        findAndHookMethod(Application.class, "attach", Context.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        context = (Context)param.args[0];
//                    }
//                });

        // 隐藏xposed
        xposedHider.getInstance().hook(loadPackageParam);

        //NET
        XHttpHook.getInstance().hook(loadPackageParam);
        XWebView.getInstance().hook(loadPackageParam);
        XGetProperty.getInstance().hook(loadPackageParam);
        //XJustTrustMe.getInstance().hook(loadPackageParam);

        //File
        XFile.getInstance().hook(loadPackageParam);
        XAssetManager.getInstance().hook(loadPackageParam);
        //XBaseDexClassLoader.getInstance().hook(loadPackageParam);

        //Privacy
        XAudioRecord.getInstance().hook(loadPackageParam);
        XCamera.getInstance().hook(loadPackageParam);
        XClipboardManager.getInstance().hook(loadPackageParam);
        XContentResolver.getInstance().hook(loadPackageParam);
        XMediaRecorder.getInstance().hook(loadPackageParam);
        XSmsManger.getInstance().hook(loadPackageParam);
        XSmsMessage.getInstance().hook(loadPackageParam);
        XTelephoneyManager.getInstance().hook(loadPackageParam);

        //View
        //XViewGroup.getInstance().hook(loadPackageParam);

        //other
        XApplicationPackageManager.getInstance().hook(loadPackageParam);
        XBroadcastReceiver.getInstance().hook(loadPackageParam);
        XClass.getInstance().hook(loadPackageParam);
        XCryptor.getInstance().hook(loadPackageParam);
        XIntent.getInstance().hook(loadPackageParam);
        XNotificationManager.getInstance().hook(loadPackageParam);
        XProcessBuilder.getInstance().hook(loadPackageParam);
        XRuntime.getInstance().hook(loadPackageParam);
        XString.getInstance().hook(loadPackageParam);




    }
}
