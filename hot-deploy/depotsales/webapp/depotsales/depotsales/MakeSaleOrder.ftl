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
        			
      			}
	      		
	      		
	      		
	      		//source: billToPartyIdsList , select: function( event, ui ) {
				//$('span#partyTooltip').html('<label>'+ui.item.label+'</label>');
				//}
				
				
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
	 
 	
	function showInventorySaleQTip(productId, quantityOnHandTotal, unitCost, inventoryItemId, productStoreId) {
		var message = "";
		message += "<form action='processInventorySalesOrder' method='post' onsubmit='return disableSubmitButton();' name='createSaleIndent' id='createSaleIndent'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Product:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='productId' name='productId' value='"+productId+"'/></td><input class='h4' type='hidden' readonly id='productStoreId' name='productStoreId' value='"+productStoreId+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Order Date:</td><td align='left' width='50%'><input type='text' id='saleOrderDate' name='saleOrderDate' onmouseover='datepick1()'/></td><input type='hidden'  id='effectiveDate' name='effectiveDate'/><input type='hidden'  id='quantityOnHandTotal' name='quantityOnHandTotal' value='"+quantityOnHandTotal+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Bill to Party:</td><td align='left' width='70%'><input class='h3' type='text' id='partyId' name='partyId' onclick='javascript:autoCompletePartyId();' size='13'/><span align='right' id='partyTooltip'></span></td></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Unit Cost:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='unitCost' name='unitCost' value='"+unitCost+"'/></td><input type='hidden'  id='disableAcctgFlag' name='disableAcctgFlag' value='Y'/><input type='hidden'  id='inventoryItemId' name='inventoryItemId' value='"+inventoryItemId+"'/></tr>";
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Quantity:</td><td align='left' width='50%'><input class='h3' type='text' id='quantity' name='quantity' value='' onBlur='showTotalAmount(quantity , unitCost, quantityOnHandTotal)'/></td><input type='hidden'  id='salesChannel' name='salesChannel' value='DEPOT_CHANNEL'/></tr>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Total Indent Amount:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='indentAmount' name='indentAmount' value=''/></td></tr>";
		message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Create Sale Indent<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
	
	
</script>
