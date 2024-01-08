package com.sunricher.zigbeenfcdemo.model;

import java.util.ArrayList;
import java.util.List;

public class ZigbeeDimDevice {

    private static final int PRODUCT_ID = 0x02000001;

    /**
     * Value range is [1000, 50000], and 1 = 0.1mA , such as 1000 = 100.0mA. Default value is 1000 (100.0mA).
     */
    private int targetCurrent = 1000;

    /**
     * Values:
     * 0x00 for linear (default),
     * 0x0F for log gamma 1.5,
     * 0x12 for log gamma 1.8,
     * 0x19 for log gamma 2.5,
     * 0x23 for log gamma 3.5,
     * 0x32 for log gamma 5.0.
     * If the value is out of range, set to default.
     */
    private int dimmingCurve = 0x00;

    /**
     * Values:
     * 0x00 for off,
     * 0x01 for on,
     * 0xFF for latest (default).
     */
    private int powerOnState = 0xFF;

    /**
     * Value range [0, 65535], and 1 = 0.1s, such as 0x0064 = 10.0s. Default value is 1 (0.1s).
     */
    private int onOffTransitionTime = 1;

    /**
     * Values:
     * 0x010101 for enable pairing,
     * 0x010102 for enable touchlink or GP mode,
     * 0x010103 for enable find&bind or delete GP.
     * others for ignore (default).
     */
    private int enablePairing = 0x00;

    /**
     * Value range is [5000-2000], and percentage = (value - 10000) / 100.0, such as value = 5000 means percentage = -50.00%,
     * value = 10000 means percentage = 0.00%, value = 20000 means percentage = 100.00%.
     * Default value is 10000.
     */
    private int minimumCurrentCompensation = 10000;

    /**
     * Values:
     * 0x00 for the level always be 1,
     * 0x01-0xFE for the level will be (value/0xFE) * 100%.
     * 0xFF for the last level (default).
     */
    private int powerOnLevel = 0xFF;

    public int getProductId() {
        return PRODUCT_ID;
    }

    public int getTargetCurrent() {
        return targetCurrent;
    }

    public void setTargetCurrent(int targetCurrent) {
        this.targetCurrent = targetCurrent;
    }

    public int getDimmingCurve() {
        return dimmingCurve;
    }

    public void setDimmingCurve(int dimmingCurve) {
        this.dimmingCurve = dimmingCurve;
    }

    public int getPowerOnState() {
        return powerOnState;
    }

    public void setPowerOnState(int powerOnState) {
        this.powerOnState = powerOnState;
    }

    public int getOnOffTransitionTime() {
        return onOffTransitionTime;
    }

    public void setOnOffTransitionTime(int onOffTransitionTime) {
        this.onOffTransitionTime = onOffTransitionTime;
    }

    public int getEnablePairing() {
        return enablePairing;
    }

    public void setEnablePairing(int enablePairing) {
        this.enablePairing = enablePairing;
    }

    public int getMinimumCurrentCompensation() {
        return minimumCurrentCompensation;
    }

    public void setMinimumCurrentCompensation(int minimumCurrentCompensation) {
        this.minimumCurrentCompensation = minimumCurrentCompensation;
    }

    public int getPowerOnLevel() {
        return powerOnLevel;
    }

    public void setPowerOnLevel(int powerOnLevel) {
        this.powerOnLevel = powerOnLevel;
    }

    public List<AttributeItem> getAttributeItems() {
        List<AttributeItem> items = new ArrayList<>();
        items.add(new AttributeItem("Product ID", "0x" + HexUtil.intToHexString(PRODUCT_ID), PRODUCT_ID, NfcCommand.ZigbeeNfcPage.PRODUCT_ID));
        items.add(new AttributeItem("Target Current", targetCurrent, NfcCommand.ZigbeeNfcPage.TARGET_CURRENT));
        items.add(new AttributeItem("Dimming Curve", dimmingCurve, NfcCommand.ZigbeeNfcPage.DIMMING_CURVE));
        items.add(new AttributeItem("Power On State", powerOnState, NfcCommand.ZigbeeNfcPage.POWER_ON_STATE));
        items.add(new AttributeItem("On Off Transition Time", onOffTransitionTime, NfcCommand.ZigbeeNfcPage.ON_OFF_TRANSITION_TIME));
        items.add(new AttributeItem("Enable Pairing", enablePairing, NfcCommand.ZigbeeNfcPage.ENABLE_PAIRING));
        items.add(new AttributeItem("Power On Level", powerOnLevel, NfcCommand.ZigbeeNfcPage.POWER_ON_LEVEL));
        return items;
    }
}
