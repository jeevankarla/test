import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.lang.*;
import java.math.BigDecimal;
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
import java.math.RoundingMode;
import java.util.Map;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilDateTime;
import in.vasista.vbiz.procurement.ProcurementReports;
import in.vasista.vbiz.procurement.ProcurementNetworkServices;
import in.vasista.vbiz.procurement.ProcurementServices;
import in.vasista.vbiz.procurement.PriceServices;
import in.vasista.vbiz.milkReceipts.MilkReceiptReports;

fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
def sdf = new SimpleDateFormat("MMMM dd, yyyy");
try {
	   if (parameters.fromDate) {
			   fromDate = UtilDateTime.getDayStart(new java.sql.Timestamp(sdf.parse(parameters.fromDate).getTime()));
	   }
	  
} catch (ParseException e) {
	   context.errorMessage = "Cannot parse date string: " + e;
	   return;
}
context.put("fromDate", fromDate);
dctx = dispatcher.getDispatchContext();


List andhraRegionShedList  = FastList.newInstance();


BigDecimal andhraQtyLtrs = BigDecimal.ZERO;
BigDecimal andhraQtyKgs = BigDecimal.ZERO;
BigDecimal andhraKgFat = BigDecimal.ZERO;
BigDecimal andhraKgSnf = BigDecimal.ZERO;

BigDecimal telanganaQtyLtrs = BigDecimal.ZERO;
BigDecimal telanganaQtyKgs = BigDecimal.ZERO;
BigDecimal telanganaKgFat = BigDecimal.ZERO;
BigDecimal telanganaKgSnf = BigDecimal.ZERO;

BigDecimal totLtrs = BigDecimal.ZERO;
BigDecimal totKgs = BigDecimal.ZERO;
BigDecimal totKgFat = BigDecimal.ZERO;
BigDecimal totKgSnf = BigDecimal.ZERO;

List allRegionShedsList = delegator.findList("FacilityGroupMember",EntityCondition.makeCondition("facilityGroupId",EntityOperator.IN,UtilMisc.toList("ANDHRA","TELANGANA")), null, null, null, false );
andhraShedList = EntityUtil.filterByAnd(allRegionShedsList, [facilityGroupId : 'ANDHRA']);
telanganaShedList = EntityUtil.filterByAnd(allRegionShedsList, [facilityGroupId : 'TELANGANA']);
List andhraShedIdsList = EntityUtil.getFieldListFromEntityList(andhraShedList,"facilityId", false);
List telanganaSheIdsList= EntityUtil.getFieldListFromEntityList(telanganaShedList,"facilityId", false);

