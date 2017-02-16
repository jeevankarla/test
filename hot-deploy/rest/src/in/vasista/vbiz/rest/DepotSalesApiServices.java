package in.vasista.vbiz.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.party.PartyHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;

import javax.ws.rs.core.Response;


@Path("/nhdc")
public class DepotSalesApiServices {
	
	@Context
	HttpHeaders headers;
	@Context
	HttpServletRequest request;
	
	
	public static final String module = DepotSalesApiServices.class.getName();
	private static int decimals;
	private static int rounding;
    public static final String resource = "AccountingUiLabels";
    
    static {
        decimals = 2;// UtilNumber.getBigDecimalScale("order.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

        // set zero to the proper scale
        //if (decimals != -1) ZERO = ZERO.setScale(decimals);
    }
    
    /*static boolean hasFacilityAccess(DispatchContext dctx, Map<String, ? extends Object> context) {  
        Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	GenericValue party = (GenericValue) context.get("party");
        if (security.hasEntityPermission("MOB_SREP_DB", "_VIEW", userLogin)) {
            return true;
        } 		
        if (security.hasEntityPermission("MOB_RTLR_DB", "_VIEW", userLogin)) {
        	if (userLogin != null && userLogin.get("partyId") != null) {
        		String userLoginParty = (String)userLogin.get("partyId");
        		String ownerParty = (String)party.get("partyId");
        		if (userLoginParty.equals(ownerParty)) {
        			return true;
        		}
        	}
        }
    	return false;
    }*/

	void populateHitMap(Delegator delegator, String methodName, String userName) {
		Map<String, Object> hitMap = FastMap.newInstance();
		hitMap.put("delegator", delegator);
		hitMap.put("methodName", methodName);	 
		hitMap.put("userName", userName);	
		request.setAttribute("restHitMap", hitMap);
	}
	
	@GET
	@Path("/getWeaverDetails")        
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getWeaverDetails(
				@QueryParam("login.username") String username,@QueryParam("login.password") String password,
				@QueryParam("tenantId") String tenantId,
				@QueryParam("partyId") String partyId,
				@QueryParam("effectiveDate") String effectiveDate
				) { 
		
		Map<String, Object> result = FastMap.newInstance();
		if (username == null || password == null || tenantId == null) {
			Debug.logError("Problem reading http header(s): login.username or login.password or tenantId", module);        	
			//::TODO:: error handling
			return result;        
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getWeaverDetails", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		Map<String, Object> resp;
		try {
			resp = dispatcher.runSync("userLogin", paramMap);
			if (ServiceUtil.isError(resp)) {
				return resp;	       
			}
		} catch (GenericServiceException e) {
			Debug.logWarning("Authentication failed for " +username + " " +  e.getMessage(), module);
			return result;	  
		}

		if (ServiceUtil.isError(resp)) {
			Debug.logWarning("userLogin authentication service failed for " +username, module);
			return result;	       
		}

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			return result;	   
		} 
		
		
		if (UtilValidate.isEmpty(partyId)) {
			Debug.logError("Empty party Id", module);
			return ServiceUtil.returnError("Empty Empty party Id");	   
		}
		GenericValue party = null;
  		try{
  			party = delegator.findOne("Party",UtilMisc.toMap("partyId",partyId),false);
  		}catch(GenericEntityException e){
  			Debug.logWarning("Error fetching party " +partyId + " " +  e.getMessage(), module);
			return ServiceUtil.returnError("Error fetching party " + partyId);	   
  		}
        /*if (!hasFacilityAccess(ctx, UtilMisc.toMap("userLogin", userLogin, "party", party))) {
            Debug.logWarning("**** Security [" + (new Date()).toString() + "]: " + 
            		userLogin.get("userLoginId") + " attempt to access Weaver Details: " + partyId, module);
            return ServiceUtil.returnError("You do not have permission for this transaction.");        	
        }*/
		
		//Timestamp effectiveDate = (Timestamp)context.get("effectiveDate");
		String partyName = "";
		String partyType = "";
		String passBookNo = "";
		String issueDate = "";
		String isDepot = "NO";
		String DOA = "";
		BigDecimal totalLooms = BigDecimal.ZERO;
		
		Map inputMap = FastMap.newInstance();
		Map addressMap = FastMap.newInstance();
		inputMap.put("partyId", partyId);
		inputMap.put("userLogin", userLogin);
		try{
			addressMap  = dispatcher.runSync("getPartyPostalAddress", inputMap);
		} catch(Exception e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		partyName = PartyHelper.getPartyName(delegator, partyId, false);
		try{
			GenericValue partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "PSB_NUMER"), false);
			if(UtilValidate.isEmpty(partyIdentification)){
				Debug.logError("Not a valid party", module);
				//return ServiceUtil.returnError("Not a valid party");
			}
			passBookNo = partyIdentification.getString("idValue");
			issueDate = partyIdentification.getString("issueDate");
			
			issueDate = UtilDateTime.toDateString(UtilDateTime.toTimestamp(partyIdentification.getDate("issueDate")),"dd-MM-yyyy");
		}catch(GenericEntityException e){
			Debug.logError("Not a valid party", module);
			return ServiceUtil.returnError("Not a valid party");
		}
		
