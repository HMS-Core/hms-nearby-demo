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

package com.huawei.hms.nearby.stores.canteen.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

/**
 * JsonUtils
 *
 * @since 2019-12-27
 */
public class JsonUtils {
    private static final String TAG = "JsonUtils";

    private static volatile Gson sGsonInstance = null;

    private static Gson getGsonInstance() {
        if (sGsonInstance == null) {
            synchronized (JsonUtils.class) {
                if (sGsonInstance == null) {
                    sGsonInstance = getGsonBuilder().create();
                }
            }
        }
        return sGsonInstance;
    }

    private static GsonBuilder getGsonBuilder() {
        return new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeSpecialFloatingPointValues()
                .disableHtmlEscaping()
                .setLenient();
    }

    /**
     * JSON to Object
     *
     * @param json        json String
     * @param objectClass objectClass
     * @return Object
     */
    public static <T> T json2Object(String json, Class<T> objectClass) {
        T obj = null;
        if (TextUtils.isEmpty(json) || objectClass == null) {
            return obj;
        }
        try {
            obj = getGsonInstance().fromJson(json, objectClass);
        } catch (JsonParseException e) {
            Log.e(TAG, "JsonParseException:" + e.getMessage());
        }

        return obj;
    }

    /**
     * Object to Json
     *
     * @param object object
     * @return Json String
     */
    public static <T> String object2Json(T object) {
        String jsonStr = null;
        if (object == null) {
            return jsonStr;
        }
        try {
            jsonStr = getGsonInstance().toJson(object);
        } catch (JsonParseException e) {
            Log.e(TAG, "JsonParseException");
        }

        return jsonStr;
    }
}
