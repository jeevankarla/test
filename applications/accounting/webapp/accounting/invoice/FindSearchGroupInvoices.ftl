<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/tagit/jquery.tagit.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/tagit/tagit.js</@ofbizContentUrl>"></script>
<script>
    $(document).ready(function() {
        $("#partyIds").tagit({
            singleField: true,
            singleFieldDelimiter: ',',
            caseSensitive: false,
            fieldName:'partyIdFrom',
            allowSpaces: true,
            tagSource:function(request,response){
            	var params= "ajaxLookup=Y&_LAST_VIEW_NAME_=apGroupPayment&searchValueFieldName=partyId&term="+request.term+"&entityName=PartyNameView";
            	$.ajax({
	            	type: "POST",
	            	url: "LookupPartyNameJSON",
	            	data: params,
	            	dataType: 'json',
	            	success: function(result){
	            	   var strResult = result['partyJSON'];
					   response($.map(strResult,function(item){return {label: item.label,value: item.value}}));
					}
            	});
            }
        });
        $("#dueDate_fld0_value").datetimepicker({
	        showSecond: true,
	        timeFormat: 'hh:mm:ss',
	        stepHour: 1,
	        stepMinute: 5,
	        stepSecond: 10,
	        showOn: 'button',
	        buttonImage: '',
	        buttonText: '',
	        buttonImageOnly: false,
	        dateFormat: 'yy-mm-dd'
	      });
         $("#dueDate_fld1_value").datetimepicker({
            showSecond: true,
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
    });
</script>
<style>
.ui-corner-all {
    moz-border-radius: 0px;
    -webkit-border-radius: 0px;
    border-radius: 0px;
    
}
.ui-widget-content {
	background:white;
	border: 1px solid #999999;
}	
</style>
<div id="search-options">
<form method="post" action="/accounting/control/apGroupPayment" id="FindApGroupInvoices" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="FindApGroupInvoices">
    <input type="hidden" name="parentTypeId" value="PURCHASE_INVOICE" id="FindApGroupInvoices_parentTypeId">
    <input type="hidden" name="statusId" value="INVOICE_READY" id="FindApGroupInvoices_statusId">
    <input type="hidden" name="hideSearch" value="Y" id="FindApGroupInvoices_hideSearch">
    <input type="hidden" name="partyIdFrom_op" value="in" id="FindApGroupInvoices_partyIdFrom_op">
    <table cellspacing="0">
    <tbody>
    <tr>
	    <td class="label">
			<span id="FindApGroupInvoices_invoiceId_title">Invoice ID</span>    </td>
	    <td>
			<select name="invoiceId_op" class="selectBox"><option value="equals">Equals</option><option value="like">Begins With</option><option value="contains" selected="selected">Contains</option><option value="empty">Is Empty</option><option value="notEqual">Not Equal</option></select>
	 		<input type="text" name="invoiceId" size="25">
	 		<input type="checkbox" name="invoiceId_ic" value="Y" checked="checked"> Ignore Case
	    </td>
    </tr>
    <tr>
	    <td class="label"><span id="FindApGroupInvoices_partyIdTo_title">Party Id</span></td>
	    <td>
			<ul id="partyIds"style="width: 600px;"></ul>
	    </td>
    </tr>
    <tr>
	    <td class="label"><span id="FindApGroupInvoices_roleTypeId_title">Role Type Id</span></td>
	    <td>
			<span class="ui-widget">
				<select name="roleTypeId" id="FindApGroupInvoices_roleTypeId" size="1">
					<option value="">&nbsp;</option>
					<#list roleTypeAttrList as eachRoleType>
						<option value="${eachRoleType.roleTypeId}">${eachRoleType.description}</option>
					</#list>
				</select>
			</span>
    	</td>
    </tr>
    <tr>
	    <td class="label"><span id="FindApGroupInvoices_dueDate_title">Due Date</span></td>
	    <td>
			<span class="view-calendar">
			<input id="dueDate_fld0_value" type="text" name="dueDate_fld0_value" size="25" maxlength="30">
			<select name="dueDate_fld0_op" class="selectBox"><option value="equals" selected="selected">Equals</option><option value="sameDay">Same Day</option><option value="greaterThanFromDayStart">Greater Than From Day Start</option><option value="greaterThan">Greater Than</option></select><input id="dueDate_fld1_value" type="text" name="dueDate_fld1_value" size="25" maxlength="30">  
			<select name="dueDate_fld1_op" class="selectBox"><option value="opLessThan">Less Than</option><option value="upToDay">Up To Day</option><option value="upThruDay">Up Thru Day</option><option value="empty">Is Empty</option></select></span>
	    </td>
    </tr>
    <tr>
	    <td class="label">&nbsp;</td>
	    <td colspan="4">
			<input type="submit" class="smallSubmit" name="submitButton" value="Find">
	    </td>
    </tr>
    </tbody></table>
</form>
