package com.wmq.hms.nearby.beaconmanager.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wmq.hms.nearby.beaconmanager.R;
import com.wmq.hms.nearby.beaconmanager.beaconbase.BeaconDiscover;
import com.wmq.hms.nearby.beaconmanager.beaconbase.blebeacon.Beacon;
import com.wmq.hms.nearby.beaconmanager.beaconbase.callback.BeaconDiscoverCallback;
import com.wmq.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.wmq.hms.nearby.beaconmanager.beaconbase.model.BeaconStatus;
import com.wmq.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;
import com.wmq.hms.nearby.beaconmanager.softbeacon.BroadcasterService;
import com.wmq.hms.nearby.beaconmanager.softbeacon.DeviceIdUtil;
import com.wmq.hms.nearby.beaconmanager.softbeacon.SafeSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static com.wmq.hms.nearby.beaconmanager.ui.MainActivity.deviceId;

public class MerchantsActivity extends BaseActivity {
    private static final String TAG = MerchantsActivity.class.getSimpleName();

    private static final int NOTIFICATION_ID = 2;

    private SwipeRefreshLayout mUnregisterRefreshLayout;

    private SwipeRefreshLayout mRegisterRefreshLayout;

    private TextView mUnregisterInfoTv;

    private TextView mRegisterInfoTv;

    private Switch mSwitch;

    private NearbyBeaconsAdapter mUnregisterAdapter;

    private NearbyBeaconsAdapter mRegisterAdapter;

    ArrayList<Beacon> unRegisterBeacons;

    ArrayList<Beacon> registerBeacons;

    private boolean isFreshingUnregister;

    private boolean isFreshingRegister;

    private BroadcastReceiver mCloseSoftBeacon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (TextUtils.equals(action, "click close soft beacon")) {
                mSwitch.setChecked(false);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants);
        setTitle(R.string.merchants);

