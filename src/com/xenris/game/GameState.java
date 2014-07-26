package com.xenris.game;

import android.graphics.*;
import java.io.*;
import java.util.*;

public class GameState {
    public static final int MAIN_MENU = 1;
    public static final int COUNTDOWN = 2;
    public static final int IN_PLAY = 3;

    private ArrayList<ClientInfo> gClientInfos;
    private int gState = MAIN_MENU;
//    private boolean gGameIsAboutToStart;
//    private boolean gGameIsInPlay;

    public GameState() {
        gClientInfos = new ArrayList<ClientInfo>();
    }

    public GameState(DataInputStream dataInputStream) throws IOException {
        gClientInfos = new ArrayList<ClientInfo>();

        final int playerCount = dataInputStream.readInt();

        for(int i = 0; i < playerCount; i++) {
            gClientInfos.add(new ClientInfo(dataInputStream));
        }

        gState = dataInputStream.readInt();

//        gGameIsAboutToStart = dataInputStream.readBoolean();
//        gGameIsInPlay = dataInputStream.readBoolean();
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(gClientInfos.size());

        for(ClientInfo playerState : gClientInfos) {
            playerState.write(dataOutputStream);
        }

        dataOutputStream.writeInt(gState);

//        dataOutputStream.writeBoolean(gGameIsAboutToStart);
//        dataOutputStream.writeBoolean(gGameIsInPlay);
    }

    public void addClientInfo(int id) {
        gClientInfos.add(new ClientInfo(id));
    }

    public void updateClientInfo(ClientInfo newClientInfo) {
        final int id = newClientInfo.getId();
        final ClientInfo playerState = findClientInfoById(id);
        if(playerState != null) {
            playerState.setValues(newClientInfo);
        }
    }

    public ClientInfo findClientInfoById(int id) {
        for(ClientInfo playerState : gClientInfos) {
            if(playerState.getId() == id) {
                return playerState;
            }
        }

        return null;
    }

    public void draw(Canvas canvas) {
        for(ClientInfo playerState : gClientInfos) {
            playerState.draw(canvas);
        }
    }

    public int state() {
        return gState;
    }

    public void state(int state) {
        gState = state;
    }

//    public boolean gameIsAboutToStart() {
//        return gGameIsAboutToStart;
//    }

//    public void gameIsAboutToStart(boolean value) {
//        gGameIsAboutToStart = value;
//    }

//    public boolean gameIsInPlay() {
//        return gGameIsInPlay;
//    }

//    public void gameIsInPlay(boolean value) {
//        gGameIsInPlay = value;
//    }
}
