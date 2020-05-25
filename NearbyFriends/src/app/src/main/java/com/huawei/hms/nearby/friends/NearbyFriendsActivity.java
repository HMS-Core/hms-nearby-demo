/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.nearby.friends;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.nearby.Nearby;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.friends.constant.Constants;
import com.huawei.hms.nearby.friends.utils.BluetoothCheckUtil;
import com.huawei.hms.nearby.friends.utils.GpsCheckUtil;
import com.huawei.hms.nearby.friends.utils.NetCheckUtil;
import com.huawei.hms.nearby.friends.utils.permission.PermissionHelper;
import com.huawei.hms.nearby.friends.utils.permission.PermissionInterface;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutOption;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Nearby Friends Main Activity
 *
 * @since 2019-12-13
 */
public class NearbyFriendsActivity extends AppCompatActivity implements View.OnClickListener, PermissionInterface {
    /**
     * Chart type
     */
    private static final String CHART_TYPE = "chart";
    /**
     * Message namespace
     */
    private static final String MESSAGE_NAMESPACE = "message";
    /**
     * Separator
     */
    private static final String SEPARATOR = "!%%!";
    /**
     * The second of six hour
     */
    private static final int SIX_HOUR = 21600;
    /**
     * Colon
     */
    private static final String COLON = ":";
    /**
     * Class Tag
     */
    private static final String TAG = "NearbyFriendsActivity";
    private static final int REQUEST_CODE = 8488;

