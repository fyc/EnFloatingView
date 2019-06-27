package com.imuxuan.floatingview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.imuxuan.floatingview.utils.ResourcesUtil;
import com.imuxuan.floatingview.utils.SystemUtils;

/**
 * @ClassName FloatingMagnetView
 * @Description 磁力吸附悬浮窗
 * @Author Yunpeng Li
 * @Creation 2018/3/15 下午5:02
 * @Mender Yunpeng Li
 * @Modification 2018/3/15 下午5:02
 */
public class FloatingMagnetView extends FrameLayout {

    public static final int MARGIN_EDGE = 13;
    private float mOriginalRawX;
    private float mOriginalRawY;
    private float mOriginalX;
    private float mOriginalY;
    private MagnetViewListener mMagnetViewListener;
    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long mLastTouchDownTime;
    protected MoveAnimator mMoveAnimator;
    protected int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;

    public void setMagnetViewListener(MagnetViewListener magnetViewListener) {
        this.mMagnetViewListener = magnetViewListener;
    }

    public FloatingMagnetView(Context context) {
        this(context, null);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMoveAnimator = new MoveAnimator();
        mStatusBarHeight = SystemUtils.getStatusBarHeight(getContext());
        setClickable(true);
        updateSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeOriginalTouchParams(event);
                updateSize();
                mMoveAnimator.stop();
                removeRunable();
                break;
            case MotionEvent.ACTION_MOVE:
                updateViewPosition(event);
                break;
            case MotionEvent.ACTION_UP:
                moveToEdge();
                if (isOnClickEvent()) {
                    dealClickEvent();
                }
                break;
        }
        return true;
    }

    protected void dealClickEvent() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onClick(this);
        }
    }

    protected boolean isOnClickEvent() {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
    }

    private void updateViewPosition(MotionEvent event) {
        setX(mOriginalX + event.getRawX() - mOriginalRawX);
        // 限制不可超出屏幕高度
        float desY = mOriginalY + event.getRawY() - mOriginalRawY;
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight;
        }
        if (desY > mScreenHeight - getHeight()) {
            desY = mScreenHeight - getHeight();
        }
        setY(desY);
    }

    private void changeOriginalTouchParams(MotionEvent event) {
        mOriginalX = getX();
        mOriginalY = getY();
        mOriginalRawX = event.getRawX();
        mOriginalRawY = event.getRawY();
        mLastTouchDownTime = System.currentTimeMillis();
    }

    protected void updateSize() {
        mScreenWidth = (SystemUtils.getScreenWidth(getContext()) - this.getWidth());
        mScreenHeight = SystemUtils.getScreenHeight(getContext());
    }

    public void moveToEdge() {
        float moveDistance = isNearestLeft() ? MARGIN_EDGE : mScreenWidth - MARGIN_EDGE;
        mMoveAnimator.start(moveDistance, getY());
    }

    protected boolean isNearestLeft() {
        int middle = mScreenWidth / 2;
        return getX() < middle;
    }

    public void onRemove() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onRemove(this);
        }
    }

    protected class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() == null || getRootView().getParent() == null) {
                return;
            }
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
            float deltaX = (destinationX - getX()) * progress;
            float deltaY = (destinationY - getY()) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            } else {
                viewAnima();//开始隐藏
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    private void move(float deltaX, float deltaY) {
        setX(getX() + deltaX);
        setY(getY() + deltaY);
    }

    public void onAttach() {
    }

    public void onDetach() {
    }

    //关于靠边隐藏==============================================================================================
    protected static final int HEDE_FLOAT_VIEW_TIME = 3000;//靠边隐藏时间
    protected static final int MES_ANIMA_LEFT = 0;
    protected static final int MES_ANIMA_RIGHT = 1;
    protected static final int VIEW_GONE = 2;
    protected static final int VIEW_Transparent = 3;

    protected Animation animationleft;
    protected Animation animationright;

    protected void viewAnima() {
        if (isNearestLeft()) {
            handler.postDelayed(myRunnableLeft, HEDE_FLOAT_VIEW_TIME);
        } else {
            handler.postDelayed(myRunnableRigth, HEDE_FLOAT_VIEW_TIME);
        }
    }

    private void removeRunable() {
        handler.removeCallbacksAndMessages(null);
    }

    protected Runnable runnableViewGone = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = VIEW_GONE;
            handler.sendMessage(message);

        }
    };

    protected Runnable runnableTransparent = new Runnable() {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = VIEW_Transparent;
            handler.sendMessage(message);
        }
    };

    protected Runnable myRunnableLeft = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_LEFT;
            handler.sendMessage(message);

        }
    };

    protected Runnable myRunnableRigth = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_RIGHT;
            handler.sendMessage(message);

        }
    };

    protected Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MES_ANIMA_LEFT:
                    if (null == animationleft) {
                        int jy_sdk_anim_float_view_left_id = ResourcesUtil.getAnimId(getContext(), "jy_sdk_anim_float_view_left");
                        animationleft = AnimationUtils.loadAnimation(getContext(),
                                jy_sdk_anim_float_view_left_id);
                        animationleft.setFillAfter(true);
                    }
                    startAnimation(animationleft);
                    animationleft.setAnimationListener(ainimaLeft);

                    break;

                case MES_ANIMA_RIGHT:
                    if (null == animationright) {
                        int jy_sdk_anim_float_view_right_id = ResourcesUtil.getAnimId(getContext(), "jy_sdk_anim_float_view_right");
                        animationright = AnimationUtils.loadAnimation(getContext(),
                                jy_sdk_anim_float_view_right_id);
                        animationright.setFillAfter(true);
                    }
                    startAnimation(animationright);

                    animationright.setAnimationListener(ainimaRigth);


                    break;

//                case VIEW_GONE:
//
//                    imgFloatView.setVisibility(View.GONE);
//                    Log.d(TAG, "handleMessage: bingo");
//                    break;

                case VIEW_Transparent:
                    break;

                default:
                    break;
            }

        }
    };

    protected Animation.AnimationListener ainimaLeft = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onEndHide();
        }
    };

    protected Animation.AnimationListener ainimaRigth = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onEndHide();
        }
    };

    /*隐藏半边之后,由子类进行具体实现*/
    protected void onEndHide() {
    }

}
