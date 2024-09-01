package com.example.bleapp.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bleapp.R;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class InfoDetailsActivity extends AppCompatActivity {
    public static ScanResult scanResult;
    private Toolbar toolbar;
    public static String scannedBleName;
    public static String scannedBleAddress;
    public static BluetoothGatt bluetoothGattData;
    private TextView nameTv;
    private TextView addressTv;
    private TextView batteryTv;
    private BluetoothGattCallback gattCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_details);
        initialization();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private BluetoothGattCallback getGattCallBack() {
        return new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String deviceAddress = gatt.getDevice().getAddress();
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.w("BluetoothGattCallback", "Successfully connected to " + deviceAddress);
                        gatt.discoverServices();
                        String name = gatt.getDevice().getName();
                        int type = gatt.getDevice().getType();
                        Toast.makeText(InfoDetailsActivity.this, "Successfully connected to " + name, Toast.LENGTH_SHORT).show();

                        Log.w("BluetoothGattCallback", " device type " + type);
                        Log.w("BluetoothGattCallback", " device name " + name);
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.w("BluetoothGattCallback", "Successfully disconnected from " + deviceAddress);
                        Toast.makeText(InfoDetailsActivity.this, "Device disconnected", Toast.LENGTH_SHORT).show();

                        gatt.close();
                    }
                } else {
                    Log.w("BluetoothGattCallback", "Error " + status + " encountered for " + deviceAddress + "! Disconnecting...");
                    Toast.makeText(InfoDetailsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    gatt.close();
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    readDeviceName(gatt);
                } else {
                    Log.w("BLE", "onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                byte[] data = characteristic.getValue();
                switch (status) {
                    case BluetoothGatt.GATT_SUCCESS:
                        Log.i("BluetoothGattCallback", "Read characteristic " + characteristic.getUuid());

                        if (characteristic.getUuid().equals(UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"))) {
                            parseBatteryLevel(characteristic);
                        } else if (characteristic.getUuid().equals(UUID.fromString("00002A00-0000-1000-8000-00805f9b34fb"))) {
                            parseDeviceName(characteristic);
                            readBatteryLevel(gatt);
                        }

                        break;
                    case BluetoothGatt.GATT_READ_NOT_PERMITTED:
                        Log.e("BluetoothGattCallback", "Read not permitted for " + characteristic.getUuid() + "!");
                        break;
                    default:
                        Log.e("BluetoothGattCallback", "Characteristic read failed for " + characteristic.getUuid() + ", error: " + status);
                        break;
                }

            }


        };
    }

    @SuppressLint("MissingPermission")
    private void readBatteryLevel(BluetoothGatt gatt) {
        UUID serviceUUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb"); // Battery Service
        UUID characteristicUUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb"); // Battery Level Characteristic

        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                gatt.readCharacteristic(characteristic);
            } else {
                Log.e("Characteristic", "Characteristic not found: " + characteristicUUID);
            }
        } else {
            Log.e("Service", "Service not found: " + serviceUUID);
        }

    }

    // Method to read the Device Name characteristic
    @SuppressLint("MissingPermission")
    private void readDeviceName(BluetoothGatt gatt) {
        UUID genericAccessServiceUUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        UUID deviceNameCharacteristicUUID = UUID.fromString("00002A00-0000-1000-8000-00805f9b34fb");
        if (gatt == null) {
            Log.w("readDeviceName", "BluetoothGatt not initialized");
            return;
        }

        BluetoothGattService genericAccessService = gatt.getService(genericAccessServiceUUID);
        if (genericAccessService == null) {
            Log.w("readDeviceName", "Generic Access Service not found!");
            return;
        }

        BluetoothGattCharacteristic deviceNameCharacteristic = genericAccessService.getCharacteristic(deviceNameCharacteristicUUID);
        if (deviceNameCharacteristic == null) {
            Log.w("readDeviceName", "Device Name characteristic not found!");
            return;
        }

        // Request to read the characteristic value
        gatt.readCharacteristic(deviceNameCharacteristic);
    }


    @SuppressLint("MissingPermission")
    private void initialization() {
        toolbar = findViewById(R.id.toolbar);
        nameTv = findViewById(R.id.tv_name);
        addressTv = findViewById(R.id.tv_device_address);
        batteryTv = findViewById(R.id.tv_bt_level);
        gattCallback = getGattCallBack();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_bar_title);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        addressTv.setText(scannedBleAddress);
        bluetoothGattData = scanResult.getDevice().connectGatt(getApplicationContext(), false, gattCallback);
    }


    private void parseBatteryLevel(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();

        if (value != null && value.length > 0) {
            int batteryLevel = value[0] & 0xFF; // Convert unsigned byte to int
            runOnUiThread(() -> {
                batteryTv.setText(String.format("%s%%", batteryLevel));
            });
            Log.i("BatteryLevel", "Battery Level: " + batteryLevel + "%");
        } else {
            Log.w("parseBatteryLevel", "Battery level data is empty or null");
        }
    }

    private void parseDeviceName(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();

        if (value != null && value.length > 0) {
            // Convert byte array to string
            String deviceName = new String(value, StandardCharsets.UTF_8);
            runOnUiThread(() -> {
                nameTv.setText(deviceName);
            });

            Log.i("DeviceName", "Device Name: " + deviceName);
        } else {
            Log.w("parseDeviceName", "Device Name data is empty or null");
        }
    }

}