package com.tina.widgetlibrary.widget;

import android.content.Context;

/*
 * Create by Tina
 * Date: 2018/8/25
 * Description：
 */
public class Utils {
    /**
     * dp-->px
     * @param context 上下文
     * @param dip
     * @return
     */
    public static int dp2x(Context context, int dip) {
        // px/dip = density;
        // density = dpi/160
        // 320*480 density = 1 1px = 1dp
        // 1280*720 density = 2 2px = 1dp

        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context,float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
