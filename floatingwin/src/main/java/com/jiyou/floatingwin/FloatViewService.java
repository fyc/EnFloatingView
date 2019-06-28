package com.jiyou.floatingwin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import com.jiyou.utils.EnContext;
import com.jiyou.utils.ResourcesUtil;

public class FloatViewService extends Service {
    private FloatViewController floatViewController;

    @Override
    public void onCreate() {
        super.onCreate();
        floatViewController = new FloatViewController(this);
        floatViewController.setClickListener(new FloatViewController.FloatOnClickListener() {
            @Override
            public void onClick(View v) {
//                int img_floatview_id = ResourcesUtil.getIdId(EnContext.get(), "img_floatview");
//                if (v.getId() == img_floatview_id) {
//                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (floatViewController != null) {
            floatViewController.show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatViewController != null) {
            floatViewController.hide();
            floatViewController = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
