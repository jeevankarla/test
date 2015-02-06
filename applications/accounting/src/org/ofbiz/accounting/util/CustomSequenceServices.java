package org.ofbiz.accounting.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

/**
* Custom Sequences are for custom sequence number generation for entities.*/

public class CustomSequenceServices {
	public static String module = CustomSequenceServices.class.getName();
   
	//new Service for Billing Sequence for GRN
	public static Map<String, Object> createShipmentReceiptSequence(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();              
        String receiptSequenceTypeId = (String) context.get("receiptSequenceTypeId");
        String receiptId = (String) context.get("receiptId");
        Timestamp receiptDueDate=(Timestamp) context.get("receiptDueDate");
        Locale locale = (Locale) context.get("locale");        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Debug.log("==receiptSequenceTypeId="+receiptSequenceTypeId+"=receiptId="+receiptId+"=receiptDueDate="+receiptDueDate);
        try {
        	Boolean enableGRNSequence  = Boolean.FALSE;
    		GenericValue tenantConfigEnableTaxInvSeq = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","enableGRNSequence"), false);
       		if (UtilValidate.isNotEmpty(tenantConfigEnableTaxInvSeq) && (tenantConfigEnableTaxInvSeq.getString("propertyValue")).equals("Y")) {
       			enableGRNSequence = Boolean.TRUE;
       		}
       		if(enableGRNSequence && UtilValidate.isNotEmpty(receiptId)){
       			Timestamp datetimeReceived=null;
       			if(UtilValidate.isEmpty(receiptDueDate)){
       			List<GenericValue> ShipmentReceiptItems = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS, receiptId), UtilMisc.toSet("datetimeReceived"), null, null, false);
       			datetimeReceived = (EntityUtil.getFirst(ShipmentReceiptItems)).getTimestamp("datetimeReceived");
       			}else{
       				datetimeReceived=receiptDueDate;
       			}
       			if(UtilValidate.isEmpty(datetimeReceived)){
						Debug.logError("ReceiptDueDate can not be empty in GRN Sequence generation !", module);
						return ServiceUtil.returnError("ReceiptDueDate can not be empty in GRN Sequence generation ! ");
				}
       			
       			Map finYearContext = FastMap.newInstance();
   				finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
   				finYearContext.put("organizationPartyId", "Company");
   				finYearContext.put("userLogin", userLogin);
   				finYearContext.put("findDate", datetimeReceived);
   				finYearContext.put("excludeNoOrganizationPeriods", "Y");
   				List customTimePeriodList = FastList.newInstance();
   				Map resultCtx = FastMap.newInstance();
   				try{
   					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
   					if(ServiceUtil.isError(resultCtx)){
   						Debug.logError("Problem in fetching financial year ", module);
   						return ServiceUtil.returnError("Problem in fetching financial year ");
   					}
   				}catch(GenericServiceException e){
   					Debug.logError(e, module);
   					return ServiceUtil.returnError(e.getMessage());
   				}
   				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
   				String finYearId = "";
   				if(UtilValidate.isNotEmpty(customTimePeriodList)){
   					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
   					finYearId = (String)customTimePeriod.get("customTimePeriodId");
   				}
       			if(UtilValidate.isEmpty(receiptSequenceTypeId)){
       				receiptSequenceTypeId="GRN_SEQUENCE";
       			}
   				GenericValue shipmentReceiptSequence = delegator.makeValue("ShipmentReceiptSequence");
   				shipmentReceiptSequence.put("receiptSequenceTypeId",receiptSequenceTypeId );
   				shipmentReceiptSequence.put("receiptId", receiptId);
   				shipmentReceiptSequence.put("finYearId", finYearId);
   				shipmentReceiptSequence.put("receiptDueDate", datetimeReceived);
				delegator.setNextSubSeqId(shipmentReceiptSequence, "sequenceId", 10, 1);
	            delegator.create(shipmentReceiptSequence);
	            String sequenceId = (String) shipmentReceiptSequence.get("sequenceId");
	            result.put("sequenceId", sequenceId) ;
       			}
        }catch(Exception e){
        	Debug.logError(e, e.toString(), module);
        	return ServiceUtil.returnError(e.toString()+" Error While Creating GRN Sequence for receiptSequenceTypeId:"+receiptSequenceTypeId+" receiptId:"+receiptId);
        }
        return result;
	}
	//new Service for Billing Sequence for CustRequest
		public static Map<String, Object> createCustRequestSequence(DispatchContext dctx, Map<String, Object> context) {
			Delegator delegator = dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();              
	        String custReqSequenceTypeId = (String) context.get("custReqSequenceTypeId");
	        String custRequestId = (String) context.get("custRequestId");
	        Timestamp custRequestDate=(Timestamp) context.get("custRequestDate");
	        Locale locale = (Locale) context.get("locale");        
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Map<String, Object> result = ServiceUtil.returnSuccess();
	        try {
	        	Boolean enableCustReqSequence  = Boolean.FALSE;
	    		GenericValue tenantConfigEnableSeq = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","enableCustReqSequence"), false);
	       		if (UtilValidate.isNotEmpty(tenantConfigEnableSeq) && (tenantConfigEnableSeq.getString("propertyValue")).equals("Y")) {
	       			enableCustReqSequence = Boolean.TRUE;
	       		}
	       		if(enableCustReqSequence && UtilValidate.isNotEmpty(custRequestId)){
	       			GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
	       			//at present sequence is generated only for Enquiry
	       			if( UtilValidate.isNotEmpty(custRequest) && ("RF_PUR_QUOTE".equals(custRequest.getString("custRequestTypeId"))) ){
	       				custRequestDate = custRequest.getTimestamp("custRequestDate");
		       			
		       			if(UtilValidate.isEmpty(custRequestDate)){
								Debug.logError("CustRequestDate can not be empty in CustRequestSequence generation !", module);
								return ServiceUtil.returnError("CustRequestDate can not be empty in CustRequestSequence generation ! ");
						}
		       			
	       			Map finYearContext = FastMap.newInstance();
	   				finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
	   				finYearContext.put("organizationPartyId", "Company");
	   				finYearContext.put("userLogin", userLogin);
	   				finYearContext.put("findDate", custRequestDate);
	   				finYearContext.put("excludeNoOrganizationPeriods", "Y");
	   				List customTimePeriodList = FastList.newInstance();
	   				Map resultCtx = FastMap.newInstance();
	   				try{
	   					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
	   					if(ServiceUtil.isError(resultCtx)){
	   						Debug.logError("Problem in fetching financial year ", module);
	   						return ServiceUtil.returnError("Problem in fetching financial year ");
	   					}
	   				}catch(GenericServiceException e){
	   					Debug.logError(e, module);
	   					return ServiceUtil.returnError(e.getMessage());
	   				}
	   				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
	   				String finYearId = "";
	   				if(UtilValidate.isNotEmpty(customTimePeriodList)){
	   					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
	   					finYearId = (String)customTimePeriod.get("customTimePeriodId");
	   				}
	   				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",finYearId), false);
	   				Timestamp timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	   				Timestamp timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
	       			if(UtilValidate.isEmpty(custReqSequenceTypeId)){
	       				custReqSequenceTypeId="ENQUIRY_SEQUENCE";
	       			}
	   				GenericValue custRequestSequence = delegator.makeValue("CustRequestSequence");
	   				custRequestSequence.put("custReqSequenceTypeId",custReqSequenceTypeId );
	   				custRequestSequence.put("custRequestId", custRequestId);
	   				custRequestSequence.put("finYearId", finYearId);
	   				//custRequestSequence.put("custRequestNo", custRequestId+"/"+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate"),"yyyy")+"-"+UtilDateTime.toDateString(customTimePeriod.getDate("thruDate"),"yy"));
					delegator.setNextSubSeqId(custRequestSequence, "sequenceId", 6, 1);
					String sequenceId = (String) custRequestSequence.get("sequenceId");
					custRequestSequence.put("custRequestNo", sequenceId+"/"+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate"),"yyyy")+"-"+UtilDateTime.toDateString(customTimePeriod.getDate("thruDate"),"yy"));
		            delegator.createOrStore(custRequestSequence);
		            
		            result.put("sequenceId", sequenceId) ;
	       			}
	       		}
	        }catch(Exception e){
	        	Debug.logError(e, e.toString(), module);
	        	return ServiceUtil.returnError(e.toString()+" Error While Creating CustRequest Sequence for custReqSequenceTypeId:"+custReqSequenceTypeId+" custRequestId:"+custRequestId);
	        }
	        return result;
		}
		//new Service for Billing Sequence for PO
				public static Map<String, Object> createOrderHeaderSequence(DispatchContext dctx, Map<String, Object> context) {
					Delegator delegator = dctx.getDelegator();
			        LocalDispatcher dispatcher = dctx.getDispatcher();              
			        String orderHeaderSequenceTypeId = (String) context.get("orderHeaderSequenceTypeId");
			        String orderId = (String) context.get("orderId");
			        Locale locale = (Locale) context.get("locale");     
			        Timestamp orderDate=(Timestamp) context.get("orderDate");
			        
			        GenericValue userLogin = (GenericValue) context.get("userLogin");
			        Map<String, Object> result = ServiceUtil.returnSuccess();
			        try {
			        	Boolean enablePOSequence  = Boolean.FALSE;
			    		GenericValue tenantConfigEnableSeq = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","enablePOSequence"), false);
			       		if (UtilValidate.isNotEmpty(tenantConfigEnableSeq) && (tenantConfigEnableSeq.getString("propertyValue")).equals("Y")) {
			       			enablePOSequence = Boolean.TRUE;
			       		}
			       		if(enablePOSequence && UtilValidate.isNotEmpty(orderId)){
			       			GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
			       			//at present sequence is generated only for OrderHeader
			       			//if( UtilValidate.isNotEmpty(custRequest) && ("RF_PUR_QUOTE".equals(custRequest.getString("custRequestTypeId"))) ){
			       			orderDate = orderHeader.getTimestamp("orderDate");
				       			if(UtilValidate.isEmpty(orderDate)){
										Debug.logError("orderDate can not be empty in OrderHeaderSequence generation !", module);
										return ServiceUtil.returnError("orderDate can not be empty in OrderHeaderSequence generation ! ");
								}
				       			
			       			Map finYearContext = FastMap.newInstance();
			   				finYearContext.put("onlyIncludePeriodTypeIdList", UtilMisc.toList("FISCAL_YEAR"));
			   				finYearContext.put("organizationPartyId", "Company");
			   				finYearContext.put("userLogin", userLogin);
			   				finYearContext.put("findDate", orderDate);
			   				finYearContext.put("excludeNoOrganizationPeriods", "Y");
			   				List customTimePeriodList = FastList.newInstance();
			   				Map resultCtx = FastMap.newInstance();
			   				try{
			   					resultCtx = dispatcher.runSync("findCustomTimePeriods", finYearContext);
			   					if(ServiceUtil.isError(resultCtx)){
			   						Debug.logError("Problem in fetching financial year ", module);
			   						return ServiceUtil.returnError("Problem in fetching financial year ");
			   					}
			   				}catch(GenericServiceException e){
			   					Debug.logError(e, module);
			   					return ServiceUtil.returnError(e.getMessage());
			   				}
			   				customTimePeriodList = (List)resultCtx.get("customTimePeriodList");
			   				String finYearId = "";
			   				if(UtilValidate.isNotEmpty(customTimePeriodList)){
			   					GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
			   					finYearId = (String)customTimePeriod.get("customTimePeriodId");
			   				}
			   				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",finYearId), false);
			   				Timestamp timePeriodStart=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			   				Timestamp timePeriodEnd=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			       			if(UtilValidate.isEmpty(orderHeaderSequenceTypeId)){
			       				orderHeaderSequenceTypeId="PO_SEQUENCE";
			       			}
			   				GenericValue orderHeaderSequence = delegator.makeValue("OrderHeaderSequence");
			   				orderHeaderSequence.put("orderHeaderSequenceTypeId",orderHeaderSequenceTypeId );
			   				orderHeaderSequence.put("orderId", orderId);
			   				orderHeaderSequence.put("finYearId", finYearId);
			   				//orderHeaderSequence.put("orderNo", orderId+"/"+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate"),"yyyy")+"-"+UtilDateTime.toDateString(customTimePeriod.getDate("thruDate"),"yy"));
							delegator.setNextSubSeqId(orderHeaderSequence, "sequenceId", 6, 1);
				            delegator.create(orderHeaderSequence);
				            String sequenceId = (String) orderHeaderSequence.get("sequenceId");
				            orderHeaderSequence.put("orderNo", sequenceId+"/"+UtilDateTime.toDateString(customTimePeriod.getDate("fromDate"),"yyyy")+"-"+UtilDateTime.toDateString(customTimePeriod.getDate("thruDate"),"yy"));
				            delegator.createOrStore(orderHeaderSequence);
				            result.put("sequenceId", sequenceId) ;
			       			//}
			       		}
			        }catch(Exception e){
			        	Debug.logError(e, e.toString(), module);
			        	return ServiceUtil.returnError(e.toString()+" Error While Creating OrderHeader Sequence for orderHeaderSequenceTypeId:"+orderHeaderSequenceTypeId+" orderId:"+orderId);
			        }
			        return result;
				}
}