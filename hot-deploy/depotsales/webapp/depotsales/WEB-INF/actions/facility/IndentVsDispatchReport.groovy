import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.inventory.InventoryServices;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.util.EntityUtil;
import java.math.RoundingMode;

DateList=[];
DateMap = [:];
partyfromDate=parameters.ivdFromDate;
partythruDate=parameters.ivdThruDate;
partyId=parameters.partyId;
//state=parameters.state;
productCategory=parameters.productCategory;

DateMap.put("partyfromDate", partyfromDate);
DateMap.put("partythruDate", partythruDate);

DateList.add(DateMap);
context.DateList=DateList;
context.partyId=partyId;
//Debug.log("partyId=================="+parameters.partyId);
branchId = parameters.branchId;

branchName = "";

branchContext=[:];
branchContext.put("branchId",branchId);
if(branchId){
	branch = delegator.findOne("PartyGroup",[partyId : branchId] , false);
	//Debug.log("branch=================="+branch);
	branchName = branch.get("groupName");
	DateMap.put("branchName", branchName);
}
	branchList = [];
	
	condListb = [];
	if(branchId){
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, branchId));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "PARENT_ORGANIZATION"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	
	branchList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	
	//if(!branchList)
	branchList.add(branchId);
	}

	branchBasedWeaversList = [];
	condListb = [];
	if(branchList){
		
	condListb.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, branchList));
	condListb.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"));
	condListb = EntityCondition.makeCondition(condListb, EntityOperator.AND);
	
	PartyRelationship = delegator.findList("PartyRelationship", condListb,UtilMisc.toSet("partyIdTo"), null, null, false);
	branchBasedWeaversList=EntityUtil.getFieldListFromEntityList(PartyRelationship, "partyIdTo", true);
	
	}
	//Debug.log("PartyRelationship=================="+PartyRelationship);
	//Debug.log("partyId=================="+parameters.partyId);
	//Debug.log("branchBasedWeaversList=================="+branchBasedWeaversList);
	
	
	//Debug.log("branchList=================="+branchList);
	productIds = [];
	
	productCategoryIds = [];
	
	condListCat = [];
	
	//if(partyId){
		if(productCategory != "OTHER"){
			condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.EQUALS, productCategory));
			condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
			ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
			
			productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
		}else if(productCategory == "OTHER"){
		
			condListCat.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.NOT_IN, ["SILK","JUTE_YARN"]));
			condListC = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
			ProductCategory = delegator.findList("ProductCategory", condListC,UtilMisc.toSet("productCategoryId"), null, null, false);
			
			productCategoryIds = EntityUtil.getFieldListFromEntityList(ProductCategory, "productCategoryId", true);
		
		}
		
		condListCat.clear();
		condListCat.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIds));
		condList1 = EntityCondition.makeCondition(condListCat, EntityOperator.AND);
		ProductCategoryMember = delegator.findList("ProductCategoryMember", condList1,UtilMisc.toSet("productId"), null, null, false);
		
		productIds = EntityUtil.getFieldListFromEntityList(ProductCategoryMember, "productId", true);
		
	//}
	//Debug.log("productIds=================="+productIds);
	
fromDate = parameters.ivdFromDate;
thruDate = parameters.ivdThruDate;

context.fromDate=fromDate;
context.thruDate=thruDate;

def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	if (UtilValidate.isNotEmpty(fromDate)) {
		fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(fromDate).getTime()));
		thruDate = UtilDateTime.getDayEnd(new java.sql.Timestamp(sdf.parse(thruDate).getTime()));
	}
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: " + e, "");
context.errorMessage = "Cannot parse date string: " + e;
	return;
}

dayStart = UtilDateTime.getDayStart(fromDate);
dayEnd = UtilDateTime.getDayEnd(thruDate);
if(UtilValidate.isNotEmpty(branchList)){
		custCondList = [];
		if((UtilValidate.isNotEmpty(branchList))){
			if(UtilValidate.isNotEmpty(partyId)){
				custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
			
			custCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, branchBasedWeaversList));
		}
		custCond = EntityCondition.makeCondition(custCondList, EntityOperator.AND);
		orderRoles = delegator.findList("OrderRole", custCond, UtilMisc.toSet("orderId"), null, null, false);
		//orderRoles = delegator.findList("OrderRole", custCond, null, null, null, false);
		branchBasedOrderIds = EntityUtil.getFieldListFromEntityList(orderRoles, "orderId", true);
	}