        initializeView();
        initNotification();
    }

    private void initData() {
        BeaconDiscover.getInstance().startScanNearbyBeacon(1, new UnregisterCallbackWappar());
    }

    private void initNotification() {
        IntentFilter filterClick = new IntentFilter();
        filterClick.addAction("click close soft beacon");
        registerReceiver(mCloseSoftBeacon, filterClick);
    }

    private int findSoftBeaconIndex() {
        ArrayList<Beacon> beaconRegisteredList = BeaconDiscover.getInstance().getBeaconRegisteredList();
        int position = -1;
        if (beaconRegisteredList != null && !beaconRegisteredList.isEmpty()) {
            for (int i = 0; i < beaconRegisteredList.size(); i++) {
                Log.d(TAG, "hexId = " + beaconRegisteredList.get(i).getHexId());
                if (TextUtils.equals(beaconRegisteredList.get(i).getHexId().toUpperCase(), deviceId)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeView() {
        Button setSoftBeacon = findViewById(R.id.btn_set_soft_beacon);

        setSoftBeacon.setOnClickListener(v -> {
            int softBeaconIndex = findSoftBeaconIndex();
            if (softBeaconIndex != -1) {
                BeaconInfo beaconInfo = BeaconRestfulClient.getInstance().queryBeaconInfo(deviceId.toLowerCase());
                if (beaconInfo == null) {
                    Toast.makeText(this, "Failed to query the beacon information.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ViewBeaconActivity.class);
                ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
                beaconInfos.add(beaconInfo);
                intent.putExtra(Constant.BEACONS_LIST_POSTION, softBeaconIndex);
                intent.putExtra(Constant.BEACONS_EDIT_STATUS, true);
                intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
                startActivityForResult(intent, Constant.AC_OP_VIEW_BEACON);
            } else {
                BeaconInfo beaconInfo = new BeaconInfo();
                beaconInfo.setBeaconId(DeviceIdUtil.getDeviceId().toLowerCase());
                beaconInfo.setBeaconType(1);
                beaconInfo.setStability("Stable");
                beaconInfo.setStatus(BeaconStatus.ACTIVE);
                Intent intent = new Intent(this, ViewBeaconActivity.class);
                intent.putExtra(Constant.BEACONS_LIST_POSTION, -1);
                intent.putExtra(Constant.BEACONS_EDIT_STATUS, false);
                ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
                beaconInfos.add(beaconInfo);
                intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
                startActivityForResult(intent, Constant.AC_OP_EDIT_SOFT_BEACON);
            }
        });

        mUnregisterInfoTv = findViewById(R.id.tv_nearbyBeacon_unregister);
        mRegisterInfoTv = findViewById(R.id.tv_nearbyBeacon_register);
        ListView unregisterListView = findViewById(R.id.lv_nearby_beacons_unregister);
        ListView registerListView = findViewById(R.id.lv_nearby_beacons_register);

        unregisterListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (!BeaconRestfulClient.getInstance().isAccessTokenSet()) {
                MainActivity.showAlertDialog(this, "Warning", "Http 401");
                return;
            }
            if (!(parent instanceof ListView)) {
                return;
            }
            ListView listView = (ListView) parent;
            Object itemObj = listView.getItemAtPosition(position);
            if (!(itemObj instanceof Beacon)) {
                return;
            }
            Beacon item = (Beacon) itemObj;
            String beaconId = item.getHexId();

            BeaconInfo beaconInfo = new BeaconInfo();
            beaconInfo.setBeaconId(beaconId);
            beaconInfo.setBeaconType(item.getBeaconType());
            beaconInfo.setStability("Stable");
            beaconInfo.setStatus(BeaconStatus.ACTIVE);
            Intent intent = new Intent(this, ViewBeaconActivity.class);
            intent.putExtra(Constant.BEACONS_EDIT_STATUS, false);
            ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
            beaconInfos.add(beaconInfo);
            intent.putExtra(Constant.BEACONS_LIST_POSTION, position);
            intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
            startActivityForResult(intent, Constant.AC_OP_EDIT_BEACON);
        });

        registerListView.setOnItemClickListener((parent, view12, position, id) -> {
            if (!BeaconRestfulClient.getInstance().isAccessTokenSet()) {
                MainActivity.showAlertDialog(this, "Warning", "Http 401");
                return;
            }
            if (!(parent instanceof ListView)) {
                return;
            }
            ListView listView = (ListView) parent;
            Object itemObj = listView.getItemAtPosition(position);
            if (!(itemObj instanceof Beacon)) {
                return;
            }
            Beacon item = (Beacon) itemObj;
            String beaconId = item.getHexId();

            BeaconInfo beaconInfo = BeaconRestfulClient.getInstance().queryBeaconInfo(beaconId);
            if (beaconInfo == null) {
                Toast.makeText(this, "Failed to query the beacon information.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ViewBeaconActivity.class);
            ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
            beaconInfos.add(beaconInfo);
            intent.putExtra(Constant.BEACONS_LIST_POSTION, position);
            intent.putExtra(Constant.BEACONS_EDIT_STATUS, true);
            intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
            startActivityForResult(intent, Constant.AC_OP_VIEW_BEACON);
        });

        mUnregisterAdapter = new NearbyBeaconsAdapter(this, false);
        mRegisterAdapter = new NearbyBeaconsAdapter(this, true);
        unregisterListView.setAdapter(mUnregisterAdapter);
        registerListView.setAdapter(mRegisterAdapter);

        mUnregisterRefreshLayout = findViewById(R.id.refreshLayout_unregister);
        mRegisterRefreshLayout = findViewById(R.id.refreshLayout_register);

        mSwitch = findViewById(R.id.beacon_switch);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                        .putBoolean(Constant.SWITCH_STATE_KEY, true);
                startForegroundService(new Intent(this, BroadcasterService.class));
                if (findSoftBeaconIndex() == -1) {
                    Toast.makeText(this, "Soft beacon is on, but not register!", Toast.LENGTH_SHORT).show();
                }
            } else {
                SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                        .putBoolean(Constant.SWITCH_STATE_KEY, false);
                stopService(new Intent(this, BroadcasterService.class));
            }
        });

        mUnregisterRefreshLayout.setOnRefreshListener(() -> {
            if (isFreshingUnregister) {
                return;
            }

            if (!checkScanPermission()) {
                return;
            }

            isFreshingUnregister = true;
            BeaconDiscover.getInstance().startScanNearbyBeacon(5, new UnregisterCallbackWappar());
        });

        mRegisterRefreshLayout.setOnRefreshListener(() -> {
            if (isFreshingRegister) {
                return;
            }

            if (!checkScanPermission()) {
                return;
            }

            isFreshingRegister = true;
            BeaconDiscover.getInstance().startScanNearbyBeacon(5, new RegisterCallbackWappar());
        });
    }

    private boolean checkScanPermission() {
        int coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int blePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bleAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);

        if ((coarsePermission == PackageManager.PERMISSION_GRANTED)
                && (finePermission == PackageManager.PERMISSION_GRANTED)
                && (blePermission == PackageManager.PERMISSION_GRANTED)
                && (bleAdminPermission == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        requestPermission();
        return false;
    }

    private void requestPermission() {
        String[] permission = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permission,
                Constant.AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS: {
                if ((grantResults[0] != PackageManager.PERMISSION_GRANTED)
                        || (grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Need BLUETOOTH permission.", Toast.LENGTH_SHORT).show();
                    mUnregisterRefreshLayout.setRefreshing(false);
                    return;
                }

                if ((grantResults[2] != PackageManager.PERMISSION_GRANTED)
                        || (grantResults[3] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Need GPS permission.", Toast.LENGTH_SHORT).show();
                    mUnregisterRefreshLayout.setRefreshing(false);
                    return;
                }

                if (mUnregisterRefreshLayout.isRefreshing()) {
                    isFreshingUnregister = true;
                    BeaconDiscover.getInstance().startScanNearbyBeacon(5, new UnregisterCallbackWappar());
                }

                if (mRegisterRefreshLayout.isRefreshing()) {
                    isFreshingRegister = true;
                    BeaconDiscover.getInstance().startScanNearbyBeacon(5, new RegisterCallbackWappar());
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if (SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .getBoolean(Constant.SWITCH_STATE_KEY, false)) {
            startForegroundService(new Intent(this, BroadcasterService.class));
        } else {
            stopService(new Intent(this, BroadcasterService.class));
        }
        mSwitch.setChecked(SafeSharedPreferences.getInstance(this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .getBoolean(Constant.SWITCH_STATE_KEY, false));
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mCloseSoftBeacon);
    }

    @SuppressLint("SetTextI18n")
    private void getUnregisterBeacons() {
        unRegisterBeacons = BeaconDiscover.getInstance().getBeaconCanbeRegisterList();
        if (unRegisterBeacons.size() != 0) {
            mUnregisterInfoTv.setVisibility(View.GONE);
        } else {
            mUnregisterInfoTv.setText("No beacon unregister");
            mUnregisterInfoTv.setVisibility(View.VISIBLE);
        }
        Log.d("refreshQustion", "unRegister size : " + unRegisterBeacons.size());
        mUnregisterAdapter.refreshData(unRegisterBeacons);
    }

    @SuppressLint("SetTextI18n")
    private void getRegisterBeacons() {
        registerBeacons = BeaconDiscover.getInstance().getBeaconRegisteredList();
        if (registerBeacons.size() != 0) {
            mRegisterInfoTv.setVisibility(View.GONE);
        } else {
            mRegisterInfoTv.setText("No beacon register");
            mRegisterInfoTv.setVisibility(View.VISIBLE);
        }
        Log.d("refreshQustion", "Register size : " + registerBeacons.size());
        mRegisterAdapter.refreshData(registerBeacons);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
        switch (requestCode) {
            case Constant.AC_OP_EDIT_SOFT_BEACON: {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    handleEditSoftBeacon(resultCode, data);
                }
                break;
            }
            case Constant.AC_OP_EDIT_BEACON: {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    handleEditBeacon(resultCode, data);
                }
                break;
            }
            case Constant.AC_OP_VIEW_BEACON: {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    int pos = data.getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
                    if (pos != -1) {
                        getUnregisterBeacons();
                        getRegisterBeacons();
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleEditBeacon(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        int pos = data.getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
        if (pos != -1) {
            List<Beacon> unregisteredList = BeaconDiscover.getInstance().getBeaconCanbeRegisterList();
            List<Beacon> registeredList = BeaconDiscover.getInstance().getBeaconRegisteredList();
            Beacon curBeacon = unregisteredList.remove(pos);
            registeredList.add(curBeacon);
            getUnregisterBeacons();
            getRegisterBeacons();
        }
        ArrayList<BeaconInfo> beaconInfos = data.getParcelableArrayListExtra(Constant.BEACONS_INFO);
        Intent intent = new Intent(this, ViewBeaconActivity.class);
        intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
        startActivity(intent);
    }

    private void handleEditSoftBeacon(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        ArrayList<Parcelable> beaconInfos = data.getParcelableArrayListExtra(Constant.BEACONS_INFO);
        Intent intent = new Intent(this, ViewBeaconActivity.class);
        intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
        startActivity(intent);
    }

    private class UnregisterCallbackWappar extends BeaconDiscoverCallback {
        @Override
        public void onCompelte() {
            refreshComplete();
        }
    }

    private class RegisterCallbackWappar extends BeaconDiscoverCallback {
        @Override
        public void onCompelte() {
            refreshComplete();
        }
    }

    private void refreshComplete() {
        mUnregisterRefreshLayout.setRefreshing(false);
        mRegisterRefreshLayout.setRefreshing(false);
        isFreshingUnregister = false;
        isFreshingRegister = false;
        getUnregisterBeacons();
        getRegisterBeacons();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
