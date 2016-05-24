
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">




/*
	var boothsData = { "2055" : {"boothDuesList" : [{"supplyDate" : "17/05/2012", "amount" : "Rs15.00"},
								 					{"supplyDate" : "13/05/2012", "amount" : "Rs217,000.00"}],
								 "totalAmount" : "Rs217,015.00"
								}
					 };
*/								 
	var productName;			 
	var dataRow;
	var rowIndex;
	var vatSurchargeList;
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
				           $('.CST').hide();
				           $('.VAT').show();
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
	
	function showUDPPriceToolTip(gridRow, index) {
	
		rowIndex = index;
		dataRow = gridRow;
		productName = dataRow["cProductName"];
		var vatSale = dataRow["VAT_SALE"];
		var vatSaleAmt = dataRow["VAT_SALE_AMT"];
		var cstSale = dataRow["CST_SALE"];
		var cstSaleAmt = dataRow["CST_SALE_AMT"];
		if(dataRow["vatSurchargeList"] != 'undefined' || dataRow["vatSurchargeList"] != null){
			vatSurchargeList = dataRow["vatSurchargeList"];
		}
		var taxList = dataRow["taxList"];
		var totalAmt = dataRow["amount"];
		var serviceCharge = dataRow["SERVICE_CHARGE"];
		var serviceChargeAmt = dataRow["SERVICE_CHARGE_AMT"];
		var message = "";
		var title = "";
		var priceExists = "N";
		var applicableTaxType = dataRow["applicableTaxType"];
		var checkE2Form = dataRow["checkE2Form"];
		var checkCForm = dataRow["checkCForm"];
		
		if(applicableTaxType != 'undefined' && applicableTaxType != null && applicableTaxType != ""){	
			$("#orderTaxType").val(applicableTaxType);
		}
		if(dataRow){
		
			message += "<table cellspacing=20 cellpadding=20 id='taxUpdationTable' >" ;
			message += "<hr class='style17'></hr>";
			message += "<tr class='h3'><th>Taxes </th></tr>";
			
			
			if($("#orderTaxType").val() == "Inter-State"){
			
				if(applicableTaxType == "Intra-State"){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' checked/>Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State'/>Inter State<br> </td>  </tr>";
				}
				if(applicableTaxType == "Inter-State"){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' />Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State' checked/>Inter State<br> </td>  </tr>";
				}
				if(applicableTaxType == 'undefined' || applicableTaxType == null || applicableTaxType == ""){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' checked/>Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State'/>Inter State<br> </td>  </tr>";
				}
				
				
				
				if(checkE2Form == "E2_FORM"){
					message += "<tr class='IntraState' style='display:none;'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' checked/>Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' />Transactions Without E2 Form<br> </td> </tr>";
				}
				if(checkE2Form == "NO_E2_FORM"){
					message += "<tr class='IntraState' style='display:none;'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' />Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' checked/>Transactions Without E2 Form<br> </td> </tr>";
				}
				if(checkE2Form == 'undefined' || checkE2Form == null){
					message += "<tr class='IntraState' style='display:none;'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' />Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' />Transactions Without E2 Form<br> </td> </tr>";
				}
				
				
				if(checkCForm == "CST_CFORM"){
					message += "<tr class='InterState'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' checked/>Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM'/>Transactions Without C Form<br> </td></tr>";
				}
				if(checkCForm == "CST_NOCFORM"){
					message += "<tr class='InterState'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' />Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM' checked/>Transactions Without C Form<br> </td></tr>";
				}
				if(checkCForm == 'undefined' || checkCForm == null){
					message += "<tr class='InterState'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' checked/>Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM'/>Transactions Without C Form<br> </td></tr>";
				}
				
				
				
			}
			if($("#orderTaxType").val() == "Intra-State"){
				
				if(applicableTaxType == "Intra-State"){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' checked/>Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State' />Inter State<br> </td>  </tr>";
				}
				if(applicableTaxType == "Inter-State"){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' />Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State' checked/>Inter State<br> </td>  </tr>";
				}
				if(applicableTaxType == 'undefined' || applicableTaxType == null || applicableTaxType == ""){
					message += "<tr ><td><input type='radio' name='applicableTaxType' value='Intra-State' checked/>Intra State<br> </td>  <td></td>   <td><input type='radio' name='applicableTaxType' value='Inter-State' />Inter State<br> </td>  </tr>";
				}
				
				
				if(checkE2Form == "E2_FORM"){
					message += "<tr class='IntraState'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' checked/>Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' />Transactions Without E2 Form<br> </td> </tr>";
				}
				if(checkE2Form == "NO_E2_FORM"){
					message += "<tr class='IntraState'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' />Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' checked/>Transactions Without E2 Form<br> </td> </tr>";
				}
				if(checkE2Form == 'undefined' || checkE2Form == null){
					message += "<tr class='IntraState'><td><input type='radio' name='checkE2Form' id='E2_FORM' value='E2_FORM' />Transactions With E2 Form<br> </td> <td></td> <td><input type='radio' name='checkE2Form' id='NO_E2_FORM' value='NO_E2_FORM' checked/>Transactions Without E2 Form<br> </td> </tr>";
				}
				
				
				if(checkCForm == "CST_CFORM"){
					message += "<tr class='InterState' style='display:none;'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' checked/>Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM'/>Transactions Without C Form<br> </td></tr>";
				}
				if(checkCForm == "CST_NOCFORM"){
					message += "<tr class='InterState' style='display:none;'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' />Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM' checked/>Transactions Without C Form<br> </td></tr>";
				}
				if(checkCForm == 'undefined' || checkCForm == null){
					message += "<tr class='InterState' style='display:none;'><td><input type='radio' name='checkCForm'  id='CST_CFORM' value='CST_CFORM' checked/>Transactions With C Form<br> </td> <td></td> <td><input type='radio' name='checkCForm' id='CST_NOCFORM' value='CST_NOCFORM'/>Transactions Without C Form<br> </td></tr>";
				}
				
				
				
				
				
				
			}
			
			message += "</table>";
			
			
			message += "<hr class='style18'></hr>";
			message += "<table cellspacing=20 cellpadding=20 id='taxTable' >" ;
			
			if($("#orderTaxType").val() == "Inter-State"){
				
				if(checkCForm == "CST_CFORM"){
					message += "<tr class='CST'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT' style='display:none;'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT' style='display:none;'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				if(checkCForm == "CST_NOCFORM"){
					message += "<tr class='CST' style='display:none;'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				if(checkCForm == 'undefined' || checkCForm == null){
					message += "<tr class='CST'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT' style='display:none;'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT' style='display:none;'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				
				
			}
			if($("#orderTaxType").val() == "Intra-State"){
			
			
				if(checkE2Form == "E2_FORM"){
					message += "<tr class='CST'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT' style='display:none;'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT' style='display:none;'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				
				if(checkE2Form == "NO_E2_FORM"){
					message += "<tr class='CST' style='display:none;'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				
				if(checkE2Form == 'undefined' || checkE2Form == null){
					message += "<tr class='CST' style='display:none;'><td align='left'>Cst Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='CST_SALE' id='CST_SALE' value='"+cstSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='CST_SALE_AMT' id='CST_SALE_AMT' value='"+cstSaleAmt+"' readOnly></td></tr>";
					message += "<tr class='VAT'><td align='left'>Vat Sale %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='VAT_SALE' id='VAT_SALE' value='"+vatSale+"' onblur='javascript:adjustAmount(this,"+totalAmt+");'/></td><td align='left'> Amt: </td><td><input type='text' name='VAT_SALE_AMT' id='VAT_SALE_AMT' value='"+vatSaleAmt+"' readOnly></td></tr>";
					for(var i=0;i<vatSurchargeList.length;i++){
						var taxType = vatSurchargeList[i];
						var taxPercentage = dataRow[taxType];
						var taxValue = dataRow[taxType + "_AMT"];
						
						message += "<tr class='VAT'><td align='left'>"+taxType+" %: </td><td><input type='number' max='100' step='.5' size='6' width='50px' name='"+taxType+"' id='"+taxType+"' value='"+taxPercentage+"' onblur='javascript:adjustSurcharge(this,VAT_SALE_AMT);'/></td><td align='left'> Amt: </td><td><input type='text' name='"+taxType+"_AMT' id='"+taxType+"_AMT' value='"+taxValue+"' readOnly></td></tr>";
					}
				}
				
			}
			
			message += "<tr class='h3'><td></td></tr>";
			message += "<tr class='h3'><td></td></tr>";
			message += "<tr class='h3'><td class='h3' align='left'><span align='right'><button value='Add Price' onclick='return addDataToGrid();' class='smallSubmit'>Add Price</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			message += "</table>";
			message += "<hr class='style18'></hr>";
			message += "<hr class='style17'></hr>";
			title = "<h2><center>User Defined Price <center></h2><br /><center>"+ productName +"</center> ";
			
			Alert(message, title);
			
			
		}
		
	};
	
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
	     		    message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>Country: </td><td align='left' width='60%'><select class='h4'  id='country'  name='country' onchange='setServiceName(this)'/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>State: </td><td align='left' width='60%'><select class='h4'  id='stateProvinceGeoId'  name='stateProvinceGeoId' onchange='storeValues();'/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>City: <font color=red>*</font></td><td align='left' width='60%'><input type='text' class='h4'  id='city'  name='city' onchange='storeValues();' required/></td></tr>";
	     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>PostalCode: <font color=red>*</font> </td><td align='left' width='60%'><input type='text' class='h4'  id='postalCode'  name='postalCode' onblur='storeValues();' /></td></tr>";
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
		CountryOptionList += "<option value = IND selected>India  </option>";          			

 		if(CountryJsonMap != undefined && CountryJsonMap != ""){
			$.each(CountryJsonMap, function(key, item){
			    CountryOptionList += "<option value = " + item.value + " >" + item.label + "</option>";          			
			});
	 	}
		CountryOptions = CountryOptionList;
 		jQuery("[name='country']").html(CountryOptions);
 					 
 		if(StateJsonMap != undefined && StateJsonMap != ""){
			$.each(StateJsonMap, function(key, item){
			                StateOptionList += "<option value = " + item.value + " >" + item.label + "</option>";          			
			});
	 	}
		 
		StateOptions = StateOptionList;
 		jQuery("[name='stateProvinceGeoId']").html(StateOptions);
	} 	
	
</script>
