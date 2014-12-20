<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<script type="text/javascript">

	$(document).ready(function(){
	
		$("#prodCatId").multiselect({
			minWidth : 180,
			height: 100,
			selectedList: 4,
			show: ["bounce", 100],
			position: {
				my: 'left bottom',
				at: 'left top'
			}
		});
	   		
		//var productCategorySelectIds = ${StringUtil.wrapString(productCategoryJSON)!'[]'};
		//$("#prodCatId").val(productCategorySelectIds);
		//$("#prodCatId").multiselect("refresh");
		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		$( "#requiredDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#requiredDate" ).datepicker("option", selectedDate);
			}
		});
		
		$('#ui-datepicker-div').css('clip', 'auto');
		
		 	$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){ 
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
	    			$('#indententryinit').submit();
	    			return false;   
			}
		});
	});
		
	//$("#prodCatId").multiselect({
		//selectedText: "# of # selected"
	//});
</script>

<#assign changeRowTitle = "Changes">                
<#include "RequestEntryInc.ftl"/>

<div class="full">
	<div class="lefthalf">
		<div class="screenlet">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Indent Header </label>
				</div>
		     </div>
      
    		<div class="screenlet-body">
     
      			<form method="post" name="indententryinit" action="<@ofbizUrl>CustRequestEntry</@ofbizUrl>" id="indententryinit">  
			    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
      
				        <tr>
				        	<td>
						      	<input type="hidden" name="isFormSubmitted"  value="YES" />
				           	</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.MaterialMangement_CustRequestDate}:</div></td>
					        <td>&nbsp;</td>
					        <#if effectiveDate?exists && effectiveDate?has_content>  
						  		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${effectiveDate}         
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
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Required Date:</div></td>
					        <td>&nbsp;</td>
					        <#if requiredDate?exists && requiredDate?has_content>  
						  		<input type="hidden" name="requiredDate" id="requiredDate" value="${requiredDate}"/>
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${requiredDate}         
					            	</div>
					          	</td> 
					         <#else> 
					        	<td valign='middle'>          
					            	<input class='h2' type="text" name="requiredDate" id="requiredDate" value="${defaultEffectiveDate}"/>           		
					            </td> 	      
					       	  </#if>
					  	</tr>
    					<tr><td><br/></td></tr>
    					<tr>
				      		<td>&nbsp;</td>
				      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Material Category:</div></td>
				      		<td>&nbsp;</td>
				      		<td>
				      		<#if productCategoryId?has_content>
				      			<div class='tabletext h3'>${productCategoryId?if_exists}</div>
				      		<#else>
				      			<select id="prodCatId" name="productCatageoryId" class='h4' multiple="multiple" >
				      				<#if categoryList?has_content><#list categoryList as eachCategory><option value='${eachCategory.productCategoryId?if_exists}'>${eachCategory.description?if_exists}</option></#list></#if>
								</select>
				      		</#if>
							</td>
						</tr>
          				
        				<tr><td><br/></td></tr>
        				<tr>
				      		<td>&nbsp;</td>
				      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.MaterialMangement_CustRequestName}</div></td>
				      		<td>&nbsp;</td>
				      		<td>
				      		<#if custRequestName?has_content>
				      			<div class='tabletext h3'>${custRequestName?if_exists}</div>
				      		<#else>
				      			<input type="text" name="custRequestName" id="custRequestName" />
				      		</#if>
							</td>
						</tr>
						
						<tr><td><br/></td></tr>
						
				        <tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.MaterialMangement_CustRequestFromPartyId}</div></td>
				          <td>&nbsp;</td>
				          <#if party?exists && party?has_content>  
					  	  		<input type="hidden" name="partyId" id="partyId" value="${party.partyId.toUpperCase()}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h2'>
				               			${party.partyId.toUpperCase()} [ ${party.groupName?if_exists} ] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
				            		</div>
				          		</td>       
				       		<#else>               
				          		<td valign='middle'>
				          			<input type="text" name="partyId" id="partyId" />
				          			 <span class="tooltip">Input party code and press Enter</span>
				          		</td>
				          	</#if>
				        </tr> 
				        
				        <tr><td><br/></td></tr>    
      				</table>
      				<div id="sOFieldsDiv" >
      				</div> 
				</form>
				<br/>
				
				<form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
					<input type="hidden" name="requestDate" id="requestDate" value="${parameters.effectiveDate?if_exists}"/>
					<input type="hidden" name="responseDate" id="responseDate" value="${parameters.requiredDate?if_exists}"/>
					<input type="hidden" name="custRequestTypeId" id="custRequestTypeId" value="PRODUCT_REQUIREMENT"/>
					<input type="hidden" name="custRequestName" id="custRequestName" value="${parameters.custRequestName?if_exists}"/>
				</form>
    		</div>
		</div>
	</div>

	<div class="righthalf">
		<div class="screenlet">
    		<div class="screenlet-body">
		 		<div class="grid-header" style="width:100%">
		 			<label>Indent Items</label>
				</div>
				<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
				<#assign formAction ='processCustRequestItems'>			
				<#if booth?exists || party?exists>
			    	<div align="center">
			    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>CustRequestEntry</@ofbizUrl>');"/>   	
			    	</div>     
				</#if>  
			</div>
		</div>     
	</div>
</div>
