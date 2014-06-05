/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package org.ofbiz.webapp.event;

import static org.ofbiz.base.util.UtilGenerics.checkMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.net.InetAddress;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;
import java.util.Enumeration;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.ServerStreamConnection;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfig;
import org.apache.xmlrpc.common.XmlRpcHttpRequestConfigImpl;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHttpServer;
import org.apache.xmlrpc.server.XmlRpcHttpServerConfig;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.util.HttpUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.ConfigXMLReader;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;
import org.ofbiz.webapp.control.ConfigXMLReader.RequestMap;

import java.sql.Timestamp;
import javax.servlet.http.HttpSession;

/**
 * XmlRpcEventHandler
 */
public class XmlRpcEventHandler extends XmlRpcHttpServer implements EventHandler {

    public static final String module = XmlRpcEventHandler.class.getName();
    protected Delegator delegator;
    protected LocalDispatcher dispatcher;

    private Boolean enabledForExtensions = null;
    private Boolean enabledForExceptions = null;

    public void init(ServletContext context) throws EventHandlerException {
        String delegatorName = context.getInitParameter("entityDelegatorName");
        this.delegator = DelegatorFactory.getDelegator(delegatorName);
        this.dispatcher = GenericDispatcher.getLocalDispatcher(delegator.getDelegatorName(), delegator);
        this.setHandlerMapping(new ServiceRpcHandler());

        String extensionsEnabledString = context.getInitParameter("xmlrpc.enabledForExtensions");
        if (UtilValidate.isNotEmpty(extensionsEnabledString)) {
            enabledForExtensions = Boolean.valueOf(extensionsEnabledString);
        }
        String exceptionsEnabledString = context.getInitParameter("xmlrpc.enabledForExceptions");
        if (UtilValidate.isNotEmpty(exceptionsEnabledString)) {
            enabledForExceptions = Boolean.valueOf(exceptionsEnabledString);
        }
    }

    void saveHit(Map<String, Object> context) {
        String apiHitId = delegator.getNextSeqId("ApiHit");   	
        GenericValue apiHit = delegator.makeValue("ApiHit");
        apiHit.set("apiHitId", apiHitId);
        apiHit.set("contentId", context.get("webappName")+ "." + context.get("serviceName"));
        apiHit.set("hitTypeId", context.get("XMLRPC"));
        apiHit.set("userLoginId", context.get("userLoginId"));
        try {
            InetAddress address = InetAddress.getLocalHost();

            if (address != null) {
            	apiHit.set("serverIpAddress", address.getHostAddress());
            	apiHit.set("serverHostName", address.getHostName());
            } else {
                Debug.logError("Unable to get localhost internet address, was null", module);
            }
        } catch (java.net.UnknownHostException e) {
            Debug.logError("Unable to get localhost internet address: " + e.toString(), module);
        }        
        apiHit.set("webappName", context.get("webappName"));
        apiHit.set("initialLocale", context.get("initialLocale"));
        apiHit.set("initialRequest", context.get("initialRequest"));
        apiHit.set("initialReferrer", context.get("initialReferrer"));
        apiHit.set("initialUserAgent", context.get("initialUserAgent"));
        apiHit.set("clientIpAddress", context.get("clientIpAddress"));
        apiHit.set("clientHostName", context.get("clientHostName"));
        apiHit.set("clientUser", context.get("clientUser"));
        apiHit.set("clientIpIspName", context.get("clientIpIspName"));
        apiHit.set("clientIpPostalCode", context.get("clientIpPostalCode"));
        apiHit.set("clientIpStateProvGeoId", context.get("clientIpStateProvGeoId"));
        apiHit.set("clientIpCountryGeoId", context.get("clientIpCountryGeoId"));
        Timestamp startDateTime = (Timestamp)context.get("startDateTime");
        Timestamp endDateTime = (Timestamp)context.get("endDateTime");        
        apiHit.set("startDateTime", startDateTime);
        apiHit.set("endDateTime", endDateTime);
        apiHit.set("totalTimeMillis", Long.valueOf(endDateTime.getTime() - startDateTime.getTime()));
        // ::TODO:: verify transaction/multi-threading safety      
        try {
            apiHit.create();
        } catch (GenericEntityException e) {
            Debug.logWarning("Error saving ApiHit: " + e.toString(), module);
        }
    }
    
