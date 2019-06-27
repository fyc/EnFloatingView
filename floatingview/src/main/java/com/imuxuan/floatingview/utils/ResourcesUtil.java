package com.imuxuan.floatingview.utils;

import android.content.Context;

public class ResourcesUtil {
    public static int getId(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static int getStyleId(Context context, String name) {
        return getId(context, name, "style");
    }

    public static int getLayoutId(Context context, String name) {
        return getId(context, name, "layout");
    }

    public static int getIdId(Context context, String name) {
        return getId(context, name, "id");
    }

    public static int getDrawableId(Context context, String name) {
        return getId(context, name, "drawable");
    }

    public static int getAttrId(Context context, String name) {
        return getId(context, name, "attr");
    }

    public static int getAnimId(Context context, String name) {
        return getId(context, name, "anim");
    }
}
