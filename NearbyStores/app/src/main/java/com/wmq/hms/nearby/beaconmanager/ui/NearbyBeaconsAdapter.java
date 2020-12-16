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

package com.wmq.hms.nearby.beaconmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmq.hms.nearby.beaconmanager.R;
import com.wmq.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.wmq.hms.nearby.beaconmanager.beaconbase.blebeacon.Beacon;
import com.wmq.hms.nearby.beaconmanager.beaconbase.blebeacon.EddystoneUid;
import com.wmq.hms.nearby.beaconmanager.beaconbase.blebeacon.IBeacon;

import java.util.ArrayList;
import java.util.Locale;

/**
 * NearbyBeaconsAdapter
 *
 * @since 2019-11-14
 */
public class NearbyBeaconsAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<Beacon> mDatas;

    private boolean isRegister;

    public NearbyBeaconsAdapter(Context mContext, boolean isRegister) {
        this.mContext = mContext;
        this.isRegister = isRegister;
        mDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Beacon item = mDatas.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.nearybeacon_list_item, parent, false);
            holder = new ViewHolder();
            holder.beaconTypeTv = convertView.findViewById(R.id.tv_beaconType);
            holder.beaconId1Tv = convertView.findViewById(R.id.tv_beaconIdPart1);
            holder.beaconId2Tv = convertView.findViewById(R.id.tv_beaconIdPart2);
            holder.beaconId3Tv = convertView.findViewById(R.id.tv_beaconIdPart3);
            holder.beaconId4Tv = convertView.findViewById(R.id.tv_beaconIdPart4);
            holder.beaconId5Tv = convertView.findViewById(R.id.tv_beaconIdPart5);
            holder.beaconTxPowerTv = convertView.findViewById(R.id.tv_beaconTxPower);
            holder.beaconRssi = convertView.findViewById(R.id.tv_beaconRssi);
            holder.ibeacon = convertView.findViewById(R.id.ll_soft_beacon);
            holder.eddyUID = convertView.findViewById(R.id.ll_beacon);
            convertView.setTag(holder);
        } else {
            Object tag = convertView.getTag();
            if (!(tag instanceof ViewHolder)) {
                return convertView;
            }
            holder = (ViewHolder) tag;
        }

        holder.beaconTypeTv.setText(BeaconUtil.beaconType2String(item.getBeaconType()));
        String hexId = item.getHexId();
        if (item.getBeaconType() == Beacon.BEACON_EDDYSTONE_UID) {
            holder.beaconId1Tv.setText(hexId.substring(0, EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconId4Tv.setText(hexId.substring(EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconId5Tv.setText("");
            holder.ibeacon.setVisibility(View.GONE);
            holder.eddyUID.setVisibility(View.VISIBLE);
        } else if (item.getBeaconType() == Beacon.BEACON_IBEACON) {
            holder.beaconId1Tv.setText(hexId.substring(0, IBeacon.UUID_LEN * 2));
            holder.beaconId2Tv
                .setText(hexId.substring(IBeacon.UUID_LEN * 2, IBeacon.UUID_LEN * 2 + IBeacon.MAJOR_LEN * 2));
            holder.beaconId3Tv.setText(hexId.substring((IBeacon.UUID_LEN + IBeacon.MAJOR_LEN) * 2));
            holder.ibeacon.setVisibility(View.VISIBLE);
            holder.eddyUID.setVisibility(View.GONE);
        } else {
            holder.beaconId1Tv.setText("");
            holder.beaconId2Tv.setText("");
            holder.beaconId3Tv.setText("");
            holder.beaconId4Tv.setText("");
            holder.beaconId5Tv.setText("");
        }
        holder.beaconTxPowerTv.setText(String.format(Locale.ENGLISH, "Tx: %d dBm", item.getTxPower()));
        holder.beaconRssi.setText(String.format(Locale.ENGLISH, "Rssi: %d dBm", item.getRssi()));
        return convertView;
    }

    static class ViewHolder {
        TextView beaconTypeTv;

        TextView beaconId1Tv;

        TextView beaconId2Tv;

        TextView beaconId3Tv;

        TextView beaconId4Tv;

        TextView beaconId5Tv;

        TextView beaconTxPowerTv;

        TextView beaconRssi;

        LinearLayout ibeacon;

        LinearLayout eddyUID;

    }

    /**
     * reset data to show
     *
     * @param datas data list
     */
    public void refreshData(ArrayList<Beacon> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
}
