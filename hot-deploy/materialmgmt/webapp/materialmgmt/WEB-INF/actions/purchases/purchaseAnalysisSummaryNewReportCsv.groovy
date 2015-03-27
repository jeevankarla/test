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

//forCsv

prchaseCategorySummery=context.get("prchaseCategorySummeryMap");
purchaseSumCatDetaildMap=context.get("purchaseSumCatDetaildMap");
purchaseSumInvDetaildMap=context.get("purchaseSumInvDetaildMap");

//Debug.log("purchaseSumCatDetaildMap====================="+purchaseSumCatDetaildMap);
//Debug.log("parameters.reportNameFlag================"+parameters.reportNameFlag);
FinalprchaseCategorySummeryCsvList=[];
if(parameters.reportNameFlag){
	if("ProductWise"==parameters.reportNameFlag){
		//Debug.log("prchaseCategorySummery================"+prchaseCategorySummery);
		prchaseCategorySummery.each{ prchaseCategorySummery ->
						tempMap=[:];
						tempMapTotal=[:];
						//PrbName=org.ofbiz.order.order.OrderServices.nameTrim(prchaseCategorySummery.getKey().replace(',', ''),25);
						//Debug.log("PrbName========================"+PrbName);
						tempMap.put("productName",prchaseCategorySummery.getKey());
						FinalprchaseCategorySummeryCsvList.addAll(tempMap);
						if(purchaseSumCatDetaildMap){
						//Debug.log("purchaseSumCatDetaildMap=============="+purchaseSumCatDetaildMap.get(prchaseCategorySummery.getKey()));
						purchaseProdCatList=purchaseSumCatDetaildMap.get(prchaseCategorySummery.getKey());
						//Debug.log("purchaseProdCatList===================="+purchaseProdCatList);
						if(purchaseProdCatList){
							if((purchaseProdCatList.size()) != 1){
								totalAssCstRevenue=0;
								tempProbTotal=[:];
								purchaseProdCatList.each{ purchaseProdCat ->
									tempProb=[:];
									if(purchaseProdCat.getKey()!="discount"){
										temppurchase=[:];
										tempPuchaseTotal=[:];
										 codeId=purchaseProdCat.getKey();
										 codeIdMap=purchaseProdCat.getValue();
										 codeIdList=codeIdMap.get("invoiceList");
//												// Debug.log("codeIdList=================="+codeIdList);
										 //Debug.log("codeId==============="+codeId);
										 if(codeId){
										 productCategory = delegator.findOne("ProductCategory", [productCategoryId : codeId], false);
//										 Debug.log("productCategory.description==========================="+productCategory.description);
										 prdName="Primary Code :"+productCategory.description;
										  temppurchase.put("productName",prdName);
										// Debug.log("temppurchase==============="+temppurchase);
										 FinalprchaseCategorySummeryCsvList.addAll(temppurchase);
//												// Debug.log("productCategory=================="+productCategory);
									 productDetailList=codeIdMap.get("productDetailMap");
										 //Debug.log("productDetailList=================="+productDetailList);
										 if(productDetailList){
											 productDetailList.each{ productDetailEach ->
												 innerProductId =productDetailEach.getKey();
												 if(innerProductId!="INELIGIBLE"){
												 productPrimeryCatName="";
												 productPrimeryCategory = delegator.findOne("ProductCategory", [productCategoryId : innerProductId], false);
												if(productPrimeryCategory){
													productPrimeryCatName ="Analysis Code :"+productPrimeryCategory.description;
													tempProb.put("productName",productPrimeryCatName);
													FinalprchaseCategorySummeryCsvList.addAll(tempProb);
												}
													innerProductDetailMap =productDetailEach.getValue();
													temptotal=[:];
//															Debug.log("innerProductDetailMap====================="+innerProductDetailMap);
													innerProductDetailList=innerProductDetailMap.get("prodInvItemList");
													innerProductDetailList.each{ invTaxMap ->
														tempInvTaxMap=[:];
														invoiceDate=org.ofbiz.base.util.UtilDateTime.toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")
//																Debug.log("invoiceDate====================="+invoiceDate);
														tempInvTaxMap.put("invoiceDate",invoiceDate);
														productName="";
														productId=invTaxMap.get("productId");
														product = delegator.findOne("Product", [productId : productId], false);
														productName =product.description;
														//PrbName=org.ofbiz.order.order.OrderServices.nameTrim(productName.replace(',', ''),25);
														tempInvTaxMap.put("productName",productName);
														tempInvTaxMap.put("vchrType",invTaxMap.get("vchrType"));
														tempInvTaxMap.put("invoiceId",invTaxMap.get("invoiceId"));
														tempInvTaxMap.put("crOrDbId",invTaxMap.get("crOrDbId"));
														assableValue=invTaxMap.get("invTotalVal");
														totalAssCstRevenue=totalAssCstRevenue+assableValue;
														tempInvTaxMap.put("invTotalVal",assableValue);
														FinalprchaseCategorySummeryCsvList.addAll(tempInvTaxMap);
													}
													invTotalVal=innerProductDetailMap.get("totalValue");
													//Debug.log("invTotalVal================"+invTotalVal);
													temptotal.put("productName", "Analysis Code-Total");
													
													temptotal.put("invTotalVal", invTotalVal);
													  FinalprchaseCategorySummeryCsvList.addAll(temptotal);
												}
											 }
										 }
										 tempPuchaseTotal.put("productName", "Primery Code-Total");
										 tempPuchaseTotal.put("invTotalVal", codeIdMap.get("totalValue"));
										 FinalprchaseCategorySummeryCsvList.addAll(tempPuchaseTotal);
										 }
									}
								}
								tempProbTotal.put("productName", "TOTAL-Discount For All Analysis Codes")
								tempProbTotal.put("invTotalVal",purchaseProdCatList.get("discount"));
								FinalprchaseCategorySummeryCsvList.addAll(tempProbTotal);
								totalAssCstRevenue=totalAssCstRevenue+purchaseProdCatList.get("discount");
							}
						}
					}
						purchase=prchaseCategorySummery.getKey();
						purchaseName=purchase+"-Total";
						tempMapTotal.put("productName", purchaseName)
						tempMapTotal.put("invTotalVal",prchaseCategorySummery.getValue().get("total"));
						FinalprchaseCategorySummeryCsvList.addAll(tempMapTotal);
		}
	}else if("Detailed"==parameters.reportNameFlag){
	InvoicePartyAnalysisMap=context.get("InvoicePartyAnalysisMap");
	totalRevenue=0;
	prchaseCategorySummery.each{ prchaseCategorySummery ->
	temppurchase=[:];
	temppurchase.put("productName",prchaseCategorySummery.getKey());
	FinalprchaseCategorySummeryCsvList.addAll(temppurchase);
	prchaseCategoryDetaildList=purchaseSumInvDetaildMap.get(prchaseCategorySummery.getKey());
	if(prchaseCategoryDetaildList){
		prchaseCategoryDetaildList.each{ invTaxMap->
			tempMap=[:];
			tempMapTotal=[:];
			invoiceDate=org.ofbiz.base.util.UtilDateTime.toDateString(invTaxMap.get("invoiceDate"),"dd-MMM-yy")
			tempMap.put("invoiceDate", invoiceDate);
			partyName="";
			if(InvoicePartyAnalysisMap){
				partyId=InvoicePartyAnalysisMap.get(invTaxMap.get("invoiceId"));
				
				if(UtilValidate.isNotEmpty(partyId)){
				partyName = PartyHelper.getPartyName(delegator, partyId, false);
				//Debug.log("partyName========================"+partyName);
				tempMap.put("productName", partyName);
				}else{
				partyId=invTaxMap.get("partyId");
				partyName = PartyHelper.getPartyName(delegator, partyId, false);
				tempMap.put("productName", partyName);
				}
				tempMap.put("vchrType",invTaxMap.get("vchrType"));
				tempMap.put("invoiceId",invTaxMap.get("invoiceId"));
				tempMap.put("crOrDbId",invTaxMap.get("crOrDbId"));
				tempMap.put("invTotalVal",invTaxMap.get("invTotalVal"));
				FinalprchaseCategorySummeryCsvList.addAll(tempMap);
				}
			}
		}
	tempProbTotal=[:];
	purchase=prchaseCategorySummery.getKey();
	purchaseName=purchase+"-Total";
	tempProbTotal.put("productName", purchaseName);
	tempProbTotal.put("invTotalVal",prchaseCategorySummery.getValue().get("total"));
	FinalprchaseCategorySummeryCsvList.addAll(tempProbTotal);
		}
	}
}else{
	prchaseCategorySummery.each{ prchaseCategorySummery ->
	temppurchase=[:];
	temppurchase.put("category",prchaseCategorySummery.getKey());
	temppurchase.put("dr",prchaseCategorySummery.getValue().get("DR"));
	temppurchase.put("cr",prchaseCategorySummery.getValue().get("CR"));
	temppurchase.put("total",prchaseCategorySummery.getValue().get("total"));
	FinalprchaseCategorySummeryCsvList.addAll(temppurchase);
	}
}

//Debug.log("FinalprchaseCategorySummeryCsvList================="+FinalprchaseCategorySummeryCsvList);
context.FinalprchaseCategorySummeryCsvList=FinalprchaseCategorySummeryCsvList;