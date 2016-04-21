
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
var jsonObj = ${StringUtil.wrapString(depotReimbursementList)!'{}'};
var timePeriodIdJson = ${StringUtil.wrapString(timePeriodIds)!'{}'};

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
	
	function fromDatepick()
	{		
		$( "#fromDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	function thruDatepick()
	{		
		$( "#thruDate" ).datepicker({
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
	
	
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	var ordersListGlobal={};
	      
	var globalShipmentId ="";
	
	
	
	function populateDate(){
	//alert("==shipmentId ==InPopulateData=="+globalShipmentId);
		jQuery("#grnShipmentId").val(globalShipmentId);
	};
	
function payReimbursement(facilityId,partyId,invoiceAmount,eligibilityAmount,groupName){
    var selectedTimePeriodId=timePeriodIdJson['timePeriodId'];
	var shipmentarr=jsonObj[facilityId];
	var receiptString="";
	if(shipmentarr){
	$.each(shipmentarr, function( index, value ) {
      receiptString+= buildReambursementList(value.claimId,value.claimType,value.receiptAmount,value.description,value.fromDate,value.thruDate);
	});
	}
		var message = "";
		message += "<html><head></head><body><form action='/depotsales/control/payDepotReimbursement' id='reambursementPayForm' method='post'><table cellspacing=20 cellpadding=20 width=550>";
			//message += "<br/><br/>";
		message += "<tr class='h3'><td align='left' class='h3' width='60%'>Depot Name :</td><td align='left' width='60%'><input class='h4' type='label' id='depotId' name='depotId' value='"+groupName+"' readOnly/></td>"+
		                "<td align='left' class='h3' width='60%'>	Scheme Time Period :</td><td align='left' width='60%'><input class='h4' type='label' value='"+selectedTimePeriodId+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>	Invoice Amount :</td><td align='left' width='60%'><input class='h4' type='label' id='invoiceAmount' name='invoiceAmount' value='"+invoiceAmount+"' readOnly/></td>"+
						"<td align='left' class='h3' width='60%'>	Eligibility Amount :</td><td align='left' width='60%'><input class='h4' type='label' id='eligibilityAmount' name='eligibilityAmount' value='"+eligibilityAmount+"' readOnly/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='60%'>From Date :</td><td align='left' width='60%'><input  class='h4'     type='text' readonly id='fromDate' name='fromDate' onmouseover='fromDatepick()'   /><br/> <label id='fromDateLabel' style='color:red;display:none'>Please enter From Date.</label></td></tr>" +
						"<tr class='h3'><td align='left' class='h3' width='60%'>Thru Date :</td><td align='left' width='60%'><input  class='h4'     type='text' readonly id='thruDate' name='thruDate' onmouseover='thruDatepick()' /><br/> <label id='thruDateLabel' style='color:red;display:none'>Please enter Thru Date.</label></td></tr>" +

						"<tr class='h3'><td align='left' class='h3' width='60%'>Claim Type :</td><td align='left' width='60%'><input maxlength='12' class='h4'  type='text' id='claimType' name='claimType' /><br/> <label id='claimTypeLabel' style='color:red;display:none'>Please select Claim Type.</label></td></tr>" +
						
						"<tr class='h3'><td align='left' class='h3' width='60%'> Amount :</td><td align='left' width='60%'><input maxlength='10' class='h4 number'    onKeypress='keypresseve(event)' type='text' id='receiptAmount' name='receiptAmount' /><br/> <label id='receiptAmountLabel' style='color:red;display:none'>Please enter Receipt Amount.</label></td></tr>" +
				 		"<tr class='h3'><td align='left' class='h3' width='60%'>Description :</td><td align='left' width='60%'><textarea maxlength='100' style= 'height: 69px; width: 177px;' class='h4'     type='text' id='description' name='description' ></textarea><br/> <label id='descriptionLabel' style='color:red;display:none'>Please enter Description.</label></td></tr>" +
				 		"<tr class='h3' COLSPAN=2><td  align='right'><span align='right'><input type='button' id='submitval' value='Add' class='smallSubmit' onclick='addReceipt();'/></span></td></tr>";
				 		message +=	"</table>";
			message +=	"<table id='reambursementList' cellspacing=20 cellpadding=20 width=550>";
			message +=	"<tr><th>Claim Type</th><th>Amount</th><th>FromDate</th><th>ThruDate</th><th>Description</th></tr>"+receiptString;	
			message +=	"</table>";
			message +=	"<table  cellspacing=20 cellpadding=20 width=550>";
			message +=	"<tr class='h3' COLSPAN=3 ><td align='right'><span align='right'><input type='button' id='submitval' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' id='cancel' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";	
			message +=	"</table>";
			message += "<input  type='hidden' name='facilityId' id='facilityId' value='"+facilityId+"' /><input  value='"+partyId+"'   type='hidden' name='partyId' id='partyId' /><input  type='hidden' name='payReimbursementList' id='payReimbursementList' /><input  type='hidden' name='selectedTimePeriodId' value='"+selectedTimePeriodId+"' id='selectedTimePeriodId' />";
			message +=	"</form></body></html>";
			var title = "Depot Reimbursement for : "+groupName;
			
		Alert(message, title);
	}
	
	
	
var reambursementList =[]; 

function addReceipt(){
 var claimType="";
 var claimId="";
 var receiptAmount="";
 var description="";
 var flag=true;
 var fromDate="";
 var thruDate="";
 
 $('#claimTypeLabel').hide();
 $('#receiptAmountLabel').hide();
 $('#fromDateLabel').hide();
 $('#thruDateLabel').hide();
 var form=	$('#reambursementPayForm').serializeArray();
	 $.each($('#reambursementPayForm').serializeArray(), function(_, kv) {
	 
  if(kv.name=="claimType") {
    if(kv.value==""){
      flag=false;
      $('#claimTypeLabel').show();
    }
    claimType =kv.value;
    
   }
   if( kv.name=="receiptAmount") {
   if(kv.value==""){
      flag=false;
 	  $('#receiptAmountLabel').show();
    }
    receiptAmount=kv.value;
   } 
 if( kv.name=="fromDate") {
   if(kv.value==""){
      flag=false;
      $('#fromDateLabel').show();
    }
    fromDate=kv.value;
    }
  if( kv.name=="thruDate") {
   if(kv.value==""){
      flag=false;
      $('#thruDateLabel').show();
    }
    thruDate=kv.value;
   }
    if( kv.name=="description") {
   if(kv.value==""){
 //     flag=false;
 //	  $('#description').show();
    }
    description=kv.value;
   } 
  });
   if(flag){
 	$('#reambursementPayForm')[0].reset();
 	var receiptString= buildReambursementList(claimId,claimType,receiptAmount,description,fromDate,thruDate);
	$("#reambursementList").append(receiptString);
   }
}
function buildReambursementList(claimId,claimType,receiptAmount,description,fromDate,thruDate){

  var reambursementObj ={};
   var receiptString="";
   receiptString+= "<tr class='h3'><td>"+claimType+"</td>";
   receiptString+= "<td>"+receiptAmount+"</td>";
   receiptString+= "<td>"+fromDate+"</td>";
   receiptString+= "<td>"+thruDate+"</td>";
   receiptString+= "<td>"+description+"</td>";
   receiptString+= "<td><input type='button' value='Delete' style='cursor: pointer;' onclick='deleteReceipt(this)' /></td></tr>";
   reambursementObj['claimType']=claimType;
   reambursementObj['receiptAmount']=receiptAmount.toString();
   reambursementObj['claimId']=claimId;
   reambursementObj['fromDate']=fromDate;
   reambursementObj['thruDate']=thruDate;
   reambursementObj['description']=description;
   reambursementList.push(reambursementObj);
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

//disable the generate button once the form submited
	function disableGenerateButton(){		
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	
	function submitFormParam(){ 
var fromDate = $("#fromDate").val();
var thruDate = $("#thruDate").val();
 $('#fromDateLabel').hide();
 $('#thruDateLabel').hide();
var flag=true;
  //  if(fromDate == undefined || fromDate==""){
  //    flag=false;
  //    $('#fromDateLabel').show();
  //  }
  // if(thruDate == undefined || thruDate==""){
  //    flag=false;
  //    $('#thruDateLabel').show();
 //   }
	if(flag){
	 $("#payReimbursementList").val(JSON.stringify(reambursementList));
	 $("input[type=submit]").attr("disabled", "disabled");
	 $('#reambursementPayForm').submit();	
	}	
	 
	
	}
</script>
