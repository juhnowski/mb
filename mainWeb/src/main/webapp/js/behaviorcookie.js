function parse_ga_cookie(cookie)
		{
			var					obj 		= new Object();
			var					arr 		= null;
			var					str 		= null;
			var					org			= null;
			var					utmzReq		= new RegExp("^[ ]*__utrs[ ]*=[ ]*", "i");
			var					utmcsrReq	= new RegExp("\.utmcsr[ ]*=[ ]*");
			var					j			= 0;
	
			obj.utma = null;
			obj.utmz = null;

			if ((cookie != null) &&
				((arr = cookie.split(";")) != null))
			{
				for (var i = 0; i < arr.length; i++)
				{
					if ((org = str = arr[i]) != null)
					{
						if (utmzReq.test(str) &&
							utmcsrReq.test(str))
						{
					
							if (((j = str.indexOf("=")) != -1) &&
								(++j < str.length) &&
								((str = str.substring(j)) != null) &&
								((str = str.trim()) != null) &&
								((j = str.search("\.utmcsr[ ]*=[ ]*")) != -1) &&
								((j = str.indexOf("=", j)) != -1) &&
								(++j < str.length) &&
								((str = str.substring(j)) != null) &&
								((str = str.trim()) != null))
							{
								obj.utmz = str.substr(1, str.length - 2);
							}
							
							if (((str = org) != null) &&
								((j = str.indexOf("=")) != -1) &&
								(++j < str.length) &&
								((str = str.substring(j)) != null) &&
								((str = str.trim()) != null) &&
								((j = str.indexOf(".utmcsr")) != -1))
							{
								obj.utma = str.substr(0, j).split(".");
							}
						}
					}
				}
			}
	
		return obj;
	}

function parse_ga_cookie_ex(cookie)
{
	var	obj = new Object();
	var tmp = null;
	
	obj.unique_visitor_id = "";
	obj.timestamp_of_first_visit = "";
	obj.visit_count = "";
	obj.from = "";
	
	if ((tmp = parse_ga_cookie(cookie)) != null)
	{
		if ((tmp.utma !== undefined) &&
			(tmp.utma != null))
		{
			if (tmp.utma.length > 0)
				obj.unique_visitor_id = tmp.utma[0];
				
			if (tmp.utma.length > 1)
				obj.timestamp_of_first_visit = tmp.utma[1];

			if (tmp.utma.length > 2)
				obj.visit_count = tmp.utma[2];
		}

		if ((tmp.utmz !== undefined) &&
			(tmp.utmz != null))
		{
			if((k = tmp.utmz.indexOf(")")) != -1)
			{
				obj.from = tmp.utmz.substr(0, k); 
			}
			else
				obj.from = tmp.utmz;
		}
	}
	
	return obj;	
}

function createUTRSCookie(clientid)
{ 
	var	cookie = document.cookie;
	var	refer = document.referrer;
	var	domain 	= document.domain;
	
	if((cookie!= null) && (clientid !== undefined))
	{
		if ((arr = cookie.split(";")) != null)
		{
			result = "";
						
			for (i = 0; i < arr.length; i++)
			{	
				if ((arr[i] != null) &&
					(arr[i].indexOf("__utrs=") != -1))
					
				{
					var cookieobj = parse_ga_cookie_ex(cookie);
					
					if((cookieobj.unique_visitor_id != null) && (cookieobj.unique_visitor_id != "") &&
					(cookieobj.timestamp_of_first_visit != null) && (cookieobj.timestamp_of_first_visit != "") && (cookieobj.visit_count != null) && (cookieobj.visit_count != ""))
					{
						if(cookieobj.from == "")
						{
							if((refer != null)&&(refer != ""))
							{
								if(refer.indexOf(domain) == -1)
									cookieobj.from = refer;
							}
							else 
								cookieobj.from = "direct";
						}
						else if( cookieobj.from.indexOf(domain) == -1 ) 
						{
							if((refer != null)&&(refer != ""))
							{
								if(refer.indexOf(domain) == -1)
									if(refer.indexOf(cookieobj.from) == -1)
										cookieobj.from = refer;
							}
						}
						
						
						cookieobj.visit_count = new Number(cookieobj.visit_count) + 1;
						result = "__utrs=" + clientid + "." + cookieobj.visit_count + ".1.utmcsr=(" + cookieobj.from + ")";
						document.cookie = result;
						return;
					}
				}
			}
		}
	}
}