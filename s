[33mcommit a2a542317411af7a59801f02597174241e509ba1[m
Author: jeevan <jeevan@vasista.in>
Date:   Mon May 15 14:23:21 2017 +0530

    updated

[1mdiff --git a/applications/accounting/build/lib/ofbiz-accounting.jar b/applications/accounting/build/lib/ofbiz-accounting.jar[m
[1mindex 1138d2213..b93c1db5b 100644[m
Binary files a/applications/accounting/build/lib/ofbiz-accounting.jar and b/applications/accounting/build/lib/ofbiz-accounting.jar differ
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/actions/invoice/BulkFindInvoices.groovy b/applications/accounting/webapp/accounting/WEB-INF/actions/invoice/BulkFindInvoices.groovy[m
[1mindex 48ca936d0..18472b898 100644[m
[1m--- a/applications/accounting/webapp/accounting/WEB-INF/actions/invoice/BulkFindInvoices.groovy[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/actions/invoice/BulkFindInvoices.groovy[m
[36m@@ -41,7 +41,6 @@[m [mcontext.thruDate=thruDate;[m
 Timestamp fromdate1=null;[m
 Timestamp thrudate1=null;[m
 [m
[31m-Debug.log("Date======"+parameters.ownerPartyId+userLogin);[m
 if(UtilValidate.isNotEmpty(fromDate)||UtilValidate.isNotEmpty(thruDate)){[m
 	def sdf = new SimpleDateFormat("yy-MM-dd");[m
 	[m
[36m@@ -84,7 +83,7 @@[m [mif(invoiceTypeId){[m
 	conditionList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS,invoiceTypeId));[m
 }[m
 conditionList.add(EntityCondition.makeCondition("invoiceId", EntityOperator.NOT_LIKE,"%OB%"));[m
[31m-conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "INVOICE_READY","INVOICE_PAID"]));[m
[32m+[m[32mconditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["INVOICE_READY","INVOICE_PAID"]));[m[41m[m
 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);[m
 Debug.log("condi====="+cond);[m
 invoiceItemList = delegator.findList("InvoiceAndType", cond, null, null, null, false);[m
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkChequeOfPayments.groovy b/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkChequeOfPayments.groovy[m
[1mindex 65f8b94ff..a10fba92e 100644[m
[1m--- a/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkChequeOfPayments.groovy[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkChequeOfPayments.groovy[m
[36m@@ -21,7 +21,7 @@[m [mcontext.thruDate=thruDate;[m
 Timestamp fromdate1=null;[m
 Timestamp thrudate1=null;[m
 [m
[31m-Debug.log("Date======"+parameters.ownerPartyId);[m
[32m+[m[32m//Debug.log("Date======"+parameters.ownerPartyId);[m[41m[m
 if(UtilValidate.isNotEmpty(fromDate)||UtilValidate.isNotEmpty(thruDate)){[m
 	def sdf = new SimpleDateFormat("yy-MM-dd");[m
 	[m
[36m@@ -56,7 +56,8 @@[m [mif(thrudate1){[m
 	conditionList.add(EntityCondition.makeCondition("paymentDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDateEnd)));[m
 }[m
 conditionList.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.IN, ["CHEQUE_PAYOUT","FT_PAYOUT"]));[m
[31m-conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "PMNT_RECEIVED","PMNT_PAID"]));[m
[32m+[m[32mconditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.LIKE, "%_PAYOUT"));[m[41m[m
[32m+[m[32mconditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["PMNT_CONFIRMED","PMNT_PAID"]));[m[41m[m
 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);[m
 Debug.log("condi====="+cond);[m
 paymentList = delegator.findList("Payment", cond, null, null, null, false);[m
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkFindPayments.groovy b/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkFindPayments.groovy[m
[1mindex e1cfec70a..3a1add8a0 100644[m
[1m--- a/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkFindPayments.groovy[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/actions/payment/BulkFindPayments.groovy[m
[36m@@ -65,7 +65,7 @@[m [mif(paymentTypeId){[m
 	conditionList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, paymentTypeId));[m
 }[m
 [m
[31m-conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "PMNT_RECEIVED","PMNT_PAID","PMNT_SENT"]));[m
[32m+[m[32mconditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, [ "PMNT_RECEIVED","PMNT_PAID","PMNT_CONFIRMED"]));[m[41m[m
 cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);[m
 Debug.log("condi====="+cond);[m
 paymentList = delegator.findList("Payment", cond, null, null, null, false);[m
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/actions/reports/accountingTransAndFinAccountTrans1.groovy b/applications/accounting/webapp/accounting/WEB-INF/actions/reports/accountingTransAndFinAccountTrans1.groovy[m
[1mindex 0e1c28961..b9cd3d107 100644[m
[1m--- a/applications/accounting/webapp/accounting/WEB-INF/actions/reports/accountingTransAndFinAccountTrans1.groovy[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/actions/reports/accountingTransAndFinAccountTrans1.groovy[m
[36m@@ -286,6 +286,8 @@[m [mif(UtilValidate.isNotEmpty(parameters.reportTypeFlag)){[m
 }[m
 context.partyIdForAdd=partyIdForAdd;[m
 [m
[32m+[m[32mDebug.log("partyIdForAddress===="+partyIdForAdd);[m[41m[m
[32m+[m[41m[m
 /*invSequenceNum = "";[m
 [m
 if(parameters.invoiceId){[m
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/actions/reports/paymentAccountingTrans1.groovy b/applications/accounting/webapp/accounting/WEB-INF/actions/reports/paymentAccountingTrans1.groovy[m
[1mnew file mode 100644[m
[1mindex 000000000..3f19e130d[m
[1m--- /dev/null[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/actions/reports/paymentAccountingTrans1.groovy[m
[36m@@ -0,0 +1,156 @@[m
[32m+[m[32mimport org.ofbiz.base.util.UtilDateTime;[m[41m[m
[32m+[m[32mimport org.ofbiz.base.util.UtilMisc;[m[41m[m
[32m+[m[32mimport org.ofbiz.entity.GenericValue;[m[41m[m
[32m+[m[32mimport org.ofbiz.entity.condition.EntityCondition;[m[41m[m
[32m+[m[32mimport org.ofbiz.entity.condition.EntityOperator;[m[41m[m
[32m+[m[32mimport org.ofbiz.entity.util.EntityUtil;[m[41m[m
[32m+[m[32mimport org.ofbiz.accounting.util.UtilAccounting;[m[41m[m
[32m+[m[32mimport org.ofbiz.party.party.PartyWorker;[m[41m[m
[32m+[m[32mimport org.ofbiz.base.util.*[m[41m[m
[32m+[m[32mimport org.ofbiz.minilang.SimpleMapProcessor[m[41m[m
[32m+[m[32mimport org.ofbiz.content.ContentManagementWorker[m[41m[m
[32m+[m[32mimport org.ofbiz.content.content.ContentWorker[m[41m[m
[32m+[m[32mimport org.ofbiz.content.data.DataResourceWorker[m[41m[m
[32m+[m[32mimport java.sql.Date;[m[41m[m
[32m+[m[32mimport java.sql.Timestamp;[m[41m[m
[32m+[m[32mimport org.ofbiz.accounting.util.UtilAccounting;[m[41m[m
[32m+[m[41m[m
[32m+[m[32mimport javolution.util.FastList;[m[41m[m
[32m+[m[41m[m
[32m+[m[32macountingTransEntriesMap=[:];[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(paymentIds)){[m[41m[m
[32m+[m	[32mpaymentIds1=paymentIds;[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mDebug.log("paymentIds===="+paymentIds1);[m[41m[m
[32m+[m[32mfor(paymentId in paymentIds1){[m[41m[m
[32m+[m	[32macctgTransId = "";[m[41m[m
[32m+[m	[32maccountingTransEntryList = [];[m[41m[m
[32m+[m	[32maccountingTransEntries = [:];[m[41m[m
[32m+[m[41m[m
[32m+[m[32mconditionList=[];[m[41m[m
[32m+[m[32m//finding on AcctgTrans for payment[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(paymentId)){[m[41m[m
[32m+[m	[32m//for paymentId[m[41m[m
[32m+[m	[32mif(UtilValidate.isNotEmpty(paymentId)){[m[41m[m
[32m+[m		[32mconditionList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS,paymentId));[m[41m[m
[32m+[m	[32m}[m[41m[m
[32m+[m	[32mconditionAcctgTrans = EntityCondition.makeCondition(conditionList, EntityOperator.AND);[m[41m[m
[32m+[m	[32m//finding on AcctgTrans[m[41m[m
[32m+[m	[32macctgTransList = delegator.findList("AcctgTrans",conditionAcctgTrans , null, null, null, false );[m[41m[m
[32m+[m	[32mif(UtilValidate.isNotEmpty(acctgTransList)){[m[41m[m
[32m+[m		[32macctgTrans = acctgTransList[0];[m[41m[m
[32m+[m		[32mif(UtilValidate.isNotEmpty(acctgTrans)){[m[41m[m
[32m+[m			[32macctgTransId = acctgTrans.acctgTransId[m[41m[m
[32m+[m		[32m}[m[41m[m
[32m+[m	[32m}[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mpartyIdForAdd="";[m[41m[m
[32m+[m[32mDebug.log("paymentIds===="+parameters.invoiceId);[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(parameters.invoiceId)){[m[41m[m
[32m+[m[32minvoice = delegator.findOne("Invoice",[invoiceId : parameters.invoiceId] , false);[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(invoice)){[m[41m[m
[32m+[m	[32mif(invoice.invoiceTypeId=="ADMIN_OUT" || invoice.invoiceTypeId=="PURCHASE_INVOICE"){[m[41m[m
[32m+[m		[32mpartyIdForAdd=invoice.partyId;[m[41m[m
[32m+[m[41m		[m
[32m+[m	[32m}[m[41m[m
[32m+[m	[32melse if(invoice.invoiceTypeId=="MIS_INCOME_IN" || invoice.invoiceTypeId=="SALES_INVOICE"){[m[41m[m
[32m+[m		[32mpartyIdForAdd=invoice.partyIdFrom;[m[41m[m
[32m+[m[41m		[m
[32m+[m	[32m}[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[41m[m
[32m+[m[32mnewList = [];[m[41m[m
[32m+[m[32mFinAccountTransList = [];[m[41m[m
[32m+[m[32mnewList = delegator.findList("FinAccountTrans",EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS , accountingTransEntries.finAccountTransId)  , null, null, null, false );[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(newList)){[m[41m[m
[32m+[m	[32mFinAccountTransList = EntityUtil.getFirst(newList);[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mcontext.put("FinAccountTransList",FinAccountTransList);[m[41m[m
[32m+[m[41m[m
[32m+[m[41m[m
[32m+[m[32mDebug.log("paymentId======"+paymentId);[m[41m[m
[32m+[m[32mpaymentDetails = delegator.findOne("Payment",[paymentId : paymentId] , false);[m[41m[m
[32m+[m[32mif (UtilAccounting.isReceipt(paymentDetails)) {[m[41m[m
[32m+[m	[32mpartyIdForAdd=paymentDetails.partyIdTo;[m[41m[m
[32m+[m	[32mcontext.partyIdForAdd=partyIdForAdd;[m[41m[m
[32m+[m[32m}else{[m[41m[m
[32m+[m	[32mpartyIdForAdd=paymentDetails.partyIdFrom;[m[41m[m
[32m+[m	[32mcontext.partyIdForAdd=partyIdForAdd;[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[41m[m
[32m+[m[32mGenericValue finAccntTransSequenceEntry;[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(acctgTransId)){[m[41m[m
[32m+[m	[32maccountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : acctgTransId] , false);[m[41m[m
[32m+[m	[32mfinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, accountingTransEntries.finAccountTransId), null, null, null, false));[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mfinAccntTransSequence = "";[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(finAccntTransSequenceEntry)){[m[41m[m
[32m+[m	[32mfinAccntTransSequence = finAccntTransSequenceEntry.transSequenceNo;[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mcontext.finAccntTransSequence = finAccntTransSequence;[m[41m[m
[32m+[m[32mcontext.put("accountingTransEntries",accountingTransEntries);[m[41m[m
[32m+[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(acctgTransId)){[m[41m[m
[32m+[m	[32maccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , acctgTransId)  , null, null, null, false );[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32mcontext.put("accountingTransEntryList",accountingTransEntryList);[m[41m[m
[32m+[m[41m[m
[32m+[m[32m//for Deposit[m[41m[m
[32m+[m[32mpayAcctgTransId = "";[m[41m[m
[32m+[m[32mpayAccountingTransEntryList = [];[m[41m[m
[32m+[m[32mpayAccountingTransEntries = [:];[m[41m[m
[32m+[m[32mconditionList.clear();[m[41m[m
[32m+[m[32m//finding on AcctgTrans for payment[m[41m[m
[32m+[m[41m[m
[32m+[m[32mentryList=[];[m[41m[m
[32m+[m[32mfinalMap=[:];[m[41m[m
[32m+[m[32mif(UtilValidate.isNotEmpty(acctgTransList) && acctgTransList.size()>0){[m[41m[m
[32m+[m	[32mfor(int i=1 ; i < (acctgTransList.size()); i++){[m[41m[m
[32m+[m	[32macctgTrans = acctgTransList[i];[m[41m[m
[32m+[m	[32mtransType = "";[m[41m[m
[32m+[m	[32mif(UtilValidate.isNotEmpty(acctgTrans)){[m[41m[m
[32m+[m		[32mpayAcctgTransId = acctgTrans.acctgTransId[m[41m[m
[32m+[m		[32mGenericValue payFinAccntTransSequenceEntry;[m[41m[m
[32m+[m		[32mif(UtilValidate.isNotEmpty(payAcctgTransId) && (payAcctgTransId != acctgTransId)){[m[41m[m
[32m+[m			[32mpayAccountingTransEntries = delegator.findOne("AcctgTrans",[acctgTransId : payAcctgTransId] , false);[m[41m[m
[32m+[m			[32mpayFinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, payAccountingTransEntries.finAccountTransId), null, null, null, false));[m[41m[m
[32m+[m			[32mtransType = payAccountingTransEntries.acctgTransTypeId;[m[41m[m
[32m+[m			[32m}[m[41m[m
[32m+[m		[32mentryList.addAll(payAccountingTransEntries);[m[41m[m
[32m+[m		[32mpayFinAccntTransSequence = "";[m[41m[m
[32m+[m		[32mif(UtilValidate.isNotEmpty(payFinAccntTransSequenceEntry)){[m[41m[m
[32m+[m			[32mpayFinAccntTransSequence = payFinAccntTransSequenceEntry.transSequenceNo;[m[41m[m
[32m+[m		[32m}[m[41m[m
[32m+[m		[32mcontext.payFinAccntTransSequence = payFinAccntTransSequence;[m[41m[m
[32m+[m		[32mcontext.put("payAccountingTransEntries",payAccountingTransEntries);[m[41m[m
[32m+[m[41m		[m
[32m+[m		[32mif(UtilValidate.isNotEmpty(payAcctgTransId)){[m[41m[m
[32m+[m			[32mpayAccountingTransEntryList = delegator.findList("AcctgTransEntry",EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS , payAcctgTransId)  , null, null, null, false );[m[41m[m
[32m+[m			[32m}[m[41m[m
[32m+[m			[32mfinalMap.put(transType,payAccountingTransEntryList);[m[41m[m
[32m+[m		[32m}[m[41m[m
[32m+[m	[32m}[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[32m//for display of sequence and Bank Name in AccountingReport in Payment overview[m[41m[m
[32m+[m[32mfinAccntTransSequenceEntry = EntityUtil.getFirst(delegator.findList("FinAccntTransSequence", EntityCondition.makeCondition("finAccountTransId", EntityOperator.EQUALS, paymentDetails.finAccountTransId), null, null, null, false));[m[41m[m
[32m+[m[32mfinAccountId="";[m[41m[m
[32m+[m[32mfinAccount="";[m[41m[m
[32m+[m[32mBankName="";[m[41m[m
[32m+[m[32mif(finAccntTransSequenceEntry){[m[41m[m
[32m+[m	[32mfinAccntTransSequence = finAccntTransSequenceEntry.transSequenceId;[m[41m[m
[32m+[m	[32mfinAccountId=finAccntTransSequenceEntry.finAccountId;[m[41m[m
[32m+[m	[32mif(finAccountId){[m[41m[m
[32m+[m			[32mfinAccount=delegator.findOne("FinAccount",[finAccountId : finAccountId] , false);[m[41m[m
[32m+[m				[32mif(finAccount.finAccountName){[m[41m[m
[32m+[m					[32mBankName=finAccount.finAccountName;[m[41m[m
[32m+[m				[32m}[m[41m	[m
[32m+[m	[32m}[m[41m[m
[32m+[m[32m}[m[41m[m
[32m+[m[41m[m
[32m+[m[32mcontext.finAccntTransSequence = finAccntTransSequence;[m[41m[m
[32m+[m[32mcontext.BankName=BankName;[m[41m[m
[32m+[m[32mcontext.put("finalMap",finalMap);[m[41m[m
[32m+[m[32mcontext.put("entryList",entryList);[m[41m[m
[32m+[m[32macountingTransEntriesMap(paymentId,)[m[41m[m
[32m+[m[32m}[m[41m[m
[1mdiff --git a/applications/accounting/webapp/accounting/WEB-INF/controller.xml b/applications/accounting/webapp/accounting/WEB-INF/controller.xml[m
[1mindex 0201dfa6f..539907aed 100755[m
[1m--- a/applications/accounting/webapp/accounting/WEB-INF/controller.xml[m
[1m+++ b/applications/accounting/webapp/accounting/WEB-INF/controller.xml[m
[36m@@ -4318,6 +4318,10 @@[m [munder the License.[m
         <security https="true" auth="true"/>[m
         <response name="success" type="view" value="PaymentAccountingTransPdf"/>[m
     </request-map>[m
[32m+[m[32m    <request-map uri="paymentAccountingTrans1.pdf">[m
[32m+[m[32m        <security https="true" auth="true"/>[m
[32m+[m[32m        <response name="success" type="view" value="PaymentAccountingTransPdf1"/>[m
[32m+[m[32m    </request-map>[m
     <request-map uri="depRctAcctgTransReportPdf.pdf">[m
         <security https="true" auth="true"/>[m
         <response name="success" type="view" value="DepRctAcctgTransReportPdf"/>[m
[36m@@ -5035,6 +5039,8 @@[m [munder the License.[m
    <view-map name="OGAPCreateInvoiceForEmployee" type="screen" page="component://accounting/widget/TreasurerPortalScreens.xml#OGAPCreateInvoiceForEmployee"/>[m
    <view-map name="EmpOGAPCreateInvoiceForPayment" type="screen" page="component://accounting/widget/TreasurerPortalScreens.xml#EmpOGAPCreateInvoiceForPayment"/>[m
    <view-map name="PaymentAccountingTransPdf" type="screenfop" page="component://accounting/widget/GlScreens.xml#PaymentAccountingTransPdf" content-type="application/pdf" encoding="none"/>[m
[32m+[m[32m   <view-map name="PaymentAccountingTransPdf1" type="screenfop" page="component://accounting/widget/GlScreens.xml#PaymentAccountingTransPdf1" content-type="application/pdf" encoding="none"/>[m
[32m+[m[41m   [m
    <view-map name="DepRctAcctgTransReportPdf" type="screenfop" page="component://accounting/widget/GlScreens.xml#DepRctAcctgTransReportPdf" content-type="application/pdf" encoding="none"/>[m
    <view-map name="FindGrantDevelopmentalBankAccountTab" type="screen" page="component://accounting/widget/TreasurerPortalScreens.xml#FindGrantDevelopmentalBankAccountTab"/>[m
    <view-map name="FindGrantImplementationBankAccountTab" type="screen" page="component://accounting/widget/TreasurerPortalScreens.xml#FindGrantImplementationBankAccountTab"/>[m
[1mdiff --git a/applications/accounting/webapp/accounting/payment/ListBulkChequesofPayments.ftl b/applications/accounting/webapp/accounting/payment/ListBulkChequesofPayments.ftl[m
[1mindex 81b426cf2..aa6309032 100644[m
[1m--- a/applications/accounting/webapp/accounting/payment/ListBulkChequesofPayments.ftl[m
[1m+++ b/applications/accounting/webapp/accounting/payment/ListBulkChequesofPayments.ftl[m
[36m@@ -25,30 +25,25 @@[m [munder the License.[m
         jQuery.each(payments, function() {[m
             this.checked = master.checked;[m
         });[m
[31m-        getInvoiceRunningTotal();[m
[31m-    }[m
[31m-[m
[31m-    function getInvoiceRunningTotal() {[m
[31m-		var checkedInvoices = jQuery("input[name='invoiceIds']:checked");[m
[31m-        if(checkedInvoices.size() > 0) {[m
[31m-            jQuery.ajax({[m
[31m-                url: 'getInvoiceRunningTotal',[m
[31m-                type: 'POST',[m
[31m-                async: true,[m
[31m-                data: jQuery('#listInvoices').serialize(),[m
[31m-                success: function(data) { jQuery('#showInvoiceRunningTotal').html(data.invoiceRunningTotal + '  (' + checkedInvoices.size() + ')') }[m
[31m-            });[m
[31m-[m
[31m-            if(jQuery('#serviceName').val() != "") {[m
[31m-            	jQuery('#submitButton').removeAttr('disabled');                [m
[31m-            }[m
[31m-[m
[31m-        } else {[m
[31m-            jQuery('#submitButton').attr('disabled', 'disabled');[m
[31m-            jQuery('#showInvoiceRunningTotal').html("");[m
[31m-        }[m
[32m+[m[41m       [m
[32m+[m[32m        calculateTotal();[m[41m[m
     }[m
 [m
[32m+[m[32m     function calculateTotal(){[m[41m[m
[32m+[m[32m     var invoices = jQuery("#listPayments :checkbox[name='paymentIds']");[m[41m[m
[32m+[m[41m  [m	[32m total=0;[m[41m[m
[32m+[m[32m     jQuery.each(invoices, function() {[m[41m[m
[32m+[m[32m         if (jQuery(this).is(':checked')) {[m[41m[m
[32m+[m[41m         [m	[32mvar domObj = $(this).parent().parent();[m[41m[m
[32m+[m[41m         [m	[32mvar amtObj = $(domObj).find("#amt");[m[41m[m
[32m+[m[41m         [m	[32mvar amt = $(amtObj).val();[m[41m[m
[32m+[m[41m         [m	[32m total = (+total) + (+amt);[m[41m[m
[32m+[m[32m         }[m[41m[m
[32m+[m[41m         [m
[32m+[m[32m     });[m[41m[m
[32m+[m[32m     jQuery('#showPaymentRunningTotal').html(total);[m[41m[m
[32m+[m[32m }[m[41m[m
[32m+[m[41m	[m
     function setServiceName(selection) {[m
         jQuery('#submitButton').attr('disabled' , 'disabled');    [m
         if ( selection.value == 'massPaymentsToSent' || selection.value == 'massPaymentsToCancel' || selection.value == 'massPaymentsToVoid' || selection.value == 'massPaymentsToReceived') {[m
[36m@@ -107,29 +102,22 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
     [m
     }[m
 </script>[m
[31m-<#if !paymentList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>[m
[32m+[m[32m<#if paymentList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>[m[41m[m
   <div>[m
[31m-    <span class="label">Total Payments :${paymentList?size}</span>  [m
[32m+[m[32m    <span class="label">Total Payments :${paymentList?size}</span> <span class="label">Selected Payments Total:</span>[m[41m [m
[32m+[m[32m     <span class="label" id="showPaymentRunningTotal"></span>[m[41m[m
   </div>[m
[31m-  [m
[31m-  <#if isCashierPortalScreen?has_content>[m
[31m-    <form name="cancelPayment" id="cancelPayment"  method="post" action="voidCashPayment">[m
[31m-  <#else>[m
[31m-   <form name="cancelPayment" id="cancelPayment"  method="post" action="voidPayment">[m
[31m-  </#if>[m
[31m- [m
[31m-  </form>[m
   <form name="listPayments" id="listPayments"  method="post" action="">[m
     <div align="right">[m
[31m-   <!--   <select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">[m
[32m+[m[41m [m	[32m   <select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">[m[41m[m
         <option value="">${uiLabelMap.AccountingSelectAction}</option>[m
[31m-        <option value="<@ofbizUrl>PrintPayments</@ofbizUrl>">Print Payments</option>[m
[31m-        <option value="massPaymentsToSent">Status To 'Sent'</option>[m
[32m+[m[32m        <option value="<@ofbizUrl>printChecks.pdf</@ofbizUrl>">Print Cheques</option>[m[41m[m
[32m+[m[32m        <#-- <option value="massPaymentsToSent">Status To 'Sent'</option>[m[41m[m
         <option value="massPaymentsToVoid">Status To 'Void'</option>[m
         <option value="massPaymentsToCancel">Status To 'Cancelled'</option>[m
[31m-        <option value="massPaymentsToReceived">Status To 'Received'</option>[m
[32m+[m[32m        <option value="massPaymentsToReceived">Status To 'Received'</option> -->[m[41m[m
       </select>[m
[31m-      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listPayments').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" /> -->[m
[32m+[m[32m      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listPayments').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" />[m[41m [m
       <input type="hidden" name="organizationPartyId" value="${defaultOrganizationPartyId}"/>[m
       <input type="hidden" name="partyIdFrom" value="${parameters.partyIdFrom?if_exists}"/>[m
       <input type="hidden" name="statusId" id="statusId" value="${parameters.statusId?if_exists}"/>[m
[36m@@ -137,7 +125,8 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
       <input type="hidden" name="thruInvoiceDate" value="${parameters.thruInvoiceDate?if_exists}"/>[m
       <input type="hidden" name="fromDueDate" value="${parameters.fromDueDate?if_exists}"/>[m
       <input type="hidden" name="thruDueDate" value="${parameters.thruDueDate?if_exists}"/>[m
[31m-      <input type="hidden" name="paymentStatusChange" id="paymentStatusChange" value="<@ofbizUrl>massChangePaymentStatus</@ofbizUrl>"/>[m
[32m+[m[41m      [m
[32m+[m[32m     <#-- <input type="hidden" name="paymentStatusChange" id="paymentStatusChange" value="<@ofbizUrl>massChangePaymentStatus</@ofbizUrl>"/> -->[m[41m [m
     </div>[m
 [m
     <table class="basic-table hover-bar" cellspacing="0">[m
[36m@@ -202,7 +191,7 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
               <td><#if payment.effectiveDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"dd/MM/yyyy")}</#if></td>              [m
               <td><@ofbizCurrency amount=payment.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>[m
               <td><@ofbizCurrency amount=amountToApply isoCode=defaultOrganizationPartyCurrencyUomId/></td>[m
[31m-             [m
[32m+[m[32m              <input type = "hidden" name = "amt" id = "amt" value = "${amountToApply}">[m[41m[m
               <#if hasPaymentCancelPermission?has_content && nowDate?has_content && payment.effectiveDate?has_content>[m
               <#assign paymentDateCompare= Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"yyyy-MM-dd")>[m
               <td>[m
[36m@@ -219,7 +208,7 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
               </td>[m
                [m
              [m
[31m-               <td align="right"><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}" onclick="javascript:getInvoiceRunningTotal();"/></td> [m
[32m+[m[32m               <td align="right"><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}" onclick="javascript:calculateTotal();"/></td>[m[41m [m
             </tr>[m
             <#-- toggle the row color -->[m
             <#assign alt_row = !alt_row>[m
[1mdiff --git a/applications/accounting/webapp/accounting/payment/ListBulkPayments.ftl b/applications/accounting/webapp/accounting/payment/ListBulkPayments.ftl[m
[1mindex c714b0a15..2ef13401c 100644[m
[1m--- a/applications/accounting/webapp/accounting/payment/ListBulkPayments.ftl[m
[1m+++ b/applications/accounting/webapp/accounting/payment/ListBulkPayments.ftl[m
[36m@@ -25,29 +25,23 @@[m [munder the License.[m
         jQuery.each(payments, function() {[m
             this.checked = master.checked;[m
         });[m
[31m-        getInvoiceRunningTotal();[m
[31m-    }[m
[31m-[m
[31m-    function getInvoiceRunningTotal() {[m
[31m-		var checkedInvoices = jQuery("input[name='invoiceIds']:checked");[m
[31m-        if(checkedInvoices.size() > 0) {[m
[31m-            jQuery.ajax({[m
[31m-                url: 'getInvoiceRunningTotal',[m
[31m-                type: 'POST',[m
[31m-                async: true,[m
[31m-                data: jQuery('#listInvoices').serialize(),[m
[31m-                success: function(data) { jQuery('#showInvoiceRunningTotal').html(data.invoiceRunningTotal + '  (' + checkedInvoices.size() + ')') }[m
[31m-            });[m
[31m-[m
[31m-            if(jQuery('#serviceName').val() != "") {[m
[31m-            	jQuery('#submitButton').removeAttr('disabled');                [m
[31m-            }[m
[31m-[m
[31m-        } else {[m
[31m-            jQuery('#submitButton').attr('disabled', 'disabled');[m
[31m-            jQuery('#showInvoiceRunningTotal').html("");[m
[31m-        }[m
[32m+[m[32m        calculateTotal();[m[41m[m
     }[m
[32m+[m[32mfunction calculateTotal(){[m[41m[m
[32m+[m[32m     var invoices = jQuery("#listPayments :checkbox[name='paymentIds']");[m[41m[m
[32m+[m[41m  [m	[32m total=0;[m[41m[m
[32m+[m[32m     jQuery.each(invoices, function() {[m[41m[m
[32m+[m[32m         if (jQuery(this).is(':checked')) {[m[41m[m
[32m+[m[41m         [m	[32mvar domObj = $(this).parent().parent();[m[41m[m
[32m+[m[41m         [m	[32mvar amtObj = $(domObj).find("#amt");[m[41m[m
[32m+[m[41m         [m	[32mvar amt = $(amtObj).val();[m[41m[m
[32m+[m[41m         [m	[32m total = (+total) + (+amt);[m[41m[m
[32m+[m[32m         }[m[41m[m
[32m+[m[41m         [m
[32m+[m[32m     });[m[41m[m
[32m+[m[32m     jQuery('#showPaymentRunningTotal').html(total);[m[41m[m
[32m+[m[32m }[m[41m[m
[32m+[m[41m    [m
 [m
     function setServiceName(selection) {[m
         jQuery('#submitButton').attr('disabled' , 'disabled');    [m
[36m@@ -107,36 +101,22 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
     [m
     }[m
 </script>[m
[31m-<#if paymentList?has_content>[m
[31m-<#else>[m
[31m-<#if payments?has_content>[m
[31m-  <#assign paymentList  =  payments.getCompleteList() />[m
[31m-  <#assign eliClose = payments.close() />[m
[31m-</#if>[m
[31m-</#if>[m
 <#if paymentList?has_content && (parameters.noConditionFind)?if_exists == 'Y'>[m
   <div>[m
[31m-    <span class="label">Total Payments :${paymentList?size}</span>  [m
[32m+[m[32m    <span class="label">Total Payments :${paymentList?size}</span> <span class="label">Selected Payments Total:</span>[m[41m [m
[32m+[m[32m     <span class="label" id="showPaymentRunningTotal"></span>[m[41m [m
   </div>[m
[31m-  [m
[31m-  <#if isCashierPortalScreen?has_content>[m
[31m-    <form name="cancelPayment" id="cancelPayment"  method="post" action="voidCashPayment">[m
[31m-  <#else>[m
[31m-   <form name="cancelPayment" id="cancelPayment"  method="post" action="voidPayment">[m
[31m-  </#if>[m
[31m- [m
[31m-  </form>[m
   <form name="listPayments" id="listPayments"  method="post" action="">[m
     <div align="right">[m
[31m-   <!--   <select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">[m
[32m+[m[32m    <select name="serviceName" id="serviceName" onchange="javascript:setServiceName(this);">[m[41m[m
         <option value="">${uiLabelMap.AccountingSelectAction}</option>[m
[31m-        <option value="<@ofbizUrl>PrintPayments</@ofbizUrl>">Print Payments</option>[m
[31m-        <option value="massPaymentsToSent">Status To 'Sent'</option>[m
[32m+[m[32m        <option value="<@ofbizUrl>paymentAccountingTrans1.pdf</@ofbizUrl>">Accounting Report</option>[m[41m[m
[32m+[m[32m        <#--  <option value="massPaymentsToSent">Status To 'Sent'</option>[m[41m[m
         <option value="massPaymentsToVoid">Status To 'Void'</option>[m
         <option value="massPaymentsToCancel">Status To 'Cancelled'</option>[m
[31m-        <option value="massPaymentsToReceived">Status To 'Received'</option>[m
[32m+[m[32m        <option value="massPaymentsToReceived">Status To 'Received'</option>-->[m[41m[m
       </select>[m
[31m-      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listPayments').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" /> -->[m
[32m+[m[32m      <input id="submitButton" type="button"  onclick="javascript:jQuery('#listPayments').submit();" value="${uiLabelMap.CommonRun}" disabled="disabled" />[m[41m [m
       <input type="hidden" name="organizationPartyId" value="${defaultOrganizationPartyId}"/>[m
       <input type="hidden" name="partyIdFrom" value="${parameters.partyIdFrom?if_exists}"/>[m
       <input type="hidden" name="statusId" id="statusId" value="${parameters.statusId?if_exists}"/>[m
[36m@@ -144,7 +124,7 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
       <input type="hidden" name="thruInvoiceDate" value="${parameters.thruInvoiceDate?if_exists}"/>[m
       <input type="hidden" name="fromDueDate" value="${parameters.fromDueDate?if_exists}"/>[m
       <input type="hidden" name="thruDueDate" value="${parameters.thruDueDate?if_exists}"/>[m
[31m-      <input type="hidden" name="paymentStatusChange" id="paymentStatusChange" value="<@ofbizUrl>massChangePaymentStatus</@ofbizUrl>"/>[m
[32m+[m[32m      <#--  <input type="hidden" name="paymentStatusChange" id="paymentStatusChange" value="<@ofbizUrl>massChangePaymentStatus</@ofbizUrl>"/>-->[m[41m[m
     </div>[m
 [m
     <table class="basic-table hover-bar" cellspacing="0">[m
[36m@@ -209,7 +189,7 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
               <td><#if payment.effectiveDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"dd/MM/yyyy")}</#if></td>              [m
               <td><@ofbizCurrency amount=payment.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>[m
               <td><@ofbizCurrency amount=amountToApply isoCode=defaultOrganizationPartyCurrencyUomId/></td>[m
[31m-             [m
[32m+[m[32m             <input type = "hidden" name = "amt" id = "amt" value = "${amountToApply}">[m[41m[m
               <#if hasPaymentCancelPermission?has_content && nowDate?has_content && payment.effectiveDate?has_content>[m
               <#assign paymentDateCompare= Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.effectiveDate ,"yyyy-MM-dd")>[m
               <td>[m
[36m@@ -226,7 +206,7 @@[m [mfunction setVoidPaymentParameters(currentPayment){[m
               </td>[m
                [m
              [m
[31m-               <td align="right"><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}" onclick="javascript:getInvoiceRunningTotal();"/></td> [m
[32m+[m[32m               <td align="right"><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId}" onclick="javascript:calculateTotal();"/></td>[m[41m [m
             </tr>[m
             <#-- toggle the row color -->[m
             <#assign alt_row = !alt_row>[m
[1mdiff --git a/applications/accounting/webapp/accounting/reports/AccountingReportOverviewScreen1.fo.ftl b/applications/accounting/webapp/accounting/reports/AccountingReportOverviewScreen1.fo.ftl[m
[1mnew file mode 100644[m
[1mindex 000000000..17956e7e6[m
[1m--- /dev/null[m
[1m+++ b/applications/accounting/webapp/accounting/reports/AccountingReportOverviewScreen1.fo.ftl[m
[36m@@ -0,0 +1,1008 @@[m
[32m+[m[32m<#--[m[41m[m
[32m+[m[32mLicensed to the Apache Software Foundation (ASF) under one[m[41m[m
[32m+[m[32mor more contributor license agreements.  See the NOTICE file[m[41m[m
[32m+[m[32mdistributed with this work for additional information[m[41m[m
[32m+[m[32mregarding copyright ownership.  The ASF licenses this file[m[41m[m
[32m+[m[32mto you under the Apache License, Version 2.0 (the[m[41m[m
[32m+[m[32m"License"); you may not use this file except in compliance[m[41m[m
[32m+[m[32mwith the License.  You may obtain a copy of the License at[m[41m[m
[32m+[m[32mhttp://www.apache.org/licenses/LICENSE-2.0[m[41m[m
[32m+[m[32mUnless required by applicable law or agreed to in writing,[m[41m[m
[32m+[m[32msoftware distributed under the License is distributed on an[m[41m[m
[32m+[m[32m"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY[m[41m[m
[32m+[m[32mKIND, either express or implied.  See the License for the[m[41m[m
[32m+[m[32mspecific language governing permissions and limitations[m[41m[m
[32m+[m[32munder the License.[m[41m[m
[32m+[m[32m-->[m[41m[m
[32m+[m[41m[m
[32m+[m[32m<#escape x as x?xml>[m[41m[m
[32m+[m[32m<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">[m[41m[m
[32m+[m[41m[m
[32m+[m[32m<#-- do not display columns associated with values specified in the request, ie constraint values -->[m[41m[m
[32m+[m[32m<fo:layout-master-set>[m[41m[m
[32m+[m	[32m<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"[m[41m[m
[32m+[m[32m            margin-top="0.2in" margin-bottom=".3in" margin-left=".5in" margin-right=".1in">[m[41m[m
[32m+[m[32m        <fo:region-body margin-top="1in"/>[m[41m[m
[32m+[m[32m        <fo:region-before extent="1in"/>[m[41m[m
[32m+[m[32m        <fo:region-after extent="1in"/>[m[41m        [m
[32m+[m[32m    </fo:simple-page-master>[m[41m   [m
[32m+[m[32m</fo:layout-master-set>[m[41m[m
[32m+[m[32m   ${setRequestAttribute("OUTPUT_FILENAME", "accountingTrans.pdf")}[m[41m[m
[32m+[m[32m        <#if accountingTransEntries?has_content>[m[41m [m
[32m+[m[41m        [m	[32m<#assign acctgTransId = accountingTransEntries.acctgTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign acctgTransTypeId = accountingTransEntries.acctgTransTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign description = accountingTransEntries.description?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign transactionDate = accountingTransEntries.transactionDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign isPosted = accountingTransEntries.isPosted?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign postedDate = accountingTransEntries.postedDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign scheduledPostingDate = accountingTransEntries.scheduledPostingDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign glJournalId = accountingTransEntries.glJournalId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign glFiscalTypeId = accountingTransEntries.glFiscalTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign voucherRef = accountingTransEntries.voucherRef?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign voucherDate = accountingTransEntries.voucherDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign fixedAssetId = accountingTransEntries.fixedAssetId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign groupStatusId = accountingTransEntries.groupStatusId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign inventoryItemId = accountingTransEntries.inventoryItemId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign physicalInventoryId = accountingTransEntries.physicalInventoryId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign partyId = accountingTransEntries.partyId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign roleTypeId = accountingTransEntries.roleTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign invoiceId = accountingTransEntries.invoiceId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign paymentId = accountingTransEntries.paymentId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign finAccountTransId = accountingTransEntries.finAccountTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign shipmentId = accountingTransEntries.shipmentId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign receiptId = accountingTransEntries.receiptId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign workEffortId = accountingTransEntries.workEffortId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign theirAcctgTransId = accountingTransEntries.theirAcctgTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign createdByUserLogin = accountingTransEntries.createdByUserLogin?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m<#assign lastModifiedByUserLogin = accountingTransEntries.lastModifiedByUserLogin?if_exists>[m[41m[m
[32m+[m[41m        	[m
[32m+[m[41m        	[m
[32m+[m[32m           <fo:page-sequence master-reference="main" force-page-count="no-force" font-size="12pt" font-family="Courier,monospace">[m[41m					[m
[32m+[m		[41m    [m	[32m<fo:static-content flow-name="xsl-region-before">[m[41m[m
[32m+[m			[41m    [m	[32m<#assign roHeader = partyIdForAdd+"_HEADER">[m[41m[m
[32m+[m[41m              [m	[41m [m	[32m<#assign roSubheader = partyIdForAdd+"_HEADER01">[m[41m[m
[32m+[m		[41m    [m	[32m    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader}, true)>[m[41m[m
[32m+[m[32m                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubheader}, true)>[m[41m   [m
[32m+[m[32m                    <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>[m[41m[m
[32m+[m[32m                    <fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportHeader.description?if_exists} </fo:block>[m[41m[m
[32m+[m					[32m<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportSubHeader.description?if_exists}  </fo:block>[m[41m[m
[32m+[m[41m[m
[32m+[m[32m                    <fo:block text-align="right" linefeed-treatment="preserve"></fo:block>[m[41m[m
[32m+[m[32m                    <#assign finAccountTransDetails = delegator.findOne("FinAccountTrans", {"finAccountTransId" : finAccountTransId}, false)?if_exists/>[m[41m[m
[32m+[m[32m                    <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false"><#--<#if finAccountTransDetails?has_content>${(finAccountTransDetails.finAccountTransTypeId)?replace("_"," ")}<#else>${acctgTransTypeId?if_exists?replace("_"," ")}</#if>--></fo:block>[m[41m[m
[32m+[m[32m                    <#--<fo:block text-align="left"  keep-together="always"  font-weight = "bold" white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMMM dd,yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>-->[m[41m[m
[32m+[m[32m                    <fo:block>[m[41m[m
[32m+[m[32m                        <fo:table>[m[41m[m
[32m+[m	[32m                    <fo:table-column column-width="50%"/>[m[41m[m
[32m+[m		[32m                    <fo:table-body>[m[41m[m
[32m+[m			[32m                    <fo:table-row>[m[41m	                    	[m
[32m+[m		[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                        [m		[32m<fo:block text-align="right" font-weight="bold"><#if invSequenceNum?has_content>Sequence Number:<fo:inline font-weight="bold">${invSequenceNum?if_exists}</fo:inline></#if></fo:block>[m[41m  [m
[32m+[m		[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m			[32m                    </fo:table-row>[m[41m[m
[32m+[m		[32m                    </fo:table-body>[m[41m[m
[32m+[m	[32m                    </fo:table>[m[41m[m
[32m+[m[32m                    </fo:block>[m[41m[m
[32m+[m[41m              [m		[32m</fo:static-content>[m[41m		[m
[32m+[m[41m            [m		[32m<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">[m[41m	[m
[32m+[m		[32m            <fo:block text-align="left" font-weight="bold">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMMM dd,yyyy")}</fo:block>[m[41m  [m
[32m+[m[41m              [m		[32m<fo:block>---------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block><fo:table>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="50%"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="70%"/>[m[41m[m
[32m+[m[32m                    <fo:table-body>[m[41m[m
[32m+[m[32m                    <fo:table-row>[m[41m[m
[32m+[m[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                   [m			[32m<fo:block text-align="left" font-weight="bold"><#if BankName?has_content>Bank:${BankName?if_exists}</#if></fo:block>[m[41m[m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block text-align="left" font-weight="bold"><#if finAccntTransSequence?has_content>Sequence Number:${finAccntTransSequence?if_exists}</#if></fo:block>[m[41m[m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                   [m	[32m</fo:table-row>[m[41m[m
[32m+[m[32m                    <fo:table-row>[m[41m[m
[32m+[m[41m            [m				[32m<#if acctgTransId?has_content>[m[41m[m
[32m+[m[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left">Acctg Trans Id:${acctgTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                   [m			[32m<#else>[m[41m[m
[32m+[m[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                   [m			[32m</#if>[m[41m[m
[32m+[m[41m                   [m			[32m<#if reportTypeFlag?has_content>[m[41m[m
[32m+[m[41m                    [m				[32m<#if finAccountTransDetails?has_content>[m[41m[m
[32m+[m	[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  keep-together="always" text-align="left">Acctg Trans Type Id:${finAccountTransDetails.finAccountTransTypeId?if_exists}</fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m<#else>[m[41m[m
[32m+[m	[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m</#if>[m[41m[m
[32m+[m	[41m                   [m		[32m<#else>[m[41m		[m
[32m+[m	[41m                   [m			[32m<#if acctgTransTypeId?has_content>[m[41m[m
[32m+[m	[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  keep-together="always" text-align="left"><!--Acctg Trans Type Id:${acctgTransTypeId?if_exists}--></fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m<#else>[m[41m[m
[32m+[m	[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m</#if>[m[41m[m
[32m+[m	[41m                   [m		[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m	[m
[32m+[m[32m                    <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if invoiceId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left">Invoice Id:${invoiceId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if paymentId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left" keep-together="always">Payment Id:${paymentId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m[m
[32m+[m[41m                     			[m
[32m+[m[41m                       [m			[32m<#if finAccountTransId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left">Fin Account Trans Id:${finAccountTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#if description?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left" wrap-option="wrap" >Description:${description?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m[m
[32m+[m[32m                    <fo:table-row>[m[41m[m
[32m+[m[41m                    [m		[32m<#if transactionDate?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Transaction Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(transactionDate, "dd-MM-yyyy")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                    [m
[32m+[m[41m                    [m		[32m<#if isPosted?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always"><#--Is Posted:${isPosted?if_exists}--></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m[m
[32m+[m[41m                    [m
[32m+[m[32m                    <#--<#if postedDate?has_content>[m[41m[m
[32m+[m[32m                                <fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Posted Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(postedDate, "dd-MM-yyyy HH:mm:ss")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</fo:table-row>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>-->[m[41m[m
[32m+[m[32m                    <#if scheduledPostingDate?has_content>[m[41m[m
[32m+[m[41m                   [m		[32m<fo:table-row>[m[41m	[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Schd Posting Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(scheduledPostingDate, "dd-MM-yyyy HH:mm:ss")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m	[32m</fo:table-row>[m[41m[m
[32m+[m[32m                    </#if>[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if glJournalId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always"><#--GL Journal Id:${glJournalId?if_exists}--></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <#--<#if glFiscalTypeId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Fiscal GL Type Id:${glFiscalTypeId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>-->[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if voucherRef?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Voucher Ref:${voucherRef?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     <#if voucherDate?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Voucher Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(voucherDate, "dd-MM-yyyy")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                     [m		[32m<#if groupStatusId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Group Status Id:${groupStatusId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if fixedAssetId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Fixed Asset Id:${fixedAssetId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if inventoryItemId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Inventory Item Id:${inventoryItemId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if physicalInventoryId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Physical Inventory Id:${physicalInventoryId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if partyId?has_content>[m[41m[m
[32m+[m[41m                     [m		[32m<#assign partyFullName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId?if_exists, false)>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap"><#if parameters.reportFlag?has_content && parameters.reportFlag =="Y">&#160;<#else>Party: <fo:inline font-weight="bold">${partyFullName?if_exists}[${partyId?if_exists}]</fo:inline></#if></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#--<#if roleTypeId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Role Type Id:${roleTypeId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>-->[m[41m[m
[32m+[m[41m                       [m		[32m<#assign invoiceDetails = delegator.findOne("Invoice", {"invoiceId" : invoiceId}, false)?if_exists/>[m[41m[m
[32m+[m[41m                       [m		[32m<#if invoiceDetails?has_content && invoiceDetails.referenceNumber?has_content>[m[41m[m
[32m+[m[41m                     [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always"><#--Party Invoice No:<fo:inline font-weight="bold">${invoiceDetails.referenceNumber?if_exists}</fo:inline>--></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                       [m		[32m<#if shipmentId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Shipment Id:${shipmentId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if receiptId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Receipt Id:${receiptId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                     [m
[32m+[m[41m                       [m		[32m<#if workEffortId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Work Effort Id:${workEffortId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if theirAcctgTransId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Their Acctg Trans Id:${theirAcctgTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                     [m		[32m<#if invoiceDetails?has_content && invoiceDetails.referenceNumberDate?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Party Invoice Date:<fo:inline font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoiceDetails.referenceNumberDate), "dd-MM-yyyy")?if_exists}</fo:inline></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <#if reportTypeFlag?has_content>[m[41m[m
[32m+[m[41m                     [m	[32m<#assign finAccountTransDetails = delegator.findOne("FinAccountTrans", {"finAccountTransId" : finAccountTransId}, false)?if_exists/>[m[41m[m
[32m+[m[41m                    [m	[32m<#if finAccountTransDetails?has_content>[m[41m[m
[32m+[m		[32m                    <fo:table-row>[m[41m	[m
[32m+[m		[41m                     [m		[32m<#if finAccountTransDetails.contraRefNum?has_content>[m[41m[m
[32m+[m		[41m                     [m		[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Instrument Number:${finAccountTransDetails.contraRefNum?if_exists}</fo:block>[m[41m  [m
[32m+[m		[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m<#else>[m[41m[m
[32m+[m		[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m		[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m</#if>[m[41m[m
[32m+[m		[41m                     [m		[32m<#if finAccountTransDetails.comments?has_content>[m[41m[m
[32m+[m		[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Cheque in favour:${finAccountTransDetails.comments?if_exists}</fo:block>[m[41m  [m
[32m+[m		[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m<#else>[m[41m[m
[32m+[m		[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m		[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m</#if>[m[41m[m
[32m+[m		[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                      [m	[32m</#if>[m[41m [m
[32m+[m[32m                      </#if>[m[41m 	   [m
[32m+[m[32m                     </fo:table-body>[m[41m[m
[32m+[m[32m                      </fo:table>[m[41m[m
[32m+[m[41m            [m		[32m</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block>--------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block font-weight = "bold" font-size = "12pt">Acct Name 		        &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Party  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Debit Amt    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Credit Amt</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block>--------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m[41m            [m	[32m<fo:block>[m[41m[m
[32m+[m[41m                 [m	[32m<fo:table>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="210pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="100pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="90pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="170pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="20pt"/>[m[41m [m
[32m+[m[32m                    <fo:table-body>[m[41m[m
[32m+[m							[32m<#if accountingTransEntryList?has_content>[m[41m[m
[32m+[m							[32m<#assign crTotal = 0>[m[41m[m
[32m+[m							[32m<#assign drTotal = 0>[m[41m[m
[32m+[m							[32m<#list accountingTransEntryList as accntngTransEntry>[m[41m[m
[32m+[m							[32m<fo:table-row>[m[41m[m
[32m+[m[41m                				[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.glAccountId?has_content>[m[41m  [m
[32m+[m[41m        [m						[32m<#assign glAccntDetails = delegator.findOne("GlAccount", {"glAccountId" :accntngTransEntry.glAccountId}, true)>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(glAccntDetails.accountName?if_exists)),50)}</fo:block>[m[41m[m
[32m+[m[41m                            [m		[32m<#if glAccntDetails.accountName=="ACCOUNTS PAYABLE"||glAccntDetails.accountName=="ACCOUNTS RECEIVABLE">[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" font-size="10pt" >(${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,accntngTransEntry.partyId, false)})</fo:block>[m[41m[m
[32m+[m[41m                            [m		[32m</#if>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false">${accntngTransEntry.partyId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.debitCreditFlag?has_content && accntngTransEntry.debitCreditFlag == "D">[m[41m[m
[32m+[m[41m                       [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m			[32m<fo:block  text-align="right"  white-space-collapse="false">${accntngTransEntry.amount?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m				[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m				[32m<#if accntngTransEntry.amount?has_content>[m[41m[m
[32m+[m[41m                       [m					[32m<#assign drTotal = drTotal+accntngTransEntry.amount>[m[41m[m
[32m+[m[41m                       [m				[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">0.00</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.debitCreditFlag?has_content && accntngTransEntry.debitCreditFlag == "C">[m[41m[m
[32m+[m	[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">${accntngTransEntry.amount?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m	[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m				[32m<#if accntngTransEntry.amount?has_content>[m[41m[m
[32m+[m[41m                       [m					[32m<#assign crTotal = crTotal+accntngTransEntry.amount>[m[41m[m
[32m+[m[41m                       [m				[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">0.00</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m		[41m  [m					[32m</#list>[m[41m[m
[32m+[m		[41m  [m					[32m<fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false">---------------------------------------------------------------------------------------------</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                				[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m		[41m  [m					[32m<fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap" font-weight="bold"> Totals: </fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  font-weight="bold" white-space-collapse="false">${drTotal?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="right" font-weight="bold" white-space-collapse="false">${crTotal?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="right" font-weight="bold" white-space-collapse="false"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m		[41m  [m					[32m</#if>[m[41m[m
[32m+[m[41m		  					[m
[32m+[m				[41m       [m		[32m</fo:table-body>[m[41m[m
[32m+[m		[32m                </fo:table>[m[41m[m
[32m+[m		[32m               </fo:block>[m[41m[m
[32m+[m[41m				       		[m
[32m+[m[41m		                 [m
[32m+[m		[32m              <#if entryList?has_content>[m[41m [m
[32m+[m		[32m              <#list entryList as payAccountingTransEntries>[m[41m[m
[32m+[m		[32m              <#--<#if entryList?has_content>-->[m[41m[m
[32m+[m[41m        [m	[32m             <#assign acctgTransId = payAccountingTransEntries.acctgTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign acctgTransTypeId = payAccountingTransEntries.acctgTransTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign description = payAccountingTransEntries.description?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign transactionDate = payAccountingTransEntries.transactionDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign isPosted = payAccountingTransEntries.isPosted?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign postedDate = payAccountingTransEntries.postedDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign scheduledPostingDate = payAccountingTransEntries.scheduledPostingDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign glJournalId = payAccountingTransEntries.glJournalId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign glFiscalTypeId = payAccountingTransEntries.glFiscalTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign voucherRef = payAccountingTransEntries.voucherRef?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign voucherDate = payAccountingTransEntries.voucherDate?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign fixedAssetId = payAccountingTransEntries.fixedAssetId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign groupStatusId = payAccountingTransEntries.groupStatusId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign inventoryItemId = payAccountingTransEntries.inventoryItemId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign physicalInventoryId = payAccountingTransEntries.physicalInventoryId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign partyId = payAccountingTransEntries.partyId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign roleTypeId = payAccountingTransEntries.roleTypeId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign invoiceId = payAccountingTransEntries.invoiceId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign paymentId = payAccountingTransEntries.paymentId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign finAccountTransId = payAccountingTransEntries.finAccountTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign shipmentId = payAccountingTransEntries.shipmentId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign receiptId = payAccountingTransEntries.receiptId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign workEffortId = payAccountingTransEntries.workEffortId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign theirAcctgTransId = payAccountingTransEntries.theirAcctgTransId?if_exists>[m[41m[m
[32m+[m[41m        [m	[32m             <#assign createdByUserLogin = payAccountingTransEntries.createdByUserLogin?if_exists>[m[41m[m
[32m+[m[41m         [m	[32m             <#assign lastModifiedByUserLogin = payAccountingTransEntries.lastModifiedByUserLogin?if_exists>[m[41m[m
[32m+[m[41m         	             [m
[32m+[m[41m         [m	[32m             <fo:block>[m[41m[m
[32m+[m[41m		               [m
[32m+[m		[32m               <fo:block> &#180;</fo:block>[m[41m[m
[32m+[m		[32m               <fo:block>---------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m		[32m               <fo:block> &#160;</fo:block>[m[41m[m
[32m+[m		[32m                 <fo:table>[m[41m[m
[32m+[m		[32m                  <fo:table-column column-width="50%"/>[m[41m[m
[32m+[m[32m                          <fo:table-column column-width="70%"/>[m[41m[m
[32m+[m[32m                             <fo:table-body>[m[41m[m
[32m+[m[32m                               <fo:table-row>[m[41m[m
[32m+[m[32m                               <fo:table-cell>[m[41m[m
[32m+[m[32m                                  <#assign finAccountTransDetails = delegator.findOne("FinAccountTrans", {"finAccountTransId" : finAccountTransId}, false)?if_exists/>[m[41m[m
[32m+[m[32m                                  <fo:block  keep-together="always" text-align="right" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false"><#--<#if finAccountTransDetails?has_content>${(finAccountTransDetails.finAccountTransTypeId)?replace("_"," ")}<#else>${acctgTransTypeId?if_exists?replace("_"," ")}</#if>--></fo:block>[m[41m[m
[32m+[m[32m                               </fo:table-cell>[m[41m[m
[32m+[m[32m                               </fo:table-row>[m[41m[m
[32m+[m[32m                             </fo:table-body>[m[41m  [m
[32m+[m		[32m                 </fo:table>[m[41m[m
[32m+[m		[32m               </fo:block>[m[41m[m
[32m+[m		[32m               <#assign transType = "">[m[41m[m
[32m+[m		[32m               <fo:block><fo:table>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="50%"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="70%"/>[m[41m[m
[32m+[m[32m                    <fo:table-body>[m[41m[m
[32m+[m[32m                    <fo:table-row>[m[41m[m
[32m+[m[41m            [m				[32m<#if acctgTransId?has_content>[m[41m[m
[32m+[m[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left">Acctg Trans Id:${acctgTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                   [m			[32m<#else>[m[41m[m
[32m+[m[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                   [m			[32m</#if>[m[41m[m
[32m+[m[41m                   [m			[32m<#if reportTypeFlag?has_content>[m[41m[m
[32m+[m[41m                    [m				[32m<#if finAccountTransDetails?has_content>[m[41m[m
[32m+[m	[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  keep-together="always" text-align="left" >Acctg Trans Type Id:${finAccountTransDetails.finAccountTransTypeId?if_exists}</fo:block>[m[41m[m
[32m+[m	[41m                        [m		[32m<#assign transType = finAccountTransDetails.finAccountTransTypeId>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m<#else>[m[41m[m
[32m+[m	[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m</#if>[m[41m[m
[32m+[m	[41m                   [m		[32m<#else>[m[41m		[m
[32m+[m	[41m                   [m			[32m<#if acctgTransTypeId?has_content>[m[41m[m
[32m+[m	[41m            [m				[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  keep-together="always" text-align="left">Acctg Trans Type Id:${acctgTransTypeId?if_exists}</fo:block>[m[41m[m
[32m+[m	[41m                        [m		[32m<#assign transType = acctgTransTypeId>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m<#else>[m[41m[m
[32m+[m	[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                        [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m	[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m	[41m                   [m			[32m</#if>[m[41m[m
[32m+[m	[41m                   [m		[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m	[m
[32m+[m[32m                    <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if invoiceId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left">Invoice Id:${invoiceId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if paymentId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left" keep-together="always">Payment Id:${paymentId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m[m
[32m+[m[41m                     			[m
[32m+[m[41m                       [m			[32m<#if finAccountTransId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left">Fin Account Trans Id:${finAccountTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#if description?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left" wrap-option="wrap" >Description:${description?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m[m
[32m+[m[32m                    <fo:table-row>[m[41m[m
[32m+[m[41m                    [m		[32m<#if transactionDate?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Transaction Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(transactionDate, "dd-MM-yyyy")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  font-weight = "bold"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                    [m
[32m+[m[41m                    [m		[32m<#if isPosted?has_content>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Is Posted:${isPosted?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[32m                    </fo:table-row>[m[41m[m
[32m+[m[41m                    [m
[32m+[m[32m                    <#--<#if postedDate?has_content>[m[41m[m
[32m+[m[32m                                <fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Posted Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(postedDate, "dd-MM-yyyy HH:mm:ss")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</fo:table-row>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>-->[m[41m[m
[32m+[m[32m                    <#if scheduledPostingDate?has_content>[m[41m[m
[32m+[m[41m                   [m		[32m<fo:table-row>[m[41m	[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Schd Posting Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(scheduledPostingDate, "dd-MM-yyyy HH:mm:ss")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m	[32m</fo:table-row>[m[41m[m
[32m+[m[32m                    </#if>[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if glJournalId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">GL Journal Id:${glJournalId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <#--<#if glFiscalTypeId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Fiscal GL Type Id:${glFiscalTypeId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>-->[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if voucherRef?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Voucher Ref:${voucherRef?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     <#if voucherDate?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Voucher Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(voucherDate, "dd-MM-yyyy")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                     [m		[32m<#if groupStatusId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Group Status Id:${groupStatusId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if fixedAssetId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Fixed Asset Id:${fixedAssetId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if inventoryItemId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Inventory Item Id:${inventoryItemId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if physicalInventoryId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Physical Inventory Id:${physicalInventoryId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[32m                     <#if partyId?has_content>[m[41m[m
[32m+[m[41m                     [m		[32m<#assign partyFullName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId?if_exists, false)>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap"><#if parameters.reportFlag?has_content && parameters.reportFlag =="Y">&#160;<#else>Party: <fo:inline font-weight="bold">${partyFullName?if_exists}[${partyId?if_exists}]</fo:inline></#if></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#--<#if roleTypeId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Role Type Id:${roleTypeId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>-->[m[41m[m
[32m+[m[41m                       [m		[32m<#assign invoiceDetails = delegator.findOne("Invoice", {"invoiceId" : invoiceId}, false)?if_exists/>[m[41m[m
[32m+[m[41m                       [m		[32m<#if invoiceDetails?has_content && invoiceDetails.referenceNumber?has_content>[m[41m[m
[32m+[m[41m                     [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Party Invoice No:<fo:inline font-weight="bold">${invoiceDetails.referenceNumber?if_exists}</fo:inline></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                       [m		[32m<#if shipmentId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Shipment Id:${shipmentId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if receiptId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Receipt Id:${receiptId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       		[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                     [m
[32m+[m[41m                       [m		[32m<#if workEffortId?has_content>[m[41m[m
[32m+[m[41m                       [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Work Effort Id:${workEffortId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[41m                       [m		[32m<#if theirAcctgTransId?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Their Acctg Trans Id:${theirAcctgTransId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[41m                     [m
[32m+[m[32m                     <fo:table-row>[m[41m	[m
[32m+[m[41m                   [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                        [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                   [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                     [m		[32m<#if invoiceDetails?has_content && invoiceDetails.referenceNumberDate?has_content>[m[41m[m
[32m+[m[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Party Invoice Date:<fo:inline font-weight="bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString((invoiceDetails.referenceNumberDate), "dd-MM-yyyy")?if_exists}</fo:inline></fo:block>[m[41m  [m
[32m+[m[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m		[32m</#if>[m[41m[m
[32m+[m[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                     [m
[32m+[m[32m                     <#if reportTypeFlag?has_content>[m[41m[m
[32m+[m[41m                     [m	[32m<#assign finAccountTransDetails = delegator.findOne("FinAccountTrans", {"finAccountTransId" : finAccountTransId}, false)?if_exists/>[m[41m[m
[32m+[m[41m                    [m	[32m<#if finAccountTransDetails?has_content>[m[41m[m
[32m+[m		[32m                    <fo:table-row>[m[41m	[m
[32m+[m		[41m                     [m		[32m<#if finAccountTransDetails.contraRefNum?has_content>[m[41m[m
[32m+[m		[41m                     [m		[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  keep-together="always">Instrument Number:${finAccountTransDetails.contraRefNum?if_exists}</fo:block>[m[41m  [m
[32m+[m		[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m<#else>[m[41m[m
[32m+[m		[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m		[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m</#if>[m[41m[m
[32m+[m		[41m                     [m		[32m<#if finAccountTransDetails.comments?has_content>[m[41m[m
[32m+[m		[41m                    [m		[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  keep-together="always" text-align="left" >Cheque in favour:${finAccountTransDetails.comments?if_exists}</fo:block>[m[41m  [m
[32m+[m		[41m                       [m		[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m<#else>[m[41m[m
[32m+[m		[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m		[41m                            [m		[32m<fo:block  text-align="left"  ></fo:block>[m[41m  [m
[32m+[m		[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m		[41m                       [m		[32m</#if>[m[41m[m
[32m+[m		[32m                     </fo:table-row>[m[41m[m
[32m+[m[41m                      [m	[32m</#if>[m[41m [m
[32m+[m[32m                      </#if>[m[41m 	   [m
[32m+[m[32m                     </fo:table-body>[m[41m[m
[32m+[m[32m                      </fo:table>[m[41m[m
[32m+[m[41m            [m		[32m</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block>--------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block font-weight = "bold" font-size = "12pt">Acct Name 		        &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Party  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Debit Amt    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Credit Amt</fo:block>[m[41m[m
[32m+[m[41m            [m		[32m<fo:block>--------------------------------------------------------------------------------------------</fo:block>[m[41m[m
[32m+[m		[32m            <fo:block>[m[41m[m
[32m+[m[41m                 [m	[32m<fo:table>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="210pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="100pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="90pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="170pt"/>[m[41m[m
[32m+[m[32m                    <fo:table-column column-width="15pt"/>[m[41m [m
[32m+[m[32m                    <fo:table-body>[m[41m[m
[32m+[m							[32m<#if finalMap?has_content>[m[41m[m
[32m+[m							[32m<#assign crTotal = 0>[m[41m[m
[32m+[m							[32m<#assign drTotal = 0>[m[41m[m
[32m+[m							[32m<#assign acctngEntriesList = finalMap(transType)>[m[41m [m
[32m+[m							[32m<#list acctngEntriesList as accntngTransEntry>[m[41m[m
[32m+[m							[32m<fo:table-row>[m[41m[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.glAccountId?has_content>[m[41m  [m
[32m+[m[41m                       [m			[32m<#assign accntngTransEntry = "">[m[41m[m
[32m+[m[41m        [m						[32m<#assign glAccntDetails = delegator.findOne("GlAccount", {"glAccountId" :accntngTransEntry.glAccountId}, true)>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap"> ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(glAccntDetails.accountName?if_exists)),50)}</fo:block>[m[41m[m
[32m+[m[41m                            [m		[32m<#if glAccntDetails.accountName=="ACCOUNTS PAYABLE"||glAccntDetails.accountName=="ACCOUNTS RECEIVABLE">[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" font-size="10pt" >(${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,accntngTransEntry.partyId, false)})</fo:block>[m[41m[m
[32m+[m[41m                            [m		[32m</#if>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false">${accntngTransEntry.partyId?if_exists}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.debitCreditFlag?has_content && accntngTransEntry.debitCreditFlag == "D">[m[41m[m
[32m+[m[41m                       [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m			[32m<fo:block  text-align="right"  white-space-collapse="false">${accntngTransEntry.amount?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m				[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m				[32m<#if accntngTransEntry.amount?has_content>[m[41m[m
[32m+[m[41m                       [m					[32m<#assign drTotal = drTotal+accntngTransEntry.amount>[m[41m[m
[32m+[m[41m                       [m				[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">0.00</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#if accntngTransEntry.debitCreditFlag?has_content && accntngTransEntry.debitCreditFlag == "C">[m[41m[m
[32m+[m	[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m	[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">${accntngTransEntry.amount?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m	[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m				[32m<#if accntngTransEntry.amount?has_content>[m[41m[m
[32m+[m[41m                       [m					[32m<#assign crTotal = crTotal+accntngTransEntry.amount>[m[41m[m
[32m+[m[41m                       [m				[32m</#if>[m[41m[m
[32m+[m[41m                       [m			[32m<#else>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  white-space-collapse="false">0.00</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m</#if>[m[41m[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m		[41m  [m					[32m</#list>[m[41m[m
[32m+[m		[41m  [m					[32m<fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false">---------------------------------------------------------------------------------------------</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                				[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m		[41m  [m					[32m<fo:table-row>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="left"  white-space-collapse="false"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                [m				[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="left" wrap-option="wrap" font-weight="bold"> Totals: </fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block  text-align="right"  font-weight="bold" white-space-collapse="false">${drTotal?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="right" font-weight="bold" white-space-collapse="false">${crTotal?if_exists?string("#0.00")}</fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m                       [m			[32m<fo:table-cell>[m[41m[m
[32m+[m[41m                            [m		[32m<fo:block text-align="right" font-weight="bold" white-space-collapse="false"></fo:block>[m[41m  [m
[32m+[m[41m                       [m			[32m</fo:table-cell>[m[41m[m
[32m+[m[41m            [m				[32m</fo:table-row>[m[41m[m
[32m+[m[41m               			[m
[32m+[m		[41m  [m					[32m</#if>[m[41m[m
[32m+[m				[41m       [m		[32m</fo:table-body>[m[41m[m
[32m+[m		[32m                </fo:table>[m[41m[m
[32m+[m		[32m               </fo:block>[m[41m[m
[32m+[m[41m				       		[m
[32m+[m[41m         [m	[32m             </#list>[m[41m[m
[32m+[m[41m         [m	[32m           </#if>[m[41m[m
[32m+[m[41m         [m	[32m           <fo:block>[m[41m[m
[32m+[m			[41m                 [m	[32m<fo:table>[m[41m[m
[32m+[m			[32m                    <fo:table-column column-width="25%"/>[m[41m[m
[32m+[m			[32m                    <fo:table-column column-width="25%"/>[m[41m[m
[32m+[m			[32m                    <fo:table-column column-width="25%"/>[m[41m[m
[32m+[m			[32m                    <fo:table-column column-width="25%"/>[m[41m[m
[32m+[m			[32m                     <fo:table-column column-width="25%"/>[m[41m[m
[32m+[m[41m			                    [m
[32m+[m			[32m                    <fo:table-body>[m[41m[m
[32m+[m			[41m                            [m	[32m<fo:table-row>[m[41m[m
[32m+[m								[32m <fo:table-cell number-columns-spanned="5">[m[41m[m
[32m+[m			[41m  [m						[32m<fo:block text-align="left"  white-space-collapse="false" font-size="12pt">Amount in Words : RUPEES ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(crTotal?string("#0.00")), "%indRupees-and-paiseRupees", locale).toUpperCase()} ONLY  </fo:block>[m[41m	[m
[32m+[m			[41m  [m					[32m </fo:table-cell>[m[41m[m
[32m+[m				[41m       [m		[32m</fo:table-row>[m[41m[m
[32m+[m			[41m                    [m		[32m<fo:table-row>[m[41m[m
[32m+[m			[41m               [m					[32m<fo:table-cell number-columns-spanned="2">[m[41m   						[m
[32m+[m										[41m [m	[32m    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>[m[41m [m
[32m+[m											[32m</fo:table-cell>[m[41m[m
[32m+[m					[41m  [m					[32m</fo:table-row>[m[41m[m
[32m+[m									[41m  [m	[32m<fo:table-row>[m[41m[m
[32m+[m			[41m               [m					[32m<fo:table-cell>[m[41m[m
[32m+[m			[41m                    [m				[32m<fo:block text-align="center">Prepared By</fo:block>[m[41m[m
[32m+[m			[41m               [m					[32m</fo:table-cell>[m[41m[m
[32m+[m			[41m               [m					[32m<fo:table-cell >[m[41m[m
[32m+[m			[41m                    [m				[32m<fo:block text-align="center">Checked By</fo:block>[m[41m[m
[32m+[m			[41m               [m					[32m</fo:table-cell>[m[41m[m
[32m+[m			[41m               [m					[32m<fo:table-cell>[m[41m[m
[32m+[m			[41m                    [m				[32m<fo:block text-align="center">Finance Incharge</fo:block>[m[41m[m
[32m+[m			[41m               [m					[32m</fo:table-cell>[m[41m[m
[32m+[m			[41m               [m					[32m<fo:table-cell >[m[41m[m
[32m+[m			[41m                    [m				[32m<fo:block text-align="center" keep-together="always">RO Incharge</fo:block>[m[41m[m
[32m+[m			[41m               [m					[32m</fo:table-cell>[m[41m[m
[32m+[m					[41m  [m					[32m</fo:table-row>[m[41m[m
[32m+[m					[32m              </fo:table-body>[m[41m[m
[32m+[m		[41m                [m	[32m</fo:table>[m[41m[m
[32m+[m		[32m               </fo:block>[m[41m[m
[32m+[m					[32m</fo:flow>[m[41m[m
[32m+[m					[32m</fo:page-sequence>[m[41m[m
[32m+[m			[32m  <#else>[m[41m[m
[32m+[m	[41m    [m	[32m<fo:page-sequence master-reference="main">[m[41m[m
[32m+[m			[32m<fo:flow flow-name="xsl-region-body" font-family="Helvetica">[m[41m[m
[32m+[m				[32m<fo:block font-size="14pt">[m[41m[m
[32m+[m		[41m            [m	[32m${uiLabelMap.NoOrdersFound}.[m[41m[m
[32m+[m		[41m       [m		[32m </fo:block>[m[41m[m
[32m+[m			[32m</fo:flow>[m[41m[m
[32m+[m		[32m</fo:page-sequence>[m[41m	[m
[32m+[m[32m    </#if>[m[41m  [m
[32m+[m[32m</fo:root>[m[41m[m
[32m+[m[32m</#escape>[m
\ No newline at end of file[m
[1mdiff --git a/applications/accounting/webapp/accounting/reports/accountingTransAndFinAccountTrans1.fo.ftl b/applications/accounting/webapp/accounting/reports/accountingTransAndFinAccountTrans1.fo.ftl[m
[1mindex 6cc77a682..2aad7c7e8 100644[m
[1m--- a/applications/accounting/webapp/accounting/reports/accountingTransAndFinAccountTrans1.fo.ftl[m
[1m+++ b/applications/accounting/webapp/accounting/reports/accountingTransAndFinAccountTrans1.fo.ftl[m
[36m@@ -64,11 +64,11 @@[m [munder the License.[m
 		    	<fo:static-content flow-name="xsl-region-before">[m
 		    		<#assign roHeader = partyIdForAdd+"_HEADER">[m
               	 	<#assign roSubheader = partyIdForAdd+"_HEADER01">[m
[31m-		    	    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader}, true)>[m
[31m-                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubheader}, true)>   [m
[32m+[m		[41m    [m	[32m    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader?default("COMPANY_HEADER")}, true)>[m[41m[m
[32m+[m[32m                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubheader?default("COMPANY_HEADER01")}, true)>[m[41m   [m
                     <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>[m
[31m-                    <fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportHeader.description?if_exists} </fo:block>[m
[31m-					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ${reportSubHeader.description?if_exists}  </fo:block>[m
[32m+[m[32m                    <fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if reportHeader?has_content>  ${reportHeader.description?if_exists}</#if> </fo:block>[m[41m[m
[32m+[m					[32m<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if reportSubHeader?has_content>${reportSubHeader.description?if_exists}</#if>  </fo:block>[m[41m[m
                     <fo:block text-align="right" linefeed-treatment="preserve"></fo:block>[m
                     <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false"><#if reportTypeFlag?has_content && reportTypeFlag == "contraCheque">CONTRA VOUCHER<#else>JOURNAL VOUCHER</#if></fo:block>[m
                     <fo:block text-align="left"  keep-together="always"  font-weight = "bold" white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMMM dd,yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>   </fo:block>[m
[1mdiff --git a/applications/accounting/widget/GlScreens.xml b/applications/accounting/widget/GlScreens.xml[m
[1mindex b302167ab..bf56f38ea 100755[m
[1m--- a/applications/accounting/widget/GlScreens.xml[m
[1m+++ b/applications/accounting/widget/GlScreens.xml[m
[36m@@ -1566,6 +1566,7 @@[m [mreportType:byAccount -->[m
 				<property-map resource="AccountingUiLabels" map-name="uiLabelMap"[m
 					global="true" />[m
 					<set field="reportTypeFlag" from-field="parameters.reportTypeFlag"/>[m
[32m+[m					[32m<set field="paymentIds" from-field="parameters.paymentIds" type="List"/>[m[41m[m
 				<script	location="component://accounting/webapp/accounting/WEB-INF/actions/reports/paymentAccountingTrans.groovy"/>[m
 			</actions>[m
 			<widgets>[m
[36m@@ -1578,6 +1579,25 @@[m [mreportType:byAccount -->[m
 			</widgets>[m
 		</section>[m
 	</screen>[m
[32m+[m	[32m<screen name="PaymentAccountingTransPdf1">[m[41m[m
[32m+[m		[32m<section>[m[41m[m
[32m+[m			[32m<actions>[m[41m[m
[32m+[m				[32m<property-map resource="AccountingUiLabels" map-name="uiLabelMap"[m[41m[m
[32m+[m					[32mglobal="true" />[m[41m[m
[32m+[m					[32m<set field="reportTypeFlag" from-field="parameters.reportTypeFlag"/>[m[41m[m
[32m+[m					[32m<set field="paymentIds" from-field="parameters.paymentIds" type="List"/>[m[41m[m
[32m+[m				[32m<script	location="component://accounting/webapp/accounting/WEB-INF/actions/reports/paymentAccountingTrans1.groovy"/>[m[41m[m
[32m+[m			[32m</actions>[m[41m[m
[32m+[m			[32m<widgets>[m[41m[m
[32m+[m				[32m<platform-specific>[m[41m[m
[32m+[m					[32m<xsl-fo>[m[41m[m
[32m+[m					[32m<html-template[m[41m[m
[32m+[m							[32mlocation="component://accounting/webapp/accounting/reports/AccountingReportOverviewScreen1.fo.ftl" />[m[41m[m
[32m+[m					[32m</xsl-fo>[m[41m[m
[32m+[m				[32m</platform-specific>[m[41m[m
[32m+[m			[32m</widgets>[m[41m[m
[32m+[m		[32m</section>[m[41m[m
[32m+[m	[32m</screen>[m[41m[m
 	<screen name="DepRctAcctgTransReportPdf">[m
 		<section>[m
 			<actions>[m
[1mdiff --git a/applications/order/build/lib/ofbiz-order.jar b/applications/order/build/lib/ofbiz-order.jar[m
[1mindex 76ac123bc..9645b0967 100644[m
Binary files a/applications/order/build/lib/ofbiz-order.jar and b/applications/order/build/lib/ofbiz-order.jar differ
[1mdiff --git a/applications/party/build/lib/ofbiz-party.jar b/applications/party/build/lib/ofbiz-party.jar[m
[1mindex efa255ab9..7686df063 100644[m
Binary files a/applications/party/build/lib/ofbiz-party.jar and b/applications/party/build/lib/ofbiz-party.jar differ
[1mdiff --git a/hot-deploy/byproducts/build/lib/byproducts.jar b/hot-deploy/byproducts/build/lib/byproducts.jar[m
[1mindex 85cfab550..8fb91c07f 100644[m
Binary files a/hot-deploy/byproducts/build/lib/byproducts.jar and b/hot-deploy/byproducts/build/lib/byproducts.jar differ
[1mdiff --git a/hot-deploy/materialmgmt/build/lib/materialmgmt.jar b/hot-deploy/materialmgmt/build/lib/materialmgmt.jar[m
[1mindex d2eaac621..88fecfc62 100644[m
Binary files a/hot-deploy/materialmgmt/build/lib/materialmgmt.jar and b/hot-deploy/materialmgmt/build/lib/materialmgmt.jar differ
[1mdiff --git a/hot-deploy/procurement/build/lib/procurement.jar b/hot-deploy/procurement/build/lib/procurement.jar[m
[1mindex 4330ce613..4f25aad62 100644[m
Binary files a/hot-deploy/procurement/build/lib/procurement.jar and b/hot-deploy/procurement/build/lib/procurement.jar differ
[1mdiff --git a/runtime/tempfiles/org.eclipse.update/last.config.stamp b/runtime/tempfiles/org.eclipse.update/last.config.stamp[m
[1mindex 92e4bcce4..8b8dd02b2 100644[m
Binary files a/runtime/tempfiles/org.eclipse.update/last.config.stamp and b/runtime/tempfiles/org.eclipse.update/last.config.stamp differ
[1mdiff --git a/runtime/tempfiles/org.eclipse.update/platform.xml b/runtime/tempfiles/org.eclipse.update/platform.xml[m
[1mindex f52ab6ff4..fe0056687 100644[m
[1m--- a/runtime/tempfiles/org.eclipse.update/platform.xml[m
[1m+++ b/runtime/tempfiles/org.eclipse.update/platform.xml[m
[36m@@ -1,5 +1,5 @@[m
 <?xml version="1.0" encoding="UTF-8"?>[m
[31m-<config date="1494479159151" transient="false" version="3.0">[m
[32m+[m[32m<config date="1494820598507" transient="false" version="3.0">[m
 <site enabled="true" policy="USER-EXCLUDE" updateable="true" url="platform:/base/">[m
 </site>[m
 </config>[m
