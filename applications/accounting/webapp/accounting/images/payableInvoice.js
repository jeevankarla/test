function enablePayrol() {
	jQuery('#NewPurchaseInvoice_partyIdFrom_title').parent().parent().show();	
	jQuery('#NewPurchaseInvoice_roleTypeId').parent().parent().hide();		
	jQuery('#NewPurchaseInvoice_description').parent().parent().hide();	
	jQuery('#NewPurchaseInvoice_invoiceMessage').parent().parent().hide();
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(0)').hide();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(1)').hide();	  	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(2)').show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(3)').show();
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(2)').show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(3)').show();
	jQuery('#0_lookupId_NewPurchaseInvoice_partyIdFrom').val('');
	
}
function enablePettyCash() {
	  	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(2)').hide();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(3)').hide();
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(2)').hide();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(3)').hide();		
	jQuery('#NewPurchaseInvoice_roleTypeId_title').parent().parent().hide();	
	jQuery('#NewPurchaseInvoice_invoiceMessage').parent().parent().hide();
	jQuery('#NewPurchaseInvoice_description').parent().parent().show();
	jQuery('#NewPurchaseInvoice_dueDate').val(jQuery('#NewPurchaseInvoice_pettyCashDueDate').val());
	jQuery('#NewPurchaseInvoice_invoiceTab').val('listInvoiceItems');
	jQuery('#NewPurchaseInvoice_roleTypeId').val(jQuery('#NewPurchaseInvoice_pettyCashVendorRoleTypeId').val());
	jQuery('#0_lookupId_NewPurchaseInvoice_partyIdFrom').val(jQuery('#NewPurchaseInvoice_pettyCashVendorId').val());
	jQuery("input[name='dueDate_i18n']").removeClass("required");
		
}

function disablePayrol() {
	jQuery('#NewPurchaseInvoice_roleTypeId_title').parent().parent().show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(0)').show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(1)').show();	
	jQuery('#NewPurchaseInvoice_description').parent().parent().show();	
	jQuery('#NewPurchaseInvoice_invoiceMessage').parent().parent().show();
}
function disablePettyCash() {
	
	jQuery('#NewPurchaseInvoice_roleTypeId_title').parent().parent().show();	
	jQuery('#NewPurchaseInvoice_description').parent().parent().show();	
	jQuery('#NewPurchaseInvoice_invoiceMessage').parent().parent().show();
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(2)').show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(1) td:eq(3)').show();
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(2)').show();	
	jQuery('#NewPurchaseInvoice table tbody tr:eq(0) td:eq(3)').show();
	//jQuery('#NewPurchaseInvoice_dueDate').val('');
	jQuery('#NewPurchaseInvoice_invoiceTab').val('');
	//jQuery('#NewPurchaseInvoice_roleTypeId').val('');
	jQuery('#0_lookupId_NewPurchaseInvoice_partyIdFrom').val('');		
}

function setInvoiceAction(){
    var defaultAction = 'createInvoice';	
    var payrolAction = 'createPayrolInvoice';
    var action;
    if (jQuery('#NewPurchaseInvoice_invoiceTypeId').val() == 'PAYROL_INVOICE') {
    	action = payrolAction;
		enablePayrol();					
    }
    
    if (jQuery('#NewPurchaseInvoice_invoiceTypeId').val() == 'PETTYCASH_INVOICE') {
    	action = defaultAction;
		enablePettyCash();					
    }
    if (jQuery('#NewPurchaseInvoice_invoiceTypeId').val() != 'PETTYCASH_INVOICE' && jQuery('#NewPurchaseInvoice_invoiceTypeId').val() != 'PAYROL_INVOICE') {
    	action = defaultAction;
		disablePettyCash();	
		disablePayrol();
    }	
    jQuery('#NewPurchaseInvoice').attr("action", action);
    updateTimePeriodField();    
}


jQuery(document).ready(function() {		
	
	getRoleTypes();	
	setRoleTypeDropdown();	
	setInvoiceAction();
});



