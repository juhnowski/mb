package ru.simplgroupp.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import ru.simplgroupp.lib.StaticFuncs;
import ru.simplgroupp.rest.AppInitializer;
/*
 * Класс для получения источника перехода
 */
public class GetReferrerFilter implements Filter {
	
	private final String domen = "https://rastorop.ru";
	private final String domenEx = "https://rastorop.ru/main/";
	
	@EJB
	AppInitializer appInit;

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		try{
			
		
	        HttpServletRequest request = (HttpServletRequest) req;
	        HttpServletResponse response = (HttpServletResponse)res;
	        String cookie = request.getHeader("cookie");
	        String referer = request.getHeader("referer");
	        	        
	        response.setHeader("cookie", "");
	        
	        Cookie[] cookies = request.getCookies();
	        
	        String ga = null;
	        String utrs = null;
	        
	        if(cookies != null)
	        {
	        	for(Cookie c : cookies)
	        	{
	        		if(StringUtils.equals(c.getName(), "__utrs"))
	        			utrs = c.getValue();
	        	
	        		if(StringUtils.equals(c.getName(), "_ga"))
	        			ga = parseCookieGa(c);
	        	}
	        	
	        	
	        	
	        	if(ga != null)
        		{
		        	if(utrs == null)
		        	{
			        	String ref = getReferer(referer);
			        		
			        	if(ref != null)
			        	{
			        		String newc = createNewUTRSCookie(ga, "1", getReferer(referer));
			        		//String newh = this.addUTRSCookieHeader(cookie, newc);
			        		if(newc != null)
			        		{
			        			//response.setHeader("cookie", newh);
			        			response.addCookie(new Cookie("__utrs", newc));
			        			//StaticFuncs.log("++++++++++++++++++++++++++++add cookie=" + newc);
			        		}
			        	}
			        	else
			        	{
			        		String newc = createNewUTRSCookie(ga, "1", "direct");
			        		if(newc != null)
			        		{
			        			response.addCookie(new Cookie("__utrs", newc));
			        			//StaticFuncs.log("++++++++++++++++++++++++++++add cookie=" + newc);
			        		}
			        		
			        	}
		        	}
		        	/*else
		        	{
		        		if(StringUtils.contains(utrs, ga))
		        		{
		        			String newh = this.editCookieHeader(cookie, changeCookieUtrs(utrs, referer));
			        		if(newh != null)
			        		{
			        			response.setHeader("cookie", newh);
			        		}
			        		StaticFuncs.log("+++++++++++++++++++++++++++edit header=" + newh);
		        		}
		        		else
		        		{
			        		String newh = this.editCookieHeader(cookie, createNewUTRSCookie(ga, "1", getReferer(referer)));
			        		if(newh != null)
			        		{
			        			response.setHeader("cookie", newh);
			        		}
				        	StaticFuncs.log("+++++++++++++++++++++++++++new header=" + newh);
		        		}
		        	}*/
        		}
	        }
        
		}
		catch(Throwable ex)
		{
			StaticFuncs.log("Filter exception"+ ex.getMessage());
		}
        
        chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	/*
	 * Создает новую куку для отслеживания поведения пользователя
	 */
	private String createNewUTRSCookie(String GA, String hits, String ref)
	{
		String utrs = null;
		if(StringUtils.isNotEmpty(GA) &&
				StringUtils.isNotEmpty(hits) && StringUtils.isNotEmpty(ref))
			utrs = GA + "."
					+ hits + ".1.utmcsr=(" + ref + ")";
		
		return utrs;
	}
	
	private String editCookieHeader(String cookie, String utrs)
	{
		String[] strarr = StringUtils.split(cookie, ";");
		if(strarr != null)
		{
			String newcookie = "";
			for(String s : strarr)
			{
				if(StringUtils.startsWith(StringUtils.trim(s), "__utrs"))
					newcookie += " __utrs=" + utrs + ";";
				else
					newcookie += s + ";";
			}
			
			return StringUtils.substring(newcookie, 0, newcookie.length()-1);
		}
		
		return cookie;
	}
	
	private String addUTRSCookieHeader(String cookie, String utrs)
	{
		
		if(StringUtils.isNotEmpty(utrs))
			return cookie+ ";" + " __utrs=" + utrs;
		else
			return cookie;
	}
	
	private String parseCookieGa(Cookie _ga)
	{
		String result = null;
		if(_ga!=null)
		{
			String ga = _ga.getValue();
			if(StringUtils.isNotEmpty(ga))
				if(StringUtils.startsWith(ga, "GA"))
				{
					String[] arrstr = StringUtils.split(ga, ".");
					if(arrstr != null)
						if(arrstr.length == 4)
							result = arrstr[2] + "." + arrstr[3];
				}
			
		}

		
		return result;
	}
	
	private String changeCookieUtrs(String _utrs, String refer)
	{
		String result = null;

		if(StringUtils.isNotEmpty(_utrs))
		{
			String[] arrstr = StringUtils.split(_utrs, ".");
			if(arrstr != null)
				if(arrstr.length == 5)
				{
					String hitsStr=arrstr[2];
					try
					{
						Integer hits = Integer.parseInt(hitsStr);
						hits++;
						hitsStr = hits.toString();
					}
					catch(NumberFormatException ex)
					{
						return result;
					}
					String utmcsr = arrstr[4];

					String ref = getReferer(refer);
					StaticFuncs.log("+++++++++++++++++++++++++++ref=" + ref);
					if(ref != null)
						utmcsr="utmcsr=(" + ref + ")";
					
					result = arrstr[0] + "." + arrstr[1] + "." + hitsStr
							+ ".1." + utmcsr;
				}
					
		}
			
		

		
		return result;
	}
	
	private String getReferer(String refer)
	{
		if(StringUtils.isNotEmpty(refer))
		{
			if(StringUtils.equals(refer, domen))
				return "direct";
			if((!StringUtils.startsWith(refer, domen)) && 
					(!StringUtils.startsWith(refer, domenEx)))
			{
				return getHostName(refer);
			}
		}
		return null;
	}
	
	private String getHostName(String url) {
		try
		{
		    URI uri = new URI(url);
		    String hostname = uri.getHost();
		    return hostname;
		}
		catch(URISyntaxException ex)
		{
		    return null;
		}
	}

}
