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
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconBaseLog;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconUtil;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.Attachment;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * BeaconAttachmentAdapter
 *
 * @since 2019-11-22
 */
public class BeaconAttachmentAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<Attachment> attachments;

    /**
     * Constructor
     *
     * @param mContext activity context
     */
    public BeaconAttachmentAdapter(Context mContext) {
        this.mContext = mContext;
        attachments = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public Object getItem(int position) {
        BeaconBaseLog.i("BeaconAttachmentAdapter", "getItem" + position + " " + attachments.get(position).toString());
        return attachments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AttachmentViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.beacon_attachment_item, parent, false);
            viewHolder = new AttachmentViewHolder();
            viewHolder.tvBeaconAttachmentNamespaceTv = convertView.findViewById(R.id.tv_beacon_attachment_namespace);
            viewHolder.tvBeaconAttachmentType = convertView.findViewById(R.id.tv_beacon_attachment_type);
            viewHolder.tvBeaconAttachmentVal = convertView.findViewById(R.id.tv_beacon_attachment_val);
            viewHolder.ivBeaconAttachmentDel = convertView.findViewById(R.id.iv_beacon_attachment_delete);
            convertView.setTag(viewHolder);

        } else {
            Object tag = convertView.getTag();
            if (!(tag instanceof AttachmentViewHolder)) {
                return convertView;
            }
            viewHolder = (AttachmentViewHolder) tag;
        }

        Attachment attachment = attachments.get(position);
        viewHolder.tvBeaconAttachmentNamespaceTv.setText(attachment.getNamespace());
        viewHolder.tvBeaconAttachmentType.setText(attachment.getType());
        viewHolder.tvBeaconAttachmentVal
            .setText(new String(BeaconUtil.base64StrToBytes(attachment.getValue()), Charset.forName("UTF-8")));

        viewHolder.ivBeaconAttachmentDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mContext instanceof ViewBeaconActivity)) {
                    return;
                }
                ViewBeaconActivity activity = (ViewBeaconActivity) mContext;
                activity.deleteBeaconAttachment(attachment);
            }
        });
        return convertView;
    }

    static class AttachmentViewHolder {
        TextView tvBeaconAttachmentNamespaceTv;

        TextView tvBeaconAttachmentType;

        TextView tvBeaconAttachmentVal;

        ImageView ivBeaconAttachmentDel;
    }

    /**
     * refresh attachments list view data
     *
     * @param attachments beacon properties
     */
    public void refreshData(ArrayList<Attachment> attachments) {
        this.attachments.clear();
        this.attachments.addAll(attachments);
        notifyDataSetChanged();
    }
}
