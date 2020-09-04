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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SimpleArrayMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import com.huawei.hms.nearby.transfer.Data;
import com.huawei.hms.nearby.transfer.DataCallback;
import com.huawei.hms.nearby.transfer.TransferEngine;
import com.huawei.hms.nearby.transfer.TransferStateUpdate;
import com.huawei.hms.nearbyconnectiondemo.utils.FileUtil;
import com.huawei.hms.nearbyconnectiondemo.utils.ToastUtil;
import com.huawei.hms.nearbyconnectiondemo.utils.permission.PermissionHelper;
import com.huawei.hms.nearbyconnectiondemo.utils.permission.PermissionInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * MainActivity class
 *
 * @since 2020-01-13
 */
public class MainActivity extends AppCompatActivity implements PermissionInterface, View.OnClickListener {
    private static final int TIMEOUT_MILLISECONDS = 10000;
    private static final int REQUEST_OPEN_DOCUMENT = 20;
    private static final String TAG = "NearbyConnectionDemo";
    private static volatile MainActivity M_SERVICE = null;
    private final SimpleArrayMap<Long, Data> incomingFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Data> completedFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();
    private List<Long> sendPayloadIds = new ArrayList<>();
    private List<Long> receivePayloadIds = new ArrayList<>();

    private TransferEngine mTransferEngine = null;
    private DiscoveryEngine mDiscoveryEngine = null;

    private PermissionHelper mPermissionHelper;

    private EditText myNameEt;
    private EditText friendNameEt;
    private EditText msgEt;

    private ListView messageListView;

    private List<MessageBean> msgList;

    private ChatAdapter adapter;

    private Button sendBtn;
    private Button connectBtn;
    private Button sendPictureBtn;
    private Button receiveBtn;

    private int connectTaskResult;

    private String myNameStr;
    private String friendNameStr;
    private String myServiceId;
    private String mEndpointId;
    private String msgStr;

    private ReceivedFileListener receivedFileListener;

    /**
     * get MainActivity.class
     *
     * @return MainActivity
     */
    public static MainActivity getService() {
        if (M_SERVICE == null) {
            synchronized (MainActivity.class) {
                if (M_SERVICE == null) {
                    M_SERVICE = new MainActivity();
                }
            }
        }
        return M_SERVICE;
    }

