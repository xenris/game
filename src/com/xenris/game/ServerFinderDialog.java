package com.xenris.game;

import android.app.AlertDialog;
import android.bluetooth.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import java.util.*;

public class ServerFinderDialog extends AlertDialog
    implements
        OnItemClickListener,
        OnCancelListener,
        Bluetooth.Callbacks {

    private ArrayList<BluetoothDevice> gBluetoothDevices;
    private ArrayAdapter serverList;
    private Callbacks gCallbacks;
    private Bluetooth gBluetooth;

    public ServerFinderDialog(Context context, Callbacks callbacks, Bluetooth bluetooth) {
        super(context);

        gCallbacks = callbacks;
        gBluetooth = bluetooth;

        if(bluetooth.isBluetoothEnabled()) {
            setTitle("Searching...");
            setupDialog(context);
            bluetooth.startSearching(this);
        } else {
            setTitle("Enable bluetooth first");
        }
    }

    public void setupDialog(Context context) {
//        bluetoothDevices = new ArrayList<BluetoothDevice>();
        gBluetoothDevices = gBluetooth.getDevices();

        final ArrayList<String> stringList = new ArrayList<String>();
        final int simple = android.R.layout.simple_list_item_1;
        serverList = new ArrayAdapter(context, simple, stringList);
        for(BluetoothDevice device : gBluetoothDevices) {
            stringList.add(device.getName());
        }

        final ListView listView = new ListView(context);
        listView.setAdapter(serverList);
        listView.setOnItemClickListener(this);

        setOnCancelListener(this);
        setView(listView);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        gBluetooth.stopsearching();
        gCallbacks.onNothingSelected(this);
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        gBluetooth.stopsearching();
        dismiss();
        final BluetoothDevice bluetoothDevice = gBluetoothDevices.get(position);
        gCallbacks.onServerSelected(this, bluetoothDevice);
    }

    public void onDeviceFound(final BluetoothDevice bluetoothDevice) {
        gBluetoothDevices.add(bluetoothDevice);
        serverList.add(bluetoothDevice.getName());
    }

    public interface Callbacks {
        public void onServerSelected(ServerFinderDialog dialog, BluetoothDevice device);
        public void onNothingSelected(ServerFinderDialog dialog);
    }
}
