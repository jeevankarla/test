import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;

tenderList = [];
tenderDetailsMap = [:];
quant = [];
pId = null;
sId = null;
custRequestId = context.custRequestId;
selectFields = ["custRequestName","openDateTime","closedDateTime","statusId"] as Set;
validTenderList = delegator.findList("CustRequest", EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS, custRequestId), selectFields , null, null, false);
if(validTenderList.size() > 0){
	tenderDetailsMap.putAt("custRequestName",validTenderList.getAt("custRequestName").getAt(0));
	tenderDetailsMap.putAt("openDateTime",validTenderList.getAt("openDateTime").getAt(0));
	tenderDetailsMap.putAt("closedDateTime",validTenderList.getAt("closedDateTime").getAt(0));
	tenderDetailsMap.putAt("statusId",validTenderList.getAt("statusId").getAt(0));
	tenderList.add(tenderDetailsMap);
}
context.put("enquiryDetailsMap",tenderDetailsMap);
poCreateFlag = "N";
if(poCreateFlag){
	poCreateFlag = "Y";
}
context.poCreateFlag = poCreateFlag;