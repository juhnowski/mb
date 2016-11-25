package ru.simplgroupp.webapp.manager.controller;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.persistence.reports.model.*;
import ru.simplgroupp.reports.dao.FilterHelperDao;
import ru.simplgroupp.reports.date.util.DateUtil;
import ru.simplgroupp.reports.service.*;
import ru.simplgroupp.reports.wrapper.ReportCompiler;
import ru.simplgroupp.webapp.controller.AbstractSessionController;
import ru.simplgroupp.webapp.util.JSFUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class ReportsController extends AbstractSessionController implements Serializable {
    private int[] reportIntervalTypes =new int[]{Calendar.DATE,Calendar.WEEK_OF_MONTH,Calendar.MONTH,Calendar.YEAR,-1};
    private String place;
    private boolean city = false;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    private List<RegionSearchModel> regions = new ArrayList<RegionSearchModel>();
    private List<PlaceSearchModel> places = new ArrayList<PlaceSearchModel>();
    private List<ProfessionSearchModel> professions = new ArrayList<ProfessionSearchModel>();
    private List<GenderSearchModel> genders = new ArrayList<GenderSearchModel>();
    private List<MarriageSearchModel> marriages = new ArrayList<MarriageSearchModel>();
    private List<ManagerSearchModel> managers = new ArrayList<ManagerSearchModel>();
    private List<ProductSearchModel> products = new ArrayList<ProductSearchModel>();
    private List<PaymentWaySearchModel> paymentWays = new ArrayList<PaymentWaySearchModel>();
    private List<ChannelSearchModel> channels = new ArrayList<ChannelSearchModel>();

    public List<ChannelSearchModel> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelSearchModel> channels) {
        this.channels = channels;
    }

    public List<ProductSearchModel> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSearchModel> products) {
        this.products = products;
    }

    public List<PaymentWaySearchModel> getPaymentWays() {
        return paymentWays;
    }

    public void setPaymentWays(List<PaymentWaySearchModel> paymentWays) {
        this.paymentWays = paymentWays;
    }

    public List<ManagerSearchModel> getManagers() {
        return managers;
    }

    public void setManagers(List<ManagerSearchModel> managers) {
        this.managers = managers;
    }

    public List<RegionSearchModel> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionSearchModel> regions) {
        this.regions = regions;
    }

    public List<PlaceSearchModel> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceSearchModel> places) {
        this.places = places;
    }

    public List<GenderSearchModel> getGenders() {
        return genders;
    }

    public void setGenders(List<GenderSearchModel> genders) {
        this.genders = genders;
    }

    public List<MarriageSearchModel> getMarriages() {
        return marriages;
    }

    public void setMarriages(List<MarriageSearchModel> marriages) {
        this.marriages = marriages;
    }

    public int[] getReportIntervalTypes(){
        return reportIntervalTypes;
    }

    public void setReportIntervalTypes(int[] reportIntervalTypes) {
        this.reportIntervalTypes = reportIntervalTypes;
    }

    public List<ProfessionSearchModel> getProfessions() {
        return professions;
    }

    public void setProfessions(List<ProfessionSearchModel> professions) {
        this.professions = professions;
    }

    private String region;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getManager() {
        return manager;
    }

    public void setManager(int manager) {
        this.manager = manager;
    }

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final long serialVersionUID = -2189105267003064281L;
    private int reportType=0;
    private int term;
    public String reportText= "";
    private Date start;
    private Date end;
    private int minAge;
    private int maxAge;
    private String gender;
    private int marriage;
    private int profession;
    private double minSalary;
    private double maxSalary;
    private int minSalaryOption;
    private int manager;
    private int product;
    private int paymentWay;
    private String channel;//канал привлечения

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(int paymentWay) {
        this.paymentWay = paymentWay;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @EJB
    private ReportCompiler reportCompiler;
    @EJB
    private GetPaymentsDataService getPaymentsDataService;
    @EJB
    private GetReportsDataService getReportDataService;
    @EJB
    private GetProlongationsReportDataService getProlongationsReportDataService;
    @EJB
    private GetClosedCreditsReportsDataService getClosedCreditsReportsDataService;
    @EJB
    private GetReportPortfailDataService getReportPortfailDataService;
    @EJB
    private GetCollectorsReportsDataService getCollectorsReportsDataService;
    @EJB
    private FilterHelperDao filterHelperDao;
    public int getMinSalaryOption() {
        return minSalaryOption;
    }

    public void setMinSalaryOption(int minSalaryOption) {
        this.minSalaryOption = minSalaryOption;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getReportText() {
        return reportText;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getMarriage() {
        return marriage;
    }

    public void setMarriage(int marriage) {
        this.marriage = marriage;
    }

    public int getProfession() {
        return profession;
    }

    public void setProfession(int profession) {
        this.profession = profession;
    }

    public double getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(double minSalary) {
        this.minSalary = minSalary;
    }

    public double getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(double maxSalary) {
        this.maxSalary = maxSalary;
    }

    @PostConstruct
    public void init() {
        FacesContext facesCtx = FacesContext.getCurrentInstance();
        term = reportIntervalTypes[0];
        end = new Date();
        start = DateUtil.getDateMinusInterval(end, -1, term);
        minSalary = -1;
        maxSalary = -1;
        gender = "";
        marriage = -1;
        profession = -1;
        manager = -1;
        minAge = 18;
        maxAge = 120;
        region = "";
        List<ProfessionSearchModel> professionSearchModels =  filterHelperDao.getAllProfessions();
        professions = new ArrayList<ProfessionSearchModel>(professionSearchModels.size() + 1);
        professions.add(new ProfessionSearchModel(-1,"Любая"));
        professions.addAll(professionSearchModels);
        List<GenderSearchModel> gendersSearchModels =  filterHelperDao.getAllGenders();
        genders = new ArrayList<GenderSearchModel>(gendersSearchModels.size() + 1);
        genders.add(new GenderSearchModel("","Любой"));
        genders.addAll(gendersSearchModels);
        List<MarriageSearchModel> marriageSearchModels =  filterHelperDao.getAllMarriages();
        marriages = new ArrayList<MarriageSearchModel>(marriageSearchModels.size() + 1);
        marriageSearchModels.add(new MarriageSearchModel(-1, "Не важно"));
        marriages.addAll(marriageSearchModels);
        minSalaryOption = 0;
        List<RegionSearchModel> regionSearchModels = filterHelperDao.getAllRegions();
        regions = new ArrayList<RegionSearchModel>(regionSearchModels.size() + 1);
        regions.add(new RegionSearchModel("","Любой"));
        regions.addAll(regionSearchModels);
        places = new ArrayList<PlaceSearchModel>();
        places.add(new PlaceSearchModel("","Любой"));
        place = "";
        List<ManagerSearchModel> managerSearchModels = filterHelperDao.getAllManagers();
        managers = new ArrayList<ManagerSearchModel>(managerSearchModels.size() + 1);
        managers.add(new ManagerSearchModel(-1,"Любой"));
        managers.addAll(managerSearchModels);
        product = -1;
        List<ProductSearchModel> productSearchModels = filterHelperDao.getAllProducts();
        products = new ArrayList<ProductSearchModel>(productSearchModels.size() + 1);
        products.add(new ProductSearchModel(-1,"Любой"));
        products.addAll(productSearchModels);

        paymentWay = -1;
        List<PaymentWaySearchModel> paymentWaySearchModels = filterHelperDao.getAllPaymentWays();
        paymentWays = new ArrayList<PaymentWaySearchModel>(paymentWays.size() + 1);
        paymentWays.add(new PaymentWaySearchModel(-1, "Любой"));
        paymentWays.addAll(paymentWaySearchModels);

        channel = "";
        List<ChannelSearchModel> channelSearchModels = filterHelperDao.getAllChannels();
        channels = new ArrayList<ChannelSearchModel>(channelSearchModels.size() + 1);
        channels.add(new ChannelSearchModel("Любой",""));
        channels.addAll(channelSearchModels);



    }
    public void generateReport(boolean file) {
       // logger.error("place "+place);
        //logger.error("min age "+minAge+"max age "+maxAge+"minSalary "+maxSalary+"gender "+gender+"marriage "+marriage+"profession "+profession
                //+" manager "+manager+" product "+product+" paymentway "+paymentWay+" channel "+channel+"region "+region+" "+place);
       // logger.error("min age "+minAge+"max age "+maxAge+"minSalary "+maxSalary+"gender "+gender+"marriage "+marriage+"profession "+profession
         //       +" manager "+manager+" product "+product+" paymentway "+paymentWay+" channel "+channel+"region "+region+"place "+place+"city "+city);
        if ((start != null) && (end != null)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            start = calendar.getTime();
            if (term == -1) {
                calendar = Calendar.getInstance();
                calendar.setTime(end);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 0);
                end = calendar.getTime();
            }
           // logger.error("date start "+start);
            //logger.error("date end "+end);
        }
        if ((start == null) || (end == null) || (start.compareTo(end) > 0)) {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            //logger.error("ошибка  дат");
            JSFUtils.handleAsError(facesCtx, null, new Exception("Неправильно введены даты"));
        }
        Object reportModel;
        List data = new ArrayList();
        String pathToReport="requests.jasper";
        //logger.error("report type is "+reportType);
        switch (reportType) {
            case 0:
                 reportModel = getReportDataService.getCreditsStatistics(start,end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "requests.jasper";
                break;
            case 1:
                reportModel = getPaymentsDataService.getPaymentStatistics(start, end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "payments.jasper";
                break;
            case 2:
                reportModel = getProlongationsReportDataService.getProlongationsReportData(start, end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "prolongations.jasper";
                break;
            case 3:
                reportModel = getClosedCreditsReportsDataService.getClosedCreditReports(start, end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "closed.jasper";
                break;
            case 4:
                reportModel = getReportPortfailDataService.getPortfelReportDate(start,end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "portfail.jasper";
                break;
            case 5:
                reportModel = getCollectorsReportsDataService.getCollectorsReportData(start,end,minAge,maxAge,gender,marriage,profession,minSalary,maxSalary,manager,product,region,city ? place : "",city ? "" : place,channel,paymentWay);
                data.add(reportModel);
                pathToReport = "penalties.jasper";
                break;
            case 6:
                try {
                   data = getReportDataService.getCreditRequestHistory(start,end);
                    pathToReport = "requests_list.jasper";
                } catch (Exception e) {
                    logger.error("reports exception",e);
                    return;
                }
                break;
            case 7:
                try {
                    data = getReportDataService.getCreditsHistory(start,end);
                    pathToReport = "credits.jasper";
                } catch (Exception e) {
                    logger.error("reports exception",e);
                    return;
                }
                break;
            case 8:
                try {
                    data = getPaymentsDataService.getPaymentsHistory(start,end);
                    pathToReport = "payments_history.jasper";
                } catch (Exception e) {
                    logger.error("reports exception",e);
                    return;
                }
                break;
            default:
                break;
        }
        try {
            if (!file) {
                reportText = reportCompiler.compile(pathToReport, data);
                logger.error("compiled");
            }
            else {
                FacesContext ctx = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
                response.reset();
                response.setHeader("Content-Type",  "application/vnd.ms-excel; charset=utf-8");
                response.setHeader("Content-Disposition", "filename=" + "report.xls" );
                OutputStream outputStream = response.getOutputStream();
                reportCompiler.exportToOutputStream(pathToReport,data,outputStream);
                outputStream.close();
                ctx.responseComplete();
                //logger.error("download completed");
            }
        } catch (JRException e) {
            logger.error("Ошибка компиляции отчета",e);
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            JSFUtils.handleAsError(facesCtx, null, new Exception("Ошибка компиляции отчета"));
        } catch (IOException e) {
            logger.error("",e);
        }

    }
    public void reportTypeChanged(){
        //logger.error("report type is "+reportType);
    }
    public void dateTypeChanged(){
        if (term != -1) {
            end = new Date();
            start = DateUtil.getDateMinusInterval(end, -1, term);
        }
        else {
        }
    }
    public void onDateStartSet(){
        if (start.compareTo(end) > 0) {
            end = start;
        }
    }
    public void onDateEndSet(){
        if (end.compareTo(start) < 0) {
            start = end;
        }
    }
    public void onMinAgeSet(){
        if (maxAge < minAge) {
            maxAge = minAge;
        }
    }
    public void onMaxAgeSet(){
        if (maxAge < minAge) {
            minAge = maxAge;
        }
    }
    public void onMinSalarySet(){
        if (maxSalary < minSalary) {
            maxSalary = minSalary;
        }
    }
    public void onMaxSalarySet(){
        if (maxSalary < minSalary) {
            minSalary = maxSalary;
        }
    }
    public void minSalaryOptionChanged() {
        if (minSalaryOption == 0) {
            minSalary = -1;
            maxSalary = -1;
        }
        else if (minSalaryOption == 1) {
            minSalary = 5000;
            maxSalary = 200000;
        }
    }
    public void onPlaceSet() {
        for (PlaceSearchModel placeSearchModel : places) {
            if (placeSearchModel.getPlace().equals(place)){
                city = placeSearchModel.isCity();
               // logger.error("place is "+place);
            }
        }
    }
    public void onRegionSet(){
        places = new ArrayList<PlaceSearchModel>();
        places.add(new PlaceSearchModel("","Любой"));
        place = "";
        if (!region.equals("")){
            List<PlaceSearchModel> cities = filterHelperDao.getAllCitiesForRegion(region);
           // logger.error(""+cities.size()+" "+region);
            List<PlaceSearchModel> placeSearchModels = filterHelperDao.getAllPlacesForRegion(region);
            //logger.error(""+placeSearchModels.size()+" "+region);
            for (PlaceSearchModel city : cities){
             city.setCity(true);
            }
            for (PlaceSearchModel place : placeSearchModels){
                place.setCity(false);
            }
            places.addAll(cities);
            places.addAll(placeSearchModels);
        }
    }



}
