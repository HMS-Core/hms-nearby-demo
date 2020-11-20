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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.FileUtil;

public class ChatAdapter extends BaseAdapter<MessageBean> {

    public ChatAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageBean item = data.get(position);
        int type = item.getType();
        SendTextViewHolder sendTextViewHolder = null;
        ReceiveTextViewHolder receiveTextViewHolder = null;
        SendImageViewHolder sendImageViewHolder = null;
        ReceiveImageViewHolder receiveImageViewHolder = null;
        SendFileViewHolder sendFileViewHolder = null;
        ReceiveFileViewHolder receiveFileViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case MessageBean.TYPE_SEND_TEXT:
                    sendTextViewHolder = new SendTextViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.send_message_item, parent, false);
                    sendTextViewHolder.sendTv = convertView.findViewById(R.id.tv_send);
                    sendTextViewHolder.nameTv = convertView.findViewById(R.id.tv_name);
                    sendTextViewHolder.tvTime = convertView.findViewById(R.id.tv_time);
                    convertView.setTag(sendTextViewHolder);
                    break;
                case MessageBean.TYPE_RECEIVE_TEXT:
                    receiveTextViewHolder = new ReceiveTextViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_message_item, parent, false);
                    receiveTextViewHolder.receiveTv = convertView.findViewById(R.id.tv_receive);
                    receiveTextViewHolder.nameTv = convertView.findViewById(R.id.tv_name);
                    receiveTextViewHolder.tvTime = convertView.findViewById(R.id.tv_time);
                    convertView.setTag(receiveTextViewHolder);
                    break;
                case MessageBean.TYPE_SEND_IMAGE:
                    sendImageViewHolder = new SendImageViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.send_image_item, parent, false);
                    sendImageViewHolder.sendIv = convertView.findViewById(R.id.iv_send_image);
                    sendImageViewHolder.sendProgess = convertView.findViewById(R.id.pb_send_image);
                    sendImageViewHolder.sendProgess.setProgress(item.getProgress());
                    sendImageViewHolder.sendProgess.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
                    Glide.with(mContext).load(item.getFileUri()).into(sendImageViewHolder.sendIv);
                    convertView.setTag(sendImageViewHolder);
                    break;
                case MessageBean.TYPE_RECEIVE_IMAGE:
                    receiveImageViewHolder = new ReceiveImageViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_image_item, parent, false);
                    receiveImageViewHolder.receiveIv = convertView.findViewById(R.id.iv_receive_image);
                    Glide.with(mContext).load(item.getFileUri()).into(receiveImageViewHolder.receiveIv);
                    convertView.setTag(receiveImageViewHolder);
                    break;
                case MessageBean.TYPE_SEND_FILE:
                    sendFileViewHolder = new SendFileViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.send_file_item, parent, false);
                    holderSendFile(sendFileViewHolder, convertView, item);
                    break;
                case MessageBean.TYPE_RECEIVE_FILE:
                    receiveFileViewHolder = new ReceiveFileViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_file_item, parent, false);
                    holderReceiveFile(receiveFileViewHolder, convertView, item);
                    break;
            }
        } else {
            switch (type) {
                case MessageBean.TYPE_SEND_TEXT:
                    sendTextViewHolder = (SendTextViewHolder) convertView.getTag();
                    break;
                case MessageBean.TYPE_RECEIVE_TEXT:
                    receiveTextViewHolder = (ReceiveTextViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case MessageBean.TYPE_SEND_TEXT:
                sendTextViewHolder.sendTv.setText(item.getMsg());
                sendTextViewHolder.nameTv.setText(item.getUserName());
                if (item.getReceiveTime() > 0) {
                    sendTextViewHolder.tvTime.setVisibility(View.VISIBLE);
                    sendTextViewHolder.tvTime.setText(CommonUtil.formatTime(mContext,item.getReceiveTime()));
                }else {
                    sendTextViewHolder.tvTime.setVisibility(View.GONE);
                }
                break;
            case MessageBean.TYPE_RECEIVE_TEXT:
                receiveTextViewHolder.receiveTv.setText(item.getMsg());
                receiveTextViewHolder.nameTv.setText(item.getUserName());
                if (item.getReceiveTime() > 0) {
                    receiveTextViewHolder.tvTime.setVisibility(View.VISIBLE);
                    receiveTextViewHolder.tvTime.setText(CommonUtil.formatTime(mContext,item.getReceiveTime()));
                }else {
                    receiveTextViewHolder.tvTime.setVisibility(View.GONE);
                }
                break;
            case MessageBean.TYPE_SEND_IMAGE:
                sendImageViewHolder = (SendImageViewHolder) convertView.getTag();
                sendImageViewHolder.sendIv = convertView.findViewById(R.id.iv_send_image);
                sendImageViewHolder.sendProgess = convertView.findViewById(R.id.pb_send_image);
                sendImageViewHolder.sendProgess.setProgress(item.getProgress());
                sendImageViewHolder.sendProgess.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
                Glide.with(mContext).load(item.getFileUri()).into(sendImageViewHolder.sendIv);
                break;
            case MessageBean.TYPE_RECEIVE_IMAGE:
                receiveImageViewHolder = (ReceiveImageViewHolder) convertView.getTag();
                receiveImageViewHolder.receiveIv = convertView.findViewById(R.id.iv_receive_image);
                Glide.with(mContext).load(item.getFileUri()).into(receiveImageViewHolder.receiveIv);
                break;
            case MessageBean.TYPE_SEND_FILE:
                sendFileViewHolder = (SendFileViewHolder) convertView.getTag();
                holderSendFile(sendFileViewHolder, convertView, item);
                break;
            case MessageBean.TYPE_RECEIVE_FILE:
                if (convertView.getTag() instanceof ReceiveFileViewHolder) {
                    receiveFileViewHolder = (ReceiveFileViewHolder) convertView.getTag();
                    holderReceiveFile(receiveFileViewHolder, convertView, item);
                }
                break;
        }
        return convertView;
    }
    private void holderSendFile(SendFileViewHolder sendFileViewHolder, View convertView, MessageBean item) {
        sendFileViewHolder.fileNameTv = convertView.findViewById(R.id.tv_fileName);
        sendFileViewHolder.fileNameTv.setText(item.getFileName());
        sendFileViewHolder.fileSizeTv = convertView.findViewById(R.id.tv_fileSize);
        sendFileViewHolder.fileSizeTv.setText(FileUtil.formatBytes(item.getTotalBytes()));
        sendFileViewHolder.progressBar = convertView.findViewById(R.id.pb_trans_file);
        sendFileViewHolder.progressBar.setProgress(item.getProgress());
        sendFileViewHolder.progressLayout = convertView.findViewById(R.id.rl_progress);
        sendFileViewHolder.progressLayout.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
        convertView.setTag(sendFileViewHolder);
    }

    private void holderReceiveFile(ReceiveFileViewHolder receiveFileViewHolder, View convertView, MessageBean item) {
        receiveFileViewHolder.fileNameTv = convertView.findViewById(R.id.tv_fileName);
        receiveFileViewHolder.fileNameTv.setText(item.getFileName());
        receiveFileViewHolder.fileSizeTv = convertView.findViewById(R.id.tv_fileSize);
        receiveFileViewHolder.fileSizeTv.setText(FileUtil.formatBytes(item.getTotalBytes()));
        receiveFileViewHolder.progressBar = convertView.findViewById(R.id.pb_trans_file);
        receiveFileViewHolder.progressBar.setProgress(item.getProgress());
        receiveFileViewHolder.progressLayout = convertView.findViewById(R.id.rl_progress);
        receiveFileViewHolder.progressLayout.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
        convertView.setTag(receiveFileViewHolder);
    }
    static class SendTextViewHolder {
        TextView sendTv,nameTv,tvTime;
    }

    static class ReceiveTextViewHolder {
        TextView receiveTv,nameTv,tvTime;
    }

    static class SendImageViewHolder {
        ImageView sendIv;
        ProgressBar sendProgess;
    }

    static class ReceiveImageViewHolder {
        ImageView receiveIv;
    }

    static class SendFileViewHolder {
        TextView fileNameTv;
        TextView fileSizeTv;
        ProgressBar progressBar;
        RelativeLayout progressLayout;
    }

    static class ReceiveFileViewHolder {
        TextView fileNameTv;
        TextView fileSizeTv;
        ProgressBar progressBar;
        RelativeLayout progressLayout;
    }
}
