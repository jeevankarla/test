
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<input type="hidden" name="PayOrderId" id="PayOrderId">
<input type="hidden" name="PayPartyId" id="PayPartyId">
<input type="hidden" name="PayPartyName" id="PayPartyName">
<input type="hidden" name="PaygrandTotal" id="PaygrandTotal">
<input type="hidden" name="Paybalance" id="Paybalance">

<script type="application/javascript">
	var BankListJSON = ${StringUtil.wrapString(BankListJSON)!'[]'};				 
	var orderData;			
	var orderId;
	var screenFlag;		
	var requestFlag;	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */

	
	 
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
	
	
	var i = 1;
	
	function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1});
		$( "#paymentDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1
			});
			
			$( "#invoiceDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1
			});
			
			
		$( "#chequeDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});	
		$('#ui-datepicker-div').css('clip', 'auto');
		
		 i=1;
		$("#chequeDate").change(function() {
        var date = $(this).datepicker("getDate");
        var oneDay = 24*60*60*1000; 
        var diffDays = Math.round(Math.abs((date.getTime() - new Date().getTime())/(oneDay)));
         
         if(parseInt(diffDays) >= 90){
           alertForPayment();
         }
        
        
    });
		
		
	}
	
	
	function alertForPayment(){
	    if(i==1)
	    {
	      alert("Instrument Date is Greater Than 3 Months.");
	       i++;
	    }
	}
	
	
	
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function cancelForm(){
		//cancelShowSpinner();		 
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
       
       var bal = parseFloat(balance)-parseFloat(amount);
	   
	    if(parseFloat(amount)<=0){
	      alert("Please Enter Amount Greater Than The 0.");
	        $("#amount").val(balance);
    	   }else{
	        if(isNaN(bal)){
	        $("#bal").html(document.getElementById("#totAmt").innerText);
	        }
	        else
	        $("#bal").html(bal);
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
						"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='partyName' name='partyName' value='"+partyName+"'/></td></tr>"+
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
		partyName =  partyName;
		balance = balance;
		
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
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount'   onblur='javascript:amountCheck()' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><span  align='left' id='bal'>"+balance+"</span></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'><span  align='left' id='totAmt'>"+grandTotal+"</span></td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='cheqInFavLable' style='color:green'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='chequDateLable' style='color:green' >Chq Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='chequeDate' name='chequeDate' onmouseover='datepick()'/></td></tr>" + 
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

  
   function showPaymentEntryForIndentPaymentDC(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		
		orderId = orderId;
		partyId = partyId;
		
		//finalBal = grandTotal-balance;
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		grandTotal = grandTotal;
		partyName =  partyName;
		balance = balance;
		
		message += "<html><head></head><body><form action='createOrderPaymentDC' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
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
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount'   onblur='javascript:amountCheck()' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><span  align='left' id='bal'>"+balance+"</span></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'><span  align='left' id='totAmt'>"+grandTotal+"</span></td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='cheqInFavLable' style='color:green'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='chequDateLable' style='color:green' >Chq Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='chequeDate' name='chequeDate' onmouseover='datepick()'/></td></tr>" + 
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
  




function showPaymentEntryForIndentPortalPayment(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		
		orderId = orderId;
		partyId = partyId;
		
		//finalBal = grandTotal-balance;
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		grandTotal = grandTotal;
		partyName =  partyName;
		balance = balance;
		
		message += "<html><head></head><body><form action='createIndentorOrderPayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
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
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount'   onblur='javascript:amountCheck()' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><span  align='left' id='bal'>"+balance+"</span></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'><span  align='left' id='totAmt'>"+grandTotal+"</span></td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='cheqInFavLable' style='color:green'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='chequDateLable' style='color:green' >Chq Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='chequeDate' name='chequeDate' onmouseover='datepick()'/></td></tr>" + 
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


	 function showPaymentEntryForDepotPayment(orderId, partyId,partyName,grandTotal,balance) {
		var message = "";
		
		orderId = orderId;
		partyId = partyId;
		
		//finalBal = grandTotal-balance;
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		grandTotal = grandTotal;
		partyName =  partyName;
		balance = balance;
		
		message += "<html><head></head><body><form action='createDepotOrderPayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
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
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount'   onblur='javascript:amountCheck()' /></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><span  align='left' id='bal'>"+balance+"</span></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'><span  align='left' id='totAmt'>"+grandTotal+"</span></td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='cheqInFavLable' style='color:green'><font color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' value='NHDC' readonly /></td></tr>"+
                        "<tr class='h3'><td align='left' class='h3' width='60%' id='chequDateLable' style='color:green' >Chq Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='chequeDate' name='chequeDate' onmouseover='datepick()'/></td></tr>" + 
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
     $("#chequeDate").parent().parent().show(); 
     $("#checkNoLabel").html("Cheque No :");
     $("#cheqInFavLable").html("Chq.in favour:");
     $("#chequDateLable").html("Cheque Date :"); 
      $("#checkNoLabel").html("Cheque/DD No :");
     $("#cheqInFavLable").html("Chq.in/DD favour:");
     $("#chequDateLable").html("Cheque/DD Date :"); 
     
                   
    }
    else if(paymentType == 'DD'){
    
     $("#inFavourOf").parent().parent().show();
     $("#comments").parent().parent().show();
     $("#issuingAuthority").parent().parent().show();
     $("#paymentRefNum").parent().parent().show(); 
     $("#chequeDate").parent().parent().show();
     $("#checkNoLabel").html("Cheque No :");
     $("#cheqInFavLable").html("Chq.in favour:");
     $("#chequDateLable").html("Cheque Date :"); 
    
     var date =  $("#chequeDate").val();
    
    }
   else if(paymentType == 'FT_PAYIN'){
      $("#paymentRefNum").parent().parent().show(); 
      $("#issuingAuthority").parent().parent().show();
      $("#chequeDate").parent().parent().hide(); 
      //$("#checkNoLabel").html("Receipt No :");
      $("#checkNoLabel").html("Chq No/UTR No/DD :");
    }
    else if('CASH_PAYIN'){
      $("#paymentRefNum").parent().parent().show(); 
       $("#chequeDate").parent().parent().hide(); 
       $("#checkNoLabel").html("Receipt No :"); 
    } 
    $("#issuingAuthority").autocomplete({					
		source:  BankListJSON,
		select: function(event, ui) {
	       var selectedValue = ui.item.label;
	       $("#issuingAuthority").val(selectedValue);	
		}
	});
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
	
	
		
    
    function fetchOrderInformation(order,partyName) {
		orderId = order;
		
		partyName = partyName;
		
		var dataJson = {"orderId": orderId};
		//showSpinner();
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
						
						
						showOrderInformation(partyName);
						
               		}
               	}							
		});
	}
	
    
    function showOrderInformation(partyName) {
		var message = "";
		var title = "";
		
		
		$("#viewClicked").val("YES");
		
		if(orderData != undefined){
			var orderAmt = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			
			var orderType = orderData[0].orderType;
			
			if(orderType == "onbehalfof")
			message += "<thead><td align='center' class='h3'>Passbook NO</td><td align='center' class='h3'>Customer Name</td><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Product Name</td><td align='center' class='h3'> Remarks</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Unit Price</td><td align='center' class='h3'> Amount</td><td align='center' class='h3'> Quota Qty</td><td align='center' class='h3'> Discount</td><td align='center' class='h3'> Other Chgs</td><td align='center' class='h3'> Payable</td>";
			else
			message += "<thead><td align='center' class='h3'>Passbook NO</td><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Product Name</td><td align='center' class='h3'> Remarks</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Unit Price</td><td align='center' class='h3'> Amount</td><td align='center' class='h3'> Quota Qty</td><td align='center' class='h3'> Discount</td><td align='center' class='h3'> Other Chgs</td><td align='center' class='h3'> Payable</td>";
			
			
			for (i = 0; i < orderData.length; ++i) {
			
			    if(orderType == "onbehalfof")
			  	message += "<tr><td align='center' class='h4'>" + orderData[i].passNo + "</td><td align='center' class='h4'>" + orderData[i].partyName + "</td><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].prductName + "</td><td align='left' class='h4'>" + orderData[i].remarks + "</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td><td align='center' class='h4'>"+ orderData[i].unitPrice +"</td><td align='center' class='h4'>"+ orderData[i].itemAmt +"</td><td align='center' class='h4'>"+ Math.round(orderData[i].quotaAvbl) +"</td><td align='center' class='h4'>"+ orderData[i].adjustmentAmount +"</td><td align='center' class='h4'>"+ orderData[i].otherCharges +"</td><td align='center' class='h4'>"+ orderData[i].payableAmt +"</td>";
			  	else
			  	message += "<tr><td align='center' class='h4'>" + orderData[i].passNo + "</td><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].prductName + "</td><td align='left' class='h4'>" + orderData[i].remarks + "</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td><td align='center' class='h4'>"+ orderData[i].unitPrice +"</td><td align='center' class='h4'>"+ orderData[i].itemAmt +"</td><td align='center' class='h4'>"+ Math.round(orderData[i].quotaAvbl) +"</td><td align='center' class='h4'>"+ orderData[i].adjustmentAmount +"</td><td align='center' class='h4'>"+ orderData[i].otherCharges +"</td><td align='center' class='h4'>"+ orderData[i].payableAmt +"</td>";
			  	
			  	orderAmt = orderAmt+orderData[i].payableAmt;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();'>Close</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "  <br>Party Name : " +partyName+ "<br /> <center>Supplier Name : " + orderData[0].supplierpartyName + "<center><br>Total Order Value = "+ orderAmt +" ";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
    function fetchPOInformation(purcahseOrderId) {
    	purcahseOrderId = purcahseOrderId;
    	var dataJson = {"purcahseOrderId": JSON.stringify(purcahseOrderId)};
		jQuery.ajax({
                url: 'getPOInformation',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
                success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
					
						POData = result["POInformationDetails"];
						
						
						showPOInformation(purcahseOrderId);
						
               		}
               	}
		});
    }
    
     function showPOInformation(purcahseOrderId) {
     	var message = "";     	
     	if(POData != undefined){
     	
     	message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
     	
     	message += "<thead><td align='center' class='h3'>PO Id</td><td align='center' class='h3'>PO No</td><td align='center' class='h3'>View</td>";
     	
     	for (i = 0; i < POData.length; ++i) {
     		message += "<tr><td align='center' class='h4'>" + POData[i].purcahseOrderId + "</td><td align='center' class='h4'>" + POData[i].orderNo + "</td><td align='center' class='h4'><a class='buttontext' href='<@ofbizUrl>PurchaseOrderViewDepotSalesDC.pdf?orderId=" + POData[i].purcahseOrderId + "</@ofbizUrl>' target='_blank'>Report</a></td></tr>";
     	}
     	message += "<tr class='h3'><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelForm();'>Close</button></span></td></tr>";    	
     	message += "</table>";
     	Alert(message);
     }
  };
    
    function cancelOrderCaution(orderId,partyId) {
		var message = "";
		
		var orderId = orderId;
		var partyId = partyId;
		
		message += "<html><head></head><body><form action='cancelDepotOrder' id='cancelDepotOrder' method='post' onsubmit='return disableGenerateButton();'><table hight=400 width=400>";
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
			
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='partyId' value='"+partyId+"'/></td></tr>";
			
            message +="<tr class='h3'><td align='center' class='h3'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Order Preference Number : [ "+orderId+" ]";
		Alert(message, title);
		
		
	}
	
	
	function cancelOrderCautionDC(orderId,partyId) {
		var message = "";
		
		var orderId = orderId;
		var partyId = partyId;
		
		message += "<html><head></head><body><form action='cancelDepotOrderDC' id='cancelDepotOrderDC' method='post' onsubmit='return disableGenerateButton();'><table hight=400 width=400>";
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
			
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='partyId' value='"+partyId+"'/></td></tr>";
			
            message +="<tr class='h3'><td align='center' class='h3'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Order Preference Number : [ "+orderId+" ]";
		Alert(message, title);
		
		
	}
	
    function cancelDepotSaleOrderCaution(orderId,partyId,inventoryItemId,facilityId) {
		var message = "";
		
		var orderId = orderId;
		var partyId = partyId;
		
		var inventoryItemId = inventoryItemId;
		var facilityId = facilityId;
		
		message += "<html><head></head><body><form action='cancelDepotSaleOrder' id='cancelDepotSaleOrder' method='post' onsubmit='return disableGenerateButton();'><table hight=400 width=400>";
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
			
			
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='orderId' value='"+orderId+"'/></td></tr>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='partyId' value='"+partyId+"'/></td></tr>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='inventoryItemId' value='"+inventoryItemId+"'/></td></tr>";
			message += "<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='facilityId' value='"+facilityId+"'/></td></tr>";
			
			
            message +="<tr class='h3'><td align='center' class='h3'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Order Preference Number : [ "+orderId+" ]";
		Alert(message, title);
		
		
	}
	
	
	function alertForDate() {
		
		 var message = "";
		message += "<html><head></head><body><form action='' id='cancelDepotOrder' method='post' onsubmit='return disableGenerateButton();'><table hight=400 width=400>";
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' class='h3' ></td> </tr>";
			message += "<tr class='h3'><td align='center' width=100% class='h3' >Instrument Date is Greater Than 3 Months.</td></tr>";
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
			
			
            message +="<tr class='h3'><td align='center' class='h3'><button value='Ok' id='cancel' onclick='return showPaymentEntryForIndentPayment();' class='smallSubmit'>Ok</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "";
		Alert(message, title);
		
		
	}
	
	function raiseDepotInvoiceFromBranch(orderId,shipmentId) {	
		var message = "";
		message += "<form action='raiseSalesInvoiceForDepotSales' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
	   
	    message +=  "<tr class='h3'><td align='left' class='h3' width='50%'></td><td align='left' width='50%'><input class='h3' type='hidden' readonly id='orderId' name='orderId' value='"+orderId+"'/><input class='h3' type='hidden' id='shipmentId' name='shipmentId' value='"+shipmentId+"'/></td></tr>";
	    
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Invoice Date:</td><td align='left' width='50%'><input type='text' name='invoiceDate' id='invoiceDate' onmouseover='datepick()'></td></tr>";
		
		message += "<tr class='h3'><td align='left' class='h3' width='50%'>Tally Ref No:</td><td align='left' width='50%'><input type='text' name='tallyRefNo' id='tallyRefNo'></td></tr>";
		
					"<#if finalDepartmentList?has_content><#list finalDepartmentList as department><option value='${department.partyId?if_exists}' >${department.groupName?if_exists}</option></#list></#if>"+            
					"</select></td></tr>";
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Depot Sales Invoice<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
	
	function issueIndentItems(orderId, partyId) {
		
		var message = "";
		message += "<form action='processIndentItemIssuance' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>OrderId:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='orderId' name='orderId' value='"+orderId+"'/></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Issue to Party:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='partyId' name='partyId' value='"+partyId+"'/></td></tr>";
		
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Issue Indent Items<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
	
</script>

