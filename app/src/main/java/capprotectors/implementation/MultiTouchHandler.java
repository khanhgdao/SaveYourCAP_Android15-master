package capprotectors.implementation;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

import capprotectors.framework.Input.TouchEvent;
import capprotectors.framework.Pool;

public class MultiTouchHandler implements TouchHandler {
    private VelocityTracker mVelocityTracker;

    private static final int MAX_TOUCHPOINTS = 10;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private boolean[] isTouched = new boolean[MAX_TOUCHPOINTS];
    private int[] touchX = new int[MAX_TOUCHPOINTS];
    private int[] touchY = new int[MAX_TOUCHPOINTS];
    private int[] id = new int[MAX_TOUCHPOINTS];
    private Pool<TouchEvent> touchEventPool;
    private List<TouchEvent> touchEvents = new ArrayList<>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<>();
    private float scaleX;
    private float scaleY;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    public MultiTouchHandler(Context context, View view, float scaleX, float scaleY) {
        Pool.PoolObjectFactory<TouchEvent> factory = new Pool.PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };
        touchEventPool = new Pool<>(factory, 100);
        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;

        // Fallback to support pre-donuts releases
        if (context == null) {
            //noinspection deprecation
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            //noinspection deprecation
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
        } else {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;

            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);

            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            int pointerCount = event.getPointerCount();
            TouchEvent touchEvent;
            for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
                if (i >= pointerCount) {
                    isTouched[i] = false;
                    id[i] = -1;
                    continue;
                }
                int pointerId = event.getPointerId(i);
                if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
                    // if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch
                    // point
                    continue;
                }
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (mCurrentDownEvent != null) {
                            mCurrentDownEvent.recycle();
                        }
                        mCurrentDownEvent = MotionEvent.obtain(event);
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DOWN;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                        isTouched[i] = true;
                        id[i] = pointerId;
                        touchEventsBuffer.add(touchEvent);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        // A fling must travel the minimum tap distance
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        final int pointerId0 = event.getPointerId(0);
                        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                        final float velocityY = velocityTracker.getYVelocity(pointerId0);
                        final float velocityX = velocityTracker.getXVelocity(pointerId0);

                        if ((Math.abs(velocityY) > mMinimumFlingVelocity)
                                || (Math.abs(velocityX) > mMinimumFlingVelocity)){
                            try {
                                float diffY = event.getY() - mCurrentDownEvent.getY();
                                float diffX = event.getX() - mCurrentDownEvent.getX();
                                /*if (Math.abs(diffX) > Math.abs(diffY)) {
                                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                        if (diffX > 0) {
                                            onSwipeRight();
                                        } else {
                                            onSwipeLeft();
                                        }
                                    }
                                }
                                else */if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                                    if (diffY > 0) {
                                        touchEvent = touchEventPool.newObject();
                                        touchEvent.type = TouchEvent.SWIPE_DOWN;
                                        touchEvent.pointer = pointerId;
                                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                                        isTouched[i] = false;
                                        id[i] = -1;
                                        touchEventsBuffer.add(touchEvent);
                                    } else {
                                        touchEvent = touchEventPool.newObject();
                                        touchEvent.type = TouchEvent.SWIPE_UP;
                                        touchEvent.pointer = pointerId;
                                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                                        isTouched[i] = false;
                                        id[i] = -1;
                                        touchEventsBuffer.add(touchEvent);
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_UP;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                        isTouched[i] = false;
                        id[i] = -1;
                        touchEventsBuffer.add(touchEvent);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
                        touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
                        isTouched[i] = true;
                        id[i] = pointerId;
                        touchEventsBuffer.add(touchEvent);
                        break;
                }
            }
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            return index >= 0 && index < MAX_TOUCHPOINTS && isTouched[index];
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS)
                return 0;
            else
                return touchX[index];
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            int index = getIndex(pointer);
            if (index < 0 || index >= MAX_TOUCHPOINTS)
                return 0;
            else
                return touchY[index];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++)
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }

    // returns the index for a given pointerId or -1 if no index.
    private int getIndex(int pointerId) {
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            if (id[i] == pointerId) {
                return i;
            }
        }
        return -1;
    }
}