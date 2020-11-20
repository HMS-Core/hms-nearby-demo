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

package com.huawei.hms.nearby.im.ui.adapter;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected Context mContext;
    protected LinkedList<T> data;

    public LinkedList<T> getData() {
        return data;
    }

    public BaseAdapter(Context mContext) {
        this.data = new LinkedList<>();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(T t){
        if (t != null) {
            this.data.add(t);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> list){
        if (list != null) {
            this.data.clear();
            this.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void removeItem(T t){
        if (t != null) {
            this.data.remove(t);
            notifyDataSetChanged();
        }
    }
}
