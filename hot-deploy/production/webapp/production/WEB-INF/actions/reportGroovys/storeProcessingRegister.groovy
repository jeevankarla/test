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
import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;

fromDate=parameters.fromDate;
shiftId=parameters.shiftId;
context.shiftId = shiftId;

dctx = dispatcher.getDispatchContext();
DateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	DateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(DateTime);
dayEnd = UtilDateTime.getDayEnd(DateTime);

Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("shiftType", "MILK_SHIFT");
inMap.put("fromDate", dayBegin);
//inMap.put("thruDate", dayBegin);
inMap.put("shiftTypeId", shiftId);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );

fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;
context.fromDate = fromDate;
context.thruDate = dayEnd

List allSilosList = FastList.newInstance();
facilityCondList =[];
facilityCondList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,"INT7" ));
facilityCondList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"SILO" ));
EntityCondition facilityCond = EntityCondition.makeCondition(facilityCondList,EntityOperator.AND);
allSilosList = delegator.findList("Facility", facilityCond , null, UtilMisc.toList("sequenceNum"), null, false );
allSiloIds=EntityUtil.getFieldListFromEntityList(allSilosList, "facilityId", true);

partyGroup = delegator.findList("PartyGroup", null , null, null, null, false );

conditionList =[];
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,allSiloIds ));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
allSiloInveAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);

/*
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"CRQ_COMPLETED" ));
conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN , ["INTERNAL_INDENT"]));
EntityCondition custReqMainCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
custRequestList = delegator.findList("CustRequest", custReqMainCond, null,null, null, false);
pmIntAllIssueIds=EntityUtil.getFieldListFromEntityList(custRequestList, "custRequestId", true);

intIssueConList =[];
intIssueConList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
intIssueConList.add(EntityCondition.makeCondition("issuedDateTime", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
intIssueConList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN,pmIntAllIssueIds));
EntityCondition intIssueCond = EntityCondition.makeCondition(intIssueConList,EntityOperator.AND);
itemIssuanceList = delegator.findList("ItemIssuance", intIssueCond, null,null, null, false);
pmItemIssuanceIds=EntityUtil.getFieldListFromEntityList(itemIssuanceList, "itemIssuanceId", true);
Debug.log("pmItemIssuanceIds================="+pmItemIssuanceIds)
*/

// INVENTORY TRANSFER DETAILS
List allSiloInvTransferList = FastList.newInstance();
List allInvTransGroupMemList = FastList.newInstance();
List allInvTransGroupMemSumList = FastList.newInstance();

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,allSiloIds ));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"IXF_COMPLETE"));
//conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"IXF_CANCELLED"));
EntityCondition invTransMainCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
allSiloInvTransferList = delegator.findList("InventoryTransfer", invTransMainCond, null,null, null, false);
if(UtilValidate.isNotEmpty(allSiloInvTransferList)){
	allinvTransIds=EntityUtil.getFieldListFromEntityList(allSiloInvTransferList, "inventoryTransferId", true);
	allInvTransGroupMemList = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,allinvTransIds ), null,null, null, false);
	allInvTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(allInvTransGroupMemList, "transferGroupId", false));
	allInvTransGroupMemSumList = delegator.findList("InventoryTransferGroupAndMemberSum", EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,allInvTransGroupIds ), null,null, null, false);
}

conditionList.clear();
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkTransferAndItemList = delegator.findList("MilkTransferAndMilkTransferItem", cond, null,null, null, false);
	
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"MXF_RECD"));
conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.IN,["INTERNALUSE","OUTGOING","COPACKING"]));
EntityCondition IntExternalcond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
intAndExtenalIssuesList=EntityUtil.filterByCondition(milkTransferAndItemList, IntExternalcond);

milkTransferList=EntityUtil.filterByCondition(milkTransferAndItemList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"MXF_APPROVED"));


List<String> internalShipIds = FastList.newInstance();
List<GenericValue> internalShipment = delegator.findList("Shipment", EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "MILK_RETURN_SHIPMENT"), null, null, null, false);
if(UtilValidate.isNotEmpty(internalShipment)){
   internalShipIds = EntityUtil.getFieldListFromEntityList(internalShipment, "shipmentId", true);
}

// MILK PROCESSING REGISTER -- UNPROCESSING MILK

Map allDetailsRegisterMap = FastMap.newInstance();
Map allSilosTotalsMap =FastMap.newInstance();
List workEffortList = FastList.newInstance();
siloFacilitiList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,"RAWMILK"));
siloIds=EntityUtil.getFieldListFromEntityList(siloFacilitiList, "facilityId", true);

