package com.xenris.game;

import android.os.*;
import android.view.*;
import android.widget.ToggleButton;
import java.util.LinkedList;

public class Client extends BaseActivity
    implements
        ServerConnection.Callbacks,
        Runnable,
        View.OnTouchListener {

//    public Server gServer; // XXX Idealy this won't be needed. Just an extra thing to be null unexpectedly.
    private ServerConnection gServerConnection;
//    private int myId;
    private ClientInfo gMe;

    private boolean gRunning;
    private Thread gGameThread;

    private LinkedList<GameState> gGameStateQueue = new LinkedList<GameState>();

    // XXX XXX XXX
    // Put game touch state here and use to send to server and draw positional info.
    //  Could also be called PlayerState or LocalPlayerState or something.
//    private TouchInfo gTouchInfo = new TouchInfo();

    private View gMenuView;
    private GameView gGameView;
//    private Thread gGameThread;

    @Override
    public void onCreate() {
        super.onCreate();

        gGameView = new GameView(this);
        addView(gGameView);
        gMenuView = addView(R.layout.game_menu);

        gGameView.setOnTouchListener(this);

        setupHandlers();

        final Server server = new Server();
        gServerConnection = server.createConnection(this);
        server.start();
        gMe = new ClientInfo(gServerConnection.getConnectionId());

        gGameThread = new Thread(this);
        gGameThread.setName("Client Game Loop Thread");
        gGameThread.start();
    }

    private void setupHandlers() {
        final MyApplication application = (MyApplication)getApplication();

        application.setUiHandler(new Handler(gUiHandlerCallback));
//        application.setGameHandler(new GameThread().getHandler());
    }

    private Handler.Callback gUiHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch(message.what) {
                case Constants.HIDE_GAME_MENU:
                    gMenuView.setVisibility(View.GONE);
                    break;
                case Constants.SHOW_GAME_MENU:
                    gMenuView.setVisibility(View.VISIBLE);
                    break;
            }

            return true;
        }
    };

//    private Handler.Callback gGameHandlerCallback = new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message message) {
//            final MyApplication application = (MyApplication)getApplication();

//            switch(message.what) {
//                case Constants.START_GAME:
//                    application.getUiHandler().sendEmptyMessage(Constants.HIDE_GAME_MENU);
//                    break;
//                case Constants.SHARE_SERVER:
//                    break;
//                case Constants.FIND_SERVER:
//                    break;
//            }

//            return true;
//        }
//    };

    @Override
    public void onDestroy() {
        gRunning = false;

        try {
            gGameThread.join();
        } catch(InterruptedException e) { }

        super.onDestroy();
    }

    @Override
    public void onNewGameState(GameState gameState) {
        gGameStateQueue.add(gameState);
//        gGameView.setGameStateToDraw(gameState);

        // TODO Add to a GameState queue for the GameThread to use when needed.
    }

//    @Override
//    public void onConnectionMade(ServerConnection serverConnection) {
//        // XXX Make sure this doesn't cause a crash.
//        gServer = null;
//        gServerConnection.close();
//        gServerConnection = serverConnection;
//    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.go_button) {
            go();
        } else if(id == R.id.share_button) {
//            share();
        } else if(id == R.id.find_button) {
//            find();
        } else if(id == R.id.ready_button) {
            final boolean ready = ((ToggleButton)view).isChecked();
            gMe.setReady(ready);
        }
    }

    @Override
    public void run() {
        gRunning = true;
        long previousTime = System.currentTimeMillis();
        GameState gameState = null;
        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();

        while(gRunning) {
            final long currentTime = System.currentTimeMillis();

            // Send 10 times per second.
            if((currentTime - previousTime) >= 10) {
                gServerConnection.sendClientInfo(gMe);
                previousTime = currentTime;
            }

            // If a new GameState has arrived from the server.
            if(!gGameStateQueue.isEmpty()) {
                gameState = gGameStateQueue.poll();
                gGameView.setGameStateToDraw(gameState);
                if(gameState.state() == GameState.IN_PLAY) {
                    if(gameMenuIsVisible()) {
                        uiHandler.sendEmptyMessage(Constants.HIDE_GAME_MENU);
                    }
                    gMe.setReady(false);
                }
            }

            Thread.yield();
        }

        gServerConnection.close();
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int action = event.getActionMasked();
        final int x = (int)event.getX();
        final int y = (int)event.getY();

        if(action == MotionEvent.ACTION_MOVE) {
            // XXX gMe probably needs to be synchronized.
            gMe.setX(x);
            gMe.setY(y);
        }

        return true;
    }

    private void go() {
        final MyApplication application = (MyApplication)getApplication();
        application.getUiHandler().sendEmptyMessage(Constants.HIDE_GAME_MENU);
    }

    private boolean gameMenuIsVisible() {
        return gMenuView.getVisibility() == View.VISIBLE;
    }

//    private void share() {
//        startSharing(gServer);
//    }

//    private void find() {
//        openSearchDialog(this);
//    }

//    private class GameThread extends Thread {
//        private Handler gHandler;

//        @Override
//        public void run() {
//            setName("GameThread");

//            Looper.prepare();

//            gHandler = new Handler(gGameHandlerCallback);

//            Looper.loop();
//        }

//        public Handler getHandler() {
//            start();

//            while(gHandler == null) {
//                Thread.yield();
//            }

//            return gHandler;
//        }
//    }
}
