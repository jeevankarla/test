import org.ofbiz.base.util.*;

// This groovy assumes that quotaList groovy has already been invoked and the context
// contains boothsResultMap and productList

productList = context.productList;
boothsResultMap = context.boothsResultMap;
if (productList == null || boothsResultMap == null) {
	context.routesResultMap = [:];
	return;
}
prodQuantityInitMap = [:];
productList.each{ product ->
	prodQuantityInitMap[product.productId] = 0;
}
totalsMap =[:];
totalsMap.putAll(prodQuantityInitMap);
routesResultMap = [:];
Iterator resMapIter = boothsResultMap.entrySet().iterator();
while (resMapIter.hasNext()) {
	Map.Entry entry = resMapIter.next();
	boothId =entry.getKey();
	boothTotalsMap = boothsResultMap[boothId];
	routeId = boothTotalsMap.routeId;
	if (routeId == null) {
		continue;
	}
	routeTotalsMap = routesResultMap[routeId];
	if (routeTotalsMap == null) {
		routeTotalsMap = [:];
		routeTotalsMap.putAll(prodQuantityInitMap);
		routesResultMap[routeId] = [:];
		routesResultMap[routeId].putAll(routeTotalsMap);
		routeTotalsMap = routesResultMap[routeId];
	}
	productList.each{ product ->
		routeTotalsMap[product.productId] += boothTotalsMap[product.productId];
		totalsMap[product.productId] += boothTotalsMap[product.productId];
	}
}
routesResultMap["Total"] = [:];
routesResultMap["Total"].putAll(totalsMap);
context.routesResultMap = routesResultMap;
//Debug.logInfo("routesResultMap="+routesResultMap,"");