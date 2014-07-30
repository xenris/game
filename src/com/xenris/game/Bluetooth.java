package com.xenris.game;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import java.io.*;
import java.util.*;

public class Bluetooth {
    private BluetoothAdapter gBluetoothAdapter;
    private AcceptThread gAcceptThread;
    private Activity gActivity;
    private Callbacks gCallbacks;

    public Bluetooth(Activity activity) {
        gActivity = activity;

        gBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(gBluetoothAdapter == null) {
            // Do something to tell the user that there is no bluetooth available.
        }
    }

    protected boolean isBluetoothEnabled() {
        if(gBluetoothAdapter == null) {
            return false;
        } else {
            return gBluetoothAdapter.isEnabled();
        }
    }

    protected void enableBluetooth(boolean enable) {
        if(enable) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            gActivity.startActivityForResult(intent, Constants.REQUEST_ENABLE_BLUETOOTH);
        } else {
            // TODO Turn bluetooth off.
        }
    }

    public ArrayList<BluetoothDevice> getDevices() {
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        // FIXME Connecting using pre-paired devices doesn't seem to work.
//        for(BluetoothDevice device : gBluetoothAdapter.getBondedDevices()) {
//            devices.add(device);
//        }

        return devices;
    }

    public void startSearching(Callbacks callbacks) {
        gCallbacks = callbacks;

        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        gActivity.registerReceiver(receiver, filter);
        gBluetoothAdapter.startDiscovery();
    }

    protected void stopsearching() {
        gBluetoothAdapter.cancelDiscovery();
        try {
            gActivity.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) { }
    }

    public BluetoothServerConnection connect(BluetoothDevice bluetoothDevice, ServerConnection.Callbacks callbacks) {
        try {
            final BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(Constants.uuid);
            bluetoothSocket.connect();
            return new BluetoothServerConnection(bluetoothSocket, callbacks);
        } catch (IOException e) {
            return null;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == Constants.REQUEST_ENABLE_BLUETOOTH) {
//            // Bluetooth has been enabled.
//        } else if(requestCode == Constants.START_SHARING) {
//            gAcceptThread.start();
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                gCallbacks.onDeviceFound(bluetoothDevice);
            }
        }
    };

    public interface Callbacks {
        public void onDeviceFound(BluetoothDevice bluetoothDevice);
    }

//    private class CreateConnection extends Thread {
//        private BluetoothDevice gBluetoothDevice;
//        private ServerConnection.Callbacks gCallbacks;

//        public CreateConnection(BluetoothDevice bluetoothDevice, ServerConnection.Callbacks callbacks) {
//            gBluetoothDevice = bluetoothDevice;
//            gCallbacks = callbacks;
//            start();
//        }

//        @Override
//        public void run() {
//            if(gBluetoothDevice != null) {
//                try {
//                    BluetoothSocket bluetoothSocket = gBluetoothDevice.createRfcommSocketToServiceRecord(Constants.uuid);
//                    bluetoothSocket.connect();
//                    onConnectionMade(new BluetoothServerConnection(bluetoothSocket, gCallbacks));
//                } catch (IOException e) {
//                    onConnectionFailed();
//                }
//            }
//        }
//    }

    public void startSharing(Server server) {
        if(gBluetoothAdapter != null) {
            if(gAcceptThread == null) {
                gAcceptThread = new AcceptThread(server);
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Constants.DISCOVERABLE_TIME);
                gActivity.startActivityForResult(intent, Constants.START_SHARING);
                gAcceptThread.start();
            }
        }
    }

    public void stopSharing() {
        if(gAcceptThread != null) {
            gAcceptThread.close();
            gAcceptThread = null;
        }
    }

    private class AcceptThread extends Thread {
        private final String NAME = "BluetoothTest";
        private BluetoothServerSocket gBluetoothServerSocket;
        private Server gServer;

        public AcceptThread(Server server) {
            gServer = server;

            try {
                gBluetoothServerSocket = gBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, Constants.uuid);
            } catch (IOException e) { }
        }

        public void run() {
            while(true) {
                try {
                    final BluetoothSocket bluetoothSocket = gBluetoothServerSocket.accept();
                    gServer.addClientConnection(new BluetoothClientConnection(bluetoothSocket));
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void close() {
            Util.close(gBluetoothServerSocket);
        }
    }
}
