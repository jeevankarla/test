
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">

	var transactionTypeTaxMap = ${StringUtil.wrapString(transactionTypeTaxMap)!'[]'};
								 
	var productName;			 
	var dataRow;
	var rowIndex;
	var vatSurchargeList;
	var cstSurchargeList;
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
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
					
					populateData();
					
					$('input[type=radio][name=applicableTaxType]').change(function() {
				        if (this.value == 'Intra-State') {
				           $('.InterState').hide();
				           $('.IntraState').show();
				           
				           $("#NO_E2_FORM").attr('checked', 'checked');
				           
				            $('.CST').hide();
				            $('.VAT').show();
				        }
				        else if (this.value == 'Inter-State') {
				        	$('.IntraState').hide();
				           	$('.InterState').show();
				           	
				           	$("#CST_CFORM").attr('checked', 'checked');
				           	
				           	$('.CST').show();
				            $('.VAT').hide();
				           
				        }
				    });
				    
				    $('input[type=radio][name=checkCForm]').change(function() {
				        if (this.value == 'CST_NOCFORM') {
				           $('.CST').show();
				           $('.VAT').hide();
				           $('#CST_SALE').val($('#VAT_SALE').val());
				           $('#CST_SURCHARGE').val($('#VAT_SURCHARGE').val());
				           
				           $('#CST_SALE_AMT').val($('#VAT_SALE_AMT').val());
				           $('#CST_SURCHARGE_AMT').val($('#VAT_SURCHARGE_AMT').val());
				        }
				        else if (this.value == 'CST_CFORM') {
				           $('.VAT').hide();
				           $('.CST').show();
				        }
				    });
				    
				    $('input[type=radio][name=checkE2Form]').change(function() {
				        if (this.value == 'E2_FORM') {
				           $('.VAT').hide();
				           $('.CST').show();
				        }
				        else{
				           $('.VAT').show();
				           $('.CST').hide();
				        }
				    });
				    
				    
					
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function cancelForm(){		 
		return false;
	}
	var customerContactList={};
	function showCustomerDetailsToolTip(gridRow, index) {
	
	    dataRow = gridRow;
		var  partyId= dataRow["customerId"];
		if(customerContactList[partyId] == undefined){
		 	if(dataRow["customerId"] != "undefined"){
				var dataString="partyId=" + partyId+"&effectiveDate="+$("#effectiveDate").val() ;
		      	$.ajax({
		             type: "POST",
		             url: "getpartyContactDetails",
		           	 data: dataString ,
		           	 dataType: 'json',
		           	 async: false,
		        	 success: function(result) {
		             	if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
		       	  		 	alert(result["_ERROR_MESSAGE_"]);
		          		 }else{
		       	  			contactDetails =result["partyJSON"];
		       	  			if( contactDetails != undefined && contactDetails != ""){
		       	  				customerContactList[partyId]=contactDetails;
							}
							      		 	
					 	}
					} ,
					error: function() {
						alert(result["_ERROR_MESSAGE_"]);
					}
					            	
				}); 				
			}
		}
		
		var customerContactDetails=customerContactList[partyId];
	    var message="";
	    var LoomDetails=customerContactDetails["LoomDetails"];
	    var LoomList=customerContactDetails["LoomList"];
	    var address1=customerContactDetails["address1"];
		address1 +=customerContactDetails["address2"];
		address1 +=customerContactDetails["city"];
		var obj ={};
		var objQuota ={};
		var  objAvailableQuota ={};
		var objUsedQuota ={};
		var totLooms = 0;
		$.each(LoomList, function(key, item){
		 obj [item.loomType]=0;
		 objQuota[item.loomType]=0;
		 objAvailableQuota[item.loomType]=0;
		 objUsedQuota[item.loomType]=0;
		 for(var i=0 ; i<LoomDetails.length ; i++){
			if(LoomDetails[i].loomType==item.loomType){
			   obj [item.loomType] = LoomDetails[i].loomQty;
          	   objQuota [item.loomType] = LoomDetails[i].loomQuota; 
          	   objAvailableQuota [item.loomType] = LoomDetails[i].availableQuota; 
          	   objUsedQuota [item.loomType] = LoomDetails[i].usedQuota; 
          	   totLooms+=LoomDetails[i].loomQty;
			 }			       	  				 	
		  }
		       	  				  
		});		       	  				   
		var tableElement;
		

	    message+="<div class='screenlet'>";
	    message+="<div class='screenlet-body'>";
	    message+="<form  name='partyDetails' id='partyDetails'>";
	    message+="<table width='700px' border='0' cellspacing='0' cellpadding='0' style='font-size:11px'>";
	    message+="<tr><td width='15%' keep-together='always' align='left'><font color='green'><b>   PartyName: </b></font></td><td width='85%'><font color='blue'><b>"+customerContactDetails['custPartyName']+"</b></font></td></tr>";
	    message+="<tr><td width='15%' keep-together='always' align='left'><font color='green'><b>   Address: </b></font></td><td width='85%'> <font color='blue'><b>"+address1+"</b></font></td></tr>";
	    message+="<tr><td width='100%' colspan='4'><table width='100%' border='1' border-style='solid'>";
	    message+="<td width='100%' style='padding-top:10px' ><table  class='style18' width='100%' border='1' border-style='solid'>";
	    message+= "<hr class='style16' ></hr>";
	    message+="<tr><td><font color='green'>PassBook: </font></td><td width='10%'><font color='blue'><b>"+customerContactDetails["psbNo"]+"</b></font></td>";
	    message+="<td width='20%'><font color='green'>IssueDate: </font></td><td width='50%'><font color='blue'><b>"+customerContactDetails["issueDate"]+"</b></font></td></tr>";
	    message+="<tr><td width='20%'><font color='green'>Depot: </font></td><td width='50%'><font color='blue'><b>"+customerContactDetails["Depo"]+"</b></font></td>";
	    message+="<td width='20%'><font color='green'>DOA: </font></td><td width='50%'><font color='blue'><b>"+customerContactDetails["DAO"]+"</b></font></td></tr>";
	    message+="<tr><td width='20%'><font color='green'>PartyType: </font></td><td width='50%'><font color='blue'><b>"+customerContactDetails["partyType"]+"</b></font></td>";
	    message+="<td width='20%'><font color='green'>Total Looms: </font></td><td width='50%'><font color='blue'><b>"+totLooms+"</b></font></td></tr>";
	    message+="</table></td></tr>";
	    
	    message+="<tr><td width='100%' style='padding-top:10px' ><table  width='100%' border='1' border-style='solid'>";
	    message+= "<hr class='style16' ></hr>";
	    message+="<tr><td><font color='green'>Loom Type </font></td><td><font color='green'>No.Looms </font></td><td><font color='green'>Elg.Quota </font></td><td><font color='green'>Bal.Quota </font></td><td><font color='green'>UsedQuota </font></td></tr>";
	   
	    $.each(LoomList, function(key, item){
	      message+="<tr><td><font color='blue'><b>"+item.loomType+"</b></font></td><td ><font color='blue'><b>"+obj[item.loomType]+"</b></font></td><td ><font color='blue'><b>"+objQuota[item.loomType]+"</b></font></td><td ><font color='blue'><b>"+objAvailableQuota[item.loomType]+"</b></font></td><td ><font color='blue'><b>"+objUsedQuota[item.loomType]+"</b></font></td></tr>";
	    });
	    
	    message+="</table></td></tr>";
	    message+="</table></td></tr><tr ><td style='text-align:center' colspan='2'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr</table></form></div></div";
		var title = "";
		
		title = "<h2><center>Customer Details <center></h2>";
			
		Alert(message, title);
		
	}
	
	function updatePercentageByAmount(taxAmountItem, totalAmt){
	 	var taxAmount = taxAmountItem.value;
	 	var taxAmtItemId = taxAmountItem.id;
	 	var taxPercItemId = (taxAmountItem.id).replace("_AMT", ""); 
	 	if(taxAmount != 'undefined' && taxAmount != null && taxAmount.length){
	 		var percentage = (taxAmount) * (100/totalAmt) ;
	 		$('#'+taxPercItemId).val(percentage);
	 	}
	 	adjustBasePrice();
	}	
	
	
	function updateAmountByPercentage(taxPercentItem, totalAmt){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		if(taxPercItemId == "VAT_SALE"){
		 		if(vatSurchargeList != 'undefined' && vatSurchargeList != null && vatSurchargeList.length){
		 			for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
		 	
		 	if(taxPercItemId == "CST_SALE"){
		 		if(cstSurchargeList != 'undefined' && cstSurchargeList != null && cstSurchargeList.length){
		 			for(var i=0;i<cstSurchargeList.length;i++){
						var taxType = cstSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
	 	}
	 	adjustBasePrice();
	}	
	
	function updateAmountByFieldPercentage(taxPercentItem, amtField){
		var amtFieldId = amtField.id;
	 	var totalAmt = $('#'+amtFieldId).val()
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		if(taxPercItemId == "VAT_SALE"){
		 		if(vatSurchargeList != 'undefined' && vatSurchargeList != null && vatSurchargeList.length){
		 			for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
		 	
		 	if(taxPercItemId == "CST_SALE"){
		 		if(cstSurchargeList != 'undefined' && cstSurchargeList != null && cstSurchargeList.length){
		 			for(var i=0;i<cstSurchargeList.length;i++){
						var taxType = cstSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
	 	}
	 	adjustBasePrice();
	 	
	}	
	
	function adjustBasePrice(){
		
		var baseAmount = parseInt($('#basicAmount').val());
		var basePriceComponents = $('input:checkbox:checked.basePriceComponent').map(function () {
								  	return this.id;
								  }).get();
											
		for(var i = 0; i < basePriceComponents.length; i++){
			var basePriceComponentId = (basePriceComponents[i]).replace("_INC_BASIC", "_AMT"); 
	    	var baseComponentAmt = $('#'+basePriceComponentId).val();
	    	if(baseComponentAmt){
	    		baseAmount = baseAmount + parseInt(baseComponentAmt);
	    	}
		}									
	
		$('#baseAmount').val(baseAmount);
		updatePurchasePrice();
	}
	
	function updatePurchasePrice(){
		var totalPurchaseValue = parseInt($('#basicAmount').val());
		$('#purchaseAdjustmentTable').find('input:text').each(function () {
			var purComponentValue = this.value;
       		if(purComponentValue){
		        totalPurchaseValue = totalPurchaseValue + parseInt(purComponentValue);
		    }
    	});
    	$('#purchaseTaxUpdationTable').find('input:text').each(function () {
			var purComponentValue = this.value;
       		if(purComponentValue){
		        totalPurchaseValue = totalPurchaseValue + parseInt(purComponentValue);
		    }
    	});
    	$('#purchaseAmount').val(totalPurchaseValue);
    	
    	//updatePurchasePriceSale();
    	adjustBasePriceSale();
    	
    	<#--
    	var saleBasePrice = totalPurchaseValue;
    	
    	saleBasePrice = saleBasePrice + parseInt($('#serviceChargeAmt').val());
    	$('#saleBaseAmt').val(saleBasePrice);
    	
    	var usedQuota = dataRow["usedQuota"];
		var quantity = dataRow["quantity"];
		
		var tenPercentSubsidy = 0;
		if(usedQuota){
			tenPercentSubsidy = .1*(totalPurchaseValue/quantity)*usedQuota;
		}
    	$('#tenPercentSubsidy').val(tenPercentSubsidy);
    	
    	var salesTaxAmt = 0;
    	$("#salesTaxUpdationTable tr :input:visible").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var taxValue = $('#'+id).val();
			    	salesTaxAmt = salesTaxAmt+taxValue/100*100 ;
			    }
		    }
		}) 
    	var totalPayableValue = saleBasePrice - tenPercentSubsidy + salesTaxAmt;
    	
    	$('#totalPayableValue').val(totalPayableValue); 
    	-->
    	
	}
	
	function updatePercentageByAmountSale(taxAmountItem, totalAmt){
		totalAmt = parseInt($('#purchaseAmount').val());
	 	var taxAmount = taxAmountItem.value;
	 	var taxAmtItemId = taxAmountItem.id;
	 	var taxPercItemId = (taxAmountItem.id).replace("_AMT", ""); 
	 	if(taxAmount != 'undefined' && taxAmount != null && taxAmount.length){
	 		var percentage = (taxAmount) * (100/totalAmt) ;
	 		$('#'+taxPercItemId).val(percentage);
	 	}
	 	adjustBasePriceSale();
	}	
	
	function updateAmountByPercentageSale(taxPercentItem, totalAmt){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		if(taxPercItemId == "VAT_SALE"){
		 		if(vatSurchargeList != 'undefined' && vatSurchargeList != null && vatSurchargeList.length){
		 			for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
		 	
		 	if(taxPercItemId == "CST_SALE"){
		 		if(cstSurchargeList != 'undefined' && cstSurchargeList != null && cstSurchargeList.length){
		 			for(var i=0;i<cstSurchargeList.length;i++){
						var taxType = cstSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
	 	}
	 	adjustBasePriceSale();
	}	
	
	function updateAmountByFieldPercentageSale(taxPercentItem, amtField){
		var amtFieldId = amtField.id;
	 	var totalAmt = $('#'+amtFieldId).val()
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		if(taxPercItemId == "VAT_SALE"){
		 		if(vatSurchargeList != 'undefined' && vatSurchargeList != null && vatSurchargeList.length){
		 			for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
		 	
		 	if(taxPercItemId == "CST_SALE"){
		 		if(cstSurchargeList != 'undefined' && cstSurchargeList != null && cstSurchargeList.length){
		 			for(var i=0;i<cstSurchargeList.length;i++){
						var taxType = cstSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
	 	}
	 	adjustBasePriceSale();
	 	
	}	
	
	function adjustBasePriceSale(){
		
		var baseAmount = parseInt($('#purchaseAmount').val());
		var basePriceComponents = $('input:checkbox:checked.saleBasePriceComponent').map(function () {
								  	return this.id;
								  }).get();
											
		for(var i = 0; i < basePriceComponents.length; i++){
			var basePriceComponentId = (basePriceComponents[i]).replace("_INC_BASIC", "_AMT"); 
	    	var baseComponentAmt = $('#'+basePriceComponentId).val();
	    	if(baseComponentAmt){
	    		baseAmount = baseAmount + parseInt(baseComponentAmt);
	    	}
		}									
	
		$('#saleBaseAmt').val(baseAmount);
		updatePurchasePriceSale();
	}
	
	function updatePurchasePriceSale(){
		var totalSaleValue = parseInt($('#purchaseAmount').val());
		$('#indentAdjustmentTable').find('input:text').each(function () {
			var saleComponentValue = this.value;
       		if(saleComponentValue){
		        totalSaleValue = totalSaleValue + parseInt(saleComponentValue);
		    }
    	});
    	
    	var salesTaxAmt = 0;
    	$('#salesTaxUpdationTable').find('input:text').each(function () {
			var saleComponentValue = this.value;
       		if(saleComponentValue){
		        totalSaleValue = totalSaleValue + parseInt(saleComponentValue);
		        salesTaxAmt = salesTaxAmt+parseInt(saleComponentValue)/100*100 ;
		    }
    	});
    	$('#saleAmount').val(totalSaleValue);
    	
    	var saleBasePrice = $('#saleBaseAmt').val();
    	
    	// Service Charge Recalculation
    	
    	var serviceChargePercent = dataRow["SERVICE_CHARGE"];
    	var serviceCharge = (serviceChargePercent/100)*(saleBasePrice);
    	
    	$('#serviceChargeAmt').val(serviceCharge);
    	
    	//totalSaleValue = totalSaleValue + serviceCharge;
    	$('#saleAmount').val(totalSaleValue);
    	
		// Subsidy Recalculation
		
		var usedQuota = dataRow["usedQuota"];
		var quantity = dataRow["quantity"];
		var tenPercentSubsidy = 0;
		if(usedQuota){
			tenPercentSubsidy = .1*(saleBasePrice/quantity)*usedQuota;
		}
    	$('#tenPercentSubsidy').val(tenPercentSubsidy);
    	
    	<#--
    	var salesTaxAmt = 0;
    	$("#salesTaxUpdationTable tr :input:visible").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var taxValue = $('#'+id).val();
			    	salesTaxAmt = salesTaxAmt+taxValue/100*100 ;
			    }
		    }
		})
		--> 
    	var totalPayableValue = totalSaleValue - tenPercentSubsidy + serviceCharge;
    	
    	$('#totalPayableValue').val(totalPayableValue); 
    	
	}
	
	function showItemAdjustmentsAndTaxes(gridRow, index) {
			rowIndex = index;
			dataRow = gridRow;
			productName = dataRow["cProductName"];
			
			var totalAmt = dataRow["amount"];
			
			var purchaseBasicAmount = totalAmt;
			if(dataRow["purchaseBasicAmount"]){
				purchaseBasicAmount = dataRow["purchaseBasicAmount"];
			}
			
			
			
			//alert("data2 ========="+JSON.stringify(data2));
			
			var adjustmentIncBasic = 0;
			
			if(data2){
				for(var i=0;i<data2.length;i++){
					var adjustment = data2[i];
					if(adjustment["assessableValue"] && adjustment["assessableValue"] == "checked"){
						if(adjustment["adjValue"]){
							adjustmentIncBasic = adjustmentIncBasic + (adjustment["adjValue"]);
						}
						
					}
				}
			}
			var baseAmt = purchaseBasicAmount + adjustmentIncBasic;
			
			var defaultTaxMapPur = dataRow["defaultTaxMapPur"];
			var taxValueMapPur = dataRow["taxValueMapPur"];
			
			var purchaseTitleTransferEnumId = $("#purchaseTitleTransferEnumId").val();
			var purTaxList = transactionTypeTaxMap[purchaseTitleTransferEnumId];
			
			var message = "";
			var title = "";
				
				message += "<table cellspacing=10 cellpadding=10 id='priceTable' >" ;
					message += "<hr class='style17'></hr>";
					message += "<tr class='h3'><th>Price </th></tr>";
					message += "<tr>"+
									"<td align='left'>";
										message += "<table cellspacing=10 cellpadding=10 id='purchasePriceTable' >"+
														"<tr>"+
															"<td align='left'>Basic Amount: </td>"+
															"<td><input type='text' style='width: 100px;' name='basicAmount' id='basicAmount' value='"+purchaseBasicAmount+"' readOnly/></td>"+
														"</tr>"+
														"<tr>"+
															"<td align='left'>Adjustments: </td>"+
															"<td><input type='text' style='width: 100px;' name='adjIncBasicAmt' id='adjIncBasicAmt' value='"+adjustmentIncBasic+"' readOnly/></td>"+
														"</tr>"+
														"<tr>"+
															"<td align='left'>Base Amount: </td>"+
															"<td><input type='text' style='width: 100px;' name='baseAmount' id='baseAmount' value='"+baseAmt+"' readOnly/></td>"+
														"</tr>";
										message += "</table>"+
									"</td>"+
								"</tr>";
				message += "</table>";
				message += "<hr class='style18'></hr>";
				
				message += "<table cellspacing=10 cellpadding=10 id='purchaseTaxUpdationTable' >" ;
					message += "<tr class='h3'>"+
									"<th>Purchase Taxation: </th>"+
									"<th>"+$("#purchaseTaxType").val()+" </th>"+
									"<th></th>"+
									"<th>"+$("#purchaseTitleTransferEnumId option:selected").text()+" </th>"+
								"</tr>";
								
					for(var i=0;i<purTaxList.length;i++){
						var purTax = purTaxList[i];
						var purTaxValue = taxValueMapPur[purTax];
						
						var purTaxAmount = purTaxValue*(totalAmt)*0.01;
						if(defaultTaxMapPur[purTax] != 'undefined' || defaultTaxMapPur[purTax] != null){
							var taxDetails = defaultTaxMapPur[purTax]["taxDetails"];
							
							if(dataRow[taxDetails.taxAuthorityRateTypeId+"_PUR"]){
								purTaxValue = dataRow[taxDetails.taxAuthorityRateTypeId+"_PUR"];
							}
							if(dataRow[taxDetails.taxAuthorityRateTypeId+"_PUR_AMT"]){
								purTaxAmount = dataRow[taxDetails.taxAuthorityRateTypeId+"_PUR_AMT"];
							}
							
							message += "<tr>"+
											"<td align='left'>"+taxDetails.description+": </td>"+
											"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+taxDetails.taxAuthorityRateTypeId+"_PUR' id='"+taxDetails.taxAuthorityRateTypeId+"_PUR' value='"+purTaxValue+"' onblur='javascript:updateAmountByFieldPercentage(this, baseAmount);'/></td>"+
											"<td align='left'> Amt: </td>"+
											"<td><input type='text' style='width: 100px;' name='"+taxDetails.taxAuthorityRateTypeId+"_PUR_AMT' id='"+taxDetails.taxAuthorityRateTypeId+"_PUR_AMT' value='"+purTaxAmount+"' readOnly></td>"+
										"</tr>";
							
							var surchargeList = defaultTaxMapPur[purTax]["surchargeList"];
							
							for(var j=0;j<surchargeList.length;j++){
								var surchargeDetails = surchargeList[j];
								var surchargeValue = taxValueMapPur[surchargeDetails.taxAuthorityRateTypeId];
								var surchargeAmount = surchargeValue*purTaxAmount*0.01;
								
								if(dataRow[surchargeDetails.taxAuthorityRateTypeId+"_PUR"]){
									surchargeValue = dataRow[surchargeDetails.taxAuthorityRateTypeId+"_PUR"];
								}
								if(dataRow[surchargeDetails.taxAuthorityRateTypeId+"_PUR_AMT"]){
									surchargeAmount = dataRow[surchargeDetails.taxAuthorityRateTypeId+"_PUR_AMT"];
								}
								
								message += "<tr>"+
												"<td align='left'>"+surchargeDetails.description+": </td>"+
												"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+surchargeDetails.taxAuthorityRateTypeId+"_PUR' id='"+surchargeDetails.taxAuthorityRateTypeId+"_PUR' value='"+surchargeValue+"' onblur='javascript:updateAmountByFieldPercentage(this, "+taxDetails.taxAuthorityRateTypeId+"_PUR_AMT);'/></td>"+
												"<td align='left'> Amt: </td><td><input type='text' style='width: 100px;' name='"+surchargeDetails.taxAuthorityRateTypeId+"_PUR_AMT' id='"+surchargeDetails.taxAuthorityRateTypeId+"_PUR_AMT' value='"+surchargeAmount+"' readOnly></td>"+
											"</tr>";
							}
						}
					
					}
					
				message += "</table>";
			
				message += "<hr class='style18'></hr>";
			
			message += "<tr class='h3'><td class='h3' align='left'><span align='right'><button value='Add Price' onclick='return addDataToGridTest();' class='smallSubmit'>Add Price</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			
			title = "<h2><center>User Defined Price <center></h2><br /><center>"+ productName +"</center> ";
			
			Alert(message, title);
	};
	
	function addDataToGridTest(){
		var totalTaxAmt = 0;
		var serviceChargeVal = 0;
		var otherChargesValue = 0;
		var totalAmt = dataRow["amount"];
		var quantity = parseFloat(dataRow["quantity"]);
		
		var purchaseBasicAmount = $("#basicAmount").val();
		var saleBasicAmount = $("#purchaseAmount").val();
		var tenPercentSubsidy = $("#tenPercentSubsidy").val();
		var serviceChargeAmt = $("#serviceChargeAmt").val();
		var totalPayableValue = $("#totalPayableValue").val();
		var baseAmount = $("#baseAmount").val();
		var saleBaseAmt = $("#saleBaseAmt").val();
		
				
		$("#purchaseTaxUpdationTable tr :input:visible").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		})
		
		//alert("totalTaxAmt = "+totalTaxAmt);
		
				
		dataRow["taxAmt"] = otherChargesValue;
		//dataRow["totPayable"] = totalPayableValue;
		
		grid.updateRow(rowIndex);
		grid.render();
		updateTotalIndentAmount();
		cancelForm();
	}
	
	
	
	
	
	
		
		
	function addDataToGrid(){
		var totalTaxAmt = 0;
		var serviceChargeVal = 0;
		var totalAmt = dataRow["amount"];
		$("#taxTable tr :input:visible").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var taxValue = $('#'+id).val();
			    	dataRow[id] = taxValue;
			    	totalTaxAmt = totalTaxAmt+taxValue/100*100 ;
			    }
			    else{
			    	var taxPercentage = $('#'+id).val();
			    	dataRow[id] = taxPercentage;
			    	
			    }
		    }
		}) 
		
		$("#taxTable tr :input:hidden").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	dataRow[id] = 0;
		    }
		}) 
		
		<#--
		$("#serviceChargeUpdationTable tr :input").each(function () {
		    var id = this.id;
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var taxValue = $('#'+id).val();
			    	dataRow[id] = taxValue;
			    }
			    else{
			    	var taxPercentage = $('#'+id).val();
			    	dataRow[id] = taxPercentage;
			    	serviceChargeVal += (taxPercentage) * (totalAmt/100) ;
			    }
		    }
		}) 
		-->
		
		
		dataRow["taxAmt"] = totalTaxAmt;
		dataRow["totPayable"] = totalAmt + dataRow["SERVICE_CHARGE_AMT"] + totalTaxAmt;
		
		var taxApplicabilityList = [];
		$('#taxUpdationTable input:radio:checked:visible').each(function () {
		  	taxApplicabilityList.push(this.value)
		  	dataRow[this.name] = this.value;
		  	if(this.name == "applicableTaxType"){
		  		if(this.value == "Intra-State"){
		  			$("#orderTaxType").val("Intra-State");
		  		}
		  		else{
		  			$("#orderTaxType").val("Inter-State");
		  		}
		  	}
		});
		dataRow["taxApplicabilityList"] = taxApplicabilityList;
		grid.updateRow(rowIndex);
		grid.render();
		updateTotalIndentAmount();
		cancelForm();
	}	
		
	function adjustAmount(taxPercentItem, totalAmt){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		if(taxPercItemId == "VAT_SALE"){
		 		if(vatSurchargeList != 'undefined' && vatSurchargeList != null && vatSurchargeList.length){
		 			for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
		 	
		 	if(taxPercItemId == "CST_SALE"){
		 		if(cstSurchargeList != 'undefined' && cstSurchargeList != null && cstSurchargeList.length){
		 			for(var i=0;i<cstSurchargeList.length;i++){
						var taxType = cstSurchargeList[i];
						var taxPercentage = $('#'+taxType).val();
						var surchargeValue = taxPercentage * taxValue/100;
						$('#'+taxType + '_AMT').val(surchargeValue);
					}
			 	}
		 	}
	 	}
	}	
	function adjustSurcharge(taxPercentItem, amtField){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	
	 	var amtId = amtField.id;
	 	var taxAmt = $('#'+amtId).val();
	 	if(taxAmt != 'undefined' && taxAmt != null){
	 		var taxValue = (percentage) * (taxAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 	}
	}
	
	function changeServiceChargePercent() {
		
		var serviceChargePercent = $('#serviceChargePercent').val();
		var message = "";
		var title = "";
		message += "<table cellspacing=20 cellpadding=20 id='serviceChgUpdationTable' >" ;
		message += "<hr class='style17'></hr>";
		message += "<tr class='h3'><th>Service Charge </th></tr>";
		message += "<tr class='h3'><td align='left'>Service Charge %: </td><td><input type='text' name='serviceChgPercent' id='serviceChgPercent' value='"+serviceChargePercent+"'/></td></tr>";
			
		message += "<tr class='h3'><td class='h3' align='left'><span align='right'><button value='Add Price' onclick='return updateServiceChargeAndGrid();' class='smallSubmit'>Add</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		
		message += "</table>";
		title = "<h2><center>Service Charge <center></h2>";
		
		Alert(message, title);
		
	};
	
	function updateServiceChargeAndGrid(){
		 $('#serviceChargePercent').val($('#serviceChgPercent').val());
		 //updateProductTotalAmount();
		 $("#serviceCharge").html("<b>"+$('#serviceChgPercent').val()+"% Service Charge is applicable</b>");
		 updateServiceChargeAmounts();
		 cancelForm();
	}
	
	function getPreviousShipAddress(partyId){
  
 
		   var dataJson = {"partyId": partyId};
		
			jQuery.ajax({
                url: 'getShipmentAddress',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						OrderAddress = result["OrderAddress"];
						
		                  $("#partyId").val();
					      $("#address1").val(OrderAddress.address1);
					      $("#address2").val(OrderAddress.address2);
					      $("#city").val(OrderAddress.city);
					      $("#postalCode").val(OrderAddress.postalCode);
					      $("#stateProvinceGeoId").val(OrderAddress.stateProvinceGeoId);
					      $("#country").val(OrderAddress.country);
					    // var countryCode = $("#countryCode").val(OrderAddress.address1);
					    // var stateName = $("#stateProvinceGeoId").find('option:selected').text();
					    // var countryName = $("#country").find('option:selected').text();
					     
						 					
                 	}	
                 	
                 }							
		      });
		
  
  
  }








