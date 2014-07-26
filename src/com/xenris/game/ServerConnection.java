package com.xenris.game;

import java.io.*;

public class ServerConnection extends Thread {
    private int gConnectionId;
    private Callbacks gCallbacks;
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;

    public ServerConnection() {
    }

    public ServerConnection(OutputStream outputStream, InputStream inputStream, Callbacks callbacks) {
        init(outputStream, inputStream, callbacks);
    }

    protected void init(OutputStream outputStream, InputStream inputStream, Callbacks callbacks) {
        gCallbacks = callbacks;

        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        try {
            gConnectionId = gDataInputStream.readInt();
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to get connection ID in ServerConnection");
            return;
        }

        start();
    }

    @Override
    public void run() {
        setName("ServerConnection");

        while(true) {
            GameState gameState = null;

            try {
                gameState = new GameState(gDataInputStream);
            } catch (IOException e) {
                break;
            }

            gCallbacks.onNewGameState(gameState);
        }

        close();
    }

    public void sendClientInfo(ClientInfo clientInfo) {
        try {
            clientInfo.write(gDataOutputStream);
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to send player state in ServerConnection");
        }
    }

    public int getConnectionId() {
        return gConnectionId;
    }

    public void close() {
        Util.close(gDataOutputStream);
        Util.close(gDataInputStream);
    }

    public interface Callbacks {
        public void onNewGameState(GameState gameState);
    }
}
