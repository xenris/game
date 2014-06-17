package com.xenris.game.client;

import android.content.*;
import android.graphics.*;
import android.view.*;
import com.xenris.game.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameState gGameState;
    private RenderThread gRenderThread;

    public GameView(Context context) {
        super(context);

        final SurfaceHolder surfaceHolder = getHolder();
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
                        final SurfaceHolder surfaceHolder = getHolder();
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

    public void setGameState(GameState gameState) {
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

    @Override
    public void onDraw(Canvas canvas) {
        if(gGameState != null) {
            canvas.drawColor(Color.GREEN);
            gGameState.draw(canvas);
        } else {
            canvas.drawColor(Color.RED);
        }
    }
}
