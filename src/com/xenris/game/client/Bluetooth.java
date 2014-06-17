package com.xenris.game.client;

import android.bluetooth.*;
import android.content.*;
import com.xenris.game.*;
import java.io.*;

public class Bluetooth extends BaseActivity {
    private BluetoothAdapter gBluetoothAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

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
            startActivityForResult(intent, Constants.REQUEST_ENABLE_BLUETOOTH);
        } else {
            // TODO Turn bluetooth off.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_ENABLE_BLUETOOTH) {
            // Bluetooth has been enabled.
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
