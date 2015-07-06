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


/*List unionPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNION"),UtilMisc.toSet("partyId"),null,null,false);
unionRole = EntityUtil.getFieldListFromEntityList(unionPartyRole, "partyId", true);

List unitsPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"UNITS"),UtilMisc.toSet("partyId"),null,null,false);
unitsRole = EntityUtil.getFieldListFromEntityList(unitsPartyRole, "partyId", true);

List wholeSalePartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"IC_WHOLESALE"),UtilMisc.toSet("partyId"),null,null,false);
wholeSaleRole = EntityUtil.getFieldListFromEntityList(wholeSalePartyRole, "partyId", true);

List excCustPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"EXCLUSIVE_CUSTOMER"),UtilMisc.toSet("partyId"),null,null,false);
excCustRole = EntityUtil.getFieldListFromEntityList(excCustPartyRole, "partyId", true);

List deopCustPartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DEPOT_CUSTOMER"),UtilMisc.toSet("partyId"),null,null,false);
deopCustRole = EntityUtil.getFieldListFromEntityList(deopCustPartyRole, "partyId", true);

List EmployeePartyRole= delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"EMPLOYEE"),UtilMisc.toSet("partyId"),null,null,false);
employeeRole = EntityUtil.getFieldListFromEntityList(EmployeePartyRole, "partyId", true);*/

List roleTypeList = FastList.newInstance();
roleTypeList = UtilMisc.toList("UNION","UNITS","IC_WHOLESALE","EXCLUSIVE_CUSTOMER","DEPOT_CUSTOMER","EMPLOYEE");
context.roleTypeList=roleTypeList;
List partyRoleList = delegator.findList("PartyRole",EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,roleTypeList),UtilMisc.toSet("roleTypeId","partyId"),null,null,false);


List productCategoryList = delegator.findList("ProductCategory",EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.EQUALS,"SALES_ACANLY"),UtilMisc.toSet("productCategoryId"),null,null,false); 
List productCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryList, "productCategoryId", true);

/*
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
*/
List conditionList = FastList.newInstance();
List productIds = FastList.newInstance();
EntityListIterator acctgTransList=null;
EntityListIterator acctgTransProdcuctList=null;
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
	conditionList.add(EntityCondition.makeCondition("transactionDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
	conditionList.add(EntityCondition.makeCondition("glAccountId",EntityOperator.IN,glAccountIdsList));
	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.NOT_EQUAL,null));
	conditionList.add(EntityCondition.makeCondition("isPosted",EntityOperator.EQUALS,"Y"));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
acctgTransList = delegator.find("AcctgTransAndEntries",condition,null,null,null,null);
acctgTransProdcuctList = delegator.find("AcctgTransAndEntries",condition,null,null,null,null);
productIds = EntityUtil.getFieldListFromEntityListIterator(acctgTransProdcuctList, "productId", true);
List productList = delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.IN,productIds),null,null,null,false);
channelWiseMap=[:];
productType=[:];
productMap=[:];
glAccountMap=[:];
saleTypeMap=[:];
channelWiseTotal=[:];
categoryWiseTotal=[:];
/*
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
*/
conditionList.clear();
conditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.IN,productCategoryIds));
conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,fromDate));
conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,
												EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayEnd(fromDate))));
