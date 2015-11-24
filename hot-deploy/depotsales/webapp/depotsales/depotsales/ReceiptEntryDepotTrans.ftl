<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />

<script type="text/javascript">
	
	function datetimepick(){
	
	//$("#effectiveDate").datetimepicker({
	//		dateFormat:'dd:mm:yy',
	//		changeMonth: true,
	//		minDate:"#effectiveDate",
	//		numberOfMonths: 1,
	//	});	
		
 var currentTime = new Date();
 // First Date Of the month 
 var startDateFrom = new Date(currentTime.getFullYear(),currentTime.getMonth(),1);
 // Last Date Of the Month 
 var startDateTo = new Date(currentTime.getFullYear(),currentTime.getMonth() +1,0);
  
 $("#effectiveDate").datetimepicker({
	dateFormat:'dd:mm:yy',
	changeMonth: true,
    minDate: startDateFrom,
   // maxDate: startDateTo
 });	
			
	$('#ui-datepicker-div').css('clip', 'auto');	
	  }
	
	$(document).ready(function(){
	
		//$('#ui-datetimepicker-div').css('clip','auto');
			
		
		$( "#suppInvoiceDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			
		});
		$( "#deliveryChallanDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			
		});
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
<#include "ReceiptEntryDepotTransInc.ftl"/>

