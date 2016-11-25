package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;
import java.util.Date;

public class PeopleMainData implements Serializable {
	
	 private String lastName;

	 private String firstName;

	 private String middleName;

	 private Date birthday;

	 private String placeOfBith;

	 private String gender;

	 private String phone;

	 private String phoneCode;

	 private String phoneHash;

	 private String email;

	 private String emailCode;

	 private String emailHash;
	 
	 private String inn;
	 
	 private String snils;

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPlaceOfBith() {
		return placeOfBith;
	}

	public void setPlaceOfBith(String placeOfBith) {
		this.placeOfBith = placeOfBith;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getPhoneHash() {
		return phoneHash;
	}

	public void setPhoneHash(String phoneHash) {
		this.phoneHash = phoneHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailCode() {
		return emailCode;
	}

	public void setEmailCode(String emailCode) {
		this.emailCode = emailCode;
	}

	public String getEmailHash() {
		return emailHash;
	}

	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
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
	 
}
