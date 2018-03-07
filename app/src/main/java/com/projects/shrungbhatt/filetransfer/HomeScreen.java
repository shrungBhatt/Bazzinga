package com.projects.shrungbhatt.filetransfer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.projects.shrungbhatt.filetransfer.db.DBAdapter;
import com.projects.shrungbhatt.filetransfer.notification.NotificationToast;
import com.projects.shrungbhatt.filetransfer.utils.Utility;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeScreen extends AppCompatActivity {

    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int WRITE_PERM_REQ_CODE = 19;
    @BindView(R.id.btn_home_wifi_direct)
    Button mBtnHomeWifiDirect;
    @BindView(R.id.home_screen_image_view)
    ImageView mHomeScreenImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);


        checkWritePermission();
        printInterfaces();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            NotificationToast.showToast(HomeScreen.this, "This permission is needed for " +
                    "file sharing. But Whatever, if that's what you want...!!!");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBAdapter.getInstance(HomeScreen.this).clearDatabase();

    }

    private void printInterfaces() {
        try {
            Enumeration<NetworkInterface> x = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(x)) {
                Log.v("NetWorkInterfaces", "display name: " + ni.getDisplayName());
                Log.v("NetWorkInterfaces", "name: " + ni.getName());
                Log.v("NetWorkInterfaces", "is up and running ? : " + String.valueOf(ni.isUp()));
                Log.v("NetWorkInterfaces", "Loopback?: " + String.valueOf(ni.isLoopback()));
                Log.v("NetWorkInterfaces", "Supports multicast: " + String.valueOf(ni
                        .supportsMulticast()));
                Log.v("NetWorkInterfaces", "is virtual: " + String.valueOf(ni.isVirtual()));
                Log.v("NetWorkInterfaces", "Hardware address: " + Arrays.toString(ni
                        .getHardwareAddress()));
                Log.v("NetWorkInterfaces", "Sub interfaces.....");
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                for (InetAddress singleNI : Collections.list(inetAddresses)) {
                    Log.v("NetWorkInterfaces", "sub ni inetaddress: " + singleNI.getHostAddress());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    private void checkWritePermission() {
        boolean isGranted = Utility.checkPermission(WRITE_PERMISSION, this);
        if (!isGranted) {
            Utility.requestPermission(WRITE_PERMISSION, WRITE_PERM_REQ_CODE, this);
        }
    }


    public void startWiFiDirect() {
        if (Utility.isWiFiEnabled(HomeScreen.this)) {
            Intent wifiDirectIntent = new Intent(HomeScreen.this, Activity_WifiDirect.class);
            startActivity(wifiDirectIntent);
            finish();
        } else {
            NotificationToast.showToast(HomeScreen.this, getString(R.string
                    .wifi_not_enabled_error));
        }
    }


    @OnClick(R.id.btn_home_wifi_direct)
    public void onViewClicked() {
        startWiFiDirect();
    }
}
