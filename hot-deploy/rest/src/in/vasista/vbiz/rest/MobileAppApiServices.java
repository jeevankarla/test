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

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.Map.Entry;

import org.ofbiz.entity.util.EntityListIterator;

import javax.ws.rs.core.Response;

import in.vasista.vbiz.rest.util.JwtUtil;


@Path("/nhdc")
public class MobileAppApiServices {
	
	@Context
	HttpHeaders headers;
	@Context
	HttpServletRequest request;
	public static final String module = MobileAppApiServices.class.getName();

	void populateHitMap(Delegator delegator, String methodName, String userName) {
		Map<String, Object> hitMap = FastMap.newInstance();
		hitMap.put("delegator", delegator);
		hitMap.put("methodName", methodName);	 
		hitMap.put("userName", userName);	
		request.setAttribute("restHitMap", hitMap);
	}
	
	// If token is present, it just returns the username.  If not, the credentials
	// are authenticated and username is returned
	String authenticate(LocalDispatcher dispatcher, 
		Map<String, Object> paramMap) {
	
		String usernameFromToken = (String)request.getAttribute("usernameFromToken");
		if (usernameFromToken != null) {
			return usernameFromToken;
		}
		
		// no token present, do authentication
		String username = null;
		Map<String, Object> resp;
		try {
			resp = dispatcher.runSync("userLogin", paramMap);
		if (ServiceUtil.isError(resp)) {
			Debug.logWarning("userLogin authentication service failed for " +username, module);				
				return null;
			}
		} catch (GenericServiceException e) {
			Debug.logWarning("Authentication failed for " +username + " " +  e.getMessage(), module);
			return null;
		}	
		return (String)paramMap.get("login.username");
	}
	
	
	@POST
	@Path("/getAuthToken")        
	@Produces(MediaType.APPLICATION_JSON)
	public  Response getAuthToken(
			@FormParam("login.username") String username,
			@FormParam("login.password") String password,
			@FormParam("tenantId") String tenantId,
			@Context HttpServletResponse response) { 
		
		Map<String, Object> result = FastMap.newInstance();
		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getAuthToken", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;
		
		String token = JwtUtil.generateToken(username);
		if (token == null) {
			Debug.logError("Unable to generate token for " +username, module);	
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		result.put("token", token);
		return Response.ok(result).build();

	}
	
	
	@POST
	@Path("/getWeaverDetails")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWeaverDetails(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("effectiveDate") Timestamp effectiveDate) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getWeaverDetails", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("partyId", partyId);
		inputMap.put("effectiveDate", effectiveDate);
		inputMap.put("userLogin", userLogin);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getWeaverDetails", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Weaver Details "+resultResult );
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Weaver Details");
			result = ServiceUtil.returnError("Error getting Weaver Details " );
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/getWeaverIndents")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWeaverIndents(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("orderId") String orderId,
				@FormParam("partyIdFrom") String partyIdFrom,
				@FormParam("partyId") String partyId,
				@FormParam("branchId") String branchId,
				@FormParam("productStoreId") String productStoreId,
				@FormParam("tallyRefNO") String tallyRefNO,
				@FormParam("orderNo") String orderNo,
				@FormParam("estimatedDeliveryDate") Timestamp estimatedDeliveryDate,
				@FormParam("estimatedDeliveryThruDate") Timestamp estimatedDeliveryThruDate,
				@FormParam("statusId") String statusId,
				@FormParam("purposeTypeId") String purposeTypeId,
				@FormParam("indentDateSort") String indentDateSort
				) { 
        
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getWeaverIndents", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin: "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		
		Map<String, Object> inputMap = FastMap.newInstance();
		
		inputMap.put("userLogin", userLogin);
		inputMap.put("orderId", orderId);
		inputMap.put("partyIdFrom", partyIdFrom);
		inputMap.put("partyId", partyId);
		inputMap.put("branchId", branchId);
		inputMap.put("productStoreId", productStoreId);
		inputMap.put("tallyRefNO", tallyRefNO);
		inputMap.put("orderNo", orderNo);
		inputMap.put("estimatedDeliveryDate", estimatedDeliveryDate);
		inputMap.put("estimatedDeliveryThruDate", estimatedDeliveryThruDate);
		inputMap.put("indentDateSort", indentDateSort);
		inputMap.put("statusId", statusId);
		inputMap.put("purposeTypeId", purposeTypeId);
		
		
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getWeaverIndents", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Indents ");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Indents");
			result = ServiceUtil.returnError("Error getting Indents ");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	@POST
	@Path("/getWeaverPayments")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWeaverPayments(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("paymentId") String paymentId
				) { 
	
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getWeaverIndents", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin :" +username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		inputMap.put("paymentId", paymentId);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getWeaverPayments", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Payments");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Payments");
			result = ServiceUtil.returnError("Error getting Payments");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
		
	}
	
	
	@POST
	@Path("/getProducts")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProducts(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("productId") String productId,
				@FormParam("primaryProductCategoryId") String primaryProductCategoryId,
				@FormParam("productName") String productName,
				@FormParam("salesDate") Timestamp salesDate
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getProducts", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("productId", productId);
		inputMap.put("primaryProductCategoryId", primaryProductCategoryId);
		inputMap.put("productName", productName);
		inputMap.put("salesDate", salesDate);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getProducts", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Products");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Products");
			result = ServiceUtil.returnError("Error getting Products");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/getTransporters")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransporters(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getTransporters", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getTransporters", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Transporters");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Transporters");
			result = ServiceUtil.returnError("Error getting Transporters");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/getSuppliers")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSuppliers(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("partyTypeId") String partyTypeId,
				@FormParam("roleTypeId") String roleTypeId,
				@FormParam("groupName") String groupName
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getSuppliers", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		inputMap.put("partyTypeId", partyTypeId);
		inputMap.put("roleTypeId", roleTypeId);
		inputMap.put("groupName", groupName);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getSuppliers", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Suppliers");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Suppliers");
			result = ServiceUtil.returnError("Error getting Suppliers");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/cancelIndent")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelIndent(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("orderId") String orderId,
				@FormParam("salesChannelEnumId") String salesChannelEnumId
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "cancelIndent", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		inputMap.put("orderId", orderId);
		inputMap.put("salesChannelEnumId", salesChannelEnumId);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("cancelIndent", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error occured while cancel Indent");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error occured while cancel Indent");
			result = ServiceUtil.returnError("Error occured while cancel Indent");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/getDepotStock")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepotStock(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId
				) {
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getDepotStock", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getDepotStock", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error while getting depot stock");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error while getting depot stock");
			result = ServiceUtil.returnError("Error while getting depot stock");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
	}
	
	
	@POST
	@Path("/getIndentShipments")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIndentShipments(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("orderId") String orderId
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getIndentShipments", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("orderId", orderId);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getIndentShipments", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error while getting shipments");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error while getting shipments");
			result = ServiceUtil.returnError("Error while getting shipments");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	
	@POST
	@Path("/createBranchSalesIndent")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response createBranchSalesIndent(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("supplierPartyId") String supplierPartyId,
				@FormParam("effectiveDate") Timestamp effectiveDate,
				@FormParam("indentItems") JSONArray indentItems,
				
				@FormParam("productStoreId") String productStoreId,
				@FormParam("referenceNo") String referenceNo,
				@FormParam("tallyReferenceNo") String tallyReferenceNo,
				@FormParam("contactMechId") String contactMechId,
				
				@FormParam("newContactMechId") String newContactMechId,
				@FormParam("transporterId") String transporterId,
				@FormParam("manualQuota") String manualQuota,
				@FormParam("cfcId") String cfcId,
				
				@FormParam("orderTaxType") String orderTaxType,
				@FormParam("schemeCategory") String schemeCategory,
				@FormParam("billingType") String billingType,
				@FormParam("orderId") String orderId,
				
				@FormParam("partyGeoId") String partyGeoId,
				@FormParam("PONumber") String PONumber,
				@FormParam("orderMessage") String orderMessage,
				@FormParam("salesChannel") String salesChannel
				) {
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "createBranchSalesIndent", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		
		inputMap.put("partyId", partyId);
		inputMap.put("supplierPartyId", supplierPartyId);
		inputMap.put("indentItems", indentItems);
		inputMap.put("effectiveDate", effectiveDate);
		
		inputMap.put("productStoreId", productStoreId);
		inputMap.put("referenceNo", referenceNo);
		inputMap.put("tallyReferenceNo", tallyReferenceNo);
		inputMap.put("contactMechId", contactMechId);
		
		inputMap.put("newContactMechId", newContactMechId);
		inputMap.put("transporterId", transporterId);
		inputMap.put("manualQuota", manualQuota);
		inputMap.put("cfcId", cfcId);
		
		inputMap.put("orderTaxType", orderTaxType);
		inputMap.put("schemeCategory", schemeCategory);
		inputMap.put("billingType", billingType);
		inputMap.put("orderId", orderId);
		
		inputMap.put("partyGeoId", partyGeoId);
		inputMap.put("PONumber", PONumber);
		inputMap.put("orderMessage", orderMessage);
		inputMap.put("salesChannel", salesChannel);
		
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("createBranchSalesIndent", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error while Creating indent");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error while Creating indent");
			result = ServiceUtil.returnError("Error while Creating indent");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
	}
	
	@POST
	@Path("/updatePassword") 
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public  Response updatePassword(
			@FormParam("login.username") String username,@FormParam("login.password") String password,
			@FormParam("tenantId") String tenantId,@FormParam("newPassword") String newPassword,@FormParam("newPasswordVerify") String newPasswordVerify,@FormParam("passwordHint") String passwordHint) { 

		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "updatePassword", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}      

		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLoginId", username);
		inputMap.put("currentPassword", password);
		inputMap.put("newPassword", newPassword);
		inputMap.put("newPasswordVerify", newPasswordVerify);
		inputMap.put("passwordHint", passwordHint);
		inputMap.put("userLogin", userLogin);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("updatePassword", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error Updating the password");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error Updating the password");
			result = ServiceUtil.returnError("Error Updating the password");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();

	}
	
	@POST
	@Path("/getMobilePermissions")        
	@Produces(MediaType.APPLICATION_JSON)
	public  Response getMobilePermissions(
			@FormParam("login.username") String username,@FormParam("login.password") String password,
			@FormParam("tenantId") String tenantId) { 

		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "getMobilePermissions", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;
		
		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}     
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("getMobilePermissions", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error getting Mobile Permission service");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error getting Mobile Permission service");
			result = ServiceUtil.returnError("Error getting Mobile Permission service");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();

	}
	
	@POST
	@Path("/makeWeaverPayment")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response makeWeaverPayment(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("paymentDate") String paymentDate,
				@FormParam("partyId") String partyId,
				@FormParam("amount") String amount,
				@FormParam("orderId") String orderId,
				@FormParam("transactionId") String transactionId
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "makeWeaverPayment", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("paymentDate", paymentDate);
		inputMap.put("partyId", partyId);
		inputMap.put("amount", amount);
		inputMap.put("orderId", orderId);
		inputMap.put("transactionId", transactionId);
		
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("makeWeaverPayment", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error while making payment");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error while making payment");
			result = ServiceUtil.returnError("Error while making payment");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
	@POST
	@Path("/createPaymentGatewayTrans")        
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPaymentGatewayTrans(
				@FormParam("login.username") String username,@FormParam("login.password") String password,
				@FormParam("tenantId") String tenantId,
				@FormParam("partyId") String partyId,
				@FormParam("orderId") String orderId,
				@FormParam("amount") String amount,
				@FormParam("mobileNumber") String mobileNumber,
				@FormParam("email") String email,
				@FormParam("paymentChannel") String paymentChannel,
				@FormParam("paymentMode") String paymentMode
				) { 
		
		Map<String, Object> result = FastMap.newInstance();

		if (tenantId == null) {
			Debug.logError("Problem reading tenantId", module);        	
			//::TODO:: error handling
			return Response.status(Response.Status.UNAUTHORIZED).entity("Problem reading tenantId").build();
		}
		String tenantDelegatorName =  "default#" + tenantId;
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator(tenantDelegatorName);
		LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(tenantDelegatorName,delegator);
		populateHitMap(delegator, "createPaymentGatewayTrans", username);

		Map<String, Object> paramMap = FastMap.newInstance();
		paramMap.put("login.username", username);
		paramMap.put("login.password", password);

		String usernameTemp = authenticate(dispatcher, paramMap);
		if (usernameTemp == null) {
			Debug.logWarning("Authentication failed for " +username, module);
			result = ServiceUtil.returnError("Authentication failed");
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		username = usernameTemp;

		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId",username), false);
		}catch(GenericEntityException e){
			Debug.logWarning("Error fetching userLogin " +username + " " +  e.getMessage(), module);
			result = ServiceUtil.returnError("Error fetching userLogin  "+username);
			return Response.status(Response.Status.UNAUTHORIZED).entity(result).build();
		}
		
		Map<String, Object> inputMap = FastMap.newInstance();
		inputMap.put("userLogin", userLogin);
		inputMap.put("partyId", partyId);
		inputMap.put("orderId", orderId);
		inputMap.put("amount", amount);
		inputMap.put("mobileNumber", mobileNumber);
		inputMap.put("email", email);
		inputMap.put("paymentChannel", paymentChannel);
		inputMap.put("paymentMode", paymentMode);
		Map<String, Object> resultResult;
		try {
			resultResult = dispatcher.runSync("createPaymentGatewayTrans", inputMap);
			if (ServiceUtil.isError(resultResult)) {
				result = ServiceUtil.returnError("Error while creating payment gateway trans");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
			}
		} catch (GenericServiceException e) {
			Debug.logError(e, "Error while creating payment gateway trans");
			result = ServiceUtil.returnError("Error while creating payment gateway trans");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
		}
		return Response.ok(resultResult).build();
    }
	
}