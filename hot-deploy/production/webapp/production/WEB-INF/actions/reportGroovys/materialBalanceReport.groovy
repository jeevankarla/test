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
import org.ofbiz.party.party.PartyHelper;

fromDate=parameters.mateBalanceFromDate;
thruDate=parameters.mateBalanceThruDate;

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);

context.fromDate = dayBegin;
context.thruDate = dayEnd;

totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);


//OPENING BALANCE REPORT ===============>
siloFacilitiList = delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS , "SILO")  , null, null, null, false );
siloIds=EntityUtil.getFieldListFromEntityList(siloFacilitiList, "facilityId", true);
//siloList = ProductionServices.getSilos(delegator, [userLogin: userLogin,]);
allSiloDetailsMap=[:];
allSiloOpeningList=[];
BigDecimal totInventoryQty = BigDecimal.ZERO;
BigDecimal totOpenFatQtyKg = BigDecimal.ZERO;
BigDecimal totOpenSnfQtyKg = BigDecimal.ZERO;
openTotBalSiloMap=[:];

if(UtilValidate.isNotEmpty(siloIds)){
	siloIds.each {eachSiloId->
		openingBalSiloMap=[:];
		
		BigDecimal openingQty = BigDecimal.ZERO;
		BigDecimal openingFatKg = BigDecimal.ZERO;
		BigDecimal openingSnfKg = BigDecimal.ZERO;
		BigDecimal openingFatPers = BigDecimal.ZERO;
		BigDecimal openingSnfPers = BigDecimal.ZERO;
		
		invCountMap = ProductionServices.getSiloInventoryOpeningBalance(dctx, [effectiveDate:dayBegin, facilityId: eachSiloId, userLogin: userLogin,]);
		invCountMapData=invCountMap.openingBalance;
		if(UtilValidate.isNotEmpty(invCountMapData)){
			// && invCountMapData.get("quantityKgs") >0
			openingQty = invCountMapData.get("quantityKgs");
			openingFatKg=invCountMapData.get("kgFat");
			openingSnfKg=invCountMapData.get("kgSnf");
			openingFatPers=invCountMapData.get("fat");
			openingSnfPers=invCountMapData.get("snf");
			if(UtilValidate.isNotEmpty(openingQty) && "0".equals(openingQty)){
		        facilityNames = delegator.findOne("Facility",["facilityId":eachSiloId],false);
		        siloName=facilityNames.get("facilityName");
				openingBalSiloMap.put("description", siloName);
				openingBalSiloMap.put("quantity", openingQty);
				openingBalSiloMap.put("fatKg", openingFatKg);
				openingBalSiloMap.put("snfKg", openingSnfKg);
				openingBalSiloMap.put("fatPers", openingFatPers);
				openingBalSiloMap.put("snfPers", openingSnfPers);
				
				allSiloOpeningList.addAll(openingBalSiloMap)
				
				totInventoryQty=totInventoryQty+openingQty;
				totOpenFatQtyKg=totOpenFatQtyKg+openingFatKg;
				totOpenSnfQtyKg=totOpenSnfQtyKg+openingSnfKg;
			}
		}
	}
	openTotBalSiloMap.put("description", "Total");
	openTotBalSiloMap.put("quantity", totInventoryQty);
	openTotBalSiloMap.put("fatKg", totOpenFatQtyKg);
	openTotBalSiloMap.put("snfKg", totOpenSnfQtyKg);
  
    allSiloOpeningList.addAll(openTotBalSiloMap);
    allSiloDetailsMap.put("Opening Balance",allSiloOpeningList);
}  



// CONVERSION REPORT======================>
conditionList =[];
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["MXF_RECD","MXF_APPROVED"]));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);

unions=null;
if(UtilValidate.isNotEmpty(MilkTransferList)){
unions=EntityUtil.getFieldListFromEntityList(MilkTransferList, "partyId", true);
}
allUnionConvList=[];

BigDecimal totConvQty = BigDecimal.ZERO;
BigDecimal totConvFatQtyKg = BigDecimal.ZERO;
BigDecimal totConvSnfQtyKg = BigDecimal.ZERO;
convTotSiloMap=[:];

if(UtilValidate.isNotEmpty(unions)){
	unions.each {union->
		conversionSiloMap=[:];
		
		BigDecimal conversionQty = BigDecimal.ZERO;
		BigDecimal conversionFatKg = BigDecimal.ZERO;
		BigDecimal conversionSnfKg = BigDecimal.ZERO;
		BigDecimal conversionFatPers = BigDecimal.ZERO;
		BigDecimal conversionSnfPers = BigDecimal.ZERO;
		
		unionList=EntityUtil.filterByCondition(MilkTransferList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, union));
		if(UtilValidate.isNotEmpty(unionList)){
			unionList.each {unionData->
				if(UtilValidate.isNotEmpty(unionData)){
					// && unionData.get("receivedQuantity") >0
				conversionQty=conversionQty+unionData.receivedQuantity;
				conversionFatKg=conversionFatKg+unionData.receivedKgFat;
				conversionSnfKg=conversionSnfKg+unionData.receivedKgSnf;
				conversionFatPers=conversionFatPers+unionData.receivedFat;
				conversionSnfPers=conversionSnfPers+unionData.receivedSnf;
				}
			}
			unionName =  PartyHelper.getPartyName(delegator, union, false);
			conversionSiloMap.put("description", unionName);
			conversionSiloMap.put("quantity", conversionQty);
			conversionSiloMap.put("fatKg", conversionFatKg);
			conversionSiloMap.put("snfKg", conversionSnfKg);
			conversionSiloMap.put("fatPers", conversionFatPers);
			conversionSiloMap.put("snfPers", conversionSnfPers);

			allUnionConvList.addAll(conversionSiloMap);
			
			totConvQty=totConvQty+conversionQty;
			totConvFatQtyKg=totConvFatQtyKg+conversionFatKg;
			totConvSnfQtyKg=totConvSnfQtyKg+conversionSnfKg;
		}
	}
	convTotSiloMap.put("description", "Total");
	convTotSiloMap.put("quantity", totConvQty);
	convTotSiloMap.put("fatKg", totConvFatQtyKg);
	convTotSiloMap.put("snfKg", totConvSnfQtyKg);
  
    allUnionConvList.addAll(convTotSiloMap);
	allSiloDetailsMap.put("Conversion",allUnionConvList);
 }



