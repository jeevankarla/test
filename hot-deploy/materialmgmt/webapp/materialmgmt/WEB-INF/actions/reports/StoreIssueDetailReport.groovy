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
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

dctx = dispatcher.getDispatchContext();
context.put("dctx",dctx);
fromDate=parameters.storeFromDate;
thruDate=parameters.storeThruDate;
reportTypeFlag = parameters.reportTypeFlag;
context.reportTypeFlag = reportTypeFlag;
productCategoryId = parameters.categoryType;
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
isByParty = Boolean.TRUE;

productIds=[];
partyId=parameters.partyId;
facilityId=parameters.issueToFacilityId;
			if(UtilValidate.isNotEmpty(facilityId)){
				facilities=delegator.findOne("Facility",["facilityId":facilityId],false);
				context.facilityId=facilities.facilityName;
			}
if(UtilValidate.isNotEmpty(facilityId)){
		ecl=EntityCondition.makeCondition([EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facilityId )],EntityOperator.AND);
		productFacilityList = delegator.findList("ProductFacility", ecl, UtilMisc.toSet("facilityId","productId"), null, null, false);
		productIdList = EntityUtil.getFieldListFromEntityList(productFacilityList, "productId", true);
		condition=EntityCondition.makeCondition([EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList),EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,"RAW_MATERIAL")],EntityOperator.AND);
		productList=delegator.findList("Product",condition,UtilMisc.toSet("productId"),null,null,false);
		productIds=EntityUtil.getFieldListFromEntityList(productList,"productId", true);
}
		custRequestIds=[];
		conditionList=[];
/*if(UtilValidate.isNotEmpty(facilityId)){
	conditionList.add(EntityCondition.makeCondition("facilityId", ,EntityOperator.EQUALS, facilityId));
}*/
		issueCondition=EntityCondition.makeCondition([EntityCondition.makeCondition("issuedDateTime",EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin),
													 EntityCondition.makeCondition("issuedDateTime",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd)],EntityOperator.AND);
		storeIssuedDetails= delegator.findList("ItemIssuanceInventoryItemAndProduct",issueCondition,UtilMisc.toSet("custRequestId"),null,null,false);
		custRequestIds=EntityUtil.getFieldListFromEntityList(storeIssuedDetails,"custRequestId",true);
if(UtilValidate.isNotEmpty(partyId)){
	    conditionList.add(EntityCondition.makeCondition("fromPartyId", ,EntityOperator.EQUALS, partyId));
}
/*
conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)));
conditionList.add(EntityCondition.makeCondition("custRequestDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)));*/
		conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
		condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		storeAbst= delegator.findList("CustRequestAndCustRequestItem",condition,UtilMisc.toSet("custRequestId","custRequestDate","productId","custRequestItemSeqId","fromPartyId"),null,null,false);
		deptIds = EntityUtil.getFieldListFromEntityList(storeAbst, "fromPartyId", true);
		prodDeptMap=[:];
