package com.jiyou.floatingwin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jiyou.utils.ResourcesUtil;
import com.jiyou.utils.ToastUtil;


class FloatViewController implements IViewController {
    private static final String TAG = "JYSDK:FloatViewController";

    public static final int HEDE_FLOAT_VIEW_TIME = 3000;//靠边隐藏时间
    private View moveView;
    private ImageView imgFloatView;
    private Context context;
    private int x;
    private int y;
    private int startX;
    private int startY;
    private int controlledSpace = 20;
    private int screenWidth;
    private int screenHeigth;
    boolean isShow = false;
    private FloatOnClickListener mClickListener;
    private WindowManager windowManager;
    private LayoutParams windowManagerParams;
    private Animation animationleft;
    private Animation animationright;
    private boolean ismove = false;
    private int deltaX;
    private int deltaY;
    private int fristx = 0;
    private int fristy = 200;
    private int lastx = 0;
    private int lasty = 0;
    private int totoalx = 0;
    private int totoaly = 0;
    private int floatMoveViewWith;
    private static final int MES_ANIMA_LEFT = 0;
    private static final int MES_ANIMA_RIGHT = 1;
    private static final int VIEW_GONE = 2;
    private static final int VIEW_Transparent = 3;


    private Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MES_ANIMA_LEFT:
                    int jy_sdk_anim_float_view_left_id = ResourcesUtil.getAnimId(context, "jy_sdk_anim_float_view_left");
                    animationleft = AnimationUtils.loadAnimation(context,
                            jy_sdk_anim_float_view_left_id);
                    animationleft.setFillAfter(true);
                    imgFloatView.startAnimation(animationleft);
                    animationleft.setAnimationListener(ainimaLeft);

                    break;

                case MES_ANIMA_RIGHT:
                    int jy_sdk_anim_float_view_right_id = ResourcesUtil.getAnimId(context, "jy_sdk_anim_float_view_right");
                    animationright = AnimationUtils.loadAnimation(context,
                            jy_sdk_anim_float_view_right_id);
                    animationright.setFillAfter(true);
                    imgFloatView.startAnimation(animationright);

                    animationright.setAnimationListener(ainimaRigth);


                    break;

//                case VIEW_GONE:
//
//                    imgFloatView.setVisibility(View.GONE);
//                    Log.d(TAG, "handleMessage: bingo");
//                    break;

                case VIEW_Transparent:
                    int jy_sdk_float_window_transparent_id = ResourcesUtil.getDrawableId(context, "jy_sdk_float_window_transparent");
                    imgFloatView.setImageResource(jy_sdk_float_window_transparent_id);
                    break;

                default:
                    break;
            }

        }
    };

    private AnimationListener ainimaLeft = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            int jy_sdk_float_window_transparent_id = ResourcesUtil.getDrawableId(context, "jy_sdk_float_window_transparent");
            imgFloatView.setImageResource(jy_sdk_float_window_transparent_id);
        }
    };

    private AnimationListener ainimaRigth = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            int jy_sdk_float_window_transparent_id = ResourcesUtil.getDrawableId(context, "jy_sdk_float_window_transparent");
            imgFloatView.setImageResource(jy_sdk_float_window_transparent_id);
        }
    };

    public FloatViewController(Context mContext) {
        this.context = mContext;
        initeWindowManager();
//        mClickListener = new MyOnClickListener();
        inite(mContext);

    }

    public interface FloatOnClickListener {
        void onClick(View v);
    }

    public FloatOnClickListener getClickListener() {
        return mClickListener;
    }

    public void setClickListener(FloatOnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @SuppressLint("InflateParams")
    public void inite(Context context) {
        int jy_sdk_float_view_id = ResourcesUtil.getLayoutId(context, "jy_sdk_float_view");
        int img_floatview_id = ResourcesUtil.getIdId(context, "img_floatview");
        moveView = LayoutInflater.from(context).inflate(jy_sdk_float_view_id, null);
        imgFloatView = (ImageView) moveView.findViewById(img_floatview_id);

        int w1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        moveView.measure(w1, h1);
        floatMoveViewWith = moveView.getMeasuredWidth();
        moveView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                screenWidth = windowManager.getDefaultDisplay().getWidth();
                screenHeigth = windowManager.getDefaultDisplay().getHeight();
                totoalx = lastx - fristx;
                totoaly = lasty - fristy;
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        lastx = (int) event.getRawX();
                        lasty = (int) event.getRawY();
                        fristx = lastx;
                        fristy = lasty;
                        setFloatViewNormalImg();
                        imgFloatView.clearAnimation();
                        removeRunable();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        deltaX = (int) event.getRawX() - lastx;
                        deltaY = (int) event.getRawY() - lasty;
                        lastx = (int) event.getRawX();
                        lasty = (int) event.getRawY();
                        if (ismove || Math.abs(totoalx) >= 10
                                || Math.abs(totoaly) >= 10) {
                            ismove = true;
                            updateViewPosition();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ismove = false;
                        if (Math.abs(x - startX) < controlledSpace
                                && Math.abs(y - startY) < controlledSpace) {
                            if (mClickListener != null) {
                                mClickListener.onClick(imgFloatView);
                            }
                        }

                        if (isMoveToSide()) {
                            if (x <= screenWidth / 2) {
                                windowManagerParams.x = 0;
                            } else {
                                windowManagerParams.x = screenWidth - floatMoveViewWith;
                            }
                            windowManager.updateViewLayout(moveView, windowManagerParams);
                        }

                        if (!ismove) {
                            viewAnima();
                        }
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public View getViewContainer() {
        return moveView;
    }


    // 隐藏该窗体
    public void hide() {
        synchronized (FloatViewController.this) {
            if (isShow) {
                removeRunable();
                windowManager.removeView(moveView);
                isShow = false;
            }
        }
    }

    // 显示该窗体
    public void show() {
        synchronized (FloatViewController.this) {
            if (!isShow && getPersimmions(context)) {
                setFloatViewNormalImg();
                windowManager.addView(moveView, windowManagerParams);
                isShow = true;
                viewAnima();
            }
        }
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        windowManagerParams.x += (int) deltaX;
        windowManagerParams.y += (int) deltaY;
        windowManager.updateViewLayout(moveView, windowManagerParams); // 刷新显示
        setFloatViewNormalImg();
    }


//    private class MyOnClickListener implements OnClickListener {
//        @Override
//        public void onClick(View v) {
//            int img_floatview_id = ResourcesUtil.getIdId(ExtApp.app(), "img_floatview");
//            if (v.getId() == img_floatview_id) {
//                if (!GameSdkLogic.getInstance().isLogin()) {
//                    ToastUtil.showLongHideSoftInput(ExtApp.app(), "登录异常！请重新登录！");
//                    return;
//                }
//                Intent intent = new Intent(ExtApp.app(), JYSDKUserCenterActivity.class);
//                context.startActivity(intent);
//            }
//        }
//
//    }

    public Runnable myRunnableLeft = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_LEFT;
            handler.sendMessage(message);

        }
    };

    public Runnable myRunnableRigth = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_RIGHT;
            handler.sendMessage(message);

        }
    };

