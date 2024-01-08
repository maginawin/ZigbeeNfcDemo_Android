package com.sunricher.zigbeenfcdemo.model;

public class NfcCommand {

    public static final byte[] SECRET_KEY = new byte[]{(byte) 0x1B, (byte) 0x33, (byte) 0x38, (byte) 0x33, (byte) 0x39};

    public static final byte READ_COMMAND = 0x30;
    public static final byte FAST_READ_COMMAND = 0x3A;
    public static final byte WRITE_COMMAND = (byte) 0xA2;

    /**
     * When you set the attribute to the device, the first byte of the NFC page has to set to 0x55.
     */
    public static final byte TAG_WRITE_TO_DEVICE = (byte) 0x55;

    /**
     * After re-power the device, the device will set the first byte of the NFC page to 0x56. This
     * means the attribute has been set.
     */
    public static final byte TAG_DEVICE_RECEIVED = (byte) 0x56;

    public static class ZigbeeNfcPage {

        public final static byte PRODUCT_ID = (byte) 0x07;

        public final static byte TARGET_CURRENT = (byte) 0x72;

        public final static byte DIMMING_CURVE = (byte) 0x3B;

        public final static byte POWER_ON_STATE = (byte) 0x3C;

        public final static byte ON_OFF_TRANSITION_TIME = (byte) 0x3D;

        public final static byte ENABLE_PAIRING = (byte) 0x45;

        public final static byte MINIMUM_CURRENT_COMPENSATION = (byte) 0x77;

        public final static byte POWER_ON_LEVEL = (byte) 0x49;

        public final static byte MAX_CCT_VALUE = (byte) 0x44;

        public final static byte MIN_CCT_VALUE = (byte) 0x43;
    }

    public static byte[] readSingleAttribute(byte nfcPage) {
        return new byte[]{READ_COMMAND, nfcPage};
    }

    public static byte[][] readMultiAttributes(byte[] nfcPages) {
        byte[][] result = new byte[nfcPages.length][2];
        for (int i = 0; i < nfcPages.length; i++) {
            result[i] = readSingleAttribute(nfcPages[i]);
        }
        return result;
    }

    public static byte[] writeNfcData(byte nfcPage, byte[] pageData) {
        // The length of the pageData must be 4.
        assert (pageData.length == 4);
        return new byte[]{
                WRITE_COMMAND, nfcPage,
                pageData[0], pageData[1], pageData[2], pageData[3]
        };
    }

}
