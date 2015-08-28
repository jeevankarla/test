import java.sql.*
import java.text.SimpleDateFormat;

import javolution.util.FastList;
import javolution.util.FastMap;
import java.math.BigDecimal;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.util.EntityUtil;

facilityId = parameters.facilityId;
productId = parameters.productId;

incommingTransfer = [];
outgoingTransfer = [];
if(facilityId){
	facilityId = facilityId.toUpperCase();
	conditionList = [];
	conditionList.add(EntityCondition.makeCondition("toFacilityId", EntityOperator.EQUALS, facilityId));
	if(productId){
		productId = productId.toUpperCase();
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_EN_ROUTE"));
	condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	incommingTransferDetails = delegator.findList("InventoryTransferGroupAndMemberSum", condition, null, null, null, false);
	
	if(incommingTransferDetails){
		incommingTransferDetails.each {eachIncommingTransfer->
			inventoryTransMap=[:];
			inventoryTransMap.put("transferGroupId", eachIncommingTransfer.transferGroupId);
			inventoryTransMap.put("xferQtySum", eachIncommingTransfer.xferQtySum);
			inventoryTransMap.put("fromFacilityId", eachIncommingTransfer.fromFacilityId);
			inventoryTransMap.put("toFacilityId", eachIncommingTransfer.toFacilityId);
			inventoryTransMap.put("statusId", eachIncommingTransfer.statusId);
			inventoryTransMap.put("productId", eachIncommingTransfer.productId);

			productCategoryIds=null;productTestCategory=[];productTestProdData=[];
			productCategoryMember = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , eachIncommingTransfer.productId)  , null, null, null, false );
			if(UtilValidate.isNotEmpty(productCategoryMember)){
				productCategoryIds=EntityUtil.getFieldListFromEntityList(productCategoryMember, "productCategoryId", true);
			 }
			if(UtilValidate.isNotEmpty(productCategoryIds)){
				productTestCategory = delegator.findList("ProductTestComponent",EntityCondition.makeCondition("productCategoryId", EntityOperator.IN , productCategoryIds)  , null, null, null, false );
			 }
			if(UtilValidate.isNotEmpty(eachIncommingTransfer.productId)){
				productTestProdData = delegator.findList("ProductTestComponent",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , eachIncommingTransfer.productId)  , null, null, null, false );
			 }
			
			if(UtilValidate.isNotEmpty(productTestProdData) || UtilValidate.isNotEmpty(productTestCategory)){
					productQcTest = delegator.findList("ProductQcTest", EntityCondition.makeCondition([transferGroupId : eachIncommingTransfer.transferGroupId]), null, null, null, false);
					if(UtilValidate.isNotEmpty(productQcTest)){
						 productQcTest = EntityUtil.getFirst(productQcTest);
						 statusId=productQcTest.statusId;
						 inventoryTransMap.put("qcStatusId",statusId);
					}else{
						inventoryTransMap.put("qcStatusId","QC_NOT_ACCEPT");
					}
					incommingTransfer.add(inventoryTransMap);
			 }else{
				inventoryTransMap.put("qcStatusId","QC_ACCEPT");
				incommingTransfer.add(inventoryTransMap);
			 }
			
		
		}
	}

	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("fromFacilityId", EntityOperator.EQUALS, facilityId));
	if(productId){
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	}
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_REQUESTED"));
	cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	outgoingTransfer = delegator.findList("InventoryTransferGroupAndMemberSum", cond, null, null, null, false);
	
}
context.incommingTransfer = incommingTransfer;
context.outgoingTransfer = outgoingTransfer;