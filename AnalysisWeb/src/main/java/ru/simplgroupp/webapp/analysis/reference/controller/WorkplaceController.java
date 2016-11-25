package ru.simplgroupp.webapp.analysis.reference.controller;

import ru.simplgroupp.interfaces.service.WorkplaceService;
import ru.simplgroupp.persistence.WorkplaceEntity;
import ru.simplgroupp.webapp.controller.AbstractSessionController;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.List;

/**
 * Workplace controller
 */
@ManagedBean
@ViewScoped
public class WorkplaceController extends AbstractSessionController implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5681172044818005434L;

	@EJB
    private WorkplaceService workplaceService;

    private List<WorkplaceEntity> workplaces;

    private String name;

    @PostConstruct
    public void init(){
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        if (!facesCtx.isPostback() && !facesCtx.isValidationFailed()){
            workplaces = workplaceService.findAll();
        }
    }

    public void saveAll(ActionEvent event){
        workplaceService.save(workplaces);
        workplaces = workplaceService.findAll();
    }

    public void create(ActionEvent event){
        WorkplaceEntity workplace = new WorkplaceEntity();
        workplace.setName(name);
        workplaceService.save(workplace);
        workplaces = workplaceService.findAll();
    }

    public List<WorkplaceEntity> getWorkplaces() {
        return workplaces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
