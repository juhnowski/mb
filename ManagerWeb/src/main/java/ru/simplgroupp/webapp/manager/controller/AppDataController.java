package ru.simplgroupp.webapp.manager.controller;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.Roles;
import ru.simplgroupp.webapp.controller.AbstractController;
import ru.simplgroupp.workflow.WorkflowObjectActionDef;

public class AppDataController extends AbstractController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 109103059989025727L;

	public final String[][] actionMap = new String[][] {
			{ CreditRequest.class.getName(), "msgChangeRequest", "link", "/views/creditrequest/edit_ch", "redirect", null},	
			{ CreditRequest.class.getName(), "msgEdit", "link", "/views/creditrequest/edit_cr", "redirect", null},
			{ CreditRequest.class.getName(), "msgRemove", "action", null, null, "Удалить заявку?"},
			{ Credit.class.getName(), "msgEdit", "link", "/views/credit/edit_cr", "redirect", null},
			{ Credit.class.getName(), "msgSuspend", "action", null, null, "Остановить кредит?"},
			{ Credit.class.getName(), "msgActivate", "action", null, null, "Запустить кредит?"},
			{ CreditRequest.class.getName(), "msgRestoreProcess", "action", null, null, "Восстановить процесс?"},
			{ CreditRequest.class.getName(), "msgClientReject", "action", null, null, "Выполнить отказ клиента?"},
			{ CreditRequest.class.getName(), "msgRestoreProcess1", "action", null, null, "Восстановить процесс?"},
			{ CreditRequest.class.getName(), "msgRestartProcess", "action", null, null, "Перезапустить процесс?"},
			{ Credit.class.getName(), "msgToCourt", "action", null, null, "Передать в суд?"},
			{ Payment.class.getName(), "msgCancelPaymentContact", "action", null, null, "Отменить платёж в Контакт"},
			{ Payment.class.getName(), "msgEdit", "link", "/views/payment/edit_pm", "redirect", null},
	};
	
	public int findActionMap(WorkflowObjectActionDef def) {
		for (int i = 0; i < actionMap.length; i++) {
			if (! compareLike(actionMap[i][0], def.getBusinessObjectClass())) {
				continue;
			}
			if (! compareLike(actionMap[i][1], def.getSignalRef())) {
				continue;
			}
			
			return i;
		}
		return -1;
	}
	
	public boolean getIsLink(int mapIndex) {
		return ("link".equals(actionMap[mapIndex][2]));
	}
	
	public String getOutcome(int mapIndex) {
		return actionMap[mapIndex][3];
	}
	
	public boolean getRedirect(int mapIndex) {
		return ("redirect".equals(actionMap[mapIndex][4]));
	}	
	
	public boolean getConfirm(int mapIndex) {
		return (! StringUtils.isBlank(actionMap[mapIndex][5]));
	}	
	
	public String getConfirmMessage(int mapIndex) {
		return actionMap[mapIndex][5];
	}
	
	public static boolean compareLike(String template, String value) {
		if (StringUtils.isBlank(template)) {
			return true;
		}
		
		if (StringUtils.isBlank(value)) {
			return false;
		}
		
		return template.equals(value);
	}
}
