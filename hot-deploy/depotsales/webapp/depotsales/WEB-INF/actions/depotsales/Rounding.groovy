import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.SortedMap;
 
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.party.party.PartyHelper;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;



/*

itemType = parameters.itemType;

decimals

roundType

places*/


conditionDeopoList = [];
conditionDeopoList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS,"COMMERCIAL_MOD_ADJ"));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
invoiceItemTypeList = delegator.findList("InvoiceItemType",conditionDepo,null,null,null,false);


invoiceItemTypeIdS=EntityUtil.getFieldListFromEntityList(invoiceItemTypeList, "invoiceItemTypeId", true);

List<String> payOrderBy = UtilMisc.toList("invoiceItemTypeId");
conditionDeopoList.clear();
conditionDeopoList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN,invoiceItemTypeIdS));
conditionDeopoList.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_IN,["ROUNDING_OFF","TEN_PER_CHARGES","ROUNDING_CHARGES","TEN_PER_DISCOUNT"]));
conditionDepo=EntityCondition.makeCondition(conditionDeopoList,EntityOperator.AND);
invoiceItemTypeList = delegator.findList("InvoiceItemType",conditionDepo,null,payOrderBy,null,false);


context.invoiceItemTypeList = invoiceItemTypeList;


InvoiceItemTypeAttribute = delegator.findList("InvoiceItemTypeAttribute",null,null,null,null,false);


context.InvoiceItemTypeAttribute = InvoiceItemTypeAttribute;
