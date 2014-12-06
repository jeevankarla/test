import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.humanres.PayrollService;


requirementsList=[];

if(UtilValidate.isNotEmpty(parameters.list)){
	list=parameters.list;
	requirementsList=list.tokenize(',');
}else{
	return;
}
productCountList=[];
totalAmt=0;
productCount=0;
totalCount=requirementsList.size();
requirementsList.each{ requirement->
	JSONObject newObj = new JSONObject();
	requirmntsList=delegator.findOne("Requirement",[requirementId:requirement],false);
	productId=requirmntsList.productId;
	if(UtilValidate.isEmpty(productCountList) || !(productCountList).contains(productId)){
		productCountList.add(productId);
	}
	productCount=productCountList.size();
	conList=[];
	conList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	condition=EntityCondition.makeCondition(conList,EntityOperator.AND);
	productsList=delegator.findList("SupplierProduct",condition,null,null,null,false);
	productList=EntityUtil.getFirst(productsList);
	lastPrice=0;
	if(UtilValidate.isNotEmpty(productList.lastPrice)){
		lastPrice=productList.lastPrice;
	}
	totalAmt=totalAmt+lastPrice;
	}
request.setAttribute("totalCount",totalCount);
request.setAttribute("productCount",productCount);
request.setAttribute("totalAmt",totalAmt);