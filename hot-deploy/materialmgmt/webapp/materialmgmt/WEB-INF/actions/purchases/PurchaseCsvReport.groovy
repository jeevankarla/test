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
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;



dctx = dispatcher.getDispatchContext();

context.put("dctx",dctx);
reportTypeFlag = parameters.reportTypeFlag;
Debug.log("reportTypeFlag==="+reportTypeFlag);

if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseDetails"){
	primaryCatMap=context.get("dayWiseInvoice");
	prodCatAnalysisList=[];
	
	primaryCatMap.each {eachValue->
		primaryProductCategory = delegator.findOne("ProductCategory", ["productCategoryId" : eachValue.getKey()], true);
		 catMap=eachValue.getValue();
		 catMap.each{eachCat->
			productCategory = delegator.findOne("ProductCategory", ["productCategoryId" : eachCat.getKey()], true);
			invMap=eachCat.getValue();
			  invMap.each{invoice->
				  invValue=invoice.getValue();
				  invValue.each {invDtls->
				  csvMap=[:]
					  if(!invDtls.getKey().equals("invoiceDate") && !invDtls.getKey().equals("supInvNumber")){
						  csvMap.put("analysisCode",productCategory.description);
						  csvMap.put("primaryCategoryCode",primaryProductCategory.description);
						  csvMap.put("voucherCode","Analysis Code");
						  csvMap.put("invoiceDate",invValue.get("invoiceDate"));
						  csvMap.put("invoiceId",invoice.getKey());
						  prodDetails = delegator.findOne("Product", ["productId" :invDtls.getKey()], true);
						  csvMap.put("productId",prodDetails.description);
						  csvMap.put("totalRevenue",invDtls.getValue());
						  prodCatAnalysisList.add(csvMap);
						  Debug.log("prodCatAnalysisList=="+prodCatAnalysisList);
					  }
			     }
			 
		    }
		}
	
	}
context.put("prodCatAnalysisList",prodCatAnalysisList);
}
	
// Purchase Abstract report
if(UtilValidate.isNotEmpty(reportTypeFlag) && reportTypeFlag == "PurchaseSummary"){
	primaryCatMap=context.get("prodMap");
	prodCatAnalysisList=[];
	totAmount=0;
	count=0;
	primaryCatMap.each {eachValue->
		 primaryProductCategory = delegator.findOne("ProductCategory", ["productCategoryId" : eachValue.getKey()], true);
		 catMap=eachValue.getValue();
		 catMap.each{eachCat->
			productCategory = delegator.findOne("ProductCategory", ["productCategoryId" : eachCat.getKey()], true);
			partyMap=eachCat.getValue();
			partyMap.each{party->
		          csvMap=[:]
				  csvMap.put("analysisCode",productCategory.description);
				  csvMap.put("primaryCategoryCode",primaryProductCategory.description);
				  csvMap.put("voucherCode","Analysis Code");
				  csvMap.put("totalRevenue",party.getValue());
				  prodCatAnalysisList.add(csvMap);
		    }
		}
	}
context.put("prodCatAnalysisList",prodCatAnalysisList);
}

