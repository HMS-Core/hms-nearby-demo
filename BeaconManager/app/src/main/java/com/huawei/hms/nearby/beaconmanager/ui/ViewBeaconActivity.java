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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Attachment;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconStatus;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * ViewBeaconActivity
 *
 * @since 2019-11-14
 */
public class ViewBeaconActivity extends BaseActivity {
    private static final String TAG = ViewBeaconActivity.class.getSimpleName();

    private TextView beaconTypeTv;

    private TextView beaconIdTv;

    private TextView beaconDescTv;

    private TextView baconStatusTv;

    private TextView beaconMobilityTv;

    private BeaconInfo beaconInfo;

    private LinearLayout beaconPlaceGroup;

    private TextView beaconPlaceIdTv;

    private TextView beaconIndoorLevelTv;

    private LinearLayout beaconLongitudeGroup;

    private TextView beaconLongitudeTv;

    private TextView beaconLatitudeTv;

    private ListView propertyListView;

    private BeaconPropertyAdapter propertyAdapter;

    private ImageView ivShowOrAddAttachments;

    private ListView attachmentListView;

    private BeaconAttachmentAdapter attachmentAdapter;

    private ArrayList<Attachment> attachments;

    private TextView noAttachemtsTv;

    private boolean isAddAttachments = false;

    private int beaconListPos;

