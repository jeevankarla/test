import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import java.text.ParseException;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.base.util.UtilMisc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import javax.swing.text.html.parser.Entity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
dctx = dispatcher.getDispatchContext();
userLogin= context.userLogin;
if (UtilValidate.isEmpty(parameters.fixedDepositDate)) {
		effectiveDate = UtilDateTime.nowTimestamp();
}
else{
	def sdf = new SimpleDateFormat("MMMM dd, yyyy");
	try {
		effectiveDate = new java.sql.Timestamp(sdf.parse(parameters.fixedDepositDate+" 00:00:00").getTime());
	} catch (ParseException e) {
		Debug.logError(e, "Cannot parse date string: " + parameters.fixedDepositDate, "");
	}
}
effectiveDate = UtilDateTime.getDayStart(effectiveDate);
printDate = UtilDateTime.toDateString(UtilDateTime.nowTimestamp(), "dd/MM/yyyy");
context.printDate = printDate;
if(parameters.categoryType != "ALL"){
	facilityList = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,parameters.categoryType,effectiveDate)).get("boothActiveList");
}else{
    facilityList = (List)((Map)ByProductNetworkServices.getAllActiveOrInactiveBooths(delegator,null,effectiveDate)).get("boothActiveList");
}
Map FDRDetail = ByProductNetworkServices.getFacilityFixedDeposit( dctx , [userLogin: userLogin, effectiveDate: effectiveDate]).get("FacilityFDRDetail");

facilityFixedDepositMap = [:];

Map SCTMap=FastMap.newInstance();

if(UtilValidate.isNotEmpty(facilityList)){
	for(i=0;i<facilityList.size();i++){
		categoryType=facilityList.get(i).get("categoryTypeEnum");
		Map facilityFDRDetail=[:];
		facilityId=facilityList.get(i).get("facilityId");
        if(!categoryType.equals("CR_INST")){
			if(UtilValidate.isEmpty(facilityFixedDepositMap[categoryType])){
				facDetailsMap=[:];
				facDetailsMap["securityDeposit"]=0;
				Map finalMap=FastMap.newInstance();
				facDetailsMap.putAt("name",facilityList.get(i).get("facilityName"));
				
				
				facDetailsMap.putAt("securityDeposit",facilityList.get(i).get("securityDeposit"));
				if(UtilValidate.isNotEmpty(FDRDetail)){
					facilityFDRDetail = (Map)FDRDetail.get(facilityId);
					if(UtilValidate.isNotEmpty(facilityFDRDetail)){
					fDRLst = facilityFDRDetail.get("FDRDetail");
					facDetailsMap.putAt("fDRLst",fDRLst);
					}
				}
				if(facilityList.get(i).get("securityDeposit")>0||UtilValidate.isNotEmpty(facilityFDRDetail)){
					
					tempMap = [:];
					tempMap.putAll(facDetailsMap);
					
					finalMap.put(facilityId,tempMap);
					
					tempFinalMap = [:];
					tempFinalMap.putAll(finalMap);
					
					facilityFixedDepositMap.put(categoryType,tempFinalMap);
				}
				
			 }else{
				 facDetailsMap = [:];
				 Map finalMap=FastMap.newInstance();
				 finalMap=facilityFixedDepositMap.get(categoryType);
				 facDetailsMap.putAt("name",facilityList.get(i).get("facilityName"));
                 facDetailsMap.putAt("securityDeposit",facilityList.get(i).get("securityDeposit"));
				 
                if(UtilValidate.isNotEmpty(FDRDetail)){
					facilityFDRDetail = (Map)FDRDetail.get(facilityId);
					if(UtilValidate.isNotEmpty(facilityFDRDetail)){
						fDRLst = facilityFDRDetail.get("FDRDetail");
						facDetailsMap.putAt("fDRLst",fDRLst);
					}
				}
				 
				tempMap = [:];
				tempMap.putAll(facDetailsMap);
				if(facilityList.get(i).get("securityDeposit")>0||UtilValidate.isNotEmpty(facilityFDRDetail)){
					finalMap.put(facilityId,tempMap);
					tempFinalMap = [:];
					tempFinalMap.putAll(finalMap);
					facilityFixedDepositMap.put(categoryType,tempFinalMap);
			    }
			 }
		 }
	}
}
context.facilityFixedDepositMap = facilityFixedDepositMap;
