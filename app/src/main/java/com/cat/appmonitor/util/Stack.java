package com.cat.appmonitor.util;


public class Stack {
    public static String getCallRef() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        String value = "";
        boolean isFirst = true;
        int i;
        for (i = 0; i < traceElements.length; ++i) {

            String elements = traceElements[i].toString();
            if (!elements.startsWith("com.cat.appmonitor")
                    && !elements.startsWith("util.Stack")
                    && !elements.startsWith("dalvik.")
                    && !elements.startsWith("java.lang")
                    && !elements.startsWith("de.robv")
                    && !elements.startsWith("android.app")
                    && !elements.startsWith("android.os")
//                    && !elements.startsWith("com.android")
                ) {

                if (isFirst) {
                    value = String.valueOf(elements.substring(0, elements.indexOf("(")));
                    isFirst = false;
                    continue;
                }

                value = value + " <- " + String.valueOf(elements.substring(0, elements.indexOf("(")));
            }
        }

        return value;
    }


    //利用Execption显示调用栈
    public static String  printStackTrace(){
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] elements = new RuntimeException().getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i].toString()).append("\n");
        }
        return sb.toString();
    }

    //利用Thread显示调用栈
    public static String  printThreadTrace(){
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
