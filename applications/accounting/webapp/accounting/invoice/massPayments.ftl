<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
					 
	 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	 var BankListJSON = ${StringUtil.wrapString(BankListJSON)!'[]'};
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
	


  function forBankNames(){
  
        $("#issuingAuthority").autocomplete({					
		source:  BankListJSON,
		select: function(event, ui) {
	       var selectedValue = ui.item.label;
	       $("#issuingAuthority").val(selectedValue);	
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
	    var voucherTypeId;
		
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
		
		if(voucherTypeId == "/"){
			payMethodList=voucherPaymentMethodTypeMap['ALL'];
	 	   	$.each(payMethodList, function(key, item){
			  methodOptionList.push('<option value="'+item.value+'">'+item.text+'</option>');
			});
		}
		
			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Payment Type :</td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId'  class='h4'>"+
						"<#if paymentTypes?has_content><#list paymentTypes as eachMethodType><option value='${eachMethodType.paymentTypeId?if_exists}' >${eachMethodType.description?if_exists}</option></#list></#if>"+            
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><#if parentTypeId?exists && parentTypeId=="SALES_INVOICE">Payment Method Type :<#else>Payment Method:</#if> </td><td align='left' width='60%'><select <#if parentTypeId?exists && parentTypeId=="SALES_INVOICE"> name='paymentMethodTypeId' <#else> name='paymentMethodId' </#if> id='paymentMethodTypeId'  class='h4'>"+
						"</select></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='useFifo' value='TRUE'/></td><input class='h4' type='hidden' id='parentTypeId' name='parentTypeId' value='${parentTypeId?if_exists}'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Payment Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='partyIdName' name='partyIdName' value='"+partyIdName+"'/><input class='h4' type='hidden' id='partyId' name='partyId' value='"+partyId+"'/><input class='h4' type='hidden' id='fromPartyId' name='fromPartyId' value='"+fromPartyId+"'/></td></tr>" ;
			<#--if(voucherType != 'CASH'){
				message += 	"<tr class='h3'><td align='left'class='h3' width='60%'>Financial Account:</td><td align='left' width='60%'><select name='finAccountId' id='finAccountId'  class='h4'>"+
							"<#if finAccountList?has_content><#list finAccountList as finAccount><option value='${finAccount.finAccountId?if_exists}' >${finAccount.finAccountName?if_exists}</option></#list></#if>"+            
							"</select></td></tr>";
						
				message += "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='effectiveDate' name='instrumentDate' onmouseover='datepick()'/></td></tr>" +
						   "<tr class='h3'><td align='left' class='h3' width='60%'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
					 	   "<tr class='h3'><td align='left' class='h3' width='60%'>Chq.in favour:</td><td align='left' width='60%'><input class='h4' type='text' id='inFavourOf' name='inFavourOf' /></td></tr>";
			}-->
			message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			message +=	"</table></form></body></html>";
			message += "</tr>";	
		title = "<center>Make Bulk Invoice Payments <center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
	
	
	  function showPaymentEntryForInvoListing(invoiceId,grandTotal,balance,partyIdFrom,partyIdTo,partyName,purposeTypeId1) {
		var message = "";
		invoiceId = invoiceId;
		
		//finalBal = grandTotal-balance;
		//var paymentList = eachAdvancePaymentOrderMap[partyId];
		
		grandTotal = grandTotal;
		partyName= partyName;
		purposeTypeId=purposeTypeId1;
		
		message += "<html><head></head><body><form action='createInvoiceApplyPayment' id='chequePayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
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
		  
		            message += "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Weaver Code :</font></td><td align='left' width='60%'>"+partyIdFrom+"</td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Method Type :</font></td><td align='left' width='60%'><select name='paymentTypeId' id='paymentTypeId' onchange='javascript:paymentFieldsOnchange();' class='h4'>"+
						<#list PaymentMethodType as payment>
						"<option value='${payment.paymentMethodTypeId}' <#if (payment.paymentMethodTypeId == 'CHEQUE')>selected='selected'</#if>>${payment.description}</option>"+
	                   </#list> 
					    "</select><input class='h4' type='input' id='paymentPurposeType' name='paymentPurposeType' value='"+purposeTypeId+"'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Amount :</font></td><td align='left' width='60%'><input class='h4' type='number' id='amount'  name='amount' max='"+balance+"' step='.01' onblur='javascript:amountOnchange(this,balance);amountCheck()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Payment Date:</font></td><td align='left' width='60%'><input class='h4' type='text' readonly id='paymentDate' name='paymentDate' onmouseover='datepick()'/></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Balance :</font></font></td><td align='left' width='60%'><label  align='left' id='bal'>"+balance+"</label></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Total :</font></td><td align='left' width='60%'>"+grandTotal+"</td><input class='h4' type='hidden' id='balance' name='balance' value='"+balance+"' readonly/></tr>"+
                       "<tr class='h3'><td align='left' class='h3' width='60%' ><font color='green'>Chq Date:</font></td><td align='left' width='60%'><input class='h4' type='text' id='chequeDate' name='chequeDate'  onmouseover='datepick()' /></td></tr>" +
                        "<tr class='h3'><td align='left' class='h3' width='60%'><font id='cheqInFavLable'  color='green'>Chq.in favour:</font></td><td align='left' width='60%'><input class='h4' type='text' id='inFavour' name='inFavour' value='NHDC' readonly /></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%' id='checkNoLabel' style='color:green'>Cheque No:</td><td align='left' width='60%'><input class='h4' type='text'  id='paymentRefNum' name='paymentRefNum'/></tr>" +
						<#-->"<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='comments' name='comments' /></td></tr>"+ -->
						"<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Issue Authority/ Bank :</font></td><td align='left' width='60%'><input class='h4' type='text' id='issuingAuthority' name='issuingAuthority' onfocus='forBankNames()' /></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='invoiceId' value='"+invoiceId+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='partyIdTo' value='"+partyIdTo+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' name='partyIdFrom' value='"+partyIdFrom+"'/></td></tr>"+
				 		"<tr class='h3'><td align='left' class='h3' width='60%'></td><td align='left' width='60%'><input class='h4' type='hidden' id='grandTotal' name='grandTotal' value='"+grandTotal+"'/></td></tr>"+
   			 		    "<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
				 		
                		
					message +=	"</table></form></body></html>";
		var title = "Dues Payment : "+partyName +" [ "+partyIdFrom+" ]";
		Alert(message, title);
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
    
    
    }
   else if(paymentType == 'FT_PAYIN'){
      $("#issuingAuthority").parent().parent().show();
      $("#chequeDate").parent().parent().hide(); 
      $("#checkNoLabel").html("Receipt No :"); 
    }
    else if('CASH_PAYIN'){
      $("#paymentRefNum").parent().parent().show(); 
       $("#chequeDate").parent().parent().hide(); 
       $("#checkNoLabel").html("Receipt No :"); 
    }
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
	
	
	
	
</script>
