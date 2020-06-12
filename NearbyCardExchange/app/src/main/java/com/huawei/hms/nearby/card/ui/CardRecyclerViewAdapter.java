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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Card recycler view adapter.
 *
 * @since 2020-06-05
 */
public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.CardRecyclerViewHolder>
        implements View.OnClickListener {
    private List<CardInfo> mItemList;
    private OnFavoriteListener mListener;

    public CardRecyclerViewAdapter(OnFavoriteListener listener) {
        mItemList = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public CardRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardRecyclerViewHolder holder, int position) {
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
            holder.mCompanyTextview.setVisibility(View.VISIBLE);
            holder.mCompanyTextview.setText(cardInfo.getCompany());
        } else {
            holder.mCompanyTextview.setVisibility(View.GONE);
            holder.mCompanyTextview.setText("");
        }
        holder.mFavoriteImageView.setImageResource(R.mipmap.star_empty);
        holder.mFavoriteImageView.setTag(R.mipmap.star_empty, position);
        holder.mFavoriteImageView.setTag(R.mipmap.star_fill, false);
        holder.mFavoriteImageView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onClick(View view) {
        if (!(view instanceof ImageView)) {
            return;
        }

        ImageView favoriteImageView = (ImageView) view;
        boolean isFavorite = (boolean) favoriteImageView.getTag(R.mipmap.star_fill);
        int position = (int) favoriteImageView.getTag(R.mipmap.star_empty);
        mListener.onFavorite(mItemList.get(position), !isFavorite);
        favoriteImageView.setTag(R.mipmap.star_fill, !isFavorite);
        if (!isFavorite) {
            favoriteImageView.setImageResource(R.mipmap.star_fill);
        } else {
            favoriteImageView.setImageResource(R.mipmap.star_empty);
        }
    }

    /**
     * Add card info to the recycler view.
     *
     * @param cardInfo CardInfo
     */
    public void addCardInfo(CardInfo cardInfo) {
        mItemList.add(cardInfo);
        notifyDataSetChanged();
    }

    /**
     * Remove card info to the recycler view.
     *
     * @param cardInfo CardInfo
     */
    public void removeCardInfo(CardInfo cardInfo) {
        mItemList.remove(cardInfo);
        notifyDataSetChanged();
    }

    /**
     * On favorite listener.
     */
    public interface OnFavoriteListener {
        /**
         * On favorite.
         *
         * @param cardInfo CardInfo
         * @param isFavorite where the card info is favorite.
         */
        void onFavorite(CardInfo cardInfo, boolean isFavorite);
    }


    static class CardRecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPersionImageView;
        private TextView mNameTextView;
        private TextView mJobTypeTextView;
        private TextView mCompanyTextview;
        private ImageView mFavoriteImageView;

        public CardRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            mPersionImageView = itemView.findViewById(R.id.persion);
            mNameTextView = itemView.findViewById(R.id.name);
            mJobTypeTextView = itemView.findViewById(R.id.job_type);
            mCompanyTextview = itemView.findViewById(R.id.company);
            mFavoriteImageView = itemView.findViewById(R.id.favorite);
        }
    }
}
