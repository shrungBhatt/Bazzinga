package com.projects.shrungbhatt.filetransfer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.shrungbhatt.filetransfer.adpaters.Adapter_DeviceListAdapter;
import com.projects.shrungbhatt.filetransfer.db.DBAdapter;
import com.projects.shrungbhatt.filetransfer.model.DeviceDTO;
import com.projects.shrungbhatt.filetransfer.notification.NotificationToast;
import com.projects.shrungbhatt.filetransfer.transfer.DataHandler;
import com.projects.shrungbhatt.filetransfer.transfer.DataSender;
import com.projects.shrungbhatt.filetransfer.transfer.TransferConstants;
import com.projects.shrungbhatt.filetransfer.utils.ConnectionUtils;
import com.projects.shrungbhatt.filetransfer.utils.DialogUtils;
import com.projects.shrungbhatt.filetransfer.utils.Utility;
import com.projects.shrungbhatt.filetransfer.wifidirect.WiFiDirectBroadcastReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_WifiDirect extends AppCompatActivity implements PeerListFragment.OnListFragmentInteractionListener
        , WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    public static final String FIRST_DEVICE_CONNECTED = "first_device_connected";
    public static final String KEY_FIRST_DEVICE_IP = "first_device_ip";

    private static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int WRITE_PERM_REQ_CODE = 19;


    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifip2pChannel;
    WiFiDirectBroadcastReceiver wiFiDirectBroadcastReceiver;
    @BindView(R.id.devices_list_recycler_view)
    RecyclerView mDevicesListRecyclerView;
    @BindView(R.id.no_devices_tv)
    TextView mNoDevicesTv;
    @BindView(R.id.btn_home_wifi_direct)
    Button mBtnHomeWifiDirect;
    boolean isConnectionInfoSent = false;
    private boolean isWifiP2pEnabled = false;
    private boolean isWDConnected = false;
    //    private ConnectionListener connListener;
    private AppController appController;
    private BroadcastReceiver localDashReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case FIRST_DEVICE_CONNECTED:
                    appController.restartConnectionListenerWith(ConnectionUtils.getPort(Activity_WifiDirect.this));

                    String senderIP = intent.getStringExtra(KEY_FIRST_DEVICE_IP);
                    int port = DBAdapter.getInstance(Activity_WifiDirect.this).getDevice
                            (senderIP).getPort();
                    DataSender.sendCurrentDeviceData(Activity_WifiDirect.this, senderIP, port, true);
                    isWDConnected = true;
                    break;
                case DataHandler.DEVICE_LIST_CHANGED:
                    ArrayList<DeviceDTO> devices = DBAdapter.getInstance(Activity_WifiDirect.this)
                            .getDeviceList();
                    int peerCount = (devices == null) ? 0 : devices.size();
                    if (peerCount > 0) {

                        /*deviceListFragment = new PeerListFragment();
                        Bundle args = new Bundle();
                        args.putSerializable(PeerListFragment.ARG_DEVICE_LIST, devices);
                        deviceListFragment.setArguments(args);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.deviceListHolder, deviceListFragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                        ft.commit();*/
                        populateRecyclerView(devices);
                    }
                    setToolBarTitle(peerCount);
                    break;
                default:
                    break;
            }
        }
    };
    private DeviceDTO selectedDevice;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        ButterKnife.bind(this);


        mDevicesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        initialize();
    }

    private void initialize() {


        String myIP = Utility.getWiFiIPAddress(Activity_WifiDirect.this);
        Utility.saveString(Activity_WifiDirect.this, TransferConstants.KEY_MY_IP, myIP);

//        Starting connection listener with default for now
//        connListener = new ConnectionListener(LocalDashWiFiDirect.this, TransferConstants.INITIAL_DEFAULT_PORT);
//        connListener.start();

        setToolBarTitle(0);

        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        wifip2pChannel = wifiP2pManager.initialize(this, getMainLooper(), null);

        // Starting connection listener with default port for now
        appController = (AppController) getApplicationContext();
        appController.startConnectionListener(TransferConstants.INITIAL_DEFAULT_PORT);

        checkWritePermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history) {
            startActivity(new Intent(
                    this, Activity_History.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void findPeers() {

        if (!isWDConnected) {
            Toast.makeText(this, "Finding peers", Toast.LENGTH_SHORT).show();
            wifiP2pManager.discoverPeers(wifip2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    NotificationToast.showToast(Activity_WifiDirect.this, "Peer discovery started");
                }

                @Override
                public void onFailure(int reasonCode) {
                    NotificationToast.showToast(Activity_WifiDirect.this, "Peer discovery failure: "
                            + reasonCode);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localDashReceiver);
        unregisterReceiver(wiFiDirectBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(DataHandler.DEVICE_LIST_CHANGED);
        localFilter.addAction(FIRST_DEVICE_CONNECTED);
        localFilter.addAction(DataHandler.CHAT_REQUEST_RECEIVED);
        localFilter.addAction(DataHandler.CHAT_RESPONSE_RECEIVED);
        LocalBroadcastManager.getInstance(Activity_WifiDirect.this).registerReceiver(localDashReceiver,
                localFilter);

        IntentFilter wifip2pFilter = new IntentFilter();
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wiFiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager,
                wifip2pChannel, this);
        registerReceiver(wiFiDirectBroadcastReceiver, wifip2pFilter);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DataHandler.DEVICE_LIST_CHANGED));
    }

    @Override
    protected void onDestroy() {
//        mNsdHelper.tearDown();
//        connListener.tearDown();
        appController.stopConnectionListener();
        Utility.clearPreferences(Activity_WifiDirect.this);
        Utility.deletePersistentGroups(wifiP2pManager, wifip2pChannel);
        DBAdapter.getInstance(Activity_WifiDirect.this).clearDatabase();
        wifiP2pManager.removeGroup(wifip2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SaveImage saveImage = new SaveImage();

        switch (requestCode) {
            case DialogUtils.CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    DataSender.sendFile(Activity_WifiDirect.this, selectedDevice.getIp(),
                            selectedDevice.getPort(), imageUri);
                    saveImage.execute(imageUri);
                }
                break;
            case DialogUtils.CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    Uri fileUri = data.getData();
                    DataSender.sendFile(this, selectedDevice.getIp(),
                            selectedDevice.getPort(), fileUri);
                    saveImage.execute(fileUri);
                }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }
    }

    private void checkWritePermission() {
        boolean isGranted = Utility.checkPermission(WRITE_PERMISSION, this);
        if (!isGranted) {
            Utility.requestPermission(WRITE_PERMISSION, WRITE_PERM_REQ_CODE, this);
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

        if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner && !isConnectionInfoSent) {

            isWDConnected = true;


            appController.restartConnectionListenerWith(ConnectionUtils.getPort(Activity_WifiDirect.this));

            String groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
            DataSender.sendCurrentDeviceDataWD(Activity_WifiDirect.this, groupOwnerAddress, TransferConstants
                    .INITIAL_DEFAULT_PORT, true);
            isConnectionInfoSent = true;
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        ArrayList<DeviceDTO> deviceDTOs = new ArrayList<>();

        List<WifiP2pDevice> devices = (new ArrayList<>());
        devices.addAll(peerList.getDeviceList());
        for (WifiP2pDevice device : devices) {
            DeviceDTO deviceDTO = new DeviceDTO();
            deviceDTO.setIp(device.deviceAddress);
            deviceDTO.setPlayerName(device.deviceName);
            deviceDTO.setDeviceName(new String());
            deviceDTO.setOsVersion(new String());
            deviceDTO.setPort(-1);
            deviceDTOs.add(deviceDTO);
        }


        populateRecyclerView(deviceDTOs);
        /*deviceListFragment = new PeerListFragment();
        Bundle args = new Bundle();
        args.putSerializable(PeerListFragment.ARG_DEVICE_LIST, deviceDTOs);
        deviceListFragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.deviceListHolder, deviceListFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();*/
    }

    @Override
    public void onListFragmentInteraction(DeviceDTO deviceDTO) {
        if (!isWDConnected) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceDTO.getIp();
            config.wps.setup = WpsInfo.PBC;
            config.groupOwnerIntent = 4;
            wifiP2pManager.connect(wifip2pChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // Connection request succeeded. No code needed here
                }

                @Override
                public void onFailure(int reasonCode) {
                    NotificationToast.showToast(Activity_WifiDirect.this, "Connection failed. try" +
                            " again: reason: " + reasonCode);
                }
            });
        } else {
            selectedDevice = deviceDTO;
//            showServiceSelectionDialog();
            DialogUtils.getServiceSelectionDialog(Activity_WifiDirect.this, deviceDTO).show();
        }
    }

    private void setToolBarTitle(int peerCount) {
        if (getSupportActionBar() != null) {
            String title = String.format(getString(R.string.wd_title_with_count), String
                    .valueOf(peerCount));
            getSupportActionBar().setTitle(title);

        }
    }

    @OnClick(R.id.btn_home_wifi_direct)
    public void onViewClicked() {
        findPeers();
    }

    private void populateRecyclerView(ArrayList<DeviceDTO> deviceDTOS) {
        if (deviceDTOS != null) {
            if (deviceDTOS.size() != 0) {
                mDevicesListRecyclerView.setAdapter(new Adapter_DeviceListAdapter(Activity_WifiDirect.this,
                        deviceDTOS, this));
                mDevicesListRecyclerView.setVisibility(View.VISIBLE);
                mNoDevicesTv.setVisibility(View.GONE);
            } else {
                mDevicesListRecyclerView.setVisibility(View.GONE);
                mNoDevicesTv.setVisibility(View.VISIBLE);
            }
        }
    }


    private class SaveImage extends AsyncTask<Uri,Void,Void>{


        @Override
        protected Void doInBackground(Uri... uris) {




            final File file = new File(Environment.getExternalStorageDirectory() + "/"
                    + "/localdash/" + "sent/"+System.currentTimeMillis() + ".mp4");

            File dirs = new File(file.getParent());
            if (!dirs.exists()) {
                boolean dirsSuccess = dirs.mkdirs();
            }
            try {
                boolean fileCreationSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final int chunkSize = 1024;  // We'll read in one kB at a time
            byte[] imageData = new byte[chunkSize];

            try {
                InputStream in = getContentResolver().openInputStream(uris[0]);
                OutputStream out = new FileOutputStream(file);  // I'm assuming you already have the File object for where you're writing to

                int bytesRead;
                while ((bytesRead = in.read(imageData)) > 0) {
                    out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
                }

                in.close();

                out.close();


            } catch (Exception ex) {
                Log.e("Something went wrong.", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getApplicationContext(),"Image saved",Toast.LENGTH_SHORT).show();
        }
    }
}
