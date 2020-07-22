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

import android.net.Uri;

/**
 * MessageBean class
 *
 * @since 2020-01-13
 */
public class MessageBean {
    public static final int TYPE_SEND_TEXT = 0;
    public static final int TYPE_RECEIVE_TEXT = 1;
    public static final int TYPE_SEND_IMAGE = 2;
    public static final int TYPE_RECEIVE_IMAGE = 3;
    public static final int TYPE_SEND_FILE = 4;
    public static final int TYPE_RECEIVE_FILE = 5;

    private String msg;
    private int type;
    private String myName;
    private String friendName;
    private boolean isSending;
    private String fileName;
    private Uri fileUri;
    private long payloadId;
    private int progress;
    private long totalBytes;
    private long transferredBytes;

    /**
     * constructor
     */
    public MessageBean() {
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

    /**
     * Get my name.
     *
     * @return myName
     */
    public String getMyName() {
        return myName;
    }

    /**
     * Set my name.
     *
     * @param myName myName
     */
    public void setMyName(String myName) {
        this.myName = myName;
    }

    /**
     * Obtain the name of the friend.
     *
     * @return friendName
     */
    public String getFriendName() {
        return friendName;
    }

    /**
     * Set the name of the friend.
     *
     * @param friendName friendName
     */
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
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
}
