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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
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
import java.nio.charset.StandardCharsets;

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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    public static final int REQUEST_CODE_SCAN_ONE = 0X01;

    private Context mContext = null;
    private TransferEngine mTransferEngine = null;
    private DiscoveryEngine mDiscoveryEngine = null;
    private String TAG = "Nearby_Agent";
    private String mFileServiceId = "NearbyAgentFileService";
    private String mRemoteEndpointId;
    private String mRemoteEndpointName;
    private String mEndpointName = android.os.Build.DEVICE;
    private String mScanInfo;
    private String mRcvedFilename = null;
    private Uri mUri = null;
    private int mRecvCnt = 0;
    private Bitmap mResultImage;
    private ImageView mBarcodeImage;
    private File mDestFile;
    private String mFileName;
    private ProgressBar mProgress;
    private TextView mDescText;
    private long mStartTime = 0;
    private float mSpeed = 60;
    private String mSpeedStr = "60";

    public NearbyAgent(Context context) {
        mContext = context;
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context);
        mTransferEngine = Nearby.getTransferEngine(context);
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) context, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }

        mProgress = ((Activity) mContext).findViewById(R.id.pb_main_download);
        mProgress.setVisibility(View.INVISIBLE);
        mDescText = ((Activity) mContext).findViewById(R.id.tv_main_desc);
        mBarcodeImage = ((Activity) mContext).findViewById(R.id.barcode_image);
    }

    public void sendFile(Uri uri) {
        init();
        /* save uri */
        mUri = uri;
        /* generate bitmap */
        try {
            //Generate the barcode.
            HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator().setBitmapMargin(1).setBitmapColor(Color.BLACK).setBitmapBackgroundColor(Color.WHITE).create();
            mResultImage = ScanUtil.buildBitmap(mEndpointName, HmsScan.QRCODE_SCAN_TYPE, 700, 700, options);
            mBarcodeImage.setVisibility(View.VISIBLE);
            mBarcodeImage.setImageBitmap(mResultImage);
        } catch (WriterException e) {
        }
        /* start broadcast */
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_P2P);
        mDiscoveryEngine.startBroadcasting(mEndpointName, mFileServiceId, mConnCbSender, advBuilder.build());
        Log.d(TAG, "Start Broadcasting.");
    }

    public void receiveFile() {
        /* scan bitmap */
        init();
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).create();
        ScanUtil.startScan((Activity) mContext, REQUEST_CODE_SCAN_ONE, options);
    }


    public void onScanResult(Intent data) {
        /* save endpoint name */
        HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
        mScanInfo = obj.getOriginalValue();
        /* start scan*/
        ScanOption.Builder scanBuilder = new ScanOption.Builder();
        scanBuilder.setPolicy(Policy.POLICY_P2P);
        mDiscoveryEngine.startScan(mFileServiceId, mDiscCb, scanBuilder.build());
        Log.d(TAG, "Start Scan.");
        mDescText.setText("Connecting to " + mScanInfo + "...");
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
        mFileName = getFileRealNameFromUri(mContext, mUri);
        filenameMsg = Data.fromBytes(mFileName.getBytes(StandardCharsets.UTF_8));

        Log.d(TAG, "Send filename: " + mFileName);
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


    private ScanEndpointCallback mDiscCb =
            new ScanEndpointCallback() {
                @Override
                public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
                    if (discoveryEndpointInfo.getName().equals(mScanInfo)) {
                        Log.d(TAG, "Found endpoint:" + discoveryEndpointInfo.getName() + ". Connecting.");
                        mDiscoveryEngine.requestConnect(mEndpointName, endpointId, mConnCbRcver);
                    }
                }

                @Override
                public void onLost(String endpointId) {
                    Log.d(TAG, "Lost endpoint.");
                }
            };

    private ConnectCallback mConnCbSender =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    Log.d(TAG, "Accept connection.");
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCbSender);
                    mRemoteEndpointName = connectionInfo.getEndpointName();
                    mRemoteEndpointId = endpointId;
                }

                @Override
                public void onResult(String endpointId, ConnectResult result) {
                    if (result.getStatus().getStatusCode() == StatusCode.STATUS_SUCCESS) {
                        Log.d(TAG, "Connection Established. Stop discovery. Start to send file.");
                        mDiscoveryEngine.stopScan();
                        mDiscoveryEngine.stopBroadcasting();
                        sendFileInner();
                        mBarcodeImage.setVisibility(View.INVISIBLE);
                        mDescText.setText("Sending file " + mFileName + " to " + mRemoteEndpointName + ".");
                        mProgress.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d(TAG, "Disconnected.");
                }
            };

    private DataCallback mDataCbSender =
            new DataCallback() {
                @Override
                public void onReceived(String endpointId, Data data) {
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                        mRecvCnt++;
                        if (mRecvCnt == 2) {
                            mDescText.setText("Transfer success. Speed: " + mSpeedStr + "MB/S.");
                            mRecvCnt = 0;
                        }
                    } else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                        showProgressSpeed(update);
                    } else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                        Log.d(TAG, "Transfer failed.");
                    } else {
                        Log.d(TAG, "Transfer cancelled.");
                    }
                }
            };


    private ConnectCallback mConnCbRcver =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    Log.d(TAG, "Accept connection.");
                    mRemoteEndpointName = connectionInfo.getEndpointName();
                    mRemoteEndpointId = endpointId;
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCbRcver);
                }

                @Override
                public void onResult(String endpointId, ConnectResult result) {
                    if (result.getStatus().getStatusCode() == StatusCode.STATUS_SUCCESS) {
                        Log.d(TAG, "Connection Established. Stop Discovery.");
                        mDiscoveryEngine.stopBroadcasting();
                        mDiscoveryEngine.stopScan();
                        mDescText.setText("Connected.");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.d(TAG, "Disconnected.");
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
                        mDescText.setText("Receiving file " + mRcvedFilename + " from " + mRemoteEndpointName + ".");
                        mProgress.setVisibility(View.VISIBLE);
                    } else if (data.getType() == Data.Type.FILE) {
                        File rawFile = data.asFile().asJavaFile();
                        Log.d(TAG, "received raw file: " + rawFile.getAbsolutePath());
                        mDestFile = new File(rawFile.getParent(), mRcvedFilename);
                        Log.d(TAG, "rename to : " + mDestFile.getAbsolutePath());
                        rawFile.renameTo(mDestFile);
                    } else {
                        Log.d(TAG, "received stream. ");
                    }
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                        mRecvCnt++;
                        if (mRecvCnt == 2) {
                            mDescText.setText("Transfer success. Speed: " + mSpeedStr + "MB/S. \nView the File at /Sdcard/Download/Nearby");
                            mDiscoveryEngine.disconnectAll();
                            mRecvCnt = 0;
                        }
                    } else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                        showProgressSpeed(update);
                    } else if (update.getStatus() == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                        Log.d(TAG, "Transfer failed.");
                    } else {
                        Log.d(TAG, "Transfer cancelled.");
                    }
                }
            };

    private void showProgressSpeed(TransferStateUpdate update)
    {
        long transferredBytes = update.getBytesTransferred();
        long totalBytes = update.getTotalBytes();
        long curTime = System.currentTimeMillis();
        Log.d(TAG, "Transfer in progress. Transferred Bytes: "
                + transferredBytes + " Total Bytes: " + totalBytes);
        mProgress.setProgress((int) (transferredBytes * 100 / totalBytes));
        if (mStartTime == 0) {
            mStartTime = curTime;
        }

        if (curTime != mStartTime) {
            mSpeed = ((float) transferredBytes) / ((float) (curTime - mStartTime)) / 1000;
            java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.00");
            mSpeedStr = myformat.format(mSpeed);
            mDescText.setText("Transfer in Progress. Speed: " + mSpeedStr + "MB/S.");
        }

        if (transferredBytes == totalBytes) {
            mStartTime = 0;
        }
    }

    private void init()
    {
        mProgress.setProgress(0);
        mProgress.setVisibility(View.INVISIBLE);
        mDescText.setText("");
        mBarcodeImage.setVisibility(View.INVISIBLE);
        mDiscoveryEngine.disconnectAll();
        mDiscoveryEngine.stopScan();
        mDiscoveryEngine.stopBroadcasting();
    }
}


