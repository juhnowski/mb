package ru.simplgroupp.webapp.manager.payment.controller;

import org.ajax4jsf.model.SequenceRange;

import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.ejb.PaymentFilter;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.toolkit.common.SortCriteria;
import ru.simplgroupp.toolkit.common.Utils;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.Payment;
import ru.simplgroupp.transfer.PeopleMain;
import ru.simplgroupp.webapp.controller.AbstractListController;
import ru.simplgroupp.webapp.controller.IEventListener;
import ru.simplgroupp.webapp.exception.WebAppException;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.List;

/**
 * Контроллер списка платежей
 */
public class ListPaymentsController extends AbstractListController implements Serializable, IEventListener {

    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7591799682909267026L;

	@EJB
    protected PaymentService paymentService;
    
    @EJB
    protected PaymentDAO payDAO;

    private PaymentFilter filter = new PaymentFilter();

    private boolean searchFull = false;

    public class PaymentDataModel extends StdRequestDataModel<Payment> {

        public PaymentDataModel() {
            super();
            multipleSorting = true;
        }

        @Override
        protected List<Payment> internalList(SequenceRange seqRange,
                                             SortCriteria[] sorting) throws WebAppException {
        	
            return paymentService.findPayments(seqRange.getFirstRow(), seqRange.getRows(), sorting, null, filter);
        }

        @Override
        protected int internalRowCount() throws WebAppException {
        	
            return paymentService.countPayments(filter);
        }

        @Override
        public Payment getRowData() {
            if (rowKey == null)
                return null;
            else {
                return payDAO.getPayment(((Number) rowKey).intValue(),
                        Utils.setOf(Credit.Options.INIT_CREDIT_REQUEST, PeopleMain.Options.INIT_PEOPLE_PERSONAL));
            }
        }
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public StdRequestDataModel createModel() {
        return new PaymentDataModel();
    }

    @Override
    public void eventFired(String eventName, Object eventSource) throws Exception {

    }

    public String dummy() {
        return null;
    }

    public PaymentFilter getFilter() {
        return filter;
    }

    public void setFilter(PaymentFilter filter) {
        this.filter = filter;
    }

    public boolean isSearchFull() {
        return searchFull;
    }

    public void setSearchFull(boolean searchFull) {
        this.searchFull = searchFull;
    }

    public void clearLsn(ActionEvent event) {
        filter = new PaymentFilter();
        refreshSearch();
    }
}