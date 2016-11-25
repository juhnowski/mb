package ru.simplgroupp.rest;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.data.*;
import ru.simplgroupp.data.Step2Data;
import ru.simplgroupp.fias.ejb.IFIASService;
import ru.simplgroupp.interfaces.KassaBeanLocal;
import ru.simplgroupp.interfaces.MailBeanLocal;
import ru.simplgroupp.interfaces.ReferenceBooksLocal;
import ru.simplgroupp.interfaces.RulesBeanLocal;
import ru.simplgroupp.interfaces.service.CreditRequestWizardService;
import ru.simplgroupp.interfaces.service.OrganizationService;
import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.toolkit.common.Convertor;
import ru.simplgroupp.transfer.creditrequestwizard.*;
import ru.simplgroupp.transfer.creditrequestwizard.Step6Data;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Возвращает информацию о заполненных полях при шагах назад
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 22.04.14.
 */
@Singleton
@Path("/data")
public class CreditRequestWizardGetController {

    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

    @EJB
    protected CreditRequestWizardService wizardService;

    @EJB
    protected KassaBeanLocal kassaBean;

    @EJB
    protected IFIASService fiasBean;

    @EJB
    protected ReferenceBooksLocal referenceBean;

    @EJB
    protected MailBeanLocal mailBean;

    @EJB
    protected RulesBeanLocal rulesBean;

    @EJB
    OrganizationService orgService;
    
