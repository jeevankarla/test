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

List allSilosList = FastList.newInstance();
allSilosList = delegator.findList("Facility",EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.IN , ["RAWMILK","PASTEURIZATION"])  , null, null, null, false );
allSiloIds=EntityUtil.getFieldListFromEntityList(allSilosList, "facilityId", true);

conditionList =[];
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,allSiloIds ));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
allSiloInveAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);

// MILK PROCESSING REGISTER -- UNPROCESSING MILK

Map allDetailsRegisterMap = FastMap.newInstance();
Map allSilosTotalsMap =FastMap.newInstance();
List workEffortList = FastList.newInstance();
siloFacilitiList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,"RAWMILK"));
siloIds=EntityUtil.getFieldListFromEntityList(siloFacilitiList, "facilityId", true);

if(UtilValidate.isNotEmpty(siloIds)){
  /* conditionList =[];
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
   conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds ));
   EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
   InventoryItemAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);*/
  
   InventoryItemAndDetailList = EntityUtil.filterByCondition(allSiloInveAndDetailList, EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds));
   
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
  
   BigDecimal totOpeningQty = BigDecimal.ZERO;
   BigDecimal totReceiptQty = BigDecimal.ZERO;
   BigDecimal totOpenReceiptQty = BigDecimal.ZERO;
   BigDecimal totVarianceQty = BigDecimal.ZERO;
   BigDecimal totIssueQty = BigDecimal.ZERO;
   BigDecimal totDayClosingQty = BigDecimal.ZERO;
   
   
   siloIds.each {eachSiloId->
		Map allDetailsMap = FastMap.newInstance();
		Map closingBalSiloMap = FastMap.newInstance();
		Map openingBalSiloMap = FastMap.newInstance();
		Map receiptSiloMap = FastMap.newInstance();
		Map IssuedSiloMap = FastMap.newInstance();
		
		BigDecimal totInventoryQty = BigDecimal.ZERO;
		BigDecimal totFatQty = BigDecimal.ZERO;
		BigDecimal totSnfQty = BigDecimal.ZERO;
		
		BigDecimal openingQty = BigDecimal.ZERO;
		
		invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:dayBegin, facilityId: eachSiloId, userLogin: userLogin,]);
		invCountMapData=invCountMap.openingBalance;
		if(UtilValidate.isNotEmpty(invCountMapData)){
			openingQty = invCountMapData.get("quantityKgs");
			openingFat=invCountMapData.get("Fat");
			openingSnf=invCountMapData.get("Snf");
		    openingBalSiloMap.put("openingQty", openingQty);
		    openingBalSiloMap.put("openingFat", openingFat);
		    openingBalSiloMap.put("openingSnf", openingSnf);
			//openingBalSiloMap.put("siloId", eachSiloId);
			if(UtilValidate.isNotEmpty(openingQty)){
				totInventoryQty=totInventoryQty+openingQty;
				totOpeningQty=totOpeningQty+openingQty;
			}
			if(UtilValidate.isNotEmpty(openingFat)){
				totFatQty=totFatQty+openingFat;
			}
			if(UtilValidate.isNotEmpty(openingSnf)){
				totSnfQty=totSnfQty+openingSnf;
			}
		}
		allDetailsMap.put("openingBalSiloMap",openingBalSiloMap);
		
		siloList=EntityUtil.filterByCondition(milkTransferList, EntityCondition.makeCondition("siloId", EntityOperator.EQUALS,eachSiloId));
		receiptNo=1;
	    BigDecimal ReceiptTotQty = BigDecimal.ZERO;
		BigDecimal ReceiptTotFat = BigDecimal.ZERO;
		BigDecimal ReceiptTotSnf = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(siloList)){
			siloList.each{siloData->
				if(UtilValidate.isNotEmpty(siloData.partyId)){
					 Map MrrDetailsMap= FastMap.newInstance();
					 partyId=siloData.partyId;
					 dcNo=siloData.dcNo;
					 containerId=siloData.containerId;
					 receivedQuantity=siloData.receivedQuantity;
					 receivedFat=siloData.receivedFat;
					 receivedSnf=siloData.receivedSnf;
					 if(UtilValidate.isNotEmpty(receivedQuantity)){
						 ReceiptTotQty=ReceiptTotQty+receivedQuantity;
					 }
					 if(UtilValidate.isNotEmpty(receivedFat)){
						 ReceiptTotFat=ReceiptTotFat+receivedFat;
					 }
					 if(UtilValidate.isNotEmpty(receivedSnf)){
						 ReceiptTotSnf=ReceiptTotSnf+receivedSnf;
					 }
					 MrrDetailsMap.put("partyId",partyId);
					 MrrDetailsMap.put("dcNo",dcNo);
					 MrrDetailsMap.put("containerId",containerId);
					 MrrDetailsMap.put("receivedQuantity",receivedQuantity);
					 MrrDetailsMap.put("receivedFat",receivedFat);
					 MrrDetailsMap.put("receivedSnf",receivedSnf);
					 
					 receiptSiloMap.put(receiptNo,MrrDetailsMap);
					 receiptNo++;
				}
				totReceiptQty=totReceiptQty+ReceiptTotQty;
				
				totInventoryQty=totInventoryQty+ReceiptTotQty;
				totFatQty=totFatQty+ReceiptTotFat;
				totSnfQty=totSnfQty+ReceiptTotSnf;
			}
		}
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
		conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, eachSiloId));
		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_COMPLETE"));
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		inventoryRecdTransfer = delegator.findList("InventoryTransfer", cond, null,null, null, false);
		if(inventoryRecdTransfer){
			inventoryRecdTransfer.each{eachInventoryRecdTransfer->
				Map transferDetailsMap=FastMap.newInstance();
				fromFacilityId=eachInventoryRecdTransfer.facilityId;
				inventoryTransferId=eachInventoryRecdTransfer.inventoryTransferId;
				
				conditionList.clear();
				//conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN,BigDecimal.ZERO));
				conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,fromFacilityId.toUpperCase() ));
				conditionList.add(EntityCondition.makeCondition("inventoryTransferId", EntityOperator.EQUALS,inventoryTransferId ));
				cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				
				inventoryTransferDetails=EntityUtil.filterByCondition(InventoryItemAndDetailList, cond);
				
				if(inventoryTransferDetails){
					inventoryTransferDetails=EntityUtil.getFirst(inventoryTransferDetails);
					//containerId=siloData.containerId;
					receivedQuantity=-(inventoryTransferDetails.quantityOnHandDiff);
					fatPercent=inventoryTransferDetails.fatPercent;
					snfPercent=inventoryTransferDetails.snfPercent;
					if(UtilValidate.isNotEmpty(receivedQuantity)){
						ReceiptTotQty=ReceiptTotQty+receivedQuantity;
					}
					transferDetailsMap.put("partyId",fromFacilityId);
					//transferDetailsMap.put("dcNo",inventoryTransferId);
					//transferDetailsMap.put("containerId",containerId);
					transferDetailsMap.put("receivedQuantity",receivedQuantity);
					transferDetailsMap.put("receivedFat",fatPercent);
					transferDetailsMap.put("receivedSnf",snfPercent);
					
				   receiptSiloMap.put(receiptNo,transferDetailsMap);
				   receiptNo++;
				}
		
		}
		 totReceiptQty=totReceiptQty+ReceiptTotQty;
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
				if(UtilValidate.isEmpty(IssuedSiloMap) || (UtilValidate.isNotEmpty(IssuedSiloMap) && UtilValidate.isEmpty(IssuedSiloMap.get(receivedFacilityId)))){
					Map qtyDetMap = FastMap.newInstance();
					qtyDetMap.put("qty",issuedQty);
					issuedTotQty=issuedTotQty+issuedQty;
					IssuedSiloMap.put(receivedFacilityId, qtyDetMap);
				}else{
					Map tempQtyMap = FastMap.newInstance();
					tempQtyMap.putAll(IssuedSiloMap.get(receivedFacilityId));
					tempQtyMap.putAt("qty", tempQtyMap.get("qty") + issuedQty);
					issuedTotQty=(issuedTotQty+ issuedQty);
					
					IssuedSiloMap.put(receivedFacilityId, tempQtyMap);
					}
	         }
			 if(eachinventoryItemDetail.quantityOnHandDiff<0 && UtilValidate.isNotEmpty(eachinventoryItemDetail.inventoryTransferId)){
				 BigDecimal issueTransferQty=BigDecimal.ZERO;
				 issueTransferQty=eachinventoryItemDetail.quantityOnHandDiff;
				 inventoryTransferId=eachinventoryItemDetail.inventoryTransferId;
				 
				 conditionList.clear();
				 conditionList.add(EntityCondition.makeCondition("inventoryTransferId", EntityOperator.EQUALS, inventoryTransferId));
				 conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_COMPLETE"));
				 cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				 inventoryIssueTransfer = delegator.findList("InventoryTransfer", cond, null,null, null, false);
				 if(inventoryIssueTransfer){
					 inventoryIssueTransfer = EntityUtil.getFirst(inventoryIssueTransfer);
					 receivedFacilityId=inventoryIssueTransfer.facilityIdTo;
					 if(UtilValidate.isEmpty(IssuedSiloMap) || (UtilValidate.isNotEmpty(IssuedSiloMap) && UtilValidate.isEmpty(IssuedSiloMap.get(receivedFacilityId)))){
						 Map qtyDetMap1 = FastMap.newInstance();
						 qtyDetMap1.put("qty",issueTransferQty);
						 issuedTotQty=issuedTotQty+issueTransferQty;
						 IssuedSiloMap.put(receivedFacilityId, qtyDetMap1);
					 }else{
						 Map tempIssueQtyMap = FastMap.newInstance();
						 tempIssueQtyMap.putAll(IssuedSiloMap.get(receivedFacilityId));
						 tempIssueQtyMap.putAt("qty", tempIssueQtyMap.get("qty") + issueTransferQty);
						 issuedTotQty=(issuedTotQty+ issueTransferQty);
						 IssuedSiloMap.put(receivedFacilityId, tempIssueQtyMap);
					 }
				 }
			 }
		   }
		 }
	 BigDecimal totVariance=BigDecimal.ZERO;
	 BigDecimal gainVariance=BigDecimal.ZERO;
	 BigDecimal lossVariance=BigDecimal.ZERO;
	 conditionList.clear();
	 conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
	 conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachSiloId));
	 cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	 pInvItemVariance = delegator.findList("PhysicalInventoryItemAndVariance", cond, null,null, null, false);
	 if(UtilValidate.isNotEmpty(pInvItemVariance)){
		 pInvItemVariance.each{eachPInvItemVariance->
			 BigDecimal varianceQty = eachPInvItemVariance.quantityOnHandVar;
			  if(varianceQty>0 && UtilValidate.isNotEmpty(varianceQty)){
				 gainVariance=gainVariance+varianceQty;
			 }
			 if(varianceQty<0 && UtilValidate.isNotEmpty(varianceQty)){
				 lossVariance=lossVariance+varianceQty;
			 }
		 }
	 }
   totVariance=gainVariance+lossVariance;
   totVarianceQty=totVarianceQty+totVariance;
   totIssueQty=totIssueQty+issuedTotQty;
   
   closingBalSiloMap.put("dayCloseBal",totInventoryQty+issuedTotQty+totVariance);
   closingBalSiloMap.put("totFatQty",totFatQty);
   closingBalSiloMap.put("totSnfQty",totSnfQty);
   
	   
	  allDetailsMap.put("IssuedSiloMap",IssuedSiloMap);
	  allDetailsMap.put("closingBalance",closingBalSiloMap);
	  allDetailsMap.put("gainLossVariance",totVariance);
	  
	 allDetailsRegisterMap.put(eachSiloId,allDetailsMap);
   }
   totOpenReceiptQty=totOpeningQty+totReceiptQty;
   totDayClosingQty=totOpenReceiptQty+totIssueQty+totVarianceQty;
   allSilosTotalsMap.put("totOpeningQty",totOpeningQty);
   allSilosTotalsMap.put("totReceiptQty",totReceiptQty);
   allSilosTotalsMap.put("totOpenReceiptQty",totOpenReceiptQty);
   allSilosTotalsMap.put("totVarianceQty",totVarianceQty);
   allSilosTotalsMap.put("totIssueQty",totIssueQty);
   allSilosTotalsMap.put("totDayClosingQty",totDayClosingQty);
}
context.allDetailsRegisterMap=allDetailsRegisterMap;
context.allSilosTotalsMap=allSilosTotalsMap;







