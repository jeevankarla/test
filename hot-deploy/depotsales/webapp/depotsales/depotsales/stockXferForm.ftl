
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<style type="text/css">
	
	
	.ui-tooltip-titlebar {
	  position: relative;
	  min-height: 30px;
	  padding: 5px 35px 5px 10px;
	  overflow: hidden;
	  border-radius: 20px;
	  border-width: 0 0 5px;
	  font-weight: bold;
	}
</style>

<script type="application/javascript">

	var chromeBrowserFlag = 'Y'
	if ( $.browser.mozilla ) {
    	chromeBrowserFlag = 'N'
  	}
			 
	var toFacility;
	var fromFacility;
	var product;
	var batchNumbers;			 
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
					changePurposeType();
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function datepick()
	{		
		/*$( "#transferDate").datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			maxDate:0,
			numberOfMonths: 1});
		$('#ui-datepicker-div').css('clip', 'auto');*/
		
		$( "#transferDate" ).datetimepicker({
			dateFormat:'dd-mm-yy',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			minDate: '-5d',
		      maxDate: '0d',
			//onSelect: function(onlyDate){ // Just a work around to append current time without time picker
	        //    var nowTime = new Date(); 
	        //    onlyDate=onlyDate+" "+nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();
	        //    $('#transactionDate').val(onlyDate);
	        //},
	        changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function showInventoryTransferQTip(productId, quantityOnHandTotal, unitCost, inventoryItemId, facilityName, facilityId,productName) {
	//showXferForm();
			var message = "";
			message += "<form action='createInventoryXferRequest' method='post' onsubmit='return disableSubmitButton();' name='transferInventory' id='transferInventory'><table cellspacing=10 cellpadding=10>";
			
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Product:</td><td align='left' width='50%'> <input type='text'  readonly  id='productId' value='"+productId+"' name='productId'/></td><input class='h4' type='hidden' readonly id='productStoreId' name='productStoreId' value='STORE'/></tr>";
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>ProductName:</td><td> '"+productName+"'</td></tr>";
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Transfer Date:</td><td align='left' width='50%'><input type='text' id='transferDate' name='transferDate' onmouseover='datepick()'/></td><input type='hidden'  id='effectiveDate' name='effectiveDate'/><input type='hidden'  id='quantityOnHandTotal' name='quantityOnHandTotal' value='"+quantityOnHandTotal+"'/></tr>";
			
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Unit Cost:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='unitCost' name='unitCost' value='"+unitCost+"'/></td><input type='hidden'  id='inventoryItemId' name='inventoryItemId' value='"+inventoryItemId+"'/></tr>";
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Quantity:</td><td align='left' width='50%'><input class='h3' type='text' id='xferQty' name='xferQty' value='' /></td><input type='hidden'  id='salesChannel' name='salesChannel' value='DEPOT_CHANNEL'/></tr>";
			
			
			message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>From:</td><td align='left' width='50%'><input class='h3' type='text' readonly id='fromFacility' name='fromFacility' value='"+facilityName+"' /></td><input type='hidden'  id='fromFacilityId' name='fromFacilityId' value='"+facilityId+"'/></tr>";
			
			message += 
			"<tr class='h3'><td align='left' class='h3' width='40%'>Route:</td><td align='left' width='60%'><select name='toFacilityId' id='toFacilityId'>"+
						"<#list facilityList as facility><option value='${facility.facilityId?if_exists}' >${facility.facilityName?if_exists}</option></#list></select></td></tr>";
			message +=  "<tr class='h3'><td class='h3' align='center'><span align='right'><input type='submit' value='Send' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
			title = "<center>Transfer Indent<center><br />";
			message += "</table></form>";
			Alert(message, title);
	};
	
	function submitTransferForm(){
		$('#xferForm').submit();
	}
	function cancelForm(){	
		return false;
	}
	
	
	function showXferForm() {
		//var dataJson = {"facilityId": facilityId, "productId": productId};
		jQuery.ajax({
                url: 'getFacilityDetails',
                type: 'POST',
                data: '',
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in getting past dues");
					}else{
						fromFacility = result['fromFacility'];
						toFacility = result['toFacility'];
               		}
               	}							
		});
		
	}	
</script>
