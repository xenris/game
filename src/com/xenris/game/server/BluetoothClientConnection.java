package com.xenris.game.server;

import android.bluetooth.BluetoothSocket;
import com.xenris.game.*;
import java.io.*;

public class BluetoothClientConnection extends ClientConnection {
    private BluetoothSocket gBluetoothSocket;

    public BluetoothClientConnection(BluetoothSocket bluetoothSocket) {
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
