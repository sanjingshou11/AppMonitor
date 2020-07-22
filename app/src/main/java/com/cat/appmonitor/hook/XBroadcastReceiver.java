package com.cat.appmonitor.hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XBroadcastReceiver extends XHook {
	private static final String className = "android.content.BroadcastReceiver";
	private static XBroadcastReceiver xBroadcastReceiver;

	public static XBroadcastReceiver getInstance() {
		if (xBroadcastReceiver == null) {
			xBroadcastReceiver = new XBroadcastReceiver();
		}
		return xBroadcastReceiver;
	}

	@Override
	void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

		XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
				"abortBroadcast", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) {

						String callRef = Stack.getCallRef();
						String log = Logger.Tolog("android.content.BroadcastReceiver.abortBroadcast()", "", callRef);
						Logger.log(log, packageParam.packageName);
					}
				});
	}
}
