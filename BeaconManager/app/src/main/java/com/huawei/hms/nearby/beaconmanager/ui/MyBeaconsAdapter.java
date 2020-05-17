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

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.Beacon;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.EddystoneUid;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.IBeacon;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconBriefInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconInfo;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconStatus;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.util.ArrayList;

/**
 * MyBeaconsAdapter
 *
 * @since 2019-11-14
 */
public class MyBeaconsAdapter extends RecyclerView.Adapter<MyBeaconsAdapter.ViewHolder> {
    private Activity mContext;

    private ArrayList<BeaconBriefInfo> mDatas;

    public MyBeaconsAdapter(Activity mContext) {
        this.mContext = mContext;
        mDatas = new ArrayList<>();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        TextView beaconSeqTv;

        TextView beaconDescTv;

        TextView beaconTypeTv;

        TextView beaconIdPart1Tv;

        TextView beaconIdPart2Tv;

        TextView beaconIdPart3Tv;

        TextView beaconStatusTv;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            beaconSeqTv = view.findViewById(R.id.tv_mybeacon_seq);
            beaconDescTv = view.findViewById(R.id.tv_all_beacons_desc);
            beaconTypeTv = view.findViewById(R.id.tv_all_beacons_type);
            beaconIdPart1Tv = view.findViewById(R.id.tv_all_beacons_id_part1);
            beaconIdPart2Tv = view.findViewById(R.id.tv_all_beacons_id_part2);
            beaconIdPart3Tv = view.findViewById(R.id.tv_all_beacons_id_part3);
            beaconStatusTv = view.findViewById(R.id.tv_all_beacons_status);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_beacon_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BeaconBriefInfo beaconBriefInfo = mDatas.get(position);
                BeaconInfo beaconInfo =
                    BeaconRestfulClient.getInstance().queryBeaconInfo(beaconBriefInfo.getBeaconId());
                if (beaconInfo == null) {
                    return;
                }
                Intent intent = new Intent(mContext, ViewBeaconActivity.class);
                ArrayList<BeaconInfo> beaconInfos = new ArrayList<>();
                beaconInfos.add(beaconInfo);
                intent.putExtra(Constant.BEACONS_LIST_POSTION, position);
                intent.putParcelableArrayListExtra(Constant.BEACONS_INFO, beaconInfos);
                mContext.startActivityForResult(intent, Constant.AC_OP_VIEW_BEACON);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BeaconBriefInfo item = mDatas.get(position);
        holder.beaconSeqTv.setText(String.valueOf(position + 1));
        holder.beaconDescTv.setText(item.getBeaconDesc());
        holder.beaconTypeTv.setText(BeaconUtil.beaconType2String(item.getBeaconType()));

        String hexId = item.getBeaconId();
        int beaconIdLen = hexId.length() / 2;
        if ((item.getBeaconType() == Beacon.BEACON_EDDYSTONE_UID) && (beaconIdLen == EddystoneUid.BEACON_ID_LEN)) {
            holder.beaconIdPart1Tv.setText(hexId.substring(0, EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconIdPart2Tv.setText(hexId.substring(EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconIdPart3Tv.setText("");
        } else if ((item.getBeaconType() == Beacon.BEACON_IBEACON) && (beaconIdLen == IBeacon.BEACON_ID_LEN)) {
            holder.beaconIdPart1Tv.setText(hexId.substring(0, IBeacon.UUID_LEN * 2));
            holder.beaconIdPart2Tv
                .setText(hexId.substring(IBeacon.UUID_LEN * 2, IBeacon.UUID_LEN * 2 + IBeacon.MAJOR_LEN * 2));
            holder.beaconIdPart3Tv.setText(hexId.substring((IBeacon.UUID_LEN + IBeacon.MAJOR_LEN) * 2));
        } else {
            holder.beaconIdPart1Tv.setText(hexId);
            holder.beaconIdPart2Tv.setText("");
            holder.beaconIdPart3Tv.setText("");
        }
        switch (item.getStatus()) {
            case BeaconStatus.ACTIVE: {
                holder.beaconStatusTv.setTextColor(0xFF008000);
                break;
            }
            case BeaconStatus.DEACTIVE: {
                holder.beaconStatusTv.setTextColor(0xFFFF0000);
                break;
            }
            case BeaconStatus.ABANDONED: {
                holder.beaconStatusTv.setTextColor(0xFFA9A9A9);
                break;
            }
            default: {
                break;
            }
        }
        holder.beaconStatusTv.setText(BeaconUtil.beaconStatus2String(item.getStatus()));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * clear data list
     */
    public void clearDatas() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * add data to list
     *
     * @param datas datas
     */
    public void addDatas(ArrayList<BeaconBriefInfo> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * remove data at position
     *
     * @param position position
     */
    public void removeData(int position) {
        if (position == -1) {
            return;
        }
        mDatas.remove(position);
        notifyDataSetChanged();
    }
}
