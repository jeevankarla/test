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



dctx = dispatcher.getDispatchContext();

effectiveDateStr = parameters.fromDate;
thruEffectiveDateStr = parameters.thruDate;

if (UtilValidate.isEmpty(effectiveDateStr)) {
	effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(effectiveDateStr+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + effectiveDate, "");
	}
}
if (UtilValidate.isEmpty(thruEffectiveDateStr)) {
	thruEffectiveDate = effectiveDate;
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		thruEffectiveDate = new java.sql.Timestamp(sdf.parse(thruEffectiveDateStr+" 00:00:00").getTime());
	}catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + thruEffectiveDate, "");
	}
}

dayBegin = UtilDateTime.getDayStart(effectiveDate);
dayEnd = UtilDateTime.getDayEnd(thruEffectiveDate);
context.put("dayBegin",dayBegin);
context.put("dayEnd",dayEnd);
totalDays=UtilDateTime.getIntervalInDays(dayBegin,dayEnd);
context.put("totalDays", totalDays+1);

unionCondList = [];
unionCondList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "Unions"));
unionCondList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
unionCondList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS , "EMPLOYMENT"));
unionCond = EntityCondition.makeCondition(unionCondList,EntityOperator.AND);
unionPartyRelationshipList = delegator.findList("PartyRelationship", unionCond, ["partyIdFrom"]as Set, null, null, false);

Set unionPartySet = new HashSet();
if(unionPartyRelationshipList){
	unionPartySet = new HashSet((List)EntityUtil.getFieldListFromEntityList(unionPartyRelationshipList,"partyIdFrom", true));
}

List unionPartiesList = FastList.newInstance();
unionPartiesList = unionPartySet.toList();

Map unionFacilityPartyMap = FastMap.newInstance();
routeIdsList = (ByProductNetworkServices.getRoutesByAMPM(dctx ,UtilMisc.toMap("supplyType" ,"AM"))).get("routeIdsList");
unionPartiesList.each { union ->
	routeWiseMap =[:];
	if(UtilValidate.isNotEmpty(routeIdsList)){
		routeIdsList.each{ routeId ->
			boothsList = ByProductNetworkServices.getRouteBooths(delegator , UtilMisc.toMap("routeId",routeId)).get("boothsList");
			boothsList = EntityUtil.filterByDate(boothsList, nowTimestamp, "openedDate", "closedDate", true);
			boothMap = [:];
			if(UtilValidate.isNotEmpty(boothsList)){
				boothsList.each{ booth ->
					unionList = [];
					conditionList=[];
					conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , booth.facilityId));
					conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS , "EMPSUBISDY_ROLE"));
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
					conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
							EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
					condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
					List<GenericValue> facilityPartyList = delegator.findList("FacilityParty", condition, null, null, null, false);
					if(UtilValidate.isNotEmpty(facilityPartyList)){
						facilityPartyList.each{ facilityParty ->
							facilityPartyId = facilityParty.partyId;
							if(UtilValidate.isNotEmpty(facilityPartyId)){
								condList=[];
								condList.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS , union));
								condList.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS , facilityPartyId));
								condList.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS , "Unions"));
								condList.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS , "EMPLOYEE"));
								condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS , "EMPLOYMENT"));
								condList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
								condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
										EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin)));
								cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
								List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship", cond, null, null, null, false);
								partyRelationship = EntityUtil.getFirst(EntityUtil.filterByDate(partyRelationshipList));
								if(UtilValidate.isNotEmpty(partyRelationship)){
									partyId = partyRelationship.partyIdTo;
									partyName = PartyHelper.getPartyName(delegator, partyRelationship.partyIdTo, false);
									unionMap = [:];
									unionMap["partyId"] = partyId;
									unionMap["partyName"] = partyName;
									unionList.addAll(unionMap);
								}
							}
						}
					}
					if(UtilValidate.isNotEmpty(unionList)){
						boothMap.put(booth.facilityId,unionList);
					}
				}
			}
			if(UtilValidate.isNotEmpty(boothMap)){
				routeWiseMap.put(routeId,boothMap);
			}
		}
	}
	if(UtilValidate.isNotEmpty(routeWiseMap)){
		unionFacilityPartyMap.put(union,routeWiseMap);
	}
}
context.put("unionFacilityPartyMap",unionFacilityPartyMap);

Map<String, Object> priceContext = FastMap.newInstance();
priceContext.put("userLogin", userLogin);
priceContext.put("productId", "15");
priceContext.put("facilityId", "S01");
priceContext.put("priceDate", dayBegin);

Map priceResult = ByProductServices.calculateByProductsPrice(delegator, dispatcher, priceContext);
BigDecimal defaultPrice = (BigDecimal)priceResult.get("mrpPrice");
BigDecimal costPerLitre = (defaultPrice*0.5);
context.put("costPerLitre",costPerLitre);
