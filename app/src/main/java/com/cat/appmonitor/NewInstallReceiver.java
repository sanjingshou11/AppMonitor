package com.cat.appmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.cat.appmonitor.util.FileIOUtils;

import java.util.HashSet;
import java.util.Set;

public class NewInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //接收广播：设备上新安装了一个应用程序包。
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString().substring(8);
            if (packageName.equals(context.getPackageName())) {
                return;
            }
            FileIOUtils.writeFileFromString(Config.SD_SETTING_FILE, packageName + "\n", true);
            FileIOUtils.writeFileFromString(Config.SETTING_FILE, packageName + "\n", true);

            ////适配android4.4
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                SharedPreferences sp = context.getSharedPreferences("pkgs", Context.MODE_WORLD_READABLE);
                Set<String> pkgSet = sp.getStringSet("pkgs", null);
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(packageName);

                if (pkgSet == null) {
                    pkgSet = hashSet;
                } else {
                    pkgSet.addAll(hashSet);
                }

                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.putStringSet("pkgs", pkgSet);
                editor.apply();
            }

        }

        //接收广播：设备上删除了一个应用程序包。
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString().substring(8);

            String pkgs = FileIOUtils.readFile2String(Config.SD_SETTING_FILE);

            if (pkgs != null) {
                HashSet<String> sets = new HashSet<>();
                String[] apps = pkgs.split("\n");
                for (String app : apps) {
                    sets.add(app);
                }
                if (sets.contains(packageName)) {
                    sets.remove(packageName);
                }
                String appss = "";
                for (String app : sets) {
                    app += "\n";
                    appss += app;
                }
                FileIOUtils.writeFileFromString(Config.SD_SETTING_FILE, appss, false);
                FileIOUtils.writeFileFromString(Config.SETTING_FILE, appss, false);
            }

            //适配android4.4
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                SharedPreferences sp = context.getSharedPreferences("pkgs", Context.MODE_WORLD_READABLE);
                Set<String> pkgSet = sp.getStringSet("pkgs", null);
                if (pkgSet == null) {
                    return;
                }
                pkgSet.remove(packageName);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.putStringSet("pkgs", pkgSet);
                editor.apply();
            }


        }
    }
}
