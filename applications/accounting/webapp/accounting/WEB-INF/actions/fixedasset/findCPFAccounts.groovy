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
import org.ofbiz.entity.util.EntityListIterator;


eachfinAccountTransGroup = [];


conditionList = [];


if(parameters.groupId){
	conditionList.add(EntityCondition.makeCondition("finAccntTransGroupId", EntityOperator.EQUALS, parameters.groupId));
}
List<String> orderBy = UtilMisc.toList("-finAccntTransDate");
EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	finAccountTransGroup = delegator.findList("FinAccountTransGroup",condition , null, orderBy, null, false);

paymentcpfList = [];
for(String eachfinAccountTransGroup : finAccountTransGroup){

			tempMap = [:];
			tempMap["finAccntTransGroupId"] = eachfinAccountTransGroup.finAccntTransGroupId;
			tempMap["finAccntTransDate"] = eachfinAccountTransGroup.finAccntTransDate;
			tempMap["statusId"] = eachfinAccountTransGroup.statusId;
			tempMap["amount"] = eachfinAccountTransGroup.amount;
			tempMap["contraRefNum"] = eachfinAccountTransGroup.contraRefNum;
			tempMap["issuingAuthority"] = eachfinAccountTransGroup.issuingAuthority;
			
			paymentcpfList.add(tempMap);

}
context.paymentcpfList= paymentcpfList;









