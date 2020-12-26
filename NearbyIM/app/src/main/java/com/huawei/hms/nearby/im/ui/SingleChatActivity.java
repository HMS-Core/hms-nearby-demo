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
package com.huawei.hms.nearby.im.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.Custom;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.presenter.NearbyConnectionPresenter;
import com.huawei.hms.nearby.im.ui.adapter.ChatAdapter;
import com.huawei.hms.nearby.im.ui.adapter.FriendsListAdapter;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.FileUtil;
import com.huawei.hms.nearby.im.view.INearbyConnectionView;
import com.huawei.hms.nearby.transfer.Data;
import java.util.LinkedList;

public class SingleChatActivity extends BaseActivity<NearbyConnectionPresenter> implements
        INearbyConnectionView, View.OnTouchListener, View.OnClickListener {
    private static final int REQUEST_OPEN_DOCUMENT = 20;
    private ImageView spreadMenuImageView;
    private Button sendButton;
    private EditText sendMsgEt;
    private View menuView;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ListView userListView;
    private FriendsListAdapter userAdapter;
    private View viewScan;
    private View viewChat;
    private View view_connectProgress;
    private Custom selectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        initView();
        mPresenter.findNearbyPeople();
    }

    @Override
    protected NearbyConnectionPresenter initPresenter() {
        return new NearbyConnectionPresenter(mContext,this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        ((TextView) findViewById(R.id.tv_actionBarTitle)).setText("Private Chat");
        spreadMenuImageView = findViewById(R.id.iv_spreadMenu);
        sendButton = findViewById(R.id.btn_send);
        sendMsgEt = findViewById(R.id.et_messageSend);
        menuView = findViewById(R.id.ll_funcMenu);
        chatListView = findViewById(R.id.lv_chat);
        chatAdapter = new ChatAdapter(mContext);
        chatListView.setAdapter(chatAdapter);
        sendMsgEt.setOnTouchListener(this);
        viewScan = findViewById(R.id.viewScan);
        viewChat = findViewById(R.id.viewChat);
        viewScan.setVisibility(View.VISIBLE);
        viewChat.setVisibility(View.GONE);
        view_connectProgress = findViewById(R.id.view_connectProgress);
        userListView = findViewById(R.id.lv_userList);
        userAdapter = new FriendsListAdapter(this);
        userListView.setAdapter(userAdapter);
        spreadMenuImageView.setOnClickListener(view -> menuView.setVisibility(View.VISIBLE));
        sendMsgEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                if (sequence.length() > 0) {
                    sendButton.setVisibility(View.VISIBLE);
                    spreadMenuImageView.setVisibility(View.GONE);
                }else {
                    sendButton.setVisibility(View.GONE);
                    spreadMenuImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        sendButton.setOnClickListener(this);
        findViewById(R.id.iv_selectPicture).setOnClickListener(this);
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            /**
             * requestConnect
             */
            selectUser = userAdapter.getItem(position);
            mPresenter.requestConnect(selectUser.getEndpointId());
            view_connectProgress.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        menuView.setVisibility(View.GONE);
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String msgStr = sendMsgEt.getText().toString().trim();
                /**
                 * send message
                 */
                MessageBean messageBean = mPresenter.sendMessage(msgStr);
                chatAdapter.addItem(messageBean);
                sendMsgEt.setText("");
                break;
            case R.id.iv_selectPicture:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                this.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
                break;
        }
    }

    /**
     * Select a file and begin to send it.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            /**
             * send file
             */
            Data filePayload = mPresenter.sendFile(uri);

            if (filePayload != null) {
                String fileName = FileUtil.getFileRealNameFromUri(this, uri);
                MessageBean item = new MessageBean();
                item.setUserName(CommonUtil.userName);
                item.setType(FileUtil.isImage(fileName)?MessageBean.TYPE_SEND_IMAGE:MessageBean.TYPE_SEND_FILE);
                item.setSending(true);
                item.setFileUri(uri);
                item.setFileName(fileName);
                item.setTotalBytes(filePayload.asFile().getSize());
                item.setPayloadId(filePayload.getId());
                addFileItem(item);
            }
        }
    }

    @Override
    public void addFileItem(MessageBean item) {
        chatAdapter.addItem(item);
    }

    /**
     * receiveMessage
     * @param item
     */
    @Override
    public void receiveMessage(MessageBean item) {
        item.setType(MessageBean.TYPE_RECEIVE_TEXT);
        chatAdapter.addItem(item);
    }

    /**
     * User be found
     * @param endpointId
     * @param discoveryEndpointInfo
     */
    @Override
    public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
        Custom custom = new Custom(discoveryEndpointInfo.getName(), endpointId, R.mipmap.icon_message_person);
        custom.setEndpointId(endpointId);
        userAdapter.addItem(custom);
        ((TextView) findViewById(R.id.tv_hint)).setText(R.string.select_a_user_to_communicate);
    }

    /**
     * User lost
     * @param endpointId
     */
    @Override
    public void onLost(String endpointId) {
        LinkedList<Custom> data = userAdapter.getData();
        Custom target = null;
        for (int i = 0; i < data.size(); i++) {
            Custom custom = data.get(i);
            if (custom.getEndpointId().equals(endpointId)) {
                target = custom;
                break;
            }
        }
        userAdapter.removeItem(target);
    }

    /**
     * The connection has been established
     * @param endpointId
     */
    @Override
    public void onEstablish(String endpointId) {
        viewScan.setVisibility(View.GONE);
        viewChat.setVisibility(View.VISIBLE);
        Custom target = selectUser;
        if (selectUser == null) {
            LinkedList<Custom> data = userAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                Custom custom = data.get(i);
                if (endpointId.equals(custom.getEndpointId())) {
                    target = custom;
                    break;
                }
            }
        }
        if(target != null){
            ((TextView) findViewById(R.id.tv_actionBarTitle)).setText("Private chat with "+target.getNickName());
        }
    }

    /**
     * File download and upload progress
     * @param transferredBytes
     * @param totalBytes
     * @param progress
     * @param payloadId
     * @param isSending
     */
    @Override
    public void updateProgress(long transferredBytes, long totalBytes,
                                int progress, long payloadId, boolean isSending) {
        LinkedList<MessageBean> msgList = chatAdapter.getData();
        for (MessageBean item : msgList) {
            if (item.getPayloadId() == payloadId) {
                item.setTransferredBytes(transferredBytes);
                item.setTotalBytes(totalBytes);
                item.setSending(isSending);
                item.setProgress(progress);
                chatAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}