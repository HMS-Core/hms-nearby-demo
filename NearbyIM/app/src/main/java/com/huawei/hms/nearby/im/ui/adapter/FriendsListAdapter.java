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

package com.huawei.hms.nearby.im.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.Custom;

public class FriendsListAdapter extends BaseAdapter<Custom> {

    public FriendsListAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = view;
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friends_list_item, parent, false);
            holder.nicknameTv = convertView.findViewById(R.id.tv_nickname);
            holder.statusTv = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            Object object = convertView.getTag();
            if (object instanceof ViewHolder) {
                holder = (ViewHolder) object;
            }
        }
        holder.nicknameTv.setText(data.get(position).getNickName());
        holder.statusTv.setText(data.get(position).getStatus());
        return convertView;
    }

    static class ViewHolder {
        TextView nicknameTv;
        TextView statusTv;
    }
}
