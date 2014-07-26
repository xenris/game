package com.xenris.game;

import android.content.*;
import android.view.*;

public class MainMenu extends BaseActivity {
    @Override
    public void onCreate() {
        super.onCreate();

        addView(R.layout.menu);
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.play_button) {
            play();
        }
    }

    private void play() {
        startActivity(new Intent(this, Client.class));
    }
}
