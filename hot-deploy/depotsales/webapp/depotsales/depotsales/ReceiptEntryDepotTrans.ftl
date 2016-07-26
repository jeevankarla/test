<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<style type="text/css">
	.form-style-8{
		   	 max-width: 1500px;
		   	 max-height: 500px;
		   	 max-right: 10px;
		   	 margin-top: 10px;
			 margin-bottom: -15px;
		     padding: 15px;
		     box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		     border-radius: 20px;
		     border: 1px solid #305A72;
		}
		.form-style-7{
		   	 max-width: 1500px;
		   	 max-height: 500px;
		   	 max-right: 10px;
		   	 margin-top: 10px;
			 margin-bottom: -15px;
		     padding: 15px;
		     background-color: Thistle;
		     box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		     border-radius: 20px;
		     border: 1px solid #305A72;
		}
</style>	
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
  
 //$("#effectiveDate").datetimepicker({
//	dateFormat:'d MM, yy',
//	changeMonth: true,
//    minDate: startDateFrom,
   // maxDate: startDateTo
 //});	
$( "#effectiveDate" ).datepicker({
	dateFormat:'d MM, yy',
	changeMonth: true,
	 minDate: startDateFrom,
});	
$( "#suppInvoiceDate" ).datepicker({
	dateFormat:'d MM, yy',
	changeMonth: true,
	
});
$( "#deliveryChallanDate" ).datepicker({
	dateFormat:'d MM, yy',
	changeMonth: true,
	
});
$( "#lrDate" ).datepicker({
	dateFormat:'d MM, yy',
	changeMonth: true,
	
});


			
	$('#ui-datepicker-div').css('clip', 'auto');	
	  }
	
	//$(document).ready(function(){
	
		//$('#ui-datetimepicker-div').css('clip','auto');
							
		/*
		$('#ui-datepicker-div').css('clip', 'auto');
		
			$("#suppInvoiceId").keydown(function(e){ 
			if (e.keyCode === 13){
    			$('#indententryinit').submit();
    			return false;   
			}
		});
		*/
	//});
	
	var transporterJSON = ${StringUtil.wrapString(transporterJSON)!'[]'};
	
	
		$(document).ready(function(){
             $("#carrierName").autocomplete({ source: transporterJSON }).keydown(function(e){});     
		});
		
		
</script>

