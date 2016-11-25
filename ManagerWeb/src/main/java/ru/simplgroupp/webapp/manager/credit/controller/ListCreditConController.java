package ru.simplgroupp.webapp.manager.credit.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import ru.simplgroupp.dao.impl.ListCContainer;
import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.ListContainerDAO;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.BaseCredit;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListConFQController;
import ru.simplgroupp.webapp.controller.ListConController;

public class ListCreditConController extends AbstractListConFQController<Credit, ListCContainer, ListCContainer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7657543337341543192L;
	
	@EJB
	CreditDAO creditDAO;

	@Override
	public ListContainerDAO<Credit> getListContainerDAO() {
		return creditDAO;
	}

	@Override
	protected Credit modelGetRowObject(Integer rowObjectId) {
		return creditDAO.getCredit(rowObjectId, this.getData().getObjectOptions());
	}

	@Override
	public void initData() throws Exception {
		data = creditDAO.genData(ListCContainer.class);
		data.setObjectOptions(Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST,PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_DOCUMENT));
		data.setSorting(new SortCriteria[] {new SortCriteria("CreditDataBeg", false)});
		quick = creditDAO.genData(ListCContainer.class);
		quick.setObjectOptions(Utils.setOf(BaseCredit.Options.INIT_CREDIT_REQUEST,PeopleMain.Options.INIT_PEOPLE_PERSONAL,PeopleMain.Options.INIT_DOCUMENT));
		quick.setSorting(new SortCriteria[] {new SortCriteria("CreditDataBeg", false)});
		super.initData();
	}

}
