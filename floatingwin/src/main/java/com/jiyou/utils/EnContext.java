package com.jiyou.utils;

import android.app.Application;

/**
 * Created by Yunpeng Li on 2018/11/8.
 */
public class EnContext {

    private static final Application INSTANCE;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            e.printStackTrace();
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        } finally {
            INSTANCE = app;
        }
    }

    public static Application get() {
        return INSTANCE;
    }
}


//public class Applications {
//
//    @NonNull
//    public static Application context() {
//        return CURRENT;
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    private static final Application CURRENT;
//
//    static {
//        try {
//            Object activityThread = getActivityThread();
//            Object app = activityThread.getClass().getMethod("getApplication")
//                    .invoke(activityThread);
//            CURRENT = (Application) app;
//        } catch (Throwable e) {
//            throw new IllegalStateException("Can not access Application context by magic code, boom!", e);
//        }
//    }
//
//    private static Object getActivityThread() {
//        Object activityThread = null;
//        try {
//            @SuppressLint("PrivateApi") Method method = Class.forName("android.app.ActivityThread")
//                    .getMethod("currentActivityThread");
//            method.setAccessible(true);
//            activityThread = method.invoke(null);
//        } catch (final Exception e) {
//            Log.w(TAG, e);
//        }
//        return activityThread;
//    }
//}