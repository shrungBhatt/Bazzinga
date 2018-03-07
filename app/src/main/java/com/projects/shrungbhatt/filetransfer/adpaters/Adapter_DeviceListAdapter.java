package com.projects.shrungbhatt.filetransfer.adpaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.projects.shrungbhatt.filetransfer.PeerListFragment;
import com.projects.shrungbhatt.filetransfer.R;
import com.projects.shrungbhatt.filetransfer.model.DeviceDTO;

import java.util.ArrayList;

/**
 * Created by jigsaw on 4/3/18.
 */

public class Adapter_DeviceListAdapter extends RecyclerView.Adapter<Adapter_DeviceListAdapter.DeviceViewHolder> {


    private Context mContext;
    private ArrayList<DeviceDTO> mDeviceDTOS;
    private PeerListFragment.OnListFragmentInteractionListener mListener;

    public Adapter_DeviceListAdapter(Context context, ArrayList<DeviceDTO> deviceDTOS,
                                     PeerListFragment.OnListFragmentInteractionListener listener){
        mContext = context;
        mDeviceDTOS = deviceDTOS;
        mListener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new DeviceViewHolder(inflater,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, final int position) {

        holder.mDeviceButton.setText(mDeviceDTOS.get(position).getDeviceName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListFragmentInteraction(mDeviceDTOS.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDeviceDTOS.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder{

        private Button mDeviceButton;

        DeviceViewHolder(LayoutInflater layoutInflater, ViewGroup parent){
            super(layoutInflater.inflate(R.layout.device_item,parent,false));

            mDeviceButton = itemView.findViewById(R.id.device_name);


        }
    }

}
