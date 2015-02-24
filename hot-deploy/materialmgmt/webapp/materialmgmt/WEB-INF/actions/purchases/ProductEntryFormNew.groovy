import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


condProductCategoryList = [];
condProductCategoryList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
condProductCategoryList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.EQUALS, "PUR_ANLS_CODE"));
condition = EntityCondition.makeCondition(condProductCategoryList,EntityOperator.OR);
productCategoryList = delegator.findList("ProductCategory", condition, null, null, null, false);
context.productCategoryList=productCategoryList;

condProductTypeList = [];
condProductTypeList.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL, null));
condition = EntityCondition.makeCondition(condProductTypeList,EntityOperator.AND);
productTypeList = delegator.findList("ProductType", condition, null, null, null, false);
context.productTypeList=productTypeList;

conditionUOMlist = [];
conditionUOMlist.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "WEIGHT_MEASURE"));
conditionUOMlist.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "VOLUME_DRY_MEASURE"));
conditionUOMlist.add(EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "VOLUME_LIQ_MEASURE"));
condition = EntityCondition.makeCondition(conditionUOMlist,EntityOperator.OR);
productUOMList = delegator.findList("Uom", condition, null, null, null, false);
context.productUOMList=productUOMList;
/*
conditionAttributeList = [];
conditionAttributeList.add(EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, "LEDGERFOLIONO"));
condition = EntityCondition.makeCondition(conditionAttributeList,EntityOperator.AND);
attributeNameList = delegator.findList("Enumeration", condition, null, null, null, false);
context.attributeNameList=attributeNameList;
*/
