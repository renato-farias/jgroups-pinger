package org.jgroups.tools.pinger.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jgroups.tools.pinger.BroadCaster;
import org.jgroups.tools.pinger.MainLogger;
import org.jgroups.tools.pinger.Settings;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class Api extends HttpServlet {
	
	static final MainLogger _logger = new MainLogger(Api.class.getName());
	
    
    private static HashMap<String, Object> getMembers() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    	BroadCaster bc = (BroadCaster) Class.forName(Settings.getBroadCasterClassName()).newInstance();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("nodes", bc.getMembers());
		result.put("total", bc.getMembers().size());

		return (HashMap<String, Object>) result;
    }
    
    private static String notFound() {
    	return "Not Found =[";
    }
    
    private String detectMethod(String url) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    	if ( url.matches("\\/api\\/members") ) {
    		_logger.debug("Getting the last registry of: " + url);
    		return JSONObject.toJSONString(getMembers());
    	}
		return notFound();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        _logger.debug("Handling with URL requested: " + request.getRequestURI());
        try {
        	response.getWriter().println(detectMethod(request.getRequestURI()));
        } catch (Exception e) {
        	response.getWriter().println(e.getMessage());
        	_logger.error("error writing response", e);
        }
    }
    
    
}