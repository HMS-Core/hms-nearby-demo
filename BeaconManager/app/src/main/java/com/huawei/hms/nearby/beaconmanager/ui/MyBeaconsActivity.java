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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.huawei.hms.nearby.beaconmanager.R;
import com.huawei.hms.nearby.beaconmanager.beaconbase.BeaconListQuery;
import com.huawei.hms.nearby.beaconmanager.beaconbase.model.BeaconBriefInfo;

import java.util.ArrayList;

/**
 * MyBeaconsActivity
 *
 * @since 2019-11-14
 */
public class MyBeaconsActivity extends BaseActivity {
    private SwipeRefreshLayout refreshLayout;

    private MyBeaconsAdapter mAdapter;

    private TextView noBeaconTv;

    private BeaconListQuery beaconListQuery;

    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private LinearLayout footerView;

    private boolean isRefreshing;

    private boolean isLondingMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_beacons);
        setTitle(R.string.my_beacons_title);
        beaconListQuery = new BeaconListQuery();
        isRefreshing = false;
        isLondingMore = false;
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        reQueryMyAllBeacons();
    }

    private void initView() {
        noBeaconTv = findViewById(R.id.tv_no_beacon);
        noBeaconTv.setVisibility(View.VISIBLE);
        refreshLayout = findViewById(R.id.mybeacon_refresh_layout);
        refreshLayout.measure(0, 0);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing || isLondingMore) {
                    refreshLayout.setRefreshing(false);
                    return;
                }
                isRefreshing = true;
                reQueryMyAllBeacons();
            }
        });

        mAdapter = new MyBeaconsAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.mybeacon_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isLondingMore || isRefreshing) {
                    return;
                }
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                int lastItem = layoutManager.findLastVisibleItemPosition();
                if (lastItem + 1 == mAdapter.getItemCount()) {
                    isLondingMore = true;
                    footerView.setVisibility(View.VISIBLE);
                    postQueryReq();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        footerView = findViewById(R.id.mybeacon_footerView);
        footerView.setVisibility(View.GONE);
    }

    private void reQueryMyAllBeacons() {
        beaconListQuery.reStart();
        mAdapter.clearDatas();
        postQueryReq();
    }

    private void postQueryReq() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAndShowMyAllBeacons();
                    }
                });
            }
        }).start();
    }

    private void getAndShowMyAllBeacons() {
        if (!beaconListQuery.hasMoreBeacon()) {
            Toast.makeText(this, "No more beacon.", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<BeaconBriefInfo> pageBeaconBriefInfos = beaconListQuery.nextPage();
            mAdapter.addDatas(pageBeaconBriefInfos);
        }
        if (mAdapter.getItemCount() == 0) {
            noBeaconTv.setVisibility(View.VISIBLE);
        } else {
            noBeaconTv.setVisibility(View.GONE);
        }

        refreshLayout.setRefreshing(false);
        footerView.setVisibility(View.GONE);
        isLondingMore = false;
        isRefreshing = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.AC_OP_VIEW_BEACON: {
                if (data != null) {
                    handleEditBeacon(resultCode, data);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleEditBeacon(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        int pos = data.getIntExtra(Constant.BEACONS_LIST_POSTION, -1);
        mAdapter.removeData(pos);
    }
}
