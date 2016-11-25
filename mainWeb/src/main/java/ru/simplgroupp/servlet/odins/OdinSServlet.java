package ru.simplgroupp.servlet.odins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.interfaces.service.OdinSServiceCentrofinance;
import ru.simplgroupp.interfaces.service.OdinSServiceSberfond;
import ru.simplgroupp.odins.model.rastorop.OnlineCreditExchange;
import ru.simplgroupp.interfaces.service.OdinSServiceRastorop;
import ru.simplgroupp.odins.model.sberfond.SputnikExchange;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Базовый, общий класс для 1С сервлетов
 */

public abstract class OdinSServlet extends HttpServlet {
	private static final long serialVersionUID = -86465333718162730L;
    private static final Logger logger = LoggerFactory.getLogger(OdinSServlet.class);

    @EJB(beanName = "OdinSServiceSberfondImpl")
    protected OdinSServiceSberfond odinSServiceSberfond;

    protected static final String TERRIBLE_PARAMETER = "kjhr9784bedjkf98734hdkjhuiwefl084634hd987767253hdjiuGYSTYS764jhsdKXBVDY7333FDk734";

    @Override
    public void init(ServletConfig config) throws ServletException {
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String someterriblesecpar = request.getParameter("someterriblesecpar");
        if(someterriblesecpar == null || !TERRIBLE_PARAMETER.equals(someterriblesecpar)){
            return;
        }
        processQuery(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String someterriblesecpar = request.getParameter("someterriblesecpar");
        if(someterriblesecpar == null || !TERRIBLE_PARAMETER.equals(someterriblesecpar)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        processQuery(request,response);
    }


    protected abstract void processQuery(HttpServletRequest request, HttpServletResponse response);

    protected void packAndReturnResponse(SputnikExchange res, HttpServletRequest request, HttpServletResponse response){
        if(res != null){
            //Генерирую ответ и кладу его в response
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(SputnikExchange.class);
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

                response.setContentType("text/xml;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();

                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(res, stringWriter);


                String xmlRespString = stringWriter.toString();
                //logger.error("******DEBUG ONLY******* OUR xmlRespString = " + xmlRespString);
                writer.print(xmlRespString);
            } catch (JAXBException e) {
                e.printStackTrace();
                logger.error("Наш ответ не был послан 1C", e.getCause());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
