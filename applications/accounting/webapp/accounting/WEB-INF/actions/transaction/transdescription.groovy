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


invoiceId=parameters.invoiceId;
paymentId=parameters.paymentId;

/*amount=parameters.amount;
flag=parameters.flag;
if("C".equals(flag)){
CreditAmount=amount;
context.CreditAmount=CreditAmount;
context.DebitAmount=0;
}else{
DebitAmount=amount;
context.CreditAmount=0;
context.DebitAmount=DebitAmount;
}
*/
transDescription=null;
invoiceDetails = delegator.findOne("Invoice", [invoiceId : parameters.invoiceId], false);

if(UtilValidate.isNotEmpty(invoiceDetails)){
   transDescription = invoiceDetails.description;
}
   if(UtilValidate.isEmpty(transDescription)){
  paymentDetails = delegator.findOne("Payment", [paymentId : parameters.paymentId], false);
  if(UtilValidate.isNotEmpty(paymentDetails)){
	  
  transDescription = paymentDetails.comments;
  finAccountTransId=paymentDetails.finAccountTransId
  if(UtilValidate.isEmpty(transDescription)){
	  finAccountTransData = delegator.findOne("FinAccountTrans", [finAccountTransId :finAccountTransId], false);
	  if(UtilValidate.isNotEmpty(finAccountTransData)){
		  transDescription=finAccountTransData.comments;
		  
	  }  
    }
  }
   }
   context.transDescription=transDescription;