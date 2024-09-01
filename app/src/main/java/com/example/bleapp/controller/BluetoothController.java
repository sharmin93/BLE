package com.example.bleapp.controller;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import static com.example.bleapp.utils.PermissionUtils.hasPermission;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bleapp.MainActivity;

public class BluetoothController  {
}