if(UtilValidate.isNotEmpty(siloIds)){
  /* conditionList =[];
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
   conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
   conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds ));
   EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
   InventoryItemAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);*/
  
   InventoryItemAndDetailList = EntityUtil.filterByCondition(allSiloInveAndDetailList, EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds));
   
   workEffortIds=EntityUtil.getFieldListFromEntityList(InventoryItemAndDetailList, "workEffortId", true);
   if(UtilValidate.isNotEmpty(workEffortIds)){
     workEffortList = delegator.findList("WorkEffort",EntityCondition.makeCondition("workEffortId", EntityOperator.IN , workEffortIds)  , null, null, null, false );
     //issuSiloIds=EntityUtil.getFieldListFromEntityList(workEffortList, "facilityId", true);
   }

   
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
		
		invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:fromDate, facilityId: eachSiloId, userLogin: userLogin,]);
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
		
	// RM RECEIPTS
		receiptNo=1;
		BigDecimal ReceiptTotQty = BigDecimal.ZERO;
		BigDecimal ReceiptTotFat = BigDecimal.ZERO;
		BigDecimal ReceiptTotSnf = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(milkTransferList)){
			siloList=EntityUtil.filterByCondition(milkTransferList, EntityCondition.makeCondition("siloId", EntityOperator.EQUALS,eachSiloId));
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
				}
			}
		}
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachSiloId));
		conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_EQUAL,null ));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		RMSiloInvDetailList = EntityUtil.filterByCondition(allSiloInveAndDetailList, cond);
		if(UtilValidate.isNotEmpty(RMSiloInvDetailList)){
			RMSiloInvDetailList.each{eachRmSiloReceipt->
				Map rmReceiptsMap =FastMap.newInstance();
				
				BigDecimal rmRecdQty = BigDecimal.ZERO;
				BigDecimal rmRecdFat = BigDecimal.ZERO;
				BigDecimal rmRecdSnf = BigDecimal.ZERO;
				String rmRecedProdId = "";
				
				rmRecedProdId=eachRmSiloReceipt.productId;
				rmRecdQty=eachRmSiloReceipt.quantityOnHandDiff;
				rmRecdFat=eachRmSiloReceipt.fatPercent;
				rmRecdSnf=eachRmSiloReceipt.snfPercent;
				//rmReceiptsMap.put("rmRecedProdId",rmRecedProdId);
				rmReceiptsMap.put("receivedQuantity",rmRecdQty);
				rmReceiptsMap.put("receivedFat",rmRecdFat);
				rmReceiptsMap.put("receivedSnf",rmRecdSnf);
				if(UtilValidate.isNotEmpty(rmRecdQty)){
					ReceiptTotQty=ReceiptTotQty+rmRecdQty;
				}
				receiptSiloMap.put(receiptNo,rmReceiptsMap);
				receiptNo++;
			}
		}
		 if(UtilValidate.isNotEmpty(internalShipIds)){
			 conditionList.clear();
			 conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN,internalShipIds ));
			 conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachSiloId ));
			 conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
			 EntityCondition internalReturnCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			 internalReturnsList = EntityUtil.filterByCondition(InventoryItemAndDetailList, internalReturnCond);
			 if(UtilValidate.isNotEmpty(internalReturnsList)){
				 internalReturnsList.each{eachInternalReturns->
					 Map rmReturnReceiptsMap =FastMap.newInstance();
					 
					 BigDecimal rmRecdQty = BigDecimal.ZERO;
					 BigDecimal rmRecdFat = BigDecimal.ZERO;
					 BigDecimal rmRecdSnf = BigDecimal.ZERO;
					 String rmRecedProdId = "";
					 
					 rmRecedProdId=eachInternalReturns.productId;
					 rmRecdQty=eachInternalReturns.quantityOnHandDiff;
					 rmRecdFat=eachInternalReturns.fatPercent;
					 rmRecdSnf=eachInternalReturns.snfPercent;
					 //rmReturnReceiptsMap.put("rmRecedProdId",rmRecedProdId);
					 rmReturnReceiptsMap.put("receivedQuantity",rmRecdQty);
					 rmReturnReceiptsMap.put("receivedFat",rmRecdFat);
					 rmReturnReceiptsMap.put("receivedSnf",rmRecdSnf);
					 if(UtilValidate.isNotEmpty(rmRecdQty)){
						 ReceiptTotQty=ReceiptTotQty+rmRecdQty;
					 }
					 receiptSiloMap.put(receiptNo,rmReturnReceiptsMap);
					 receiptNo++;
				 }
			 }
	 
		  }

		 
		 
		if(UtilValidate.isNotEmpty(allInvTransGroupMemSumList)){
			inventoryRecdTransfer=EntityUtil.filterByCondition(allSiloInvTransferList, EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS,eachSiloId));
			if(UtilValidate.isNotEmpty(inventoryRecdTransfer)){
				rmInvTransRecIds=EntityUtil.getFieldListFromEntityList(inventoryRecdTransfer, "inventoryTransferId", true);
				rmInvTransGroupMemberRecs=EntityUtil.filterByCondition(allInvTransGroupMemList, EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,rmInvTransRecIds));
				rmRecTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(rmInvTransGroupMemberRecs, "transferGroupId", false));
				if(UtilValidate.isNotEmpty(rmRecTransGroupIds)){
					rmInvTransSumRecs=EntityUtil.filterByCondition(allInvTransGroupMemSumList, EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,rmRecTransGroupIds));
					if(UtilValidate.isNotEmpty(rmInvTransSumRecs)){
						rmInvTransSumRecs.each{eachInventoryRecdTransfer->
							Map transferDetailsMap=FastMap.newInstance();
							receivedQuantity=eachInventoryRecdTransfer.xferQtySum;
							fromFacilityId=eachInventoryRecdTransfer.fromFacilityId;
							if(UtilValidate.isNotEmpty(receivedQuantity)){
								ReceiptTotQty=ReceiptTotQty+receivedQuantity;
							}
							rmInvTransRecdParty=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,fromFacilityId));
							if(UtilValidate.isNotEmpty(rmInvTransRecdParty)){
								if(UtilValidate.isNotEmpty(rmInvTransRecdParty.comments)){
									fromFacilityId=rmInvTransRecdParty.comments;
								}
							}
							transferDetailsMap.put("partyId",fromFacilityId);
							transferDetailsMap.put("receivedQuantity",receivedQuantity);
							receiptSiloMap.put(receiptNo,transferDetailsMap);
							receiptNo++;
		 
							
						}
					}
				}
					
			}
		}
		totReceiptQty=totReceiptQty+ReceiptTotQty;
		totInventoryQty=totInventoryQty+ReceiptTotQty;
		totFatQty=totFatQty+ReceiptTotFat;
		totSnfQty=totSnfQty+ReceiptTotSnf;

	 allDetailsMap.put("receiptSiloMap",receiptSiloMap);
	 allDetailsMap.put("totInventoryQty",totInventoryQty);
	
	// RM PRODUCTION ISSUES,TRANSFER ISSUES, INT&Ext ISSUES            
	 rmIssuesNo=1;
	 BigDecimal issuedTotQty = BigDecimal.ZERO;
	 inventoryItemDetails=EntityUtil.filterByCondition(InventoryItemAndDetailList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachSiloId));
	 workEffortIssueIds = new HashSet(EntityUtil.getFieldListFromEntityList(inventoryItemDetails, "workEffortId", false));
	 if(UtilValidate.isNotEmpty(workEffortIssueIds)){
		 workEffortIssueIds.each{eachIssueWorkeffId->
			 Map rmProductinIssuesMap =FastMap.newInstance();
			 BigDecimal issuedQty=BigDecimal.ZERO;
			 siloWorkList=EntityUtil.filterByCondition(workEffortList, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachIssueWorkeffId));
			 String receivedFacilityId="";
			 /*if(UtilValidate.isNotEmpty(siloWorkList)){
				  	receivedFacilityId = siloWorkList[0].get("facilityId");
			 }*/
			 workEffortInventoryItemDetails=EntityUtil.filterByCondition(inventoryItemDetails, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachIssueWorkeffId));
			 workEffortInventoryItemDetails.each{eachWorkEffInvIssue->
				 tempIssuedQty = (BigDecimal)eachWorkEffInvIssue.get("quantityOnHandDiff");
				 if(UtilValidate.isNotEmpty(receivedFacilityId) && tempIssuedQty<0){
					 tempIssuedQty=-tempIssuedQty;
					 issuedQty=issuedQty+tempIssuedQty;
				 }
			 }
			 workEffortRecdInvItemDetails=EntityUtil.filterByCondition(allSiloInveAndDetailList, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachIssueWorkeffId));
			 workEffortRecdInvItemDetails.each{eachWorkEffRecdInvIssue->
				 recdSilo = (String)eachWorkEffRecdInvIssue.get("facilityId");
				 productBatchId = (String)eachWorkEffRecdInvIssue.get("productBatchId");
				 tempIssuedQty = (BigDecimal)eachWorkEffRecdInvIssue.get("quantityOnHandDiff");
				 if(UtilValidate.isNotEmpty(recdSilo) && UtilValidate.isNotEmpty(productBatchId) && tempIssuedQty>0){
					 if(UtilValidate.isNotEmpty(receivedFacilityId)){
						 receivedFacilityId=receivedFacilityId+","+recdSilo;
					 }else{
						 receivedFacilityId=recdSilo;
					 }
				 }
			 }
			 if(UtilValidate.isNotEmpty(issuedQty) && issuedQty>0){
				 rmProductinIssuesMap.put("partyId",receivedFacilityId);
				 rmProductinIssuesMap.put("issuedQuantity",issuedQty);
				 IssuedSiloMap.put(rmIssuesNo,rmProductinIssuesMap);
				 issuedTotQty=issuedTotQty+issuedQty;
				 rmIssuesNo++;
				 
			 }
		 }
	 }
	// RM Internal And External Milk Issues
	 if(UtilValidate.isNotEmpty(intAndExtenalIssuesList)){
		 intAndExtRmIssuesList=EntityUtil.filterByCondition(intAndExtenalIssuesList, EntityCondition.makeCondition("siloId", EntityOperator.EQUALS,eachSiloId));
		 if(UtilValidate.isNotEmpty(intAndExtRmIssuesList)){
			 intAndExtRmIssuesList.each{eachIntRMIssues->
				 Map rmIntExtIssuesMap=FastMap.newInstance();
				 BigDecimal rmIntExtIssueQty=BigDecimal.ZERO;
				 rmIntExtIssueQty=eachIntRMIssues.receivedQuantity;
				 rmIntExtIssueParty=eachIntRMIssues.partyIdTo;
				 rmIntExtIssueProduct=eachIntRMIssues.productId;
				 if(UtilValidate.isNotEmpty(rmIntExtIssueQty)){
					 issuedTotQty=issuedTotQty+rmIntExtIssueQty;
				 }
				 rmInternalIssuesPatry=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,rmIntExtIssueParty));
				 if(UtilValidate.isNotEmpty(rmInternalIssuesPatry)){
					 rmInternalIssuesPatry = EntityUtil.getFirst(rmInternalIssuesPatry);
					 if(UtilValidate.isNotEmpty(rmInternalIssuesPatry.comments)){
						 rmIntExtIssueParty=rmInternalIssuesPatry.comments;
					 }
				 }
				 rmIntExtIssuesMap.put("partyId",rmIntExtIssueParty);
				 rmIntExtIssuesMap.put("issuedQuantity",rmIntExtIssueQty);
				 IssuedSiloMap.put(rmIssuesNo,rmIntExtIssuesMap);
				 rmIssuesNo++;
 
			 }
		 }
	 }
	 if(UtilValidate.isNotEmpty(allInvTransGroupMemSumList)){
		 inventoryIssuedTransfer=EntityUtil.filterByCondition(allSiloInvTransferList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachSiloId));
		 if(UtilValidate.isNotEmpty(inventoryIssuedTransfer)){
			 rmInvTransIssueIds=EntityUtil.getFieldListFromEntityList(inventoryIssuedTransfer, "inventoryTransferId", true);
			 rmInvTransGroupMemberIssues=EntityUtil.filterByCondition(allInvTransGroupMemList, EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,rmInvTransIssueIds));
			 rmIssueTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(rmInvTransGroupMemberIssues, "transferGroupId", false));
			 if(UtilValidate.isNotEmpty(rmIssueTransGroupIds)){
				 rmInvTransSumIssues=EntityUtil.filterByCondition(allInvTransGroupMemSumList, EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,rmIssueTransGroupIds));
				 if(UtilValidate.isNotEmpty(rmInvTransSumIssues)){
					 rmInvTransSumIssues.each{eachInventoryIssuedTransfer->
						 Map transferIssueDetailsMap=FastMap.newInstance();
						 BigDecimal issueTransferQty=BigDecimal.ZERO;
						 issueTransferQty=eachInventoryIssuedTransfer.xferQtySum;
						 toFacilityId=eachInventoryIssuedTransfer.toFacilityId;
						 rmInvTransIssuedParty=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,toFacilityId));
						 if(UtilValidate.isNotEmpty(rmInvTransIssuedParty)){
							 rmInvTransIssuedParty = EntityUtil.getFirst(rmInvTransIssuedParty);
							 if(UtilValidate.isNotEmpty(rmInvTransIssuedParty.comments)){
								 toFacilityId=rmInvTransIssuedParty.comments;
							 }
						 }
						 if(UtilValidate.isNotEmpty(issueTransferQty)){
							 issuedTotQty=issuedTotQty+issueTransferQty;
						 }
						 transferIssueDetailsMap.put("partyId",toFacilityId);
						 transferIssueDetailsMap.put("issuedQuantity",issueTransferQty);
						 IssuedSiloMap.put(rmIssuesNo,transferIssueDetailsMap);
						 rmIssuesNo++;
					 }
				 }
			 }
				 
		 }
	 }

	 BigDecimal totVariance=BigDecimal.ZERO;
	 BigDecimal gainVariance=BigDecimal.ZERO;
	 BigDecimal lossVariance=BigDecimal.ZERO;
	 conditionList.clear();
	 conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	 conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
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
   
   closingBalSiloMap.put("dayCloseBal",totInventoryQty-issuedTotQty+totVariance);
   closingBalSiloMap.put("totFatQty",totFatQty);
   closingBalSiloMap.put("totSnfQty",totSnfQty);
   
	   
	  allDetailsMap.put("IssuedSiloMap",IssuedSiloMap);
	  allDetailsMap.put("closingBalance",closingBalSiloMap);
	  allDetailsMap.put("gainLossVariance",totVariance);
	  
	 allDetailsRegisterMap.put(eachSiloId,allDetailsMap);
   }
   totOpenReceiptQty=totOpeningQty+totReceiptQty;
   totDayClosingQty=totOpenReceiptQty-totIssueQty+totVarianceQty;
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


