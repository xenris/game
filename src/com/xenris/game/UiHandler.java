package com.xenris.game;

import android.os.*;

public class UiHandler extends Handler {
    private UiHandlerCallback gUiHandlerCallback;

    public UiHandler(UiHandlerCallback uiHandlerCallback) {
        super(Looper.getMainLooper());

        gUiHandlerCallback = uiHandlerCallback;
    }

    @Override
    public void handleMessage(Message message) {
        gUiHandlerCallback.onMessageRecieved(message);
    }

    public interface UiHandlerCallback {
        public void onMessageRecieved(Message message);
    }
}
