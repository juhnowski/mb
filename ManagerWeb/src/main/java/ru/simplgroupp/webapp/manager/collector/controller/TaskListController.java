package ru.simplgroupp.webapp.manager.collector.controller;

import ru.simplgroupp.dao.impl.ListCollectorContainer;
import ru.simplgroupp.dao.interfaces.CollectorDAO;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.ActiveStatus;
import ru.simplgroupp.transfer.Collector;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;

/**
 * 14.09.2015
 * 10:54
 */

public class TaskListController extends AbstractSessionController implements Serializable {
    @EJB
    private CollectorDAO collectorDAO;

    private List<Collector> newTasksList;
    private List<Collector> activeTasksList;

    @PostConstruct
    public void init() {
        super.init();
        reloadNewRequestList();
        reloadActiveRequestList();
    }

    private void reloadActiveRequestList() {
        ListCollectorContainer container = collectorDAO.genData(ListCollectorContainer.class);
        container.setPrmIsActive(ActiveStatus.ACTIVE);
        container.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        activeTasksList = collectorDAO.listData(-1, -1, container);
    }

    public void reloadNewRequestList() {
        ListCollectorContainer container = collectorDAO.genData(ListCollectorContainer.class);
        container.setPrmIsActive(ActiveStatus.DRAFT);
        container.setObjectOptions(Utils.setOf(PeopleMain.Options.INIT_PEOPLE_PERSONAL));
        newTasksList = collectorDAO.listData(-1, -1, container);
    }

    public List<Collector> getNewTasksList() {
        return newTasksList;
    }

    public void setNewTasksList(List<Collector> newTasksList) {
        this.newTasksList = newTasksList;
    }

    public List<Collector> getActiveTasksList() {
        return activeTasksList;
    }

    public void setActiveTasksList(List<Collector> activeTasksList) {
        this.activeTasksList = activeTasksList;
    }
}
