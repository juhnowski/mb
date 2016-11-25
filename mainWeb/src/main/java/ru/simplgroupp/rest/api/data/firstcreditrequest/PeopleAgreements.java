package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleAgreements implements Serializable {

	private boolean dogovorAgree;
	//private boolean deliveryAgree;
	private boolean useInfoAgree;
	public boolean isDogovorAgree() {
		return dogovorAgree;
	}
	public void setDogovorAgree(boolean dogovorAgree) {
		this.dogovorAgree = dogovorAgree;
	}
	/*public boolean isDeliveryAgree() {
		return deliveryAgree;
	}
	public void setDeliveryAgree(boolean deliveryAgree) {
		this.deliveryAgree = deliveryAgree;
	}*/
	public boolean isUseInfoAgree() {
		return useInfoAgree;
	}
	public void setUseInfoAgree(boolean useInfoAgree) {
		this.useInfoAgree = useInfoAgree;
	}
}
