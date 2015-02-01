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

statusFlag="N";
quoteDetails = delegator.findList("QuoteItem",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(quoteDetails)){
	quoteDetails.each{quoteitems->
if("QTITM_QUALIFIED".equals(quoteitems.statusId)){
	poOrder = delegator.findList("OrderItem",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , quoteId)  , null, null, null, false );
if(UtilValidate.isNotEmpty(poOrder)){
	statusFlag="N";
}
if(UtilValidate.isEmpty(poOrder)){
	statusFlag="Y";
}
}		
	}
}
context.statusFlag=statusFlag;