EntityCondition con = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
List productCategoryMemberList = delegator.findList("ProductCategoryMember",con,null,null,null,false);

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
			glAccountMap[transEntry.glAccountId]=value;
		}else{
			existingVal=0;
			existingVal=glAccountMap[transEntry.glAccountId];
			glAccountMap[transEntry.glAccountId]=value+existingVal;;
		}
		partyId=transEntry.partyId;
		partyId=partyId.toUpperCase();
		glAccountId=transEntry.glAccountId;
		productId=transEntry.productId;
		List productCategoryMember = EntityUtil.filterByCondition(productCategoryMemberList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		String productCategoryId="";
		if(productCategoryMember){
			GenericValue productCategory = EntityUtil.getFirst(productCategoryMember);
			productCategoryId = productCategory.productCategoryId;
		}
		List partyRoles = EntityUtil.filterByCondition(partyRoleList, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		String roleTypeId="";
		if(UtilValidate.isNotEmpty(partyRoles)){
			if(partyRoles.size()>1){
				List roleTypeIds = EntityUtil.getFieldListFromEntityList(partyRoles,"roleTypeId",true);
				if(roleTypeIds.contains("UNION")){
					roleTypeId="UNION";
				}else if(roleTypeIds.contains("UNITS")){
				    roleTypeId = "UNITS";
				}else if(roleTypeIds.contains("DEPOT_CUSTOMER")){
					roleTypeId="DEPOT_CUSTOMER";
				}
			}else{
				GenericValue partyRole = EntityUtil.getFirst(partyRoles);
				roleTypeId = partyRole.roleTypeId;
			}
		}else{
		 	roleTypeId="OTHER";
		}
		/*products = EntityUtil.filterByCondition(productList, EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		productDetails = EntityUtil.getFirst(products);
		primaryProductCategoryId="";
		if(UtilValidate.isNotEmpty(productDetails)){
		primaryProductCategoryId = productDetails.primaryProductCategoryId;
		}*/
		Map tempCategoryMap = FastMap.newInstance();
		//tempCatTypeMap=FastMap.newInstance();
		tempProductMap=FastMap.newInstance();
		tempRoleTypeMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(channelWiseMap[glAccountId])){
			tempCategoryMap= channelWiseMap[glAccountId];
			Map<String, Map> newSortedMap = new TreeMap<String, Map>(tempCategoryMap);
			tempCategoryMap.clear();
			tempCategoryMap.putAll(newSortedMap);
		}
		
		
		if(UtilValidate.isNotEmpty(tempCategoryMap[productCategoryId])){
			tempProductMap=tempCategoryMap[productCategoryId];
		}
		/*if(UtilValidate.isNotEmpty(tempCatTypeMap[primaryProductCategoryId])){
			tempProductMap=tempCatTypeMap[primaryProductCategoryId];
		
		}*/
		if(UtilValidate.isNotEmpty(tempProductMap[productId])){
			tempProductMap.put(productId, tempProductMap.get(productId)+value);
		}else{
			tempProductMap.put(productId, value);
		}
		
		/*if(UtilValidate.isNotEmpty(primaryProductCategoryId)){
			tempCatTypeMap.put(primaryProductCategoryId, tempProductMap);
		}*/
		if(UtilValidate.isNotEmpty(productCategoryId)){
			tempCategoryMap.put(productCategoryId, tempProductMap);
		}
		if(UtilValidate.isNotEmpty(glAccountId)){
			channelWiseMap.put(glAccountId, tempCategoryMap);
		}
		
		if(UtilValidate.isNotEmpty(productMap[productId])){
			tempRoleTypeMap=productMap[productId];
		}
		if(UtilValidate.isNotEmpty(tempRoleTypeMap[roleTypeId])){
			tempRoleTypeMap.put(roleTypeId,tempRoleTypeMap.get(roleTypeId)+value);
		}else{
			tempRoleTypeMap.put(roleTypeId,value);
		}
		if(UtilValidate.isNotEmpty(productId)){
			productMap.put(productId, tempRoleTypeMap);
		}
	}
	acctgTransList.close();
}		
context.glAccountMap=glAccountMap;
context.channelWiseMap=channelWiseMap;
context.productMap = productMap;
//For CSV
if(UtilValidate.isNotEmpty(parameters.flag) && parameters.flag=="CSVReport"){
salesAnalysisCsv=[];
if(UtilValidate.isNotEmpty(glAccountMap)){
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
	for(Map.Entry channel:channelWiseMap.entrySet()){
		if(UtilValidate.isNotEmpty(channel.getValue())){
			grandTotal=0;
			grandWholeSaleValue=0;
			grandUnionSaleValue=0;
			grandUnitSaleValue=0;
			grandEmplSaleValue=0;
			grandDepoSaleValue=0;
			grandAmulSaleValue=0;
			grandOtherSaleValue=0;
			titleMap=[:];
			glAccnt=delegator.findOne("GlAccount",[glAccountId:channel.getKey()],false);
			description="";
			if(glAccnt){
				description=glAccnt.description+"["+channel.getKey()+"]";
			}
			titleMap.glAccountId=description;
			titleMap.productId="CATEGORY / PRODUCT";
			titleMap.description="PRODUCT NAME";
			titleMap.amount="AMOUNT";
			roleTypeList.each{roleType->
				roleTypeName=delegator.findOne("RoleType",[roleTypeId:roleType],false);
			  titleMap.put(roleType, roleTypeName.description); 	
			}
			titleMap.OTHER="OTHERS";
			salesAnalysisCsv.add(titleMap);
			Map categoryWise=FastMap.newInstance();
			newCategoryMap=channel.getValue();
			tempMap=[:];
			tempMap.glAccountId=saleTypeMap.get(channel.getKey());
			salesAnalysisCsv.add(tempMap);
			/*for(Map.Entry category:categoryWise.entrySet()){
				newCategoryMap=category.getValue();
				prodCat=delegator.findOne("ProductCategory",[productCategoryId:category.getKey()],false);
				tempMap=[:];
				tempMap.productId=prodCat.description;
				salesAnalysisCsv.add(tempMap);*/
				 for(Map.Entry newCategory:newCategoryMap.entrySet()){
					 products = newCategory.getValue();
					 wholeSaleValue=0;
					 unionSaleValue=0;
					 unitSaleValue=0;
					 emplSaleValue=0;
					 depoSaleValue=0;
					 amulSaleValue=0;
					 otherSaleValue=0;
					 newProdCat=delegator.findOne("ProductCategory",[productCategoryId:newCategory.getKey()],false);
					 tempMap=[:];
					 tempMap.productId=newProdCat.description;
					 salesAnalysisCsv.add(tempMap);
					 totValue=0;
						for(Map.Entry product:products.entrySet()){
							tempMap=[:];
							saleTypeValues = [:];
							productId=product.getKey();
							if(UtilValidate.isNotEmpty(productMap)){
								saleTypeValues = productMap.get(product.getKey());
							}
							if(product.getValue()!=0){
								
								value=0;
								if(product.getValue()>0){
									value=product.getValue() +"(Dr)";
								}else if(product.getValue()<0){
									value=-(product.getValue()) +"(Cr)";
								}
								roleTypeList.each{roleType->
									saleValue=0;
									if(UtilValidate.isNotEmpty(saleTypeValues.get(roleType))){
										saleValue = saleTypeValues.get(roleType);
										if(roleType == "IC_WHOLESALE"){
											wholeSaleValue=wholeSaleValue+saleValue;
										}else if(roleType == "UNION"){
											unionSaleValue=unionSaleValue+saleValue;
										}else if(roleType == "UNITS"){
											unitSaleValue=unitSaleValue+saleValue;
										}else if(roleType == "EXCLUSIVE_CUSTOMER"){
											amulSaleValue=amulSaleValue+saleValue;
										}else if(roleType == "DEPOT_CUSTOMER"){
											depoSaleValue=depoSaleValue+saleValue;
										}else if(roleType == "EMPLOYEE"){
											emplSaleValue=emplSaleValue+saleValue;
										}
										if(saleValue >0){
											tempMap.put(roleType, saleValue +"(Dr)");
										}else if(saleValue <0){
											tempMap.put(roleType, -(saleValue) +"(Cr)");
										}
									}
								}
								otherValue=0;
								if(UtilValidate.isNotEmpty(saleTypeValues.get("OTHER"))){
									otherValue = saleTypeValues.get("OTHER");
									otherSaleValue=otherSaleValue+otherValue;
								}
								if(otherValue >0){
									tempMap.put("OTHER", otherValue +"(Dr)");
								}else if(otherValue <0){
									tempMap.put("OTHER", -(otherValue) +"(Cr)");
								}
								totValue=totValue+product.getValue();
								
								prod=delegator.findOne("Product",[productId:productId],false);
								tempMap.productId=productId;
								tempMap.description=prod.productName;
								tempMap.amount=value;
								salesAnalysisCsv.add(tempMap);
							}
						}
						grandTotal=grandTotal+totValue;
						tempMap=[:];
						roleTypeList.each{roleType->
							totalValue=0;
								if(roleType == "IC_WHOLESALE"){
									totalValue=wholeSaleValue;
									grandWholeSaleValue=grandWholeSaleValue+totalValue;
								}else if(roleType == "UNION"){
									totalValue=unionSaleValue;
									grandUnionSaleValue=grandUnionSaleValue+totalValue;
								}else if(roleType == "UNITS"){
									totalValue=unitSaleValue;
									grandUnitSaleValue=grandUnitSaleValue+totalValue;
								}else if(roleType == "EXCLUSIVE_CUSTOMER"){
									totalValue=amulSaleValue;
									grandAmulSaleValue=grandAmulSaleValue+totalValue;
								}else if(roleType == "DEPOT_CUSTOMER"){
									totalValue=depoSaleValue;
									grandDepoSaleValue=grandDepoSaleValue+totalValue;
								}else if(roleType == "EMPLOYEE"){
									totalValue=emplSaleValue;
									grandEmplSaleValue=grandEmplSaleValue+totalValue;
								}
								if(totalValue >0){
									tempMap.put(roleType, totalValue +"(Dr)");
								}else if(totalValue <0){
									tempMap.put(roleType, -(totalValue) +"(Cr)");
								}
						}
						grandOtherSaleValue=grandOtherSaleValue+otherSaleValue;
						if(otherSaleValue >0){
							tempMap.put("OTHER", otherSaleValue +"(Dr)");
						}else if(otherSaleValue <0){
							tempMap.put("OTHER", -(otherSaleValue) +"(Cr)");
						}
						if(totValue>0){
							totValue=totValue +"(Dr)";
						}else if(totValue<0){
							totValue=-(totValue) +"(Cr)";
						}
						tempMap.description="TOTAL  :";
						tempMap.amount=totValue;
						salesAnalysisCsv.add(tempMap);
				 }	 
				 tempMap=[:];
				 
				 roleTypeList.each{roleType->
					 grandTotalValue=0;
						 if(roleType == "IC_WHOLESALE"){
							 grandTotalValue=grandWholeSaleValue;
						 }else if(roleType == "UNION"){
							 grandTotalValue=grandUnionSaleValue;
						 }else if(roleType == "UNITS"){
							 grandTotalValue=grandUnitSaleValue;
						 }else if(roleType == "EXCLUSIVE_CUSTOMER"){
							 grandTotalValue=grandAmulSaleValue;
						 }else if(roleType == "DEPOT_CUSTOMER"){
							 grandTotalValue=grandDepoSaleValue;
						 }else if(roleType == "EMPLOYEE"){
							 grandTotalValue=grandEmplSaleValue;
						 }
						 if(grandTotalValue >0){
							 tempMap.put(roleType, grandTotalValue +"(Dr)");
						 }else if(grandTotalValue <0){
							 tempMap.put(roleType, -(grandTotalValue) +"(Cr)");
						 }
				  }
				 if(grandOtherSaleValue >0){
					 tempMap.put("OTHER", grandOtherSaleValue +"(Dr)");
				 }else if(grandOtherSaleValue <0){
					 tempMap.put("OTHER", -(grandOtherSaleValue) +"(Cr)");
				 }
				 if(grandTotal>0){
					 grandTotal=grandTotal +"(Dr)";
				 }else if(grandTotal<0){
					 grandTotal=-(grandTotal) +"(Cr)";
				 }
				 tempMap.description="GRAND TOTAL  :";
				 tempMap.amount=grandTotal;
				 salesAnalysisCsv.add(tempMap);
			//}
		}
	}
}
context.salesAnalysisCsv=salesAnalysisCsv;
}










