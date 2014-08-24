
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<style type="text/css">
.ui-tooltip1, .qtip{
	position: absolute;
	left: -10000em;
	top: -10000em;
 
	max-width: 550px; /* Change this? */
	min-width: 450px; /* ...and this! */
}
</style>
<script type="application/javascript">
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 var booth;
	 var dueAmount;
	 var paymentMethod;
	 var facilityName;
	 var route;
	function dialogue(content, title) {
		/* 
		 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
		 * out-of-DOM element as our target instead of an actual element like document.body
		 */
		$('<div />').qtip(
		{
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
					populateAccountDetails();
					$('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 	function paymentFieldsOnchange(){
 		var str=jQuery("#paymentMethodTypeId").val();
		if(str == undefined){
			return;
		}
		if(str.search(/(FT)+/g) >= 0){
			htmlMsg = "";
			htmlMsg += "<select name='issuingAuthority' id='issuingAuthority' class='h4'>"+
						"<option value=''>-- Select -- </option>"+
						"<#if fundTransferAccounts?has_content><#list fundTransferAccounts as eachFinAccount><option value='${eachFinAccount.finAccountId?if_exists}' >${eachFinAccount.finAccountName?if_exists}</option></#list></#if>"+            
						"</select>";
			jQuery("#bankSelect").html('');
			jQuery("#bankSelect").html(htmlMsg);
			$( this ).css(".ui-tooltip");
		}
		else{
			jQuery("#bankSelect").html('');
			htmlChqMsg = "";
			htmlChqMsg += "<input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' value='${chequeBankName?if_exists}'/>";
			jQuery("#bankSelect").html(htmlChqMsg);
		}
		$( this ).css();	
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
		$( "#paymentDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	function submitFormParam(){
		var payDate = $("#paymentDate").val();
		$("#effectiveDate").val(payDate);
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	var accountInfo;
	//var boothId;
	//var dueAmount;
	function showPaymentEntryQTip(boothId, duesPay, boothName) {
		var message = "";
		booth = boothId;
		dueAmount = duesPay;
		facilityName = boothName;
		message += "<html><head></head><body><form action='createInstitutionPayment' method='post' onsubmit='return disableGenerateButton();'><table width='100%' cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='40%'>Institution Code :</td><td align='left' width='60%'><input class='h4' type='label' id='facilityId' name='facilityId' value='${facilityId?if_exists}' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Payment Method Type :</td><td align='left' width='60%'><select name='paymentMethodTypeId' id='paymentMethodTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						"<#if paymentMethodList?has_content><#list paymentMethodList as eachMethodType><option value='${eachMethodType.paymentMethodTypeId?if_exists}' >${eachMethodType.description?if_exists}</option></#list></#if>"+            
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='40%'>Issue Authority/ Bank :</td><td align='left' width='60%'><span id='bankSelect'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></span></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='40%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' id='paymentDate' name='paymentDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='40%'>Payment Ref. No. :</td><td align='left' width='60%'><input class='h4' type='text' id='paymentRefNum' name='paymentRefNum' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='40%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='40%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='supplyDate' value='${paymentDate?if_exists}'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='40%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='useFifo' value='TRUE'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+facilityName +" [ "+booth+" ]";
		Alert(message, title);
		getFinaccountDetails(boothId);
		
	}	
	
	function showPaymentEntry(routeId, boothId ,amount, paymentMethodType, boothName) {
		var message = "";
		route = routeId;
		booth = boothId;
		dueAmount = amount;
		facilityName = boothName;
		paymentMethod = paymentMethodType;
		message += "<html><head></head><body><form action='createChequePayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			message += "<input type='hidden' name='routeId' id='routeSearchId'><input class='h4' type='hidden' id='effectiveDate' name='instrumentDate'/><input type='hidden' name='paymentMethodTypeId' id='paymentMethodTypeId' value='${parameters.paymentMethodTypeId?if_exists}'><input type='hidden' name='paymentPurposeType' id='paymentPurposeType' value='ROUTE_MKTG'><input type='hidden' name='subTabItem' id='subTabItem' value='${parameters.subTabItem?if_exists}'>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Retailer Code :</td><td align='left' width='60%'><input class='h4' type='label' id='facilityId' name='facilityId' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Issue Authority/ Bank :</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' id='paymentDate' name='paymentDate' value='${defaultEffectiveDate?if_exists}' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Num :</td><td align='left' width='60%'><input class='h4' type='text' id='paymentRefNum' name='paymentRefNum' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='supplyDate' value='${paymentDate}'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+facilityName +" [ "+booth+" ]";
		Alert(message, title);
		getFinaccountDetails(booth);
		
	}
	function getFinaccountDetails(facilityId){
		$.ajax({
             type: "POST",
             url: 'getFacilityFinAccountInfo',
             data: {facilityId : facilityId},
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	   accountInfo = result["accountInfo"];
            		populateAccountDetails();   
            	      	   
               }
               
             } 
        });
	}
	function populateAccountDetails(){
		if(accountInfo != null){
			jQuery("#amount").val(dueAmount);
			jQuery("#facilityId").val(accountInfo["facilityId"]);
			jQuery("#issuingAuthority").val(accountInfo["finAccountName"]);
			jQuery("#issuingAuthorityBranch").val(accountInfo["finAccountBranch"]);
		}
		jQuery("#facilityId").val(booth);
		jQuery("#amount").val(dueAmount);
		jQuery("#routeSearchId").val(route);
	}
	
</script>
