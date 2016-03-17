	
	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.GenericEntityException;
	import org.ofbiz.entity.util.EntityUtil;
	import org.ofbiz.entity.*;
	import org.ofbiz.entity.condition.*;
	import org.ofbiz.base.util.UtilMisc;
	import org.ofbiz.entity.condition.EntityCondition;
	import org.ofbiz.entity.condition.EntityOperator;
	import org.ofbiz.entity.GenericValue;
	import org.ofbiz.party.party.PartyHelper;
	
	import java.sql.*;
	import java.util.Calendar;
	import javolution.util.FastList;
	import javolution.util.FastMap;
	import java.sql.Timestamp;
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import net.sf.json.JSONObject;
	import org.ofbiz.service.ServiceUtil;
	import org.ofbiz.entity.model.DynamicViewEntity
	import org.ofbiz.entity.model.ModelKeyMap;
	
	
	condList = [];
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_SUPPLIER"));
	
	//fieldToSelect = UtilMisc.toSet("partyId", "groupName", "paAddress1", "paAddress2", "paPostalCode", "paCountryGeoId", "paStateProvinceGeoId", "tnContactNumber");
	List supplierPartyDetails = delegator.findList("PartyRoleAndContactMechDetail",EntityCondition.makeCondition(condList, EntityOperator.AND),null,null,null,false);
	
	List supplierIds = EntityUtil.getFieldListFromEntityList(supplierPartyDetails, "partyId", true);
	
	supplierMastersList = [];
	for(int i=0; i<supplierIds.size(); i++){
		supplierId = supplierIds.get(i);
		suppList = EntityUtil.filterByCondition(supplierPartyDetails, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		supplierDetails = suppList.get(0);
		
		supplierMap = [:];
		supplierMap.put("partyId", supplierId);
		supplierMap.put("name", supplierDetails.groupName);
		supplierMap.put("paAddress1", supplierDetails.paAddress1);
		supplierMap.put("paAddress2", supplierDetails.paAddress2);
		supplierMap.put("postCode", supplierDetails.paPostalCode);
		supplierMap.put("state", supplierDetails.paStateProvinceGeoId);
		supplierMap.put("country", supplierDetails.paCountryGeoId);
		supplierMap.put("contactNo", supplierDetails.tnContactNumber);
		
		tempSupMap = [:];
		tempSupMap.putAll(supplierMap);
		
		supplierMastersList.add(tempSupMap);
		
	}
	Debug.log("supplierMastersList ============== "+supplierMastersList);
	context.supplierMastersList=supplierMastersList;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	