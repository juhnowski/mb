package ru.simplgroupp.servlet.odins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplgroupp.odins.model.rastorop.OnlineCreditExchange;
import ru.simplgroupp.odins.model.sberfond.SputnikExchange;
import ru.simplgroupp.util.DatesUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Date;

/**
 * Вызывается из сервера 1С, чтобы получить данные о начисленных процентах в XML формате
 *
 */

@WebServlet("/odins/accruedinterest")
public class AccruedInterestOdinSServlet extends OdinSServlet {
	private static final long serialVersionUID = -8646532327658162730L;
	private static final Logger logger = LoggerFactory.getLogger(AccruedInterestOdinSServlet.class);

    protected void processQuery(HttpServletRequest request, HttpServletResponse response){

        String dateString = request.getParameter("repdate");
        Date date = null;

        logger.error("******AccruedInterestOdinSServlet STARTED!!!!****************" + dateString);

        if(dateString != null){
            try {
                date = DatesUtils.DATE_FORMAT_YYYY_MM_dd.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                logger.error("******DEBUG ONLY******* processQuery bad repdate" + dateString);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                packAndReturnResponse(null,request,response);
                return;

            }
        }

        SputnikExchange res = odinSServiceSberfond.generateXMLAccruedInterest(date);
        packAndReturnResponse(res,request,response);
    }



}
