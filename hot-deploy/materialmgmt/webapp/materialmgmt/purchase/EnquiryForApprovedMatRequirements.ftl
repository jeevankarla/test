
<script type="text/javascript">

$(document).ready(function(){

});

	function toggleRequirementId(master) {
        var Ids = jQuery("#EnquiryForApprovedMatRequirements :checkbox[name='requirementIds']");

        jQuery.each(Ids, function() {
            this.checked = master.checked;
            
        });
        getRequrimentsRunningTotal();
    }
    
 	function getRequrimentsRunningTotal() {
  		var requirementsList=[];
		var requirements = jQuery("#EnquiryForApprovedMatRequirements :checkbox[name='requirementIds']");
		
		jQuery.each(requirements,function(){
			if(jQuery(this).is(':checked')){
				requirementsList.push($(this).val());
			}		
		});
		if(requirementsList.length==0){
			totalCount=0;
			productCount=0;
			jQuery("#totalCount").html(totalCount);
			jQuery("#productCount").html(productCount);
		}
		var data="list="+requirementsList;
		$.ajax({
	        type: "POST",
	        url: "getRequrimentsRunningTotal",
	        data: data,
	        dataType: 'json',
        	success: function(result) {
        		totalCount=result["totalCount"];
        		productCount=result["productCount"];
        			jQuery("#totalCount").html(totalCount);
        			jQuery("#productCount").html(productCount);
       		 },
       		error: function() {
       	 		alert(result["_ERROR_MESSAGE_"]);
       	 	}
		});
	}
	
 	function massRequirementsSubmit(current){
		jQuery(current).attr( "disabled", "disabled");
		var requirements = jQuery("#EnquiryForApprovedMatRequirements :checkbox[name='requirementIds']");
		jQuery.each(requirements,function(){
			if(jQuery(this).is(':checked')){
				var reqId=$(this).val();
				$('#sendRequirementIds').append('<input type="hidden" name="requirementIds" value="'+reqId+'" />');
			}		
		});
		 jQuery("#sendRequirementIds").submit();
       
 	}	
</script>
<#if requirements?has_content>
<form id="sendRequirementIds" name="sendRequirementIds" action="sendRequirementIds" method="post">
</form>
<div align="left">
	  <font size="15" color="blue"><b>Total Selected :<b/></font><font size="15" color="red"><b><span id="totalCount"></span></b></font>&nbsp;&nbsp;&nbsp;<font size="15" color="blue"><b>No Of Products :<b/></font><font size="15" color="red"><b><span id="productCount"></span></b></font>
	</div>
<form id="EnquiryForApprovedMatRequirements" name="EnquiryForApprovedMatRequirements" action="" method="post">
	<div align="right">
		<input id="submitButton" type="button"  onclick="javascript:massRequirementsSubmit(this);" value="Submit" />
	</div>
	<table class="basic-table hover-bar" cellspacing="0">
		<thead>
		  <tr class="header-row-2">
		  <td>Requirement Id</td>
		  <td>Product Id</td>
		  <td>Product Name</td>
		  <td>Facility Id</td>
		  <td>Quantity</td>
		  <td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllRequirements" name="checkAllRequirements" onchange="javascript:toggleRequirementId(this);"/></td>
		  </tr>
		</thead>
		<tbody>
		 <#assign alt_row = false>
		<#list requirements as requirement>
			<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				<td>${requirement.requirementId?if_exists}</td>
				<td>${requirement.productId?if_exists}</td>
				<td>  <#assign productName = delegator.findOne("Product", {"productId" : requirement.productId}, true) /> ${productName.productName}</td>
				<td>${requirement.facilityId?if_exists}</td>
				<td>${requirement.quantity?if_exists}</td>
				<td align="right"><input type="checkbox" id="requirementId_${requirement.requirementId?if_exists}" name="requirementIds" value="${requirement.requirementId?if_exists}" onclick="javascript:getRequrimentsRunningTotal();"/></td>
			</tr>
			<#assign alt_row = !alt_row>
		</#list>	
		</tbody>
	</table>
</form>
<#else>
<h3>No Approved Requirements Found...!</h3>
</#if>