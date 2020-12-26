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

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity implements View.OnClickListener{

    private WiFiShareHelper mWiFiShare;
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
    ListView listView;
    TextView tv_authCodeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mWiFiShare = new WiFiShareHelper(this);
        mWiFiShare.setViewToFill(listView, tv_authCodeText);
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
    }

    private void initView() {
        listView = findViewById(R.id.listView);
        tv_authCodeText = findViewById(R.id.authCodeText);
        findViewById(R.id.button_share_wifi).setOnClickListener(this);
        findViewById(R.id.button_connect_wifi).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_share_wifi:
                mWiFiShare.shareWiFiConfig();
                break;
            case R.id.button_connect_wifi:
                mWiFiShare.requestWiFiConfig();
                break;
        }
    }

}
