<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">

		var billToPartyIdsList = ${StringUtil.wrapString(billToPartyIdsJSON)}
					 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	var organisationList;
	var partyName;
	var partyIdVal;
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
				  //getEmploymentDetails(partyIdVal);
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
	
	
 	function cancelForm(){                 
         return false;
 	}
 	
 	function datepick1()	{	
		$( "#saleOrderDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: false,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).val(selectedDate);
			}
			
			});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		//
	}
	function autoCompletePartyId(){
	
	      $("#partyId").autocomplete({ 
	      
	      		
	      		source: function( request, response ) {
        			$.ajax({
          					url: "LookupEmpanelledPartyName",
          					dataType: "html",
          					data: {
            					ajaxLookup: "Y",
            					term : request.term
          					},
          					success: function( data ) {
          						var dom = $(data);
        						dom.filter('script').each(function(){
            						$.globalEval(this.text || this.textContent || this.innerHTML || '');
        						});
            					response($.map(autocomp, function(v,i){
            						$('span#partyTooltip').html('<label>'+v.label+'</label>');
    								return {
                						label: v.label,
                						value: v.value
               						};
								}));
          					}
        			});
        			
      			},
	      		
	      		
	      		
	      		select: function( event, ui ) {
				$('span#partyTooltip').html('<label>'+ui.item.label+'</label>');
					fillPartyQuota(ui.item.value);
				}
				
				
		  });	
	 }
	function showTotalAmount(quantityStr, unitCostStr, quantityOnHandTotal){
		var quantity = quantityStr.value;
		var unitCost = unitCostStr.value;
		unitCost = unitCost.replace(/[^0-9\.]/g, '');
		createSaleIndent.unitCost.value=unitCost;
		var qtyOnHand = quantityOnHandTotal.value;
		if(qtyOnHand-quantity < 0 ){
			alert("Indenting Quantity Cannot Exceed Inventory.!");
			createSaleIndent.quantity.value='';
			createSaleIndent.indentAmount.value='';
			document.createSaleIndent.quantity.focus();
		}
		else{
			var indentAmt = quantity*unitCost;
	    	createSaleIndent.indentAmount.value=indentAmt;
	    }
	}
	function calculateKgs(quantity,uom,org2,quantityStr,unitCostStr){
	   var uom =uom.value;
	   var quantity = quantity.value;
	   var org2 =org2.value;
	   var result=0;
	    if(uom == "Bale"){
			 result=quantity*(org2*40);
			}
		else if(uom == "Half-Bale"){
				result=quantity*(org2*20);
			}
		else if(uom == "Bundle"){
				result = quantity*org2;
			}
		else if(uom == "KGs"){				
			result = quantity;
		    }		    
		$("#quantity").val(result);
		var quantity = quantityStr.value;
		var unitCost = unitCostStr.value;		
		unitCost = unitCost.replace(/[^0-9\.]/g, '');
		//createSaleIndent.unitCost.value=unitCost;
		var qtyOnHand = quantityOnHandTotal.value;
		if(qtyOnHand-quantity < 0 ){
			alert("Indenting Quantity Cannot Exceed Inventory.!");
			createSaleIndent.quantity.value='';
			createSaleIndent.indentAmount.value='';
			document.createSaleIndent.quantity.focus();
		}
		else{
			var indentAmt = result*unitCost;
	    	createSaleIndent.indentAmount.value=indentAmt;
	    }
		return result;
	 }
	 	 	   
	function showInventorySaleQTip(productId, quantityOnHandTotal, unitCost, inventoryItemId, productStoreId, uom, bundleWeight,bundleUnitPrice) {
		var message = "";
		message += "<form action='processInventorySalesOrder' method='post' onsubmit='return disableSubmitButton();' name='createSaleIndent' id='createSaleIndent'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Product:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='productId' name='productId' value='"+productId+"'/></td><input class='h4' type='hidden' readonly id='productStoreId' name='productStoreId' value='"+productStoreId+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Order Date:</td><td align='left' width='50%'><input type='text' id='saleOrderDate' name='saleOrderDate' onmouseover='datepick1()'/></td><input type='hidden'  id='effectiveDate' name='effectiveDate'/><input type='hidden'  id='quantityOnHandTotal' name='quantityOnHandTotal' value='"+quantityOnHandTotal+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Bill to Party:</td><td align='left' width='70%'><input class='h3' type='text' id='partyId' name='partyId' onclick='javascript:autoCompletePartyId();' size='13'/><span align='right' id='partyTooltip'></span></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Scheme Category:</td><td align='left' width='70%'><select name='schemeCategory' id='schemeCategory' onchange='getQuota(this)' class='h3' style='width:162px'></select></td></tr>";
		message +=  "<tr id='quotatr' style='display:none' class='h3'><td align='left'  class='h3' width='50%'>Available Quota:</td><td align='left' width='70%'><input class='h3' type='text' id='quota' readonly name='quota' size='13'/></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Qty(Nos):</td><td align='left' width='50%'><input class='h3' type='text' id='baleQuantity' name='baleQuantity' value='' onblur='calculateKgs(baleQuantity,uom,bundleWeight,unitCost,quantityOnHandTotal);'/></tr>";
		message +="<tr class='h3'><td align='left' class='h3' width='60%'>UOM :</td><td align='left' width='60%'><select name='uom' id='uom'  class='h4' onchange='calculateKgs(baleQuantity,uom,bundleWeight);'>"+
						"<option value="+uom+" selected>"+uom+"</option>"+
						"<option value='Bale'>Bale</option>"+
						"<option value='Half-Bale'>Half-Bale</option>"+
						"<option value='Bundle'>Bundle</option>"+
					    "</select></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Bundle Weight():</td><td align='left' width='50%'><input class='h3' type='text' readonly id='bundleWeight' name='bundleWeight' value='"+bundleWeight+"'/></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Unit Price(Bundle):</td><td align='left' width='50%'><input class='h3' type='text' readonly id='bundleUnitPrice' name='bundleUnitPrice' value='"+bundleUnitPrice+"'/></td> </tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Unit Cost:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='unitCost' name='unitCost' value='"+unitCost+"'/></td><input type='hidden'  id='disableAcctgFlag' name='disableAcctgFlag' value='Y'/><input type='hidden'  id='inventoryItemId' name='inventoryItemId' value='"+inventoryItemId+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Quantity:</td><td align='left' width='50%'><input class='h3' type='text' id='quantity' name='quantity'/></td><input type='hidden'  id='salesChannel' name='salesChannel' value='WEB_SALES_CHANNEL'/></tr>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Total Indent Amount:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='indentAmount' name='indentAmount' value=''/></td></tr>";
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Create Sale Indent<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};