Map mpfMilkReceiptsMap = FastMap.newInstance();
mpfMilkReceiptsMap=MilkReceiptReports.getAllMilkReceipts(dctx,[fromDate:fromDate,thruDate:UtilDateTime.getDayEnd(fromDate)] );
String countryCode = "91";
String andhraShedMsg="Milk Receipts,"+UtilDateTime.toDateString(fromDate,"dd/MM/yy")+":";
String notifyAllShedsMsg="";
String endingText = " MIS,APDDCF LTD";
if(UtilValidate.isNotEmpty(mpfMilkReceiptsMap)){
	Map milkReceipts=mpfMilkReceiptsMap.get("milkReceiptsMap");
	if(UtilValidate.isNotEmpty(milkReceipts)){
			for(mccKey in milkReceipts.keySet()){
				if(mccKey != "dayTotals"){
					Map mccTotalsMap = FastMap.newInstance();
					mccTotalsMap.putAll(milkReceipts.get(mccKey));
					if(UtilValidate.isNotEmpty(mccTotalsMap)){
					for(shedKey in mccTotalsMap.keySet()){
						if(shedKey!= "dayTotals"){
							String DDMsg="";
							Map shedTotalsMap = FastMap.newInstance();	
							shedTotalsMap.putAll(mccTotalsMap.get(shedKey));
							if(UtilValidate.isNotEmpty(shedTotalsMap)){
								for(unitKey in shedTotalsMap.keySet()){
									Map unitTotalsMap = FastMap.newInstance();
									unitTotalsMap.putAll(shedTotalsMap.get(unitKey));
									Map dayTotalsMap = FastMap.newInstance();
									if(UtilValidate.isNotEmpty(unitTotalsMap)){
										String text = UtilDateTime.toDateString(fromDate,"dd/MM/yy")+":";
										String contactNumberTo="";
										if(unitKey == "dayTotals"){
											dayTotalsMap.putAll(unitTotalsMap.getAt("TOT").get("TOT"));
											GenericValue  facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",shedKey),false);
											String qtyLtrs = dayTotalsMap.get("recdQtyLtrs");
											String qtyKgs = dayTotalsMap.get("recdQtyKgs");
											qtyLtrs = qtyLtrs.substring(0,qtyLtrs.indexOf('.')+1).concat(qtyLtrs.substring(qtyLtrs.indexOf('.')+1,qtyLtrs.indexOf('.')+3));
											qtyKgs = qtyKgs.substring(0,qtyKgs.indexOf('.')+1).concat(qtyKgs.substring(qtyKgs.indexOf('.')+1,qtyKgs.indexOf('.')+3));
											
											
											text = text+","+facilityDetails.get("facilityName")+" : Recived QtyLtrs :"+qtyLtrs+" ,QtyKgs :"+qtyKgs+",fat :"+dayTotalsMap.get("receivedFat")+",snf:"+dayTotalsMap.get("receivedSnf")+","+endingText;
											
											//getting ddDetails 
											
											List conditionList = FastList.newInstance();
											conditionList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityDetails.get("facilityId")));
											conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"DD_ROLE"));
											//conditionList.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
											EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
											List ddAccFacilityPartyList = delegator.findList("FacilityParty",condition,null,null,null,false);
											
											if(UtilValidate.isNotEmpty(ddAccFacilityPartyList)){
												
												List<GenericValue>	activeDDsList = EntityUtil.filterByDate(ddAccFacilityPartyList,fromDate);
												
												if(UtilValidate.isNotEmpty(activeDDsList)){
													//sending sms to DD
													DDMsg=DDMsg+facilityDetails.get("facilityId")+" : Recived QtyLtrs :"+qtyLtrs+" ,QtyKgs :"+qtyKgs+",fat :"+dayTotalsMap.get("receivedFat")+",snf:"+dayTotalsMap.get("receivedSnf");
													GenericValue activeDD = EntityUtil.getFirst(activeDDsList);
													String partyId = (String)activeDD.get("partyId");
													
													Map<String, Object> getTelParams = FastMap.newInstance();
													getTelParams.put("partyId", partyId);
													getTelParams.put("userLogin", userLogin);
													Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
													Map userServiceResult = FastMap.newInstance();
													if(ServiceUtil.isSuccess(serviceResult)){
															if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
																countryCode = (String)serviceResult.get("countryCode");
															}
															contactNumberTo = countryCode+(String) serviceResult.get("contactNumber");
															Map<String, Object> sendUserSmsParams = FastMap.newInstance();
															sendUserSmsParams.put("contactNumberTo", contactNumberTo);
															sendUserSmsParams.put("text", DDMsg);
															if(UtilValidate.isNotEmpty(contactNumberTo)){
																userServiceResult  = dispatcher.runSync("sendSms", sendUserSmsParams);
															}
														}
												}
											}
											 totLtrs = totLtrs.add(dayTotalsMap.get("recdQtyLtrs"));
											 totKgs = totKgs.add(dayTotalsMap.get("recdQtyKgs"));
											 totKgFat = totKgFat.add(dayTotalsMap.get("recdKgFat"));
											 totKgSnf = totKgSnf.add(dayTotalsMap.get("recdKgSnf"));
											 
											if(andhraShedIdsList.contains(shedKey)){
												andhraQtyLtrs=andhraQtyLtrs.add(dayTotalsMap.get("recdQtyLtrs"));
												andhraQtyKgs=andhraQtyKgs.add(dayTotalsMap.get("recdQtyKgs"));
												andhraKgFat =andhraKgFat.add(dayTotalsMap.get("recdKgFat"));
												andhraKgSnf = andhraKgSnf.add(dayTotalsMap.get("recdKgSnf"));
											}else if(telanganaSheIdsList.contains(shedKey)){
												telanganaQtyLtrs=telanganaQtyLtrs.add(dayTotalsMap.get("recdQtyLtrs"));
												telanganaQtyKgs=telanganaQtyKgs.add(dayTotalsMap.get("recdQtyKgs"));
												telanganaKgFat =telanganaKgFat.add(dayTotalsMap.get("recdKgFat"));
												telanganaKgSnf = telanganaKgSnf.add(dayTotalsMap.get("recdKgSnf"));
											}else{											
												notifyAllShedsMsg=notifyAllShedsMsg+","+facilityDetails.get("facilityId")+" : Recived QtyLtrs :"+qtyLtrs+" ,QtyKgs :"+qtyKgs+",fat :"+dayTotalsMap.get("receivedFat")+",snf:"+dayTotalsMap.get("receivedSnf");
											}
												
										}else{
											dayTotalsMap.putAll(unitTotalsMap.get("dayTotals").getAt("TOT").get("TOT"));
											String qtyLtrs = dayTotalsMap.get("recdQtyLtrs");
											String qtyKgs = dayTotalsMap.get("recdQtyKgs");
											
											qtyLtrs = qtyLtrs.substring(0,qtyLtrs.indexOf('.')+1).concat(qtyLtrs.substring(qtyLtrs.indexOf('.')+1,qtyLtrs.indexOf('.')+3));
											qtyKgs = qtyKgs.substring(0,qtyKgs.indexOf('.')+1).concat(qtyKgs.substring(qtyKgs.indexOf('.')+1,qtyKgs.indexOf('.')+3));
											
											GenericValue  facilityDetails = delegator.findOne("Facility",UtilMisc.toMap("facilityId",unitKey),false); 	
											text =text+","+ facilityDetails.get("facilityName")+" : Recived QtyLtrs :"+qtyLtrs+",fat :"+dayTotalsMap.get("receivedFat")+"%"+",snf:"+dayTotalsMap.get("receivedSnf")+"%"+","+endingText;
											DDMsg=DDMsg+facilityDetails.get("facilityName")+" : Recived QtyLtrs :"+qtyLtrs+",fat :"+dayTotalsMap.get("receivedFat")+"%"+",snf:"+dayTotalsMap.get("receivedSnf")+"%"+",";
											//sending sms to perticulerUnit
											Map<String, Object> getTelParams = FastMap.newInstance();
											getTelParams.put("partyId", facilityDetails.get("ownerPartyId"));
											getTelParams.put("userLogin", userLogin);
											Map serviceResult = dispatcher.runSync("getPartyTelephone", getTelParams);
											Map userServiceResult = FastMap.newInstance();
											if(ServiceUtil.isSuccess(serviceResult)){
													if(UtilValidate.isNotEmpty(serviceResult.get("countryCode"))){
														countryCode = (String)serviceResult.get("countryCode");
													}
													contactNumberTo = countryCode+(String) serviceResult.get("contactNumber");
													Map<String, Object> sendUserSmsParams = FastMap.newInstance();
													sendUserSmsParams.put("contactNumberTo", contactNumberTo);
													sendUserSmsParams.put("text", text);
													
													if(UtilValidate.isNotEmpty(contactNumberTo)){
														userServiceResult  = dispatcher.runSync("sendSms", sendUserSmsParams);
													}
												}
										}
									}
								}			
							}
						}
					}
				}	
			}
		}
	}
}
andhraFat=0;
andhraSnf=0;
if(andhraQtyKgs !=0){	
	 andhraFat = (ProcurementNetworkServices.calculateFatOrSnf(andhraKgFat, andhraQtyKgs)).setScale(1, BigDecimal.ROUND_HALF_UP);
	 andhraSnf = (ProcurementNetworkServices.calculateFatOrSnf(andhraKgSnf, andhraQtyKgs)).setScale(2, BigDecimal.ROUND_HALF_UP);
	
}
telanganaFat=0;
telanganaSnf=0;
if(telanganaQtyKgs !=0){
	telanganaFat = (ProcurementNetworkServices.calculateFatOrSnf(telanganaKgFat, telanganaQtyKgs)).setScale(1, BigDecimal.ROUND_HALF_UP);
	telanganaSnf = (ProcurementNetworkServices.calculateFatOrSnf(telanganaKgSnf, telanganaQtyKgs)).setScale(2, BigDecimal.ROUND_HALF_UP);
}

