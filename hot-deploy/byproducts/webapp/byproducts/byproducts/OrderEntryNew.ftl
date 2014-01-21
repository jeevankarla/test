<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

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
		
		$('#boothId').keypress(function (e) {
  			if (e.which == $.ui.keyCode.ENTER) {
    			$('#indententryinit').submit();
    			return false;   
  			}
		});		
	});
</script>

<#assign changeRowTitle = "Changes">                

<#include "OrderEntryInc.ftl"/>
<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
		<h3>Parlor Sales
		<ul>
         <li><a href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=parlorEntry&&all=Y</@ofbizUrl>" >All Check List</a></li>
         <li><a href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=parlorEntry</@ofbizUrl>">My Check List</a></li>
		 </ui>
		</h3>
	</div>
    <div class="screenlet-body">
		<form method="post" name="indententryinit" id="indententryinit" action="<@ofbizUrl>OrderEntryNew</@ofbizUrl>">  
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
        <tr><td><br/></td></tr>        
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Order Options:</div></td>
          <td>&nbsp;</td>
          <#if effectiveDate?exists && effectiveDate?has_content>  
	  	  	<input type="hidden" name="orderOption" id="orderOption" value="${orderOption?if_exists}"/>  
          	<td valign='middle'>
            	<div class='tabletext h2'><#if orderOption=="update">Single Order<#else>Multi Order</#if>         
            	</div>
          	</td>       
       	  <#else>               
          	<td valign='middle'>          
            	 <select name="orderOption" class='h3'>
        		<option value='new'>Multi Order</option>
        		<option selected="selected" value='update'>Single Order</option>                
            </select>          
          	</td>
       	</#if>
        </tr>		      
        <tr><td><br/></td></tr>             
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Party Code:</div></td>
          <td>&nbsp;</td>
       <#if booth?exists && booth?has_content>    
	  	  <input type="hidden" name="boothId" id="boothId" value="${boothId.toUpperCase()}"/>  
          <td valign='middle'>
            <div class='tabletext h2'>${booth.facilityId.toUpperCase()}  [${booth.facilityName?if_exists}]       
            </div>
          </td>       
       <#else>               
          <td valign='middle'>          
             <input class='h2' type="text" size="10" maxlength="6" name="boothId" id="boothId" value=""/>          
             <span class="tooltip">Input parlour number and press Enter</span>
          </td>
       </#if>
        </tr>   
      </table>
</form>
<br/>
<form method="post" id="indententry" action="<@ofbizUrl>OrderEntryInit</@ofbizUrl>">  
	<#if effectiveDate?exists && effectiveDate?has_content>
		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate}"/>
	<#else>
		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>
	</#if>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="orderOption" id="orderOption" value="${parameters.orderOption?if_exists}"/>
	<input type="hidden" name="orderId" id="orderId" value="${orderId?if_exists}"/>  
	<br/>
</form>
</div>
</div>
<div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Entries Made by ${parameters.userLogin.userLoginId} Today: ${entrySize?if_exists}</#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:75px;"></div>		
</div>  
</div>

<div class="righthalf">
	<div class="screenlet-body">
    <div class="grid-header" style="width:100%">
		<label>Parlour Order Entry </label><span id="totalAmount"></span>
	</div>
	<div id="myGrid1" style="width:100%;height:350px;"></div>
	<br>
	<#if parameters.boothId?exists>					
    <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>processOrderEntryNew</@ofbizUrl>');"/>
    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>OrderEntryInit</@ofbizUrl>');"/>   	
    </div>     
	</#if> 
</div>     
</div>
</div>