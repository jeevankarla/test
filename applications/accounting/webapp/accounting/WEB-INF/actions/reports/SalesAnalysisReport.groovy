import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityListIterator;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.jar.Manifest.FastInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.product.product.ProductWorker;

import in.vasista.vbiz.humanres.HumanresService;

import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.accounting.ledger.GeneralLedgerServices;
dctx = dispatcher.getDispatchContext();


fromDateStr = parameters.fromDate;
thruDateStr = parameters.thruDate;

SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMM dd");
Timestamp fromDateTs = null;
if(fromDateStr){
	try {
		fromDateTs = new java.sql.Timestamp(formatter.parse(fromDateStr).getTime());
	} catch (ParseException e) {
	}
}
Timestamp thruDateTs = null;
if(thruDateStr){
	try {
		thruDateTs = new java.sql.Timestamp(formatter.parse(thruDateStr).getTime());
	} catch (ParseException e) {
	}
}
fromDate = UtilDateTime.getDayStart(fromDateTs, timeZone, locale);
thruDate = UtilDateTime.getDayEnd(thruDateTs, timeZone, locale);
context.fromDate=fromDate;
context.thruDate=thruDate;

List glAccountIdsList=FastList.newInstance();
glAccountIdsList=UtilMisc.toList("401004","401005","401006","401007","401008","401009");
glAccountIdsList.add("401015");


List unionPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNION"),null,null,null,false);
unionRole = EntityUtil.getFieldListFromEntityList(unionPartyRole, "partyId", true);

List unitsPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNITS"),null,null,null,false);
unitsRole = EntityUtil.getFieldListFromEntityList(unitsPartyRole, "partyId", true);

List wholeSalePartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"IC_WHOLESALE"),null,null,null,false);
wholeSaleRole = EntityUtil.getFieldListFromEntityList(wholeSalePartyRole, "partyId", true);

List excCustPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"EXCLUSIVE_CUSTOMER"),null,null,null,false);
excCustRole = EntityUtil.getFieldListFromEntityList(excCustPartyRole, "partyId", true);

List deopCustPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DEPOT_CUSTOMER"),null,null,null,false);
deopCustRole = EntityUtil.getFieldListFromEntityList(deopCustPartyRole, "partyId", true);

List dairyProductsList=FastList.newInstance();
List powderProductsList=FastList.newInstance();
List tradingProductsList=FastList.newInstance();
List icpAmulProductsList=FastList.newInstance();
List icpBellaryProductsList=FastList.newInstance();
List icpNandiniProductsList=FastList.newInstance();
List conversionProductsList=FastList.newInstance();
dairyProducts=ProductWorker.getProductsByCategory(delegator ,"DAIRY_PRODUCTS" ,null);
if(dairyProducts){
	dairyProductsList=EntityUtil.getFieldListFromEntityList(dairyProducts, "productId", true);
}
powderProducts=ProductWorker.getProductsByCategory(delegator ,"POWDER_PRODUCTS" ,null);
if(powderProducts){
	powderProductsList=EntityUtil.getFieldListFromEntityList(powderProducts, "productId", true);
}
tradingProducts=ProductWorker.getProductsByCategory(delegator ,"PUR_WSD" ,null);
if(tradingProducts){
	tradingProductsList=EntityUtil.getFieldListFromEntityList(tradingProducts, "productId", true);
}
icpAmulProducts=ProductWorker.getProductsByCategory(delegator ,"ICE_CREAM_AMUL" ,null);
if(icpAmulProducts){
	icpAmulProductsList=EntityUtil.getFieldListFromEntityList(icpAmulProducts, "productId", true);
}
icpBellaryProducts=ProductWorker.getProductsByCategory(delegator ,"ICE_CREAM_BELLARY" ,null);
if(icpBellaryProducts){
	icpBellaryProductsList=EntityUtil.getFieldListFromEntityList(icpBellaryProducts, "productId", true);
}
icpNandiniProducts=ProductWorker.getProductsByCategory(delegator ,"ICE_CREAM_NANDINI" ,null);
if(icpNandiniProducts){
	icpNandiniProductsList=EntityUtil.getFieldListFromEntityList(icpNandiniProducts, "productId", true);
}
conversionProducts=ProductWorker.getProductsByCategory(delegator ,"CON_CHG" ,null);
if(conversionProducts){
	conversionProductsList=EntityUtil.getFieldListFromEntityList(conversionProducts, "productId", true);
}

