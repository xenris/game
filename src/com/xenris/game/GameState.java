package com.xenris.game;

import android.graphics.*;
import java.io.*;
import java.util.*;

public class GameState {
    private ArrayList<PlayerState> gPlayerStates;

    public GameState() {
        gPlayerStates = new ArrayList<PlayerState>();
    }

    public GameState(DataInputStream dataInputStream) throws IOException {
        gPlayerStates = new ArrayList<PlayerState>();

        final int playerCount = dataInputStream.readInt();

        for(int i = 0; i < playerCount; i++) {
            gPlayerStates.add(new PlayerState(dataInputStream));
        }
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(gPlayerStates.size());

        for(PlayerState playerState : gPlayerStates) {
            playerState.write(dataOutputStream);
        }
    }

    public void addPlayerState(int id) {
        gPlayerStates.add(new PlayerState(id));
    }

    public void setPlayerState(PlayerState newPlayerState) {
        final int id = newPlayerState.getId();
        final PlayerState playerState = findPlayerStateById(id);
        if(playerState != null) {
            playerState.setValues(newPlayerState);
        }
    }

    public PlayerState findPlayerStateById(int id) {
        for(PlayerState playerState : gPlayerStates) {
            if(playerState.getId() == id) {
                return playerState;
            }
        }

        return null;
    }

    public void draw(Canvas canvas) {
        for(PlayerState playerState : gPlayerStates) {
            playerState.draw(canvas);
        }
    }
}
