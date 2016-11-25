package ru.simplgroupp.data;

/**
 * Step1Data
 * Данные передаваемые на первом шаге в json
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 22.04.14.
 */
public class Step2Data {

    private String
            realtyDate,
            gender,
            whenIssued,
            code_dep,
            birthplace,
            whogive,
            registrationFias,
            home,
            builder,
            korpus,
            apartment,
            inn,
            seria,
            nomer, snils, 
            source_from,
            first_vizit_date;
    private String
            databeg,
            typeworkTitle,
            regionTitle,
            lplaceTitle,
            areaTitle,
            cityTitle,
            streetTitle,
            marriageTitle,
            birthdate,
            mobilephone,
            midname,
            name,
            surname;
    private Integer
            children,
            marriage,
            typework,
            ga_visitor_id,
            visit_count;

    public Step2Data() {
        realtyDate = gender = seria = nomer = whenIssued = code_dep = birthplace = whogive = registrationFias = home = builder = korpus = apartment = inn = snils = source_from = first_vizit_date = "";
        typework = children = marriage = visit_count = ga_visitor_id = null;
        databeg = typeworkTitle = birthdate = mobilephone = midname = name = surname = regionTitle = lplaceTitle = areaTitle = cityTitle = streetTitle = marriageTitle = "";
    }

    public String getRealtyDate() {
        return realtyDate;
    }

    public void setRealtyDate(String realtyDate) {
        this.realtyDate = realtyDate;
    }

    public String getWhenIssued() {
        return whenIssued;
    }

    public void setWhenIssued(String whenIssued) {
        this.whenIssued = whenIssued;
    }

    public String getCode_dep() {
        return code_dep;
    }

    public void setCode_dep(String code_dep) {
        this.code_dep = code_dep;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getWhogive() {
        return whogive;
    }

    public void setWhogive(String whogive) {
        this.whogive = whogive;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getKorpus() {
        return korpus;
    }

    public void setKorpus(String korpus) {
        this.korpus = korpus;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSeria() {
        return seria;
    }

    public void setSeria(String seria) {
        this.seria = seria;
    }

    public String getNomer() {
        return nomer;
    }

    public void setNomer(String nomer) {
        this.nomer = nomer;
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

    public String getRegionTitle() {
        return regionTitle;
    }

    public void setRegionTitle(String regionTitle) {
        this.regionTitle = regionTitle;
    }

    public String getLplaceTitle() {
        return lplaceTitle;
    }

    public void setLplaceTitle(String lplaceTitle) {
        this.lplaceTitle = lplaceTitle;
    }

    public String getAreaTitle() {
        return areaTitle;
    }

    public void setAreaTitle(String areaTitle) {
        this.areaTitle = areaTitle;
    }

    public String getCityTitle() {
        return cityTitle;
    }

    public void setCityTitle(String cityTitle) {
        this.cityTitle = cityTitle;
    }

    public String getStreetTitle() {
        return streetTitle;
    }

    public void setStreetTitle(String streetTitle) {
        this.streetTitle = streetTitle;
    }

    public String getMarriageTitle() {
        return marriageTitle;
    }

    public void setMarriageTitle(String marriageTitle) {
        this.marriageTitle = marriageTitle;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getMidname() {
        return midname;
    }

    public void setMidname(String midname) {
        this.midname = midname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getTypework() {
        return typework;
    }

    public void setTypework(Integer typework) {
        this.typework = typework;
    }

    public String getTypeworkTitle() {
        return typeworkTitle;
    }

    public void setTypeworkTitle(String typeworkTitle) {
        this.typeworkTitle = typeworkTitle;
    }

    public String getDatabeg() {
        return databeg;
    }

    public void setDatabeg(String databeg) {
        this.databeg = databeg;
    }

    public String getRegistrationFias() {
        return registrationFias;
    }

    public void setRegistrationFias(String registrationFias) {
        this.registrationFias = registrationFias;
    }

	public String getSource_from() {
		return source_from;
	}

	public String setSource_from(String source_from) {
		this.source_from = source_from;
		return source_from;
	}

	public String getFirst_vizit_date() {
		return first_vizit_date;
	}

	public String setFirst_vizit_date(String first_vizit_date) {
		this.first_vizit_date = first_vizit_date;
		return first_vizit_date;
	}

	public Integer getGa_visitor_id() {
		return ga_visitor_id;
	}

	public Integer setGa_visitor_id(Integer ga_visitor_id) {
		this.ga_visitor_id = ga_visitor_id;
		return ga_visitor_id;
	}

	public Integer getVisit_count() {
		return visit_count;
	}

	public Integer setVisit_count(Integer visit_count) {
		this.visit_count = visit_count;
		return visit_count;
	}

	
}
