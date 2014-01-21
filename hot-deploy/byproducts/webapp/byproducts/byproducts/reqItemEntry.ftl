<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">

$(document).ready(function(){
		$( "#responseRequiredDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#responseRequiredDate" ).datepicker( "option", "minDate", selectedDate );
			}
		});
		
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	
	
	
	function appendParamsToIndentEntryProducts(t){
	
	
		var form = $("#EditRequestTest");
		
	    var responseRequiredDate = $("input[name=responseRequiredDate]").val();
	    var fromPartyId = $("input[name=fromPartyId]").val();
	    var custRequestName = $("input[name=custRequestName]").val();
	    //var indentType = jQuery('#indentType').val();   
	    var priority = jQuery('#priority').val();
	    var indentType = jQuery('#indentType').val();
	    var productStoreId = jQuery('#productStoreId').val();
	    	   
	    // lets append fromPartyId param to form
	    var inputfromPartyId = $("<input type='hidden'/>");
	     inputfromPartyId.attr("id", "fromPartyId")
	     .attr("name", "fromPartyId")
	     .attr("value", fromPartyId);
	    $(form).append(inputfromPartyId);
	    
	     // lets append responseRequiredDate param to form
	     var inputResponseRequiredDate = $("<input type='hidden'/>");
	     inputResponseRequiredDate.attr("id", "responseRequiredDate")
	     .attr("name", "responseRequiredDate")
	     .attr("value", responseRequiredDate);
	    $(form).append(inputResponseRequiredDate);
	    
	     // lets append custRequestName param to form
	     var inputCustRequestName = $("<input type='hidden'/>");
	     inputCustRequestName.attr("id", "custRequestName")
	     .attr("name", "custRequestName")
	     .attr("value", custRequestName);
	    $(form).append(inputCustRequestName);
	    
	    // lets append priority param to form
	     var inputPriority = $("<input type='hidden'/>");
	     inputPriority.attr("id", "priority")
	      .attr("name", "priority")
	      .attr("value", priority);
	     $(form).append(inputPriority); 
	    form.submit();		
	    
	    // lets append indentType param to form
	     var inputIndentType = $("<input type='hidden'/>");
	     inputIndentType.attr("id", "indentType")
	      .attr("name", "indentType")
	      .attr("value", indentType);
	     $(form).append(inputIndentType); 
	    form.submit();
	    
	    // lets append productStoreId param to form
	     var inputProductStoreId = $("<input type='hidden'/>");
	     inputProductStoreId.attr("id", "productStoreId")
	      .attr("name", "productStoreId")
	      .attr("value", productStoreId);
	     $(form).append(inputProductStoreId); 
	    form.submit();
}
	
</script>


<div class="screenlet">
	 <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.IndentEntry}</li>    
    </ul>
    <br class="clear"/>
  </div>
    <div class="screenlet-body">
<form method="post" name="reqItemEntry" action="">	 
      <table width="60%" border="0" cellspacing="0" cellpadding="0">    
        
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Store</div></td>
          <td>&nbsp;</td>
          <td >
      		 <select name="productStoreId" id="productStoreId" class='h4'>
               <option value='9001' selected="selected">Vijaya</option>
               <option value='9002' >VIJAYA FGS</option>
               <option value='10000'>TestStore</option>
			 </select>
          </td>
       </tr>
       <tr><td><br/></td></tr>
      	<tr>
      	  	<td>&nbsp;</td>
      	  	<td align='right' valign='middle' class='h3' nowrap="nowrap">
      	    	 Indent Name:
      	  	</td>
       	 	<td>&nbsp;</td>
       	 	<td align='left'>
        	  	<input type='text' size='30' maxlength='40' name='custRequestName'/>
      	 	</td>
      	</tr> 
      	<tr>
          	<td>&nbsp;</td>
          	<td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Requesting Party:</div></td>
          	<td>&nbsp;</td>
          	<td valign='middle'>
            	<div class='tabletext'>
              		<@htmlTemplate.lookupField value='${thisPartyId?if_exists}' formName="reqItemEntry" name="fromPartyId" id="fromPartyId" fieldFormName="LookupPartyName"/>
            	</div>
          	</td>
       	</tr>
       	<tr><td><br/></td></tr>
      	<tr>
          	<td>&nbsp;</td>
          	<td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Priority:</div></td>
          	<td>&nbsp;</td>
          	<td >
      		 	<select name="priority" id="priority" class='h4'>
               		<option value='1'>High</option>
               		<option value='2' selected="selected">Medium</option>
               		<option value='3'>Low</option>
			 	</select>
          	</td>
       	</tr>
        <tr>
          	<td>&nbsp;</td>
          	<td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Required Date:</div></td>
          	<td>&nbsp;</td>
          	<td valign='middle'>
           	<div class='h3'><input type="text" id="responseRequiredDate" name="responseRequiredDate"/></div></td>
        </tr> 
        <tr>
          	<td>&nbsp;</td>
          	<td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Indent Type:</div></td>
          	<td>&nbsp;</td>
          	<td >
      		 	<select name="indentType" id="indentType" class='h4'>
               		<option value='stockTransfer'>Stock Transfer</option>
               		<option value='order' selected="selected">Order</option>
			 	</select>
          	</td>
       	</tr>
        <tr><td><br/></td></tr>             
       
              
      </table>
</form>
<br/>
    </div>
</div>