package ru.simplgroupp.rest.api.data.user;

import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.PeopleMisc;

import java.util.List;

/**
 * Другие данные пользователя
 */
public class MiscData {
    /**
     * основания занятия жилого помещения (id)
     */
    private Integer realty;

    /**
     * дата начала проживания
     */
    private String realtyDate;

    /**
     * дата прописки
     */
    private String regDate;

    /**
     * дата прописки из справочника
     */
    private Integer regDateId;
    
    /**
     * дата начала пребывания из справочника
     */
    private Integer realtyDateId;

    /**
     * количество детей
     */
    private Integer children;

    /**
     * семейное положение (id)
     */
    private Integer marriage;

    /**
     * типы семейного положения
     */
    private List<ReferenceData> marriageTypes;

    /**
     * временные диапазоны для даты регистрации
     */
    private List<ReferenceData> regDateTypes;

    /**
     * типы занятия жилого помещения
     */
    private List<ReferenceData> realtyTypes;


    public MiscData() {
    }

    public MiscData(PeopleMisc peopleMisc) {
        this.realty = peopleMisc.getRealty().getCodeInteger();
        this.realtyDate = Convertor.dateToString(peopleMisc.getRealtyDate(), "dd / MM / yyyy");
        this.regDate = Convertor.dateToString(peopleMisc.getRegDate(), "dd / MM / yyyy");
        this.children = peopleMisc.getChildren();

        if (peopleMisc.getRegDateId() != null) {
            this.regDateId = peopleMisc.getRegDateId().getCodeInteger();
        }
        
        if (peopleMisc.getRealtyDateId() != null) {
            this.realtyDateId = peopleMisc.getRealtyDateId().getCodeInteger();
        }

        if (peopleMisc.getMarriage() != null) {
            this.marriage = peopleMisc.getMarriage().getCodeInteger();
        }
    }

    public Integer getRealty() {
        return realty;
    }

    public void setRealty(Integer realty) {
        this.realty = realty;
    }

    public String getRealtyDate() {
        return realtyDate;
    }

    public void setRealtyDate(String realtyDate) {
        this.realtyDate = realtyDate;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public Integer getRegDateId() {
        return regDateId;
    }

    public void setRegDateId(Integer regDateId) {
        this.regDateId = regDateId;
    }

    public List<ReferenceData> getMarriageTypes() {
        return marriageTypes;
    }

    public void setMarriageTypes(List<ReferenceData> marriageTypes) {
        this.marriageTypes = marriageTypes;
    }

    public List<ReferenceData> getRegDateTypes() {
        return regDateTypes;
    }

    public void setRegDateTypes(List<ReferenceData> regDateTypes) {
        this.regDateTypes = regDateTypes;
    }

	public Integer getRealtyDateId() {
		return realtyDateId;
	}

	public void setRealtyDateId(Integer realtyDateId) {
		this.realtyDateId = realtyDateId;
	}

    public List<ReferenceData> getRealtyTypes() {
        return realtyTypes;
    }

    public void setRealtyTypes(List<ReferenceData> realtyTypes) {
        this.realtyTypes = realtyTypes;
    }
}
