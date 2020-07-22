package com.cat.appmonitor.hook.Net;

import android.os.Build;

import com.cat.appmonitor.util.HookUtils;
import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.net.HttpURLConnection;
import java.net.URL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by CKCat on 2019/1/10.
 */

public class XHttpHook {

    private static XHttpHook httpHookInstance;
    final int apiLevel = Build.VERSION.SDK_INT;

    public static XHttpHook getInstance() {
        if (httpHookInstance == null) {
            httpHookInstance = new XHttpHook();
        }
        return httpHookInstance;
    }

    public void hook(final XC_LoadPackage.LoadPackageParam packageParam) {


        //hook java.net.URL的openConnection方法
        findAndHookMethod(URL.class, "openConnection", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

                URL url = (URL) param.thisObject;
                String RequestMethod = "";
                try {
                    HttpURLConnection connection = (HttpURLConnection)param.getResult();
                    RequestMethod = connection.getRequestMethod();
                }catch (Exception e){
                    e.printStackTrace();
                }
                String callRef = Stack.getCallRef();


                String log = Logger.Tolog("java.net.URL.openConnection()", url.toString() + ", Method : " + RequestMethod, callRef);
                Logger.log(log, packageParam.packageName);

                HookUtils.HookSatckMethod(packageParam, callRef);
            }
        });

        //hook org.apache.http.impl.client.AbstractHttpClient 的execute方法
        findAndHookMethod("org.apache.http.impl.client.AbstractHttpClient", packageParam.classLoader, "execute",
                HttpHost.class, HttpRequest.class, HttpContext.class, new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {

                        HttpHost host = (HttpHost) param.args[0];
                        HttpRequestBase request = (HttpRequestBase) param.args[1];

                        String url = request.getURI().toString() + " ,Method : " + request.getMethod();
                        String callRef = Stack.getCallRef();
                        String log = Logger.Tolog("AbstractHttpClient.execute()", url, callRef);

                        Logger.log(log, packageParam.packageName);
                        HookUtils.HookSatckMethod(packageParam, callRef);
                    }
                });


        XC_MethodHook URLGetInputStreamHook = new XC_MethodHook() {

            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                HttpURLConnection connection = (HttpURLConnection)param.thisObject;
                URL url = null;
                if (connection != null){
                    url = connection.getURL();
                }
                String callRef = Stack.getCallRef();
                String log = Logger.Tolog("getInputStream()", url.toString(), callRef);
                Logger.log(log, packageParam.packageName);


            }
        };

        if (apiLevel >= 23) {
            XposedHelpers.findAndHookMethod("com.android.okhttp.internal.huc.HttpURLConnectionImpl", packageParam.classLoader, "getInputStream", URLGetInputStreamHook);
        } else if (apiLevel >= 19) {
            XposedHelpers.findAndHookMethod("com.android.okhttp.internal.http.HttpURLConnectionImpl", packageParam.classLoader, "getInputStream", URLGetInputStreamHook);
        } else {
            XposedHelpers.findAndHookMethod("libcore.net.http.HttpURLConnectionImpl", packageParam.classLoader, "getInputStream", URLGetInputStreamHook);
        }