    /**
     * Выбираем данные для показа на шаге 0
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step0")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step0(Data data) {
        Integer requestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());

        Data answer = new Data();
        answer.setStatus(true);

        Integer stepCount = 0;
        if (requestId > 0) {
            stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), requestId);
            if (stepCount >= 8 || stepCount < 0) {
                Data.setErrorRedirect();
                return Data.setError(); // нельзя ничего показывать
            }
        }
        answer.setRequest_id(StaticFuncs.generateHashId(requestId));
        answer.setSession_id(StaticFuncs.generateSessionId(requestId, stepCount));
        answer.setStepcount(stepCount);

        Step0Data info = wizardService.step0(requestId, stepCount >= 0);
        answer.setStep0(info);

        return answer;
    }

    /**
     * Выбираем данные для показа на шаге 1
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step1")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Data step1(Data data) {
        Data answer = new Data();
        Integer creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (creditRequestId < 1) {
            return Data.setErrorRedirect();
        }

        Integer stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);

        if (stepCount < 1) {
            if (stepCount >= 8) {
                Data.setErrorRedirect();
            }
            return Data.setError(); // нельзя ничего показывать
        }

        Step1Data step1 = wizardService.step1(creditRequestId);

        answer.setStatus(true);
        answer.setStep1(step1);
        answer.setRequest_id(StaticFuncs.generateHashId(creditRequestId));
        answer.setSession_id(StaticFuncs.generateSessionId(creditRequestId, stepCount));
        answer.setStepcount(stepCount);
        return answer;
    }

    /**
     * Выбираем данные для показа на шаге 2
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step2")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step2(Data data) {
        StaticFuncs.log("GET STEP2");
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            StaticFuncs.log("Step2 count=" + stepcount);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepcount >= 2) {

                // во всех других случаях - это повторный заход или возврат

                Step2Data step2 = new Step2Data();
                Map<String, Object> map = kassaBean.paramsStep2(reqId);
                StaticFuncs.log(map);

                step2.setRegistrationFias((String) map.get("registrationFias"));

                if (map.get("snils") != null) {
                    String snils = ((String) map.get("snils")).substring(1, 4) + "-" + ((String) map.get("snils")).substring(3, 6) + "-" +
                            ((String) map.get("snils")).substring(6, 9) + "-" + ((String) map.get("snils")).substring(9, 11);
                    map.put("snils", snils);
                }
                step2.setTypework((Integer) map.get("typework"));
                if (step2.getTypework() != null) {
                    step2.setTypeworkTitle(referenceBean.getEmployType(step2.getTypework()).getName());
                }
                step2.setApartment((String) map.get("apartment"));
                step2.setRealtyDate((map.get("RealtyDate") != null ? df.format((Date) map.get("RealtyDate")) : ""));
                step2.setBirthplace((String) map.get("place"));
                step2.setBuilder((String) map.get("builder"));
                step2.setChildren((Integer) map.get("children"));
                step2.setCode_dep((String) map.get("code_dep"));
                if (map.get("gender") != null)
                    step2.setGender(Convertor.toInteger(map.get("gender")).toString());
                step2.setHome((String) map.get("home"));
                step2.setInn((String) map.get("inn"));
                step2.setKorpus((String) map.get("korpus"));
                if (map.get("marriage") != null){
                    step2.setMarriage(Convertor.toInteger(map.get("marriage")));
                }
                if (step2.getMarriage()!=null) {
                	step2.setMarriageTitle(referenceBean.getMarriageType(step2.getMarriage()).getName());
                }
                step2.setNomer((String) map.get("nomer"));
                step2.setSeria((String) map.get("seria"));
                step2.setSnils((String) map.get("snils"));
                step2.setWhenIssued((map.get("whenIssued") != null ? df.format((Date) map.get("whenIssued")) : ""));
                step2.setWhogive((String) map.get("whoIssued"));
                step2.setSurname((String) map.get("surname"));
                step2.setName((String) map.get("name"));
                step2.setMidname((String) map.get("midname"));
                step2.setMobilephone((String) map.get("mobilephone"));
                step2.setBirthdate((map.get("birthdate") != null ? df.format((Date) map.get("birthdate")) : ""));
                step2.setDatabeg((map.get("databeg") != null ? df.format((Date) map.get("databeg")) : ""));

                ans.setStatus(true);
                ans.setStep2(step2);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepcount));
                ans.setStepcount(stepcount);
                return ans;
            } else {
                if (stepcount >= 8) {
                    StaticFuncs.log("Errror step>=7");
                    Data.setErrorRedirect();
                }
                if ((2 - stepcount) > 1) {
                    StaticFuncs.log("Errror 2-step>1");
                    return Data.setErrorRedirect("index" + (stepcount + 1) + ".shtml");
                } else {
                    StaticFuncs.log("пустой ответ");
                    return Data.setError();
                }

            }
        }
        StaticFuncs.log("Errror reqId");
        return Data.setErrorRedirect();
    }


    /**
     * Выбираем данные для показа на шаге 3
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step3")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step3(Data data) {
        StaticFuncs.log("GET STEP3");
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepCount >= 3) {
                // во всех других случаях - это повторный заход или возврат

                Step3Data step3 = new Step3Data();
                Map<String, Object> map = kassaBean.paramsStep3(reqId);
                StaticFuncs.log(map);

                step3.setMatuche((Integer) map.get("matuche"));
                step3.setPhone((String) map.get("phone"));
                step3.setRegphone((String) map.get("regphone"));
                step3.setAvailable((map.get("available") != null && (Boolean) map.get("available")) ? 1 : 0);
                step3.setRegavailable((map.get("regavailable") != null && (Boolean) map.get("regavailable")) ? 1 : 0);
                step3.setRegDate((map.get("RegDate") != null ? df.format((Date) map.get("RegDate")) : ""));

                step3.setLivingFias((String) map.get("livingFias"));

                step3.setApartmentFact((String) map.get("apartmentFact"));
                step3.setBuilderFact((String) map.get("builderFact"));
                step3.setHomeFact((String) map.get("homeFact"));
                step3.setKorpusFact((String) map.get("korpusFact"));
                if (map.get("ground") != null)
                    step3.setGround(Convertor.toInteger(map.get("ground")));
                if (step3.getGround() != null) {
                    step3.setGroundTitle(referenceBean.getRealtyType(step3.getGround()).getName());
                }

                ans.setStatus(true);
                ans.setStep3(step3);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepCount));
                ans.setStepcount(stepCount);
                return ans;
            } else {
                if (stepCount >= 7) {
                    Data.setErrorRedirect();
                }
                if ((3 - stepCount) > 1) {
                    return Data.setErrorRedirect("index" + (stepCount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }


    /**
     * Выбираем данные для показа на шаге 4
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step4")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step4(Data data) {
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepCount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepCount >= 4) {
                // во всех других случаях - это повторный заход или возврат

                Step4Data step4 = new Step4Data();
                Map<String, Object> map = kassaBean.paramsStep4(reqId);
                StaticFuncs.log(map);

                step4.setJobFias((String) map.get("jobFias"));

                step4.setEducation(Convertor.toInteger(map.get("education")));
                if (step4.getEducation() != null) {
                    step4.setEducationTitle(referenceBean.getEducationType(step4.getEducation()).getName());
                }
                step4.setEmployment(Convertor.toInteger(map.get("employment")));
                if (step4.getEmployment() != null) {
                    step4.setEmploymentTitle(referenceBean.getEmployType(step4.getEmployment()).getName());
                }
                step4.setProfession((Integer) map.get("profession"));
                if (step4.getProfession() != null) {
                    step4.setProfessionTitle(referenceBean.getOrganizationType(step4.getProfession()).getName());
                }
                step4.setSalarydate(Convertor.toInteger(map.get("salarydate")));
                if (step4.getSalarydate() != null) {
                    step4.setSalarydateTitle(referenceBean.getIncomeFreqType(step4.getSalarydate()).getName());
                }
                step4.setBuilder((String) map.get("builder"));
                step4.setHome((String) map.get("home"));
                step4.setKorpus((String) map.get("korpus"));

                step4.setExperience((map.get("experience") != null ? df.format((Date) map.get("experience")) : ""));
                step4.setWorkplace((String) map.get("workplace"));
                step4.setOccupation((String) map.get("occupation"));
                step4.setDatestartwork((map.get("datestartwork") != null ? df.format((Date) map.get("datestartwork")) : ""));
                step4.setMonthlyincome(map.get("monthlyincome") != null ? new DecimalFormat("#").format((Double) map.get("monthlyincome")) : null);
                step4.setExtincome(map.get("extincome") != null ? new DecimalFormat("#").format((Double) map.get("extincome")) : null);


                step4.setExtsalarysource(Convertor.toInteger(map.get("extsalarysource")));
                if (step4.getExtsalarysource() != null) {
                    step4.setExtsalarysourceTitle(referenceBean.getExtSalaryType(step4.getExtsalarysource()).getName());
                }

                step4.setWorkphone((String) map.get("phone"));
                step4.setAvailable((map.get("available") != null && (Boolean) map.get("available")) ? 1 : 0);
                step4.setCar((map.get("car") != null && (Boolean) map.get("car")) ? 1 : 0);

                ans.setStatus(true);
                ans.setStep4(step4);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepCount));
                ans.setStepcount(stepCount);
                return ans;
            } else {
                if (stepCount >= 7) {
                    Data.setErrorRedirect();
                }
                if ((4 - stepCount) > 1) {
                    return Data.setErrorRedirect("index" + (stepCount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }

    /**
     * Выбираем данные для показа на шаге 5
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step5")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step5(Data data) {
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepcount >= 5) {
                // во всех других случаях - это повторный заход или возврат

                Step5Data info = new Step5Data();
                Map<String, Object> map = kassaBean.paramsStep5(reqId);
                StaticFuncs.log(map);

                info.setCredittype(Convertor.toInteger(map.get("credittype")));
                if (info.getCredittype() != null) {
                    info.setCredittypeTitle(referenceBean.getCreditType(info.getCredittype()).getName());
                }
                info.setCurrencytype(Convertor.toInteger(map.get("currencytype")));
                if (info.getCurrencytype() != null) {
                    info.setCurrencytypeTitle(referenceBean.getCurrency(info.getCurrencytype()).getName());
                }
                info.setCreditOrganization(Convertor.toInteger(map.get("creditorganization")));
                if (info.getCreditOrganization() != null) {
                    info.setCreditOrganizationTitle(orgService.getCreditOrganizationActive(info.getCreditOrganization()).getName());
                }
                info.setCreditsumprev(map.get("creditsumprev") != null ? new DecimalFormat("#").format((Double) map.get("creditsumprev")) : null);
                info.setCreditcardlimit(map.get("creditcardlimit") != null ? new DecimalFormat("#").format((Double) map.get("creditcardlimit")) : null);
                info.setCreditdate((map.get("creditdate") != null ? df.format((Date) map.get("creditdate")) : ""));
                info.setOverdue((String) map.get("overdue"));
                if (StringUtils.isNotEmpty(info.getOverdue())) {
                    info.setOverdueTitle(referenceBean.getCreditOverdueType(info.getOverdue()).getName());
                }
                info.setCreditisover((map.get("creditisover") != null && (Boolean) map.get("creditisover")) ? 1 : 0);
                info.setPrevcredits(Convertor.toInteger(map.get("prevcredits")));

                ans.setStatus(true);
                ans.setStep5(info);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepcount));
                ans.setStepcount(stepcount);
                return ans;
            } else {
                if (stepcount >= 8) {
                    Data.setErrorRedirect();
                }
                if ((5 - stepcount) > 1) {
                    return Data.setErrorRedirect("index" + (stepcount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }

    /**
     * Выбираем данные для показа на шаге 6
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step6")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step6(Data data, @Context HttpServletRequest request) {
        Data answer = new Data();
        Integer creditRequestId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (creditRequestId > 0) {
            Integer stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), creditRequestId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepcount >= 5) {
                // во всех других случаях - это повторный заход или возврат

                Step6Data step6 = wizardService.step6(creditRequestId);
                step6.setVerifySum("1".equals(request.getParameter("verifysum")));

                answer.setStatus(true);
                answer.setStep6(step6);
                answer.setRequest_id(StaticFuncs.generateHashId(creditRequestId));
                answer.setSession_id(StaticFuncs.generateSessionId(creditRequestId, stepcount));
                answer.setStepcount(stepcount);
                return answer;
            } else {
                if (stepcount >= 8) {
                    Data.setErrorRedirect();
                }
                if ((6 - stepcount) > 1) {
                    return Data.setErrorRedirect("index" + (stepcount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }

    /**
     * Выбираем данные для показа на шаге 7
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step7")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step7(Data data) {
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepcount == 7) {
                // во всех других случаях - это повторный заход или возврат

                Step7Data step7 = new Step7Data();
                Map<String, Object> map = kassaBean.paramsStep7(reqId);
                StaticFuncs.log(map);

                step7.setCreditdays(Convertor.toInteger(map.get("creditdays")));
                step7.setCreditsum(new Integer(map.get("creditsum") != null ? new DecimalFormat("#").format((Double) map.get("creditsum")) : null));
             //   info.setAgreement((String) map.get("agreement"));
                step7.setStake(((Double) map.get("stake") * 100));
                step7.setStakeMin((Double) map.get("stakeMin"));
                step7.setStakeMax((Double) map.get("stakeMax"));
                step7.setCreditDaysMin(Convertor.toInteger(map.get("creditDaysMin")));
                step7.setCreditDaysMax(Convertor.toInteger(map.get("creditDaysMax")));
                step7.setCreditSumMin((Double) map.get("creditSumMin"));
                step7.setCreditSumMax((Double) map.get("creditSumMax"));

                // отправим смс с подтверждением
//                if(data.getIssended()==false) {
//                    String code = StaticFuncs.generateCode(6);
//                    String phone = (String) map.get("cellphone");
//                    StaticFuncs.log("Phone: " + phone + " code=" + code);
//                    mailBean.sendSMS(phone.replaceAll("[^\\d.]", ""), "Код согласия с офертой "+code);
//                    info.setPhonehash(StaticFuncs.md5("saltsupersalt" + code + "megasalt"));
//                }

                ans.setStatus(true);
                ans.setStep7(step7);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepcount));
                ans.setStepcount(stepcount);
                return ans;
            } else {
                if (stepcount >= 8) {
                    Data.setErrorRedirect();
                }
                if ((7 - stepcount) > 1) {
                    return Data.setErrorRedirect("index" + (stepcount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }

    /**
     * Выбираем данные для показа на шаге 8
     *
     * @param data
     * @return
     */
    @POST
    @Path("/step8")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Data step8(Data data) {
        Data ans = new Data();
        Integer reqId = StaticFuncs.decodeIdFromHash(data.getRequest_id());
        if (reqId > 0) {
            Integer stepcount = StaticFuncs.getSessionStepCount(data.getSession_id(), reqId);
            // теперь если в reqId и stepcount 0 - то мы первый раз на этой странице
            if (stepcount == 8) {
                // все верно

                Step8Data info = new Step8Data();
                Map<String, Object> map = kassaBean.paramsStep8(reqId);
                StaticFuncs.log(map);

                info.setName((String) map.get("name"));
                info.setMidname((String) map.get("midname"));
                info.setGender((Integer) map.get("gender"));
                info.setTemplink((String) map.get("templink"));

                ans.setStatus(true);
                ans.setStep8(info);
                ans.setRequest_id(StaticFuncs.generateHashId(reqId));
                ans.setSession_id(StaticFuncs.generateSessionId(reqId, stepcount));
                ans.setStepcount(stepcount);
                return ans;
            } else {
                if ((8 - stepcount) > 1) {
                    return Data.setErrorRedirect("index" + (stepcount + 1) + ".shtml");
                } else {
                    return Data.setError();
                }
            }
        }
        return Data.setErrorRedirect();
    }
}
