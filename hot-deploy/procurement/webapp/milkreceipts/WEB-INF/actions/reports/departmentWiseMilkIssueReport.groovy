
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.lang.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.sql.*;
import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilNumber;

import java.math.RoundingMode;
import java.util.Map;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;

import in.vasista.vbiz.milkReceipts.MilkReceiptReports;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.milkReceipts.MilkReceiptBillingServices;
dctx = dispatcher.getDispatchContext();

fromDeptId = parameters.fromDeptId;
thruDeptId = parameters.thruDeptId;
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;


if(UtilValidate.isEmpty(fromDeptId)){
	Debug.logError("fromDeptId Cannot Be Empty","");
	context.errorMessage = "fromDeptId Cannot Be Empty, Please Enter fromDeptId";
	return;
}
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

totalDays= UtilDateTime.getIntervalInDays(dayBegin,dayEnd)+1;


Map inMap = FastMap.newInstance();
inMap.put("userLogin", userLogin);
inMap.put("shiftType", "MILK_SHIFT");
inMap.put("fromDate", dayBegin);
inMap.put("thruDate", dayEnd);
Map workShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,inMap );
fromDate=workShifts.fromDate;
thruDate=workShifts.thruDate;
context.fromDate = fromDate;
context.thruDate = dayEnd;

Map allDeptsIssuesMap = FastMap.newInstance();

List facilityList = delegator.findList("Facility", null, null,null, null, false);

conditionList =[];
conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, fromDeptId));
conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PLANT"));
EntityCondition facilityIssueDeptCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
issuedToDeptsList=EntityUtil.filterByCondition(facilityList,facilityIssueDeptCond );
issuedToDepts=EntityUtil.getFieldListFromEntityList(issuedToDeptsList, "ownerPartyId", true);
fromDeptStorageIds=null;

