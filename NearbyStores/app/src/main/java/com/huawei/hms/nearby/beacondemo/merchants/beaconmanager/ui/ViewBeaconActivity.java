/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
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

package com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.le.AdvertiseSettings;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.huawei.hms.nearby.beacondemo.R;
import com.huawei.hms.nearby.beacondemo.common.BaseActivity;
import com.huawei.hms.nearby.beacondemo.common.Constant;
import com.huawei.hms.nearby.beacondemo.consumer.model.StoreAdapterInfo;
import com.huawei.hms.nearby.beacondemo.consumer.utils.JsonUtils;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.model.Attachment;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.model.BeaconInfo;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.model.BeaconStatus;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.model.Namespace;
import com.huawei.hms.nearby.beacondemo.merchants.beaconmanager.restfulapi.BeaconRestfulClient;
import com.huawei.hms.nearby.beacondemo.merchants.softbeacon.SafeSharedPreferences;
import com.huawei.hms.nearby.beacondemo.merchants.utils.BeaconUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ViewBeaconActivity
 *
 * @since 2019-11-14
 */
public class ViewBeaconActivity extends BaseActivity implements EditPropertyDialogFragment.NoticeDialogListener {
    private static final String TAG = ViewBeaconActivity.class.getSimpleName();

    private static final String BEACONID_PREFIX = "6bff00f723fdf7471402";

    private TextView beaconDetailTv;

    private EditText beaconDescEt;

    private TextView beaconTypeTv;

    private TextView beaconIdTv;

    private TextView baconStatusTv;

    private BeaconInfo beaconInfo;

    private EditText beaconLatitudeEt;

    private EditText beaconLongitudeEt;

    private BeaconPropertyAdapter propertyAdapter;

    private ImageView ivShowOrAddAttachments;

    private RelativeLayout attachmentView;

    private Spinner beaconPowerSpinner;

    private Spinner beaconMobilitySpinner;

    private LinearLayout beaconDetails;

    private LinearLayout beaconSubmit;

    private ImageView showBeaconDetails;

    private ArrayList<Attachment> attachments;

    private ImageView addBeaconPropertiesIv;

    private EditText attachmentNotice;

    private EditText attachmentImageUrl;

    private EditText attachmentName;

    private ArrayAdapter<String> mobilityArrayAdapter;

    private ArrayAdapter<String> powerArrayAdapter;

    private EditText attachmentDesc;

    private LinearLayout beaconPlaceGroup;

    private LinearLayout beaconLongitudeGroup;

    private ListView propertyListView;

    private EditText beaconPlaceIdEt;

    private EditText beaconIndoorLevelEt;

    private List<String> mobilityList;

    private List<String> powerList;

    private Attachment attachment;

    private List<String> namespaceList;

    private EditPropertyDialogFragment editPropertyDialogFragment;

    private int beaconListPos;

    private boolean beaconSoft;

