package com.xenris.game.server;

import com.xenris.game.*;
import com.xenris.game.client.*;
import java.io.*;
import java.util.*;

public class Server extends Thread {
    private ArrayList<ClientConnection> gClientConnections = new ArrayList<ClientConnection>();
    private GameState gGameState = new GameState();

    @Override
    public void run() {
        while(gClientConnections.size() > 0) {
            getPlayerStates();
//            updateGameState();
            sendGameState();
            removeClosedConnections();
            Util.sleep(100); // XXX Dodgy speed regulation.
        }
    }

    private void getPlayerStates() {
        for(ClientConnection clientConnection : gClientConnections) {
            final PlayerState playerState = clientConnection.getPlayerState();
            if(playerState != null) {
                gGameState.setPlayerState(playerState);
            }
        }
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
        gGameState.addPlayerState(id);
        gClientConnections.add(clientConnection);
    }
}
