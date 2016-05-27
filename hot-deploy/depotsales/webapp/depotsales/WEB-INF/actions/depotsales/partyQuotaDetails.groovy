import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import org.ofbiz.entity.transaction.*
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.model.DynamicViewEntity;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.order.order.*;
import java.math.RoundingMode;
import org.ofbiz.party.contact.ContactMechWorker;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.entity.model.ModelKeyMap;




JSONObject partyJSON = new JSONObject();
JSONArray AllLoomArrayJSON = new JSONArray();
effectiveDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
JSONObject finalJson = new JSONObject();
if(parameters.partyId){
	
	partyRoleAndIde = new DynamicViewEntity();
	partyRoleAndIde.addMemberEntity("PC", "PartyClassification");
	partyRoleAndIde.addMemberEntity("PCG", "SchemePartyClassificationGroup");
	partyRoleAndIde.addAliasAll("PC", null, null);
	partyRoleAndIde.addAliasAll("PCG", null, null);
	partyRoleAndIde.addViewLink("PC","PCG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyClassificationGroupId","partyClassificationGroupId"));
	condList = [];
	condList.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS, (parameters.partyId)));
	cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
	beganTransaction = false;
	EntityListIterator prodsEli = null;
	JSONArray schemeArray = new JSONArray();
	try {
	beganTransaction = TransactionUtil.begin();
	prodsEli = delegator.findListIteratorByCondition(partyRoleAndIde, cond,null, UtilMisc.toSet("schemeId"), null, null);
	groupNameList = prodsEli.getCompleteList();
	for(scheme in groupNameList){
		JSONObject schemeJson = new JSONObject();
		if(scheme.get("schemeId").equals("TEN_PERCENT_MGPS")){
			schemeJson.put("schemeId",scheme.get("schemeId"));
			schemeJson.put("schemeValue","MGPS + 10%");
			schemeArray.add(schemeJson);
		}else if(scheme.get("schemeId").equals("MGPS")){
		    schemeJson.put("schemeId",scheme.get("schemeId"));
		    schemeJson.put("schemeValue","MGPS");
			schemeArray.add(schemeJson);
		}
	}
	} catch (Exception e) {
	}
	finally{
	  if(UtilValidate.isNotEmpty(prodsEli)){
		prodsEli.close();
	   }
	    
	}
	JSONObject schemeJson = new JSONObject();
	schemeJson.put("schemeId","General");
	schemeJson.put("schemeValue","General");
	schemeArray.add(schemeJson);
	finalJson.put("SchemeList", schemeArray);
	quota=0;
	conditionList=[];
	conditionList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS,"SCHEME_MGPS"));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parameters.productId));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	ProductCategoryAndMemberObject = EntityUtil.getFirst(delegator.findList("ProductCategoryAndMember",condition,null,null,null,false));
	productCategoryId=ProductCategoryAndMemberObject.get("productCategoryId");
	resultCtx = dispatcher.runSync("getPartyAvailableQuotaBalanceHistory",UtilMisc.toMap("userLogin",userLogin, "partyId", parameters.partyId,"effectiveDate",effectiveDate,"productCategoryId",productCategoryId));
	productCategoryQuotasMap = resultCtx.get("schemesMap");
	
	if(productCategoryQuotasMap.containsKey(productCategoryId)){
		if(UtilValidate.isNotEmpty(productCategoryQuotasMap.get(productCategoryId))){
			quota = productCategoryQuotasMap.get(productCategoryId);
		}
		else{
			
		}

	}
	
	finalJson.put("quota", quota);
	context.quotaJson=finalJson;
	Debug.log("&&&&&&&&&&&&&&&&&&&&&&&&-----------------"+finalJson);
	request.setAttribute("quotaJson", finalJson);
	
}
return "success";

	

