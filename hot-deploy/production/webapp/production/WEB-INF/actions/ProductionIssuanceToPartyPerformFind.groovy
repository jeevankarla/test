
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
import net.sf.json.JSONArray;
import java.util.SortedMap;
import java.math.RoundingMode;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityTypeUtil;


if(UtilValidate.isNotEmpty(result.listIt)){
list=result.listIt;
if(UtilValidate.isNotEmpty(parameters.partyFrom)){
	parameters.partyIdFrom=parameters.partyFrom;
}
List partyIdFromList = FastList.newInstance();
if(UtilValidate.isNotEmpty(context.get("partyId"))){
	partyIdFromList = context.get("partyId");
}
List resultList=FastList.newInstance();
   GenericValue custRequest=null;
	while(custRequest=list.next()){
	   List	custRequstParty=delegator.findList("CustRequestParty",EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,custRequest.custRequestId),null,null,null,false);
		custReqParty=EntityUtil.getFirst(custRequstParty);
		Map tempMap = FastMap.newInstance();
			tempMap.itemIssuanceId = custRequest.itemIssuanceId;
			tempMap.custRequestId=custRequest.custRequestId;
			tempMap.custRequestItemSeqId=custRequest.custRequestItemSeqId;
			tempMap.custRequestDate = custRequest.custRequestDate;
			tempMap.shipmentId = custRequest.shipmentId;
			tempMap.fromPartyId = custRequest.fromPartyId;
			tempMap.custRequestName = custRequest.custRequestName;
			tempMap.productId = custRequest.productId;
			tempMap.quantity = custRequest.quantity;
			tempMap.issueDate = custRequest.issueDate;
			tempMap.issuedByUserLoginId = custRequest.issuedByUserLoginId;
			tempMap.custRequestTypeId=custRequest.custRequestTypeId;
			if(UtilValidate.isNotEmpty(custReqParty.partyId)){
				tempMap.partyIdFrom = custReqParty.partyId;
				}
			if(UtilValidate.isEmpty(parameters.partyIdFrom) && UtilValidate.isEmpty(partyIdFromList)){
			 resultList.add(tempMap);
			}else if(UtilValidate.isNotEmpty(partyIdFromList) && UtilValidate.isEmpty(parameters.partyIdFrom)){
				if(partyIdFromList.contains(custReqParty.partyId)){
					resultList.add(tempMap);
				}
			}else if(UtilValidate.isNotEmpty(parameters.partyIdFrom) && parameters.partyIdFrom==custReqParty.partyId){
			 resultList.add(tempMap);
			}
	}
	context.listIt=resultList;
}