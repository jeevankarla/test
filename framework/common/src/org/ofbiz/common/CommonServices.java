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
package org.ofbiz.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.xa.XAException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.http.HttpRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

import static org.ofbiz.base.util.UtilGenerics.checkList;
import static org.ofbiz.base.util.UtilGenerics.checkMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBase;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceXaWrapper;
import org.ofbiz.service.mail.MimeMessageWrapper;
import com.linuxense.javadbf.*;
import java.io.*;
/**
 * Common Services
 */
public class CommonServices {

    public final static String module = CommonServices.class.getName();
    public static final String resource = "CommonUiLabels";

    /**
     * Generic Test Service
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> testService(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> response = ServiceUtil.returnSuccess();

        if (context.size() > 0) {
            for (Map.Entry<String, ?> entry: context.entrySet()) {
                Object cKey = entry.getKey();
                Object value = entry.getValue();

                System.out.println("---- SVC-CONTEXT: " + cKey + " => " + value);
            }
        }
        if (!context.containsKey("message")) {
            response.put("resp", "no message found");
        } else {
            System.out.println("-----SERVICE TEST----- : " + (String) context.get("message"));
            response.put("resp", "service done");
        }

        System.out.println("----- SVC: " + dctx.getName() + " -----");
        return response;
    }

    /**
     * Generic Test SOAP Service
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> testSOAPService(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> response = ServiceUtil.returnSuccess();

        List<GenericValue> testingNodes = FastList.newInstance();
        for (int i = 0; i < 3; i ++) {
            GenericValue testingNode = delegator.makeValue("TestingNode");
            testingNode.put("testingNodeId", "TESTING_NODE" + i);
            testingNode.put("description", "Testing Node " + i);
            testingNode.put("createdStamp", UtilDateTime.nowTimestamp());
            testingNodes.add(testingNode);
        }
        response.put("testingNodes", testingNodes);
        return response;
    }

    public static Map<String, Object> blockingTestService(DispatchContext dctx, Map<String, ?> context) {
        Long duration = (Long) context.get("duration");
        if (duration == null) {
            duration = 30000l;
        }
        System.out.println("-----SERVICE BLOCKING----- : " + duration/1000d +" seconds");
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
        }
        return CommonServices.testService(dctx, context);
    }

    public static Map<String, Object> testRollbackListener(DispatchContext dctx, Map<String, ?> context) {
        Locale locale = (Locale) context.get("locale");
        ServiceXaWrapper xar = new ServiceXaWrapper(dctx);
        xar.setRollbackService("testScv", context);
        try {
            xar.enlist();
        } catch (XAException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonTestRollingBack", locale));
    }

    public static Map<String, Object> testCommitListener(DispatchContext dctx, Map<String, ?> context) {
        ServiceXaWrapper xar = new ServiceXaWrapper(dctx);
        xar.setCommitService("testScv", context);
        try {
            xar.enlist();
        } catch (XAException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * Create Note Record
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> createNote(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp noteDate = (Timestamp) context.get("noteDate");
        String partyId = (String) context.get("partyId");
        String noteName = (String) context.get("noteName");
        String note = (String) context.get("note");
        String noteType = (String) context.get("noteType");
        String noteId = delegator.getNextSeqId("NoteData");
        Locale locale = (Locale) context.get("locale");
        if (noteDate == null) {
            noteDate = UtilDateTime.nowTimestamp();
        }

        // check for a party id
        if (partyId == null) {
            if (userLogin != null && userLogin.get("partyId") != null)
                partyId = userLogin.getString("partyId");
        }

        Map<String, String> fields = UtilMisc.toMap("noteId", noteId, "noteName", noteName, "noteInfo", note, "noteType",noteType,
                "noteParty", partyId, "noteDateTime", noteDate);

        try {
            GenericValue newValue = delegator.makeValue("NoteData", fields);

            delegator.create(newValue);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();

        result.put("noteId", noteId);
        result.put("partyId", partyId);
        return result;
    }
    
    public static Map<String, Object> removePartyNote(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String noteId = (String) context.get("noteId");
        Locale locale = (Locale) context.get("locale");
        try {
 			GenericValue partyNote = delegator.findOne("PartyNote", UtilMisc.toMap("partyId", partyId, "noteId", noteId), true);
			delegator.removeValue(partyNote);
			
			GenericValue noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", noteId), true);
			delegator.removeValue(noteData);

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("partyId", partyId);
        return result;
    }
    
    /**
     * Service for setting debugging levels.
     *@param dctc The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> adjustDebugLevels(DispatchContext dctc, Map<String, ?> context) {
        Debug.set(Debug.FATAL, "Y".equalsIgnoreCase((String) context.get("fatal")));
        Debug.set(Debug.ERROR, "Y".equalsIgnoreCase((String) context.get("error")));
        Debug.set(Debug.WARNING, "Y".equalsIgnoreCase((String) context.get("warning")));
        Debug.set(Debug.IMPORTANT, "Y".equalsIgnoreCase((String) context.get("important")));
        Debug.set(Debug.INFO, "Y".equalsIgnoreCase((String) context.get("info")));
        Debug.set(Debug.TIMING, "Y".equalsIgnoreCase((String) context.get("timing")));
        Debug.set(Debug.VERBOSE, "Y".equalsIgnoreCase((String) context.get("verbose")));

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> addOrUpdateLogger(DispatchContext dctc, Map<String, ?> context) {
        String name = (String) context.get("name");
        String level = (String) context.get("level");
        boolean additivity = "Y".equalsIgnoreCase((String) context.get("additivity"));

        Logger logger = null;
        if ("root".equals(name)) {
            logger = Logger.getRootLogger();
        } else {
            logger = Logger.getLogger(name);
        }
        logger.setLevel(Level.toLevel(level));
        logger.setAdditivity(additivity);

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> forceGc(DispatchContext dctx, Map<String, ?> context) {
        System.gc();
        return ServiceUtil.returnSuccess();
    }

    /**
     * Echo service; returns exactly what was sent.
     * This service does not have required parameters and does not validate
     */
     public static Map<String, Object> echoService(DispatchContext dctx, Map<String, ?> context) {
         Map<String, Object> result = FastMap.newInstance();
         result.putAll(context);
         result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
         return result;
     }