// MILK POCESSING RGISTER == PASTURISED MILK

Map pmRegisterMap = FastMap.newInstance();
Map pmSilosTotalsMap =FastMap.newInstance();
Map eachPmSiloMap =FastMap.newInstance();

pmSiloList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,"PASTEURIZATION"));
pmSiloIds=EntityUtil.getFieldListFromEntityList(pmSiloList, "facilityId", true);
pmSiloIds.each{eachPmSiloId->
	Map pmSiloOpenBalMap =FastMap.newInstance();
	BigDecimal pmSiloInventory = BigDecimal.ZERO;
	
	pmInvCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:dayBegin, facilityId: eachPmSiloId, userLogin: userLogin,]);
	if(UtilValidate.isNotEmpty(pmInvCountMap)){
		pmInvCountMapData=pmInvCountMap.openingBalance;
		BigDecimal pmOpeningQty = BigDecimal.ZERO;
		BigDecimal pmOpeningFat = BigDecimal.ZERO;
		BigDecimal pmOpeningSnf = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(pmInvCountMapData.get("invProductId"))){
			pmOpenProdId = pmInvCountMapData.get("invProductId");
			pmOpeningQty = pmInvCountMapData.get("quantityKgs");
			pmOpeningFat = pmInvCountMapData.get("Fat");
			pmOpeningSnf = pmInvCountMapData.get("Snf");
			pmSiloOpenBalMap.put("pmOpenProdId", pmOpenProdId);
			pmSiloOpenBalMap.put("pmOpeningQty", pmOpeningQty);
			pmSiloOpenBalMap.put("pmOpeningFat", pmOpeningFat);
			pmSiloOpenBalMap.put("pmOpeningSnf", pmOpeningSnf);
			//openingBalSiloMap.put("siloId", eachSiloId);
			if(UtilValidate.isNotEmpty(pmOpeningQty)){
				pmSiloInventory=pmSiloInventory+pmOpeningQty;
			}
		}
		eachPmSiloMap.put("pmSiloOpenBalMap",pmSiloOpenBalMap);
	}
	
}







