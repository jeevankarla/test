<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations 
under the License.
-->
<script type="text/javascript">
//<![CDATA[
	var convProductJSON = ${StringUtil.wrapString(convProductJSON)!'[]'};
	var milkTransProdJson = ${StringUtil.wrapString(milkTransProdJson)!'[]'};
	$(document).ready(function() {	
		getConvertionProducts();
		setConvertionProduct();
	});
	function getConvertionProducts(){
		var tableObj = $('#_col table tr');
		$(tableObj).each(function(index, element){
		var purposeTypeObj = $(this).find("[name='purposeTypeId']");
		var purposeTypeId = $(purposeTypeObj).val();
		var productIdObj = $(this).find("[name='productId']");
		var productId = $(productIdObj).val();
		if(productId!= "undefined"){
		 	var optionList = '';
		 	if(purposeTypeId=="CONVERSION"){
		 		optionList += "<option value = " + "" + " >" +" "+ "</option>";
				var list= convProductJSON[productId];
				if (list) {		       				        	
		        	for(var i=0 ; i<list.length ; i++){
						var innerList=list[i];	 
		                optionList += "<option value = " + innerList['productId'] + " >" +innerList['productName']+" </option>";          			
		      		}//end of main list for loop
		      	}
		      	jQuery("[name='"+"productIdTo"+"']").html(optionList);
		 	}
		 	}
		});
	}
	function setConvertionProduct(){
		var tableObj = $('#_col table tr');
		$(tableObj).each(function(index, element){
			var purposeTypeObj = $(this).find("[name='purposeTypeId']");
			var purposeTypeId = $(purposeTypeObj).val();
			var milkTransferObj = $(this).find("[name='milkTransferId']");
			var productIdToObj = $(this).find("[name='productIdTo']");
            var milkTransferId = $(milkTransferObj).val();
            var productIdObj = $(this).find("[name='productId']");
			var productId = $(productIdObj).val();
			var optionList = '';
          //  alert("milkTransferId==============="+milkTransferId);
            if(purposeTypeId=="CONVERSION"){
            	optionList += "<option value = " + "" + " >" +" "+ "</option>";
            	var productIdTo = milkTransProdJson[milkTransferId];
            //	alert("productIdTo==============="+productIdTo);
            	var list= convProductJSON[productId];
				if (list) {		       				        	
		        	for(var i=0 ; i<list.length ; i++){
						var innerList=list[i];	 
		                optionList += "<option value = " + innerList['productId'] + " >" +innerList['productName']+" </option>";          			
		      		}//end of main list for loop
		      	}
		      	$(productIdToObj).html(optionList);
            	if(typeof(productIdTo)!='undefined' && productIdTo!='' && productIdTo != null ){
            	    $(productIdToObj).val(''+productIdTo);   
            		$(productIdToObj).attr("selected", "selected");
            	}
            }else{
            	var optionList = '';
            	$(productIdToObj).html(optionList);
            }
		});
	}
	function getConvProduct(purposeTypeId,productIdStr,index){
		var purposeTypeId = purposeTypeId.value;
        var productId = productIdStr;
        var index=index;
        if(productId!= "undefined"){
		 	var optionList = '';
		 	if(purposeTypeId=="CONVERSION"){
		 		optionList += "<option value = " + "" + " >" +" "+ "</option>";
				var list= convProductJSON[productId];
				if (list) {		       				        	
		        	for(var i=0 ; i<list.length ; i++){
						var innerList=list[i];	 
		                optionList += "<option value = " + innerList['productId'] + " >" +innerList['productName']+" </option>";          			
		      		}//end of main list for loop
		      	}
		     } 	
		     var productIdToIndex = "productIdTo_"+index;		      	
		     $("#"+productIdToIndex).html(optionList);
		      	setConvertionProduct();
		}
	}
	function toggleRequestId(master) {
        var receipts = jQuery("#listMilkReceipts :checkbox[name='receiptCheckBoxId']");
        jQuery.each(receipts, function() {
            this.checked = master.checked;
        });
    }
    
    function processReceipt(current){
        jQuery(current).attr( "disabled", "disabled");
        var actionFlag = $(current).val();
        $("#submitButton").hide();
        var receipts = jQuery("#listMilkReceipts :checkbox[name='receiptCheckBoxId']");
        var index = 0;
        var submitCount=0;
        jQuery.each(receipts, function() {
            if (jQuery(this).is(':checked')) {
                var domObj = $(this).parent().parent();
                var milkTransferObj = $(domObj).find("[name='milkTransferId']");
                var milkTransferId = $(milkTransferObj).val();
                if(actionFlag == "Finalize"){
                    var productIdObj = $(domObj).find("[name='productId']");
                    var productId = productIdObj.val();
                    var partyIdObj = $(domObj).find("[name='partyId']");
                    var partyId = partyIdObj.val();
                    var purposeObj = $(domObj).find("[name='purposeTypeId']");
                    var purposeTypeId = purposeObj.val();
                    var productIdToObj = $(domObj).find("[name='productIdTo']");
                    var productIdTo = productIdToObj.val();
                    var dcNoObj = $(domObj).find("[name='dcNo']");
                	var dcNo = $(dcNoObj).val();   
                	var dispatchQtyObj = $(domObj).find("[name='dispatchQty']");
                	var dispatchQty = $(dispatchQtyObj).val();   
                    if(purposeTypeId == "CONVERSION" && ((productIdTo == null) ||(productIdTo == ""))){
                        submitCount=submitCount+1;
                        alert("Please select conversion Product..!");
                        jQuery(current).attr( "disabled", false);
                        return false;
                    }
                    if(purposeTypeId == "CONVERSION"){
                        var appendProductIdToStr = "<input type=hidden name=productIdTo_o_"+index+" value="+productIdTo+" />";
                        $("#updateMilkTransferForm").append(appendProductIdToStr);
                    }
                    var appendProductIdStr = "<input type=hidden name=productId_o_"+index+" value="+productId+" />";
                    $("#updateMilkTransferForm").append(appendProductIdStr);
                    var appendProductIdStr = "<input type=hidden name=dcNo_o_"+index+" value="+dcNo+" />";
                    $("#updateMilkTransferForm").append(appendProductIdStr);
                    var appendProductIdStr = "<input type=hidden name=dispatchQty_o_"+index+" value="+dispatchQty+" />";
                    $("#updateMilkTransferForm").append(appendProductIdStr);
                    var appendPartyIsStr = "<input type=hidden name=partyId_o_"+index+" value="+partyId+" />";
                    $("#updateMilkTransferForm").append(appendPartyIsStr);
                    var purposeTypeIdStr = "<input type=hidden name=purposeTypeId_o_"+index+" value="+purposeTypeId+" />";
                    $("#updateMilkTransferForm").append(purposeTypeIdStr);
                }
                var appendStr = "<input type=hidden name=milkTransferId_o_"+index+" value="+milkTransferId+" />";
                $("#updateMilkTransferForm").append(appendStr);
                index = index+1;
            }
            
        });
        if(index == 0 && submitCount==0){
                alert("Please Select Record(s) To Process!.");
                jQuery(current).attr( "disabled", false);
                $("#submitButton").show();
                return false;
         }
         if(submitCount==0){
            jQuery('#updateMilkTransferForm').submit();
         }
    }
       
