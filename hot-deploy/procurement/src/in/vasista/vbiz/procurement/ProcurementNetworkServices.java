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
package in.vasista.vbiz.procurement;

/**
 * @author vadmin
 *
 */
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.math.BigDecimal;


public class ProcurementNetworkServices {

	 public static final String module = ProcurementNetworkServices.class.getName();
	 private static BigDecimal ZERO = BigDecimal.ZERO;
	    private static int decimals;
	    private static int rounding;
	    public static final String resource_error = "OrderErrorUiLabels";
	    static {
	        decimals = 3;//UtilNumber.getBigDecimalScale("order.decimals");
	        rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

	        // set zero to the proper scale
	        if (decimals != -1) ZERO = ZERO.setScale(decimals); 
	    }	
	    
	   public static List getSheds(Delegator delegator) {
	    	
	    	List shedsList = FastList.newInstance();
	    	try {
	    		shedsList = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SHED"), null, null, null, true);
	    		     
	    	}catch (GenericEntityException e) {
	            Debug.logError(e, module);	           
	        }
	        return shedsList;
	    }     
	    
	 // this method will return all the units belongs the given shedId or shed code
	 // if both shedcode and shedid given then shed id is takes the highest priority   
	  public static Map<String, Object> getShedUnitsByShed(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedCode = (String)context.get("shedCode");	    	
	    	String shedId = (String)context.get("shedId");
	    	List<String> units = FastList.newInstance();
	    	List<GenericValue> facilities = null;
	    	try {
	    		
	    		if(UtilValidate.isNotEmpty(shedCode)){
	    			facilities = delegator.findList("Facility", EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SHED"),EntityOperator.AND,EntityCondition.makeCondition("facilityCode", EntityOperator.EQUALS,shedCode) ), null, UtilMisc.toList("facilityId"), null, false);
	    			GenericValue shedDetail = EntityUtil.getFirst(facilities);
	    			if(UtilValidate.isEmpty(shedDetail)){
	    				Debug.logError("Invalid Shed code"+shedCode, module);
	    	            return ServiceUtil.returnError("Invalid Shed code"+shedCode);
	    			}
		    		shedId = shedDetail.getString("facilityId");
	    		}
	    		facilities = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS,shedId), null, UtilMisc.toList("facilityId"), null, false);
	            units = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("unitsList", units);
	        result.put("unitsDetailList", facilities);
	        return result;
	    }
	  
	  
	  //this methood will return all the sheds and there units  Map,Shed Id as key units as values
	  // [shedId : {unit1<GenericValue>,unit2<GenericValue>,....} ] 
	  
