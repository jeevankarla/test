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

finalpurchasetaxDetails=[];


temp5pt0Map=context.get("taxDetails5pt0List");
partyMap=context.get("InvoicePartyMap");

if( UtilValidate.isNotEmpty(temp5pt0Map))
{
	tax5pt0list=[];
	taxDetails5pt0List=[];
	temp5pt0Map.each{ eachtemp ->
		productNameMap=[:];
		tax5pt0InnerMap=eachtemp.getValue();
		partyId=null;
		partyName=null;
	if( UtilValidate.isNotEmpty(partyMap.get(tax5pt0InnerMap.invoiceId))){
		partyId=partyMap.get(tax5pt0InnerMap.invoiceId);
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
	}
	else{
		partyId=tax5pt0InnerMap.partyId;
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
		
		}
		productNameMap.put("invoiceDate",tax5pt0InnerMap.invoiceDate);
		productNameMap.put("invoiceId",tax5pt0InnerMap.invoiceId);
		productNameMap.put("partyId",tax5pt0InnerMap.partyId);
		productNameMap.put("productId",tax5pt0InnerMap.productId);
		productNameMap.put("tinNumber",tax5pt0InnerMap.tinNumber);
		productNameMap.put("vchrType",tax5pt0InnerMap.vchrType);
		productNameMap.put("crOrDbId",tax5pt0InnerMap.crOrDbId);
		productNameMap.put("invTotalVal",tax5pt0InnerMap.invTotalVal);
		productNameMap.put("vatAmount",tax5pt0InnerMap.vatAmount);
		productNameMap.put("partyName",partyName);
		taxDetails5pt0List.addAll(productNameMap);
		}
		tax5pt0TotalMap=context.get("tax5pt0TotalMap");
		taxDetails5pt0List.addAll(tax5pt0TotalMap);
		//Debug.log("======tax5ptTotalMap==== from ========tax5ptTotalMap=========tax5ptTotalMap==========="+tax5ptTotalMap);
		finalpurchasetaxDetails.addAll(taxDetails5pt0List);
}
//Debug.log("======finalpurchasetaxDetails============finalpurchasetaxDetails=========finalpurchasetaxDetails==========="+finalpurchasetaxDetails);


//5.5 totals are here
tempMap=context.get("taxDetails5pt5List");

if( UtilValidate.isNotEmpty(tempMap))
{
	tax5ptlist=[];
	taxDetails5pt5List=[];
	tempMap.each{ eachtemp ->
		productNameMap=[:];
		tax5ptlist=eachtemp.getValue();
		partyId=null;
		partyName=null;
	if( UtilValidate.isNotEmpty(partyMap.get(tax5ptlist.invoiceId))){
		partyId=partyMap.get(tax5ptlist.invoiceId);
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
	}
	else{
		partyId=tax5ptlist.partyId;
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
		
		}
		productNameMap.put("invoiceDate",tax5ptlist.invoiceDate);
		productNameMap.put("invoiceId",tax5ptlist.invoiceId);
		productNameMap.put("partyId",tax5ptlist.partyId);
		productNameMap.put("productId",tax5ptlist.productId);
		productNameMap.put("tinNumber",tax5ptlist.tinNumber);
		productNameMap.put("vchrType",tax5ptlist.vchrType);
		productNameMap.put("crOrDbId",tax5ptlist.crOrDbId);
		productNameMap.put("invTotalVal",tax5ptlist.invTotalVal);
		productNameMap.put("vatAmount",tax5ptlist.vatAmount);
		productNameMap.put("partyName",partyName);
		taxDetails5pt5List.addAll(productNameMap);
		}
		tax5ptTotalMap=context.get("tax5pt5TotalMap");
		taxDetails5pt5List.addAll(tax5ptTotalMap);
		//Debug.log("======tax5ptTotalMap==== from ========tax5ptTotalMap=========tax5ptTotalMap==========="+tax5ptTotalMap);
		finalpurchasetaxDetails.addAll(taxDetails5pt5List);
}

