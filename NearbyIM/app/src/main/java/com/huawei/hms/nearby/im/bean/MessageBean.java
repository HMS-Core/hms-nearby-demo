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
package com.huawei.hms.nearby.im.bean;

import android.net.Uri;

public class MessageBean {
    public static final int TYPE_SEND_TEXT = 0;
    public static final int TYPE_RECEIVE_TEXT = 1;
    public static final int TYPE_SEND_IMAGE = 2;
    public static final int TYPE_RECEIVE_IMAGE = 3;
    public static final int TYPE_SEND_FILE = 4;
    public static final int TYPE_RECEIVE_FILE = 5;
    public static final String ACTION_TAG_ONLINE = "The user is online!";
    private int type;
    private String msg;
    private long receiveTime;
    private String userName;
    private int headIcon;
    private String groupId;
    private String groupName;
    private String sendTime;
    private long sendTimeValue;
    private String fileName;
    private Uri fileUri;

    private boolean isSending;
    private long payloadId;
    private int progress;
    private long totalBytes;
    private long transferredBytes;

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public long getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(long payloadId) {
        this.payloadId = payloadId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getTransferredBytes() {
        return transferredBytes;
    }

    public void setTransferredBytes(long transferredBytes) {
        this.transferredBytes = transferredBytes;
    }

    /**
     * constructor
     */
    public MessageBean() {
    }
    public MessageBean(String userName, String sendTime, String msgContent) {
        this.userName = userName;
        this.sendTime = sendTime;
        this.msg = msgContent;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "msg='" + msg + '\'' +
                ", userName='" + userName + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public long getSendTimeValue() {
        return sendTimeValue;
    }

    public void setSendTimeValue(long sendTimeValue) {
        this.sendTimeValue = sendTimeValue;
    }

    /**
     * get message
     *
     * @return message
     */
    public String getMsg() {
        return msg;
    }

    /**
     * set message
     *
     * @param msg message
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }
}
