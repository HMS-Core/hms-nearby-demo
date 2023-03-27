/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
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

package com.huawei.hms.nearby.beacondemo.merchants.softbeacon;

import java.util.Locale;

public class Conversion {

    private static final String DEFINITION = "0123456789ABCDEF";

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase(Locale.ROOT);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] hexBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            hexBytes[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return hexBytes;
    }

    public static byte charToByte(char ch) {
        return (byte) DEFINITION.indexOf(ch);
    }

    /**
     * @param targetLenth   补齐后的长度
     * @param src           需要补齐的字符串
     * @param leftCharacter 用来补齐长度的字符
     * @return 补齐字符串
     */
    public static String formatStringLenth(int targetLenth, String src, char leftCharacter) {

        if (src.length() > targetLenth) {
            return src.substring(src.length() - targetLenth);
        } else {

            int delta = targetLenth - src.length();
            for (int i = 0; i < delta; i++) {
                src = leftCharacter + src;
            }
            return src;
        }
    }
}
