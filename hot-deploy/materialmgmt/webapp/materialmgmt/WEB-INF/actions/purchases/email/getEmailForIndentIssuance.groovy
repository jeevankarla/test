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
import org.ofbiz.order.order.*;
import java.math.RoundingMode;

rounding = RoundingMode.HALF_UP;
dctx = dispatcher.getDispatchContext();
custRequestId = parameters.custRequestId;

conditionList = [];
Debug.log("=====custRequestId Before preparation=of email==custRequestId:"+parameters.custRequestId+"=itemIssuanceId="+parameters.itemIssuanceId+"==itemSeqId="+custRequestItemSeqId);

GenericValue tenantConfigSendIssueEmail = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","enableIndentIssuanceEmail"), false);
sendEmailFlag="";
if (UtilValidate.isNotEmpty(tenantConfigSendIssueEmail)) {
	sendEmailFlag=tenantConfigSendIssueEmail.getString("propertyValue");
}
if (UtilValidate.isEmpty(tenantConfigSendIssueEmail)) {
	sendEmailFlag="N";
}

if("N".equalsIgnoreCase(sendEmailFlag)){
	Debug.log("===SendIssueance Email Not Yet Enabled and propertyValue:"+sendEmailFlag);
	return "success" ;
}

if(UtilValidate.isNotEmpty(parameters.custRequestId)){
custRequestItem = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
if(UtilValidate.isNotEmpty(custRequestItem) && UtilValidate.isNotEmpty(custRequestItem.fromPartyId)){
	indentIssueEmailInput=[:];
	indentIssueEmailInput["userLogin"]=userLogin;
	indentIssueEmailInput["custRequestId"]=parameters.custRequestId;
	indentIssueEmailInput["custRequestItemSeqId"]=parameters.custRequestItemSeqId;
	indentIssueEmailInput["partyId"]=parameters.partyId;
	indentIssueEmailInput["itemIssuanceId"]=parameters.itemIssuanceId;
	indentIssueEmailInput["flag"]="itemIssuance";
	
	partyId="";
	sendTo="";
	subject="Indent #"+parameters.custRequestId+" issued ";
	//"link : <@ofbizContentUrl>/materialmgmt/control/ViewMaterialRequest?custRequestId=10161 "
	bodyText="Hello, <br/> Your indented material (indent #" + parameters.custRequestId + ") has been issued.  Please find attached issuance report for further details.  <br/>  <br/> Regards, <br/> Stores Dept. ";
	
	indentIssueEmailInput["subject"]=subject;
	indentIssueEmailInput["bodyText"]=bodyText;

/*<attribute name="custRequestId" type="String" mode="IN" optional="false"/>
<attribute name="sendFrom" type="String" mode="IN" optional="true"/>
<attribute name="sendTo" type="String" mode="IN" optional="false"/>
<attribute name="sendCc" type="String" mode="IN" optional="true"/>
<attribute name="subject" type="String" mode="IN" optional="true"/>
<attribute name="bodyText" type="String" mode="IN" optional="true" allow-html="safe"/>
<attribute name="partyId" type="String" mode="IN" optional="true"/>*/
	deptPrimeryEmail= dispatcher.runSync("getPartyEmail", [partyId:custRequestItem.fromPartyId, userLogin: userLogin]);
	if(deptPrimeryEmail){
	sendTo=deptPrimeryEmail.emailAddress;
	}
	deptSecondEmail= dispatcher.runSync("getPartyEmail", [partyId:custRequestItem.fromPartyId,contactMechPurposeTypeId:"SECONDARY_EMAIL" ,userLogin: userLogin]);
	if(deptSecondEmail){
		sendTo=sendTo+","+ deptSecondEmail.emailAddress;
	}
	if(UtilValidate.isNotEmpty(sendTo)){
		indentIssueEmailInput["sendTo"]=sendTo;
		emailSendRes=dispatcher.runSync("sendIndentIssuanceEmail", indentIssueEmailInput);
		Debug.log("==emailSendRes==="+emailSendRes+"==ENDDDDDDDDDDDDDDDDD=");
	}
 }
}
return "success" ;