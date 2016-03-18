	import org.ofbiz.base.util.*;
	import org.ofbiz.entity.Delegator;
	import org.ofbiz.entity.GenericValue;
	import org.ofbiz.entity.util.EntityUtil;
	import java.util.*;
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
	import org.ofbiz.party.contact.ContactHelper;
	
	productsIter = delegator.find("Product",null, null,UtilMisc.toSet("productId","productName","primaryProductCategoryId"), null, null);
	
	List prodCategoryAttrType = delegator.findList("ProductCategoryAttributeType", null,null,null,null,false);
	attributeTypeList = EntityUtil.getFieldListFromEntityList(prodCategoryAttrType, "attrTypeId", true);
	Debug.log("attributeTypeList=======sfcsd==============="+attributeTypeList);
	
	List productAttributeList = delegator.findList("ProductAttribute", null,null,null,null,false);
	Debug.log("productAttributeList=======sfcsd==============="+productAttributeList);
	
	finalList = [];
	while (product = productsIter.next()) {
		
		productId = product.productId;
		
		primaryProductCategoryId = product.primaryProductCategoryId;
		productCategory = delegator.findOne("ProductCategory", [productCategoryId : primaryProductCategoryId], false);
		
		prodMap = [:];
		prodMap.put("parentCategory", productCategory.primaryParentCategoryId);
		prodMap.put("productId", productId);
		prodMap.put("name", product.productName);
		prodMap.put("categoryName", productCategory.description);
		
		productAttributes = EntityUtil.filterByCondition(productAttributeList, EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		
		if(productAttributes){
			for(int i=0; i<productAttributes.size(); i++){
				eachAttribute =  productAttributes.get(i);
				prodMap.put(eachAttribute.attrName, eachAttribute.attrValue);
			}
		}
		
		
		
		tempProdMap = [:];
		tempProdMap.putAll(prodMap);
		
		finalList.add(tempProdMap);
	}
	productsIter.close();
	
	context.listIt = finalList;
	//Debug.log("finalList======================"+finalList);
		