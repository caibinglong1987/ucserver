package com.roamtech.uc.client.rxevent;

import com.roamtech.uc.model.DataCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin03 on 2016/8/17.
 */
public class RegisterDataCardEvent {

    private List<DataCard> dataCards;

    public RegisterDataCardEvent(DataCard dc) {
        dataCards = new ArrayList<DataCard>();
        dataCards.add(dc);
    }

    public RegisterDataCardEvent(List<DataCard> dcs) {
        setDataCards(dcs);
    }

    public List<DataCard> getDataCards() {
        return dataCards;
    }

    public void setDataCards(List<DataCard> dataCards) {
        this.dataCards = dataCards;
    }

}