<#assign changeRowTitle = "Changes">                
<#include "ReceiptEntryDepotTransInc.ftl"/>

		<div class="full" style="width:100%">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<label><font color="green">Dispatch Header </font></label>
				</div>
		     </div>
      
    		<div class="screenlet-body">
     
      			<form method="post" class="form-style-8" name="indententryinit" action="<@ofbizUrl>SupplierDispatchEntry</@ofbizUrl>" id="indententryinit">  
			    	<table width="100%">
			    	<tr>
				        <td width="50%">
			    	<table  border="0" cellspacing="0" cellpadding="0">
				        <tr>
				        	<td>
						      	<input type="hidden" name="isFormSubmitted"  value="YES" />
				           	</td>
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
				               			<#if orderNo?has_content>${orderNo}<#else>${orderId}</#if>             
				            		</div>
				          		</td>       
				       		<#else>
				          		<td valign='middle' class='tabletext h3'>
				          		   <#if !(withoutPO?exists && withoutPO?has_content)>
				          				<input type="text" name="orderId" id="orderId" />
										<#assign flag = true>                
				          			</#if>
				          			</td>
				          		
				          	</#if>
				        </tr>
				      	<#if supplierId?has_content>
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
		          				<input class="h3" type="text" name="suppInvoiceDate" id="suppInvoiceDate" onmouseover="datetimepick()"/>
		          			</td>
				          </#if>
				        </tr>
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
		          				<input class="h3" type="text" name="suppInvoiceId" id="suppInvoiceId" />
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
		          				<input class="h3" type="text" name="deliveryChallanDate" readonly  id="deliveryChallanDate" onmouseover="datetimepick()" value="${defaultEffectiveDate}" />
		          			</td>
				          </#if>
		          			
				        </tr>
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
		          				<input class="h3" type="text" name="deliveryChallanNo" id="deliveryChallanNo" />
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
					  	<tr>
				          <td>&nbsp;</td>
				          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>LR Date: </div></td>
				          <td>&nbsp;</td>
				          <#if (parameters.lrDate)?exists && (parameters.lrDate)?has_content> 
				                 <input type="hidden" name="lrDate" id="lrDate" value="${parameters.lrDate}"/>  
					          	<td valign='middle'>
					            	<div class='tabletext h3'>${parameters.lrDate}         
					            	</div>
					          	</td>
				             <#else> 
				              <td valign='middle'>
		          				<input class="h3" type="text" name="lrDate" readonly  id="lrDate" onmouseover="datetimepick()" value="${defaultEffectiveDate}" />
		          			</td>
				          </#if>
		          			
				        </tr>
					  	<tr>
							<td>&nbsp;</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Carrier/Courier Name: </div></td>
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
					  	<tr>
							<td>&nbsp;</td>
					        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Vehicle No: <#--><font color='red'>*</font>--></div></td>
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
                        <tr>
                          <td>&nbsp;</td>
                          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Freight Charges: </div></td>
                           <td>&nbsp;</td>
                          <td valign='middle'>
                                  <input class="h3" type="text" name="freightCharges" id="freightCharges" />
                              </td>
                          </tr>
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
		          				<input class="h3" type="text" name="remarks" id="remarks" />
		          			</td>
				          </#if>
		          			
				        </tr>
				        <#--<tr>
				         	  <td>&nbsp;</td>
					          <td>&nbsp;</td>
					          <td>&nbsp;</td>
				         	 <td valign='middle'>
		          				<input type="submit" name="Submit"/>
		          			</td>
				        </tr>-->
				        <tr>
				         	 <td>
		          				<input type="hidden" name="hideQCflow" id="hideQCflow" value="N"/>  
				                <input type="hidden" name="allowedGraterthanTheOrdered" id="allowedGraterthanTheOrdered" value="N"/>  
		          			</td>
				        </tr>
				        
	    			</table>
	    			</td>
	    			<td border="1">
	    			<#if ShipmentReceipt?has_content>
	    				<table  class="form-style-7">
				      	  <tr>
				      	  
				      		  <td align="center" colspan="5">
				       			<font ><b><u> Shipment History</u></b></font>
				       		 </td>
	    				 </tr>
	    				 <tr>
	    				 	<td width="10%">
								<b>ShipmentId</b>	    						 
			    			</td>
	    					<td width="10%">
								<b>Product</b>	    						 
			    			</td>
			    			<td width="10%">
								<b>Accepted Quantity</b>    						 
			    			</td>
			    			<td width="10%">
			    				<b>Status</b>
			    			</td>
			    			<td width="10%">
			    				<b>Invoice No</b>
			    			</td>
			    			<td width="10%">
			    				<b>LR No</b>
			    			</td>
			    			<td width="10%">
								<b>Entry by </b>  						 
			    			</td>
	    				 </tr>
	    				 <#assign totalQty=0>
	    				 <#list ShipmentReceipt as eachShipment>
	    				 	  <#assign ShipmentDetail = delegator.findOne("Shipment", {"shipmentId" : eachShipment.get("shipmentId")}, true)?if_exists/>
	    				 
	    				 <tr>
	    				 	<td width="10%">
	    						 ${eachShipment.get("shipmentId")}
	    						 
			    			</td>
	    					<td width="10%">
	    						<#assign product = delegator.findOne("Product", {"productId" : eachShipment.get("productId")}, true)?if_exists/>
	    					
	    						 ${product.get("brandName")?if_exists}
	    						 
			    			</td>
			    			<td width="10%">
	    						 ${eachShipment.get("quantityAccepted")?if_exists}
	    						<#assign totalQty=totalQty+eachShipment.get("quantityAccepted")>
			    			</td>
			    			<td width="10%">
	    						<#if "SR_RECEIVED"==eachShipment.get("statusId")>Received<#else>${eachShipment.get("statusId")}</#if>
	    						 
			    			</td>
			    			<td width="10%">
	    						 ${ShipmentDetail.get("supplierInvoiceId")?if_exists}
	    						 
			    			</td>
			    			<td width="10%">
	    						 ${ShipmentDetail.get("lrNumber")?if_exists}
	    						 
			    			</td>
			    			
			    			<td width="10%">
	    						 ${eachShipment.get("receivedByUserLoginId")}
	    						 
			    			</td>
			    			
	    				 </tr>
	    				 </#list>
	    				  <tr>
	    				 	<td width="10%" colspan="4" align="center">
	    				 		<b>Total Quantity Shipped Till Now : ${totalQty?if_exists}</b>
	    				 	</td>
	    				  </tr>
	    				</table>
	    			</#if>
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

		<div class="screenlet">
    		<div class="screenlet-body">
		 		<div class="grid-header" style="width:100%">
		 		 <#if orderId?exists && orderId?has_content>
		 		           <#assign OrderHeaderDetails = delegator.findOne("OrderHeader", {"orderId" :orderId}, true)>
		 		
		 			<label id="grandTot"><font color="green">Dispatch Items Entry   &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; Order Value:</font> &#160;&#160;<font color="blue">${OrderHeaderDetails.grandTotal?if_exists?string("##0.00")}</font></label>
		 			<#else>
		 			<label>Dispatch Items Entry   </label>
		 			</#if>
				</div>
				<div id="myGrid1" style="width:100%;height:200px;"></div>
			  
				<#assign formAction ='processDepotTransReceiptItems'>			
				<#if orderId?exists>
			    	<div align="center">
			    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
			    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    		<input type="button" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:cancelForm();"/>   	
			    	</div>     
				</#if>  
			</div>
	</div>
<script type="application/javascript">
    var partyAutoJson = ${StringUtil.wrapString(supplierJSON)!'[]'};
    
  
   
   function cancelForm(){
   jQuery("#indententryinit").attr("action", "/depotsales/control/FindSupplierPO");
   jQuery("#indententryinit").submit();
   
   }
   
   
 
   
   
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
