<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

<script type="text/javascript">

$(document).ready(function(){

   		
   		
   		/*
   		var productCategorySelectIds = ${StringUtil.wrapString(productCategoryJSON)!'[]'};
		$("#prodCatId").val(productCategorySelectIds);
		$("#prodCatId").multiselect("refresh");
		*/

		$( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			minDate: new Date(),
			maxDate: 14,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate);
			}
		});
		$( "#chequeDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
		<#if changeFlag?exists && changeFlag=='AdhocSaleNew'>
			$("#boothId").autocomplete({ source: boothAutoJson }).keydown(function(e){
    	<#else>
		 	$("#partyId").autocomplete({ source: partyAutoJson }).keydown(function(e){ 
		</#if>
			if (e.keyCode === 13){
		      	 $('#boothId').autocomplete('close');
	    			$('#indententryinit').submit();
	    			return false;   
			}
		});
		
		
		var productStoreObjOnload=$('#productStoreIdFrom');
		//alert("====productStoreObjOnload===="+productStoreObjOnload.val());
			if (productStoreObjOnload != null && productStoreObjOnload.val() != undefined ){
			showStoreCatalog(productStoreObjOnload);
			}
		
		
	});
	
	var partyName;
	function dispSuppName(selection){
	   var value = $("#partyId").val();
	   partyName = partyNameObj[value];
	   $("#partyName").html("<h4>"+partyName+"</h4>");
	}   
	
	
	var globalCatgoryOptionList=[];
	 var catagoryList=[];
	function showStoreCatalog(productStoreObj) {
		
	var productStoreId=$(productStoreObj).val();
		
       var dataString="productStoreId=" + productStoreId ;
      $.ajax({
             type: "POST",
             url: "getDepotStoreCatalogCatagory",
           	 data: dataString ,
           	 dataType: 'json',
           	 async: false,
        	 success: function(result) {
              if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
       	  		 alert(result["_ERROR_MESSAGE_"]);
          			}else{
       	  				  catagoryList =result["catagoryList"];
       	  				  var catgoryOptionList=[];
       	  				 
       	  				 // alert("catagoryList=========="+catagoryList);
       	  					if(catagoryList != undefined && catagoryList != ""){
								$.each(catagoryList, function(key, item){
								if(item.value=="BYPROD"){
									catgoryOptionList.push('<option value="'+item.value+'" selected="selected">' +item.text+'</option>');
								}else{
								 catgoryOptionList.push('<option value="'+item.value+'">' +item.text+'</option>');
								 }
									});
				           }
				           $('#productCatageoryId').html(catgoryOptionList.join(''));   
					            $("#productCatageoryId").multiselect({
					   			minWidth : 180,
					   			height: 100,
					   			selectedList: 4,
					   			show: ["bounce", 100],
					   			position: {
					      			my: 'left bottom',
					      			at: 'left top'
					      		}
					   		});
					   		 $("#productCatageoryId").multiselect("refresh");
				          // alert("==globalCatgoryOptionList=="+globalCatgoryOptionList);
				         
      	 	 
      			}
               
          	} ,
         	 error: function() {
          	 	alert(result["_ERROR_MESSAGE_"]);
         	 }
          }); 
           
     }
     
  function validateParty()
  {
  	if(indententryinit.partyId.value.length < 1)
  		{
  			alert("Party ID is Mandatory");
  			 indententryinit.isFormSubmitted.value="";
  		}
  	if(indententryinit.productCatageoryId.value == "")
  		{
  			alert("Product Category is Mandatory");
  			indententryinit.isFormSubmitted.value="";
  		}
  }
 
</script>
<#assign changeRowTitle = "Changes">                

<#include "indentAdhocDepotInc.ftl"/>
<#include "EditUDPPriceDepot.ftl"/>
<#--
<#assign initAction =''>	
	<#if changeFlag?exists && changeFlag=='supplDeliverySchedule'>
 		<#assign initAction='SupplDeliveryScheduleNew'>
 	<#elseif changeFlag?exists && changeFlag=='ByProdGatePass'>
 		<#assign initAction='byProdGatePassNew'>	
 	<#else>
 		<#assign initAction='IndentEntryNew'>	 	
	</#if>	
-->
					

