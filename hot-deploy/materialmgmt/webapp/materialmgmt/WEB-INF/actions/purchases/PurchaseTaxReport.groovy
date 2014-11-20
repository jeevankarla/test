import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;



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

// Purchase abstract Sales report
reportTypeFlag = parameters.reportTypeFlag;
taxType=parameters.taxType;
EntityListIterator invoiceItemsIter = null;

exprList=[];
exprList.add(EntityCondition.makeCondition("glAccountTypeId", EntityOperator.EQUALS, "PURCHASE_ACCOUNT"));
//exprList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, product.primaryProductCategoryId));
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
productcatList = delegator.findList("ProductCategoryGlAccount", condition, null, null, null, false);
productCategoryId = EntityUtil.getFieldListFromEntityList(productcatList, "productCategoryId", true);
//get product from ProductCategory
productCategoryMember = delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryId), null, null, null, false);
productCatMap=[:]
productCategoryMember.each{prodCatMember ->
	productCatMap[prodCatMember.productId] = prodCatMember.productCategoryId;
}
Debug.log("productCatMap============================>"+productCatMap);
taxDetails5pt5List=[];
taxDetails14pt5List=[];
taxDetailsOthrList=[];

taxDetails5pt5Map=[:];
taxDetails14pt5Map=[:];


tax5pt5TotalMap=[:];
tax5pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
tax5pt5TotalMap["vatAmount"]=BigDecimal.ZERO;
tax14pt5TotalMap=[:];
tax14pt5TotalMap["invTotalVal"]=BigDecimal.ZERO;
tax14pt5TotalMap["vatAmount"]=BigDecimal.ZERO;

taxExTotalMap=[:];
taxExTotalMap["invTotalVal"]=BigDecimal.ZERO;
taxExTotalMap["cstAmount"]=BigDecimal.ZERO;

taxDetailsCstMap=[:];

taxCstTotalMap=[:];
taxCstTotalMap["invTotalVal"]=BigDecimal.ZERO;
taxCstTotalMap["cstAmount"]=BigDecimal.ZERO;


tax5pt5CatMap=[:];
tax5pt5CatMap["discount"]=BigDecimal.ZERO;


tax14pt5CatMap=[:];
tax14pt5CatMap["discount"]=BigDecimal.ZERO;

taxCstCatMap=[:];
taxCstCatMap["discount"]=BigDecimal.ZERO;


