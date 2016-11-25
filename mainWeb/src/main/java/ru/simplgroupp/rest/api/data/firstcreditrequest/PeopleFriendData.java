package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleFriendData implements Serializable {

	private String firstName;
	private String lastName;
	private String phone;
	private boolean canCall;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isCanCall() {
		return canCall;
	}
	public void setCanCall(boolean canCall) {
		this.canCall = canCall;
	}
	
}
