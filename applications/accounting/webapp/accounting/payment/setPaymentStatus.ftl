
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
				populateParams();
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
	
	function datepick()	{
	  var startDate = new Date('${payment.paymentDate?if_exists}');		
		$( "#transactionDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: false,
			minDate:startDate,
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
	
	
	function paymentStatusToolTip(checkFlag){
		var message = "";
		testFlag = checkFlag;
		message += "<form action='setPaymentStatus' method='post' onsubmit='return disableButton();'><table cellspacing=10 cellpadding=10>" ; 		
			message += "<tr class='h3'><td align='left' class='h3' width='40%'>Transaction Date:</td><td align='left' width='60%'><input class='h3' type='text' id='transactionDate' name='transactionDate' onmouseover='datepick()' size='17' readonly/></td></tr>";
			            if(testFlag == true && (paymentMethodType == 'FUND_TRANSFER' || paymentMethod == 'PAYMENTMETHOD4' || paymentMethod == 'PAYMENTMETHOD6')){
			            	message += "<tr class='h3'><td align='left' class='h3' width='40%'>Financial Account:</td><td align='right' width='60%'><select name='finAccountId' id='finAccountId'>";
			            	for(var i=0 ; i<finAccountIdsList.length ; i++){
								var innerList=finAccountIdsList[i];
								message += "<option value='"+innerList['finAccountId']+"'>"+ innerList['finAccountName'] + "</option>";
							}
							message += "</select></td></tr>";	              			             
			      		}
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
