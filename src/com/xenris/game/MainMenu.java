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
        } else if(id == R.id.settings_button) {
            settings();
        } else if(id == R.id.about_button) {
            about();
        }
    }

    private void play() {
        startActivity(new Intent(this, Client.class));
    }

    private void settings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void about() {
        startActivity(new Intent(this, AboutActivity.class));
    }
}
