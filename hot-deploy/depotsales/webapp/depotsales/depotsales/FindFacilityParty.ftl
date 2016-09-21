<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="text/javascript">
$(document).ready(function(){
	$('#loader').hide();
	$('#messages-container').hide();
	$("#fromDate").datepicker({
		dateFormat:'d MM, yy',
		changeMonth: true,
		numberOfMonths: 1,
		changeYear: true,
		onSelect: function(selectedDate) {
			$("#fromDate").datepicker("option", selectedDate);
		}
	});
	
	var societyAutoJson = ${StringUtil.wrapString(societyJSON)!'[]'};
	$('#societyPartyId').keypress(function (e) { 
		$("#societyPartyId").autocomplete({ source: societyAutoJson , select: function( event, ui ) {
			$('span#societyPartyName').html('<label>'+ui.item.label+'</label>');
		} });	
	});
	
	$("#createFacility").click(function(event){
		event.preventDefault();
		$(".smallSubmit").attr("disabled", true);
		if($("#partyId").val()==""){
			$('span#partyName').html('<label style="color:red">Customer Can not be empty</label>');
			return false;
		}
		if($("#societyId").val()==""){
			$('span#societyPartyName').html('<label style="color:red">Society Can not be empty</label>');
			return false;
		}
		$('#loader').show();
		$('#messages-container').hide();
		
		var partyId = $("#partyId").val();
		var societyPartyId = $("#societyPartyId").val();
		var fromDate = $("#fromDate").val();
		var roleTypeId = $("#roleTypeId").val();
		var dataJson = {
			"partyId" : partyId,
			"societyPartyId" : societyPartyId,
			"roleTypeId" : roleTypeId,
			"fromDate" : fromDate
		};
		$.ajax({
			 type: "POST",
             url: "createCustomerAssoc",
             data: dataJson,
             dataType: 'json',	            
			 success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					document.getElementById("content-messages").className = "content-messages errorMessage";
					document.getElementById("responseMessage").innerHTML = result["_ERROR_MESSAGE_"];
					$('#loader').hide();
					$('#messages-container').show();
					$(".smallSubmit").prop('disabled', false);					
				}else{
					document.getElementById("content-messages").className = "content-messages eventMessage";		
					document.getElementById("responseMessage").innerHTML = result["_EVENT_MESSAGE_"];				
					$('#loader').hide();
					$('#messages-container').show();
					document.getElementById("createCustomerAssoc").reset();
					$('span#societyPartyName').html('<label>Required</label>');
					$('span#partyName').html('<label>Required</label>');
					$(".smallSubmit").prop('disabled', false);
				}								 
			 },
			 error: function(){
				alert("error");
			 }							
		});
	
	});
});	

function autoCompletePartyId(){
	$("#partyId").autocomplete({ 
		source: function( request, response ) {
		$.ajax({
			url: "LookupBranchCustomers",
			dataType: "html",
			data: {
				ajaxLookup: "Y",
				term : request.term,
			},
			success: function( data ) {
				var dom = $(data);
				dom.filter('script').each(function(){
					$.globalEval(this.text || this.textContent || this.innerHTML || '');
				});
				response($.map(autocomp, function(v,i){
					return {
						label: v.label,
						value: v.value
					};
				}));
			}
		});
        			
      },
		select: function(e, ui) {
			$('span#partyName').html('<label>'+ui.item.label+'</label>');
		}
					
	});	
}
</script>

<div id="messages-container">
  <div id="content-messages" class="content-messages eventMessage" onclick="document.getElementById('content-messages').parentNode.removeChild(this)">
  	<p>The Following Occured</p>
    <p id="responseMessage"></p>
  </div>
</div>

<div id="loader" > 
      <p align="center" style="font-size: large;">
        <img src="<@ofbizContentUrl>/images/jquery/plugins/jqplot/examples/ajax-loader.gif</@ofbizContentUrl>">
      </p>
</div>

<div class="full">
    <div class="screenlet-body">
		
		<form name="createCustomerAssoc" id="createCustomerAssoc" method="post" action="<@ofbizUrl>createCustomerAssoc</@ofbizUrl>">
		    <input type="hidden" name="roleTypeId" id="roleTypeId" value="EMPANELLED_CUSTOMER">
		    <table class="basic-table" cellspacing="0">
		      <tbody>
		      <tr>
		        <td class="label">Customer</td>
		        <td>
		          <input type="text" size="20" name="partyId" id="partyId" onfocus='javascript:autoCompletePartyId();' class="required" required>
		          <span class="tooltip" id="partyName">Required</span>
		        </td>
		      </tr>
		      <tr>
		        <td class="label">Society</td>
		        <td>
		          <input type="text" size="20" name="societyPartyId" id="societyPartyId" class="required" required>
		          <span class="tooltip" id="societyPartyName">Required</span>
		        </td>
		      </tr>
		      <tr>
		        <td class="label">From Date</td>
		        <td>
		          <input type="text" size="20" name="fromDate" id="fromDate" >
		        </td>
		      </tr>
		      <tr>
			    <td class="label">&nbsp;</td>
			    <td colspan="4"><input type="submit" class="smallSubmit" name="submit" id="createFacility" value="Create">
			    </td>
			  </tr>
		    </tbody></table> 
		</form>
		
	 </div> 
</div>
