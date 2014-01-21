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
package org.ofbiz.webtools;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletOutputStream;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;


import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.webtools.Conversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.ofbiz.webtools.DBFResponseWrapper;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
public class DbfConversionFilter extends ContextFilter {

    public final static String module = DbfConversionFilter.class.getName();
    
    
    protected static String defaultLocaleString = null;
    protected static String redirectUrl = null;
    public static String curDir = System.getProperty("user.dir"); 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Delegator delegator = (Delegator) httpRequest.getSession().getServletContext().getAttribute("delegator");
        
        String tmpUploadRepository = UtilProperties.getPropertyValue(
				"general.properties", "http.upload.tmprepository",
				"runtime/tmp");
    	try {   		
    		
	        DBFResponseWrapper wrappedResponse = new DBFResponseWrapper(httpResponse);
	        chain.doFilter(request, wrappedResponse);
	        
	        String requestUri = httpRequest.getRequestURI();
	        String dbfFileName = requestUri.substring(requestUri.lastIndexOf("/"), requestUri.length());
	        //OutputStream out = response.getOutputStream();
	        byte[] fileByteData =  wrappedResponse.getData();
	        
	        InputStream is = new ByteArrayInputStream(fileByteData);
	        InputStreamReader reader = new InputStreamReader(is);
	        BufferedReader buffReader = new BufferedReader(reader);
	        String fileName = dbfFileName.replaceAll("DBF", "csv");
	        String filePath = tmpUploadRepository+"/"+fileName;
	        /*FileOutputStream fos = new FileOutputStream(filePath);
	        fos.write(fileByteData);
	        fos.close();*/
	        // lets clear 
	        fileByteData = null;
	    	String dbfFilePath = filePath.replace(".csv", ".DBF");
	        try{   	 	
		   		String line;
		   		line = buffReader.readLine();
		   		if (line == null || line.isEmpty()) {
		   			System.err.println("Input file '" + "' is empty");
		   			System.err.flush();
		   			throw new IOException("Input file '" + "' is empty");
		   		}
		   		ArrayList procLine = preProcessEmbedCommas(line);
				line = (String) procLine.get(0);
		   		line = line.replaceAll("\"", "");
		   		if(line.endsWith(",")){			
		   			line= line.substring(0, (line.length()-1));
		   			
		   		}
		   		String[] nameTokens = line.split(",");
		   		Map<String ,Map<String,Object>> fieldNameTypeMap = FastMap.newInstance();
		   		int fieldSize = 0;
		   		for (int i = 0; i < nameTokens.length; i++) {
		   			String entityField = nameTokens[i];
		   			Map fieldTypeMap = FastMap.newInstance();
		   			int pos = entityField.indexOf('_');
		   			if (pos == -1) {
		   				System.err.println("Input file '" +
		   						"': entityField '" + entityField + "' missing _ ");
		   				System.err.flush();
		   				throw new IOException("Input file '" + 
		   						"': entityField '" + entityField + "' missing _ ");
		   			}
	   			String fieldName = entityField.substring(0, pos);
	   			String fieldType = entityField.substring(pos + 1,
	   					entityField.length());
	   			// if field type String like (fieldName_C%L(length)%D(no.of Decimals))
	   			
	   			fieldTypeMap.put("type", fieldType);
	   			
	   			String[] fieldTypeTokens = fieldType.split("%");
	   			if(fieldTypeTokens.length >0 ){
	   				fieldTypeMap.put("type", fieldTypeTokens[0]);
	   				if(fieldTypeTokens.length >1){
	   					if(fieldTypeTokens.length > 1 ){
	   						fieldTypeMap.put("length", fieldTypeTokens[1].replace("L", ""));
	   						if(fieldTypeTokens.length >2){							
	   							fieldTypeMap.put("decimal", fieldTypeTokens[2].replace("D", ""));
	   						}
	   						
	   					}
	   				}				
	   			}
	   			Debug.logInfo("fieldTypeMap================"+fieldTypeMap,module);
	   			fieldNameTypeMap.put(fieldName,fieldTypeMap);
	   			fieldSize++;
	   		}
	   		ArrayList<ArrayList<String>> fieldValuesRows = new ArrayList<ArrayList<String>>();
	   		int rowNum = 1;
	   		
	   		while ((line = buffReader.readLine()) != null) {
	   			rowNum++;
	   			ArrayList arr = preProcessEmbedCommas(line);
				line = (String) arr.get(0);				
	   			line = line.replaceAll("\"", ""); 
	   			if(line.endsWith(",")){
	   				line= line.substring(0, (line.length()-1));
	   			}
	   			String[] valueTokens = line.split(",", -1);
	   			
	   			if (valueTokens.length == 0 || line.isEmpty()) {
	   				// for now we'll log and just skip this row. TODO can we do
	   				// better?
	   				System.out.println("row '" + rowNum + "' {" + line + "}: "
	   						+ "has empty values, skipping this row...");
	   				System.out.flush();
	   				continue;
	   			}
	   			String checkForWhiteSpace = line.trim();
	   			if (checkForWhiteSpace.isEmpty()) {
	   				// for now we'll log and just skip this row. TODO can we do
	   				// better?
	   				System.out.println("row '" + rowNum + "' {" + line + "}: "
	   						+ "has empty values, skipping this row...");
	   				System.out.flush();
	   				continue;
	   			}
	   			ArrayList<String> fieldValues = new ArrayList<String>();
	   			for (int i = 0; i < valueTokens.length; i++) {
	   				String tempField;				
	   				tempField = valueTokens[i];
	   				fieldValues.add(tempField);
	   			}
	   			// sanity check
	   			if (fieldSize != fieldValues.size()) {
	   				System.err.println("row '" + rowNum + "' {" + line + "}: "
	   						+ "fieldSize (" + fieldSize
	   						+ ") and fieldValues (" + fieldValues.size()
	   						+ ") - size mismatch " + line.length());
	   				System.err.flush();
	   				throw new IOException("While processing File  '"
	   						 + "' :: row '" + rowNum + "' {"
	   						+ line + "}: " + "fieldSize (" + fieldSize
	   						+ ") and fieldValues (" + fieldValues.size()
	   						+ ") - size mismatch " + line.length());
	   			}

	   			fieldValuesRows.add(fieldValues);
	   		}
	   		
	   	      	 Debug.logInfo("fieldSize==========="+fieldSize ,module);
	   	    	 DBFField fields[] = new DBFField[fieldSize];
	   	    	 int fieldCount =0;
	   	    	 Map fieldTypeSequenceMap = FastMap.newInstance();
	   	    	 for (Map.Entry<String ,Map<String,Object>> fieldNameType : fieldNameTypeMap.entrySet()) {	             
	   	             String fieldName = fieldNameType.getKey();
	   	             Map fieldTypeMap = (Map)fieldNameType.getValue();
	   	             String fieldType = (String)fieldTypeMap.get("type");
	   	             fields[fieldCount] = new DBFField();
	   			     fields[fieldCount].setName( fieldName);
	   			     if(UtilValidate.isNotEmpty(fieldTypeMap.get("length"))){
	   			    	 fields[fieldCount].setFieldLength(Integer.parseInt(fieldTypeMap.get("length").toString()));
	   			     }
	   			     if(UtilValidate.isNotEmpty(fieldTypeMap.get("decimal"))){
	   			    	 fields[fieldCount].setDecimalCount(Integer.parseInt(fieldTypeMap.get("decimal").toString()));
	   			     }
	   			     fieldTypeSequenceMap.put(fieldCount, fieldType);
	   			     
	   	             if ("C".equals(fieldType )) {
	   	            	 fields[fieldCount].setDataType( DBFField.FIELD_TYPE_C);  
	   	             }else if ("N".equals(fieldType )) {
	   	            	 fields[fieldCount].setDataType( DBFField.FIELD_TYPE_N);	            	 
	   	             }else if ("F".equals(fieldType )) {
	   	            	 fields[fieldCount].setDataType( DBFField.FIELD_TYPE_F);
	   	            	 
	   	             }else if ("L".equals(fieldType )) {
	   	            	 fields[fieldCount].setDataType( DBFField.FIELD_TYPE_L); 
	   		          } else if ("D".equals(fieldType )) {
	   	            	 fields[fieldCount].setDataType( DBFField.FIELD_TYPE_D); 
	   	            }            
	   	            fieldCount++;
	   		    
	   	    	 }	    

	   		     DBFWriter writer = new DBFWriter();
	   		     writer.setFields( fields);
	   		     
	   		     // now populate DBFWriter
	   		     for(int i=0;i<fieldValuesRows.size();i++){   	 
	   		    	 List<String> fieldValueList = fieldValuesRows.get(i);		    	 
	   		    	 Object rowData[] = new Object[fieldSize];
	   		    	 for(int j=0 ;j<fieldValueList.size();j++){		    		 
	   		    		 String fieldType = (String)fieldTypeSequenceMap.get(j);
	   		    		 
	   		    		 if(UtilValidate.isNotEmpty(fieldValueList.get(j))){
	   		    			 if ("C".equals(fieldType )) {
	   			    			 rowData[j] = fieldValueList.get(j);
	   			             }else if ("N".equals(fieldType )) {
	   			            	 rowData[j] = new Double(fieldValueList.get(j));
	   			            	
	   			             }else if ("F".equals(fieldType )) {
	   			            	 rowData[j] = new Double(fieldValueList.get(j));
	   			             }else if ("L".equals(fieldType )) {
	   			            	 rowData[j] = Boolean.TRUE;
	   			            	 if((fieldValueList.get(j)).equalsIgnoreCase("false")){
	   			            		 rowData[j] = Boolean.FALSE;
	   			            	 }			            	 
	   			             }else if ("D".equals(fieldType )) {			            	
	   			            	 Date tempDate = null;
	   			            	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   			            	 try {
	   			            		 tempDate = new java.sql.Date(sdf.parse(fieldValueList.get(j)).getTime());			            				    		    	 
	   			         		} catch (ParseException e) {
	   			         			Debug.logError(e, "Cannot parse date string: " + fieldValueList.get(j), module);		   
	   			         		}
	   			            	 rowData[j] = tempDate; 
	   			            }   
	   		    		 }		    		      
	   		    		 
	   		    	 }			   
	   			     writer.addRecord( rowData);
	   		     }   		    		    
	   		  buffReader.close(); 
	   		  FileOutputStream dbfFos = new FileOutputStream(dbfFilePath);
   		      writer.write(dbfFos);
   		      dbfFos.close();	
	   		  ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
	   	      BufferedOutputStream  midStream = new BufferedOutputStream(outStrm);
	   		  writer.write(midStream);
	   		  byte[] buffer = outStrm.toByteArray();
	   		  // lets clear the dbf writer
	   		 writer = null;
	   		  InputStream in = null;
	   		 OutputStream out =null;
	  		try{
	  			out = response.getOutputStream();
	  			in = new ByteArrayInputStream(buffer);
	  			//in = new FileInputStream(dbfFilePath);
	  			httpResponse.setContentType("application/x-dbase");
	  			httpResponse.setHeader("Content-Disposition","inline; filename="+dbfFileName+";"); 				
	  			String mimeType = "application/x-dbase";
	  			byte[] bytes = new byte[2048];
	  			int bytesRead;

	  			httpResponse.setContentType(mimeType);
	  			while ((bytesRead = in.read(bytes)) != -1) {
	  				out.write(bytes, 0, bytesRead);
	  				String s = new String(bytes); // possibly with a charset	  				
	  			}
	  			// do the following in a finally block:	  			
	  			bytes = null;
	  			buffer = null;
	  		} catch (IOException ioe_ex) {
	  			Debug.logInfo(ioe_ex.getMessage(), module);			
	  			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage()); 			
	  		}
	  		finally{
	  			out.flush();
	  			out.close();	  			
	  			in.close();
	  			outStrm.close();
	  			midStream.close();
	  			is.close();	
	  			reader.close();
	  			//csvFile.delete();
	  			//(new File(dbfFilePath)).delete();
	  		}

	   	 }catch (Exception e) {
   			// TODO: handle exception
   	    	 String errMsg = "There was an error creating Dbf"+e.toString();
   	    	 Debug.logError(errMsg, module);
	   		} 	 

    	}catch (Exception e) {
			// TODO: handle exception
    		//errorMessage = "Attachement not found";
			Debug.logError(e.toString(), module);			
			request.setAttribute("_ERROR_MESSAGE_",e.toString());
		}      
        // we're done checking; continue on
       // chain.doFilter(request, response);
    } 
    
    static ArrayList preProcessEmbedCommas(String line) {
		int charNum = 0;
		Boolean flag =Boolean.FALSE;
		StringBuffer newline = new StringBuffer();
		charNum = line.indexOf('"');
		if (charNum != -1) {
			newline.append(line.substring(0, charNum));
			while (charNum < line.length()) {
				// Get current character from string
				newline.append(line.charAt(charNum));
				int firstIndex = 0;
				if (line.charAt(charNum) == '"') {
					firstIndex = charNum;
					for (int i = charNum + 1; i < line.length(); i++) {
						if (line.charAt(i) == '"') {
							String tempStr = line.substring(firstIndex + 1,
									i + 1);
							tempStr = tempStr.replace(",", "");
							if (!tempStr.equals(line.substring(firstIndex + 1, i + 1))){
								flag = Boolean.TRUE;								
							}
							newline.append(tempStr);
							charNum = i;
							break;
						}
					}
					charNum++;
					continue;
				}

				charNum++;
			}
			line = newline.toString();
		}
		ArrayList result = new ArrayList();
		result.add(line);
		result.add(flag);
		return result;
	}
   
}