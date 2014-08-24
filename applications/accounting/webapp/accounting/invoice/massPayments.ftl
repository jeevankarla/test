
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
					 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	 
	 var voucherPaymentMethodTypeMap = ${StringUtil.wrapString(voucherPaymentMethodJSON)!'{}'};
	    var paymentMethodList;
	    var paymentMethod;
	    
		var invoiceId;
		var voucherType;
		var amount;
		var partyName;
	    
		var methodOptionList =[];
		var payMethodList="";
	 
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
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	var invoiceId;
	var amt;
	
	
	function massInvoicePayments(form) {
		var test = $(form).html();
		var table = $("#parameters").parent().html();
		
		var partyIdName;
	    var partyId;
	    var fromPartyId;
		
		message = "";
		message += "<form action='makeMassInvoicePayments' method='post' onsubmit='return disableSubmitButton();'>";
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>";
		$('#parameters tr').each(function(i, row){
			var tdObj = $(row).find('td');
			message += "<tr>";
			$(tdObj).each(function(j, cell){
				var inv = $($(cell).find("#invId")).val();
				var amt = $($(cell).find("#amt")).val();
				partyId = $($(cell).find("#partyId")).val();
				fromPartyId = $($(cell).find("#fromPartyId")).val();
				partyIdName = $($(cell).find("#partyIdName")).val();
				voucherTypeId = $($(cell).find("#voucherTypeId")).val();
				if(voucherTypeId != undefined && voucherTypeId != "" && voucherPaymentMethodTypeMap != undefined){
				  payMethodList=voucherPaymentMethodTypeMap[voucherTypeId];
			 	 if(payMethodList != undefined && payMethodList != ""){
					$.each(payMethodList, function(key, item){
					  methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
					});
			 	   }
				 }else{
			 	   payMethodList=voucherPaymentMethodTypeMap['ALL'];
			 	   	$.each(payMethodList, function(key, item){
					  methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
					});
			 	   }
				  paymentMethodList = methodOptionList;		
				message += "<tr class='h2'><td align='left'class='h3' width='60%'>Invoice:</td><td><input type=hidden name='invoiceId_o_"+i+"' value='"+inv+"'>"+inv+"</td>";
				message += "<td align='left'class='h3' width='60%'>Amount:</td><td><input type=text name='amt_o_"+i+"' value='"+amt+"'></td></tr>";
			});
		});
		testStr = '';
			if(voucherTypeId != 'CASH'){
				testStr = "<tr class='h3'><td align='left'class='h3' width='60%'>Financial Account:</td><td align='left' width='60%'><select name='finAccountId' id='finAccountId'  class='h4'>"+
						"<#if finAccountList?has_content><#list finAccountList as finAccount><option value='${finAccount.finAccountId?if_exists}' >${finAccount.finAccountName?if_exists}</option></#list></#if>"+            
						"</select></td></tr>";
			}
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Payment Type :</td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId'  class='h4'>"+
						"<#if paymentTypes?has_content><#list paymentTypes as eachMethodType><option value='${eachMethodType.paymentTypeId?if_exists}' >${eachMethodType.description?if_exists}</option></#list></#if>"+            
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><#if parentTypeId?exists && parentTypeId=="SALES_INVOICE">Payment Method Type :<#else>Payment Method:</#if> </td><td align='left' width='60%'><select <#if parentTypeId?exists && parentTypeId=="SALES_INVOICE"> name='paymentMethodTypeId' <#else> name='paymentMethodId' </#if> id='paymentMethodTypeId'  class='h4'>"+
						"</select></td></tr>"+testStr+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='effectiveDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/><input class='h4' type='hidden' id='parentTypeId' name='parentTypeId' value='${parentTypeId?if_exists}'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='useFifo' value='TRUE'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='partyIdName' name='partyIdName' value='"+partyIdName+"'/><input class='h4' type='hidden' id='partyId' name='partyId' value='"+partyId+"'/><input class='h4' type='hidden' id='fromPartyId' name='fromPartyId' value='"+fromPartyId+"'/></td></tr>" +
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			  message +=	"</table></form></body></html>";
			message += "</tr>";	
		title = "<center>Make Bulk Invoice Payments <center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
</script>
