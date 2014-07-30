package com.xenris.game;

import android.bluetooth.*;
import java.io.*;

public class BluetoothServerConnection extends ServerConnection {
    private BluetoothSocket gBluetoothSocket;

    public BluetoothServerConnection(BluetoothSocket bluetoothSocket) {
        gBluetoothSocket = bluetoothSocket;

        try {
            final OutputStream outputStream = bluetoothSocket.getOutputStream();
            final InputStream inputStream = bluetoothSocket.getInputStream();

            init(outputStream, inputStream);
        } catch (IOException e) {
            close();
            return;
        }
    }

    @Override
    public void close() {
        Util.close(gBluetoothSocket);
        super.close();
    }
}
