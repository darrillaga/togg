package com.t3coode.togg.activities.utils;

public class HexUtils {

    public static String getHexStringFromBytes(byte[] data) {
        if (data.length <= 0)
            return null;
        StringBuffer hexString = new StringBuffer();
        String fix = null;
        for (int i = 0; i < data.length; i++) {
            fix = Integer.toHexString(0xFF & data[i]);
            if (fix.length() == 1)
                fix = "0" + fix;
            hexString.append(fix);
        }
        fix = null;
        fix = hexString.toString();
        return fix;
    }

    public static byte[] getBytesFromHexString(String strHexData) {
        if (1 == strHexData.length() % 2) {
            return null;
        }
        byte[] bytes = new byte[strHexData.length() / 2];
        try {
            for (int i = 0; i < strHexData.length() / 2; i++) {
                bytes[i] = (byte) Integer.parseInt(
                        strHexData.substring(i * 2, (i + 1) * 2), 16);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return bytes;
    }

}
