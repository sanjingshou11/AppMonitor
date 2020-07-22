package com.cat.appmonitor;

import android.util.Log;

import com.jaredrummler.android.shell.Shell;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashSet;

public class Config {
    public static final String TAG = "AppMonitor";
    public static final String KW_XPOSED = "xposed";
    //path
    public static final String APPMONITOR_PATH = "/data/data/com.cat.appmonitor";
    public static final String SETTING_FILE = "/data/local/tmp/Appmonitor/setting.xml";


    public static final String SD_SETTING_FILE = "/sdcard/Appmonitor/setting.xml";
    public static final String COMM_DIR = "/data/local/tmp/Appmonitor";
    public static final String SD_COMM_DIR = "/sdcard/Appmonitor";
    public static final String LOG_FILE = "AppMonitor.log";


    //保存配置文件
    public static boolean saveConfig(File file, String config){
        try{
            FileUtils.write(file, config, "UTF-8");
            return true;
        }catch (Exception e){
            Log.d(Config.TAG, "saveConfig: " + e.getMessage());
            return false;
        }

    }

    public HashSet<String> readConfig(File file){
        try {

            String content = FileUtils.readFileToString(file, "UTF-8");
            if (content.isEmpty()){
                Log.d(TAG, "readConfig is Empty." );
                return null;
            }

            HashSet<String> set = new HashSet<>();
            for (String app: content.split("\n")){
                set.add(app);
            }
            return set;

        }catch (Exception e){
            Log.d(Config.TAG, "saveConfig: " + e.getMessage());
            return null;
        }
    }

    //创建/data/local/tmp/Appmonitor/setting.xml文件
    public static void createfile(){
        File fileDataLocalConfig = new File(Config.SETTING_FILE);

        // 使用Root命令进行创建
        if (!fileDataLocalConfig.exists()) {
            Shell.SU.run("mkdir " + "/data/local/tmp/Appmonitor");
            Shell.SU.run("touch " + Config.SETTING_FILE);
            Shell.SU.run("chmod 777 " + Config.SETTING_FILE);
        }

        // 文件依然还不存在
        if (!fileDataLocalConfig.exists()) {
            Log.e(Config.TAG, Config.SETTING_FILE + " not exist");
        }

        // 文件没有写的权限
        if (!fileDataLocalConfig.canWrite()) {
            Shell.SU.run("chmod 777 " + Config.SETTING_FILE);
        }
        // 文件依然没有写的权限
        if (!fileDataLocalConfig.canWrite()) {
            Log.e(Config.TAG, Config.SETTING_FILE + " can not write");
        }
    }
}