    /**
     * public constructor
     */
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initView();
        sendBtn.setEnabled(false);
        msgEt.setEnabled(false);
        receiveBtn.setEnabled(false);
        sendPictureBtn.setEnabled(false);
    }

    private void initView() {
        myNameEt = findViewById(R.id.et_my_name);
        friendNameEt = findViewById(R.id.et_friend_name);
        msgEt = findViewById(R.id.et_msg);
        connectBtn = findViewById(R.id.btn_connect);
        sendBtn = findViewById(R.id.btn_send);
        sendPictureBtn = findViewById(R.id.btn_picture);
        receiveBtn = findViewById(R.id.btn_receivedPicture);
        connectBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        sendPictureBtn.setOnClickListener(this);
        receiveBtn.setOnClickListener(this);
        messageListView = findViewById(R.id.lv_chat);
        msgList = new ArrayList<>();
        adapter = new ChatAdapter(this, msgList);
        messageListView.setAdapter(adapter);
        connectTaskResult = StatusCode.STATUS_ENDPOINT_UNKNOWN;
    }

    /**
     * Handle timeout function
     */
    @SuppressLint("HandlerLeak")
    private Handler handler =
            new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    handler.removeMessages(0);
                    if (connectTaskResult != StatusCode.STATUS_SUCCESS) {
                        ToastUtil.showShortToastTop(getApplicationContext(),
                                "Connection timeout, make sure your friend is ready and try again.");
                        if (myNameStr.compareTo(friendNameStr) > 0) {
                            mDiscoveryEngine.stopScan();
                        } else {
                            mDiscoveryEngine.stopBroadcasting();
                        }
                        myNameEt.setEnabled(true);
                        friendNameEt.setEnabled(true);
                        connectBtn.setEnabled(true);
                    }
                }
            };

    private void requestPermissions() {
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();
    }

    @Override
    public int getPermissionsRequestCode() {
        return 10086;
    }

    /**
     * Permission for this app
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        Log.d(TAG, "request permissions success.");
    }

    @Override
    public void requestPermissionsFail() {
        Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect: {
                if (checkName()) {
                    return;
                }
                connect(view);
                handler.sendEmptyMessageDelayed(0, TIMEOUT_MILLISECONDS);
                break;
            }
            case R.id.btn_send: {
                if (checkMessage()) {
                    return;
                }
                sendMessage();
                break;
            }
            case R.id.btn_picture: {
                onSendFileButtonClicked();
                break;
            }
            case R.id.btn_receivedPicture: {
                Intent intent = new Intent(getApplicationContext(), ReceivedPhotoActivity.class);
                startActivity(intent);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Check input message
     */
    private boolean checkMessage() {
        if (TextUtils.isEmpty(msgEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input data you want to send.");
            return true;
        }
        return false;
    }

    /**
     * Check input name
     */
    private boolean checkName() {
        if (TextUtils.isEmpty(myNameEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input your name.");
            return true;
        }
        if (TextUtils.isEmpty(friendNameEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input your friend's name.");
            return true;
        }
        if (TextUtils.equals(myNameEt.getText().toString(), friendNameEt.getText().toString())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input two different names.");
            return true;
        }
        friendNameStr = friendNameEt.getText().toString();
        myNameStr = myNameEt.getText().toString();
        getServiceId();
        return false;
    }

    /**
     * Send message function
     */
    private void sendMessage() {
        msgStr = msgEt.getText().toString() + ":manually input";
        Data data = Data.fromBytes(msgStr.getBytes(Charset.defaultCharset()));
        Log.d(TAG, "myEndpointId " + mEndpointId);
        mTransferEngine.sendData(mEndpointId, data).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> {
                Log.i(TAG, "sendData [Message] success. Message:" + msgStr);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "sendData [Message] failed, Message:" + msgStr + ", cause: ", e);
            });
        });
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);
        item.setMsg(msgStr.split(":")[0]);
        item.setType(MessageBean.TYPE_SEND_TEXT);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        msgEt.setText("");
        messageListView.setSelection(messageListView.getBottom());
    }

    /**
     * Receive message function
     */
    private void receiveMessage(Data data) {
        msgStr = new String(data.asBytes(), UTF_8);
        Log.d(TAG, "onReceived [Message] success. msgStr-------->>>>" + msgStr);
        if (!msgStr.endsWith(":manually input")) {
            return;
        }
        msgStr = msgStr.split(":")[0];
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);
        item.setMsg(msgStr);
        item.setType(MessageBean.TYPE_RECEIVE_TEXT);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        messageListView.setSelection(messageListView.getBottom());
    }

    private void connect(View view) {
        ToastUtil.showShortToastTop(getApplicationContext(), "Connecting to your friend.");
        connectBtn.setEnabled(false);
        myNameEt.setEnabled(false);
        friendNameEt.setEnabled(false);
        Context context = getApplicationContext();
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context);
        if (myNameStr.compareTo(friendNameStr) > 0) {
            doStartScan(view);
        } else {
            doStartBroadcast(view);
        }
    }

    /**
     * Broadcast function.
     *
     * @param view Android view
     */
    public void doStartBroadcast(View view) {
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_STAR);
        mDiscoveryEngine.startBroadcasting(myNameStr, myServiceId, mConnCb, advBuilder.build());
    }

    private void getServiceId() {
        if (myNameStr.compareTo(friendNameStr) > 0) {
            myServiceId = myNameStr + friendNameStr;
        } else {
            myServiceId = friendNameStr + myNameStr;
        }
    }

    /**
     * Scan function.
     *
     * @param view Android view
     */
    public void doStartScan(View view) {
        ScanOption.Builder discBuilder = new ScanOption.Builder();
        discBuilder.setPolicy(Policy.POLICY_STAR);
        mDiscoveryEngine.startScan(myServiceId, mDiscCb, discBuilder.build());
    }

    private ConnectCallback mConnCb =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    mTransferEngine = Nearby.getTransferEngine(getApplicationContext());
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCb);
                    ToastUtil.showShortToastTop(getApplicationContext(), "Let's chat!");
                    sendBtn.setEnabled(true);
                    msgEt.setEnabled(true);
                    sendPictureBtn.setEnabled(true);
                    receiveBtn.setEnabled(true);
                    connectBtn.setEnabled(false);
                    connectTaskResult = StatusCode.STATUS_SUCCESS;
                    if (myNameStr.compareTo(friendNameStr) > 0) {
                        mDiscoveryEngine.stopScan();
                    } else {
                        mDiscoveryEngine.stopBroadcasting();
                    }
                }

                @Override
                public void onResult(String endpointId, ConnectResult resolution) {
                    mEndpointId = endpointId;
                }

                @Override
                public void onDisconnected(String endpointId) {
                    ToastUtil.showShortToastTop(getApplicationContext(), "Disconnect.");
                    connectTaskResult = StatusCode.STATUS_NOT_CONNECTED;
                    sendBtn.setEnabled(false);
                    connectBtn.setEnabled(true);
                    msgEt.setEnabled(false);
                    myNameEt.setEnabled(true);
                    friendNameEt.setEnabled(true);
                    sendPictureBtn.setEnabled(false);
                    receiveBtn.setEnabled(false);
                }
            };

    private ScanEndpointCallback mDiscCb =
            new ScanEndpointCallback() {
                @Override
                public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
                    mEndpointId = endpointId;
                    mDiscoveryEngine.requestConnect(myNameStr, mEndpointId, mConnCb);
                }

                @Override
                public void onLost(String endpointId) {
                    Log.d(TAG, "Nearby Connection Demo app: Lost endpoint: " + endpointId);
                }
            };

    private DataCallback mDataCb =
            new DataCallback() {
                @Override
                public void onReceived(String string, Data data) {
                    Log.d(TAG, "onReceived, Data.Type = " + data.getType());
                    Log.d(TAG, "onReceived, string ======== " + string);
                    switch (data.getType()) {
                        case Data.Type.BYTES:
                            String str = new String(data.asBytes(), UTF_8);
                            if (str.endsWith(":manually input")) {
                                receiveMessage(data);
                            } else {
                                Log.i(TAG, "onReceived [Filename] success, Data.Type.BYTES, payloadFilename ===" + str);
                                addPayloadFilename(str);
                            }
                            break;
                        case Data.Type.FILE:
                            incomingFilePayloads.put(data.getId(), data);
                            completedFilePayloads.put(data.getId(), data);
                            processFilePayload(data.getId());
                            Log.i(TAG, "onReceived [FilePayload] success, Data.Type.FILE, payloadId ===" + data.getId());
                            break;
                        default:
                            Log.i(TAG, "the other Unknown data type, please check the uploaded file.");
                            return;
                    }
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    long transferredBytes = update.getBytesTransferred();
                    long totalBytes = update.getTotalBytes();
                    long payloadId = update.getDataId();
                    Log.d(TAG, "onTransferUpdate, payloadId============" + payloadId);
                    switch (update.getStatus()) {
                        case TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS:
                            filePayloadFilenames.remove(payloadId);
                            updateProgress(transferredBytes, totalBytes, 100, payloadId, false);
                            Log.d(TAG, "onTransferUpdate.Status============success.");
                            Data payload = incomingFilePayloads.remove(payloadId);

                            if (payload != null) {
                                if (payload.getType() == Data.Type.FILE) {
                                    sendPayloadIds.remove(payloadId);
                                    receivePayloadIds.remove(payloadId);
                                    ToastUtil.showLongToast(getApplicationContext(),
                                            "Your friend shares a file with you. Tap [RECE] to find it.");
                                }
                                Log.d(TAG, "onTransferUpdate, payload.Type " + payload.getType());
                                completedFilePayloads.put(payloadId, payload);
                            }
                            break;
                        case TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS:
                            Log.d(TAG, "onTransferUpdate.Status==========transfer in progress.");
                            if (!sendPayloadIds.contains((payloadId)) && !receivePayloadIds.contains(payloadId)) {
                                if (TextUtils.isEmpty(filePayloadFilenames.get(payloadId))) {
                                    return;
                                }
                                receivePayloadIds.add(payloadId);
                                if (!FileUtil.isImage(filePayloadFilenames.get(payloadId))) {
                                    updateListViewItem(update.getDataId(),
                                            null, filePayloadFilenames.get(payloadId), totalBytes);
                                }
                            }
                            int progress = (int) (transferredBytes * 100 / totalBytes);
                            updateProgress(transferredBytes, totalBytes, progress, payloadId, true);
                            break;
                        default:
                            Log.d(TAG, "onTransferUpdate.Status=======" + update.getStatus());
                            return;
                    }
                }
            };

    private void updateProgress(long transferredBytes, long totalBytes,
                                int progress, long payloadId, boolean isSending) {
        for (MessageBean item : msgList) {
            if (item.getPayloadId() == payloadId) {
                item.setTransferredBytes(transferredBytes);
                item.setTotalBytes(totalBytes);
                item.setSending(isSending);
                item.setProgress(progress);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * open picture gallery, and select a photo.
     */
    public void onSendFileButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        this.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    /**
     * select a photo and begin to send it to peed device.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            Data filePayload;
            try {
                ParcelFileDescriptor pfd = getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
                filePayload = Data.fromFile(pfd);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found, cause: ", e);
                return;
            }
            sendFilePayload(filePayload, mEndpointId, uri);
        }
    }

    /**
     * begin to send photo.
     *
     * @param filePayload photo we've selected.
     * @param endpointID  peer device that we are sending our photo to.
     * @param uri         image uri
     */
    private void sendFilePayload(Data filePayload, String endpointID, Uri uri) {
        String fileName = FileUtil.getFileRealNameFromUri(this, uri);
        String filenameMessage = filePayload.getId() + ":" + fileName;
        Data filenameBytesPayload = Data.fromBytes(filenameMessage.getBytes(StandardCharsets.UTF_8));
        mTransferEngine.sendData(endpointID, filenameBytesPayload).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> {
                Log.i(TAG, "sendData [Filename] success. filename:" + filenameMessage);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "sendData [Filename] failed, filename:" + filenameMessage + ", cause: ", e);
            });
        });
        mTransferEngine.sendData(endpointID, filePayload).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> {
                Log.i(TAG, "sendData [FilePayload] success. payloadId:" + filePayload.getId());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "sendData [FilePayload] failed,  payloadId:" + filePayload.getId() + ", cause: ", e);
            });
        });
        sendPayloadIds.add(filePayload.getId());
        updateListViewItem(filePayload.getId(), uri, fileName, filePayload.asFile().getSize());
    }

    private void updateListViewItem(long payloadId, Uri uri, String fileName, long totalBytes) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "filename is null.");
            return;
        }
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);

        if (sendPayloadIds.contains(payloadId)) {
            //send
            if (FileUtil.isImage(fileName)) {
                item.setType(MessageBean.TYPE_SEND_IMAGE);
            } else {
                item.setType(MessageBean.TYPE_SEND_FILE);
            }
        } else {
            //receive
            if (FileUtil.isImage(fileName)) {
                item.setType(MessageBean.TYPE_RECEIVE_IMAGE);
            } else {
                item.setType(MessageBean.TYPE_RECEIVE_FILE);
            }
        }

        item.setSending(true);
        item.setFileUri(uri);
        item.setFileName(fileName);
        item.setTotalBytes(totalBytes);
        item.setPayloadId(payloadId);
        Log.d(TAG, "updateListViewItem, payloadId============" + payloadId);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        messageListView.setSelection(messageListView.getBottom());
    }

    private void processFilePayload(long payloadId) {
        Log.d(TAG, "processFilePayload, payloadId=========" + payloadId);
        Data filePayload = completedFilePayloads.get(payloadId);
        String filename = filePayloadFilenames.get(payloadId);
        Log.d(TAG, "received file: " + filename);
        if (filePayload != null && filename != null) {
            completedFilePayloads.remove(payloadId);
            File payloadFile = filePayload.asFile().asJavaFile();
            Log.d(TAG, "processFilePayload, payloadFile name------>>>>>>: " + payloadFile.getName());
            // Rename the file.
            File targetFileName = new File(payloadFile.getParentFile(), filename);

            boolean result = payloadFile.renameTo(targetFileName);
            if (result) {
                if (FileUtil.isImage(filename)) {
                    updateListViewItem(payloadId, Uri.fromFile(targetFileName), filename, targetFileName.length());
                }
            } else {
                Log.e(TAG, "rename the file failed.");
            }
            // inform UI
            if (receivedFileListener != null) {
                receivedFileListener.receivedFile(targetFileName);
            }
        }
    }

    private long addPayloadFilename(String payloadFilenameMessage) {
        Log.d(TAG, "addPayloadFilename, payloadFilenameMessage======== " + payloadFilenameMessage);
        String[] parts = payloadFilenameMessage.split(":");
        long payloadId = Long.parseLong(parts[0]);
        String filename = parts[1];
        filePayloadFilenames.put(payloadId, filename);
        return payloadId;
    }

    public void setReceivedFileListener(ReceivedFileListener receivedFileListener) {
        this.receivedFileListener = receivedFileListener;
    }

    /**
     * ReceivedFileListener interface.
     */
    public interface ReceivedFileListener {
        /**
         * Receive file function。
         *
         * @param file File we received.
         */
        void receivedFile(File file);
    }
}