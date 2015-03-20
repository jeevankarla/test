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


organizationPartyId=parameters.partyId;
parentTypeId=parameters.parentTypeId;

context.organizationPartyId=organizationPartyId;
context.parentTypeId=parentTypeId;

invoiceItemList=[];

conditionList = [];
conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
if(parentTypeId){
   conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId));
}
cond =EntityCondition.makeCondition(conditionList, EntityOperator.AND);
invoiceItemTypeList = delegator.findList("InvoiceItemTypeMapInvoiceType", cond , null, ["invoiceTypeId"], null, false);

 
invoiceItemTypeList.each{ eachItem ->
	 tempMap=[:];
	 
	 tempMap.put("invoiceType",eachItem.description);
	 tempMap.put("invoiceItemTypeId", eachItem.invoiceItemTypeId);
	 tempMap.put("description", eachItem.invoiceItemTyeDescription);
	 tempMap.put("glAccountId", eachItem.defaultGlAccountId);
	 invoiceItemList.addAll(tempMap);
 }
context.invoiceItemList=invoiceItemList;
 