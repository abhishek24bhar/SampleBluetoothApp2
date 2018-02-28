package com.my.com.samplebluetoothapp;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   private BluetoothHeadset mBluetoothHeadset;
    private static final int REQUEST_ENABLE_BT = 1;
private List<String>  mDeviceList=new ArrayList<>();
private ListView listView;
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         listView=(ListView)findViewById(R.id.lv);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
       // mBluetoothAdapter.startDiscovery();

    }
    private void checkBTState(){
// Check for Bluetooth support and then check to make sure it is turned on
        // If it isn't request to turn it on
        // List paired devices
        // Emulator doesn't support Bluetooth and will return null
        if(mBluetoothAdapter==null) {

            return;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
               // out.append("\nBluetooth is enabled...");

                // Starting the device discovery
                mBluetoothAdapter.startDiscovery();
            } else {
                Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v)
    {
        if(v.getId()==R.id.btooth)
        {
            //checkBluetoothConnection();
        }
    }
    private void checkBluetoothConnection(){
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    mBluetoothHeadset = (BluetoothHeadset) proxy;
                }

            }

            public void onServiceDisconnected(int profile) {
                Log.i("Bluetooth Service","Disconnected");
                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                // TODO
            }
        };
        mBluetoothAdapter.getProfileProxy(MainActivity.this, mProfileListener, BluetoothProfile.HEADSET);
        //mBluetoothHeadset.isAudioConnected()

        for (BluetoothDevice device:mBluetoothAdapter.getBondedDevices()
             ) {
            if(mBluetoothHeadset!=null) {
                mBluetoothHeadset.isAudioConnected(device);
                if (mBluetoothHeadset.getConnectionState(device) == BluetoothHeadset.STATE_AUDIO_CONNECTED) {

                } else if (mBluetoothHeadset.getConnectionState(device) == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getBluetoothConnectedDevice(){
       final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    boolean deviceConnected = false;
                    BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
                    List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
                    if (a2dpConnectedDevices.size() != 0) {
                        for (BluetoothDevice device : a2dpConnectedDevices) {
                            if (device.getName().contains("DEVICE_NAME")) {
                                deviceConnected = true;
                            }
                        }
                    }
                    if (!deviceConnected) {
                        Toast.makeText(MainActivity.this, "DEVICE NOT CONNECTED", Toast.LENGTH_SHORT).show();
                    }
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
                }
            }

            public void onServiceDisconnected(int profile) {
                // TODO
            }
        };
        mBluetoothAdapter.getProfileProxy(MainActivity.this, mProfileListener, BluetoothProfile.A2DP);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
