//UPLOADING A CONTENT TO THE SERVER

package org.ofbiz.webtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import javax.servlet.ServletOutputStream;
import java.util.Map;


import java.util.List;
import com.linuxense.javadbf.*;

import javolution.util.FastMap;
import in.vasista.vbiz.datapop.SlurpCSV;
import jxl.*; 
import jxl.demo.CSV;
import java.util.Iterator;


public class Conversion {
	final static private String IN_DATA_DIR = "vasistautils/datapop/data/csv";
	final static private String OUT_DATA_DIR = "/vasistautils/datapop/data/xml";
	final static private String OUT_DATA_DIR_UPLOAD = "vasistautils/datapop/data/xml";
	final static private String TEMP_FOLDER = "/runtime/tmp";
	final static private String TEMP_FOLDER_CSV = "/runtime/tmp/csv";
	final static private String TEMP_FOLDER_XML = "/runtime/tmp/xml";
	
	public static final String module = Conversion.class.getName();
	public static String curDir = System.getProperty("user.dir"); 
	public static String uploadFile(HttpServletRequest request,
			HttpServletResponse response)  throws Exception{
		// ServletFileUpload fu = new ServletFileUpload(new
		// DiskFileItemFactory(10240, new File(new File("runtime"), "tmp")));
		// //Creation of servletfileupload		 
		String errorMessage ="";  
		String tmpUploadRepository = UtilProperties.getPropertyValue(
				"general.properties", "http.upload.tmprepository",
				"runtime/tmp");
		ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory(
				10240, new File(tmpUploadRepository))); // Creation of
														// servletfileupload

		java.util.List lst = null;
		
		String file_name = "";
		try {
			lst = UtilGenerics.checkList(fu.parseRequest(request));
		} catch (FileUploadException fup_ex) {
			fup_ex.printStackTrace();
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);			
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		if (lst.size() == 0) // There is no item in lst
		{
			errorMessage = "There is no item in zip file";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		FileItem file_item = null;
		FileItem selected_file_item = null;

		// Checking for form fields - Start
		for (int i = 0; i < lst.size(); i++) {
			file_item = (FileItem) lst.get(i);
			String fieldName = file_item.getFieldName();
			// Check for the attributes for user selected file - Start
			if (fieldName.equals("uploadedFile")) {
				selected_file_item = file_item;
				file_name = tmpUploadRepository + "/" + file_item.getName(); // Getting
																				// the
																				// file
																				// name
				break;
			}
			// Check for the attributes for user selected file - End
		}
		// Checking for form fields - End

		// Uploading the file content - Start
		if (selected_file_item == null) // If selected file item is null
		{
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		byte[] file_bytes = selected_file_item.get();
		byte[] extract_bytes = new byte[file_bytes.length];

		for (int l = 0; l < file_bytes.length; l++) {
			extract_bytes[l] = file_bytes[l];
		}

		if (extract_bytes == null) {
			errorMessage = "AttachementException";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
            	
		}

		// Creation & writing to the file in server - Start
		FileOutputStream fout=null;
		File dir=null;
		try {
			fout = new FileOutputStream(file_name);
			fout.flush();
			fout.write(extract_bytes);
			String resultpreUnZip = SlurpCSV.unzip(file_name);
			dir=new File(curDir + "/"
					+ tmpUploadRepository);
			if(resultpreUnZip != "success"){			
				emptyTmpDir(dir);
				errorMessage = resultpreUnZip;
				request.setAttribute("_ERROR_MESSAGE_",errorMessage);	
				return "error";
				
			}
			
		} catch (IOException ioe_ex) {
			emptyTmpDir(dir);
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
			          	
		} 
		finally{
			fout.flush();
			fout.close();			
		}
		ServletOutputStream out = null;			
		InputStream in = null;
		try{
			out = response.getOutputStream();
			in = new FileInputStream(curDir + "/"
					+ tmpUploadRepository + "/" + "out.zip");
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition","inline; filename=xml_"+file_item.getName()+";"); 				
			String mimeType = "application/zip";
			byte[] bytes = new byte[1024];
			int bytesRead;

			response.setContentType(mimeType);

			while ((bytesRead = in.read(bytes)) != -1) {
				out.write(bytes, 0, bytesRead);
			}

			// do the following in a finally block:
			
			
		} catch (IOException ioe_ex) {
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
			          	
		}
		finally{
			out.flush();
			out.close();
			in.close();	
			emptyTmpDir(dir);
		}
		
		// Creation & writing to the file in server - End
		return "success";
		// Uploading the file content - End
	}
	public static void emptyTmpDir(File dir){		
		if (dir.isDirectory()) {
	          File[] list = dir.listFiles();
	          if (list != null) {
	              for (int i = 0; i < list.length; i++) {
	                  File tmpF = list[i];
	                  if (tmpF.isDirectory()) {
	                	  emptyTmpDir(tmpF);
	                  }	                 
	                  tmpF.delete();	                  
	              }
	          }
		}else{
			dir.delete();
		}
	}	
	public static String uploadDbfFile(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String errorMessage ="";  
		Map paramMap = UtilHttp.getParameterMap(request);
		String shedId ="" ;
		if(UtilValidate.isNotEmpty(paramMap)){
			shedId = (String)paramMap.get("shedCode");
		}
		String tmpUploadRepository = UtilProperties.getPropertyValue(
				"general.properties", "http.upload.tmprepository",
				"runtime/tmp");
		ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory(
				10240, new File(tmpUploadRepository))); // Creation of
														// servletfileupload

		java.util.List lst = null;
		File dir=new File(curDir + "/"+ tmpUploadRepository);
		
		String file_name = "";
		try {
			lst = UtilGenerics.checkList(fu.parseRequest(request));
		} catch (FileUploadException fup_ex) {
			fup_ex.printStackTrace();
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage+fup_ex, module);			
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}
		String fileName ="";
		FileItem file_item = null;
		FileItem selected_file_item = null;

