package com.example.ninh.facenewsprt3;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by ninh on 22/06/2015.
 */
public class OverScrollListView extends ListView {
    private boolean status;

    public OverScrollListView(Context context) {
        super(context);
        init();
    }

    public OverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0, 150, isTouchEvent);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if(scrollY <-75)
        this.setStatus(true);
        else this.setStatus(false);

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }
    public boolean getStatus()
    {
        return this.status;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}