    /**
     * Return Error Service; Used for testing error handling
     */
    public static Map<String, Object> returnErrorService(DispatchContext dctx, Map<String, ?> context) {
        Locale locale = (Locale) context.get("locale");
        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonServiceReturnError", locale));
    }

    /**
     * Return TRUE Service; ECA Condition Service
     */
    public static Map<String, Object> conditionTrueService(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("conditionReply", Boolean.TRUE);
        return result;
    }

    /**
     * Return FALSE Service; ECA Condition Service
     */
    public static Map<String, Object> conditionFalseService(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("conditionReply", Boolean.FALSE);
        return result;
    }

    /** Cause a Referential Integrity Error */
    public static Map<String, Object> entityFailTest(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        // attempt to create a DataSource entity w/ an invalid dataSourceTypeId
        GenericValue newEntity = delegator.makeValue("DataSource");
        newEntity.set("dataSourceId", "ENTITY_FAIL_TEST");
        newEntity.set("dataSourceTypeId", "ENTITY_FAIL_TEST");
        newEntity.set("description", "Entity Fail Test - Delete me if I am here");
        try {
            delegator.create(newEntity);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEntityTestFailure", locale));
        }

        /*
        try {
            newEntity.remove();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        */

        return ServiceUtil.returnSuccess();
    }

