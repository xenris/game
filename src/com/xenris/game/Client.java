package com.xenris.game;

import android.app.*;
import android.bluetooth.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.*;

public class Client extends BaseActivity
    implements
        ServerConnection.Callbacks,
        Runnable,
        View.OnTouchListener {

    public Server gServer; // XXX Idealy this won't be needed. Just an extra thing to be null unexpectedly.
    private ServerConnection gServerConnection;
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

    private Bluetooth gBluetooth;

    private AlertDialog gSearchAlertDialog;
    private ArrayList<BluetoothDevice> gBluetoothDevices;

    @Override
    public void onCreate() {
        super.onCreate();

        gGameView = new GameView(this);
        addView(gGameView);
        gMenuView = addView(R.layout.game_menu);

        gGameView.setOnTouchListener(this);

        setupHandlers();

        gServer = new Server();
        gServerConnection = gServer.createConnection(this);
        gServerConnection.start();
        gServer.start();
        gMe = new ClientInfo(gServerConnection.getConnectionId());

        gGameThread = new Thread(this);
        gGameThread.setName("Client Game Loop Thread");
        gGameThread.start();

        gBluetooth = new Bluetooth(this);
    }

    private void setupHandlers() {
        final MyApplication application = (MyApplication)getApplication();

        application.setUiHandler(new Handler(gUiHandlerCallback));
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

    @Override
    public void onDestroy() {
        gBluetooth.stopSharing();

        gRunning = false;

        try {
            gGameThread.join();
        } catch(InterruptedException e) { }

        super.onDestroy();
    }

    @Override
    public void onNewGameState(GameState gameState) {
        gGameStateQueue.add(gameState);
    }

    public void buttonHandler(View view) {
        final int id = view.getId();

        if(id == R.id.share_button) {
            share();
        } else if(id == R.id.find_button) {
            find();
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
            if((currentTime - previousTime) >= 100) {
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

    private boolean gameMenuIsVisible() {
        return gMenuView.getVisibility() == View.VISIBLE;
    }

    private void share() {
        if(gBluetooth.isBluetoothEnabled()) {
            gBluetooth.startSharing(gServer);
        } else {
            Toast.makeText(this, "Enable bluetooth first", Toast.LENGTH_SHORT).show();
        }
    }

    private void find() {
        final ServerFinderDialog.Callbacks callbacks = new ServerFinderDialog.Callbacks() {
            @Override
            public void onServerSelected(ServerFinderDialog dialog, BluetoothDevice device) {
                // TODO Show "connecting" progress dialog. Have onConnectionMade and onConnectionFailed callbacks.
                final ServerConnection newServerConnection = gBluetooth.connect(device, Client.this);
                if(newServerConnection != null) {
                    gServerConnection.close();
                    gServerConnection = newServerConnection;
                    gServerConnection.start();
                    gMe = new ClientInfo(gServerConnection.getConnectionId());
                } else {
                    // TODO Show connection error message.
                    Log.message("could not connect");
                }
            }

            @Override
            public void onNothingSelected(ServerFinderDialog dialog) {
            }
        };

        final ServerFinderDialog dialog = new ServerFinderDialog(this, callbacks, gBluetooth);
        dialog.show();
    }
}