		try {
            List<GenericValue> facility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS , partyId), null, null, null, false);
            if(UtilValidate.isNotEmpty(facility)){
            	GenericValue facilityDetail = EntityUtil.getFirst(facility);
            	isDepot = "YES";
            	DOA = UtilDateTime.toDateString(facilityDetail.getTimestamp("openedDate"),"dd-MM-yyyy");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		
		try {
            List<GenericValue> partyClassification = delegator.findList("PartyClassification", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS , partyId), null, null, null, false);
            if(UtilValidate.isNotEmpty(partyClassification)){
            	GenericValue partyDetail = EntityUtil.getFirst(partyClassification);
            	GenericValue partyClassificationGroup = delegator.findOne("PartyClassificationGroup",UtilMisc.toMap("partyClassificationGroupId", partyDetail.getString("partyClassificationGroupId")), false);
            	if(UtilValidate.isNotEmpty(partyClassificationGroup)){
            		partyType = partyClassificationGroup.getString("description");
            	}
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		
		List<GenericValue> loomTypes = null;	
		try {
			loomTypes = delegator.findList("LoomType",null,null,null,null,false);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		List<GenericValue> partyLoomDetails = null;
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, effectiveDate));
			conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, effectiveDate), 
	  					EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)));
			Debug.log("conditionList===="+conditionList);
			EntityCondition partyLoomCond =  EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
			partyLoomDetails = delegator.findList("PartyLoom",partyLoomCond,null,null,null,false);
			Debug.log("partyLoomDetails=========="+partyLoomDetails);
		} catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
		
		Map resultCtx = FastMap.newInstance();
		Map productCategoryQuotasMap = FastMap.newInstance();
		Map usedQuotaMap = FastMap.newInstance();
		Map eligibleQuota = FastMap.newInstance();
		try {
			resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId,"effectiveDate",effectiveDate));
		} catch(Exception e){
			Debug.logError("Problem while getting Quota details with:"+partyId, module);
			return ServiceUtil.returnError("Problem while getting Quota details with partyId:"+partyId);
		}
		productCategoryQuotasMap = (Map)resultCtx.get("schemesMap");
		usedQuotaMap = (Map)resultCtx.get("usedQuotaMap");
		eligibleQuota = (Map)resultCtx.get("eligibleQuota");
		
		
		Map loomDetails = FastMap.newInstance();
		for(GenericValue eachLoomType:loomTypes){
			String loomTypeId = eachLoomType.getString("loomTypeId");
			String description = eachLoomType.getString("description");
			BigDecimal loomQty = BigDecimal.ZERO;
			BigDecimal loomQuota = BigDecimal.ZERO;
			BigDecimal avlQuota = BigDecimal.ZERO;
			BigDecimal usedQuota = BigDecimal.ZERO;
			List<GenericValue> filteredPartyLooms = EntityUtil.filterByCondition(partyLoomDetails,EntityCondition.makeCondition("loomTypeId",EntityOperator.EQUALS,loomTypeId));
			for(GenericValue eachPartyLoom:filteredPartyLooms){
				loomQty = eachPartyLoom.getBigDecimal("quantity");
			}
			totalLooms = totalLooms.add(loomQty);
			loomQuota = (BigDecimal) eligibleQuota.get(loomTypeId);
			avlQuota = (BigDecimal) productCategoryQuotasMap.get(loomTypeId);
			usedQuota = (BigDecimal) usedQuotaMap.get(loomTypeId);
			Map loomDetailMap = FastMap.newInstance();
			loomDetailMap.put("loomTypeId",loomTypeId);
			loomDetailMap.put("description",description);
			loomDetailMap.put("loomQty",loomQty.setScale(decimals, rounding).intValue());
			loomDetailMap.put("loomQuota",loomQuota.setScale(decimals, rounding).intValue());
			loomDetailMap.put("avlQuota",avlQuota.setScale(decimals, rounding).intValue());
			loomDetailMap.put("usedQuota",usedQuota.setScale(decimals, rounding).intValue());
			loomDetails.put(eachLoomType.getString("loomTypeId"),loomDetailMap);
		}
		
		Map customerBranch = FastMap.newInstance();
		try {
			customerBranch = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin, "partyId", partyId));
		} catch(Exception e){
			Debug.logError("Problem while getting Customer Branch details with:"+partyId, module);
			return ServiceUtil.returnError("Problem while getting Customer Branch details with partyId:"+partyId);
		}
		List<GenericValue> productStoreList = (List)customerBranch.get("productStoreList");
		List customerBranchList = FastList.newInstance();
		if(UtilValidate.isNotEmpty(productStoreList)){
			for(GenericValue eachProdStore:productStoreList){
				/*Map tempMap = FastMap.newInstance();
				tempMap.put("productStoreId",eachProdStore.getString("productStoreId"));
				tempMap.put("storeName",eachProdStore.getString("storeName"));
				tempMap.put("companyName",eachProdStore.getString("companyName"));
				tempMap.put("title",eachProdStore.getString("title"));
				tempMap.put("payToPartyId",eachProdStore.getString("payToPartyId"));
				customerBranchMap.put(eachProdStore.getString("productStoreId"),tempMap);*/
				customerBranchList.add(eachProdStore.getString("productStoreId"));
			}
		}
		
		Map resultMap = FastMap.newInstance();
		resultMap.put("partyId",partyId);
		resultMap.put("partyName",partyName);
		resultMap.put("addressMap",addressMap);
		resultMap.put("partyType",partyType);
		resultMap.put("passBookNo",passBookNo);
		resultMap.put("issueDate",issueDate);
		resultMap.put("isDepot",isDepot);
		resultMap.put("DOA",DOA);
		resultMap.put("loomDetails",loomDetails);
		resultMap.put("totalLooms",totalLooms.setScale(decimals, rounding).intValue());
		resultMap.put("customerBranchList",customerBranchList);
		result.put("weaverDetails",resultMap);
		return result;
    }
}