		// Checking for form fields - Start
		for (int i = 0; i < lst.size(); i++) {
			file_item = (FileItem) lst.get(i);
			String fieldName = file_item.getFieldName();
			// Check for the attributes for user selected file - Start
			if (fieldName.equals("uploadedFile")) {
				selected_file_item = file_item;
				file_name = tmpUploadRepository + "/" + file_item.getName(); // Getting
																				// the
																				// file
																				// name
				break;
			}
			// Check for the attributes for user selected file - End
		}
		// Checking for form fields - End

		// Uploading the file content - Start
		if (selected_file_item == null) // If selected file item is null
		{
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		byte[] file_bytes = selected_file_item.get();
		byte[] extract_bytes = new byte[file_bytes.length];

		for (int l = 0; l < file_bytes.length; l++) {
			extract_bytes[l] = file_bytes[l];
		}

		if (extract_bytes == null) {
			errorMessage = "AttachementException";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
            	
		}
		// Creation & writing to the file in server - Start
		FileOutputStream fout=null;
		
		try {
			fout = new FileOutputStream(file_name);
			fout.flush();
			fout.write(extract_bytes);
			
		} catch (IOException ioe_ex) {
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
			          	
		} 
		finally{
			fout.flush();
			fout.close();			
		}
		fileName = file_name.replace(tmpUploadRepository+"/" ,"");
		Map dbfToVbizInMap = FastMap.newInstance();
		Map dbfToVbizResultMap = FastMap.newInstance();
		dbfToVbizInMap.put("userLogin", userLogin);
		dbfToVbizInMap.put("file_name", file_name);
		dbfToVbizInMap.put("shedId",shedId);
		if(fileName.equalsIgnoreCase("MPMAS.DBF")){
			try{
				dbfToVbizResultMap = dispatcher.runSync("mpMasToVbiz",dbfToVbizInMap);
				if(ServiceUtil.isError(dbfToVbizResultMap)){
					String resultMsg = (String)dbfToVbizResultMap.get("errorMessage");
					request.setAttribute("_ERROR_MESSAGE_", resultMsg);
					return "error";
				}
			}catch (GenericServiceException e) {
				Debug.logError("Error while coverting mpMas To sql "+e, module);
				request.setAttribute("_ERROR_MESSAGE_","Error while coverting mpMas To sql :"+e.getMessage());
				return "error";
			}
		}else if(fileName.equalsIgnoreCase("RTMAS.DBF")){
			try{
				dbfToVbizResultMap = dispatcher.runSync("rtMasToVbiz",dbfToVbizInMap);
				if(ServiceUtil.isError(dbfToVbizResultMap)){
					String resultMsg = (String)dbfToVbizResultMap.get("errorMessage");
					request.setAttribute("_ERROR_MESSAGE_", resultMsg);
					return "error";
				}
			}catch (GenericServiceException e) {
				Debug.logError("Error while coverting mpMas To sql "+e, module);
				request.setAttribute("_ERROR_MESSAGE_","Error while coverting mpMas To sql :"+e.getMessage());
				return "error";
			}
		}else if(fileName.equalsIgnoreCase("UNTMAS.DBF")){
			try{
				dbfToVbizResultMap = dispatcher.runSync("unitMasToVbiz",dbfToVbizInMap);
				if(ServiceUtil.isError(dbfToVbizResultMap)){
					String resultMsg = (String)dbfToVbizResultMap.get("errorMessage");
					request.setAttribute("_ERROR_MESSAGE_", resultMsg);
					return "error";
				}
			}catch (GenericServiceException e) {
				Debug.logError("Error while coverting Dbf To sql "+e, module);
				request.setAttribute("_ERROR_MESSAGE_","Error while coverting mpMas To sql :"+e.getMessage());
				return "error";
			}
		}else if(fileName.equalsIgnoreCase("BANKMAS.DBF")){
			try{
				dbfToVbizResultMap = dispatcher.runSync("bankMasToVbiz",dbfToVbizInMap);
				if(ServiceUtil.isError(dbfToVbizResultMap)){
					String resultMsg = (String)dbfToVbizResultMap.get("errorMessage");
					request.setAttribute("_ERROR_MESSAGE_", resultMsg);
					return "error";
				}
			}catch (GenericServiceException e) {
				Debug.logError("Error while coverting Dbf To sql "+e, module);
				request.setAttribute("_ERROR_MESSAGE_","Error while coverting mpMas To sql :"+e.getMessage());
				return "error";
			}
		}
		String resultMsg = (String)dbfToVbizResultMap.get("successMessage");
		request.setAttribute("_EVENT_MESSAGE_", resultMsg);
		return "success";
	}//End of the service 
	public static String uploadCsvFile(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String errorMessage ="";  
		String tmpUploadRepository = UtilProperties.getPropertyValue(
				"general.properties", "http.upload.tmprepository",
				"runtime/tmp");
		ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory(
				10240, new File(tmpUploadRepository))); // Creation of
														// servletfileupload

