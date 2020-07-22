package com.cat.appmonitor.hook.View;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XViewGroup{
    private static final String className = ViewGroup.class.getName();
    private static XViewGroup xViewGroup;
    //android.view.WindowManagerImpl
    public static XViewGroup getInstance() {
        if (xViewGroup == null) {
            xViewGroup = new XViewGroup();
        }
        return xViewGroup;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "addView",
                View.class, Integer.TYPE, ViewGroup.LayoutParams.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        View view = (View) param.args[0];
                        String viewName = view.getClass().getName();

                        // TODO maybe read the view api to a list could be better ...
                        if (viewName.startsWith("android.widget.") || viewName.startsWith("android.view.")
                                || viewName.startsWith("android.support.v7.widget.") || viewName.startsWith("android.support.v7.internal.widget.")
                                || viewName.startsWith("com.android.internal.widget.") || viewName.startsWith("com.android.internal.view")) {
                            return;
                        }


                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.view.ViewGroup.addView()", "ViewName : " + viewName, callRef);
                        Logger.log(log, packageParam.packageName);

                    }

                });

                XposedHelpers.findAndHookMethod("android.view.WindowManagerImpl",packageParam.classLoader,
                        "addView", View.class, ViewGroup.LayoutParams.class,  new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

//                            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//                            wmParams.type = Build.VERSION.SDK_INT > 19 ? 2005 : 2002;  // 置于所有应用程序之上，状态栏之下
//                        LayoutParams(int w, int h, int _type, int _flags, int _format)
//                        各参数解释
//                                Width = m_int/10
//                        Height = 50
//                        Type = 2003 FIRST_SYSTEM_WINDOW | TYPE_APPLICATION_STARTING
//                        Flags = 262168
//                        以上两个参数决定了view层级以及是否可以点击等
//                                Format = -3
//                        此参数通常有以下3个选择
//                        TRANSLUCENT(半透明) = -3
//                        TRANSPARENT(透明) = -2
//                        OPAQUE(不透明) = -1
//                        Alpha(0~1)(透明~不透明)

                        //过滤掉系统View
                        View view = (View) param.args[0];
                        String viewName = view.getClass().getName();
                        if (viewName.startsWith("android.widget.") || viewName.startsWith("android.view.")
                                || viewName.startsWith("android.support.v7.widget.") || viewName.startsWith("android.support.v7.internal.widget.")
                                || viewName.startsWith("com.android.internal.widget.") || viewName.startsWith("com.android.internal.view")) {
                            return;
                        }

                        WindowManager.LayoutParams wmParams = (WindowManager.LayoutParams)param.args[1];
                        String tmp = "wmParams.flags = " + wmParams.flags +  ", wmParams.type = " + wmParams.type + ", wmParams.alpha = " + wmParams.alpha + ", wmParams.format = " + wmParams.format
                                + ", wmParams.width = " + wmParams.width + ", wmParams.height = " + wmParams.height;

                        //将透明窗口显示出来
                        if (wmParams.alpha == 0f)
                            wmParams.alpha = 0.1f;
                        if (wmParams.format == -2)
                            wmParams.format = -3;
                        param.args[1] = wmParams;

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.view.WindowManagerImpl.addView()", "ViewName : " + viewName + ", ViewParams : " + wmParams, callRef);

                        Logger.log(log, callRef);


                    }
                });

//        //setVisibility
//        XposedHelpers.findAndHookMethod(View.class.getName(), packageParam.classLoader, "setVisibility",
//                Integer.TYPE, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) {
//                        int visibility= (int) param.args[0];
//
//                        String callRef = Stack.getCallRef();
//                        String log = Logger.Tolog("android.view.View.setVisibility()", "visibility : " + visibility, callRef);
//                        Logger.log(log, packageParam.packageName);
//
//                    }
//
//                });
//
//        //dispatchTouchEvent
//        XposedHelpers.findAndHookMethod(View.class.getName(), packageParam.classLoader, "dispatchTouchEvent",
//                MotionEvent.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) {
//                        MotionEvent motionEvent= (MotionEvent) param.args[0];
//
//                        String callRef = Stack.getCallRef();
//                        String log = Logger.Tolog("android.view.View.dispatchTouchEvent ()", "motionEvent : " + motionEvent.toString(), callRef);
//                        Logger.log(log, packageParam.packageName);
//
//                    }
//
//                });


    }

}
