import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.product.inventory.InventoryServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.fromDate;
thruDate=parameters.thruDate;
productId=parameters.productId;
issueToFacilityId=parameters.issueToFacilityId;
context.put("issueToFacilityId",issueToFacilityId);
reportTypeFlag = parameters.reportTypeFlag;
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
fromDateTime = UtilDateTime.getDayStart(fromDateTime);
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
context.fromDate = dayBegin;
context.thruDate = dayEnd;
totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}

prodDetails = delegator.findOne("Product", [productId : productId], false);
if(UtilValidate.isNotEmpty(prodDetails)){
   materialName = prodDetails.productName;
  context.put("materialName",materialName);
}
allDetailsMap=[:];	

List currentDateKeysList = [];
totalDays=totalDays+1;
for(int i=0; i <totalDays; i++){
currentDayStart = UtilDateTime.getDayStart(fromDateTime, i);
currentDayEnd = UtilDateTime.getDayEnd(currentDayStart);

date = UtilDateTime.toDateString(currentDayStart);
//currentDateKeysList.add(date);

storeIssueReceipts = MaterialHelperServices.getMaterialReceiptsForPeriod(dctx, [fromDate:currentDayStart, thruDate:currentDayEnd,productId:productId,facilityId:issueToFacilityId,isForMrrReg:"Y"]);
MaterialReceiptRegister=storeIssueReceipts.get("MaterialReceiptRegisterMap");
receiptList =storeIssueReceipts.get("receiptsList");

storeIssue=MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx,[fromDate:currentDayStart, thruDate:currentDayEnd,productId:productId,facilityId:issueToFacilityId,userLogin : userLogin]);
StoreIssueList=storeIssue.get("itemIssuanceList");

bookStock=InventoryServices.getProductInventoryOpeningBalance(dctx,[ effectiveDate:currentDayEnd, productId:productId,facilityId:issueToFacilityId,userLogin : userLogin]);
inventoryCount = bookStock.inventoryCount;

//bookStock = dispatcher.runSync("getInventoryAvailableByFacility", [fromDate:currentDayStart, thruDate:currentDayEnd,productId :productId, facilityId : issueToFacilityId ,ownerPartyId :"Company"]);
//productDetailMap["inventoryCount"]=bookStock.quantityOnHandTotal;


// inventoryCount+reciptTotQty - issuetotQty= closingQty

receiptIssuesMap=[:];
MrrMap=[:];

if(UtilValidate.isNotEmpty(receiptList)){
	
		
	receiptList.each{receiptData->
		 MrrDetailsMap=[:];
		 
		 receiptId=receiptData.receiptId;
		 ReceiptQty=receiptData.quantity;
		 ReceiptRate=receiptData.price;
		 ReceiptAmount=receiptData.amount;
		 receivedDate=receiptData.datetReceived;
		 
		 mrrDetails=MaterialReceiptRegister.get(receiptId);				 
		 billNo=mrrDetails.billNo;
		 
		//MrrDetailsMap.put("receiptId",receiptId);
		MrrDetailsMap.put("ReceiptQty",ReceiptQty);
		MrrDetailsMap.put("ReceiptRate",ReceiptRate);
		MrrDetailsMap.put("ReceiptAmount",ReceiptAmount);
		MrrDetailsMap.put("receivedDate",receivedDate);
		MrrDetailsMap.put("billNo",billNo);
		
		MrrMap.put(receiptId,MrrDetailsMap);
	}
	receiptIssuesMap.put("MrrMap",MrrMap);
}

issueMap=[:];
if(UtilValidate.isNotEmpty(StoreIssueList)){
	StoreIssueList.each{storeIssueDetails->
		storeIssueDetailsMap=[:];
		indentNo=storeIssueDetails.get("custRequestId")
		custRequestDate=storeIssueDetails.get("custRequestDate")
		issueDate = UtilDateTime.toDateString(custRequestDate);		
		storeIssueDetailsMap.put("issueDate",storeIssueDetails.get("issueDate"));
		//storeIssueDetailsMap.put("IndentNo",storeIssueDetails.get("custRequestId"));
		storeIssueDetailsMap.put("IssueQty",storeIssueDetails.get("quantity"));
		storeIssueDetailsMap.put("IssueRate",storeIssueDetails.get("price"));
		storeIssueDetailsMap.put("IssueAmount",storeIssueDetails.get("amount"));			
		issueMap.put(indentNo,storeIssueDetailsMap);
	}
	receiptIssuesMap.put("issueMap",issueMap);
	
}
if(UtilValidate.isNotEmpty(receiptIssuesMap)){	
allDetailsMap.put(date, receiptIssuesMap)
}
}
context.allDetailsMap=allDetailsMap;





/*
 ProdTotalsList=storeIssueReceipts.get("productTotals");
 materialList=[];
 if(UtilValidate.isNotEmpty(ProdTotalsList)){
	 ProdTotalsList.each{ProdTotalsDetails->
		 ProdTotalsDetailsMap=[:];
		 ProdTotalsDetailsMap.put("MaterialCode",ProdTotalsDetails.getKey());
		 if(UtilValidate.isNotEmpty(ProdTotalsDetailsMap.get("MaterialCode"))){
			ProdDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS,ProdTotalsDetailsMap.get("MaterialCode")),null,null,null,false);
			materialName=ProdDetails.productName;
		 }
		 ProdTotalsDetailsMap.put("materialName",materialName);
		 materialList.add(ProdTotalsDetailsMap);
		 
	  }
 }
 context.materialList=materialList;
 
 Debug.log("materialList=============="+materialList);
 
 */


