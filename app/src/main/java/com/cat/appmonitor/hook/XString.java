package com.cat.appmonitor.hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XString extends XHook {
	private static final String className = "java.lang.String";
	private static XString classLoadHook;

	public static XString getInstance() {
		if (classLoadHook == null) {
			classLoadHook = new XString();
		}
		return classLoadHook;
	}

	@Override
	void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
		XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
				String.class, new XC_MethodHook() {

					@Override
					protected void afterHookedMethod(MethodHookParam param) {
						String str = param.args[0].toString();
						String callRef = Stack.getCallRef();

						String log = Logger.Tolog("java.lang.String.<init>", "string : " + str, callRef);
						Logger.log(log, packageParam.packageName);
					}


				});
		XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
				char[].class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String)param.getResult();
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.String.<init>", "string : " + str, callRef);
                        Logger.log(log, packageParam.packageName);
					}
				});

        XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
                byte[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String)param.getResult();
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.String.<init>", "string : " + str, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
                StringBuffer.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String)param.getResult();
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.String.<init>", "string : " + str, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
                StringBuilder.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String)param.getResult();
                        String callRef = Stack.getCallRef();

                        String log = Logger.Tolog("java.lang.String.<init>", "string : " + str, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

	}

    private static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
