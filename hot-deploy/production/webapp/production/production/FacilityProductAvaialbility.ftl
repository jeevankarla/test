<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">		
	
			 
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
					$('button', api.elements.content).click(api.hide);
					// It will remove special characters except - symbol
					$('.capsOnly').keyup(function()
         				{ 
         					$(this).val($(this).val().toUpperCase().replace(/[&\/\\#,+()$~%'":*?<>^{}`~,\]\[ ]/g, ''));
         			});
         			$('.addAll').keyup(function(event){
         				var keyCode = event.keyCode ;
         				
         				
         				if(keyCode == 110 || keyCode == 190){
    						$(this).val( $(this).val().replace('.',''));
    					}
    					$(this).val( $(this).val().replace(/[^0-9\.]/g,''));
         				
         			});
         			
         			
         			datepick();
         			
         			$('#tankerNo').keyup(function(){
			  			$('[name=tankerName]').val(($('[name=tankerName]').val()).toUpperCase());
			  			populateVehicleName();
			  			populateVehicleSpan();
			  		});
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
	
	
	function populateVehicleSpan(){
		var vehicleCodeJson = ${StringUtil.wrapString(vehicleCodeJson?if_exists)}
		var tempVehJson = vehicleCodeJson[$('[name=tankerName]').val()];
		if(tempVehJson){
			$('span#tankerToolTip').addClass("tooltip");
			$('span#tankerToolTip').removeClass("tooltipWarning");
			var vehicleName = tempVehJson["vehicleName"];
			var vehicleId = tempVehJson["vehicleId"];
			if(!vehicleName){
				vehicleName = vehicleId;
			}
			$('span#tankerToolTip').html(vehicleName);
			$('[name=tankerNo]').val(vehicleId);
		}else{
			$('[name=tankerNo]').val('');
			$('span#tankerToolTip').removeClass("tooltip");
			$('span#tankerToolTip').addClass("tooltipWarning");
			$('span#tankerToolTip').html('Code not found');
		}
	}	
	function populateVehicleName(){
			var availableTags = ${StringUtil.wrapString(vehItemsJSON)!'[]'};
				$("#tankerNo").autocomplete({					
						source:  availableTags,
						select: function(event, ui) {
					        var selectedValue = ui.item.value;
					        $('[name=tankerName]').val(selectedValue);
					        populateVehicleSpan();
					    }
				});
	}
	
	function datepick()
	{		
		var currentTime = new Date();
	 	// First Date Of the month 
	 	var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
	 	// Last Date Of the Month 
	 	var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
		$( "#testDate" ).datetimepicker({
			dateFormat:'yy-mm-dd',
			showSecond: true,
			timeFormat: 'hh:mm:ss',
			minDate: startDateFrom,
			maxDate:0,
	        changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
 //handle cancel event
	function cancelForm(){		 
		return false;
	}
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
		dialogue(message, title );
	}
	
	function validateIssuingQty(){
		
		var reqQty = $('[name=toBeIssued]').val();
		var reqQtyNumVal = parseFloat(reqQty.replace(",",""));
		var issuedQty = 0;
		var add_ele = document.getElementsByClassName('addAll');
		for (var i = 0; i < add_ele.length; ++i) {
			    var item = add_ele[i];
			    var itemVal = item.value;   
			    var itemNumVal = parseFloat(itemVal); 
			    issuedQty = issuedQty+itemNumVal ;
		}
		if(reqQtyNumVal<issuedQty){	
			alert('Please check the issuing Qty Details. Reason Issuing Qty exceeds requested Qty');
			return false;
		}else{
			if(issuedQty<=0){
				alert('Please check the issuing Qty Details. Reason Issuing Qty is less than or equal to zero ');
				return false;
			}else{
				return true;
			}
		}		
	}
//disable the generate button once the form submited
	function disableGenerateButton(){
		   var resultVal = true;	
		   resultVal =  validateIssuingQty();				
		   if(!resultVal){
		   		return false;
		   }
		   
		   $("input[type=submit]").attr("disabled", "disabled");
		   $("#cancelButton").attr("disabled", "disabled");
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	function getProductFacilityAvailable(productId,ownerFacilityId){
		var dataJson = {"productId":productId,"ownerFacilityId":ownerFacilityId};
		var productTestComponentDetails = {};
		$.ajax({
			 type: "POST",
             url: 'getProductFacilityAvailable',
             data: dataJson,
             dataType: 'json',
             async:false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               	alert('Product Ffacilities not found, Please contact Admin');
               }else{
               		productTestComponentDetails = result['productFacilityComponentDetails'];
               }
             } 
		});
		return productTestComponentDetails;
	}	
	function showProductFacilityAvailableForm(productId,ownerFacilityId,reqQty,toBeIssued,custRequestId,custRequestItemSeqId) {	
		var message = "";
		var noOfFacilities=0;
		var totIssuedQty =0;
		var productFacilityComponentDetailsMap = {};
		var toBeIssuedVal = toBeIssued;
		if(typeof(toBeIssuedVal) == 'undefined' || toBeIssuedVal==''){
			toBeIssuedVal = reqQty;
		}
		productFacilityComponentDetailsMap = getProductFacilityAvailable(productId,ownerFacilityId);
		var productFacilityDetailsList = productFacilityComponentDetailsMap['productFacilityDetailsList'];
		message += "<form action='IssueRequestThroughTransfer'  method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		message += "<tr class='h3'><td>Indent Qty </td><td>"+reqQty+"</td><input type='hidden' name='reqQty' value='"+reqQty+"'/></tr>";
		message += "<tr class='h3'><td>Remaining Qty </td><td>"+toBeIssuedVal+"</td><input type='hidden' name='toBeIssued' value='"+toBeIssuedVal+"'/></tr>";
		message += "<tr class='h3'><td>product </td><td>"+productId+"</td><input type='hidden' name='productId' value='"+productId+"'/></tr>";
		message += "<tr class='h2'><td colspan='3'>--------------------------------------------------------------------------------------------</td></tr>";
		message += "<tr class='h2'><td>Silo/Tank/Floor</td><td>Available Qty</td> <td>Issue Qty</td> </tr>";
		message += "<tr class='h2'><td colspan='3'>--------------------------------------------------------------------------------------------</td></tr>";
			for(i=0; i<productFacilityDetailsList.length;i++){
			 	var productFacilityDetails =  productFacilityDetailsList[i];
				var facilityId = productFacilityDetails['facilityId'];
				var availableQty = productFacilityDetails['availableQty'] ;
				var issueFieldName = "issueQty_o_"+i;
				var facilityName = "facilityId_o_"+i;
				var productName = "productId_o_"+i;
			 	var custRequestName = "custRequestId_o_"+i;
			 	var custRequestSeqName = "custRequestItemSeqId_o_"+i;
			 	var toBeIssuedQtyName = "toBeIssuedQty_o_"+i;			 	
			 	message += "<tr><td>"+facilityId+"</td><td>"+availableQty+"</td>"+
			 				"<td align='left'><input type='text' autocomplete='off' name='"+toBeIssuedQtyName+"' maxlength='10' id='"+toBeIssuedQtyName+"' class='addAll' > </td>"+
			 				"<td><input type='hidden' name='"+facilityName+"' id='"+facilityName+"' value='"+facilityId+"' > </td></tr>"
			 				
			 	message += "<tr class='h3'><td><input type='hidden' name='"+custRequestName+"'  value='"+custRequestId+"'></td></tr>";
			 	message += "<tr class='h3'><td><input type='hidden' name='"+custRequestSeqName+"'  value='"+custRequestItemSeqId+"'></td></tr>";
			 				
			 	}
		message += "<tr class='h2'><td colspan='3'>--------------------------------------------------------------------------------------------</td></tr>";
		message += "<tr class='h3'><td>Vehicle Number</td><td><input type='text' name='tankerName' id='tankerNo' autocomplete='off'   required/></td><td><span class='tooltip h2' id ='tankerToolTip'>none</span></td>"+
				"<td><input type='hidden' name='tankerNo' id='tankerNumber' value=''/> </td></tr>";	
				 	
		message += "<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Submit' id='submitEntry' class='smallSubmit'/></span></td></td><td width='10%' align='center' class='h3'><span align='right'><button id='cancelButton' class='styled-button' value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";	
		var title = "Issuing details" ;
		Alert(message, title);
	};	
</script>

