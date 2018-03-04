package com.projects.shrungbhatt.filetransfer.adpaters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.projects.shrungbhatt.filetransfer.R;

/**
 * Created by jigsaw on 4/3/18.
 */

public class Adapter_DeviceListAdapter {




    class DeviceViewHolder extends RecyclerView.ViewHolder{

        private Button mDeviceButton;

        DeviceViewHolder(LayoutInflater layoutInflater, ViewGroup parent){
            super(layoutInflater.inflate(R.layout.device_item,parent,false));

            mDeviceButton = itemView.findViewById(R.id.device_name);


        }
    }

}