	  public static Map<String, Object> getShedUnits(DispatchContext ctx, Map<String, ? extends Object> context) {	  	  
		  	Delegator delegator = ctx.getDelegator();
		    List<String> sheds= FastList.newInstance();
	    	Map<String, Object> shedUnitsMap = FastMap.newInstance();
	    	Map<String, Object> result =  FastMap.newInstance();
	    	List<GenericValue> facilities = FastList.newInstance();
	    	try {
	    		facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "SHED"), null, UtilMisc.toList("facilityId"), null, false);
	    		sheds = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    		for(int i=0 ;i<sheds.size();i++){
	    			String shedId = sheds.get(i);
	    			result = getShedUnitsByShed(ctx ,UtilMisc.toMap("shedId", shedId));
	    			shedUnitsMap.put(shedId, result.get("unitsDetailList"));
	    			result.clear();
	    		}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	    	result.put("shedList", facilities);     
	        result.put("shedUnits", shedUnitsMap);
	        return result;
	    }
	    
	  public static Map<String, Object> getUnits(Delegator delegator) {	  	  
	    	List<String> units= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "UNIT"), null, UtilMisc.toList("facilityId"), null, false);
	    		units = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("unitsList", units);
	        return result;
	    }
	  public static Map<String, Object> getUnitRoutes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String unitId = (String)context.get("unitId");
	    	List<String> routes= FastList.newInstance();
	    	List<GenericValue> facilities = FastList.newInstance();
	    	try {
	    		facilities = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS,unitId), null, UtilMisc.toList("facilityId"), null, false);
	            routes = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("routesList", routes);
	        result.put("routesDetailList", facilities);
	        return result;
	    }

	  public static Map<String, Object> getRouteAgents(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String routeId = (String)context.get("routeId");
	    	List<String> agents= FastList.newInstance();
	    	List<GenericValue> facilities = FastList.newInstance();
	    	try {
	    		GenericValue routeDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeId), false);
	    		if(UtilValidate.isEmpty(routeDetail)){
	    			Debug.logError("Route not found with the Id==>"+routeId, module);
	           		return ServiceUtil.returnError("Route not found with the Id==>"+routeId);  
	        	}	    	
	    		if (!"PROC_ROUTE".equals(routeDetail.getString("facilityTypeId"))) {
	    			Debug.logError("Input not of type Route: routeId==>"+routeId, module);
	           		return ServiceUtil.returnError("Input not of type Route: routeId==>"+routeId); 	    			
	    		}
	    	    facilities = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, routeId), null, UtilMisc.toList("facilityId"), null, false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess(); 
	        agents = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", true);
	        result.put("agentsList", agents);
	        result.put("agentDetailsList", facilities);
	        return result;
	    }
	  
	  // This will  return facilityId based on given shed code and unit code
	  public static Map<String, Object> getFacilityByShedAndUnitCodes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedCode = (String)context.get("shedCode");
	    	String unitCode = (String)context.get("unitCode");
	    	List unitDetails= null;
	    	List units = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(shedCode)){
    			units = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedCode",shedCode))).get("unitsDetailList"); 
    		}    		 
	        Map<String, Object> result = ServiceUtil.returnSuccess(); 
	        unitDetails = (List)EntityUtil.filterByCondition(units, EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId" ,EntityOperator.EQUALS,"UNIT"),EntityOperator.AND, EntityCondition.makeCondition("facilityCode" ,EntityOperator.EQUALS,unitCode) ));
	        String facilityId = null;
	        facilityId = (String)((Map)unitDetails.get(0)).get("facilityId");
	        result.put("unitDetails", unitDetails);
	        result.put("facilityId", facilityId);	       
	        return result;
	    }
	  //This will  return agents(centers) belongs to  unit code

	  public static Map<String, Object> getUnitAgents(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedCode = (String)context.get("shedCode");
	    	String shedId = (String)context.get("shedId");
	    	String unitCode = (String)context.get("unitCode");
	    	String unitId = (String)context.get("unitId");
	    	List<GenericValue> agentsList = FastList.newInstance();
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	 List<EntityCondition> condList = FastList.newInstance();
	    	// if unitId is not null return agents here
	    	if(UtilValidate.isNotEmpty(unitId)){
	    		List routeList = (List)(getUnitRoutes(ctx,UtilMisc.toMap("unitId",unitId))).get("routesList");	    	
	    		try{
	    			condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN,routeList));	        		        	
		        	agentsList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
		        	result.put("agentsList", agentsList);
	    		}catch (GenericEntityException e) {
					// TODO: handle exception
	    			  Debug.logError(e, module);
	  	            return ServiceUtil.returnError(e.getMessage());
				}
	        	
		        return result;
	    	}	    	
	    	
	    	if(UtilValidate.isEmpty(shedCode) && UtilValidate.isEmpty(shedId)){
	    		Debug.logError("shed code or shed id is missing.=======>", module);	    		
	    		result = ServiceUtil.returnError("shed code or shed id is missing=======>");
	    		result.put("agentsList", agentsList);
           		return result;  
	    	}
	    	if(UtilValidate.isEmpty(unitCode)){
	    		Debug.logError("unit code or center code is missing. UnitCode=======>"+unitCode, module);	    		
	    		result = ServiceUtil.returnError("unit code or center code is missing. UnitCode=======>"+unitCode);
	    		result.put("agentsList", agentsList);
           		return result;  
	    	}
	    	try {
	    		List units = FastList.newInstance();
	    		if(UtilValidate.isNotEmpty(shedCode)){
	    			units = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedCode",shedCode))).get("unitsList"); 
	    		}else{
	    			 units = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",shedId))).get("unitsList"); 
	    		}
	    		
	    		 condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN, units));	   
	    		 condList.add(EntityCondition.makeCondition("facilityTypeId", "UNIT"));	    		 
	    		 condList.add(EntityCondition.makeCondition("facilityCode", unitCode));
	    		 List unitList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
	        	 GenericValue unitDetail =  EntityUtil.getFirst(unitList);
	        	 if(UtilValidate.isEmpty(unitDetail)){
	        		 Debug.logError("Unit  not found with the code==>"+unitCode, module);
	           		return ServiceUtil.returnError("Unit  not found with the code==>"+unitCode);  
	        	 }	        	
	    		List routeList = (List)(getUnitRoutes(ctx,UtilMisc.toMap("unitId",unitDetail.getString("facilityId")))).get("routesList");	    	
	    		condList.clear();
	        	condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN,routeList));	        		        	
	        	agentsList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	    		
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }	                
	        result.put("agentsList", agentsList);

	        return result;
	    }   

	  //This will  return all agents(centers) belonging to the input plant
	  public static Map<String, Object> getPlantAgents(DispatchContext ctx, Map<String, ? extends Object> context) {
		  Delegator delegator = ctx.getDelegator();
		  String plantId = (String)context.get("plantId");		  
		  List<String> agents= FastList.newInstance();
	    	try {	
	    		GenericValue plantDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",plantId), false);
	    		if(UtilValidate.isEmpty(plantDetail)){
	    			Debug.logError("Plant not found with the Id==>"+plantId, module);
	           		return ServiceUtil.returnError("Plant not found with the Id==>"+plantId);  
	        	}	    	
	    		if (!"PLANT".equals(plantDetail.getString("facilityTypeId"))) {
	    			Debug.logError("Input not of type Plant: plantId==>"+plantId, module);
	           		return ServiceUtil.returnError("Input not of type Plant: plantId==>"+plantId); 	    			
	    		}	    		
	    		List<GenericValue> sheds = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, plantId), null, UtilMisc.toList("facilityId"), null, false);
	    		for (int i = 0; i < sheds.size(); i++) {
	    			Map temp = getFacilityAgents(ctx, UtilMisc.toMap("facilityId", sheds.get(i).getString("facilityId")));
		    		List tempAgents = (List)temp.get("facilityIds");
	    			agents.addAll(tempAgents);
	    		}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	    	
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("agentsList", agents);
	        return result;
	  }
	  
	  // This will return all agents(centers) for the given shed
	  public static Map<String, Object> getShedAgents(DispatchContext ctx, Map<String, ? extends Object> context) {
		  Delegator delegator = ctx.getDelegator();
		  String shedId = (String)context.get("shedId");		  
		  List<String> agents= FastList.newInstance();
	    	try {	
	    		GenericValue shedDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",shedId), false);
	    		if(UtilValidate.isEmpty(shedDetail)){
	    			Debug.logError("shedId not found with the Id==>"+shedId, module);
	           		return ServiceUtil.returnError("shedId not found with the Id==>"+shedId);  
	        	}	    	
	    		if (!"SHED".equals(shedDetail.getString("facilityTypeId"))) {
	    			Debug.logError("Input not of type Plant: shedId==>"+shedId, module);
	           		return ServiceUtil.returnError("Input not of type Plant: shedId==>"+shedId); 	    			
	    		}	    		
	    		List<GenericValue> units = delegator.findList("Facility", EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, shedId), null, UtilMisc.toList("facilityId"), null, false);
	    		for (int i = 0; i < units.size(); ++i) {
	    			Map temp = getUnitAgents(ctx, UtilMisc.toMap("unitId", units.get(i).getString("facilityId")));	    			
		    		List tempAgents = EntityUtil.getFieldListFromEntityList((List)temp.get("agentsList"), "facilityId", false);
	    			agents.addAll(tempAgents);
	    		}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("agentsList", agents);
	        return result;
	  }
	  
	  public static Map<String, Object> getRoutes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	List<String> routes= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PROC_ROUTE"), null, UtilMisc.toList("facilityId"), null, false);
	            routes = EntityUtil.getFieldListFromEntityList(facilities, "facilityId", false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("routesList", routes);

	        return result;
	    } 
	  
	  //This will  return agent(center) facility id based on unit code and center code
	  public static Map<String, Object> getAgentFacility(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String unitCode = (String)context.get("unitCode");
	    	String centerCode = (String)context.get("centerCode");
	    	GenericValue agentFacility= null;
	    	 Map<String, Object> result = ServiceUtil.returnSuccess();  
	    	if(UtilValidate.isEmpty(unitCode) || UtilValidate.isEmpty(centerCode)){
	    		Debug.logError("unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode, module);	    		
	    		result = ServiceUtil.returnError("unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode);
	    		result.put("agentFacility", agentFacility);
           		return result;  
	    	}
	    	try {
	    		 List<EntityCondition> condList = FastList.newInstance();  
	    		 condList.add(EntityCondition.makeCondition("facilityTypeId", "UNIT"));	    		 
	    		 condList.add(EntityCondition.makeCondition("facilityCode", unitCode));
	    		 List unitList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
	        	 GenericValue unitDetail =  EntityUtil.getFirst(unitList);
	        	 if(UtilValidate.isEmpty(unitDetail)){
	        		 Debug.logError("Unit  not found with the code==>"+unitCode, module);
	           		return ServiceUtil.returnError("Unit  not found with the code==>"+unitCode);  
	        	 }	        	
	    		List routeList = (List)(getUnitRoutes(ctx,UtilMisc.toMap("unitId",unitDetail.getString("facilityId")))).get("routesList");	    	      	 
	    		condList.clear();
	    		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN,routeList));
	        	condList.add(EntityCondition.makeCondition("facilityCode", centerCode));	        	
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	    		agentFacility = EntityUtil.getFirst(facilities);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }	                
	        result.put("agentFacility", agentFacility);

	        return result;
	    }
	  
	  //This will  return agent(center) facility id based on unit code and center code
	  public static Map<String, Object> getAgentFacilityByShedCode(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedCode = (String)context.get("shedCode");
	    	String unitCode = (String)context.get("unitCode");
	    	String centerCode = (String)context.get("centerCode");
	    	GenericValue agentFacility= null;
	    	 Map<String, Object> result = ServiceUtil.returnSuccess();  
	    	if(UtilValidate.isEmpty(shedCode) || UtilValidate.isEmpty(unitCode) || UtilValidate.isEmpty(centerCode)){
	    		Debug.logError("shed code or unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode, module);	    		
	    		result = ServiceUtil.returnError("shed code or unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode);
	    		result.put("agentFacility", agentFacility);
           		return result;  
	    	}
	    	try {    		
	    		
	    		 List<EntityCondition> condList = FastList.newInstance();
	    		 condList.add(EntityCondition.makeCondition("facilityTypeId", "SHED"));	    		 
	    		 condList.add(EntityCondition.makeCondition("facilityCode", shedCode));
	    		 List shedList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
	        	 GenericValue shedDetail =  EntityUtil.getFirst(shedList);
	        	 if(UtilValidate.isEmpty(shedDetail)){
	        		 Debug.logError("shed  not found with the code==>"+shedCode, module);
	           		return ServiceUtil.returnError("shed  not found with the code==>"+shedCode);  
	        	 }	 
	    		
	    		 
	    		List unitList = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",shedDetail.getString("facilityId")))).get("unitsList");
	    		
	    		condList.clear();
	    		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, shedDetail.getString("facilityId")));
	        	condList.add(EntityCondition.makeCondition("facilityCode", unitCode));	        	
	    		List<GenericValue> Unitfacilities = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);  	
	    		if(UtilValidate.isEmpty(Unitfacilities)){
	    			Debug.logError("Unit not found with the code==>"+unitCode, module);
	           		return ServiceUtil.returnError("Unit not found with the code==>"+unitCode);  
	    		}
	    		GenericValue unitDetail = EntityUtil.getFirst(Unitfacilities);
	    		List routeList = (List)(getUnitRoutes(ctx,UtilMisc.toMap("unitId",unitDetail.getString("facilityId")))).get("routesList");	    	      	 
	    		condList.clear();
	    		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.IN,routeList));
	        	condList.add(EntityCondition.makeCondition("facilityCode", centerCode));	        	
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
	    		agentFacility = EntityUtil.getFirst(facilities);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }	                
	        result.put("agentFacility", agentFacility);

	        return result;
	    }   
	  
	  
	  //This will  return Route , Unit and Shed  deatils and for the given Center Id
	  public static Map<String, Object> getCenterDtails(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String centerId = (String)context.get("centerId");
	    	
	    	GenericValue routeFacility = null;
	    	GenericValue unitFacility= null;
	    	GenericValue shedFacility = null;
	    	GenericValue centerDeatail = null;
	    	 Map<String, Object> result = ServiceUtil.returnSuccess();  
	    	if(UtilValidate.isEmpty(centerId)){
	    		Debug.logError("CenterId is  missing. UnitCode=======>"+centerId, module);	    		
	    		result = ServiceUtil.returnError("CenterId is  missing. UnitCode=======>"+centerId);
	    		result.put("unitFacility", unitFacility);
         		return result;  
	    	}
	    	try {	    		
	    		centerDeatail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",centerId), false);
	    		if(UtilValidate.isEmpty(centerDeatail)){
	        		 Debug.logError("Center   not found with the Id==>"+centerId, module);
	           		return ServiceUtil.returnError("Center   not found with the Id==>"+centerId);  
	        	 }
	    		routeFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",centerDeatail.getString("parentFacilityId")), false);	
	    		unitFacility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",routeFacility.getString("parentFacilityId")), false);
	    		shedFacility =  delegator.findOne("Facility", UtilMisc.toMap("facilityId", unitFacility.getString("parentFacilityId")), false);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	    	result.put("centerFacility", centerDeatail);
	    	result.put("routeFacility", routeFacility);
	        result.put("unitFacility", unitFacility);
	        result.put("shedFacility", shedFacility);
	        return result;
	    }
	  public static Map<String, Object> getFacilityAgents(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();   	 	
	        Delegator delegator = dctx.getDelegator();
	        Map resultMap = ServiceUtil.returnSuccess();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String facilityId = (String) context.get("facilityId");
	        List<String> facilityIds= FastList.newInstance();
	        try {	
	    		GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	    		if(UtilValidate.isEmpty(facilityDetail)){
	    			Debug.logError("Facility not found with the Id==>"+facilityId, module);
	           		return ServiceUtil.returnError("Facility not found with the Id==>"+facilityId);  
	        	}
	    		if ("PROC_ROUTE".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getRouteAgents(dctx, UtilMisc.toMap("routeId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList"));
	    		} 
	    		else if ("UNIT".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", facilityDetail.getString("facilityId")));
		    		List tempAgents = EntityUtil.getFieldListFromEntityList((List)temp.get("agentsList"), "facilityId", false);    			
	    			facilityIds.addAll(tempAgents);
	    		} 
	    		else if ("PLANT".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getPlantAgents(dctx, UtilMisc.toMap("plantId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList"));
	    		} 
	    		else if ("SHED".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getShedAgents(dctx, UtilMisc.toMap("shedId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList"));
	    		}
	    		else if ("CENTER".equals(facilityDetail.getString("facilityTypeId"))) {
	    			facilityIds.add(facilityId);
	    		}     		
	    		else {
	    			Debug.logError("Input not of type Plant/Unit/Route/Center: facilityId==>"+facilityId, module);
	           		return ServiceUtil.returnError("Input not of type Plant/Unit/Route/Center: facilityId==>"+facilityId); 	    			
	    		} 
	        }catch(GenericEntityException e){
	        	Debug.logError(e, module);
	        }        
	        resultMap.put("facilityIds", facilityIds);        
	        return resultMap;
	    }
	  
	  public static Map<String, Object> getFacilityChildernTree(DispatchContext dctx, Map<String, ? extends Object> context) {
	   	 	LocalDispatcher dispatcher = dctx.getDispatcher();   	 	
	        Delegator delegator = dctx.getDelegator();
	        Map resultMap = ServiceUtil.returnSuccess();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String facilityId = (String) context.get("facilityId");
	        List<String> facilityIds= FastList.newInstance();
	        try {	
	    		GenericValue facilityDetail = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	    		facilityIds.add(facilityId);
	    		if(UtilValidate.isEmpty(facilityDetail)){
	    			Debug.logError("Facility not found with the Id==>"+facilityId, module);
	           		return ServiceUtil.returnError("Facility not found with the Id==>"+facilityId);  
	        	}
	    		if ("PROC_ROUTE".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getRouteAgents(dctx, UtilMisc.toMap("routeId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList"));
	    		} 
	    		else if ("UNIT".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getUnitAgents(dctx, UtilMisc.toMap("unitId", facilityDetail.getString("facilityId")));
		    		List tempAgents = EntityUtil.getFieldListFromEntityList((List)temp.get("agentsList"), "facilityId", false);    			
	    			facilityIds.addAll(tempAgents);
	    			// get unit routes and add those routes to list
	    			List routeList = (List)(getUnitRoutes(dctx,UtilMisc.toMap("unitId",facilityId))).get("routesList");
	    			facilityIds.addAll(routeList);
	    		} 
	    		else if ("PLANT".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getPlantAgents(dctx, UtilMisc.toMap("plantId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList"));
	    		} 
	    		else if ("SHED".equals(facilityDetail.getString("facilityTypeId"))) {
	    			Map temp = ProcurementNetworkServices.getShedAgents(dctx, UtilMisc.toMap("shedId", facilityId));
	    			facilityIds.addAll((List)temp.get("agentsList")); //unitsDetailList
	    			List<GenericValue>  unitsList= (List)getShedUnitsByShed(dctx ,UtilMisc.toMap("shedId", facilityId)).get("unitsDetailList");
	    			for(GenericValue unit : unitsList){
	    				facilityIds.add(unit.getString("facilityId"));
	    				List routeList = (List)(getUnitRoutes(dctx,UtilMisc.toMap("unitId",unit.getString("facilityId")))).get("routesList");
		    			facilityIds.addAll(routeList);
	    			}
	    			
	    		}
	    		else if ("CENTER".equals(facilityDetail.getString("facilityTypeId"))) {
	    			facilityIds.add(facilityId);
	    		}     		
	    		else {
	    			Debug.logError("Input not of type Plant/Unit/Route/Center: facilityId==>"+facilityId, module);
	           		return ServiceUtil.returnError("Input not of type Plant/Unit/Route/Center: facilityId==>"+facilityId); 	    			
	    		} 
	        }catch(GenericEntityException e){
	        	Debug.logError(e, module);
	        }        
	        resultMap.put("facilityIds", facilityIds);        
	        return resultMap;
	    }
	  
	  
	  public static BigDecimal convertKGToLitre(BigDecimal quantity) {	        	
	     return convertKGToLitreSetScale(quantity,true) ;	    		 
	  } 	
	  public static BigDecimal convertKGToLitreSetScale(BigDecimal quantity,boolean setScale) {	        	
		 BigDecimal quantityLtrs = quantity.divide(new BigDecimal("1.03"), 2,BigDecimal.ROUND_DOWN);
	    	 if(setScale == true){
	    		 //this decimal calculation is customised as per apDairy
	    		 BigDecimal decimalValue = BigDecimal.ZERO;
		    	 decimalValue = quantityLtrs.subtract(new BigDecimal((quantityLtrs.intValue())));
		    	 /* decimal value calculation
		    	  * 	decimal value between 0.25 and 0.75 = 0.5
		    	  * 	greater than or equal to 0.75 = 1.0
		    	  * 	less than 0.25 = 0
		    	  */
		    	 if((decimalValue.compareTo(new BigDecimal(0.75))<0)&&(decimalValue.compareTo(new BigDecimal(0.25))>=0)){
		    		 decimalValue = new BigDecimal(0.5);
		    	 }else if(decimalValue.compareTo(new BigDecimal(0.75))>=0){
		    		 decimalValue = new BigDecimal(1.0);
		         }else{
		    		 decimalValue = BigDecimal.ZERO;
		         }         
		    	 BigDecimal intQtyLts = new BigDecimal(quantityLtrs.intValue());
		    	 quantityLtrs = intQtyLts.add(decimalValue);
	    	 } 
    	 return  quantityLtrs;	    		 
	  } 
	  public static BigDecimal calculateKgFatOrKgSnf(BigDecimal qtyKgs,BigDecimal fatOrSnf ){
	    	BigDecimal result = BigDecimal.ZERO;
	    	if(!((UtilValidate.isEmpty(fatOrSnf))||(UtilValidate.isEmpty(qtyKgs)))){
	    		result = (qtyKgs.multiply(fatOrSnf.divide(new BigDecimal(100)))).setScale(decimals, rounding);
	    	}
	    	return result;
	    }
	  public static BigDecimal calculateFatOrSnf(BigDecimal kgFatOrSnf,BigDecimal totQtyKgs){
		  BigDecimal result = BigDecimal.ZERO;
		  if((totQtyKgs.compareTo(BigDecimal.ZERO)!=0) && (!(UtilValidate.isEmpty(kgFatOrSnf)))){
	    		result = (((kgFatOrSnf.divide(totQtyKgs, 5, rounding)).multiply(new BigDecimal(100)))).setScale(5, rounding);
	    	}
	    	return result;
	    }
	  
	  public static BigDecimal calculateLtrAmt(BigDecimal ltrCost,BigDecimal totLtrs){
		  BigDecimal result = BigDecimal.ZERO;
		  if((ltrCost.compareTo(BigDecimal.ZERO)!=0) && (totLtrs.compareTo(BigDecimal.ZERO)!=0)){
			  ltrCost = ltrCost.setScale(2,rounding);
	    		result = (totLtrs.multiply(ltrCost));
		  }		  
	    	return result;
	    }	  
	  public static List<GenericValue> getProcurementProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
	    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
	         Delegator delegator = dctx.getDelegator();
	         LocalDispatcher dispatcher = dctx.getDispatcher();
	         if(!UtilValidate.isEmpty(context.get("salesDate"))){
	        	salesDate =  (Timestamp) context.get("salesDate");  
	         }
	        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
	    	List<GenericValue> productList = ProductWorker.getProductsByCategory(delegator ,"MILK_PROCUREMENT" ,UtilDateTime.getDayStart(salesDate));	    	
	    	return productList;
		}
	  /*
	   * service for get Procurement Transfer Products
	   * 
	   */
	  public static List<GenericValue> getProcurementTransferProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
	    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
	         Delegator delegator = dctx.getDelegator();
	         LocalDispatcher dispatcher = dctx.getDispatcher();
	         if(!UtilValidate.isEmpty(context.get("salesDate"))){
	        	salesDate =  (Timestamp) context.get("salesDate");  
	         }
	        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
	    	List<GenericValue> productList = ProductWorker.getProductsByCategory(delegator ,"MILK_PROC_TRN" ,UtilDateTime.getDayStart(salesDate));	    	
	    	return productList;
		}
	  
	  /**
	   * helper method for getting shed details for given facility Id
	   */
	  public static Map<String, ? extends Object> getShedDetailsForFacility(DispatchContext dctx, Map<String, ? extends Object> context) {
		  LocalDispatcher dispatcher = dctx.getDispatcher();   	 	
	      Delegator delegator = dctx.getDelegator();
	      Map<String, Object> result = FastMap.newInstance();
	      String facilityId = (String)context.get("facilityId");
	      GenericValue facility = null;
	      if(UtilValidate.isNotEmpty(facilityId)){
	    	  try{
	    		  facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	    		  if(UtilValidate.isEmpty(facility)){
		    		  result = ServiceUtil.returnError(" facility not found with the facility Id ========> "+facilityId);
					  return result;
		    	  }
	    	  }catch (GenericEntityException e) {
				  result = ServiceUtil.returnError(" facility not found with the facility Id ========> "+facilityId);
				  return result;
	    	  }
	    	 
	    	  String facilityType = facility.getString("facilityTypeId");
	    	  if(facilityType.equalsIgnoreCase("SHED")){
	    		  	  result.put("facility",facility);
	    			  return result;
	    	  }
	    	  facilityId =(String) facility.get("parentFacilityId");
	    	  return getShedDetailsForFacility(dctx,UtilMisc.toMap("facilityId", facilityId));
    		  
	      }
		  return result;
	  }
	  // this is general service 
	  public static BigDecimal getSnfFromLactoReading(BigDecimal lactoReading,BigDecimal fatQty ){
		  /* 
     	  * formula for calculate total solids using lr,fat% 
     	  * snf = lactoReading/4+0.21*fat+0.364
     	  */
		  	BigDecimal snfQty = BigDecimal.ZERO;
	    	snfQty = (lactoReading.divide(new BigDecimal(4),5,4)).add((fatQty.multiply(new BigDecimal(0.21)))).add(new BigDecimal(0.36)).setScale(3,5);
	    	return snfQty;
	    }
	 // this service is alternate for calculating snf from Lacto reading
	  public static BigDecimal getSnfFromRichmondsLactoReading(BigDecimal lactoReading,BigDecimal fatQty ){
		  /* 
     	  * formula for calculate total solids using lr,fat% 
     	  * snf = lactoReading/4+0.21*fat+0.614
     	  */
		  	BigDecimal snfQty = BigDecimal.ZERO;
	    	snfQty = (lactoReading.divide(new BigDecimal(4),5,4)).add((fatQty.multiply(new BigDecimal(0.21)))).add(new BigDecimal(0.61)).setScale(3,5);
	    	return snfQty;
	    }
	  public static BigDecimal convertLitresToKG(BigDecimal qtyLtrs ){
		  BigDecimal qtyKgs = BigDecimal.ZERO;
		  qtyKgs = qtyLtrs.multiply(new BigDecimal("1.03")).setScale(2,BigDecimal.ROUND_UP);
		  return qtyKgs;
		  
	  }
	// this is general service
	  public static BigDecimal convertFatSnfToLR(BigDecimal fat,BigDecimal snf ){
      /* 
       * lactoReading = [(snf-0.364)-(0.21*fat)]*4
       */
          BigDecimal lactoReading = BigDecimal.ZERO;
          lactoReading = (((snf.subtract(new BigDecimal(0.36))).subtract((fat.multiply(new BigDecimal(0.21)).setScale(2,4)))).multiply(new BigDecimal(4))).setScale(2,4); 
            return lactoReading;
     }
		 // this service is alternate for calculating Lacto reading From Fat and snf
	 public static BigDecimal convertFatSnfToRichmondLR(BigDecimal fat,BigDecimal snf ){
	      /* 
	       * lactoReading = [(snf-0.614)-(0.21*fat)]*4
	       */
	          BigDecimal lactoReading = BigDecimal.ZERO;
	          lactoReading = (((snf.subtract(new BigDecimal(0.61))).subtract((fat.multiply(new BigDecimal(0.21)).setScale(2,4)))).multiply(new BigDecimal(4))).setScale(2,4); 
	          return lactoReading;
	 } 
	 
	  /*
	   * Note:  This method assumes that there this is only one active fin account per center.
	   */
	  public static Map<String, Object> getShedFacilityFinAccount(DispatchContext ctx, Map<String, ? extends Object> context) {
		  Delegator delegator = ctx.getDelegator();
		  Map temp= getFacilityChildernTree(ctx, context);
		  List<String> agents = (List<String>)temp.get("facilityIds");
	      Map<String, Object> result = ServiceUtil.returnSuccess();        
		  Map<String, Object> facAccntsMap = FastMap.newInstance();
		  List<Map> finAccountList= FastList.newInstance();
		  try {	
			  List<EntityCondition> condList = FastList.newInstance();  
			  condList.add(EntityCondition.makeCondition("statusId", "FNACT_ACTIVE"));				  
			  condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, agents));	
			  List<GenericValue> facAccnts = delegator.findList("FacilityPersonAndFinAccount", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
			  for (int i = 0; i < facAccnts.size(); ++i) {
	    			GenericValue facAccnt = facAccnts.get(i);
	    			String facilityId = facAccnt.getString("facilityId");
	    			Map<String, String> facAccntMap = FastMap.newInstance();
	    			facAccntMap.put("finAccountId", facAccnt.getString("finAccountId"));
	    			facAccntMap.put("finAccountName", facAccnt.getString("finAccountName"));
	    			facAccntMap.put("finAccountCode", facAccnt.getString("finAccountCode"));
	    			facAccntMap.put("finAccountBranch", facAccnt.getString("finAccountBranch"));
	    			facAccntMap.put("micrNumber", facAccnt.getString("micrNumber"));
	    			facAccntMap.put("ifscCode", facAccnt.getString("ifscCode"));
	    			facAccntMap.put("gbCode", facAccnt.getString("gbCode"));
	    			facAccntMap.put("bCode", facAccnt.getString("bCode"));
	    			facAccntMap.put("bPlace", facAccnt.getString("bPlace"));
	    			facAccntsMap.put(facilityId, facAccntMap);
	    			finAccountList.add(facAccntMap);
			  }
		  } catch (GenericEntityException e) {
			  Debug.logError(e, module);
	          return ServiceUtil.returnError(e.getMessage());
		  }
	      result.put("facAccntsMap", facAccntsMap);
	      result.put("finAccountList", finAccountList);
	      return result;	  
	  }
	  public static Map<String, Object> createProcFacilityRate(DispatchContext dctx, Map context) {
	    	Map resultMap = ServiceUtil.returnSuccess();
	    	String facilityId = (String) context.get("facilityId");
	    	String productId = (String) context.get("productId");
	    	BigDecimal rateAmount = (BigDecimal)context.get("rateAmount");
	    	String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
	    	String rateTypeId = (String)context.get("rateTypeId");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			// from date is setting to previous year start
			Timestamp fromDate = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()),-1));
			Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
			if(UtilValidate.isNotEmpty(context.get("fromDate"))){
				 fromDate = (Timestamp) context.get("fromDate");
			}
			if(UtilValidate.isEmpty(productId)){
				productId = "_NA_";
			}
			if(UtilValidate.isEmpty(supplyTypeEnumId)){
				supplyTypeEnumId = "_NA_";
			}
			try{
				GenericValue facility = delegator.findOne("Facility", false,UtilMisc.toMap("facilityId",facilityId));
				if(UtilValidate.isEmpty(facility)){
					resultMap = ServiceUtil.returnError("Facility Not Found ========>"+facilityId);
					return resultMap;
				}
				List facilityRateConditionList = FastList.newInstance();
				facilityRateConditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
				facilityRateConditionList.add(EntityCondition.makeCondition("supplyTypeEnumId",EntityOperator.EQUALS,supplyTypeEnumId));
				facilityRateConditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
				facilityRateConditionList.add(EntityCondition.makeCondition("rateCurrencyUomId",EntityOperator.EQUALS,"INR"));
				facilityRateConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN,fromDate));
				facilityRateConditionList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS,rateTypeId));
				EntityCondition facilityRateCondition = EntityCondition.makeCondition(facilityRateConditionList,EntityOperator.AND);	
				List<GenericValue> facilityRateList = delegator.findList("FacilityRate", facilityRateCondition, null,null, null, false);
				if(UtilValidate.isNotEmpty(facilityRateList)){
					GenericValue activeFacilityRate = EntityUtil.getFirst((List<GenericValue>)EntityUtil.filterByDate(facilityRateList,fromDate));
					activeFacilityRate.set("thruDate", previousDayEnd);
					activeFacilityRate.store();
				}
				GenericValue FacilityRate = delegator.makeValue("FacilityRate");
				FacilityRate.set("facilityId", facilityId);
				FacilityRate.set("rateAmount", rateAmount);
				FacilityRate.set("rateCurrencyUomId", "INR");
				FacilityRate.set("supplyTypeEnumId", supplyTypeEnumId);
				FacilityRate.set("productId", productId);
				FacilityRate.set("fromDate",fromDate);
				FacilityRate.set("rateTypeId", rateTypeId);
				FacilityRate.create();
				
			}catch(GenericEntityException e){
				Debug.logError("Error while creating Facility Rate"+e.getMessage(), module);
			}
	    	return resultMap;
	    }
	  public static Map<String, Object> getFacilityShedByUserLogin(DispatchContext dctx, Map context) {
		  GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		  LocalDispatcher dispatcher = dctx.getDispatcher();
		  Map resultMap = ServiceUtil.returnSuccess();
		  GenericValue userLogin = (GenericValue)context.get("userLogin");
		  String partyId = (String)userLogin.get("partyId") ;
		  if(UtilValidate.isEmpty(partyId)){
			  resultMap = ServiceUtil.returnError("No Facility is associated with this user");
			  return resultMap;
		  }
		  List<GenericValue> facilityParties = FastList.newInstance();
		  GenericValue activeFacilityParty = null;
		  String facilityId = null;
		  try{
			  facilityParties = delegator.findList("FacilityParty",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId), null, null, null,false);
			  activeFacilityParty = EntityUtil.getFirst((List<GenericValue>)EntityUtil.filterByDate(facilityParties,UtilDateTime.nowTimestamp()));
			  if(UtilValidate.isEmpty(facilityParties)){
				  resultMap = ServiceUtil.returnError("No Facility is associated with this user ::"+userLogin.get("loginId"));
				  return resultMap;
			  }
			  facilityId = (String) activeFacilityParty.get("facilityId");
			  Map unitDetailsMap = FastMap.newInstance();
			  Map shedDetailsMap = FastMap.newInstance();
			  GenericValue facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId", facilityId),false);
			  String facilityTypeId = (String)facilityDetails.get("facilityTypeId");
			  if("UNIT".equalsIgnoreCase(facilityTypeId)){
				  unitDetailsMap = facilityDetails;
			  }
			  Map shedInMap = FastMap.newInstance();
			  shedInMap.put("userLogin", userLogin);
			  shedInMap.put("facilityId", facilityId);
			  Map shedDetails = getShedDetailsForFacility(dctx,shedInMap);
			  if(ServiceUtil.isError(shedDetails)){
				  resultMap = ServiceUtil.returnError("Error while getting shed Details");
				  return resultMap;
			  }
			  shedDetailsMap = (Map) shedDetails.get("facility");
			  resultMap.put("unitDetails", unitDetailsMap);
			  resultMap.put("shedDetails",shedDetailsMap);
		  }catch(GenericEntityException e){
			  Debug.logError("Error while getting facilities associated with this user"+e.getMessage(), module);
			  resultMap = ServiceUtil.returnError("Error while getting facilities associated with this user ::"+userLogin.get("loginId"));
			  return resultMap;
		  }
		  return resultMap;
		  
	    }// end of the service
	  
}
