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

package com.huawei.hms.nearby.beacondemo.consumer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.nearby.beacondemo.R;
import com.huawei.hms.nearby.beacondemo.consumer.model.StoreAdapterInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Store Adapter
 *
 * @since 2019-12-13
 */
public class StoreAdapter extends BaseAdapter {
    /**
     * View Holder
     *
     * @since 2019-12-13
     */
    static class ViewHolder {
        ImageView storeIv;
        TextView storeNameTv;
        TextView storeDescTv;
        TextView storePriceTv;
        TextView discountDesTv;
        LinearLayout discountLayout;
    }

    private Context mContext;
    private List<StoreAdapterInfo> mStoreAdapterInfoList;

    /**
     * Store Adapter Construct
     *
     * @param context Context
     * @param storeAdapterInfoList StoreAdapterInfoList
     */
    public StoreAdapter(Context context, List<StoreAdapterInfo> storeAdapterInfoList) {
        this.mContext = context;
        this.mStoreAdapterInfoList = storeAdapterInfoList;
    }

    @Override
    public int getCount() {
        return mStoreAdapterInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStoreAdapterInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = view;
        ViewHolder holder = new ViewHolder();
        StoreAdapterInfo item = mStoreAdapterInfoList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.store_list_item, null, false);
            holder.storeIv = convertView.findViewById(R.id.iv_store);
            holder.storeDescTv = convertView.findViewById(R.id.tv_store_desc);
            holder.storeNameTv = convertView.findViewById(R.id.tv_store_name);
            holder.storePriceTv = convertView.findViewById(R.id.tv_store_price);
            holder.discountLayout = convertView.findViewById(R.id.ll_discount);
            holder.discountDesTv = convertView.findViewById(R.id.tv_discount_des);
            convertView.setTag(holder);
        } else {
            Object object = convertView.getTag();
            if (object instanceof ViewHolder) {
                holder = (ViewHolder) object;
            }
        }
        Picasso.with(mContext)
                .load(item.getStoreImageUrl())
                .placeholder(R.mipmap.timg)
                .into(holder.storeIv);
        holder.storeDescTv.setText(item.getStoreDesc());
        holder.storeNameTv.setText(item.getStoreName());
        if (item.isShowNotice()) {
            holder.discountLayout.setVisibility(View.VISIBLE);
            holder.discountDesTv.setText(item.getNotice());
        } else {
            holder.discountLayout.setVisibility(View.GONE);
            holder.discountDesTv.setText("");
        }
        return convertView;
    }

    /**
     * Set Store Adapter Info Data
     *
     * @param storeAdapterInfoList Store Adapter Info List
     */
    public void setDatas(List<StoreAdapterInfo> storeAdapterInfoList) {
        mStoreAdapterInfoList = storeAdapterInfoList;
        notifyDataSetChanged();
    }
}
