package in.vasista.vbiz.depotsales;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.util.HashSet;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.accounting.util.formula.Evaluator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.TimeDuration;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastSet;

public class ReimbursementService {
	
				public static final String module = ReambursementService.class.getName();
				
			public static Map<String, Object> createDepotReimbursementBilling(DispatchContext dctx, Map<String, Object> context) throws Exception{
				//GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				Delegator delegator = dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> periodBillingMap = ServiceUtil.returnSuccess();	
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				String intOrgId = (String) context.get("orgPartyId");
				String schmeTimePeriodId= (String) context.get("schemeTimePeriodId");
				List<GenericValue> ownerPartyIdsList=null;
				GenericValue periodBilling =null;
			

				String geoId = (String) context.get("geoId");
				String periodBillingId = null;
				
				List<GenericValue> billingParty = FastList.newInstance();
				
				String billingTypeId = "DEPOT_BILLING";
				if(UtilValidate.isNotEmpty(context.get("billingTypeId"))){
					billingTypeId  = (String)context.get("billingTypeId");
				}
				List conditionList = FastList.newInstance();
		        List periodBillingList = FastList.newInstance();
		        
		        String partyId = (String) context.get("partyId");
		        if(UtilValidate.isEmpty(partyId)){
					partyId = "Company";
				}	        
		        
		       SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");	
		        Timestamp basicSalTimestamp = UtilDateTime.nowTimestamp();
		        //code to handle department wise pay bill

		        //get SchemeTimeperiod start
		        GenericValue schemeTimePeriod=null;
				try {
					schemeTimePeriod = delegator.findOne("SchemeTimePeriod",UtilMisc.toMap("schemeTimePeriodId", schmeTimePeriodId), false);
				} catch (GenericEntityException e1) {
					 TransactionUtil.rollback();
					Debug.logError(e1,"Error While Finding Schemetime Period");
					return ServiceUtil.returnError("Error While Finding Schemetime Period" + e1);
				}
				// Check if Period billing is already generated for the give RO for that time period
				
				try {
					List condList= FastList.newInstance();
				        condList.add(EntityCondition.makeCondition("schemeTimePeriodId", EntityOperator.EQUALS, schmeTimePeriodId));
				        condList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, intOrgId));
				        condList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, billingTypeId));

						List<GenericValue> periodBillingList1 = delegator.findList("PeriodBilling",EntityCondition.makeCondition(condList, EntityOperator.AND),null, null, null, false);
						if(UtilValidate.isNotEmpty(periodBillingList1)){
							periodBilling = EntityUtil.getFirst(periodBillingList1);
						}
						//add status also
					} catch (GenericEntityException e1) {
						 TransactionUtil.rollback();
						Debug.logError(e1,"Error While Finding PeriodBilling");
						return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
					}
				//end
				if(UtilValidate.isNotEmpty(periodBilling)){
					periodBillingId = (String) periodBilling.get("periodBillingId");	
					
					if(periodBilling.get("statusId")=="GENERATED"){	
					//periodBilling already exist.
				     return ServiceUtil.returnError("PeriodBilling already generated.");
					}
				}
				else{
			        // Create a period billing record
			        
			        GenericValue newEntity = delegator.makeValue("PeriodBilling");
			        newEntity.set("billingTypeId", billingTypeId);
			        newEntity.set("schemeTimePeriodId", schmeTimePeriodId);
			        newEntity.set("statusId", "IN_PROCESS");
			        newEntity.set("partyId", intOrgId);	
			        newEntity.set("createdByUserLogin", userLogin.get("userLoginId"));
			        newEntity.set("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			        newEntity.set("createdDate", UtilDateTime.nowTimestamp());
			        newEntity.set("lastModifiedDate", UtilDateTime.nowTimestamp());
				    try {     
				        delegator.createSetNextSeqId(newEntity);
						periodBillingId = (String) newEntity.get("periodBillingId");	
						periodBilling =delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", periodBillingId), false);
					
						
				    } catch (GenericEntityException e) {
						Debug.logError(e,"Failed To Create New Period_Billing", module);
						return ServiceUtil.returnError("Problems in service Parol Header");
					}
				}
					
					  // Get All Depots under that RO.
					EntityListIterator reambursementIdsList =null;
			        try {
			        List condList = FastList.newInstance();
			        Map<String, Object> resultMap = getOwnerPartyIdList(dctx,UtilMisc.<String, Object>toMap("intOrgId", intOrgId,"userLogin", userLogin));
			        ownerPartyIdsList =(List)resultMap.get("ownerPartyIdsList");
		        	
		        	Map<String,  Object> runSACOContext = UtilMisc.<String, Object>toMap("periodBillingId", periodBillingId,"userLogin", userLogin);
					runSACOContext.put("branchId", intOrgId);
					runSACOContext.put("schemeTimePeriod", schemeTimePeriod);
			        runSACOContext.put("ownerPartyIdsList", ownerPartyIdsList);
			        runSACOContext.put("periodBilling", periodBilling);
					generateInvoiceBilling(dctx,runSACOContext);

					periodBilling.set("statusId","GENERATED");
					periodBilling.store();
					for(int i=0;i< ownerPartyIdsList.size();i++){
					GenericValue ownerParty=ownerPartyIdsList.get(i);
					String facilityId=ownerParty.getString("facilityId");
					condList.clear();
					condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
					condList.add(EntityCondition.makeCondition("schemeTimePeriodId",EntityOperator.EQUALS,schmeTimePeriodId));
					EntityCondition condition=EntityCondition.makeCondition(condList,EntityOperator.AND);
		        	List<GenericValue> reambursementList = delegator.findList("DepotReimbursementReceipt", condition, null, null, null, false);
			        	for(int j=0;j<reambursementList.size();j++){
			        		GenericValue  reambursementReceipt=reambursementList.get(j);
			        		reambursementReceipt.set("statusId","GENERATED");
			        		reambursementReceipt.store();
			        	}
					
					}
		        	
			        } catch (Exception e) {
			        	periodBilling.set("statusId","GENERATION_FAIL");
						periodBilling.store();
						
					}
			        
					
					if(ServiceUtil.isError(periodBillingMap)){
       					Debug.logError("Problems in Depot reambursement Bill generation. ", module);
			  			return ServiceUtil.returnError("Problems in Depot reambursement Bill generation.");
       				}
		    	
					
			    return periodBillingMap;
		        // Generate invoices for the relavant depots and update periodBillingId in each of those invoices.

			}	 
			
			public static Map<String, Object> getOwnerPartyIdList(DispatchContext dctx, Map context) throws GenericEntityException {
				 GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				 LocalDispatcher dispatcher = dctx.getDispatcher();
				 String intOrgId = (String) context.get("intOrgId");
				 Map<String, Object> result = ServiceUtil.returnSuccess();
				 EntityListIterator reambursementIdsList =null;
				 List<GenericValue> ownerPartyIdsList=null;

			       try {
			        
			        List condList = FastList.newInstance();
			        DynamicViewEntity dynamicView =new DynamicViewEntity();
			        dynamicView.addMemberEntity("FACILITY", "Facility");
	                dynamicView.addMemberEntity("PRS", "PartyRelationship");
	                dynamicView.addAliasAll("FACILITY", null, null);
	                dynamicView.addAliasAll("PRS", null, null);
	                dynamicView.addViewLink("FACILITY","PRS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("ownerPartyId","partyIdTo"));
	                
	                
	                condList.add(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS, "DEPOT_SOCIETY"));
	                condList.add(EntityCondition.makeCondition("roleTypeIdFrom" ,EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	                condList.add(EntityCondition.makeCondition("roleTypeIdTo" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	                condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,intOrgId));
					EntityCondition condition1=EntityCondition.makeCondition(condList,EntityOperator.AND);
		        	reambursementIdsList = delegator.findListIteratorByCondition(dynamicView, condition1, null, null, null, null);
		        	ownerPartyIdsList = reambursementIdsList.getCompleteList();
					result.put("ownerPartyIdsList",ownerPartyIdsList);

				}catch(Exception e){
					Debug.logError(e, module);
					Debug.logError(e, "Error in getting owner Party Id sList", module);	 		  		  
			  		return ServiceUtil.returnError("Error in owner Party Ids");
				}
			        finally{
						reambursementIdsList.close();
					}
				return result;
			}		

			public static Map<String, Object> generateInvoiceBilling(DispatchContext dctx, Map<String, Object> context) throws Exception{
				GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				TimeZone timeZone = TimeZone.getDefault();
				Locale locale = Locale.getDefault();
				Map<String, Object> result = ServiceUtil.returnSuccess();	
				String periodBillingId = (String) context.get("periodBillingId");
				GenericValue schemeTimePeriod = (GenericValue) context.get("schemeTimePeriod"); 
				Timestamp fromDateTime=UtilDateTime.toTimestamp(schemeTimePeriod.getDate("fromDate"));
				Timestamp thruDateTime=UtilDateTime.toTimestamp(schemeTimePeriod.getDate("thruDate"));
				Timestamp monthBegin = UtilDateTime.getDayStart(fromDateTime, timeZone, locale);
				Timestamp monthEnd = UtilDateTime.getDayEnd(thruDateTime, timeZone, locale);
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				Timestamp invoiceDate = UtilDateTime.getDayStart(monthBegin);
				String partyId = (String) context.get("branchId");
				List<GenericValue> ownerPartyIdsList = (List) context.get("ownerPartyIdsList");
				GenericValue periodBilling =(GenericValue) context.get("periodBilling");

				for(int i=0;i< ownerPartyIdsList.size();i++){
					GenericValue ownerParty=ownerPartyIdsList.get(i);
					
					BigDecimal depotReimbursementReceiptAmount=BigDecimal.ZERO;
					BigDecimal depotInvoiceAmount=BigDecimal.ZERO;
					BigDecimal reimbursementEligibilityPercentage=new BigDecimal(2);
					String facilityId=ownerParty.getString("facilityId");
					String ownerPartyId=ownerParty.getString("ownerPartyId");
					
					//get Depot Receipt Invoice Amount
					 List condList = FastList.newInstance();
					 condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
					 condList.add(EntityCondition.makeCondition("schemeTimePeriodId",EntityOperator.EQUALS,schemeTimePeriod.getString("schemeTimePeriodId")));
					 try {
					 List<GenericValue> depotReimbursementReceiptList = delegator.findList("DepotReimbursementReceipt",EntityCondition.makeCondition(condList, EntityOperator.AND),null, null, null, false);		
					 if(UtilValidate.isNotEmpty(depotReimbursementReceiptList) && 0<depotReimbursementReceiptList.size()){
							for(int j=0;j< depotReimbursementReceiptList.size();j++){
								GenericValue depotReimbursementReceipt=depotReimbursementReceiptList.get(j);
								depotReimbursementReceiptAmount=depotReimbursementReceiptAmount.add(depotReimbursementReceipt.getBigDecimal("receiptAmount"));
							}
						}
						else{
							continue;
						}
					  }catch (GenericEntityException e1) {
							Debug.logError(e1,"Error While Finding PeriodBilling");
							return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
						}
					
					// end		
					
//get Period Invoice Amount
					 condList.clear();
					 condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,ownerPartyId));
					 condList.add(EntityCondition.makeCondition("invoiceTypeId",EntityOperator.EQUALS,"SALES_INVOICE"));
					 condList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.BETWEEN,UtilMisc.toList(UtilDateTime.toTimestamp(schemeTimePeriod.getDate("fromDate")),UtilDateTime.toTimestamp(schemeTimePeriod.getDate("thruDate")))));
					
					 try {
						 List<GenericValue> invoiceItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(condList, EntityOperator.AND),null, null, null, false);	
						 if(UtilValidate.isNotEmpty(invoiceItemList) && 0<invoiceItemList.size()){
								for(int j=0;j< invoiceItemList.size();j++){
									GenericValue invoiceItem=invoiceItemList.get(j);
									depotInvoiceAmount=depotReimbursementReceiptAmount.add(invoiceItem.getBigDecimal("quantity").multiply(invoiceItem.getBigDecimal("amount")));
								
								}
							}
							else{
								continue;
							}
						  }catch (GenericEntityException e1) {
								Debug.logError(e1,"Error While Finding PeriodBilling");
								return ServiceUtil.returnError("Error While Finding PeriodBilling" + e1);
							}
					//end
					 BigDecimal  invoiceEligablityAmount=(depotInvoiceAmount.multiply(reimbursementEligibilityPercentage)).divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);
					 BigDecimal  finaEligablityAmount=depotReimbursementReceiptAmount.compareTo(invoiceEligablityAmount)>0?invoiceEligablityAmount:depotReimbursementReceiptAmount;
					 Map<String, Object> createInvoiceContext = FastMap.newInstance();
					 createInvoiceContext.put("partyId", partyId);
					 createInvoiceContext.put("partyIdFrom", ownerParty.getString("ownerPartyId"));
					 createInvoiceContext.put("invoiceDate", invoiceDate);
		   	  		 createInvoiceContext.put("facilityId", ownerParty.getString("facilityId"));
		   	  		 createInvoiceContext.put("invoiceTypeId", "PURCHASE_INVOICE");
		   	  		 createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
		   	  		 createInvoiceContext.put("userLogin", userLogin);
		   	  		 createInvoiceContext.put("periodBillingId",periodBillingId);
		   	  		 createInvoiceContext.put("referenceNumber",periodBilling.getString("billingTypeId")+"_"+periodBillingId);
		   		     SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");	        
		   		     createInvoiceContext.put("description", "Depot Reimbursement [" + sd.format(UtilDateTime.toCalendar(monthBegin).getTime()) 
		   		        		+ " - " + sd.format(UtilDateTime.toCalendar(monthEnd).getTime()) + "]");	

		   	  		 String invoiceId = null;
		   	  	     Map<String, Object> serviceResults;	
		   	  		 try {
		   	  			 serviceResults = dispatcher.runSync("createInvoice",createInvoiceContext);	
		   	  			 if (ServiceUtil.isError(serviceResults)) {
		   	  				 return ServiceUtil.returnError("There was an error while creating Invoice"+ ServiceUtil.getErrorMessage(serviceResults));
		   	  			 }
		   	  			 invoiceId = (String) serviceResults.get("invoiceId");
		   	  		 } catch (GenericServiceException e) {
		   	  			 Debug.logError(e, module);
		   	  			 return ServiceUtil.returnError("Unable to create payroll Invoice record");	
		   	  		 }
		     			
		   	  		 GenericValue invoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
		   	  		
		   	  	 Map inputItemCtx = UtilMisc.toMap("userLogin",userLogin);
	   	  		 inputItemCtx.put("amount",finaEligablityAmount);
	   	  		 inputItemCtx.put("invoiceId", invoiceId);
	   	  		 inputItemCtx.put("invoiceItemTypeId", "PAYROL_DD_GH_DED");
	   	  		 
	   	  		 try {
	   	  			 serviceResults = dispatcher.runSync("createInvoiceItem", inputItemCtx);
	   	  			 if (ServiceUtil.isError(serviceResults)) {
	   	  				 return ServiceUtil.returnError("Unable to create Invoice Item");
	   	  			 }
	   	  		 } catch (GenericServiceException e) {
	   	  			 Debug.logError(e, e.toString(), module);
	   	  			 return ServiceUtil.returnError(e.toString());
	   	  		 }
	   	  		 
	   	  		 try {
	   	  			 serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId",	"INVOICE_APPROVED", "userLogin", userLogin));
	   	  			 if (ServiceUtil.isError(serviceResults)) {
	   	  				 return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
	   	  			 }
	   	  			 serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId","INVOICE_READY", "userLogin", userLogin));
	   	  			 if (ServiceUtil.isError(serviceResults)) {
	   	  				 return ServiceUtil.returnError("Unable to set Invoice Status",null, null, serviceResults);
	   	  			 }
	   	  		 } catch (GenericServiceException e) {
	   	  			 Debug.logError(e, e.toString(), module);
	   	  			 return ServiceUtil.returnError(e.toString());
	   	  		 }
				}

				return result;
			}
			
			
			
			
			public static Map<String ,Object> cancelDepotReimbursementBilling(DispatchContext dctx, Map<String, ? extends Object> context){
				 Delegator delegator = dctx.getDelegator();
			     LocalDispatcher dispatcher = dctx.getDispatcher();       
			     GenericValue userLogin = (GenericValue) context.get("userLogin");
			     Map<String, Object> result = ServiceUtil.returnSuccess();
			     String schemeTimePeriodId = (String)context.get("schemeTimePeriodId");
			     String periodBillingId = (String)context.get("periodBillingId");	  
			     List conditionList = FastList.newInstance();
			     try{
			   	 GenericValue customTimePeriod = delegator.findOne("SchemeTimePeriod", UtilMisc.toMap("schemeTimePeriodId", schemeTimePeriodId), false);
			   	 Timestamp fromDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
			   	 Timestamp thruDate = UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
			   	 Timestamp endThruDate = UtilDateTime.getDayEnd(thruDate);
			   	 Timestamp dayBeginFromDate = UtilDateTime.getDayStart(fromDate);
			   	 if(!periodBillingId.equals("allInstitutions")){
			   		 conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, periodBillingId));
			   	 }
			   	 conditionList.add(EntityCondition.makeCondition("schemeTimePeriodId", EntityOperator.EQUALS, schemeTimePeriodId));
			   	 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			   	 conditionList.add(EntityCondition.makeCondition("billingTypeId", EntityOperator.EQUALS, "DEPOT_BILLING"));
			 		 EntityCondition checkExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			 		 List<GenericValue> periodBillingList = delegator.findList("PeriodBilling", checkExpr, null, null, null, false);
			 		 if(UtilValidate.isEmpty(periodBillingList)){
			 			 Debug.logError("No valid period billing exists to cancel for the billingId "+periodBillingId, module);
			             return ServiceUtil.returnError("No valid period billing exists to cancel for the billingId "+periodBillingId);
			 		 }
			   	 
			 		 List<String> partyIdsList = EntityUtil.getFieldListFromEntityList(periodBillingList, "partyId", true);
			 		 List cancelInvoiceIdsList = FastList.newInstance();
			 		 for(String ptyId: partyIdsList){
			 			 List<GenericValue> periodBillList = EntityUtil.filterByCondition(periodBillingList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ptyId));
			 			 String billingId = ((GenericValue)EntityUtil.getFirst(periodBillList)).getString("periodBillingId");
			 			 String invoiceReferenceNum = "DEPOT_BILLING_"+billingId;
			 			 
			 			 conditionList.clear();
			 			 conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ptyId));
			   		 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			   		 conditionList.add(EntityCondition.makeCondition("periodBillingId", EntityOperator.EQUALS, billingId));
			   		 //conditionList.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS, invoiceReferenceNum));
			   		 EntityCondition invCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			   		 
			   		 List<GenericValue> invoice = delegator.findList("Invoice", invCond, UtilMisc.toSet("invoiceId"), null, null, false);
			   		 if(UtilValidate.isNotEmpty(invoice)){
			   			 cancelInvoiceIdsList = EntityUtil.getFieldListFromEntityList(invoice, "invoiceId", true);
			   		 }
			   		 GenericValue periodBilling = delegator.findOne("PeriodBilling", UtilMisc.toMap("periodBillingId", billingId), false);
			   		 periodBilling.set("statusId", "COM_CANCELLED");
			   		 periodBilling.store();
			   		 
			   		 Map<String, Object> resultMap = getOwnerPartyIdList(dctx,UtilMisc.<String, Object>toMap("intOrgId", ptyId,"userLogin", userLogin));
			   		List<GenericValue> ownerPartyIdsList =(List)resultMap.get("ownerPartyIdsList");
			   		for(int i=0;i< ownerPartyIdsList.size();i++){
						GenericValue ownerParty=ownerPartyIdsList.get(i);
						String facilityId=ownerParty.getString("facilityId");
			   		 
			 		 conditionList.clear();
			 		 conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
			 		 conditionList.add(EntityCondition.makeCondition("schemeTimePeriodId",EntityOperator.EQUALS,schemeTimePeriodId));
					 EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				     List<GenericValue> reambursementList = delegator.findList("DepotReimbursementReceipt", condition, null, null, null, false);
					        	for(int j=0;j< reambursementList.size();j++){
					        		GenericValue  reambursementReceipt=reambursementList.get(j);
					        		reambursementReceipt.set("statusId","APPLYED");
					        		reambursementReceipt.store();
					        	}
			   			}
			 		 }
			 		 Map resultCtx = dispatcher.runSync("massChangeInvoiceStatus", UtilMisc.toMap("invoiceIds", cancelInvoiceIdsList, "statusId","INVOICE_CANCELLED","userLogin", userLogin));
		      		 
			 		 if (ServiceUtil.isError(resultCtx)) {
			 			 Debug.logError("There was an error while Cancelling  the Invoices: " + ServiceUtil.getErrorMessage(result), module);	              
			 			 return ServiceUtil.returnError("There was an error while Cancelling  the Invoices: ");   			 
			 		 }
			 		 
			 		 
			   	 
			     }catch (Exception e) {
					 Debug.logError(e, "Error cancelling Invoices for period billing", module);		 
					 return ServiceUtil.returnError("Error cancelling Invoices for period billing");			 
				 }
			     
			     
			     result = ServiceUtil.returnSuccess("Billing cancelled successfully !!");
			     return result;
		   	}
			
}//end of class