//]]>
</script>
<#if parameters.flag?has_content && parameters.flag == "APPROVE_RECEIPTS">
<#assign action="ApproveReceipts">
</#if>
<#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
<#assign action="FinalizeTankerReceipts">
</#if>
<form name="updateMilkTransferForm" id="updateMilkTransferForm" method="post" action=${action}>
	<#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
		<input  type="hidden" id="paramMilkTransferId" name="paramMilkTransferId" value='${parameters.milkTransferId?if_exists}'>
		<input  type="hidden" id="paramPartyId" name="paramPartyId" value='${parameters.partyId?if_exists}'>
		<input  type="hidden" id="paramVehicleId" name="paramVehicleId" value='${parameters.vehicleId?if_exists}'>
		<input  type="hidden" id="paramSiloId" name="paramSiloId" value='${parameters.siloId?if_exists}'>
		<input  type="hidden" id="paramProductId" name="paramProductId" value='${parameters.productId?if_exists}'>
		<input  type="hidden" id="paramFromDate" name="paramFromDate" value='${parameters.fromDate?if_exists}'>
    </#if>
</form>
<#if milkDetailslist?has_content>
  
  <form name="listMilkReceipts" id="listMilkReceipts"  method="post" >
  
    <div align="right" width="100%">
    	<table width="25%">
    		<tr>
    			<td></td>
    			<td></td>
				<#if parameters.flag?has_content && parameters.flag == "APPROVE_RECEIPTS">
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processReceipt(this);" value="Approve"</h2></td>
    			</#if>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
    			<td align="right"><h2><input id="submitButton" type="button"  onclick="javascript:processReceipt(this);" value="Finalize"</h2></td>
    			</#if>
    		</tr>
    	</table>
    </div>
 
    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td>Record No</td>
          <td>Vehicle Number</td>
          <td>Contractor</td>
          <td>Entry Date</td>
          <td>Dc No</td>
          <td>TareWeight  Date</td>
          <td>From Chilling Center - Union</td>
          <td>Milk Type</td>
          <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
          <td>Milk Used For</td>
          <td>Conv.Product</td>
          </#if>
          <td>Silo</td>
          <td>&#160;      Dispatch Qty(Kgs)</td>
          <td>&#160;      Received Qty(Kgs)</td>
          <td>&#160;      Fat %</td>
          <td>&#160;      SNF %</td>
          <td>&#160;      KgFat</td>
          <td>&#160;      KgSNF</td>
          <td>Status</td>
		  <td align="right" cell-padding>${uiLabelMap.CommonSelect} <input type="checkbox" id="checkAllReceipts" name="checkAllReceipts" onchange="javascript:toggleRequestId(this);"/></td>
        </tr>
      </thead>
      <tbody>
      <#assign alt_row = false>
      <#assign index = 0>
      <#list milkDetailslist as eachItem>	
			<#assign productIdStr = eachItem.productId>
      		<tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
				<input type=hidden name="milkTransferId" value='${eachItem.milkTransferId?if_exists}'>
				<input type=hidden name="productId" value='${eachItem.productId?if_exists}'>
              	<td>${eachItem.milkTransferId?if_exists}</td>
              	<td>${eachItem.containerId?if_exists}</td>
              	<td>${eachItem.contractorId?if_exists}</td>
				<td>${eachItem.vehicleEntryDate}</td>
              	 <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
             		<td><input type="text" size="10" maxlength="10" name="dcNo" id="dcNo"  value="${eachItem.dcNo?if_exists}" /></td>
              	 <#else>
              	   <td>${eachItem.dcNo?if_exists}</td>
              	 </#if>
              	 <#if eachItem.receiveDate?has_content>
				<td>${eachItem.receiveDate}</td>
				<#else>
                <td></td>
                </#if>
              	
              	
              	<#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : eachItem.partyId}, false))!>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <td> 
                <select name="partyId"  class='h4' >
                     <#assign existedParty = eachItem.partyId >
                    <#--><option value='${eachItem.partyId}'>${partyName.groupName?if_exists}</option>-->
			       <#list unionsList as union>  
			        <#if existedParty?has_content  && existedParty == union.partyId >
			       <option value='${eachItem.partyId}' selected='selected'>${union.groupName?if_exists}</option>
			           <#else>
			          	  <option value='${union.partyId}' >
				         ${union.groupName?if_exists}
				      </option>
				      </#if>
			       </#list>            
				</select>
				</td>
                <#else>  
                <td>${partyName.groupName?if_exists}[${eachItem.partyId}]</td>
                </#if>
              	
                <#assign product = (delegator.findOne("Product", {"productId" : eachItem.productId}, false))!>
             <#--  <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <td>  
                <select name="productId" class='h4' >
                   <#assign existedPurpose = eachItem.productId >
			       <#list productsList as product>    
			       <#if existedPurpose?has_content  && existedPurpose == product.productId >
			           <option value='${eachItem.productId}' selected='selected'>${product.description?if_exists}</option>
   			       <#else>
			          <option value='${product.productId}' >
				         ${product.description?if_exists}
				      </option>
				    </#if>
			       </#list>            
				</select>
				</td>
                <#else> -->
                <td>${product.description?if_exists}</td>
               <#-- </#if> -->
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
                <#assign enumeration = delegator.findOne("Enumeration",{"enumId":eachItem.purposeTypeId},false)>
                <#assign existedPurpose = eachItem.purposeTypeId>
                <td>
                	<select name="purposeTypeId" class='h4' onchange="javascript:getConvProduct(this,${productIdStr},${index});">
			       <#list milkPurchasePurposeTypeList as purpose>    
			         <#if existedPurpose?has_content  && existedPurpose == purpose.enumId >
			        	<option value='${eachItem.purposeTypeId}' selected='selected'>${enumeration.enumCode?if_exists}</option> 	
			         <#else>
			          <option value='${purpose.enumId}' >
				         ${purpose.enumCode?if_exists}
				      </option>
				      </#if>
			       </#list>            
				</select>
                </td>
                <td><#assign conversionProductId=eachItem.productIdTo>
                     <select id="productIdTo_${index}" name="productIdTo"  class='h4'>
                      <#if convProductList?has_content>  
                     	<#list convProductList as convProduct>
                            <#if conversionProductId?has_content && conversionProductId == convProduct.productId>
 	                            <option value='eachItem.productIdTo' selected='selected'>${convProduct.productName?if_exists}</option>
                            <#else>
                          		<option value='${convProduct.productId}'>${convProduct.productName?if_exists}</option>
                            </#if>
                        </#list> 
                      </#if>
                     </select>
                </td>
                </#if>
                <td>${eachItem.siloId?if_exists}</td>
                <#if parameters.flag?has_content && parameters.flag == "FINALIZATION">
             		<td align="right"><input type="text" size="10" maxlength="10" name="dispatchQty" id="dispatchQty"  value="${eachItem.quantity?if_exists}" /></td>
              	<#else>
              	   <td align="right">${eachItem.quantity?if_exists}</td>
              	</#if>
              	<td align="right">${eachItem.receivedQuantity?if_exists}</td>
				<td align="right">${eachItem.receivedFat?if_exists}</td>
				<td align="right">${eachItem.receivedSnf?if_exists}</td>
				<td align="right">${eachItem.receivedKgFat?if_exists}</td>
				<td align="right">${eachItem.receivedKgSnf?if_exists}</td>
			   <#assign statusItem = (delegator.findOne("StatusItem",{"statusId" : eachItem.statusId},false))!>
                <td>${statusItem.description?if_exists}</td>
           		<td><input type="checkbox" id="receiptCheckBoxId_${eachItem_index}" name="receiptCheckBoxId"/></td>
            </tr>
            <#assign index = index+1>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
</#if>
