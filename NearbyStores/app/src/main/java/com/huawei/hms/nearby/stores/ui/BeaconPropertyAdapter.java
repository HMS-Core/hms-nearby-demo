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

package com.huawei.hms.nearby.stores.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.nearby.stores.R;

import java.util.HashMap;

/**
 * BeaconsPropertyAdapter
 *
 * @since 2019-11-22
 */
public class BeaconPropertyAdapter extends BaseAdapter {
    private Context mContext;

    private HashMap<String, String> properties;

    private String[] propertyKeys;

    private boolean editAble;

    /**
     * Constructor
     *
     * @param mContext activity context
     */
    public BeaconPropertyAdapter(Context mContext, boolean editAble) {
        this.mContext = mContext;
        this.editAble = editAble;
        properties = new HashMap<>();
        propertyKeys = properties.keySet().toArray(new String[0]);
    }

    @Override
    public int getCount() {
        return properties.size();
    }

    @Override
    public Object getItem(int position) {
        String[] keyVal = {propertyKeys[position], properties.get(propertyKeys[position])};
        return keyVal;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PropertyViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.beacon_properties_item, parent, false);
            viewHolder = new PropertyViewHolder();
            viewHolder.tvBeaconPropertyKey = convertView.findViewById(R.id.tv_beacon_property_key);
            viewHolder.tvBeaconPropertyVal = convertView.findViewById(R.id.tv_beacon_property_val);
            viewHolder.ivBeaconPropertyDel = convertView.findViewById(R.id.iv_beacon_property_delete);
            convertView.setTag(viewHolder);

        } else {
            Object tag = convertView.getTag();
            if (!(tag instanceof PropertyViewHolder)) {
                return convertView;
            }
            viewHolder = (PropertyViewHolder) tag;
        }

        String propertyKey = propertyKeys[position];
        String propertyVal = properties.get(propertyKey);
        viewHolder.tvBeaconPropertyKey.setText(propertyKey);
        viewHolder.tvBeaconPropertyVal.setText(propertyVal);
        if (!editAble) {
            viewHolder.ivBeaconPropertyDel.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class PropertyViewHolder {
        ImageView ivBeaconPropertyDel;

        TextView tvBeaconPropertyKey;

        TextView tvBeaconPropertyVal;
    }

    /**
     * refresh properties list view data
     *
     * @param propertiesIn beacon properties
     */
    public void refreshData(HashMap<String, String> propertiesIn) {
        properties.clear();
        properties.putAll(propertiesIn);
        propertyKeys = properties.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }
}
