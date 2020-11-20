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
import android.util.Log;

import com.huawei.hms.nearby.im.utils.Constants;
import com.huawei.hms.nearby.im.view.BaseView;

public class BasePresenter<T extends BaseView> {

    protected T view;
    protected Context mContext;

    public BasePresenter(Context mContext,T view) {
        this.mContext = mContext;
        this.view = view;
    }

    /**
     * bindView onCreate
     */
    public void onCreate(){
        Log.i(Constants.TAG,"Activity -- onCreate()--"+view.getClass().getName());
    };

    public void onStart(){
        Log.i(Constants.TAG,"Activity -- onStart()--"+view.getClass().getName());
    };

    public void onResume() {
        Log.i(Constants.TAG,"Activity -- onResume()--"+view.getClass().getName());
    }
    public void onPause() {
        Log.i(Constants.TAG,"Activity -- onPause()--"+view.getClass().getName());
    }
    /**
     * bindView onStop
     */
    public void onStop(){
        Log.i(Constants.TAG,"Activity -- onStop()--"+view.getClass().getName());
    };

    /**
     * bindView onDestroy
     */
    public void onDestroy(){
        Log.i(Constants.TAG,"Activity -- onDestroy()--"+view.getClass().getName());
    };

}
