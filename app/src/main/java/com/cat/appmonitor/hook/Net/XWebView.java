package com.cat.appmonitor.hook.Net;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


public class XWebView{

    private static final String className = "android.webkit.WebView";
    private static XWebView xWebView;

    public static XWebView getInstance() {
        if (xWebView == null) {
            xWebView = new XWebView();
        }
        return xWebView;
    }

    public  void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        //Injects the supplied Java object into this WebView.
        //http://developer.android.com/intl/pt-br/reference/android/webkit/WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        findAndHookMethod(WebView.class, "addJavascriptInterface",
                Object.class, String.class, new XC_MethodHook() {

                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object obj = param.args[0];
                        String objName = (String) param.args[1];

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.webkit.WebView.addJavascriptInterface()", "obj : " + obj.toString() + ", Name: " + objName, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        findAndHookMethod(WebView.class, "loadUrl",
                String.class, new XC_MethodHook() {

                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String url = (String)param.args[0];

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.webkit.WebView.loadUrl()", "Url : " + url, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        findAndHookMethod(WebView.class, "loadData",
                String.class, String.class, String.class, new XC_MethodHook() {

                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        String data = (String)param.args[0];
                        String mimeType = (String)param.args[1];
                        String encoding = (String)param.args[2];

                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("android.webkit.WebView.loadData()", "data : " + data + ", mimeType : " + mimeType + ", encoding : " + encoding, callRef);
                        Logger.log(log, packageParam.packageName);
                    }
                });

        findAndHookMethod(WebView.class, "setWebChromeClient", WebChromeClient.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                String WebChromeClient = ((WebChromeClient)param.args[0]).toString();

                String callRef = Stack.getCallRef();
                String log = Logger.Tolog("android.webkit.WebView.setWebChromeClient()", "WebChromeClient : " + WebChromeClient, callRef);
                Logger.log(log, packageParam.packageName);
            }
        });

        findAndHookMethod(WebView.class, "setWebViewClient", WebViewClient.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                String WebViewClient = ((WebViewClient)param.args[0]).toString();

                String callRef = Stack.getCallRef();
                String log = Logger.Tolog("android.webkit.WebView.setWebViewClient()", "WebViewClient : " + WebViewClient, callRef);
                Logger.log(log, packageParam.packageName);
            }
        });

        findAndHookMethod(WebView.class, "setWebContentsDebuggingEnabled", "boolean", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean enabled = (boolean) param.args[0];

                String callRef = Stack.getCallRef();
                String log = Logger.Tolog("android.webkit.WebView.setWebContentsDebuggingEnabled()", "enabled : " + enabled, callRef);
                Logger.log(log, packageParam.packageName);
            }
        });
    }
}
