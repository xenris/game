package com.xenris.game.client;

import android.view.*;
import com.xenris.game.*;

public class MenuView extends GameView {
    private View gMenuView;

    @Override
    public void onCreate() {
        super.onCreate();

        gMenuView = addView(R.layout.game_menu);
    }

    // Screen flickers if set to INVISIBLE.
    protected void menuViewVisible(boolean visible) {
        gMenuView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
