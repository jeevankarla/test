
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
			minDate: '-1d',
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
	
	function showXferFormEdit() {	
		
		var message = "";
		var title = "";
		if(toFacility){
			
			var prodId = product["productId"];
			var prodName = product["productName"];
			message += "<form name='xferForm' id='xferForm' method='post' action='createStockXferRequest'><input type='hidden' name='transferGroupTypeId' value='INTERNAL_XFER'>";
			message += "<table cellspacing=10 cellpadding=10 style='background-repeat:no-repeat; width:450px;margin:0;'>" ;
			message += "<tr><td align='left'><h2>Product</h2></td><td>&nbsp;</td><td align='left'><input type='hidden' name='productId' id='productId' value='"+prodId+"'><input type='hidden' name='statusId' id='statusId' value='IXF_REQUESTED'><h3>"+prodName+"</h3></td></tr>";
			message += "<tr><td align='left'><h2>Date</h2></td><td>&nbsp;</td><td align='left'><h3><input style='width:150px;' type='text' name='transferDate' id='transferDate' onmouseover='datepick()' readOnly></h3></td></tr>";
			message += "<tr><td align='left'><h2>From Plant/Silo</h2></td><td>&nbsp;</td><td align='left'><h3><select style='width:150px;' id='fromFacilityId' name='fromFacilityId'>";
			for(var i=0;i<fromFacility.length;i++){
				var facId = fromFacility[i]['facilityId'];
				var facName = fromFacility[i]['facilityName'];
				message += "<option value='"+facId+"'>"+facName+"</option>";
			}
			message += "</select></h3></td></tr>";
			message += "<tr><td align='left'><h2>To Plant/Silo</h2></td><td>&nbsp;</td><td align='left'><h3><select style='width:150px;' id='toFacilityId' name='toFacilityId' onchange='javascript: changePurposeType();'>";
			for(var i=0;i<toFacility.length;i++){
				var facId = toFacility[i]['facilityId'];
				var facName = toFacility[i]['facilityName'];
				message += "<option value='"+facId+"'>"+facName+"</option>";
			}
			message += "</select></h3></td></tr>";
			message += "<tr><td align='left'><h2>Batch Number</h2></td><td>&nbsp;</td><td align='left'><h3><select style='width:150px;' id='inventoryItemId' name='inventoryItemId' onchange='javascript: checkBatchValidate();'>";
			message += "<option value=''> --   FIFO   --</option>";
			for(var i=0;i<batchNumbers.length;i++){
				var invId = batchNumbers[i]['inventoryItemId'];
				var batchNo = batchNumbers[i]['batchNumber'];
				var qoh = batchNumbers[i]['qoh'];
				message += "<option value='"+invId+"'>"+batchNo+" - [ "+qoh+" ]</option>";
			}
			message += "</select></h3></td></tr>";
			message += "<tr><td align='left'><h2>Xfer Qty</h2><sub>(in Kg/Ltr)</sub></td><td>&nbsp;</td><td align='left'><h3><input style='width:150px;' type='text' name='xferQty' id='xferQty'></h3></td></tr>";
			message += "<tr><td align='left'><h2>Purpose</h2></td><td>&nbsp;</td><td align='left'><h3><select style='width:150px;' id='comments' name='comments'></select></h3></tr>";
			if(chromeBrowserFlag && chromeBrowserFlag == 'Y'){
				message += "<tr class='h3'><td>&nbsp;</td><td align='right'><button type='submit' onclick='javascript: submitTransferForm();' class='submit'>Submit</button></td><td class='h3' align='left'><button onclick='return cancelForm();'>Close</button></td></tr>";
			}
			else{
				message += "<tr class='h3'><td>&nbsp;</td><td align='right'><input type='submit' onclick='javascript: submitTransferForm();' class='submit' value='Submit' /></td><td class='h3' align='left'><button onclick='return cancelForm();'>Close</button></td></tr>";
			}
			
			title = "<center><h2>Stock Transfer</h2></center>";
			message += "</table></form>";
			Alert(message, title);
		}
		
	};
	
	function submitTransferForm(){
		$('#xferForm').submit();
	}
	function cancelForm(){	
		return false;
	}
	function checkBatchValidate(){
		var invItem = $('#inventoryItemId').val();
		if(invItem){
			for(var i=0;i<batchNumbers.length;i++){
				var invId = batchNumbers[i]['inventoryItemId'];
				if(invId == invItem){
					var qoh = batchNumbers[i]['qoh'];
					$('#xferQty').val(qoh);
					$("#xferQty").prop("readonly", true);
					$("#xferQty").css("background-color", '#E3E3E3');
				}
			}
		}
		else{
			$('#inventoryItemId').val('');
			$('#xferQty').val('');
			$("#xferQty").prop("readonly", false);
			$("#xferQty").css("background-color", '#FFFFFF');
		}
	}
	function changePurposeType(){
		var toFacilityId = $('[name=toFacilityId]').val();
		var productIdStr = product["productId"];
		if(typeof(toFacilityId) != 'undefined' && toFacilityId!=null && toFacilityId!=''){
			var dataJson = {"facilityId": toFacilityId};
			jQuery.ajax({
                url: 'getFacilityPurposeProducts',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               	success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in getting purpose");
					}else{
						var optionList = '';
						var purposeProducts = result['purposeProducts'];
						if(typeof(purposeProducts) != 'undefined' && purposeProducts!=null && purposeProducts!=''){
							for(i=0;i<purposeProducts.length ; i++){
								var puposeProduct = purposeProducts[i];
								optionList += "<option value = " + puposeProduct['productId'] + " >" +puposeProduct['productName']+"</option>";
							}
						}
						jQuery("[name=comments]").html(optionList);		
               		}
               	}							
			});
		}
	}
	function showXferForm(facilityId, productId) {
		var dataJson = {"facilityId": facilityId, "productId": productId};
		jQuery.ajax({
                url: 'getProductFacilityDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in getting past dues");
					}else{
						fromFacility = result['fromFacility'];
						toFacility = result['toFacility'];
						product = result['product'];
						batchNumbers = result['batchNumberList'];
					  	showXferFormEdit();
               		}
               	}							
		});
		
	}	
</script>
