package com.xenris.game.client;

import android.content.*;
import android.graphics.*;
import android.view.*;
import com.xenris.game.*;

public class GameView extends Network implements SurfaceHolder.Callback {
    private SurfaceView gSurfaceView;
    private GameState gGameState;
    private RenderThread gRenderThread;

    @Override
    public void onCreate() {
        super.onCreate();

        gSurfaceView = new SurfaceView(this);

        addView(gSurfaceView);

        final SurfaceHolder surfaceHolder = gSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private class RenderThread extends Thread {
        private boolean running;

        @Override
        public void run() {
            running = true;

            while(running) {
                synchronized (this) {
                    if(gGameState != null) {
                        final SurfaceHolder surfaceHolder = gSurfaceView.getHolder();
                        final Canvas canvas = surfaceHolder.lockCanvas();
                        if(canvas != null) {
                            onDraw(canvas);
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }

                Thread.yield();
            }
        }

        public void close() {
            running = false;
        }
    }

    public void setGameStateForDrawing(GameState gameState) {
        synchronized (this) {
            gGameState = gameState;
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gRenderThread = new RenderThread();
        gRenderThread.start();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gRenderThread.close();
        Util.join(gRenderThread);
    }

    public void onDraw(Canvas canvas) {
        if(gGameState != null) {
            canvas.drawColor(Color.GREEN);
            gGameState.draw(canvas);
        } else {
            canvas.drawColor(Color.RED);
        }
    }

    protected void setOnTouchListener(View.OnTouchListener listener) {
        gSurfaceView.setOnTouchListener(listener);
    }
}
