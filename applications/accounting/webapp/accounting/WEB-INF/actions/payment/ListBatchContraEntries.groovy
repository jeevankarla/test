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

Debug.log("Hai=================Welcome to New Groovy=======================");

finAccountTransList = [];
finAccountId = parameters.finAccountId;
amountStr = parameters.amount;
contraRefNum = parameters.contraRefNum;
transactionDate = parameters.transactionDate;
finAccountTransTypeId = parameters.finAccountTransTypeId;
finAccountTransGroupId = parameters.finAccountTransGroupId;
Debug.log("finAccountId====================="+finAccountId);
transDate = null;
transDateStart = null;
transDateEnd = null;
if(UtilValidate.isNotEmpty(transactionDate)){
	def sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
		transDate = new java.sql.Timestamp(sdf.parse(transactionDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + transactionDate, "");
	}
	transDateStart = UtilDateTime.getDayStart(transDate);
	transDateEnd = UtilDateTime.getDayEnd(transDate);
}
List conditionList=[];
if(UtilValidate.isNotEmpty(finAccountId)){
	conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, finAccountId));
}
if(UtilValidate.isNotEmpty(finAccountTransGroupId)){
	conditionList.add(EntityCondition.makeCondition("finAccntTransGroupId", EntityOperator.EQUALS, finAccountTransGroupId));
}
if(UtilValidate.isNotEmpty(amountStr)){
	BigDecimal amount = new BigDecimal(amountStr);
	conditionList.add(EntityCondition.makeCondition("amount", EntityOperator.EQUALS, amount));
}
if(UtilValidate.isNotEmpty(contraRefNum)){
	conditionList.add(EntityCondition.makeCondition("contraRefNum", EntityOperator.EQUALS, contraRefNum));
}
if(UtilValidate.isNotEmpty(transactionDate)){
	conditionList.add(EntityCondition.makeCondition("finAccntTransDate", EntityOperator.GREATER_THAN_EQUAL_TO, transDateStart));
	conditionList.add(EntityCondition.makeCondition("finAccntTransDate", EntityOperator.LESS_THAN_EQUAL_TO, transDateEnd));
}

conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.NOT_EQUAL, "FIN_ACCNT1"));
//conditionList.add(EntityCondition.makeCondition("reasonEnumId", EntityOperator.EQUALS, "FATR_CONTRA"));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FNACTTRNSGRP_CREATED"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
Debug.log("condition========================"+condition);
finAccountTransList = delegator.findList("FinAccountTransGroup", condition , null, null, null, false );
Debug.log("finAccountTransList=========================="+finAccountTransList);
context.finAccountTransList=finAccountTransList;
