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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.huawei.hms.nearby.card.R;
import com.huawei.hms.nearby.card.model.CardInfo;
import com.huawei.hms.nearby.card.model.Constants;
import com.huawei.hms.nearby.card.utils.JsonUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Search card dialog fragment.
 *
 * @since 2020-06-05
 */
public class SearchCardDialogFragment extends DialogFragment implements CardRecyclerViewAdapter.OnFavoriteListener {
    private RecyclerView mRecyclerView;
    private CardRecyclerViewAdapter mAdapter;
    private Map<String, CardInfo> mFavoriteMap;
    private OnCloseListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_search_card, null);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CardRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    mFavoriteMap = null;
                    if (mListener != null) {
                        mListener.onClose();
                        mListener = null;
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    mFavoriteMap = null;
                    if (mListener != null) {
                        mListener.onClose();
                        mListener = null;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onFavorite(CardInfo cardInfo, boolean isFavorite) {
        if (isFavorite) {
            mFavoriteMap.put(cardInfo.getId(), cardInfo);
        } else {
            mFavoriteMap.remove(cardInfo.getId());
        }
        Set<String> set = new HashSet<>(mFavoriteMap.size());
        for (CardInfo card : mFavoriteMap.values()) {
            set.add(JsonUtils.object2Json(card));
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(Constants.MY_FAVORITES_KEY, set).apply();
    }

    /**
     * Add the card info to the dialog.
     *
     * @param cardInfo CardInfo.
     */
    public void addCardInfo(CardInfo cardInfo) {
        if (mFavoriteMap == null) {
            mFavoriteMap = new HashMap<>();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            Set<String> favoriteStrSet = sharedPreferences.getStringSet(Constants.MY_FAVORITES_KEY, new HashSet<>());
            for (String json : favoriteStrSet) {
                CardInfo card = JsonUtils.json2Object(json, CardInfo.class);
                mFavoriteMap.put(card.getId(), card);
            }
        }

        CardInfo info = mFavoriteMap.get(cardInfo.getId());
        if (info != null && !info.equals(cardInfo)) {
            mFavoriteMap.put(cardInfo.getId(), cardInfo);
            Set<String> set = new HashSet<>();
            for (CardInfo card: mFavoriteMap.values()) {
                String cardStr = JsonUtils.object2Json(card);
                set.add(cardStr);
            }
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            sharedPreferences.edit().putStringSet(Constants.MY_FAVORITES_KEY, set).apply();
        }

        if (info == null) {
            mAdapter.addCardInfo(cardInfo);
        } else {
            Toast.makeText(this.getActivity(), cardInfo.getName() + " is already in your favorites.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Remove the card info from dialog.
     *
     * @param cardInfo CardInfo.
     */
    public void removeCardInfo(CardInfo cardInfo) {
        mAdapter.removeCardInfo(cardInfo);
    }

    /**
     * Set on close listener.
     *
     * @param listener OnCloseListener
     */
    public void setOnCloseListener(OnCloseListener listener) {
        mListener = listener;
    }

    /**
     * On close listener.
     */
    public interface OnCloseListener {
        /**
         * On close.
         */
        void onClose();
    }
}
