package com.example.bleapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

public class PermissionUtils {


    public static boolean hasPermission(Context context, String permissionType) {
        return ContextCompat.checkSelfPermission(context, permissionType) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasRequiredBluetoothPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            return hasPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}


