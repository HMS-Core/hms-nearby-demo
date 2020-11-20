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
package com.huawei.hms.nearby.im.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.view.INearbyPeopleView;

public class NearbyPeoplePresenter extends BasePresenter<INearbyPeopleView>{


    private final NearbyAgent nearbyAgent;

    public NearbyPeoplePresenter(Context mContext, INearbyPeopleView view) {
        super(mContext, view);
        nearbyAgent = new NearbyAgent(mContext, new NearbyAgent.INearbyMessageView() {

            @Override
            public void onMessageFound(MessageBean messageBean) {
                view.onMemberChanged(false,messageBean);
            }

            @Override
            public void onMessageLost(MessageBean messageBean) {
                view.onMemberChanged(true,messageBean);
            }

            @Override
            public void onMsgSendResult(boolean isSucceed, MessageBean item) {
                view.onLoginResult(isSucceed,item);
            }
        });
    }

    public void findNearbyPeople() {
        nearbyAgent.broadcastMessage(null,MessageBean.ACTION_TAG_ONLINE);
        nearbyAgent.startScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        nearbyAgent.stopScan();
    }
}
