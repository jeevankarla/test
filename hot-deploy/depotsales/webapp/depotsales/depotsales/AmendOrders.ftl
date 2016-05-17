	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
	<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />
	
	
	<style type="text/css">
	.slick-header-column.ui-state-default {
    background:none ;
    background-color: #738595 ;
    color: #eeeeee;  
    border: solid 1px;  
    padding: 0;
    text-shadow: none;
    font-family: Arial, Verdana, Helvetica, sans-serif;
    font-size: 13px;
    height: 30px;
    line-height: 40px;    
}
	</style>
	
	
		
	<#assign changeRowTitle = "Changes">   
	<#if (catType=="Silk" || catType=="Other")>
	
		<#if (orderType=="direct")>
			<#include "AmendSilkBranchSaleIndent.ftl"/>
		<#else>
			<#include "AmendMultiSilkBranchSalesIndent.ftl"/>
		
		</#if>
	<#else>
	<#if (orderType=="direct")>
		<#include "AmendCottonBranchSalesIndent.ftl"/>
		<#else>
			<#include "AmendMultiCottonBranchSaleIndent.ftl"/>
		
		</#if>
		
	</#if>
	
<div class="full">
		<div class="screenlet-title-bar" >
			<div class="grid-header" style="width:100%" align="center">
					<label>Amend Indent </label>
				</div>
		    <div class="screenlet-body">
		    
		    <form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
		<input type="hidden" name="boothId" id="boothId" value="${parameters.partyId?if_exists}"/>
		<input type="hidden" name="partyId" id="partyId" value="${parameters.partyId?if_exists}"/>
		<input type="hidden" name="orderId" id="orderId" value="${parameters.orderId?if_exists}"/>   	   	   	   
		<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>
		
		<br>
	</form>    
		     <form name="indententryinit">
		    <table> <tr><td>
		    
		    <font color="green"><h5><b>OrderId :</b></h5></font></td><td> <font color="blue">${parameters.orderId}</font></td></tr></table>
		   
		    </form>
				<div id="myGrid1" style="width:100%;height:210px;"></div>
					<#assign formAction='amendPOItemEvent'>			
				    	<div align="center">
				    		<input type="submit" style="padding:.3em" id="changeSave" value="${uiLabelMap.CommonSubmit}" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
				    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>processOrdersBranchSales</@ofbizUrl>');"/>   	
				    	</div>     
					
				</div>
			</div>     
		</div>
		</div>
	
