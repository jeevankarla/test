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
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.network.NetworkServices;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	  parameters["customTimePeriodId"]= parameters.shedCustomTimePeriodId;
}
if(UtilValidate.isEmpty(parameters.customTimePeriodId)){
	Debug.logError("customTimePeriod Cannot Be Empty","");
	context.errorMessage = "No Shed Has Been Selected.......!";
	return;
}

customTimePeriod=delegator.findOne("CustomTimePeriod",[customTimePeriodId : parameters.customTimePeriodId], false);
fromDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate"));
thruDate=UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate"));

fromDateStart=UtilDateTime.getDayStart(UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")));
thruDateEnd=UtilDateTime.getDayEnd(UtilDateTime.toTimestamp(customTimePeriod.getDate("thruDate")));

context.put("fromDate", fromDate);
context.put("thruDate", thruDate);

String fromReqDate = UtilDateTime.toDateString(fromDate,"MMMdd")+"-";
String ThruReqDate = UtilDateTime.toDateString(thruDate,"MMMdd yyyy");

dctx = dispatcher.getDispatchContext();

facilityId = parameters.shedId;
context.put("facilityId",facilityId);

centerDetailsMap = [:];
centerWiseDetailsList = [];
centerWiseDetailsList = context.get("MPABSFoxproCsv");

Map unitWiseTotMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(context.get("summeryTotalsMap"))){
		unitWiseTotMap.putAll((Map)context.get("summeryTotalsMap"));
	}

String countryCode = "91"; 
List smsSentFacilityIdsList = FastList.newInstance();
if(UtilValidate.isNotEmpty(centerWiseDetailsList)){
	for(centerDetails in centerWiseDetailsList){
		String centerId = centerDetails.get("centerId");
		if(UtilValidate.isNotEmpty(smsSentFacilityIdsList) &&smsSentFacilityIdsList.contains(centerId)){
			continue;
			}else{
				
				BigDecimal netAmt = (BigDecimal)centerDetails.get("netRndAmount");
				if(UtilValidate.isNotEmpty(netAmt) && netAmt.compareTo(BigDecimal.ZERO) >0){
				
					BigDecimal bmQtyLtrs  = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(centerDetails.get("BMQtyLtrs"))){
						bmQtyLtrs = (BigDecimal)centerDetails.get("BMQtyLtrs");
					}
					BigDecimal cmQtyLtrs  = BigDecimal.ZERO;
					if(UtilValidate.isNotEmpty(centerDetails.get("CMQtyLtrs"))){
						cmQtyLtrs = (BigDecimal)centerDetails.get("CMQtyLtrs");
					}
					if(UtilValidate.isNotEmpty(centerDetails.get("BMsQtyLtrs"))){
						bmQtyLtrs  = (bmQtyLtrs).add((BigDecimal)centerDetails.get("BMsQtyLtrs")) ;
					}
					if(UtilValidate.isNotEmpty(centerDetails.get("CMsQtyLtrs"))){
						cmQtyLtrs  = (cmQtyLtrs).add((BigDecimal)centerDetails.get("BMsQtyLtrs")) ;
					}
					GenericValue  facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",centerId),false);
					String facilityName =(String) facilityDetails.get("facilityName");
					String facilityCode =(String) facilityDetails.get("facilityCode");
					
					String BmQtyLtrsStr = bmQtyLtrs.toString();
					String CmQtyLtrsStr = cmQtyLtrs.toString();
					
					if(BmQtyLtrsStr.indexOf('.') > 0){
						BmQtyLtrsStr = BmQtyLtrsStr.substring(0,BmQtyLtrsStr.indexOf('.')+1).concat(BmQtyLtrsStr.substring(BmQtyLtrsStr.indexOf('.')+1,BmQtyLtrsStr.indexOf('.')+3));
					}
					if(CmQtyLtrsStr.indexOf('.') > 0){
						CmQtyLtrsStr = CmQtyLtrsStr.substring(0,CmQtyLtrsStr.indexOf('.')+1).concat(CmQtyLtrsStr.substring(CmQtyLtrsStr.indexOf('.')+1,CmQtyLtrsStr.indexOf('.')+3));
					}
					
					String textMsg = "";
					textMsg= facilityName+"("+facilityCode+")Milk Proc Bill for"+"("+fromReqDate+ThruReqDate+")"+":"+"BMQtyLtrs:"+BmQtyLtrsStr+" CMQtyLtrs:"+CmQtyLtrsStr+" TotAmt:" +netAmt;
					Map<String, Object> getTelParams = FastMap.newInstance();
					getTelParams.put("partyId", facilityDetails.get("ownerPartyId"));
					getTelParams.put("userLogin", userLogin);
					Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
					if(ServiceUtil.isSuccess(serviceResult)){
						String contactNumber = "";	
						if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
							countryCode = (String)serviceResult.get("countryCode");
						}
						contactNumber = countryCode+(String) serviceResult.get("contactNumber");
						if(UtilValidate.isNotEmpty(contactNumber)){
							Map<String, Object> sendUserSmsParams = FastMap.newInstance();
							String contactNumberTo = countryCode.concat(contactNumber);
							sendUserSmsParams.put("contactNumberTo", contactNumberTo);
							sendUserSmsParams.put("text", textMsg);
							dispatcher.runAsync("sendSms", sendUserSmsParams);
						}
					}
				}
				smsSentFacilityIdsList.add(centerId);
			}
	}
}
smsSentFacilityIdsList.clear();
Map unitWiseDetailsMap = FastMap.newInstance();
if(UtilValidate.isNotEmpty(context.getAt("UnitWiseDetailsMap"))){
		unitWiseDetailsMap.putAll(context.getAt("UnitWiseDetailsMap"));
	}
