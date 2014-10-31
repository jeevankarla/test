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
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;


fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.put("fromDate", fromDate);
dayEnd=UtilDateTime.getDayEnd(fromDate);
dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
finalMap = [:];
procurementProductList =[];
shedUnitsMap=[:];
facilityIds=[];
qty=[];
Map finalQtyMap=FastMap.newInstance();
List receiptsList = FastList.newInstance();
conditionList =[];
milkDetailslist=[];
conditionList.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS , "MAIN_PLANT"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS , "MXF_RECD"));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,null,null,false);
totalQty=0;
totKgFat=0;
totKgSnf=0;

if(UtilValidate.isNotEmpty(milkDetailslist)){
	receiptsList.addAll(milkDetailslist);
	  milkDetailslist.each{ milkDtls->
	  BigDecimal sendQty = BigDecimal.ZERO;
	  BigDecimal RecQty = BigDecimal.ZERO;
	  facilityIds=milkDtls.getString("facilityId");
	  facilityDetails = delegator.findOne("Facility", ["facilityId" :facilityIds], true);
	  
	  facilityAttribute = delegator.findOne("FacilityAttribute", ["facilityId" :facilityDetails.get("parentFacilityId"), "attrName":"enableQuantityKgs"], true);
	  String facilityAttrValue= "N";
	  if(UtilValidate.isNotEmpty(facilityAttribute)){
			facilityAttrValue = facilityAttribute.attrValue;
		  }
	  sendQty=milkDtls.get("quantityLtrs");
	  if("Y".equalsIgnoreCase(facilityAttrValue)){
		 sendQty=milkDtls.get("quantity");
		  }
	  RecQty=milkDtls.get("receivedQuantityLtrs");
	  kgFat=milkDtls.get("receivedKgFat");
	  kgSnf=milkDtls.get("receivedKgSnf");
	  kgFat=kgFat.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	  totKgFat=totKgFat+kgFat;
	  kgSnf=kgSnf.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	  totKgSnf=totKgSnf+kgSnf;
	  qtyKgs=milkDtls.get("receivedQuantity");
	  qtyKgs=qtyKgs.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	  totalQty=totalQty+qtyKgs;
	  String milkTranferId= null;
	  milkTranferId=milkDtls.getString("milkTransferId");
	  Map milkQtyMap = FastMap.newInstance();
	  milkQtyMap.put("send",sendQty);
	  milkQtyMap.put("recd",RecQty);
	  if(UtilValidate.isEmpty(finalQtyMap.get(milkTranferId))){
		  finalQtyMap.put(milkTranferId, milkQtyMap);
	  }
	  else{
		  Map tempQtyMap=FastMap.newInstance();
		  tempQtyMap=finalQtyMap.get(milkTranferId);
		  tempQtyMap.put("send",tempQtyMap.get("send")+sendQty);
		  tempQtyMap.put("recd",tempQtyMap.get("recd")+RecQty);
		  finalQtyMap.put(milkTranferId, tempQtyMap);
	  }
  }
	  
}
context.putAt("totKgFat", totKgFat);
context.putAt("totKgSnf", totKgSnf);
context.putAt("totalQty", totalQty);
receiptsList = UtilMisc.sortMaps(receiptsList, UtilMisc.toList("milkTransferId"));
 context.putAt("milkDetailslist",receiptsList);
 context.putAt("finalQtyMap",finalQtyMap);
 context.putAt("listSize",receiptsList.size());