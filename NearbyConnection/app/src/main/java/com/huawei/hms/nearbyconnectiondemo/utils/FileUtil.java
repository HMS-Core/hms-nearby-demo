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

package com.huawei.hms.nearbyconnectiondemo.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * FileUtil
 *
 * @since 2020-03-27
 */
public class FileUtil {
    private static final int CONVERSION_BASE = 1024;
    private static final String[] BYTES_UNIT_LIST = {"B", "KB", "MB", "GB", "TB"};
    private static final long BASE_KB = CONVERSION_BASE;
    private static final long BASE_MB = CONVERSION_BASE * BASE_KB;
    private static final long BASE_GB = CONVERSION_BASE * BASE_MB;
    private static final long BASE_TB = CONVERSION_BASE * BASE_GB;
    private static final long[] BASE_LIST = {1, BASE_KB, BASE_MB, BASE_GB, BASE_TB};

    /**
     * Convert bytes to human readable format.
     *
     * @param bytes input bytes
     * @return format string
     */
    public static String formatBytes(long bytes) {
        long inputBytes = bytes;
        BigDecimal bigBytes = new BigDecimal(bytes);
        NumberFormat numberFormat = new DecimalFormat();
        numberFormat.setMaximumFractionDigits(1);
        int index;
        for (index = 0; index < BYTES_UNIT_LIST.length; index++) {
            if (inputBytes < CONVERSION_BASE) {
                break;
            }
            inputBytes /= CONVERSION_BASE;
        }
        return numberFormat.format(bigBytes.divide(new BigDecimal(BASE_LIST[index]))) + " " + BYTES_UNIT_LIST[index];
    }

    /**
     * Get the real path of the file according to the URI
     *
     * @param context Context object
     * @param fileUri fileUri
     * @return real path
     */
    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) {
            return null;
        }
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) {
            return null;
        }
        return documentFile.getName();
    }

    public static boolean isImage(String fileName) {
        String[] suffixArray = {"jpg", "png", "gif", "bmp", "tif"};
        for (String suffix : suffixArray) {
            if (!TextUtils.isEmpty(fileName) && fileName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