if(UtilValidate.isNotEmpty(deptIds)){
		 
	     deptIds.each{fromPartyId->
			 
	     deptName =  PartyHelper.getPartyName(delegator, fromPartyId, false);
		 conditionList.clear();
		 if(UtilValidate.isNotEmpty(facilityId)){
			 conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.IN, productIds));
		 }
		 conditionList.add(EntityCondition.makeCondition("fromPartyId",EntityOperator.EQUALS, fromPartyId));
		 /*conditionList.add(EntityCondition.makeCondition("custRequestDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDateTime)));
		 conditionList.add(EntityCondition.makeCondition("custRequestDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateTime)));*/
		 conditionList.add(EntityCondition.makeCondition("custRequestId",EntityOperator.IN,custRequestIds));
		 condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		 productIdsDetails = delegator.findList("CustRequestAndCustRequestItem", condition , null, null, null, false );
		 if(UtilValidate.isEmpty(facilityId)){
			 productIds = EntityUtil.getFieldListFromEntityList(productIdsDetails, "productId", true);		
		 } 
         conditionList.clear();
		 conditionList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
		 conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
		 conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
		 conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
				                                         EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
		 if(UtilValidate.isNotEmpty(productCategoryId)){
			conditionList.add(EntityCondition.makeCondition("productCategoryId", ,EntityOperator.EQUALS, productCategoryId));
		 }
		 condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		 productCatDetails = delegator.findList("ProductCategoryAndMember", condition, UtilMisc.toSet("productCategoryId","productId","fromDate","thruDate"), null, null, false);
         productCatIds = EntityUtil.getFieldListFromEntityList(productCatDetails,"productCategoryId", true);
         prodMap=[:];
         if(UtilValidate.isNotEmpty(productCatIds)){
				       
 
                        productCatIds.each{productCatId->
							
				             prodList=[];
			                 if(UtilValidate.isNotEmpty(productIdsDetails)){
								 
				                      productIdsDetails.each{custReq->	
					                        productDetailMap=[:];
											
					                        custRequestId=custReq.custRequestId;
					                        custRequestDate=custReq.custRequestDate;
					                        productId=custReq.productId;
											
											if(productId){								
											conditionList.clear();
											conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productId));
											condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
											productCatTypeDetails = EntityUtil.filterByCondition(productCatDetails,condition);							
											productCatTypes= EntityUtil.getFirst(productCatTypeDetails); 
											if((productCatTypes) && (productCatTypes.productCategoryId)){								
											      productCatTypeId=productCatTypes.productCategoryId;	
												  if(productCatTypeId.equals(productCatId)){									 									     
						                                 custRequestItemSeqId=custReq.custRequestItemSeqId;								   
											             quantity=custReq.quantity;							
						                                 productDetailMap["custRequestId"]=custRequestId;
						                                 //productDetailMap["custRequestDate"]=custRequestDate;														
												         productDetails = delegator.findOne("Product",["productId":productId],false);
												         if(productDetails){
															 productDetailMap["productId"]=productDetails.productId;															 
												             productDetailMap["internalName"]=productDetails.internalName;
															 productDetailMap["productName"]=productDetails.productName;											 
								                                     productDetailMap["description"]=productDetails.description;
														             uomId=productDetails.quantityUomId;
														  }
											             if(UtilValidate.isNotEmpty(uomId)){
												                unitDesciption = delegator.findOne("Uom",["uomId":uomId],false);
											                    productDetailMap["unit"]=unitDesciption.abbreviation;
											              }							
						                                 conditionList.clear();
							                             conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
							                             conditionList.add(EntityCondition.makeCondition("custRequestItemSeqId", EntityOperator.EQUALS, custRequestItemSeqId));
							                             conditionList.add(EntityCondition.makeCondition("issuedDateTime",EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
														 conditionList.add(EntityCondition.makeCondition("issuedDateTime",EntityOperator.LESS_THAN_EQUAL_TO,dayEnd));
							                             condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
							                             storeAbstQtyDetails= delegator.findList("ItemIssuanceInventoryItemAndProduct",condition,null,null,null,false);
														 //productDetailMap.
														 unitPrice=0;
							                             totQty=0;
							                             totunitCost=0;
														 totVal=0;
														 dateWiseMap=[:];
						                                if(UtilValidate.isNotEmpty(storeAbstQtyDetails)){	
															if(storeAbstQtyDetails.size()>1){
						                                      storeAbstQtyDetails.each{storeAbstQty->
						                                          unitCost=0;
									                              itemquantity=storeAbstQty.quantity;
									                              if(UtilValidate.isNotEmpty(storeAbstQty.unitCost)&& storeAbstQty.unitCost != null){
																	 unitCost = storeAbstQty.unitCost;
							                                      }
																  if(UtilValidate.isNotEmpty(storeAbstQty.cancelQuantity)&& storeAbstQty.cancelQuantity != null){
																	  itemquantity=itemquantity-storeAbstQty.cancelQuantity;
																	  //unitCost = unitCost - storeAbstQty.unitCost;
																  }			
																  totQty =totQty+itemquantity;
									                             totunitCost=totunitCost+unitCost;
																  totVal+=itemquantity*unitCost;
																  productDetailMap.custRequestDate=storeAbstQty.issuedDateTime;
						                                      }
															}else{
																storeAbstQty=EntityUtil.getFirst(storeAbstQtyDetails);
																unitCost=0;
																quantity=storeAbstQty.quantity;
																if(UtilValidate.isNotEmpty(storeAbstQty.unitCost)&& storeAbstQty.unitCost != null){
																	unitCost=storeAbstQty.unitCost;
																}  
																if(UtilValidate.isNotEmpty(storeAbstQty.cancelQuantity)&& storeAbstQty.cancelQuantity != null){
																	quantity-=storeAbstQty.cancelQuantity;
																	//unitCost = unitCost - storeAbstQty.unitCost;
																}
																totQty=quantity;
																totunitCost=totunitCost+unitCost;
																 totVal=quantity*unitCost;
																 productDetailMap.custRequestDate=storeAbstQty.issuedDateTime;
															}
															storeAbstQtyDetails.each{storeAbs->
																dateIssued=UtilDateTime.toDateString(storeAbs.issuedDateTime,"dd/MM/yyyy");
																itemQuantity=storeAbs.quantity;
																itemIssuedDate=storeAbs.issuedDateTime;
																
																if(UtilValidate.isNotEmpty(storeAbs.unitCost)&& storeAbs.unitCost != null){
																    itemUnitCost = storeAbs.unitCost;
																} 
																if(UtilValidate.isNotEmpty(storeAbs.cancelQuantity)&& storeAbs.cancelQuantity != null){
																	itemQuantity-=storeAbs.cancelQuantity;
																	//itemUnitCost-= storeAbs.unitCost;
																}
																 totalValue=itemQuantity*itemUnitCost;
																 internalCode=storeAbs.internalName;
																 itemCustId=storeAbs.custRequestId;
																 itemProductId=storeAbs.productId;
																 pdctDetail = delegator.findOne("Product",["productId":itemProductId],false);
																 itemUnit="Non";
																 if(UtilValidate.isNotEmpty(pdctDetail)){
																 Id=pdctDetail.quantityUomId;
																 if(UtilValidate.isNotEmpty(Id)){
																 unitDesc = delegator.findOne("Uom",["uomId":Id],false);
																 itemUnit=unitDesc.abbreviation;
																 }
																 }
																 itemDescription=storeAbs.description;
																 
																if(UtilValidate.isEmpty(dateWiseMap[dateIssued])){
																	tempMap=[:];
																	tempMap.itemQuantity=itemQuantity;
																	tempMap.itemIssuedDate=itemIssuedDate;
																	tempMap.totalValue=totalValue;
																	tempMap.internalCode=internalCode;
																	tempMap.itemCustId=itemCustId;
																	tempMap.itemProductId=itemProductId;
																	tempMap.itemDescription=itemDescription;
																	tempMap.itemUnit=itemUnit;
																	dateWiseMap[dateIssued]=tempMap;
																}else{
																	tempProdMap = [:];
																	tempProdMap.putAll(dateWiseMap.get(dateIssued));
																	tempProdMap.put("totalValue",tempProdMap.get("totalValue")+totalValue);
																	tempProdMap.put("itemQuantity",tempProdMap.get("itemQuantity")+itemQuantity);
																	dateWiseMap[dateIssued]=tempProdMap;
																}
															}	
																productDetailMap["dateWiseMap"]=dateWiseMap;
															 /* if(storeAbstQtyDetails.size()>1){
														         if(totunitCost != 0){  
							                                           unitPrice=totQty/totunitCost;
																	   productDetailMap["unitPrice"]=unitPrice;
																	   totVal=	totQty*	unitPrice;
														         }else{
																 productDetailMap["unitPrice"]=unitPrice;
														         }
															  }
															  else{
																  productDetailMap["unitPrice"]=unitCost;
																  totVal=	totQty*	unitCost;
															  }*/
								                            
								                            productDetailMap["totQty"]=totQty;				                           
								                            productDetailMap["totVal"]=totVal;
															productDetailMap["totunitCost"]=totunitCost;
						                              }
						                              else{									
								                            productDetailMap["totQty"]=0;
								                            productDetailMap["unitPrice"]=0;
								                            productDetailMap["totVal"]=0;									
						                              }	
													  if(totQty != 0){										  
						                                 prodList.addAll(productDetailMap);
													  }
							                    }
					                       }																	
				                       }						
                                 }					 
					 
	                      }	
						 if(UtilValidate.isNotEmpty(prodList)){
					         prodMap.put(productCatId,prodList);			 
						 }
                       }
                    }
				if(UtilValidate.isNotEmpty(prodMap)){
			       prodDeptMap.put(deptName,prodMap);
			    }
       }
	  context.prodDeptMap=prodDeptMap;
}
//get productDetails for abstract
deptMap=[:];
for(Map.Entry entryDep : prodDeptMap.entrySet()){
	dept = entryDep.getKey();
	deptDetails = entryDep.getValue();
	CatMap=[:];
	for(Map.Entry entryCat : deptDetails.entrySet()){
		cat=entryCat.getKey();
		catDetails = entryCat.getValue();
			productsMap=[:];
			for(eachentry in catDetails){
				productId = eachentry.productId;				
				tempMap=[:];
				if(UtilValidate.isEmpty(productsMap[productId])){
					tempMap = [:];
					tempMap["internalName"]=eachentry.internalName;					
					tempMap["productName"]=eachentry.productName;
					tempMap["unit"]=eachentry.unit;					
					tempMap["totQty"]=eachentry.totQty;
					tempMap["totVal"]=eachentry.totVal;
					tempMap["totunitCost"]=eachentry.totunitCost;
					productsMap.put(productId,tempMap);
				}else{
					tempProdMap = [:];
					tempProdMap.putAll(productsMap.get(productId));
					tempProdMap.put("totQty",tempProdMap.get("totQty")+eachentry.totQty);
					tempProdMap.put("totVal",tempProdMap.get("totVal")+eachentry.totVal);
					tempProdMap.put("totunitCost", tempProdMap.get("totunitCost")+eachentry.totunitCost);
					productsMap.put(productId,tempProdMap);
				}
			}
		CatMap.put(cat, productsMap);
	}
	deptMap.put(dept, CatMap);
}
context.deptMap=deptMap;



	 