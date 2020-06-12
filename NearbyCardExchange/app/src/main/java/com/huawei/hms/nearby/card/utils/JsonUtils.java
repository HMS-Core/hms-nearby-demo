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

package com.huawei.hms.nearby.card.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import androidx.annotation.NonNull;

/**
 * Json utils.
 *
 * @since 2020-06-04
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();
    private static final Gson GSON = new Gson();

    /**
     * Parse json to object.
     *
     * @param jsonStr The json string.
     * @param objectClass The class of the object to be parsed.
     * @param <T> The type of the object.
     * @return The object.
     */
    public static <T> T json2Object(String jsonStr, @NonNull Class<T> objectClass) {
        T obj = null;
        if (TextUtils.isEmpty(jsonStr)) {
            return obj;
        }

        try {
            obj = GSON.fromJson(jsonStr, objectClass);
        } catch (JsonParseException e) {
            Log.e(TAG, "Parse json to object fail, exception: " + e.getMessage());
        }

        return obj;
    }

    /**
     * Parse object to json string.
     *
     * @param object The object to be parse.
     * @param <T> The type of the object.
     * @return The json string.
     */
    public static <T> String object2Json(@NonNull T object) {
        String jsonStr = null;
        try {
            jsonStr = GSON.toJson(object);
        } catch (JsonParseException e) {
            Log.e(TAG, "Parse object to json fail, exception: " + e.getMessage());
        }

        return jsonStr;
    }
}