    private boolean isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_beacon);
        setTitle("Configure Beacon");
        beaconListPos = getIntent().getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
        beaconSoft = getIntent().getBooleanExtra(Constant.IBEACON_SET_SELF, false);
        isRegistered = getIntent().getBooleanExtra(Constant.BEACONS_EDIT_STATUS, false);
        ArrayList<BeaconInfo> beaconInfos = getIntent().getParcelableArrayListExtra(Constant.BEACONS_INFO);
        if (beaconInfos != null && !beaconInfos.isEmpty()) {
            beaconInfo = beaconInfos.get(0);
        } else {
            Toast.makeText(this, "The beaconInfos is null", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        attachment = new Attachment("", "", "");
        namespaceList = new ArrayList<>();
        getAllNamespace();
        initBeaconDetailInfoView();
        initPropertyAdapterView();
        initAttachmentAdapterView();
        showBeaconData();
        showAttachmentData();
        editPropertyDialogFragment = new EditPropertyDialogFragment();
    }

    private void showAttachmentData() {
        if (!isRegistered) {
            return;
        }
        queryAddShowAttachments();
    }

    private void initBeaconDetailInfoView() {
        beaconDetailTv = findViewById(R.id.tv_beacon_id);
        beaconDetailTv.setText(beaconInfo.getBeaconId());
        beaconDescEt = findViewById(R.id.et_beacon_desc);
        if (isRegistered) {
            beaconDescEt.setText(beaconInfo.getBeaconDesc());
        }
        beaconDescEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                beaconInfo.setBeaconDesc(editable.toString());
            }
        });

        initPower();
        initMobility();
        initPlace();
        beaconTypeTv = findViewById(R.id.tv_beacon_type);
        baconStatusTv = findViewById(R.id.tv_beacon_status);
        showBeaconDetails = findViewById(R.id.iv_show_beacon_details);
        beaconDetails = findViewById(R.id.lv_beacon_detail);

        showBeaconDetails.setOnClickListener(click -> {
            if (beaconDetails.getVisibility() == View.VISIBLE) {
                beaconDetails.setVisibility(View.GONE);
            } else {
                beaconDetails.setVisibility(View.VISIBLE);
            }
        });

        attachmentNotice = findViewById(R.id.et_attachment_notice);
        attachmentImageUrl = findViewById(R.id.et_attachment_storeImageUrl);
        attachmentName = findViewById(R.id.et_attachment_storeName);
        attachmentDesc = findViewById(R.id.et_attachment_storeDesc);

        initSubmit();
    }

    private void initSubmit() {
        Button submitBtn = findViewById(R.id.submit_attachment);
        submitBtn.setOnClickListener(click -> {
            String notice = attachmentNotice.getText().toString();
            String imageUrl = attachmentImageUrl.getText().toString();
            String name = attachmentName.getText().toString();
            String desc = attachmentDesc.getText().toString();

            if (beaconInfo == null) {
                Toast.makeText(this, "The beaconInfo is null", Toast.LENGTH_SHORT).show();
                return;
            }

            String beaconId = beaconInfo.getBeaconId();
            if (TextUtils.isEmpty(beaconId)) {
                Toast.makeText(this, "The beaconId is null", Toast.LENGTH_SHORT).show();
                return;
            }

            int index = beaconId.indexOf(BEACONID_PREFIX);
            if (index != 0) {
                Toast.makeText(this, "The beaconId is not legal.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (beaconInfo.getStatus() != BeaconStatus.ACTIVE) {
                Toast.makeText(this, "The beacon is not active!\n You should active this beacon first.",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isRegistered) {
                long ret = registerBeacon();
                if (ret != 0) {
                    finish();
                    return;
                }
            } else {
                updateBeacon();
            }

            if (!checkValueLength(notice) || !checkValueLength(imageUrl) || !checkValueLength(name)
                || !checkValueLength(desc)) {
                return;
            }
            sumbitAttachment(notice, imageUrl, name, desc);
        });
    }

    private void sumbitAttachment(String notice, String imageUrl, String name, String desc) {
        if (attachments != null && !attachments.isEmpty()) {
            for (int i = 0; i < attachments.size(); i++) {
                if (TextUtils.equals(attachments.get(i).getType(), Constant.TYPE_ATTACHMENT)) {
                    deleteBeaconAttachment(attachments.get(i));
                }
            }
            attachments.clear();
        }

        attachment.setType(Constant.TYPE_ATTACHMENT);
        String str = "{\"storeDesc\":\"" + desc + "\",\"storeName\":\"" + name + "\",\"storeImageUrl\":\"" + imageUrl
            + "\",\"notice\":\"" + notice + "\"}";
        attachment.setValue(BeaconUtil.bytesToBase64Str(str.getBytes(Charset.forName("UTF-8"))));
        Attachment ret = BeaconRestfulClient.getInstance().addAttachment(beaconInfo.getBeaconId(), attachment);
        if (ret == null) {
            Toast.makeText(this, "Submit Failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Submit Success!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initPlace() {
        beaconPlaceGroup = findViewById(R.id.iv_beacon_place_group);
        beaconPlaceGroup.setVisibility(View.GONE);
        beaconPlaceIdEt = findViewById(R.id.iv_beacon_place_id);
        beaconPlaceIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                beaconInfo.setPlaceId(editable.toString());
            }
        });
        beaconIndoorLevelEt = findViewById(R.id.iv_beacon_indoor_level);
        beaconIndoorLevelEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                beaconInfo.setIndoorLevel(editable.toString());
            }
        });
        initLongitudeLatitude();
    }

    private void initLongitudeLatitude() {
        /*
         * Latitude and longitude are entered manually in this sample code.
         * Developer should get the right latitude and longitude from the Map.
         */
        beaconLongitudeGroup = findViewById(R.id.iv_beacon_longitude_group);
        beaconLongitudeGroup.setVisibility(View.GONE);
        beaconLongitudeEt = findViewById(R.id.iv_beacon_longitude);
        beaconLatitudeEt = findViewById(R.id.iv_beacon_latitude);
    }

    private boolean checkValueLength(String attachmentVal) {
        if (attachmentVal == null) {
            Toast.makeText(this, "attachment value should be 1~2048B.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((attachmentVal.length() == 0) || (attachmentVal.length() > Attachment.VAL_MAX_LEN)) {
            Toast.makeText(this, "attachment value should be 1~2048B.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initPropertyAdapterView() {
        addBeaconPropertiesIv = findViewById(R.id.iv_add_beacon_properties);
        addBeaconPropertiesIv.setOnClickListener(click -> {
            HashMap<String, String> properties = beaconInfo.getProperties();
            if ((properties == null) || (properties.size() < BeaconInfo.PROPERTIES_MAX_CNT)) {
                activeUpdateBeaconProperty("", "");
            } else {
                Toast.makeText(getApplicationContext(), "Beacon's property count shouldn't more than 2.",
                    Toast.LENGTH_SHORT).show();
            }
        });

        propertyListView = findViewById(R.id.lv_beacon_properties);
        propertyAdapter = new BeaconPropertyAdapter(this, true);
        propertyListView.setAdapter(propertyAdapter);
        propertyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = propertyAdapter.getItem(position);
                if (!(item instanceof String[])) {
                    return;
                }
                String[] keyValue = (String[]) item;
                beaconInfo.getProperties().remove(keyValue[0]);
                activeUpdateBeaconProperty(keyValue[0], keyValue[1]);
            }
        });
    }

    private void initAttachmentAdapterView() {
        ivShowOrAddAttachments = findViewById(R.id.iv_show_beacon_attachments);
        beaconSubmit = findViewById(R.id.submit_list);
        ivShowOrAddAttachments.setOnClickListener(click -> {
            if (beaconSubmit.getVisibility() == View.VISIBLE) {
                beaconSubmit.setVisibility(View.GONE);
            } else {
                beaconSubmit.setVisibility(View.VISIBLE);
            }
        });
    }

    private void activeUpdateBeaconProperty(String propertyKey, String propertyVal) {
        editPropertyDialogFragment.setKey(propertyKey);
        editPropertyDialogFragment.setValue(propertyVal);
        editPropertyDialogFragment.show(getSupportFragmentManager(), "Edit Property");
    }

    private void queryAddShowAttachments() {
        attachments = BeaconRestfulClient.getInstance().queryBeaconAttachment(beaconInfo.getBeaconId());
        if (attachments == null) {
            return;
        }
        for (Attachment attachment : attachments) {
            if (TextUtils.equals(attachment.getType(), Constant.TYPE_ATTACHMENT)) {
                StoreAdapterInfo storeAdapterInfo = JsonUtils.json2Object(
                    new String(BeaconUtil.base64StrToBytes(attachment.getValue()), Charset.forName("UTF-8")),
                    StoreAdapterInfo.class);
                if (storeAdapterInfo != null) {
                    attachmentNotice.setText(storeAdapterInfo.getNotice());
                    attachmentImageUrl.setText(storeAdapterInfo.getStoreImageUrl());
                    attachmentName.setText(storeAdapterInfo.getStoreName());
                    attachmentDesc.setText(storeAdapterInfo.getStoreDesc());
                }
            }
        }
    }

    private void showBeaconProperties() {
        if (beaconInfo.getProperties() != null) {
            propertyAdapter.refreshData(beaconInfo.getProperties());
        } else {
            propertyAdapter.refreshData(new HashMap<String, String>());
        }
    }

    private void initPower() {
        beaconPowerSpinner = findViewById(R.id.sp_beacon_power_spinner);
        LinearLayout powerUi = findViewById(R.id.iv_beacon_power_group);
        if (!beaconSoft) {
            Log.d(TAG, "not IBeacon");
            powerUi.setVisibility(View.GONE);
            return;
        }
        powerUi.setVisibility(View.VISIBLE);
        powerList = new ArrayList<>();
        powerList.add("high");
        powerList.add("lowest");
        powerList.add("low");
        powerList.add("medium");
        powerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, powerList);
        beaconPowerSpinner.setAdapter(powerArrayAdapter);
        powerArrayAdapter.remove(
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .getString(beaconInfo.getBeaconId(), "high"));
        powerArrayAdapter.insert(
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .getString(beaconInfo.getBeaconId(), "high"), 0);
        beaconPowerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                    .putString(beaconInfo.getBeaconId(), powerList.get(position));
                saveAdvPower(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveAdvPower(int position) {
        if (TextUtils.equals(powerList.get(position), "high")) {
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER, AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER_RSSI, -56);
        }
        if (TextUtils.equals(powerList.get(position), "lowest")) {
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER, AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER_RSSI, -79);
        }
        if (TextUtils.equals(powerList.get(position), "low")) {
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER, AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER_RSSI, -75);
        }
        if (TextUtils.equals(powerList.get(position), "medium")) {
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER, AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
            SafeSharedPreferences.getInstance(ViewBeaconActivity.this, Constant.SP_FILE_NAME, MODE_PRIVATE)
                .putInt(Constant.IBEACON_POWER_RSSI, -66);
        }
    }

    private void initMobility() {
        beaconMobilitySpinner = findViewById(R.id.sp_beacon_mobility_spinner);
        mobilityList = new ArrayList<>();
        mobilityList.add("Stable");
        mobilityList.add("Portable");
        mobilityList.add("Mobile");
        mobilityList.add("Roving");
        mobilityArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mobilityList);
        beaconMobilitySpinner.setAdapter(mobilityArrayAdapter);
        beaconMobilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beaconInfo.setStability(mobilityList.get(i));
                if (mobilityList.get(i).equalsIgnoreCase("Stable")) {
                    stableBeacon();
                } else {
                    beaconPlaceGroup.setVisibility(View.GONE);
                    beaconLongitudeGroup.setVisibility(View.GONE);
                    beaconInfo.setPlaceId("");
                    beaconInfo.setIndoorLevel("");
                    beaconInfo.setLongitude(0.0f);
                    beaconInfo.setLatitude(0.0f);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void stableBeacon() {
        beaconPlaceGroup.setVisibility(View.VISIBLE);
        beaconLongitudeGroup.setVisibility(View.VISIBLE);
        beaconInfo.setPlaceId(beaconPlaceIdEt.getText().toString());
        beaconInfo.setIndoorLevel(beaconIndoorLevelEt.getText().toString());
        if (beaconLongitudeEt.getText().toString().length() != 0) {
            beaconInfo.setLongitude(Float.parseFloat(beaconLongitudeEt.getText().toString()));
        } else {
            beaconInfo.setLongitude(0.0f);
        }
        if (beaconLatitudeEt.getText().toString().length() != 0) {
            beaconInfo.setLatitude(Float.parseFloat(beaconLatitudeEt.getText().toString()));
        } else {
            beaconInfo.setLatitude(0.0f);
        }
    }

    private void showBeaconData() {
        String beaconDesc = beaconInfo.getBeaconDesc();
        if (beaconDesc != null) {
            beaconDescEt.setText(beaconDesc);
        }

        String mobility = beaconInfo.getStability();
        if (mobility != null) {
            beaconMobilitySpinner.setSelection(mobilityArrayAdapter.getPosition(mobility));
            if (mobility.equalsIgnoreCase("Stable")) {
                beaconPlaceGroup.setVisibility(View.VISIBLE);
                beaconLongitudeGroup.setVisibility(View.VISIBLE);
            } else {
                beaconPlaceGroup.setVisibility(View.GONE);
                beaconLongitudeGroup.setVisibility(View.GONE);
            }
        }

        if (beaconInfo.getPlaceId() != null) {
            beaconPlaceIdEt.setText(beaconInfo.getPlaceId());
        }

        if (beaconInfo.getIndoorLevel() != null) {
            beaconIndoorLevelEt.setText(beaconInfo.getIndoorLevel());
        }
        beaconLongitudeEt.setText(String.valueOf(beaconInfo.getLongitude()));
        beaconLatitudeEt.setText(String.valueOf(beaconInfo.getLatitude()));
        showBeaconProperties();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacon_operation_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_beacon: {
                updateBeacon();
                break;
            }
            case R.id.activity_beacon: {
                activeBeacon();
                break;
            }
            case R.id.deactivity_beacon: {
                deActiveBeacon();
                break;
            }
            case R.id.decommission_beacon: {
                showAlertDialog("Are you sure to abandon this beacon!", (dialog, which) -> abandonBeacon());
                break;
            }
            case R.id.delete_beacon: {
                showAlertDialog("Are you sure to delete this beacon!", (dialog, which) -> unRegisterBeacon());
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private long registerBeacon() {
        if (!validityCheck()) {
            return -1;
        }

        long ret = BeaconRestfulClient.getInstance().registerBeacon(beaconInfo);
        if (ret != 0) {
            Toast.makeText(this, "Repeated registration!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Register Beacon Success!", Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    private long updateBeacon() {
        if (!validityCheck()) {
            return -1;
        }
        long ret = BeaconRestfulClient.getInstance().updateBeacon(beaconInfo);
        if (ret != 0) {
            Toast.makeText(this, "Update Beacon Failed! Error:" + ret + ".", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update Beacon Success!", Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    private boolean validityCheck() {
        String beaconDesc = beaconInfo.getBeaconDesc();
        if ((beaconDesc != null) && (beaconDesc.length() > BeaconInfo.BEACON_DESC_MAX_LEN)) {
            Toast.makeText(this,
                "Error 'Beacon Description', length should be 0 -- " + BeaconInfo.BEACON_DESC_MAX_LEN + ".",
                Toast.LENGTH_SHORT).show();
            return false;
        }

        String placeId = beaconInfo.getPlaceId();
        if ((placeId != null) && (placeId.length() > BeaconInfo.PLACE_ID_MAX_LEN)) {
            Toast.makeText(this,
                "Error 'Beacon Place information', length should be 0 -- " + BeaconInfo.PLACE_ID_MAX_LEN + ".",
                Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            if (beaconLatitudeEt.getText().toString().length() == 0) {
                beaconInfo.setLatitude(0.0f);
            } else {
                beaconInfo.setLatitude(Float.parseFloat(beaconLatitudeEt.getText().toString()));
            }

            if (beaconLongitudeEt.getText().toString().length() == 0) {
                beaconInfo.setLongitude(0.0f);
            } else {
                beaconInfo.setLongitude(Float.parseFloat(beaconLongitudeEt.getText().toString()));
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Longitude/Latitude format error.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((beaconInfo.getLongitude() < BeaconInfo.LONGITUDE_MIN) || (beaconInfo.getLongitude()
            > BeaconInfo.LONGITUDE_MAX)) {
            Toast.makeText(this, "Error 'Longitude', value should be -180.0f -- 180.0f.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((beaconInfo.getLatitude() < BeaconInfo.LATITUDE_MIN) || (beaconInfo.getLatitude()
            > BeaconInfo.LATITUDE_MAX)) {
            Toast.makeText(this, "Error 'Latitude', value should be -90.0f -- 90.0f.", Toast.LENGTH_SHORT).show();
            return false;
        }

        String indoorLevel = beaconInfo.getIndoorLevel();
        if ((indoorLevel != null) && (indoorLevel.length() > BeaconInfo.INDOORLEVEL_MAX_LEN)) {
            Toast.makeText(this, "Error 'Indoor Level', length should be 0 -- 64.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void activeBeacon() {
        long ret = BeaconRestfulClient.getInstance().activeBeacon(beaconInfo.getBeaconId());
        if (ret == 0) {
            beaconInfo.setStatus(BeaconStatus.ACTIVE);
            baconStatusTv.setText(BeaconUtil.beaconStatus2String(BeaconStatus.ACTIVE));
            baconStatusTv.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Active Beacon Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Active Beacon Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deActiveBeacon() {
        long ret = BeaconRestfulClient.getInstance().deactiveBeacon(beaconInfo.getBeaconId());
        if (ret == 0) {
            beaconInfo.setStatus(BeaconStatus.DEACTIVE);
            baconStatusTv.setText(BeaconUtil.beaconStatus2String(BeaconStatus.DEACTIVE));
            baconStatusTv.setVisibility(View.VISIBLE);
            Toast.makeText(this, "DeActive Beacon Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "DeActive Beacon Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog(String content, DialogInterface.OnClickListener onOkClicked) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Waring!");
        alertDialog.setMessage(content);
        alertDialog.setPositiveButton("OK", onOkClicked);
        alertDialog.setNegativeButton("CANCEL", null);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void abandonBeacon() {
        long ret = BeaconRestfulClient.getInstance().abandonBeacon(beaconInfo.getBeaconId());
        if (ret == 0) {
            beaconInfo.setStatus(BeaconStatus.ABANDONED);
            baconStatusTv.setText(BeaconUtil.beaconStatus2String(BeaconStatus.ABANDONED));
            baconStatusTv.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Abandon Beacon Success!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Abandon Beacon Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void unRegisterBeacon() {
        long ret = BeaconRestfulClient.getInstance().unRegisterBeacon(beaconInfo.getBeaconId());
        if (ret == 0) {
            Intent intent = new Intent();
            intent.putExtra(Constant.BEACONS_LIST_POSTION, beaconListPos);
            setResult(RESULT_OK, intent);
            Toast.makeText(this, "Delete Beacon Success!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Delete Beacon Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.AC_OP_EDIT_BEACON) {
            if (data != null) {
                handleEditBeacon(resultCode, data);
            }
        }
    }

    private void handleEditBeacon(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        ArrayList<BeaconInfo> beaconInfos = data.getParcelableArrayListExtra(Constant.BEACONS_INFO);
        beaconInfo = beaconInfos.get(0);
        showBeaconData();
    }

    /**
     * delete beacon attachment
     *
     * @param attachment attachment
     */
    public void deleteBeaconAttachment(Attachment attachment) {
        long ret = BeaconRestfulClient.getInstance()
            .deleteAttachment(beaconInfo.getBeaconId(), attachment.getAttachmentId());
        if (ret != 0) {
            Log.e(TAG, "Delete attachment fail");
            return;
        }
        Log.e(TAG, "Delete attachment success");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String propertyKey = editPropertyDialogFragment.getKey();
        String propertyVal = editPropertyDialogFragment.getValue();
        HashMap<String, String> properties = beaconInfo.getProperties();
        if (properties == null) {
            properties = new HashMap<>();
            beaconInfo.setProperties(properties);
        }
        properties.put(propertyKey, propertyVal);
        beaconInfo.convertPropertisToStr();
        showBeaconProperties();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        String propertyKey = editPropertyDialogFragment.getKey();
        if ((propertyKey == null) || (propertyKey.length() == 0)) {
            return;
        }
        String propertyVal = editPropertyDialogFragment.getValue();
        beaconInfo.getProperties().put(propertyKey, propertyVal);
        beaconInfo.convertPropertisToStr();
        showBeaconProperties();
    }

    private void getAllNamespace() {
        ArrayList<Namespace> namespaces = BeaconRestfulClient.getInstance().queryNamespace();
        if (namespaces == null) {
            return;
        }
        namespaceList.clear();
        namespaceList.add("");
        for (Namespace namespace : namespaces) {
            namespaceList.add(namespace.getNamespace());
        }
        attachment.setNamespace(namespaceList.get(1));
    }
}