    /**
     * @see org.ofbiz.webapp.event.EventHandler#invoke(ConfigXMLReader.Event, ConfigXMLReader.RequestMap, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(Event event, RequestMap requestMap, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {

    	HttpSession session = request.getSession();
        Map<String, Object> apiHitMap = FastMap.newInstance();
        String webappName = (String) session.getAttribute("_WEBAPP_NAME_");
        apiHitMap.put("webappName", webappName);        
        String initialRequest = (String) session.getAttribute("_CLIENT_REQUEST_");
        apiHitMap.put("initialRequest", initialRequest.length() > 250 ? initialRequest.substring(0, 250) : initialRequest);        
        String initialReferrer = (String) session.getAttribute("_CLIENT_REFERER_");
        apiHitMap.put("initialReferrer", initialReferrer.length() > 250 ? initialReferrer.substring(0, 250) : initialReferrer);        
        String initialUserAgent = (String) session.getAttribute("_CLIENT_USER_AGENT_");
        apiHitMap.put("initialUserAgent", initialUserAgent.length() > 250 ? initialUserAgent.substring(0, 250) : initialUserAgent);        
        Locale initialLocaleObj = (Locale) session.getAttribute("_CLIENT_LOCALE_");
        String initialLocale = initialLocaleObj != null ? initialLocaleObj.toString() : "";
        apiHitMap.put("initialLocale", initialLocale);        
        String clientIpAddress = (String)session.getAttribute("_CLIENT_REMOTE_ADDR_");
        apiHitMap.put("clientIpAddress", clientIpAddress);        
        String clientHostName = (String)session.getAttribute("_CLIENT_REMOTE_HOST_");
        apiHitMap.put("clientHostName", clientHostName);        
        String clientUser = (String)session.getAttribute("_CLIENT_REMOTE_USER_");   
        apiHitMap.put("clientUser", clientUser);        
        if (UtilValidate.isEmpty(webappName)) {
            Debug.logInfo(new Exception(), "The webappName was empty, somehow the initial request settings were missing.", module);
        }
        ServiceRpcHandler serviceHandler = (ServiceRpcHandler)this.getHandlerMapping();
        serviceHandler.setApiHitMap(apiHitMap);
    	String report = request.getParameter("echo");
        if (report != null) {
            StringBuilder buf = new StringBuilder();
            try {
                // read the inputstream buffer
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    buf.append(line).append("\n");
                }
            } catch (Exception e) {
                throw new EventHandlerException(e.getMessage(), e);
            }
            Debug.logInfo("Echo: " + buf.toString(), module);

            // echo back the request
            try {
                response.setContentType("text/xml");
                Writer out = response.getWriter();
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                out.write("<methodResponse>");
                out.write("<params><param>");
                out.write("<value><string><![CDATA[");
                out.write(buf.toString());
                out.write("]]></string></value>");
                out.write("</param></params>");
                out.write("</methodResponse>");
                out.flush();
            } catch (Exception e) {
                throw new EventHandlerException(e.getMessage(), e);
            }
        } else {
            try {
            	
                this.execute(this.getXmlRpcConfig(request), new HttpStreamConnection(request, response));
            } catch (XmlRpcException e) {
                Debug.logError(e, module);
                throw new EventHandlerException(e.getMessage(), e);
            }
        }
//Debug.logInfo("apiHitMap:" + apiHitMap, module);
        saveHit(apiHitMap);
        return null;
    }

    @Override
    protected void setResponseHeader(ServerStreamConnection con, String header, String value) {
        ((HttpStreamConnection) con).getResponse().setHeader(header, value);
    }

    protected XmlRpcHttpRequestConfig getXmlRpcConfig(HttpServletRequest req) {
        XmlRpcHttpRequestConfigImpl result = new XmlRpcHttpRequestConfigImpl();
        XmlRpcHttpServerConfig serverConfig = (XmlRpcHttpServerConfig) getConfig();

        result.setBasicEncoding(serverConfig.getBasicEncoding());
        result.setContentLengthOptional(serverConfig.isContentLengthOptional());
        result.setEnabledForExtensions(serverConfig.isEnabledForExtensions());
        result.setGzipCompressing(HttpUtil.isUsingGzipEncoding(req.getHeader("Content-Encoding")));
        result.setGzipRequesting(HttpUtil.isUsingGzipEncoding(req.getHeaders("Accept-Encoding")));
        result.setEncoding(req.getCharacterEncoding());
        //result.setEnabledForExceptions(serverConfig.isEnabledForExceptions());
        HttpUtil.parseAuthorization(result, req.getHeader("Authorization"));

        // context overrides
        if (enabledForExtensions != null) {
            result.setEnabledForExtensions(enabledForExtensions);
        }
        if (enabledForExceptions != null) {
            result.setEnabledForExtensions(enabledForExceptions);
        }
        return result;
    }

    class OfbizRpcAuthHandler implements AbstractReflectiveHandlerMapping.AuthenticationHandler {

        protected Map<String, Object> getContext(XmlRpcRequest xmlRpcReq, String serviceName) throws XmlRpcException {
            ModelService model;
            try {
                model = dispatcher.getDispatchContext().getModelService(serviceName);
            } catch (GenericServiceException e) {
                throw new XmlRpcException(e.getMessage(), e);
            }

            // context placeholder
            Map<String, Object> context = FastMap.newInstance();

            if (model != null) {
                int parameterCount = xmlRpcReq.getParameterCount();

                // more than one parameter; use list notation based on service def order
                if (parameterCount > 1) {
                    int x = 0;
                    for (String name: model.getParameterNames("IN", true, true)) {
                        context.put(name, xmlRpcReq.getParameter(x));
                        x++;

                        if (x == parameterCount) {
                            break;
                        }
                    }

                // only one parameter; if its a map use it as the context; otherwise make sure the service takes one param
                } else if (parameterCount == 1) {
                    Object param = xmlRpcReq.getParameter(0);
                    if (param instanceof Map<?, ?>) {
                        context = checkMap(param, String.class, Object.class);
                    } else {
                        if (model.getDefinedInCount() == 1) {
                            String paramName = model.getInParamNames().iterator().next();
                            context.put(paramName, xmlRpcReq.getParameter(0));
                        } else {
                            throw new XmlRpcException("More than one parameter defined on service; cannot call via RPC with parameter list");
                        }
                    }
                }

                // do map value conversions
                context = model.makeValid(context, ModelService.IN_PARAM);
            }

            return context;
        }
        
        public boolean isAuthorized(XmlRpcRequest xmlRpcReq) throws XmlRpcException {
            XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) xmlRpcReq.getConfig();
			
            ModelService model;
            try {
                model = dispatcher.getDispatchContext().getModelService(xmlRpcReq.getMethodName());
            } catch (GenericServiceException e) {
                throw new XmlRpcException(e.getMessage(), e);
            }

            if (model != null && model.auth) {
                String username = config.getBasicUserName();
                String password = config.getBasicPassword();

                // check the account
                Map<String, Object> context = FastMap.newInstance();
                context.put("login.username", username);
                context.put("login.password", password);

                Map<String, Object> resp;
                try {
                    resp = dispatcher.runSync("userLogin", context);
                } catch (GenericServiceException e) {
                    throw new XmlRpcException(e.getMessage(), e);
                }

                if (ServiceUtil.isError(resp)) {
                    return false;
                }
            }

            return true;
        }
    }

    class ServiceRpcHandler extends AbstractReflectiveHandlerMapping implements XmlRpcHandler {

        Map<String, Object> apiHitMap = FastMap.newInstance();
   	
        public ServiceRpcHandler() {
            this.setAuthenticationHandler(new OfbizRpcAuthHandler());
        }

        public void setApiHitMap(Map<String, Object> hitMap) {
        	apiHitMap = hitMap;
        }
        
        public Map<String, Object> getApiHitMap() {
        	return apiHitMap;
        }
        
        @Override
        public XmlRpcHandler getHandler(String method) throws XmlRpcNoSuchHandlerException, XmlRpcException {
            ModelService model = null;
            try {
                model = dispatcher.getDispatchContext().getModelService(method);
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            if (model == null) {
                throw new XmlRpcNoSuchHandlerException("No such service [" + method + "]");
            }
            return this;
        }

        public Object execute(XmlRpcRequest xmlRpcReq) throws XmlRpcException {
            DispatchContext dctx = dispatcher.getDispatchContext();
            String serviceName = xmlRpcReq.getMethodName();
            ModelService model = null;
            try {
                model = dctx.getModelService(serviceName);
            } catch (GenericServiceException e) {
                throw new XmlRpcException(e.getMessage(), e);
            }

            // check remote invocation security
            if (model == null || !model.export) {
                throw new XmlRpcException("Unknown method");
            }

            // prepare the context -- single parameter type struct (map)
            Map<String, Object> context = this.getContext(xmlRpcReq, serviceName);

            // add in auth parameters
            XmlRpcHttpRequestConfig config = (XmlRpcHttpRequestConfig) xmlRpcReq.getConfig();
            String username = config.getBasicUserName();
            String password = config.getBasicPassword();
            if (UtilValidate.isNotEmpty(username)) {
                context.put("login.username", username);
                context.put("login.password", password);
            }

            // capture hit details
            apiHitMap.put("userLoginId", context.get("login.username"));
            apiHitMap.put("serviceName", serviceName);
            apiHitMap.put("startDateTime", UtilDateTime.nowTimestamp());
            
            // add the locale to the context
            context.put("locale", Locale.getDefault());
            
            String tenantId = (String)context.get("tenantId");
            if (UtilValidate.isNotEmpty(tenantId)) {
            	String tenantDelegatorName = delegator.getDelegatorBaseName() + "#" + tenantId;
            	// after this line the delegator is replaced with the new per-tenant delegator
            	delegator = DelegatorFactory.getDelegator(tenantDelegatorName);
            	dispatcher = GenericDispatcher.getLocalDispatcher(delegator.getDelegatorName(), delegator);
            }            		
            // invoke the service
            Map<String, Object> resp;
            try {
                resp = dispatcher.runSync(serviceName, context);
            } catch (GenericServiceException e) {
                throw new XmlRpcException(e.getMessage(), e);
            }
            if (ServiceUtil.isError(resp)) {
                Debug.logError(ServiceUtil.getErrorMessage(resp), module);
                throw new XmlRpcException(ServiceUtil.getErrorMessage(resp));
            }
            apiHitMap.put("endDateTime", UtilDateTime.nowTimestamp());
            // return only definied parameters
            return model.makeValid(resp, ModelService.OUT_PARAM, false, null);
        }

        protected Map<String, Object> getContext(XmlRpcRequest xmlRpcReq, String serviceName) throws XmlRpcException {
            ModelService model;
            try {
                model = dispatcher.getDispatchContext().getModelService(serviceName);
            } catch (GenericServiceException e) {
                throw new XmlRpcException(e.getMessage(), e);
            }

            // context placeholder
            Map<String, Object> context = FastMap.newInstance();

            if (model != null) {
                int parameterCount = xmlRpcReq.getParameterCount();

                // more than one parameter; use list notation based on service def order
                if (parameterCount > 1) {
                    int x = 0;
                    for (String name: model.getParameterNames("IN", true, true)) {
                        context.put(name, xmlRpcReq.getParameter(x));
                        x++;

                        if (x == parameterCount) {
                            break;
                        }
                    }

                // only one parameter; if its a map use it as the context; otherwise make sure the service takes one param
                } else if (parameterCount == 1) {
                    Object param = xmlRpcReq.getParameter(0);
                    if (param instanceof Map<?, ?>) {
                        context = checkMap(param, String.class, Object.class);
                    } else {
                        if (model.getDefinedInCount() == 1) {
                            String paramName = model.getInParamNames().iterator().next();
                            context.put(paramName, xmlRpcReq.getParameter(0));
                        } else {
                            throw new XmlRpcException("More than one parameter defined on service; cannot call via RPC with parameter list");
                        }
                    }
                }

                // do map value conversions
                context = model.makeValid(context, ModelService.IN_PARAM);
            }

            return context;
        }
    }

    class HttpStreamConnection implements ServerStreamConnection {

        protected HttpServletRequest request;
        protected HttpServletResponse response;

        protected HttpStreamConnection(HttpServletRequest req, HttpServletResponse res) {
            this.request = req;
            this.response = res;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public InputStream newInputStream() throws IOException {
            return request.getInputStream();
        }

        public OutputStream newOutputStream() throws IOException {
            response.setContentType("text/xml");
            return response.getOutputStream();
        }

        public void close() throws IOException {
            response.getOutputStream().close();
        }
    }
}
