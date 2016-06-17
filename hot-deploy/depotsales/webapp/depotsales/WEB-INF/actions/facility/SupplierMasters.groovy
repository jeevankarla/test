	
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
	
	
	geoList = delegator.findList("Geo", null ,UtilMisc.toSet("geoId","geoName"),null,null,false);
	geoNameMap = [:];
	for(geo in geoList){
		geoNameMap.put(geo.get("geoId"), geo.get("geoName"));
	}
	
	
	condList = [];
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.IN,UtilMisc.toList("SUPPLIER","UNEMPALED_SUPPLIER")));
	//fieldToSelect = UtilMisc.toSet("partyId", "groupName", "paAddress1", "paAddress2", "paPostalCode", "paCountryGeoId", "paStateProvinceGeoId", "tnContactNumber");
	List supplierPartyDetails = delegator.findList("PartyRoleAndContactMechDetail",EntityCondition.makeCondition(condList, EntityOperator.AND),null,null,null,false);
	
	condList = [];
	condList.add(EntityCondition.makeCondition("partyIdentificationTypeId" ,EntityOperator.IN, UtilMisc.toList("CST_NUMBER","PAN_NUMBER","TAN_NUMBER","TIN_NUMBER")));
	//fieldToSelect = UtilMisc.toSet("partyId", "groupName", "paAddress1", "paAddress2", "paPostalCode", "paCountryGeoId", "paStateProvinceGeoId", "tnContactNumber");
	List partyIdentificationList = delegator.findList("PartyIdentification",EntityCondition.makeCondition(condList, EntityOperator.AND),null,null,null,false);
	
	List supplierIds = EntityUtil.getFieldListFromEntityList(supplierPartyDetails, "partyId", true);
	
	supplierMastersList = [];
	for(int i=0; i<supplierIds.size(); i++){
		supplierId = supplierIds.get(i);
		suppList = EntityUtil.filterByCondition(supplierPartyDetails, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		supplierDetails = suppList.get(0);
		
		suppPostalAddress = null;
		suppPostalAddressList = EntityUtil.filterByCondition(suppList, EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
		if(UtilValidate.isNotEmpty(suppPostalAddressList)){
			suppPostalAddress = suppPostalAddressList.get(0);
		}
		
		
		suppIdentificationList = EntityUtil.filterByCondition(partyIdentificationList, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplierId));
		
		cstNoList = EntityUtil.filterByCondition(suppIdentificationList, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "CST_NUMBER"));
		tanNoList = EntityUtil.filterByCondition(suppIdentificationList, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TAN_NUMBER"));
		panNoList = EntityUtil.filterByCondition(suppIdentificationList, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "PAN_NUMBER"));
		tinNoList = EntityUtil.filterByCondition(suppIdentificationList, EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "TIN_NUMBER"));
		
		
		cstDetail = null;
		tinDetail = null;
		panDetail = null;
		tanDetail = null;
		if(UtilValidate.isNotEmpty(cstNoList)){
			cstDetail = cstNoList.get(0);
		}
		if(UtilValidate.isNotEmpty(tanNoList)){
			tanDetail = tanNoList.get(0);
		}
		if(UtilValidate.isNotEmpty(panNoList)){
			panDetail = panNoList.get(0);
		}
		if(UtilValidate.isNotEmpty(tinNoList)){
			tinDetail = tinNoList.get(0);
		}
		
		
		supplierMap = [:];
		supplierMap.put("partyId", supplierId);
		
		
		if(cstDetail && cstDetail.idValue){
			supplierMap.put("cstNo", cstDetail.idValue);
		}
		
		if(tanDetail && tanDetail.idValue){
			supplierMap.put("tanNo", tanDetail.idValue);
		}
		
		if(panDetail && panDetail.idValue){
			supplierMap.put("panNo", panDetail.idValue);
		}
		
		if(tinDetail && tinDetail.idValue){
			
			Debug.log("tinDetail.idValue============"+tinDetail.idValue);
			
			supplierMap.put("tinNo", tinDetail.idValue);
		}
		
		if(supplierDetails.groupName){
			supplierMap.put("name", supplierDetails.groupName);
		}
		
		if(suppPostalAddress && suppPostalAddress.paAddress1){
			supplierMap.put("paAddress1", suppPostalAddress.paAddress1);
		}
		
		if(suppPostalAddress && suppPostalAddress.paAddress2){
			supplierMap.put("paAddress2", suppPostalAddress.paAddress2);
		}
		
		if(suppPostalAddress && suppPostalAddress.paCity){
			supplierMap.put("city", suppPostalAddress.paCity);
		}
		
		if(suppPostalAddress && suppPostalAddress.paPostalCode){
			supplierMap.put("postCode", suppPostalAddress.paPostalCode);
		}
		
		if(supplierDetails.paStateProvinceGeoId){
			if(geoNameMap.get(supplierDetails.paStateProvinceGeoId)){
				supplierMap.put("state",geoNameMap.get(supplierDetails.paStateProvinceGeoId));
			}
		}
		
		if(supplierDetails.paCountryGeoId){
			if(geoNameMap.get(supplierDetails.paCountryGeoId)){
				supplierMap.put("country",geoNameMap.get(supplierDetails.paCountryGeoId));
			}
		}
		
		if(supplierDetails.tnContactNumber){
			supplierMap.put("contactNo", supplierDetails.tnContactNumber);
		}
		
	
		 conditionList=[];
		   conditionList.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,supplierId));
		   BankAccountList = delegator.findList("BankAccount", null ,null,null,null,false);
		   
		   
		   if(BankAccountList[0]){
			   if(BankAccountList[0].bankAccountName)
			    supplierMap.put("bankAccountName", BankAccountList[0].bankAccountName);
			   else
			   supplierMap.put("bankAccountName", "");
			   if(BankAccountList[0].bankAccountCode)
			    supplierMap.put("bankAccountCode", BankAccountList[0].bankAccountCode);
			    else
				supplierMap.put("bankAccountCode", "");
				if(BankAccountList[0].ifscCode)
				supplierMap.put("ifscCode", BankAccountList[0].ifscCode);
			    else
				supplierMap.put("ifscCode","");
				if(BankAccountList[0].branchCode)
				supplierMap.put("branchCode", BankAccountList[0].branchCode);
			     else
				 supplierMap.put("branchCode", "");
				
				if(BankAccountList[0].bankAccountName)
				supplierMap.put("bankAccountName", BankAccountList[0].bankAccountName);
				 else
				 supplierMap.put("bankAccountName","");
				
				
		   }
		   
		   
		
		
		
		
		tempSupMap = [:];
		tempSupMap.putAll(supplierMap);
		
		supplierMastersList.add(tempSupMap);
		
	}
	context.supplierMastersList=supplierMastersList;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	