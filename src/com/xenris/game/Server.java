package com.xenris.game;

import java.io.*;
import java.util.*;

// Note: Need to call start on the server AFTER the first connection
//  is made otherwise the server will simply exit.

public class Server extends Thread {
    private ArrayList<ClientConnection> gClientConnections = new ArrayList<ClientConnection>();
    private GameState gGameState = new GameState();

    @Override
    public void run() {
        setName("Server Game Loop Thread");

        while(gClientConnections.size() > 0) {
            getClientInfo();

            updateGameState();

            sendGameState();

            removeClosedConnections();

            Util.sleep(100); // XXX Dodgy speed regulation. 10 times per second.
        }
    }

    private void getClientInfo() {
        for(ClientConnection clientConnection : gClientConnections) {
            final ClientInfo clientInfo = clientConnection.getClientInfo();
            if(clientInfo != null) {
                gGameState.updateClientInfo(clientInfo);
            }
        }
    }

    private void updateGameState() {
        if(gGameState.state() == GameState.IN_PLAY) {
        } else if(gGameState.state() == GameState.COUNTDOWN) {
            gGameState.state(GameState.IN_PLAY);
        } else if(gGameState.state() == GameState.MAIN_MENU) {
            // Check if everyone is ready to start the game.
            boolean everyoneIsReady = true;

            for(ClientConnection clientConnection : gClientConnections) {
                final ClientInfo clientInfo = clientConnection.getClientInfo();
                if(clientInfo != null) {
                    everyoneIsReady = everyoneIsReady && clientInfo.isReady();
                } else {
                    everyoneIsReady = false;
                }
            }

            if(everyoneIsReady) {
                gGameState.state(GameState.COUNTDOWN);
            }
        }

        // TODO Step game.
    }

    public void sendGameState() {
        for(ClientConnection clientConnection : gClientConnections) {
            clientConnection.sendGameState(gGameState);
        }
    }

    public void removeClosedConnections() {
        ClientConnection toRemove = null;

        for(ClientConnection clientConnection : gClientConnections) {
            if(clientConnection.isClosed()) {
                toRemove = clientConnection;
                break;
            }
        }

        if(toRemove != null) {
            gClientConnections.remove(toRemove);
            toRemove.close();
        }
    }

    public ServerConnection createConnection(ServerConnection.Callbacks callbacks) {
        // s = for server
        // c = for client
        final PipedOutputStream oss = new PipedOutputStream();
        final PipedOutputStream osc = new PipedOutputStream();
        final PipedInputStream iss = new PipedInputStream();
        final PipedInputStream isc = new PipedInputStream();

        try {
            oss.connect(isc);
            iss.connect(osc);
        } catch (IOException e) {
            return null;
        }

        addClientConnection(new ClientConnection(oss, iss));

        return new ServerConnection(osc, isc, callbacks);
    }

    public void addClientConnection(ClientConnection clientConnection) {
        final int id = clientConnection.getConnectionId();
        gGameState.addClientInfo(id);
        gClientConnections.add(clientConnection);
    }
}