List conditionList = FastList.newInstance();
EntityListIterator acctgTransList=null;
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIdsList));
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
acctgTransList = delegator.find("AcctgTransAndEntries",condition,null,null,null,null);
channelWiseMap=[:];
productType=[:];
productMap=[:];
glAccountMap=[:];
saleTypeMap=[:];
if(acctgTransList){
	while (acctgTransList.hasNext()) {
		GenericValue transEntry = acctgTransList.next();
		debit=0;credit=0;value=0;
		if(transEntry.debitCreditFlag=="C"){
			credit=transEntry.amount;
		}
		if(transEntry.debitCreditFlag=="D"){
			debit=transEntry.amount;
		}
		value=debit-credit;
		if(UtilValidate.isEmpty(glAccountMap[transEntry.glAccountId])){
//			value=debit-credit;
			glAccountMap[transEntry.glAccountId]=value;
		}else{
//			value=debit-credit;
		    
			existingVal=0;
			existingVal=glAccountMap[transEntry.glAccountId];
			glAccountMap[transEntry.glAccountId]=value+existingVal;;
		}
		partyId=transEntry.partyId;
		productId=transEntry.productId;
		saleType ="";
		categoryType="";
		if(unitsRole.contains(partyId)){
			saleType = "INTER_UNIT";
			saleTypeMap[saleType]="Inter-Unit Sale";
		}
		if(deopCustRole.contains(partyId)){
			saleType = "DEPO_SALE";
			saleTypeMap[saleType]="Depo Sale";
		}
		if(excCustRole.contains(partyId)){
			saleType = "ICE_AMUL";
			saleTypeMap[saleType]="ICP AMUL Sale";
		}
		if(wholeSaleRole.contains(partyId)){
			saleType = "ICE_NANDINI";
			saleTypeMap[saleType]="ICP NANDINI Sale";
		}
		if(unionRole.contains(partyId)){
			saleType = "CONVERSION"
			saleTypeMap[saleType]="CONVERSION FEE";
		}
		if(dairyProductsList.contains(productId)){
			categoryType="DAIRY_PRODUCTS";
		}
		if(powderProductsList.contains(productId)){
		    categoryType="POWDER_PRODUCTS";	
		}	
		if(tradingProductsList.contains(productId)){
			categoryType="PUR_WSD";
		}
		if(icpAmulProductsList.contains(productId)){
			categoryType="ICE_CREAM_AMUL";
		}
		if(icpNandiniProductsList.contains(productId)){
			categoryType="ICE_CREAM_NANDINI";
		}
		if(icpBellaryProductsList.contains(productId)){
			categoryType="ICE_CREAM_BELLARY";
		}
		if(conversionProductsList.contains(productId)){
			categoryType="CONVERSION_PROD";
		}
		tempCatTypeMap=FastMap.newInstance();
		tempProductMap=FastMap.newInstance();
		if(UtilValidate.isNotEmpty(channelWiseMap[saleType])){
			tempCatTypeMap=channelWiseMap[saleType];
		    
		}
		if(UtilValidate.isNotEmpty(tempCatTypeMap[categoryType])){
			tempProductMap=tempCatTypeMap[categoryType];
		
		}
		if(UtilValidate.isNotEmpty(tempProductMap[productId])){
			tempProductMap.put(productId, tempProductMap.get(productId)+value);
		
		}else{
			tempProductMap.put(productId, value);
		}
		if(UtilValidate.isNotEmpty(categoryType)){
			tempCatTypeMap.put(categoryType, tempProductMap);
		}
		if(UtilValidate.isNotEmpty(saleType)){
			channelWiseMap.put(saleType, tempCatTypeMap);
		}
	}
	acctgTransList.close();
}
context.glAccountMap=glAccountMap;
context.channelWiseMap=channelWiseMap;
context.saleTypeMap=saleTypeMap;
//For CSV
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
salesAnalysisCsv=[];
if(UtilValidate.isNotEmpty(glAccountMap)){
	titleMap=[:];
	for(Map.Entry glAccount:glAccountMap.entrySet()){
		tempMap=[:];
		glAccountId=glAccount.getKey();
		amount=0;
		if(glAccount.getValue()>=0){
			amount=glAccount.getValue() +"(Dr)" ;
		}else{
			amount=-(glAccount.getValue()) +"(Cr)";
		}
		tempMap.glAccountId=glAccountId;
		glAccnt=delegator.findOne("GlAccount",[glAccountId:glAccountId],false);
		description="";
		if(glAccnt){
			description=glAccnt.description;
		}
		tempMap.description=description;
		tempMap.amount=amount;
		salesAnalysisCsv.add(tempMap);
	}
	titleMap.glAccountId="CHANNEL / CATEGORY";
	titleMap.productId="PRODUCT ID";
	titleMap.description="PRODUCT NAME";
	titleMap.amount="AMOUNT";
	salesAnalysisCsv.add(titleMap);
	for(Map.Entry channel:channelWiseMap.entrySet()){
		if(UtilValidate.isNotEmpty(channel.getValue())){
            Map categoryWise=FastMap.newInstance();
			categoryWise=channel.getValue();
			tempMap=[:];
			tempMap.glAccountId=saleTypeMap.get(channel.getKey());
			salesAnalysisCsv.add(tempMap);
			for(Map.Entry category:categoryWise.entrySet()){
				products=category.getValue();
				prodCat=delegator.findOne("ProductCategory",[productCategoryId:category.getKey()],false);
				tempMap=[:];
				tempMap.glAccountId=prodCat.description;
				salesAnalysisCsv.add(tempMap);
				totValue=0;
				for(Map.Entry product:products.entrySet()){
					tempMap=[:];
					if(product.getValue()!=0){
					value=0;
					if(product.getValue()>=0){
						value=product.getValue() +"(Dr)";
					}else{
						value=-(product.getValue()) +"(Cr)";
					}
					totValue=totValue+product.getValue();
					productId=product.getKey();
					prod=delegator.findOne("Product",[productId:productId],false);
					tempMap.productId=productId;
					tempMap.description=prod.productName;
					tempMap.amount=value;
					salesAnalysisCsv.add(tempMap);
					}
				}
				tempMap=[:];
				if(totValue>=0){
					totValue=totValue +"(Dr)";
				}else{
					totValue=-(totValue) +"(Cr)";
				}
				tempMap.description="TOTAL  :";
				tempMap.amount=totValue;
				salesAnalysisCsv.add(tempMap);
			}
		}
	}
}
context.salesAnalysisCsv=salesAnalysisCsv;
}














