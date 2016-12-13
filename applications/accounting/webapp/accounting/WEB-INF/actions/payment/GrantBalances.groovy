import java.util.ArrayList;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import in.vasista.vbiz.byproducts.ByProductServices;
import org.ofbiz.product.product.ProductWorker;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.network.LmsServices;
import in.vasista.vbiz.byproducts.TransporterServices;
import org.ofbiz.party.party.PartyHelper;



//Grant Utilization for Implementation Grants

List<GenericValue> utilizationfinAccntTypeList = [];
List condList=[];
utilizationamount = 0;
condList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
condList.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "GRANT_DISB"));
EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
List<GenericValue> finAccountTypeList1 = delegator.findList("FinAccountTrans", cond, null, null, null, false);

if(UtilValidate.isNotEmpty(finAccountTypeList1)){
	utilizationfinAccntTypeList = EntityUtil.getFieldListFromEntityList(finAccountTypeList1, "amount", true);
	
}

utilizationfinAccntTypeList.each { amt1 ->
	utilizationamount = (utilizationamount + amt1).setScale(2,BigDecimal.ROUND_HALF_UP);

}
context.utilizationamount = utilizationamount;

//Grant Balance for Implementation Grants


List<GenericValue> balancefinAccntTypeList = [];
List cond1List=[];
balanceamount = 0;
cond1List.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
cond1List.add(EntityCondition.makeCondition("finAccountTransTypeId", EntityOperator.EQUALS, "WITHDRAWAL"));
EntityCondition cond1 = EntityCondition.makeCondition(cond1List, EntityOperator.AND);
List<GenericValue> finAccountTypeList2 = delegator.findList("FinAccountTrans", cond1, null, null, null, false);

if(UtilValidate.isNotEmpty(finAccountTypeList2)){
	balancefinAccntTypeList = EntityUtil.getFieldListFromEntityList(finAccountTypeList2, "amount", true);
	
	}

balancefinAccntTypeList.each { amt2 ->
	balanceamount = (balanceamount + amt2).setScale(2,BigDecimal.ROUND_HALF_UP);
	
	finalbalanceamount = utilizationamount - balanceamount;
	context.finalbalanceamount = finalbalanceamount;
}

finalbalanceamount = utilizationamount - balanceamount;
context.finalbalanceamount = finalbalanceamount;

//Grant Utilization for Developmental Grants

List<GenericValue> utilizationfinAccntTypeList1 = [];

utilizationDevamount = 0;

conlist = [];
invoiceIdsList = [];
invoiceIdsList1 = [];
conlist.add(EntityCondition.makeCondition("referenceNumber", EntityOperator.EQUALS, finAccountId));
EntityCondition invcondition = EntityCondition.makeCondition(conlist, EntityOperator.AND);
List<GenericValue> invoiceIdsList = delegator.findList("Invoice", invcondition, null, null, null, false);
if(UtilValidate.isNotEmpty(invoiceIdsList)){
	invoiceIdsList1 = EntityUtil.getFieldListFromEntityList(invoiceIdsList, "invoiceId", true);
}
invoiceIdsList1.each { inv ->
	List conddList=[];
conddList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, inv));
EntityCondition condd = EntityCondition.makeCondition(conddList, EntityOperator.AND);
List<GenericValue> finAccountTypeList3 = delegator.findList("InvoiceItem", condd, null, null, null, false);

if(UtilValidate.isNotEmpty(finAccountTypeList3)){
	finAccountTypeList3.each{finAcct->
		
		amt3 = finAcct.amount;
		utilizationDevamount = (utilizationDevamount + amt3).setScale(2,BigDecimal.ROUND_HALF_UP);
		
	}
	
	}

}
context.utilizationDevamount = utilizationDevamount;

	
//Grant Balance for Developmental Grants


List<GenericValue> balanceDevfinAccntTypeList = [];
List condd1List=[];
BigDecimal balanceDevamount = BigDecimal.ZERO;
condd1List.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
EntityCondition condd1 = EntityCondition.makeCondition(condd1List, EntityOperator.AND);
List<GenericValue> finAccountTypeDevList2 = delegator.findList("FinAccount", condd1, null, null, null, false);

if(UtilValidate.isNotEmpty(finAccountTypeDevList2)){
	balanceDevfinAccntTypeList = EntityUtil.getFieldListFromEntityList(finAccountTypeDevList2, "actualBalance", true);
}

balanceDevfinAccntTypeList.each { amt4 ->
	balanceDevamount = (balanceDevamount + amt4).setScale(2,BigDecimal.ROUND_HALF_UP);;
}

context.balanceDevamount = balanceDevamount;












