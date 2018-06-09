package com.guineatech.CareC;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by CAHans on 2018/4/16.
 */
//點空白消除鍵盤
public class HideInputUtils {
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
