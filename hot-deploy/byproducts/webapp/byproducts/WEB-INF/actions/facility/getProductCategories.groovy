import org.ofbiz.base.util.UtilDateTime;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javolution.util.FastList;
import org.ofbiz.entity.Delegator;
import org.ofbiz.base.util.*;
import net.sf.json.JSONObject;
import org.ofbiz.entity.util.*;
import net.sf.json.JSONArray;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;

List prodCatList=FastList.newInstance();
ecl = EntityCondition.makeCondition([EntityCondition.makeCondition("productTypeId",EntityOperator.EQUALS,"FINISHED_GOOD"),
									 EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.NOT_IN,UtilMisc.toList("BOX","CAN","CRATE"))],EntityOperator.AND);
List productList=delegator.findList("Product",ecl,null,null,null,false);
if(UtilValidate.isNotEmpty(productList)){
	prodCatList=EntityUtil.getFieldListFromEntityList(productList, "primaryProductCategoryId", true);
}
context.prodCatList=prodCatList;