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
package com.huawei.hms.nearby.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.nearby.im.ui.BaseActivity;
import com.huawei.hms.nearby.im.ui.ChatRecordTransferActivity;
import com.huawei.hms.nearby.im.ui.CreateGroupActivity;
import com.huawei.hms.nearby.im.ui.GroupChatActivity;
import com.huawei.hms.nearby.im.ui.NearbyFriendsActivity;
import com.huawei.hms.nearby.im.ui.SingleChatActivity;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.utils.NetCheckUtil;

import java.util.Random;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        requestPermissions();
    }

    private void initView() {
        findViewById(R.id.btn_nearbyFriends).setOnClickListener(this);
        findViewById(R.id.btn_createGroup).setOnClickListener(this);
        findViewById(R.id.btn_nearbyFriendsChat).setOnClickListener(this);
        findViewById(R.id.btn_singleChat).setOnClickListener(this);
        findViewById(R.id.btn_getChatRecord).setOnClickListener(this);

        /**
         *  Simulate an account for test
         */
        CommonUtil.userName = Constants.NICKNAMES[new Random().nextInt(10)]+String.valueOf(System.currentTimeMillis()).substring(5);
        TextView userNameTv = findViewById(R.id.tv_userName);
        userNameTv.setText(CommonUtil.userName);
        userNameTv.setTextColor(getResources().getColor(R.color.color_aaa));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_nearbyFriends:
                if (!NetCheckUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext,R.string.no_network,Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(mContext, NearbyFriendsActivity.class));
                break;
            case R.id.btn_createGroup:
                if (!NetCheckUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext,R.string.no_network,Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(mContext, CreateGroupActivity.class));
                break;
            case R.id.btn_nearbyFriendsChat:
                if (!NetCheckUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext,R.string.no_network,Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(mContext, GroupChatActivity.class));
                break;
            case R.id.btn_singleChat:
                startActivity(new Intent(mContext, SingleChatActivity.class));
                break;
            case R.id.btn_getChatRecord:
                startActivity(new Intent(mContext, ChatRecordTransferActivity.class));
                break;
        }
    }

}