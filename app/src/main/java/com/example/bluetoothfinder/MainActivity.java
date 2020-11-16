 package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

 public class MainActivity extends AppCompatActivity {

     ListView listView;
     TextView statustextView;
     Button searchButton;
     ArrayList<String> devices = new ArrayList<>();
     ArrayList<String> addresses = new ArrayList<>();
     ArrayAdapter arrayAdapter;

     BluetoothAdapter bluetoothAdapter;
     private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("bluetooth",action);

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                statustextView.setText("Finished");
                searchButton.setEnabled(true);
            } else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String Address = device.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);  //more the negative number,stronger is the bluetooth
                String RSSI = Integer.toString(rssi);

//                Log.i("Device found", "Name " + name + "Address " + Address + "RSSI value " + RSSI);

                if(!addresses.contains(Address)) {
                    addresses.add(Address);

                    String deviceString = "";

                    if(name == null || name.equals("")) {
                        deviceString = Address + "            -RSSI " + RSSI + "dBm";
                    } else {
                        deviceString = name + "            -RSSI " + RSSI + "dBm";
                    }

                    devices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
         }
     };

    public void searchClicked(View view) {
        statustextView.setText("Searching...");
        searchButton.setEnabled(false);   //disables searchButton
        devices.clear();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        statustextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,devices);
        listView.setAdapter(arrayAdapter);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }
}