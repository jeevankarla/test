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
fromDate=parameters.storeAbstFromDate;
thruDate=parameters.storeAbstThruDate;
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
conditionList=[];
conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)));
conditionList.add(EntityCondition.makeCondition("custRequestDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
storeAbst= delegator.findList("CustRequestAndCustRequestItem",condition,UtilMisc.toSet("custRequestId","custRequestDate","productId","custRequestItemSeqId","fromPartyId"),null,null,false);
deptIds = EntityUtil.getFieldListFromEntityList(storeAbst, "fromPartyId", true);
productIds = EntityUtil.getFieldListFromEntityList(storeAbst, "productId", true);
productCatDetails = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productId", EntityOperator.IN , productIds)  , null, null, null, false );
productCatIds = EntityUtil.getFieldListFromEntityList(productCatDetails,"productCategoryId", true);

if(UtilValidate.isNotEmpty(deptIds)){
	prodDeptMap=[:];
	deptIds.each{fromPartyId->
	deptName =  PartyHelper.getPartyName(delegator, fromPartyId, false);
if(UtilValidate.isNotEmpty(productCatIds)){
	prodMap=[:];
	
	productCatIds.each{productCatId->
	prodList=[];	
if(UtilValidate.isNotEmpty(storeAbst)){
 storeAbst.each{custReq->
	  productDetailMap=[:];
	  
	  custRequestId=custReq.custRequestId;
	  custRequestDate=custReq.custRequestDate;
	  productId=custReq.productId;
	  custRequestItemSeqId=custReq.custRequestItemSeqId;
	  productDetailMap["custRequestId"]=custRequestId;
	  productDetailMap["custRequestDate"]=custRequestDate;
	  
	  
	  productUomDetails = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS , productId)  , null, null, null, false );
	  productUomDetails=EntityUtil.getFirst(productUomDetails);
	  
	  productDetailMap["productId"]=productUomDetails.productId;
	  productDetailMap["description"]=productUomDetails.description;
	  uomId=productUomDetails.quantityUomId;
	  if(UtilValidate.isNotEmpty(uomId)){
		  uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
		  uomDesc=EntityUtil.getFirst(uomDesc);
		  productDetailMap["unit"]=uomDesc.abbreviation;
	  }
	  conditionList.clear();
	  conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	  conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
	  condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	  storeAbstQtyDetails= delegator.findList("ItemIssuanceInventoryItemAndProduct",condition,null,null,null,false);
	  totQty=0;
	  totunitCost=0;
	  if(UtilValidate.isNotEmpty(storeAbstQtyDetails)){
		  
		  storeAbstQtyDetails.each{storeAbstQty->
			  quantity=storeAbstQty.quantity;
			  totQty =totQty+quantity;
			  unitCost=storeAbstQty.unitCost;
			  totunitCost=totunitCost+unitCost;
		  }
		  unitPrice=totQty/totunitCost;
		  totVal=	totQty*	unitPrice;
		  productDetailMap["totQty"]=totQty;
		  productDetailMap["unitPrice"]=unitPrice;
		  productDetailMap["totVal"]=totVal;
     }
	  if(UtilValidate.isEmpty(storeAbstQtyDetails)){
		  productDetailMap["totQty"]=0;
		  productDetailMap["unitPrice"]=0;
		  productDetailMap["totVal"]=0;
	  }
	  prodList.addAll(productDetailMap);
    }
   }
    prodMap.put(productCatId,prodList);
  }
 }
 prodDeptMap.put(deptName,prodMap);
}
	context.prodDeptMap=prodDeptMap;	
}
Debug.log("prodDeptMap==============="+prodDeptMap);