package com.xenris.game.client;

import android.view.*;
import com.xenris.game.*;
import com.xenris.game.server.Server;

public class Client extends Network implements ServerConnection.Callbacks, View.OnTouchListener {
    private View gMenuView;
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

        gMenuView = addView(R.layout.game_menu);

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
        stopSharing();

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

    @Override
    public void onConnectionMade(ServerConnection serverConnection) {
        // XXX Make sure this doesn't cause a crash.
        gServer = null;
        gServerConnection.close();
        gServerConnection = serverConnection;
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.go_button) {
            go();
        } else if(id == R.id.share_button) {
            share();
        } else if(id == R.id.find_button) {
            find();
        }
    }

    private void go() {
        gMenuView.setVisibility(View.INVISIBLE);
    }

    private void share() {
        startSharing(gServer);
    }

    private void find() {
        openSearchDialog(this);
    }
}
