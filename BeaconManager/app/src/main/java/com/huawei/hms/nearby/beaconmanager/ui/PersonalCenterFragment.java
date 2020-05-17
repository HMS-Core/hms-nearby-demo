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

package com.huawei.hms.nearby.beaconmanager.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconBaseLog;
import com.huawei.hms.nearby.beaconmanager.beaconbase.ServiceAccountSignInClient;
import com.huawei.hms.nearby.beaconmanager.beaconbase.restfulapi.BeaconRestfulClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * PersonalCenterFragment
 *
 * @since 2019-11-14
 */
public class PersonalCenterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = PersonalCenterFragment.class.getSimpleName();

    private TextView tvAccount;

    public PersonalCenterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RelativeLayout myAccount = view.findViewById(R.id.rl_head);
        ImageView lognIn = view.findViewById(R.id.iv_head);
        myAccount.setOnClickListener(this);
        lognIn.setOnClickListener(this);
        RelativeLayout allBeaconsLayout = view.findViewById(R.id.rl_all_beacons);
        allBeaconsLayout.setOnClickListener(this);
        tvAccount = view.findViewById(R.id.tv_account);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_head:
            case R.id.iv_head: {
                chooseSeviceAccountKeyFile();
                break;
            }
            case R.id.rl_all_beacons: {
                showMyAllBeacons();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.AC_OP_SIGN_IN: {
                if (data != null) {
                    seviceAccountKeyFileChooseResult(resultCode, data);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.AC_OP_REQUEST_STORAGE_PERMISSIONS: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Need EXTERNAL_STORAGE permission.", Toast.LENGTH_SHORT).show();
                    return;
                }
                chooseSeviceAccountKeyFile();
                break;
            }
            default: {
                break;
            }
        }
    }

    private boolean checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permission, Constant.AC_OP_REQUEST_STORAGE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showMyAllBeacons() {
        if (!BeaconRestfulClient.getInstance().isAccessTokenSet()) {
            MainActivity.showAlertDialog(getActivity(), "Warning", "Please Login Service Account First!");
            return;
        }
        Intent intent = new Intent(getActivity(), MyBeaconsActivity.class);
        startActivity(intent);
    }

    private void chooseSeviceAccountKeyFile() {
        if (!checkPermission()) {
            return;
        }
        /*
         * Call the system API of the file selector to get the service account file.
         * Service Account file（***private.json）contains private key for logging into Nearby service.
         * Developer should provide the measure to protect the file from being tampered with.
         */
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select Your Service Account Private Key Json File"),
            Constant.AC_OP_SIGN_IN);
    }

    private void seviceAccountKeyFileChooseResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        /*
         * Read service account file, and call the API from ServiceAccountSignInClient class to login and get the JWT.
         * Service Account file（***private.json）contains private key for logging into Nearby service.
         * Developer should provide the measure to protect the file from being tampered with.
         */
        Uri uri = data.getData();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            BeaconBaseLog.e(TAG, e.getMessage());
        }
        String serviceAccountKey = stringBuilder.toString();
        if ((serviceAccountKey == null) || (serviceAccountKey.length() == 0)) {
            Toast.makeText(getActivity(), "Read Service Account File Error!", Toast.LENGTH_SHORT).show();
            return;
        }
        loginServiceAccountResult(serviceAccountKey);
        return;
    }

    private void loginServiceAccountResult(String serviceAccountKey) {
        ServiceAccountSignInClient signInClient = ServiceAccountSignInClient.buildFromJsonData(serviceAccountKey);
        if (signInClient.signIn() != 0) {
            return;
        }
        BeaconRestfulClient.getInstance().setAccessToken(signInClient.getJwt());
        tvAccount.setText(signInClient.getIssuer());
        BeaconRestfulClient.getInstance().setAccessTokenTimeExpiredCallback(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                tvAccount.setText("Please Select Your Service Account Private Key File To Login");
                MainActivity.showAlertDialog(getActivity(), "Warning",
                    "Access token session time expired!\n" + "Please login your service account.");
            });
        });
    }
}
