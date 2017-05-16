
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	var finAccountIdsList;
	var testFlag;
	var paymentMethodType;
	var paymentMethod;
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
				populateParams();chequeBounceFieldsOnchange();
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	function chequeBounceFieldsOnchange(){
		
		jQuery("input[name='returnDate']").parent().parent().hide();
		jQuery("input[name='fineAmount']").parent().parent().hide();
		var str=jQuery("select[name='chequeBounce']").val();
		if(str == "Y"){	
		
			jQuery("input[name='returnDate']").parent().parent().show();
			jQuery("input[name='fineAmount']").parent().parent().show();
		}else{
			$('#bounceReason').attr('value','');
			
			jQuery("input[name='returnDate']").parent().parent().hide();
			jQuery("input[name='fineAmount']").parent().parent().hide();
			
		}
	}
	function datepick2()
	{		
		$( "#returnDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	function datepick()	{
	  var startDate = new Date('${payment.paymentDate?if_exists}');		
		$( "#transactionDate" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			//onSelect: function(onlyDate){ // Just a work around to append current time without time picker
	        //    var nowTime = new Date(); 
	        //    onlyDate=onlyDate+" "+nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();
	        //    $('#transactionDate').val(onlyDate);
	        //},
	        changeMonth: false,
			
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	function datepick1()	{

		$( "#instrumentDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	//disable the generate button once the form submited
	function disableButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	
	function setCancelDomObj(thisObj){
		cancelDom = thisObj;
	}
	function submitForm(){		 
		
		var option = $("#chequeBounce").val();
		if(option=='Y')
		{
		var returnDate = $("#returnDate").val();
		if(returnDate == undefined || returnDate == ""){
		alert("Please enter the return Date");
		return false;
		}
		var amt = $("#fineAmount").val();
		if(amt == undefined || amt == ""){
		alert("Please enter the Fine Amount");
		return false;
		}
		}
		var comments = $("#bounceReason").val();
		if(comments == undefined || comments == ""){
		alert("Please enter the Reason for to Void The Payment");
		return false;
		}
		disableGenerateButton();
		 jQuery('#invoicestatuschange').submit();
	}
	
	function getPaymentDescription(paymentId){
	  var message = "";
		message += "<html><head></head><body><form name='invoicestatuschange' id='invoicestatuschange' action='cancelPaymentForChequeReturns' method='post' onsubmit='return submitForm();'><table cellspacing=10 cellpadding=10 width=400>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Bounce/Returns :</td><td align='left' width='60%'><select name='chequeBounce'  id='chequeBounce' onchange='javascript:chequeBounceFieldsOnchange();' class='h4'>"+
						"<option value='N' >No</option>"+  
						"<option value='Y' >Yes</option>"+          
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Return Date :</td><td align='left' width='70%'><input class='h4' type='text' id='returnDate' name='returnDate' onmouseover='datepick2()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Reason For Bounce/Return :</td><td align='left' width='70%'><input class='h4' type='text' id='bounceReason' name='bounceReason' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Amount:</td><td align='left' width='70%'><input class='h4' type='text' id='fineAmount' name='fineAmount' /></td></tr>" +
						"<tr class='h3'><td></td><td><input type='hidden' name='paymentId' id='paymentId' value='"+paymentId+"'></td></tr>"+
					"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' /></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
					message +=	"</table></form></body></html>";
		var title = "Cancel Payment For paymentId : "+paymentId;
		Alert(message, title);
}
	
	function paymentStatusToolTip(checkFlag){
		var message = "";
		testFlag = checkFlag;
		message += "<form action='setPaymentStatus' method='post' onsubmit='return disableButton();'><table cellspacing=10 cellpadding=10>" ; 		
		            if(testFlag == true && (paymentMethodType == 'FT_PAYIN' || paymentMethodType == 'FUND_TRANSFER' || paymentMethod == 'CHEQUE_PAYIN' || paymentMethodType == 'CHEQUE' || paymentMethodType == 'CHEQUE_PAYOUT'	|| paymentMethodType == 'FT_PAYOUT')){
		            	message += "<tr class='h3'><td align='left' class='h3' width='40%'>Financial Account:</td><td align='right' width='60%'><select name='finAccountId' id='finAccountId'>";
		            	for(var i=0 ; i<finAccountIdsList.length ; i++){
							var innerList=finAccountIdsList[i];
							message += "<option value='"+innerList['finAccountId']+"'>"+ innerList['finAccountName'] + "</option>";
						}
						message += "</select></td></tr>";	              			             
		      		}
		if(paymentMethodType == 'FUND_TRANSFER' || paymentMethodType == 'CHEQUE_PAYIN' || paymentMethodType == 'CHEQUE' || paymentMethodType == 'FT_PAYIN' || paymentMethodType == 'CHEQUE_PAYOUT'|| paymentMethod == 'PAYMENTMETHOD4' || paymentMethod == 'PAYMENTMETHOD6'){      		
			message +=  "<tr class='h3'><td align='left' class='h3' width='40%'>Instrument Number:</td><td align='left' width='60%'><input class='h4' type='text' class='required' id='paymentRefNum' name='paymentRefNum'/></td></tr>";
			message +=	"<tr class='h3'><td align='left' class='h3' width='40%'>Instrument Date:</td><td align='left' width='60%'><input class='h4' type='text' class='required' readonly id='instrumentDate' name='instrumentDate' onmouseover='datepick1()' required /></td></tr>";
			if(paymentMethod == 'PAYMENTMETHOD4' || paymentMethodType == 'CHEQUE_PAYIN'){
					message +=	"<tr class='h3'><td align='left' class='h3' width='40%'>Cheque In favour of:</td><td align='left' width='60%'><input class='h4' type='text'  id='inFavourOf' name='inFavourOf'value='${chequeInFavour?if_exists}'/></td></tr>";
			}
		}
		message +=	"<input type='hidden' name='depositReceiptFlag' id='depositReceiptFlag' value='Y'/>";
		message += "<tr class='h3'><td align='left' class='h3' width='40%'>Transaction Date:</td><td align='left' width='60%'><input class='h3' type='text' id='transactionDate' name='transactionDate' onmouseover='datepick()' size='17' readonly/></td></tr>";
		message += "<input type='hidden' name='paymentId' id='paymentId' value='${payment.paymentId?if_exists}'/> <input type='hidden' name='statusId' id='statusId' value='${statusId?if_exists}'/>"+
					"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='setPaymentStatus' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "<h2><center>Select Transaction Date</center></h2>";
		Alert(message, title);
	}
	
	function showSetPaymentStatus() {	
		finAccountNameList();
	};
	var estimatedDate;	
	var dateFormatted;
	var shipmentTypeId;
	
	function populateParams(){
		paymentMethodType = '${payment.paymentMethodTypeId?if_exists}';
		paymentMethod = '${payment.paymentMethodId?if_exists}';
	    jQuery("#statusId").val(jQuery("input[name=statusId]").val());
	    jQuery("#transactionDate").val('${payment.paymentDate?if_exists}');
	};
	
	
	function finAccountNameList() {
		var paymentId = jQuery("input[name='paymentId']").val();
        jQuery.ajax({
            url: 'getFinAccountIdsListForPayment',
            type: 'POST',
            async: true,
            data: {paymentId : paymentId } , 
            success: function(result){ finAccountIdsList = result["finAcountIdList"],testFlag = result["flag"];
             	paymentStatusToolTip(testFlag);   
                /*if (finAccountIdsList) {	
                     var optionList;	       				        	
			        	for(var i=0 ; i<finAccountIdsList.length ; i++){
							var innerList=finAccountIdsList[i];	              			             
			                optionList += "<option value = " + innerList['finAccountId'] + " >" + innerList['finAccountName'] + "</option>";          			
			      		}//end of main list for loop
				    jQuery("[name='finAccountId']").html(optionList);
	  			}*/
            }
        });
        populateParams();
	}	
	
	
</script>
