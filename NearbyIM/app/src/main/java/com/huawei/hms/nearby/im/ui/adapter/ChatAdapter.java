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
        BaseViewHolder viewHolder = null;
        if (convertView == null) {
            switch (type) {
                case MessageBean.TYPE_SEND_TEXT:
                    viewHolder = new SendTextViewHolder(mContext, parent);
                    break;
                case MessageBean.TYPE_RECEIVE_TEXT:
                    viewHolder = new ReceiveTextViewHolder(mContext, parent);
                    break;
                case MessageBean.TYPE_SEND_IMAGE:
                    viewHolder = new SendImageViewHolder(mContext, parent);
                    break;
                case MessageBean.TYPE_RECEIVE_IMAGE:
                    viewHolder = new ReceiveImageViewHolder(mContext, parent);
                    break;
                case MessageBean.TYPE_SEND_FILE:
                    viewHolder = new SendFileViewHolder(mContext, parent);
                    break;
                case MessageBean.TYPE_RECEIVE_FILE:
                    viewHolder = new ReceiveFileViewHolder(mContext, parent);
                    break;
                default:
                    return null;
            }
            convertView = viewHolder.convertView;
            convertView.setTag(viewHolder);
        }
        showUIData(type, convertView, item);
        return convertView;
    }

    private void showUIData(int type, View convertView, MessageBean item) {
        switch (type) {
            case MessageBean.TYPE_SEND_TEXT:
                SendTextViewHolder sendTextViewHolder = (SendTextViewHolder) convertView.getTag();
                sendTextViewHolder.sendTv.setText(item.getMsg());
                sendTextViewHolder.nameTv.setText(item.getUserName());
                break;
            case MessageBean.TYPE_RECEIVE_TEXT:
                ReceiveTextViewHolder receiveTextViewHolder = (ReceiveTextViewHolder) convertView.getTag();
                receiveTextViewHolder.receiveTv.setText(item.getMsg());
                receiveTextViewHolder.nameTv.setText(item.getUserName());
                break;
            case MessageBean.TYPE_SEND_IMAGE:
                SendImageViewHolder sendImageViewHolder = (SendImageViewHolder) convertView.getTag();
                sendImageViewHolder.sendIv = convertView.findViewById(R.id.iv_send_image);
                sendImageViewHolder.sendProgess = convertView.findViewById(R.id.pb_send_image);
                sendImageViewHolder.sendProgess.setProgress(item.getProgress());
                sendImageViewHolder.sendProgess.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
                Glide.with(mContext).load(item.getFileUri()).into(sendImageViewHolder.sendIv);
                break;
            case MessageBean.TYPE_RECEIVE_IMAGE:
                ReceiveImageViewHolder receiveImageViewHolder = (ReceiveImageViewHolder) convertView.getTag();
                receiveImageViewHolder.receiveIv = convertView.findViewById(R.id.iv_receive_image);
                Glide.with(mContext).load(item.getFileUri()).into(receiveImageViewHolder.receiveIv);
                break;
            case MessageBean.TYPE_SEND_FILE:
                SendFileViewHolder sendFileViewHolder = (SendFileViewHolder) convertView.getTag();
                sendFileViewHolder.fileNameTv.setText(item.getFileName());
                sendFileViewHolder.fileSizeTv.setText(FileUtil.formatBytes(item.getTotalBytes()));
                sendFileViewHolder.progressBar.setProgress(item.getProgress());
                sendFileViewHolder.progressLayout.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
                break;
            case MessageBean.TYPE_RECEIVE_FILE:
                ReceiveFileViewHolder receiveFileViewHolder = (ReceiveFileViewHolder) convertView.getTag();
                receiveFileViewHolder.fileNameTv.setText(item.getFileName());
                receiveFileViewHolder.fileSizeTv.setText(FileUtil.formatBytes(item.getTotalBytes()));
                receiveFileViewHolder.progressBar.setProgress(item.getProgress());
                receiveFileViewHolder.progressLayout.setVisibility(item.isSending() ? View.VISIBLE : View.GONE);
                break;
        }
    }

    static class BaseViewHolder {
        View convertView;
    }

    static class SendTextViewHolder extends BaseViewHolder {
        TextView sendTv;
        TextView tvTime;
        TextView nameTv;

        public SendTextViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.send_message_item, parent, false);
            sendTv = convertView.findViewById(R.id.tv_send);
            nameTv = convertView.findViewById(R.id.tv_name);
            tvTime = convertView.findViewById(R.id.tv_time);
        }
    }

    static class ReceiveTextViewHolder extends BaseViewHolder {
        TextView receiveTv;
        TextView nameTv;
        TextView tvTime;

        public ReceiveTextViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_message_item, parent, false);
            receiveTv = convertView.findViewById(R.id.tv_receive);
            nameTv = convertView.findViewById(R.id.tv_name);
            tvTime = convertView.findViewById(R.id.tv_time);
        }
    }

    static class SendImageViewHolder extends BaseViewHolder {
        ImageView sendIv;
        ProgressBar sendProgess;

        public SendImageViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.send_image_item, parent, false);
            sendIv = convertView.findViewById(R.id.iv_send_image);
            sendProgess = convertView.findViewById(R.id.pb_send_image);
        }
    }

    static class ReceiveImageViewHolder extends BaseViewHolder {
        ImageView receiveIv;

        public ReceiveImageViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_image_item, parent, false);
            receiveIv = convertView.findViewById(R.id.iv_receive_image);
        }
    }

    static class SendFileViewHolder extends BaseViewHolder {
        TextView fileNameTv;
        TextView fileSizeTv;
        ProgressBar progressBar;
        RelativeLayout progressLayout;

        public SendFileViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.send_file_item, parent, false);
            fileNameTv = convertView.findViewById(R.id.tv_fileName);
            progressLayout = convertView.findViewById(R.id.rl_progress);
            progressBar = convertView.findViewById(R.id.pb_trans_file);
            fileSizeTv = convertView.findViewById(R.id.tv_fileSize);
        }
    }

    static class ReceiveFileViewHolder extends BaseViewHolder {
        TextView fileNameTv;
        TextView fileSizeTv;
        ProgressBar progressBar;
        RelativeLayout progressLayout;

        public ReceiveFileViewHolder(Context mContext, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.receive_file_item, parent, false);
            fileNameTv = convertView.findViewById(R.id.tv_fileName);
            fileSizeTv = convertView.findViewById(R.id.tv_fileSize);
            progressBar = convertView.findViewById(R.id.pb_trans_file);
            progressLayout = convertView.findViewById(R.id.rl_progress);
        }
    }
}
