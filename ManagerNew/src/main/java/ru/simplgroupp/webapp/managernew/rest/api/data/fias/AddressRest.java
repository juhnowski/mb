package ru.simplgroupp.webapp.managernew.rest.api.data.fias;

import ru.simplgroupp.fias.persistence.AddrObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressRest implements Serializable {
    private String id;

    private String label;

    private String levelName;

    public AddressRest() {

    }

    public AddressRest(String id, String label) {
        this.id = id;
        this.label = label;
    }


    public AddressRest(String id, String label, String levelName) {
        this.id = id;
        this.label = label;
        this.levelName = levelName;
    }



    public static List<AddressRest> toAddressRestList(List<AddrObj> addressObjectList) {
        List<AddressRest> restList = new ArrayList(addressObjectList.size());
        for(AddrObj addressObject: addressObjectList) {
            restList.add(new AddressRest(addressObject.getAOGUID(), addressObject.getOfficialName() + " " + addressObject.getType().getName().toLowerCase(),
                    addressObject.getType().getName()));
        }
        return restList;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getLevelName() {
        return levelName;
    }


    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
