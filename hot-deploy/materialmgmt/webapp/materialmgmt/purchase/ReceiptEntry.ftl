<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<script type="text/javascript">

	$(document).ready(function(){
	
		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		$( "#suppInvoiceDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		suppInvoiceDate
		$('#ui-datepicker-div').css('clip', 'auto');
		
			$("#suppInvoiceId").keydown(function(e){ 
			if (e.keyCode === 13){
    			$('#indententryinit').submit();
    			return false;   
			}
		});
	});
		
</script>

<#assign changeRowTitle = "Changes">                
<#include "ReceiptEntryInc.ftl"/>

<div class="full">
	<div class="lefthalf">
		<div class="screenlet">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Receipt Header </label>
				</div>
		     </div>
      
    		<div class="screenlet-body">
     
      			<form method="post" name="indententryinit" action="<@ofbizUrl>POMaterialReceipts</@ofbizUrl>" id="indententryinit">  
			    	<table width="100%" border="0" cellspacing="0" cellpadding="0">
				        <tr>
				        	<td>
						      	<input type="hidden" name="isFormSubmitted"  value="YES" />
				           	</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Receipt Date:</div></td>
					        <td>&nbsp;</td>
					        <#if effectiveDate?exists && effectiveDate?has_content>  
						  		<input type="hidden" name="effectiveDate" id="effectiveDate" value="${effectiveDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${effectiveDate}         
					            	</div>
					          	</td>       
					       	<#else> 
					        	<td valign='middle'>          
					            	<input class='h3' type="text" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDate}"/>           		
					            </td>
					       	  </#if>
					  	</tr>
	    				<tr><td><br/></td></tr>
		 				<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Purchase Order Id:</div></td>
				          <td>&nbsp;</td>
				          <#if orderId?exists && orderId?has_content>  
					  	  		<input type="hidden" name="orderId" id="orderId" value="${orderId}"/>  
				          		<td valign='middle'>
				            		<div class='tabletext h3'>
				               			${orderId}             
				            		</div>
				          		</td>       
				       		<#else>
				          		<td valign='middle' class='tabletext h3'>
				          		   <#if !(withoutPO?exists && withoutPO?has_content)>
				          				<input type="text" name="orderId" id="orderId" />
				          				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				          			   Without PO:<input type="checkbox" name="withoutPO" id="withoutPO" value="Y" onclick="toggleSupplier(this)"/>
				          			 <#else>
				          			   NO PO
				          			   <input type="hidden" name="withoutPO" id="withoutPO" value="${withoutPO}"/>
				          			</#if>
				          			
				          		</td>
				          	</#if>
				        </tr>
				        
				          
				      		<tr><td><br/></td></tr>
				      		<tr class='h3' id="supplierDiv" style="display:none">
				      			<td>&nbsp;</td>
				      			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier:</div></td>
				      			<td>
				      				&nbsp;
				      			</td>
				      			<td>&nbsp;<input type="text" name="supplierId" id="supplierId" ></td>
				      		</tr>
				       	
				      	<tr><td><br/></td></tr>
				      	<#if supplierId?has_content>
				      		<tr><td><br/></td></tr>
				      		<tr>
				      			<td>&nbsp;</td>
				      			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier:</div></td>
				      			
				      			<td>&nbsp;<input type="hidden" name="supplierId" id="supplierId" value="${supplierId}"></td>
				      			<td>
				      				<div class='tabletext h3'>${supplierName?if_exists} [${supplierId}]</div>
				      			</td>
				      		</tr>
				      		
				      		<tr><td><br/></td></tr>
				      	</#if>
						
						<tr>
							<td>&nbsp;</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Vehicle No:</div></td>
					        <td>&nbsp;</td>
					        <#if vehicleId?exists && vehicleId?has_content>  
						  		<input type="hidden" name="vehicleId" id="vehicleId" value="${vehicleId}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${vehicleId}         
					            	</div>
					          	</td>       
					       	<#else> 
					        	<td valign='middle'>          
					            	<input class='h3' type="text" name="vehicleId" id="vehicleId"/>           		
					            </td>
				       	  </#if>
					  	</tr>
					  	<tr><td><br/></td></tr>
					  	<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier Invoice Date:</div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.suppInvoiceDate)?exists && (parameters.suppInvoiceDate)?has_content> 
				                 <input type="hidden" name="suppInvoiceDate" id="suppInvoiceDate" value="${parameters.suppInvoiceDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.suppInvoiceDate}         
					            	</div>
					          	</td>
				             <#else> 
				              <td valign='middle'>
		          				<input type="text" name="suppInvoiceDate" id="suppInvoiceDate" />
		          			</td>
				          </#if>
		          			
				        </tr>
						<tr><td><br/></td></tr>
		 				<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier Invoice No:</div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.suppInvoiceDate)?exists && (parameters.suppInvoiceDate)?has_content> 
				          		<input type="hidden" name="suppInvoiceId" id="suppInvoiceId" value="${parameters.suppInvoiceId}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.suppInvoiceId}         
					            	</div>
					          	</td>
				         	 <#else> 
				         	 <td valign='middle'>
		          				<input type="text" name="suppInvoiceId" id="suppInvoiceId" />
		          				 <span class="tooltip">Input InvoiceId and press Enter</span>
		          			</td>
				          </#if>
		          			
				        </tr>
						<tr><td><br/></td></tr>
	
	    			</table>
				</form>
				<br/>
				
				<form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
					<input type="hidden" name="receiptDate" id="receiptDate" value="${parameters.effectiveDate?if_exists}"/>
				</form>
    		</div>
		</div>
	</div>

	<div class="righthalf">
		<div class="screenlet">
    		<div class="screenlet-body">
		 		<div class="grid-header" style="width:100%">
		 			<label>Receipt Items Entry</label>
				</div>
				<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
				<#assign formAction ='processReceiptItems'>			
				<#if orderId?exists>
			    	<div align="center">
			    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>POMaterialReceipts</@ofbizUrl>');"/>   	
			    	</div>     
				</#if>  
			</div>
		</div>     
	</div>
</div>
<script type="application/javascript">
	  function toggleSupplier(el){
		  if($(el).is(':checked')){
		     $("#supplierDiv").show();
		  }else{
		  	 $("#supplierDiv").hide();
		  }
		
		}
</script>