// PURCHASE REPORT==================================>
unionsIDR=null;
if(UtilValidate.isNotEmpty(MilkTransferList)){
unionsIDR=EntityUtil.getFieldListFromEntityList(MilkTransferList, "partyId", true);
}
allUnionIDRList=[];

BigDecimal totIDRQty = BigDecimal.ZERO;
BigDecimal totIDRFatQtyKg = BigDecimal.ZERO;
BigDecimal totIDRSnfQtyKg = BigDecimal.ZERO;
idrTotSiloMap=[:];

if(UtilValidate.isNotEmpty(unionsIDR)){
	unionsIDR.each {unionIDR->
		idrSiloMap=[:];
		
		BigDecimal idrQty = BigDecimal.ZERO;
		BigDecimal idrFatKg = BigDecimal.ZERO;
		BigDecimal idrSnfKg = BigDecimal.ZERO;
		BigDecimal idrFatPers = BigDecimal.ZERO;
		BigDecimal idrSnfPers = BigDecimal.ZERO;
		
		unionIDRList=EntityUtil.filterByCondition(MilkTransferList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, unionIDR));
		if(UtilValidate.isNotEmpty(unionIDRList)){
			unionIDRList.each {unionIDRData->
				if(UtilValidate.isNotEmpty(unionIDRData)){
					// && unionData.get("receivedQuantity") >0
				idrQty=idrQty+unionIDRData.receivedQuantity;
				idrFatKg=idrFatKg+unionIDRData.receivedKgFat;
				idrSnfKg=idrSnfKg+unionIDRData.receivedKgSnf;
				idrFatPers=idrFatPers+unionIDRData.receivedFat;
				idrSnfPers=idrSnfPers+unionIDRData.receivedSnf;
				}
			}
			unionIDRName =  PartyHelper.getPartyName(delegator, unionIDR, false);
			idrSiloMap.put("description", unionIDRName);
			idrSiloMap.put("quantity", idrQty);
			idrSiloMap.put("fatKg", idrFatKg);
			idrSiloMap.put("snfKg", idrSnfKg);
			idrSiloMap.put("fatPers", idrFatPers);
			idrSiloMap.put("snfPers", idrSnfPers);

			allUnionIDRList.addAll(idrSiloMap);
			
			totIDRQty=totIDRQty+idrQty;
			totIDRFatQtyKg=totIDRFatQtyKg+idrFatKg;
			totIDRSnfQtyKg=totIDRSnfQtyKg+idrSnfKg;
		}
	}
	idrTotSiloMap.put("description", "Total");
	idrTotSiloMap.put("quantity", totIDRQty);
	idrTotSiloMap.put("fatKg", totIDRFatQtyKg);
	idrTotSiloMap.put("snfKg", totIDRSnfQtyKg);
  
	allUnionIDRList.addAll(idrTotSiloMap);
	allSiloDetailsMap.put("Purchase",allUnionIDRList);
 }

// ISSUE DETAILS==============================>
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,siloIds ));
EntityCondition cond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
InventoryItemAndDetailList = delegator.findList("InventoryItemAndDetail", cond, null,null, null, false);

allProductIssuList=[];

BigDecimal totIssueQty = BigDecimal.ZERO;
productTotIssueMap=[:];

workEffortIds=EntityUtil.getFieldListFromEntityList(InventoryItemAndDetailList, "workEffortId", true);
if(UtilValidate.isNotEmpty(workEffortIds)){
	
    workEffortList = delegator.findList("WorkEffort",EntityCondition.makeCondition("workEffortId", EntityOperator.IN , workEffortIds)  , null, null, null, false );
    issuworkEffIds=EntityUtil.getFieldListFromEntityList(workEffortList, "workEffortId", true);
	
    inventoryItemDetails=EntityUtil.filterByCondition(InventoryItemAndDetailList, EntityCondition.makeCondition("workEffortId", EntityOperator.IN,issuworkEffIds));
    issuedProdIds=EntityUtil.getFieldListFromEntityList(inventoryItemDetails, "productId", true);
	issuedProdIds.each{issuedProdId->
	  productIssueMap=[:];
	  BigDecimal isssudQty = BigDecimal.ZERO;
      produtIssueDetails=EntityUtil.filterByCondition(inventoryItemDetails, EntityCondition.makeCondition("productId", EntityOperator.EQUALS,issuedProdId));
	  produtIssueDetails.each{produtIssueDetail->
	      isssudQty=isssudQty+produtIssueDetail.quantityOnHandDiff;
	  }
	  totIssueQty=totIssueQty+isssudQty;
	  productNames = delegator.findOne("Product",["productId":issuedProdId],false);
	  productName=productNames.get("description");
	  productIssueMap.put("description", productName);
	  productIssueMap.put("quantity", -isssudQty);
	  allProductIssuList.addAll(productIssueMap);
    }	
	productTotIssueMap.put("description", "Total");
	productTotIssueMap.put("quantity", -totIssueQty);

	allProductIssuList.addAll(productTotIssueMap);
	allSiloDetailsMap.put("Issued Products",allProductIssuList);

}
context.allSiloDetailsMap=allSiloDetailsMap;

