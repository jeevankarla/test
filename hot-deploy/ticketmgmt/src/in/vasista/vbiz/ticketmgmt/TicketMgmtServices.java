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
		Timestamp custRequestDate = UtilDateTime.getDayStart((Timestamp)context.get("custRequestDate"));
		String statusId = (String)context.get("statusId");
		String productId = (String)context.get("productId");
		String salesChannelEnumId = (String)context.get("salesChannelEnumId");
		String custRequestTypeId = (String)context.get("custRequestTypeId");
		//description is used to get the complaint
		String description = (String) context.get("description");
		String PADDR = (String) context.get("PADDR");
		String PPHONE = (String)context.get("PPHONE");
		String PNAME = (String)context.get("PNAME");
		String ACTION = (String)context.get("ACTION");
		Map<String,Object> inMap = FastMap.newInstance();
		inMap.put("custRequestDate",custRequestDate);
		inMap.put("statusId",statusId);
		inMap.put("custRequestTypeId",custRequestTypeId);
		inMap.put("description",description);
		inMap.put("salesChannelEnumId",salesChannelEnumId);
		inMap.put("userLogin",userLogin);
		Map<String,Object> custRequest = FastMap.newInstance();
		try{
			custRequest = dispatcher.runSync("createNewComplaint",inMap);
			if(ServiceUtil.isError(custRequest)){
					Debug.logError("Error while creating Complaint ::"+ServiceUtil.getErrorMessage(custRequest),module);
					return ServiceUtil.returnError("Error While creating Complaint ::");
			}
		}catch(GenericServiceException e){
			Debug.logError("Error While creating Complaint=====>"+e,module);
			resultMap = ServiceUtil.returnError("Error While creating Complaint");
			return resultMap;
		}
		
			String custRequestId = (String)custRequest.get("custRequestId");
			try{
				GenericValue attr = delegator.makeValue("CustRequestAttribute");
		        attr.set("custRequestId", custRequestId);
		        attr.set("attrName", "PADDR");
		        attr.set("attrValue", PADDR);
		        delegator.create(attr);
		        attr.set("attrName","PPHONE");
		        attr.set("attrValue",PPHONE);
		        delegator.create(attr);
		        attr.set("attrName","ACTION");
		        attr.set("attrValue",ACTION);
		        delegator.create(attr);
		        attr.set("attrName","PNAME");
		        attr.set("attrValue",PNAME);
		        delegator.create(attr);
		        
		        Map<String,Object> itemInMap = FastMap.newInstance();
		        itemInMap.put("custRequestId",custRequestId);
		        itemInMap.put("statusId",statusId);
		        itemInMap.put("userLogin",userLogin);
		        itemInMap.put("productId",productId);
		        resultMap = dispatcher.runSync("createCustRequestItem",itemInMap);
					        if (ServiceUtil.isError(resultMap)) {
					        	Debug.logError(ServiceUtil.getErrorMessage(resultMap), module);
					        	resultMap = ServiceUtil.returnError("ServiceError While creating Complaint Item ");
					            return resultMap;
					        }
				}catch(GenericEntityException e){
					Debug.logError("Error While creating Complaint Attributes=====>"+e,module);
					resultMap = ServiceUtil.returnError("Error While creating Complaint Attributes");
					return resultMap;
				}catch(GenericServiceException e){
					Debug.logError("Error While creating Complaint Item =====>"+e,module);
					resultMap = ServiceUtil.returnError("Error While creating Complaint Item");
					return resultMap;
				}
				resultMap = ServiceUtil.returnSuccess("complaint Successfully Registered with the complaint number===========>"+custRequestId);
				resultMap.put("custRequestId",custRequestId);
				//Sending sms by seca rule and setting the attribute values for SMS
				resultMap.put("contactNumberTo",PPHONE);
				resultMap.put("text","Sending Sms For Testing");
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
		//description is used to get the complaint
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
			        	resultMap = ServiceUtil.returnError("ServiceError While creating Complaint ItemStaus ");
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
		        	resultMap = ServiceUtil.returnError("Error:While Details updating for Complaint Number==>"+custRequestId);
		            return resultMap;
		        }
				resultMap = ServiceUtil.returnSuccess("Complaint Details updated Successfully for Complaint Number==>"+custRequestId);
			}
		}catch (GenericEntityException e) {
			Debug.logError("Error While updating Complaint===>"+e,module);
			resultMap = ServiceUtil.returnError("Error while updating Complaint ==>"+custRequestId);
		}catch (GenericServiceException e) {
			// TODO: handle exception
			Debug.logError("Error while updating Status of complaint"+e,module);
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
		}else{
			tempParentTypeIdList.add(complaintType);
		}
			
		Map<String, Object> result= FastMap.newInstance();
		List<GenericValue> complaintTypes = null;
		try{
			complaintTypes = delegator.findList("CustRequestType",EntityCondition.makeCondition("parentTypeId",EntityOperator.IN,tempParentTypeIdList),null,null,null,false);
		}catch (GenericEntityException e) {
			// TODO: handle exception
			Debug.logError("Error while getting  Complaint types ::"+e,module);
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
