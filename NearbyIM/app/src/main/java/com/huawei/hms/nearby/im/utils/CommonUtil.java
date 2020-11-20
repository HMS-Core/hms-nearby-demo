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

package com.huawei.hms.nearby.im.utils;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.huawei.hms.nearby.im.R;
import com.huawei.hms.nearby.im.bean.MessageBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;

public class CommonUtil {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String userName;

    public static String getCurrentTime(String pattern){
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(System.currentTimeMillis());
    }

    public static String formatTime(Context mContext, long time) {
        String blank = " ";
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR);
        int apm = mCalendar.get(Calendar.AM_PM);
        System.out.println("hour==" + hour + " apm==" + apm);
        String data = new SimpleDateFormat("HH:mm").format(time);
        if (apm == 0) {
            return blank + mContext.getString(R.string.common_lib_string_am) + data;
        } else {
            return blank + mContext.getString(R.string.common_lib_string_pm) + data;
        }
    }
}
