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

package com.huawei.hms.nearbyconnectiondemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huawei.hms.nearbyconnectiondemo.utils.FileUtil;

import java.util.List;

/**
 * ChatAdapter
 *
 * @since 2020-01-13
 */
public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private List<MessageBean> msgList;

    public ChatAdapter(Context mContext, List<MessageBean> msgList) {
        this.mContext = mContext;
        this.msgList = msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).getType();
    }

    private void holderSendFile(SendFileViewHolder sendFileViewHolder, View convertView, MessageBean item) {
        sendFileViewHolder.fileNameTv = convertView.findViewById(R.id.tv_fileName);
        sendFileViewHolder.fileNameTv.setText(item.getFileName());
        sendFileViewHolder.fileSizeTv = convertView.findViewById(R.id.tv_fileSize);
        sendFileViewHolder.fileSizeTv.setText(FileUtil.formatBytes(item.getTotalBytes()));
        sendFileViewHolder.progressBar = convertView.findViewById(R.id.pb_trans_file);
        sendFileViewHolder.progressBar.setProgress(item.getProgress());
        sendFileViewHolder.transferredTv = convertView.findViewById(R.id.tv_progress);
        sendFileViewHolder.transferredTv.setText(FileUtil.formatBytes(item.getTransferredBytes())
                + "/" + FileUtil.formatBytes(item.getTotalBytes()));
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
        receiveFileViewHolder.transferredTv = convertView.findViewById(R.id.tv_progress);
        receiveFileViewHolder.transferredTv.setText(FileUtil.formatBytes(item.getTransferredBytes())
                + "/" + FileUtil.formatBytes(item.getTotalBytes()));
        receiveFileViewHolder.progressLayout = convertView.findViewById(R.id.rl_progress);
        receiveFileViewHolder.progressLayout.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
        convertView.setTag(receiveFileViewHolder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageBean item = msgList.get(position);
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
                    sendTextViewHolder.sendTv.setText(item.getMsg());
                    convertView.setTag(sendTextViewHolder);
                    break;
                case MessageBean.TYPE_RECEIVE_TEXT:
                    receiveTextViewHolder = new ReceiveTextViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_message_item, parent, false);
                    receiveTextViewHolder.receiveTv = convertView.findViewById(R.id.tv_receive);
                    receiveTextViewHolder.receiveTv.setText(item.getMsg());
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
                default:
                    break;
            }

        } else {
            switch (type) {
                case MessageBean.TYPE_SEND_TEXT:
                    sendTextViewHolder = (SendTextViewHolder) convertView.getTag();
                    sendTextViewHolder.sendTv = convertView.findViewById(R.id.tv_send);
                    sendTextViewHolder.sendTv.setText(item.getMsg());
                    break;
                case MessageBean.TYPE_RECEIVE_TEXT:
                    receiveTextViewHolder = (ReceiveTextViewHolder) convertView.getTag();
                    receiveTextViewHolder.receiveTv = convertView.findViewById(R.id.tv_receive);
                    receiveTextViewHolder.receiveTv.setText(item.getMsg());
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
                default:
                    break;
            }
        }

        return convertView;
    }

    static class SendTextViewHolder {
        TextView sendTv;
    }

    static class ReceiveTextViewHolder {
        TextView receiveTv;
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
        TextView transferredTv;
        RelativeLayout progressLayout;
    }

    static class ReceiveFileViewHolder {
        TextView fileNameTv;
        TextView fileSizeTv;
        ProgressBar progressBar;
        TextView transferredTv;
        RelativeLayout progressLayout;
    }

}
