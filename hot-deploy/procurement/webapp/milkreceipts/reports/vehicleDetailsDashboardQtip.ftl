<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}
</style>

 <input type="hidden" name="milkreceipEditFlag" id="milkreceipEditFlag" value="${milkreceipEditFlag}">

<script type="application/javascript">
   var milkReceiptFlag = $("#milkreceipEditFlag").val();
  var vehicleCodeJson = ${StringUtil.wrapString(vehItemsJSON)};
 var partyItemsJSON = ${StringUtil.wrapString(partyItemsJSON)!'[]'};
 var productItemsJSON = ${StringUtil.wrapString(productItemsJSON)!'[]'};
 
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
				populateData()
				
				//=====The below code used for width and height of editMilkReceiptEntry Qtip====
				
				if(milkReceiptFlag == "EDIT_MILK_RECEIPT_QTIP"){
				 $('.qtip').css({"max-width":"1200px"});
				  $('.qtip').css({"max-height":"650px"});
				}
				
				//========================================================================
				
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
 
		dialogue(message, title );		
		
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	var globalVehicleId;
	function vehicleDashBoardQtip(vehicleId ,sequenceNum){
       var vehicleStatusDetails=[];
       var vehicleProdDetails={};
	   globalVehicleId=vehicleId;
       //var dataString="vehicleId=" + vehicleId ;
       var dataJson = {"vehicleId":vehicleId,"sequenceNum":sequenceNum};
       $.ajax({
             type: "POST",
             url: "getVehicleDashboardDetails",
           	 data: dataJson ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
	            if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){ 
	      	  		 alert(result["_ERROR_MESSAGE_"]);
	        	 }else{
					 vehicleProdDetails =result["productObj"];
					 vehicleStatusDetails =result["vehicleStatusJSONList"];
			     }
          	 } ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
       });
        var receivedFat=vehicleProdDetails.receivedFat;
        var netWeight=vehicleProdDetails.netWeight;
    	var message = "";
        message += "<form action='' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 > " ; 
		message +="<tr>"+"<td>"+"<input  type='hidden' required='required' name='vehicleId' value='"+vehicleId+"' />"+"</td></tr>";
	    message +="<tr class='h3'><td align='left' class='h3' width='25%'>Product Name :</td><td align='left' width='25%'>"+vehicleProdDetails.productName+" [ "+vehicleProdDetails.productId+"] </td></tr>";
	    message +="<tr class='h3'><td align='left' class='h3' width='25%'>Union/Chilling Center :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.partyId+"</td><td align='left' width='30%'>DC No      :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.dcNo+"</td></tr>";
		
		if (netWeight) {
		message +="<tr class='h3'><td align='left' width='20%'>Dispatch Weight(Kgs) :</td><td align='left' width='10%'>"+vehicleProdDetails.dispatchWeight+"</td><td align='left' width='20%'>Gross Weight(Kgs) :</td><td align='left' width='10%'>"+vehicleProdDetails.grossWeight+"</td><td align='left' width='20%'>Tare Weight (Kgs) :</td><td align='left' width='10%' keep-together='always'>"+vehicleProdDetails.tareWeight+"</td><td align='left' width='20%' keep-together='always'></tr>";
		message +="<tr class='h3'><td align='left' class='h3' width='25%'>Silo Id :</td><td align='left' width='30%'>"+vehicleProdDetails.siloId+"</td><td align='left' width='25%' >Net Weight (Kgs) :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.netWeight+"</td></tr>";
		}else if(vehicleProdDetails.grossWeight){
			message +="<tr class='h3'><td align='left' width='20%'>Dispatch Weight(Kgs) :</td><td align='left' width='10%'>"+vehicleProdDetails.dispatchWeight+"</td><td align='left' width='20%'>Gross Weight(Kgs) :</td><td align='left' width='10%'>"+vehicleProdDetails.grossWeight+"</td></tr>";
		}
		if((vehicleProdDetails.siloId) && !(netWeight)){
			message +="<tr class='h3'><td align='left' class='h3' width='25%'>Silo Id :</td><td align='left' width='30%'>"+vehicleProdDetails.siloId+"</td></tr>";
		}
		if (receivedFat) {
		message +="<tr class='h3'><td align='left' class='h3' width='25%'>Fat % :</td><td align='left' width='30%'>"+vehicleProdDetails.receivedFat+"</td><td align='left' width='25%'>SNF % :</td><td align='left' width='25%' keep-together='always'>"+vehicleProdDetails.receivedSnf+"</td></tr>";
		}
		for(var i=0 ; i<vehicleStatusDetails.length ; i++){
			var innerList=vehicleStatusDetails[i];
			var statusId=  innerList['statusId']; 
			var statusEntryDate=  innerList['statusEntryDate']; 
		    message +="<tr class='h3'><td align='left' width='30%' keep-together='always'>"+statusId+" Time :</td><td align='left' width='30%' keep-together='always'>"+statusEntryDate+"</td></tr>";
		}
		message += "<tr class='h3'><td></td><td width='10%' align='center' class='h3'><span align='right'><button id='cancelButton' class='styled-button' value='${uiLabelMap.CommonClose}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonClose}</button></span></td></tr>";
		message += "</table></form>";	    
  		//alert("#####"+message);
	    var title ="Tanker "+vehicleId+" Details ";
		Alert(message, title);   
        
 	}
 	
 	
 	
	
 	
 	
 	
 	function populateData(){
        $("#vehicleProcessId").autocomplete({ source: vehicleCodeJson }).keydown(function(e){});
        $("#partyId1").autocomplete({ source: partyItemsJSON }).keydown(function(e){});
        $("#partyId2").autocomplete({ source: partyItemsJSON }).keydown(function(e){});
        $("#partyId3").autocomplete({ source: partyItemsJSON }).keydown(function(e){});
        $("#FCmilkType").autocomplete({ source: productItemsJSON }).keydown(function(e){});
        $("#MCmilkType").autocomplete({ source: productItemsJSON }).keydown(function(e){});
        $("#BCmilkType").autocomplete({ source: productItemsJSON }).keydown(function(e){});
        $("#FCreceivedProductId").autocomplete({ source: productItemsJSON }).keydown(function(e){});
        $("#MCreceivedProductId").autocomplete({ source: productItemsJSON }).keydown(function(e){});
        $("#BCreceivedProductId").autocomplete({ source: productItemsJSON }).keydown(function(e){});
         
         
         
         //============Dispatched=============
         
         //=======FC
          $('#FCfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#FCsendLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#FCsnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#FCsendAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#FCsendTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#FCsendSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#FCsendMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#FCquantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
         
         //=======MC 
          
          $('#MCfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#MCsendLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#MCsnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#MCsendAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#MCsendTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#MCsendSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#MCsendMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#MCquantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
      
           
           //=======BC 
           
           
          $('#BCfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#BCsendLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#BCsnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#BCsendAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#BCsendTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#BCsendSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#BCsendMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#BCquantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
           
           
          //============Received============= 
          
          $('#FCrecfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#FCreceivedLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#FCreceivedSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#FCreceivedAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#FCreceivedTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#FCreceivedSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#FCreceivedMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#FCreceivedQuantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
          
           $('#MCrecfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#MCreceivedLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#MCreceivedSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#MCreceivedAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#MCreceivedTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#MCreceivedSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#MCreceivedMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#MCreceivedQuantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
          
           $('#BCrecfat').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#BCreceivedLR').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#BCreceivedSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true}).trigger('focusout');
          $('#BCreceivedAcidity').autoNumeric({mNum: 3,mDec: 3 , autoTab : true}).trigger('focusout');
          $('#BCreceivedTemparature').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
          $('#BCreceivedSealNumber').autoNumeric({mNum: 7,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#BCreceivedMBRT').autoNumeric({mNum: 4,mDec: 0 , autoTab : true}).trigger('focusout');
          $('#BCreceivedQuantity').autoNumeric({mNum: 8,mDec: 0 , autoTab : true}).trigger('focusout');
         
        
	}
	
	
	function forValidation(){
	
	  
	  $('#FCsendSealNumber').val(($('#FCsendSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#FCsendMBRT').val(($('#FCsendMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#FCquantity').val(($('#FCquantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  $('#MCsendSealNumber').val(($('#MCsendSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#MCsendMBRT').val(($('#MCsendMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#MCquantity').val(($('#MCquantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  $('#BCsendSealNumber').val(($('#BCsendSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#BCsendMBRT').val(($('#BCsendMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#BCquantity').val(($('#BCquantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  
	  $('#FCreceivedSealNumber').val(($('#FCreceivedSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#FCreceivedMBRT').val(($('#FCreceivedMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#FCreceivedQuantity').val(($('#FCreceivedQuantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  $('#MCreceivedSealNumber').val(($('#MCreceivedSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#MCreceivedMBRT').val(($('#MCreceivedMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#MCreceivedQuantity').val(($('#MCreceivedQuantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  $('#BCreceivedSealNumber').val(($('#BCreceivedSealNumber').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#BCreceivedMBRT').val(($('#BCreceivedMBRT').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  $('#BCreceivedQuantity').val(($('#BCreceivedQuantity').val()).replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,''));
	  
	  
	
	
	
	
	
	}
			
 	
 	function editMilkReceiptEntry(milkTransferId,containerId,driverName,gatePass){
 	
 	 var milkTransferDetails=[];
 	 
 	 var milkReceiptDetailsFC=[];
 	 var milkReceiptDetailsMC=[];
 	 var milkReceiptDetailsBC=[];
 	 
 	  var dataJson = {"milkTransferId":milkTransferId};
       $.ajax({
             type: "POST",
             url: "getEditMilktransferDetails",
           	 data: dataJson ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
	            if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){ 
	      	  		 alert(result["_ERROR_MESSAGE_"]);
	        	 }else{
					 milkTransferDetails =result["milkTransferDetails"];
					 var milkReceiptDetails =result["milkReceiptDetailMap"];
					 
					  milkReceiptDetailsFC = milkReceiptDetails["FC"];
					  milkReceiptDetailsMC = milkReceiptDetails["MC"];
					  milkReceiptDetailsBC = milkReceiptDetails["BC"];
					 
					// alert("milkReceiptDetailsFC=========="+JSON.stringify(milkReceiptDetailsFC));
					 
					// alert("milkReceiptDetailsMC=========="+JSON.stringify(milkReceiptDetailsMC));
					// alert("milkReceiptDetailsBC=========="+JSON.stringify(milkReceiptDetailsBC));
					 
			     }
          	 } ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
       });
		
		
         
			var message = "";
			message += "<html><head></head><body><form id='editTanker'  action='EditMilkTankerEntryDetails' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=40 width=50>";
                 
                     
                   message +=  "<tr class='h3'><td align='left' class='h3' width='50%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td><pre><h1>     <font color='red'>New Value</font><h1><pre></td></tr>";
                   message +=  "<tr class='h3'><td align='left' class='h3' width='50%'>Vehicle No:</td><td align='left' width='60%'>"+containerId+" :</td><td><input class='h4' type='text' id='vehicleProcessId' name='vehicleProcessId' onselect=vamsi(); /></td></tr>";                  
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>Driver Name:</td><td align='left' width='60%'>"+driverName+" :</td><td align='left' width='80%'><input class='h4' type='text' id='driverName' name='driverName' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>Gate Pass:</td><td align='left' width='60%'>"+gatePass+" :</td><td align='left' width='80%'><input class='h4' type='text' id='gatePass' name='gatePass' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>FC Seal Check:</td><td align='left' width='60%'> "+milkTransferDetails["FCpartyId"]+":</td><td align='left' width='80%'><input class='h4' type='text' id='partyId1' name='partyId1' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>SC Seal Check:</td><td align='left' width='60%'> "+milkTransferDetails["MCpartyId"]+":</td><td align='left' width='80%'><input class='h4' type='text' id='partyId2' name='partyId2' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>BC Seal Check:</td><td align='left' width='60%'> "+milkTransferDetails["BCpartyId"]+":</td><td align='left' width='80%'><input class='h4' type='text' id='partyId3' name='partyId3' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>Dispatch Weight:</td><td align='left' width='60%'> "+milkTransferDetails["dispatchWeight"]+":</td><td align='left' width='80%'><input class='h4' type='text' id='dispatchWeight' name='dispatchWeight' /></td></tr>";
                   message += "<tr class='h3'><td align='left' class='h3' width='20%'>Gross Weight:</td><td align='left' width='60%'> "+milkTransferDetails["grossWeight"]+":</td><td align='left' width='80%'><input class='h4' type='text' id='grossWeight' name='grossWeight' /></td></tr>";  
				   
				   message += "<tr class='h3'><td align='left' class='h3' width='40%'><h1><u>Dispatch Quality<u><h1></td></tr>";
				  // message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>FC</font><h2></td></tr>";
				   
				   if(milkReceiptDetailsFC != "NoDetails"){
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>FC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.fat+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCfat' name='FCfat' /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsendLR' name='FCsendLR' /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.snf+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsnf' name='FCsnf' /></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsendAcidity' name='FCsendAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsendTemparature' name='FCsendTemparature' /></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsendSealNumber' name='FCsendSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCsendMBRT' name='FCsendMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendHeat+":</td><td align='left' width='80%'><select name='FCheat' id='FCheat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendApt+":</td><td align='left' width='80%'><select name='FCsendApt' id='FCsendApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendOrganoLepticTest+":</td><td align='left' width='80%'><select name='FCsendOrganoLepticTest' id='FCsendOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td>			  </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendCob+":</td><td align='left' width='80%'><select name='FCsendCob' id='FCsendCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendtest+":</td><td align='left' width='80%'><select name='FCsendtest' id='FCsendtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='60%'><pre><h3>Milk Type:<h3><pre></td><td align='left' width='20%'> "+milkReceiptDetailsFC.sendProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCmilkType' name='FCmilkType' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendSoda+":</td><td align='left' width='80%'><select name='FCsendSoda' id='FCsendSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.sendPt+":</td><td align='left' width='80%'><select name='FCsendPt' id='FCsendPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.quantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCquantity' name='FCquantity' onblur=forValidation() /></td></tr>";   
				    
				    }  
				    if(milkReceiptDetailsMC != "NoDetails"){  
				      
				     //message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>MC<h2></font></td></tr>"; 
				      
				      
				    message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>MC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>";  
				    message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.fat+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCfat' name='MCfat' /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsendLR' name='MCsendLR' /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.snf+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsnf' name='MCsnf' /></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsendAcidity' name='MCsendAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsendTemparature' name='MCsendTemparature' /></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsendSealNumber' name='MCsendSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCsendMBRT' name='MCsendMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendHeat+":</td><td align='left' width='80%'><select name='MCsendHeat' id='MCsendHeat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendApt+":</td><td align='left' width='80%'><select name='MCsendApt' id='MCsendApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendOrganoLepticTest+":</td><td align='left' width='80%'><select name='MCsendOrganoLepticTest' id='MCsendOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td></tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendCob+":</td><td align='left' width='80%'><select name='MCsendCob' id='MCsendCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendtest+":</td><td align='left' width='80%'><select name='MCsendtest' id='MCsendtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='20%'>Milk Type:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCmilkType' name='MCmilkType' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendSoda+":</td><td align='left' width='80%'><select name='MCsendSoda' id='MCsendSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.sendPt+":</td><td align='left' width='80%'><select name='MCsendPt' id='MCsendPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.quantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCquantity' name='MCquantity' onblur=forValidation() /></td></tr>";   
				   }
				   
				   if(milkReceiptDetailsBC != "NoDetails"){  
				   
				  // message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>BC</font><h2></td></tr>";
				   
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>BC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>";
				   
				    message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.fat+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCfat' name='BCfat' /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsendLR' name='BCsendLR' /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.snf+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsnf' name='BCsnf' /></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsendAcidity' name='BCsendAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsendTemparature' name='BCsendTemparature' /></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsendSealNumber' name='BCsendSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCsendMBRT' name='BCsendMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendHeat+":</td><td align='left' width='80%'><select name='BCheat' id='BCheat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendApt+":</td><td align='left' width='80%'><select name='BCsendApt' id='BCsendApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendOrganoLepticTest+":</td><td align='left' width='80%'><select name='BCsendOrganoLepticTest' id='BCsendOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td>			  </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendCob+":</td><td align='left' width='80%'><select name='BCsendCob' id='BCsendCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendtest+":</td><td align='left' width='80%'><select name='BCsendtest' id='BCsendtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='20%'>Milk Type:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCmilkType' name='BCmilkType' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendSoda+":</td><td align='left' width='80%'><select name='BCsendSoda' id='BCsendSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.sendPt+":</td><td align='left' width='80%'><select name='BCsendPt' id='BCsendPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.quantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCquantity' name='BCquantity' onblur=forValidation() /></td></tr>";   
				   
				   } 
				    
				   //==============================Received====================================
				   message += "<tr class='h3'><td align='left' class='h3' width='40%'><h1><u>Received Quality<u><h1></td></tr>";    
				      
				      
				      if(milkReceiptDetailsFC != "NoDetails"){
				      
				    //  message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>FC</font><h2></td></tr>";
				      
				    message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>FC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>";  
				    message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedFat+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCrecfat' name='FCrecfat' onblur=forPopulateSnfFC() /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedLR' name='FCreceivedLR' onblur=forPopulateSnfFC() /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedSnf+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedSnf' name='FCreceivedSnf' readonly /></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedAcidity' name='FCreceivedAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedTemparature' name='FCreceivedTemparature' /></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedSealNumber' name='FCreceivedSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedMBRT' name='FCreceivedMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.recdHeat+":</td><td align='left' width='80%'><select name='FCrecdHeat' id='FCrecdHeat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.recdApt+":</td><td align='left' width='80%'><select name='FCrecdApt' id='FCrecdApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.recdOrganoLepticTest+":</td><td align='left' width='80%'><select name='FCrecdOrganoLepticTest' id='FCrecdOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td>			  </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedCob+":</td><td align='left' width='80%'><select name='FCreceivedCob' id='FCreceivedCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.recdtest+":</td><td align='left' width='80%'><select name='FCrecdtest' id='FCrecdtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='20%'>Milk Type:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedProductId' name='FCreceivedProductId' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedSoda+":</td><td align='left' width='80%'><select name='FCreceivedSoda' id='FCreceivedSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedPt+":</td><td align='left' width='80%'><select name='FCreceivedPt' id='FCreceivedPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsFC.receivedQuantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='FCreceivedQuantity' name='FCreceivedQuantity' onblur=forValidation() /></td></tr>";   
			
			}
			
			if(milkReceiptDetailsMC != "NoDetails"){
				   
				 //   message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>MC</font><h2></td></tr>";  
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>MC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedFat+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCrecfat' name='MCrecfat' onblur=forPopulateSnfMC() /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedLR' name='MCreceivedLR' onblur=forPopulateSnfMC() /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedSnf+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedSnf' name='MCreceivedSnf' readonly /></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedAcidity' name='MCreceivedAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedTemparature' name='MCreceivedTemparature' /></td>    </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedSealNumber' name='MCreceivedSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedMBRT' name='MCreceivedMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.recdHeat+":</td><td align='left' width='80%'><select name='MCrecdHeat' id='MCrecdHeat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.recdApt+":</td><td align='left' width='80%'><select name='MCrecdApt' id='MCrecdApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.recdOrganoLepticTest+":</td><td align='left' width='80%'><select name='MCrecdOrganoLepticTest' id='MCrecdOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td>			  </tr>";
				   
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedCob+":</td><td align='left' width='80%'><select name='MCreceivedCob' id='MCreceivedCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.recdtest+":</td><td align='left' width='80%'><select name='MCrecdtest' id='MCrecdtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='20%'>Milk Type:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedProductId' name='MCreceivedProductId' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedSoda+":</td><td align='left' width='80%'><select name='MCreceivedSoda' id='MCreceivedSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedPt+":</td><td align='left' width='80%'><select name='MCreceivedPt' id='MCreceivedPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				   message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsMC.receivedQuantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='MCreceivedQuantity' name='MCreceivedQuantity' onblur=forValidation() /></td></tr>";   
				      
				      }
				      if(milkReceiptDetailsBC != "NoDetails"){
				     // message += "<tr class='h3'><td align='left' class='h3' width='20%'><h2><font color='red'>BC</font><h2></td></tr>"; 
				      
				       message += "<tr class='h3'>  <td align='left' class='h3' width='60%'><h2><font color='blue'>BC</font><h2></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>  <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'> <pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td> <td align='left' class='h3' width='20%'></td><td align='left' width='60%'><pre><h1><font color='red'>Existing Value<font></h1></pre></td><td align='left' width='80%'><pre><h1>     <font color='red'>New Value</font><h1><pre></td>    </tr>"; 
				        message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Fat%:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedFat+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCrecfat' name='BCrecfat' onblur=forPopulateSnfBC() /></td>  <td align='left' class='h3' width='20%'>CLR:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedLR+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedLR' name='BCreceivedLR' onblur=forPopulateSnfBC() /></td> <td align='left' class='h3' width='20%'>Snf:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedSnf+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedSnf' name='BCreceivedSnf' readonly/></td> <td align='left' class='h3' width='20%'>Acidity%:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedAcidity+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedAcidity' name='BCreceivedAcidity' /></td> <td align='left' class='h3' width='20%'>Temp:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedTemparature+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedTemparature' name='BCreceivedTemparature' /></td>    </tr>";
				   
				        message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Seal:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedSealNumber+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedSealNumber' name='BCreceivedSealNumber' onblur=forValidation() /></td>  <td align='left' class='h3' width='20%'>MBRT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedMBRT+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedMBRT' name='BCreceivedMBRT' onblur=forValidation() /></td> <td align='left' class='h3' width='20%'>H.S:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.recdHeat+":</td><td align='left' width='80%'><select name='BCrecdHeat' id='BCrecdHeat'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='0.3MLPositive' >0.3ML Positive</option>"+"<option value='0.3MLNegative' >0.3ML Negative</option>"+"<option value='0.4MLPositive' >0.4ML Positive</option>"+"<option value='0.4MLNegative' >0.4ML Negative</option>"+"<option value='0.5MLPositive' >0.5ML Positive</option>"+"<option value='0.5ML Negative' >0.5ML Negative</option>"+"<option value='0.6MLPositive' >0.6ML Positive</option>"+"<option value='0.6MLNegative' >0.6ML Negative</option>"+"</select></td> <td align='left' class='h3' width='20%'>APT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.recdApt+":</td><td align='left' width='80%'><select name='BCrecdApt' id='BCrecdApt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='55' >55%</option>"+"<option value='60' >60%</option>"+"<option value='62' >62%</option>"+"<option value='65' >65%</option>"+"<option value='68' >68%</option>"+"<option value='70' >70%</option>"+"<option value='75' >75%</option>"+"</select></td> <td align='left' class='h3' width='20%'>OT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.recdOrganoLepticTest+":</td><td align='left' width='80%'><select name='BCrecdOrganoLepticTest' id='BCrecdOrganoLepticTest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='NORMAL' >NORMAL</option>"+"<option value='ABNORMAL' >ABNORMAL</option>"+"<option value='SLIGHTLY_STALE' >SLIGHTLY STALE</option>"+"<option value='STALE' >STALE</option>"+"<option value='SOUR' >SOUR</option>"+"</select></td>			  </tr>";
				   
				        message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>COB:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedCob+":</td><td align='left' width='80%'><select name='BCreceivedCob' id='BCreceivedCob'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>OTHER:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.recdtest+":</td><td align='left' width='80%'><select name='BCrecdtest' id='BCrecdtest'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='UREA' >UREA</option>"+"<option value='SALT' >SALT</option>"+"<option value='SUGAR' >SUGAR</option>"+"<option value='H2O2' >H2O2</option>"+"<option value='STARCH' >STARCH</option>"+"</select></td><td align='left' class='h3' width='20%'>Milk Type:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedProductId+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedProductId' name='BCreceivedProductId' /></td> <td align='left' class='h3' width='20%'>SODA/NUTRALKER:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedSoda+":</td><td align='left' width='80%'><select name='BCreceivedSoda' id='BCreceivedSoda'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td><td align='left' class='h3' width='20%'>PT:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedPt+":</td><td align='left' width='80%'><select name='BCreceivedPt' id='BCreceivedPt'  class='h4'>"+"<option value='' > SELECT</option>"+"<option value='Y' >Positive</option>"+"<option value='N' >Negative</option>"+"</select></td> </tr>";
				      
				        message += "<tr class='h3'>  <td align='left' class='h3' width='60%'>Quantity:</td><td align='left' width='60%'> "+milkReceiptDetailsBC.receivedQuantity+":</td><td align='left' width='80%'><input class='h4' type='text' id='BCreceivedQuantity' name='BCreceivedQuantity' onblur=forValidation()/></td></tr>";   
				
				      
				      }
				      
				      
				         message += "<tr class='h3'><td align='left' class='h3' width='20%'></td><td align='left' width='20%'></td><td align='left' width='80%'><input class='h4' type='hidden' id='milkTransferId' name='milkTransferId' value='"+milkTransferId+"' /></td></tr>";  
                 
                   message +="<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
						message +=	"</table></form></body></html>";
			var title = "Edit Price for Product: ";
			Alert(message, title);
 	
 	}
 	
 	
 	
 	//========================================================
 	
 	//=======================calculate Fat Snf Values=======================
 	
 	function forPopulateSnfFC(){
 	
 	    var fat = $('[name=FCrecfat]').val();
		var clr = $('[name=FCreceivedLR]').val();
		populateSnf(fat,clr,'FCreceivedSnf');
		
 	}
 	
 	function forPopulateSnfMC(){
 	
 	    var fat = $('[name=MCrecfat]').val();
		var clr = $('[name=MCreceivedLR]').val();
		populateSnf(fat,clr,'MCreceivedSnf');
		
 	}
 	
 	function forPopulateSnfBC(){
 	
 	    var fat = $('[name=BCrecfat]').val();
		var clr = $('[name=BCreceivedLR]').val();
		populateSnf(fat,clr,'BCreceivedSnf');
		
 	}
 	
 	
 	
 	function populateSnf(fat,lr,fieldName){
		if(typeof(lr) != 'undefined' && lr!=='' && lr != null ){
		}else{
			return;
		}
		var snfQty = 0;
		var dataString = {"fatQty": fat,
						   "lactoReading":lr
						 };
		var action= 'getSnfFromLactoReading';
		$.ajax({
	         type: "POST",
	         url: action,
	         data: dataString,
	         dataType: 'json',
	         success: function(result) { 
	       		snfQty = result['snfQty'];
	       		$('[name='+fieldName+']').val(snfQty);
	         },
	         error: function(XMLHttpRequest, textStatus, errorThrown)
	        {
	        	$('[name='+fieldName+']').val('');
	            alert('Net work failure . Please check network Conection');
	
	        }
    	});
	}
	
	
</script>
Status 