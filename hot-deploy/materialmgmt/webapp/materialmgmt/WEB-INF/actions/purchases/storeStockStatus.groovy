import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;
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

import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.purchase.MaterialHelperServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.inventory.InventoryWorker;
import in.vasista.vbiz.purchase.MaterialHelperServices;

dctx = dispatcher.getDispatchContext();
facilityId=parameters.issueToFacilityId;
 productDetails = delegator.findList("ProductFacility",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS , facilityId)  ,  UtilMisc.toSet("productId"), null, null, false );
 productIds = EntityUtil.getFieldListFromEntityList(productDetails, "productId", true);
 
productCatDetails = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productId", EntityOperator.IN , productIds)  , null, null, null, false );
productCatIds = EntityUtil.getFieldListFromEntityList(productCatDetails,"productCategoryId", true);
if(UtilValidate.isNotEmpty(productCatIds)){
	prodMap=[:];
	productCatIds.each{productCatId->
    prodList=[];
	List conlist=[];
	conlist.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCatId));
	conlist.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
	cond=EntityCondition.makeCondition(conlist,EntityOperator.AND);
	prodIdData = delegator.findList("ProductAndCategoryMember", cond , null, null, null, false );
  
	if(UtilValidate.isNotEmpty(prodIdData)){
	  prodIdData.each{productDetails->
	  productDetailMap=[:];
	  productDetailMap["productId"]=productDetails.productId;
	  productDetailMap["description"]=productDetails.description;
	  uomId=productDetails.quantityUomId;
	  
	  if(UtilValidate.isNotEmpty(uomId)){
		  uomDesc = delegator.findList("Uom",EntityCondition.makeCondition("uomId", EntityOperator.EQUALS , uomId)  , null, null, null, false );
		  uomDesc=EntityUtil.getFirst(uomDesc);
		  productDetailMap["unit"]=uomDesc.abbreviation;
	      }
	  
	  prodList.addAll(productDetailMap);
	  
       }
	 }
  prodMap.put(productCatId,prodList);
   }
}

context.prodMap=prodMap;

