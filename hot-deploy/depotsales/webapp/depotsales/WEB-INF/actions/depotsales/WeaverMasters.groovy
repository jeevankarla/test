	
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
	
	
	
	
	
	//resultCtx = dispatcher.runSync("getCustomerBranch",UtilMisc.toMap("userLogin",userLogin));
	
	
	/*
	Map formatMap = [:];
	List formatList = [];
	
		for (eachList in resultCtx.get("productStoreList")) {
			
			formatMap = [:];
			formatMap.put("productStoreName",eachList.get("storeName"));
			formatMap.put("payToPartyId",eachList.get("payToPartyId"));
			formatList.addAll(formatMap);
			
		}
	context.formatList = formatList;
	*/
	
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
	
	condList = [];
	condList.add(EntityCondition.makeCondition("roleTypeIdTo" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));
	condList.add(EntityCondition.makeCondition("roleTypeId" ,EntityOperator.EQUALS, "EMPANELLED_CUSTOMER"));

	//condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, "62690"));
	
	if(partyId!=null && partyId!=""){
		condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,partyId.trim()));
	}
	if(groupName!=null && groupName!=""){
		group1=EntityCondition.makeCondition(EntityCondition.makeCondition("firstName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%") ,EntityOperator.OR,EntityCondition.makeCondition("lastName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%"));
		group2=EntityCondition.makeCondition(EntityCondition.makeCondition("middleName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%") ,EntityOperator.OR,EntityCondition.makeCondition("groupName" ,EntityOperator.LIKE,"%"+groupName.trim()+"%"));
		condList.add(EntityCondition.makeCondition(group1 ,EntityOperator.OR,group2));
	}
	if(branchId!=null && branchId!=""){
		condList.add(EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,branchId.trim()));
	}
	if(passbookNumber!=null && passbookNumber!=""){
		condList.add(EntityCondition.makeCondition("idValue" ,EntityOperator.EQUALS,passbookNumber.trim()));
	}
	
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	prodsEli = delegator.findListIteratorByCondition(partyRoleAndIde, cond, null,null,null,null);
	groupNameList = prodsEli.getCompleteList();
	
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
		
		/*
		tempMap.put("address1",customer["address1"]);
		tempMap.put("address2",customer["address2"]);
		tempMap.put("city",customer["city"]);
		tempMap.put("postalCode",customer["postalCode"]);
		tempMap.put("stateProvinceGeoId",customer["stateProvinceGeoId"]);
		*/
		
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
       	
       	/*
		if(UtilValidate.isNotEmpty(branchId)){
			result = EntityUtil.filterByCondition(resultCtx.get("productStoreList"), EntityCondition.makeCondition("payToPartyId", EntityOperator.EQUALS, branchId));
			if(UtilValidate.isNotEmpty(result[0].get("storeName"))){
				tempMap.put("storeName",result[0].get("storeName"));
			}else{
			  	tempMap.put("storeName","");
			}
		}else{
			tempMap.put("storeName","");
		}
		*/
		
		tempMap.put("storeName",branchId);
		
		
		// Postal Address
       	partyPostalAddress = EntityUtil.filterByCondition(postalAddress, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eachPartyId));

       	if(UtilValidate.isNotEmpty(partyPostalAddress)){
           	activePostalAddress = EntityUtil.getFirst(partyPostalAddress);
           	
           	fullAddress = "";
		
			if(customer.get("address1")){
				fullAddress = fullAddress + activePostalAddress.get("address1") + ", ";
			}
			if(customer.get("address2")){
				fullAddress = fullAddress + activePostalAddress.get("address2") + ", ";
			}
			if(customer.get("city")){
				fullAddress = fullAddress + activePostalAddress.get("city")+ "-";
			}
			if(customer.get("postalCode")){
				fullAddress = fullAddress + activePostalAddress.get("postalCode");
			}
           	
           	
           	tempMap.put("fullAddress",fullAddress);
			tempMap.put("stateProvinceGeoId",customer["stateProvinceGeoId"]);
			tempMap.put("districtGeoId",customer["districtGeoId"]);
           	
        }

		// Get Party Loom
		
		partyLoomDetailsList = EntityUtil.filterByCondition(partyLoomDetails, EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,eachPartyId));
        //Debug.log("facilityDepot ================================="+facilityDepot );

       	if(UtilValidate.isNotEmpty(partyLoomDetailsList)){
           	tempMap.put("loomType",(EntityUtil.getFirst(partyLoomDetailsList)).get("loomTypeId"));
           	tempMap.put("qty",(EntityUtil.getFirst(partyLoomDetailsList)).get("quantity"));
        }
        
		weaverMap = [:];
        weaverMap.putAll(tempMap);
        //Debug.log("weaverMap================================="+weaverMap);

		finalList.add(weaverMap);

        
		if(partyLoomDetailsList.size() > 1){
			for(int i=1; i<partyLoomDetailsList.size(); i++){
				addLoom = partyLoomDetailsList.get(i);
				
				tempMap = [:];
				tempMap.put("loomType",addLoom.get("loomTypeId"));
           		tempMap.put("qty",addLoom.get("quantity"));
           		
           		weaverMap = [:];
        		weaverMap.putAll(tempMap);
        		
           		finalList.add(weaverMap);
			}
		}
		
		
	}
	prodsEli.close();
	Debug.log("finalList ==============================="+finalList.size());
	
	context.listIt = finalList;
	
	
	