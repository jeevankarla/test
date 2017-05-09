import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;


paymentList = [];
finAccountTransIds = [];
isFormSubmitted = parameters.isFormSubmitted;
if(isFormSubmitted && "Y".equals(isFormSubmitted)){
	
	instrumentNo = parameters.instrumentNo;Debug.log("ins no===="+instrumentNo);
	if(instrumentNo){
		paymentList = delegator.findList("Payment", EntityCondition.makeCondition("paymentRefNum", EntityOperator.EQUALS, instrumentNo), UtilMisc.toSet("finAccountTransId"), null, null, false);
		finAccountTransIds = EntityUtil.getFieldListFromEntityList(paymentList, "finAccountTransId", true);
		finAccountTransList = EntityUtil.filterByCondition(finAccountTransList, EntityCondition.makeCondition("finAccountTransId",EntityOperator.IN ,finAccountTransIds));
		grandTotal = BigDecimal.ZERO;
		createdGrandTotal = BigDecimal.ZERO;
		totalCreatedTransactions = BigDecimal.ZERO;
		totalApprovedTransactions = BigDecimal.ZERO; 
		approvedGrandTotal = BigDecimal.ZERO;
		totalCreatedApprovedTransactions = BigDecimal.ZERO;
		glReconciliationApprovedGrandTotal = BigDecimal.ZERO;
		
		for(GenericValue finAccountTransEntry : finAccountTransList){
			if(finAccountTransEntry.finAccountTransTypeId == "WITHDRAWAL"){
				grandTotal = grandTotal.subtract(finAccountTransEntry.amount);
				createdApprovedGrandTotal = createdApprovedGrandTotal.subtract(finAccountTransEntry.amount);
				
				if(finAccountTransEntry.statusId == "FINACT_TRNS_CREATED"){
					createdGrandTotal =  createdGrandTotal.subtract(finAccountTransEntry.amount);
					totalCreatedTransactions = totalCreatedTransactions.add(new BigDecimal("1"));
				}
				if(finAccountTransEntry.statusId == "FINACT_TRNS_APPROVED"){
					approvedGrandTotal =  approvedGrandTotal.subtract(finAccountTransEntry.amount);
					glReconciliationApprovedGrandTotal = glReconciliationApprovedGrandTotal.subtract(finAccountTransEntry.amount);
					totalApprovedTransactions = totalApprovedTransactions.add(new BigDecimal("1"));
				}
			}
			else{
				grandTotal = grandTotal.add(finAccountTrans.amount);
				createdApprovedGrandTotal = createdApprovedGrandTotal.add(finAccountTransEntry.amount);
				createdGrandTotal =  createdGrandTotal.add(finAccountTransaction.amount);
				if(finAccountTransEntry.statusId == "FINACT_TRNS_CREATED"){
					createdGrandTotal =  createdGrandTotal.add(finAccountTransaction.amount);
					totalCreatedTransactions = totalCreatedTransactions.add(new BigDecimal("1"));
				}
				if(finAccountTransEntry.statusId == "FINACT_TRNS_APPROVED"){
					approvedGrandTotal =  approvedGrandTotal.add(finAccountTransEntry.amount);
					glReconciliationApprovedGrandTotal = glReconciliationApprovedGrandTotal.add(finAccountTransEntry.amount);
					totalApprovedTransactions = totalApprovedTransactions.add(new BigDecimal("1"));
				}
			}
		}
		if(parameters.openingBalance){
			openingBalance = parameters.openingBalance;
			glReconciliationApprovedGrandTotal = glReconciliationApprovedGrandTotal.add(openingBalance);
		}
		totalCreatedApprovedTransactions = totalCreatedApprovedTransactions.add(totalCreatedTransactions);
		totalCreatedApprovedTransactions = totalCreatedApprovedTransactions.add(totalApprovedTransactions);
		
		context.finAccountTransList = finAccountTransList;
		context.grandTotal = grandTotal;
		context.createdGrandTotal = createdGrandTotal;
		context.approvedGrandTotal = approvedGrandTotal;
		context.totalCreatedTransactions = totalCreatedTransactions;
		context.totalApprovedTransactions = totalApprovedTransactions;
		context.totalCreatedApprovedTransactions = totalCreatedApprovedTransactions;
		context.glReconciliationApprovedGrandTotal = glReconciliationApprovedGrandTotal;
	}
}
