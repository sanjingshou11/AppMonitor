package com.cat.appmonitor.util;

import com.cat.appmonitor.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    
    public static String getSystemTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd----hh:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String dateTime = sDateFormat.format(date);
        return dateTime;
    }

    //写log文件
    public static void writeLog(String pkgName, String log){

            String FilePath = "/data/data/" + pkgName + "/Appmonitor/" + Config.LOG_FILE;
            FileIOUtils.writeFileFromString(FilePath, log + "\r\n", true);

    }

    //写文件
    public static void writeFile(String pkgName, String filePath){

        InputStream in = null;
        OutputStream out = null;
        String outDir = "/data/data/" + pkgName + "/Appmonitor/";
        String outPath = outDir  + File.separator+ filePath;
        int i = 0;

        while (new File(outPath).exists()) {
            filePath += i;
            i++;
        }
        try {
            in = new FileInputStream(filePath);
            FileIOUtils.writeFileFromIS(outPath, in);
//            out = new FileOutputStream(outPath);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            in.close();
//            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getCall(String callRef){
        String[] clsss = callRef.split(" <- ");
        return clsss[1];
    }

    public static String getClsName(String Call){
        String method = getMethodName(Call);
        return Call.substring(0, Call.length() - method.length()-1);
    }

    public static String getMethodName(String Call){
        String[] method = Call.split("\\.");
        return method[method.length - 1];
    }

}