		java.util.List lst = null;
		File dir=new File(curDir + "/"+ tmpUploadRepository);
		
		String file_name = "";
		try {
			lst = UtilGenerics.checkList(fu.parseRequest(request));
		} catch (FileUploadException fup_ex) {
			fup_ex.printStackTrace();
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);			
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		if (lst.size() == 0) // There is no item in lst
		{
			errorMessage = "There is no item in zip file";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		FileItem file_item = null;
		FileItem selected_file_item = null;

		// Checking for form fields - Start
		for (int i = 0; i < lst.size(); i++) {
			file_item = (FileItem) lst.get(i);
			String fieldName = file_item.getFieldName();
			// Check for the attributes for user selected file - Start
			if (fieldName.equals("uploadedFile")) {
				selected_file_item = file_item;
				file_name = tmpUploadRepository + "/" + file_item.getName(); // Getting
																				// the
																				// file
																				// name
				break;
			}
			// Check for the attributes for user selected file - End
		}
		// Checking for form fields - End

		// Uploading the file content - Start
		if (selected_file_item == null) // If selected file item is null
		{
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		byte[] file_bytes = selected_file_item.get();
		byte[] extract_bytes = new byte[file_bytes.length];

		for (int l = 0; l < file_bytes.length; l++) {
			extract_bytes[l] = file_bytes[l];
		}

		if (extract_bytes == null) {
			errorMessage = "AttachementException";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
            	
		}
		// Creation & writing to the file in server - Start
		FileOutputStream fout=null;
		
		try {
			fout = new FileOutputStream(file_name);
			fout.flush();
			fout.write(extract_bytes);
			
		} catch (IOException ioe_ex) {
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
			          	
		} 
		finally{
			fout.flush();
			fout.close();			
		}
		 Map<String, Object> dbfConversionResult = ServiceUtil.returnSuccess();
		 Map<String, Object> dbfConversionToolCtx = FastMap.newInstance();
		 dbfConversionToolCtx.put("userLogin", userLogin);                                                                
		 dbfConversionToolCtx.put("fileName",file_name.replace(tmpUploadRepository+"/" ,""));                    
		 dbfConversionToolCtx.put("fileLocation", tmpUploadRepository);
		 dbfConversionResult = dispatcher.runSync("dbfConversionTool", dbfConversionToolCtx);
		if (ServiceUtil.isError(dbfConversionResult)) {
			emptyTmpDir(dir);
			Debug.logWarning("There was an error Dbf conversion :" + ServiceUtil.getErrorMessage(dbfConversionResult), module);
			request.setAttribute("_ERROR_MESSAGE_", "There was an error Dbf conversion: " + ServiceUtil.getErrorMessage(dbfConversionResult));
			return "error";
        }
		 String outputFilePath = (String)dbfConversionResult.get("outputFile");
		ServletOutputStream out = null;			
		InputStream in = null;
		try{
			out = response.getOutputStream();
			in = new FileInputStream(curDir + "/"
					+ outputFilePath);
			response.setContentType("application/x-dbase");
			response.setHeader("Content-Disposition","inline; filename="+outputFilePath.replace(tmpUploadRepository+"/" ,"")+";"); 				
			String mimeType = "application/x-dbase";
			byte[] bytes = new byte[1024];
			int bytesRead;

			response.setContentType(mimeType);

			while ((bytesRead = in.read(bytes)) != -1) {
				out.write(bytes, 0, bytesRead);
			}

			// do the following in a finally block:
			
			
		} catch (IOException ioe_ex) {
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
			          	
		}
		finally{
			out.flush();
			out.close();
			in.close();	
			emptyTmpDir(dir);
		}
		
		// Creation & writing to the file in server - End
		return "success";
		// Uploading the file content - End
	}
	
	public static String importExcelEntityData(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String errorMessage ="";  
		Map paramMap = UtilHttp.getParameterMap(request);
		
		String tmpUploadRepository = UtilProperties.getPropertyValue(
				"general.properties", "http.upload.tmprepository",
				"runtime/tmp");
		ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory(
				10240, new File(tmpUploadRepository))); // Creation of
														// servletfileupload

		java.util.List lst = null;
		File dir=new File(curDir + "/"+ tmpUploadRepository);
		
		String file_name = "";
		try {
			lst = UtilGenerics.checkList(fu.parseRequest(request));
		} catch (FileUploadException fup_ex) {
			fup_ex.printStackTrace();
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage+fup_ex, module);			
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}
		String fileName ="";
		FileItem file_item = null;
		FileItem selected_file_item = null;

