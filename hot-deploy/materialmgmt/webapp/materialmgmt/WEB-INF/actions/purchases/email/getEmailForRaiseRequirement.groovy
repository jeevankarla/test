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
productId = parameters.productId;
requirementId = parameters.requirementId;
fromPartyId = parameters.fromPartyId;
facilityId = parameters.facilityId;
quantity = parameters.quantity;

conditionList = [];


GenericValue tenantConfigSendIssueEmail = delegator.findOne("TenantConfiguration", UtilMisc.toMap("propertyTypeEnumId","PURCHASE_OR_STORES", "propertyName","enableRaiseRequirementEmail"), false);
sendEmailFlag="";
if (UtilValidate.isNotEmpty(tenantConfigSendIssueEmail)) {
	sendEmailFlag=tenantConfigSendIssueEmail.getString("propertyValue");
}
if (UtilValidate.isEmpty(tenantConfigSendIssueEmail)) {
	sendEmailFlag="N";
}

if("N".equalsIgnoreCase(sendEmailFlag)){
	Debug.log("===Send Raise Requirement Email Not Yet Enabled and propertyValue:"+sendEmailFlag);
	return "success" ;
}

if(UtilValidate.isNotEmpty(parameters.custRequestId) && UtilValidate.isNotEmpty(parameters.fromPartyId) && UtilValidate.isNotEmpty(parameters.requirementId) && UtilValidate.isNotEmpty(parameters.productId)){
custRequestItem = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId", custRequestId), false);
requirement = delegator.findOne("Requirement",UtilMisc.toMap("requirementId",requirementId),false);
product =  delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
if(UtilValidate.isNotEmpty(custRequestItem) && UtilValidate.isNotEmpty(requirement) && UtilValidate.isNotEmpty(product)){
	raiseRequirementEmailInput=[:];
	raiseRequirementEmailInput["userLogin"]=userLogin;
	raiseRequirementEmailInput["flag"]="raiseRequirement";
	
	String uomDesc = "";
	String internalName = "";
	String productName = "";
	String description = "";
	String facilityName = "";
	if(UtilValidate.isNotEmpty(parameters.facilityId)){
		facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
		if(UtilValidate.isNotEmpty(facility)){
			facilityName = facility.facilityName;
		}
	}
	if(UtilValidate.isNotEmpty(product.internalName)){
		internalName = product.internalName;
	}
	if(UtilValidate.isNotEmpty(product.productName)){
		productName = product.productName;
	}
	if(UtilValidate.isNotEmpty(product.description)){
		description = product.description;
	}
	if(UtilValidate.isNotEmpty(product.quantityUomId)){
		uom = delegator.findOne("Uom",UtilMisc.toMap("uomId",product.quantityUomId),false);
		if(UtilValidate.isNotEmpty(uom) && UtilValidate.isNotEmpty(uom)){
			uomDesc=uom.description;
		}
	}
	partyId="";
	sendTo="";
	subject="Store ::"+facilityName+" ,RQ Id ::"+requirementId+", ProductId ::"+internalName+" "+productName+" ,Nos :: "+uomDesc;
	
	bodyText="Please note the " + facilityName + " has raised the following requirement. <br/>  <br/> RequirementId :"+ requirementId +" <br/> ProductId : "+ internalName +" <br/>Description :"+ description+" <br/> Quantity :"+quantity +"("+uomDesc+")"+" <br/> Indent No :"+custRequestId+" <br/> Indented Department :"+PartyHelper.getPartyName(delegator, fromPartyId, false);
	
	raiseRequirementEmailInput["subject"]=subject;
	raiseRequirementEmailInput["bodyText"]=bodyText;
	
	deptPrimeryEmail= dispatcher.runSync("getPartyEmail", [partyId: "INT5", userLogin: userLogin]);
	if(UtilValidate.isNotEmpty(deptPrimeryEmail) && UtilValidate.isNotEmpty(deptPrimeryEmail.emailAddress) ){
		sendTo=deptPrimeryEmail.emailAddress;
	}
	deptSecondEmail= dispatcher.runSync("getPartyEmail", [partyId:"INT5",contactMechPurposeTypeId:"SECONDARY_EMAIL" ,userLogin: userLogin]);
	if(UtilValidate.isNotEmpty(deptSecondEmail) && UtilValidate.isNotEmpty(deptSecondEmail.emailAddress) ){
		sendTo=sendTo+","+ deptSecondEmail.emailAddress;
	}
	
	Debug.log("===sendTo Addresss is:"+sendTo+"=custRequestId="+custRequestId+"==requirementId="+requirementId);
	if(UtilValidate.isNotEmpty(sendTo)){
		raiseRequirementEmailInput["sendTo"]=sendTo +", kvarma@vasista.in ";
		emailSendRes=dispatcher.runSync("sendRaiseRequirementEmail", raiseRequirementEmailInput);
		Debug.log("==emailSendRes==="+emailSendRes+"==ENDDDDDDDDDDDDDDDDD="+"==Send=To=partyId=="+fromPartyId);
	}else{
	Debug.log("===SendTo addresss is empty ,so email not sent to Department=========>for partyId: "+fromPartyId);
	}
 }
}
return "success" ;
