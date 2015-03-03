/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ofbiz.product.product.ProductWorker;

import org.ofbiz.entity.util.EntityUtil;
uomList=[];
condList=[];
productStoreId=parameters.productStoreId;

Debug.log("=======productStoreId="+productStoreId);
/*<set field="productStoreResult" value="${groovy:in.vasista.vbiz.byproducts.icp.ICPServices.getProductStoreByGroupId(delegator, 'FGS_SALE_STORE')}" />
<set field="productStoreId" value="${productStoreResult.factoryStoreId}"/>
<set field="prodCatalogIdResult" value="${groovy:org.ofbiz.product.catalog.CatalogWorker.getStoreCatalogs(delegator,productStoreId)}" />
<!--  <set field="prodCatalogIdResult" value="${prodCatalogIdResult.[0]get(&quot;prodCatalogId&quot;)}"/> -->
<set field="prodCatalogId" value="${prodCatalogIdResult[0].prodCatalogId}"/>
<set field="productCategoryListResult" value="${groovy:org.ofbiz.product.catalog.CatalogWorker.getProdCatalogCategories(delegator,prodCatalogId,null)}" />
<set field="productCategoryIdsList" value="${groovy:org.ofbiz.entity.util.EntityUtil.getFieldListFromEntityList(productCategoryListResult, 'productCategoryId', true);}" type="List"/>

<entity-condition entity-name="ProductCategory" list="productCategoryListResult" use-cache="true">
	<condition-list>
		<condition-expr field-name="productCategoryId" operator="in"  from-field="productCategoryIdsList"/>
	</condition-list>
</entity-condition>*/

/*if("y".equals(parameters.productflag))
{
	JSONArray productJSONList = new JSONArray();
	
	productDetails=ProductWorker.findProduct(delegator, productId);
	JSONObject productObj = new JSONObject();
	productObj.put("productName",productDetails.productName);
	productObj.put("description",productDetails.description);
	productObj.put("longDescription",productDetails.longDescription);
	request.setAttribute("productObj", productObj);
	return "success";
}*/

productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);

prodCatalogIdResult=org.ofbiz.product.catalog.CatalogWorker.getStoreCatalogs(delegator,productStoreId);

prodCatalogId="";
prodCatalog=EntityUtil.getFirst(prodCatalogIdResult);

JSONArray dataJSONList = new JSONArray();
if(UtilValidate.isNotEmpty(prodCatalog)){
prodCatalogId=prodCatalog.prodCatalogId;

productCategoryListResult=org.ofbiz.product.catalog.CatalogWorker.getProdCatalogCategories(delegator,prodCatalogId,null);
productCategoryIdsList=EntityUtil.getFieldListFromEntityList(productCategoryListResult, 'productCategoryId', true);
condList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryIdsList));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
productCategoryList = delegator.findList("ProductCategory", cond, null,null, null, false);
productCategoryList.each{eachCatagory ->
	JSONObject newUomObj = new JSONObject();
	newUomObj.put("value",eachCatagory.productCategoryId);
	newUomObj.put("text",eachCatagory.description);
	dataJSONList.add(newUomObj);
}
}
request.setAttribute("catagoryList", dataJSONList);
Debug.log("=======dataJSONList="+dataJSONList);
context.dataJSONList=dataJSONList;
return "success";