<div class="full">
	<div class="lefthalf">
		<div class="screenlet">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label>Receipt Header </label>
				</div>
		     </div>
      
    		<div class="screenlet-body">
     
      			<form method="post" name="indententryinit" action="<@ofbizUrl>SupplierDispatchEntry</@ofbizUrl>" id="indententryinit">  
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
					            	<input class='h3' type="text" name="effectiveDate" id="effectiveDate" onmouseover="datetimepick()" value="${defaulteffectiveDate}"/>           		
					            </td>
					       	  </#if>
					  	</tr>
	    				<tr><td><br/></td></tr>
	    				<tr>
						  <#assign flag = false>                
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3' id='purchaseId'>Purchase Order Id: <font color='red'>*</font></div></td>
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
										<#assign flag = true>                
				          			</#if>
				          			</td>
				          		<#--	<#if flag == true>
				          			<td class='tabletext h3'>
				          			 Without PO:<input type="checkbox" name="withoutPO" id="withoutPO" value="Y" onclick="toggleSupplier(this)"/>
				          			</td>
				          			<#else>
				          			<td>	NO PO
				          			   <input type="hidden" name="withoutPO" id="withoutPO" value="${withoutPO}"/>
				          			</td>
				          			</#if>-->
				          	</#if>
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
				      		<#else>
				      		 
				      		<tr class='h3' id="supplierDiv" style="display:none">
				      			<td>&nbsp;</td>
				      			<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier:</div></td>
				      			<td>
				      				&nbsp;
				      			</td>
				      			<td>&nbsp;<input type="text" name="supplierId" id="supplierId" ></td>
				      		</tr>
				      	</#if>
						<tr><td><br/></td></tr>
				      	<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier Invoice Date: <font color='red'>*</font></div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.suppInvoiceDate)?exists && (parameters.suppInvoiceDate)?has_content> 
				                 <input type="hidden" name="suppInvoiceDate" id="suppInvoiceDate" value="${parameters.suppInvoiceDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.suppInvoiceDate}         
					            	</div>
					          	</td>
				             <#else> 
				              <td valign='middle'>
		          				<input type="text" readOnly name="suppInvoiceDate" id="suppInvoiceDate" />
		          			</td>
				          </#if>
				        </tr>
						<tr><td><br/></td></tr>
						<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier Invoice No: <font color='red'>*</font></div></td>
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
		          			</td>
				          </#if>
				        </tr>
						<tr><td><br/></td></tr>
	    				<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Delivery Challan Date: </div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.deliveryChallanDate)?exists && (parameters.deliveryChallanDate)?has_content> 
				                 <input type="hidden" name="deliveryChallanDate" id="deliveryChallanDate" value="${parameters.deliveryChallanDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.deliveryChallanDate}         
					            	</div>
					          	</td>
				             <#else> 
				              <td valign='middle'>
		          				<input type="text" name="deliveryChallanDate" readonly  id="deliveryChallanDate" value="${defaultEffectiveDate}" />
		          			</td>
				          </#if>
		          			
				        </tr>
						<tr><td><br/></td></tr>
	    				<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Delivery Challan No: </div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.deliveryChallanNo)?exists && (parameters.deliveryChallanNo)?has_content> 
				          		<input type="hidden" name="deliveryChallanNo" id="deliveryChallanNo" value="${parameters.deliveryChallanNo}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.deliveryChallanNo}         
					            	</div>
					          	</td>
				         	 <#else> 
				         	 <td valign='middle'>
		          				<input type="text" name="deliveryChallanNo" id="deliveryChallanNo" />
		          			</td>
				          </#if>
		          			
				        </tr>
				        <tr><td><br/></td></tr>
						<tr>
							<td>&nbsp;</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Vehicle No: <font color='red'>*</font></div></td>
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
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>LR Number: </div></td>
					        <td>&nbsp;</td>
					        <#if lrNumber?exists && lrNumber?has_content>  
						  		<input type="hidden" name="lrNumber" id="lrNumber" value="${lrNumber}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${lrNumber}         
					            	</div>
					          	</td>       
					       	<#else> 
					        	<td valign='middle'>          
					            	<input class='h3' type="text" name="lrNumber" id="lrNumber"/>           		
					            </td>
				       	  </#if>
					  	</tr>
					  	<tr><td><br/></td></tr>
					  	<tr>
							<td>&nbsp;</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Carrier Name: </div></td>
					        <td>&nbsp;</td>
					        <#if carrierName?exists && carrierName?has_content>  
						  		<input type="hidden" name="carrierName" id="carrierName" value="${carrierName}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${carrierName}         
					            	</div>
					          	</td>       
					       	<#else> 
					        	<td valign='middle'>          
					            	<input class='h3' type="text" name="carrierName" id="carrierName"/>           		
					            </td>
				       	  </#if>
					  	</tr>
					  	<tr><td><br/></td></tr>
						<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Remarks: </div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.remarks)?exists && (parameters.remarks)?has_content> 
				          		<input type="hidden" name="remarks" id="remarks" value="${parameters.remarks}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.remarks}         
					            	</div>
					          	</td>
				         	 <#else> 
				         	 <td valign='middle'>
		          				<input type="text" name="remarks" id="remarks" />
		          			</td>
				          </#if>
		          			
				        </tr>
				        <tr><td><br/></td></tr>
				        <tr>
				         	  <td>&nbsp;</td>
					          <td>&nbsp;</td>
					          <td>&nbsp;</td>
				         	 <td valign='middle'>
		          				<input type="submit" name="Submit"/>
		          			</td>
				        </tr>
				        <tr>
				         	 <td>
		          				<input type="hidden" name="hideQCflow" id="hideQCflow" value="N"/>  
				                <input type="hidden" name="allowedGraterthanTheOrdered" id="allowedGraterthanTheOrdered" value="N"/>  
		          			</td>
				        </tr>
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
			  
				<#assign formAction ='processDepotTransReceiptItems'>			
				<#if orderId?exists>
			    	<div align="center">
			    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>DepotTransMaterialReceipts</@ofbizUrl>');"/>   	
			    	</div>     
				</#if>  
			</div>
		</div>     
	</div>
</div>
<script type="application/javascript">
    var partyAutoJson = ${StringUtil.wrapString(supplierJSON)!'[]'};
   
	  function toggleSupplier(el){
	      $("#supplierId").autocomplete({ source: partyAutoJson }).keydown(function(e){});
		  if($(el).is(':checked')){
		     $("#supplierDiv").show();
		     $("#purchaseId").hide();
		     $("#orderId").hide();
		     
		  }else{
		  	 $("#supplierDiv").hide();
		     $("#purchaseId").show();
		     $("#orderId").show();
		  }
		
		}
</script>