<div class="full">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
         <div class="grid-header" style="width:100%">
			<label>Depot Sales Entry </label>
		</div>
		<!-- 
         <#if orderStatus?exists && orderStatus?has_content>
         	<li><a  target="_blank" href="<@ofbizUrl>invoiceDeliveryNote?facilityId=${orderRoute}&&estimatedShipDate=${defaultEffectiveDate}&&orderBooth=${orderFacility}&&shipmentTypeId=BYPRODUCTS</@ofbizUrl>" >Invoice[${orderFacility}][${orderRoute}]</a></li>
         </#if>
         <#if changeFlag?exists && changeFlag!='supplDeliverySchedule'>
 			 <li><a  target="_blank" href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=indentEntry&&all=Y</@ofbizUrl>" >All Check List</a></li>
        	 <li><a  target="_blank" href="<@ofbizUrl>checkListReport.pdf?userLoginId=${userLogin.get("userLoginId")}&&checkListType=indentEntry</@ofbizUrl>">My Check List</a></li>
		</#if>
         </ui>         
         </h3> -->
         
     </div>
      
    <div class="screenlet-body">
  
    <#if changeFlag?exists && changeFlag=='DepotSales'>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>DepotSalesOrder</@ofbizUrl>" id="indententryinit" onsubmit="validateParty()">  
    <#else>
    	<form method="post" name="indententryinit" action="<@ofbizUrl>AdhocSaleNewMm</@ofbizUrl>" id="indententryinit">  
    </#if>
	
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        	<td>&nbsp;<input type="hidden" name="productSubscriptionTypeId"  value="CASH" />
		      	<input type="hidden" name="isFormSubmitted"  value="YES" />
		      	<input type="hidden" name="changeFlag"  value="${changeFlag?if_exists}" />
			      <#if changeFlag?exists && changeFlag=="EditDepotSales">
					 <input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
					 <input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="DEPOT_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="DEPOT_CHANNEL"/>
				  </#if>
		        <#if changeFlag?exists && changeFlag=='DepotSales'>
		         	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="DEPOT_SHIPMENT"/> 
		           	<input type="hidden" name="salesChannel" id="salesChannel" value="DEPOT_CHANNEL"/>
		        <#else>
		          	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="RM_DIRECT_SHIPMENT"/>
		          	<input type="hidden" name="salesChannel" id="salesChannel" value="RM_DIRECT_CHANNEL"/>
		        </#if>
           	</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Indent Date:</div></td>
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
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'><#if changeFlag?exists && changeFlag=='AdhocSaleNew'>Retailer:<#elseif changeFlag?exists && changeFlag=='InterUnitTransferSale'>KMF Unit ID:<#else>Party:</#if><font color="red">*</font></div></td>
          <td>&nbsp;</td>
          
        <#if changeFlag?exists && changeFlag=='EditDepotSales'>
			<#if partyId?exists && partyId?has_content>  
	  	  		<input type="hidden" name="partyId" id="partyId" value="${partyId?if_exists}"/>  
          		<td valign='middle'>
            		<div class='tabletext h3'>
               			${partyId} [ ${partyName?if_exists} ] ${partyAddress?if_exists}  <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
            		</div>
          		</td>       
          	</#if>
    	<#else>
		 	<#if party?exists && party?has_content>  
	  	  		<input type="hidden" name="partyId" id="partyId" value="${party.partyId.toUpperCase()}"/>  
          		<td valign='middle'>
            		<div class='tabletext h2'>
               			${party.partyId.toUpperCase()} [ ${party.groupName?if_exists} ] ${partyAddress?if_exists} <#--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="javascript:processChangeIndentParty()" class="buttontext">Party Change</a>-->             
            		</div>
          		</td>       
       		<#else>               
          		<td valign='middle'>
          			<input type="text" name="partyId" id="partyId" onblur= 'javascript:dispSuppName(this);' />
          			 <span class="tooltip" id="partyName"></span>
          			 <span class="tooltip">Input party code and press Enter</span>
          		</td>
          	</#if>
        </#if>
        </tr>
        <tr><td><br/></td></tr>
        <#if changeFlag?exists && changeFlag != "EditDepotSales">
	      	<tr>
	      		<td>&nbsp;</td>
	      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>PO Number:</div></td>
	      		<td>&nbsp;</td>
	   			<#if PONumber?exists && PONumber?has_content>  
	  	  			<input type="hidden" name="PONumber" id="PONumber" value="${PONumber?if_exists}"/>  
	      			<td valign='middle'>
	        			<div class='tabletext h3'>${PONumber?if_exists}</div>
	      			</td>       	
	   			<#else>      	         
	      			<td valign='middle'>
	      				<input type="text" name="PONumber" id="PONumber" />    
	      			</td>
	   			</#if>
	    	</tr>
	    	<#if changeFlag?exists && changeFlag == "IcpSales">
		    	<tr><td><br/></td></tr>
		    	<tr>
		      		<td>&nbsp;</td>
		      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Promotion Adj. Amt:</div></td>
		      		<td>&nbsp;</td>
		   			<#if promotionAdj?exists && promotionAdj?has_content>  
		  	  			<input type="hidden" name="promotionAdj" id="promotionAdj" value="${promotionAdj?if_exists}"/>  
		      			<td valign='middle'>
		        			<div class='tabletext h3'>Rs. ${promotionAdj?if_exists}</div>
		      			</td>       	
		   			<#else>      	         
		      			<td valign='middle'>
		      				<input type="text" name="promotionAdj" id="promotionAdj" />    
		         			<span class="tooltip">Fill Promotion Adjustment Amount if applicable</span>       
		      			</td>
		   			</#if>
		    	</tr>
	    	</#if>
	    	 <tr><td><br/></td></tr>
		        <tr>
		      		<td>&nbsp;</td>
		      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Sale From Store:</div></td>
		      		<td>&nbsp;</td>
		   			<#if productStoreId?exists && productStoreId?has_content>  
		  	  			<input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId?if_exists}"/>  
		      			<td valign='middle'>
		        			<div class='tabletext h3'>${productStoreId?if_exists}</div>
		      			</td>       	
		   			<#else>      	         
		      			<td valign='middle'>
		      				<select name="productStoreIdFrom" id="productStoreIdFrom"   onchange="javascript:showStoreCatalog(this)"  class='h2'>
		      					<option value="1003"> STORE </option>
		      				</select>
		      			</td>
		   			</#if>
		        </tr>
	    	   <tr><td><br/></td></tr>
		    	<tr>
		      		<td>&nbsp;</td>
		      		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Product Category:<font color="red">*</font></div></td>
		      		<td>&nbsp;</td>
		      		<td>
		      		<#if productCategoryId?has_content>
		      			<div class='tabletext h3'>${productCategoryId?if_exists}</div>
		      		<#else>
		      			<select id="productCatageoryId" name="productCatageoryId" class='h4' multiple="multiple" >
						</select>
		      		</#if>
					</td>
				</tr>
		    	<tr><td><br/></td></tr>
    	</#if>
        	<#if changeFlag?exists && changeFlag !='EditDepotSales' && changeFlag !='ICPTransferSale'>
        		<tr>
	          		<td>&nbsp;</td>
	          		<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Order Tax Type:</div></td>
	          		<td>&nbsp;</td>
	       			<#if orderTaxType?exists && orderTaxType?has_content>  
		  	  			<input type="hidden" name="orderTaxType" id="orderTaxType" value="${orderTaxType?if_exists}"/>  
	          			<td valign='middle'>
	            			<div class='tabletext h3'>${orderTaxType?if_exists}</div>
	          			</td>       	
	       			<#else>      	         
	          			<td valign='middle'>
	          				<select name="orderTaxType" id="orderTaxType" class='h2'>
	          					<option value="INTRA">With in State</option>
	          					<option value="INTER">Out of State</option>
	          				</select>
	          			</td>
	       			</#if>
        		</tr>
        	</#if>
		
		<#-- Order Message Field Starts -->
		<#if changeFlag?exists && !(changeFlag=='AdhocSaleNew')>
		
        <tr><td><br/></td></tr>
        <#if parameters.orderMessage?exists && parameters.orderMessage?has_content>  
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Message:</div></td>
          <td>&nbsp;</td>
	  	  		<input type="hidden" name="orderMessage" id="orderMessage" value="${parameters.orderMessage?if_exists}"/>  
          		<td valign='middle'>
            		<div class='tabletext h3'>
               			${parameters.orderMessage?if_exists}              
            	</div>
          	</td> 
         </tr>
        <#elseif orderMessage?exists && orderMessage?has_content> 
         <tr>
         <td colspan="4" >
         <table  border="0" cellspacing="0" cellpadding="0">
         <tbody>
            <tr>
                <td width="20%" align="right" class="label labelFontCSS">&nbsp; Message: &nbsp; &nbsp; &nbsp; &nbsp;</td>
                <td width="50%"><textarea name="orderMessage" id="orderMessage" cols="25" rows="4">${orderMessage?if_exists}</textarea></td>
            </tr>
         </tbody>
         </table>
		</td> 
        </tr> 
        <#else> 
        <tr>
         <td colspan="4" >
         <table  border="0" cellspacing="0" cellpadding="0">
         <tbody>
            <tr>
                <td width="20%" align="right" class="label labelFontCSS">&nbsp; Message: &nbsp; &nbsp; &nbsp; &nbsp;</td>
                <td width="50%"><textarea name="orderMessage" id="orderMessage"  cols="25" rows="4"></textarea></td>
            </tr>
         </tbody>
         </table>
		</td> 
        </tr> 
        </#if>
        </#if>
       
         <tr><td><input type="hidden" name="disableAcctgFlag" id="disableAcctgFlag" value="${disableAcctgFlag?if_exists}"/><br/></td></tr>    
                   
      </table>
      <div id="sOFieldsDiv" >
      </div> 
