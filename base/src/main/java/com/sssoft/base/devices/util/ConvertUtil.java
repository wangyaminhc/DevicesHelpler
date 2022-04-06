package com.sssoft.base.devices.util;

import java.io.ByteArrayInputStream;

public class ConvertUtil {
    static final byte SPACE = 32;
    private static final byte[] hexDigits = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    public ConvertUtil() {
    }

    public static ByteArrayInputStream bytesToInputStream(byte[] buff, int offset, int length) {
        if (buff == null) {
            return null;
        } else {
            ByteArrayInputStream bis = new ByteArrayInputStream(buff, offset, length);
            return bis;
        }
    }

    public static ByteArrayInputStream bytesToInputStream(byte[] buff) {
        if (buff == null) {
            return null;
        } else {
            ByteArrayInputStream bis = new ByteArrayInputStream(buff);
            return bis;
        }
    }

    public static byte int2Byte(int input) {
        byte output1 = (byte)(input & 255);
        return output1;
    }

    public static byte[] int2Byte(int[] src, int start, int len) {
        int end = start + len;
        byte[] out = new byte[len];

        for(int i = 0; i < end; ++i) {
            out[i] = int2Byte(src[start + i]);
        }

        return out;
    }

    public static int byte2Unsigned(byte b) {
        return b < 0 ? b & 255 : b;
    }

    public static int[] byte2Int(byte[] src, int start, int len, byte space) {
        int[] out = new int[len];

        for(int i = 0; i < len; ++i) {
            int pos = start + i;
            if (pos < src.length) {
                out[i] = byte2Unsigned(src[pos]);
            } else {
                out[i] = space;
            }
        }

        return out;
    }

    public static int[] byte2Int(byte[] src, int start, int len) {
        return byte2Int(src, start, len, (byte)32);
    }

    public static String byte2Hex(byte ib) {
        byte[] ob = new byte[]{hexDigits[ib >>> 4 & 15], hexDigits[ib & 15]};
        String s = new String(ob);
        return s;
    }

    public static byte[] byte2Hex(byte[] src) {
        byte[] out = new byte[src.length * 2];

        for(int i = 0; i < src.length; ++i) {
            int tmp = byte2Unsigned(src[i]);
            out[i * 2] = hexDigits[tmp >>> 4 & 15];
            out[i * 2 + 1] = hexDigits[tmp & 15];
        }

        return out;
    }

    public static String asc2HexStr(String src, String charset) throws Exception {
        if (charset == null) {
            charset = "GBK";
        }

        byte[] asc = src.getBytes(charset);
        String tmp = asc2HexStr(asc);
        return tmp;
    }

    public static String asc2HexStr(byte[] asc) {
        StringBuilder resultSb = new StringBuilder();

        for(int i = 0; i < asc.length; ++i) {
            resultSb.append(byte2Hex(asc[i]));
        }

        return resultSb.toString();
    }

    static String hex2AscStr(String hexStr) {
        byte[] buff = hex2AscBytes(hexStr);
        return new String(buff);
    }

    public static String hex2AscStr(String hexStr, String charset) throws Exception {
        if (charset == null) {
            charset = "GBK";
        }

        byte[] buff = hex2AscBytes(hexStr);
        return new String(buff, charset);
    }

    /** @deprecated */
    @Deprecated
    public static byte[] hex2Bytes(String hexStr) {
        return hex2AscBytes(hexStr);
    }

    public static byte[] hex2AscBytes(String hexStr) {
        hexStr = hexStr2ABCD(hexStr);
        byte[] buff = new byte[hexStr.length() / 2];

        for(int i = 0; i < hexStr.length() / 2; ++i) {
            String s = hexStr.substring(i * 2, i * 2 + 2);
            buff[i] = (byte)Integer.parseInt(s, 16);
        }

        return buff;
    }

    public static byte[] hex2ss(String hexStr) {
        byte[] out = new byte[hexStr.length()];

        for(int i = 0; i < hexStr.length(); ++i) {
            char c = hexStr.charAt(i);
            int cx;
            if (c >= 'A' && c <= 'F') {
                cx = c - 65 + 58;
            } else if (c >= 'a' && c <= 'f') {
                cx = c - 97 + 58;
            } else {
                cx = c;
            }

            out[i] = (byte)cx;
        }

        return out;
    }

    public static String hexStr2ss(String hexStr) {
        return new String(hex2ss(hexStr));
    }

    public static String hexStr2ABCD(String hexStr) {
        StringBuilder hex = new StringBuilder();

        for(int i = 0; i < hexStr.length(); ++i) {
            char c = hexStr.charAt(i);
            if (c >= '0' && c <= '9') {
                hex.append(c);
            } else {
                int cx;
                if (c <= '?') {
                    cx = c - 58 + 65;
                    hex.append((char)cx);
                } else if (c >= 'a' && c <= 'f') {
                    cx = c - 97 + 65;
                    hex.append((char)cx);
                } else {
                    hex.append(c);
                }
            }
        }

        return hex.toString();
    }

    public static String hexStr2abcd(String hexStr) {
        StringBuilder hex = new StringBuilder();

        for(int i = 0; i < hexStr.length(); ++i) {
            char c = hexStr.charAt(i);
            if (c >= '0' && c <= '9') {
                hex.append(c);
            } else {
                int cx;
                if (c <= '?') {
                    cx = c - 58 + 97;
                    hex.append((char)cx);
                } else if (c >= 'A' && c <= 'F') {
                    cx = c - 65 + 97;
                    hex.append((char)cx);
                } else {
                    hex.append(c);
                }
            }
        }

        return hex.toString();
    }

    public static void printArray(int[] bytes) {
        for(int i = 0; i < bytes.length; ++i) {
            System.out.print("[" + bytes[i] + "]");
        }

        System.out.println();
    }

    public static void printArray(byte[] bytes) {
        for(int i = 0; i < bytes.length; ++i) {
            System.out.print("[" + bytes[i] + "]");
        }

        System.out.println();
    }

    public static void printArrayHex(byte[] bytes) {
        for(int i = 0; i < bytes.length; ++i) {
            System.out.print("[" + byte2Hex(bytes[i]) + "]");
        }

        System.out.println();
    }
}