function getQuota(obj){
$("#quotatr").hide();
	if($(obj).val()=="MGPS_10Pecent"){
	  $("#quotatr").show();
	}

}
	
function fillPartyQuota(partyId){

	if( partyId != undefined && partyId != ""){
				var dataString="partyId="+partyId+"&productId="+$("#productId").val();
	      	$.ajax({
	             type: "POST",
	             url: "getpartyQuotaDetails",
	           	 data: dataString ,
	           	 dataType: 'json',
	           	 async: false,
	        	 success: function(result) {
	              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	       	  		 alert(result["_ERROR_MESSAGE_"]);
	          			}else{ 
	          			      $("#quotatr").hide();
	       	  				  contactDetails =result["quotaJson"];
	       	  				  $("#quota").val(contactDetails['quota']);
	       	  				  SchemeList=contactDetails["SchemeList"];
	       	  				  var tableElement="";
	       	  				  
	       	  				   $.each(SchemeList, function(key, item){
		       	  				    tableElement +="<option value='"+item['schemeId']+"'>"+item['schemeValue']+"</option>";
		       	  				 });
		       	  			$('#schemeCategory').empty().append(tableElement);
							 if($('#schemeCategory').val()=="MGPS_10Pecent"){
		       	  				    	$("#quotatr").show();
		       	  				    }
	      			}
	               
	          	} ,
	         	 error: function() {
	          	 	alert(result["_ERROR_MESSAGE_"]);
	         	 }
	         	 });
	         	 }
	        }
</script>
