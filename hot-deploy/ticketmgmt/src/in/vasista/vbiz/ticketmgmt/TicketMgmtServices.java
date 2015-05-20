package in.vasista.vbiz.ticketmgmt;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

public class TicketMgmtServices {
	
	public static final String module = TicketMgmtServices.class.getName();
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String,Object> createComplaint(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String dateStr = (String)context.get("custRequestDate");
        Timestamp custRequestDate=null;
        if (UtilValidate.isNotEmpty(dateStr)) {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        	try {
        		custRequestDate = new java.sql.Timestamp(sdf.parse(dateStr)
        	.getTime());
        	} catch (ParseException e) {
        	Debug.logError(e, "Cannot parse date string: " + dateStr,
        	module);
        	// effectiveDate = UtilDateTime.nowTimestamp();
        	} catch (NullPointerException e) {
        	Debug.logError(e, "Cannot parse date string: " + dateStr,
        	module);
        	}
        }
		String statusId = "OPEN_NEW";
		String productId = (String)context.get("productId");
		String salesChannelEnumId = (String)context.get("salesChannelEnumId");
		String custRequestTypeId = (String)context.get("custRequestTypeId");
		//description is used to get the Ticket
		String description = (String) context.get("description");
		String PADDR = (String) context.get("PADDR");
		String PPHONE = (String)context.get("PPHONE");
		String PNAME = (String)context.get("PNAME");
		String ACTION = (String)context.get("ACTION");
		
		String CATEGORY_ID = (String)context.get("categoryId");
		String PRODUCT_CATEGORY_ID = (String)context.get("productCategoryId");
		String severity = (String)context.get("severity");
		String ENVIRONMENT = (String)context.get("environment");
		String SUBJECT = (String)context.get("subject");
		String PROJECT = (String)context.get("project");
		String EMAIL = (String)context.get("emailAddress");
		String GROUP_CLIENT = (String)context.get("groupClient");
		String ASSET_MAPPING = (String)context.get("assetMapping");
		String SLA = (String)context.get("SLA");
		String REMARKS = (String)context.get("remarks");


		Map<String,Object> inMap = FastMap.newInstance();
		inMap.put("custRequestDate",custRequestDate);
		inMap.put("statusId",statusId);
		inMap.put("custRequestTypeId",custRequestTypeId);
		inMap.put("salesChannelEnumId",salesChannelEnumId);
		inMap.put("severity",severity);
		inMap.put("userLogin",userLogin);
		inMap.put("description",description);
		Map<String,Object> custRequest = FastMap.newInstance();
		try{
			custRequest = dispatcher.runSync("createNewComplaint",inMap);
			if(ServiceUtil.isError(custRequest)){
					Debug.logError("Error while creating Ticket ::"+ServiceUtil.getErrorMessage(custRequest),module);
					return ServiceUtil.returnError("Error While creating Ticket ::");
			}
		}catch(GenericServiceException e){
			Debug.logError("Error While creating Ticket=====>"+e,module);
			resultMap = ServiceUtil.returnError("Error While creating Ticket");
			return resultMap;
		}
		
			String custRequestId = (String)custRequest.get("custRequestId");
			try{
				GenericValue attr = delegator.makeValue("CustRequestAttribute");
		        attr.set("custRequestId", custRequestId);
		       
		        attr.set("attrName","CATEGORY_ID");
		        attr.set("attrValue",CATEGORY_ID);
		        delegator.create(attr);
		        attr.set("attrName","PRODUCT_CATEGORY_ID");
		        attr.set("attrValue",PRODUCT_CATEGORY_ID);
		        delegator.create(attr);
		        
		     
		        
		        attr.set("attrName","EMAIL");
		        attr.set("attrValue",EMAIL);
		        delegator.create(attr);
		        attr.set("attrName","ENVIRONMENT");
		        attr.set("attrValue",ENVIRONMENT);
		        delegator.create(attr);
		        attr.set("attrName","SUBJECT");
		        attr.set("attrValue",SUBJECT);
		        delegator.create(attr);
		        attr.set("attrName","PROJECT");
		        attr.set("attrValue",PROJECT);
		        delegator.create(attr);
		        attr.set("attrName","GROUP_CLIENT");
		        attr.set("attrValue",GROUP_CLIENT);
		        delegator.create(attr);
		    
		        attr.set("attrName","ASSET_MAPPING");
		        attr.set("attrValue",ASSET_MAPPING);
		        delegator.create(attr);
		        attr.set("attrName","SLA");
		        attr.set("attrValue",SLA);
		        delegator.create(attr);
		        attr.set("attrName","REMARKS");
		        attr.set("attrValue",REMARKS);
		        delegator.create(attr);
		        
		        Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("statusId",statusId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        resultMap = dispatcher.runSync("createCustRequestItem",itemInMap);
		        if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		        	resultMap = ServiceUtil.returnError("ServiceError While creating Ticket Item ");
		            return resultMap;
		        }
				}catch(GenericEntityException e){
					Debug.logError("Error While creating Ticket Attributes=====>"+e,module);
					resultMap = ServiceUtil.returnError("Error While creating Ticket Attributes");
					return resultMap;
				}catch(GenericServiceException e){
					Debug.logError("Error While creating Ticket Item =====>"+e,module);
					resultMap = ServiceUtil.returnError("Error While creating Ticket Item");
					return resultMap;
				}
				resultMap = ServiceUtil.returnSuccess("Ticket successfully registered with the Ticket number===========>"+custRequestId);
				resultMap.put("custRequestId",custRequestId);
				//Sending sms by seca rule and setting the attribute values for SMS
				//resultMap.put("contactNumberTo",PPHONE);
				//resultMap.put("text","Sending Sms For Testing");
			return resultMap;
	}// End of the service
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return 
	 */
	public static Map<String,Object> updateComplaint(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp custRequestDate = UtilDateTime.getDayStart((Timestamp)context.get("custRequestDate"));
        String custRequestId = (String)context.get("custRequestId");
        String statusId = (String)context.get("statusId");
		String custRequestTypeId = (String)context.get("custRequestTypeId");
		//description is used to get the Ticket
		String description = (String) context.get("description");
		String PADDR = (String) context.get("PADDR");
		String PPHONE = (String)context.get("PPHONE");
		String PNAME = (String)context.get("PNAME");
		String ACTION = (String)context.get("ACTION");
		//String ACTION = (String)context.get("productId");
		String productId = (String)context.get("productId");
		String custRequestItemSeqId = (String)context.get("custRequestItemSeqId");
		String salesChannelEnumId = (String)context.get("salesChannelEnumId");
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try{
			GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId",custRequestId),false);
			if(UtilValidate.isNotEmpty(custRequest)){	
				String prevStatusId = (String)custRequest.get("statusId");
				if(!statusId.equals(prevStatusId)){
					Map<String, Object> statusInMap = FastMap.newInstance();
					statusInMap.put("statusId",statusId);
					statusInMap.put("custRequestId",custRequestId);
					statusInMap.put("userLogin",userLogin);
					
					resultMap =dispatcher.runSync("createCustRequestStatus",statusInMap);
					if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			        	resultMap = ServiceUtil.returnError("ServiceError While creating Ticket ItemStaus ");
			            return resultMap;
			        }
				}
				custRequest.set("salesChannelEnumId",salesChannelEnumId);
				custRequest.set("description",description);
				custRequest.set("statusId",statusId);
				custRequest.set("custRequestTypeId",custRequestTypeId);
				custRequest.set("lastModifiedDate",nowTimestamp);
				custRequest.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
				custRequest.store();
				List<GenericValue> attributes = FastList.newInstance();
				List<GenericValue> custRequestAttributes = delegator.findList("CustRequestAttribute",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
				for(GenericValue attribute : custRequestAttributes){
					if("PADDR".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",PADDR);
					}else if("PPHONE".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",PPHONE);
					}else if("PNAME".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",PNAME);
					}else{
						attribute.put("attrValue",ACTION);
					} 
					attributes.add(attribute);
				}
				delegator.storeAll(attributes);
				GenericValue custRequestItem = null;
				custRequestItem = delegator.findOne("CustRequestItem", UtilMisc.toMap("custRequestId",custRequestId,"custRequestItemSeqId",custRequestItemSeqId),false);
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("custRequestItemSeqId",custRequestItemSeqId);
				itemInMap.put("statusId",statusId);
				itemInMap.put("userLogin",userLogin);
				itemInMap.put("productId",productId);
				if(UtilValidate.isNotEmpty(custRequestItem)){
					resultMap = dispatcher.runSync("updateCustRequestItem", itemInMap);
				}else{
					resultMap = dispatcher.runSync("createCustRequestItem", itemInMap);
				}
		         if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		        	resultMap = ServiceUtil.returnError("Error:While Details updating for Ticket Number==>"+custRequestId);
		            return resultMap;
		        }
				resultMap = ServiceUtil.returnSuccess("Ticket Details updated Successfully for Ticket Number==>"+custRequestId);
			}
		}catch (GenericEntityException e) {
			Debug.logError("Error While updating Ticket===>"+e,module);
			resultMap = ServiceUtil.returnError("Error while updating Ticket ==>"+custRequestId);
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while updating Status of Ticket"+e,module);
		}
		resultMap.put("custRequestId",custRequestId);
		return resultMap;
	}// End of the service
	
	public static Map<String,Object> updateTMSComplaint(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp custRequestDate = UtilDateTime.getDayStart((Timestamp)context.get("custRequestDate"));
        String custRequestId = (String)context.get("custRequestId");
		String custRequestTypeId = (String)context.get("custRequestTypeId");
		String productId = (String)context.get("productId");
		String severity = (String)context.get("severity");
		String categoryId = (String)context.get("categoryId");
		String productCategoryId = (String)context.get("productCategoryId");
		String assetMapping = (String)context.get("assetMapping");
		String environment = (String)context.get("environment");
		String project = (String)context.get("project");
		String subject = (String)context.get("subject");
		String remarks = (String)context.get("remarks");
		String sla = (String)context.get("sla");
		String email = (String)context.get("email");
		String groupClient = (String)context.get("groupClient");



		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try{
			GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId",custRequestId),false);
			if(UtilValidate.isNotEmpty(custRequest)){	
				custRequest.set("custRequestDate",custRequestDate);
				custRequest.set("severity",severity);
				custRequest.set("custRequestTypeId",custRequestTypeId);
				custRequest.set("lastModifiedDate",nowTimestamp);
				custRequest.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
				custRequest.store();
				List<GenericValue> attributes = FastList.newInstance();
				List<GenericValue> custRequestAttributes = delegator.findList("CustRequestAttribute",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
				for(GenericValue attribute : custRequestAttributes){
					if("CATEGORY_ID".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",categoryId);
					}else if("PRODUCT_CATEGORY_ID".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",productCategoryId);
					}else if("ASSET_MAPPING".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",assetMapping);
					}else if("ENVIRONMENT".equals((String)attribute.get("attrName"))){
						attribute.put("attrValue",environment);
					}else if("PROJECT".equals((String)attribute.get("attrName"))){
		                attribute.put("attrValue",project);
		            }else if("SUBJECT".equals((String)attribute.get("attrName"))){
		             	attribute.put("attrValue",subject);
			        }else if("REMARKS".equals((String)attribute.get("attrName"))){
			           	attribute.put("attrValue",remarks);
			        }else if("SLA".equals((String)attribute.get("attrName"))){
					    attribute.put("attrValue",sla);
			        }else if("EMAIL".equals((String)attribute.get("attrName"))){
					    attribute.put("attrValue",email);
					}else if("GROUP_CLIENT".equals((String)attribute.get("attrName"))){
					    attribute.put("attrValue",groupClient);
					} 
					attributes.add(attribute);
				}
				delegator.storeAll(attributes);
				
	  		    GenericValue custRequestItem = null;
  	  		    String custRequestItemSeqId = "";
				List<GenericValue>  custRequestItems = delegator.findList("CustRequestItem",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequestId),null,null,null,false);
				if(UtilValidate.isNotEmpty(custRequestItems)){	
			    custRequestItem = EntityUtil.getFirst(custRequestItems);
			    custRequestItemSeqId = custRequestItem.getString("custRequestItemSeqId");
				Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("custRequestItemSeqId",custRequestItemSeqId);
				itemInMap.put("productId",productId);
				itemInMap.put("userLogin",userLogin);
				if(UtilValidate.isNotEmpty(custRequestItem)){
					resultMap = dispatcher.runSync("updateCustRequestItem", itemInMap);
				}else{
					resultMap = dispatcher.runSync("createCustRequestItem", itemInMap);
				}
			  }
		         if (ServiceUtil.isError(resultMap)) {
		        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
		        	resultMap = ServiceUtil.returnError("Error:While Details updating for Ticket Number==>"+custRequestId);
		            return resultMap;
		        }
				resultMap = ServiceUtil.returnSuccess("Ticket Details updated Successfully for Ticket Number==>"+custRequestId);
			}
		}catch (GenericEntityException e) {
			Debug.logError("Error While updating Ticket===>"+e,module);
			resultMap = ServiceUtil.returnError("Error while updating Ticket ==>"+custRequestId);
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while updating Status of Ticket"+e,module);
		}
		resultMap.put("custRequestId",custRequestId);
		return resultMap;
	}// End of the service
	
	public static Map<String,Object> updateComplaintStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> resultMap = FastMap.newInstance();
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp fromDate = (Timestamp)context.get("fromDate");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        if(UtilValidate.isEmpty(fromDate)){
        	fromDate = nowTimestamp;
        }
        Timestamp thruDate = UtilDateTime.getDayEnd((Timestamp)context.get("thruDate"));
        String custRequestId = (String)context.get("custRequestId");
        String statusId = (String)context.get("statusId");
        String comments = (String)context.get("comments");
        String partyId = (String)context.get("partyId");
		
		try{
			if(!statusId.equals("CLOSED")){
				if(UtilValidate.isEmpty(partyId)){
					Debug.logError("PartyId is missing",module);
					return ServiceUtil.returnError("PartyId is missing!");
				}
			}
			GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId",custRequestId),false);
			if(UtilValidate.isNotEmpty(custRequest)){	
				String prevStatusId = (String)custRequest.get("statusId");
				if(!statusId.equals(prevStatusId)){
					Map<String, Object> statusInMap = FastMap.newInstance();
					statusInMap.put("statusId",statusId);
					statusInMap.put("custRequestId",custRequestId);
					statusInMap.put("statusDatetime",fromDate);
					statusInMap.put("comments",comments);
					statusInMap.put("partyId",partyId);
					statusInMap.put("changedByUserLogin",userLogin.getString("partyId"));
					statusInMap.put("userLogin",userLogin);
					resultMap =dispatcher.runSync("createCustRequestStatus",statusInMap);
					if (ServiceUtil.isError(resultMap)) {
			        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
			        	resultMap = ServiceUtil.returnError("ServiceError While creating Ticket ItemStaus ");
			            return resultMap;
			        }
				}
				custRequest.set("statusId",statusId);
				custRequest.set("lastModifiedDate",nowTimestamp);
				custRequest.set("lastModifiedByUserLogin",userLogin.get("userLoginId"));
				custRequest.store();
				String roleTypeId = "REQ_TAKER";
				if(UtilValidate.isNotEmpty(partyId)){
			        GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId), false);
					if(UtilValidate.isEmpty(partyRole)){
						Debug.logError("No party role found", module);
						resultMap = ServiceUtil.returnError("Error While creating Ticket, No party role found!");
				  		return resultMap;
					}
				}
				Map inputCtx = FastMap.newInstance();
				if(UtilValidate.isNotEmpty(partyId)){
					inputCtx.put("partyId", partyId);
				}else{
					inputCtx.put("partyId", userLogin.getString("partyId"));
				}
				inputCtx.put("custRequestId", custRequestId);
				inputCtx.put("roleTypeId",roleTypeId);
				inputCtx.put("userLogin", userLogin);
				Map resultCtx = dispatcher.runSync("createCustRequestParty", inputCtx);
				if (ServiceUtil.isError(resultCtx)) {
					Debug.logError("RequestItem set status failed for Request: " + custRequestId, module);
					return ServiceUtil.returnError("Error occuring while calling createCustRequestParty service:");
				}
				resultMap = ServiceUtil.returnSuccess("Ticket status changed successfully for Ticket Number==>"+custRequestId);
			}
		}catch (GenericEntityException e) {
			Debug.logError("Error While updating Ticket===>"+e,module);
			resultMap = ServiceUtil.returnError("Error while updating Ticket ==>"+custRequestId);
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while updating Status of Ticket"+e,module);
		}
		resultMap.put("custRequestId",custRequestId);
		return resultMap;
	}// End of the service
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return 
	 */
	
	public static Map<String,Object> getTicketMgmtComplaintTypes(DispatchContext dctx, Map<String, ? extends Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
		String complaintType = "CUSTOMER_COMP_TYPE";
        List<GenericValue> complaintsList = FastList.newInstance();
        List tempParentTypeIdList = FastList.newInstance();
        if(UtilValidate.isNotEmpty(context.get("complaintsList"))){ 
			complaintsList = (List)context.get("complaintsList");
			tempParentTypeIdList = EntityUtil.getFieldListFromEntityList(complaintsList, "custRequestTypeId", false);
		}/*else{
			tempParentTypeIdList.add(complaintType);
		}*/
			
		Map<String, Object> result= FastMap.newInstance();
		List<GenericValue> complaintTypes = null;
		try{
			if(UtilValidate.isNotEmpty(tempParentTypeIdList)){
				complaintTypes = delegator.findList("CustRequestType",EntityCondition.makeCondition("parentTypeId",EntityOperator.IN,tempParentTypeIdList),null,null,null,false);
			}else{
				complaintTypes = delegator.findList("CustRequestType",null,null,null,null,false);
			}
			
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError("Error while getting  Ticket types ::"+e,module);
		}
		if(UtilValidate.isNotEmpty(complaintTypes)){
			complaintsList = complaintTypes;
		    result = getTicketMgmtComplaintTypes(dctx,UtilMisc.toMap("complaintsList",complaintsList));
		}else{
			result.put("complaintsList", complaintsList);
		}
        return result;
	}//end of service
}
