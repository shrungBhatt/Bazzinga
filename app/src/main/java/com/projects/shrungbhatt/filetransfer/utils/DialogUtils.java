package com.projects.shrungbhatt.filetransfer.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.projects.shrungbhatt.filetransfer.model.DeviceDTO;


public class DialogUtils {

    public static final int CODE_PICK_IMAGE = 21;

    public static AlertDialog getServiceSelectionDialog(final Activity activity, final DeviceDTO
            selectedDevice) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(selectedDevice.getDeviceName());
        String[] types = {"Share image", "Chat"};
        alertDialog.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                switch (which) {
                    case 0:
                        Intent imagePicker = new Intent(Intent.ACTION_PICK);
                        imagePicker.setType("image/*");
                        activity.startActivityForResult(imagePicker, CODE_PICK_IMAGE);
                        break;

                }
            }

        });

        return (alertDialog.create());
    }



}
