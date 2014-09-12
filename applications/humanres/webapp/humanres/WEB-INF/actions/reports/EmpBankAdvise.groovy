
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.party.contact.ContactMechWorker;
import java.lang.Integer;

if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap))
bankWiseEmplDetailsMap=bankWiseEmplDetailsMap;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap))
CanaraBankMap=CanaraBankMap;
if(UtilValidate.isNotEmpty(BankAdvicePayRollMap))
BankAdvicePayRollMap=BankAdvicePayRollMap;


finalList=[];

if(UtilValidate.isNotEmpty(bankWiseEmplDetailsMap)){
	Iterator empIter = bankWiseEmplDetailsMap.entrySet().iterator();
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		emplIds=empEntry.getValue();
		List conditionList=[];
		if(UtilValidate.isNotEmpty(empEntry.getKey())){
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, empEntry.getKey()));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		bankDetails= delegator.findList("FinAccount", condition, UtilMisc.toSet("finAccountName"), null, null, false );
		if(UtilValidate.isNotEmpty(bankDetails)){
			details=EntityUtil.getFirst(bankDetails);
			bankName=details.get("finAccountName");
		}
		emplIds.each{ emplId->
			finalMap=[:];
			finalMap.put("bankName",bankName);
			finalMap.put("emplId",emplId);
			name="";
			acNo="";
			netAmt="";
			if(BankAdvicePayRollMap && BankAdvicePayRollMap.get(emplId)){
				name=BankAdvicePayRollMap.get(emplId).get("empName");
				acNo=BankAdvicePayRollMap.get(emplId).get("acNo");
				netAmt=BankAdvicePayRollMap.get(emplId).get("netAmt");
			}
			finalMap.put("name",name);
			finalMap.put("acNo",acNo);
			finalMap.put("netAmt",netAmt);
			finalList.add(finalMap);
		}
	}
}
//canara bank
if(UtilValidate.isNotEmpty(CanaraBankMap)){
	Iterator empIter = CanaraBankMap.entrySet().iterator();
	while(empIter.hasNext()){
		Map.Entry empEntry = empIter.next();
		emplIds=empEntry.getValue();
		List conditionList=[];
		if(UtilValidate.isNotEmpty(empEntry.getKey())){
			conditionList.add(EntityCondition.makeCondition("finAccountId", EntityOperator.EQUALS, empEntry.getKey()));
		}
		condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		bankDetails= delegator.findList("FinAccount", condition, UtilMisc.toSet("finAccountName"), null, null, false );
		if(UtilValidate.isNotEmpty(bankDetails)){
			details=EntityUtil.getFirst(bankDetails);
			bankName=details.get("finAccountName");
		}
		emplIds.each{ emplId->
			finalMap=[:];
			name="";
			acNo="";
			netAmt="";
			if(BankAdvicePayRollMap && BankAdvicePayRollMap.get(emplId)){
				name=BankAdvicePayRollMap.get(emplId).get("empName");
				acNo=BankAdvicePayRollMap.get(emplId).get("acNo");
				netAmt=BankAdvicePayRollMap.get(emplId).get("netAmt");
				finalMap.put("bankName",bankName);
				finalMap.put("emplId",emplId);
				finalMap.put("name",name);
				finalMap.put("acNo",acNo);
				finalMap.put("netAmt",netAmt);
				
			}
				finalList.add(finalMap);
		}
	}
}
context.finalList=finalList;











































