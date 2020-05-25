/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.hms.nearby.friends.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Common Util
 *
 * @since 2019-12-13
 */
public class CommonUtil {
    private CommonUtil() {
    }

    /**
     * Show Toast
     *
     * @param context  Context
     * @param content  Content
     * @param showTime ShowTime
     */
    public static void showToast(Context context, String content, int showTime) {
        Looper.prepare();
        Toast.makeText(context, content, showTime).show();
        Looper.loop();
    }

    /**
     * Is Content Empty
     *
     * @param content content
     * @return true:Content is empty
     */
    public static boolean isEmpty(String content) {
        return !(content != null && content.length() > 0);
    }
}
