package com.example.bleapp.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.bleapp.R;

public class CustomDialog {

    private Activity activity;
    private AlertDialog dialog;

    public CustomDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_loader_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
