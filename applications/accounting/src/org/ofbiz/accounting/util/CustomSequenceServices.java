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
}