if(UtilValidate.isNotEmpty(unitWiseTotMap)){
	for(unitId in unitWiseTotMap.keySet()){
		Map unitDetails = FastMap.newInstance();
		unitDetails.putAll((Map)unitWiseTotMap.get(unitId));
		Map unitPaymentDetails = FastMap.newInstance();
		
		if(UtilValidate.isNotEmpty(unitWiseDetailsMap.get(unitId))){
			unitPaymentDetails.putAll(unitWiseDetailsMap.get(unitId));
			}
		BigDecimal netAmt = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(unitPaymentDetails) && UtilValidate.isNotEmpty(unitPaymentDetails.get("netAmtPayable"))){
			 netAmt = (BigDecimal)unitPaymentDetails.get("netAmtPayable");
			}
		if(UtilValidate.isNotEmpty(netAmt) && netAmt.compareTo(BigDecimal.ZERO) >0){
			BigDecimal bmQtyLtrs  = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(unitDetails.get("BMQtyLtrs"))){
				bmQtyLtrs = (BigDecimal)unitDetails.get("BMQtyLtrs");
			}
			BigDecimal cmQtyLtrs  = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(unitDetails.get("CMQtyLtrs"))){
				cmQtyLtrs = (BigDecimal)unitDetails.get("CMQtyLtrs");
			}
			if(UtilValidate.isNotEmpty(unitDetails.get("BMsQtyLtrs"))){
				bmQtyLtrs  = (bmQtyLtrs).add((BigDecimal)unitDetails.get("BMsQtyLtrs")) ;
			}
			if(UtilValidate.isNotEmpty(unitDetails.get("CMsQtyLtrs"))){
				cmQtyLtrs  = (cmQtyLtrs).add((BigDecimal)unitDetails.get("BMsQtyLtrs")) ;
			}
			GenericValue  facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",unitId),false);
			String facilityName =(String) facilityDetails.get("facilityName");
			String facilityCode =(String) facilityDetails.get("facilityCode");
			
			String BmQtyLtrsStr = bmQtyLtrs.toString();
			String CmQtyLtrsStr = cmQtyLtrs.toString();
			
			if(BmQtyLtrsStr.indexOf('.') > 0){
				BmQtyLtrsStr = BmQtyLtrsStr.substring(0,BmQtyLtrsStr.indexOf('.')+1).concat(BmQtyLtrsStr.substring(BmQtyLtrsStr.indexOf('.')+1,BmQtyLtrsStr.indexOf('.')+3));
			}
			if(CmQtyLtrsStr.indexOf('.') > 0){
				CmQtyLtrsStr = CmQtyLtrsStr.substring(0,CmQtyLtrsStr.indexOf('.')+1).concat(CmQtyLtrsStr.substring(CmQtyLtrsStr.indexOf('.')+1,CmQtyLtrsStr.indexOf('.')+3));
			}
			
			String textMsg = "";
			textMsg= facilityName+":Milk Proc Bill for"+"("+fromReqDate+ThruReqDate+")"+":"+"BMQtyLtrs:"+BmQtyLtrsStr+" CMQtyLtrs:"+CmQtyLtrsStr+" TotAmt:" +netAmt;
			Map<String, Object> getTelParams = FastMap.newInstance();
			getTelParams.put("partyId", facilityDetails.get("ownerPartyId"));
			getTelParams.put("userLogin", userLogin);
			Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
			if(ServiceUtil.isSuccess(serviceResult)){
				String contactNumber = "";	
				if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
					countryCode = (String)serviceResult.get("countryCode");
				}
				contactNumber = (String) serviceResult.get("contactNumber");
				if(UtilValidate.isNotEmpty(contactNumber)){
					Map<String, Object> sendUserSmsParams = FastMap.newInstance();
					String contactNumberTo = countryCode.concat(contactNumber);
					sendUserSmsParams.put("contactNumberTo", contactNumberTo);
					sendUserSmsParams.put("text", textMsg);
					dispatcher.runAsync("sendSms", sendUserSmsParams);
				}
			}
		}
		
	}	
}
BigDecimal bmQtyLtrs = BigDecimal.ZERO;
BigDecimal cmQtyLtrs = BigDecimal.ZERO;
BigDecimal BMAmount = BigDecimal.ZERO;
BigDecimal CMAmount = BigDecimal.ZERO;
BigDecimal BMTipAmount = BigDecimal.ZERO;
BigDecimal CMTipAmount = BigDecimal.ZERO;
BigDecimal bmPercentage = BigDecimal.ZERO;
BigDecimal cmPercentage = BigDecimal.ZERO;