totFat=0;
totSnf=0;
if(totKgs !=0){
	totFat = (ProcurementNetworkServices.calculateFatOrSnf(totKgFat, totKgs)).setScale(1, BigDecimal.ROUND_HALF_UP);
	totSnf = (ProcurementNetworkServices.calculateFatOrSnf(totKgSnf, totKgs)).setScale(2, BigDecimal.ROUND_HALF_UP);
}
if(UtilValidate.isNotEmpty(andhraQtyLtrs)){
	andhraQtyLtrs=andhraQtyLtrs/100000;
	andhraQtyLtrs=andhraQtyLtrs.setScale(1,BigDecimal.ROUND_HALF_EVEN);
}
if(UtilValidate.isNotEmpty(telanganaQtyLtrs)){
	telanganaQtyLtrs=telanganaQtyLtrs/100000;
	telanganaQtyLtrs=telanganaQtyLtrs.setScale(1,BigDecimal.ROUND_HALF_EVEN);
}
if(UtilValidate.isNotEmpty(totLtrs)){
	totLtrs=totLtrs/100000;
	totLtrs=totLtrs.setScale(1,BigDecimal.ROUND_HALF_EVEN);
}
andhraShedMsg=andhraShedMsg+"ANDHRA"+" : Recived QtyLtrs :"+andhraQtyLtrs+"(LLPD)"+",fat :"+andhraFat+"%"+",snf:"+andhraSnf+"%";
telanganaMsg="TELANGANA"+" : Recived QtyLtrs:"+telanganaQtyLtrs+"(LLPD)"+",fat :"+telanganaFat+"%"+",snf:"+telanganaSnf+"%";
if(UtilValidate.isNotEmpty(andhraShedMsg) && (andhraQtyLtrs !=0)){
	Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "ANDHRA_NOTIFY_LST",
		"text", (andhraShedMsg+endingText), "userLogin", userLogin);
	dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
}
allNotifyMsg="";
if(andhraQtyLtrs !=0){
	allNotifyMsg=allNotifyMsg+andhraShedMsg;
}
if(telanganaQtyLtrs !=0){
	allNotifyMsg=allNotifyMsg+","+telanganaMsg;
}
if(UtilValidate.isNotEmpty(notifyAllShedsMsg)){
	allNotifyMsg=allNotifyMsg+","+notifyAllShedsMsg;	
}
if(totLtrs !=0){
	allNotifyMsg=allNotifyMsg+","+"TOTAL"+" : Recived QtyLtrs :"+totLtrs+"(LLPD)"+",fat :"+totFat+"%"+",snf:"+totSnf+"%";
}
if(UtilValidate.isNotEmpty(allNotifyMsg)){
	Map<String,  Object> sendSmsContext = UtilMisc.<String, Object>toMap("contactListId", "RECPT_NOTIFY_LST",
		"text", (allNotifyMsg+endingText), "userLogin", userLogin);
	dispatcher.runAsync("sendSmsToContactListNoCommEvent", sendSmsContext);
}

