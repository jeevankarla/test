
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
		var message = "";
		var title = "";
		var priceExists = "N";
		if(dataRow){
			var basicPrice  = 0;
			var vatPrice  = 0;
			var bedPrice  = 0;
			var cstPrice  = 0;
			var serviceTaxPrice  = 0;
			if(dataRow["basicPrice"]){
				basicPrice = dataRow["basicPrice"];
			}
			if(dataRow["vatPrice"]){
				vatPrice = dataRow["vatPrice"];
			}
			if(dataRow["bedPrice"]){
				bedPrice = dataRow["bedPrice"];
			}
			if(dataRow["cstPrice"]){
				cstPrice = dataRow["cstPrice"];
			}
			if(dataRow["serviceTaxPrice"]){
				serviceTaxPrice = dataRow["serviceTaxPrice"];
			}
			
			if(basicPrice>0 || vatPrice>0 || bedPrice>0 || cstPrice>0 || serviceTaxPrice>0){
				priceExists = "Y";
			}
		}
		
		message += "<table cellspacing=10 cellpadding=10>" ;
		if(priceExists == "N"){
			message += "<tr class='h3'><td align='left'>Basic Price : </td><td align='right'><input type='text' name='amount' id='basicAmount' /></td></tr><tr class='h3'><td align='left'>VAT Amount : </td><td><input type='text' name='vatAmount' id='vatAmount'></td></tr>";
			message += "<tr class='h3'><td align='left'>CST Amount : </td><td><input type='text' name='cstAmount' id='cstAmount'/></td></tr><tr class='h3'><td align='left'>BED Amount : </td><td><input type='text' name='bedAmount' id='bedAmount'></td></tr>";
			message += "<tr class='h3'><td align='left'>Service Tax Amount : </td><td><input type='text' name='serviceTaxAmount' id='serviceTaxAmount' /></td></tr>";
			message += "<tr class='h3'><td class='h3' align='left'><span align='right'><button value='Add Price' onclick='return addDataToGrid();' class='smallSubmit'>Add Price</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		}else{
			message += "<tr class='h3'><td align='left'>Basic Price : </td><td><input type='text' name='amount' id='basicAmount' value='"+basicPrice+"'/></td></tr><tr class='h3'><td align='left'>VAT Amount : </td><td><input type='text' name='vatAmount' id='vatAmount' value='"+vatPrice+"'></td></tr>";
			message += "<tr class='h3'><td align='left'>CST Amount : </td><td><input type='text' name='cstAmount' id='cstAmount' value='"+cstPrice+"'/></td></tr><tr class='h3'><td align='left'>BED Amount : </td><td><input type='text' name='bedAmount' id='bedAmount' value='"+bedPrice+"'></td></tr>";
			message += "<tr class='h3'><td align='left'>Service Tax Amount : </td><td><input type='text' name='serviceTaxAmount' id='serviceTaxAmount' value='"+serviceTaxPrice+"'/></td></tr>";
			message += "<tr class='h3'><td align='left'><span align='right'><button value='Add Price' onclick='return addDataToGrid();' class='smallSubmit'>Add Price</button></span></td><td><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		}	
		title = "<h2><center>User Defined Price <center></h2><br /><br /> <center>"+ productName +"</center> ";
		message += "</table>";
		Alert(message, title);
		
	};
	
	function addDataToGrid(){
		var bPrice = $('#basicAmount').val();
		var cstPrice = $('#cstAmount').val();
		var vatPrice = $('#vatAmount').val();
		var bedPrice = $('#bedAmount').val();
		var serviceTaxPrice = $('#serviceTaxAmount').val();
		if(!bPrice){
			bPrice = 0;
		}else{
			bPrice = parseFloat(bPrice);
		}
		if(!cstPrice){
			cstPrice = 0;
		}else{
			cstPrice = parseFloat(cstPrice);
		}
		if(!vatPrice){
			vatPrice = 0;
		}else{
			vatPrice = parseFloat(vatPrice);
		}
		if(!bedPrice){
			bedPrice = 0;
		}else{
			bedPrice = parseFloat(bedPrice);
		}
		if(!serviceTaxPrice){
			serviceTaxPrice = 0;
		}else{
			serviceTaxPrice = parseFloat(serviceTaxPrice);
		}
		var totalUnitPrice = bPrice+cstPrice+vatPrice+bedPrice+serviceTaxPrice;
		dataRow['basicPrice'] = bPrice;
		dataRow['cstPrice'] = cstPrice;
		dataRow['vatPrice'] = vatPrice;
		dataRow['bedPrice'] = bedPrice;
		dataRow['serviceTaxPrice'] = serviceTaxPrice;
		dataRow['unitPrice'] = totalUnitPrice;
		var qty = dataRow['quantity'];
		
		dataRow['amount'] = qty*totalUnitPrice;
		grid.updateRow(rowIndex);
		grid.render();
		cancelForm();
	}
		
</script>