		// Checking for form fields - Start
		for (int i = 0; i < lst.size(); i++) {
			file_item = (FileItem) lst.get(i);
			String fieldName = file_item.getFieldName();
			// Check for the attributes for user selected file - Start
			if (fieldName.equals("uploadedFile")) {
				selected_file_item = file_item;
				file_name = tmpUploadRepository + "/" + file_item.getName(); // Getting
																				// the
																				// file
																				// name
				break;
			}
			// Check for the attributes for user selected file - End
		}
		// Checking for form fields - End

		// Uploading the file content - Start
		if (selected_file_item == null) // If selected file item is null
		{
			errorMessage = "Attachement not found";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
		}

		byte[] file_bytes = selected_file_item.get();
		byte[] extract_bytes = new byte[file_bytes.length];

		for (int l = 0; l < file_bytes.length; l++) {
			extract_bytes[l] = file_bytes[l];
		}

		if (extract_bytes == null) {
			errorMessage = "AttachementException";
			Debug.logError(errorMessage, module);
			request.setAttribute("_ERROR_MESSAGE_",errorMessage);
			return "error";
            	
		}
		// Creation & writing to the file in server - Start
		FileOutputStream fout=null;
		File fileCsv = new File(tmpUploadRepository + "/" +"excelToCsv.csv");
		FileOutputStream csvOut = new FileOutputStream(fileCsv);
		
