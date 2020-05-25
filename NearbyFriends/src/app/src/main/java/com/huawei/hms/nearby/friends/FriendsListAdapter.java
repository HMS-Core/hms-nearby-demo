/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.nearby.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Friends List Adapter
 *
 * @since 2019-12-13
 */
public class FriendsListAdapter extends BaseAdapter {
    /**
     * View Holder
     *
     * @since 2019-12-13
     */
    static class ViewHolder {
        ImageView headIv;
        TextView nicknameTv;
        TextView statusTv;
    }

    private LinkedList<Custom> mData;
    private Context mContext;

    /**
     * Friends List Adapter Construct
     *
     * @param data    CustomData
     * @param context Context
     */
    public FriendsListAdapter(LinkedList<Custom> data, Context context) {
        this.mData = data;
        this.mContext = context;
    }

    /**
     * Set data
     *
     * @param data CustomData
     */
    public void setData(LinkedList<Custom> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return new Object();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = view;
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friends_list_item, parent, false);
            holder.headIv = convertView.findViewById(R.id.iv_head);
            holder.nicknameTv = convertView.findViewById(R.id.tv_nickname);
            holder.statusTv = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            Object object = convertView.getTag();
            if (object instanceof ViewHolder) {
                holder = (ViewHolder) object;
            }
        }
        holder.headIv.setImageResource(mData.get(position).getHeadIcon());
        holder.nicknameTv.setText(mData.get(position).getNickName());
        holder.statusTv.setText(mData.get(position).getStatus());
        return convertView;
    }
}
