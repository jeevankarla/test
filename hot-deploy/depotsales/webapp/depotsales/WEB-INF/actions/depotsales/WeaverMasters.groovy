	
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
	
	branchId = parameters.branchId;
	passbookNumber = parameters.passbookNumber;
	partyId = parameters.partyId;
	groupName = parameters.groupName;
	
	partyRoleAndIde = new DynamicViewEntity();
	partyRoleAndIde.addMemberEntity("PRPD", "PartyRoleDetailAndPartyDetail");
	partyRoleAndIde.addMemberEntity("PID", "PartyIdentification");
	partyRoleAndIde.addMemberEntity("PRS", "PartyRelationship");
	partyRoleAndIde.addMemberEntity("PC", "PartyClassification");
	//partyRoleAndIde.addMemberEntity("PPA", "PartyAndPostalAddress");
	
	
	partyRoleAndIde.addAlias("PRPD", "partyId", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "roleTypeId", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "firstName", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "middleName", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "lastName", null, null, null, Boolean.TRUE, null);
    
    partyRoleAndIde.addAlias("PRPD", "groupName", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "cstNumber", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "tinNumber", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRPD", "panId", null, null, null, Boolean.TRUE, null);
    
    
    partyRoleAndIde.addAlias("PID", "partyIdentificationTypeId", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PID", "idValue", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PID", "issueDate", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PID", "expiryDate", null, null, null, Boolean.TRUE, null);
   
    partyRoleAndIde.addAlias("PRS", "partyIdFrom", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRS", "partyIdTo", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRS", "fromDate", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRS", "roleTypeIdFrom", null, null, null, Boolean.TRUE, null);
    partyRoleAndIde.addAlias("PRS", "roleTypeIdTo", null, null, null, Boolean.TRUE, null);
   
    partyRoleAndIde.addAlias("PC", "partyClassificationGroupId", null, null, null, Boolean.TRUE, null);
    
    //partyRoleAndIde.addAlias("PPA", "address1", null, null, null, Boolean.TRUE, null);
    //partyRoleAndIde.addAlias("PPA", "address2", null, null, null, Boolean.TRUE, null);
    //partyRoleAndIde.addAlias("PPA", "city", null, null, null, Boolean.TRUE, null);
    //partyRoleAndIde.addAlias("PPA", "postalCode", null, null, null, Boolean.TRUE, null);
    //partyRoleAndIde.addAlias("PPA", "stateProvinceGeoId", null, null, null, Boolean.TRUE, null);
	
	
	partyRoleAndIde.addViewLink("PRPD","PID", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
	partyRoleAndIde.addViewLink("PRPD","PRS", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId","partyIdTo"));
	partyRoleAndIde.addViewLink("PRPD","PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
	//partyRoleAndIde.addViewLink("PRPD","PPA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
	
	List formatList = [];
	if(parameters.branchId){
		resultCtx = dispatcher.runSync("getRoBranchList",UtilMisc.toMap("userLogin",userLogin,"productStoreId",parameters.branchId));
		Map formatMap = [:];
		
		partyList=[];
		if(resultCtx && resultCtx.get("partyList")){
			partyList=resultCtx.get("partyList")
			partyIdToList= EntityUtil.getFieldListFromEntityList(partyList,"partyIdTo", true);			
			formatList=(List)partyIdToList;
		}
	}
		Debug.log("formatList======================="+formatList);
	condList = [];
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	if(formatList){
		
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.IN,formatList));
	}
	//condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, "62690"));
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	prodsEli = delegator.findListIteratorByCondition(partyRoleAndIde, cond, null,null,null,null);
	groupNameList = prodsEli.getCompleteList();
	
	//Debug.log("groupNameList ===== "+groupNameList);	
	partyIdsList =  EntityUtil.getFieldListFromEntityList(groupNameList, "partyId", true);
	

	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIdsList));
	partyLoomDetails = delegator.findList("PartyLoom",null,UtilMisc.toSet("partyId","loomTypeId","quantity"),null,null,false);
	//Debug.log("PartyLoomDetails = "+PartyLoomDetails);
	
	facilityCond=[];
	facilityCond.add(EntityCondition.makeCondition("ownerPartyId" ,EntityOperator.IN, partyIdsList));
	faccond = EntityCondition.makeCondition(facilityCond, EntityOperator.AND);
	DepotFacilityList = delegator.findList("Facility", null , UtilMisc.toSet("ownerPartyId","openedDate"), null, null, false );
	
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIdsList));
	conditionList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS,"POSTAL_ADDRESS"));
	postalAddress = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(conditionList, EntityOperator.AND),UtilMisc.toSet("partyId","address1","address2","city","postalCode", "stateProvinceGeoId", "districtGeoId", "countryGeoId"),null,null,false);
	
	//conditionList=[];
	//conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIdsList));
	//conditionList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS,"POSTAL_ADDRESS"));
	productStoreList = delegator.findList("ProductStore", null,UtilMisc.toSet("productStoreId","payToPartyId","storeName"),null,null,false);

	geoList = delegator.findList("Geo", null ,UtilMisc.toSet("geoId","geoName"),null,null,false);
	geoNameMap = [:];
	for(geo in geoList){
		geoNameMap.put(geo.get("geoId"), geo.get("geoName"));
	}
	
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("schemeId", EntityOperator.EQUALS,"TEN_PERCENT_MGPS"));
	schemeCategories = delegator.findList("SchemeProductCategory", null ,UtilMisc.toSet("productCategoryId","maxQty"),null,null,false);
	schemeQtyMap = [:];
	for(category in schemeCategories){
		schemeQtyMap.put(category.get("productCategoryId"), category.get("maxQty"));
	}
	
	finalList =[];
	i=0;
	for(customer in groupNameList){
	
		i = i + 1;
		Debug.log("count ================================="+i);
		eachPartyId=String.valueOf(customer.get("partyId"));
		branchId=String.valueOf(customer.get("partyIdFrom"));
		
		partyName = "";
		
		if(customer.get("firstName")){
			partyName = partyName + customer.get("firstName") + " ";
		}
		if(customer.get("middleName")){
			partyName = partyName + customer.get("middleName") + " ";
		}
		if(customer.get("lastName")){
			partyName = partyName + customer.get("lastName");
		}
		if(customer.get("groupName")){
			partyName = partyName + customer.get("groupName");
		}
		
		
		tempMap = [:];
		tempMap.put("partyId",eachPartyId);
		//String partyName = PartyHelper.getPartyName(delegator,eachPartyId,false);
		tempMap.put("groupName",partyName);
		tempMap.put("passbookId",customer["idValue"]);
		tempMap.put("partyClassificationGroupId",customer["partyClassificationGroupId"]);
		tempMap.put("issueDate",String.valueOf(customer["issueDate"]));
		tempMap.put("tinNumber",customer["tinNumber"]);
		tempMap.put("panId",customer["panId"]);
		
		facilityDepot = EntityUtil.getFirst(EntityUtil.filterByCondition(DepotFacilityList, EntityCondition.makeCondition("ownerPartyId",EntityOperator.EQUALS,eachPartyId)));
      		//Debug.log("facilityDepot ================================="+facilityDepot );

        String Depot="NO";
       	String DAO="";
       	if(UtilValidate.isNotEmpty(facilityDepot)){
            Depot="YES";
           	if(UtilValidate.isNotEmpty(facilityDepot.openedDate)){
          		DAO=UtilDateTime.toDateString(facilityDepot.openedDate,"dd-MM-yyyy");
          	}
        }
       	tempMap.put("depot",Depot);
       	tempMap.put("daoDate",DAO);
       	
       	if(UtilValidate.isNotEmpty(branchId)){
			result = EntityUtil.filterByCondition(productStoreList, EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, branchId));
			if(UtilValidate.isNotEmpty(result)){
				if(UtilValidate.isNotEmpty(result[0].get("storeName"))){
					tempMap.put("storeName",result[0].get("storeName"));
				}else{
					  tempMap.put("storeName",branchId);
				}
			}
		}else{
			tempMap.put("storeName","");
		}
		
		
		// Postal Address
       	partyPostalAddress = EntityUtil.filterByCondition(postalAddress, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eachPartyId));
	
       	if(UtilValidate.isNotEmpty(partyPostalAddress)){
           	activePostalAddress = EntityUtil.getFirst(partyPostalAddress);
           	
           	fullAddress = "";
		
			if(activePostalAddress.get("address1")){
				fullAddress = fullAddress + activePostalAddress.get("address1");
			}
			if(activePostalAddress.get("address2")){
				fullAddress = fullAddress + ", " + activePostalAddress.get("address2");
			}
			if(activePostalAddress.get("city")){
				fullAddress = fullAddress + ", " + activePostalAddress.get("city");
			}
			if(activePostalAddress.get("postalCode")){
				fullAddress = fullAddress + "-" + activePostalAddress.get("postalCode");
			}
           	
           	tempMap.put("fullAddress",fullAddress);
           	
           	if(activePostalAddress.get("stateProvinceGeoId")){
           		if(geoNameMap.get(activePostalAddress.get("stateProvinceGeoId"))){
           			tempMap.put("stateProvinceGeoId",geoNameMap.get(activePostalAddress.get("stateProvinceGeoId")));
           		}
           	}
           	
           	if(activePostalAddress.get("districtGeoId")){
           		if(geoNameMap.get(activePostalAddress.get("districtGeoId"))){
           			tempMap.put("districtGeoId",geoNameMap.get(activePostalAddress.get("districtGeoId")));
           		}
           	}
           	
        }

		// Get Party Loom
		
		partyLoomDetailsList = EntityUtil.filterByCondition(partyLoomDetails, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eachPartyId));

       	if(UtilValidate.isNotEmpty(partyLoomDetailsList)){
       	
       		loomType = (EntityUtil.getFirst(partyLoomDetailsList)).get("loomTypeId");
       		noOfLooms = (EntityUtil.getFirst(partyLoomDetailsList)).get("quantity");
       		
           	tempMap.put("loomType", loomType);
           	tempMap.put("qty", noOfLooms);
           	
           	maxQtyPerLoom = schemeQtyMap.get(loomType);
           	tempMap.put("ledgerQuota", noOfLooms*maxQtyPerLoom);
           	
        }
        
		weaverMap = [:];
        weaverMap.putAll(tempMap);

		finalList.add(weaverMap);

        
		if(partyLoomDetailsList.size() > 1){
			for(int i=1; i<partyLoomDetailsList.size(); i++){
				addLoom = partyLoomDetailsList.get(i);
				
				tempMap = [:];
				tempMap.put("loomType",addLoom.get("loomTypeId"));
           		tempMap.put("qty",addLoom.get("quantity"));
           		
           		maxQtyPerLoom = schemeQtyMap.get(addLoom.get("loomTypeId"));
           		tempMap.put("ledgerQuota", (addLoom.get("quantity"))*maxQtyPerLoom);
           		
           		
           		weaverMap = [:];
        		weaverMap.putAll(tempMap);
        		
           		finalList.add(weaverMap);
			}
		}
		
		
	}
	prodsEli.close();
	Debug.log("finalList ==============================="+finalList.size());
	
	context.listIt = finalList;