//        //hook okhttp3.Request.Builder的url方法
//        try {
//            final Class<?> okHttpBuilder = XposedHelpers.findClass("okhttp3.Request.Builder", packageParam.classLoader);
//            findAndHookMethod(okHttpBuilder, "url", String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) {
//
//                    String url = (String)param.args[0];
//                    String callRef = Stack.getCallRef();
//                    String log = Logger.Tolog("okhttp3.Request.Builder.url()", url, callRef);
//
//                    Logger.log(log, packageParam.packageName);
//                    //HookUtils.HookSatckMethod(packageParam, callRef);
//                }
//            });
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        try {
//            final Class<?> okHttpClient = XposedHelpers.findClass("com.squareup.okhttp.OkHttpClient", packageParam.classLoader);
//
//            findAndHookMethod(okHttpClient, "open", URI.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    URI url = (URI)param.args[0];
//                    String callRef = Stack.getCallRef();
//                    String log = Logger.Tolog("com.squareup.okhttp.OkHttpClient.open()", url.toURL().toString(), callRef);
//
//                    Logger.log(log, packageParam.packageName);
//                }
//            });
//        } catch (XposedHelpers.ClassNotFoundError e) {
//            e.printStackTrace();
//        }
//
//        // NB: Same deal, but for OkHttp 2.x's async API
//        try {
//            final Class<?> okHttpClient = XposedHelpers.findClass("com.squareup.okhttp.Request.Builder", packageParam.classLoader);
//
//            findAndHookMethod(okHttpClient, "url", String.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    String url = (String)param.args[0];
//                    String callRef = Stack.getCallRef();
//                    String log = Logger.Tolog("com.squareup.okhttp.Request.Builder.url()", url, callRef);
//
//                    Logger.log(log, packageParam.packageName);
//                }
//            });
//        }  catch (XposedHelpers.ClassNotFoundError e) {
//            e.printStackTrace();
//        }
//
//        // https://code.google.com/p/httpclientandroidlib, used in Instagram
//        try {
//            final Class<?> boyeHttpRequestBase = XposedHelpers.findClass("ch.boye.httpclientandroidlib.client.methods.HttpRequestBase", packageParam.classLoader);
//            findAndHookMethod(boyeHttpRequestBase, "setURI", URI.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    URI url = (URI)param.args[0];
//                    String callRef = Stack.getCallRef();
//                    String log = Logger.Tolog("ch.boye.httpclientandroidlib.client.methods.HttpRequestBase.setURI()", url.toURL().toString(), callRef);
//
//                    Logger.log(log, packageParam.packageName);
//                }
//            });
//        }  catch (XposedHelpers.ClassNotFoundError e) {
//            e.printStackTrace();
//        }

    }





    public String handleHttpGet(HttpHost httpHost, HttpGet httpGet) {
        String host = httpHost.toURI().toString();
        String url = httpGet.getURI().toString();

        Header[] header = httpGet.getAllHeaders();

        try{
            StringBuilder sb = new StringBuilder();
            if (header != null) {
                for (int i = 0; i < header.length; i++) {
                    sb.append(header[i].getName() + ": " + header[i].getValue() + ",");
                }
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String handleHttpPost(HttpHost httpHost, HttpPost httpPost) {
        String host = httpHost.toURI().toString();
        String url = httpPost.getURI().toString();

        Header[] header = httpPost.getAllHeaders();
        try{
            StringBuilder sb = new StringBuilder();
            if (header != null) {
                for (int i = 0; i < header.length; i++) {
                    sb.append(header[i].getName()+ ": " + header[i].getValue() + ",");
                }
            }
            HttpEntity entity = httpPost.getEntity();
            if (entity == null){
                return sb.toString();
            }
            String content = "";
            String contentType = null;
            if (entity.getContentType() != null) {
                contentType = entity.getContentType().getValue();
                if (URLEncodedUtils.CONTENT_TYPE.equals(contentType)) {
                    try {
                        byte[] data = new byte[(int) entity.getContentLength()];
                        entity.getContent().read(data);
                        content = new String(data, HTTP.DEFAULT_CONTENT_CHARSET);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (contentType.startsWith(HTTP.DEFAULT_CONTENT_TYPE)) {
                    try {
                        byte[] data = new byte[(int) entity.getContentLength()];
                        entity.getContent().read(data);
                        content = new String(data, contentType.substring(contentType.lastIndexOf("=") + 1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    byte[] data = new byte[(int) entity.getContentLength()];
                    entity.getContent().read(data);
                    content = new String(data, HTTP.DEFAULT_CONTENT_CHARSET);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.append("content: " + content);
            return sb.toString();

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}
