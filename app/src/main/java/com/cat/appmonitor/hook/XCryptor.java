package com.cat.appmonitor.hook;


import com.cat.appmonitor.util.HexDumper;
import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import java.security.MessageDigest;

import javax.crypto.Cipher;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XCryptor extends XHook {

    private static XCryptor xCryptor;

    public static XCryptor getInstance() {
        if (xCryptor == null) {
            xCryptor = new XCryptor();
        }
        return xCryptor;
    }
    @Override
    void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.DESKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] keybyte = new byte[8];
                    int offset = 0;

                    // 拷贝数据
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                        offset = (int)param.args[1];

                    System.arraycopy((byte[])param.args[0], offset, keybyte, 0, 8);

                    String key = HexDumper.dumpHexString(keybyte);
                    String callRef = Stack.getCallRef();
                    String log = Logger.Tolog("javax.crypto.spec.DESKeySpec.<init>", "DES KEY : " + key, callRef);
                    Logger.log(log, loadPackageParam.packageName);
                }
            });

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.DESedeKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] keybyte = new byte[24];
                    int offset = 0;

                    // 拷贝数据
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                        offset = (int)param.args[1];
                    System.arraycopy((byte[])param.args[0], offset, keybyte, 0, 24);

                    String key = HexDumper.dumpHexString(keybyte);
                    String callRef = Stack.getCallRef();
                    String log = Logger.Tolog("javax.crypto.spec.DESedeKeySpec.<init>", "3DES KEY : " + key, callRef);
                    Logger.log(log, loadPackageParam.packageName);
                }
            });

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.SecretKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    int offset = 0;
                    int size = 0;
                    String Algorithm;

                    if(param.args.length != 2)
                    {
                        offset = (int)param.args[1];
                        size = (int)param.args[2];
                        Algorithm = (String)param.args[3];
                    }else {
                        Algorithm = (String) param.args[1];
                        size = ((byte[])param.args[0]).length;
                    }

                    byte[] data = new byte[size];
                    System.arraycopy((byte[])param.args[0],offset,data,0,size);

                    String str ;
                    str = Algorithm + " Key : ";
                    String key = HexDumper.dumpHexString(data);

                    String callRef = Stack.getCallRef();
                    String log = Logger.Tolog("javax.crypto.spec.SecretKeySpec.<init>", str + key, callRef);
                    Logger.log(log, loadPackageParam.packageName);
                }
            });

            // IV 向量
            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.IvParameterSpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] IVByte;
                    byte[] tmp;
                    int offset = 0;
                    int size;
                    tmp = (byte[])param.args[0];
                    size = tmp.length;
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                    {
                        offset = (int)param.args[1];
                        size = (int)param.args[2];
                    }
                    IVByte = new byte[size];
                    System.arraycopy(tmp,offset,IVByte,0,size);


                    String key = HexDumper.dumpHexString(IVByte);

                    String callRef = Stack.getCallRef();
                    String log = Logger.Tolog("javax.crypto.spec.IvParameterSpec.<init>", "IV : " + key, callRef);
                    Logger.log(log, loadPackageParam.packageName);
                }
            });
            // XposedBridge.hookAllMethods(XposedHelpers.findClass("javax.crypto.Cipher",loadPackageParam.classLoader),"doFinal",new HookCipher(loadPackageParam.packageName,0));
            XposedBridge.hookAllMethods(XposedHelpers.findClass("javax.crypto.Cipher", loadPackageParam.classLoader),
                    "doFinal", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Cipher cip = (Cipher)param.thisObject;
                            if(param.args.length >= 1)
                            {
                                String str = cip.getAlgorithm() + " Data : ";

                                String Data = HexDumper.dumpHexString((byte[])param.args[0]);
                                String result = HexDumper.dumpHexString((byte[])param.getResult());

                                String callRef = Stack.getCallRef();
                                String log = Logger.Tolog("javax.crypto.Cipher.doFinal()", str + Data, "result: " + result, callRef);
                                Logger.log(log, loadPackageParam.packageName);
                            }
                        }
                    });

            XposedBridge.hookAllMethods(XposedHelpers.findClass("java.security.MessageDigest",loadPackageParam.classLoader), "update", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    MessageDigest md = (MessageDigest)param.thisObject;
                    String str = md.getAlgorithm() + " update data : ";
                    String callRef = Stack.getCallRef();

                    String Data = HexDumper.dumpHexString((byte[])param.args[0]);
                    String log = Logger.Tolog("java.security.MessageDigest.update()", str + Data, "result: ", callRef);
                    Logger.log(log, loadPackageParam.packageName);

                }
            });

            XposedBridge.hookAllMethods(XposedHelpers.findClass("java.security.MessageDigest", loadPackageParam.classLoader), "digest", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args.length >= 1)
                    {
                        MessageDigest md = (MessageDigest)param.thisObject;

                        String str;

                        str = md.getAlgorithm() + "  data : ";
                        String Data = HexDumper.dumpHexString((byte[])param.args[0]);
                        String result = HexDumper.dumpHexString((byte[])param.getResult());

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("java.security.MessageDigest.digest()", str + Data, "result : " + result, callRef);
                        Logger.log(log, loadPackageParam.packageName);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

