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

<#include "ReceiptEntryDepotTransIncDC.ftl"/>

<div class="full" style="width:100%">
	<div class="screenlet-title-bar">
 		<div class="grid-header" style="width:100%">
			<label><font color="green">Dispatch Header </font></label>
		</div>
    </div>
      
    <div class="screenlet-body">
     
    <#if shipmentDetail?has_content> 
    <form method="post" class="form-style-8" name="indententryinit" action="<@ofbizUrl>processEditGrnShipmentDC</@ofbizUrl>" id="indententryinit">  
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
	          <#if orderNo?exists && orderNo?has_content>  
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
	      	<tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Supplier Invoice Date: <font color='red'>*</font></div></td>
	          <td>&nbsp;</td>
	          <#if (shipmentDetail.supplierInvoiceDate)?exists && (shipmentDetail.supplierInvoiceDate)?has_content> 
	          	  <#assign supplierInvoiceDate = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDetail.supplierInvoiceDate, "dd MMMM, yyyy"))>
	              <td valign='middle'>
      				<input class="h3" type="text" name="suppInvoiceDate" id="suppInvoiceDate" value="${supplierInvoiceDate}" onmouseover="datetimepick()"/>
      				<input type="hidden" name="shipmentId" id="shipmentId" value="${shipmentDetail.shipmentId}"/>
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
	          <#if (shipmentDetail.supplierInvoiceId)?exists && (shipmentDetail.supplierInvoiceId)?has_content> 
	         	 <td valign='middle'>
      				<input class="h3" type="text" name="suppInvoiceId" id="suppInvoiceId" value="<#if (shipmentDetail.supplierInvoiceId)?has_content>${shipmentDetail.supplierInvoiceId}</#if>"/>
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
	          <#if (shipmentDetail.deliveryChallanDate)?exists && (shipmentDetail.deliveryChallanDate)?has_content> 
		          <#assign deliveryChallanDate = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDetail.deliveryChallanDate, "dd MMMM, yyyy"))> 
	              <td valign='middle'>
      				<input class="h3" type="text" name="deliveryChallanDate" readonly  id="deliveryChallanDate" onmouseover="datetimepick()" value="${deliveryChallanDate}" />
      			</td>
	          <#else>
	          	<td valign='middle'>
      				<input class="h3" type="text" name="deliveryChallanDate" readonly  id="deliveryChallanDate" onmouseover="datetimepick()" />
      			</td>
	          </#if>
      			
	        </tr>
	        <tr><td><br/></td></tr>
		  	<tr>
				<td>&nbsp;</td>
		        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>LR Number: </div></td>
		        <td>&nbsp;</td>
		        <#if (shipmentDetail.lrNumber)?exists && (shipmentDetail.lrNumber)?has_content>
		        	<td valign='middle'>          
		            	<input class='h3' type="text" name="lrNumber" id="lrNumber" value="${shipmentDetail.lrNumber}"/>           		
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
	          <#if (shipmentDetail.estimatedReadyDate)?exists && (shipmentDetail.estimatedReadyDate)?has_content> 
	              <#assign lrDate = (Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentDetail.estimatedReadyDate, "dd MMMM, yyyy"))> 
	              <td valign='middle'>
      				<input class="h3" type="text" name="lrDate" readonly  id="lrDate" onmouseover="datetimepick()" value="${lrDate}" />
      			</td>
	          <#else>
	          	<td valign='middle'>
      				<input class="h3" type="text" name="lrDate" readonly  id="lrDate" onmouseover="datetimepick()" />
      			</td>
	          </#if>
		          			
	        </tr>
		  	<tr>
				<td>&nbsp;</td>
		        <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Carrier/Courier Name: </div></td>
		        <td>&nbsp;</td>
		        <#if shipmentDetail.carrierName?exists && shipmentDetail.carrierName?has_content>  
		          	<td valign='middle'>
		            	<input class='h3' type="text" name="carrierName" id="carrierName" value="${shipmentDetail.carrierName}"/>  
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
		        <#if shipmentDetail.vehicleId?exists && shipmentDetail.vehicleId?has_content>
		        	<td valign='middle'>  
			  			<input class='h3' type="text" name="vehicleId" id="vehicleId" value="${shipmentDetail.vehicleId}"/>
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
              <#if shipmentDetail.estimatedShipCost?exists && shipmentDetail.estimatedShipCost?has_content>
		        	<td valign='middle'>  
			  			<input class='h3' type="text" name="freightCharges" id="freightCharges" value="${shipmentDetail.estimatedShipCost}"/>
			  		</td>  
		       	<#else> 
		        	<td valign='middle'>          
		            	<input class='h3' type="text" name="freightCharges" id="freightCharges"/>           		
		            </td>
	       	  </#if>
			<tr>
	          <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Remarks: </div></td>
	          <td>&nbsp;</td>
	          <#if (shipmentDetail.description)?exists && (shipmentDetail.description)?has_content> 
		         <td valign='middle'>
      				<input class="h3" type="text" name="remarks" id="remarks" value="${shipmentDetail.description}"/>
      			 </td>
	          <#else> 
	         	 <td valign='middle'>
      				<input class="h3" type="text" name="remarks" id="remarks" />
      			</td>
	          </#if>
      			
	        </tr>
	    	<tr>
    			<td>&nbsp;    </td>
				<td align="right">
					<input type="submit" class="smallSubmit" name="submitButton" value="Submit">
    			</td>
   			</tr>
				        
			</table>
			</td>
		</tr>
	</table>
	</form>
	<#else>
		Invalid Shipment Id
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
