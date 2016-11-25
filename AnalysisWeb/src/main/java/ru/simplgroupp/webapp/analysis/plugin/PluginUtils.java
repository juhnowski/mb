package ru.simplgroupp.webapp.analysis.plugin;

import ru.simplgroupp.interfaces.ScoringEquifaxBeanLocal;
import ru.simplgroupp.interfaces.ScoringNBKIBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbCaisBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbHunterBeanLocal;
import ru.simplgroupp.interfaces.ScoringOkbIdvBeanLocal;
import ru.simplgroupp.interfaces.ScoringRsBeanLocal;
import ru.simplgroupp.interfaces.ScoringSkbBeanLocal;
import ru.simplgroupp.interfaces.ScoringSociohubBeanLocal;
import ru.simplgroupp.interfaces.VerificationBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.ContactAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.ContactCancelPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.ContactPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.PayonlineAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.PayonlinePayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.QiwiAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.QiwiPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.WinpayAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.WinpayPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexAcquiringBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexIdentificationPayBeanLocal;
import ru.simplgroupp.interfaces.plugins.payment.YandexPayBeanLocal;
import ru.simplgroupp.webapp.analysis.plugin.controller.AbstractEditPluginController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginContactAcqController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginContactPayController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginEFController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginExtController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginNbkiController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginOkbCaisController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginOkbHunterController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginOkbIdvController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginPayAcqController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginPayonlinePayController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginQiwiAcqController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginQiwiPayController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginRsController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginSkbController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginSociohubController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginVerifyController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginWinPayController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginWinpayAcqController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginYandexAcqController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginYandexPayController;
import ru.simplgroupp.webapp.analysis.plugin.controller.EditPluginYandexVerController;

public class PluginUtils {
	public static EditPluginExtController attachExtCtl(String pluginName, AbstractEditPluginController parentCtl) {
		if (VerificationBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginVerifyController extCtl = new EditPluginVerifyController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ScoringRsBeanLocal.SYSTEM_NAME.endsWith(pluginName)) { 
			EditPluginRsController extCtl = new EditPluginRsController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ScoringSkbBeanLocal.SYSTEM_NAME.endsWith(pluginName)) { 
			EditPluginSkbController extCtl = new EditPluginSkbController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ScoringNBKIBeanLocal.SYSTEM_NAME.endsWith(pluginName)) { 
			EditPluginNbkiController extCtl = new EditPluginNbkiController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ScoringSociohubBeanLocal.SYSTEM_NAME.endsWith(pluginName)) { 
			EditPluginSociohubController extCtl = new EditPluginSociohubController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		}else if (ScoringEquifaxBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginEFController extCtl = new EditPluginEFController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ScoringOkbCaisBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginOkbCaisController extCtl = new EditPluginOkbCaisController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;		
		} else if (ScoringOkbIdvBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginOkbIdvController extCtl = new EditPluginOkbIdvController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (ContactAcquiringBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginContactAcqController extCtl = new EditPluginContactAcqController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (ContactPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginContactPayController extCtl = new EditPluginContactPayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (ContactCancelPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginContactPayController extCtl = new EditPluginContactPayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (PayonlineAcquiringBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginPayAcqController extCtl = new EditPluginPayAcqController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (QiwiAcquiringBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginQiwiAcqController extCtl = new EditPluginQiwiAcqController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (QiwiPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginQiwiPayController extCtl = new EditPluginQiwiPayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (WinpayPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginWinPayController extCtl = new EditPluginWinPayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (WinpayAcquiringBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginWinpayAcqController extCtl = new EditPluginWinpayAcqController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;
		} else if (YandexAcquiringBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginYandexAcqController extCtl = new EditPluginYandexAcqController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (YandexPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginYandexPayController extCtl = new EditPluginYandexPayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (YandexIdentificationPayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginYandexVerController extCtl = new EditPluginYandexVerController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (PayonlinePayBeanLocal.SYSTEM_NAME.endsWith(pluginName)) {
			EditPluginPayonlinePayController extCtl = new EditPluginPayonlinePayController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;				
		} else if (ScoringOkbHunterBeanLocal.SYSTEM_NAME.endsWith(pluginName)){
			EditPluginOkbHunterController extCtl=new EditPluginOkbHunterController();
			parentCtl.setExtCtl(extCtl);
			extCtl.setEditCtl(parentCtl);
			return extCtl;		
		} else {
			return null;
		} 
		
	}
}
