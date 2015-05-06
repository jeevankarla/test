import java.sql.*

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

fromDate=parameters.convFromDate;
thruDate=parameters.convThruDate;
//Debug.log("fromDate==============================="+fromDate);

dctx = dispatcher.getDispatchContext();
fromDateTime = null;
thruDateTime = null;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	fromDateTime = new java.sql.Timestamp(sdf.parse(fromDate).getTime());
	thruDateTime = new java.sql.Timestamp(sdf.parse(thruDate).getTime());
	//Debug.log("fromDateTime==========================="+fromDateTime);
	
} catch (ParseException e) {
	Debug.logError(e, "Cannot parse date string: "+fromDate, "");
}
dayBegin = UtilDateTime.getDayStart(fromDateTime);
dayEnd = UtilDateTime.getDayEnd(thruDateTime);
//Debug.log("dayBegin==============================="+dayBegin);

context.fromDate = dayBegin;
context.thruDate = dayEnd;

totalDays=UtilDateTime.getIntervalInDays(fromDateTime,thruDateTime);

/*isByParty = Boolean.TRUE;
if(totalDays > 32){
	Debug.logError("You Cannot Choose More Than 31 Days.","");
	context.errorMessage = "You Cannot Choose More Than 31 Days";
	return;
}
*/
conditionList =[];
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayBegin));
conditionList.add(EntityCondition.makeCondition("receiveDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
MilkTransferList = delegator.findList("MilkTransferAndMilkTransferItem", condition, null,null, null, false);
//Debug.log("==========MilkTransferList========================"+MilkTransferList);
unions=null;
if(UtilValidate.isNotEmpty(MilkTransferList)){
unions=EntityUtil.getFieldListFromEntityList(MilkTransferList, "partyIds", true);
}
//Debug.log("==========unions========================"+unions);

if(UtilValidate.isNotEmpty(unions)){
	unions.each {union->
		unionList=[];
		unionList=EntityUtil.filterByCondition(MilkTransferList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, union.partyId));
		if(UtilValidate.isNotEmpty(unionList)){
		totTankers=0;
			unionList.each {unionData->
				
				totTankers=totTankers+1;
				
			}
				
		}
			
	}
}
	