temp14ptMap=context.get("taxDetails14pt5List");
if( UtilValidate.isNotEmpty(temp14ptMap))
{
	tax14ptlist=[];
	taxDetails14pt5List=[];
	
	temp14ptMap.each{ eachtemp ->
		productNameMap=[:];
		
		//Debug.log("======tempMap============tempMap=========tempMap==========="+eachtemp.getValue());
		tax14ptlist=eachtemp.getValue()
		partyId=null;
		partyName=null;
	if( UtilValidate.isNotEmpty(partyMap.get(tax14ptlist.invoiceId))){
		partyId=partyMap.get(tax14ptlist.invoiceId);
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
	}
	else{
		partyId=tax14ptlist.partyId;
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
		
		}
		productNameMap.put("invoiceDate",tax14ptlist.invoiceDate);
		productNameMap.put("invoiceId",tax14ptlist.invoiceId);
		productNameMap.put("partyId",tax14ptlist.partyId);
		productNameMap.put("productId",tax14ptlist.productId);
		productNameMap.put("tinNumber",tax14ptlist.tinNumber);
		productNameMap.put("vchrType",tax14ptlist.vchrType);
		productNameMap.put("crOrDbId",tax14ptlist.crOrDbId);
		productNameMap.put("invTotalVal",tax14ptlist.invTotalVal);
		productNameMap.put("vatAmount",tax14ptlist.vatAmount);
		productNameMap.put("partyName",partyName);
		taxDetails14pt5List.addAll(productNameMap);
		//taxDetails14pt5List.addAll(tax14ptlist);
		//Debug.log("======taxDetails14pt5List============taxDetails14pt5List=========taxDetails14pt5List==========="+taxDetails14pt5List);
		
		}
		
		tax14ptTotalMap=context.get("tax14pt5TotalMap");
		//Debug.log("======tax14ptTotalMap============tax14ptTotalMap=========tax14ptTotalMap==========="+tax14ptTotalMap);
		taxDetails14pt5List.addAll(tax14ptTotalMap);
		//Debug.log("======taxDetails14pt5List============taxDetails14pt5List=========taxDetails14pt5List==========="+taxDetails14pt5List);
		
		
		finalpurchasetaxDetails.addAll(taxDetails14pt5List);
		
}

tempCstMap=context.get("taxDetailsCstList");
if( UtilValidate.isNotEmpty(tempCstMap))
{
	taxCstList=[];
	taxDetailsCstList=[];
	tempCstMap.each{ eachtemp ->
		productNameMap=[:];
		
		//Debug.log("======tempMap============tempMap=========tempMap==========="+eachtemp.getValue());
		taxCstList=eachtemp.getValue()
		partyId=null;
		partyName=null;
	if( UtilValidate.isNotEmpty(partyMap.get(taxCstList.invoiceId))){
		partyId=partyMap.get(taxCstList.invoiceId);
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
	}
	else{
		partyId=taxCstList.partyId;
		partyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator,partyId, false);
		
		}
		productNameMap.put("invoiceDate",taxCstList.invoiceDate);
		productNameMap.put("invoiceId",taxCstList.invoiceId);
		productNameMap.put("partyId",taxCstList.partyId);
		productNameMap.put("productId",taxCstList.productId);
		productNameMap.put("tinNumber",taxCstList.tinNumber);
		productNameMap.put("vchrType",taxCstList.vchrType);
		productNameMap.put("crOrDbId",taxCstList.crOrDbId);
		productNameMap.put("invTotalVal",taxCstList.invTotalVal);
		productNameMap.put("vatAmount",taxCstList.cstAmount);
		productNameMap.put("partyName",partyName);
		taxDetailsCstList.addAll(productNameMap);
		//taxDetailsCstList.addAll(taxCstList);
		}
		
		taxCstTotalMap=context.get("taxCstTotalMap");
		//Debug.log("======taxCstTotalMap============taxCstTotalMap=========taxCstTotalMap==========="+taxCstTotalMap.get("cstAmount"));
		FinaltaxCstTotalMap=[:];
		FinaltaxCstTotalMap.put("invTotalVal",taxCstTotalMap.get("invTotalVal"));
		FinaltaxCstTotalMap.put("vatAmount",taxCstTotalMap.get("cstAmount"));
		taxDetailsCstList.addAll(FinaltaxCstTotalMap);
		//Debug.log("======taxDetailsCstList============taxDetailsCstList=========taxDetailsCstList==========="+taxDetailsCstList);
		
		finalpurchasetaxDetails.addAll(taxDetailsCstList);
}
//Debug.log("======finalpurchasetaxDetails============finalpurchasetaxDetails=========finalpurchasetaxDetails==========="+finalpurchasetaxDetails);
context.finalpurchasetaxDetails=finalpurchasetaxDetails;





