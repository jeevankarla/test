import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import in.vasista.vbiz.production.ProductionServices;
// deploy/production/src/in/vasista/vbiz/production/ProductionServices.java
fromDate=parameters.fromDate;

dctx = dispatcher.getDispatchContext();
DateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	DateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	//Debug.log("fromDateTime==========================="+fromDateTime);
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(DateTime);
dayEnd = UtilDateTime.getDayEnd(DateTime);
context.fromDate = dayBegin;
allDetailsRegisterMap=[:];
workEffortList=[];

siloFacilitiList = delegator.findList("Facility",EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS , "RAWMILK")  , null, null, null, false );
siloIds=EntityUtil.getFieldListFromEntityList(siloFacilitiList, "facilityId", true);

if(UtilValidate.isNotEmpty(siloIds)){
   conditionList =[];
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds ));
   EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
   InventoryItemAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);

   workEffortIds=EntityUtil.getFieldListFromEntityList(InventoryItemAndDetailList, "workEffortId", true);
   if(UtilValidate.isNotEmpty(workEffortIds)){
     workEffortList = delegator.findList("WorkEffort",EntityCondition.makeCondition("workEffortId", EntityOperator.IN , workEffortIds)  , null, null, null, false );
     //issuSiloIds=EntityUtil.getFieldListFromEntityList(workEffortList, "facilityId", true);
   }

   conditionList.clear();
   conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
   conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["MXF_RECD","MXF_APPROVED"]));
   EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
   milkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", cond, null,null, null, false);

   if(UtilValidate.isNotEmpty(siloIds)){
	  siloIds.each {eachSiloId->
		allDetailsMap=[:];
		closingBalSiloMap=[:];
		openingBalSiloMap=[:];
		receiptSiloMap=[:];
		IssuedSiloMap=[:];
		
		BigDecimal totInventoryQty = BigDecimal.ZERO;
		BigDecimal totFatQty = BigDecimal.ZERO;
		BigDecimal totSnfQty = BigDecimal.ZERO;
		
		BigDecimal openingQty = BigDecimal.ZERO;
		
		invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:dayBegin, facilityId: eachSiloId, userLogin: userLogin,]);
		invCountMapData=invCountMap.openingBalance;
		if(UtilValidate.isNotEmpty(invCountMapData)){
			openingQty = invCountMapData.get("quantityKgs");
			openingFat=invCountMapData.get("kgFat");
			openingSnf=invCountMapData.get("kgSnf");
    	    openingBalSiloMap.put("openingQty", openingQty);
		    openingBalSiloMap.put("openingFat", openingFat);
		    openingBalSiloMap.put("openingSnf", openingSnf);
			//openingBalSiloMap.put("siloId", eachSiloId);
			totInventoryQty=totInventoryQty+openingQty;
			totFatQty=totFatQty+openingFat;
			totSnfQty=totSnfQty+openingSnf;
			//Debug.log("===========openingBalSiloMap=======444===================="+openingBalSiloMap);
		}
		allDetailsMap.put("openingBalSiloMap",openingBalSiloMap);
		
		siloList=EntityUtil.filterByCondition(milkTransferList, EntityCondition.makeCondition("siloId", EntityOperator.EQUALS,eachSiloId));
		if(UtilValidate.isNotEmpty(siloList)){
		receiptNo=1;
	    BigDecimal ReceiptTotQty = BigDecimal.ZERO;
		BigDecimal ReceiptTotFat = BigDecimal.ZERO;
		BigDecimal ReceiptTotSnf = BigDecimal.ZERO;
		siloList.each{siloData->
		  if(UtilValidate.isNotEmpty(siloData.partyId)){
			 MrrDetailsMap=[:];
			 partyId=siloData.partyId;
			 dcNo=siloData.dcNo;
			 containerId=siloData.containerId;
			 receivedQuantity=siloData.receivedQuantity;
			 receivedKgFat=siloData.receivedKgFat;
			 receivedKgSnf=siloData.receivedKgSnf;
			 if(UtilValidate.isNotEmpty(receivedQuantity)){
			 ReceiptTotQty=ReceiptTotQty+receivedQuantity;
			 }
			 if(UtilValidate.isNotEmpty(receivedKgFat)){
				 ReceiptTotFat=ReceiptTotFat+receivedKgFat;
			 }
			 if(UtilValidate.isNotEmpty(receivedKgSnf)){
				 ReceiptTotSnf=ReceiptTotSnf+receivedKgSnf;
			  }
			 MrrDetailsMap.put("partyId",partyId);
			 MrrDetailsMap.put("dcNo",dcNo);
			 MrrDetailsMap.put("containerId",containerId);
			 MrrDetailsMap.put("receivedQuantity",receivedQuantity);
			 MrrDetailsMap.put("receivedKgFat",receivedKgFat);
			 MrrDetailsMap.put("receivedKgSnf",receivedKgSnf);
			 
			receiptSiloMap.put(receiptNo,MrrDetailsMap);
			receiptNo++;
			
		  }
		 }
	     totInventoryQty=totInventoryQty+ReceiptTotQty;
         totFatQty=totFatQty+ReceiptTotFat;
	     totSnfQty=totSnfQty+ReceiptTotSnf;
       }
	 allDetailsMap.put("receiptSiloMap",receiptSiloMap);
	 allDetailsMap.put("totInventoryQty",totInventoryQty);
	
	 issueMap=[:];
	 initTempMap=[:]; 
	 BigDecimal issuedTotQty = BigDecimal.ZERO;
	
	 inventoryItemDetails=EntityUtil.filterByCondition(InventoryItemAndDetailList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachSiloId));
	 //	issuSiloIds=EntityUtil.getFieldListFromEntityList(workEffortList, "facilityId", true);
	 if(UtilValidate.isNotEmpty(inventoryItemDetails)){
		inventoryItemDetails.each{eachinventoryItemDetail->
			BigDecimal issuedQty=BigDecimal.ZERO;
	     issuedQty = (BigDecimal)eachinventoryItemDetail.get("quantityOnHandDiff");
    	workEffortId=eachinventoryItemDetail.workEffortId;
		siloWorkList=EntityUtil.filterByCondition(workEffortList, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,workEffortId));
	 	if(UtilValidate.isNotEmpty(siloWorkList)){
	        String receivedFacilityId = siloWorkList[0].get("facilityId");
			Map qtyDetMap = FastMap.newInstance();
			if(UtilValidate.isEmpty(IssuedSiloMap) || (UtilValidate.isNotEmpty(IssuedSiloMap) && UtilValidate.isEmpty(IssuedSiloMap.get(receivedFacilityId)))){
				qtyDetMap.put("qty",issuedQty);
				issuedTotQty=issuedTotQty+issuedQty;
				IssuedSiloMap.put(receivedFacilityId, qtyDetMap);
			}else{
				Map tempQtyMap = FastMap.newInstance();
				tempQtyMap.putAll(IssuedSiloMap.get(receivedFacilityId));
				tempQtyMap.putAt("qty", tempQtyMap.get("qty") + issuedQty);
				issuedTotQty=(issuedTotQty+tempQtyMap.get("qty") + issuedQty);
				
				IssuedSiloMap.put(receivedFacilityId, qtyDetMap);
			}
					
					
		}
	   }
	 }
   closingBalSiloMap.put("dayCloseBal",totInventoryQty+issuedTotQty);
   closingBalSiloMap.put("totFatQty",totFatQty);
   closingBalSiloMap.put("totSnfQty",totSnfQty);
		
  allDetailsMap.put("IssuedSiloMap",IssuedSiloMap);
  allDetailsMap.put("closingBalance",closingBalSiloMap);
				
 allDetailsRegisterMap.put(eachSiloId,allDetailsMap);
  }
 }
}
context.allDetailsRegisterMap=allDetailsRegisterMap;
//Debug.log("===========allDetailsRegisterMap==========================="+allDetailsRegisterMap);


