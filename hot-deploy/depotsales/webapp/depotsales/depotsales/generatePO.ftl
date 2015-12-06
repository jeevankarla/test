
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
		var CountryJsonMap = ${StringUtil.wrapString(countryListJSON)!'{}'};
		var StateJsonMap = ${StringUtil.wrapString(stateListJSON)!'{}'};
		var AllSupplierAddrMap = ${StringUtil.wrapString(supplierAddrJSON)!'{}'};
		var CountryOptionList;
	   var CountryOptions;
	   var StateOptionList;
	   var StateOptions;
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
				$("#PostalAddress").hide();
				populateData();
				//getAllIndentRejects();
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
		function disableButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}

	function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
function submitForm(){		 
		
		 jQuery('#purchaseOrderEntry').submit();
	}

	function datepick1()	{

		$( "#orderDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}



function manualAddressEntry(){
$("#ManualAddress").hide();
$("#PostalAddress").show();
}

function changeToUpperCase(){

		var address1 = $("#address1").val();
		if(address1 != null || address1 != undefined){
			address1 = address1.toUpperCase();
			$("#address1").val(address1);
		}
		var address2 = $("#address2").val();
		if(address2 != null || address2 != undefined){
			address2 = address2.toUpperCase();
			$("#address2").val(address2);
		}
		var city = $("#city").val();
		if(city != null || city != undefined){
			city = city.toUpperCase();
			$("#city").val(city);
		}


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






function purchaseOrder(orderId, salesChannel,supplierPartyId,supplierPartyName,productStoreId,partyId,orderDate){
	var shipDetails=AllSupplierAddrMap[partyId];
	
	  var action;
     var message = "";

                message += "<html><head></head><body><form id='purchaseOrderEntry' method='post' action='createPOByOrder' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10 width=700>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>OrderId:</td><td align='left'  width='90%'><font color=light-blue size=45%>"+orderId+"</font><input class='h4' type='hidden' value="+orderId+" id='orderId' name='orderId' /></td></tr>";
      			message += "<tr class='h2'><td align='left' class='h5' width='60%'>Branch Name:</td><td align='left'  width='90%'><font color=light-blue size=45%>"+productStoreId+"</font><input class='h4' type='hidden' value="+productStoreId+" id='productStoreId' name='productStoreId' /></td></tr>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>supplierPartyId:</td><td align='left' class='h5' width='60%'><font color=light-blue>"+supplierPartyName+"["+supplierPartyId+"]</font><input class='h4' type='hidden'  id='supplierId' name='supplierId' value="+supplierPartyId+"   readOnly /><input class='h4' type='hidden'  id='partyId' name='partyId' value="+partyId+"  /></td></tr>";
				message +=	"<tr class='h3'><td align='left' class='h3' width='40%'>Order Date:</td><td align='left' width='60%'><input class='h4' type='text' color=light-blue class='required' readonly  value="+orderDate+" id='orderDate' name='orderDate' onmouseover='datepick1()' required /></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
				message +=	"<tr class='h3'><td align='left' width='60%'></td><td align='left' class='h3' width='40%'><font size=55%><b><u>Shipping Deatils</u></b></font></td></tr>";
				
				if(shipDetails != undefined && shipDetails != ""){
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>ADDRESS: </td><td align='left' width='60%'><font color=light-blue>"+shipDetails.address1+",</font></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'><font color=light-blue>"+shipDetails.address2+",</font></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>CITY: </td><td align='left' width='60%'><font color=light-blue>"+shipDetails.city+"<font></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>POSTAL CODE: </td><td align='left' width='60%'><font color=light-blue>"+shipDetails.postalCode+"</font></td></tr>";
     		   	
     		   	}else{
     		   		message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'></td></tr>";
     		   	}
     		    message +=	"</table>";
     		  
     		   	message += "<table  id='PostalAddress' cellspacing=10 cellpadding=10 width=400>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>ADDRESS1:<font color=red>*</font> </td><td align='left' width='60%'><input type='text' class='h4'  id='address1'  name='address1' onblur='changeToUpperCase();' /></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>ADDRESS2: </td><td align='left' width='60%'><input type='text' class='h4'  id='address2'  name='address2' onblur='changeToUpperCase();' /></td></tr>";
     		    message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>Country: </td><td align='left' width='60%'><select class='h4'  id='country'  name='country' onchange='setServiceName(this)'/></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>State: </td><td align='left' width='60%'><select class='h4'  id='stateProvinceGeoId'  name='stateProvinceGeoId'/></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>City: </td><td align='left' width='60%'><input type='text' class='h4'  id='city'  name='city' onblur='changeToUpperCase();' /></td></tr>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'>PostalCode: </td><td align='left' width='60%'><input type='text' class='h4'  id='postalCode'  name='postalCode' onblur='changeToUpperCase();' /></td></tr>";
     		   	
     		   	message +=	"</table>";
     		   	message += "<table  id='submitfrm' cellspacing=10 cellpadding=10 width=400>";
     		   	message +=	"<tr class='h3'><td align='center' class='h3' width='40%'></td><td align='left' width='60%'><input class='h4' type='button' class='h4'  value='Manual Address Entry' id='ManualAddress'  name='ManualAddress' onclick='manualAddressEntry()'/></td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='return submitForm();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message +=	"</table></form></body></html>";
	var title = "Generate Purchase Order";
    Alert(message, title);
     //action= makeMassReject;
     //jQuery('#ListIndentSubmit').attr("action", action);
     //jQuery('#ListIndentSubmit').submit();
};


		
	
// view order Details 


function cancelviewOrderForm(){
		cancelShowSpinner();		 
		return false;
	}
	
	
	
	function showOrderDetails() {
		
		var message = "";
		var title = "";
		if(orderData != undefined){
			var orderAmt = 0;
			message += "<table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product Id</td><td align='center' class='h3'> Description</td><td align='center' class='h3'> Batch No.</td><td align='center' class='h3'> Qty</td><td align='center' class='h3'> Tax %</td><td align='center' class='h3'> Amount</td>";
			for (i = 0; i < orderData.length; ++i) {
				message += "<tr><td align='center' class='h4'>" + orderData[i].productId + "</td><td align='left' class='h4'>" + orderData[i].itemDescription + "</td><td align='center' class='h4'>"+ orderData[i].batchNo +"</td><td align='center' class='h4'>"+ orderData[i].quantity +"</td>";
				message += "<td align='center' class='h4'>" + orderData[i].taxPercent + "</td><td align='center' class='h4'>" + orderData[i].itemTotal + "</td></tr>";
				orderAmt = orderAmt+orderData[i].itemTotal;
			}
			message += "<tr class='h3'><td></td><td></td><td class='h3' align='left'><span align='center'><button onclick='return cancelviewOrderForm();' class='submit'>Close</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br /><br /> Total Order Value = "+ orderAmt +" ";
			message += "</table>";
			Alert(message, title);
		}
		
	};
	
	function showOrderBatchDetails(flag) {
		
		requestFlag = flag;
		var message = "";
		var title = "";
		var action = "";
		if(requestFlag == "powder"){
			action = "editPowderOrderBatchNumber";
		}
		if(requestFlag == "amul"){
			action = "editAmulOrderBatchNumber";
		}
		if(requestFlag == "nandini"){
			action = "editNandiniOrderBatchNumber";
		}
		if(requestFlag == "fgs"){
			action = "editFGSOrderBatchNumber";
		}
		if(orderData != undefined){
		
			message += "<form action='"+action+"' method='post' onsubmit='return disableSubmitButton();'>";
			message += "<input type=hidden name=orderId value='"+orderId+"'><table cellspacing=10 cellpadding=10 border=2 width='100%'>" ;
			message += "<thead><td align='center' class='h3'> Product</td><td align='center' class='h3'> Quantity</td><td align='center' class='h3'> Batch No.</td></thead>";
			for (i = 0; i < orderData.length; ++i) {
				message += "<tr><td align='left' class='h4'><input type=hidden name='orderId_o_"+i+"' value='"+orderData[i].orderId+"'><input type=hidden name='orderItemSeqId_o_"+i+"' value='"+orderData[i].orderItemSeqId+"'>"+ orderData[i].itemDescription +"</td><td align='left' class='h4'>" + orderData[i].quantity + "</td><td align='center' class='h4'><input type=text name='batchNo_o_"+i+"' value='"+orderData[i].batchNo +"'></td></tr>";
			}
			message += "<tr class='h3'><td></td><td class='h3' align='left'><span align='center'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='EditBatchNo' class='smallSubmit'/></span></td><td class='h3' align='left'><span align='center'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelviewOrderForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td><td></td></tr>";
			title = "<center>Order : " + orderId + "<center><br />";
			message += "</table></form>";
			Alert(message, title);
		}
		
	};
	
	function showSpinner() {
		
		var message = "";
		var title = "";
		message += "<div align='center' name ='displayMsg' id='pastDues_spinner'/><button onclick='return cancelForm();' class='submit'/>";
		Alert(message, title);
		
	};
	function cancelShowSpinner(){
		$('button').click();
		return false;
	}
	
	function fetchOrderDetails(order, requestFlag) {
		orderId = order;
		screenFlag = requestFlag;
		var dataJson = {"orderId": orderId, "screenFlag":screenFlag};
		showSpinner();
		jQuery.ajax({
                url: 'getOrderDetails',
                type: 'POST',
                data: dataJson,
                dataType: 'json',
               success: function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    alert("Error in order Items");
					}else{
						orderData = result["orderItemListJSON"];
					    requestFlag = result["requestFlag"];
					    cancelShowSpinner();
					  		
						if(screenFlag == "batchEdit"){
							showOrderBatchDetails(requestFlag);
						}
						else{
					  		showOrderDetails();
						}
               		}
               	}							
		});
	}	
	
	
	
	
	
	
</script>