fromDeptSiloList=EntityUtil.filterByCondition(facilityList, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,fromDeptId));
if(UtilValidate.isNotEmpty(fromDeptSiloList)){
	fromDeptStorageIds=EntityUtil.getFieldListFromEntityList(fromDeptSiloList, "facilityId", true);
	fromDeptNameList=EntityUtil.filterByCondition(fromDeptSiloList, EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS,"PLANT"));
	if(UtilValidate.isNotEmpty(fromDeptNameList)){
		fromDeptNames = EntityUtil.getFirst(fromDeptNameList);
		context.fromDeptId = fromDeptNames.facilityName;
	}
		
}
List allInvTransGroupMemSumList=FastList.newInstance();
List custRequestAndIssuanceList=FastList.newInstance();
if(UtilValidate.isNotEmpty(fromDeptStorageIds)){
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN,fromDeptStorageIds ));
	conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"IXF_COMPLETE"));
	EntityCondition invTransMainCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	allSiloInvTransferList = delegator.findList("InventoryTransfer", invTransMainCond, null,null, null, false);
	if(UtilValidate.isNotEmpty(allSiloInvTransferList)){
		allinvTransIds=EntityUtil.getFieldListFromEntityList(allSiloInvTransferList, "inventoryTransferId", true);
		allInvTransGroupMemList = delegator.findList("InventoryTransferGroupMember", EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,allinvTransIds ), null,null, null, false);
		allInvTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(allInvTransGroupMemList, "transferGroupId", false));
		allInvTransGroupMemSumList = delegator.findList("InventoryTransferGroupAndMemberSum", EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,allInvTransGroupIds ), null,null, null, false);
	}
	
	conditionList.clear();
	conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN , fromDeptStorageIds));
	conditionList.add(EntityCondition.makeCondition("itemIssuanceId", EntityOperator.NOT_EQUAL , null));
	conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	inventoryItemAndDetailList = delegator.findList("InventoryItemAndDetail", condition, null,null, null, false);
	itemIssuanceIds=EntityUtil.getFieldListFromEntityList(inventoryItemAndDetailList, "itemIssuanceId", true);
	custRequestAndIssuanceList = delegator.findList("CustRequestAndIssuance", EntityCondition.makeCondition("itemIssuanceId", EntityOperator.IN,itemIssuanceIds), null,null, null, false);
	//milkIsssuedDepts = new HashSet(EntityUtil.getFieldListFromEntityList(custRequestAndIssuanceList, "fromPartyId", false));
}
//issuedToDepts=["INT10"];
if(UtilValidate.isNotEmpty(issuedToDepts)){
	issuedToDepts.each{eachMilkIssuedDept->
		Map eachDeptIssuesMap = FastMap.newInstance();
		BigDecimal deptIssuedQty = BigDecimal.ZERO;
		BigDecimal deptTransferQty = BigDecimal.ZERO;
		eachDeptMilkIssuesList=EntityUtil.filterByCondition(custRequestAndIssuanceList, EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS,eachMilkIssuedDept));
		eachDeptItemIssuIds=null;
		if(UtilValidate.isNotEmpty(eachDeptMilkIssuesList)){
			eachDeptItemIssuIds = new HashSet(EntityUtil.getFieldListFromEntityList(eachDeptMilkIssuesList, "itemIssuanceId", false));
		}
		toDeptStorageIds=null;
		toDeptSiloList=EntityUtil.filterByCondition(facilityList, EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,eachMilkIssuedDept));
		if(UtilValidate.isNotEmpty(toDeptSiloList)){
			toDeptStorageIds=EntityUtil.getFieldListFromEntityList(toDeptSiloList, "facilityId", true);
		}
		
		for(int j=0 ; j < (totalDays); j++){
			Timestamp issueDate = UtilDateTime.addDaysToTimestamp(dayBegin, j);
		    Map dateMap = FastMap.newInstance();
			dateMap.put("userLogin", userLogin);
			dateMap.put("shiftType", "MILK_SHIFT");
			dateMap.put("fromDate", issueDate);
			Map issueShifts = MilkReceiptBillingServices.getShiftDaysByType(dctx,dateMap );
			issueDateStart=issueShifts.fromDate;
			issueDateEnd=issueShifts.thruDate;
			
			Map eachDayIssueMap = FastMap.newInstance();
			// Dept Inventory Issues
			if(UtilValidate.isNotEmpty(eachDeptItemIssuIds)){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("itemIssuanceId", EntityOperator.IN , eachDeptItemIssuIds));
				conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,issueDateStart));
				conditionList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, issueDateEnd));
				EntityCondition issueInvCon = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				eachDeptMilkIssueInvList=EntityUtil.filterByCondition(inventoryItemAndDetailList, issueInvCon);
				eachDeptMilkIssueInvList.each{eachMilkIssueInv->
					BigDecimal issueQty = BigDecimal.ZERO;
					issueQty=-(eachMilkIssueInv.quantityOnHandDiff);
					productId=eachMilkIssueInv.productId;
					if(UtilValidate.isEmpty(eachDayIssueMap) || (UtilValidate.isNotEmpty(eachDayIssueMap) && UtilValidate.isEmpty(eachDayIssueMap.get(productId)))){
						eachDayIssueMap.put(productId, issueQty);
					}else{
					  tempQty=0;
					  tempQty=eachDayIssueMap.get(productId);
					  tempQty=tempQty+issueQty;
					  eachDayIssueMap.put(productId, tempQty);
					}

				}
			}
			
			// Dept Inventory Transfers
			if(UtilValidate.isNotEmpty(toDeptStorageIds)){
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO,issueDateStart));
				conditionList.add(EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO, issueDateEnd));
				EntityCondition invTransEachCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				eachDeptInvTransList=EntityUtil.filterByCondition(allSiloInvTransferList, invTransEachCond);
			    List eachDeptInvTransGroupMemSumList=[];
				if(UtilValidate.isNotEmpty(eachDeptInvTransList)){
					eachDeptinvTransIds=EntityUtil.getFieldListFromEntityList(eachDeptInvTransList, "inventoryTransferId", true);
					eachDeptInvTransGroupMemList=EntityUtil.filterByCondition(allInvTransGroupMemList, EntityCondition.makeCondition("inventoryTransferId", EntityOperator.IN,eachDeptinvTransIds ));
					eachDeptInvTransGroupIds = new HashSet(EntityUtil.getFieldListFromEntityList(eachDeptInvTransGroupMemList, "transferGroupId", false));
					eachDeptInvTransGroupMemSumList=EntityUtil.filterByCondition(allInvTransGroupMemSumList, EntityCondition.makeCondition("transferGroupId", EntityOperator.IN,eachDeptInvTransGroupIds ));
				
				}
				if(UtilValidate.isNotEmpty(eachDeptInvTransGroupMemSumList)){
					toDeptInvTransGroupList=EntityUtil.filterByCondition(eachDeptInvTransGroupMemSumList, EntityCondition.makeCondition("toFacilityId", EntityOperator.IN,toDeptStorageIds));
					if(UtilValidate.isNotEmpty(toDeptInvTransGroupList)){
						toDeptInvTransGroupList.each{eachToDeptIssuTransfer->
							BigDecimal transferQty = BigDecimal.ZERO;
							transferQty=eachToDeptIssuTransfer.xferQtySum;
							transferProdId=eachToDeptIssuTransfer.productId;
							
							if(UtilValidate.isEmpty(eachDayIssueMap) || (UtilValidate.isNotEmpty(eachDayIssueMap) && UtilValidate.isEmpty(eachDayIssueMap.get(transferProdId)))){
								eachDayIssueMap.put(transferProdId, transferQty);
							}else{
							  tempQty=0;
							  tempQty=eachDayIssueMap.get(transferProdId);
							  tempQty=tempQty+transferQty;
							  eachDayIssueMap.put(transferProdId, tempQty);
							}
						}
					}
				}
					
			}
			if(UtilValidate.isNotEmpty(eachDayIssueMap)){
				eachDeptIssuesMap.put(issueDateStart,eachDayIssueMap);
			}
		}
		
		conditionList.clear();
		conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.NOT_EQUAL, eachMilkIssuedDept));
		conditionList.add(EntityCondition.makeCondition("facilityTypeId", EntityOperator.EQUALS, "PLANT"));
		EntityCondition IssueDeptNameCond = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		issuedDeptNameList=EntityUtil.filterByCondition(facilityList,IssueDeptNameCond );
		if(UtilValidate.isNotEmpty(issuedDeptNameList) && (UtilValidate.isNotEmpty(eachDeptIssuesMap))){
			Map issuedDeptNameMap = EntityUtil.getFirst(issuedDeptNameList);
			allDeptsIssuesMap.put(issuedDeptNameMap.get("facilityName"), eachDeptIssuesMap);
		}
		
		
	}
}
context.allDeptsIssuesMap=allDeptsIssuesMap;	

	

