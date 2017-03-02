package in.vasista.vbiz.rest.filter;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import org.ofbiz.base.util.Debug;

import in.vasista.vbiz.rest.util.JwtUtil;

public class TokenAuthentication implements Filter {
    public static final String module = TokenAuthentication.class.getName();

	public void init(FilterConfig config) throws ServletException {

	}

	public void  doFilter(ServletRequest request, 
			ServletResponse response,
			FilterChain chain) 
					throws java.io.IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
   	
		String token = httpRequest.getHeader("token");
        Debug.log("token: " + token, module);
        if (token != null) {
        	String username = JwtUtil.parseToken(token);
            Debug.log("username: " + username, module);   
    		request.setAttribute("usernameFromToken", username);            
        }
        
		// Pass request back down the filter chain
		chain.doFilter(request,response);


	}

	public void destroy() {
		// This is optional step but if you like you
		// can write hitCount value in your database.
	}
}
