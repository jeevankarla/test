
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
if(UtilValidate.isNotEmpty(parameters.invoiceSequence)){
	invoiceSequence = parameters.invoiceSequence;
	billOfSaleInvoiceSequence = delegator.findList("BillOfSaleInvoiceSequence",EntityCondition.makeCondition("invoiceSequence", EntityOperator.EQUALS , invoiceSequence)  , null, null, null, false );
	invoiceId=EntityUtil.getFirst(billOfSaleInvoiceSequence).invoiceId;
	parameters.invoiceId=invoiceId;
}
