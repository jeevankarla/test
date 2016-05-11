
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
					 
	var orderData;			
	var orderId;
	var screenFlag;		
	var requestFlag;	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */

	 function amountOnchange(bal,actual){
	 	var amtappling=bal.value;
	 	var transtotal=actual.value;
	 	var Bal="";
	 	if(amtappling != undefined && transtotal != undefined){
	 	Bal=transtotal-amtappling;
	 	if(Bal <0){
	 	Bal=0;
	 	}
	 	Bal=Bal.toFixed(2);
	 	}
		 $('#bal').html("<h4>"+Bal+"</h4>");
	 }
	 
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
				
				$(document).ready(function(){
		             $("#inFavourOf").autocomplete({ source: partyAutoJson }).keydown(function(e){
		        });
	           });
				
				paymentFieldsOnchange();
						
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	
	
	
	function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1});
		$( "#paymentDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
	
	
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function cancelForm(){
		cancelShowSpinner();		 
		return false;
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function showOrderDetails() {
		var message = "";
		var title = "";
		if(orderData != undefined){
			var orderAmt = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Description</td><td align='center' class='h3'> Batch No.</td><td align='center' class='h3'> Qty</td><td align='center' class='h3'> Tax %</td><td align='center' class='h3'> Amount</td>";
			for (i = 0; i < orderData.length; ++i) {
				message += "<tr><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].itemDescription + "</td><td align='center' class='h4'>"+ orderData[i].batchNo +"</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td>";
				message += "<td align='center' class='h4'>" + orderData[i].taxPercent + "</td><td align='center' class='h4'>" + orderData[i].itemTotal + "</td></tr>";
				orderAmt = orderAmt+orderData[i].itemTotal;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();' class='submit'>Close</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br /><br /> Total Order Value = "+ orderAmt +" ";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
	function showOrderBatchDetails(flag) {
		
		requestFlag = flag;
		var message = "";
		var title = "";
		var action = "";
		if(requestFlag == "powder"){
			action = "editPowderOrderBatchNumber";
		}
		if(requestFlag == "amul"){
			action = "editAmulOrderBatchNumber";
		}
		if(requestFlag == "nandini"){
			action = "editNandiniOrderBatchNumber";
		}
		if(requestFlag == "fgs"){
			action = "editFGSOrderBatchNumber";
		}
		if(orderData != undefined){
		
			message += "<form action='"+action+"' method='post' onsubmit='return disableSubmitButton();'>";
			message += "<input type=hidden name=orderId value='"+orderId+"'><table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Batch No.</td></thead>";
			for (i = 0; i < orderData.length; ++i) {
				message += "<tr><td align='left' class='h4'><input type=hidden name='orderId_o_"+i+"' value='"+orderData[i].orderId+"'><input type=hidden name='orderItemSeqId_o_"+i+"' value='"+orderData[i].orderItemSeqId+"'>"+ orderData[i].itemDescription +"</td><td align='left' class='h4'>" + orderData[i].quantity + "</td><td align='center' class='h4'><input type=text name='batchNo_o_"+i+"' value='"+orderData[i].batchNo +"'></td></tr>";
			}
			message += "<tr class='h3'><td></td><td class='h3' align='left'><span align='center'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='EditBatchNo' class='smallSubmit'/></span></td><td class='h3' align='left'><span align='center'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br />";
			message += "</table></form>";
			Alert(message, title);
		}
		
	};
	
	function showSpinner() {
		var message = "hello";
		var title = "";
		message += "<div align='center' name ='displayMsg' id='pastDues_spinner'/><button onclick='return cancelForm();' class='submit'/>";
		Alert(message, title);
		
	};
	function cancelShowSpinner(){
		$('button').click();
		return false;
	}
	
	function fetchOrderDetails(order, requestFlag) {
		orderId = order;
		screenFlag = requestFlag;
		var dataJson = {"orderId": orderId, "screenFlag":screenFlag};
		showSpinner();
		jQuery.ajax({
                url: 'getOrderDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
						orderData = result["orderItemListJSON"];
					    requestFlag = result["requestFlag"];
					    cancelShowSpinner();
					  		
						if(screenFlag == "batchEdit"){
							showOrderBatchDetails(requestFlag);
						}
						else{
					  		showOrderDetails();
						}
               		}
               	}							
		});
	}
	
	
	function amountCheck()
	{
      var amount = $("#amount").val();
      var balance = $("#balance").val();
       if(parseFloat(amount) > parseFloat(balance)){
	      alert("Please Enter Amount Less Than The balance Total.");
	        $("#amount").val(balance);
	   }
	   else if(parseFloat(amount)<=0){
	      alert("Please Enter Amount Greater Than The 0.");
	        $("#amount").val(balance);
	   }
	}
	
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};
	
	
	function showPaymentEntry(orderId, partyId,partyName,grandTotal,balance, formActionVar) {
		var message = "";
		orderId = orderId;
		partyId = partyId;
		
		
		
		grandTotal = grandTotal;
		partyName= partyName;
		var formAction = 'createOrderPayment';
		if(formActionVar){
			formAction = formActionVar;
		}
		
		message += "<html><head></head><body><form action='"+formAction+"' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Retailer Code :</td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='partyId' value='"+partyId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Method Type :</td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						<#list PaymentMethodType as payment>
						"<option value='${payment.paymentMethodTypeId}' >${payment.description}</option>"+
	                   </#list>
					    "</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount'  name='amount' value='"+grandTotal+"'  onblur='javascript: amountCheck();'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
						<#-->"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+ -->
						"<tr class='h3'><td align='left' class='h3' width='60%'>Issue Authority/ Bank :</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='grandTotal' name='grandTotal' value='"+grandTotal+"'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+partyName +" [ "+partyId+" ]";
		Alert(message, title);
	}



   function showPaymentEntryForIndentPayment(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		orderId = orderId;
		partyId = partyId;
		
		
		
		//finalBal = grandTotal-balance;
		
		
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		
		
		grandTotal = grandTotal;
		partyName= partyName;
		
		
		message += "<html><head></head><body><form action='createOrderPayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		
		
		/*   if(paymentList[0].paymentId != "NoAdvance"){
			
			 message += "<tr class='h3'><td align='left' class='h3' width='50%'>Payment Id</td><td align='left' class='h3' width='50%'>Amount</td><td align='left' class='h3' width='50%'>Balance</td><td align='left' class='h3' width='50%'>Select Balance</td></tr>";          
	        for (i = 0; i < paymentList.length; i++) {
		         if(paymentList[i].balance!=0)
		         {
		         message += "<tr class='h3'><td align='left' class='h3' width='50%'><pre>" + paymentList[i].paymentId + "</pre></td><td align='left' width='50%'>" + paymentList[i].amount + "</td><td align='left'  width='60%'>" + paymentList[i].balance + "</td><td align='left' width='50%'><input class='h4' type='checkbox' id='allStatus' name='allStatus' value = '"+i+"' /></td></tr></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPaymentIds' value='"+paymentList[i].paymentId+"'/></td></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPayments' value='"+paymentList[i].amount+"'/></td></tr>";
		         }
		       }
		  } */
		  
		  
			message += "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Retailer Code :</font></td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='partyId' value='"+partyId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Method Type :</font></td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						<#list PaymentMethodType as payment>
						"<option value='${payment.paymentMethodTypeId}' <#if (payment.paymentMethodTypeId == 'CHEQUE')>selected='selected'</#if>>${payment.description}</option>"+
	                   </#list> 
					    "</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount' max='"+balance+"' step='.01' onblur='javascript:amountOnchange(this,balance);amountCheck()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><label  align='left' id='bal'>"+balance+"</label></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'>"+grandTotal+"</td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%' id='checkNoLabel' style='color:green'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
						<#-->"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+ -->
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Issue Authority/ Bank :</font></td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='grandTotal' name='grandTotal' value='"+grandTotal+"'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+partyName +" [ "+partyId+" ]";
		Alert(message, title);
	}



  function showPaymentEntryForDepotIndentPayment(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		orderId = orderId;
		partyId = partyId;
		
		
		
		//finalBal = grandTotal-balance;
		
		
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		
		grandTotal = grandTotal;
		partyName= partyName;
		
		
		message += "<html><head></head><body><form action='createOrderPaymentDepot' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		
		/*
		   if(paymentList[0].paymentId != "NoAdvance"){
			
			 message += "<tr class='h3'><td align='left' class='h3' width='50%'>Payment Id</td><td align='left' class='h3' width='50%'>Amount</td><td align='left' class='h3' width='50%'>Balance</td><td align='left' class='h3' width='50%'>Select Balance</td></tr>";          
	        for (i = 0; i < paymentList.length; i++) {
		         if(paymentList[i].balance!=0)
		         {
		         message += "<tr class='h3'><td align='left' class='h3' width='50%'><pre>" + paymentList[i].paymentId + "</pre></td><td align='left' width='50%'>" + paymentList[i].amount + "</td><td align='left'  width='60%'>" + paymentList[i].balance + "</td><td align='left' width='50%'><input class='h4' type='checkbox' id='allStatus' name='allStatus' value = '"+i+"' /></td></tr></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPaymentIds' value='"+paymentList[i].paymentId+"'/></td></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPayments' value='"+paymentList[i].amount+"'/></td></tr>";
		         }
		       }
		  }
		   */
		  
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Retailer Code :</td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='partyId' value='"+partyId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Method Type :</td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						<#list PaymentMethodType as payment>
						"<option value='${payment.paymentMethodTypeId}' >${payment.description}</option>"+
	                   </#list>
					    "</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount' step='.01' onblur='javascript:amountOnchange(this,balance);amountCheck()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Balance :</td><td align='left' width='60%'><label  align='left' id='bal'>"+balance+"</label></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Total :</td><td align='left' width='60%'>"+balance+"</td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
						"<tr class='h3'><td id='checkNoLabel' align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
						<#-->"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+ -->
						"<tr class='h3'><td align='left' class='h3' width='60%'>Issue Authority/ Bank :</td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='grandTotal' name='grandTotal' value='"+grandTotal+"'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+partyName +" [ "+partyId+" ]";
		Alert(message, title);
	}







    function showPaymentEntryForIndentorIndentPayment(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		orderId = orderId;
		partyId = partyId;
		
		//finalBal = grandTotal-balance;
		
		
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		
		
		grandTotal = grandTotal;
		partyName= partyName;
		
		
		message += "<html><head></head><body><form action='createIndentorOrderPayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		
		/*
		
		   if(paymentList[0].paymentId != "NoAdvance"){
			
			 message += "<tr class='h3'><td align='left' class='h3' width='50%'>Payment Id</td><td align='left' class='h3' width='50%'>Amount</td><td align='left' class='h3' width='50%'>Balance</td><td align='left' class='h3' width='50%'>Select Balance</td></tr>";          
	        for (i = 0; i < paymentList.length; i++) {
		         if(paymentList[i].balance!=0)
		         {
		         message += "<tr class='h3'><td align='left' class='h3' width='50%'><pre>" + paymentList[i].paymentId + "</pre></td><td align='left' width='50%'>" + paymentList[i].amount + "</td><td align='left'  width='60%'>" + paymentList[i].balance + "</td><td align='left' width='50%'><input class='h4' type='checkbox' id='allStatus' name='allStatus' value = '"+i+"' /></td></tr></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPaymentIds' value='"+paymentList[i].paymentId+"'/></td></tr>";
		         message +="<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='advPayments' value='"+paymentList[i].amount+"'/></td></tr>";
		         }
		       }
		  }
		  
		    */
			message += "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Retailer Code :</font></td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='partyId' value='"+partyId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Method Type :</font></td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						<#list PaymentMethodType as payment>
						"<option value='${payment.paymentMethodTypeId}' <#if (payment.paymentMethodTypeId == 'CHEQUE')>selected='selected'</#if>>${payment.description}</option>"+
	                   </#list>
					    "</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount' max='"+balance+"' step='.01' onblur='javascript:amountOnchange(this,balance);'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><label  align='left' id='bal'>"+balance+"</label></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'>"+balance+"</td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Cheque No:</font></td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
						<#-->"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+ -->
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Issue Authority/ Bank :</font></td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='grandTotal' name='grandTotal' value='"+grandTotal+"'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues  Payment : "+partyName +" [ "+partyId+" ]";
		Alert(message, title);
	}


   



	
	function paymentFieldsOnchange(){
  
     
      var paymentType =  $("#paymentTypeId").val()
      $("#inFavourOf").parent().parent().hide();
      $("#comments").parent().parent().hide();
      $("#issuingAuthority").parent().parent().hide();
      $("#paymentRefNum").parent().parent().hide();
     
     
     if(paymentType == 'CHEQUE'){
     
     $("#inFavourOf").parent().parent().show();
     $("#comments").parent().parent().show();
     $("#issuingAuthority").parent().parent().show();
     $("#paymentRefNum").parent().parent().show(); 
     $("#checkNoLabel").html("Cheque No :"); 
                   
    }
   else if(paymentType == 'FT_PAYIN'){
      $("#issuingAuthority").parent().parent().show();
    }
    else if('CASH_PAYIN'){
      $("#paymentRefNum").parent().parent().show(); 
       $("#checkNoLabel").html("Receipt No :"); 
    }
  }
	
	
	/*
	function showPayment(orderPreferenceId) {
		var message = "";
		
		var orderPreferenceId = orderPreferenceId;
		message += "<html><head></head><body><form action='createCustomPaymentFromPreference' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
			//message += "<br/><br/>";
			
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Retailer Code :</td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='orderPaymentPreferenceId' value='"+orderPreferenceId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Amount :</td><td align='left' width='60%'><input class='h4' type='text' id='amount' name='amount'/></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderPreferenceId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Order Preference Number : [ "+orderPreferenceId+" ]";
		Alert(message, title);
		
		
	}
	
	  */
	
	
		
    
    function fetchOrderInformation(order) {
		orderId = order;
		var dataJson = {"orderId": orderId};
		showSpinner();
		jQuery.ajax({
                url: 'getOrderInformation',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
						orderData = result["orderInformationDetails"];
						
						
						showOrderInformation();
						
               		}
               	}							
		});
	}
	
    
    function showOrderInformation() {
		var message = "";
		var title = "";
		if(orderData != undefined){
			var orderAmt = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Product Name</td><td align='center' class='h3'> Remarks</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Unit Price</td><td align='center' class='h3'> Amount</td><td align='center' class='h3'> Quota Qty</td><td align='center' class='h3'> Discount</td><td align='center' class='h3'> Other Chgs</td><td align='center' class='h3'> Payable</td>";
			for (i = 0; i < orderData.length; ++i) {
			  	message += "<tr><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].prductName + "</td><td align='left' class='h4'>" + orderData[i].remarks + "</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td><td align='center' class='h4'>"+ orderData[i].unitPrice +"</td><td align='center' class='h4'>"+ orderData[i].itemAmt +"</td><td align='center' class='h4'>"+ Math.round(orderData[i].quotaAvbl) +"</td><td align='center' class='h4'>"+ orderData[i].adjustmentAmount +"</td><td align='center' class='h4'>"+ orderData[i].otherCharges +"</td><td align='center' class='h4'>"+ orderData[i].payableAmt +"</td>";
			  	orderAmt = orderAmt+orderData[i].payableAmt;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();' class='submit'>Close</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br /> Total Order Value = "+ orderAmt +" ";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
    
    function cancelOrderCaution(orderId,salesChannelEnumId,partyId) {
		var message = "";
		
		var orderId = orderId;
		message += "<html><head></head><body><form action='createCustomPaymentFromPreference' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table hight=400 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' width=100% class='h3' >Do You Really Want to Cancel This Order</td></tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			
            message +="<tr class='h3'><td align='center' class='h3'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Order Preference Number : [ "+orderId+" ]";
		Alert(message, title);
		
		
	}
    
	
	
</script>
