import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import javolution.util.FastMap;
import java.text.ParseException;
import org.ofbiz.service.ServiceUtil;
import in.vasista.vbiz.facility.util.FacilityUtil;
import org.ofbiz.accounting.payment.PaymentWorker;
unAppliedCheck=parameters.unAppliedCheck;
parentTypeId=parameters.parentTypeId;

//------------------------------------------------------------------------------checking Un-applied payments
	if((UtilValidate.isNotEmpty(unAppliedCheck)) && "Y".equals(unAppliedCheck)){
		if(UtilValidate.isNotEmpty(result.listIt)){
			resultList = [];
			list=result.listIt;
			GenericValue paymentEntry = null;
			while((paymentEntry=list.next()) != null){
				BigDecimal paymentNotApplied = PaymentWorker.getPaymentNotApplied(delegator,paymentEntry.paymentId);
				if(paymentNotApplied.compareTo(BigDecimal.ZERO)>0){
					resultList.add(paymentEntry);
				}
			}
			if(parentTypeId.equals("RECEIPT")){
			context.paymentList=resultList;
			}else if(parentTypeId.equals("DISBURSEMENT")){
			context.listIt=resultList;
			}
		}
	}
//------------------------------------------------------------------------------checking applied payments
	if((UtilValidate.isNotEmpty(unAppliedCheck)) && "N".equals(unAppliedCheck)){
		if(UtilValidate.isNotEmpty(result.listIt)){
			resultList = [];
			list=result.listIt;
			GenericValue paymentEntry = null;
			while((paymentEntry=list.next()) != null){
			BigDecimal paymentNotApplied = PaymentWorker.getPaymentNotApplied(delegator,paymentEntry.paymentId);
				if(paymentNotApplied.compareTo(BigDecimal.ZERO)==0){
					resultList.add(paymentEntry);
				}
			}
			if(parentTypeId.equals("RECEIPT")){
			context.paymentList=resultList;
			}else if(parentTypeId.equals("DISBURSEMENT")){
			context.listIt=resultList;
			}
		}
	}