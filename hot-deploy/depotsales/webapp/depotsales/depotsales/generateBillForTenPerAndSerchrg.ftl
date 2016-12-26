
<input type="hidden" name="partyClassificationGroupId" id="partyClassificationGroupId" value="${partyClassificationGroupId}">

<script type="application/javascript">

	function makeDatePicker(fromDateId ,thruDateId){
	$('#fromDate').datepicker({
			dateFormat:'mm/dd/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
	$('#toDate').datepicker({   
			dateFormat:'mm/dd/yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}	
   $(document).ready(function(){
 		makeDatePicker("fromDate","toDate");
 		var list = ${StringUtil.wrapString(billingPeriodsList)!'[]'};
 		var list2 = ${StringUtil.wrapString(closebillingPeriodsList)!'[]'}; 
 		var optionList = '';
		var optionList2 = '';
		optionList += "<option value = " + "" + " >" +" "+ "</option>";
		optionList2 += "<option value = " + "" + " >" +" "+ "</option>";
		if (list) {		       				        	
        	for(var i=0 ; i<list.length; i++){
				var innerList=list[i];	     
                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
      		}//end of main list for loop
      	}
      	if (list2) {		       				        	
        	for(var i=0 ; i<list2.length; i++){
				var innerList=list2[i];	     
                optionList2 += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
      		}//end of main list2 for loop
      	}
      	$("#billingPeriod").html(optionList);
      	$("#findbillingPeriod").html(optionList2);
	});
    
    function generateBillingForPeriod()
    { 
        var schemeBillingId= $('#billingPeriod').val();
        var dataJson ={};
        dataJson["schemeBillingId"]=schemeBillingId;
      	$('div#pastDues_spinner').show();
      	$('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
         $.ajax({
             type: "POST",
             url: "generateBillingForPeriod",
             data: dataJson,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]);
               }else{
            	    var billingPeriodsList =   result["billingPeriodsList"];
            	    var closebillingPeriodsList =   result["closebillingPeriodsList"];
            	    //var isInvoiceGen =   result["isInvoiceGen"];
			 		var optionList = '';
			 		var optionList2 = '';
					optionList += "<option value = " + "" + " >" +" "+ "</option>";
					optionList2 += "<option value = " + "" + " >" +" "+ "</option>";
					if (billingPeriodsList) {		       				        	
			        	for(var i=0 ; i<billingPeriodsList.length; i++){
							var innerList=billingPeriodsList[i];	     
			                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
			      		}//end of main billingPeriodsList for loop
			      	}
			      	if (closebillingPeriodsList) {		       				        	
			        	for(var i=0 ; i<closebillingPeriodsList.length; i++){
							var innerList=closebillingPeriodsList[i];	     
			                optionList2 += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
			      		}
			      	}
			      	$("#findbillingPeriod").html(optionList2);
			      	$("#billingPeriod").html(optionList);
			      	$('div#pastDues_spinner').hide();
			      	$('#responseMsg1').fadeIn('slow');
		       		$('#responseMsg1').delay(3000).fadeOut('slow');
			      	
               }
               
             } ,
             error: function() {
            	 	alert("Error In generateBillingForPeriod.groovy");
            	 	$('div#pastDues_spinner').hide();
            	 }
            }); 
    }
    
     function getInvoicesForForPeriod()
     { 
        var billingId= $('#findbillingPeriod').val();
        var dataJson ={};
        dataJson["billingId"]=billingId;
        dataJson["isInvoiceFind"]="Y";
      	$('div#pastDues_spinner').show();
      	$('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
         $.ajax({
             type: "POST",
             url: "getRembursmentInvoiceListing",
             data: dataJson,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]);
               }else{
            	    var rembInvoicesList =   result["rembInvoicesList"];
			 		var appendStr="";
	
			 		if (rembInvoicesList) {		
			 		    appendStr += "<form name='invoiceListingForm' id='invoiceListingForm'   method='post'>";
			 		    appendStr += "<table  class='basic-table hover-bar' cellspacing='0'>";  
			 		    appendStr += "<tr>";
			 		    appendStr += "<thead>";
			 		    appendStr += "<tr class='header-row-2'>";
			 		    appendStr += "<td align='center'>INVOICE ID</td>";
			 		    appendStr += "<td align='center'>INVOICE ITEM TYPE</td>";
			 		    appendStr += "<td align='center'>BILLING PERIOD</td>";
			 		    appendStr += "<td align='center'>PERIOD NAME</td>";
			 		    appendStr += "<td align='center'>INVOICE DATE</td>";
			 		    appendStr += "<td align='center'>INVOICE AMOUNT</td>";  
			 		    appendStr += "<td align='center'>VOUCHER</td>";
			 		    appendStr += "</thead>";
			 		    appendStr += "</tr>";
			 		    
			 		    
			        	for(var i=0 ; i<rembInvoicesList.length; i++){
							var innerList=rembInvoicesList[i];	 
							appendStr += "<tr> <td>"+innerList['invoiceId']+"</td> <td>"+innerList['invoiceItemTypeId']+"</td> <td>"+innerList['billingPeriod']+"</td> <td>"+innerList['periodName']+"</td> <td>"+innerList['invoiceDate']+"</td> <td>"+innerList['itemValue']+"</td>  <td><a class='buttontext' target='_blank' href='<@ofbizUrl>invoiceVoucher?invoiceId="+innerList['invoiceId']+"</@ofbizUrl>'>Print</a></td>  </tr>";    
			      		}
			      		appendStr += "</table>"
			 		    appendStr += "</form>";
			 		    $('div#pastDues_spinner').hide();
			      		$("#displayInvoiceList").html(appendStr);
			      		
			      	}
               }
             } ,
             error: function() {
            	 	alert("Error In generateBillForTenPerAndSerchrg.groovy");
					$('div#pastDues_spinner').hide();
            	 }
            }); 
    }
    
    function createSchemeTimePeriod()
    { 
        var frmDate= $('#fromDate').val();
        var toDate= $('#toDate').val();
        var periodNam= $('#periodName').val();
        var dataJson ={};
        dataJson["periodName"]=periodNam;
        dataJson["fromDate"]=frmDate;
        dataJson["toDate"]=toDate;
        $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
         $.ajax({
             type: "POST",
             url: "createSchemeBillingTimePeriod",
             data: dataJson,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]);
               }else{
            	    var billingPeriodsList =   result["billingPeriodsList"];
            	    var closebillingPeriodsList =   result["closebillingPeriodsList"];
			 		var optionList = '';
			 		var optionList2 = '';
					optionList += "<option value = " + "" + " >" +" "+ "</option>";
					optionList2 += "<option value = " + "" + " >" +" "+ "</option>";
					if (billingPeriodsList) {		       				        	
			        	for(var i=0 ; i<billingPeriodsList.length; i++){
							var innerList=billingPeriodsList[i];	     
			                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
			      		}//end of main billingPeriodsList for loop
			      	}
			      	if (closebillingPeriodsList) {		       				        	
			        	for(var i=0 ; i<closebillingPeriodsList.length; i++){
							var innerList=closebillingPeriodsList[i];	     
			                optionList2 += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
			      		}//end of main closebillingPeriodsList for loop
			      	}
			      	$("#billingPeriod").html(optionList);
			      	$("#findbillingPeriod").html(optionList2);
			      	$('div#pastDues_spinner').hide();
			        $('#responseMsg').fadeIn('slow');
		       		$('#responseMsg').delay(3000).fadeOut('slow');
               }
               
             } ,
             error: function() {
            	 	alert("Error In createSchemeTimePeriod.groovy");
            	 	$('div#pastDues_spinner').hide();
            	 }
            }); 
    }