// for productWise report
finalList=[];

issueToDeptInvMap.each{ eachissueToDeptInvMap ->
	//Debug.log("eachissueToDeptInvMap======================"+eachissueToDeptInvMap.getValue());
	invoicetaxvaluesMap=eachissueToDeptInvMap.getValue();
	deptId=eachissueToDeptInvMap.getKey();
	tax5pt0CatMap=invoicetaxvaluesMap.get("tax5pt0CatMap");
	tax5pt0TotalMap=invoicetaxvaluesMap.get("tax5pt0TotalMap");
	tax5pt5CatMap=invoicetaxvaluesMap.get("tax5pt5CatMap");
	tax5pt5TotalMap=invoicetaxvaluesMap.get("tax5pt5TotalMap");
	tax14pt5CatMap=invoicetaxvaluesMap.get("tax14pt5CatMap");
	tax14pt5TotalMap=invoicetaxvaluesMap.get("tax14pt5TotalMap");
	taxCstCatMap=invoicetaxvaluesMap.get("taxCstCatMap");
	taxCstTotalMap=invoicetaxvaluesMap.get("taxCstTotalMap");
	deptName=null;
	if(deptId !="Other")
	{
		deptName= org.ofbiz.party.party.PartyHelper.getPartyName(delegator,deptId, false);
	}
	else{
		deptName="OTHER";
	}
	//Debug.log("tax14pt5TotalMap====================tax14pt5TotalMap========================"+tax14pt5TotalMap);
	//Debug.log("tax5pt5TotalMap====================tax5pt5TotalMap========================"+tax5pt5TotalMap);
	
	if( UtilValidate.isNotEmpty(tax5pt0CatMap))
	{
		final5ptlist=[];
		productList=[];
		tax5pt0CatMap.each{ eachtax5pt5CatMap ->
			
			if(eachtax5pt5CatMap.getKey()!="discount")
			{
						codeIdMap=eachtax5pt5CatMap.getValue();
						codeIdList=codeIdMap.get("invoiceList");
						codeIdList.each{ eachCodeIdList ->
							productNameMap=[:];
						productId=null;
						description=null;
						productId=eachCodeIdList.productId;
						product = delegator.findOne("Product", ["productId" : productId], true);
						description=product.description;
						productNameMap.put("deptName",deptName);
						productNameMap.put("invoiceDate",eachCodeIdList.invoiceDate);
						productNameMap.put("invoiceId",eachCodeIdList.invoiceId);
						productNameMap.put("partyId",eachCodeIdList.partyId);
						productNameMap.put("productName",description);
						productNameMap.put("productId",eachCodeIdList.productId);
						productNameMap.put("vchrType",eachCodeIdList.vchrType);
						productNameMap.put("crOrDbId",eachCodeIdList.crOrDbId);
						productNameMap.put("invTotalVal",eachCodeIdList.invTotalVal);
						productNameMap.put("vatAmount",eachCodeIdList.vatAmount);
						finalList.addAll(productNameMap);
					}
						totalvalueMap=[:];
						totalvalue=null;
						totalvalue=codeIdMap.get("totalValue");
						totalvalueMap.put("invTotalVal",totalvalue);
						finalList.addAll(totalvalueMap);
			}
		}
		dicountList=[];
		dicountMap=[:];
	discount=null;
	discount=tax5pt0CatMap.get("discount");
	dicountMap.put("invTotalVal",discount);
	dicountMap.put("crOrDbId","discount");
	
	dicountList.addAll(dicountMap);
	finalList.addAll(dicountList);
	tax5pt0TotalMap.put("crOrDbId","TOTAL");
		finalList.addAll(tax5pt0TotalMap);
		
	}
	
	//adding 5.5 total Map here
	if( UtilValidate.isNotEmpty(tax5pt5CatMap))
	{
		final5ptlist=[];
		productList=[];
		tax5pt5CatMap.each{ eachtax5pt5CatMap ->
			
			if(eachtax5pt5CatMap.getKey()!="discount")
			{
						codeIdMap=eachtax5pt5CatMap.getValue();
						codeIdList=codeIdMap.get("invoiceList");
						codeIdList.each{ eachCodeIdList ->
							productNameMap=[:];
						productId=null;
						description=null;
						productId=eachCodeIdList.productId;
						product = delegator.findOne("Product", ["productId" : productId], true);
						description=product.description;
						productNameMap.put("deptName",deptName);
						productNameMap.put("invoiceDate",eachCodeIdList.invoiceDate);
						productNameMap.put("invoiceId",eachCodeIdList.invoiceId);
						productNameMap.put("partyId",eachCodeIdList.partyId);
						productNameMap.put("productName",description);
						productNameMap.put("productId",eachCodeIdList.productId);
						productNameMap.put("vchrType",eachCodeIdList.vchrType);
						productNameMap.put("crOrDbId",eachCodeIdList.crOrDbId);
						productNameMap.put("invTotalVal",eachCodeIdList.invTotalVal);
						productNameMap.put("vatAmount",eachCodeIdList.vatAmount);
						finalList.addAll(productNameMap);
					}
						totalvalueMap=[:];
						totalvalue=null;
						totalvalue=codeIdMap.get("totalValue");
						totalvalueMap.put("invTotalVal",totalvalue);
						finalList.addAll(totalvalueMap);
			}
		}
		dicountList=[];
		dicountMap=[:];
	discount=null;
	discount=tax5pt5CatMap.get("discount");
	dicountMap.put("invTotalVal",discount);
	dicountMap.put("crOrDbId","discount");
	
	dicountList.addAll(dicountMap);
	finalList.addAll(dicountList);
	tax5pt5TotalMap.put("crOrDbId","TOTAL");
		finalList.addAll(tax5pt5TotalMap);
		
	}
	if( UtilValidate.isNotEmpty(tax14pt5CatMap))
	{
		final5ptlist=[];
		productList=[];
		
		tax14pt5CatMap.each{ eachtax5pt5CatMap ->
			if(eachtax5pt5CatMap.getKey()!="discount")
			{
						codeIdMap=eachtax5pt5CatMap.getValue();
						codeIdList=codeIdMap.get("invoiceList");
					 codeIdList.each{ eachCodeIdList ->
							productNameMap=[:];
						productId=null;
						description=null;
						productId=eachCodeIdList.productId;
						product = delegator.findOne("Product", ["productId" : productId], true);
						description=product.description;
						productNameMap.put("invoiceDate",eachCodeIdList.invoiceDate);
						productNameMap.put("invoiceId",eachCodeIdList.invoiceId);
						productNameMap.put("partyId",eachCodeIdList.partyId);
						productNameMap.put("productName",description);
						productNameMap.put("productId",eachCodeIdList.productId);
						productNameMap.put("vchrType",eachCodeIdList.vchrType);
						productNameMap.put("crOrDbId",eachCodeIdList.crOrDbId);
						productNameMap.put("invTotalVal",eachCodeIdList.invTotalVal);
						productNameMap.put("vatAmount",eachCodeIdList.vatAmount);
						productNameMap.put("deptName",deptName);
						
						finalList.addAll(productNameMap);
					}
					 totalvalueMap=[:];
					 totalvalue=null;
					 totalvalue=codeIdMap.get("totalValue");
					 totalvalueMap.put("invTotalVal",totalvalue);
					 finalList.addAll(totalvalueMap);
					 
					
			}
			
		}
		dicountList=[];
			dicountMap=[:];
		discount=null;
		discount=tax14pt5CatMap.get("discount");
		dicountMap.put("invTotalVal",discount);
		dicountMap.put("crOrDbId","discount");
		
		dicountList.addAll(dicountMap);
		finalList.addAll(dicountList);
		tax14pt5TotalMap.put("crOrDbId","TOTAL");
		
		finalList.addAll(tax14pt5TotalMap);

	}
	if( UtilValidate.isNotEmpty(taxCstCatMap))
	{
		final5ptlist=[];
		productList=[];
		totalcst=0;
		taxCstCatMap.each{ eachtax5pt5CatMap ->
			
			if(eachtax5pt5CatMap.getKey()!="discount")
			{
						codeIdMap=eachtax5pt5CatMap.getValue();
						codeIdList=codeIdMap.get("invoiceList");
						//Debug.log("codeIdMap============codeIdMap================"+codeIdMap);
						
					 codeIdList.each{ eachCodeIdList ->
							productNameMap=[:];
						productId=null;
						description=null;
						productId=eachCodeIdList.productId;
						invTotalVal=eachCodeIdList.invTotalVal;
						cstAmount=eachCodeIdList.cstAmount;
						totalvalue=invTotalVal+cstAmount;
						totalcst=totalcst+totalvalue;
						//Debug.log("cstAmount============cstAmount================"+cstAmount);
						product = delegator.findOne("Product", ["productId" : productId], true);
						description=product.description;
						productNameMap.put("deptName",deptName);
						productNameMap.put("invoiceDate",eachCodeIdList.invoiceDate);
						productNameMap.put("invoiceId",eachCodeIdList.invoiceId);
						productNameMap.put("partyId",eachCodeIdList.partyId);
						productNameMap.put("productName",description);
						productNameMap.put("productId",eachCodeIdList.productId);
						productNameMap.put("vchrType",eachCodeIdList.vchrType);
						productNameMap.put("crOrDbId",eachCodeIdList.crOrDbId);
						productNameMap.put("invTotalVal",totalvalue);
						productNameMap.put("vatAmount",eachCodeIdList.vatAmount);
						finalList.addAll(productNameMap);
						
						
					}
					 totalvalueMap=[:];
					 total=null;
					 totalValue=codeIdMap.get("totalValue");
					 cstAmount=codeIdMap.get("cstAmount");
					 total=totalValue+cstAmount;
					 totalvalueMap.put("invTotalVal",total);
					 finalList.addAll(totalvalueMap);
			}
		}
		dicountList=[];
		dicountMap=[:];
		totallist=[];
		taxCstTotalMap=[:];
		discount=null;
		discount=taxCstCatMap.get("discount");
		dicountMap.put("crOrDbId","discount");
		dicountMap.put("invTotalVal",discount);
		dicountList.addAll(dicountMap);
		//Debug.log("dicountList======================="+dicountList);
		
	finalList.addAll(dicountList);
	
	totalcst=totalcst+discount;
	//Debug.log("totalcst======================="+totalcst);
	
	taxCstTotalMap.put("crOrDbId","TOTAL");
	
		taxCstTotalMap.put("invTotalVal",totalcst);
		totallist.addAll(taxCstTotalMap);
		finalList.addAll(totallist);
	
	}
	}
context.finalList=finalList;