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

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Card fragment state adapter.
 *
 * @since 2020-06-04
 */
public class CardFragmentStateAdapter extends FragmentStateAdapter {
    private static final String TAG = CardFragmentStateAdapter.class.getSimpleName();

    private Fragment[] fragments = new Fragment[2];

    public CardFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position >= fragments.length) {
            Log.e(TAG, "Can not create fragment in the position of " + position);
            return fragment;
        }

        fragment = fragments[position];
        if (fragment != null) {
            return fragment;
        }

        if (position == 0) {
            fragment = new MyCardFragment();
        }

        if (position == 1) {
            fragment = new MyFavoritesFragment();
        }

        fragments[position] = fragment;

        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