</form>
<br/>
<form method="post" id="indententry" action="<@ofbizUrl>IndentEntryInit</@ofbizUrl>">  
	<input type="hidden" name="effectiveDate" id="effectiveDate" value="${parameters.effectiveDate?if_exists}"/>
	<input type="hidden" name="boothId" id="boothId" value="${parameters.boothId?if_exists}"/>
	<input type="hidden" name="productSubscriptionTypeId" id="productSubscriptionTypeId" value="${parameters.productSubscriptionTypeId?if_exists}"/>   	   	   	   
	<input type="hidden" name="subscriptionTypeId" id="subscriptionTypeId" value="${parameters.subscriptionTypeId?if_exists}"/>
	<input type="hidden" name="destinationFacilityId" id="destinationFacilityId" value="${parameters.destinationFacilityId?if_exists}"/>
	<input type="hidden" name="shipmentTypeId" id="shipmentTypeId" value="${parameters.shipmentTypeId?if_exists}"/>
	<input type="hidden" name="vehicleId" id="vehicleId" value="${parameters.vehicleId?if_exists}"/>
	<input type="hidden" name="salesChannel" id="salesChannel" value="${parameters.salesChannel?if_exists}"/>
	<input type="hidden" name="billToCustomer" id="billToCustomer" value="${parameters.billToCustomer?if_exists}"/>
	<br>
