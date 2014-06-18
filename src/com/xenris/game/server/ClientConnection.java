package com.xenris.game.server;

import com.xenris.game.*;
import java.io.*;

public class ClientConnection extends Thread {
    private int gConnectionId;
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private PlayerState gPlayerStateA;
    private PlayerState gPlayerStateB;
    private boolean gIsClosed = false;

    public ClientConnection() {
    }

    public ClientConnection(OutputStream outputStream, InputStream inputStream) {
        init(outputStream, inputStream);
    }

    public void init(OutputStream outputStream, InputStream inputStream) {
        gConnectionId = Util.getNextId();

        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        try {
            gDataOutputStream.writeInt(gConnectionId);
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to write connection ID in ClientConnection");
            return;
        }

        start();
    }

    @Override
    public void run() {
        while(true) {
            try {
                gPlayerStateB = new PlayerState(gDataInputStream);
            } catch (IOException e) {
                break;
            }

            shiftPlayerStates();
        }

        close();
    }

    public PlayerState getPlayerState() {
        synchronized (this) {
            return gPlayerStateA;
        }
    }

    private void shiftPlayerStates() {
        synchronized (this) {
            gPlayerStateA = gPlayerStateB;
            gPlayerStateB = null;
        }
    }

    public void sendGameState(GameState gameState) {
        try {
            gameState.write(gDataOutputStream);
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to send game state in ClientConnection");
        }
    }

    public int getConnectionId() {
        return gConnectionId;
    }

    public void close() {
        gIsClosed = true;
        Util.close(gDataOutputStream);
        Util.close(gDataInputStream);
    }

    public boolean isClosed() {
        return gIsClosed;
    }
}
