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
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.huawei.hms.nearby.im.bean.Custom;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.MessageBean;
import com.huawei.hms.nearby.im.presenter.NearbyPeoplePresenter;
import com.huawei.hms.nearby.im.ui.adapter.FriendsListAdapter;
import com.huawei.hms.nearby.im.utils.CommonUtil;
import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.view.INearbyPeopleView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class NearbyFriendsActivity extends BaseActivity<NearbyPeoplePresenter> implements INearbyPeopleView {

    private GifImageView scanGiv;
    private GifDrawable gifDrawable;
    private TextView scanTipTv;
    private ListView friendsListView;
    private FriendsListAdapter mAdapter;
    private ArrayList<String> nickNameList = new ArrayList<>();
    private TextView userNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_friends);
        initView();
        requestPermissions();
    }

    @Override
    protected NearbyPeoplePresenter initPresenter() {
        return new NearbyPeoplePresenter(mContext,this);
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_actionBarTitle)).setText(R.string.nearby_friends);
        userNameTv = findViewById(R.id.tv_userName);
        scanGiv = findViewById(R.id.giv_scan);
        scanTipTv = findViewById(R.id.tv_bein_scan_tip);
        scanTipTv.setText(R.string.begin_scan_tip);
        friendsListView = findViewById(R.id.lv_friend_list);
        mAdapter = new FriendsListAdapter(mContext);
        friendsListView.setAdapter(mAdapter);
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.scan);
            scanGiv.setImageDrawable(gifDrawable);
            gifDrawable.stop();
        } catch (IOException e) {
            Log.e(Constants.TAG, "IntScanGiv error!", e);
        }
        gifDrawable.start();
        scanTipTv.setText(R.string.scanning_tip);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.findNearbyPeople();
    }

    @Override
    public void onMemberChanged(boolean isLost, MessageBean messageBean) {
        String userName = messageBean.getUserName();
        Custom target = null;
        if (nickNameList.contains(userName)) {
            List<Custom> data = mAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                Custom custom = data.get(i);
                if (messageBean.getUserName().equals(custom.getNickName())) {
                    target = custom;
                    break;
                }
            }
        }
        if (isLost && target != null) { // onLost -> someone left
            if (messageBean.getSendTimeValue() < target.getTimeValue()) {
                Log.e(Constants.TAG, "----this login message is not new, so discard----");
                return;
            }
            Toast.makeText(mContext, userName + " is far away!", Toast.LENGTH_SHORT).show();
            mAdapter.removeItem(target);
            nickNameList.remove(userName);
        } else if(!isLost){ // onFound
            if (!nickNameList.contains(userName)){ // found new people
                Toast.makeText(mContext, "Found " + userName + " nearby!", Toast.LENGTH_SHORT).show();
                Custom custom = new Custom(messageBean.getUserName(), "Just around the corner!", R.mipmap.icon_message_person);
                custom.setTimeValue(messageBean.getSendTimeValue());
                mAdapter.addItem(custom);
                nickNameList.add(userName);
            }else { // update loginTime
                target.setTimeValue(messageBean.getSendTimeValue());
            }
        }
    }

    @Override
    public void onLoginResult(boolean isSucceed, MessageBean item) {
        if (MessageBean.ACTION_TAG_ONLINE.equals(item.getMsg())) {
            userNameTv.setText(isSucceed?"Hi:"+ CommonUtil.userName :"offline");
        }
    }

}
