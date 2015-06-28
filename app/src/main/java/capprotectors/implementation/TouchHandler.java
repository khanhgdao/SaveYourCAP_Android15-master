package capprotectors.implementation;

import android.view.View.OnTouchListener;

import java.util.List;

import capprotectors.framework.Input.TouchEvent;

public interface TouchHandler extends OnTouchListener {
    boolean isTouchDown(int pointer);

    int getTouchX(int pointer);

    int getTouchY(int pointer);

    List<TouchEvent> getTouchEvents();
}
