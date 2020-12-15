package com.wmq.hms.nearby.beaconmanager.softbeacon;

public class Conversion {
    public static byte loUint16(short v) {
        return (byte) (v & 0xFF);
    }

    public static byte hiUint16(short v) {
        return (byte) (v >> 8);
    }

    public static short buildUint16(byte hi, byte lo) {
        return (short) ((hi << 8) + (lo & 0xff));
    }

    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    // 从字节数组到十六进制字符串转�?
    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    public static int byteStrToInt(String valueStr) {
        valueStr = valueStr.toUpperCase();
        if (valueStr.length() % 2 != 0) {
            valueStr = "0" + valueStr;
        }

        int returnValue = 0;

        int length = valueStr.length();

        for (int i = 0; i < length; i++) {

            int value = charToByte(valueStr.charAt(i));

            returnValue += Math.pow(16, length - i - 1) * value;
        }
        return returnValue;
    }

    public static int bytesToInt(byte[] values) {

        String valueStr = Bytes2HexString(values);
        if (valueStr.length() % 2 != 0) {
            valueStr = "0" + valueStr;
        }

        int returnValue = 0;

        int length = valueStr.length();

        for (int i = 0; i < length; i++) {

            int value = charToByte(valueStr.charAt(i));

            returnValue += Math.pow(16, length - i - 1) * value;
        }
        return returnValue;
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) +
                (byte) ((b >> 6) & 0x1) +
                (byte) ((b >> 5) & 0x1) +
                (byte) ((b >> 4) & 0x1) +
                (byte) ((b >> 3) & 0x1) +
                (byte) ((b >> 2) & 0x1) +
                (byte) ((b >> 1) & 0x1) +
                (byte) ((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static byte[] subByteArray(byte[] src, int start, int length) {


        byte[] result = new byte[length];

        System.arraycopy(src, start, result, 0, length);


        return result;

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