if(UtilValidate.isNotEmpty(context.getAt("shedTotLtrsMap"))){
	shedTotLtrsMap = context.get("shedTotLtrsMap");
	bmQtyLtrs = shedTotLtrsMap.get("Buffalo Milk");
	cmQtyLtrs = shedTotLtrsMap.get("Cow Milk");
	totQtyLtrs =  bmQtyLtrs+cmQtyLtrs;
	if((totQtyLtrs)!= 0){
		bmPerc = ((bmQtyLtrs/totQtyLtrs)*100);
		cmPerc = ((cmQtyLtrs/totQtyLtrs)*100);
		bmPercentage =(bmPerc.setScale(0,BigDecimal.ROUND_HALF_UP));
		cmPercentage = (cmPerc.setScale(0,BigDecimal.ROUND_HALF_UP));
	}
	
	productsAmountsMap =context.get("totAmountsMap");
	if((productsAmountsMap.get("BM"))!= 0){
		BMAmount = productsAmountsMap.get("BM");
	}
	if((productsAmountsMap.get("CM"))!= 0){
		CMAmount = productsAmountsMap.get("CM");
	}
	if((productsAmountsMap.get("BMTipAmt"))!= 0){
		BMTipAmount = productsAmountsMap.get("BMTipAmt");
	}
	if((productsAmountsMap.get("CMTipAmt"))!= 0){
		CMTipAmount = productsAmountsMap.get("CMTipAmt");
	}
	
	totBmTipAmt = BMAmount+BMTipAmount;
	totCmTipAmt = CMAmount+CMTipAmount;
	BigDecimal BMAvgRate = BigDecimal.ZERO;
	BigDecimal CMAvgRate = BigDecimal.ZERO;
	
	if(UtilValidate.isNotEmpty(bmQtyLtrs) && ((bmQtyLtrs)!= 0)){
		BMAvgRate = (totBmTipAmt/bmQtyLtrs).setScale(2,BigDecimal.ROUND_HALF_UP);
	}
	if(UtilValidate.isNotEmpty(cmQtyLtrs) && ((cmQtyLtrs)!= 0)){
		CMAvgRate = (totCmTipAmt/cmQtyLtrs).setScale(2,BigDecimal.ROUND_HALF_UP);
	}
	String BmQtyLtrs = bmQtyLtrs;
	String CmQtyLtrs = cmQtyLtrs;
	
	BMQtyLtrs = BmQtyLtrs.substring(0,BmQtyLtrs.indexOf('.')+1).concat(BmQtyLtrs.substring(BmQtyLtrs.indexOf('.')+1,BmQtyLtrs.indexOf('.')+3));
	CMQtyLtrs = CmQtyLtrs.substring(0,CmQtyLtrs.indexOf('.')+1).concat(CmQtyLtrs.substring(CmQtyLtrs.indexOf('.')+1,CmQtyLtrs.indexOf('.')+3));
	
	
	List conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
	conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DD_ROLE"));
	//conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
	EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	List ddAccFacilityPartyList = delegator.findList("FacilityParty",condition,null,null,null,false);
	countryCode = "91";
	if(UtilValidate.isNotEmpty(ddAccFacilityPartyList)){
		List<GenericValue>	activeDDsList = EntityUtil.filterByDate(ddAccFacilityPartyList,fromDate);
		if(UtilValidate.isNotEmpty(activeDDsList)){
			//sending sms to DD
			BigDecimal sanctionedAmt = BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(context.get("sanctionedAmt")) ){
				sanctionedAmt = context.get("sanctionedAmt");
				}
			String sanctionedAmtStr = (sanctionedAmt.toString()).substring(0, (sanctionedAmt.toString()).indexOf("."));
			
			DDMsg=facilityId+" Milk Proc Bill for("+fromReqDate+ThruReqDate+")"+":"+"BMQtyLtrs:"+BMQtyLtrs+"("+bmPercentage+"%)"+",CMQtyLtrs:"+CMQtyLtrs+"("+cmPercentage+"%)"+",BMRate:"+BMAvgRate+"CMRate:" + CMAvgRate+",Sanc. Amt:"+sanctionedAmtStr;
			
			// here we are sending sms to group
			Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "PROC_NOTIFY_LST",
				"text", DDMsg, "userLogin", userLogin);
			dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
			// here we are sending sms to DD
			GenericValue activeDD = EntityUtil.getFirst(activeDDsList);
			String partyId = (String)activeDD.get("partyId");
			Map<String, Object> getTelParams = FastMap.newInstance();
			getTelParams.put("partyId", partyId);
			getTelParams.put("userLogin", userLogin);
			Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
			Map userServiceResult = FastMap.newInstance();
			if(ServiceUtil.isSuccess(serviceResult)){
					String contactNumber = "";	
						if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
							countryCode = (String)serviceResult.get("countryCode");
						}
						contactNumber = countryCode+(String) serviceResult.get("contactNumber");
						if(UtilValidate.isNotEmpty(contactNumber)){
							Map<String, Object> sendUserSmsParams = FastMap.newInstance();
							String contactNumberTo = countryCode.concat(contactNumber);
							sendUserSmsParams.put("contactNumberTo", contactNumberTo);
							sendUserSmsParams.put("text", DDMsg+" Msg sent by MIS");
							dispatcher.runAsync("sendSms", sendUserSmsParams);
						}
				}
			
		}
	}
}	