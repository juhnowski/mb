package ru.simplgroupp.webapp.manager.creditrequest.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import ru.simplgroupp.dao.impl.ListCRContainer;
import ru.simplgroupp.dao.interfaces.CreditRequestDAO;
import ru.simplgroupp.dao.interfaces.ListContainerDAO;
import ru.simplgroupp.interfaces.WorkflowEngineBeanLocal;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.CreditRequest;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.util.UserUtil;
import ru.simplgroupp.webapp.controller.AbstractListConFQController;
import ru.simplgroupp.webapp.controller.IEventListener;
import ru.simplgroupp.workflow.WorkflowObjectStateDef;

public class ListCreditRequestConController extends AbstractListConFQController<CreditRequest, ListCRContainer, ListCRContainer> implements IEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7657543337341543692L;

	@EJB
	CreditRequestDAO crDAO;
	
    @EJB
    WorkflowEngineBeanLocal wfEngine;		
	
	protected List<WorkflowObjectStateDef> prmTaskDefValues;
	
	@PostConstruct
	public void init() {
		data = crDAO.genData(ListCRContainer.class);
		data.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT));
		data.setSorting(new SortCriteria[] {new SortCriteria("DateContest", false)});
		quick = crDAO.genData(ListCRContainer.class);
		quick.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT));
		quick.setSorting(new SortCriteria[] {new SortCriteria("DateContest", false)});
		super.init();
	}	

	@Override
	public ListContainerDAO<CreditRequest> getListContainerDAO() {
		return crDAO;
	}

	@Override
	protected CreditRequest modelGetRowObject(Integer rowObjectId) {
		return crDAO.getCreditRequest(rowObjectId, this.getData().getObjectOptions());
	}

	@Override
	public void initData() throws Exception {
		FacesContext facesCtx = FacesContext.getCurrentInstance();
		
		Map<String, String> prms = facesCtx.getExternalContext().getRequestParameterMap();
		if (prms.get("taskDef") != null) {
			data.setTaskDefKey( prms.get("taskDef").replace('-', ':') );			
		}
		
		List<String> roleNames = UserUtil.rolesToStrings(userCtl.getUser().getRoles());
		prmTaskDefValues = wfEngine.listTaskDefs(null, roleNames , CreditRequest.class.getName(), null);	
		super.initData();
	}
	
	public List<WorkflowObjectStateDef> getPrmTaskDefValues() {
		return prmTaskDefValues;
	}

	@Override
	public void eventFired(String eventName, Object eventSource)
			throws Exception {
		// TODO Auto-generated method stub
		
	}	

}