package ru.simplgroupp.rest.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.AntifraudDAO;
import ru.simplgroupp.interfaces.AntifraudBeanLocal;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.rest.api.data.antifraud.AntifraudField;
import ru.simplgroupp.rest.api.data.antifraud.AntifraudPage;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;

@RequestScoped
public class AntifraudService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(AntifraudService.class.getName());

    private AntifraudDAO antifraudDAO;

    @EJB
    private AntifraudBeanLocal antifraudBean;

    public void addField(AntifraudField field) {

        Integer creditRequestId = null;

        if(field.getRequestId() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(field.getRequestId());
        }

        antifraudDAO.saveAntifraudField(field.getFieldName(), field.getFieldValueBefore(), field.getFieldValueAfter(), creditRequestId, field.getSessionId());
    }

    public void addPage(AntifraudPage page) {

        Integer creditRequestId = null;

        if(page.getRequestId() != null) {
            creditRequestId = StaticFuncs.decodeIdFromHash(page.getRequestId());
        }

        antifraudDAO.saveAntifraudPage(page.getPageName(), page.isLeaving(), creditRequestId, page.getSessionId());
    }

    public AntifraudService() {
    }

    @Inject
    public AntifraudService(AntifraudDAO antifraudDAO) {
        this.antifraudDAO = antifraudDAO;
    }
}
