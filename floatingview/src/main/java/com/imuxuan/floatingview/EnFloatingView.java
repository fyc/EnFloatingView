package com.imuxuan.floatingview;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.imuxuan.floatingview.utils.ResourcesUtil;

/**
 * @ClassName EnFloatingView
 * @Description 悬浮窗
 * @Author Yunpeng Li
 * @Creation 2018/3/15 下午5:04
 * @Mender Yunpeng Li
 * @Modification 2018/3/15 下午5:04
 */
public class EnFloatingView extends FloatingMagnetView {

    private final ImageView mIcon;

    public EnFloatingView(@NonNull Context context) {
        super(context, null);
        int en_floating_view_id = ResourcesUtil.getLayoutId(context, "en_floating_view");
        inflate(context, en_floating_view_id, this);
        int icon_id = ResourcesUtil.getIdId(context, "icon");
        mIcon = findViewById(icon_id);
        setFloatViewNormalImg();
    }

    public void setIconImage(@DrawableRes int resId){
        mIcon.setImageResource(resId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setFloatViewNormalImg();
                    clearAnimation();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return true;
    }

    private void setFloatViewNormalImg() {
//        int jy_sdk_float_window_normal_id = ResourcesUtil.getDrawableId(getContext(), "jy_sdk_float_window_normal");
////        setIconImage(jy_sdk_float_window_normal_id);
    }

    /*隐藏半边之后*/
    @Override
    protected void onEndHide() {
//        int jy_sdk_float_window_transparent_id = ResourcesUtil.getDrawableId(getContext(), "jy_sdk_float_window_transparent");
//        setIconImage(jy_sdk_float_window_transparent_id);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                int jy_sdk_float_window_transparent_id = ResourcesUtil.getDrawableId(getContext(), "jy_sdk_float_window_transparent");
//                setIconImage(jy_sdk_float_window_transparent_id);
                viewAnima();
            }
        }, 2000);
    }

}
