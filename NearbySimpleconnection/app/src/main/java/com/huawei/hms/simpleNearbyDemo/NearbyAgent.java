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

package com.huawei.hms.simpleNearbyDemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NearbyAgent {
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private Context mContext = null;
    private TransferEngine mTransferEngine = null;
    private DiscoveryEngine mDiscoveryEngine = null;
    private String TAG = "Nearby_Agent";
    private String mFileServiceId = "NearbyAgentFileService";
    private String mRemoteEndpointId;
    private String mScanEndpointName = "NearbyAgentScanner";
    private String mBoradcastEndpointName = android.os.Build.DEVICE;
    private String mRcvedFilename = null;
    private Uri mUri = null;
    private int mRecvCnt = 0;
    private String mSelectedEndpoitId;
    private boolean mIsDialogOn = false;
    private RadioGroup radioGroup;

    public NearbyAgent(Context context) {
        mContext = context;
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context);
        mTransferEngine = Nearby.getTransferEngine(context);
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions( (Activity)context, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    public void sendFile(Uri uri) {
        mUri = uri;
        ScanOption.Builder scanBuilder = new ScanOption.Builder();
        scanBuilder.setPolicy(Policy.POLICY_P2P);
        mDiscoveryEngine.startScan(mFileServiceId, mDiscCbSender, scanBuilder.build());
        Log.d(TAG, "Start Scan.");
    }

    private void sendFileInner() {
        Data filenameMsg = null;
        Data filePayload = null;
        try {
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(mUri, "r");
            filePayload = Data.fromFile(pfd);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
            return;
        }
        String fileName = getFileRealNameFromUri(mContext, mUri);
        filenameMsg = Data.fromBytes(fileName.getBytes(StandardCharsets.UTF_8));

        Log.d(TAG, "Send filename: " + fileName);
        mTransferEngine.sendData(mRemoteEndpointId, filenameMsg);
        Log.d(TAG, "Send Payload.");
        mTransferEngine.sendData(mRemoteEndpointId, filePayload);
    }

    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) {
            return "UnknownFile";
        }
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) {
            return "UnknownFile";
        }
        return documentFile.getName();
    }

    private void dialogUpdate(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
        Log.d(TAG, "dialogUpdate: " + endpointId);

        RadioButton radioButton = new RadioButton(mContext);
        radioButton.setText(discoveryEndpointInfo.getName());
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedEndpoitId = endpointId;
            }
        });
        radioGroup.addView(radioButton);
    }

    private void dialogShow(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
        Log.d(TAG, "dialogShow: " + endpointId);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog, null);
        radioGroup = v.findViewById(R.id.radioGroup);
        mSelectedEndpoitId = endpointId;

        RadioButton radioButton = new RadioButton(mContext);
        radioButton.setText(discoveryEndpointInfo.getName());
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedEndpoitId = endpointId;
            }
        });
        radioGroup.addView(radioButton);
        radioGroup.check(radioButton.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final Dialog dialog = builder.create();
        dialog.show();
        mIsDialogOn = true;
        dialog.getWindow().setContentView(v);
        dialog.getWindow().setGravity(Gravity.CENTER);

        Button btnSure = v.findViewById(R.id.dialog_btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mIsDialogOn = false;
                mDiscoveryEngine.requestConnect(mScanEndpointName, mSelectedEndpoitId, mConnCbSender);
                Toast.makeText(mContext, "Sending File...", Toast.LENGTH_LONG).show();
            }
        });

        Button btnCancel = v.findViewById(R.id.dialog_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mIsDialogOn = false;
                dialog.dismiss();
            }
        });
    }

    private ScanEndpointCallback mDiscCbSender =
            new ScanEndpointCallback() {
                @Override
                public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
                    if (!mIsDialogOn) {
                        dialogShow(endpointId, discoveryEndpointInfo);
                    }else {
                        dialogUpdate(endpointId, discoveryEndpointInfo);
                    }
                }

                @Override
                public void onLost(String endpointId) {
                    Log.d(TAG, "Lost endpoint." );
                }
            };

    private ConnectCallback mConnCbSender =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    Log.d(TAG, "Accept connection.");
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCbSender);
                }

                @Override
                public void onResult(String endpointId, ConnectResult result) {
                    if (result.getStatus().getStatusCode() == StatusCode.STATUS_SUCCESS) {
                        Log.d(TAG, "Connection Established. Stop discovery. Start to send file." );
                        mRemoteEndpointId = endpointId;
                        mDiscoveryEngine.stopScan();
                        sendFileInner();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d(TAG, "Disconnected." );
                }
            };

    private DataCallback mDataCbSender =
            new DataCallback() {
                @Override
                public void onReceived(String endpointId, Data data) {
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    long transferredBytes = update.getBytesTransferred();
                    long totalBytes = update.getTotalBytes();

                    if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                        Log.d(TAG, "Transfer success.");
                    }
                    else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                        Log.d(TAG, "Transfer in progress. Transferred Bytes: "
                                + transferredBytes + " Total Bytes: " + totalBytes);
                    }
                    else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                        Log.d(TAG, "Transfer failed.");
                    }
                    else{
                        Log.d(TAG, "Transfer cancelled.");
                    }
                }
            };

    public void receiveFile() {
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_P2P);
        mDiscoveryEngine.startBroadcasting(mBoradcastEndpointName, mFileServiceId, mConnCbRcver, advBuilder.build());
        Log.d(TAG, "Start Broadcasting.");
    }

    private ConnectCallback mConnCbRcver =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    Log.d(TAG, "Accept connection.");
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCbRcver);
                }

                @Override
                public void onResult(String endpointId, ConnectResult result) {
                    if (result.getStatus().getStatusCode() == StatusCode.STATUS_SUCCESS) {
                        Log.d(TAG, "Connection Established. Stop Discovery." );
                        mDiscoveryEngine.stopBroadcasting();
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d(TAG, "Disconnected." );
                }
            };

    private DataCallback mDataCbRcver =
            new DataCallback() {
                @Override
                public void onReceived(String endpointId, Data data) {
                    if (data.getType() == Data.Type.BYTES) {
                        String msg = new String(data.asBytes(), UTF_8);
                        mRcvedFilename = msg;
                        Log.d(TAG, "received filename: " + mRcvedFilename);
                    }
                    else if (data.getType() == Data.Type.FILE) {
                        File rawFile = data.asFile().asJavaFile();
                        Log.d(TAG, "received raw file: " + rawFile.getAbsolutePath());
                        File destFile = new File(rawFile.getParent(), mRcvedFilename);
                        Log.d(TAG, "rename to : " + destFile.getAbsolutePath());
                        boolean result = rawFile.renameTo(destFile);
                        if (!result) {
                            Log.d(TAG, "rename failed. deep copy.");
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Files.move(rawFile.toPath(), destFile.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (IOException e) {
                                Log.d(TAG, "deep copy failed.");
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "received stream. ");
                    }
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    long transferredBytes = update.getBytesTransferred();
                    long totalBytes = update.getTotalBytes();

                    if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                        Log.d(TAG, "Transfer success.");
                        mRecvCnt++;
                        //filename + filepayload = 2
                        if (mRecvCnt == 2) {
                            Toast.makeText(mContext,"Transfer success.",  Toast.LENGTH_LONG).show();
                            mDiscoveryEngine.disconnectAll();
                            mRecvCnt = 0;
                        }
                    }
                    else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                        Log.d(TAG, "Transfer in progress. Transferred Bytes: "
                                + transferredBytes + " Total Bytes: " + totalBytes);
                    }
                    else if(update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                        Log.d(TAG, "Transfer failed.");
                    }
                    else {
                        Log.d(TAG, "Transfer cancelled.");
                    }
                }
            };

}


