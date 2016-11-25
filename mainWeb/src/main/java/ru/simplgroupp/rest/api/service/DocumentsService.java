package ru.simplgroupp.rest.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.OfficialDocumentsDAO;
import ru.simplgroupp.dao.interfaces.PeopleDAO;
import ru.simplgroupp.rest.api.data.OfficialDocumentsData;
import ru.simplgroupp.rest.api.data.ReferenceData;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.OfficialDocuments;
import ru.simplgroupp.transfer.PeopleMain;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DocumentsService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DocumentsService.class.getName());

    @EJB
    PeopleDAO peopleDAO;

    @EJB
    OfficialDocumentsDAO officialDocumentsDAO;

    @Inject
    private UserService userServ;

    public List<OfficialDocumentsData> getDocuments() {

        int peopleId = this.userServ.getUser().getPeopleMain().getId();
        PeopleMain peopleMain = peopleDAO.getPeopleMain(peopleId, Utils.setOf(PeopleMain.Options.INIT_OFFICIAL_DOCUMENT));
        List<OfficialDocuments> officialDocumentsList = peopleMain.getOfficialDocuments();

        if(officialDocumentsList == null) return null;

        List<OfficialDocumentsData> officialDocumentsDataList = new ArrayList<>();
        for(OfficialDocuments officialDocuments : officialDocumentsList) {

            OfficialDocumentsData officialDocumentsData = new OfficialDocumentsData();
            officialDocumentsData.setActive(officialDocuments.getIsActive());
            officialDocumentsData.setAnotherId(officialDocuments.getAnotherId());
            officialDocumentsData.setDocText(officialDocuments.getDocText());
            officialDocumentsData.setDocNumber(officialDocuments.getDocNumber());
            officialDocumentsData.setDocDate(officialDocuments.getDocDate());
            officialDocumentsData.setDateChange(officialDocuments.getDateChange());
            officialDocumentsData.setSmsCode(officialDocuments.getSmsCode());
            officialDocumentsData.setDocumentTypeId(new ReferenceData(String.valueOf(officialDocuments.getDocumentType().getId()), officialDocuments.getDocumentType().getName()));
            officialDocumentsData.setId(officialDocuments.getId());
            officialDocumentsDataList.add(officialDocumentsData);
        }
        return officialDocumentsDataList;
    }

    public OfficialDocuments getDocumentById(int id) {

        OfficialDocuments document = officialDocumentsDAO.getOfficialDocument(id, Utils.setOf());
        if (document != null && !document.getPeopleMain().getId().equals(this.userServ.getUser().getPeopleMain().getId())) {
            logger.warn("document's user id " + document.getPeopleMain().getId() + " doesn't match real user id " + this.userServ.getUser().getPeopleMain().getId());
            return null;
        }
        return document;
    }

}
