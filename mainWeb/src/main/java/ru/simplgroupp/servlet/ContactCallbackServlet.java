package ru.simplgroupp.servlet;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.dao.interfaces.CreditDAO;
import ru.simplgroupp.dao.interfaces.PaymentDAO;
import ru.simplgroupp.ejb.ActionContext;
import ru.simplgroupp.ejb.plugins.payment.ContactAcquiringPluginConfig;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.interfaces.*;
import ru.simplgroupp.interfaces.plugins.payment.ContactAcquiringBeanLocal;
import ru.simplgroupp.services.PaymentService;
import ru.simplgroupp.persistence.CreditEntity;
import ru.simplgroupp.persistence.DocumentEntity;
import ru.simplgroupp.servlet.contact.*;
import ru.simplgroupp.transfer.Credit;
import ru.simplgroupp.transfer.Documents;
import ru.simplgroupp.workflow.PluginExecutionContext;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contact callback
 * Вызывается из Контакта, когда клиент приходит в пункт приема Контакта, чтобы перевести деньги к нам
 */

//Для получения перевода клиент должен назвать свои Ф??О, номер перевода и его сумму.
//При выплате перевода потребуется паспорт - т.к. выплата перевода является банковской операцией.
@WebServlet("/contact/call")
public class ContactCallbackServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8646529365718162730L;
	
	private static final Logger logger = LoggerFactory.getLogger(ContactCallbackServlet.class);
    public static final DateFormat CONTACT_STAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final DateFormat CONTACT_STAMP_FORMATZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @EJB
    private PaymentService paymentService;

    @EJB
    private transient ActionProcessorBeanLocal actionProcessor;

    @EJB
    private WorkflowBeanLocal workflowBean;

    @EJB
    private CreditBeanLocal creditBean;
    
    @EJB
    private CreditDAO creditDAO;
    
    @EJB
    private PeopleBeanLocal peopleBean;
    @EJB
    protected CreditCalculatorBeanLocal creditCalc;

    @EJB
    PaymentDAO payDAO;

    private String contactPassword;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ActionContext actionContext = actionProcessor.createActionContext(null, true);
        PluginExecutionContext context = PluginExecutionContext.createExecutionContext(actionContext, ContactAcquiringBeanLocal.SYSTEM_NAME);
        ContactAcquiringPluginConfig pluginConfig = (ContactAcquiringPluginConfig) context.getPluginConfig();
        contactPassword = pluginConfig.getPassword();
    }

    private void processQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContactCallbackResponse contactResponse = new ContactCallbackResponse();
        contactResponse.setStamp(CONTACT_STAMP_FORMATZ.format(new Date()));

        logger.error("******DEBUG ONLY******* started version x.1.0");

        logger.info("Parameters: " + request.getParameterMap());
        String queryText = null;

        String theString = null;
        if(request.getInputStream()!=null){
            StringWriter writer = new StringWriter();
            IOUtils.copy(request.getInputStream(), writer, "windows-1251");
            theString = writer.toString();
            logger.error("******DEBUG ONLY******* InputStream value(IOUtils.copy)" + theString);
        }

        if(request.getParameterMap()!=null){
            logger.error("******DEBUG ONLY******* parameters from parameterMap ");
            for(String key: request.getParameterMap().keySet()){
                if(key.contains("<REQUEST")){
                    //queryText = new String(key.getBytes("windows-1251"),"Windows-1251");
                    //queryText = new String(key.getBytes("windows-1251"),"utf-8");
                    queryText=key;

                }
                logger.error("******DEBUG ONLY******* windows-1251 key=" + new String(key.getBytes(),"windows-1251") +"; value="+request.getParameterMap().get(key));
                logger.error("******DEBUG ONLY******* UTF8 key=" + new String(key.getBytes(),"UTF-8") +"; value="+request.getParameterMap().get(key));
            }
        }
        if(queryText==null){
            queryText = theString;
        }

        /*
        BufferedReader br =  request.getReader();
        String hash = br.readLine(); //первая строка - хэш
        logger.error("******DEBUG ONLY******* hash = " + hash);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        while ((line = br.readLine()) != null) {
            if(!first){
                stringBuilder.append(EOL);
            }else{
                first = false;
            }
            stringBuilder.append(line);
        }
        xmlString = stringBuilder.toString();
        */

        logger.error("******DEBUG ONLY******* queryText(kakoy est)" + queryText);

        if(queryText == null){
            contactResponse.setRe(CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
            //contactResponse.setErrText("Пустой запрос");
            contactResponse.setErrText("Empty query");
        }else{
            String[] xmlLines = queryText.split("\n");
            String hash = null;
            String xmlString = null;

            for(String s: xmlLines){
                if(s.length()>15){
                    if(hash == null){
                        hash = s.trim();
                    }else{
                        if(xmlString == null){
                            xmlString = s.trim();
                        }
                    }
                }
            }


            String calculatedMd5 = genHash(xmlString);
            logger.error("******DEBUG ONLY******* calculatedMd5 = " + calculatedMd5);

            if(!calculatedMd5.equalsIgnoreCase(hash)){
                logger.error("Hash is not right!");
                contactResponse.setRe(CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
                contactResponse.setErrText("Wrong hash.");//contactResponse.setErrText("Неправильный хэш");
            }else{
                ContactCallbackRequest callbackRequest = null;
                if(StringUtils.isNotEmpty(xmlString)){
                    try {
                        JAXBContext jaxbContext = JAXBContext.newInstance(ContactCallbackRequest.class);
                        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                        callbackRequest = (ContactCallbackRequest) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
                    } catch (JAXBException e) {
                        e.printStackTrace();
                        logger.error("Не парсится запрос от Контакта", e);
                    }
                    logger.info(callbackRequest.toString());
                }else{
                    logger.error("Пустой запрос от Контакта!");
                }
                if(callbackRequest!=null){
                    logger.error("******DEBUG ONLY******* callbackRequest = " + callbackRequest.toString());
                }

                ContactRequestProcessor processor = findProcessor(callbackRequest.getAction());
                if(processor==null){
                    logger.error("******DEBUG ONLY******* processor IS NULL. WHY? action=" + callbackRequest.getAction());
                }else{
                    logger.error("******DEBUG ONLY******* processor = " + processor.getClass().getName());
                }

                if(processor == null){
                    contactResponse.setRe(CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
                    //contactResponse.setErrText("Пустое или неверное название метода.Аттрибут action");
                    contactResponse.setErrText("Action attr is wrong");
                }else{
                    try {
                        contactResponse = processor.process(callbackRequest,this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        contactResponse.setRe(CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR);
                        //contactResponse.setErrText("Ошибка обработки запроса!"+e.getCause());
                        contactResponse.setErrText("Error");
                        logger.error("******DEBUG ONLY******* ERROR IN contactResponse = processor.process(callbackRequest,this);");
                    }
                }
                if(contactResponse!=null){
                    logger.error("******DEBUG ONLY******* contactResponse = " + contactResponse.toString());
                }

            }
        }



        //Генерирую ответ и кладу его в response
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ContactCallbackResponse.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "WINDOWS-1251");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            //marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            //marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml version=\"1.0\" encoding=\"WINDOWS-1251\"?>");

            PrintWriter writer = response.getWriter();

            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(contactResponse, stringWriter);

            String xmlRespString = stringWriter.toString();
            //xmlRespString = xmlRespString.replaceAll("[\r\n]+$", ""); //remove \n from end
            String md5Hash = genHash(xmlRespString);
            logger.error("******DEBUG ONLY******* OUR md5Hash = " + md5Hash);
            response.setContentType("text/xml;charset=WINDOWS-1251");
            String responseString = md5Hash + "\n" + xmlRespString;
            logger.error("******DEBUG ONLY******* OUR responseString = " + responseString);
            writer.write(responseString);
        } catch (JAXBException e) {
            e.printStackTrace();
            logger.error("Наш ответ не был послан Контакту", e.getCause());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        processQuery(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        processQuery(request,response);
    }


    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setWorkflowBean(WorkflowBeanLocal workflowBean) {
        this.workflowBean = workflowBean;
    }

    public void setActionProcessor(ActionProcessorBeanLocal actionProcessor) {
        this.actionProcessor = actionProcessor;
    }

    public void setCreditBean(CreditBeanLocal creditBean) {
        this.creditBean = creditBean;
    }

    public void setPeopleBean(PeopleBeanLocal peopleBean) {
        this.peopleBean = peopleBean;
    }

    public PaymentDAO getPaymentDAO() {
    	return payDAO;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ActionProcessorBeanLocal getActionProcessor() {
        return actionProcessor;
    }

    public WorkflowBeanLocal getWorkflowBean() {
        return workflowBean;
    }

    public CreditBeanLocal getCreditBean() {
        return creditBean;
    }

    public PeopleBeanLocal getPeopleBean() {
        return peopleBean;
    }

    public CreditCalculatorBeanLocal getCreditCalc() {
        return creditCalc;
    }

    public void setCreditCalc(CreditCalculatorBeanLocal creditCalc) {
        this.creditCalc = creditCalc;
    }

    public static final int CONTACT_CALLBACK_RETURN_CODE_SUCCESS = 0;
    public static final int CONTACT_CALLBACK_RETURN_CODE_PAYMENT_NOT_FOUND = -7001;
    public static final int CONTACT_CALLBACK_RETURN_CODE_PAYMENT_ZEROED = -7002;
    public static final int CONTACT_CALLBACK_RETURN_CODE_PAYMENT_ALREADY_DONE = -7003;
    public static final int CONTACT_CALLBACK_RETURN_CODE_SERVICE_UNAVAILABLE = -7004;
    public static final int CONTACT_CALLBACK_RETURN_CODE_ANY_ERROR = -7099;

    public static final int CONTACT_CALLBACK_STATE_PAYED = 0;
    public static final int CONTACT_CALLBACK_STATE_NOT_PAYED = 100;
    public static final int CONTACT_CALLBACK_STATE_CANCELLED = 8;


    public boolean isPassport(String docType){
        if(docType == null){
            return true;
        }
        String low = docType.toLowerCase();
        return low.contains("passport") || low.contains("паспорт");
    }

    private ContactRequestProcessor findProcessor(String action){
        if(action == null){
            return null;
        }
        if(action.equalsIgnoreCase("check")){
            return new CheckContactRequestProcessor();
        }
        if(action.equalsIgnoreCase("pay")){
            return new PayContactRequestProcessor();
        }
        if(action.equalsIgnoreCase("get")){
            return new GetContactRequestProcessor();
        }
        if(action.equalsIgnoreCase("CANCELPAY")){
            return new CancelPayContactRequestProcessor();
        }
        return null;
    }

    public String genHash(String xml){
        return getMD5Hash3(xml+contactPassword);
        //return DigestUtils.md5Hex(xml + EOL + contactPassword);
    }

    public String getContactPassword() {
        return contactPassword;
    }

    public void setContactPassword(String contactPassword) {
        this.contactPassword = contactPassword;
    }

    public ContactCallbackRequest extractRequestData(String xmlString){
        ContactCallbackRequest callbackRequest = null;
        if(xmlString != null && xmlString.length()>0){
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ContactCallbackRequest.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                callbackRequest = (ContactCallbackRequest) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            } catch (JAXBException e) {
                e.printStackTrace();
                logger.error("Не парсится запрос от Контакта", e);
            }
            logger.info(callbackRequest.toString());
        }else{
            logger.error("Пустой запрос от Контакта!");
        }
        return callbackRequest;
    }
    public ContactCallbackResponse extractResponseData(String xmlString){
        ContactCallbackResponse res = null;
        if(xmlString != null && xmlString.length()>0){
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(ContactCallbackResponse.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                res = (ContactCallbackResponse) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            } catch (JAXBException e) {
                e.printStackTrace();
                logger.error("Не парсится мой собственный ответ контакту.Не должно сему быть.", e);
            }
            logger.info(res.toString());
        }else{
            logger.error("Пустой запрос от Контакта!");
        }
        return res;
    }

    public CreditEntity findCredit(ContactCallbackRequest callbackRequest){
        String accountNumber = callbackRequest.gettAttr1();
        //Задача - найти CreditEntity
        CreditEntity creditEntity = null;
        if(accountNumber!=null && StringUtils.isNotEmpty(accountNumber.trim())){
            creditEntity = creditDAO.findCreditByAccountNumber(accountNumber);
        }
        if(creditEntity == null){
            //придется искать по паспорту
            String idnumber = callbackRequest.getsIDnumber();
            logger.error("________________idnumber_____________ = " +idnumber);
            if(idnumber!=null){
                idnumber = idnumber.trim();
            }
            String docType = callbackRequest.getsIDtype();
            if(isPassport(docType)){
                String ser = idnumber.substring(0,4);
                String num = idnumber.substring(4);
                logger.error("________________________ser = " +ser +";   num=" + num);

                DocumentEntity document = peopleBean.findDocument(Documents.PASSPORT_RF, ser, num);
                if(document != null){
                    logger.error("******DEBUG ONLY******* document is not null!!!! id" + document.getId() +"; peoplemainid="+document.getPeopleMainId().getId());
                    //PeopleMainEntity peopleMainEntity = peopleBean.getPeopleMain(document.getPeopleMainId().getId());
                    try {
                        Credit credit = creditBean.findCreditActive(document.getPeopleMainId().getId(),null);
                        if(credit != null){
                            logger.error("******DEBUG ONLY******* Credit is not null!!!! id" + credit.getId());
                            creditEntity = credit.getEntity();
                        }
                    } catch (KassaException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return creditEntity;

    }

    private String getMD5Hash3(String str) {
        final MessageDigest messageDigest;
        String result = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes(Charset.forName("WINDOWS-1251")));
            final byte[] resultByte = messageDigest.digest();
            result = new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