</script>
<form name="updateForm" id="updateForm"   method="post"  action="createOrUpdateWeaversPartyClassification"> </form>
 
<form name="tenPerBillingForm" id="tenPerBillingForm"   method="post">
     <table id="coreTable" class="basic-table" cellspacing="0">
       <thead>
           <tr class="header-row-2">
	          <td align="center"><h3>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;CREATE NEW PERIOD FOR BILLING</h3></td>
	          <td align="center"><h3>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;GENERATE BILLING FOR PERIODS </h3></td>
	          <td align="center"><h3>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;FIND REIMBURSEMENT INVOICES </h3></td>
	          <td> &#160;</td>
	        </tr>
		</thead>
     <tbody>
		<tr>
			<td><b>Period Name :</b> <input type="text" name="periodName" id="periodName" size="18"/></td> 
			<td ></td>
		</tr>
		<tr>
		  	<td ><b>From Date :</b>&#160; &#160;&#160; <input type="text" name="fromDate" id="fromDate" size="18" maxlength="40"   /></td> 
		  	<td ><b>Billing Period</b>
				<select name="billingPeriod" id="billingPeriod">
				</select>
			</td>
			<td ><b>Billing Period</b>
				<select name="findbillingPeriod" id="findbillingPeriod">
				</select>
			</td>
		</tr>
		<tr>
		  	<td ><b>To Date :</b>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<input type="text" name="toDate" id="toDate" size="18" maxlength="40"/></td>
		  	 
		</tr>
		<tr>
		  	<td>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<input type="button" name="create" value="create" onClick="javascript:createSchemeTimePeriod();"/></td>
		  	<td>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<input  type="button" name="GenerateBilling" value="Generate Billing" onClick="javascript:generateBillingForPeriod();"/></td>
		  	<td>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<input  type="button" name="find" value="find" onClick="javascript:getInvoicesForForPeriod();"/></td>
		</tr>
		<tr>
			<td> &#160;</td>
			<td> &#160;</td>
			<td> &#160;</td>
        </tr>
       
     	<tr>
     		<td> </td>
     		<td> <div align='center' name ='displayMsg' id='pastDues_spinner'/> </td>
     		<td> </td>
     	</tr>
     	<tr>
     		<td> </td>
     		<td> <div align='center' name ='responseMsg' id='responseMsg' style="display:none"><h1 style="font-color:Green">Billing Period Updated Successfully..<h1> </div> </td>
     		<td> </td>
     	</tr>
     	<tr>
     		<td> </td>
     		<td> <div align='center' name ='responseMsg1' id='responseMsg1' style="display:none"><h1 style="font-color:Green">Billing Successfully Generated For the Slected Period..<h1> </div> </td>
     		<td> </td>
     	</tr>
      	<tr>
     		<td> </td>
     		<td> <div align='center' name ='responseMsg2' id='responseMsg2' style="display:none"><h1 style="font-color:Green">Invoice Already Generated for Selected Period..<h1> </div> </td>
     		<td> </td>
     	</tr>
      </tbody>
    </table>
  </form>

<div id="displayInvoiceList">

</div>