//    public Runnable runnableViewGone = new Runnable() {
//        public void run() {
//
//            Message message = handler.obtainMessage();
//            message.what = VIEW_GONE;
//            handler.sendMessage(message);
//
//        }
//    };

    public Runnable runnableTransparent = new Runnable() {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = VIEW_Transparent;
            handler.sendMessage(message);
        }
    };

    private void viewAnima() {
        if (windowManagerParams.x < 25) {
            windowManagerParams.x = 0;
            windowManager.updateViewLayout(moveView, windowManagerParams); // 刷新显示
            handler.postDelayed(myRunnableLeft, HEDE_FLOAT_VIEW_TIME);
        } else if (windowManagerParams.x > (screenWidth - floatMoveViewWith - 25)) {
            windowManagerParams.x = screenWidth - floatMoveViewWith;
            windowManager.updateViewLayout(moveView, windowManagerParams); // 刷新显示
            handler.postDelayed(myRunnableRigth, HEDE_FLOAT_VIEW_TIME);
        }
    }

    private void removeRunable() {
        handler.removeCallbacksAndMessages(null);
    }


    private void initeWindowManager() {
        windowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeigth = windowManager.getDefaultDisplay().getHeight();
        windowManagerParams = new LayoutParams();
        windowManagerParams.alpha = 0.9f;
        if (Build.VERSION.SDK_INT < 19 || Build.VERSION.SDK_INT > 24 || FloatWinCompatHelper.isDeviceNotSupportNewWindowType()) {
//            windowManagerParams.type = LayoutParams.TYPE_PHONE;//这里会报错permission denied for window type 2002
//            解决办法 在Android 8.0以上
//            如果需要创建悬浮窗 需要设置
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            windowManagerParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            windowManagerParams.type = LayoutParams.TYPE_TOAST;
        }
        windowManagerParams.format = PixelFormat.RGBA_8888;
        windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_FULLSCREEN;
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManagerParams.x = fristx;
        windowManagerParams.y = fristy;
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;
    }

    private boolean isMoveToSide() {
        boolean isMoveToSide = false;
//        if (screenHeigth > screenWidth) {
        isMoveToSide = true;
//        }
        return isMoveToSide;

    }

    private void setFloatViewNormalImg() {
        int jy_sdk_float_window_normal_id = ResourcesUtil.getDrawableId(context, "jy_sdk_float_window_normal");
        imgFloatView.setImageResource(jy_sdk_float_window_normal_id);
        handler.postDelayed(runnableTransparent, 2000);
    }

    /*<!--小浮窗所需权限-->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    在manifest中添加即可*/
    @TargetApi(23)
    public boolean getPersimmions(Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /***
             * 发出一个意图让用户去设置同意附表权限，申请权限
             */
            // 定位精确位置
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                activity.startActivity(intent);
//                ToastUtil.showLongHideSoftInput(activity, "悬浮窗需要权限！请到设置中进行设置！");
                return false;
            }

        }
        return true;
    }
}
