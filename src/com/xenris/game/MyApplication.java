package com.xenris.game;

import android.app.*;
import android.os.*;

public class MyApplication extends Application {
    private Handler gUiHandler;

    public void setUiHandler(Handler handler) {
        gUiHandler = handler;
    }

    public Handler getUiHandler() {
        return gUiHandler;
    }
}
