<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<style>
.button1 {
    background-color: grey;
    border: none;
    color: white;
    padding: 10px 5px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 10px;
    margin: 4px 2px;
    cursor: pointer;
}
</style>

<style type="text/css">
.myTable { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable th { 
  background-color: green;
  color: white; 
  }
.myTable td, 
.myTable th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
  
  .myTable1 { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable1 th { 
  background-color: green;
  color: white; 
  }
.myTable1 td, 
.myTable1 th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
</style>

<style type="text/css">
input[type=button] {
	color: white;
    padding: .5x 7px;
    background:#008CBA;
    border: .8px solid green;
    border:0 none;
    cursor:pointer;
    -webkit-border-radius: 5px;
    border-radius: 5px; 
}
input[type=button]:hover {
    background-color: #3e8e41;
}
</style>


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
	
	function showItemAdjustmentsAndTaxes(gridRow, index) {
			rowIndex = index;
			dataRow = gridRow;
			productName = dataRow["cProductName"];
			
			
			var orderAdjustmentsList = [];
				
				orderAdjustmentsList.push("CESS");
				orderAdjustmentsList.push("INSURANCE_CHGS");
				orderAdjustmentsList.push("OTHER_CHARGES");
				orderAdjustmentsList.push("PACKING_FORWARDIG");
				
				dataRow["orderAdjustmentsList"] = orderAdjustmentsList;
			
			/*
			if(typeof(dataRow["orderAdjustmentsList"]) != 'undefined' || dataRow["orderAdjustmentsList"] != null){
				orderAdjustmentsList = dataRow["orderAdjustmentsList"];	
			}   
			*/
			
			productName = dataRow["cProductName"];
			var totalAmt = dataRow["amount"];
			
			
			//var saleAmount = dataRow["amount"];
			
			
			
			var purchaseBasicAmount = totalAmt;
			
			if(dataRow["purchaseBasicAmount"]){
				purchaseBasicAmount = dataRow["purchaseBasicAmount"];
			}
			
			var purchaseBaseAmount = purchaseBasicAmount;
			if(dataRow["baseAmount"]){
				purchaseBaseAmount = dataRow["baseAmount"];
			}
			
			var saleBaseAmount = totalAmt;
			if(dataRow["saleBaseAmt"]){
				saleBaseAmount = dataRow["saleBaseAmt"];
			}
						
			var serviceCharge = dataRow["SERVICE_CHARGE"];
			var serviceChargeAmt = dataRow["SERVICE_CHARGE_AMT"];
			
			var usedQuota = dataRow["usedQuota"];
			var quantity = dataRow["quantity"];
			
			var schemeCategory = $("#schemeCategory").val();
			
			var tenPercentSubsidy = 0;
			
			if(dataRow["TEN_PERCENT_SUBSIDY_SALE_AMT"]){
			   tenPercentSubsidy = dataRow["TEN_PERCENT_SUBSIDY_SALE_AMT"];
			}
			else{
				tenPercentSubsidy = .1*(saleBaseAmount/quantity)*usedQuota;
			}
	    	var totalPayableValue = dataRow["totPayable"];
	    	
	    	var totalPurPayableValue = 0;
			if(dataRow["totalPurPayableValue"]){
				totalPurPayableValue = dataRow["totalPurPayableValue"];
			}else{
				totalPurPayableValue = dataRow["amount"];
			}
			
			// Purchase Tax Details
			
			var defaultTaxMapPur = dataRow["defaultTaxMapPur"];
			var taxValueMapPur = dataRow["taxValueMapPur"];
			
			var purchaseTitleTransferEnumId = $("#purchaseTitleTransferEnumId").val();
			var purTaxList = transactionTypeTaxMap[purchaseTitleTransferEnumId];
			
			// Sale Tax Details
			
			var defaultTaxMap = dataRow["defaultTaxMap"];
			var taxValueMap = dataRow["taxValueMap"];
			
			
			var saleTitleTransferEnumId = $("#saleTitleTransferEnumId").val();
			var saleTaxList = transactionTypeTaxMap[saleTitleTransferEnumId];
			
			var message = "";
			var title = "";
			
					
			
			var allAdjustments = [];
			
			var allAdjustmentsSale = [];
			
			
			  $.each(orderAdjustmentsList, function(key, item){
			  
			     var tempMap = {};
			        
			        tempMap['orderAdjustmentTypeId'] = item;
			  
			    allAdjustments.push(tempMap);
			    allAdjustmentsSale.push(tempMap);
			  
			  });
			  
			 var schemeCategory = $("#schemeCategory").val();
			 
			 if(schemeCategory == "General"){
			    totalAmt = dataRow["unitPricePur"]*dataRow["quantity"];
			 }
			 
			  var onbehalfOff = $("#onbehalfOff").val();
			  
			  var taxList1 = [];
			  taxList1.push("VAT");
			  taxList1.push("CST");
			  taxList1.push("VAT_SURCHARGE");
			  taxList1.push("CST_SURCHARGE");
			  
			  var taxList2 = [];
			  taxList2.push("VAT");
			  taxList2.push("CST");
			  taxList2.push("VAT_SURCHARGE");
			  taxList2.push("CST_SURCHARGE");
			  if(schemeCategory == "General" && onbehalfOff != "onbehalfOff")
			  taxList2.push("SERVICE_CHARGE");
			  if(schemeCategory == "MGPS_10Pecent" && onbehalfOff != "onbehalfOff" && tenPercentSubsidy != 0)
		      taxList2.push("TEN_PERCENT_SUBSIDY");
			  
			  
			  
			  //var taxList1 = dataRow["taxList1"];
			  
			  //alert("taxValueMap==============="+JSON.stringify(taxValueMap));
			  
			   $.each(taxList1, function(key, item){
					
					if(purTaxList == item+"_SALE"){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item;
			        allAdjustments.push(tempMap);
			        }
			        if(purTaxList == item+"_SALE"){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item+"_SURCHARGE";
			        allAdjustments.push(tempMap);
			        }
			  
			  });
			  
			  
			   $.each(taxList2, function(key, item){
					
					if(saleTaxList == item+"_SALE"){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item;
			        allAdjustmentsSale.push(tempMap);
			        }
			        if(saleTaxList == item+"_SALE"){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item+"_SURCHARGE";
			        allAdjustmentsSale.push(tempMap);
			        }
			        if(item == 'TEN_PERCENT_SUBSIDY'){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item;
			        allAdjustmentsSale.push(tempMap);
			        }
			        if(item == 'SERVICE_CHARGE'){
					var tempMap = {};			        
			        tempMap['orderAdjustmentTypeId'] = item;
			        allAdjustmentsSale.push(tempMap);
			        }
			  
			  });
			  
			  
		
			  //alert("saleTaxList=================="+saleTaxList);
			  
			  dataRow["allAdjustments"] = allAdjustments;
			  
			  dataRow["allAdjustmentsSale"] = allAdjustmentsSale;
			
			
			var adjDropdown ="";
			
			var adjDropdownSale ="";
			  
			  
			   adjDropdown +="<option value=''></option>"
			   $.each(allAdjustments, function(key, item){
  				    adjDropdown +="<option value='"+item['orderAdjustmentTypeId']+"'>"+item['orderAdjustmentTypeId']+"</option>";
  				 });
  				 
  				 
  				  adjDropdownSale +="<option value=''></option>"
			   $.each(allAdjustmentsSale, function(key, item){
  				    adjDropdownSale +="<option value='"+item['orderAdjustmentTypeId']+"'>"+item['orderAdjustmentTypeId']+"</option>";
  				 });
  				 
  				 
			/*
			if(schemeCategory == "General"){
				
							var message = "";
			var title = "";
				
				message += "<table cellspacing=10 cellpadding=10 id='priceTable' >" ;
					message += "<hr class='style17'></hr>";
					message += "<tr class='h3'><th>Price </th></tr>";
					message += "<tr>"+
									"<td align='left'>";
										message += "<table cellspacing=10 cellpadding=10 id='SalePriceTable' >"+
														"<tr>"+
															"<td align='left'>Basic Amount: </td>"+
															"<td><input type='text' style='width: 100px;' name='basicAmountPur' id='basicAmountPur' value='"+totalAmt+"' readOnly/></td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>Purchase Value: </td>"+
															"<td><input type='text' style='width: 100px;' name='purchaseBasicAmount' id='purchaseBasicAmount' value='"+purchaseBasicAmount+"' readOnly/>"+
														
														"</tr>";
														
														message += 	"<tr>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>Total Payable Value: </td>"+
							                                "<td><input type='text' style='width: 100px;' name='totalPurPayableValue' id='totalPurPayableValue' value='"+totalPurPayableValue+"' readOnly/>"+
														"</tr>"; 
														
										message += "</table>"+
									"</td>"+
								"</tr>";
				message += "</table>";
				message += "<hr class='style18'></hr>";
				
				
				
				if(dataRow){
			
			message += "<h1 align='center'><font color='blue'>Adjustments And Taxes For Purchase</font></h1>";
			message += "<table cellspacing=10 cellpadding=10 width='800'  id='indentAdjustmentTablePur' class='myTable'>" ;
           
			
			for(var i=0;i<allAdjustments.length;i++){
						var orderAdjustment = allAdjustments[i];
						
						
						//alert(JSON.stringify(orderAdjustment.orderAdjustmentTypeId+"_PUR"));
						
						var orderAdjPercent = 0;
						var orderAdjAmt = 0;
						var incBasic = "FALSE";
						if(dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR"]){
							orderAdjPercent = dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR"];
						}
						if(dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT"]){
							orderAdjAmt = dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT"];
						}
						if(dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR_INC_BASIC"]){
							incBasic = dataRow[orderAdjustment.orderAdjustmentTypeId+"_PUR_INC_BASIC"];
						}
						
						//alert(orderAdjAmt);
						
						if(orderAdjAmt != 0){
		
						  // alert("totalAmt=======loop======"+totalAmt);
						  
						  if(orderAdjustment.orderAdjustmentTypeId != "ROUNDING_CHARGES" && orderAdjustment.orderAdjustmentTypeId != "ROUNDING_OFF" && orderAdjustment.orderAdjustmentTypeId != "VAT"){
						
							message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentagePur(this,"+totalAmt+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' value='"+orderAdjAmt+"' /></td>"+
										"<td align='left'> Remove: </td>"+
										"<td><input type='button' style='width: 100px;' name='remove' id='Remove' value='Remove' class='delete' onclick='javascript:removeAdjustmentPur();'></td>";
									"</tr>";
									
							}else if(orderAdjustment.orderAdjustmentTypeId == "VAT"){
									
								 message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentagePur(this,"+totalAmt+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' value='"+orderAdjAmt+"' onblur='javascript:updatePercentageByAmountPur(this,"+totalAmt+");'></td>"+
									 	"<td align='left'> Input Credit: </td>"+
									 	"<td><input type='checkbox' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_INC_BASIC' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_INC_BASIC' class='basePriceComponent' value='Y' onclick='javascript:inputCredit();'/>Inc BasicPrice</td>"+
									 	"<td align='left'> Remove: </td>"+
										"<td><input type='button' style='width: 100px;' name='remove' id='Remove' value='Remove' class='delete'  onclick='javascript:removeAdjustmentPur();'></td>";
									    
									"</tr>";	
									
							}
							else{
							
							    message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentagePur(this,"+totalAmt+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_PUR_AMT' value='"+orderAdjAmt+"' onblur='javascript:updatePercentageByAmountPur(this,"+totalAmt+");'></td>"+
									 	"<td align='left'> Remove: </td>"+
										"<td><input type='button' style='width: 100px;' name='remove' id='Remove' value='Remove' class='delete'  onclick='javascript:removeAdjustmentPur();'></td>";
									    
									"</tr>";
							
							
							}		
						
						}
						
						
					}
			
			message += "</table>";
			
			
			}
			
			
			message += "<table cellspacing=10 cellpadding=10 id='addButtonForAdjustmentsList' >" ;
						message += "<tr>";
						message += "<td><select id='addAdjList'  class='h4' onchange='javascript:storeIncludeListPur();'>"+adjDropdown+"</select></td><td><div style='width: 200px; height: 100px;  border-style: solid; border-color: grey; overflow-y: scroll;'><ul id='addAdjGivenList'></ul></div></td>";
						message += "<tr class='h3'><td align='left'><input type='button' class='button1' style='solid blue'; max='30' name='vamsi' id='vamsi' value='Add Adjustment' onclick='javascript:MakeAdjTablePur("+totalAmt+");' /></tr>";
			message += "</table>";
			
			message += "<hr class='style18'></hr>";
			message += "<hr class='style18'></hr>";
				
			}
			*/
			if(dataRow['SERVICE_CHARGE_SALE_AMT'])
				serviceChargeAmt = dataRow['SERVICE_CHARGE_SALE_AMT'];
				if(dataRow['SERVICE_CHARGE_SALE'])
				serviceCharge = dataRow['SERVICE_CHARGE_SALE'];
				
				var saleAmount = 0;
				if(dataRow['saleAmount'])
				saleAmount = dataRow['saleAmount'];
				else
				saleAmount = totalPurPayableValue;
				
			//=============================Sale Taxation================================
			
			message += "<table cellspacing=10 cellpadding=10 id='salesTaxUpdationTable' >" ;
			
					message += "<tr class='h3'><th>Price </th></tr>";
					message += "<tr>"+
									"<td align='left'>";
										message += "<table cellspacing=10 cellpadding=10 id='SalePriceTableSale' >"+
														"<tr>"+
															"<td align='left'>Basic Amount: </td>"+
															"<td><input type='text' style='width: 100px;' name='basicAmount' id='basicAmount' value='"+totalPurPayableValue+"' readOnly/></td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>Sale Value: </td>"+
															"<td><input type='text' style='width: 100px;' name='saleAmount' id='saleAmount' value='"+saleAmount+"' readOnly/>"+
							 								
														"</tr>"+
														
														"<tr>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>Service Charge<input type='hidden' id = 'serviceChargeIN' value='"+serviceCharge+"'> <span id='serviceCharge1'>"+serviceCharge+"%</span>: </td>"+
							                                "<td><input type='text' style='width: 100px;' name='serviceChargeAmt' id='serviceChargeAmt' value='"+serviceChargeAmt+"' readOnly/>"+
														"</tr>";
														
													message += 	"<tr>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>10% Subsidy Amount: </td>"+
							                                "<td><input type='text' style='width: 100px;' name='tenPercentSubsidy' id='tenPercentSubsidy' value='"+tenPercentSubsidy+"' readOnly/>"+
														"</tr>";
														
												  message += 	"<tr>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>"+
															"<td align='left'>Total Payable Value: </td>"+
							                                "<td><input type='text' style='width: 100px;' name='totalPayableValue' id='totalPayableValue' value='"+totalPayableValue+"' readOnly/>"+
														"</tr>";   
														
														
														
										message += "</table>"+
									"</td>"+
								"</tr>";
				message += "</table>";	
			        
			        	message += "<hr class='style18'></hr>";
			        
			        
			        message += "<h1 align='center'><font color='blue'>Adjustments And Taxes For Sale</font></h1>";
			message += "<table cellspacing=10 cellpadding=10 width='800'   id='indentAdjustmentTable' class='myTable1'>" ;
			
			
			for(var i=0;i<allAdjustmentsSale.length;i++){
						var orderAdjustment = allAdjustmentsSale[i];
						
						var orderAdjPercent = 0;
						var orderAdjAmt = 0;
						var incBasic = "FALSE";
						if(dataRow[orderAdjustment.orderAdjustmentTypeId+"_SALE"]){
							orderAdjPercent = dataRow[orderAdjustment.orderAdjustmentTypeId+"_SALE"];
						}
						if(dataRow[orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT"]){
							orderAdjAmt = dataRow[orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT"];
						}
						
						if(orderAdjAmt != 0){
		
						  if(orderAdjustment.orderAdjustmentTypeId != "SERVICE_CHARGE" && orderAdjustment.orderAdjustmentTypeId != "TEN_PERCENT_SUBSIDY" && orderAdjustment.orderAdjustmentTypeId != "ROUNDING_CHARGES" && orderAdjustment.orderAdjustmentTypeId != "ROUNDING_OFF"){
							message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentage(this,"+totalPurPayableValue+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' value='"+orderAdjAmt+"' ></td>"+
									 	"<td align='left'> Remove: </td>"+
										"<td><input type='button' style='width: 100px;' name='remove' id='Remove' value='Remove' class='delete'  onclick='javascript:removeAdjustment();'></td>";
									"</tr>";
						    }
						    else if(orderAdjustment.orderAdjustmentTypeId == "ROUNDING_CHARGES" || orderAdjustment.orderAdjustmentTypeId == "ROUNDING_OFF" ){
						        message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentage(this,"+totalPurPayableValue+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' value='"+orderAdjAmt+"' onblur='javascript:updatePercentageByAmount(this,"+totalPurPayableValue+");'></td>"+
									 	"<td align='left'> Remove: </td>"+
										"<td><input type='button' style='width: 100px;' name='remove' id='Remove' value='Remove' class='delete'  onclick='javascript:removeAdjustment();'></td>";
									"</tr>";
						    }
						    else{
						      message += "<tr>"+
										"<td align='left'><font color='blue'>"+orderAdjustment.orderAdjustmentTypeId+": </font></td>"+
										"<td><input type='number' max='100' step='.5' maxlength='4' style='width: 50px;'  width='50px' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE' value='"+orderAdjPercent+"' onblur='javascript:updateAmountByPercentage(this,"+totalPurPayableValue+");'/></td>"+
										"<td align='left'> Amt: </td>"+
										"<td><input type='text' style='width: 100px;' name='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' id='"+orderAdjustment.orderAdjustmentTypeId+"_SALE_AMT' value='"+orderAdjAmt+"' readonly/></td>";
									"</tr>";
						    
						    }			
						
						}
						
						
					}
			
			message += "</table>";
			
				message += "<table cellspacing=10 cellpadding=10 id='addButtonForAdjustmentsList' >" ;
						message += "<tr>";
						message += "<td><select id='addAdjListSale'  class='h4' onchange='javascript:storeIncludeList();'>"+adjDropdownSale+"</select></td><td><div style='width: 200px; height: 100px;  border-style: solid; border-color: grey; overflow-y: scroll;'><ul id='addAdjGivenListSale'></ul></div></td>";
						message += "<tr class='h3'><td align='left'><input type='button' class='button1' style='solid blue'; max='30' name='vamsi' id='vamsi' value='Add Adjustment' onclick='javascript:MakeAdjTable("+totalPurPayableValue+");' /></tr>";
			message += "</table>";
			
			
			message += "<hr class='style18'></hr>";
			
			message += "<tr><td>&nbsp;&nbsp;&nbsp;</td><td>&nbsp;&nbsp;&nbsp;</td><td><button  value='Add Price' onclick='return addDataToGridTest();' class='button1'>Add Price </button></td></tr>";
	
			
			title = "<h2><center>User Defined Price <center></h2><br /><center>"+ productName +"</center> ";
			
			Alert(message, title);
	};
	
	
	//=======================purchase Tax   API=========================================
	
	
	
	function inputCredit(){
	
	
	
	$("#indentAdjustmentTablePur tr :input:visible").each(function () {
		    var id = this.id;
			
			var incBasic = "";
			
			
			if(id != 'undefined' && id != null && id.length && id == "VAT_PUR_AMT"){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	var incBasic = $("#VAT_PUR_INC_BASIC").is(":checked");
			    	
			    	 
			    	
			    	if(incBasic == true){
			    	
			    	  var basicAmount = $("#basicAmount").val();
			    	  
			    	  basicAmount = parseFloat(basicAmount)-parseFloat(adjValue);
					
					  dataRow[VAT_PUR_INC_BASIC] = "true";
								    	  
			    	  
			    	  $("#basicAmount").val(basicAmount);
			    	   $("#saleAmount").val(basicAmount);
			    	   
			    	   adjustBasePriceNew();
			    	
			    	}else{
			    	
			    	var basicAmount = $("#basicAmount").val();
			    	  
			    	  basicAmount = parseFloat(basicAmount)+parseFloat(adjValue);
			    	  
			    	   dataRow[VAT_PUR_INC_BASIC] = "false";
			    	  $("#basicAmount").val(basicAmount);
			    	   $("#saleAmount").val(basicAmount);
			    	
			    	    adjustBasePriceNew();
			    	}
			    	
			    	
			    }
			    
			    
			    
		    }
            });
	
	
	}
	
	
	
	
	
	
	
	
	
	function MakeAdjTablePur(totalAmt){
	
	totalAmt = $("#basicAmount").val();
	
	//alert(totalAmt)
	
	var addAdjType = $("#addAdjList").val();
	
	
	var addFlag = "Y";
	
	$("#indentAdjustmentTablePur tr :input:visible").each(function () {
		    var id = this.id;
		    if(id == addAdjType+"_PUR")
		    addFlag = "N";
	 });
	
	
	if(addFlag == "Y"){
	
	var removeRow = "javascript:removeAdjustmentPur()";
	
	var totalAmtparam = '\'' + totalAmt + '\'';
    var updateAmountByPercentage = "javascript:updateAmountByPercentagePur(this,"+ totalAmtparam +")";
	
	  var updatePercentageByAmount = "javascript:updatePercentageByAmountPur(this,"+ totalAmtparam +")";
	  
	  var inputCredit = "javascript:inputCredit(this,"+ totalAmtparam +")";
	
	var adjIdPer=addAdjType+"_PUR";
	
	var adjIdAmt=addAdjType+"_PUR_AMT";
	
	var adjIdAmtCheck=addAdjType+"_PUR_INC_BASIC";
	
	//alert(addAdjType);
	
	if(addAdjType != "")
	
	 if(addAdjType != "ROUNDING_CHARGES" && addAdjType != "ROUNDING_OFF" && addAdjType != "VAT")
	 $(".myTable").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'"   /></td><td>Remove</td><td><input type="button" style="width: 100px;" name="remove" class="delete" id="Remove" value="Remove" onclick="'+removeRow+'"></td></tr>');
     else if(addAdjType == "VAT")
     $(".myTable").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'"  onblur="'+updatePercentageByAmount+'" /></td>  <td>Input Credit:</td><td><input type="checkbox" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmtCheck+'" onclick="'+inputCredit+'"  /></td><td>Remove</td><td><input type="button" style="width: 100px;" name="remove" class="delete" id="Remove" value="Remove" onclick="'+removeRow+'"></td></tr>');
     else
     $(".myTable").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'"  onblur="'+updatePercentageByAmount+'" /></td><td>Remove</td><td><input type="button" style="width: 100px;" name="remove" class="delete" id="Remove" value="Remove" onclick="'+removeRow+'"></td></tr>');
	}else{
	
	   alert("Sorry Already "+addAdjType+" Added");
	}
	
	}
	
	function removeAdjustmentPur(){
	
	
	
	  $("#indentAdjustmentTablePur tr :input:visible").each(function () {
		    var id = this.id;
			//alert(id);
			
			if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = 0;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = 0;
			    	
			    }
		    }
            });
	
	
	     $('#indentAdjustmentTablePur tr').click(function () {
             $(".delete").live('click', function(event) {
	          $(this).parent().parent().remove();
	        adjustBasePriceNewPur();
           });
           
            });
	
	}
	
	
	
	function storeIncludeListPur(){
	
	 var allAdjustments = dataRow["allAdjustments"];
	 
	 var adjType = $("#addAdjList").val();
	 
	 var checkedListPerent;
	 
	  var adjCheckedMap;
	  if(dataRow['adjCheckedMap'+"_"+adjType]){
	  adjCheckedMap = dataRow['adjCheckedMap'+"_"+adjType];
	   checkedListPerent = adjCheckedMap[adjType];
	  }
	 
	 
	 $('#addAdjGivenList').html('');
	
	  if(checkedListPerent != 'undefined' && checkedListPerent != null){
	  
	   var resultMap = {};
	  $.each(allAdjustments, function(key, item){
			   
			    var checkedList = adjCheckedMap[adjType];
			   
			   if(checkedList != 'undefined' && checkedList != null){
			    $.each(checkedList, function(key, item1){
			       if(item1 == item['orderAdjustmentTypeId']){
			         resultMap[item1] = "N";
			        }else{
			         resultMap[item1] = "Y";
			        }
			    });
			    
			    }
			    		  		
  	    });
  	    
  	     $.each(allAdjustments, function(key, item){
  	          if(resultMap[item['orderAdjustmentTypeId']] == "Y")
			  $('#addAdjGivenList').append($('<li>').html( "<font color='black'><input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedList' onclick='javascript:checkedAdjListPur();' checked><font size='100' color='green'>"+item['orderAdjustmentTypeId']+"/>"));
  		      else
  		      $('#addAdjGivenList').append($('<li>').html( "<input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedList' onclick='javascript:checkedAdjListPur();'><font size='100' color='green'>"+item['orderAdjustmentTypeId']+"/>"));
  	     });
  	    
	 
	 }else{
	 
	  $.each(allAdjustments, function(key, item){
	   $('#addAdjGivenList').append($('<li>').html( "<input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedList' onclick='javascript:checkedAdjListPur();'><font size='100' color='green'>"+item['orderAdjustmentTypeId']+"</option>"));
	  });
	 }
	}
	
	
	function checkedAdjListPur(){
	
	
	   var adjCheckedMap = {};
	
	   var adjType = $("#addAdjList").val();
	   
	   var checkedList = $('input:checkbox:checked.checkedList').map(function () {
								  	return this.id;
								  }).get();
											
		
		adjCheckedMap[adjType] = checkedList;
											
		
	   dataRow['adjCheckedMap'+"_"+adjType] = adjCheckedMap;
	
	
	}
	
	
	
	function updateAmountByPercentagePur(taxPercentItem, totalAmt){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	
	 	
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 	
	 	
	 	       var totalAmt = getAmountWIthIncludeListPur(totalAmt);
	 	       
	 	       if(taxPercItemId == "VAT_SURCHARGE_PUR" || taxPercItemId == "CST_SURCHARGE_PUR")
	 	       var totalAmt = getReleventAmtPur(taxPercItemId);
	 	
	 	
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		
	 		$('#'+taxValueItemId).val(taxValue);
	 		
	 	}
	 	
	 	
	 	adjustBasePriceNewPur();
	}	
	
	
	
	function adjustBasePriceNewPur(){
	
	
	var basicAmount = $("#basicAmountPur").val();
	
	//alert(basicAmount);
	
	
	
	var totaladjValueTaxForEntry = 0;
	
	
	$("#indentAdjustmentTablePur tr :input:visible").each(function () {
		    var id = this.id;
		    
		    
		    
		    if(id == "CST_PUR_AMT" || id == "VAT_PUR_AMT" || id == "ENTRY_TAX_PUR_AMT" || id == "CST_SURCHARGE_PUR_AMT" || id == "VAT_SURCHARGE_PUR_AMT")  {
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    	
			    }
		    }
		    
		    
		      var incBasic = $("#VAT_PUR_INC_BASIC").is(":checked");
			    	
		      if(id == "VAT_PUR_AMT" && incBasic == false)
		      totaladjValueTaxForEntry = totaladjValueTaxForEntry + parseFloat(adjValue);
		      else if(id != "VAT_PUR_AMT" && incBasic == false)
		      totaladjValueTaxForEntry = totaladjValueTaxForEntry + parseFloat(adjValue);
		      
		    
		   }//end
		    if(id == "CESS_PUR_AMT" || id == "INSURANCE_CHGS_PUR_AMT" || id == "OTHER_CHARGES_PUR_AMT" || id == "PACKING_FORWARDIG_PUR_AMT" || id == "ROUNDING_CHARGES_PUR_AMT") {
		    
		    
		     if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		   // alert("adjValue========222===="+adjValue);
		    
		     totaladjValueTaxForEntry = totaladjValueTaxForEntry + parseFloat(adjValue);
		    
		    
		    }//end
		   
		     
		    if(id == "OTHER_DISCOUNT_PUR_AMT" || id == "PRICE_DISCOUNT_PUR_AMT" || id == "QTY_DISCOUNT_PUR_AMT" || id == "ROUNDING_OFF_PUR_AMT") {
		    
		    
		       if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    
		     totaladjValueTaxForEntry = totaladjValueTaxForEntry - parseFloat(adjValue);
		    
		    }  
		   
		   
		    
		})
	
	
	
	$("#saleAmount").val(totaladjValueTaxForEntry+parseFloat(basicAmount));
	
	var purchaseValue = totaladjValueTaxForEntry+parseFloat(basicAmount);
	$("#basicAmount").val(purchaseValue);
	$("#purchaseBasicAmount").val(purchaseValue);
	  var totalPurPayableValue = purchaseValue;
	  $("#totalPurPayableValue").val(purchaseValue);
	  
	  
	  
	  var serviceChargeAmt1 = $("#serviceChargeAmt").val();
	  var saleValue = totaladjValueTaxForEntry+parseFloat(basicAmount);
	
	 $("#saleAmount").val(saleValue);
	  var tenPercentSubsidy = $("#tenPercentSubsidy").val();
	  var totalPayableValue = saleValue + parseFloat(tenPercentSubsidy) + parseFloat(serviceChargeAmt1);
	  $("#totalPayableValue").val(totalPayableValue);
	
	}
	
	
	
	function getReleventAmtPur(taxPercItemId){
	
	var totAmt = 0;
		
		if(taxPercItemId == "VAT_SURCHARGE_PUR")
		totAmt = $("#VAT_PUR_AMT").val();
		if(taxPercItemId == "CST_SURCHARGE_PUR")
		totAmt = $("#CST_PUR_AMT").val();
		
		return totAmt;
	}
	
	
	
	
	
	function getAmountWIthIncludeListPur(totalAmt){
	
	 var totalAmt = parseFloat(totalAmt);
	 
	 var adjType = $("#addAdjList").val();
	 
	 var checkedListPerent;
	 
	  var adjCheckedMap;
	  if(dataRow['adjCheckedMap'+"_"+adjType]){
	  adjCheckedMap = dataRow['adjCheckedMap'+"_"+adjType];
	   checkedListPerent = adjCheckedMap[adjType];
	  }
	    
	 
	  if(checkedListPerent != 'undefined' && checkedListPerent != null){
	  
	  
	    $.each(checkedListPerent, function(key, item){
	  
	  
	     //==============iterete Table============
	     
	     
		    var id = item;
		    
		    id = id+"_PUR_AMT";
		    
		    
		    if(id == "CST_PUR_AMT" || id == "VAT_PUR_AMT" || id == "ENTRY_TAX_PUR_AMT" || id == "CST_SURCHARGE_PUR_AMT" || id == "VAT_SURCHARGE_PUR_AMT" || id == "SERVICE_CHARGE_PUR_AMT")  {
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    
		   }//end
		    if(id == "CESS_PUR_AMT" || id == "INSURANCE_CHGS_PUR_AMT" || id == "OTHER_CHARGES_PUR_AMT" || id == "PACKING_FORWARDIG_PUR_AMT" || id == "ROUNDING_CHARGES_PUR_AMT") {
		    
		    
		     if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		     totalAmt = totalAmt + parseFloat(adjValue);
		    
		    
		    }//end
		   
		     
		    if(id == "OTHER_DISCOUNT_PUR_AMT" || id == "PRICE_DISCOUNT_PUR_AMT" || id == "QTY_DISCOUNT_PUR_AMT" || id == "TEN_PERCENT_SUBSIDY_PUR_AMT" || id == "ROUNDING_OFF_PUR_AMT") {
		    
		    
		       if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		     totalAmt = totalAmt - parseFloat(adjValue);
		    
		    
		    }  
		   
	    
	      });
	  
	  
	  }
	 
	   	   // alert("totalAmt=============="+totalAmt);
	   
	  return totalAmt;
	
	}
	
	
	
	//=====================================END==========================================
	
	function addDataToGridTestPur(){
		var totalTaxAmt = 0;
		var serviceChargeVal = 0;
		var otherChargesValue = 0;
		var discountValue = 0;
		
		
		var totTax = 0;
	 	var totAdj = 0;
	 	var totDis = 0;
		
		
		var totalAmt = dataRow["amount"];
		var quantity = parseFloat(dataRow["quantity"]);
		
		var purchaseBasicAmount = $("#purchaseBasicAmount").val();
		var totalPurPayableValue = $("#totalPurPayableValue").val();
				
		$("#indentAdjustmentTablePur tr :input:visible").each(function () {
		    var id = this.id;
		    
		    var adjValueAmt = $('#'+id).val();
		    
		   // alert(adjValueAmt);
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    
		  if(id == "CST_PUR_AMT" || id == "VAT_PUR_AMT" || id == "ENTRY_TAX_PUR_AMT" || id == "CST_SURCHARGE_PUR_AMT" || id == "VAT_SURCHARGE_PUR_AMT")  
		    totTax = totTax + parseFloat(adjValueAmt);
		  if(id == "CESS_PUR_AMT" || id == "INSURANCE_CHGS_PUR_AMT" || id == "OTHER_CHARGES_PUR_AMT" || id == "PACKING_FORWARDIG_PUR_AMT")    
		    totAdj = totAdj + parseFloat(adjValueAmt);
		  if(id == "OTHER_DISCOUNT_PUR_AMT" || id == "PRICE_DISCOUNT_PUR_AMT" || id == "QTY_DISCOUNT_PUR_AMT")     
		    totDis = totDis + parseFloat(adjValueAmt);
		    
		});
		
		
		dataRow["purchaseBasicAmount"] = purchaseBasicAmount;
		
		dataRow["totalPurPayableValue"] = totalPurPayableValue;
		
		
		
		
        /*		
		dataRow["baseAmount"] = baseAmount;
		dataRow["saleBaseAmt"] = saleBaseAmt;
		dataRow["saleAmount"] = saleAmount;
		dataRow["SERVICE_CHARGE_PUR_AMT"] = serviceChargeAmt;
		dataRow["SERVICE_CHARGE_AMT"] = serviceChargeAmt;
		dataRow["OTH_CHARGES_AMT"] = totAdj;
		dataRow["DISCOUNT_AMT"] = totDis;
		dataRow["tenPercent"] = tenPercentSubsidy;		
		dataRow["taxAmt"] = totTax;
		dataRow["totPayable"] = totalPayableValue;
		*/
		
		
		grid.updateRow(rowIndex);
		grid.render();
		cancelForm();
	}
		
	
	
	//===========================Sale Tax API========================================
	
	
	
	function MakeAdjTable(totalPurPayableValue){
	
	
	 var purchaseWithAdj = $("#basicAmount").val(); 
						
	
	var addAdjType = $("#addAdjListSale").val();
	
	var addFlag = "Y";
	
	$("#indentAdjustmentTable tr :input:visible").each(function () {
		    var id = this.id;
		    if(id == addAdjType+"_SALE")
		    addFlag = "N";
	 });
	
	
	if(addFlag == "Y"){
	var removeRow = "javascript:removeAdjustment()";
	
	var totalAmtparam = '\'' + purchaseWithAdj + '\'';
    var updateAmountByPercentage = "javascript:updateAmountByPercentage(this,"+ totalAmtparam +")";
    
    var updatePercentageByAmount = "javascript:updatePercentageByAmount(this,"+ totalAmtparam +")";
	
	
	
	var adjIdPer=addAdjType+"_SALE";
	
	var adjIdAmt=addAdjType+"_SALE_AMT";
	
	 if(addAdjType != ""){
	 
	    if(addAdjType != "SERVICE_CHARGE" && addAdjType != "TEN_PERCENT_SUBSIDY" && addAdjType != "ROUNDING_CHARGES" && addAdjType != "ROUNDING_OFF")
	    $(".myTable1").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'"  /></td><td>Remove</td><td><input type="button" style="width: 100px;" name="remove" class="delete" id="Remove" value="Remove" onclick="'+removeRow+'"></td></tr>');
	    else if(addAdjType == "ROUNDING_CHARGES" || addAdjType == "ROUNDING_OFF" )
	    $(".myTable1").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'"  onblur="'+updatePercentageByAmount+'" /></td><td>Remove</td><td><input type="button" style="width: 100px;" name="remove" class="delete" id="Remove" value="Remove" onclick="'+removeRow+'"></td></tr>');
	    else
	    $(".myTable1").append('<tr class="item"><td><font color="blue">'+addAdjType+'</font></td><td><input type="number" max="100" step=".5" maxlength="4" style="width: 50px;"  width="50px"  id="'+adjIdPer+'" name="'+adjIdPer+'" onblur="'+updateAmountByPercentage+'"   /></td><td>Amt:</td><td><input type="text" max="100"  step=".5" maxlength="4" style="width: 100px;"  width="100px" id="'+adjIdAmt+'" readonly/></td></tr>');
	 
	}
	
	
	}else{
	
	   alert("Sorry Already "+addAdjType+" Added");
	}
	
	}
	
	function removeAdjustment(){
	
	  $("#indentAdjustmentTable tr :input:visible").each(function () {
		    var id = this.id;
			//alert(id);
			
			if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = 0;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = 0;
			    	
			    }
		    }
            });
	
	
	  $('#indentAdjustmentTable tr').click(function () {
             $(".delete").live('click', function(event) {
	          $(this).parent().parent().remove();
	        adjustBasePriceNew();
           });
              
          });
	}
	
	
	
	function storeIncludeList(){
	
	 var allAdjustments = dataRow["allAdjustmentsSale"];
	 
	 var adjType = $("#addAdjListSale").val();
	 
	 
	 var checkedListPerent;
	 
	  var adjCheckedMap;
	  if(dataRow['adjCheckedMap'+"_"+adjType]){
	  adjCheckedMap = dataRow['adjCheckedMap'+"_"+adjType];
	   checkedListPerent = adjCheckedMap[adjType];
	  }
	 
	 
	 $('#addAdjGivenListSale').html('');
	
	  if(checkedListPerent != 'undefined' && checkedListPerent != null){
	  
	   var resultMap = {};
	  $.each(allAdjustments, function(key, item){
			   
			    var checkedList = adjCheckedMap[adjType];
			   
			   if(checkedList != 'undefined' && checkedList != null){
			    $.each(checkedList, function(key, item1){
			       if(item1 == item['orderAdjustmentTypeId']){
			         resultMap[item1] = "N";
			        }else{
			         resultMap[item1] = "Y";
			        }
			    });
			    
			    }
			    		  		
  	    });
  	    
  	     $.each(allAdjustments, function(key, item){
  	          if(resultMap[item['orderAdjustmentTypeId']] == "Y")
			  $('#addAdjGivenListSale').append($('<li>').html( "<font color='black'><input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedList' onclick='javascript:checkedAdjList();' checked><font size='100' color='black'>"+item['orderAdjustmentTypeId']+"</option>"));
  		      else
  		      $('#addAdjGivenListSale').append($('<li>').html( "<input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedListSale' onclick='javascript:checkedAdjList();'><font size='100' color='black'>"+item['orderAdjustmentTypeId']+"</option>"));
  	     });
  	    
	 
	 }else{
	 
	  $.each(allAdjustments, function(key, item){
	   $('#addAdjGivenListSale').append($('<li>').html( "<input type='checkbox' id='"+item['orderAdjustmentTypeId']+"' value='"+item['orderAdjustmentTypeId']+"' class='checkedListSale' onclick='javascript:checkedAdjList();'><font size='100' color='black'>"+item['orderAdjustmentTypeId']+"</option>"));
	  });
	 }
	}
	
	
	function checkedAdjList(){
	
	
	   var adjCheckedMap = {};
	
	   var adjType = $("#addAdjListSale").val();
	   
	   var checkedList = $('input:checkbox:checked.checkedListSale').map(function () {
								  	return this.id;
								  }).get();
											
		
		adjCheckedMap[adjType] = checkedList;
											
		
	   dataRow['adjCheckedMap'+"_"+adjType] = adjCheckedMap;
	
	
	}
	
	
	
	function updateAmountByPercentage(taxPercentItem, totalAmt){
	 	var percentage = taxPercentItem.value;
	 	var taxPercItemId = taxPercentItem.id;
	 	
	 	
	 	if(taxPercentItem.id == "TEN_PERCENT_SUBSIDY_SALE"){
	 	  var tenPer =  $("#TEN_PERCENT_SUBSIDY_SALE").val();
	 	  if(tenPer != 10){
	 	  $("#TEN_PERCENT_SUBSIDY_SALE").val(10);
	 	   percentage = 10;
	 	  }
	 	}
	 	  
	 	
	 	
	 	
	 	var taxValueItemId = taxPercentItem.id + "_AMT";
	 	if(percentage != 'undefined' && percentage != null && percentage.length){
	 	
	 	
	 	       var totalAmt = getAmountWIthIncludeList(totalAmt);
	 	       
	 	       if(taxPercItemId == "VAT_SURCHARGE_SALE" || taxPercItemId == "CST_SURCHARGE_SALE")
	 	       var totalAmt = getReleventAmt(taxPercItemId);
	 	
	 	// alert("totalAmt==============="+totalAmt);
	 	
	 		var taxValue = (percentage) * (totalAmt/100) ;
	 		$('#'+taxValueItemId).val(taxValue);
	 		
	 	}
	 	
	 	
	 	adjustBasePriceNew();
	}	
	
	
	
	
	function updatePercentageByAmount(taxAmountItem, totalAmt){
	 	var taxAmount = taxAmountItem.value;
	 	var taxAmtItemId = taxAmountItem.id;
	 	var taxPercItemId = (taxAmountItem.id).replace("_AMT", ""); 
	 	if(taxAmount != 'undefined' && taxAmount != null && taxAmount.length){
	 		var percentage = (taxAmount) * (100/totalAmt) ;
	 		$('#'+taxPercItemId).val(percentage);
	 	}
	 	updateAmountByPercentage(taxAmountItem,totalAmt);
	}
	
	
	
	
	
	
	
	
	
	function adjustBasePriceNew(){
	
	
	var basicAmount = $("#basicAmount").val();
	
	//alert(basicAmount);
	
	var totaladjValueTaxForEntry = 0;
	
	
	$("#indentAdjustmentTable tr :input:visible").each(function () {
		    var id = this.id;
		    
		    //===============Service Charge=================
		    if(id == "SERVICE_CHARGE_SALE"){
		    
		      var adjPercentage = $('#'+id).val();
		      
		      dataRow[id] = adjPercentage;
		      $("#serviceCharge1").html(adjPercentage);
		      
		      $("#serviceChargeIN").val(adjPercentage);
		      
		    } 
		    
		    if(id == "SERVICE_CHARGE_SALE_AMT"){
		    
		     if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    }
			    
			    
			   // alert("adjValue================"+adjValue);
		    
		      $("#serviceChargeAmt").val(adjValue);
		     
		    }  
		    
		   //==========================END==================== 
		   
		   
		   //=========================TenPercent=====================
		 
		   
		   
		    if(id == "TEN_PERCENT_SUBSIDY_SALE_AMT"){
		       if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    }
		      $("#tenPercentSubsidy").val(-1*parseFloat(adjValue));
		    }  
		   
		    
		    
		    if(id == "CST_SALE_AMT" || id == "VAT_SALE_AMT" || id == "ENTRY_TAX_SALE_AMT" || id == "CST_SURCHARGE_SALE_AMT" || id == "VAT_SURCHARGE_SALE_AMT")  {
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    	
			    }
		    }
		    
		    
		      
		    
		     totaladjValueTaxForEntry = totaladjValueTaxForEntry + parseFloat(adjValue);
		    
		   }//end
		    if(id == "CESS_SALE_AMT" || id == "INSURANCE_CHGS_SALE_AMT" || id == "OTHER_CHARGES_SALE_AMT" || id == "PACKING_FORWARDIG_SALE_AMT" || id == "ROUNDING_CHARGES_SALE_AMT") {
		    
		    
		     if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    //alert("adjValue========222===="+adjValue);
		    
		     totaladjValueTaxForEntry = totaladjValueTaxForEntry + parseFloat(adjValue);
		    
		    
		    }//end
		   
		     
		    if(id == "OTHER_DISCOUNT_SALE_AMT" || id == "PRICE_DISCOUNT_SALE_AMT" || id == "QTY_DISCOUNT_SALE_AMT" || id == "ROUNDING_OFF_SALE_AMT" ) {
		    
		    
		       if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		     totaladjValueTaxForEntry = totaladjValueTaxForEntry - parseFloat(adjValue);
		    
		    }  
		   
		   
		    
		})
	
	
	var serviceChargeAmt1 = $("#serviceChargeAmt").val();
	
	
	//alert("basicAmount============="+basicAmount);
	
	
	var saleValue = totaladjValueTaxForEntry+parseFloat(basicAmount);
	
		//alert("saleValue============="+saleValue);
	
	
	 $("#saleAmount").val(saleValue);
	
	  var tenPercentSubsidy = $("#tenPercentSubsidy").val();
	  
	  
	  var totalPayableValue = saleValue - parseFloat(tenPercentSubsidy) + parseFloat(serviceChargeAmt1);
	
	  $("#totalPayableValue").val(totalPayableValue);
	
	}
	
	
	
	
	function getReleventAmt(taxPercItemId){
	
	var totAmt = 0;
		
		if(taxPercItemId == "VAT_SURCHARGE_SALE")
		totAmt = $("#VAT_SALE_AMT").val();
		if(taxPercItemId == "CST_SURCHARGE_SALE")
		totAmt = $("#CST_SALE_AMT").val();
		
		return totAmt;
	}
	
	
	
	
	
	function getAmountWIthIncludeList(totalAmt){
	
	 var totalAmt = parseFloat(totalAmt);
	 
	
	   // alert("totalAmt=============="+totalAmt);
	 
	    
	 var adjType = $("#addAdjListSale").val();
	 
	 var checkedListPerent;
	 
	  var adjCheckedMap;
	  if(dataRow['adjCheckedMap'+"_"+adjType]){
	  adjCheckedMap = dataRow['adjCheckedMap'+"_"+adjType];
	   checkedListPerent = adjCheckedMap[adjType];
	  }
	    
	 
	  if(checkedListPerent != 'undefined' && checkedListPerent != null){
	  
	  
	    $.each(checkedListPerent, function(key, item){
	  
	  
	     //==============iterete Table============
	     
	     
		    var id = item;
		    
		    id = id+"_SALE_AMT";
		    
		   // alert("id==================="+id);
		    
		    if(id == "CST_SALE_AMT" || id == "VAT_SALE_AMT" || id == "ENTRY_TAX_SALE_AMT" || id == "CST_SURCHARGE_SALE_AMT" || id == "VAT_SURCHARGE_SALE_AMT" || id == "SERVICE_CHARGE_SALE_AMT")  {
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		     if(id == "SERVICE_CHARGE_SALE_AMT"){
		    
		    var serviceChargeAmt = $("#serviceChargeAmt").val();
		    
		    totalAmt = totalAmt + parseFloat(serviceChargeAmt);
		    
		    }else{
		    
		     totalAmt = totalAmt + parseFloat(adjValue);
		     
		     }
		    
		    
		    
		   }//end
		    if(id == "CESS_SALE_AMT" || id == "INSURANCE_CHGS_SALE_AMT" || id == "OTHER_CHARGES_SALE_AMT" || id == "PACKING_FORWARDIG_SALE_AMT" || id == "ROUNDING_CHARGES_SALE_AMT") {
		    
		    
		     if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		     totalAmt = totalAmt + parseFloat(adjValue);
		    
		    
		    }//end
		   
		     
		    if(id == "OTHER_DISCOUNT_SALE_AMT" || id == "PRICE_DISCOUNT_SALE_AMT" || id == "QTY_DISCOUNT_SALE_AMT" || id == "TEN_PERCENT_SUBSIDY_SALE_AMT" || id == "ROUNDING_OFF_SALE_AMT") {
		    
		    
		       if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    
		     if(id == "TEN_PERCENT_SUBSIDY_SALE_AMT"){
		    
		    var tenPercentSubsidy = $("#tenPercentSubsidy").val();
		    
		    totalAmt = totalAmt - parseFloat(tenPercentSubsidy);
		    
		    }else{
		    
		     totalAmt = totalAmt - parseFloat(adjValue);
		     
		     }
		    
		     
		    
		    
		    }  
		   
	    
	      });
	  
	  
	  }
	 
	   	   // alert("totalAmt=============="+totalAmt);
	   
	  return totalAmt;
	
	}
	
	
	
	
	function addDataToGridTest(){
		
		var totalTaxAmt = 0;
		var serviceChargeVal = 0;
		var otherChargesValue = 0;
		var discountValue = 0;
		
		
		var totTax = 0;
	 	var totAdj = 0;
	 	var totDis = 0;
		
		
		//alert("vamsi");
		
		var totalAmt = dataRow["amount"];
		var quantity = parseFloat(dataRow["quantity"]);
		
		var purchaseBasicAmount = $("#basicAmount").val();
		//var saleBasicAmount = $("#purchaseAmount").val();
		var tenPercentSubsidy = $("#tenPercentSubsidy").val();
		var serviceChargeAmt = $("#serviceChargeAmt").val();
		var totalPayableValue = $("#totalPayableValue").val();
		var baseAmount = $("#baseAmount").val();
		var saleBaseAmt = $("#saleBaseAmt").val();
		var saleAmount = $("#saleAmount").val();
		
		
		var scheme = $("#schemeCategory").val();
		
		if(scheme == "General"){
		 var salebasePrice = purchaseBasicAmount/quantity;
		 dataRow["unitPrice"] = salebasePrice;
		 dataRow["KgunitPrice"] = salebasePrice;
		 dataRow["amount"] = salebasePrice*quantity;
		 
		}
		
		var serviceChargePer = $("#serviceChargeIN").val();
				
		$("#indentAdjustmentTable tr :input:visible").each(function () {
		    var id = this.id;
		    
		    var adjValueAmt = $('#'+id).val();
		    
		   // alert(adjValueAmt);
		    
		    if(id != 'undefined' && id != null && id.length){
		    	if (id.indexOf("_AMT") >= 0){
			    	var adjValue = $('#'+id).val();
			    	dataRow[id] = adjValue;
			    	//otherChargesValue = otherChargesValue+adjValue/100*100 ;
			    }
			    else{
			    	var adjPercentage = $('#'+id).val();
			    	dataRow[id] = adjPercentage;
			    }
		    }
		    
		    
		  if(id == "CST_SALE_AMT" || id == "VAT_SALE_AMT" || id == "ENTRY_TAX_SALE_AMT" || id == "CST_SURCHARGE_SALE_AMT" || id == "VAT_SURCHARGE_SALE_AMT")  
		    totTax = totTax + parseFloat(adjValueAmt);
		  if(id == "CESS_SALE_AMT" || id == "INSURANCE_CHGS_SALE_AMT" || id == "OTHER_CHARGES_SALE_AMT" || id == "PACKING_FORWARDIG_SALE_AMT" || id == "ROUNDING_CHARGES_SALE_AMT")    
		    totAdj = totAdj + parseFloat(adjValueAmt);
		  if(id == "OTHER_DISCOUNT_SALE_AMT" || id == "PRICE_DISCOUNT_SALE_AMT" || id == "QTY_DISCOUNT_SALE_AMT" || id == "ROUNDING_OFF_SALE_AMT")     
		    totDis = totDis + parseFloat(adjValueAmt);
		    
		    
		    
		    
		    
		})
		
		
		
		dataRow["baseAmount"] = baseAmount;
		dataRow["saleBaseAmt"] = saleBaseAmt+totTax+totAdj+totDis;
		
		//alert("saleAmount============"+saleAmount);
		
		//alert("totTax============"+totTax);
		
		//alert("totAdj============"+totAdj);
		
		//alert("totDis============"+totDis);
		
		
		
		dataRow["saleAmount"] = saleAmount;
		
		
		dataRow["SERVICE_CHARGE_SALE_AMT"] = serviceChargeAmt;
		dataRow["SERVICE_CHARGE_AMT"] = serviceChargeAmt;
		dataRow["SERVICE_CHARGE_SALE"] = serviceChargePer;
		
		dataRow["OTH_CHARGES_AMT"] = totAdj;
		dataRow["DISCOUNT_AMT"] = totDis;
		dataRow["SUBSIDY"] = tenPercentSubsidy;		
				
				
		dataRow["taxAmt"] = totTax;
		dataRow["totPayable"] = totalPayableValue;
		
		updateTotalIndentAmount();
		
		grid.updateRow(rowIndex);
		grid.render();
		
		addDataToGridTestPur();
		
		cancelForm();
	}
	
	
	//=========================================END====================
	
	function changeServiceChargePercent() {
		
		
		var editDestination = $("#editDestination").val();
		
		if($("#changeDesti").val())
	     editDestination = $("#changeDesti").val();
		
		var message = "";
		var title = "";
		message += "<table cellspacing=20 cellpadding=20 id='serviceChgUpdationTable' >" ;
		message += "<hr class='style17'></hr>";
		message += "<tr class='h3'><th>Edit Destination </th></tr>";
		message += "<tr class='h3'><td align='left'>Edit Destination: </td><td><input type='text' name='editDestina' id='editDestina' value='"+editDestination+"' onblur='changedesti();'/></td></tr>";
		message += "<tr class='h3'><td class='h3' align='left'><span align='right'><button value='Add'  class='smallSubmit'>Add</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table>";
		title = "<h2><center>Edit Destination<center></h2>";
		Alert(message, title);
	};
	
	function updateServiceChargeAndGrid(){
		 $('#serviceChargePercent').val($('#serviceChgPercent').val());
		 //updateProductTotalAmount();
		 $("#serviceCharge").html("<b>"+$('#serviceChgPercent').val()+"% Service Charge is applicable</b>");
		 updateServiceChargeAmounts();
		 cancelForm();
	}
	
	function changedesti(){
	
	  $("#changeDesti").val( $("#editDestina").val());
	
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