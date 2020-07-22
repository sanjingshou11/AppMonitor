package com.cat.appmonitor.util;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by CKCat on 2019/1/15.
 */

public class HookUtils {
    public static String methodName = "";
    public static String packageName = "";
    public static XC_MethodHook Methodhook = new XC_MethodHook() {

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            //Logger.log("获取method Hook 成功");
            Object obj = param.getResult();
            if (obj instanceof String){//返回String的情况
                //打印log并写入日志
                String callRef = Stack.getCallRef();
                String log = Logger.Tolog(methodName, "null", (String) obj, callRef);
                Logger.log(log, packageName);

            }else if (obj instanceof InputStream){//返回InputStream的情况

//                StringBuilder response = new StringBuilder();
//                InputStream in = (InputStream)obj;
//                //下面对获取到的输入流进行读取
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//                //打印log并写入日志
//                String log = Logger.Tolog(methodName, response.toString(), callRef_);
//                Logger.log(log);
//                Utils.writeLog(packageName, log);
            }else if (obj instanceof JSONObject){//返回JSONObject的情况

                String callRef = Stack.getCallRef();
                String json = ((JSONObject)obj).toString();
                //打印log并写入日志
                String log = Logger.Tolog(methodName, "", json, callRef);
                Logger.log(log, packageName);
            }
        }
    };

    public static void HookSatckMethod(XC_LoadPackage.LoadPackageParam packageParam, String callRef){

        //继续hook调用该方法的函数，获取该方法所在的类，方法
        String Call = Utils.getCall(callRef);
        String cls = Utils.getClsName(Call);
        String method = Utils.getMethodName(Call);
        packageName = packageParam.packageName;
        methodName = method;

        //Log.d(Config.TAG, "HookSatckMethod: before " + cls + ", " + method);
        if (cls.equals("org.apache.http.impl.client.AbstractHttpClient"))
            return;

        //Log.d(Config.TAG, "HookSatckMethod: after " + cls + ", " + method);
        try{

            Class<?> clazz = XposedHelpers.findClass(cls, packageParam.classLoader);

            if (clazz != null){

                Method[] methods = clazz.getDeclaredMethods();

                for (Method m : methods) {

                    if (m.getName().equals(method)){

                        Class<?>[] getTypeParameters = m.getParameterTypes();
                        Class<?> resultType = m.getReturnType();
                        //如果函数返回类型为void则直接返回
                        if (resultType == Void.TYPE){
                            return ;
                        }

                        //继续hook上一层的方法
                        switch (getTypeParameters.length){
                            case 0:
                            {
                                //无参
                                //Logger.log("获取Parameters成功" + " Clazz: " +clazz.getName() + ", method: " + method);
                                XposedHelpers.findAndHookMethod(clazz, method, Methodhook);
                                break;
                            }
                            case 1:
                            {

                                XposedHelpers.findAndHookMethod(clazz, method, getTypeParameters[0], Methodhook);
                                break;
                            }
                            case 2:
                            {

                                XposedHelpers.findAndHookMethod(clazz, method, getTypeParameters[0],
                                        getTypeParameters[1], Methodhook);
                                break;
                            }
                            case 3:
                            {

                                XposedHelpers.findAndHookMethod(clazz, method, getTypeParameters[0],
                                        getTypeParameters[1], getTypeParameters[2], Methodhook);
                                break;
                            }
                            case 4:
                            {

                                XposedHelpers.findAndHookMethod(clazz, method, getTypeParameters[0],
                                        getTypeParameters[1], getTypeParameters[2], getTypeParameters[3], Methodhook);
                                break;

                            }
                            case 5:
                            {

                                XposedHelpers.findAndHookMethod(clazz, method, getTypeParameters[0],
                                        getTypeParameters[1], getTypeParameters[2], getTypeParameters[3],
                                        getTypeParameters[4], Methodhook);
                                break;
                            }
                            default:
                                break;
                        }

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
