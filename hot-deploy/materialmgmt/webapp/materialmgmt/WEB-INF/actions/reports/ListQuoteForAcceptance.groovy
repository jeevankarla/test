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

custRequestId=parameters.custRequestId;
conditionList=[];
conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"QUO_CREATED"));
cond=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
quoteItemList = delegator.findList("QuoteAndItemAndCustRequest",cond, null, null, null, false );
quoteIds = EntityUtil.getFieldListFromEntityList(quoteItemList, "quoteId", true);
List quotesList=[];

quoteIds.each{eachquoteId->
	quoteItemMap=[:];
	
	quoteItemDetails = delegator.findList("QuoteAndItemAndCustRequest",EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS , eachquoteId)  , null, null, null, false );
	quoteItemDetail=EntityUtil.getFirst(quoteItemDetails);	
	quoteId= quoteItemDetail.quoteId;
	partyId	= quoteItemDetail.partyId	;
	issueDate = quoteItemDetail.issueDate;
	validFromDate=quoteItemDetail.validFromDate;
	validThruDate=quoteItemDetail.validThruDate;	
	quoteItemMap.put("quoteId",quoteId);
	quoteItemMap.put("partyId",partyId);
	quoteItemMap.put("issueDate",issueDate);
	quoteItemMap.put("validFromDate",validFromDate);
	quoteItemMap.put("validThruDate",validThruDate);
	quotesList.add(quoteItemMap);
}	
context.quotesList=quotesList;

	
	
	
	