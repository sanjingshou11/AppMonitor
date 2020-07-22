package com.cat.appmonitor.hook.File;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Utils;

import java.io.File;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XBaseDexClassLoader{
    private static final String className = BaseDexClassLoader.class.getName();
    private static XBaseDexClassLoader classLoadHook;

    public static XBaseDexClassLoader getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XBaseDexClassLoader();
        }
        return classLoadHook;
    }

    public boolean isstartsWithPath(String dexpath){

        return (dexpath.startsWith("/system/framework/") || dexpath.startsWith("/data/app/") || dexpath.startsWith("/system/lib/"));

    }
    // public BaseDexClassLoader(String	dexPath,File optimizedDirectory, String	libraryPath, ClassLoader parent)
    // libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
    // http://developer.android.com/reference/dalvik/system/BaseDexClassLoader.html
    // DexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent)
    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookConstructor(DexClassLoader.class.getName(), packageParam.classLoader,
                String.class, String.class, String.class,
                ClassLoader.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        String dexPath = (String) param.args[0];
                        String optimizedDir = (String) param.args[1];
                        String libPath = (String) param.args[2];
                        ClassLoader parent = (ClassLoader) param.args[3];

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("dalvik.system.DexClassLoader.<init>", "dexPath: " + dexPath +
                                ", optimizedDir: " +  optimizedDir + ", libPath: " +
                                libPath + ", parent: " + parent, callRef);

                        Logger.log(log, packageParam.packageName);



                        if (new File(dexPath).exists()){
                            Utils.writeFile(packageParam.packageName, dexPath);
                        }
                    }
                });

//        /**
//         *     public DexPathList(ClassLoader definingContext, String dexPath, String libraryPath, File optimizedDirectory)
//         */
//        XposedHelpers.findAndHookConstructor("dalvik.system.DexPathList", packageParam.classLoader,
//                ClassLoader.class, String.class, String.class,
//                File.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        String time = Utils.getSystemTime();
//                        ClassLoader parent = (ClassLoader) param.args[0];
//                        String dexPath = (String) param.args[1];
//                        String libPath = (String) param.args[2];
//                        File optimizedDir = (File) param.args[3];
//
//                        //过滤系统路径
//                        if(isstartsWithPath(dexPath))
//                            return;
//
//                        String callRef = Stack.getCallRef();
//                        String log = Logger.Tolog("dalvik.system.DexPathList<init>", "dexPath: " + dexPath +
//                                ", optimizedDir: " +  optimizedDir + ", libPath: " +
//                                libPath + ", parent: " + parent, callRef);
//
//                        Logger.log(log, packageParam.packageName);
//                        if (new File(dexPath).exists()){
//                            Utils.writeFile(packageParam.packageName, dexPath);
//                        }
//                    }
//                });

        /**
//         * private static int openDexFile(String sourceName, String outputName, int flags)
//         */
//        XposedHelpers.findAndHookMethod("dalvik.system.DexFile", packageParam.classLoader, "openDexFile",
//                String.class, String.class, int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                String sourceName = (String) param.args[0];
//                String outputName = (String) param.args[1];
//
//                //过滤系统路径
//                if(isstartsWithPath(sourceName))
//                    return;
//
//                String callRef = Stack.getCallRef();
//                String log = Logger.Tolog("dalvik.system.DexFile.openDexFile()", "dexPath: " + sourceName +
//                        ", optimizedDir: " +  outputName, callRef);
//
//                Logger.log(log, packageParam.packageName);
//                if (new File(sourceName).exists()){
//                    Utils.writeFile(packageParam.packageName, sourceName);
//                }
//            }
//
//        });

        XposedHelpers.findAndHookMethod(Runtime.class.getName(), packageParam.classLoader,
                "doLoad", String.class, ClassLoader.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String soName = (String) param.args[0];

                String callRef = Stack.getCallRef();

                String log = Logger.Tolog("java.lang.Runtime.doLoad()", "soName: " + soName, callRef);
                Logger.log(log, packageParam.packageName);

                if (isstartsWithPath(soName))
                    return;

                if (new File(soName).exists()){
                    Utils.writeFile(packageParam.packageName, soName);
                }
            }
        });

    }

}


//        /**
//         * Hook LoadClass 进而hook其它dex中的方法
//         */
//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass",
//                String.class, new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if (param.hasThrowable()){
//                    return;
//                }
//
//                Class<?> cls = (Class<?>) param.getResult();
//                String name = cls.getName();
//                if (targetCls.equals(name)){
//                    XposedHelpers.findAndHookMethod(cls, targerMethod, String.class, new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                        }
//
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//
//                        }
//                    });
//                }
//            }
//        });