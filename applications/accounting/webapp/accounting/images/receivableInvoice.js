
function setInvoiceTypeDropdown(onlyGroupInvoices)
{	var optionList = '';
	var allInvoiceTypes=roleTypeListValues['roleTypeList'];
	for(var i=0 ; i < allInvoiceTypes.length ; i++){
		var invoiceType = allInvoiceTypes[i];
		if(onlyGroupInvoices && 'N' == invoiceType['isGroupInvoicable']) {
			continue;
		}
		optionList += "<option value = " + invoiceType['invoiceTypeId'] + " >" + invoiceType['description'] + "</option>";                    		        			
    }
    jQuery("[name='invoiceTypeId']").html(optionList);
}

function enablePartyGroup() {
	jQuery('#NewSalesInvoice table tbody tr:eq(2) td:eq(0)').show();	
	jQuery('#NewSalesInvoice table tbody tr:eq(2) td:eq(1)').show();     	
	jQuery('#NewSalesInvoice table tbody tr:eq(4) td:eq(2)').hide();	
	jQuery('#NewSalesInvoice table tbody tr:eq(4) td:eq(3)').hide();	
	jQuery('#NewSalesInvoice_roleTypeId_title').parent().parent().hide();		
	jQuery('#NewSalesInvoice_description').parent().parent().hide();	
	jQuery('#NewSalesInvoice_invoiceMessage').parent().parent().hide();	
	jQuery("input[name='partyId']").removeClass("required");
	setInvoiceTypeDropdown(true);
	invoiceTypeOnChangeHandler();	
}

function disablePartyGroup() {
	jQuery('#NewSalesInvoice table tbody tr:eq(2) td:eq(0)').hide();	
	jQuery('#NewSalesInvoice table tbody tr:eq(2) td:eq(1)').hide();      	
	jQuery('#NewSalesInvoice table tbody tr:eq(4) td:eq(2)').show();	
	jQuery('#NewSalesInvoice table tbody tr:eq(4) td:eq(3)').show();  	
	jQuery('#NewSalesInvoice_roleTypeId_title').parent().parent().show();	
	jQuery('#NewSalesInvoice_description').parent().parent().show();	
	jQuery('#NewSalesInvoice_invoiceMessage').parent().parent().show();	
	jQuery("input[name='partyId']").addClass("required");	
	setInvoiceTypeDropdown(false);	
	invoiceTypeOnChangeHandler();	
}

function setPartyGroupAction() {
    var defaultAction = 'createInvoice';	
    var groupAction = 'createPartyGroupInvoice';
    var action;
    if (jQuery('input[name=groupInvoice]').is(':checked')) {
    	action = groupAction;
		enablePartyGroup();	
    }
    else {
    	action = defaultAction;
    	disablePartyGroup();
    }
    jQuery('#NewSalesInvoice').attr("action", action);
}

function invoiceTypeOnChangeHandler() {
	setRoleTypeDropdown();	
    updateTimePeriodField();    
}

jQuery(document).ready(function() {

	
	getRoleTypes();	
	invoiceTypeOnChangeHandler();	
	var action;
	if (jQuery('select[name=partyClassificationGroupId] option:selected').val().length == 0)
	{
		jQuery('#NewSalesInvoice table tbody tr:eq(1) td:eq(0)').hide();	
		jQuery('#NewSalesInvoice table tbody tr:eq(1) td:eq(1)').hide();	 		
	}
	setPartyGroupAction();  
    jQuery('#NewSalesInvoice').attr("action", action);        
});



