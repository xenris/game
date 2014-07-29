package com.xenris.game;

import android.os.*;
import java.io.*;

public class Util {
    private static int gNextId = 0;

    public static int getNextId() {
        return gNextId++;
    }

    public static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.message(Log.tag, "Error: Util.join(): " + e.getMessage());
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.message(Log.tag, "Error: Util.sleep(): " + e.getMessage());
        }
    }

    public static boolean close(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
                return true;
            } catch (IOException e) { }
        }

        return false;
    }
}
