package com.wmq.hms.nearby.beaconmanager.softbeacon;

import android.os.Build;

import com.wmq.hms.nearby.beaconmanager.BeaconApplication;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

public class DeviceIdUtil {
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (byte datum : data) {
            stmp = (Integer.toHexString(datum & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

    public static String getAndroidID(){
        if(null == BeaconApplication.context){
            return null;
        }
        return android.provider.Settings.Secure.getString(BeaconApplication.context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getUUIDOfAndroidID(){
        if(null == BeaconApplication.context){
            return null;
        }
        return new UUID(getAndroidID().hashCode(), Build.FINGERPRINT.hashCode()).toString().replace("-", "");
    }

    public static String getDeviceId() {
        try {
            byte[] hash = getHashByString(getUUIDOfAndroidID());
            String sha1 = bytesToHex(hash);
            if (sha1.length() > 0) {

                return sha1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
