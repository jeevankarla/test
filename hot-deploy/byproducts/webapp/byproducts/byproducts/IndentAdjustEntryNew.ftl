<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	


<#include "IndentAdjustInc.ftl"/>
<script type="text/javascript">

$(document).ready(function(){
		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');

		
		$('#productId').keypress(function (e) {
			$("#productId").autocomplete({					
  				source:  availableTags
			 });
  			if (e.which == $.ui.keyCode.ENTER) {
  				$('#indententryinit').submit();
    			return false;   
  			}
		});		
	});
</script>


<#assign changeRowTitle = "Changes">                


<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
       <h3>Indent Input</h3>
     </div>
    <div class="screenlet-body">
	<form method="post" name="indententryinit" action="<@ofbizUrl>IndentAdjustEntryNew</@ofbizUrl>" id="indententryinit">  
      <table width="100%" border="0" cellspacing="0" cellpadding="0">     
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <#if effectiveDate?exists && effectiveDate?has_content>  
	  	  	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
          	<td valign='middle'>
            	<div class='tabletext h2'>${effectiveDate}         
            	</div>
          	</td>       
       	  <#else>               
          	<td valign='middle'>          
            	 <input class='h2' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>          
          	</td>
       	</#if>
        </tr>

	    	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="BYPRODUCTS"/> 
       <tr><td><br/></td></tr>
        <tr>
          	<td>&nbsp;</td>
          	<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Route:</div></td>
          	<td>&nbsp;</td>
      		<#if route?exists || productId?exists>  
	  	  	<input type="hidden" name="routeId" id="routeId" value="${routeId}"/>  
          	<td valign='middle'>
            	<div class='tabletext h2'><#if route?exists>  
               		${route.facilityId?if_exists} [${route.facilityName?if_exists}]</#if>               
            	</div>
          	</td>       
       		<#else>               
          	<td valign='middle'>          
            	<input class='h2' type="text" size="10" maxlength="6" name="routeId" id="routeId" value=""/>          
          	</td>
       		</#if>
        </tr>   
        <tr><td><br/></td></tr>          
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Product Code:</div></td>
          <td>&nbsp;</td>
       <#if product?exists>  
	  	  <input type="hidden" name="productId" id="productId" value="${productId}"/>  
          <td valign='middle'>
            <div class='tabletext h2'><#if productId?exists>  
               	${productId.toUpperCase()?if_exists} [${product.productName?if_exists}]</#if>              
            </div>
          </td>       
       <#else>               
          <td valign='middle'>          
             <input class='h2' type="text" size="10" maxlength="6" name="productId" id="productId" value=""/>          
             <span class="tooltip">Input Product Code and press Enter</span>
          </td>
       </#if>
        </tr>
                  
      </table>
</form>
<br/>
<form method="post" id="indententry" action="<@ofbizUrl></@ofbizUrl>">  
	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate?if_exists}"/>
	<input type="hidden" name="productId" id="productId" value="${parameters.productId?if_exists}"/>	
	<input type="hidden" name="routeId" id="routeId" value="${parameters.routeId?if_exists}"/>
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
			
			<br>

</form>
</div>
</div>
<div class="screenlet">
    <div class="screenlet-body">
   			 <div class="grid-header" style="width:100%;">
				<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] </#if></label>	
			</div>
			<div id="myGrid2" style="width:100%;height:80px;"></div>	
    </div>
</div>     
  
</div>
 
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Product Change : ${productId?if_exists}   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><span id="totalIndents" align="right"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>,&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span id="totalQty" align="right"></span>
		</div> 
		<div id="myGrid1" style="width:100%;height:350px;"></div>  
	<#if product?exists>					
    <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>processIndentEntryUpdate</@ofbizUrl>');"/>
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel"/>   	
    </div>     
	</#if>  
	</div>
</div>     
</div>
</div>





 	