</form>
    </div>
</div>

<#if changeFlag?exists && changeFlag=='AdhocSaleNew'>			
	<div class="screenlet">
	    <div class="screenlet-body">
	 		<div class="grid-header" style="width:100%">
				<label>Last Change <#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Entries Made by ${parameters.userLogin.userLoginId} Today: ${entrySize?if_exists}</#if></label>
			</div>
			<div id="myGrid2" style="width:100%;height:75px;"></div>		
	    </div>
	</div>
</#if>     
</div>

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<span id="totalAmount"></span>
		</div>
			<div id="myGrid1" style="width:100%;height:350px;"></div>
			  
			<#assign formAction =''>			
		    <#if changeFlag?exists && changeFlag=='AdhocSaleNew'>
		 		<#assign formAction='processAdhocSaleMm'>
		    <#elseif changeFlag?exists && changeFlag=='DepotSales'>
		         <#assign formAction='processDepotSalesOrder'>
		    <#elseif changeFlag?exists && changeFlag=='EditDepotSales'>
		         <#assign formAction='processDepotSalesOrder'>     
		 	
		 	<#else>
				<#assign formAction='processIcpSaleMm'>		 					 	
			</#if>				
			
	<#if booth?exists || party?exists || partyId?exists >
 		    <div class="screenlet-title-bar">
					<div class="grid-header" style="width:35%">
						<label>Other Charges</label><span id="totalAmount"></span>
					</div>
					<div id="myGrid2" style="width:35%;height:150px;">
						<div class="grid-header" style="width:35%">
						</div>
					</div>
				</div>	
    	<div align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>${formAction}</@ofbizUrl>');"/>
    		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    		<input type="submit" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:processIndentEntry('indententry','<@ofbizUrl>DepotSalesOrder</@ofbizUrl>');"/>   	
    	</div>     
	</#if>  
	</div>
</div>     
</div>
 	

</div>
 
