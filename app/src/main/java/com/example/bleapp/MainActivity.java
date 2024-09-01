package com.example.bleapp;

import static com.example.bleapp.utils.PermissionUtils.hasPermission;
import static com.example.bleapp.utils.PermissionUtils.hasRequiredBluetoothPermissions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;

import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bleapp.activity.InfoDetailsActivity;
import com.example.bleapp.adapter.BleItemInterface;
import com.example.bleapp.adapter.ScanResultAdapter;
import com.example.bleapp.databinding.ActivityMainBinding;
import com.example.bleapp.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BleItemInterface {

    private ActivityMainBinding binding;
    private Button scanButton;
    private TextView textView;
    private RecyclerView scanResultrecyclerView;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothAdapter bluetoothAdapter;
    ActivityResultLauncher<Intent> bluetoothEnablingResult;
    private ScanSettings scanSettings;

    BluetoothLeScanner bleScanner;
    private ScanCallback scanCallback;
    private boolean isScanning = false;
    private List<ScanResult> scanResults = new ArrayList<>();
    private ScanResultAdapter scanResultAdapter;
    public CustomDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        scanButton = findViewById(R.id.button);
        setContentView(binding.getRoot());
        initialization(getApplicationContext());

    }

    private void initialization(Context applicationContext) {
        scanButton = findViewById(R.id.button);
        textView = findViewById(R.id.text);
        scanResultrecyclerView = findViewById(R.id.scanned_recycler_view);
        scanResultrecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        scanResultAdapter = new ScanResultAdapter(MainActivity.this, scanResults, MainActivity.this);
        scanResultrecyclerView.setAdapter(scanResultAdapter);
        loadingDialog = new CustomDialog(MainActivity.this);


        scanButton.setOnClickListener(buttonClickLister());
        initializeBluetoothAdapter();
        scanCallback = getScanCallback();
        getBleScanner();
        bluetoothEnablingResult = getBluetoothEnablingResult();
        scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

    }

    private ActivityResultLauncher<Intent> getBluetoothEnablingResult() {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth is enabled, good to go
                    } else {
                        // User dismissed or denied Bluetooth prompt
                        promptEnableBluetooth();
                    }
                }
        );
    }


    private View.OnClickListener buttonClickLister() {
        return new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                if (isScanning()) {
                    textView.setText(R.string.stop_scan);
                    stopBleScan();
                } else {
                    textView.setText(R.string.start_scan);
                    startBleScan();

                }

            }
        };
    }

    @SuppressLint({"MissingPermission", "NotifyDataSetChanged"})
    private void startBleScan() {
        if (!hasRequiredBluetoothPermissions(getApplicationContext())) {
            requestRelevantRuntimePermissions();
        } else {
            // TODO: Actually perform scan
            if (bluetoothAdapter.isEnabled()) {
                scanResults.clear();
                scanResultAdapter.notifyDataSetChanged();
                bleScanner.startScan(null, scanSettings, scanCallback);
                setIsScanning(true);
            }

        }
    }

    @SuppressLint("MissingPermission")
    private void stopBleScan() {
        bleScanner.stopScan(scanCallback);
        setIsScanning(false);
    }

    private void requestRelevantRuntimePermissions() {
        if (hasRequiredBluetoothPermissions(getApplicationContext())) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestLocationPermission();
        } else {
            requestBluetoothPermissions();
        }
    }

    private void requestLocationPermission() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Location permission required")
                    .setMessage("location access in order to scan for BLE devices.")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, PERMISSION_REQUEST_CODE);
                    })
                    .show();
        });
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private void requestBluetoothPermissions() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth permission required")
                    .setMessage(
                            "Starting from Android 12, the system requires apps to be granted " +
                                    "Bluetooth access in order to scan for and connect to BLE devices."
                    )
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{
                                        Manifest.permission.BLUETOOTH_SCAN,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                },
                                PERMISSION_REQUEST_CODE
                        );
                    })
                    .show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_REQUEST_CODE) return;

        boolean containsPermanentDenial = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                containsPermanentDenial = true;
                break;
            }
        }

        boolean containsDenial = false;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                containsDenial = true;
                break;
            }
        }

        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (containsPermanentDenial) {
            // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
            // Note: The user will need to navigate to App Settings and manually grant
            // permissions that were permanently denied
        } else if (containsDenial) {
            requestRelevantRuntimePermissions();
        } else if (allGranted && hasRequiredBluetoothPermissions(getApplicationContext())) {
            startBleScan();
        } else {
            // Unexpected scenario encountered when handling permissions
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            promptEnableBluetooth();
        }
    }

    void initializeBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void promptEnableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !hasPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT)) {
            // Insufficient permission to prompt for Bluetooth enabling
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothEnablingResult.launch(intent);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {

        if (bluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        return bluetoothAdapter;
    }

    private BluetoothLeScanner getBleScanner() {
        if (bleScanner == null) {
            bleScanner = getBluetoothAdapter().getBluetoothLeScanner();
        }
        return bleScanner;
    }

    private ScanCallback getScanCallback() {
        return new ScanCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                int index = -1;
                for (int i = 0; i < scanResults.size(); i++) {
                    if (scanResults.get(i).getDevice().getAddress().equals(result.getDevice().getAddress())) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    BluetoothDevice device = result.getDevice();
                    String deviceName = (device.getName() != null) ? device.getName() : "UnKnown";
                    String deviceAddress = device.getAddress();
                    System.out.println("scanCall" + deviceName);
                    Log.i("ScanCallback", "Found BLE device! Name: " + deviceName + ", address: " + deviceAddress);
                    scanResults.add(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scanResultAdapter.notifyItemInserted(scanResults.size() - 1);
                            scanResultAdapter = new ScanResultAdapter(MainActivity.this, scanResults, MainActivity.this);
                            scanResultrecyclerView.setAdapter(scanResultAdapter);
                        }
                    });

                }

            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e("ScanCallback", "onScanFailed: code " + errorCode);
                Toast.makeText(MainActivity.this, "Scan failed, Please try again later.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    // Custom setter for isScanning
    private void setIsScanning(boolean value) {
        isScanning = value;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scanButton.setText(isScanning ? "Stop Scan" : "Start Scan");
            }
        });
    }

    // Getter for isScanning (if needed)
    public boolean isScanning() {
        return isScanning;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onItemClickListener(ScanResult scanResult) {
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
                Intent intent = new Intent(MainActivity.this, InfoDetailsActivity.class);
                InfoDetailsActivity.scanResult = scanResult;
                startActivity(intent);

            }
        }, 4000);

    }


}