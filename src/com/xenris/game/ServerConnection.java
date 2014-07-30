package com.xenris.game;

import java.io.*;
import java.util.*;

public class ServerConnection extends Thread {
    private int gConnectionId;
    private DataOutputStream gDataOutputStream;
    private DataInputStream gDataInputStream;
    private LinkedList<GameState> gGameStateQueue = new LinkedList<GameState>();

    public ServerConnection() {
    }

    public ServerConnection(OutputStream outputStream, InputStream inputStream) {
        init(outputStream, inputStream);
    }

    protected void init(OutputStream outputStream, InputStream inputStream) {
        gDataOutputStream = new DataOutputStream(outputStream);
        gDataInputStream = new DataInputStream(inputStream);

        try {
            gConnectionId = gDataInputStream.readInt();
        } catch (IOException e) {
            Log.message(Log.tag, "Error: failed to get connection ID in ServerConnection");
            return;
        }
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

            gGameStateQueue.add(gameState);
        }

        close();
    }

    public void sendClientInfo(ClientInfo clientInfo) {
        try {
            clientInfo.write(gDataOutputStream);
        } catch (IOException e) {
//            Log.message(Log.tag, "Error: failed to send player state in ServerConnection");
        }
    }

    public int getConnectionId() {
        return gConnectionId;
    }

    public void close() {
        Util.close(gDataOutputStream);
        Util.close(gDataInputStream);
    }

    public GameState getNextGameState() {
        return gGameStateQueue.poll();
    }
}
