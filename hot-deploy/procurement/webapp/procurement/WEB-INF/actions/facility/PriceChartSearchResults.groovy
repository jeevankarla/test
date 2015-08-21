
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;



	procPriceChartId = parameters.procPriceChartId;
	procurementPriceTypeId = parameters.procurementPriceTypeId;
	productId = parameters.productId;
	regionId = parameters.regionId;
	
	
	conditionList =[];
	if(procPriceChartId){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, procPriceChartId)));
	}
	if(procurementPriceTypeId){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("procurementPriceTypeId", EntityOperator.EQUALS, procurementPriceTypeId)));
	}
	if(regionId){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("regionId", EntityOperator.EQUALS, regionId)));
	}
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	procurementPriceChartList = delegator.findList("ProcurementPriceChart",condition,null,null,null,false);
	procPriceChartIdsList = EntityUtil.getFieldListFromEntityList(procurementPriceChartList, "procPriceChartId", true);
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("procPriceChartId", EntityOperator.IN, procPriceChartIdsList)));
	if(productId){
		conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)));
	}
	condition2 = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	procurementPriceList = delegator.findList("ProcurementPrice",condition2,null,null,null,false);
	
	p1 = [];
	p2 = [];
	
	procPriceChartList = [];
	for(i=0; i< procurementPriceList.size(); i++){
		
		procurementPrice = procurementPriceList[i];
		procPriceChartId = procurementPrice.procPriceChartId;
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("procPriceChartId", EntityOperator.EQUALS, procPriceChartId));
		condition3=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		filteredProcurementPriceList = EntityUtil.filterByCondition(procurementPriceChartList, condition3);
		
		productId = procurementPrice.productId;
		procPriceChartId = procurementPrice.procPriceChartId;
		
		tmpMap = [:];
		
			if( (!(p1.contains(productId))   || ( !  (p2.contains(procPriceChartId) ) ) )     ){
				
				p1.add(productId);
				p2.add(procPriceChartId);
				
				tmpMap.put("procPriceChartId", procurementPrice.procPriceChartId);
				tmpMap.put("productId", procurementPrice.productId);
				if(filteredProcurementPriceList){
					tmpMap.put("regionId", filteredProcurementPriceList[0].regionId);
					tmpMap.put("partyId", filteredProcurementPriceList[0].partyId);
					tmpMap.put("useBaseSnf", filteredProcurementPriceList[0].useBaseSnf);
					tmpMap.put("useBaseFat", filteredProcurementPriceList[0].useBaseFat);
					tmpMap.put("fromDate", filteredProcurementPriceList[0].fromDate);
					tmpMap.put("thruDate", filteredProcurementPriceList[0].thruDate);
				}else{
					tmpMap.put("regionId", "");
					tmpMap.put("partyId", "");
					tmpMap.put("useBaseSnf", "");
					tmpMap.put("useBaseFat", "");
					tmpMap.put("fromDate", "");
					tmpMap.put("thruDate", "");
				
				}
				temp = [:];
				temp.putAll(tmpMap);
				procPriceChartList.add(temp);
				
			}
		}
	
		context.procPriceChartList = procPriceChartList;
	
	
	
	                          
	
	
	
