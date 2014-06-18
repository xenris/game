package com.xenris.game.client;

import android.app.*;
import android.bluetooth.*;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;
import com.xenris.game.*;
import java.util.*;

public class Network extends Bluetooth implements OnCancelListener, OnItemClickListener {
    private AlertDialog searchAlertDialog;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private ArrayAdapter serverList;
    private ServerConnection.Callbacks gCallbacks;

    protected void openSearchDialog(ServerConnection.Callbacks callbacks) {
        gCallbacks = callbacks;

        bluetoothDevices = new ArrayList<BluetoothDevice>();

        final ArrayList<String> stringList = new ArrayList<String>();
        final int simple = android.R.layout.simple_list_item_1;
        serverList = new ArrayAdapter(this, simple, stringList);

        final ListView listView = new ListView(this);
        listView.setAdapter(serverList);
        listView.setOnItemClickListener(this);

        searchAlertDialog = new AlertDialog.Builder(this)
            .setTitle("Searching...")
            .setOnCancelListener(this)
            .setView(listView)
            .show();

        startSearching();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        stopsearching();
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        searchAlertDialog.cancel();
        BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);
        stopsearching();
        createServerConnection(bluetoothDevice, gCallbacks);
        // TODO Create a dialog to show that the connection is
        //  currently being made or has failed.
    }

    @Override
    public void onDeviceFound(final BluetoothDevice bluetoothDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothDevices.add(bluetoothDevice);
                serverList.add(bluetoothDevice.getName());
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        Log.message(Log.tag, "onConnectionFailed()");
    }
}
