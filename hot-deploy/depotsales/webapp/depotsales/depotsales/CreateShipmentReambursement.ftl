
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
var jsonObj = ${StringUtil.wrapString(shipmentReimbursementList)!'{}'};
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
				populateDate()
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
	
	function datepick()
	{		
		$( "#receiptDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	function datepickForReceiptDate()
	{		
		$( "#receiptDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	var ordersListGlobal={};
	      
	var globalShipmentId ="";
	
	function showLinkGrnWithPOForm(shipmentId,partyId) {	
	  globalShipmentId=shipmentId;
	 var dataString  = 'shipmentId='+ shipmentId;
         if(shipmentId!=="undefined"){
           var dataJson = "shipmentId=" + shipmentId + "&partyId=" + partyId;
           dataString=dataJson;
       	 }
            //get Orders for GRN link
     $.ajax({
             type: "POST",
             url: "getShipmentLinkOrdersAjax",
             data: dataString,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   ordersListGlobal =   result["ordersListForGRNLink"];
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
            
      var list= ordersListGlobal;
	
		var message = "";
		message += "<div style='width:100%;height:380px;overflow-x:auto;overflow-y:auto;' ><table cellspacing=10 cellpadding=10  width='100%' > " ; 
		message += "<tr class='h3'><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		<#--		
			message +="<tr class='h3'><td >GRN Shipment Id :  <input class='h3' type='text' readonly id='grnShipmentId' name='grnShipmentId'  size='13'/></td></tr>";
          -->
           message += "<tr class='h3'><td align='left' class='h3' width='100%' ><table cellspacing=10 cellpadding=10 border=2 width='100%' >" ;
           message +="<thead class='h3'><th align='center' class='h3' width='50%' >Supplier Name</th><th  align='center' class='h3' width='20%' >OrderId</th><th align='center' width='20%' class='h3' >Order Date</th><th align='right' class='h3' >Link</th></thead>";
          	message += "</td></<thead></table>";
          	if (list) {	
	        	for(var i=0 ; i<list.length ; i++){
					var innerMap=list[i];	       
					 message += "<tr ><td align='left'  width='100%'  ><form action='processUpdateGRNWithPO' method='post' onsubmit='return disableGenerateButton();'>";
			          message += "<input type=hidden name=shipmentId  value='"+shipmentId+"'><input type=hidden name=orderId  value='"+innerMap['orderId']+"'> <table cellspacing=10 cellpadding=10 border=2 width='100%' >" ;
			          message +="<tr class='h4'><td align='left' width='50%'  class='h5' >"+innerMap['supplierName']+"</td><td width='20%' align='left' class='h3' >"+innerMap['orderId']+"</td><td width='20%' align='left' class='h4' >"+innerMap['entryDate']+"</td><td width='20%' align='right' class='h3' ><input type='submit' value='Link PO' id='generateTruckSheet' class='smallSubmit'/></td></tr>";
			          
			          
			          
			          message += "<input  type='hidden' name='payReimbursementList' id='payReimbursementList' /></form></td></tr></table>";
	      		}//end of main list for loop
	      	}
	      		
		message += "</table></div>";				
		var title = "<center>GRN Shipment Id : " + shipmentId + "<center><br /><br /> And Possibilities PO's Are  Below ";
		Alert(message, title);
	};
	
	function populateDate(){
	//alert("==shipmentId ==InPopulateData=="+globalShipmentId);
		jQuery("#grnShipmentId").val(globalShipmentId);
	};
	
	function payReimbursement(shipmentId,invoiceAmount,eligibilityAmount){
	
	var shipmentarr=jsonObj[shipmentId];
	var receiptString="";
	if(shipmentarr){
	$.each(shipmentarr, function( index, value ) {
      receiptString+= buildReambursementList(value.claimId,value.receiptNo,value.receiptAmount,value.receiptDate,value.description);
	});
	}
		var message = "";
		message += "<html><head></head><body><form action='/depotsales/control/payReambursement' id='reambursementPayForm' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		message += "<tr class='h3'><td align='left' class='h3' width='60%'>Shipment :</td><td align='left' width='60%'><input class='h4' type='label' id='shipmentId' name='shipmentId' value='"+shipmentId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>	Invoice Amount :</td><td align='left' width='60%'><input class='h4' type='label' id='invoiceAmount' name='invoiceAmount' value='"+invoiceAmount+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>	Eligibility Amount :</td><td align='left' width='60%'><input class='h4' type='label' id='eligibilityAmount' name='eligibilityAmount' value='"+eligibilityAmount+"' readOnly/></td></tr>"+

						"<tr class='h3'><td align='left' class='h3' width='60%'>Receipt Number :</td><td align='left' width='60%'><input maxlength='12' class='h4'  type='text' id='receiptNo' name='receiptNo' /><br/> <label id='receiptNoLabel' style='color:red;display:none'>Please enter Receipt Number.</label></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Receipt Amount :</td><td align='left' width='60%'><input maxlength='10' class='h4 number'    onKeypress='keypresseve(event)' type='text' id='receiptAmount' name='receiptAmount' /><br/> <label id='receiptAmountLabel' style='color:red;display:none'>Please enter Receipt Amount.</label></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Receipt Date :</td><td align='left' width='60%'><input  class='h4'     type='text' readonly id='receiptDate' name='receiptDate' onmouseover='datepick()' /><br/> <label id='receiptDateLabel' style='color:red;display:none'>Please enter Receipt Date.</label></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Description :</td><td align='left' width='60%'><textarea maxlength='100' style= 'height: 69px; width: 177px;' class='h4'     type='text' id='description' name='description' ></textarea><br/> <label id='descriptionLabel' style='color:red;display:none'>Please enter Description.</label></td></tr>" +
				 		"<tr class='h3' COLSPAN=2><td  align='right'><span align='right'><input type='button' id='submitval' value='Add' class='smallSubmit' onclick='addReceipt();'/></span></td></tr>";
				 		message +=	"</table>";
			message +=	"<table id='reambursementList' cellspacing=20 cellpadding=20 width=550>";
			message +=	"<tr><th>Receipt Number</th><th>Receipt Amount</th><th>Receipt Date</th><th>Description</th></tr>"+receiptString;	
			message +=	"</table>";
			message +=	"<table  cellspacing=20 cellpadding=20 width=550>";
			message +=	"<tr class='h3' COLSPAN=3 ><td align='right'><span align='right'><input type='submit' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";	
			message +=	"</table>";
			message += "<input  type='hidden' name='payReimbursementList' id='payReimbursementList' />";
			message +=	"</form></body></html>";
			var title = "Shipment Reimbursement for : "+shipmentId;
			
		Alert(message, title);
	}
	
	
	
var reambursementList =[]; 

function addReceipt(){
 var receiptNo="";
 var claimId="";
 var receiptAmount="";
 var receiptDate="";
 var description="";
 var flag=true;
 
 $('#receiptNoLabel').hide();
 $('#receiptAmountLabel').hide();
 var form=	$('#reambursementPayForm').serializeArray();
	 $.each($('#reambursementPayForm').serializeArray(), function(_, kv) {
	 
  if(kv.name=="receiptNo") {
    if(kv.value==""){
      flag=false;
      $('#receiptNoLabel').show();
    }
    receiptNo =kv.value;
    
   }
   if( kv.name=="receiptAmount") {
   if(kv.value==""){
      flag=false;
 	  $('#receiptAmountLabel').show();
    }
    receiptAmount=kv.value;
   } 
    if( kv.name=="receiptDate") {
   if(kv.value==""){
 //	  $('#receiptDateLabel').show();
    }
    receiptDate=kv.value;
   } 
    if( kv.name=="description") {
   if(kv.value==""){
      flag=false;
 //	  $('#description').show();
    }
    description=kv.value;
   } 
  });
   if(flag){
 	$('#reambursementPayForm')[0].reset();
 	var receiptString= buildReambursementList(claimId,receiptNo,receiptAmount,receiptDate,description);
	$("#reambursementList").append(receiptString);
   }
}
function buildReambursementList(claimId,receiptNo,receiptAmount,receiptDate,description){
  var reambursementObj ={};
   var receiptString="";
   receiptString+= "<tr class='h3'><td>"+receiptNo+"</td>";
   receiptString+= "<td>"+receiptAmount+"</td>";
   receiptString+= "<td>"+receiptDate+"</td>";
    receiptString+= "<td>"+description+"</td>";
   receiptString+= "<td><input type='button' value='Delete' style='cursor: pointer;' onclick='deleteReceipt(this)' /></td></tr>";
   reambursementObj['receiptNo']=receiptNo;
   reambursementObj['receiptAmount']=receiptAmount.toString();
   reambursementObj['claimId']=claimId;
   reambursementObj['receiptDate']=receiptDate;
   reambursementObj['description']=description;
   reambursementList.push(reambursementObj);
   $("#payReimbursementList").val(JSON.stringify(reambursementList));
   return receiptString;
}
function deleteReceipt(obj){
reambursementList.splice(($(obj).closest('tr').index()-1), 1);
$(obj).closest('tr').remove();
$("#payReimbursementList").val(JSON.stringify(reambursementList));
}

function keypresseve(event) {
  if ((event.which != 46 || $(this).val().indexOf('.') != -1) &&
    ((event.which < 48 || event.which > 57) &&
      (event.which != 0 && event.which != 8))) {
    event.preventDefault();
  }

  var text = $(this).val();

  if ((text.indexOf('.') != -1) &&
    (text.substring(text.indexOf('.')).length > 2) &&
    (event.which != 0 && event.which != 8) &&
    ($(this)[0].selectionStart >= text.length - 2)) {
    event.preventDefault();
  }
}

</script>
