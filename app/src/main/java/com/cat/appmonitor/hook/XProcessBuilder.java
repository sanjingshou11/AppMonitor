package com.cat.appmonitor.hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Utils;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XProcessBuilder extends XHook {

    private static final String className = "java.lang.ProcessBuilder";
    private static XProcessBuilder classLoadHook;

    public static XProcessBuilder getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XProcessBuilder();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "start",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Utils.getSystemTime();

                        ProcessBuilder pb = (ProcessBuilder) param.thisObject;
                        List<String> cmds = pb.command();
                        StringBuilder cmdSb = new StringBuilder();
                        for (int i = 0; i < cmds.size(); i++) {
                            cmdSb.append(cmds.get(i) + " ");
                        }

                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.ProcessBuilder.start()", "cmd : " + cmdSb.toString(), callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });
    }

}
