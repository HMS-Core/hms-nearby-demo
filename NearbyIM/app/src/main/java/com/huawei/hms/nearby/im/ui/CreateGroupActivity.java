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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.Custom;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.presenter.CreateGroupPresenter;
import com.huawei.hms.nearby.im.ui.adapter.GroupMemberAdapter;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.utils.NetCheckUtil;
import com.huawei.hms.nearby.im.view.ICreateGroupView;
import java.util.LinkedList;

public class CreateGroupActivity extends BaseActivity<CreateGroupPresenter> implements ICreateGroupView {

    private EditText ed_inputPwd;
    private TextView tv_pwd;
    private GridView mGridView;
    private GroupMemberAdapter mAdapter;
    private View group1,group2;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
        requestPermissions();
    }

    @Override
    protected CreateGroupPresenter initPresenter() {
        return new CreateGroupPresenter(mContext,this);
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_actionBarTitle)).setText(R.string.face_to_face_build_group);
        ed_inputPwd = findViewById(R.id.ed_inputPwd);
        ed_inputPwd.setFocusable(true);
        ed_inputPwd.requestFocus();
        tv_pwd = findViewById(R.id.tv_pwd);
        mGridView = findViewById(R.id.gridView);
        group1 = findViewById(R.id.groupView1);
        group2 = findViewById(R.id.groupView2);
        group2.setVisibility(View.GONE);
        findViewById(R.id.btn_enterGroupChat).setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GroupChatActivity.class);
            intent.putExtra(GroupChatActivity.ARG_GROUP_ID,groupId);
            startActivity(intent);
        });
    }

    @Override
    public void requestPermissionsSuccess() {
        mAdapter = new GroupMemberAdapter(mContext);
        mGridView.setAdapter(mAdapter);
        ed_inputPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    if (!NetCheckUtil.isNetworkAvailable(mContext)) {
                        Toast.makeText(mContext,R.string.no_network,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    groupId = s.toString();
                    tv_pwd.setText(groupId);
                    group1.setVisibility(View.GONE);
                    group2.setVisibility(View.VISIBLE);

                    mPresenter.joinGroup(groupId);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onPeopleFound(MessageBean messageBean) {
        String userName = messageBean.getUserName();
        LinkedList<Custom> customs = mAdapter.getData();
        for (int i = 0; i < customs.size(); i++) {
            if (customs.get(i).getNickName().equals(userName)) {
                Log.i(Constants.TAG,"the user have existed");
                return;
            }
        }
        Toast.makeText(mContext, "Found " + messageBean.getUserName() + " nearby!", Toast.LENGTH_SHORT).show();
        mAdapter.addItem(new Custom(userName, R.mipmap.icon_message_person));
    }

    @Override
    public void onJoinGroupResult(boolean isSucceed, MessageBean item) {
        if (isSucceed) {
            Toast.makeText(mContext, R.string.create_group_succeed, Toast.LENGTH_SHORT).show();
            mAdapter.addItem(new Custom(CommonUtil.userName, R.mipmap.icon_message_person));
        }else {
            Toast.makeText(mContext, R.string.create_group_failed, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}