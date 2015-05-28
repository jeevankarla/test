
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
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.product.inventory.InventoryWorker;

userLogin=parameters.userLogin;
partyIdTo=userLogin.partyId;
List employmentList=FastList.newInstance();
List facilityList=FastList.newInstance();
 partyRole=delegator.findOne("PartyRole",[partyId:partyIdTo,roleTypeId:"PRODUCTION_RUN"],false);
 if(UtilValidate.isNotEmpty(partyRole)){
	condition=EntityCondition.makeCondition([EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,partyIdTo),
		                                     EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null)],EntityOperator.AND);
	employmentList=delegator.findList("Employment",condition,null,null,null,false);
	employment=EntityUtil.getFirst(employmentList);
	partyIdFrom="";
	if(UtilValidate.isNotEmpty(employment)){
		partyIdFrom=employment.partyIdFrom;
	}
	
	facilityList=delegator.findList("Facility",EntityCondition.makeCondition([EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,partyIdFrom),
				                                                                      EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT")],EntityOperator.AND),null,null,null,false);
//	if(UtilValidate.isNotEmpty(facilityList)){
//	facility=EntityUtil.getFirst(facilityList);
//	context.facilityId=facility.facilityId;
//	}
   context.facilityListFlag="Yes";																				  
 }else{
		 facilityList=delegator.findList("Facility",EntityCondition.makeCondition("facilityTypeId",EntityOperator.EQUALS,"PLANT"),null,null,null,false);
 }
 
 context.facilityList=facilityList;
