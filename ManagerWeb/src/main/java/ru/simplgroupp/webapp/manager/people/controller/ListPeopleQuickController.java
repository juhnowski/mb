package ru.simplgroupp.webapp.manager.people.controller;

import javax.ejb.EJB;

import ru.simplgroupp.dao.impl.ListPeopleOfflineContainer;
import ru.simplgroupp.dao.interfaces.ListContainerDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListConController;

public class ListPeopleQuickController extends AbstractListConController<PeopleMain, ListPeopleOfflineContainer> {
	
	private static final long serialVersionUID = -2525271735756768793L;
	
	@EJB
	PeopleDAO peopleDAO;	

	@Override
	public ListContainerDAO<PeopleMain> getListContainerDAO1() {
		return peopleDAO;
	}

	@Override
	protected PeopleMain modelGetRowObject(Integer rowObjectId) {
		return peopleDAO.getPeopleMain(rowObjectId, this.getData().getObjectOptions());
	}

	@Override
	public void initData() throws Exception {
		data = peopleDAO.genData(ListPeopleOfflineContainer.class);
		data.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL, PeopleMain.Options.INIT_PEOPLE_CONTACT, 
				PeopleMain.Options.INIT_CREDIT,PeopleMain.Options.INIT_DOCUMENT));
		data.setSorting(new SortCriteria[] {new SortCriteria("Initials", true)});

	}

}
