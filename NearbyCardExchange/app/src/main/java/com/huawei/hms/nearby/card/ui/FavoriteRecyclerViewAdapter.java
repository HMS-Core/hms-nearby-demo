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

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;
import com.huawei.hms.nearby.card.model.Constants;
import com.huawei.hms.nearby.card.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Favorite recycler view adapter.
 *
 * @since 2020-06-05
 */
public class FavoriteRecyclerViewAdapter
        extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.FavoriteRecyclerViewHolder>
        implements View.OnClickListener {
    private List<CardInfo> mItemList;
    private SharedPreferences mSharedPreferences;
    private OnClickListener mListener;

    public FavoriteRecyclerViewAdapter(SharedPreferences sharedPreferences, OnClickListener listener) {
        mItemList = new ArrayList<>();
        mSharedPreferences = sharedPreferences;
        mListener = listener;
    }

    @NonNull
    @Override
    public FavoriteRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        return new FavoriteRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteRecyclerViewHolder holder, int position) {
        if (mItemList == null || mItemList.isEmpty() || mItemList.size() <= position) {
            return;
        }

        CardInfo cardInfo = mItemList.get(position);
        if (cardInfo == null) {
            return;
        }

        holder.mPersionImageView.setImageResource(cardInfo.getPersionResourceId());
        holder.mNameTextView.setText(cardInfo.getName());
        holder.mJobTypeTextView.setText(cardInfo.getJobType());
        if (!TextUtils.isEmpty(cardInfo.getCompany())) {
            holder.mCompanyTextView.setVisibility(View.VISIBLE);
            holder.mCompanyTextView.setText(cardInfo.getCompany());
        } else {
            holder.mCompanyTextView.setVisibility(View.GONE);
            holder.mCompanyTextView.setText("");
        }
        holder.mDeleteImageView.setTag(position);
        holder.mDeleteImageView.setOnClickListener(this);
        holder.mMainLayout.setTag(position);
        holder.mMainLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {
            ImageView favoriteImageView = (ImageView) view;
            int position = (int) favoriteImageView.getTag();
            CardInfo cardInfo = mItemList.get(position);
            removeCardInfo(cardInfo);
            return;
        }

        if (view instanceof LinearLayout) {
            LinearLayout mainLayout = (LinearLayout) view;
            int position = (int) mainLayout.getTag();
            CardInfo cardInfo = mItemList.get(position);
            mListener.onClick(cardInfo);
        }
    }

    /**
     * Update the recycler view item.
     *
     * @param collection The collection of CardInfo.
     */
    public void updateItemList(Collection<CardInfo> collection) {
        mItemList.clear();
        mItemList.addAll(collection);
        notifyDataSetChanged();
    }

    private void removeCardInfo(CardInfo cardInfo) {
        mItemList.remove(cardInfo);
        Set<String> set = new HashSet<>(mItemList.size());
        for (CardInfo card : mItemList) {
            set.add(JsonUtils.object2Json(card));
        }
        mSharedPreferences.edit().putStringSet(Constants.MY_FAVORITES_KEY, set).apply();
        notifyDataSetChanged();
    }

    /**
     * On click listener.
     */
    public interface OnClickListener {
        /**
         * On click.
         *
         * @param cardInfo The card info which is clicked.
         */
        void onClick(CardInfo cardInfo);
    }

    static class FavoriteRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPersionImageView;
        private TextView mNameTextView;
        private TextView mJobTypeTextView;
        private TextView mCompanyTextView;
        private ImageView mDeleteImageView;
        private LinearLayout mMainLayout;

        public FavoriteRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            mPersionImageView = itemView.findViewById(R.id.persion);
            mNameTextView = itemView.findViewById(R.id.name);
            mJobTypeTextView = itemView.findViewById(R.id.job_type);
            mCompanyTextView = itemView.findViewById(R.id.company);
            mDeleteImageView = itemView.findViewById(R.id.delete);
            mMainLayout = itemView.findViewById(R.id.main_layout);
        }
    }
}