//Debug.log("branchBasedOrderIds=================="+branchBasedOrderIds);
rounding = RoundingMode.HALF_UP;
Map finalMap = FastMap.newInstance();
tempTotMap=[:];
finalCSVList=[];
conditionList=[];
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, branchBasedOrderIds));
conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			conditionList.add(EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			conditionList.add(EntityCondition.makeCondition("purposeTypeId", EntityOperator.EQUALS, "BRANCH_SALES"));
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			OrderHeaderList = delegator.findList("OrderHeaderAndItems", condition, null, null, null, false); 
			OrderIdList = EntityUtil.getFieldListFromEntityList(OrderHeaderList, "orderId", true);
			
			//Debug.log("OrderIdList=================="+OrderIdList);
			//if(UtilValidate.isNotEmpty(parameters.header)&&parameters.header.equals("required")){
			headerData=[:];
			headerData.put("orderNo", " ");
			headerData.put("orderDate", " ");
			headerData.put("partyName", "INDENT VS DISPATCH REPORT");
			headerData.put("productName", " ");
			headerData.put("supplierName", " ");
			headerData.put("initialQty", " ");
			headerData.put("indentValue", " ");
			headerData.put("finalQty", " ");
			headerData.put("dispatchValue", " ");
			headerData.put("diffQty", " ");
			finalCSVList.add(headerData);
			headerData1=[:];
			headerData1.put("orderNo", " ");
			headerData1.put("orderDate", " ");
			headerData1.put("partyName", " ");
			headerData1.put("productName", " ");
			headerData1.put("supplierName", " ");
			headerData1.put("initialQty", " ");
			headerData1.put("indentValue", " ");
			headerData1.put("finalQty", " ");
			headerData1.put("dispatchValue", " ");
			headerData1.put("diffQty", " ");
			finalCSVList.add(headerData1);
			headerData2=[:];
			headerData2.put("orderNo", "ORDER ID");
			headerData2.put("orderDate", "ORDER DATE");
			headerData2.put("partyName", "PARTY NAME");
			headerData2.put("productName", "PRODUCT NAME");
			headerData2.put("supplierName", "SUPPLIER NAME");
			headerData2.put("initialQty", "INDENTED QUANTITY");
			headerData2.put("indentValue", "INDENT VALUE");
			headerData2.put("finalQty", "DISPATCHED QUANTITY");
			headerData2.put("dispatchValue", "DISPATCH VALUE");
			headerData2.put("diffQty", "DIFFERENCE QUANTITY");
			finalCSVList.add(headerData2);
			
			//}
			
			BigDecimal totInitialQty=BigDecimal.ZERO;
			BigDecimal totFinalQty=BigDecimal.ZERO;
			BigDecimal totDiffQty=BigDecimal.ZERO;
			BigDecimal totIndentValue=BigDecimal.ZERO;
			BigDecimal totDispatchValue=BigDecimal.ZERO;
	if(UtilValidate.isNotEmpty(OrderIdList)){
			
			OrderIdList.each{orderId->
				
				Map ProductWiseMap = FastMap.newInstance(); 
				List productList = EntityUtil.filterByAnd(OrderHeaderList, UtilMisc.toMap("orderId", orderId));
				//Debug.log("productList====================== ####### "+productList);
				if(UtilValidate.isNotEmpty(productList)){
					productList.each{productEntry->
						conditionList.clear();
						tempMap =[:];
						tempMap["productCode"]="";
						tempMap["productName"]="";
						BigDecimal initial=BigDecimal.ZERO;
						tempMap["initialQty"]=BigDecimal.ZERO;
						tempMap["finalQty"]=BigDecimal.ZERO;
						tempMap["diffQty"]=BigDecimal.ZERO;
						tempMap["partyName"]="";
						orderNo ="NA";
						tempMap["indentPrice"]=BigDecimal.ZERO;
						dispatchPrice=BigDecimal.ZERO;
						tempMap["indentValue"]=BigDecimal.ZERO;
						tempMap["dispatchValue"]=BigDecimal.ZERO;
						tempMap["supplierName"]="";
						orderHeaderSequences = delegator.findList("OrderHeaderSequence",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS ,orderId), null, null, null, false );
						if(UtilValidate.isNotEmpty(orderHeaderSequences)){
							orderSeqDetails = EntityUtil.getFirst(orderHeaderSequences);
							orderNo = orderSeqDetails.orderNo;
						}
						//give prefrence to ShipToCustomer
						conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SHIP_TO_CUSTOMER"));
						shipCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						orderRole = EntityUtil.getFirst(delegator.findList("OrderRole", shipCond, null, null, null, false));
						product = delegator.findOne("Product",UtilMisc.toMap("productId", productEntry.productId), false);
						//OrderItemAttr = EntityUtil.getFirst(delegator.findByAnd("OrderItemAttribute", UtilMisc.toMap("orderId",orderId,"attrName","INDENTQTY_FOR:"+productEntry.productId)));	
						if(UtilValidate.isNotEmpty(orderRole)){
						/*if(UtilValidate.isNotEmpty(OrderItemAttr)){
							initial=new BigDecimal(OrderItemAttr.attrValue);
						}*/
						//if(!(initial.compareTo(productEntry.quantity)==0)){
							//initial=new BigDecimal(OrderItemAttr.attrValue);
							tempMap["initialQty"]=productEntry.quantity;
							tempMap["indentPrice"]=productEntry.unitPrice;
							//Debug.log("initialQty======="+productEntry.quantity);
							//Debug.log("indentPrice======="+productEntry.unitPrice);
							totInitialQty=totInitialQty+productEntry.quantity;
							//Debug.log("totinitialQty======="+totinitialQty);
							orderAssocs = EntityUtil.getFirst(delegator.findList("OrderAssoc",EntityCondition.makeCondition("toOrderId", EntityOperator.EQUALS , orderId)  , null, null, null, false ));
							shippedQty=0;
							if(orderAssocs){
								poOrderId=orderAssocs.orderId;
								conditionList.clear();
								conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, poOrderId));
								conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productEntry.productId));
								shiprecCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
								shipmentReceipts =EntityUtil.getFirst(delegator.findList("ShipmentReceipt",shiprecCond, null, null, null, false ));
								if(UtilValidate.isNotEmpty(shipmentReceipts)){
									shippedQty=shipmentReceipts.quantityAccepted;
								}	
							}
							tempMap["finalQty"]=shippedQty;
							dispatchPrice=productEntry.unitPrice;
							totFinalQty=totFinalQty+shippedQty;
							tempMap["indentPrice"]=(tempMap["indentPrice"])/productList.size();
							dispatchPrice=dispatchPrice/productList.size();
						if(tempMap["initialQty"]!=null && tempMap["finalQty"]!=null){
							
						tempMap["diffQty"] = (tempMap["initialQty"]).subtract(tempMap["finalQty"]);
						totDiffQty=totDiffQty+tempMap["diffQty"];
						}
						
						indentValue=(productEntry.quantity)*(tempMap["indentPrice"]);
						//Debug.log("indentValue======="+indentValue);
						totIndentValue=totIndentValue+indentValue;
						//Debug.log("TOATALindentValue======="+totIndentValue);
						dispatchValue=(shippedQty)*(dispatchPrice);
						totDispatchValue=totDispatchValue+dispatchValue;
						//Debug.log("productlistsize======="+productList.size());
						tempMap["indentValue"]=indentValue.setScale(2, rounding);
						tempMap["dispatchValue"]=dispatchValue.setScale(2, rounding);
						if(UtilValidate.isNotEmpty(product)){
							tempMap["productName"]=product.productName;
							tempMap["productCode"]=(product.internalName).substring(0, 8);
						}
						tempMap["partyName"] = PartyHelper.getPartyName(delegator, orderRole.partyId, false);
						tempMap["orderDate"]=UtilDateTime.toDateString(productEntry.orderDate, "dd-MM-yy");
						//Debug.log("orderDate======="+tempMap["orderDate"]);
						tempMap["orderNo"]=orderNo;
						supplierPartyId="";
						productStoreId="";
						exprList=[];
						exprList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						exprList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"));
						EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
						supplierDetails = EntityUtil.getFirst(delegator.findList("OrderRole", discontinuationDateCondition, null,null,null, false));
						if(supplierDetails){
							supplierPartyId=supplierDetails.get("partyId");
						}
						supplierPartyName="";
						if(supplierPartyId){
							supplierPartyName = PartyHelper.getPartyName(delegator, supplierPartyId, false);
						}
						
						tempMap["supplierName"]=supplierPartyName;
						
						
						ProductWiseMap.put(productEntry.productId, tempMap);
						
					finalMap.put(orderId, ProductWiseMap);
					finalCSVList.add(tempMap);
					
					//}
				  }
						
				}
			}
		}
	}
	tempTotMap.put("orderNo", "TOTAL");
	tempTotMap.put("initialQty", totInitialQty.setScale(2, rounding));
	tempTotMap.put("indentValue", totIndentValue.setScale(2, rounding));
	tempTotMap.put("finalQty", totFinalQty.setScale(2, rounding));
	tempTotMap.put("dispatchValue", totDispatchValue.setScale(2, rounding));
	tempTotMap.put("diffQty", totDiffQty.setScale(2, rounding));
	context.tempTotMap=tempTotMap;
	finalCSVList.add(tempTotMap);
	context.IndentVsDispatchMap=finalMap;
	context.finalCSVList=finalCSVList;