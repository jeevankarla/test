/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.widget.screen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.view.AbstractViewHandler;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.MacroFormRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;

/**
 * Uses XSL-FO formatted templates to generate PDF, PCL, POSTSCRIPT etc.  views
 * This handler will use JPublish to generate the XSL-FO
 */
public class ScreenFopViewHandler extends AbstractViewHandler {
    public static final String module = ScreenFopViewHandler.class.getName();
    protected static final String DEFAULT_ERROR_TEMPLATE = "component://common/widget/CommonScreens.xml#FoError";

    protected ServletContext servletContext = null;

    /**
     * @see org.ofbiz.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) throws ViewHandlerException {
        this.servletContext = context;
    }

    /**
     * @see org.ofbiz.webapp.view.ViewHandler#render(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenStringRenderer screenStringRenderer = new MacroScreenRenderer(UtilProperties.getPropertyValue("widget", getName() + ".name"), UtilProperties.getPropertyValue("widget", getName() + ".screenrenderer"));
            FormStringRenderer formStringRenderer = new MacroFormRenderer(UtilProperties.getPropertyValue("widget", getName() + ".formrenderer"), request, response);
            // TODO: uncomment these lines when the renderers are implemented
            //TreeStringRenderer treeStringRenderer = new MacroTreeRenderer(UtilProperties.getPropertyValue("widget", getName() + ".treerenderer"), writer);
            //MenuStringRenderer menuStringRenderer = new MacroMenuRenderer(UtilProperties.getPropertyValue("widget", getName() + ".menurenderer"), writer);
            ScreenRenderer screens = new ScreenRenderer(writer, null, screenStringRenderer);
            screens.populateContextForRequest(request, response, servletContext);

            // this is the object used to render forms from their definitions
            screens.getContext().put("formStringRenderer", formStringRenderer);
            screens.getContext().put("simpleEncoder", StringUtil.getEncoder(UtilProperties.getPropertyValue("widget", getName() + ".encoder")));
            screens.render(page);
        } catch (Exception e) {
            renderError("Problems with the response writer/output stream", e, "[Not Yet Rendered]", request, response);
            return;
        }

        // set the input source (XSL-FO) and generate the output stream of contentType
        String screenOutString = writer.toString();
        if (!screenOutString.startsWith("<?xml")) {
            
            /*if (encoding.isEmpty()) {
        		encoding = "encoding=\"UTF-8\"";
        	}
        	else {
        		encoding = "encoding=\"" + encoding + "\"";
        	}
            screenOutString = "<?xml version=\"1.0\" " + encoding + "?>\n" + screenOutString;*/
        	screenOutString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + screenOutString;
        }
        
        if (Debug.verboseOn()) Debug.logVerbose("XSL:FO Screen Output: " + screenOutString, module);

        if (UtilValidate.isEmpty(contentType)) {
            contentType = UtilProperties.getPropertyValue("widget", getName() + ".default.contenttype");
        }
        String fopContentType = contentType;
        if (contentType.equals("text/plain")) {
        	// don't use the default text renderer in fop, instead use pdf rendere and convert pdf to text
        	fopContentType = "application/pdf";
        }        
        Reader reader = new StringReader(screenOutString);
        StreamSource src = new StreamSource(reader);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Fop fop = ApacheFopWorker.createFopInstance(out, fopContentType);
            ApacheFopWorker.transform(src, null, fop);
        } catch (Exception e) {
            renderError("Unable to transform FO file", e, screenOutString, request, response);
            return;
        }

		response.setContentType(contentType);        	
    	if (contentType.equals("text/plain")) {
            try {        		
                // set output file name
                String outputFileName = (String) request.getAttribute("OUTPUT_FILENAME");
                if (UtilValidate.isEmpty(outputFileName)) {
                	outputFileName = "report.txt";
                }
                response.setHeader("Content-Disposition", "attachment; filename=" + outputFileName);
                           	
                File tempPDFFile = File.createTempFile("TruckSheet", ".pdf");
                tempPDFFile.deleteOnExit();            	
            	FileOutputStream pdfFile = new FileOutputStream(tempPDFFile);
            	pdfFile.write(out.toByteArray());
            	pdfFile.close(); 
                String ofbizHome = System.getProperty("ofbiz.home");
                String relPath = UtilProperties.getPropertyValue("general.properties", "fop.pdftotext", "bin/pdftotext");
                
                String pdfToTextPath = ofbizHome + "/" + relPath;
                ProcessBuilder builder = new ProcessBuilder(pdfToTextPath, "-layout", 
                		tempPDFFile.getAbsolutePath(), "-");
                builder.redirectErrorStream(true);                
                Process process = builder.start();               
                InputStream stdout = process.getInputStream();
                
                byte[] search = "VST_ASCII-".getBytes("UTF-8");
                //char c = 15;
                //byte[] replacement = {(byte)c};
                byte[] replacement = " ".getBytes("UTF-8");
                InputStream ris = new ReplacingInputStream(stdout, search, replacement);
                
                /*BufferedReader bufReader = new BufferedReader(new InputStreamReader(stdout)); */               
               	ServletOutputStream servletOut = response.getOutputStream();        	
            	int readBytes;
            	while ((readBytes = ris.read()) != -1) {
            		servletOut.write(readBytes);
    	    	}
        		servletOut.flush();    
        		ris.close();
    		}
    		catch (IOException e) {
                renderError("pdftotext failed: ", e, screenOutString, request, response);        			
    		}	
    	}
    	else {
    		try {
    			response.setContentLength(out.size());
    			// write to the browser
    			out.writeTo(response.getOutputStream());
        		response.getOutputStream().flush();            	        	
    		}
    		catch (IOException e) {
    			renderError("Unable to write to OutputStream", e, screenOutString, request, response);
    	    }        			
    	}      
    }

    protected void renderError(String msg, Exception e, String screenOutString, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Debug.logError(msg + ": " + e + "; Screen XSL:FO text was:\n" + screenOutString, module);
        try {
            Writer writer = new StringWriter();
            ScreenRenderer screens = new ScreenRenderer(writer, null, new HtmlScreenRenderer());
            screens.populateContextForRequest(request, response, servletContext);
            screens.getContext().put("errorMessage", msg + ": " + e);
            screens.render(DEFAULT_ERROR_TEMPLATE);
            response.setContentType("text/html");
            response.getWriter().write(writer.toString());
            writer.close();
        } catch (Exception x) {
            Debug.logError("Multiple errors rendering FOP", module);
            throw new ViewHandlerException("Multiple errors rendering FOP", x);
        }
    }
}
