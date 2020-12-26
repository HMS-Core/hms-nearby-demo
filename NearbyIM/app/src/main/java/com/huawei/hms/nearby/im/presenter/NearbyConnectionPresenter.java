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
package com.huawei.hms.nearby.im.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.discovery.BroadcastOption;
import com.huawei.hms.nearby.discovery.ConnectCallback;
import com.huawei.hms.nearby.discovery.ConnectInfo;
import com.huawei.hms.nearby.discovery.ConnectResult;
import com.huawei.hms.nearby.discovery.DiscoveryEngine;
import com.huawei.hms.nearby.discovery.Policy;
import com.huawei.hms.nearby.discovery.ScanEndpointCallback;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.discovery.ScanOption;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.utils.FileUtil;
import com.huawei.hms.nearby.im.view.INearbyConnectionView;
import com.huawei.hms.nearby.transfer.Data;
import com.huawei.hms.nearby.transfer.DataCallback;
import com.huawei.hms.nearby.transfer.TransferEngine;
import com.huawei.hms.nearby.transfer.TransferStateUpdate;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import androidx.collection.SimpleArrayMap;
import static com.huawei.hms.nearby.im.utils.Constants.TAG;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NearbyConnectionPresenter extends BasePresenter<INearbyConnectionView>{

    private DiscoveryEngine mDiscoveryEngine;
    private TransferEngine mTransferEngine;
    private String mEndpointId;
    private String myNameStr = CommonUtil.userName;
    private String serviceId = "NearbyIM";
    private Gson gson;
    private ScanOption scanOption;
    private BroadcastOption broadcastOption;

    public NearbyConnectionPresenter(Context mContext, INearbyConnectionView view) {
        super(mContext, view);
        init();
    }

    private void init() {
        mDiscoveryEngine = Nearby.getDiscoveryEngine(mContext.getApplicationContext());
        mTransferEngine = Nearby.getTransferEngine(mContext.getApplicationContext());
        scanOption = new ScanOption.Builder().setPolicy(Policy.POLICY_STAR).build();
        broadcastOption = new BroadcastOption.Builder().setPolicy(Policy.POLICY_STAR).build();
        gson = new Gson();
        mDiscoveryEngine.startBroadcasting(myNameStr, serviceId, connectCallback, broadcastOption);
    }

    /**
     * scanAndBroadcasting to find nearby people
     */
    public void findNearbyPeople(){
        mDiscoveryEngine.startScan(serviceId, new ScanEndpointCallback() {
            @Override
            public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
                Log.d(TAG, "onFound -- Nearby Connection Demo app: onFound endpoint: " + endpointId);
                view.onFound(endpointId,discoveryEndpointInfo);
            }

            @Override
            public void onLost(String endpointId) {
                Log.d(TAG, "onLost -- Nearby Connection Demo app: Lost endpoint: " + endpointId);
                view.onLost(endpointId);
            }
        }, scanOption);
    }

    /**
     * requestConnect
     * @param endpointId the endpointId of remote device
     */
    public void requestConnect(String endpointId) {
        Log.d(TAG, "requestConnect -- endpoint: " + endpointId);
        mDiscoveryEngine.requestConnect(myNameStr, endpointId, connectCallback);
    }

    /**
     * Send message ,Data.Type.BYTES
     */
    public MessageBean sendMessage(String msgStr) {
        MessageBean item = new MessageBean();
        item.setUserName(CommonUtil.userName);
        item.setMsg(msgStr);
        item.setType(MessageBean.TYPE_SEND_TEXT);
        item.setSendTime(CommonUtil.getCurrentTime(CommonUtil.FORMAT));
        Data data = Data.fromBytes(gson.toJson(item).getBytes(Charset.defaultCharset()));
        mTransferEngine.sendData(mEndpointId, data);
        return item;
    }

    /**
     * send file ,Data.Type.FILE
     * @param uri
     */
    public Data sendFile(Uri uri) {
        Data filePayload;
        try {
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(uri, "r");
            filePayload = Data.fromFile(pfd);
        } catch (FileNotFoundException e) {
            Log.e(Constants.TAG, "File not found, cause: ", e);
            return null;
        }
        String fileName = FileUtil.getFileRealNameFromUri(mContext, uri);
        String filenameMessage = filePayload.getId() + ":" + fileName;
        Data filenameBytesPayload = Data.fromBytes(filenameMessage.getBytes(StandardCharsets.UTF_8));
        mTransferEngine.sendData(mEndpointId, filenameBytesPayload);
        mTransferEngine.sendData(mEndpointId, filePayload);
        return filePayload;
    }

    /**
     * ConnectCallback
     */
    private ConnectCallback connectCallback = new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    Log.d(TAG, "onEstablish()--- endpointId=="+endpointId);
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCallback);
                }

                @Override
                public void onResult(String endpointId, ConnectResult result) {
                    if (result.getStatus().getStatusCode() == StatusCode.STATUS_SUCCESS) {
                        Log.d(TAG, "onResult() -- Connection Established.Start to send file.");
                        Toast.makeText(mContext,"Let's chat!",Toast.LENGTH_SHORT).show();
                        NearbyConnectionPresenter.this.mEndpointId = endpointId;
                        mDiscoveryEngine.stopScan();
                        mDiscoveryEngine.stopBroadcasting();
                        view.onEstablish(endpointId);
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Toast.makeText(mContext,"Disconnect",Toast.LENGTH_SHORT).show();
                }
            };

    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();

    private DataCallback mDataCallback = new DataCallback() {
        @Override
        public void onReceived(String endPoint, Data data) {
            Log.d(TAG, "onReceived, Data.Type = " + data.getType() + ",endPoint:" + endPoint);
            switch (data.getType()) {
                case Data.Type.BYTES:
                    String msgStr = new String(data.asBytes(), UTF_8);
                    if (msgStr.contains("userName")) {
                        view.receiveMessage(new Gson().fromJson(msgStr, MessageBean.class));
                    } else {
                        Log.i(TAG, "onReceived [Filename] success, Data.Type.BYTES, payloadFilename ===" + msgStr);
                        String[] parts = msgStr.split(":");
                        long payloadId = Long.parseLong(parts[0]);
                        String filename = parts[1];
                        filePayloadFilenames.put(payloadId, filename);
                    }
                    break;
                case Data.Type.FILE:
                    dearWithFile(data);
                    break;
                default:
                    return;
            }
        }

        @Override
        public void onTransferUpdate(String string, TransferStateUpdate update) {
            long transferredBytes = update.getBytesTransferred();
            long totalBytes = update.getTotalBytes();
            long payloadId = update.getDataId();
            switch (update.getStatus()) {
                case TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS:
                    Log.d(TAG, "onTransferUpdate.Status============success.");
                    filePayloadFilenames.remove(payloadId);
                    view.updateProgress(transferredBytes, totalBytes, 100, payloadId, false);
                    break;
                case TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS:
                    if (totalBytes == 0) {
                        Log.e(TAG,"--------->>> totalBytes == 0");
                        return;
                    }
                    int progress = (int) (transferredBytes * 100 / totalBytes);
                    Log.d(TAG, "onTransferUpdate.===transfer in progress:" + progress);
                    view.updateProgress(transferredBytes, totalBytes, progress, payloadId, true);
                    break;
                default:
                    return;
            }
        }
    };

    private void dearWithFile(Data data) {
        long payloadId = data.getId();
        String filename = filePayloadFilenames.get(payloadId);
        if (TextUtils.isEmpty(filename)) {
            return;
        }
        File payloadFile = data.asFile().asJavaFile();
        File targetFileName = new File(payloadFile.getParentFile(), filename);
        boolean renameResult = payloadFile.renameTo(targetFileName);
        Log.d(TAG, "onReceived, payloadFile:" + payloadFile.getName() + ",filename:"+filename);
        if (renameResult) {
            MessageBean item = new MessageBean();
            item.setUserName(CommonUtil.userName);
            item.setType(FileUtil.isImage(filename) ? MessageBean.TYPE_RECEIVE_IMAGE
                    :MessageBean.TYPE_RECEIVE_FILE);
            item.setSending(true);
            item.setFileUri(Uri.fromFile(targetFileName));
            item.setFileName(filename);
            item.setTotalBytes(targetFileName.length());
            item.setPayloadId(payloadId);
            view.addFileItem(item);
        } else {
            Log.e(TAG, "rename the file failed. please check the permission");
        }
    }

    @Override
    public void onDestroy() {
        mDiscoveryEngine.stopScan();
        mDiscoveryEngine.stopBroadcasting();
        mDiscoveryEngine.disconnectAll();
        super.onDestroy();
    }
}
