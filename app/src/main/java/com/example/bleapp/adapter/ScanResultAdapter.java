package com.example.bleapp.adapter;

import static com.example.bleapp.activity.InfoDetailsActivity.scannedBleAddress;
import static com.example.bleapp.activity.InfoDetailsActivity.scannedBleName;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bleapp.R;

import java.util.ArrayList;
import java.util.List;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private BleItemInterface bleItemInterface;
    List<ScanResult> scanResultList = new ArrayList<>();

    // data is passed into the constructor
    public ScanResultAdapter(Context context, List<ScanResult> scanResultList, BleItemInterface bleItemInterface) {
        this.mInflater = LayoutInflater.from(context);
        this.scanResultList = scanResultList;
        this.bleItemInterface = bleItemInterface;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.devices_info_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        @SuppressLint("MissingPermission")
        String name = scanResultList.get(position).getDevice().getName();
        @SuppressLint("MissingPermission")
        String address = scanResultList.get(position).getDevice().getAddress();
        if (name == null) {
            holder.name.setText("Unknown");
            scannedBleName = "Unknown";
        } else {
            holder.name.setText(name);
            scannedBleName = name;
        }

        holder.address.setText(address);
        scannedBleAddress = address;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return scanResultList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView address;
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_device_name);
            address = itemView.findViewById(R.id.tv_device_address);
            button = itemView.findViewById(R.id.connect_button);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + name.getText());
            bleItemInterface.onItemClickListener(scanResultList.get(getLayoutPosition()));
        }
    }

}
