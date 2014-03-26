
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.party.party.PartyHelper;

JSONArray jsonRevenueData = new JSONArray();



JSONObject parentGlJson = new JSONObject();
//REVENUE
if (revenueAccountBalanceList) {
	i = 0;
	revenueAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		jsonRevenueData.add(obj);
	}
	
	revParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
}
context.put("jsonRevenueData", jsonRevenueData.toString());

// EXPENCE
JSONArray jsonExpenceData = new JSONArray();
if (expenseAccountBalanceList) {
	i = 0;
	expenseAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		jsonExpenceData.add(obj);
	}
	
	expenParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
	//
}
context.put("jsonExpenceData", jsonExpenceData.toString());

// INCOME
JSONArray jsonIncomeData = new JSONArray();
if (incomeAccountBalanceList) {
	i = 0;
	incomeAccountBalanceList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		jsonIncomeData.add(obj);
	}
	
	incomeParentGlAccount.each{ parentGl ->
		parentGlJson.put(parentGl.glAccountId , parentGl.accountName);
	}
	//
}
context.put("jsonIncomeData", jsonIncomeData.toString());


//Balance Totals

JSONArray jsonBalanceTotalData = new JSONArray();
if (balanceTotalList) {
	i = 0;
	balanceTotalList.each { accValue ->
		JSONObject obj = new JSONObject();
		id = "id_" + i++;
		obj.put("id", id);
		obj.putAll(accValue);
		obj.put("currencySymbol", com.ibm.icu.util.Currency.getInstance("INR").getSymbol(locale));
		jsonBalanceTotalData.add(obj);
	}
	
}
context.put("jsonBalanceTotalData", jsonBalanceTotalData.toString());

context.put("parentGlJson", parentGlJson.toString());

