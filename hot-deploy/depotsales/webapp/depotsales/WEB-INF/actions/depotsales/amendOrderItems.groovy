
import in.vasista.vbiz.purchase.PurchaseStoreServices;
import org.ofbiz.party.party.PartyHelper;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import in.vasista.vbiz.facility.util.FacilityUtil;
import in.vasista.vbiz.byproducts.icp.ICPServices;
import org.ofbiz.party.contact.ContactMechWorker;


conditionList=[];
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
condExpr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderItemAttr = delegator.findList("OrderItemAttribute", condExpr, null, null, null, false);
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId));
expr = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
partyOrders = delegator.findList("OrderRole", expr, null, null, null, false);

orderType="direct";
onbehalfof = EntityUtil.filterByCondition(partyOrders, EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ON_BEHALF_OF"));
if(onbehalfof){
	orderType = "onbehalfof";
}
context.orderType=orderType;
JSONObject OrderItemUIJSON = new JSONObject();
JSONArray orderItemsJSON = new JSONArray();

if(UtilValidate.isNotEmpty(result.listIt)){
	list=result.listIt;
	resultList = [];
	GenericValue eachItem = null;
	//productIds = EntityUtil.getFieldListFromEntityList(list, "productId", true);
	
	Debug.log("list==============================="+list);
	//products = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
	int i=0;
	while ((eachItem=list.next()) != null) {

		Debug.log("eachItem==============================="+eachItem);
		
	
	prodDetail =  context.product = delegator.findOne("Product",[productId:eachItem.productId],true);
	if(i==0){
		catType="";
		productCategorySelect = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, eachItem.productId), null, null, null, false);
		productCategorySelectIds = EntityUtil.getFieldListFromEntityList(productCategorySelect, "productCategoryId", true);
		
		JSONArray productCategoryJSON = new JSONArray();
		category="";
		productCategorySelectIds.each{eachCatId ->
			category=eachCatId;
			productCategoryJSON.add(eachCatId);
		}
		if(category.contains("SILK") || category.contains("TUSSAR")){
			catType="Silk";
		}else if(category.contains("COTTON") || category.contains("HANK")){
			catType="Cotton";
		}else{
			catType="other";
		}
		context.catType=catType;
		i=i+1;
	}
	
	JSONObject newObj = new JSONObject();
	
	cond=[];
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "BALE_QTY"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpBale = EntityCondition.makeCondition(cond, EntityOperator.AND);
	baleQtyAttr = EntityUtil.filterByCondition(orderItemAttr, condExpBale);
	Debug.log("productDetails====================1111111111=dfsdf==========");
	
	cond.clear();
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "YARN_UOM"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpYarn = EntityCondition.makeCondition(cond, EntityOperator.AND);
	yarnUOMAttr = EntityUtil.filterByCondition(orderItemAttr,condExpYarn);
	Debug.log("productDetails====================2222222=dfsdf==========");
	
	cond.clear();
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "WIEVER_CUSTOMER"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpWiever = EntityCondition.makeCondition(cond, EntityOperator.AND);
	WieverAttr = EntityUtil.filterByCondition(orderItemAttr,condExpWiever);
	Debug.log("productDetails====================33333=dfsdf==========");
	
	
	cond.clear();
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "BUNDLE_WGHT"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpBundle = EntityCondition.makeCondition(cond, EntityOperator.AND);
	bundleAttr = EntityUtil.filterByCondition(orderItemAttr,condExpBundle);
	Debug.log("productDetails====================666666=dfsdf==========");
	
	
	cond.clear();
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "BANDLE_UNITPRICE"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpBundlePrice = EntityCondition.makeCondition(cond, EntityOperator.AND);
	bundlePriceAttr = EntityUtil.filterByCondition(orderItemAttr,condExpBundlePrice);
	Debug.log("bundlePriceAttr====================666666=dfsdf==========");
	
	
	cond.clear();
	cond.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "REMARKS"));
	cond.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS,eachItem.orderItemSeqId));
	condExpRmrk = EntityCondition.makeCondition(cond, EntityOperator.AND);
	RmrkAttr = EntityUtil.filterByCondition(orderItemAttr,condExpRmrk);
	Debug.log("productDetails====================9999999=dfsdf==========");
	
	yarnUOM="";
	bundleWeight=0;
	BundleUnitPrice=0;
	if(baleQtyAttr){
		baleQty=(baleQtyAttr.get(0)).get("attrValue");
	}
	if(yarnUOMAttr){
		yarnUOM=(yarnUOMAttr.get(0)).get("attrValue");
	}
	remrk="";
	if(RmrkAttr){
		remrk=(RmrkAttr.get(0)).get("attrValue");
	}
	if(bundlePriceAttr){
		BundleUnitPrice=(bundlePriceAttr.get(0)).get("attrValue");
	}
	wieverName="";
	wieverId="";
	psbNo="";
	Debug.log("productDetails====================89898989898=dfsdf==========");
	
	if(WieverAttr){
		wieverId=(WieverAttr.get(0)).get("attrValue");
		partyIdentification = delegator.findOne("PartyIdentification",UtilMisc.toMap("partyId", wieverId, "partyIdentificationTypeId", "PSB_NUMER"), false);
		if(partyIdentification){
			psbNo = partyIdentification.get("idValue");
		}
		wieverName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator, wieverId, false);
	}
	if(bundleAttr){
		bundleWeight=(bundleAttr.get(0)).get("attrValue");
	}
	newObj.put("customerName",wieverName+"["+psbNo+"]");
	newObj.put("customerId",wieverId);
	newObj.put("orderItemSeqId",eachItem.orderItemSeqId);	
	newObj.put("remarks",remrk);
	newObj.put("psbNumber",psbNo);
	newObj.put("cProductId",eachItem.productId);
	newObj.put("cProductName",prodDetail.description +" [ "+prodDetail.brandName+"]");
	newObj.put("baleQuantity",baleQty);
	newObj.put("KgunitPrice",BundleUnitPrice);
	
	newObj.put("cottonUom",yarnUOM);
	newObj.put("bundleWeight",bundleWeight);
	newObj.put("quantity",eachItem.quantity);
	newObj.put("unitPrice",eachItem.unitPrice);
	amount=eachItem.unitPrice*eachItem.quantity;
	newObj.put("amount", amount);
	orderItemsJSON.add(newObj);
	if(OrderItemUIJSON.get(eachItem.productId)){
		JSONObject existsObj = new JSONObject();
		existsObj=OrderItemUIJSON.get(eachItem.productId);
		//existsObj["quantity"]=existsObj.get("quantity")+eachItem.quantity;
		//existsObj["baleQuantity"]=existsObj.get("baleQuantity")+baleQty;
		OrderItemUIJSON.put(eachItem.productId, existsObj);
		
	}else{
		OrderItemUIJSON.put(eachItem.productId, newObj);
	}
	}
	
}
context.dataJSON = orderItemsJSON;