//to get Discounts and Other-Charges
/*exprList=[];
exprList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
exprList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
exprList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");

exprList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, "_NA_"));
exprList.add(EntityCondition.makeCondition("isVirtual", EntityOperator.NOT_EQUAL, "Y"));
exprList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, "Milk"));
exprList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null),EntityOperator.OR,
  EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, fromDate)));
  EntityCondition discontinuationDateCondition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	prodList =delegator.findList("Product", discontinuationDateCondition,null, null, null, false);
	prodIdsList=EntityUtil.getFieldListFromEntityList(prodList, "productId", false);*/

		invoiceMap = [:];
		invoiceDtlsMap = [:];
		
		try {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));
			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL, null));//want to skip other than product items
			conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,"PURCHASE_INVOICE"));
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company"));
			/*if (UtilValidate.isNotEmpty(partyIds)) {
				conditionList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, partyIds));
			}*/
			conditionList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
			conditionList.add(EntityCondition.makeCondition("invoiceDate",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<String> orderBy = UtilMisc.toList("invoiceDate","invoiceId","partyId");
			invoiceItemsIter = delegator.find("InvoiceAndItem", condition, null, null, orderBy, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		invoiceItemsIter.each{invoiceItem->
			//innerTaxItemMap=[:];
			
			if(UtilValidate.isNotEmpty(invoiceItem.vatPercent) && UtilValidate.isNotEmpty(invoiceItem.vatAmount)){
				if(invoiceItem.vatPercent==5.5){
				BigDecimal vatRevenue = invoiceItem.vatAmount;
				productId = invoiceItem.productId;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				BigDecimal totalBed = BigDecimal.ZERO;
				   if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
						totalBed+=invoiceItem.bedAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
						 totalBed+=invoiceItem.bedcessAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
					totalBed+=invoiceItem.bedseccessAmount;
					}
				invTotalVal+=totalBed;
				
				//preparing Another Map here for Category
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["vatAmount"]=vatRevenue;
				innerItemMap["invTotalVal"]=invTotalVal;
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(tax5pt5CatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["vatAmount"]=vatRevenue;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						tax5pt5CatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(tax5pt5CatMap[prodCategoryId])){
					    Map innerTaxCatMap=tax5pt5CatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["vatAmount"]+=vatRevenue;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						tax5pt5CatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				
				invDetailMap=taxDetails5pt5Map[invoiceItem.invoiceId];
					if(UtilValidate.isEmpty(invDetailMap)){
					innerTaxItemMap=[:];
					innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
					innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
					innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
					innerTaxItemMap["tinNumber"]="";
					innerTaxItemMap["vchrType"]="Purchase";
					innerTaxItemMap["crOrDbId"]="D";
					invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
							                                              EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_ITEM17"))  , null, null, null, false );
					  if(UtilValidate.isNotEmpty(invoiceDisItemList)){
						  discountInvoiceItem=invoiceDisItemList.getFirst();
						  invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						  tax5pt5CatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
					  }
					//
					//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);
					//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
					
					//invTotalVal=invTotalVal-vatRevenue;
					
					innerTaxItemMap["invTotalVal"]=invTotalVal;
					innerTaxItemMap["vatAmount"]=vatRevenue;
					tax5pt5TotalMap["invTotalVal"]+=invTotalVal;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;
					fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
						if(UtilValidate.isNotEmpty(fromPartyDetail)){
							innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
					      }
					taxDetails5pt5List.addAll(innerTaxItemMap);
					
					//intilize inner map when empty
					taxDetails5pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
					}else if(UtilValidate.isNotEmpty(invDetailMap)){
					invDetailMap["vatAmount"]+=vatRevenue;
					tax5pt5TotalMap["vatAmount"]+=vatRevenue;
					
					invDetailMap["invTotalVal"]+=invTotalVal;
					tax5pt5TotalMap["invTotalVal"]+=invTotalVal;
					
					taxDetails5pt5Map[invoiceItem.invoiceId]=invDetailMap;
					}
				}
				if(invoiceItem.vatPercent==14.5){
					productId = invoiceItem.productId;
					BigDecimal vatRevenue = invoiceItem.vatAmount;
					invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
					BigDecimal totalBed = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
						totalBed+=invoiceItem.bedAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
						 totalBed+=invoiceItem.bedcessAmount;
					}
					if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
					totalBed+=invoiceItem.bedseccessAmount;
					}
					invTotalVal+=totalBed;
					
					//preparing Another Map here for Category
					innerItemMap=[:];
					innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
					innerItemMap["invoiceId"]=invoiceItem.invoiceId;
					innerItemMap["partyId"]=invoiceItem.partyIdFrom;
					innerItemMap["productId"]=invoiceItem.productId;
					innerItemMap["tinNumber"]="";
					innerItemMap["vchrType"]="Purchase";
					innerItemMap["crOrDbId"]="D";
					innerItemMap["invTotalVal"]=invTotalVal;
					innerItemMap["vatAmount"]=vatRevenue;
					innerItemMap["invTotalVal"]=invTotalVal;
					// get category
					if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
						prodCategoryId=productCatMap.get(productId);
						if(UtilValidate.isEmpty(tax14pt5CatMap[prodCategoryId])){
							innerTaxCatMap=[:];
							innerTaxCatMap["totalValue"]=invTotalVal;
							innerTaxCatMap["vatAmount"]=vatRevenue;
							invoiceList=[];
							invoiceList.addAll(innerItemMap);
							innerTaxCatMap["invoiceList"]=invoiceList;
							tax14pt5CatMap[prodCategoryId]=innerTaxCatMap;
						}else if(UtilValidate.isNotEmpty(tax14pt5CatMap[prodCategoryId])){
							Map innerTaxCatMap=tax14pt5CatMap[prodCategoryId];
							innerTaxCatMap["totalValue"]+=invTotalVal;
							innerTaxCatMap["vatAmount"]+=vatRevenue;
							invoiceList=innerTaxCatMap["invoiceList"];
							invoiceList.addAll(innerItemMap);
							innerTaxCatMap["invoiceList"]=invoiceList;
							tax14pt5CatMap[prodCategoryId]=innerTaxCatMap;
						}
					}
					//category ends here
					
					invDetailMap=taxDetails14pt5Map[invoiceItem.invoiceId];
						if(UtilValidate.isEmpty(invDetailMap)){
						innerTaxItemMap=[:];
						innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
						innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
						innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
					
						innerTaxItemMap["tinNumber"]="";
						fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
							if(UtilValidate.isNotEmpty(fromPartyDetail)){
								innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
							 }
						innerTaxItemMap["vchrType"]="Purchase";
						innerTaxItemMap["crOrDbId"]="D";
						//to get Discount Item
						invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
							EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_ITEM17")) , null, null, null, false );
						if(UtilValidate.isNotEmpty(invoiceDisItemList)){
						discountInvoiceItem=invoiceDisItemList.getFirst();
						invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						tax14pt5CatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
						}
						//invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal(delegator,invoiceItem.invoiceId);
						
						//invTotalVal=invTotalVal-vatRevenue;
						innerTaxItemMap["invTotalVal"]=invTotalVal;
						innerTaxItemMap["vatAmount"]=vatRevenue;
						
						tax14pt5TotalMap["invTotalVal"]+=invTotalVal;
						tax14pt5TotalMap["vatAmount"]+=vatRevenue;
						taxDetails14pt5List.addAll(innerTaxItemMap);
						//intilize inner map when empty
						taxDetails14pt5Map[invoiceItem.invoiceId]=innerTaxItemMap;
						//Debug.log("=invoiceId==FOR FOURTEEnnn=="+invoiceItem.invoiceId+"==ANdAmouunt=="+invoiceItem.vatAmount+"==percent="+invoiceItem.vatPercent+"=Total="+invTotalVal);
						}else if(UtilValidate.isNotEmpty(invDetailMap)){
						invDetailMap["vatAmount"]+=vatRevenue;
						tax14pt5TotalMap["vatAmount"]+=vatRevenue;
						
						invDetailMap["invTotalVal"]+=invTotalVal;
						tax14pt5TotalMap["invTotalVal"]+=invTotalVal;
						
						taxDetails14pt5Map[invoiceItem.invoiceId]=invDetailMap;
						}
					}
			}
			//Caliculating CST
			if(UtilValidate.isNotEmpty(invoiceItem.cstPercent) && UtilValidate.isNotEmpty(invoiceItem.cstAmount)){
				productId = invoiceItem.productId;
				BigDecimal cstAmount = invoiceItem.cstAmount;
				invTotalVal=org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceItemTotal(invoiceItem);
				BigDecimal totalBed = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(invoiceItem.bedPercent) && UtilValidate.isNotEmpty(invoiceItem.bedAmount)){
					totalBed+=invoiceItem.bedAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedcessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedcessAmount)){
					 totalBed+=invoiceItem.bedcessAmount;
				}
				if(UtilValidate.isNotEmpty(invoiceItem.bedseccessPercent) && UtilValidate.isNotEmpty(invoiceItem.bedseccessAmount)){
				totalBed+=invoiceItem.bedseccessAmount;
				}
				invTotalVal+=totalBed;
				
				
				//preparing Another Map here for Category
				innerItemMap=[:];
				innerItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerItemMap["productId"]=invoiceItem.productId;
				innerItemMap["tinNumber"]="";
				innerItemMap["vchrType"]="Purchase";
				innerItemMap["crOrDbId"]="D";
				innerItemMap["invTotalVal"]=invTotalVal;
				innerItemMap["cstAmount"]=cstAmount;
				innerItemMap["invTotalVal"]=invTotalVal;
				// get category
				if(UtilValidate.isNotEmpty(productCatMap)&& productCatMap.get(productId)){
					prodCategoryId=productCatMap.get(productId);
					if(UtilValidate.isEmpty(taxCstCatMap[prodCategoryId])){
						innerTaxCatMap=[:];
						innerTaxCatMap["totalValue"]=invTotalVal;
						innerTaxCatMap["cstAmount"]=cstAmount;
						invoiceList=[];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						taxCstCatMap[prodCategoryId]=innerTaxCatMap;
					}else if(UtilValidate.isNotEmpty(taxCstCatMap[prodCategoryId])){
						Map innerTaxCatMap=taxCstCatMap[prodCategoryId];
						innerTaxCatMap["totalValue"]+=invTotalVal;
						innerTaxCatMap["cstAmount"]+=cstAmount;
						invoiceList=innerTaxCatMap["invoiceList"];
						invoiceList.addAll(innerItemMap);
						innerTaxCatMap["invoiceList"]=invoiceList;
						taxCstCatMap[prodCategoryId]=innerTaxCatMap;
					}
				}
				//category ends here
				invDetailMap=taxDetailsCstMap[invoiceItem.invoiceId];
				if(UtilValidate.isEmpty(invDetailMap)){
				innerTaxItemMap=[:];
				innerTaxItemMap["invoiceDate"]=invoiceItem.invoiceDate;
				innerTaxItemMap["invoiceId"]=invoiceItem.invoiceId;
				innerTaxItemMap["partyId"]=invoiceItem.partyIdFrom;
				innerTaxItemMap["tinNumber"]="";
				fromPartyDetail = (Map)(org.ofbiz.party.party.PartyWorker.getPartyIdentificationDetails(delegator, invoiceItem.partyIdFrom)).get("partyDetails");
					if(UtilValidate.isNotEmpty(fromPartyDetail)){
						innerTaxItemMap["tinNumber"]=fromPartyDetail.get('TIN_NUMBER');
					 }
				innerTaxItemMap["vchrType"]="Purchase";
				innerTaxItemMap["crOrDbId"]="D";
				//to get Discount Item
				invoiceDisItemList = delegator.findList("InvoiceAndItem",EntityCondition.makeCondition(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceItem.invoiceId),EntityOperator.AND,
					EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "COGS_ITEM17"))  , null, null, null, false );
				if(UtilValidate.isNotEmpty(invoiceDisItemList)){
				discountInvoiceItem=invoiceDisItemList.getFirst();
				invTotalVal+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
				taxCstCatMap["discount"]+=org.ofbiz.accounting.invoice.InvoiceWorker.getPurchaseInvoiceItemTotal(discountInvoiceItem,false);
				}
				innerTaxItemMap["invTotalVal"]=invTotalVal;
				innerTaxItemMap["cstAmount"]=cstAmount;
				
				taxCstTotalMap["invTotalVal"]+=invTotalVal;
				taxCstTotalMap["cstAmount"]+=cstAmount;
				
				taxDetailsCstMap[invoiceItem.invoiceId]=innerTaxItemMap;
				//Debug.log("=invoiceId==FOR FOURTEEnnn=="+invoiceItem.invoiceId+"==ANdAmouunt=="+invoiceItem.vatAmount+"==percent="+invoiceItem.vatPercent+"=Total="+invTotalVal);
				}else if(UtilValidate.isNotEmpty(invDetailMap)){
				invDetailMap["cstAmount"]+=cstAmount;
				taxCstTotalMap["cstAmount"]+=cstAmount;
				
				invDetailMap["invTotalVal"]+=invTotalVal;
				taxCstTotalMap["invTotalVal"]+=invTotalVal;
				
				taxDetailsCstMap[invoiceItem.invoiceId]=invDetailMap;
				}
				
			}
		}
		//Debug.log("=taxDetailsCstMap==="+taxDetailsCstMap);
		if (invoiceItemsIter != null) {
			try {
				invoiceItemsIter.close();
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
			}
		}
		if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT5PT5")){
			//context.put("taxDetails5pt5List",taxDetails5pt5List);
			context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="VAT14PT5")){
		     context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
		    //context.put("taxDetails14pt5List",taxDetails14pt5List);
		}else if(UtilValidate.isNotEmpty(taxType)&&(taxType=="CST")){
		     context.put("taxDetailsCstList",taxDetailsCstMap.entrySet());
		    
		    //context.put("taxDetails14pt5List",taxDetails14pt5List);
		}else{
		//context.put("taxDetails5pt5List",taxDetails5pt5List);
		//context.put("taxDetails14pt5List",taxDetails14pt5List);
		context.put("taxDetails5pt5List",taxDetails5pt5Map.entrySet());
		context.put("taxDetails14pt5List",taxDetails14pt5Map.entrySet());
		context.put("taxDetailsCstList",taxDetailsCstMap.entrySet());
		}
		context.put("tax5pt5TotalMap",tax5pt5TotalMap);
		context.put("tax14pt5TotalMap",tax14pt5TotalMap);
		context.put("taxCstTotalMap",taxCstTotalMap);
		
		//preparing catageoryMap for Vat and CST
		
		context.put("tax5pt5CatMap",tax5pt5CatMap);
		context.put("tax14pt5CatMap",tax14pt5CatMap);
		context.put("taxCstCatMap",taxCstCatMap);
		
		Debug.log("tax5pt5CatMap====="+tax5pt5CatMap);
		
		/*invTaxTotalmap=org.ofbiz.accounting.invoice.InvoiceWorker.getPeriodPurchaseInvoiceTotals(dctx, [fromDate:dayBegin, thruDate:dayEnd]);
		Debug.log("invTaxTotalmap====="+invTaxTotalmap);*/
		
		taxParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX4"), false);
		taxAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX4"), false);
		context.taxParty = taxParty;
		context.taxAuthority = taxAuthority;
		invItemTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "VAT_PUR"), false);
		context.invItemTypeGl = invItemTypeGl;
		//cst GL
		taxCstParty = delegator.findOne("Party", UtilMisc.toMap("partyId", "TAX9"), false);
		taxCstAuthority = delegator.findOne("TaxAuthority", UtilMisc.toMap("taxAuthGeoId","IND", "taxAuthPartyId","TAX9"), false);
		context.taxCstParty = taxCstParty;
		context.taxCstAuthority = taxCstAuthority;
		invItemCstTypeGl = delegator.findOne("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", "CST_PUR"), false);
		context.invItemCstTypeGl = invItemCstTypeGl;
		
// Purchase Abstract report


