package com.sunricher.zigbeenfcdemo.model;

import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class HexUtil {

    public static String intToHexString(int value) {
        // 将整数转换为十六进制字符串
        String hexString = Integer.toHexString(value);

        // 如果结果字符串的长度是奇数，则在前面补零
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString;
    }

    public static int bytesToInt(byte[] bytes) {
        int result = 0;

        if (bytes.length < 4) {
            Log.w("ToolUtil", "bytes lenth" + bytes.length);
            return 0;
        }

        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= bytes[i];
        }
        return result;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();

        for (byte b : bytes) {
            // 将每个字节转换为两位16进制字符串，并追加到 StringBuilder 中
            hexStringBuilder.append(String.format("%02X", b));
        }

        return hexStringBuilder.toString();
    }

    public static String hexStringToAscii(String hexString) {
        // 将16进制字符串转换为BigInteger
        BigInteger bigInt = new BigInteger(hexString, 16);

        // 将BigInteger转换为ASCII字符串
        String asciiString = new String(bigInt.toByteArray());

        return asciiString;
    }

    public static String byteArrayToAscii(byte[] byteArray) {
        // 使用字符串的构造函数将字节数组转换为 ASCII 字符串
        String asciiString = new String(byteArray, StandardCharsets.US_ASCII);

        return asciiString;
    }

    public static String byteArrayToAsciiWithNullTermination(byte[] byteArray) {
        StringBuilder result = new StringBuilder();

        for (byte b : byteArray) {
            // 遇到 null 终止符 (byte) 0x00 就中断转换
            if (b == 0x00) {
                break;
            }

            // 使用字符串的构造函数将字节转换为 ASCII 字符串
            result.append(new String(new byte[]{b}, StandardCharsets.US_ASCII));
        }

        return result.toString();
    }

}