		try {
			fout = new FileOutputStream(file_name);
			fout.flush();
			fout.write(extract_bytes);
			
			Workbook workbook = Workbook.getWorkbook(new File(file_name));
			new ExcelToCSV(workbook,csvOut,null,false,IN_DATA_DIR);
			csvOut.flush();
			csvOut.close();
			
		} catch (IOException ioe_ex) {
			Debug.logInfo(ioe_ex.getMessage(), module);			
			request.setAttribute("_ERROR_MESSAGE_",ioe_ex.getMessage());
			return "error";
		} 
		finally{
			fout.flush();
			fout.close();
			csvOut.flush();
			csvOut.close();
		}
		SlurpCSV.processFiles(IN_DATA_DIR, OUT_DATA_DIR);
		Map entityImportDirMap = FastMap.newInstance();
		Map resultMap = FastMap.newInstance();
		entityImportDirMap.put("userLogin", userLogin);
		entityImportDirMap.put("path", OUT_DATA_DIR_UPLOAD);
		entityImportDirMap.put("recursiveImport", Boolean.TRUE);
		try{
			resultMap = dispatcher.runSync("entityImportDir",entityImportDirMap);
		    if(ServiceUtil.isError(resultMap)){
		    	emptyTmpDir(fileCsv);
			    emptyTmpDir(new File(file_name));
			    emptyTmpDir(new File(OUT_DATA_DIR_UPLOAD));
			    emptyTmpDir(new File(IN_DATA_DIR));
					String resultMsg = (String)resultMap.get("errorMessage");
					request.setAttribute("_ERROR_MESSAGE_", resultMsg);
					return "error";
				}
		    //empty all directories 
		    emptyTmpDir(fileCsv);
		    emptyTmpDir(new File(file_name));
		    emptyTmpDir(new File(OUT_DATA_DIR_UPLOAD));
		    emptyTmpDir(new File(IN_DATA_DIR));
		    
			}catch (GenericServiceException e) {
				Debug.logError("Error while coverting mpMas To sql "+e, module);
				request.setAttribute("_ERROR_MESSAGE_","Error while coverting mpMas To sql :"+e.getMessage());
				return "error";
			}
		//fileName = file_name.replace(tmpUploadRepository+"/" ,"");
		//String resultMsg = (String)resultMap.get("messages"); 
		request.setAttribute("_EVENT_MESSAGE_", (resultMap.get("messages")).toString());
		return "success";
	}//End of the service
	
}