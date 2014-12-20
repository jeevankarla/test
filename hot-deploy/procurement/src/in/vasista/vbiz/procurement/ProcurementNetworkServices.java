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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityListIterator;
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
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.EntityUtil;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.NullPointerException;


import in.vasista.vbiz.procurement.ProcurementReports;
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
	   public static List getProcurementTimePeriods(Delegator delegator) {
	    	
	    	List timePeriodsList = FastList.newInstance();
	    	try {
	    		timePeriodsList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"), null, UtilMisc.toList("-thruDate"), null, true);
	    		     
	    	}catch (GenericEntityException e) {
	            Debug.logError(e, module);	           
	        }
	        return timePeriodsList;
	    } 
	 // this method will return all the units belongs the given shedId or shed code
	 // if both shedcode and shedid given then shed id is takes the highest priority   
	  public static Map<String, Object> getShedUnitsByShed(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedCode = (String)context.get("shedCode");	    	
	    	String shedId = (String)context.get("shedId");
	    	List<String> units = FastList.newInstance();
	    	List<GenericValue> facilities = FastList.newInstance();
	    	List<GenericValue> facilityCodeList = FastList.newInstance();
	    	String facilityCode = null;
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
	    	List<GenericValue> unitsDetailList = getSortedFacilities(facilities);
	    	
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("unitsList", units);
	        result.put("unitsDetailList", unitsDetailList);
	        return result;
	    }
	  //this methood will return all the  units for given shedId and customTimePeriodId
	  public static Map<String, Object> getShedCustomTimePeriodUnits(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedId = (String)context.get("shedId");
	    	String customTimePeriodId = (String)context.get("customTimePeriodId");
	    	List<String> unitIds = FastList.newInstance();	    	
	    	List<GenericValue> customTimePeriodFacilities = FastList.newInstance();
	    	List<GenericValue> customTimePeriodUnitDetailsList =FastList.newInstance();
	    	try {
	    		if(UtilValidate.isNotEmpty(shedId)){
	    			List shedUnitIds = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",shedId))).get("unitsList"); 
	    			customTimePeriodFacilities = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod", EntityCondition.makeCondition(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId),EntityOperator.AND,EntityCondition.makeCondition("facilityId", EntityOperator.IN,shedUnitIds) ), null, UtilMisc.toList("facilityId"), null, false);
	    			if(UtilValidate.isEmpty(customTimePeriodFacilities)){
	    				Debug.logError("Invalid Shed Id"+shedId, module);
	    	            return ServiceUtil.returnError("Invalid Shed Id"+shedId);
	    			}
	    		}
	    		unitIds=EntityUtil.getFieldListFromEntityList(customTimePeriodFacilities, "facilityId", true);
	    		customTimePeriodUnitDetailsList = delegator.findList("Facility", EntityCondition.makeCondition("facilityId", EntityOperator.IN,unitIds), null, UtilMisc.toList("facilityId"), null, false);	           
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	    	 customTimePeriodUnitDetailsList = getSortedFacilities(customTimePeriodUnitDetailsList);	    	
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("unitsList", unitIds);
	        result.put("customTimePeriodUnitsDetailList", customTimePeriodUnitDetailsList);
	        return result;
	    }
	  public static Map<String, Object> getShedOrderAdjustmentDescription(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String shedId = (String)context.get("shedId");
	    	List<GenericValue> orderAdjItemsList =FastList.newInstance();
	    	Map adjustmentDedTypes = FastMap.newInstance();
	    	List orderAdjDescList =FastList.newInstance();
	    	try {	    		
	    		orderAdjItemsList = delegator.findList("OrderAdjustmentType",EntityCondition.makeCondition(EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MILKPROC_DEDUCTIONS"),EntityOperator.OR, EntityCondition.makeCondition("parentTypeId" ,EntityOperator.EQUALS,"MILKPROC_ADDITIONS") ),null,null,null,false);
	    		for(int i=0;i<orderAdjItemsList.size();i++){
	  				GenericValue orderAdjItem = orderAdjItemsList.get(i);
	  				String orderAdjustmentTypeId=orderAdjItem.getString("orderAdjustmentTypeId");
	  				String description=orderAdjItem.getString("description");    
	  				Map tempDescMap=FastMap.newInstance();
	  				if("WGD".equals(shedId)){
	  					if("MILKPROC_SEEDDED".equals(orderAdjustmentTypeId)){
	  						description="MEDICIN";
	  					}
	  					if("MILKPROC_STORET".equals(orderAdjustmentTypeId)){
	  						description="STORES";
	  					}
	  					if("MILKPROC_STOREA".equals(orderAdjustmentTypeId)){
	  						description="SEED";
	  					}
	  					if("MILKPROC_VIJAYARD".equals(orderAdjustmentTypeId)){
	  						description="M.MIXTURE";
	  					}	    					
	  					if("MILKPROC_VIJAYALN".equals(orderAdjustmentTypeId)){
	  						description="G.CAKE";
	  					}
	  					if("MILKPROC_OTHERDED".equals(orderAdjustmentTypeId)){
	  						description="EMT LOAN";
	  					}
	  					if("MILKPROC_MTESTER".equals(orderAdjustmentTypeId)){
	  						description="EMT SPARES";
	  					}	    					
	  					if("MILKPROC_MSPARES".equals(orderAdjustmentTypeId)){
	  						description="LOCAL SALES";
	  					}
	  					if("MILKPROC_CESSONSALE".equals(orderAdjustmentTypeId)){
	  						description="BILL DIFF";
	  					}
	  					if("MILKPROC_STATONRY".equals(orderAdjustmentTypeId)){
	  						description="OTHERS";
	  					}  					
	  				}
	  				adjustmentDedTypes.put(orderAdjustmentTypeId, description);
	  				tempDescMap.put("orderAdjustmentTypeId",orderAdjustmentTypeId);
  					tempDescMap.put("description", description);
	  				orderAdjDescList.add(tempDescMap);
	  			}
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        Map<String, Object> result = ServiceUtil.returnSuccess();        
	        result.put("shedAdjustmentDescriptionMap", adjustmentDedTypes);
	        result.put("orderAdjDescList", orderAdjDescList);
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
	    	facilities = getSortedFacilities(facilities);
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
	    		facilities =getSortedFacilities(facilities);
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
	    	    facilities =getSortedFacilities(facilities);
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
	    	String shedId = (String)context.get("shedId");
	    	List unitDetails= null;
	    	List units = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(shedCode)){
    			units = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedCode",shedCode))).get("unitsDetailList"); 
    		}
    		if(UtilValidate.isNotEmpty(shedId)){
    			units = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",shedId))).get("unitsDetailList"); 
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
		        	agentsList = getSortedFacilities(agentsList);
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
	        	agentsList = getSortedFacilities(agentsList);
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
	  
	  public static Map<String, Object> getSupervisorDetails(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
			String routeId = (String) context.get("routeId"); 
	        Map<String, Object> result = FastMap.newInstance(); 
	        GenericValue supervisorDetails;
	        String supervisorName;
	        try {
	        	 List<EntityCondition> condList = FastList.newInstance();  
	    		 condList.add(EntityCondition.makeCondition("facilityId", routeId));	    		 
	    		 condList.add(EntityCondition.makeCondition("roleTypeId", "SUPERVISOR"));
	    		 List supervisorList = delegator.findList("FacilityFacilityPartyAndPerson", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, true);
	        	 supervisorDetails =  EntityUtil.getFirst(supervisorList);
	        	if (supervisorDetails == null) {
	                Debug.logError("Invalid facilityId " + routeId, module);
	                return ServiceUtil.returnError("Invalid facilityId " + routeId);         		
	        	}
	        	supervisorName = PartyHelper.getPartyName(delegator, supervisorDetails.getString("partyId"), false);
	        }
	        catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());          	
	        }    
	        Map<String, Object> supervisorMap = FastMap.newInstance();
	        supervisorMap.put("facilityId", routeId);
	        supervisorMap.put("partyId", supervisorDetails.getString("partyId"));
	        supervisorMap.put("supervisorName", supervisorName);
	        supervisorMap.put("firstName", supervisorDetails.getString("firstName"));
	        supervisorMap.put("lastName", supervisorDetails.getString("lastName"));
	        result.put("supervisor", supervisorMap);
	        return result;
	    }
	  public static Map<String, Object> getRoutes(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	List<String> routes= FastList.newInstance();
	    	try {
	    		List<GenericValue> facilities = delegator.findList("Facility", EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PROC_ROUTE"), null, UtilMisc.toList("facilityId"), null, false);
	    		facilities = getSortedFacilities(facilities);
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
	    	String shedId = (String)context.get("shedId");
	    	String shedCode = (String)context.get("shedCode");
	    	String unitCode = (String)context.get("unitCode");
	    	String centerCode = (String)context.get("centerCode");
	    	GenericValue agentFacility= null;
	    	Map<String, Object> result = ServiceUtil.returnSuccess();
	    	if((UtilValidate.isEmpty(shedCode)&&UtilValidate.isEmpty(shedId)) || UtilValidate.isEmpty(unitCode) || UtilValidate.isEmpty(centerCode)){
	    		Debug.logError("shed code or unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode, module);	    		
	    		result = ServiceUtil.returnError("shed code or unit code or center code is missing. UnitCode=======>"+unitCode+"====center Code====>"+centerCode);
	    		result.put("agentFacility", agentFacility);
           		return result;  
	    	}
	    	try {    		
	    		List<EntityCondition> condList = FastList.newInstance();
	    		if(UtilValidate.isEmpty(shedId)){
	    			
		    		 condList.add(EntityCondition.makeCondition("facilityTypeId", "SHED"));	    		 
		    		 condList.add(EntityCondition.makeCondition("facilityCode", shedCode));
		    		 List shedList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
		    		 GenericValue shedDetail =  EntityUtil.getFirst(shedList);
		        	 if(UtilValidate.isEmpty(shedDetail)){
		        		 Debug.logError("shed  not found with the code==>"+shedCode, module);
		           		return ServiceUtil.returnError("shed  not found with the code==>"+shedCode);  
		        	 }else{
		        		 shedId = shedDetail.getString("facilityId");
		        	 }	 
	    		}
	    		List unitList = (List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",shedId))).get("unitsList");
	    		condList.clear();
	    		condList.add(EntityCondition.makeCondition("parentFacilityId", EntityOperator.EQUALS, shedId));
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
	  
	  
	  //This will  return facilityId for the given Can Code
	  public static Map<String, Object> getFacilityByCanCode(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	String canCode = (String)context.get("canCode");
	    	 Map<String, Object> result = ServiceUtil.returnSuccess(); 
	    	 GenericValue facility = null;
	    	try {
	    		 List<GenericValue> facilityCanList = delegator.findList("ProcAgentCanMas",EntityCondition.makeCondition("canCode",EntityOperator.EQUALS,canCode),null,null,null,false);
	    		 if(facilityCanList.size()== 0 || (facilityCanList.size()> 1)){
	    			 Debug.logError("Invalid Can Code==>"+canCode,module); 
	    		 }
	    		 GenericValue facilityCanMas = EntityUtil.getFirst(facilityCanList);
	    		 facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityCanMas.getString("facilityId")), true);
	    	} catch (GenericEntityException e) {
	            Debug.logError(e, module);
	            return ServiceUtil.returnError(e.getMessage());
	        }
	        result.put("facility", facility);
	       
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
		 BigDecimal quantityLtrs = quantity.divide(new BigDecimal("1.0295"), 2,BigDecimal.ROUND_HALF_UP);
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
	    		result = (qtyKgs.multiply(fatOrSnf.divide(new BigDecimal(100)))).setScale(2, BigDecimal.ROUND_HALF_UP);
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
	  /**
	   * Service to get MilkReciptProducts
	   * @param dctx
	   * @param context
	   * @return
	   */
	  public static List<GenericValue> getMilkReceiptProducts(DispatchContext dctx, Map<String, ? extends  Object> context){
	    	 Timestamp salesDate = UtilDateTime.nowTimestamp();    	
	         Delegator delegator = dctx.getDelegator();
	         LocalDispatcher dispatcher = dctx.getDispatcher();
	         if(!UtilValidate.isEmpty(context.get("salesDate"))){
	        	salesDate =  (Timestamp) context.get("salesDate");  
	         }
	        Timestamp dayBegin =UtilDateTime.getDayStart(salesDate);
	    	List<GenericValue> productList = ProductWorker.getProductsByCategory(delegator ,"MILK_RECEIPTS" ,UtilDateTime.getDayStart(salesDate));	    	
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
	    	snfQty = (lactoReading.divide(new BigDecimal(4),5,4)).add((fatQty.multiply(new BigDecimal(0.21)))).add(new BigDecimal(0.36)).setScale(2,BigDecimal.ROUND_HALF_UP);
	    	return snfQty;
	    }
	// this  service  returns Snf value by evoluting Accounting Formula
	  public static Map<String, Object> getSnfFromLactoReading(DispatchContext dctx, Map<String, ? extends  Object> context){
		  	Map result = ServiceUtil.returnSuccess();
		  	BigDecimal snfQty = BigDecimal.ZERO;
		  	String acctgFormulaId =(String)context.get("acctgFormulaId");
		  	BigDecimal fatQty = (BigDecimal)context.get("fatQty");
		  	BigDecimal lactoReading =(BigDecimal)context.get("lactoReading");
		  	GenericValue userLogin = (GenericValue) context.get("userLogin");
		  	LocalDispatcher dispatcher = dctx.getDispatcher();
		  	if(UtilValidate.isEmpty(acctgFormulaId)){
		  		snfQty = getSnfFromLactoReading(lactoReading,fatQty);
		  		result.put("snfQty", snfQty);
		  		return result;
		  	}
		  	try{
		  		result.put("snfQty", snfQty);
			  	Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "acctgFormulaId",acctgFormulaId, "variableValues","FAT="+fatQty+","+"LR="+lactoReading);
			  	Map<String, Object> incentivesResult = dispatcher.runSync("evaluateAccountFormula", input);
			  	if (ServiceUtil.isError(incentivesResult)) {
			  		Debug.logError("unable to evaluate AccountFormula"+acctgFormulaId, module);	
			  		result = ServiceUtil.returnError("unable to evaluate AccountFormula"+acctgFormulaId);
			  		return result; 
			  	}
		  		double formulaValue = (Double) incentivesResult.get("formulaResult");
		  		BigDecimal formulaBigDecimal = (new BigDecimal(formulaValue)).setScale(3, BigDecimal.ROUND_HALF_UP);
		  		snfQty = formulaBigDecimal.setScale(2,BigDecimal.ROUND_HALF_DOWN);
		  	}catch(GenericServiceException e){
		  		Debug.logError("Error while evaluating AccountFormula "+e,module);
		  		return result;
		  	}catch(Exception e){
		  		Debug.logError("Error while evaluating AccountFormula "+e, module);
		  		return result;
		  	}
		  	
		  	result.put("snfQty", snfQty);
		  	/*BigDecimal snfQty = BigDecimal.ZERO;
		    snfQty = (lactoReading.divide(new BigDecimal(4),5,4)).add((fatQty.multiply(new BigDecimal(0.21)))).add(new BigDecimal(0.36)).setScale(2,BigDecimal.ROUND_HALF_UP);*/
		    return result;
	    }
	  public static BigDecimal convertLitresToKG(BigDecimal qtyLtrs ){
		  BigDecimal qtyKgs = BigDecimal.ZERO;
		  qtyKgs = qtyLtrs.multiply(new BigDecimal("1.0295")).setScale(2,BigDecimal.ROUND_HALF_EVEN);
		  return qtyKgs;
		  
	  }
	  public static BigDecimal convertLitresToKGSetScale(BigDecimal qtyLtrs,boolean setScale) {
		  BigDecimal qtyKgs = BigDecimal.ZERO;
	      if(setScale == true){
	    	//this decimal calculation is customised as per apDairy ,  this will round the qtyKgs to  0 or 0.5
	    	  //  x= (ltr*1.03*100)/50
	    	  // qtyKgs = = (x*50)/100
	    	  qtyKgs = (qtyLtrs.multiply(new BigDecimal("1.0295"))).multiply(new BigDecimal("100")).divide(new BigDecimal("50")).setScale(0,rounding);
	    	  qtyKgs = qtyKgs.multiply(new BigDecimal("50")).divide(new BigDecimal("100")).setScale(1, rounding);
	    	  
	    	}else{
	    		return convertLitresToKG(qtyLtrs);
	    	}
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
		  String statusFlag = (String)context.get("statusFlag");
		  String finAccountName = (String)context.get("finAccountName");
		  try {	
			  List<EntityCondition> condList = FastList.newInstance();  
			  if(UtilValidate.isEmpty(statusFlag)|| statusFlag.equalsIgnoreCase("ACTIVE")){
				  condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS, "FNACT_ACTIVE"));
			  }
			  if(UtilValidate.isNotEmpty(finAccountName) && !(UtilValidate.areEqual(finAccountName, "All"))){
				  condList.add(EntityCondition.makeCondition("finAccountName",EntityOperator.EQUALS, finAccountName));
			  }
			  condList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, agents));
			  //here exclude Company owned finaccounts
			  condList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.NOT_EQUAL, "Company"));
			  EntityCondition condition =EntityCondition.makeCondition(condList, EntityOperator.AND);
			  //List<GenericValue> facAccnts = delegator.findList("FacilityPersonAndFinAccount", EntityCondition.makeCondition(condList, EntityOperator.AND), null, null, null, false);
			  EntityListIterator finAccIter = null;
			  finAccIter = delegator.find("FacilityPersonAndFinAccount", condition,	null, null, null, null);
			  GenericValue facAccnt= null;
			  
			  while( finAccIter != null && (facAccnt = finAccIter.next()) != null) {
				  String facilityId = facAccnt.getString("facilityId");
	    			Map<String, String> facAccntMap = FastMap.newInstance();
	    			facAccntMap.put("facilityId", facilityId);
	    			facAccntMap.put("facilityCode", facAccnt.getString("facilityCode"));
	    			facAccntMap.put("facilityType", facAccnt.getString("facilityTypeId"));
	    			facAccntMap.put("finAccountId", facAccnt.getString("finAccountId"));
	    			facAccntMap.put("finAccountName", facAccnt.getString("finAccountName"));
	    			facAccntMap.put("finAccountCode", facAccnt.getString("finAccountCode"));
	    			facAccntMap.put("finAccountBranch", facAccnt.getString("finAccountBranch"));
	    			facAccntMap.put("partyId", facAccnt.getString("ownerPartyId"));
	    			facAccntMap.put("micrNumber", facAccnt.getString("micrNumber"));
	    			facAccntMap.put("ifscCode", facAccnt.getString("ifscCode"));
	    			facAccntMap.put("gbCode", facAccnt.getString("gbCode"));
	    			facAccntMap.put("bCode", facAccnt.getString("bCode"));
	    			facAccntMap.put("bPlace", facAccnt.getString("bPlace"));
	    			facAccntMap.put("shortName", facAccnt.getString("shortName"));
	    			facAccntMap.put("thruDate", facAccnt.getString("thruDate"));
	    			facAccntsMap.put(facilityId, facAccntMap);
	    			finAccountList.add(facAccntMap);
				  
			  }
			  if (finAccIter != null) {
		            try {
		            	finAccIter.close();
		            } catch (GenericEntityException e) {
		                Debug.logWarning(e, module);
		            }
		        }

			  /*for (int i = 0; i < facAccnts.size(); ++i) {
	    			GenericValue facAccnt = facAccnts.get(i);
	    			String facilityId = facAccnt.getString("facilityId");
	    			Map<String, String> facAccntMap = FastMap.newInstance();
	    			facAccntMap.put("facilityId", facilityId);
	    			facAccntMap.put("facilityCode", facAccnt.getString("facilityCode"));
	    			facAccntMap.put("facilityType", facAccnt.getString("facilityTypeId"));
	    			facAccntMap.put("finAccountId", facAccnt.getString("finAccountId"));
	    			facAccntMap.put("finAccountName", facAccnt.getString("finAccountName"));
	    			facAccntMap.put("finAccountCode", facAccnt.getString("finAccountCode"));
	    			facAccntMap.put("finAccountBranch", facAccnt.getString("finAccountBranch"));
	    			facAccntMap.put("partyId", facAccnt.getString("ownerPartyId"));
	    			facAccntMap.put("micrNumber", facAccnt.getString("micrNumber"));
	    			facAccntMap.put("ifscCode", facAccnt.getString("ifscCode"));
	    			facAccntMap.put("gbCode", facAccnt.getString("gbCode"));
	    			facAccntMap.put("bCode", facAccnt.getString("bCode"));
	    			facAccntMap.put("bPlace", facAccnt.getString("bPlace"));
	    			facAccntMap.put("shortName", facAccnt.getString("shortName"));
	    			facAccntMap.put("thruDate", facAccnt.getString("thruDate"));
	    			facAccntsMap.put(facilityId, facAccntMap);
	    			finAccountList.add(facAccntMap);
			  }*/
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
	    	String uomId = (String) context.get("uomId");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			// from date is setting to previous year start
			Timestamp fromDate = UtilDateTime.getYearStart(UtilDateTime.addDaysToTimestamp(UtilDateTime.getYearStart(UtilDateTime.nowTimestamp()),-1));
			if(UtilValidate.isNotEmpty(context.get("fromDate"))){
				 fromDate = (Timestamp) context.get("fromDate");
			}
			Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
			Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
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
				facilityRateConditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.GREATER_THAN,dayBegin));
				facilityRateConditionList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS,rateTypeId));
				EntityCondition facilityRateCondition = EntityCondition.makeCondition(facilityRateConditionList,EntityOperator.AND);	
				List<GenericValue> facilityRateList = delegator.findList("FacilityRate", facilityRateCondition, null,null, null, false);
				if(UtilValidate.isNotEmpty(facilityRateList)){
					GenericValue activeFacilityRate = EntityUtil.getFirst((List<GenericValue>)EntityUtil.filterByDate(facilityRateList,dayBegin));
					activeFacilityRate.set("thruDate", previousDayEnd);
					activeFacilityRate.store();
				}
				GenericValue FacilityRate = delegator.makeValue("FacilityRate");
				FacilityRate.set("facilityId", facilityId);
				FacilityRate.set("rateAmount", rateAmount);
				FacilityRate.set("rateCurrencyUomId", "INR");
				FacilityRate.set("supplyTypeEnumId", supplyTypeEnumId);
				FacilityRate.set("productId", productId);
				FacilityRate.set("fromDate",dayBegin);
				if(UtilValidate.isNotEmpty(uomId)){
					FacilityRate.set("uomId",uomId);	
				}
				FacilityRate.set("rateTypeId", rateTypeId);
				FacilityRate.create();
				
			}catch(GenericEntityException e){
				Debug.logError("Error while creating Facility Rate"+e.getMessage(), module);
			}
	    	return resultMap;
	    }
	  public static Map<String, Object> updateProcFacilityRate(DispatchContext dctx, Map context) {
	    	Map resultMap = ServiceUtil.returnSuccess();
	    	String facilityId = (String) context.get("facilityId");
	    	String productId = (String) context.get("productId");
	    	BigDecimal rateAmount = (BigDecimal)context.get("rateAmount");
	    	String supplyTypeEnumId = (String) context.get("supplyTypeEnumId");
	    	String rateTypeId = (String)context.get("rateTypeId");
	    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp dayBegin = UtilDateTime.getDayStart(fromDate);
			Timestamp previousDayEnd = UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(fromDate, -1));
			String uomId = (String) context.get("uomId");
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
				facilityRateConditionList.add(EntityCondition.makeCondition("rateTypeId",EntityOperator.EQUALS,rateTypeId));
				EntityCondition facilityRateCondition = EntityCondition.makeCondition(facilityRateConditionList,EntityOperator.AND);	
				List<GenericValue> facilityRateList = delegator.findList("FacilityRate", facilityRateCondition, null,null, null, false);
				boolean createEntry = false;
				if(UtilValidate.isNotEmpty(facilityRateList)){
					List activeFacilityRateList = EntityUtil.filterByDate(facilityRateList,dayBegin);
					GenericValue activeFacilityRate = EntityUtil.getFirst(activeFacilityRateList);
					BigDecimal activeFacilityRateAmount = activeFacilityRate.getBigDecimal("rateAmount");
					Timestamp activeFacilityRateFromDate = activeFacilityRate.getTimestamp("fromDate");
					String activeUomId = activeFacilityRate.getString("uomId");
					if(activeFacilityRateFromDate.compareTo(dayBegin)== 0){
						if((UtilValidate.isNotEmpty(activeFacilityRateAmount) && activeFacilityRateAmount.compareTo(rateAmount)!= 0) || (UtilValidate.isNotEmpty(activeUomId) && !activeUomId.equals(uomId))){
							activeFacilityRate.set("rateAmount", rateAmount);
							activeFacilityRate.set("uomId", uomId);
							activeFacilityRate.store();
							createEntry = false;
						}
					}
					else{
						activeFacilityRate.set("thruDate", previousDayEnd);
						activeFacilityRate.store();
						createEntry = true;
					}
				}
				else{
					createEntry = true;
				}
				if(createEntry){
					GenericValue FacilityRate = delegator.makeValue("FacilityRate");
					FacilityRate.set("facilityId", facilityId);
					FacilityRate.set("rateAmount", rateAmount);
					FacilityRate.set("rateCurrencyUomId", "INR");
					FacilityRate.set("supplyTypeEnumId", supplyTypeEnumId);
					FacilityRate.set("productId", productId);
					FacilityRate.set("fromDate",dayBegin);
					FacilityRate.set("rateTypeId", rateTypeId);
					if(UtilValidate.isNotEmpty(uomId)){
						FacilityRate.set("uomId",uomId);
					}
					FacilityRate.create();
				}
			}catch(GenericEntityException e){
				Debug.logError("Error while updating Proc Facility Rate"+e.getMessage(), module);
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
			  List conditionList = FastList.newInstance();
			  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId)));
			  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"PROCUREMENT_ROLE")));
			  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
			  facilityParties = delegator.findList("FacilityFacilityPartyAndPerson",condition, null, null, null,false);
			  List<GenericValue> activeFacilityParties = (List<GenericValue>)EntityUtil.filterByDate(facilityParties,UtilDateTime.nowTimestamp()); 
			  activeFacilityParty = EntityUtil.getFirst((List<GenericValue>)EntityUtil.filterByDate(facilityParties,UtilDateTime.nowTimestamp()));
			  if(UtilValidate.isEmpty(activeFacilityParties)){
				  resultMap = ServiceUtil.returnError("No Facility is associated with this user ::"+userLogin.get("loginId"));
				  return resultMap;
			  }
			  
			  Map shedMap = FastMap.newInstance();
			  Map shedUnitDetailsMap = FastMap.newInstance();
			  for(GenericValue activeFacility : activeFacilityParties){
				  List unitIdsList = FastList.newInstance();
				  List unitMapsList = FastList.newInstance();
				  String shedId = null;
				  facilityId = (String)activeFacility.get("facilityId");
				  if("UNIT".equalsIgnoreCase(((String)activeFacility.get("facilityTypeId")))){
					  shedId = (String)activeFacility.get("parentFacilityId");
					  if(UtilValidate.isNotEmpty(shedMap) && UtilValidate.isNotEmpty(shedMap.get(shedId))){
						  unitIdsList = (List)shedMap.get(shedId);
						  unitMapsList = (List)shedUnitDetailsMap.get(shedId);
					  }
					  if(!unitIdsList.contains(facilityId)){
						  unitMapsList.add(activeFacility);
						  unitIdsList.add(facilityId);
					  }
				  }else if("SHED".equalsIgnoreCase(((String)activeFacility.get("facilityTypeId")))){
					  shedId = facilityId;
					  //unitIdsList.addAll((List)(getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",facilityId))).get("unitsList"));
					  //unitMapsList.addAll((List)(getShedUnitsByShed(dctx,UtilMisc.toMap("shedId",facilityId))).get("unitsDetailList"));
				  }
				  shedMap.put(shedId,unitIdsList);
				  shedUnitDetailsMap.put(shedId, unitMapsList);
			  }
			  if(UtilValidate.isNotEmpty(shedMap)){
				  Set keys = new HashSet(shedMap.keySet());
				  List keysList = FastList.newInstance();
				  keysList.addAll(keys);
				  Map shedInMap = FastMap.newInstance();
				  shedInMap.put("userLogin", userLogin);
				  shedInMap.put("facilityId", keysList.get(0));
				  Map shedDetails = getShedDetailsForFacility(dctx,shedInMap);
				  if(ServiceUtil.isError(shedDetails)){
					  resultMap = ServiceUtil.returnError("Error while getting shed Details");
					  return resultMap;
				  }
				  Map shedDetailsMap = (Map) shedDetails.get("facility");
				  resultMap.put("unitMapsList", shedUnitDetailsMap.get(keysList.get(0)));
				  resultMap.put("unitIdsList", shedMap.get(keysList.get(0)));
				  resultMap.put("shedDetails",shedDetailsMap);
				  
			  }
		  }catch(GenericEntityException e){
			  Debug.logError("Error while getting facilities associated with this user"+e.getMessage(), module);
			  resultMap = ServiceUtil.returnError("Error while getting facilities associated with this user ::"+userLogin.get("loginId"));
			  return resultMap;
		  }
		  return resultMap;
		  
	    }// end of the service
	  
	  
	  public static Map<String, Object> getFacilityProcurementReportConfig(DispatchContext dctx, Map context) {
		  GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		  LocalDispatcher dispatcher = dctx.getDispatcher();
		  Map resultMap = ServiceUtil.returnSuccess();
		  GenericValue userLogin = (GenericValue)context.get("userLogin");
		
		  String shedId = null;
		  Map userRelatedFacilities = ProcurementNetworkServices.getFacilityShedByUserLogin(dctx,UtilMisc.toMap("userLogin",userLogin));
		  if(UtilValidate.isNotEmpty(userRelatedFacilities)){
				Map unitDetails = (Map)userRelatedFacilities.get("unitDetails");
				Map shedDetails = (Map)userRelatedFacilities.get("shedDetails");
				/*if(UtilValidate.isNotEmpty(unitDetails)){
					context.put("unitId",unitDetails.get("facilityId"));
					context.put("unitName",unitDetails.get("facilityName"));
					context.put("unitCode",unitDetails.get("facilityCode"));
				}*/
				if(UtilValidate.isNotEmpty(shedDetails)){
					shedId =(String)shedDetails.get("facilityId");
				}
			
		}
		  List<GenericValue> facilityReportList = FastList.newInstance();
		  Map ReportConfigMap = FastMap.newInstance();
		 
		  try{
			  List conditionList = FastList.newInstance();
			  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,shedId)));
			  conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("moduleId",EntityOperator.EQUALS,"MILK_PROCUREMENT")));
			  EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityJoinOperator.AND);
			  facilityReportList = delegator.findList("FacilityWiseReportConfig",condition, null, null, null,false);
			  if(UtilValidate.isEmpty(facilityReportList)){
				  //resultMap = ServiceUtil.returnError("No Facility is associated with this user ::"+userLogin.get("loginId"));
				  return resultMap;
			  }
			  for(GenericValue facilityReport : facilityReportList){
				  ReportConfigMap.put(facilityReport.get("ReportId"), Boolean.TRUE);
				  if(UtilValidate.isNotEmpty(facilityReport.get("showInScreen")) && ((facilityReport.getString("showInScreen")).equalsIgnoreCase("N"))){
					  ReportConfigMap.put(facilityReport.get("ReportId"), Boolean.FALSE);
				  }
				 
			  }
			 
		  }catch(GenericEntityException e){
			  Debug.logError("Error while getting facilities associated with this user"+e.getMessage(), module);
			  resultMap = ServiceUtil.returnError("Error while getting facilities associated with this user ::"+userLogin.get("loginId"));
			  return resultMap;
		  }
		  resultMap.put("ReportConfigMap", ReportConfigMap);
		  return resultMap;
		  
	    }// end of the service
	  
	  
	  /**
	   * Service for Calculating opCost based on UomId
	   * @param totalSolids
	   * @param qty
	   * @param uomId
	   * @return opCost
	   */
	  public static BigDecimal calculateProcOPCost(DispatchContext dctx, Map context){
	    	BigDecimal opCost = BigDecimal.ZERO;
	    	BigDecimal totalSolids = BigDecimal.ZERO;
	    	BigDecimal opCostRate = BigDecimal.ZERO;
	    	BigDecimal qtyLtrs = BigDecimal.ZERO;
	    	String uomId = "VLIQ_L";
	    	if(UtilValidate.isNotEmpty(context.get("totalSolids"))){
	    		totalSolids = (BigDecimal) context.get("totalSolids");
	    	}
	    	if(UtilValidate.isNotEmpty(context.get("uomId"))){
	    		uomId = (String) context.get("uomId");
	    	}
	    	if(UtilValidate.isNotEmpty(context.get("opCostRate"))){
	    		opCostRate = (BigDecimal) context.get("opCostRate");
	    	}
	    	if(UtilValidate.isNotEmpty(context.get("qtyLtrs"))){
	    		qtyLtrs = (BigDecimal) context.get("qtyLtrs");
	    	}
	    	if(uomId.equalsIgnoreCase("VLIQ_TS")){
	    		opCost = opCost.add(opCostRate.multiply(totalSolids));
	    	}else{
	    		opCost = opCost.add(opCostRate.multiply(qtyLtrs));
	    	}
	    	return opCost;
	    }// end of the service
	  
	  public static Map<String, Object>  ProcurementSummarySms(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();		
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String shedId = (String) context.get("shedId");
	        String supplyTypeEnumId = (String) context.get("purchaseTime");
	        String procSmsDate = (String)context.get("procurementSmsDate");
	        Timestamp procurementSmsDate = null;
			if (UtilValidate.isNotEmpty(procSmsDate)) { 
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
				try {
					procurementSmsDate = new java.sql.Timestamp(sdf.parse(procSmsDate).getTime());
					procurementSmsDate = UtilDateTime.getDayStart(procurementSmsDate);
				} catch (ParseException e) {
					Debug.logError(e, "Cannot parse date string: "+ procSmsDate, module);
					return ServiceUtil.returnError("Error in parsoing procurement date");
				} catch (NullPointerException e) {
					Debug.logError(e, "Cannot parse date string: "+ procSmsDate, module);
					return ServiceUtil.returnError("Error in parsoing procurement date");
				}
			}
			else{
				Debug.logError("Procurement date is empty",module);
   	    		return ServiceUtil.returnError("Procurement date is empty");
			}
			Timestamp nowDateTime = UtilDateTime.getDayStart(procurementSmsDate);
			Timestamp dayEnd = UtilDateTime.getDayEnd(procurementSmsDate);
	        BigDecimal totalQtyKgs =BigDecimal.ZERO;
	        BigDecimal totalPrice = BigDecimal.ZERO;
	        String supplyTypeDes = null;
			Map<String,Object> procurementPeriodTotals = FastMap.newInstance();
			procurementPeriodTotals = ProcurementReports.getPeriodTotals(dctx , UtilMisc.toMap("fromDate", nowDateTime,"thruDate", dayEnd,"userLogin",userLogin,"facilityId", shedId,"supplyTypeEnumId", supplyTypeEnumId));
			if(!procurementPeriodTotals.isEmpty()){
				Map tempDayTotalsMap = (Map)((Map)procurementPeriodTotals.get(shedId)).get("dayTotals");
				totalQtyKgs = (BigDecimal)((Map)((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")).get("TOT")).get("totQtyKgs");
				totalPrice = (BigDecimal)((Map)((Map)((Map)tempDayTotalsMap.get("TOT")).get("TOT")).get("TOT")).get("totPrice");
			}
				if ("AM".equals(supplyTypeEnumId)){
					supplyTypeDes = "Morning";
				}else{
					supplyTypeDes = "Evening";
				}
			try {
				// Send SMS notification to list
				String todayDate = (UtilDateTime.toDateString(nowDateTime, "dd/MM/yyyy")).toString();
				String text = "Today's (" +todayDate+" "+supplyTypeDes+") Milk Procurement: TotalQtyKgs" +  
				totalQtyKgs.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				"  TotalPrice: " +
				totalPrice.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				"  Message sent by Vasista.";
				Debug.logInfo("Sms text: " + text, module);
				Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "PROC_NOTIFY_LST", 
					"text", text, "userLogin", userLogin);
				dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
			}
			catch (GenericServiceException e) {
				Debug.logError(e, "Error calling sendSmsToContactListNoCommEvent service", module);
				return ServiceUtil.returnError(e.getMessage());			
			} 
	        return ServiceUtil.returnSuccess("Sms successfully sent!");		
		}
	 
	  public static Map<String, Object>  ApProcurementSummarySms(DispatchContext dctx, Map<String, Object> context)  {
	        LocalDispatcher dispatcher = dctx.getDispatcher();	
	        Delegator delegator = dctx.getDelegator();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String userLoginPartyId = userLogin.getString("partyId");
	        String shedId = (String) context.get("shedId");
	        String customTimePeriodId = (String)context.get("customTimePeriodId");
	        BigDecimal bmTotLtrs = (BigDecimal)context.get("bmTotLtrs");
	        BigDecimal cmTotLtrs = (BigDecimal)context.get("cmTotLtrs");
	        BigDecimal netAmountPayable = (BigDecimal)context.get("netAmountPayable");
	        String partyId = null;
	        String fromDate = null;
	        String thruDate = null;
	        Map<String, Object> serviceResult;
	        Map<String, Object> userServiceResult;
	        String countryCode = "91";
	        String contactNumberTo = null;
	        try {
		        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId),false);
	        	if (UtilValidate.isNotEmpty(customTimePeriod)) {
	        		Timestamp fromDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
	        		Timestamp thruDateTime=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));
				    fromDate = (UtilDateTime.toDateString(fromDateTime, "MMMdd")).toString();
				    thruDate = (UtilDateTime.toDateString(thruDateTime, "MMMdd yyyy")).toString();
	        	}
	        	
	        	List condList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,shedId));
				condList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DD_ROLE"));
				EntityCondition condition = EntityCondition.makeCondition(condList,EntityJoinOperator.AND);
				List facilityPartyList = delegator.findList("FacilityParty", condition, null, null, null, false);
				partyId = (EntityUtil.getFirst(facilityPartyList)).getString("partyId");
				
	        }catch (GenericEntityException e) {
               Debug.logError(e, module);
               return ServiceUtil.returnError(e.getMessage());
			}
			try {
				// Send SMS notification to list
				String text = "("+shedId+") Milk Procurement Bill Process for Period(" +fromDate+"-"+thruDate+") : Total Buffalo Milk Litres: " +  
				bmTotLtrs.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				"  Total Cow Milk Litres:: " +
				cmTotLtrs.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) +
				" Net Amount Payable: Rs: " +  
				netAmountPayable.setScale(1, UtilNumber.getBigDecimalRoundingMode("order.rounding")) + 
				"  Msg sent by MIS";
				Debug.logInfo("Sms text: " + text, module);
				Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "PROC_NOTIFY_LST", 
					"text", text, "userLogin", userLogin);
				dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
				
				//Sms to DD in Procurement
				Map<String, Object> getTelParams = FastMap.newInstance();
	            getTelParams.put("partyId", partyId);
	            getTelParams.put("userLogin", userLogin);
	            serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
	            if (ServiceUtil.isError(serviceResult)) {
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
	            if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
	            	countryCode = (String)serviceResult.get("countryCode");
	            }
	            contactNumberTo =  countryCode + (String) serviceResult.get("contactNumber"); 
	            Map<String, Object> sendSmsParams = FastMap.newInstance();  
	            sendSmsParams.put("contactNumberTo", contactNumberTo);          
	            sendSmsParams.put("text", text); 
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	 serviceResult  = dispatcher.runSync("sendSms", sendSmsParams); 
	            }
	            //Sms to userLogin in Procurement
				Map<String, Object> getUserTelParams = FastMap.newInstance();
				getUserTelParams.put("partyId", userLoginPartyId);
				getUserTelParams.put("userLogin", userLogin);
	            userServiceResult = dispatcher.runSync("getPartyTelephone", getUserTelParams);
	            if (ServiceUtil.isError(userServiceResult)) {
	            	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
	            } 
	            if(UtilValidate.isNotEmpty(userServiceResult.get("countryCode"))){
	            	countryCode = (String)userServiceResult.get("countryCode");
	            }
	            contactNumberTo =  countryCode + (String) userServiceResult.get("contactNumber"); 
	            Map<String, Object> sendUserSmsParams = FastMap.newInstance();  
	            sendUserSmsParams.put("contactNumberTo", contactNumberTo);          
	            sendUserSmsParams.put("text", text); 
	            if(UtilValidate.isNotEmpty(contactNumberTo)){
	            	userServiceResult  = dispatcher.runSync("sendSms", sendUserSmsParams);
	            }
			}
			catch (GenericServiceException e) {
				Debug.logError(e, "Error calling sendSmsToContactListNoCommEvent service", module);
				return ServiceUtil.returnError(e.getMessage());			
			} 
			return ServiceUtil.returnSuccess("Sms successfully sent!");
	 }
	  
	  public static List<GenericValue> getSortedFacilities(List<GenericValue> facilities ) {
	    	List<String> units = FastList.newInstance();
	    	List<GenericValue> facilityCodeList = FastList.newInstance();
	    	List<GenericValue> facilityCodeSortedList = FastList.newInstance();
            boolean sortFlag = true;
            if(UtilValidate.isNotEmpty(facilities)){ 
            	facilities = EntityUtil.orderBy(facilities, UtilMisc.toList("facilityCode"));
            	for(GenericValue facility : facilities){
            		if(UtilValidate.isNotEmpty(facility.getString("facilityCode"))){
            			Pattern pAlp = Pattern.compile("[a-zA-Z]");
            			Matcher mAlp = pAlp.matcher(facility.getString("facilityCode"));
            			if (!mAlp.find()){
            			    //handle sorting here
            				facility.set("facilityCode", Integer.parseInt((facility.getString("facilityCode"))));
	            			facilityCodeList.add(facility);
            			}else{
            				sortFlag = false;
            				facilityCodeList.add(facility);
            			}
            		}
            	}
            	if(sortFlag){
            		 facilityCodeList = EntityUtil.orderBy(facilityCodeList, UtilMisc.toList("facilityCode"));
            		 for(GenericValue facility : facilityCodeList){
         	    		facility.set("facilityCode", (facility.getInteger("facilityCode")).toString());
         	    		facilityCodeSortedList.add(facility);
         	    	}
            	 }else{
            		 facilityCodeSortedList = facilityCodeList;
            	 }
            }
	     return facilityCodeSortedList;
	   }
	 
	  /*
	   * Note:  This method assumes that there this is only one active fin account per center.
	   */
	 public static Map<String, Object> getUnitBillsAbstract(DispatchContext ctx, Map<String, ? extends Object> context) {
		    Delegator delegator = ctx.getDelegator();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String unitId = (String) context.get("unitId");
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        Map centerWiseAbsMap = FastMap.newInstance();
	        ModelReader reader = delegator.getModelReader();
	        EntityListIterator abstractItemsIterator = null;
	        Map result =ServiceUtil.returnSuccess();
	        try{
	        	List condList = FastList.newInstance();
				List periodBillingList = FastList.newInstance();
				condList.add(EntityCondition.makeCondition("customTimePeriodId",EntityOperator.EQUALS,customTimePeriodId));
				condList.add(EntityCondition.makeCondition("billingTypeId",EntityOperator.EQUALS,"PB_PROC_MRGN"));
				condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"GENERATED"));
				condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,unitId));
				EntityCondition procConditon = EntityCondition.makeCondition(condList,EntityJoinOperator.AND);
				periodBillingList = delegator.findList("CustomTimePeriodAndBilling", procConditon, null, null, null, false);
				
				if (UtilValidate.isEmpty(periodBillingList)) {
					Debug.logError("invalid period billing. ", module);
					result.put("centerWiseAbsMap", centerWiseAbsMap);
					return result;
				}
			   GenericValue	periodBilling = EntityUtil.getFirst(periodBillingList);
			   String  periodBillingId = periodBilling.getString("periodBillingId");
			   //Set<String> fieldToSelect = UtilMisc.toSet("facilityId","productId","grossAmt","qtyKgs","qtyLtrs");
			   //List<GenericValue> abstractEntryList = delegator.findList("ProcurementAbstract", EntityCondition.makeCondition("periodBillingId",EntityJoinOperator.EQUALS ,periodBillingId), null, null, null, false);
			   abstractItemsIterator = delegator.find("ProcurementAbstract", EntityCondition.makeCondition("periodBillingId",EntityJoinOperator.EQUALS ,periodBillingId), null, null, null, null);
			   Map orderAdjustmentTypeMapping = FastMap.newInstance();
			   //here we are querying OrderAdjustmentTypeProcAbstractMapping
				List<GenericValue> orderAdjumentMapList = FastList.newInstance();
				orderAdjumentMapList = delegator.findList("OrderAdjustmentTypeAndProcAbstractMapping", null, null, null, null, false);
				if(UtilValidate.isNotEmpty(orderAdjumentMapList)){
					for(GenericValue orderAdjustmentMap : orderAdjumentMapList){
						orderAdjustmentTypeMapping.put(orderAdjustmentMap.get("procAbstractFieldName"), UtilMisc.toMap("id",(String)orderAdjustmentMap.get("orderAdjustmentTypeId"),"parentTypeId",orderAdjustmentMap.get("parentTypeId")));
					}
					
				}
			    GenericValue absEntry =null;
			    while( abstractItemsIterator != null && (absEntry = abstractItemsIterator.next()) != null) {
	        	//for(GenericValue absEntry : abstractEntryList){
	        		
	        		String facilityId = absEntry.getString("facilityId");
	        		String productId = absEntry.getString("productId");
	        		Map tempCenterWiseMap = FastMap.newInstance();
	        		Map tempProductWiseMap = FastMap.newInstance();
	        		Map tempProductWiseTotMap = FastMap.newInstance();
	        		Map tempTotProductWiseMap = FastMap.newInstance();
	        		Map tempTotProductMap = FastMap.newInstance();
	        		Map tempTotMap = FastMap.newInstance();
	        		
	        		if(UtilValidate.isNotEmpty(centerWiseAbsMap.get(facilityId))){
	        			tempCenterWiseMap = (Map)centerWiseAbsMap.get(facilityId);
	        		}
	        		if(UtilValidate.isNotEmpty(tempCenterWiseMap.get(productId))){
	        			tempProductWiseMap = (Map)tempCenterWiseMap.get(productId);
	        			
	        		}
	        		
	        		if(UtilValidate.isNotEmpty(tempCenterWiseMap.get("TOT"))){
	        			tempProductWiseTotMap = (Map)tempCenterWiseMap.get("TOT");
	        		}
	        		//totals map
	        		if(UtilValidate.isNotEmpty(centerWiseAbsMap.get("TOT"))){
	        			tempTotMap = (Map)centerWiseAbsMap.get("TOT");
	        		}
	        		if(UtilValidate.isNotEmpty(tempTotMap.get(productId))){
	        			tempTotProductWiseMap = (Map)tempTotMap.get(productId);
	        			
	        		}
	        		
	        		if(UtilValidate.isNotEmpty(tempTotMap.get("TOT"))){
	        			tempTotProductMap = (Map)tempTotMap.get("TOT");
	        			
	        		}
	        		Iterator absEntryItr = absEntry.entrySet().iterator();
	        		while (absEntryItr.hasNext()) {
						Map.Entry absItrEntry = (Entry) absEntryItr.next();
						String keyField = (String) absItrEntry.getKey();
						Object valueField = absItrEntry.getValue();
						
						ModelEntity entity = reader.getModelEntity("ProcurementAbstract");
						 ModelField field = entity.getField(keyField);
						ModelFieldType type = delegator.getEntityFieldType(entity, field.getType());
						/*Debug.log("keyField===="+keyField);
						Debug.log("field type===="+type.getJavaType());*/
						if((type.getJavaType()).equals("java.math.BigDecimal") && UtilValidate.isEmpty(valueField)){
							valueField = BigDecimal.ZERO;
						}
						String adjParentTypeId = null;
						 if(UtilValidate.isNotEmpty(orderAdjustmentTypeMapping.get(keyField))){
							 adjParentTypeId = (String)((Map)orderAdjustmentTypeMapping.get(keyField)).get("parentTypeId");
							 keyField = (String)((Map)orderAdjustmentTypeMapping.get(keyField)).get("id");
						 }
						 if ((UtilValidate.isNotEmpty(valueField)) && (valueField instanceof BigDecimal)) {
							// tempProductWiseMap.put(keyField, (absEntry.getBigDecimal(keyField)).add((BigDecimal)valueField));
							 BigDecimal valueFieldBigDecimal = (BigDecimal)valueField;
							 if(UtilValidate.isEmpty(tempProductWiseMap.get(keyField))){
								 tempProductWiseMap.put(keyField,BigDecimal.ZERO);
								 
							 }
							 
							 if(UtilValidate.isEmpty(tempProductWiseTotMap.get(keyField))){
								 tempProductWiseTotMap.put(keyField,BigDecimal.ZERO);
								
							 }
							 if(UtilValidate.isEmpty(tempTotProductWiseMap.get(keyField))){
								 tempTotProductWiseMap.put(keyField,BigDecimal.ZERO);
								
							 }
							 if(UtilValidate.isEmpty(tempTotProductMap.get(keyField))){
								 tempTotProductMap.put(keyField,BigDecimal.ZERO);
							 }
						    tempProductWiseMap.put(keyField, ((BigDecimal)tempProductWiseMap.get(keyField)).add(valueFieldBigDecimal));
							tempProductWiseTotMap.put(keyField, ((BigDecimal)tempProductWiseTotMap.get(keyField)).add(valueFieldBigDecimal));
						    tempTotProductWiseMap.put(keyField, ((BigDecimal)tempTotProductWiseMap.get(keyField)).add(valueFieldBigDecimal));
							tempTotProductMap.put(keyField, ((BigDecimal)tempTotProductMap.get(keyField)).add(valueFieldBigDecimal));
							
						/*	//lets populate additions and deductions totals
							 if(UtilValidate.isNotEmpty(adjParentTypeId)){
								 if(adjParentTypeId.equals("MILKPROC_DEDUCTIONS")){
									 tempProductWiseMap.put("TOTDED", ((BigDecimal)tempProductWiseMap.get("TOTDED")).add(valueFieldBigDecimal));
									 tempProductWiseTotMap.put("TOTDED", ((BigDecimal)tempProductWiseTotMap.get("TOTDED")).add(valueFieldBigDecimal));
									 tempTotProductWiseMap.put("TOTDED", ((BigDecimal)tempTotProductWiseMap.get("TOTDED")).add(valueFieldBigDecimal));
									 tempTotProductMap.put("TOTDED", ((BigDecimal)tempTotProductMap.get("TOTDED")).add(valueFieldBigDecimal));
								 }
								 if(adjParentTypeId.equals("MILKPROC_ADDITIONS")){
									 tempProductWiseMap.put("TOTADD", ((BigDecimal)tempProductWiseMap.get("TOTADD")).add(valueFieldBigDecimal));
									 tempProductWiseTotMap.put("TOTADD", ((BigDecimal)tempProductWiseTotMap.get("TOTADD")).add(valueFieldBigDecimal));
									 tempTotProductWiseMap.put("TOTADD", ((BigDecimal)tempTotProductWiseMap.get("TOTADD")).add(valueFieldBigDecimal));
									 tempTotProductMap.put("TOTADD", ((BigDecimal)tempTotProductMap.get("TOTADD")).add(valueFieldBigDecimal));
								 }
								 
							 }//end of additions and deductions map
*/							 
						 }else{
							    tempProductWiseMap.put(keyField, valueField);
								tempProductWiseTotMap.put(keyField, valueField);
							    tempTotProductWiseMap.put(keyField, valueField);
								tempTotProductMap.put(keyField,valueField);
						 }
						 
						 
					}// end while
	        		
	        		
	        		
	        		tempCenterWiseMap.put(productId,tempProductWiseMap);
	        		tempCenterWiseMap.put("TOT", tempProductWiseTotMap);
	        		centerWiseAbsMap.put(facilityId, tempCenterWiseMap);
	        		tempTotProductWiseMap.put("TOT" ,tempTotProductMap);
	        		tempTotMap.put(productId,tempTotProductWiseMap);
	        		tempTotMap.put("TOT",tempTotProductMap);
	        		centerWiseAbsMap.put("TOT", tempTotMap);
	        		
	        	}//end of abstractEntryList
	        	
	        	if (abstractItemsIterator != null) {
    	            try {
    	            	abstractItemsIterator.close();
    	            } catch (GenericEntityException e) {
    	                Debug.logWarning(e, module);
    	            }
    	        }
	        	
	        }catch (Exception e) {
				// TODO: handle exception
	        	Debug.logError(e.toString(), module);
	        	return ServiceUtil.returnError(e.toString());
			}
	       result.put("centerWiseAbsMap", centerWiseAbsMap);
		  return result;
	  
	 }
	 
	 /**
	  * This service returns PeriodBillingId,CustomTimePeriod for Unit/Shed
	  * @param unit/shed/center/route
	  * @return PeriodBillingId List
	  * 
	  */
	  public static Map<String, Object> getProcFacilityBillingPeriods(DispatchContext ctx, Map<String, ? extends Object> context) {
		    Delegator delegator = ctx.getDelegator();
		    GenericValue userLogin = (GenericValue) context.get("userLogin");
	        String facilityId = (String) context.get("facilityId");
	        Timestamp fromDate = (Timestamp) context.get("fromDate");
	        Timestamp thruDate = (Timestamp) context.get("thruDate");
	        String customTimePeriodId = (String) context.get("customTimePeriodId");
	        List customTimePeriodIdsList = FastList.newInstance();
	        List<String> facilityIdsList = FastList.newInstance();
	        Map result =ServiceUtil.returnSuccess();
	        if(UtilValidate.isEmpty(facilityId)){
	        	Debug.logError("FacilityId Should not be empty",module);
	        	return ServiceUtil.returnError("FacilityId Should not be empty");
	        }
	        if(UtilValidate.isEmpty(fromDate)&&UtilValidate.isEmpty(thruDate)&&UtilValidate.isEmpty(customTimePeriodId)){
	        	Debug.logError("FromDate,ThruDate or customTimePeriodId is missing",module);
	        	return ServiceUtil.returnError("FromDate,ThruDate or customTimePeriodId is missing");
	        }
	        if((UtilValidate.isEmpty(fromDate)&&UtilValidate.isEmpty(thruDate))&&UtilValidate.isNotEmpty(customTimePeriodId)){
	        	Debug.logError("FromDate or ThruDate is missing",module);
	        	return ServiceUtil.returnError("FromDate or ThruDate is missing");
	        }
	        try{
	        	GenericValue facilityDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId),false);
	        	if(UtilValidate.isEmpty(facilityDetails)){
	        		Debug.logError("Facility Not Found with Id"+facilityId,module);
		        	return ServiceUtil.returnError("Facility Not Found");
	        	}
	        	String facilityTypeId = (String)facilityDetails.get("facilityTypeId");
	        	if(facilityTypeId.equalsIgnoreCase("SHED")){
	        		facilityIdsList.addAll((List)(getShedUnitsByShed(ctx,UtilMisc.toMap("shedId",facilityId))).get("unitsList"));
	        	}else if(facilityTypeId.equalsIgnoreCase("CENTER")){
	        		Map getCenterDetailsMap = FastMap.newInstance();
	        		getCenterDetailsMap.put("centerId",facilityId);
	        		getCenterDetailsMap.put("userLogin",userLogin);
	        		Map CenterDtails = getCenterDtails(ctx,getCenterDetailsMap);
	        		GenericValue unitDetails = null;
	        		if(ServiceUtil.isSuccess(CenterDtails)){
						unitDetails = (GenericValue) CenterDtails.get("unitFacility");
					}
					if(UtilValidate.isEmpty(unitDetails)){
						Debug.logError("unit Details not Found. ", module);
						return ServiceUtil.returnError("Unit details Not Found .");
					}
					facilityIdsList.add((String)unitDetails.getString("facilityId"));
	        		
	        	}else if(facilityTypeId.equalsIgnoreCase("PROC_ROUTE")){
	        		GenericValue unitDetails = null;
	        		GenericValue routeDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId",facilityId), false);
	        		unitDetails = delegator.findOne("Facility", UtilMisc.toMap("facilityId",(String)routeDetails.get("facilityId")), false);
	        		facilityIdsList.add((String)unitDetails.getString("facilityId"));
	        	}else{
	        		facilityIdsList.add(facilityId);
	        	}
	        	List conditionList =FastList.newInstance();
	        	if(UtilValidate.isEmpty(customTimePeriodId)){
	        		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(fromDate.getTime())));
	    			conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(thruDate.getTime())));
	    			conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"));
	    			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,facilityIdsList));
	    			EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	    			List<GenericValue> customTimePeriodList = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",condition,null,null, null,false);
	    			if(UtilValidate.isEmpty(customTimePeriodList)){
	    				Debug.logError("customTimePeriods Not Found",module);
			        	return ServiceUtil.returnError("customTimePeriods Not Found");
	    			}
	    			customTimePeriodIdsList.addAll(EntityUtil.getFieldListFromEntityList(customTimePeriodList, "customTimePeriodId", false));
	        	}else{
	        		customTimePeriodIdsList.add(customTimePeriodId);
	        	}
	        	
	        	// we are trying to get PeriodBillingIds
	        	conditionList.clear();
	        	List<GenericValue> periodBillingList = FastList.newInstance();
			   	List periodBillingConditionList = FastList.newInstance();
			   	conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, customTimePeriodIdsList));
			   	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "GENERATED"));
			   	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, facilityIdsList));
			   	EntityCondition periodBillingCondition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			   	periodBillingList = delegator.findList("PeriodBilling",periodBillingCondition,null,null , null,false);
			   	List periodBillingIdsList = FastList.newInstance();
			   	periodBillingIdsList.addAll(EntityUtil.getFieldListFromEntityList(periodBillingList, "periodBillingId", false));
			   	result.put("periodBillingIdsList", periodBillingIdsList);
	        }catch (Exception e) {
				// TODO: handle exception
	        	Debug.logError("Error while getting PeriodBilling Ids"+e,module );
	        	return ServiceUtil.returnError("Error while getting PeriodBilling Ids :"+e.getMessage());
			}
	        return result;
	 }//End of Service 
	  
		  public static Map<String, Object> checkCustomTimePeriod(DispatchContext dctx, Map<String, Object> context) 
		  throws GenericEntityException {
		    Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			String unitId = (String)context.get("unitId");
			String unitCode = (String)context.get("unitCode");
			String shedCode = (String)context.get("shedCode");
			String shedId = (String)context.get("shedId");
			String customTimePeriodId = (String)context.get("customTimePeriodId");
			Timestamp orderDate = (Timestamp) context.get("orderDate");
			
			List<GenericValue> customTimePeriodFacilities = FastList.newInstance();
			List<GenericValue> facilities = FastList.newInstance();
			List<GenericValue> tempCustomTimePeriodList = FastList.newInstance();
			GenericValue tempCustomTimePeriod = null;
			try {
				if(UtilValidate.isEmpty(customTimePeriodId)){
					List conditionList = FastList.newInstance();
			  		conditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS,"PROC_BILL_MONTH"));
			  		if(UtilValidate.isNotEmpty(orderDate)){
						conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(orderDate.getTime())));
			     		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(orderDate.getTime())));
			  		}
			  		EntityCondition  condition =  EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					tempCustomTimePeriodList = delegator.findList("CustomTimePeriod",condition ,null,null, null,false);
					if(UtilValidate.isNotEmpty(tempCustomTimePeriodList)){
						tempCustomTimePeriod = EntityUtil.getFirst(tempCustomTimePeriodList);	
						if(UtilValidate.isNotEmpty(tempCustomTimePeriod)){
							customTimePeriodId = tempCustomTimePeriod.getString("customTimePeriodId");
						}
					}
				}
				if(UtilValidate.isEmpty(customTimePeriodId)){
					Debug.logError("customTimePeriodId Not Found "+customTimePeriodId,module);
	           		return ServiceUtil.returnError("customTimePeriodId is Empty==>"+customTimePeriodId);  
	        	}
				if((UtilValidate.isNotEmpty(shedCode))&&(UtilValidate.isEmpty(shedId))){
					facilities = delegator.findList("Facility", EntityCondition.makeCondition(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SHED"),EntityOperator.AND,EntityCondition.makeCondition("facilityCode", EntityOperator.EQUALS,shedCode) ), null, UtilMisc.toList("facilityId"), null, false);
					GenericValue shedDetail = EntityUtil.getFirst(facilities);
					if(UtilValidate.isEmpty(shedDetail)){
						Debug.logError("Invalid Shed code"+shedCode, module);
			            return ServiceUtil.returnError("Invalid Shed code"+shedCode);
					}
		    		shedId = shedDetail.getString("facilityId");
				}
				if(UtilValidate.isNotEmpty(unitCode)){
					 List<EntityCondition> condList = FastList.newInstance();  
					 condList.add(EntityCondition.makeCondition("parentFacilityId", shedId));
					 condList.add(EntityCondition.makeCondition("facilityTypeId", "UNIT"));	    		 
		    		 condList.add(EntityCondition.makeCondition("facilityCode", unitCode));
		    		 List unitList = delegator.findList("Facility", EntityCondition.makeCondition(condList, EntityOperator.AND), UtilMisc.toSet("facilityId"), null, null, true);
		    		 GenericValue unitDetail =  EntityUtil.getFirst(unitList);
		        	 if(UtilValidate.isEmpty(unitDetail)){
		        		 Debug.logError("Unit  not found with the code==>"+unitCode, module);
		           	     return ServiceUtil.returnError("Unit  not found with the code==>"+unitCode);  
		        	 }
		        	 unitId = unitDetail.getString("facilityId");
				}
				if(UtilValidate.isNotEmpty(customTimePeriodId)){
					List customTimePeriodConditionList = FastList.newInstance();
					if(UtilValidate.isNotEmpty(unitId)){
						customTimePeriodConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, unitId));
					}else{
						customTimePeriodConditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, shedId));
					}
					customTimePeriodConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, customTimePeriodId));
					customTimePeriodConditionList.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "PROC_BILL_MONTH"));
					customTimePeriodConditionList.add(EntityCondition.makeCondition("fctpIsClosed", EntityOperator.EQUALS, "Y"));
				   	EntityCondition customTimePeriodCondition = EntityCondition.makeCondition(customTimePeriodConditionList,EntityOperator.AND);
				   	customTimePeriodFacilities = delegator.findList("CustomTimePeriodAndFacilityCustomTimePeriod",customTimePeriodCondition,null,null , null,false);
				   	if (UtilValidate.isNotEmpty(customTimePeriodFacilities)) {
				   	     Debug.logError("Failed To Process--> Billing Already Generated:==>"+unitId+" "+customTimePeriodId,module);
				   	     return ServiceUtil.returnError("Failed To Process--> Billing Already Generated::");	
		            }
				}
			} catch (GenericEntityException e) {
		        String errMsg = "Failed To Process" + e.getMessage();
		        Debug.logError(e, errMsg, module);
		        return ServiceUtil.returnError(errMsg);
		    }
		    return ServiceUtil.returnSuccess();
		}
		  public static Map<String, Object>  closeFacilityProcurementTimePeriod(DispatchContext dctx, Map<String, ? extends Object> context)  {
		    	GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
				LocalDispatcher dispatcher = dctx.getDispatcher();
				Map<String, Object> result = FastMap.newInstance();	
				String customTimePeriodId = (String) context.get("customTimePeriodId");
				String facilityId = (String) context.get("facilityId");
				List<GenericValue> tempFacilityCustomTimePeriodList = FastList.newInstance();
				try{
					if(UtilValidate.isNotEmpty(customTimePeriodId)){
						List conditionList = FastList.newInstance();
				  		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facilityId));
				  		conditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS,customTimePeriodId));
				  		EntityCondition  condition =  EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				  		tempFacilityCustomTimePeriodList = delegator.findList("FacilityCustomTimePeriod",condition ,null,null, null,false);
						if(UtilValidate.isNotEmpty(tempFacilityCustomTimePeriodList)){
							GenericValue facilityCustomTimePeriod =  EntityUtil.getFirst(tempFacilityCustomTimePeriodList);
							facilityCustomTimePeriod.set("isClosed", "Y");
							facilityCustomTimePeriod.store();
						}
					}	
				}catch(GenericEntityException e){
					Debug.logError("error while closing Facility CustomTimePeriod"+e.getMessage(), module);
					result = ServiceUtil.returnError("Error while closing Facility CustomTimePeriod"); 
				}
				result = ServiceUtil.returnSuccess("Facility CustomTimePeriod Closed Successfully");
				return result;
		}// end of service
}
