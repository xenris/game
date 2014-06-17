package com.xenris.game;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class BaseActivity extends Activity {
    private FrameLayout gFrameLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreate();
    }

    public void onCreate() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final int fullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        final int keepOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().addFlags(fullscreen | keepOn);

        gFrameLayout = new FrameLayout(this);
        setContentView(gFrameLayout);
    }

    protected View addView(View view) {
        gFrameLayout.addView(view);
        return view;
    }

    protected View addView(int resource) {
        final View view = getLayoutInflater().inflate(resource, null);
        return addView(view);
    }
}
