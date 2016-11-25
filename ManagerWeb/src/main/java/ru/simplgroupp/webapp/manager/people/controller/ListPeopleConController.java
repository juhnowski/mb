package ru.simplgroupp.webapp.manager.people.controller;

import javax.ejb.EJB;

import ru.simplgroupp.dao.impl.ListPMContainer;
import ru.simplgroupp.dao.impl.ListPQContainer;
import ru.simplgroupp.dao.interfaces.ListContainerDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListConFQController;

public class ListPeopleConController extends AbstractListConFQController<PeopleMain, ListPMContainer, ListPQContainer> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7657543337341243692L;
	
	@EJB
	PeopleDAO peopleDAO;

	@Override
	public ListContainerDAO<PeopleMain> getListContainerDAO() {
		return peopleDAO;
	}

	@Override
	protected PeopleMain modelGetRowObject(Integer rowObjectId) {
		return peopleDAO.getPeopleMain(rowObjectId, this.getData().getObjectOptions());
	}

	@Override
	public void initData() throws Exception {
		data = peopleDAO.genData(ListPMContainer.class);
		data.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_CREDIT, PeopleMain.Options.INIT_DOCUMENT));
		data.setSorting(new SortCriteria[] {new SortCriteria("Initials", true)});
		quick = peopleDAO.genData(ListPQContainer.class);
		quick.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT, PeopleMain.Options.INIT_CREDIT, PeopleMain.Options.INIT_DOCUMENT));
		quick.setSorting(new SortCriteria[] {new SortCriteria("Initials", true)});
		super.initData();
	}

}