var CountryJsonMap = ${StringUtil.wrapString(countryListJSON)!'{}'};

var StateJsonMap = ${StringUtil.wrapString(stateListJSON)!'{}'};
	

       var CountryOptionList;
	   var CountryOptions;
	   var StateOptionList;
	   var StateOptions;

       var addressMap = {};
	
	 function manualAddress(){
		 
		 var message = "";
		 
		 
		var partyId =  $('#partyId').val();
		
		 if(partyId)
		 getPreviousShipAddress(partyId);
		
		
		message += "<html><head></head><body><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		
			        message += "<tr class='h3'><td align='left' class='h3' width='60%'><font color='green'>Retailer Code :</font></td><td align='left' width='60%'><input class='h4' type='label' id='partyId' name='partyId' value='"+partyId+"' readOnly/></td></tr>";
		            message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>Address1:<font color=red>*</font> </td><td align='left' width='60%'><input type='text' class='h4'  id='address1'  name='address1' onblur='storeValues();' required/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>Address2: </td><td align='left' width='60%'><input type='text' class='h4'  id='address2'  name='address2' onblur='storeValues();' /></td></tr>";
	     		    message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>Country: </td><td align='left' width='60%'><select name='country' id='country'><option value='IND' selected>India</option></select></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>State: </td><td align='left' width='60%'><select class='h4'  id='stateProvinceGeoId'  name='stateProvinceGeoId' onchange='storeValues();'/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>City: <font color=red>*</font></td><td align='left' width='60%'><input type='text' class='h4'  id='city'  name='city' onchange='storeValues();' required/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>PostalCode: </td><td align='left' width='60%'><input type='text' class='h4'  id='postalCode'  name='postalCode' onblur='storeValues();'  /></td></tr>";
				    message +=  "<tr class='h3'><td align='center'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitAddress();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
                		
					message +=	"</table></body></html>";
		var title = "Party :  [ "+partyId+" ]";
		Alert(message, title);
		 
		 
		 }
		
		 
	
	   function storeValues(){
	   
	     
	     
	     var partyId = $("#partyId").val();
	     var address1 = $("#address1").val();
	     var address2 = $("#address2").val();
	     var city = $("#city").val();
	     var postalCode = $("#postalCode").val();
	     
	     if(postalCode == ''){
			 postalCode = '0';
	 	}
	     
	     var countryCode = $("#countryCode").val();
	     var stateProvinceGeoId = $("#stateProvinceGeoId").val();
	     var stateName = $("#stateProvinceGeoId").find('option:selected').text();
	     var countryName = $("#country").find('option:selected').text();
	     
	     var country = $("#country").val();
	     
	     
          	     
	     addressMap['partyId'] = partyId;
	     addressMap['address1'] = address1;
	     addressMap['address2'] = address2;
	     addressMap['city'] = city;
	     addressMap['postalCode'] = postalCode;
	     addressMap['countryCode'] = countryCode;
	     addressMap['stateProvinceGeoId'] = stateProvinceGeoId;
	     addressMap['stateName'] = stateName;
	     addressMap['country'] = country;
	     addressMap['countryName'] = countryName;
	   
	   }
	
	
	 var orderData;
	 var contactctMechId;
	 function submitAddress() {
	
	 	var count = Object.keys(addressMap).length;
	 
	 	var partyId = addressMap.partyId;
	 	var city = addressMap.city;
	 	var address1 = addressMap.address1;
	 	var address2 = addressMap.address2;
		var countryName = addressMap.countryName;
	 	var postalCode = addressMap.postalCode;
	 	var stateName = addressMap.stateName;
	 
	 if(count != 0 && city.length !=0 && address1.length !=0 && postalCode.length != 0 && partyId.length != 0){
	    showSpinner();
		jQuery.ajax({
                url: 'storePartyPostalAddress',
                type: 'POST',
                data: addressMap,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["orderList"];
					    if(orderData.length != 0) 
					    {
					     contactctMechId = orderData.contactMechId;
					     $("#contactMechId").val(contactctMechId);
					     cancelShowSpinner();
					   }
               		}
               	}							
		});
		
		}
		else{
		  alert("Please Fill The Values");
		}
	}
	
	
	function showSpinner() {
		var message = "hello";
		var title = "";
		message += "<div align='center' name ='displayMsg' id='forAddress'/><button onclick='return cancelForm();' class='submit'/>";
		Alert(message, title);
		
	};
	function cancelShowSpinner(){
		$('button').click();
		return false;
	}
	
	
 	var stateListJSON;
 	function setServiceName(selection) {
 		var country=selection.value;
  		jQuery.ajax({
            url: 'getCountryStateList',
            type: 'POST',
            async: true,
            data: {countryGeoId:country} ,
 				success: function(result){
 				stateListJSON = result["stateListJSON"];
 				if (stateListJSON) {	
                    var optionList;	       				        	
			        for(var i=0 ; i<stateListJSON.length ; i++){
						var innerList=stateListJSON[i];	              			             
			            optionList += "<option value = " + innerList['value'] + " >" + innerList['label'] + "</option>";          			
			      	}//end of main list for loop
	  			}else{
			        optionList += "<option value = " + "_NA_" + " >" + "_NA_" + "</option>";          			

				}
 				jQuery("[name='stateProvinceGeoId']").html(optionList);

            }    
        });
        storeValues(); 
 
	}
 	
 	


	function populateData(){
		//CountryOptionList += "<option value = IND selected>India  </option>";          			

 		//if(CountryJsonMap != undefined && CountryJsonMap != ""){
		//	$.each(CountryJsonMap, function(key, item){
			//    CountryOptionList += "<option value = " + item.value + " >" + item.label + "</option>";          			
			//});
	 	//}
		//CountryOptions = CountryOptionList;
 		//jQuery("[name='country']").html(CountryOptions);
 					 
 		if(StateJsonMap != undefined && StateJsonMap != ""){
			$.each(StateJsonMap, function(key, item){
			                StateOptionList += "<option value = " + item.value + " >" + item.label + "</option>";          			
			});
	 	}
		 
		StateOptions = StateOptionList;
 		jQuery("[name='stateProvinceGeoId']").html(StateOptions);
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
			message += "<tr class='h3'><td align='center' width=100% class='h3'><font color=red>Received Date is greater than Indent Date</font></td></tr>";
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
			
           message +="<tr class='h3'><td align='center' class='h3'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>Ok</button></td>  </tr>";				 		
                		
					message +=	"</table></form></body></html>";
		var title = "";
		Alert(message, title);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
</script>