    private Context mContext;
    private GifImageView scanGiv;
    private GifDrawable gifDrawable;
    private TextView scanTipTv;
    private TextView nameTv;
    private Button scanBtn;
    private ListView friendsListView;
    private FriendsListAdapter mAdapter;
    private boolean isScanning;
    private String nickNameStr;
    private MessageEngine engine;
    private Message nameMessage;
    private PermissionHelper mPermissionHelper;
    private LinkedList<Custom> mDatas = new LinkedList<Custom>();
    private Map<String, Message> messageMap = new HashMap<>();
    private List<String> nickNameList = new ArrayList<>();
    private Map<String, ArrayList<String>> chatContentMap = new HashMap<>();
    private PutOption putOption;
    private MessageHandler messageHandler;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(this, this);
        if (!NetCheckUtil.isNetworkAvailable(this)) {
            showWarnDialog(Constants.NETWORK_ERROR);
            return;
        }
        if (!BluetoothCheckUtil.isBlueEnabled()) {
            showWarnDialog(Constants.BLUETOOTH_ERROR);
            return;
        }
        if (!GpsCheckUtil.isGpsEnabled(this)) {
            showWarnDialog(Constants.GPS_ERROR);
            return;
        }
        mContext = this;
        engine = Nearby.getMessageEngine(this);
        setContentView(R.layout.activity_nearby_friends);
        initVariable();
        initView();
        showNicknameDialog();
    }

    private void initVariable() {
        Policy policy = new Policy.Builder().setTtlSeconds(SIX_HOUR).build();
        putOption = new PutOption.Builder().setPolicy(policy).build();
    }

    private void requestPermissions(Activity activity, PermissionInterface permissionInterface) {
        mPermissionHelper = new PermissionHelper(activity, permissionInterface);
        mPermissionHelper.requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (nameMessage != null) {
            engine.unput(nameMessage);
            chatContentMap.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (nameMessage != null) {
            engine = Nearby.getMessageEngine(this);
            engine.put(nameMessage, putOption);
            chatContentMap.clear();
            if (isScanning) {
                startScan();
            }
        }
    }

    private void showWarnDialog(String content) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warn);
        builder.setIcon(R.mipmap.warn);
        builder.setMessage(content);
        builder.setNegativeButton(R.string.btn_confirm, onClickListener);
        builder.show();
    }

    private void initView() {
        intScanGiv();
        scanTipTv = findViewById(R.id.tv_bein_scan_tip);
        scanTipTv.setText(R.string.begin_scan_tip);
        nameTv = findViewById(R.id.tv_my_name);
        scanBtn = findViewById(R.id.btn_scan);
        scanBtn.setText(R.string.btn_begin_scan);
        scanBtn.setOnClickListener(this);
        friendsListView = findViewById(R.id.lv_friend_list);
        mAdapter = new FriendsListAdapter(mDatas, mContext);
        friendsListView.setAdapter(mAdapter);
    }

    private void intScanGiv() {
        scanGiv = findViewById(R.id.giv_scan);
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.scan);
            scanGiv.setImageDrawable(gifDrawable);
            gifDrawable.stop();
        } catch (IOException e) {
            Log.e(TAG, "IntScanGiv error!", e);
        }
    }

    private void showNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_input_nickname, null, false);
        builder.setView(view);

        final Dialog dialog = builder.create();
        final EditText nicknameEdt = (EditText) view.findViewById(R.id.et_input_nickname);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm_nickname);
        confirm.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(nicknameEdt.getText())) {
                    Toast.makeText(mContext, R.string.nickname_empty_tip, Toast.LENGTH_SHORT).show();
                    return;
                } else if (nicknameEdt.getText().toString().contains(SEPARATOR)) {
                    Toast.makeText(mContext, R.string.nickname_invalid_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                nickNameStr = nicknameEdt.getText().toString();

                try {
                    nameMessage = new Message(nickNameStr.getBytes("UTF-8"), CHART_TYPE, MESSAGE_NAMESPACE);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "nickNameStr getBytes error", e);
                }
                messageMap.put(nickNameStr, nameMessage);
                Task<Void> task = engine.put(nameMessage, putOption);
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Login failed:", e);
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int errorStatusCode = apiException.getStatusCode();
                            if (errorStatusCode == StatusCode.STATUS_MESSAGE_AUTH_FAILED) {
                                Toast.makeText(mContext, R.string.configuration_error, Toast.LENGTH_SHORT).show();
                            } else if (errorStatusCode == StatusCode.STATUS_MESSAGE_APP_UNREGISTERED) {
                                Toast.makeText(mContext, R.string.permission_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, R.string.login_success, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        nameTv.setText("Hello, " + nickNameStr);
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startScan() {
        isScanning = true;
        gifDrawable.start();
        MessagePicker picker = new MessagePicker.Builder()
                .includeNamespaceType(MESSAGE_NAMESPACE, CHART_TYPE)
                .build();
        Policy policy = new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_INFINITE).build();
        GetOption getOption = new GetOption.Builder().setPicker(picker).setPolicy(policy).build();
        messageHandler = new MessageHandler() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                doOnFound(message);
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                doOnLost(message);
            }
        };
        engine.get(messageHandler, getOption);
    }

    private void stopScan() {
        isScanning = false;
        gifDrawable.stop();
        engine.unget(messageHandler);
    }

    private void doOnLost(Message message) {
        if (message == null || message.getContent() == null) {
            return;
        }
        String contentStr = new String(message.getContent());
        Log.i(TAG, message.getType() + COLON + contentStr);

        if (contentStr.contains(SEPARATOR)) {
            String[] contentArray = contentStr.split(SEPARATOR);
            if (contentArray.length != 3) {
                return;
            }
            String src = contentArray[0];
            String des = contentArray[1];
            String msg = contentArray[2];
            if (nickNameStr == null || !nickNameStr.equals(des)) {
                return;
            }

            for (String friend : nickNameList) {
                if (friend.equals(src)) {
                    if (chatContentMap.containsKey(friend)) {
                        String chartMessage = src + COLON + msg;
                        chatContentMap.get(friend).remove(chartMessage);
                    }
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            if (!nickNameList.contains(contentStr)) {
                return;
            }
            nickNameList.remove(contentStr);
            Iterator<Custom> mDatasIterator = mDatas.iterator();
            while (mDatasIterator.hasNext()) {
                Custom custom = mDatasIterator.next();
                if (contentStr.equalsIgnoreCase(custom.getNickName())) {
                    mDatasIterator.remove();
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Master, " + contentStr + " is far away!", Toast.LENGTH_SHORT).show();
                    mAdapter.setData(mDatas);
                }
            });
        }
    }

    private void doOnFound(Message message) {
        final String contentStr = new String(message.getContent());
        if (message == null || contentStr == null || "".equals(contentStr)) {
            return;
        }
        Log.i(TAG, new String(message.getContent()));

        if (contentStr.contains(SEPARATOR)) {
            String[] contentArray = contentStr.split(SEPARATOR);
            if (contentArray.length != 3) {
                return;
            }
            String src = contentArray[0];
            String des = contentArray[1];
            String msg = contentArray[2];
            if (nickNameStr == null || !nickNameStr.equals(des)) {
                return;
            }

            for (String friend : nickNameList) {
                if (friend.equals(src)) {
                    String chartMessage = src + COLON + msg;
                    if (chatContentMap.containsKey(friend)) {
                        chatContentMap.get(friend).add(chartMessage);
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(chartMessage);
                        chatContentMap.put(friend, list);
                    }
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        } else {
            if (nickNameList.contains(contentStr)) {
                return;
            }
            nickNameList.add(contentStr);
            mDatas.add(new Custom(contentStr, "Nearby just now!", R.mipmap.ic_launcher_round));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setData(mDatas);
                    Toast.makeText(mContext, "Master, I found " + contentStr + " nearby!", Toast.LENGTH_SHORT).show();
                }
            });

            if (!chatContentMap.containsKey(contentStr)) {
                chatContentMap.put(contentStr, new ArrayList<String>());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan: {
                if (isScanning) {
                    stopScan();
                    scanBtn.setText(R.string.btn_begin_scan);
                    scanTipTv.setText(R.string.begin_scan_tip);
                } else {
                    startScan();
                    scanBtn.setText(R.string.btn_stop_scan);
                    scanTipTv.setText(R.string.scanning_tip);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public int getPermissionsRequestCode() {
        return REQUEST_CODE;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
    }

    @Override
    public void requestPermissionsSuccess() {
    }

    @Override
    public void requestPermissionsFail() {
        finish();
    }
}
