package com.sunricher.zigbeenfcdemo.model;

public class AttributeItem {

    private String title;
    private String detail;

    private byte nfcPage;
    private int value;

    public String getTitle() {
        return title;
    }

    public AttributeItem(String title, int value, byte nfcPage) {
        this.title = title;
        this.value = value;
        this.detail = String.valueOf(value);
        this.nfcPage = nfcPage;
    }

    public AttributeItem(String title, String detail, int value, byte nfcPage) {
        this.title = title;
        this.detail = detail;
        this.value = value;
        this.nfcPage = nfcPage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public byte getNfcPage() {
        return nfcPage;
    }

    public void setNfcPage(byte nfcPage) {
        this.nfcPage = nfcPage;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setNfcValue(byte[] bytes) {
        if (bytes.length < 4) {
            return;
        }
        // bytes[0] is a tag, when tag equals 0x55 or 0x56 means the nfc value bytes is valid.
        if (bytes[0] != NfcCommand.TAG_WRITE_TO_DEVICE && bytes[0] != NfcCommand.TAG_DEVICE_RECEIVED) {
            // The value on the device is invalid, maybe you should set the value to the default value here.
            return;
        }
        switch (nfcPage) {
            case NfcCommand.ZigbeeNfcPage.TARGET_CURRENT:
            case NfcCommand.ZigbeeNfcPage.ON_OFF_TRANSITION_TIME:
            case NfcCommand.ZigbeeNfcPage.MINIMUM_CURRENT_COMPENSATION:
            case NfcCommand.ZigbeeNfcPage.MAX_CCT_VALUE:
            case NfcCommand.ZigbeeNfcPage.MIN_CCT_VALUE: {
                // bytes[1] the high byte
                // bytes[2] the low byte
                int highValue = (int) bytes[1] & 0xFF;
                int lowValue = (int) bytes[2] & 0xFF;
                // Update the value.
                setValue((highValue << 8) | lowValue);
                // Update the detail of the item.
                setDetail(String.valueOf(getValue()));
                break;
            }
            case NfcCommand.ZigbeeNfcPage.DIMMING_CURVE:
            case NfcCommand.ZigbeeNfcPage.POWER_ON_STATE:
            case NfcCommand.ZigbeeNfcPage.POWER_ON_LEVEL: {
                // bytes[1] is the value.
                setValue((int) bytes[1] & 0xFF);
                setDetail(String.valueOf(getValue()));
                break;
            }
            case NfcCommand.ZigbeeNfcPage.ENABLE_PAIRING: {
                // Enable pairing is special, when the tag is 0x56 means the device has already
                // entered pairing mode and will not enter pairing mode again.
                // Write enable pairing to the device, re-power the device, then the device will
                // enter pairing mode, the tag will be set to 0x56.
                if (bytes[0] == NfcCommand.TAG_DEVICE_RECEIVED) {
                    // If the device has already entered pairing mode, set to 0x00.
                    setValue(0x00);
                    setDetail("0");
                    break;
                }
                // bytes[1] is the highValue.
                // bytes[2] is the midValue.
                // bytes[3] is the lowValue;
                int highValue = (int) bytes[1] & 0xFF;
                int midValue = (int) bytes[2] & 0xFF;
                int lowValue = (int) bytes[3] & 0xFF;
                setValue((highValue << 16) | (midValue << 8) | lowValue);
                setDetail(String.valueOf(getValue()));
                break;
            }
            default:
                break;
        }
    }

    public byte[] getItemValue() {
        byte highValue = (byte) 0xFF;
        byte lowValue = (byte) 0xFF;
        byte lastValue = (byte) 0xFF;
        switch (nfcPage) {
            case NfcCommand.ZigbeeNfcPage.DIMMING_CURVE:
            case NfcCommand.ZigbeeNfcPage.POWER_ON_STATE:
            case NfcCommand.ZigbeeNfcPage.POWER_ON_LEVEL: {
                highValue = (byte) value;
                break;
            }
            case NfcCommand.ZigbeeNfcPage.TARGET_CURRENT:
            case NfcCommand.ZigbeeNfcPage.ON_OFF_TRANSITION_TIME:
            case NfcCommand.ZigbeeNfcPage.MINIMUM_CURRENT_COMPENSATION:
            case NfcCommand.ZigbeeNfcPage.MAX_CCT_VALUE:
            case NfcCommand.ZigbeeNfcPage.MIN_CCT_VALUE: {
                highValue = (byte) (value >> 8);
                lowValue = (byte) (value & 0xFF);
                break;
            }
            case NfcCommand.ZigbeeNfcPage.ENABLE_PAIRING: {
                highValue = (byte) (value >> 16);
                lowValue = (byte) (value >> 8);
                lastValue = (byte) (value & 0xFF);
                break;
            }
            default:
                break;
        }
        // The first value must be 0x55, If the first byte is 0x55, the device will read the following
        // bytes and update the attribute, otherwise won't.
        // We always set the placeholder byte to 0xFF.
        return new byte[]{
                0x55, highValue, lowValue, lastValue
        };
    }
}
