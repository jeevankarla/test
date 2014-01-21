<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">

$(document).ready(function(){
		$( "#orderDate" ).datepicker({
			dateFormat:'yy-mm-dd',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#orderDate" ).datepicker( "option", "minDate", selectedDate );
			}
		});
		
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	
	
	
	function appendParamsToIndentEntryProducts(t){
	
	
		var form = $("#IndentEntryProducts");
		
	    var orderDate = $("input[name=orderDate]").val();
	    var partyId = $("input[name=customerId]").val();
	    var orderName = $("input[name=orderName]").val();
	    //var indentType = jQuery('#indentType').val();   
	    var productStoreId = jQuery('#productStoreId').val();
	    	   
	    // lets append partyId param to form
	    var inputPartyId = $("<input type='hidden'/>");
	     inputPartyId.attr("id", "partyId")
	     .attr("name", "partyId")
	     .attr("value", partyId);
	    $(form).append(inputPartyId);
	    
	     // lets append orderDate param to form
	     var inputOrderDate = $("<input type='hidden'/>");
	     inputOrderDate.attr("id", "orderDate")
	     .attr("name", "orderDate")
	     .attr("value", orderDate);
	    $(form).append(inputOrderDate);
	    
	     // lets append orderName param to form
	     var inputOrderName = $("<input type='hidden'/>");
	     inputOrderName.attr("id", "orderName")
	     .attr("name", "orderName")
	     .attr("value", orderName);
	    $(form).append(inputOrderName);
	    
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
<form method="post" name="indentEntry" action="<@ofbizUrl>initorderentry</@ofbizUrl>">	 
      <table width="60%" border="0" cellspacing="0" cellpadding="0">    
        <#--<tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>Indent Type:</div></td>
          <td>&nbsp;</td>
          <td >
      		 <select name="indentType" id="indentType" class='h4'>
                <option value='RMS' selected="selected" >RMS</option>
                <option value='DC' selected="selected">DC</option>
                <option value='RO' selected="selected">RO</option>
			 </select>
          </td>
       </tr>--> 
       <#--<tr>
        	<td>&nbsp;</td>
       		<td align='right' valign='middle' class='h3' nowrap="nowrap">Indent Number:</td>
       		<td>&nbsp;</td>
        	<td align='left'>
          <input type='text' size='20' maxlength='20' name='IndentNumber'/>
      	  </td>
      </tr>-->
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
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
           <div class='h3'><input type="text" id="orderDate" name="orderDate"/></div></td>
        </tr> 
        <tr><td><br/></td></tr>             
       <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap="nowrap"><div class='h3'>${uiLabelMap.OrderCustomer}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${thisPartyId?if_exists}' formName="indentEntry" name="customerId" id="customerId" fieldFormName="LookupCustomerName"/>
            </div>
          </td>
       </tr>
       <tr>
        <td>&nbsp;</td>
        <td align='right' valign='middle' class='h3' nowrap="nowrap">
           ${uiLabelMap.OrderOrderName}:
        </td>
        <td>&nbsp;</td>
        <td align='left'>
          <input type='text' size='30' maxlength='40' name='orderName'/>
        </td>
      </tr>        
      </table>
</form>
<br/>
    </div>
</div>