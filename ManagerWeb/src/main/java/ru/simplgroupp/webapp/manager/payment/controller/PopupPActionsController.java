package ru.simplgroupp.webapp.manager.payment.controller;

import java.io.Serializable;

import javax.ejb.EJB;

import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.webapp.manager.controller.AbstractPopupActionController;

public class PopupPActionsController extends AbstractPopupActionController<Payment> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7657543356825543692L;
	
	@EJB
	PaymentDAO payDAO;

	@Override
	protected void reloadData() throws KassaException {
		businessObject = payDAO.getPayment(prmBusinessObjectId, Utils.setOf());
	}

}
