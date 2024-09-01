package com.example.bleapp.adapter;

import android.bluetooth.le.ScanResult;

public interface BleItemInterface {
    public void onItemClickListener(ScanResult  scanResult);
}
