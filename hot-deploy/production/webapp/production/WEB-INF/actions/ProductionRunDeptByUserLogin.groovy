
import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.json.JSONArray;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.purchase.MaterialHelperServices;
dctx = dispatcher.getDispatchContext();
userLogin=parameters.userLogin;
partyIdTo=userLogin.partyId;
List employmentList=FastList.newInstance();
List facilityList=FastList.newInstance();
partyIdFrom="";
/*condition=EntityCondition.makeCondition([EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyIdTo),
										 EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null)],EntityOperator.AND);
employmentList=delegator.findList("Employment",condition,null,null,null,false);
employment=EntityUtil.getFirst(employmentList);
 partyRole=delegator.findOne("PartyRole",[partyId:partyIdTo,roleTypeId:"PRODUCTION_RUN"],false);*/
Map inputMap = FastMap.newInstance();
inputMap.clear();
inputMap.put("userLogin",userLogin);
inputMap.put("partyId",partyIdTo);
inputMap.put("roleTypeIdTo","PRODUCTION_RUN");
resultMap=MaterialHelperServices.getDepartmentByUserLogin(dctx, inputMap);
 if(UtilValidate.isNotEmpty(resultMap.get("deptId"))){
	 if((resultMap.get("deptId")).size()>1){
		 partyIdFromList= resultMap.get("deptId");
		 facilityList=delegator.findList("Facility",EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,partyIdFromList),
			 																		EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT")],EntityOperator.AND),null,null,null,false);
	 }else{
		partyIdFrom=(resultMap.get("deptId")).get(0);
		facilityList=delegator.findList("Facility",EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyIdFrom),
																					  EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT")],EntityOperator.AND),null,null,null,false);
	 }																				  
//	if(UtilValidate.isNotEmpty(facilityList)){
//	facility=EntityUtil.getFirst(facilityList);
//	context.facilityId=facility.facilityId;
//	}
   context.facilityListFlag="Yes";
 }else{
		 facilityList=delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"),null,null,null,false);
 }
 
 context.facilityList=facilityList;
 deptName=PartyHelper.getPartyName(delegator, partyIdFrom, false);
if(UtilValidate.isNotEmpty(partyIdFrom)){
	
	context.partyIdFrom=partyIdFrom;
}else{
	
	if(UtilValidate.isNotEmpty(resultMap.get("deptId"))){
		if((resultMap.get("deptId")).size()>1){
			partyIdFromList= resultMap.get("deptId");
			context.partyIdFromList= partyIdFromList;
		}else{
		partyIdFrom=(resultMap.get("deptId")).get(0);
		context.partyIdFrom=partyIdFrom;
		}
	}
	
}
context.deptName=deptName;
