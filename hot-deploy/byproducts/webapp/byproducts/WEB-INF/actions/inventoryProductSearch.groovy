
import org.ofbiz.base.util.*;

listIt = [];
paramList = context.listIt;
if(UtilValidate.isNotEmpty(paramList)){
	listIt = paramList.getCompleteList();
}
myMap = [:];
myMapValue = [:];
listIt.each{eachItem ->
	tempMap = [:];
	productId = eachItem.getAt("productId");
	primaryProductCategoryId = eachItem.getAt("primaryProductCategoryId");
	brandName = eachItem.getAt("brandName");
	QOH = eachItem.getAt("quantityOnHandTotal");
	ATP = eachItem.getAt("availableToPromiseTotal");
	tempMap.put("productId", productId);
	tempMap.put("primaryProductCategoryId", primaryProductCategoryId);
	tempMap.put("brandName", brandName);
	tempMap.put("QOH", QOH);
	tempMap.put("ATP", ATP);
	if(myMap.containsKey(productId)){
		myMapValue = myMap.get(productId);
		myMapQOH = myMapValue.get("QOH");
		myMapATP = myMapValue.get("ATP");
		resultQOH = myMapQOH + QOH;
		resultATP = myMapATP + ATP;
		myMapValue.putAt("QOH", resultQOH);
		myMapValue.putAt("ATP", resultATP);
		myMap.put(productId, myMapValue);
	}
	else{
		myMap.put(productId, tempMap);
	}
}
listed = [];
mapValue = [:];
myMap.each{item ->
	mapValue = item.getValue();
	listed.add(mapValue);
}
context.put("listIt",listed);


