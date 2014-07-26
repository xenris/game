package com.xenris.game;

import android.app.*;
import android.os.*;

public class MyApplication extends Application {
    private Handler gUiHandler;
//    private Handler gGameHandler;

    public void setUiHandler(Handler handler) {
        gUiHandler = handler;
    }

//    public void setGameHandler(Handler handler) {
//        gGameHandler = handler;
//    }

    public Handler getUiHandler() {
        return gUiHandler;
    }

//    public Handler getGameHandler() {
//        return gGameHandler;
//    }
}
