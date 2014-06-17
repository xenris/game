package com.xenris.game.client;

import android.view.*;
import com.xenris.game.*;
import com.xenris.game.server.Server;

public class Client extends Bluetooth implements ServerConnection.Callbacks, View.OnTouchListener {
    private GameView gGameView;
    private Server gServer;
    private ServerConnection gServerConnection;
    private PlayerState gMe;

    @Override
    public void onCreate() {
        super.onCreate();

        gGameView = new GameView(this);
        addView(gGameView);
        gGameView.setOnTouchListener(this);

        gServer = new Server();
        gServerConnection = gServer.createConnection(this);
        gMe = new PlayerState(gServerConnection.getConnectionId());
        gServer.start();
    }

    @Override
    public void onNewGameState(GameState gameState) {
        gGameView.setGameState(gameState);
    }

    @Override
    public void onDestroy() {
        gServerConnection.close();

        super.onDestroy();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getActionMasked();

        final int x = (int)motionEvent.getX();
        final int y = (int)motionEvent.getY();

        if(action == MotionEvent.ACTION_DOWN) {
            gMe.setX(x);
            gMe.setY(y);
        } else if(action == MotionEvent.ACTION_MOVE) {
            gMe.setX(x);
            gMe.setY(y);
        } else if(action == MotionEvent.ACTION_UP) {
            gMe.setX(-1);
            gMe.setY(-1);
        }

        gServerConnection.sendPlayerState(gMe);

        return true;
    }
}
