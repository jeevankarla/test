
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	var countVal=0;
	
	var contracterNameObj = ${StringUtil.wrapString(contracterNameObj)}
   var contractorJSON = ${StringUtil.wrapString(contractorJSON)}
 function showTextBox(selected){
 	var capacity = selected.value;
 	if(capacity == "OTHER"){
 	$("#capacityDiv").show();
 	countVal=countVal+1;
 	}else{
 	$("#capacityDiv").hide();
 	countVal=0;
 	}
 }
 function checkCapacity(){
	 var capacity = $("#custmCapacity").val();
	 if((capacity == "") && (countVal!=0)){
	   alert("Please Enter the Vehicle Capacity.");
	   return false;
	 }
	 var vehicleId = $("#vehicleId").val();
	 var roleTypeId = $("#roleTypeId").val();
	 var partyId = $("#0_lookupId_partyId").val();
	 var fromDate = $("#fromDate").val();
	 var vehicleCapacity = $("#vehicleCapacity").val();
	 if((vehicleId == "") || (roletypeId = "") || (partyId =="undefined") || (fromDate == "") || (vehicleCapacity == "")){
		alert("Please Fill The Required Details.!");
		return false;
	 }
	 $("#createNewTankerForm").submit();
 }
 
 $(document).ready(function(){
		$("#partyId").autocomplete({ source: contractorJSON }).keydown(function(e){});
		$( "#fromDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
		});
		$('#ui-datepicker-div').css('clip', 'auto');
		
		 $("input").keyup(function(e){
	  		if(e.target.name == "vehicleId"){
	  			$('[name=vehicleId]').val(($('[name=vehicleId]').val()).toUpperCase());
	  		}
          });
          $("input").keyup(function(e){
	  		if(e.target.name == "partyId"){
	  			$('[name=partyId]').val(($('[name=partyId]').val()).toUpperCase());
	  		}
          });
	});
	
function displayName(selection){
	  var value = $("#partyId").val();
	   var name = contracterNameObj[value];
	   $('span#contractorName').html('<label>'+name+'</label>');   
	}
</script>
<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>Create New Tanker</h3>
    </div>
    <div class="screenlet-body">
    	<form name="createNewTankerForm" id="createNewTankerForm" action="createNewTanker" method="post">
    	<table style="border-spacing: 50px 5px;" border="1">
    		<input type="hidden" id="roleTypeId" name="roleTypeId" value="PTC_VEHICLE"/>
    		<tr>
    			<td><span class="h3">Vehicle No </span></td>
    			<td><input type="text" name="vehicleId" id="vehicleId" size="12"/><font color='red'>*</font></td>
    		</tr>
    	<#--  <tr>
    			<td><span class="h3">Vehicle Type </span></td>
    			<#if vehicleTypesList?has_content>
    			<td> <select name="roleTypeId" id="roleTypeId"/>
    			             <option value=""></option>
    			             <#list vehicleTypesList as vehicleType>
                             <option value="${vehicleType.roleTypeId}">${vehicleType.description?if_exists}</option>
                             </#list>
    						 </select><font color='red'>*</font>	
    			</td>
    			</#if>
    		</tr>  -->
    		<tr>
    			<td><span class="h3">Contractor </span></td>
    			<td><input type="text" name="partyId" id="partyId" size="12" onblur="javascript:displayName(this);"/><font color='red'>*</font><span class="tooltip" id="contractorName"/> </td>
    		</tr>
    	<#--	<tr>
    			<td><span class="h3">Vehicle Rate </span></td>
    			<td><input type="text" name="rateAmount" id="rateAmount" size="12"/></td>
    		</tr> -->
             <tr>
    			<td><span class="h3">From Date </span></td>
    			<td><input type="text" name="fromDate" id="fromDate" size="12"/><font color='red'>*</font></td>
    		</tr>
    		<tr>
    			<td><span class="h3">Vehicle Capacity(Kgs) </span></td>
    			<#if vehicleCapacitys?has_content>
    			<td> <select name="vehicleCapacity" id="vehicleCapacity" onchange="javascript:showTextBox(this);"/>
    			             <option value=""></option>
    			             <#list vehicleCapacitys as vehicleCapacity>
                             <option value="${vehicleCapacity}">${vehicleCapacity?if_exists}</option>
                             </#list>
                           <#--  <option value="OTHER">Other</option> --> 
    						 </select><font color='red'>*</font>	
    			</td>
    			</#if>
				    <td id="capacityDiv" style="display:none"><input type="text" name="custmCapacity" id="custmCapacity" size="12" ></td>
    		</tr>
    		<tr>
    		<td></td>
    		<td><input type="button" style="padding:.3em" id="changeSave" value="Create" onclick="javascript:checkCapacity();"/></td>
    		</tr>
    	</table>
    	</form>
    </div>
</div>