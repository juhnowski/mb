package ru.simplgroupp.servlet;

import org.apache.commons.lang.StringUtils;
import ru.simplgroupp.toolkit.common.Convertor;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 *
 */
public abstract class ServletRequestUtils {

    public static Integer getIntParameter(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        if (parameter == null || parameter.isEmpty()) {
            return null;
        }

        try {
            return Convertor.toInteger(parameter);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long getLongParameter(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        if (parameter == null || parameter.isEmpty()) {
            return null;
        }

        try {
            return Convertor.toLong(parameter);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDoubleParameter(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        if (parameter == null || parameter.isEmpty()) {
            return null;
        }

        try {
            return Convertor.toDouble(parameter);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Date getDateParameter(HttpServletRequest request, String name, String format) {
        String parameter = request.getParameter(name);
        if (parameter == null || parameter.isEmpty()) {
            return null;
        }

        try {
            return Convertor.toDate(parameter, format);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
