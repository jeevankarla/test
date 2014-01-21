package org.ofbiz.accounting.budget;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

public class BudgetWorker {
    public static final String module = BudgetWorker.class.getName();
    
    
    public static BigDecimal getGlAccountBudgetAmount(Delegator delegator, String glAccountId, String organizationId) throws GenericEntityException {	
      	BigDecimal result = null;  	
    	List<GenericValue> itemRows = delegator.findList("InvoiceItemType", EntityCondition.makeCondition("defaultGlAccountId", EntityOperator.EQUALS, glAccountId), null, null, null, false);
    	if (itemRows != null) { 
    		for (GenericValue itemRow : itemRows) {
    			String invoiceItemTypeId = itemRow.getString("invoiceItemTypeId");
    			BigDecimal itemAmount = getInvoiceItemTypeGlBudgetAmount(delegator, invoiceItemTypeId, organizationId);
    			if (itemAmount != null) {
    				if (result == null)
    				{
    					result = itemAmount;
    				}
    				else {
    					result = result.add(itemAmount);
    				}
    			}
    		}  
    	}
    	return result;
	}    
    
    public static BigDecimal getInvoiceItemTypeGlBudgetAmount(Delegator delegator, String invoiceItemTypeId, String organizationId) throws GenericEntityException {	
		List conditionList = UtilMisc.toList(
                EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, invoiceItemTypeId));
        conditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationId));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
    	List<GenericValue> budgetItems = delegator.findList("BudgetItemBudget", condition, null, null, null, false);   
   		if (budgetItems != null) {
   			for (GenericValue budgetItem : budgetItems) {
   				String timePeriodId = budgetItem.getString("customTimePeriodId");
                GenericValue budgetTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", timePeriodId), false);	 				
                if (budgetTimePeriod != null && (budgetTimePeriod.getDate("fromDate").before(UtilDateTime.nowDate()) && 
                		budgetTimePeriod.getDate("thruDate").after(UtilDateTime.nowDate()))) {
                	BigDecimal amount = budgetItem.getBigDecimal("amount");
                	Debug.logInfo("Budget amount for [" + invoiceItemTypeId + ',' + organizationId + "] = " + amount.doubleValue(), module);    		
                	return amount;
                }
			}
		}
    	return null;
	}
    
    public static BigDecimal getInvoiceItemTypeGlPostedBalance(Delegator delegator, String invoiceItemTypeId, String organizationId) throws GenericEntityException {	
    	BigDecimal result = BigDecimal.ZERO;
    	GenericValue itemTypeRow = delegator.findOne("InvoiceItemType", UtilMisc.toMap(
    			"invoiceItemTypeId", invoiceItemTypeId), false);
        if (itemTypeRow != null) {
            String glAccountId = itemTypeRow.getString("defaultGlAccountId");
            if (glAccountId != null) {    	
            	GenericValue row = delegator.findOne("GlAccountOrganization", UtilMisc.toMap(
            			"glAccountId", glAccountId,"organizationPartyId", organizationId), false);
            	if (row != null) {
            		result = row.getBigDecimal("postedBalance");
            	}
            }
        }
        Debug.logInfo("getInvoiceItemTypeGlPostedBalance for [" + invoiceItemTypeId + "," + organizationId + "] =" + result.doubleValue(), module);    		            	                
    	return result;
	}    

    public static BigDecimal getInvoiceItemTypeGlUnpostedBalance(Delegator delegator, String invoiceItemTypeId, String organizationId) throws GenericEntityException {	
    	BigDecimal result = BigDecimal.ZERO;
    	GenericValue itemTypeRow = delegator.findOne("InvoiceItemType", UtilMisc.toMap(
    			"invoiceItemTypeId", invoiceItemTypeId), false);
        if (itemTypeRow != null) {
            String glAccountId = itemTypeRow.getString("defaultGlAccountId");
            if (glAccountId != null) {    	
            	List conditionList = UtilMisc.toList(
            			EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
            	conditionList.add(EntityCondition.makeCondition("organizationId", EntityOperator.EQUALS, organizationId));
                conditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"), EntityOperator.AND, 
                		EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "INVOICE_READY")));            	
            	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      	
            	List<GenericValue> itemRows = delegator.findList("InvoiceItemInvoiceItemTypeInvoice", condition, null, null, null, false);
            	if (itemRows != null) {
            		for (int i = 0; i < itemRows.size(); ++i) {
            			BigDecimal quantity = itemRows.get(i).getBigDecimal("quantity");
            			BigDecimal amount = itemRows.get(i).getBigDecimal("amount");    
            			BigDecimal itemAmount = quantity.multiply(amount);
            			result = result.add(itemAmount);
            		}            	
            	}
            }
        }
		Debug.logInfo("getInvoiceItemTypeGlUnpostedBalance amount=" + result.doubleValue(), module);    		            	            			            		        
    	return result;
	}    
    
    public static boolean isBudgetEnabled(Delegator delegator) throws GenericEntityException {
		GenericValue budgetEnabled = delegator.findByPrimaryKeyCache("TenantConfiguration",
				UtilMisc.toMap("propertyTypeEnumId", "ACCOUNT_BUDGET", "propertyName", "enableBudget"));
		if (budgetEnabled != null) {
			return budgetEnabled.getBoolean("propertyValue").booleanValue();
		}
        return false;
    }    

    public static boolean isBudgetEnforced(Delegator delegator) throws GenericEntityException {
    	if (isBudgetEnabled(delegator)) {
    		GenericValue budgetEnforced = delegator.findByPrimaryKeyCache("TenantConfiguration",
    				UtilMisc.toMap("propertyTypeEnumId", "ACCOUNT_BUDGET", "propertyName", "enforceBudget"));
    		if (budgetEnforced != null) {
    			return budgetEnforced.getBoolean("propertyValue").booleanValue();
    		}
    	}
        return false;
    }        
}
