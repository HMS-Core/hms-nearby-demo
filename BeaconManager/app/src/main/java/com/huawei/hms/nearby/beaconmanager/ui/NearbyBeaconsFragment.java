/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hms.nearby.beaconmanager.ui;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconDiscover;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.Beacon;
import com.huawei.hms.nearby.beaconmanager.beaconbase.callback.BeaconDiscoverCallback;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconStatus;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.util.ArrayList;
import java.util.List;

/**
 * NearbyBeaconsFragment
 *
 * @since 2019-11-14
 */
public class NearbyBeaconsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private SwipeRefreshLayout refreshLayout = null;

    private TextView beaconRemindInfoTv = null;

    private ListView nearbyBeaconListView;

    private boolean isRegistered;

    private NearbyBeaconsAdapter mAdapter = null;

    private Context mContext = null;

    private boolean isFreshing;

    public NearbyBeaconsFragment() {
        isFreshing = false;
    }

    /**
     * get NearbyBeaconsFragment instance
     *
     * @param isRegistered isRegistered
     * @return NearbyBeaconsFragment instance
     */
    public static NearbyBeaconsFragment newInstance(boolean isRegistered) {
        NearbyBeaconsFragment fragment = new NearbyBeaconsFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constant.BEACONS_EDIT_STATUS, isRegistered);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_nearby_beacons, container, false);

        beaconRemindInfoTv = view.findViewById(R.id.tv_nearbyBeaconRemindInfo);
        nearbyBeaconListView = view.findViewById(R.id.lv_nearby_beacons);
        nearbyBeaconListView.setOnItemClickListener(this);
        Bundle args = getArguments();
        if (args != null) {
            isRegistered = args.getBoolean(Constant.BEACONS_EDIT_STATUS);
        }
        mAdapter = new NearbyBeaconsAdapter(mContext);
        nearbyBeaconListView.setAdapter(mAdapter);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isFreshing) {
                    return;
                }

                if (!checkPermission()) {
                    return;
                }

                isFreshing = true;
                BeaconDiscover.getInstance().startScanNearbyBeacon(5, new BeaconDiscoverCallbackWappar());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBeaconsAndView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS: {
                if ((grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    || (grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getContext(), "Need BLUETOOTH permission.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    return;
                }

                if ((grantResults[2] != PackageManager.PERMISSION_GRANTED)
                    || (grantResults[3] != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getContext(), "Need GPS permission.", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    return;
                }

                isFreshing = true;
                BeaconDiscover.getInstance().startScanNearbyBeacon(5, new BeaconDiscoverCallbackWappar());
                break;
            }
            default: {
                break;
            }
        }
    }

    private boolean checkPermission() {
        int coarsePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        int blePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH);
        int bleAdminPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN);
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
        ActivityCompat.requestPermissions(getActivity(), permission,
            Constant.AC_OP_REQUEST_BLUETOOTH_AND_LOCATION_PERMISSIONS);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            getBeaconsAndView();
        }
    }

    private void getBeaconsAndView() {
        ArrayList<Beacon> beacons;
        if (isRegistered) {
            beacons = BeaconDiscover.getInstance().getBeaconRegisteredList();
        } else {
            beacons = BeaconDiscover.getInstance().getBeaconCanbeRegisterList();
        }

        if (beacons.size() != 0) {
            beaconRemindInfoTv.setVisibility(View.GONE);
        } else {
            beaconRemindInfoTv.setText("No beacon nearby");
            beaconRemindInfoTv.setVisibility(View.VISIBLE);
        }
        mAdapter.refreshData(beacons);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!BeaconRestfulClient.getInstance().isAccessTokenSet()) {
            MainActivity.showAlertDialog(getActivity(), "Warning", "Please Login Service Account First!");
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

        BeaconInfo beaconInfo;
        Intent intent;
        if (!isRegistered) {
            beaconInfo = new BeaconInfo();
            beaconInfo.setBeaconId(beaconId);
            beaconInfo.setBeaconType(item.getBeaconType());
            beaconInfo.setStability("Stable");
            beaconInfo.setStatus(BeaconStatus.ACTIVE);
            intent = new Intent(mContext, EditBeaconActivity.class);
            intent.putExtra(Constant.BEACONS_EDIT_STATUS, false);
            ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
            beaconInfos.add(beaconInfo);
            intent.putExtra(Constant.BEACONS_LIST_POSTION, position);
            intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
            startActivityForResult(intent, Constant.AC_OP_EDIT_BEACON);
        } else {
            beaconInfo = BeaconRestfulClient.getInstance().queryBeaconInfo(beaconId);
            if (beaconInfo == null) {
                Toast.makeText(mContext, "Failed to query the beacon information.", Toast.LENGTH_SHORT).show();
                return;
            }
            intent = new Intent(mContext, ViewBeaconActivity.class);
            ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
            beaconInfos.add(beaconInfo);
            intent.putExtra(Constant.BEACONS_LIST_POSTION, position);
            intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
            startActivityForResult(intent, Constant.AC_OP_VIEW_BEACON);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                        List<Beacon> unregisteredList = BeaconDiscover.getInstance().getBeaconCanbeRegisterList();
                        List<Beacon> registeredList = BeaconDiscover.getInstance().getBeaconRegisteredList();
                        Beacon curBeacon = registeredList.remove(pos);
                        unregisteredList.add(curBeacon);
                        getBeaconsAndView();
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
            getBeaconsAndView();
        }
        ArrayList<BeaconInfo> beaconInfos = data.getParcelableArrayListExtra(Constant.BEACONS_INFO);
        Intent intent = new Intent(mContext, ViewBeaconActivity.class);
        intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
        mContext.startActivity(intent);
    }

    private class BeaconDiscoverCallbackWappar extends BeaconDiscoverCallback {
        @Override
        public void onCompelte() {
            refreshLayout.setRefreshing(false);
            isFreshing = false;
            getBeaconsAndView();
        }
    }
}
