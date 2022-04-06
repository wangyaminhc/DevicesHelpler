package com.sssoft.base.devices.util;

public class SS3Des extends SSDes {
    public SS3Des() {
    }

    @Override
    protected void digest(int[] text, byte[] key, int flag, int[] crBlock) {
        this.str_3des_crypt(text, key, flag, crBlock);
    }

    int str_3des_crypt(int[] MWEN, byte[] KEY, int flag, int[] cr_block) {
        int[] now_block = new int[16];
        int[] now_block1 = new int[16];
        byte[] key1 = new byte[16];
        byte[] key2 = new byte[16];
        System.arraycopy(KEY, 0, key1, 0, 16);
        System.arraycopy(KEY, 16, key2, 0, 16);
        if (flag == 0) {
            this.user_des_crypt(MWEN, key1, 0, now_block);
            this.user_des_crypt(now_block, key2, 1, now_block1);
            this.user_des_crypt(now_block1, key1, 0, now_block);
        } else {
            this.user_des_crypt(MWEN, key1, 1, now_block);
            this.user_des_crypt(now_block, key2, 0, now_block1);
            this.user_des_crypt(now_block1, key1, 1, now_block);
        }

        System.arraycopy(now_block, 0, cr_block, 0, 16);
        return 0;
    }
}
