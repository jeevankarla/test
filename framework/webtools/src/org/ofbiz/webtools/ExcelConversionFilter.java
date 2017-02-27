/*********************************************************************
*
*      Copyright (C) 2002 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package org.ofbiz.webtools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.hssf.util.HSSFColor;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.webapp.control.ContextFilter;

public class ExcelConversionFilter extends ContextFilter {
	 public final static String module = ExcelConversionFilter.class.getName();
	 protected static String defaultLocaleString = null;
	    protected static String redirectUrl = null;
	    public static String curDir = System.getProperty("user.dir"); 
	    @Override
	    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	        HttpServletRequest httpRequest = (HttpServletRequest) request;
	        HttpServletResponse httpResponse = (HttpServletResponse) response;
	        Delegator delegator = (Delegator) httpRequest.getSession().getServletContext().getAttribute("delegator");
	        
	        DBFResponseWrapper wrappedResponse = new DBFResponseWrapper(httpResponse);
	        chain.doFilter(request, wrappedResponse);
	        String tmpUploadRepository = UtilProperties.getPropertyValue(
					"general.properties", "http.upload.tmprepository",
					"runtime/tmp");
	        
	        String requestUri = httpRequest.getRequestURI();
	        String excelFileName = requestUri.substring(requestUri.lastIndexOf("/"), requestUri.length());
	        //OutputStream out = response.getOutputStream();
	        byte[] fileByteData =  wrappedResponse.getData();
	        InputStream is = new ByteArrayInputStream(fileByteData);
	        InputStreamReader reader = new InputStreamReader(is);
	        BufferedReader buffReader = new BufferedReader(reader);
	        String fileName = excelFileName.replaceAll("xls", "csv");
	        String filePath = tmpUploadRepository+"/"+fileName;
	        String excelFilePath = filePath.replace(".csv", ".xls");
	        /*String tmpUploadRepository = UtilProperties.getPropertyValue(
					"general.properties", "http.upload.tmprepository",
					"runtime/tmp");
	       
	        String fName = tmpUploadRepository+"/test.csv";
	        Debug.log("*****======fName====***"+fName);
	        String currentLine;
	        ArrayList<ArrayList<String>> allRowAndColData = null;
	        ArrayList<String> oneRowData = null;
	        FileInputStream fis = new FileInputStream(fName);
	        DataInputStream myInput = new DataInputStream(fis);
*/	
	        /*byte[] fileByteData =  wrappedResponse.getData();
	        
	        InputStream is = new ByteArrayInputStream(fileByteData);
	        InputStreamReader reader = new InputStreamReader(is);*/
	        int l = 0;
	        String currentLine;
	        ArrayList allRowAndColData = null;
	        ArrayList<String> oneRowData = null;
	        allRowAndColData = new ArrayList<ArrayList<String>>();
	        Map<String, Object> stylesMap = FastMap.newInstance();
	        stylesMap=(Map)request.getAttribute("stylesMap");
	        Boolean enableStyles = (Boolean)request.getAttribute("enableStyles");
	        while ((currentLine = buffReader.readLine()) != null)
	        {
	            oneRowData = new ArrayList<String>();
	            ArrayList procLine = DbfConversionFilter.preProcessEmbedCommas(currentLine);
	            currentLine = (String) procLine.get(0);
	            String oneRowArray[] = currentLine.split(",");
	            for (int j = 0; j < oneRowArray.length; j++)
	            {
	                oneRowData.add(oneRowArray[j]);
	            }
	            if(UtilValidate.isNotEmpty(enableStyles) && enableStyles && l!=0)
	            {
	            	allRowAndColData.add(oneRowData);
	            }else if(UtilValidate.isEmpty(enableStyles))
	            {
	            	allRowAndColData.add(oneRowData);
	            }
	            l++;
	        }
	       try
	       {
    	 	 HSSFWorkbook workBook = new HSSFWorkbook();
    	 	 HSSFSheet sheet = workBook.createSheet("sheet1");
	    	 if(UtilValidate.isNotEmpty(enableStyles) && enableStyles && stylesMap!=null)
	    	 {
	    		 applyStylesToSheet(UtilMisc.toMap("stylesMap",stylesMap,"allRowAndColData",allRowAndColData),workBook,sheet,request);
		     }else{
	        	 for (int i = 0; i < allRowAndColData.size(); i++)
	        	 {
		             ArrayList<?> ardata = (ArrayList<?>) allRowAndColData.get(i);
		             HSSFRow row = sheet.createRow((short) 0 + i);
		             for (int k = 0; k < ardata.size(); k++) {
		                //System.out.print(ardata.get(k));
		                HSSFCell cell = row.createCell((short) k);
		                cell.setCellValue((ardata.get(k).toString()).replaceAll("\"", ""));
		             }
		         }
		     }
             
	         buffReader.close(); 
	         /*FileOutputStream fileOutputStream =  new FileOutputStream(tmpUploadRepository+"/outputFile.xls");
	         workBook.write(fileOutputStream);
	         fileOutputStream.close();*/
	      
	   		 /*FileOutputStream excelFos = new FileOutputStream(excelFilePath);
	   		 workBook.write(excelFos);
		     excelFos.close();	*/
   		     ByteArrayOutputStream outStrm = new ByteArrayOutputStream();
   		     workBook.write(outStrm);
   		     BufferedOutputStream  midStream = new BufferedOutputStream(outStrm);
   		     workBook.write(midStream);
   		     byte[] buffer = outStrm.toByteArray();
   		     // lets clear the xls writer
   		     workBook =null;
   		     InputStream in = null;
   		     OutputStream out =null;
   		     try
   		     {
	  			out = response.getOutputStream();
	  			in = new ByteArrayInputStream(buffer);
	  			//in = new FileInputStream(excelFilePath);
	  			httpResponse.setHeader("Content-Disposition","attachment; filename="+excelFileName+";"); 				
	  			String mimeType = "application/vnd.ms-excel";
	  			byte[] bytes = new byte[4096];
	  			int bytesRead;
	
	  			httpResponse.setContentType(mimeType);
	  			while ((bytesRead = in.read(bytes)) != -1) {
	  				out.write(bytes, 0, bytesRead);
	  				String s = new String(bytes); // possibly with a charset	  				
	  			}
	  			// do the following in a finally block:	  			
	  			bytes = null;
	  			buffer = null;
  		     }catch (IOException ioe_ex){
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
	  			 //(new File(excelFilePath)).delete();
   		     }
	      }catch (Exception e){
	    	  Debug.logInfo(e.getMessage(), module);			
	    	  request.setAttribute("_ERROR_MESSAGE_",e.getMessage()); 
	   
	      }
      } 
	    
      void applyStylesToSheet(Map<String, ? extends Object> context, HSSFWorkbook workBook,HSSFSheet sheet,ServletRequest request)
	  {
	        List headerKeys = new ArrayList();
	        int noOfheads=0;
	        headerKeys=UtilMisc.toList("mainHeader1", "mainHeader2","mainHeader3","mainHeader4","mainHeader5");
	        Map<String, Object> stylesMap = FastMap.newInstance();
	        stylesMap=(Map)context.get("stylesMap");
	        ArrayList  allRowAndColData=(ArrayList)context.get("allRowAndColData");
	        Integer mainHeadingCell = 5;
	        Integer mainHeadercellHeight = null;
	        String mainHeaderFontName = null;
	        Integer mainHeaderFontSize = null;
	        Boolean mainHeaderBold = true;
	        Integer columnHeaderCellHeight = null; 
	        Boolean columnHeaderBold = true;
	        Boolean columnHeaderBgColor = null;
	        String columnHeaderFontName = null;
	        Boolean autoSizeCell = true;
	        Integer columnHeaderFontSize = null; 
        	 mainHeadercellHeight = (Integer)stylesMap.get("mainHeadercellHeight");
	         mainHeaderFontName = (String)stylesMap.get("mainHeaderFontName");
	         mainHeaderFontSize = (Integer)stylesMap.get("mainHeaderFontSize");
	         if(stylesMap.get("mainHeaderBold")!=null)
	         {
	        	 mainHeaderBold = (Boolean)stylesMap.get("mainHeaderBold");
	         }
	         if(stylesMap.get("columnHeaderBold")!=null)
	         {
	        	 columnHeaderBold = (Boolean)stylesMap.get("columnHeaderBold");
	         }
	         if(stylesMap.get("autoSizeCell")!=null)
	         {
	        	 autoSizeCell = (Boolean)stylesMap.get("autoSizeCell");
	         }
	         if(stylesMap.get("mainHeadingCell")!=null)
	         {
	        	 mainHeadingCell = (Integer)stylesMap.get("mainHeadingCell");
	         }
	         columnHeaderCellHeight = (Integer)stylesMap.get("columnHeaderCellHeight"); 
	         columnHeaderBgColor = (Boolean)stylesMap.get("columnHeaderBgColor");
	         columnHeaderFontName = (String)stylesMap.get("columnHeaderFontName");
	         columnHeaderFontSize = (Integer)stylesMap.get("columnHeaderFontSize"); 
        	ArrayList styles=new ArrayList(stylesMap.keySet());
        	for(int i=0; i<styles.size(); i++)
        	{ 
        		ArrayList tempArrayList = new ArrayList<String>();
        		if(headerKeys.contains(styles.get(i)))
        		{
        			tempArrayList.add(stylesMap.get(styles.get(i)));
        			for(int j=0; j<allRowAndColData.size()-1; j++)
        			{
        				tempArrayList.add("");
        			}
        			allRowAndColData.add(i, tempArrayList);
        			++noOfheads;
        		}
        	}
            try{
            	for (int i = 0; i < allRowAndColData.size(); i++)
        		{
		             HSSFCellStyle style = workBook.createCellStyle();
		        	 HSSFFont font = workBook.createFont(); 
		             ArrayList<?> ardata = (ArrayList<?>) allRowAndColData.get(i);
		             HSSFRow row = sheet.createRow((short) 0 + i);
		             for (int k = 0; k < ardata.size(); k++)
		             {
		                HSSFCell cell = row.createCell((short) k);
		                if(k==mainHeadingCell && i<=noOfheads)
		                {
		                	if(UtilValidate.isNotEmpty(mainHeadercellHeight))
		                	{
		                		row.setHeight((short) mainHeadercellHeight.shortValue());
		                	}else
		                	{
		                		row.setHeight((short) 400);
		                	}
		                	if(UtilValidate.isNotEmpty(mainHeaderFontName))
		                	{
		                		 font.setFontName(mainHeaderFontName);
		                	}
		                	if(UtilValidate.isNotEmpty(mainHeaderBold) && mainHeaderBold)
		                	{
		                		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		                	}
		                	if(UtilValidate.isNotEmpty(mainHeaderFontSize)){
		                		font.setFontHeightInPoints((short) mainHeaderFontSize.shortValue());
		                	}else
		                	{
		                		font.setFontHeightInPoints((short) 12);  //default value 
		                	}
		                	style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		                	style.setFont(font);
		                	cell.setCellValue((ardata.get(0).toString()).replaceAll("\"", ""));
			            	cell.setCellStyle(style);
		                }else if(i==noOfheads+1)
		                {
		                	if(UtilValidate.isNotEmpty(columnHeaderCellHeight))
		                	{
		                		row.setHeight((short) columnHeaderCellHeight.shortValue());
		                	}else
		                	{
		                		row.setHeight((short) 300);
		                	}
		                	if(UtilValidate.isNotEmpty(columnHeaderBold) &&  columnHeaderBold)
		                	{
		                		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		                	}
		                	if(UtilValidate.isNotEmpty(columnHeaderFontSize))
		                	{
		                		font.setFontHeightInPoints((short) columnHeaderFontSize.shortValue());
		                	}else
		                	{
		                		font.setFontHeightInPoints((short) 9);
		                	}
		                	if(UtilValidate.isNotEmpty(columnHeaderFontName))
		                	{ 
		                		font.setFontName(columnHeaderFontName);
		                	}
		                	if(UtilValidate.isNotEmpty(columnHeaderBgColor) &&  columnHeaderBgColor){
		                		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		                	}
		                	style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		                	style.setFont(font);
		                	cell.setCellValue((ardata.get(k).toString()).replaceAll("\"", ""));
		                	cell.setCellStyle(style);
		                }else if(i>noOfheads)
		                {
		                	cell.setCellValue((ardata.get(k).toString()).replaceAll("\"", ""));
		                }
		                if(UtilValidate.isNotEmpty(autoSizeCell) && autoSizeCell)
		                {
		                	sheet.autoSizeColumn(k);
		                }
		             }
		         }
            }catch (Exception e){
            	Debug.logInfo(e.getMessage(), module);			
        		request.setAttribute("_ERROR_MESSAGE_",e.getMessage()); 
            	
            }
	   }
}











