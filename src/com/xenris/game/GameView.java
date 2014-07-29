package com.xenris.game;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameState gGameState;
    private RenderThread gRenderThread;

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);
    }

    private class RenderThread extends Thread {
        private boolean running;

        @Override
        public void run() {
            setName("RenderThread");

            running = true;

            while(running) {
                synchronized (this) {
                    if(gGameState != null) {
                        final Canvas canvas = getHolder().lockCanvas();
                        if(canvas != null) {
                            onDraw(canvas);
                            getHolder().unlockCanvasAndPost(canvas);
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

    public void setGameStateToDraw(GameState gameState) {
        synchronized(this) {
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
}
