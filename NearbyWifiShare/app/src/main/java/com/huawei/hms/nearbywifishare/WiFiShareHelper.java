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
package com.huawei.hms.nearbywifishare;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.wifishare.WifiShareCallback;
import com.huawei.hms.nearby.wifishare.WifiShareEngine;
import com.huawei.hms.nearby.wifishare.WifiSharePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiFiShareHelper {
    private static final String TAG = "Wi-FiShareDemo*Helper";
    private Context mContext;
    private HashMap<String, ScanEndpointInfo> mScanEndpointMap;
    private ListView mNearbyDevicesListView;
    private TextView mAuthCodeTextView;
    private WifiShareEngine mWiFiShareEngine;

    public WiFiShareHelper(Context context) {
        mContext = context;
        mWiFiShareEngine = Nearby.getWifiShareEngine(context);
        mScanEndpointMap = new HashMap<>();
    }

    /**
     * The device to share WiFi
     */
    public void shareWiFiConfig() {
        Log.d(TAG, "Start to share WiFi");
        mWiFiShareEngine.startWifiShare(mWiFiShareCallback, WifiSharePolicy.POLICY_SHARE)
                .addOnFailureListener(ex -> {
                    if (ex instanceof ApiException) {
                        int errorCode = ((ApiException) ex).getStatusCode();
                        String codeStr = StatusCode.getStatusCode(errorCode);
                        Toast.makeText(mContext, codeStr, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "shareWiFiConfig apiException.getStatusCode()===="+ errorCode +",codeStr:"+codeStr);
                    }else {
                        Toast.makeText(mContext, "share failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ex.toString());
                    }
                });
        showListView();
    }

    /**
     * The device to connect WiFi
     */
    public void requestWiFiConfig() {
        Log.d(TAG, "requestWiFiConfig");
        mWiFiShareEngine.startWifiShare(mWiFiShareCallback, WifiSharePolicy.POLICY_SET)
                .addOnFailureListener(ex -> {
                    if (ex instanceof ApiException) {
                        int errorCode = ((ApiException) ex).getStatusCode();
                        String codeStr = StatusCode.getStatusCode(errorCode);
                        Toast.makeText(mContext, codeStr, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "shareWiFiConfig apiException.getStatusCode()===="+ errorCode +",codeStr:"+codeStr);
                    }else {
                        Toast.makeText(mContext, "connect failed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, ex.toString());
                    }
                });
    }

    private WifiShareCallback mWiFiShareCallback = new WifiShareCallback() {
        @Override
        public void onFound(String endpointId, ScanEndpointInfo scanEndpointInfo) {
            Log.i(TAG, "onFound,endpointId:"+endpointId+",name:"+scanEndpointInfo.getName());
            mScanEndpointMap.put(endpointId, scanEndpointInfo);
            showListView();
        }

        @Override
        public void onLost(String endpointId) {
            Log.i(TAG, "onLost,endpointId:"+endpointId);
            mScanEndpointMap.remove(endpointId);
            showListView();
        }

        @Override
        public void onFetchAuthCode(String endpointId, String authCode) {
            Log.i(TAG, "onFetchAuthCode() authCode:" + authCode+",endpointId:"+endpointId);
            // To display AuthCode
            String toDisplay = mContext.getString(R.string.auth_code) + authCode;
            mAuthCodeTextView.setText(toDisplay);
        }

        @Override
        public void onWifiShareResult(String endpointId, int statusCode) {
            Log.i(TAG, "-------onWifiShareResult: " + endpointId + ", statusCode: " + statusCode+",statusStr:"+StatusCode.getStatusCode(statusCode));
            mWiFiShareEngine.stopWifiShare();
        }
    };

    public void setViewToFill(ListView listView, TextView textView) {
        mNearbyDevicesListView = listView;
        mAuthCodeTextView = textView;
    }

    SimpleAdapter mSimpleAdapter;
    List<HashMap<String, Object>> data = new ArrayList<>();
    /**
     * To show onFound devices, and select a device to share WiFi
     */
    private void showListView() {
        data.clear();
        for (Map.Entry<String, ScanEndpointInfo> entry: mScanEndpointMap.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("id", entry.getKey());
            item.put("name", entry.getValue().getName());
            data.add(item);
        }
        if (mSimpleAdapter == null) {
            mSimpleAdapter = new SimpleAdapter(mContext, data, R.layout.item,
                    new String[]{"id", "name"}, new int[]{R.id.itemId, R.id.itemName});
            mNearbyDevicesListView.setAdapter(mSimpleAdapter);
            mNearbyDevicesListView.setOnItemClickListener((parent, view, position, id) -> {
                HashMap<String, Object> item = (HashMap<String, Object>) mNearbyDevicesListView.getItemAtPosition(position);
                String endpointId = (String) item.get("id");
                Log.e(TAG, "ListView on click listener Share WiFi to endpoint: " + endpointId);
                mWiFiShareEngine.shareWifiConfig(endpointId);
            });
        }else {
            mSimpleAdapter.notifyDataSetChanged();
        }
    }

}
