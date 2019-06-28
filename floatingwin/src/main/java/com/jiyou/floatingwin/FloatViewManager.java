package com.jiyou.floatingwin;

import android.content.Context;
import android.content.Intent;

/**
 * 悬浮窗
 */
public class FloatViewManager {

    private static FloatViewManager instance;
    private FloatViewController floatViewController;
    Context context;

    private FloatViewManager(Context context) {
        this.context = context;
        floatViewController = new FloatViewController(context);
    }

    public static synchronized FloatViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new FloatViewManager(context);
        }
        return instance;
    }


    public synchronized void destory() {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (floatViewController != null) {
//                    floatViewController.hide();
//                    floatViewController = null;
//                    instance = null;
//                }
//            }
//        });
    }


    public synchronized void show() {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (floatViewController != null) {
//                    floatViewController.show();
//                }
//            }
//        },10);
        startMonkServer(context);
    }

    public synchronized void hide() {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (floatViewController != null) {
//                    floatViewController.hide();
//                }
//            }
//        });
        stopMonkServer(context);
    }

    /**
     * 开启服务悬浮窗
     */
    private void startMonkServer(Context context) {
        Intent intent = new Intent(context, FloatViewService.class);
        context.startService(intent);
    }

    /**
     * 关闭悬浮窗
     */
    private void stopMonkServer(Context context) {
        Intent intent = new Intent(context, FloatViewService.class);
        context.stopService(intent);
    }

}
