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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EditBeaconActivity
 *
 * @since 2019-11-14
 */
public class EditBeaconActivity extends BaseActivity implements EditPropertyDialogFragment.NoticeDialogListener {
    private static final String TAG = EditBeaconActivity.class.getSimpleName();

    private TextView beaconDetailTv;

    private EditText beaconDescEt;

    private Spinner beaconMobilitySpinner;

    private ArrayAdapter<String> mobilityArrayAdapter;

    private List<String> mobilityList;

    private boolean isRegistered;

    private ImageView addBeaconPropertiesIv;

    private BeaconInfo beaconInfo;

    private LinearLayout beaconPlaceGroup;

    private EditText beaconPlaceIdEt;

    private EditText beaconIndoorLevelEt;

    private LinearLayout beaconLongitudeGroup;

    private EditText beaconLongitudeEt;

    private EditText beaconLatitudeEt;

    private ListView propertyListView;

    private BeaconPropertyAdapter propertyAdapter;

    private EditPropertyDialogFragment editPropertyDialogFragment;

    private int beaconListPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_beacon);
        isRegistered = getIntent().getBooleanExtra(Constant.BEACONS_EDIT_STATUS, false);
        if (isRegistered) {
            setTitle(R.string.beacon_configuration_title);
        } else {
            setTitle(R.string.beacon_registration_title);
        }
        beaconListPos = getIntent().getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
        ArrayList<BeaconInfo> beaconInfos = getIntent().getParcelableArrayListExtra(Constant.BEACONS_INFO);
        beaconInfo = beaconInfos.get(0);
        initBeaconDetailInfoView();
        initPropertyAdapterView();
        showBeaconData();
        editPropertyDialogFragment = new EditPropertyDialogFragment();
    }

    private void initMobility() {
        beaconMobilitySpinner = findViewById(R.id.sp_beacon_mobility_spinner);
        mobilityList = new ArrayList<>();
        mobilityList.add("Stable");
        mobilityList.add("Portable");
        mobilityList.add("Mobile");
        mobilityList.add("Roving");
        mobilityArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mobilityList);
        beaconMobilitySpinner.setAdapter(mobilityArrayAdapter);
        beaconMobilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beaconInfo.setStability(mobilityList.get(i));
                if (mobilityList.get(i).equalsIgnoreCase("Stable")) {
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
            public void afterTextChanged(Editable s) {
                beaconInfo.setPlaceId(s.toString());
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
            public void afterTextChanged(Editable s) {
                beaconInfo.setIndoorLevel(s.toString());
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

    private void initBeaconDetailInfoView() {
        beaconDetailTv = findViewById(R.id.tv_beacon_detail);
        beaconDetailTv.setText(beaconInfo.getBeaconId());
        beaconDescEt = findViewById(R.id.et_beacon_desc);
        beaconDescEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                beaconInfo.setBeaconDesc(s.toString());
            }
        });

        initMobility();
        initPlace();
    }

    private void initPropertyAdapterView() {
        addBeaconPropertiesIv = findViewById(R.id.iv_add_beacon_properties);
        addBeaconPropertiesIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> properties = beaconInfo.getProperties();
                if ((properties == null) || (properties.size() < BeaconInfo.PROPERTIES_MAX_CNT)) {
                    activeUpdateBeaconProperty("", "");
                } else {
                    Toast
                        .makeText(getApplicationContext(), "Beacon's property count shouldn't more than 2.",
                            Toast.LENGTH_SHORT)
                        .show();
                }
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

    private void showBeaconProperties() {
        if (beaconInfo.getProperties() != null) {
            propertyAdapter.refreshData(beaconInfo.getProperties());
        } else {
            propertyAdapter.refreshData(new HashMap<String, String>());
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
        getMenuInflater().inflate(R.menu.beacon_save_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_edit: {
                long ret;
                if (isRegistered) {
                    ret = updateBeacon();
                } else {
                    ret = registerBeacon();
                }
                if (ret == 0) {
                    Intent intent = new Intent();
                    ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
                    beaconInfos.add(beaconInfo);
                    intent.putExtra(Constant.BEACONS_LIST_POSTION, beaconListPos);
                    intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validityCheck() {
        String beaconDesc = beaconInfo.getBeaconDesc();
        if ((beaconDesc != null) && (beaconDesc.length() > BeaconInfo.BEACON_DESC_MAX_LEN)) {
            Toast
                .makeText(this,
                    "Error 'Beacon Description', length should be 0 -- " + BeaconInfo.BEACON_DESC_MAX_LEN + ".",
                    Toast.LENGTH_SHORT)
                .show();
            return false;
        }

        String placeId = beaconInfo.getPlaceId();
        if ((placeId != null) && (placeId.length() > BeaconInfo.PLACE_ID_MAX_LEN)) {
            Toast
                .makeText(this,
                    "Error 'Beacon Place information', length should be 0 -- " + BeaconInfo.PLACE_ID_MAX_LEN + ".",
                    Toast.LENGTH_SHORT)
                .show();
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

        if ((beaconInfo.getLongitude() < BeaconInfo.LONGITUDE_MIN)
            || (beaconInfo.getLongitude() > BeaconInfo.LONGITUDE_MAX)) {
            Toast.makeText(this, "Error 'Longitude', value should be -180.0f -- 180.0f.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((beaconInfo.getLatitude() < BeaconInfo.LATITUDE_MIN)
            || (beaconInfo.getLatitude() > BeaconInfo.LATITUDE_MAX)) {
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

    private long registerBeacon() {
        if (!validityCheck()) {
            return -1;
        }

        long ret = BeaconRestfulClient.getInstance().registerBeacon(beaconInfo);
        if (ret != 0) {
            Toast.makeText(this, "Register Beacon Failed! Error:" + ret + ".", Toast.LENGTH_SHORT).show();
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

    /**
     * delete beacon property
     *
     * @param propertyKey propertyKey
     */
    public void delBeaconProperty(String propertyKey) {
        HashMap<String, String> properties = beaconInfo.getProperties();
        if (properties == null) {
            return;
        }
        properties.remove(propertyKey);
        if (properties.size() == 0) {
            beaconInfo.setProperties(null);
        } else {
            beaconInfo.convertPropertisToStr();
        }
        showBeaconProperties();
    }

    /**
     * start activity to update beacon property
     *
     * @param propertyKey propertyKey
     * @param propertyVal propertyVal
     */
    private void activeUpdateBeaconProperty(String propertyKey, String propertyVal) {
        editPropertyDialogFragment.setKey(propertyKey);
        editPropertyDialogFragment.setValue(propertyVal);
        editPropertyDialogFragment.show(getSupportFragmentManager(), "Edit Property");
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
}