    private Set<String> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_beacon);
        setTitle("Beacon Information");
        typeList = new HashSet<>();
        typeList.add("");
        beaconListPos = getIntent().getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
        ArrayList<BeaconInfo> beaconInfos = getIntent().getParcelableArrayListExtra(Constant.BEACONS_INFO);
        beaconInfo = beaconInfos.get(0);
        initBeaconDetailInfoView();
        initPropertyAdapterView();
        initAttachmentAdapterView();
        showBeaconData();
    }

    private void initBeaconDetailInfoView() {
        beaconTypeTv = findViewById(R.id.tv_beacon_type);
        beaconIdTv = findViewById(R.id.tv_beacon_id);
        baconStatusTv = findViewById(R.id.tv_beacon_status);
        beaconDescTv = findViewById(R.id.tv_beacon_desc);

        beaconPlaceGroup = findViewById(R.id.ll_beacon_place_group);
        beaconPlaceIdTv = findViewById(R.id.tv_beacon_place);
        beaconIndoorLevelTv = findViewById(R.id.tv_beacon_indoor_level);

        beaconLongitudeGroup = findViewById(R.id.ll_beacon_longitude_group);
        beaconLongitudeTv = findViewById(R.id.tv_beacon_longitude);
        beaconLatitudeTv = findViewById(R.id.tv_beacon_latitude);

        beaconMobilityTv = findViewById(R.id.tv_beacon_mobility);
    }

    private void initPropertyAdapterView() {
        propertyListView = findViewById(R.id.lv_show_beacon_properties);
        propertyAdapter = new BeaconPropertyAdapter(this, false);
        propertyListView.setAdapter(propertyAdapter);
    }

    private void initAttachmentAdapterView() {
        ivShowOrAddAttachments = findViewById(R.id.iv_show_beacon_attachments);
        ivShowOrAddAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddAttachments) {
                    activeAddBeaconAttachment();
                } else {
                    queryAddShowAttachments();
                }
            }
        });

        attachmentListView = findViewById(R.id.lv_beacon_attachments);
        attachmentAdapter = new BeaconAttachmentAdapter(this);
        attachmentListView.setAdapter(attachmentAdapter);
        noAttachemtsTv = findViewById(R.id.beacon_show_no_attachment);
    }

    private void queryAddShowAttachments() {
        attachments = BeaconRestfulClient.getInstance().queryBeaconAttachment(beaconInfo.getBeaconId());
        if (attachments == null) {
            return;
        }
        isAddAttachments = true;
        ivShowOrAddAttachments.setImageResource(R.mipmap.ic_add);
        showBeaconAttachments();
    }

    private void showBeaconAttachments() {
        if (attachments.size() == 0) {
            noAttachemtsTv.setVisibility(View.VISIBLE);
        } else {
            noAttachemtsTv.setVisibility(View.GONE);
        }
        attachmentAdapter.refreshData(attachments);
        for (Attachment attachment : attachments) {
            typeList.add(attachment.getType());
        }
    }

    private void showBeaconProperties() {
        if (beaconInfo.getProperties() != null) {
            propertyAdapter.refreshData(beaconInfo.getProperties());
        } else {
            propertyAdapter.refreshData(new HashMap<String, String>());
        }
    }

    private void showBeaconData() {
        if (beaconInfo.getStatus() != BeaconStatus.UNKNOWN) {
            baconStatusTv.setText(BeaconUtil.beaconStatus2String(beaconInfo.getStatus()));
            baconStatusTv.setVisibility(View.VISIBLE);
        }
        beaconTypeTv.setText(BeaconUtil.beaconType2String(beaconInfo.getBeaconType()));
        beaconIdTv.setText(beaconInfo.getBeaconId());
        if (beaconInfo.getBeaconDesc() != null) {
            beaconDescTv.setText(beaconInfo.getBeaconDesc());
        }
        if (beaconInfo.getPlaceId() != null) {
            beaconPlaceIdTv.setText(beaconInfo.getPlaceId());
        }
        if (beaconInfo.getIndoorLevel() != null) {
            beaconIndoorLevelTv.setText(beaconInfo.getIndoorLevel());
        }

        beaconLongitudeTv.setText(String.valueOf(beaconInfo.getLongitude()));
        beaconLatitudeTv.setText(String.valueOf(beaconInfo.getLatitude()));

        if (beaconInfo.getStability() != null) {
            beaconMobilityTv.setText(beaconInfo.getStability());
            if (beaconInfo.getStability().equalsIgnoreCase("Stable")) {
                beaconPlaceGroup.setVisibility(View.VISIBLE);
                beaconLongitudeGroup.setVisibility(View.VISIBLE);
            } else {
                beaconPlaceGroup.setVisibility(View.GONE);
                beaconLongitudeGroup.setVisibility(View.GONE);
            }
        }

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

    private void updateBeacon() {
        Intent intent = new Intent(this, EditBeaconActivity.class);
        intent.putExtra(Constant.BEACONS_EDIT_STATUS, true);
        ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
        beaconInfos.add(beaconInfo);
        intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
        startActivityForResult(intent, Constant.AC_OP_EDIT_BEACON);
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
        switch (requestCode) {
            case Constant.AC_OP_EDIT_BEACON: {
                if (data != null) {
                    handleEditBeacon(resultCode, data);
                }
                break;
            }
            case Constant.AC_OP_EDIT_BEACON_SPULEMENT: {
                if (data != null) {
                    handleEditBeaconAttachment(data);
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
        long ret =
            BeaconRestfulClient.getInstance().deleteAttachment(beaconInfo.getBeaconId(), attachment.getAttachmentId());
        if (ret != 0) {
            Toast.makeText(this, "Delete attachment Failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Delete attachment Success!", Toast.LENGTH_SHORT).show();
        attachments.remove(attachment);
        showBeaconAttachments();
    }

    /**
     * start activity to add beacon attachment
     */
    public void activeAddBeaconAttachment() {
        if (beaconInfo.getStatus() != BeaconStatus.ACTIVE) {
            Toast.makeText(this, "The beacon is not active!\n You should active this beacon first.", Toast.LENGTH_SHORT)
                .show();
            return;
        }

        if (attachments.size() >= 9) {
            Toast
                .makeText(getApplicationContext(), "Beacon's attachment count shouldn't more than 9.",
                    Toast.LENGTH_SHORT)
                .show();
            return;
        }
        Intent intent = new Intent(this, EditBeaconAttachmentActivity.class);
        ArrayList<Attachment> attachmentList = new ArrayList<>();
        intent.putExtra(Constant.BEACONS_ID, beaconInfo.getBeaconId());
        intent.putStringArrayListExtra(Constant.BEACONS_ATTACHMENT_TYPE, new ArrayList<>(typeList));
        intent.putParcelableArrayListExtra(Constant.BEACONS_ATTACHMENT, attachmentList);
        startActivityForResult(intent, Constant.AC_OP_EDIT_BEACON_SPULEMENT);
    }

    private void handleEditBeaconAttachment(Intent data) {
        ArrayList<Attachment> attachmentsRet = data.getParcelableArrayListExtra(Constant.BEACONS_ATTACHMENT);
        attachments.add(attachmentsRet.get(0));
        showBeaconAttachments();
    }
}
