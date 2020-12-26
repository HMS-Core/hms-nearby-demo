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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.presenter.GroupChatPresenter;
import com.huawei.hms.nearby.im.ui.adapter.ChatAdapter;
import com.huawei.hms.nearby.im.view.IGroupChatView;

public class GroupChatActivity extends BaseActivity<GroupChatPresenter> implements IGroupChatView {

    public static final String ARG_GROUP_ID = "groupId";
    private Button sendButton;
    private EditText messageSendEt;
    private ListView mListView;
    private ChatAdapter mChatAdapter;
    private View sendProgressView;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        initView();
        requestPermissions();
    }

    @Override
    protected GroupChatPresenter initPresenter() {
        return new GroupChatPresenter(mContext,this);
    }

    private void initView() {
        groupId = getIntent().getStringExtra(ARG_GROUP_ID);
        messageSendEt = findViewById(R.id.et_messageSend);
        sendButton = findViewById(R.id.btn_send);
        mListView = findViewById(R.id.lv_chat);
        mChatAdapter = new ChatAdapter(this);
        mListView.setAdapter(mChatAdapter);
        sendProgressView = findViewById(R.id.view_sendProgress);
        TextView titleTv = (TextView) findViewById(R.id.tv_actionBarTitle);
        if (TextUtils.isEmpty(groupId)) {
            titleTv.setText(R.string.group_chat_with_nearby);
        }else {
            titleTv.setText("Group chat(" + groupId + ")");
        }
        sendButton.setOnClickListener(view -> {
            String sendContent = messageSendEt.getText().toString().trim();
            if (TextUtils.isEmpty(sendContent)) {
                Toast.makeText(mContext, R.string.content_empty_tip, Toast.LENGTH_SHORT).show();
                return;
            }
            sendProgressView.setVisibility(View.VISIBLE);
            mPresenter.broadcastMessage(groupId,sendContent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.findMessage(groupId);
    }

    /**
     * new message coming
     * @param messageBean
     */
    @Override
    public void onMessageFound(MessageBean messageBean) {
        mChatAdapter.addItem(messageBean);
    }

    /**
     * the result of sendMessage
     * @param isSucceed true:send succeed;  false:failed
     * @param item
     */
    @Override
    public void onMsgSendResult(boolean isSucceed, MessageBean item) {
        sendProgressView.setVisibility(View.GONE);
        if (isSucceed) {
            Toast.makeText(mContext, R.string.send_succeed, Toast.LENGTH_SHORT).show();
            mChatAdapter.addItem(item);
            messageSendEt.setText("");
        }else {
            Toast.makeText(mContext, R.string.send_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
