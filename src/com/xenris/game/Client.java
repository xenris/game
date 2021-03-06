package com.xenris.game;

import android.app.*;
import android.bluetooth.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.xenris.game.Bluetooth.CreateConnectionCallbacks;
import java.util.*;

public class Client extends BaseActivity
    implements
        CreateConnectionCallbacks,
        Runnable,
        View.OnTouchListener {

    public Server gServer; // XXX Idealy this won't be needed. Just an extra thing to be null unexpectedly.
    private ServerConnection gServerConnection;
    private ClientInfo gMe;

    private boolean gRunning;
    private Thread gGameThread;

    // XXX XXX XXX
    // Put game touch state here and use to send to server and draw positional info.
    //  Could also be called PlayerState or LocalPlayerState or something.
//    private TouchInfo gTouchInfo = new TouchInfo();

    private View gMenuView;
    private GameView gGameView;

    private Bluetooth gBluetooth;

    private AlertDialog gSearchAlertDialog;
    private ArrayList<BluetoothDevice> gBluetoothDevices;

    private int backButtonCount = 0;
    private long backButtonPreviousTime = 0;
    private boolean backButtonMessageHasBeenShown = false;

    @Override
    public void onCreate() {
        super.onCreate();

        gGameView = new GameView(this);
        gGameView.setVisibility(View.GONE);
        addView(gGameView);
        gMenuView = addView(R.layout.game_menu);

        gGameView.setOnTouchListener(this);

        setupHandlers();

        gServer = new Server();
        gServerConnection = gServer.createConnection();
        gServerConnection.start();
        gServer.start();
        gMe = new ClientInfo(gServerConnection.getConnectionId(), Color.BLUE, true);
        gGameView.setClientInfoToDraw(gMe);

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
                case Constants.SWITCH_TO_GAME_MENU:
                    gMenuView.setVisibility(View.VISIBLE);
                    gGameView.setVisibility(View.GONE);
                    break;
                case Constants.SWITCH_TO_GAME_VIEW:
                    gMenuView.setVisibility(View.GONE);
                    gGameView.setVisibility(View.VISIBLE);
                    break;
                case Constants.CONNECTION_MADE:
                    Toast.makeText(Client.this, "Connection made!", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CONNECTION_FAILED:
                    Toast.makeText(Client.this, "Connection failed", Toast.LENGTH_SHORT).show();
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
            final GameState newGameState = gServerConnection.getNextGameState();

            if(newGameState != null) {
                gameState = newGameState;
                gGameView.setGameStateToDraw(gameState);
                if(gameState.state() == GameState.IN_PLAY) {
                    if(gameMenuIsVisible()) {
                        uiHandler.sendEmptyMessage(Constants.SWITCH_TO_GAME_VIEW);
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
        final float x = event.getX() / gGameView.getWidth();
        final float y = event.getY() / gGameView.getHeight();
        final float sx = x * 800;
        final float sy = y * 480;

        if(action == MotionEvent.ACTION_MOVE) {
            // XXX gMe probably needs to be synchronized.
            gMe.setX((int)sx);
            gMe.setY((int)sy);
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
                // TODO Show "connecting" progress dialog.
                gBluetooth.connect(device, Client.this);
            }

            @Override
            public void onNothingSelected(ServerFinderDialog dialog) {
            }
        };

        final ServerFinderDialog dialog = new ServerFinderDialog(this, callbacks, gBluetooth);
        dialog.show();
    }

    @Override
    public void onConnectionMade(BluetoothServerConnection bluetoothServerConnection) {
        gServerConnection.close();
        gServerConnection = bluetoothServerConnection;
        gServerConnection.start();
        gMe = new ClientInfo(gServerConnection.getConnectionId(), Color.BLUE, true);
        gGameView.setClientInfoToDraw(gMe);

        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();
        uiHandler.sendEmptyMessage(Constants.CONNECTION_MADE);
    }

    @Override
    public void onConnectionFailed() {
        final MyApplication application = (MyApplication)getApplication();
        final Handler uiHandler = application.getUiHandler();
        uiHandler.sendEmptyMessage(Constants.CONNECTION_FAILED);
    }

    @Override
    public void onBackPressed() {
        final long currentTime = System.currentTimeMillis();
        final long timeDiff = currentTime - backButtonPreviousTime;

        backButtonPreviousTime = currentTime;

        if((timeDiff < Constants.BACK_PRESS_DELAY) || (backButtonCount == 0)) {
            backButtonCount++;
        } else {
            backButtonCount = 1;
        }

        if(backButtonCount >= Constants.BACK_PRESS_COUNT) {
            finish();
        }

        if(!backButtonMessageHasBeenShown) {
            final String msg = "Press back " + Constants.BACK_PRESS_COUNT + " times to exit";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            backButtonMessageHasBeenShown = true;
        }
    }
}
