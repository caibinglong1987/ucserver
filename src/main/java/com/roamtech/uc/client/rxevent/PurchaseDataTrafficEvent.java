package com.roamtech.uc.client.rxevent;

import com.roamtech.uc.model.DataCardTraffic;

import java.util.List;

/**
 * Created by admin03 on 2016/8/17.
 */
public class PurchaseDataTrafficEvent {
    private List<DataCardTraffic> dataCardTraffics;

    public PurchaseDataTrafficEvent(List<DataCardTraffic> dataCardTraffics) {
        setDataCardTraffics(dataCardTraffics);
    }

    public List<DataCardTraffic> getDataCardTraffics() {
        return dataCardTraffics;
    }

    public void setDataCardTraffics(List<DataCardTraffic> dataCardTraffics) {
        this.dataCardTraffics = dataCardTraffics;
    }
}
