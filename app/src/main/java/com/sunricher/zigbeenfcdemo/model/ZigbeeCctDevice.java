package com.sunricher.zigbeenfcdemo.model;

import java.util.List;

public class ZigbeeCctDevice extends ZigbeeDimDevice {

    private static final int PRODUCT_ID = 0x02000002;

    /**
     * Get the current kelvin form the maxCct or minCct value.
     * max_kelvin = 1000_000 / maxCct
     * min_kelvin = 1000_1000 / minCct
     */

    /**
     * The value range is [155, 450], the default value is 155.
     */
    private int maxCct = 155;

    /**
     * The value range is [155, 450], the default value is 450.
     */
    private int minCct = 450;

    public int getMaxCct() {
        return maxCct;
    }

    public void setMaxCct(int maxCct) {
        this.maxCct = maxCct;
    }

    public int getMinCct() {
        return minCct;
    }

    public void setMinCct(int minCct) {
        this.minCct = minCct;
    }

    @Override
    public List<AttributeItem> getAttributeItems() {
        List<AttributeItem> items = super.getAttributeItems();
        items.remove(0);
        items.add(new AttributeItem("Product ID", "0x" + HexUtil.intToHexString(PRODUCT_ID), PRODUCT_ID, NfcCommand.ZigbeeNfcPage.PRODUCT_ID));
        items.add(new AttributeItem("Max CCT", maxCct, NfcCommand.ZigbeeNfcPage.MAX_CCT_VALUE));
        items.add(new AttributeItem("Min CCT", minCct, NfcCommand.ZigbeeNfcPage.MIN_CCT_VALUE));
        return items;
    }

    public int getProductId() {
        return PRODUCT_ID;
    }
}
