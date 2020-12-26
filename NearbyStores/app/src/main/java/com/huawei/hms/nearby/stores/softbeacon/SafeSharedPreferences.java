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

package com.huawei.hms.nearby.stores.softbeacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SafeSharedPreferences {
    private static final String TAG = "SafeSharedPreferences";

    private static volatile SafeSharedPreferences sSafeSharedPreferences;

    private SharedPreferences sp;

    public static SafeSharedPreferences getInstance(Context context, String name, int mode) {
        if (sSafeSharedPreferences == null) {
            synchronized (SafeSharedPreferences.class) {
                if (sSafeSharedPreferences == null) {
                    sSafeSharedPreferences = new SafeSharedPreferences(context, name, mode);
                }
            }
        }
        return sSafeSharedPreferences;
    }

    /**
     * 初始化SafeSharedPreferences
     *
     * @param context 上下文
     * @param name sp文件名
     * @param mode 访问模式
     */
    public SafeSharedPreferences(Context context, String name, int mode) {
        try {
            sp = context.getSharedPreferences(name, mode);
        } catch (Exception e) {
            Log.i(TAG, "throw Exception.");
        }
    }

    /**
     * putString method
     *
     * @param key String
     * @param value String
     */
    public void putString(String key, String value) {
        try {
            if (sp != null) {
                sp.edit().putString(key, value).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "putString, " + e.getMessage());
        }
    }

    /**
     * getString method
     *
     * @param key String
     * @param defValue String
     *
     * @return String
     */
    public String getString(String key, String defValue) {
        try {
            if (sp != null) {
                return sp.getString(key, defValue);
            }
        } catch (Exception e) {
            Log.d(TAG, "getString, " + e.getMessage());
        }
        return defValue;
    }

    /**
     * putInt method
     *
     * @param key String
     * @param value int
     */
    public void putInt(String key, int value) {
        try {
            if (sp != null) {
                sp.edit().putInt(key, value).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "putInt, " + e.getMessage());
        }
    }

    /**
     * getInt method
     *
     * @param key String
     * @param defValue int
     *
     * @return int
     */
    public int getInt(String key, int defValue) {
        try {
            if (sp != null) {
                return sp.getInt(key, defValue);
            }
        } catch (Exception e) {
            Log.d(TAG, "getInt, " + e.getMessage());
        }
        return defValue;
    }

    /**
     * putBoolean method
     *
     * @param key String
     * @param value boolean
     */
    public void putBoolean(String key, boolean value) {
        try {
            if (sp != null) {
                sp.edit().putBoolean(key, value).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "putBoolean, " + e.getMessage());
        }
    }

    /**
     * getBoolean method
     *
     * @param key String
     * @param defValue boolean
     *
     * @return boolean
     */
    public boolean getBoolean(String key, boolean defValue) {
        try {
            if (sp != null) {
                return sp.getBoolean(key, defValue);
            }
        } catch (Exception e) {
            Log.d(TAG, "getBoolean, " + e.getMessage());
        }
        return defValue;
    }

    /**
     * putLong method
     *
     * @param key String
     * @param value Long
     */
    public void putLong(String key, long value) {
        try {
            if (sp != null) {
                sp.edit().putLong(key, value).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "putLong, " + e.getMessage());
        }
    }

    /**
     * getLong method
     *
     * @param key String
     * @param defValue Long
     *
     * @return Long
     */
    public Long getLong(String key, long defValue) {
        try {
            if (sp != null) {
                return sp.getLong(key, defValue);
            }
        } catch (Exception e) {
            Log.d(TAG, "getLong, " + e.getMessage());
        }
        return defValue;
    }

    /**
     * remove method
     *
     * @param key String
     */
    public void remove(String key) {
        try {
            if (sp != null) {
                sp.edit().remove(key).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "remove key, " + e.getMessage());
        }
    }
}
