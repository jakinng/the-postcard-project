package com.example.thepostcardproject.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class GestureLockedBottomSheetBehavior<View extends android.view.View> extends BottomSheetBehavior<View> {

    /**
     * Constructor for a new BottomSheetBehavior where the swipe gesture does NOT display the backdrop
     * @param context
     */
    public GestureLockedBottomSheetBehavior(@NonNull Context context) {
        super(context, null);
    }

    public GestureLockedBottomSheetBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*
        Override all the gestures to open the backdrop; only clicking the filter button should open the backdrop
         */
    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent event) {
        return false;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull android.view.View directTargetChild, @NonNull android.view.View target, int axes, int type) {
        return false;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull android.view.View target, int dx, int dy, @NonNull int[] consumed, int type) {}

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull android.view.View target, int type) {}

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull android.view.View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }
}
