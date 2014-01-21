import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator.*;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.text.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.*;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


List conditionList=[];

conditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, "9003"));
condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
catalogIds = delegator.findList("ProductStoreCatalog", condition , ["prodCatalogId"] as Set, null, null, false );

catalogIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogIds, "prodCatalogId", false);

conditionList=[];
conditionList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, catalogIdsFieldList));
condition1=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
catalogCategoryIds = delegator.findList("ProdCatalogCategory", condition1 , ["productCategoryId"] as Set, null, null, false );

categoryIdsFieldList = EntityUtil.getFieldListFromEntityList(catalogCategoryIds, "productCategoryId", false);

conditionList=[];
conditionList.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.IN, categoryIdsFieldList));
conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL, "BYPROD"));
/*conditionList.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "N"));*/
condition2=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
categoryProductIds = delegator.findList("ProductAndCategoryMember", condition2 , ["productId","brandName"] as Set, null, null, false );

productIdsList = EntityUtil.getFieldListFromEntityList(categoryProductIds, "productId", false);
brandNamesList = EntityUtil.getFieldListFromEntityList(categoryProductIds, "brandName", false);

context.put("categoryIdsFieldList", categoryIdsFieldList);
context.put("productIds", productIdsList);
parameters.productCategoryIds = catalogCategoryIds;

JSONObject brandNameJSON = new JSONObject();
brandNameJSON.put("brandName", brandNamesList);
context.brandNameJSON = brandNameJSON;

JSONObject productIdsJSON = new JSONObject();
productIdsJSON.put("productId", productIdsList);
context.productIdsJSON = productIdsJSON;



