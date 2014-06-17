package com.xenris.game;

import android.graphics.*;
import java.io.*;

public class PlayerState {
    private static final Paint gPaint = new Paint();
    private int gId;
    private int gX;
    private int gY;
    private int gColor;

    public PlayerState(int id) {
        gId = id;
        gColor = Color.BLUE;
    }

    public int getId() {
        return gId;
    }

    public int getX() {
        return gX;
    }

    public void setX(int x) {
        gX = x;
    }

    public int getY() {
        return gY;
    }

    public void setY(int y) {
        gY = y;
    }

    public int getColor() {
        return gColor;
    }

    public void setColor(int color) {
        gColor = color;
    }

    public PlayerState(DataInputStream dataInputStream) throws IOException {
        gId = dataInputStream.readInt();
        gX = dataInputStream.readInt();
        gY = dataInputStream.readInt();
        gColor = dataInputStream.readInt();
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(gId);
        dataOutputStream.writeInt(gX);
        dataOutputStream.writeInt(gY);
        dataOutputStream.writeInt(gColor);
    }

    public void setValues(PlayerState otherPlayerState) {
        gX = otherPlayerState.getX();
        gY = otherPlayerState.getY();
        gColor = otherPlayerState.getColor();
    }

    public void draw(Canvas canvas) {
        gPaint.setColor(gColor);
        canvas.drawCircle(gX, gY, 200, gPaint);
    }
}
