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
public class Probe extends HttpServlet {
	
	static final MainLogger _logger = new MainLogger(Probe.class.getName());	
	
	private static boolean checkBackEnd() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		BroadCaster bc = (BroadCaster) Class.forName(Settings.getBroadCasterClassName()).newInstance();
		return bc.isOkay();
	}
	
	private String checkAllServices() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		boolean backendStatus = checkBackEnd();
		boolean pingerStatus = false;
		
		if (backendStatus) {
			pingerStatus = true;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("backend", backendStatus);
		result.put("status", pingerStatus);

		return JSONObject.toJSONString(result);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        try {
			response.getWriter().println(checkAllServices());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}
