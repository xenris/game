package com.xenris.game;

public class Log {
    public static final String tag = "GameMessage";

    public static void message(String t, String s) {
        android.util.Log.i(t, s);
    }

    public static void message(String s) {
        android.util.Log.i(tag, s);
    }
}
