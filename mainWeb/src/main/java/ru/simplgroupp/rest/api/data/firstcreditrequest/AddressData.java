package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

/**
 * Адрес
 */
public class AddressData implements Serializable {
	
	private String registrationFias;
    private String home;
    private String korpus;
    private String builder;
    private String apartment;

	public String getRegistrationFias() {
		return registrationFias;
	}

	public void setRegistrationFias(String registrationFias) {
		this.registrationFias = registrationFias;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getKorpus() {
		return korpus;
	}

	public void setKorpus(String korpus) {
		this.korpus = korpus;
	}

	public String getBuilder() {
		return builder;
	}

	public void setBuilder(String builder) {
		this.builder = builder;
	}

	public String getApartment() {
		return apartment;
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}

}