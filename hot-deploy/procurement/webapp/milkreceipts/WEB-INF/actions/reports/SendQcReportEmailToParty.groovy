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

dctx = dispatcher.getDispatchContext();
String milkTransferId="";
if(UtilValidate.isEmpty(parameters.milkTransferId)){
	Debug.logError("TareWeight Details Not Entered.","");
	return "success";
}
milkTransferId=parameters.milkTransferId;

GenericValue tenantConfigSendQcReportEmail = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","MILK_RECEIPTS", "propertyName","enableQcReportEmail"), false);
sendEmailFlag="";
if (UtilValidate.isNotEmpty(tenantConfigSendQcReportEmail)) {
	sendEmailFlag=tenantConfigSendQcReportEmail.getString("propertyValue");
}
if (UtilValidate.isEmpty(tenantConfigSendQcReportEmail)) {
	sendEmailFlag="N";
}

if("N".equalsIgnoreCase(sendEmailFlag)){
	Debug.log("===Sending QC Report Email Not Yet Enabled and propertyValue:"+sendEmailFlag);
	return "success" ;
}

GenericValue milkTransfer = delegator.findOne("MilkTransfer",UtilMisc.toMap("milkTransferId",milkTransferId),false);
String partyId = "";
if(UtilValidate.isEmpty(milkTransfer)){
	Debug.log("=======No Such MilkTransfer Found For "+milkTransferId);
	return "success";
}
partyId = milkTransfer.getString("partyId");
productId = milkTransfer.getString("productId");
GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
String prodName = "";
String disptchDateTime = UtilDateTime.toDateString(milkTransfer.getTimestamp("sendDate"),"dd MMM, yyyy HH:mm");
String dcNo = milkTransfer.getString("dcNo");
String vehicleId = milkTransfer.getString("containerId");
if(UtilValidate.isNotEmpty(productId)){
	prodName = product.description;
}
String attchName =prodName+"_"+dcNo+"_"+vehicleId+"_"+UtilDateTime.toDateString(milkTransfer.getTimestamp("sendDate"),"dd-MM-yyyy")+".pdf";
sendQcReportEmailInput=[:];
sendQcReportEmailInput["userLogin"]=userLogin;
sendQcReportEmailInput["milkTransferId"]=milkTransferId;
sendQcReportEmailInput["partyId"]=partyId;
sendQcReportEmailInput["attchName"]=attchName;

sendTo="";
subject="Milk Type : "+prodName+", Despatched Date : "+disptchDateTime+", DC No : "+dcNo+", Vehicle No :"+vehicleId;
bodyText="Hello, <br/> Please find attached details of Milk Tanker dispatched to Mother Dairy. <br/>  <br/> Thanks And Regards, <br/> Mother Dairy, <br/> Unit Of KMF, <br/> Yelahanka,<br/> Bangalore. <br/> Note: Please do not reply to this email. It has been sent from an email account that is not monitored. ";

sendQcReportEmailInput["subject"]=subject;
sendQcReportEmailInput["bodyText"]=bodyText;

partyPrimeryEmail= dispatcher.runSync("getPartyEmail", [partyId:partyId, userLogin: userLogin]);
if(UtilValidate.isNotEmpty(partyPrimeryEmail) && UtilValidate.isNotEmpty(partyPrimeryEmail.emailAddress) ){
	sendTo=partyPrimeryEmail.emailAddress;
	}
partySecondEmail= dispatcher.runSync("getPartyEmail", [partyId:partyId,contactMechPurposeTypeId:"SECONDARY_EMAIL" ,userLogin: userLogin]);
if(UtilValidate.isNotEmpty(partySecondEmail) && UtilValidate.isNotEmpty(partySecondEmail.emailAddress) ){
	sendTo=sendTo+","+ partySecondEmail.emailAddress;
}
if(UtilValidate.isNotEmpty(sendTo)){
	sendQcReportEmailInput["sendTo"]=sendTo+", kvarma@vasista.in ";
	emailSendRes=dispatcher.runSync("sendQcReportEmail", sendQcReportEmailInput);
	Debug.log("==emailSendRes==="+emailSendRes+"==ENDDDDDDDDDDDDDDDDD="+"==Send=To=partyId=="+partyId);
}else{
Debug.log("===SendTo addresss is empty ,so email not sent =========>for partyId: "+partyId);
}
return "success";