    /** Test entity sorting */
    public static Map<String, Object> entitySortTest(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        Set<ModelEntity> set = new TreeSet<ModelEntity>();

        set.add(delegator.getModelEntity("Person"));
        set.add(delegator.getModelEntity("PartyRole"));
        set.add(delegator.getModelEntity("Party"));
        set.add(delegator.getModelEntity("ContactMech"));
        set.add(delegator.getModelEntity("PartyContactMech"));
        set.add(delegator.getModelEntity("OrderHeader"));
        set.add(delegator.getModelEntity("OrderItem"));
        set.add(delegator.getModelEntity("OrderContactMech"));
        set.add(delegator.getModelEntity("OrderRole"));
        set.add(delegator.getModelEntity("Product"));
        set.add(delegator.getModelEntity("RoleType"));

        for (ModelEntity modelEntity: set) {
            Debug.log(modelEntity.getEntityName(), module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> makeALotOfVisits(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        int count = ((Integer) context.get("count")).intValue();

        for (int i = 0; i < count; i++) {
            GenericValue v = delegator.makeValue("Visit");
            String seqId = delegator.getNextSeqId("Visit");

            v.set("visitId", seqId);
            v.set("userCreated", "N");
            v.set("sessionId", "NA-" + seqId);
            v.set("serverIpAddress", "127.0.0.1");
            v.set("serverHostName", "localhost");
            v.set("webappName", "webtools");
            v.set("initialLocale", "en_US");
            v.set("initialRequest", "http://localhost:8080/webtools/control/main");
            v.set("initialReferrer", "http://localhost:8080/webtools/control/main");
            v.set("initialUserAgent", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-us) AppleWebKit/124 (KHTML, like Gecko) Safari/125.1");
            v.set("clientIpAddress", "127.0.0.1");
            v.set("clientHostName", "localhost");
            v.set("fromDate", UtilDateTime.nowTimestamp());

            try {
                delegator.create(v);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> displayXaDebugInfo(DispatchContext dctx, Map<String, ?> context) {
        if (TransactionUtil.debugResources) {
            if (UtilValidate.isNotEmpty(TransactionUtil.debugResMap)) {
                TransactionUtil.logRunningTx();
            } else {
                Debug.log("No running transaction to display.", module);
            }
        } else {
            Debug.log("Debug resources is disabled.", module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> byteBufferTest(DispatchContext dctx, Map<String, ?> context) {
        ByteBuffer buffer1 = (ByteBuffer) context.get("byteBuffer1");
        ByteBuffer buffer2 = (ByteBuffer) context.get("byteBuffer2");
        String fileName1 = (String) context.get("saveAsFileName1");
        String fileName2 = (String) context.get("saveAsFileName2");
        String ofbizHome = System.getProperty("ofbiz.home");
        String outputPath1 = ofbizHome + (fileName1.startsWith("/") ? fileName1 : "/" + fileName1);
        String outputPath2 = ofbizHome + (fileName2.startsWith("/") ? fileName2 : "/" + fileName2);

        try {
            RandomAccessFile file1 = new RandomAccessFile(outputPath1, "rw");
            RandomAccessFile file2 = new RandomAccessFile(outputPath2, "rw");
            file1.write(buffer1.array());
            file2.write(buffer2.array());
        } catch (FileNotFoundException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> uploadTest(DispatchContext dctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        byte[] array = (byte[]) context.get("uploadFile");
        String fileName = (String) context.get("_uploadFile_fileName");
        String contentType = (String) context.get("_uploadFile_contentType");

        Map<String, Object> createCtx = FastMap.newInstance();
        createCtx.put("binData", array);
        createCtx.put("dataResourceTypeId", "OFBIZ_FILE");
        createCtx.put("dataResourceName", fileName);
        createCtx.put("dataCategoryId", "PERSONAL");
        createCtx.put("statusId", "CTNT_PUBLISHED");
        createCtx.put("mimeTypeId", contentType);
        createCtx.put("userLogin", userLogin);

        Map<String, Object> createResp = null;
        try {
            createResp = dispatcher.runSync("createFile", createCtx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(createResp)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResp));
        }

        GenericValue dataResource = (GenericValue) createResp.get("dataResource");
        if (dataResource != null) {
            Map<String, Object> contentCtx = FastMap.newInstance();
            contentCtx.put("dataResourceId", dataResource.getString("dataResourceId"));
            contentCtx.put("localeString", ((Locale) context.get("locale")).toString());
            contentCtx.put("contentTypeId", "DOCUMENT");
            contentCtx.put("mimeTypeId", contentType);
            contentCtx.put("contentName", fileName);
            contentCtx.put("statusId", "CTNT_PUBLISHED");
            contentCtx.put("userLogin", userLogin);

            Map<String, Object> contentResp = null;
            try {
                contentResp = dispatcher.runSync("createContent", contentCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(contentResp)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(contentResp));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> simpleMapListTest(DispatchContext dctx, Map<String, ?> context) {
        List<String> listOfStrings = checkList(context.get("listOfStrings"), String.class);
        Map<String, String> mapOfStrings = checkMap(context.get("mapOfStrings"), String.class, String.class);

        for (String str: listOfStrings) {
            String v = mapOfStrings.get(str);
            Debug.log("SimpleMapListTest: " + str + " -> " + v, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> mcaTest(DispatchContext dctx, Map<String, ?> context) {
        MimeMessageWrapper wrapper = (MimeMessageWrapper) context.get("messageWrapper");
        MimeMessage message = wrapper.getMessage();
        try {
            if (message.getAllRecipients() != null) {
               Debug.log("To: " + UtilMisc.toListArray(message.getAllRecipients()), module);
            }
            if (message.getFrom() != null) {
               Debug.log("From: " + UtilMisc.toListArray(message.getFrom()), module);
            }
            Debug.log("Subject: " + message.getSubject(), module);
            if (message.getSentDate() != null) {
                Debug.log("Sent: " + message.getSentDate().toString(), module);
            }
            if (message.getReceivedDate() != null) {
                Debug.log("Received: " + message.getReceivedDate().toString(), module);
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> streamTest(DispatchContext dctx, Map<String, ?> context) {
        InputStream in = (InputStream) context.get("inputStream");
        OutputStream out = (OutputStream) context.get("outputStream");

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Writer writer = new OutputStreamWriter(out);
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                Debug.log("Read line: " + line, module);
                writer.write(line);
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("contentType", "text/plain");
        return result;
    }

    public static Map<String, Object> ping(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();
        String message = (String) context.get("message");
        Locale locale = (Locale) context.get("locale");
        if (message == null) {
            message = "PONG";
        }

        long count = -1;
        try {
            count = delegator.findCountByCondition("SequenceValueItem", null, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonPingDatasourceCannotConnect", locale));
        }

        if (count > 0) {
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.put("message", message);
            return result;
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonPingDatasourceInvalidCount", locale));
        }
    }
    
 public static Map<String, Object> runCustomTimePeriodConfiguration(DispatchContext ctx, Map<String, ? extends Object> context) {
	 	Delegator delegator = ctx.getDelegator();
	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	TimeZone timeZone = TimeZone.getDefault();
		Locale locale = Locale.getDefault();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	 	int  day = UtilDateTime.getDayOfMonth(nowTimestamp, timeZone, locale);
	 	Timestamp monthStart = UtilDateTime.getMonthStart(nowTimestamp);
	 	List<GenericValue> customTimePeriodConfigList =FastList.newInstance();
	 	Map<String,Object> createResp= ServiceUtil.returnSuccess();
	 	try{
	 		customTimePeriodConfigList = delegator.findList("CustomTimePeriodConfiguration", EntityCondition.makeCondition(EntityCondition.makeCondition("startDate",EntityOperator.EQUALS,new Long(day)),EntityOperator.OR,EntityCondition.makeCondition("createDate",EntityOperator.EQUALS,new Long(day))), null, null, null, true);
	 		for( GenericValue customTimePeriodConfig :customTimePeriodConfigList){
	 			Map createCtx = FastMap.newInstance();
	 			String organizationPartyId = customTimePeriodConfig.getString("organizationPartyId");
	 			int startDate = (customTimePeriodConfig.getLong("startDate")).intValue();
	 			int endDate = (customTimePeriodConfig.getLong("endDate")).intValue();
	 			int intervalDays = (customTimePeriodConfig.getLong("intervalDays")).intValue();
	 			createCtx.put("periodTypeId", customTimePeriodConfig.getString("periodTypeId"));
	 			createCtx.put("organizationPartyId", organizationPartyId);
	 			if(UtilValidate.isEmpty(organizationPartyId)){
	 				createCtx.put("organizationPartyId", "Company");
	 			}
	 			Date fromDate = new Date(UtilDateTime.addDaysToTimestamp(monthStart, startDate-1).getTime());
	 			Timestamp thruDateMonthStart = UtilDateTime.getMonthStart(UtilDateTime.addDaysToTimestamp(monthStart, intervalDays));
	 			Date thruDate = new Date(UtilDateTime.addDaysToTimestamp(thruDateMonthStart, endDate-1).getTime());
	 			createCtx.put("fromDate",fromDate);
	 			
	 			List custTimePeriodList = delegator.findByAnd("CustomTimePeriod", createCtx);
	 			if(UtilValidate.isNotEmpty(custTimePeriodList)){
	 				Debug.logInfo("Custom timeperiod already exists with values:[ periodTypeId :"+createCtx.get("periodTypeId") +",organizationPartyId : "+createCtx.get("organizationPartyId") +" ,fromDate : "+createCtx.get("fromDate")+" ]", module);
	 				continue;
	 				
	 			}
	 			createCtx.put("userLogin",userLogin);	 			
	 			if( intervalDays <= 30 && endDate == 30){
	 				thruDate = new Date(UtilDateTime.getMonthEnd(monthStart, timeZone, locale).getTime());	 				
	 			}
	 			if( intervalDays > 30 && endDate == 30){
	 				thruDate = new Date(UtilDateTime.getMonthEnd(thruDateMonthStart, timeZone, locale).getTime());	 				
	 			}
	 			createCtx.put("thruDate",thruDate);
	 			GenericValue periodType = delegator.findOne("PeriodType", UtilMisc.toMap("periodTypeId", customTimePeriodConfig.getString("periodTypeId")), true);
	 			String periodName = periodType.getString("description") +" [ "+UtilDateTime.toDateString(fromDate, "dd MMMM,yyyy")+"-"+UtilDateTime.toDateString(thruDate, "dd MMMM,yyyy")+" ]";
	 			createCtx.put("periodName",periodName);
	 			createCtx.put("isClosed","N");
	 			int periodNum = (UtilDateTime.getYear(monthStart, timeZone, locale)*12)+ UtilDateTime.getMonth(monthStart, timeZone, locale);
	 			createCtx.put("periodNum",new Long(periodNum) );	 			
	 			 try {
	 	            createResp = dispatcher.runSync("createCustomTimePeriod", createCtx);
	 	        } catch (GenericServiceException e) {
	 	            Debug.logError(e, module);
	 	            return ServiceUtil.returnError(e.getMessage());
	 	        }
	 	        if (ServiceUtil.isError(createResp)) {
	 	            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createResp));
	 	        }
	 			
	 		}
	 	}catch (GenericEntityException e) {
			// TODO: handle exception
	 		Debug.logError(e, module);
	        return ServiceUtil.returnError(e.getMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
 
  public static Map<String, Object> dbfConversionTool(DispatchContext ctx, Map<String, ? extends Object> context) {
	  	 Delegator delegator = ctx.getDelegator();
	 	 LocalDispatcher dispatcher = ctx.getDispatcher();
	 	 GenericValue userLogin = (GenericValue) context.get("userLogin");
	 	 String ofbizHome = System.getProperty("ofbiz.home");
	 	 String fileLocation = (String) context.get("fileLocation");
	 	String fileName = (String) context.get("fileName");
	 	  // let us create field definitions first
	     // we will go for 5 fields
	 	String filePath = fileLocation+"/"+fileName;
	 	String dbfFilePath = filePath.replace(".csv", ".DBF");
	 try{	  
	 	 File csvFile = new File(filePath);
	 	BufferedReader buffReader = new BufferedReader(new FileReader(csvFile));
		String line;
		line = buffReader.readLine();
		if (line == null || line.isEmpty()) {
			System.err.println("Input file '" + csvFile + "' is empty");
			System.err.flush();
			throw new IOException("Input file '" + csvFile + "' is empty");
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
				System.err.println("Input file '" + csvFile
						+ "': entityField '" + entityField + "' missing _ ");
				System.err.flush();
				throw new IOException("Input file '" + csvFile
						+ "': entityField '" + entityField + "' missing _ ");
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
						+ csvFile.getName() + "' :: row '" + rowNum + "' {"
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
		    	 Debug.logInfo("fieldValueList==========="+fieldValueList ,module);		    	 
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
		     
		     FileOutputStream fos = new FileOutputStream(dbfFilePath);
		     writer.write( fos);
		     fos.close();		    

	     }catch (Exception e) {
			// TODO: handle exception
	    	 String errMsg = "There was an error creating Dbf"+e.toString();
	    	 Debug.logError(errMsg, module);
	    	 return ServiceUtil.returnError(errMsg);
		} 	 
	     Map<String, Object> result = ServiceUtil.returnSuccess();
	     result.put("outputFile", dbfFilePath);
	 	return result;
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
		Debug.log("pre process line==============="+line);
		result.add(line);
		result.add(flag);
		return result;
	}
    
}
