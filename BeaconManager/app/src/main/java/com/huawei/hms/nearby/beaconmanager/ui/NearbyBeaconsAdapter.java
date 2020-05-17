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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.Beacon;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.EddystoneUid;
import com.huawei.hms.nearby.beaconmanager.beaconbase.blebeacon.IBeacon;

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

    public NearbyBeaconsAdapter(Context mContext) {
        this.mContext = mContext;
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
            holder.beaconIv = convertView.findViewById(R.id.lv_nearby_beacons);
            holder.registeredIv = convertView.findViewById(R.id.iv_nearbyRegistered);
            holder.beaconTypeTv = convertView.findViewById(R.id.tv_beaconType);
            holder.beaconId1Tv = convertView.findViewById(R.id.tv_beaconIdPart1);
            holder.beaconId2Tv = convertView.findViewById(R.id.tv_beaconIdPart2);
            holder.beaconId3Tv = convertView.findViewById(R.id.tv_beaconIdPart3);
            holder.beaconTxPowerTv = convertView.findViewById(R.id.tv_beaconTxPower);
            holder.beaconRssi = convertView.findViewById(R.id.tv_beaconRssi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.beaconTypeTv.setText(BeaconUtil.beaconType2String(item.getBeaconType()));
        String hexId = item.getHexId();
        if (item.getBeaconType() == Beacon.BEACON_EDDYSTONE_UID) {
            holder.beaconId1Tv.setText(hexId.substring(0, EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconId2Tv.setText(hexId.substring(EddystoneUid.NAME_SPACE_ID_LEN * 2));
            holder.beaconId3Tv.setText("");
        } else if (item.getBeaconType() == Beacon.BEACON_IBEACON) {
            holder.beaconId1Tv.setText(hexId.substring(0, IBeacon.UUID_LEN * 2));
            holder.beaconId2Tv
                .setText(hexId.substring(IBeacon.UUID_LEN * 2, IBeacon.UUID_LEN * 2 + IBeacon.MAJOR_LEN * 2));
            holder.beaconId3Tv.setText(hexId.substring((IBeacon.UUID_LEN + IBeacon.MAJOR_LEN) * 2));
        } else {
            holder.beaconId1Tv.setText("");
            holder.beaconId2Tv.setText("");
            holder.beaconId3Tv.setText("");
        }
        holder.beaconTxPowerTv.setText(String.format(Locale.ENGLISH, "Tx: %d dBm", item.getTxPower()));
        holder.beaconRssi.setText(String.format(Locale.ENGLISH, "Rssi: %d dBm", item.getRssi()));
        return convertView;
    }

    static class ViewHolder {
        ImageView beaconIv;

        ImageView registeredIv;

        TextView beaconTypeTv;

        TextView beaconId1Tv;

        TextView beaconId2Tv;

        TextView beaconId3Tv;

        TextView beaconTxPowerTv;

        TextView beaconRssi;
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
