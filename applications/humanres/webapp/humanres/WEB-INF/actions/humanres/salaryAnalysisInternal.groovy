import org.ofbiz.base.util.Debug;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.math.RoundingMode;


//Debug.logError("parameters.customTimePeriodId="+parameters.customTimePeriodId, "");
//Debug.logError("payRollSummaryMap="+payRollSummaryMap, "");
//Debug.logError("payRollMap="+payRollMap, "");

rounding = RoundingMode.HALF_UP;
JSONArray benefitsPieDataJSON = new JSONArray();
JSONArray benefitsTableJSON = new JSONArray();
payRollSummaryMap = context.payRollSummaryMap;
if (payRollSummaryMap != null) {
payRollSummaryMap.each { payhead ->
	if (payhead.getValue() > 0) {
		benefitName = benefitDescMap.get(payhead.getKey()) ? benefitDescMap.get(payhead.getKey()) : payhead.getKey();
		JSONArray benefitJSON = new JSONArray();
		benefitJSON.add(benefitName);
		benefitJSON.add(payhead.getValue().setScale(0,rounding));
		benefitsTableJSON.add(benefitJSON);
	
		JSONObject benefitPie = new JSONObject();
		benefitPie.put("label", benefitName);
		benefitPie.put("data", payhead.getValue().setScale(0,rounding));
		benefitsPieDataJSON.add(benefitPie);
	}
}
}
//Debug.logError("benefitsPieDataJSON="+benefitsPieDataJSON,"");
context.benefitsPieDataJSON = benefitsPieDataJSON;
context.benefitsTableJSON = benefitsTableJSON;