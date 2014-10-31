import org.apache.http.util.EntityUtils;
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


SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
Timestamp fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
if(UtilValidate.isNotEmpty(parameters.get("fromDate"))){
	fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	}
thruDate = UtilDateTime.getDayEnd(fromDate);
if(UtilValidate.isNotEmpty(parameters.get("thruDate"))){
	thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(parameters.thruDate).getTime()));
	}
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
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
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO ,thruDate));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("quantityLtrs", EntityOperator.NOT_EQUAL,BigDecimal.ZERO),EntityOperator.OR,EntityCondition.makeCondition("receivedQuantityLtrs", EntityOperator.NOT_EQUAL,BigDecimal.ZERO)));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List orderBy = UtilMisc.toList("milkTransferId");
List<GenericValue> milkDetailslist = delegator.findList("MilkTransferAndMilkTransferItem",condition,null,orderBy,null,false);
Map itemsMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(milkDetailslist)){
	transferIdsList = (new HashSet(EntityUtil.getFieldListFromEntityList(milkDetailslist, "milkTransferId", true))).toList();
	for(transferId in  transferIdsList){
		List itemsList = EntityUtil.filterByAnd(milkDetailslist, [EntityCondition.makeCondition("milkTransferId", EntityOperator.EQUALS, transferId)]);
		itemsMap.put(transferId, itemsList.size());
		}
}




List foxproList = FastList.newInstance();
int rcNo = 1;
int rctNo = 1;
String previousTranId = "prev";
if(UtilValidate.isNotEmpty(milkDetailslist)){
	  milkDetailslist.each{ milkDtls->
	   String milkTransferId = milkDtls.get("milkTransferId");
	   
	   Debug.log("milkTransferId ::"+milkTransferId+" :: "+itemsMap.get(milkTransferId));
	   
	   BigDecimal sendQty = milkDtls.get("quantity");
	   BigDecimal recdQty = milkDtls.get("receivedQuantity");
	  	if(sendQty.compareTo(BigDecimal.ZERO)!= 0 || recdQty.compareTo(BigDecimal.ZERO)!= 0){
			  String facilityId = milkDtls.get("facilityId");
			  GenericValue facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
			  GenericValue shedDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityDetails.parentFacilityId),false);
			  int f_u = 1;
			  if(UtilValidate.isNotEmpty(shedDetails.get("mccTypeId"))){
				  String mccTypeId = (String)shedDetails.get("mccTypeId");
				  if(mccTypeId.equalsIgnoreCase("FEDERATION")){
					  	f_u = 1;
					  }else{
					  	f_u = 2;
					  }
				  }
			  String enableQuantityKgs = "N";
			  GenericValue facilityAttrbute = delegator.findOne("FacilityAttribute",UtilMisc.toMap("facilityId",shedDetails.get("facilityId"),"attrName","enableQuantityKgs"),false);
			  if(UtilValidate.isNotEmpty( facilityAttrbute)){
				  enableQuantityKgs = facilityAttrbute.get("attrValue");
				  }
			  
			  if(((previousTranId != "prev")&& previousTranId != milkTransferId)){
				  rctNo +=1;
				  }
			  
			   Map foxproMap = FastMap.newInstance();
			  
			  foxproMap.put("RCNO",rcNo);
			  foxproMap.put("RCTNO",rctNo);
			  foxproMap.put("SCODE",shedDetails.get("facilityCode"));
			  foxproMap.put("DCODE",facilityDetails.get("dCode"));
			  foxproMap.put("TCODE",facilityDetails.get("tCode"));
			  foxproMap.put("GCNO",facilityDetails.get("mccCode"));
			  foxproMap.put("GNAME",facilityDetails.get("facilityName"));
			  foxproMap.put("F_U",f_u);
			  foxproMap.put("GDATE",milkDtls.get("receiveDate"));
			  foxproMap.put("GTM",milkDtls.get("sendProductId"));
			  GenericValue productDetails = delegator.findOne("Product",UtilMisc.toMap("productId",milkDtls.get("sendProductId")),false);
			  foxproMap.put("G",(productDetails.get("brandName")).replace(' WM', ''));
			  foxproMap.put("MTYP",milkDtls.get("milkCondition"));
			  if(itemsMap.get(milkTransferId)>1){
				  foxproMap.put("GCELL",milkDtls.get("cellType"));
			  }
			  foxproMap.put("GTKNO",milkDtls.get("vehicleId"));
			  foxproMap.put("GTIME1",milkDtls.get("sendTime"));
			  
			  
			  foxproMap.put("GQTY1",milkDtls.get("quantityLtrs"));
			  if(UtilValidate.isNotEmpty(enableQuantityKgs)&& "Y".equalsIgnoreCase(enableQuantityKgs)){
				  foxproMap.put("GQTY1",milkDtls.get("quantity"));
			  }
			  
			  
			  foxproMap.put("GFAT1",milkDtls.get("fat"));
			  foxproMap.put("GSNF1",milkDtls.get("snf"));
			  foxproMap.put("GCLR1",milkDtls.get("sendLR"));
			  foxproMap.put("GACID1",milkDtls.get("sendAcidity"));
			  foxproMap.put("GTEMP1",milkDtls.get("sendTemparature"));
			  foxproMap.put("GC1",milkDtls.get("sendCob"));
			 
			  
			  foxproMap.put("GTIME2",milkDtls.get("ackTime"));
			  foxproMap.put("RECQTY","");
			  foxproMap.put("GQTY2",milkDtls.get("receivedQuantityLtrs"));
			  foxproMap.put("GFAT2",milkDtls.get("receivedFat"));
			  foxproMap.put("GSNF2",milkDtls.get("receivedSnf"));
			  foxproMap.put("GCLR2",milkDtls.get("receivedLR"));
			  foxproMap.put("GACID2",milkDtls.get("receivedAcidity"));
			  foxproMap.put("GTEMP2",milkDtls.get("receivedTemparature"));
			  foxproMap.put("GC2",milkDtls.get("receivedCob"));
			  
			  foxproMap.put("GKGT",milkDtls.get("receivedQuantity"));
			  foxproMap.put("GTKGF",milkDtls.get("receivedKgFat"));
			  foxproMap.put("GTKGSNF",milkDtls.get("receivedKgSnf"));
			  foxproMap.put("GTS",milkDtls.get("receivedKgSnf")+milkDtls.get("receivedKgFat"));
			  foxproMap.put("GCC",0);
			  foxproMap.put("GCAP",milkDtls.get("capacity"));
			  foxproMap.put("DTKGS",milkDtls.get("quantity"));
			  foxproMap.put("DTKGF",milkDtls.get("sendKgFat"));
			  foxproMap.put("DTKGSNF",milkDtls.get("sendKgSnf"));
			  
			  
			  foxproList.add(foxproMap);
			  previousTranId = milkTransferId;
			  
			  rcNo +=1; 
			 }
	  }
}
  context.putAt("foxproList", foxproList);