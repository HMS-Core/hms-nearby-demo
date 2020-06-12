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

package com.huawei.hms.nearby.card.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;
import com.huawei.hms.nearby.card.model.Constants;
import com.huawei.hms.nearby.card.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * My favorites fragment.
 *
 * @since 2020-06-08
 */
public class MyFavoritesFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FavoriteRecyclerViewAdapter mAdapter;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        mAdapter = new FavoriteRecyclerViewAdapter(mSharedPreferences, cardInfo -> {
            Intent intent = new Intent(getContext(), CardActivity.class);
            intent.putExtra(CardInfo.class.getSimpleName(), cardInfo);
            startActivity(intent);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_favorites, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            Set<String> set = mSharedPreferences.getStringSet(Constants.MY_FAVORITES_KEY, new HashSet<>());
            List<CardInfo> list = new ArrayList<>(set.size());
            for (String json : set) {
                list.add(JsonUtils.json2Object(json, CardInfo.class));
            }
            mAdapter.updateItemList(list);
            mSwipeRefreshLayout.setRefreshing(false);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        Set<String> set = mSharedPreferences.getStringSet(Constants.MY_FAVORITES_KEY, new HashSet<>());
        List<CardInfo> list = new ArrayList<>(set.size());
        for (String json : set) {
            list.add(JsonUtils.json2Object(json, CardInfo.class));
        }
        mAdapter.updateItemList(list);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
