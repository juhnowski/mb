package ru.simplgroupp.webapp.analysis.plugin.controller;

import java.util.List;
import javax.faces.event.ActionEvent;

import ru.simplgroupp.ejb.VerificationPluginConfig;
import ru.simplgroupp.transfer.QAutoAnswer;
import ru.simplgroupp.util.QuestionUtil;

public class EditPluginVerifyController extends EditPluginExtController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7906975452432193520L;
	
	protected QAutoAnswer currentQA;
	
	@Override
	void reload() {
//		VerificationPluginConfig vplc = (VerificationPluginConfig) editCtl.plugin;
		
	}
	
	public List<QAutoAnswer> getAutoAnswers() {
		VerificationPluginConfig vplc = (VerificationPluginConfig) editCtl.plugin;
		return vplc.getAutoAnswers();
	}

	@Override
	void save() {
		VerificationPluginConfig vplc = (VerificationPluginConfig) editCtl.plugin;
		// TODO Auto-generated method stub

	}

	public QAutoAnswer getCurrentQA() {
		return currentQA;
	}

	public void setCurrentQA(QAutoAnswer currentQA) {
		this.currentQA = currentQA;
	}
	
	public void saveCurrentQA(ActionEvent event) {
		if (currentQA == null) {
			return;
		}
//		String qaKey = (String) event.getComponent().getAttributes().get("qaKey");
//		currentQA = QuestionUtil.findAutoAnswer(getAutoAnswers(), qaKey);
	}	
	
	public void loadCurrentQA(ActionEvent event) {
		String qaKey = (String) event.getComponent().getAttributes().get("qaKey");
		currentQA = QuestionUtil.findAutoAnswer(getAutoAnswers(), qaKey);
	}

}
