package com.xenris.game;

import android.view.*;

public class TouchHandler implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getActionMasked();

        final int x = (int)motionEvent.getX();
        final int y = (int)motionEvent.getY();

//        if(action == MotionEvent.ACTION_DOWN) {
//            gMe.setX(x);
//            gMe.setY(y);
//        } else if(action == MotionEvent.ACTION_MOVE) {
//            gMe.setX(x);
//            gMe.setY(y);
//        } else if(action == MotionEvent.ACTION_UP) {
//            gMe.setX(-1);
//            gMe.setY(-1);
//        }

//        gServerConnection.sendPlayerState(gMe);

        return true;
    }
}
