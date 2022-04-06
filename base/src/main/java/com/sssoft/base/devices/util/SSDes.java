package com.sssoft.base.devices.util;

public class SSDes {
    static int FLAG_ENC = 0;
    static int FLAG_DEC = 1;
    static byte SPACE = -1;
    private Byte space = null;
    private byte[] key = null;
    static int[] ip = new int[]{58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};
    static int[] rip = new int[]{40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
    static int[][] mc = new int[][]{{1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1}, {28, 27, 26, 26, 26, 26, 26, 26, 27, 26, 26, 26, 26, 26, 26, 27}};
    static int[] pc1 = new int[]{57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};
    static int[] pc2 = new int[]{14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};
    static int[] e = new int[]{32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};
    static int[][][] s = new int[][][]{{{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7}, {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8}, {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0}, {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}}, {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10}, {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5}, {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15}, {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}}, {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8}, {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1}, {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7}, {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}}, {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15}, {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9}, {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4}, {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}}, {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9}, {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6}, {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14}, {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}}, {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11}, {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8}, {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6}, {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}}, {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1}, {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6}, {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2}, {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}}, {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7}, {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2}, {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8}, {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}};
    static int[] p = new int[]{16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25};

    public SSDes() {
    }

    public static String encHexStr3(String hexStr, String key) throws Exception {
        SSDes des = new SS3Des();
        des.setSpace(SPACE);
        des.setKey(key);
        String encStr = des.encrypt(hexStr);
        return encStr;
    }

    public static String decHexStr3(String hexStr, String key) throws Exception {
        SSDes des = new SS3Des();
        des.setKey(key);
        String decStr = des.decrypt(hexStr);
        return decStr;
    }

    public static String encHexStr(String text, String key) throws Exception {
        SSDes des = new SSDes();
        des.setSpace(SPACE);
        des.setKey(key);
        String encStr = des.encrypt(text);
        return encStr;
    }

    public static String decHexStr(String text, String key) throws Exception {
        SSDes des = new SSDes();
        des.setKey(key);
        String decStr = des.decrypt(text);
        return decStr;
    }

    public void setSpace(Byte sp) {
        this.space = sp;
    }

    public void setKey(String keyStr) {
        this.key = keyStr.getBytes();
    }

    public void setKey(byte[] keyBytes) {
        this.key = keyBytes;
    }

    public byte[] encryptAsc(byte[] ascBytes) throws Exception {
        byte[] mbytes = ascBytes;
        int blen = ascBytes.length % 8;
        if (this.space != null) {
            BytesBuilder srcBuilder = new BytesBuilder();
            srcBuilder.append(ascBytes);
            srcBuilder.append(this.space, blen);
            mbytes = srcBuilder.toBytes();
        }

        String hexStr = ConvertUtil.asc2HexStr(mbytes);
        String encStr = this.encrypt(hexStr);
        return ConvertUtil.hex2AscBytes(encStr);
    }

    public byte[] decryptAsc(byte[] ascBytes) throws Exception {
        String hexStr = ConvertUtil.asc2HexStr(ascBytes);
        String decStr = this.decrypt(hexStr);
        return ConvertUtil.hex2AscBytes(decStr);
    }

    public String encrypt(String hexStr) throws Exception {
        byte[] textBytes = hexStr.getBytes();
        byte[] encBytes = this.digest(textBytes, this.key, FLAG_ENC);
        return new String(encBytes);
    }

    public String decrypt(String hexStr) throws Exception {
        byte[] textBytes = hexStr.getBytes();
        byte[] decBytes = this.digest(textBytes, this.key, FLAG_DEC);
        return new String(decBytes);
    }

    public byte[] encrypt(byte[] textBytes) throws Exception {
        byte[] encBytes = this.digest(textBytes, this.key, FLAG_ENC);
        return encBytes;
    }

    public byte[] decrypt(byte[] textBytes) throws Exception {
        byte[] decBytes = this.digest(textBytes, this.key, FLAG_DEC);
        return decBytes;
    }

    protected byte[] digest(byte[] text, byte[] key, int flag) throws Exception {
        int len = text.length;
        int num = len / 16;
        int m = len % 16;
        int pos = 0;
        int[] cr = new int[16];
        BytesBuilder resBuilder = new BytesBuilder();
        byte sp = 0;
        if (m > 0 && this.space != null) {
            sp = this.space;
            ++num;
        }

        for(int i = 0; i < num; ++i) {
            int[] src = ConvertUtil.byte2Int(text, pos, 16, sp);
            pos += 16;
            this.digest(src, key, flag, cr);
            resBuilder.append(ConvertUtil.int2Byte(cr, 0, 16));
        }

        if (this.space == null) {
            resBuilder.append(text, pos, m);
            return resBuilder.toBytes();
        } else {
            return resBuilder.toBytes();
        }
    }

    protected void digest(int[] text, byte[] key, int flag, int[] crBlock) {
        this.user_des_crypt(text, key, flag, crBlock);
    }

    int user_des_crypt(int[] MWEN, byte[] KEY, int flag, int[] cr_block) {
        int[] mwen = new int[64];
        int[] key = new int[64];
        int[] MWEN1 = new int[16];
        int[] trav = new int[64];
        int[] l0 = new int[32];
        int[] r0 = new int[32];
        int[] l = new int[64];
        int[][] k = new int[16][48];
        System.arraycopy(MWEN, 0, MWEN1, 0, 16);
        int[] KEY1 = ConvertUtil.byte2Int(KEY, 0, 16);

        int i;
        for(i = 0; i < 16; ++i) {
            if (MWEN1[i] >= 48 && MWEN1[i] <= 63) {
                MWEN1[i] -= 48;
            } else if (MWEN1[i] >= 97 && MWEN1[i] <= 102) {
                MWEN1[i] = MWEN1[i] - 97 + 10;
            } else if (MWEN1[i] >= 65 && MWEN1[i] <= 70) {
                MWEN1[i] = MWEN1[i] - 65 + 10;
            }

            if (KEY1[i] >= 48 && KEY1[i] <= 63) {
                KEY1[i] -= 48;
            } else if (KEY1[i] >= 97 && KEY1[i] <= 102) {
                KEY1[i] = KEY1[i] - 97 + 10;
            } else if (KEY1[i] >= 65 && KEY1[i] <= 70) {
                KEY1[i] = KEY1[i] - 65 + 10;
            }
        }

        for(i = 0; i < 16; ++i) {
            mwen[i * 4] = (MWEN1[i] & 8) >> 3;
            mwen[i * 4 + 1] = (MWEN1[i] & 4) >> 2;
            mwen[i * 4 + 2] = (MWEN1[i] & 2) >> 1;
            mwen[i * 4 + 3] = MWEN1[i] & 1;
            key[i * 4] = (KEY1[i] & 8) >> 3;
            key[i * 4 + 1] = (KEY1[i] & 4) >> 2;
            key[i * 4 + 2] = (KEY1[i] & 2) >> 1;
            key[i * 4 + 3] = KEY1[i] & 1;
        }

        this.getk(k, key, flag);

        for(i = 0; i < 64; ++i) {
            trav[i] = mwen[ip[i] - 1];
        }

        for(i = 0; i < 32; ++i) {
            l0[i] = trav[i];
            r0[i] = trav[i + 32];
        }

        for(i = 0; i < 16; ++i) {
            int[] fp = this.f(r0, k[i]);

            int j;
            for(j = 0; j < 32; ++j) {
                l[j] = r0[j];
                l[j + 32] = l0[j] ^ fp[j];
            }

            for(j = 0; j < 32; ++j) {
                l0[j] = l[j];
                r0[j] = l[j + 32];
            }
        }

        for(i = 0; i < 32; ++i) {
            l[i] = r0[i];
            l[i + 32] = l0[i];
        }

        for(i = 0; i < 64; ++i) {
            trav[i] = l[rip[i] - 1];
        }

        for(i = 0; i < 16; ++i) {
            cr_block[i] = trav[i * 4] * 8 + trav[i * 4 + 1] * 4 + trav[i * 4 + 2] * 2 + trav[i * 4 + 3];
        }

        for(i = 0; i < 16; ++i) {
            if (cr_block[i] > 9) {
                cr_block[i] = cr_block[i] + 65 - 10;
            } else {
                cr_block[i] += 48;
            }
        }

        return 0;
    }

    int getk(int[][] ki, int[] keyi, int flag) {
        int[] trav1 = new int[56];

        int i;
        for(i = 0; i < 56; ++i) {
            trav1[i] = keyi[pc1[i] - 1];
        }

        for(i = 0; i < 16; ++i) {
            int n;
            for(int j = 0; j < mc[flag][i]; ++j) {
                int tem1 = trav1[0];
                int tem2 = trav1[28];

                for(n = 0; n < 27; ++n) {
                    trav1[n] = trav1[n + 1];
                    trav1[n + 28] = trav1[n + 28 + 1];
                }

                trav1[27] = tem1;
                trav1[55] = tem2;
            }

            for(n = 0; n < 48; ++n) {
                ki[i][n] = trav1[pc2[n] - 1];
            }
        }

        return 0;
    }

    int[] f(int[] fr, int[] fk) {
        int[] trav2 = new int[48];
        int[] e0 = new int[32];
        int[] ffk = new int[32];

        int i;
        for(i = 0; i < 48; ++i) {
            trav2[i] = fr[e[i] - 1];
            trav2[i] ^= fk[i];
        }

        for(i = 0; i < 8; ++i) {
            int row = trav2[i * 6] * 2 + trav2[i * 6 + 5];
            int col = trav2[i * 6 + 1] * 8 + trav2[i * 6 + 2] * 4 + trav2[i * 6 + 3] * 2 + trav2[i * 6 + 4];
            int temp = s[i][row][col];
            e0[i * 4] = temp / 8;
            e0[i * 4 + 1] = temp % 8 / 4;
            e0[i * 4 + 2] = temp % 4 / 2;
            e0[i * 4 + 3] = temp % 2;
        }

        for(i = 0; i < 32; ++i) {
            ffk[i] = e0[p[i] - 1];
        }

        return ffk;
    }
}
