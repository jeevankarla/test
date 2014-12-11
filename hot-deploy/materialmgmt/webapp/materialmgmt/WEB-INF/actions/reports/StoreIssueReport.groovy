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
storeIssueReceipts = MaterialHelperServices.getMaterialReceiptsForPeriod(dctx, [fromDate:dayBegin, thruDate:dayEnd,isForMrrReg:"Y"]);
MaterialReceiptRegister=storeIssueReceipts.get("MaterialReceiptRegisterMap");
MrrList=[];
if(UtilValidate.isNotEmpty(MaterialReceiptRegister)){
	MaterialReceiptRegister.each{MrrDetails->
		 MrrDetailsMap=[:];
		MrrDetailsMap.put("Date",MrrDetails.getValue().get("datetimeReceived"));
		MrrDetailsMap.put("BillNo",MrrDetails.getValue().get("billNo"));
		MrrDetailsMap.put("MRRNo",MrrDetails.getValue().get("receiptId"));
		MrrList.add(MrrDetailsMap);
	}
}
context.MrrList=MrrList;
receiptList =storeIssueReceipts.get("receiptsList");
ReceiptList=[];
if(UtilValidate.isNotEmpty(receiptList)){
	receiptList.each{receiptDetails->
		receiptDetailsMap=[:];
		receiptDetailsMap.put("ReceiptQty",receiptDetails.get("quantity"));
		receiptDetailsMap.put("ReceiptRate",receiptDetails.get("price"));
		receiptDetailsMap.put("ReceiptAmount",receiptDetails.get("amount"));
		ReceiptList.add(receiptDetailsMap);
	}
}
context.ReceiptList=ReceiptList;
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
Debug.log("materialList==============="+materialList);
storeIssue=MaterialHelperServices.getCustRequestIssuancesForPeriod(dctx,[fromDate:dayBegin, thruDate:dayEnd,userLogin : userLogin]);
StoreIssueList=storeIssue.get("itemIssuanceList");
issueList=[];
if(UtilValidate.isNotEmpty(StoreIssueList)){
	StoreIssueList.each{storeIssueDetails->
		storeIssueDetailsMap=[:];
			storeIssueDetailsMap.put("IndentNo",storeIssueDetails.get("custRequestId"));
			storeIssueDetailsMap.put("IssueQty",storeIssueDetails.get("quantity"));
			storeIssueDetailsMap.put("IssueRate",storeIssueDetails.get("price"));
			storeIssueDetailsMap.put("IssueAmount",storeIssueDetails.get("amount"));			
			issueList.add(storeIssueDetailsMap);
	}
}
context.issueList=issueList;
//storeDetails=MaterialHelperServices.getMaterialStores(dctx,[fromDate:dayBegin, thruDate:dayEnd,userLogin : userLogin]);
//StoreList=storeDetails.get("storesList");
//finalList=[];
//if(UtilValidate.isNotEmpty(StoreList)){
//	StoreList.each{StoreListDetails->
//		StoreListDetailsMap=[:];
//		StoreListDetailsMap.put("facilityId",StoreListDetails.get("facilityId"));
//		finalList.add(StoreListDetailsMap);
//	}
//}
//context.finalList=finalList;		
//Debug.log("store========================"+store);


//f(UtilValidate.isNotEmpty(productId)){
//	prodDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId)  , null, null, null, false );
//	prodName=prodDetails.productName;
//	prodCAtegory=prodDetails.primaryProductCategoryId;
//	materialCode=prodDetails.internalName;


