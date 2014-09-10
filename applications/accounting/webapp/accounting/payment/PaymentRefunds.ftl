
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	
	function dialogue(content, title) {
		
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
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 	
 	$(document).ready(function(){
		$('.onlyNumbers').bind('keyup blur',function(){ 
			$(this).val( $(this).val().replace(/[^\0-9]/g,''));}
		);
	});
		
	function Alert(message, title)	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	function datepick()
		{		
			
			$( "#effectiveDate" ).datepicker({
				dateFormat:'dd/mm/yy',
				changeMonth: true,
				numberOfMonths: 1});
			$( "#challanDate" ).datepicker({
				dateFormat:'dd/mm/yy',
				changeMonth: true,
				numberOfMonths: 1,
				maxDate: new Date
			});
						
			$('#ui-datepicker-div').css('clip', 'auto');
			
		}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
	}
	function enableGenerateButton(){			
		   $("input[type=submit]").removeAttr("disabled", "disabled");
	}
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	
	function showPaymentRefundQTip(paymentId, partyIdTo, partyIdFrom, availableAmtToRefund, paymentTypeId, action, isDepositWithDrawPayment, finAccountTransTypeId) {
		
	
		var message = "";
		 		 
			message += "<html><head></head><body><form action='"+action+"' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=450>";
			message += "<br/><br/>";
			
			message += "<tr class='h4'>"+
							"<td align='left' class='h3' width='40%'>Amount To Refund :</td>"+
							"<td align='left' width='80%'><input class='onlyNumbers' type='text' id='amountToRefund' name='amountToRefund' onBlur='validateAmt("+availableAmtToRefund+")'/><span class='amtCaution'> Max Limit: Rs."+availableAmtToRefund+"</span></td>"+
					   "</tr>";
			
		    message += 
		    
		    			/*
		    			"<tr class='h4'>"+
		    				"<td align='left' class='h3' width='40%'>Payment Type :</td><td align='left' width='80%'>"+
		    					"<select name='paymentTypeId' id='paymentTypeId'  class='h4'>"+
									"<#if refundPaymentTypes?has_content>"+
										"<#list refundPaymentTypes as eachMethodType>"+
											"<option value='${eachMethodType.paymentTypeId?if_exists}' >${eachMethodType.description?if_exists}</option>"+
										"</#list>"+
									"</#if>"+            
								"</select>"+
							"</td>"+
						"</tr>"+
						*/
						
						"<#if paymentMethodsList?has_content>"+
							"<tr class='h4'>"+
			    				"<td align='left' class='h3' width='40%'>Payment Method Id:</td><td align='left' width='80%'>"+
			    					"<select name='paymentMethodId' id='paymentMethodId' onchange='setPaymentMethodFields()' class='h4'>"+
										"<#if paymentMethodsList?has_content>"+
											"<#list paymentMethodsList as eachPaymentMethod>"+
												"<option value='${eachPaymentMethod.paymentMethodId?if_exists}' >${eachPaymentMethod.description?if_exists}</option>"+
											"</#list>"+
										"</#if>"+            
									"</select>"+
								"</td>"+
							"</tr>"+
						"</#if>"+
						
						"<tr class='noncash'><td align='left' class='h3' width='40%'>Instrument Date:</td><td align='left' width='80%'><input class='h4' type='text' readonly id='effectiveDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='noncash'><td align='left' class='h3' width='40%'>Instrument Number:</td><td align='left' width='80%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></td></tr>" +
						"<tr class='noncash'>"+
							"<td align='left' class='h3' width='40%'>Issuing Authority:</td><td align='left' width='80%'>"+
								"<input class='h4' type='text' id='issuingAuthority' name='issuingAuthority'/>"+
								"<input class='h4' type='hidden' id='partyIdFrom' name='partyIdFrom' value='"+partyIdTo+"'/>"+
								"<input class='h4' type='hidden' id='partyIdTo' name='partyIdTo' value='"+partyIdFrom+"'/>"+
								"<input class='h4' type='hidden' id='paymentId' name='paymentId' value='"+paymentId+"'/>"+
								"<input class='h4' type='hidden' id='statusId' name='statusId' value='PMNT_SENT'/>"+
								"<input class='h4' type='hidden' id='paymentTypeId' name='paymentTypeId' value='"+paymentTypeId+"'/>"+
								"<input class='h4' type='text' id='isDepositWithDrawPayment' name='isDepositWithDrawPayment' value='"+isDepositWithDrawPayment+"'/>"+
								"<input class='h4' type='text' id='finAccountTransTypeId' name='finAccountTransTypeId' value='"+finAccountTransTypeId+"'/>"+
							"</td>"+
						"</tr>" +
						"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				
				message +=	"</table></form><br/><br/></body></html>";
			
		var title = "Payment Entry : ";
		Alert(message, title);
	}
	
	function showApPaymentRefundQTip(paymentId, partyIdTo, partyIdFrom, availableAmtToRefund, paymentTypeId, action, isDepositWithDrawPayment, finAccountTransTypeId) {
		
	
		var message = "";
		 		 
			message += "<html><head></head><body><form action='"+action+"' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=450>";
			message += "<br/><br/>";
			
			message += "<tr class='h4'>"+
							"<td align='left' class='h3' width='40%'>Amount To Refund :</td>"+
							"<td align='left' width='80%'><input class='onlyNumbers' type='text' id='amountToRefund' name='amountToRefund' onBlur='validateAmt("+availableAmtToRefund+")'/><span class='amtCaution'> Max Limit: Rs."+availableAmtToRefund+"</span></td>"+
					   "</tr>";
			
		    message += 
		    
		    			/*
		    			"<tr class='h4'>"+
		    				"<td align='left' class='h3' width='40%'>Payment Type :</td><td align='left' width='80%'>"+
		    					"<select name='paymentTypeId' id='paymentTypeId'  class='h4'>"+
									"<#if refundPaymentTypes?has_content>"+
										"<#list refundPaymentTypes as eachMethodType>"+
											"<option value='${eachMethodType.paymentTypeId?if_exists}' >${eachMethodType.description?if_exists}</option>"+
										"</#list>"+
									"</#if>"+            
								"</select>"+
							"</td>"+
						"</tr>"+
						*/
						
						"<#if paymentMethodTypeList?has_content>"+
							"<tr class='h4'>"+
			    				"<td align='left' class='h3' width='40%'>Payment Method Type:</td><td align='left' width='80%'>"+
			    					"<select name='paymentMethodTypeId' id='paymentMethodTypeId' onchange='setPaymentMethodTypeFields()' class='h4'>"+
										"<#if paymentMethodTypeList?has_content>"+
											"<#list paymentMethodTypeList as eachPaymentMethodType>"+
												"<option value='${eachPaymentMethodType.paymentMethodTypeId?if_exists}' >${eachPaymentMethodType.description?if_exists}</option>"+
											"</#list>"+
										"</#if>"+            
									"</select>"+
								"</td>"+
							"</tr>"+
						"</#if>"+
						
						"<tr class='noncash'><td align='left' class='h3' width='40%'>Instrument Date:</td><td align='left' width='80%'><input class='h4' type='text' readonly id='effectiveDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='noncash'><td align='left' class='h3' width='40%'>Instrument Number:</td><td align='left' width='80%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></td></tr>" +
						"<tr class='noncash'>"+
							"<td align='left' class='h3' width='40%'>Issuing Authority:</td><td align='left' width='80%'>"+
								"<input class='h4' type='text' id='issuingAuthority' name='issuingAuthority'/>"+
								"<input class='h4' type='hidden' id='partyIdFrom' name='partyIdFrom' value='"+partyIdTo+"'/>"+
								"<input class='h4' type='hidden' id='partyIdTo' name='partyIdTo' value='"+partyIdFrom+"'/>"+
								"<input class='h4' type='hidden' id='paymentId' name='paymentId' value='"+paymentId+"'/>"+
								"<input class='h4' type='hidden' id='statusId' name='statusId' value='PMNT_RECEIVED'/>"+
								"<input class='h4' type='hidden' id='paymentTypeId' name='paymentTypeId' value='"+paymentTypeId+"'/>"+
								"<input class='h4' type='text' id='isDepositWithDrawPayment' name='isDepositWithDrawPayment' value='"+isDepositWithDrawPayment+"'/>"+
								"<input class='h4' type='text' id='finAccountTransTypeId' name='finAccountTransTypeId' value='"+finAccountTransTypeId+"'/>"+
							"</td>"+
						"</tr>" +
						"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				
				message +=	"</table></form><br/><br/></body></html>";
			
		var title = "Payment Entry : ";
		Alert(message, title);
	}
	
	
	function setPaymentMethodFields() { 
		var str = $("#paymentMethodId option:selected").text();  	
		if((str.search(/(CASH)+/g) >= 0) ||(str.search(/(CREDITNOTE)+/g) >= 0)|| (str.search(/(DEBITNOTE)+/g) >= 0) ||(str.search(/(Cash)+/g) >= 0) ||(str.search(/(cash)+/g) >= 0) ){
			$('.noncash').hide();
		}	
		else{
			$('.noncash').show();
		}
	}
	function setPaymentMethodTypeFields() { 
		var str = $("#paymentMethodTypeId option:selected").text();  	
		if((str.search(/(CASH)+/g) >= 0) ||(str.search(/(CREDITNOTE)+/g) >= 0)|| (str.search(/(DEBITNOTE)+/g) >= 0) ||(str.search(/(Cash)+/g) >= 0) ||(str.search(/(cash)+/g) >= 0) ){
			$('.noncash').hide();
		}	
		else{
			$('.noncash').show();
		}
	}
	
	
	function validateAmt(availableAmtToRefund) { 
	
		/*
		if( ($("#amountToRefund").val()) > availableAmtToRefund ){
			$("#amountToRefund").css('background', 'yellow'); 
	       	setTimeout(function () {
	           	$("#amountToRefund"	).css('background', 'white').focus(); 
	       	}, 800);
	       	$(".amtCaution").css("color", "red");
	       
			disableGenerateButton();
		}
		else{
			$(".amtCaution").css("color", "green");
			enableGenerateButton();
		}
		*/
	}
	
	
</script>
