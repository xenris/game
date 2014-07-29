package com.xenris.game;

import android.view.*;

public class SettingsActivity extends BaseActivity {
    private View gView;

    @Override
    public void onCreate() {
        super.onCreate();

        gView = addView(R.layout.settings);
    }
}
