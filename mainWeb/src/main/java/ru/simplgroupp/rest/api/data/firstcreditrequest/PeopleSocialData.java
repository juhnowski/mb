package ru.simplgroupp.rest.api.data.firstcreditrequest;

import java.io.Serializable;

public class PeopleSocialData implements Serializable {
	private boolean hasVk;

    private boolean hasMoyMir;

    private boolean hasFacebook;

    private boolean hasOdnoklassniki;

	public boolean isHasVk() {
		return hasVk;
	}

	public void setHasVk(boolean hasVk) {
		this.hasVk = hasVk;
	}

	public boolean isHasMoyMir() {
		return hasMoyMir;
	}

	public void setHasMoyMir(boolean hasMoyMir) {
		this.hasMoyMir = hasMoyMir;
	}

	public boolean isHasFacebook() {
		return hasFacebook;
	}

	public void setHasFacebook(boolean hasFacebook) {
		this.hasFacebook = hasFacebook;
	}

	public boolean isHasOdnoklassniki() {
		return hasOdnoklassniki;
	}

	public void setHasOdnoklassniki(boolean hasOdnoklassniki) {
		this.hasOdnoklassniki = hasOdnoklassniki;
	}

}