//pmSiloList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,"PASTEURIZATION"));

/*conditionList.clear();
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachMpuSiloId));
conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_EQUAL,null ));
conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
pmSiloInvDetailList = EntityUtil.filterByCondition(allSiloInveAndDetailList, cond);
*/
mpuSiloList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.NOT_EQUAL,"RAWMILK"));
mpuSiloTypes = new HashSet(EntityUtil.getFieldListFromEntityList(mpuSiloList, "categoryTypeEnum", false));
Map mpuAllSilosMap = FastMap.newInstance();

mpuSiloTypes.each{eachSiloType->
	Map eachSiloTypeMap = FastMap.newInstance();
	Map pmRegisterMap = FastMap.newInstance();
	Map pmSilosTotalsMap =FastMap.newInstance();
	
	BigDecimal totPmOpeningQty = BigDecimal.ZERO;
	BigDecimal totPmReceiptQty = BigDecimal.ZERO;
	BigDecimal totPmOpenReceiptQty = BigDecimal.ZERO;
	BigDecimal totPmVarianceQty = BigDecimal.ZERO;
	BigDecimal totPmIssueQty = BigDecimal.ZERO;
	BigDecimal totPmDayClosingQty = BigDecimal.ZERO;
	
	eachTypeList = EntityUtil.filterByCondition(allSilosList, EntityCondition.makeCondition("categoryTypeEnum", EntityOperator.EQUALS,eachSiloType));
	mpuSiloIds=EntityUtil.getFieldListFromEntityList(eachTypeList, "facilityId", true);
	mpuSiloIds.each{eachMpuSiloId->
		Map eachPmSiloMap =FastMap.newInstance();
		Map pmSiloOpenBalMap =FastMap.newInstance();
		Map pmSiloRecdMap =FastMap.newInstance();
		Map pmSiloIssueMap =FastMap.newInstance();
		Map pmSiloClosingMap =FastMap.newInstance();
		
		
		BigDecimal pmSiloInventory = BigDecimal.ZERO;
		
		pmInvCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:fromDate, facilityId: eachMpuSiloId, userLogin: userLogin,]);
		if(UtilValidate.isNotEmpty(pmInvCountMap)){
			pmInvCountMapData=pmInvCountMap.openingBalance;
			BigDecimal pmOpeningQty = BigDecimal.ZERO;
			BigDecimal pmOpeningFat = BigDecimal.ZERO;
			BigDecimal pmOpeningSnf = BigDecimal.ZERO;
			String pmOpenProdId ="";
			
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
					totPmOpeningQty=totPmOpeningQty+pmOpeningQty;
					
				}
			}
			eachPmSiloMap.put("pmSiloOpenBalMap",pmSiloOpenBalMap);
		}
		
		// PM Received Qty + PM Recd Transfer Qty
		BigDecimal pmRecdSiloQty = BigDecimal.ZERO;
		receiptNo=1;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachMpuSiloId));
		conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.NOT_EQUAL,null ));
		conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		pmSiloInvDetailList = EntityUtil.filterByCondition(allSiloInveAndDetailList, cond);
		if(UtilValidate.isNotEmpty(pmSiloInvDetailList)){
			pmSiloInvDetailList.each{eachPmSiloReceipt->
				Map pmReceiptsMap =FastMap.newInstance();
				
				BigDecimal pmRecdQty = BigDecimal.ZERO;
				BigDecimal pmRecdFat = BigDecimal.ZERO;
				BigDecimal pmRecdSnf = BigDecimal.ZERO;
				String pmRecedProdId = "";
				String issuedFromSilo = "";
				
				pmRecedProdId=eachPmSiloReceipt.productId;
				pmRecdQty=eachPmSiloReceipt.quantityOnHandDiff;
				pmRecdFat=eachPmSiloReceipt.fatPercent;
				pmRecdSnf=eachPmSiloReceipt.snfPercent;
				issudworkEffortId=eachPmSiloReceipt.workEffortId;
				
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,issudworkEffortId));
				conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN,BigDecimal.ZERO));
				EntityCondition issudSiloCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				pmWorkEffortIssdInvItemDetails = EntityUtil.filterByCondition(allSiloInveAndDetailList, issudSiloCond);
				if(UtilValidate.isNotEmpty(pmWorkEffortIssdInvItemDetails)){
					//issudFromSiloIds = new HashSet(EntityUtil.getFieldListFromEntityList(pmWorkEffortIssdInvItemDetails, "facilityId", false));
					pmWorkEffortIssdInvItemDetails = EntityUtil.getFirst(pmWorkEffortIssdInvItemDetails);
					String IssdFromSilo = (String)pmWorkEffortIssdInvItemDetails.get("facilityId");
					BigDecimal tempIssuedFromQty = (BigDecimal)pmWorkEffortIssdInvItemDetails.get("quantityOnHandDiff");
					if(UtilValidate.isNotEmpty(IssdFromSilo) && tempIssuedFromQty<0){
							issuedFromSilo=IssdFromSilo;
					}
				}
				pmReceiptsMap.put("partyId",issuedFromSilo);
				pmReceiptsMap.put("pmRecedProdId",pmRecedProdId);
				pmReceiptsMap.put("pmRecdQty",pmRecdQty);
				pmReceiptsMap.put("pmRecdFat",pmRecdFat);
				pmReceiptsMap.put("pmRecdSnf",pmRecdSnf);
				if(UtilValidate.isNotEmpty(pmRecdQty)){
					pmRecdSiloQty=pmRecdSiloQty+pmRecdQty;
				}
				pmSiloRecdMap.put(receiptNo,pmReceiptsMap);
				receiptNo++;
			}
		}
		if(UtilValidate.isNotEmpty(internalShipIds)){
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("shipmentId", EntityOperator.IN,internalShipIds ));
			conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachMpuSiloId ));
			conditionList.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
			EntityCondition internalPmReturnCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
			internalPmReturnsList = EntityUtil.filterByCondition(allSiloInveAndDetailList, internalPmReturnCond);
			if(UtilValidate.isNotEmpty(internalPmReturnsList)){
				internalPmReturnsList.each{eachInternalPmReturns->
					Map pmReturnReceiptsMap =FastMap.newInstance();
					
					BigDecimal pmRecdQty = BigDecimal.ZERO;
					BigDecimal pmRecdFat = BigDecimal.ZERO;
					BigDecimal pmRecdSnf = BigDecimal.ZERO;
					String pmRecedProdId = "";
					
					pmRecedProdId=eachInternalPmReturns.productId;
					pmRecdQty=eachInternalPmReturns.quantityOnHandDiff;
					pmRecdFat=eachInternalPmReturns.fatPercent;
					pmRecdSnf=eachInternalPmReturns.snfPercent;
					pmReturnReceiptsMap.put("pmRecedProdId",pmRecedProdId);
					pmReturnReceiptsMap.put("pmRecdQty",pmRecdQty);
					pmReturnReceiptsMap.put("pmRecdFat",pmRecdFat);
					pmReturnReceiptsMap.put("pmRecdSnf",pmRecdSnf);
					if(UtilValidate.isNotEmpty(pmRecdQty)){
						pmRecdSiloQty=pmRecdSiloQty+pmRecdQty;
					}
					pmSiloRecdMap.put(receiptNo,pmReturnReceiptsMap);
					receiptNo++;
				}
			}
	
		 }
		if(UtilValidate.isNotEmpty(allInvTransGroupMemSumList)){
			PMInvRecdTransfer=EntityUtil.filterByCondition(allSiloInvTransferList, EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS,eachMpuSiloId));
			if(UtilValidate.isNotEmpty(PMInvRecdTransfer)){
				pmInvTransRecIds=EntityUtil.getFieldListFromEntityList(PMInvRecdTransfer, "inventoryTransferId", true);
				pmInvTransGroupMemberRecs=EntityUtil.filterByCondition(allInvTransGroupMemList, EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,pmInvTransRecIds));
				pmRecTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(pmInvTransGroupMemberRecs, "transferGroupId", false));
				if(UtilValidate.isNotEmpty(pmRecTransGroupIds)){
					pmInvTransSumRecs=EntityUtil.filterByCondition(allInvTransGroupMemSumList, EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,pmRecTransGroupIds));
					if(UtilValidate.isNotEmpty(pmInvTransSumRecs)){
						pmInvTransSumRecs.each{eachInventoryRecdTransfer->
							Map pmRecdTransfersMap=FastMap.newInstance();
							BigDecimal pmTransferQty = BigDecimal.ZERO;
							pmTransferQty=eachInventoryRecdTransfer.xferQtySum;
							pmFromFacId=eachInventoryRecdTransfer.fromFacilityId;
							String recdTransprodId=eachInventoryRecdTransfer.productId;
							if(UtilValidate.isNotEmpty(pmTransferQty)){
								pmRecdSiloQty=pmRecdSiloQty+pmTransferQty;
							}
							pmInvTransRecdParty=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,pmFromFacId));
							if(UtilValidate.isNotEmpty(pmInvTransRecdParty)){
								if(UtilValidate.isNotEmpty(pmInvTransRecdParty.comments)){
									pmFromFacId=pmInvTransRecdParty.comments;
								}
							}
							//Debug.log("pmFromFacId========================================"+pmFromFacId);
							pmRecdTransfersMap.put("partyId",pmFromFacId);
							pmRecdTransfersMap.put("pmRecdQty",pmTransferQty);
							pmRecdTransfersMap.put("pmRecedProdId",recdTransprodId);
							pmSiloRecdMap.put(receiptNo,pmRecdTransfersMap);
							receiptNo++;
						}
					}
				}
					
			}
		}
		pmSiloInventory=pmSiloInventory+pmRecdSiloQty;
		totPmReceiptQty=totPmReceiptQty+pmRecdSiloQty;
		eachPmSiloMap.put("pmSiloRecdMap",pmSiloRecdMap);
		eachPmSiloMap.put("pmSiloInventory",pmSiloInventory);
		
		
		// PM Issues Qty + PM TransferIssues
		BigDecimal pmIssuedSiloQty = BigDecimal.ZERO;
		issueNo=1;
		issuedInvDetails=EntityUtil.filterByCondition(allSiloInveAndDetailList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachMpuSiloId));
		
		pmWorkEffortIssueIds = new HashSet(EntityUtil.getFieldListFromEntityList(issuedInvDetails, "workEffortId", false));
		if(UtilValidate.isNotEmpty(pmWorkEffortIssueIds)){
			pmWorkEffortIssueIds.each{eachPmIssueWorkeffId->
				Map pmProductinIssuesMap =FastMap.newInstance();
				BigDecimal pmProductionIssuQty=BigDecimal.ZERO;
				String pmRecFacilityId="";
				pmWorkEffortInvItemDetails=EntityUtil.filterByCondition(issuedInvDetails, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachPmIssueWorkeffId));
				pmWorkEffortInvItemDetails.each{eachPmWorkEffInvIssue->
					recdSilo = (String)eachPmWorkEffInvIssue.get("facilityId");
					productBatchId = (String)eachPmWorkEffInvIssue.get("productBatchId");
					tempPmIssuedQty = (BigDecimal)eachPmWorkEffInvIssue.get("quantityOnHandDiff");
					if(UtilValidate.isNotEmpty(recdSilo) && tempPmIssuedQty<0){
						tempPmIssuedQty=-tempPmIssuedQty;
						pmProductionIssuQty=pmProductionIssuQty+tempPmIssuedQty;
					}
				}
				pmWorkEffortRecdInvItemDetails=EntityUtil.filterByCondition(allSiloInveAndDetailList, EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,eachPmIssueWorkeffId));
				pmWorkEffortRecdInvItemDetails.each{eachPmWorkEffRecdInvIssue->
					recdSilo = (String)eachPmWorkEffRecdInvIssue.get("facilityId");
					productBatchId = (String)eachPmWorkEffRecdInvIssue.get("productBatchId");
					tempPmIssuedQty = (BigDecimal)eachPmWorkEffRecdInvIssue.get("quantityOnHandDiff");
					if(UtilValidate.isNotEmpty(recdSilo) && UtilValidate.isNotEmpty(productBatchId) && tempPmIssuedQty>0){
						if(UtilValidate.isNotEmpty(pmRecFacilityId)){
							pmRecFacilityId=pmRecFacilityId+","+recdSilo;
							
						}else{
							pmRecFacilityId=recdSilo;
						}
					}
				}
				if(UtilValidate.isNotEmpty(pmProductionIssuQty) && pmProductionIssuQty>0){
					pmProductinIssuesMap.put("recFacility",pmRecFacilityId);
					pmProductinIssuesMap.put("qty",pmProductionIssuQty);
					pmSiloIssueMap.put(issueNo,pmProductinIssuesMap);
					pmIssuedSiloQty=pmIssuedSiloQty+pmProductionIssuQty;
					issueNo++;
					
				}
			}
		}
		
		if(UtilValidate.isNotEmpty(issuedInvDetails)){
			issuedInvDetails.each{eachinventoryItemDetail->
			   // PM issued Qty through production
			   BigDecimal pmissueProdnQty = ((BigDecimal)eachinventoryItemDetail.get("quantityOnHandDiff"));
			   pmItemIssuanceId=eachinventoryItemDetail.itemIssuanceId; 
			   if(UtilValidate.isNotEmpty(pmItemIssuanceId) && pmissueProdnQty<0){
				   pmissueProdnQty=-pmissueProdnQty;
				   custFromPartyId='';
				   pmItemIssuanceCustIdList = delegator.findOne("ItemIssuance",["itemIssuanceId":pmItemIssuanceId],false);
				   if(pmItemIssuanceCustIdList){
					   pmIsscustReqId=pmItemIssuanceCustIdList.get("custRequestId");
					   pmItemIssuancePartyIdToList = delegator.findOne("CustRequest",["custRequestId":pmIsscustReqId],false);
					   if(pmItemIssuancePartyIdToList){
						   custFromPartyId=pmItemIssuancePartyIdToList.get("fromPartyId");
					   }
				   }
				   pmCustIssuedParty=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,custFromPartyId));
				   if(UtilValidate.isNotEmpty(pmCustIssuedParty)){
					   pmCustIssuedParty = EntityUtil.getFirst(pmCustIssuedParty);
					   if(UtilValidate.isNotEmpty(pmCustIssuedParty.comments)){
						   custFromPartyId=pmCustIssuedParty.comments;
					   }
				   }
				   Map pmIssIntExtMap = FastMap.newInstance();
				   pmIssIntExtMap.put("qty",pmissueProdnQty);
				   pmIssIntExtMap.put("recFacility",custFromPartyId);
				   pmSiloIssueMap.put(issueNo, pmIssIntExtMap);
				   pmIssuedSiloQty=pmIssuedSiloQty+pmissueProdnQty;
				   issueNo++;
			   }
			}
		}
		// PM Internal And External Milk Issues
		/*if(UtilValidate.isNotEmpty(intAndExtenalIssuesList)){
			intAndExtPMIssuesList=EntityUtil.filterByCondition(intAndExtenalIssuesList, EntityCondition.makeCondition("siloId", EntityOperator.EQUALS,eachMpuSiloId));
			if(UtilValidate.isNotEmpty(intAndExtPMIssuesList)){
				intAndExtPMIssuesList.each{eachIntPMIssues->
					Map pmIntExtIssuesMap=FastMap.newInstance();
					BigDecimal pmIntExtIssueQty=BigDecimal.ZERO;
					pmIntExtIssueQty=eachIntPMIssues.receivedQuantity;
					pmIntExtIssueParty=eachIntPMIssues.partyIdTo;
					pmIntExtIssueProduct=eachIntPMIssues.productId;
					if(UtilValidate.isNotEmpty(pmIntExtIssueQty)){
						pmIssuedSiloQty=pmIssuedSiloQty+pmIntExtIssueQty;
					}
					pmIntExtIssuesMap.put("recFacility",pmIntExtIssueParty);
					pmIntExtIssuesMap.put("qty",pmIntExtIssueQty);
					pmSiloIssueMap.put(issueNo,pmIntExtIssuesMap);
					issueNo++;
	
				}
			}
		}
	*/
	 // PM Material Transfer Issues
		if(UtilValidate.isNotEmpty(allInvTransGroupMemSumList)){
			   pmInventoryIssuedTransfer=EntityUtil.filterByCondition(allSiloInvTransferList, EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,eachMpuSiloId));
			   if(UtilValidate.isNotEmpty(pmInventoryIssuedTransfer)){
				   pmInvTransIssueIds=EntityUtil.getFieldListFromEntityList(pmInventoryIssuedTransfer, "inventoryTransferId", true);
				   pmInvTransGroupMemberIssues=EntityUtil.filterByCondition(allInvTransGroupMemList, EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,pmInvTransIssueIds));
				   pmIssueTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(pmInvTransGroupMemberIssues, "transferGroupId", false));
				   if(UtilValidate.isNotEmpty(pmIssueTransGroupIds)){
					   pmInvTransSumIssues=EntityUtil.filterByCondition(allInvTransGroupMemSumList, EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,pmIssueTransGroupIds));
					   if(UtilValidate.isNotEmpty(pmInvTransSumIssues)){
						   pmInvTransSumIssues.each{eachInventoryIssuedTransfer->
							   Map pmTransferIssueDetailsMap=FastMap.newInstance();
							   BigDecimal pmIssueTransQty=BigDecimal.ZERO;
							   pmIssueTransQty=eachInventoryIssuedTransfer.xferQtySum;
							   pmRecdFacilityId=eachInventoryIssuedTransfer.toFacilityId;
							   if(UtilValidate.isNotEmpty(pmIssueTransQty)){
								   pmIssuedSiloQty=pmIssuedSiloQty+pmIssueTransQty;
							   }
							   pmInvTransIssuedParty=EntityUtil.filterByCondition(partyGroup, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,pmRecdFacilityId));
							   if(UtilValidate.isNotEmpty(pmInvTransIssuedParty)){
								   pmInvTransIssuedParty = EntityUtil.getFirst(pmInvTransIssuedParty);
								   if(UtilValidate.isNotEmpty(pmInvTransIssuedParty.comments)){
									   pmRecdFacilityId=pmInvTransIssuedParty.comments;
								   }
							   }
							   pmTransferIssueDetailsMap.put("recFacility",pmRecdFacilityId);
							   pmTransferIssueDetailsMap.put("qty",pmIssueTransQty);
							   pmSiloIssueMap.put(issueNo,pmTransferIssueDetailsMap);
							   issueNo++;
						   }
					   }
				   }
					   
			   }
		   }
	    	  
	    totPmIssueQty=totPmIssueQty+pmIssuedSiloQty;
	
		// PM Variances------------
		BigDecimal pmTotVariance=BigDecimal.ZERO;
		BigDecimal pmGainVariance=BigDecimal.ZERO;
		BigDecimal pmLossVariance=BigDecimal.ZERO;
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		conditionList.add(EntityCondition.makeCondition("physicalInventoryDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, eachMpuSiloId));
		cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		pmInvItemVariance = delegator.findList("PhysicalInventoryItemAndVariance", cond, null,null, null, false);
		if(UtilValidate.isNotEmpty(pmInvItemVariance)){
			pmInvItemVariance.each{eachPmInvItemVariance->
				BigDecimal pmVarianceQty = eachPmInvItemVariance.quantityOnHandVar;
				 if(pmVarianceQty>0 && UtilValidate.isNotEmpty(pmVarianceQty)){
					pmGainVariance=pmGainVariance+pmVarianceQty;
				}
				if(pmVarianceQty<0 && UtilValidate.isNotEmpty(pmVarianceQty)){
					pmLossVariance=pmLossVariance+pmVarianceQty;
				}
			}
		}
		if(UtilValidate.isNotEmpty(pmGainVariance) && UtilValidate.isNotEmpty(pmLossVariance)){
			pmTotVariance=pmGainVariance+pmLossVariance;
			totPmVarianceQty=totPmVarianceQty+pmTotVariance;
		}
	 // totVarianceQty=totVarianceQty+totVariance;
	 // totIssueQty=totIssueQty+issuedTotQty;
	  
	  pmSiloClosingMap.put("dayCloseBal",pmSiloInventory-pmIssuedSiloQty+pmTotVariance);
	 // pmSiloClosingMap.put("totFatQty",totFatQty);
	 // pmSiloClosingMap.put("totSnfQty",totSnfQty);
	  
	  
	  eachPmSiloMap.put("pmSiloIssueMap",pmSiloIssueMap);
	  eachPmSiloMap.put("pmGainVariance",pmTotVariance);
	  eachPmSiloMap.put("pmSiloClosingMap",pmSiloClosingMap);
	  
	 pmRegisterMap.put(eachMpuSiloId,eachPmSiloMap);
	
		
	}
	totPmOpenReceiptQty=totPmOpeningQty+totPmReceiptQty;
	totPmDayClosingQty=totPmOpenReceiptQty+totPmVarianceQty-totPmIssueQty;
	
	pmSilosTotalsMap.put("totPmOpeningQty",totPmOpeningQty);
	pmSilosTotalsMap.put("totPmReceiptQty",totPmReceiptQty);
	pmSilosTotalsMap.put("totPmOpenReceiptQty",totPmOpenReceiptQty);
	pmSilosTotalsMap.put("totPmVarianceQty",totPmVarianceQty);
	pmSilosTotalsMap.put("totPmIssueQty",totPmIssueQty);
	pmSilosTotalsMap.put("totPmDayClosingQty",totPmDayClosingQty);
	
	eachSiloTypeMap.put("pmRegisterMap",pmRegisterMap);
	eachSiloTypeMap.put("pmSilosTotalsMap",pmSilosTotalsMap);
	mpuAllSilosMap.put(eachSiloType,eachSiloTypeMap);
}

context.mpuAllSilosMap=mpuAllSilosMap;

