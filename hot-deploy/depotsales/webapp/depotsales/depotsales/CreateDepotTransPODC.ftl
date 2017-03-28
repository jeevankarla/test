<#include "CreateDepotTransPOIncDC.ftl"/>
<#--
<link href="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.css</@ofbizContentUrl>" rel="stylesheet">
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
-->
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/flexselect.css</@ofbizContentUrl>">
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/liquidmetal.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jquery.flexselect-0.5.3/jquery.flexselect.js</@ofbizContentUrl>"></script>

<style type="text/css">
.myTable { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable th { 
  background-color: green;
  color: white; 
  }
.myTable td, 
.myTable th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
  
  
  .myTable1 { 
  width: 100%;
  text-align: left;
  background-color: lemonchiffon;
  border-collapse: collapse; 
  }
.myTable1 th { 
  background-color: green;
  color: white; 
  }
.myTable1 td, 
.myTable1 th { 
  padding: 10px;
  border: 1px solid goldenrod; 
  }
  
  
</style>

<style type="text/css">
input[type=button] {
	color: white;
    padding: .5x 7px;
    background:#008CBA;
    border: .8px solid green;
    border:0 none;
    cursor:pointer;
    -webkit-border-radius: 5px;
    border-radius: 5px; 
}
input[type=button]:hover {
    background-color: #3e8e41;
}

</style>

<script type="application/javascript">
	
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
	dateFormat:'dd MM, yy',
	changeMonth: true,
	numberOfMonths: 1,
	onSelect: function( selectedDate ) {
	$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
	}
	});
	$( "#"+thruDateId ).datepicker({
	dateFormat:'MM d, yy',
	changeMonth: true,
	numberOfMonths: 1,
	onSelect: function( selectedDate ) {
	//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
	}
	});
	}
	function makeDayDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
	dateFormat:'d MM, yy',
	changeMonth: true,
	numberOfMonths: 1,
	onSelect: function( selectedDate ) {
	$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
	}
	});
	$( "#"+thruDateId ).datepicker({
	dateFormat:'MM d, yy',
	changeMonth: true,
	numberOfMonths: 1,
	onSelect: function( selectedDate ) {
	//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
	}
	});
	}
	
    $(document).ready(function(){
    	populateBranchDepots();
            $("#wizard-2").steps({
                headerTag: "h3",
                bodyTag: "section",
                transitionEffect: "slideLeft",
                onStepChanging: function (event, currentIndex, newIndex)
                {	
                	if(currentIndex == 0 && newIndex == 1){
                	    var productStoreId = $("#productStoreId").val();  
	            	    if( (productStoreId).length < 1 ) {
	    	$('#productStoreId').css('background', 'yellow'); 
	       	setTimeout(function () {
	           	$('#productStoreId').css('background', 'white').focus(); 
	       	}, 800);
	    	return false;
	    	}
                	var supplierId = $("#supplierId").val();  
                	    if( (supplierId).length < 1 ) {
	    	$('#supplierId').css('background', 'yellow'); 
	       	setTimeout(function () {
	           	$('#supplierId').css('background', 'white').focus(); 
	       	}, 800);
	    	return false;
	    	}
                        supplierName = partyNameObj[supplierId];
                        if( (supplierName).length < 1 ) {
 	    	$('#supplierName').css('background', 'yellow'); 
 	       	setTimeout(function () {
 	           	$('#supplierName').css('background', 'white').focus(); 
 	       	}, 800);
 	    	return false;
	    	}
	    //populate SlickGrid starts here
	 if(supplierId){
	    gridShowCall();
	 	setupGrid1();
	     }else{ 
	        gridHideCall();
	     }
	        jQuery(".grid-header .ui-icon")
	            .addClass("ui-state-default ui-corner-all")
	            .mouseover(function(e) {
	                jQuery(e.target).addClass("ui-state-hover")
	            })
	            .mouseout(function(e) {
	                jQuery(e.target).removeClass("ui-state-hover")
	            });	
	jQuery("#gridContainer").resizable();	   	
	    	var tabindex = 1;
	    	jQuery('input,select').each(function() {
	        	if (this.type != "hidden") {
	            	var $input = $(this);
	            	$input.attr("tabindex", tabindex);
	            	tabindex++;
	        	}
	    	});
	
	    	var rowCount = jQuery('#myGrid1 .slick-row').length;
	if (rowCount > 0) {	
	$(mainGrid.getCellNode(rowCount-1, 0)).click();	   
	    	}
    	 	      //populate SlickGrid ends here
                	return true;
                	}
                	if(currentIndex == 1 && newIndex == 2){
                	     return true;
                	}
                	if(currentIndex == 2 && newIndex == 3){
                	return true;
                	}
                	if(currentIndex == 3 && newIndex == 4){
                	return true;
                	}
                	return true;
                },
                onFinishing: function (event, currentIndex)
                {	
                    var facilityId = $("#facilityId").val(); 
            	    if( (facilityId).length < 1 || facilityId == 'selected') {
	    	$('#facilityId').css('background', 'yellow'); 
	       	setTimeout(function () {
	           	$('#facilityId').css('background', 'white').focus(); 
	       	}, 800);
	    	return false;
	    	}
                    return true;
                },
                onFinished: function (event, currentIndex)
                {
                   
	    processPOEntryInternal("CreateMPO", "CreateMaterialDepotTransPODC");
                	form.submit();
                }
            });
	}); 
	
	$(document).ready(function(){
	makeDatePicker("estimatedDeliveryDate","fromDateId");
	makeDatePicker("orderDate","fromDateId");
	makeDatePicker("refDate","fromDateId");
	makeDatePicker("quoteDate","fromDateId");
	
	makeDayDatePicker("effectiveDate","fromDateId");
	makeDayDatePicker("SInvoiceDate","fromDateId");
	
	makeDatePicker("fromDate","fromDateId");
        makeDatePicker("thruDate","fromDateId");
	
	//$(this.target).find('input').autocomplete();
	
	$("#supplierId").autocomplete({ source: supplierJSON }).keydown(function(e){
	
	});	
	$( "input[name*='paymentTermTypeId']" ).autocomplete({ source: paymentTermsJSON });
	$( "input[name*='deliveryTermTypeId']" ).autocomplete({ source: deliveryTermsJSON });
	$('#ui-datepicker-div').css('clip', 'auto');
	hideExtPO();	
	getPayTermDes();
	getDelTermDes();
	
	<#--
	var depotsAutoJson = ${StringUtil.wrapString(depotsJSON)!'[]'};
	$('#shipToPartyId').keypress(function (e) { 
	$("#shipToPartyId").autocomplete({ source: depotsAutoJson , select: function( event, ui ) {
	$('span#depotName').html('<h4>'+ui.item.label+'</h4>');
	} });
	});
	-->
	var branchAutoJSON = ${StringUtil.wrapString(branchJSON)!'[]'};
	$('#productStoreId').keypress(function (e) { 
	$("#productStoreId").autocomplete({ source: branchAutoJSON , select: function( event, ui ) {
	$('span#branchName').html('<h4>'+ui.item.label+'</h4>');
	} });
	});
	
	});
	function hideExtPO(){
	var orderTypeId = $("#orderTypeId").val();
	var formObj = jQuery("#CreateMPO");
	var poNumberObj  = $(formObj).find("#PONumber");
	    var quoteNumObj  = $(formObj).find("#quoteNum");
        var quoteDateObj  = $(formObj).find("#quoteDate");
        var billToPartyIdObj  = $(formObj).find("#billToPartyId");
        var quoteDateObj  = $(formObj).find("#quoteDate");
        var fileNo  = $(formObj).find("#fileNo");
        var estimatedDeliveryDate  = $(formObj).find("#estimatedDeliveryDate");

	
	if(orderTypeId && orderTypeId == "PURCHASE_ORDER" || orderTypeId == "EXTEN_PURCHASE_ORDER" || orderTypeId == "LETTER_OF_INTENT" ){
	$(poNumberObj).parent().parent().show();
	$(fileNo).parent().parent().show();
	$(estimatedDeliveryDate).parent().parent().show();
	$(billToPartyIdObj).parent().parent().parent().show();
	$(quoteNumObj).parent().parent().show();
	$(quoteDateObj).parent().parent().show();
	
	}
	if(orderTypeId && orderTypeId == "ARC_ORDER" || orderTypeId == "CPC_ORDER" ){	
	$(poNumberObj).parent().parent().hide();
	$(fileNo).parent().parent().hide();
	$(estimatedDeliveryDate).parent().parent().hide();	
	$(quoteNumObj).parent().parent().hide();
	$(quoteDateObj).parent().parent().hide();
	$(billToPartyIdObj).parent().parent().parent().hide();	
	}
	}    
	
	var supplierName;
	function dispSuppName(selection){
	   var value = $("#supplierId").val();
	   supplierGeoId = partyGeoObj[value];
	   supplierName = partyNameObj[value];
	   $("#supplierGeoId").val(supplierGeoId); 
	   $("#supplierName").html("<h4>"+supplierName+"</h4>");
	}    
	    
	 var payTermDes;
	 var delTermDes;
	 var paymentTerms = ${StringUtil.wrapString(paymentTermsJSON!'[]')};
	 var deliveryTerms = ${StringUtil.wrapString(deliveryTermsJSON!'[]')};
	 function getPayTermDes(){
	  $.each(paymentTerms, function(key, val){
	    $("#payTermDes").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
	  });
	  $("#payTermDes").flexselect({
	preSelection: false,
	allowClear: false,
	allowMismatch:false,
	    allowEmpty:true,
	    allowNewElements:false,
	    placeholder: "select",
	    hideDropdownOnEmptyInput: false
	});	
	
	   }	 
	   function getDelTermDes(){
	  $.each(deliveryTerms, function(key, val){
	    $("#delTermDes").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
	  });
	  $("#delTermDes").flexselect({
	preSelection: false,
	allowClear: false,
	allowMismatch:false,
	allowEmpty:true,
	allowNewElements:false,
	placeholder: "select",
	hideDropdownOnEmptyInput: false
	  });	
	}  
	 
	var billToPartyId;
	function populateBranchDepots() {
	var productStoreId = $("#productStoreId").val();
	var originFacilityId=$("#originFacilityId").val();
	var originFacilityName=$("#originFacilityName").val();
	
	$.ajax({
	 type: "POST",
	             url: 'populateBranchDepotsAjax',
	             data: {productStoreId : productStoreId},
	             dataType: 'json',
	            
	 success:function(result){
	if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available courses');
	}else{	
	depotsList = result["depotsList"];
	billToPartyId = result["ownerPartyId"];
	CreateMPO.billToPartyId.value=billToPartyId;	
	var paramName = 'facilityId';
	var optionList = '';   	
	var list= depotsList;
	if (list) {	
	if (originFacilityId != null || originFacilityId != undefined || originFacilityId !=""){
	 optionList += "<option value = " + originFacilityId + "  selected>" + originFacilityName + "</option>";          	
	  }     	        	
	        	for(var i=0 ; i<list.length ; i++){
	var innerList=list[i];	              	             
	                optionList += "<option value = " + innerList.facilityId + " >" + innerList.facilityName + "</option>";          	
	      	}
	      	}	
	      	if(paramName){
	      	jQuery("[name='"+paramName+"']").html(optionList);
	      	}
	}	 
	},
	error: function(){
	alert("record not found");
	}	
	});
	
	} 
	  
	    
</script>

<style type="text/css">
.styled-button {
	-webkit-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	-moz-box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	box-shadow:rgba(0,0,0,0.2) 0 1px 0 0;
	color:#333;
	background-color:#FA2;
	border-radius:5px;
	-moz-border-radius:5px;
	-webkit-border-radius:5px;
	border:none;
	font-family:'Helvetica Neue',Arial,sans-serif;
	font-size:25px;
	font-weight:700;
	height:32px;
	padding:4px 16px;
	text-shadow:#FE6 0 1px 0
}
</style>
	
<#assign orderInfo = {}>
<#assign quoteInfo = {}>
<#assign orderAdjInfo = {}>
<#assign orderTermInfo = {}>
<#assign orderPayTermInfo = []>
<#assign orderShipTermInfo = []>
<#assign bedCheck = "">
<#assign quoteTerm = "">
<#assign orderTypeId = "">
<#if orderEditParam?has_content>
	<#assign orderInfo = orderEditParam.get("orderHeader")?if_exists>
	<#assign quoteInfo = orderEditParam.get("quoteDetails")?if_exists>
	<#if quoteInfo.quoteId?has_content>
	<#assign quoteTerm = delegator.findByAnd("QuoteTerm", {"quoteId" : quoteInfo.quoteId, "termTypeId" : "BED_PUR" })>
	<#if quoteTerm!= "undefined" && quoteTerm?has_content>
	<#assign bedCheck = quoteTerm[0].termTypeId?if_exists>
	</#if>
	</#if>
	<#assign orderAdjInfo = orderEditParam.get("orderAdjustment")?if_exists>
	<#assign orderTermInfo = orderEditParam.get("orderTerms")?if_exists>
	<#assign orderTypeId = orderInfo.orderTypeId>
	<#if orderTermInfo?has_content>
	<#assign orderPayTermInfo = orderTermInfo.get("paymentTerms")>
	<#assign orderShipTermInfo = orderTermInfo.get("deliveryTerms")>
	</#if>
</#if>
	
<form id="CreateMPO"  action="<@ofbizUrl>CreateMaterialDepotTransPODC</@ofbizUrl>" name="CreateMPO" method="post">

<div id="wizard-2">
    <h3>PO Header</h3>
    <section>
      <fieldset>
            <table cellpadding="15" cellspacing="15" class='h3' width="50%">
                   <tr>
	<td class="label">Order Type(<font color="red">*</font>) :</td>
	    <td>
	<select name="orderTypeId" id="orderTypeId" onchange="javascript: hideExtPO();">
	      	   <#list orderTypes as orderType>
	      	   	<#if orderId?exists && (orderTypeId == orderType.orderTypeId)>
	      	   	<option value='${orderType.orderTypeId}' selected>${orderType.description}</option> 
	      	   	<#else>
	      	   	<option value='${orderType.orderTypeId}'>${orderType.description}</option>
	      	   	</#if>   
    	
	    </#list> 
	      	</select>
	    </td>
	</tr>
	<tr>
	    <td class="label"><b>Branch(<font color="red">*</font>) :</b></td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("productStoreId")?exists>
	    	<input type="text" name="productStoreId" id="productStoreId" size="18" maxlength="60" value="${orderInfo.get("productStoreId")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="productStoreId" id="productStoreId" size="18" maxlength="60" onblur="populateBranchDepots()"/>
	   	</#if> 
        	 </td>
	</tr>
	<tr>
	    <td class="label">Supplier(<font color="red">*</font>) : </td>
	    <input type="hidden" name="orderId" id="orderId"  value="${orderId?if_exists}" />
	    <input type="hidden" name="purposeTypeId" id="purposeTypeId"  value="DC_DEPOT_PURCHASE" />
	    
	     <#--<input type="hidden" name="productStoreId"  value="${productStoreId?if_exists}" />-->
	    <#if changeFlag?exists && changeFlag=='InterUnitPurchase'>
	       <input type="hidden" name="salesChannel" id="salesChannel" value="INTER_PRCHSE_CHANNEL"/> 
	    <#else>
	       <input type="hidden" name="salesChannel" id="salesChannel" value="WEB_SALES_CHANNEL"/>  
	    </#if>
	    <td>
	    	<#if orderId?exists && orderInfo.get("supplierId")?exists>
	    	<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60" value="${orderInfo.get("supplierId")}" readonly onblur= 'javascript:dispSuppName(this);'/>
	    	<span> ${orderInfo.get("supplierName")?if_exists}</span>
	    	<input type="hidden" name="supplierGeoId" id="supplierGeoId" value="${orderInfo.get("supplierGeoId")}" size="18" maxlength="60"/>
	    	<#else>
	    	<input type="text" name="supplierId" id="supplierId" size="18" maxlength="60"  onblur= 'javascript:dispSuppName(this);'/>
	    	<span id="supplierName"></span>
	    	<input type="hidden" name="supplierGeoId" id="supplierGeoId" size="18" maxlength="60"/>
	    	</#if>
	      	
	    </td>
	</tr>
	<tr>
	    <#assign purchaseTypeFlag = parameters.purchaseTypeFlag?if_exists>
	    <#if purchaseTypeFlag?has_content && purchaseTypeFlag == "contractPurchase">
	    <td class="label"><b>Date : </b></td>
                        <#else><td class="label"><b>PO Date : </b></td> 
                        </#if> 
	    <td>
	    	<#if orderId?exists && orderInfo.get("orderDate")?exists>
	    	<input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("orderDate")?if_exists}"/>
	    	<#else>
	      	<input type="text" name="orderDate" id="orderDate" size="18" maxlength="60" autocomplete="off"/>
	      	</#if>
        	 </td>
	</tr>
	<#assign purchaseTypeFlag = parameters.purchaseTypeFlag?if_exists>
	<#if purchaseTypeFlag?has_content && purchaseTypeFlag == "contractPurchase">
	<tr>
	<td class="label"><b>From Date : </b></td>
	<td>
	<#if orderId?exists && orderInfo.get("validFromDate")?exists>
	    	<input type="text" name="fromDate" id="fromDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("validFromDate")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="fromDate" id="fromDate" size="18" maxlength="60" autocomplete="off"/>
	    	</#if>
	</td>
	</tr>
	<tr>
	<td class="label"><b>Thru Date : </b></td>
	<td>
	<#if orderId?exists && orderInfo.get("validThruDate")?exists>
	    	<input type="text" name="thruDate" id="thruDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("validThruDate")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="thruDate" id="thruDate" size="18" maxlength="60" autocomplete="off"/>
	    	</#if>
	</td>
	</tr>
	</#if>
	<#if orderId?exists && quoteInfo.get("quoteId")?exists>
	      	        <tr>
	      	        	<td class="label"><b>Quote No: </b></td>
	    <td>
	    	${quoteInfo.get("quoteId")?if_exists}
	        	 </td>
	</tr>
	</#if>
	<#if orderId?exists && quoteInfo.get("quoteIssueDate")?exists>
	<tr>
	    	<td class="label"><b>Quote Date : </b></td>
	    	<td>
	    	${quoteInfo.get("quoteIssueDate")?if_exists}
	        	 </td>
	</tr>
	</#if>
      	        <tr>
	    <#--<td class="label">Bill To Party :</td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("billToPartyId")?exists>
	    	<#assign billToPartyId=orderInfo.get("billToPartyId")>
	    	<@htmlTemplate.lookupField value="${billToPartyId?if_exists}" formName="CreateMPO" size="18" maxlength="60" name="billToPartyId"  id="billToPartyId" fieldFormName="LookupPartyName"/>
	    	<#else>
	      	<@htmlTemplate.lookupField  formName="CreateMPO" size="18" maxlength="60" name="billToPartyId" id="billToPartyId" fieldFormName="LookupPartyName"/>
      	                </#if>
      	                <span class="tooltip">If billing and vendor party are different, invoice will be raise against this Party </span>
	    </td>-->
	    <input type="hidden" name="billToPartyId" id="billToPartyId"  value=""/>
	    
	 </tr>
	 <tr>
	    <td class="label"><b>Ref No. :</b></td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("refNo")?exists>
	    	<input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("refNo")?if_exists}"/>
	    	<#else>
	      	<input type="text" name="refNo" id="refNo" size="18" maxlength="60" autocomplete="off"/>
	      	</#if>
	      
        	 </td>
	</tr>
	<tr>
	    <td class="label"><b>Estimated Delivery Date :</b></td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("estimatedDeliveryDate")?exists>
	    	<input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("estimatedDeliveryDate")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="estimatedDeliveryDate" id="estimatedDeliveryDate" size="18" maxlength="60" autocomplete="off"/>
	   	</#if> 
        	 </td>
	</tr>
	<!--<tr>
	    <td class="label"><b>Ref Date:* </b></td>
	    <td>
	      <input type="text" name="refDate" id="refDate" size="18" maxlength="60" autocomplete="off"/>
        	 </td>
	</tr>-->
	<!--<tr>
	    <td class="label"><b>ARC No / CPC No / Ext. PO :</b></td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("PONumber")?exists>
	    	<input type="text" name="PONumber" id="PONumber" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("PONumber")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="PONumber" id="PONumber" size="18" maxlength="60" autocomplete="off"/>
	    	</#if>
        	 </td>
	</tr>-->
	<!--<tr>
	    <td class="label"><b>File No : </b></td>
	    <td>
	    	<#if orderId?exists && orderInfo.get("fileNo")?exists>
	    	<input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("fileNo")?if_exists}"/>
	    	<#else>
	    	<input type="text" name="fileNo" id="fileNo" size="18" maxlength="60" autocomplete="off"/>
	    	</#if>
	      
        	 </td>
	</tr>-->
	        <tr>
	            <td class="label"><b> Description :</b></td>
	            <td>
	            	<#if orderId?exists && orderInfo.get("orderName")?exists>
	    	<input type="text" name="orderName" id="orderName" size="18" maxlength="60" autocomplete="off" value="${orderInfo.get("orderName")?if_exists}"/>
	    	<#else>
	    	<input class="h3" type="textarea" size="30" maxlength="100" name="orderName" id="orderName" style="width: 200px; height: 50px" />
	    	</#if>
        	   
          	</td>
	        </tr>
	        <tr>
		  		<td align='left' class="label" nowrap="nowrap">Purchase Tax Type:</td>
		  		<td valign='middle'>
					<select name="purchaseTaxType" id="purchaseTaxType" class='h4' style="width:120px">
						<#if orderId?exists && orderInfo.get("purchaseTaxType")?exists>
							<#if orderInfo.get("purchaseTaxType") == "Intra-State">
								<option value="Intra-State" selected>With In State</option>
							<#else>
								<option value="Inter-State" selected>Inter State</option>
							</#if> 
						</#if> 
						<option value="Intra-State">With In State</option>
						<option value="Inter-State">Inter State</option>
					</select>
				</td>
				<td>&nbsp;</td>
		          		<td align='left' class="label" nowrap="nowrap">Transaction Type:</td>
		       			<td valign='middle'>
	          				<select name="purchaseTitleTransferEnumId" id="purchaseTitleTransferEnumId" class='h4' style="width:205px">
	          					<#if orderId?exists && orderInfo.get("purchaseTitleTransferEnumId")?exists>
	          						<#if orderInfo.get("purchaseTitleTransferEnumId") == "CST_CFORM">
	          							<option value="CST_CFORM" selected>Transaction With C Form</option>
	          							<option value="CST_NOCFORM">Transaction Without C Form</option>
		          						<option value="NO_E2_FORM"></option>
	          						</#if>
	          						<#if orderInfo.get("purchaseTitleTransferEnumId") == "CST_NOCFORM">
	          							<option value="CST_NOCFORM" selected>Transaction Without C Form</option>
	          							<option value="CST_CFORM">Transaction With C Form</option>
	          							<option value="NO_E2_FORM"></option>
	          						</#if>
	          						<#if orderInfo.get("purchaseTitleTransferEnumId") == "NO_E2_FORM">
	          							<option value="NO_E2_FORM" selected></option>
	          							<option value="CST_NOCFORM">Transaction Without C Form</option>
	          							<option value="CST_CFORM">Transaction With C Form</option>
	          						</#if> 
	          					<#else> 
		          					<option value="CST_CFORM">Transaction With C Form</option>
		          					<option value="CST_NOCFORM">Transaction Without C Form</option>
		          					<option value="NO_E2_FORM"></option>
	          					</#if>
	          				</select>
	          			</td>
		            </tr>		
                  </table>
                </fieldset>  
             </section>
	
	<#-- Working area-->
        <h3>PO Items </h3>
        <section>
        
        	<div class="full" style="width:100%;height:500px;">
	<div class="screenlet">
	    	<div class="screenlet-title-bar">
	 	<div class="grid-header" style="width:100%">
	<label>Purchase Order</label>
	</div>
	 	<div class="screenlet-body" id="FieldsDIV" >
	 	<table width="100%" border="0" cellspacing="10" cellpadding="10">
	 	        <tr>
	 	        	<#--<#if includeTax?exists && includeTax=="Y">
	        	<td align='left' nowrap="nowrap"><h3><font color="red">Include Tax:<input class='h3' type="checkbox" id="incTax" name="incTax" value="true" checked /></font></h3></td>
	<#else>
	<td align='left' nowrap="nowrap"><h3><font color="red">Include Tax:<input class='h3' type="checkbox" id="incTax" name="incTax" value="true" onClick="javascript: updateGridAmount();" /></font></h3></td>
	</#if>-->
	        	<td align="center"><h2><font color="black">Total PO Value :</font> <font color="green"><span id="totalPOAmount">Rs. 0</span></font></h2></td>
	        	<td align='right'><input class="styled-button" type="button" id="calculateBtn"  name="calculateBtn" value="Calculate" onClick="javascript: calculatePOValue();"/></td>
	 	         </tr>
	        	</table>
	        	
	        	<div class="screenlet-title-bar">
	        	<div class="grid-header" style="width:80%">
	<label>Material Details</label>
	</div>
	
	<div id="myGrid1" style="width:80%;height:150px;">
	<div class="grid-header" style="width:80%">
	</div>
	             	</div>
	             	<br/>
	<#--- <center><input class="styled-button" type="button" id="otherChargesBtn"  name="otherChargesBtn" value="Add Charges" onClick="javascript: displayChargesGrid();"/></center> -->	
	<br/>	             	
	</div>
	
	<div class="screenlet-title-bar" id="titleScreen">
	<div class="grid-header" style="width:80%">
	<label>Other Charges</label>
	</div>
	<div id="myGrid2" style="width:80%;height:150px;">
	<div class="grid-header" style="width:80%">
	</div>
	</div>
	</div>
	</div>
	</div>
	</div>     
        </section>
         <#--<div class="full">
	     <div class="screenlet">
	    	<div class="screenlet-body">
	    	 <div class="screenlet-body" id="FieldsDIV" >
	<table width="50%" border="0" cellspacing="0" cellpadding="0">
	
	 	       <tr><td><br/></td></tr>
	 	        <tr>
	        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Inc Tax: <input class='h3' type="checkbox" id="incTax" name="incTax" value="true" onClick="javascript: updateGridAmount();"/></div></td>
	         	
	 	         </tr>
	 	          <tr><td><br/></td></tr>
	        	</table>
	</div>
	<div class="grid-header" style="width:100%">
	<label>Purchase Entry </label><span id="totalAmount"></span>
	</div>
	<div id="myGrid1" style="width:100%;height:250px;"></div>
	
	             </div>  
	          </div>
	</div>        
           </section>-->
           <#-- Working area-->
           
           <#--<h3>Payment Terms</h3>
	          <section>
	          <fieldset>
	            <table cellpadding="15" cellspacing="15" class='h2'>
	<tr>
	          	<td align='left' valign='middle' nowrap="nowrap"></td>
	                <td>
	               	
	               	<table border="0" cellspacing="10" cellpadding="10" id="paymentTermsTable" style="width:200px;" align="center">
	                <tr>
	       <td>
	       	<table class='h3' cellspacing="15">
	       	<tr>
	           	<td><input type="button" id="addPaymentTerm" value="Add" style="padding: 2.5px;"/></td>
	        	<td> <input type="button" id="delPaymentTerm" value="Delete" style="padding: 2.5px;"/></td>
	        	</tr> 
	        	</table>
	        </td>
	        <td></td>
	        <td></td>
	    </tr>
	    <tr></tr>
	    <tr>
	        	<td align="center">Term Type</td>
	         	<#--<td align="center">Term Days</td>
	          	<td align="center">Term Value</td>
	          	<td align="center">UOM</td>-->
	          	<#--<td align="center">Description</td>
	    	</tr>
	    	<#if orderId?exists && orderPayTermInfo?has_content>
	    	<#assign rowCount = 0>
	    	<#list orderPayTermInfo as eachPayTerm>
	    	<tr>
	    	<input type="hidden"  name="paymentTermTypeId_o_${rowCount}" value="${eachPayTerm.get("termTypeId")?if_exists}"/>
	        	<td>
	          	<input type="text"  name="paymentTermDesc" value="${eachPayTerm.get("termTypeDescription")?if_exists}" size="40"/>
	        	</td>
	            	<#--<td>
	                	<input type="text" name="paymentTermDays_o_${rowCount}" value="${eachPayTerm.get("termDays")?if_exists}" size="10"/>
	            	</td>
	            	<td>
	                	<input type="text" name="paymentTermValue_o_${rowCount}" value="${eachPayTerm.get("termValue")?if_exists}" size="10"/>
	            	</td>
	            	<td>
	            	<select name="paymentTermUom_o_${rowCount}">
	            	<#if eachPayTerm.get("uomId") == "INR">
	            	<option value="INR" selected>Rupees</option>
	            	<option value="PERCENT">Percent</option>
	            	<#else>
	            	<option value="INR">Rupees</option>
	            	<option value="PERCENT" selected>Percent</option>
	            	</#if>
	            	
	            	</select>
	            	</td>-->
	            	<#--<td>
	                	<input type="textarea" cols="40" rows="5" maxlength="255" name="paymentTermDescription_o_${rowCount}" value="${eachPayTerm.get("description")?if_exists}" />
	            	</td>
	    	</tr>
	    	<#assign rowCount = rowCount+1>
	    	</#list> 
	    	<#else>
	    	<tr>
	        	<td>
	        	<select id='payTermDes' name="paymentTermTypeId_o_0" class='flexselect' ></select>
	        	</td>
	            	<#--<td>
	                	<input type="text" name="paymentTermDays_o_0" value=""  size="10"/>
	            	</td>
	            	<td>
	                	<input type="text" name="paymentTermValue_o_0" value="" size="10"/>
	            	</td>
	            	<td>
	            	<select name="paymentTermUom_o_0">
	            	<option value="INR">Rupees</option>
	            	<option value="PERCENT">Percent</option>
	            	</select>
	            	</td>-->
	            	<#--><td>
	                	<input type="textarea" name="paymentTermDescription_o_0" value="" maxlength="255"/>
	            	</td>
	    	</tr>
	    	</#if>
	</table>
          	     </td>
	  </tr>
	  </table>
	</fieldset>
                 </section>-->
                 <h3>Delivery Terms</h3>
	         <section>
	          <fieldset>
	            <table cellpadding="15" cellspacing="15" class='h2'>
	         <tr>
	          	<td align='left' valign='middle' nowrap="nowrap"></td>
	                 <td>
	               <table border="2" cellspacing="10" cellpadding="10" id="deliveryTermsTable" style="width:200px;" align="center">
	                
	                <tr>
                                	<td class="label"><FONT COLOR="#045FB4"><b>Select Depot</b></FONT></td>
	    	<td>
	    	
	                                	<select name="facilityId" id="facilityId"></select>
	   	<#if orderId?exists && orderInfo.get("originFacilityId")?has_content>
	   	<input type="hidden"  name="originFacilityId" id="originFacilityId" value="${orderInfo.get("originFacilityId")?if_exists}"/>
	   	<input type="hidden"  name="originFacilityName" id="originFacilityName" value="${orderInfo.get("originFacilityName")?if_exists}"/>
	   	<#else>
	   	<input type="hidden"  name="originFacilityId" id="originFacilityId" value=""/>
	   	<input type="hidden"  name="originFacilityName" id="originFacilityName" value=""/>
	   	
	   	</#if>
	   	
	      	</td>
	      	</tr>
	                <#--
	                <tr>
	    <td>Ship To Party(<font color="red">*</font>):</td>
	    <td>
	        <#if orderId?exists && orderInfo.get("shipToPartyId")?exists>
	        <#assign shipToPartyId=orderInfo.get("shipToPartyId")>
	    	<input type="text" name="shipToPartyId" id="shipToPartyId" size="18" maxlength="60" autocomplete="off" value="${shipToPartyId?if_exists}"/>
	    	<#else>
	      	<input type="text" name="shipToPartyId" id="shipToPartyId" size="18" maxlength="60" autocomplete="off"/>
	      	<span id="depotName"></span>
	      	</#if>
	    </td>
	 </tr>
	 -->
	                <#--<tr>
	       <td><table cellspacing="15"><tr>
	           <td><input type="button" id="addDeliveryTerm" value="Add" style="padding: 2.5px;"/>  </td>
	        	<td> <input type="button" id="delDeliveryTerm" value="Delete" style="padding: 2.5px;"/></td>
	        </tr> </table>
	          </td>
	         <td> </td>
	          <td></td>
	    </tr>
	    <tr>
	        	<td align="center">Term Type</td>
	         	<#--<td align="center">Term Days</td>
	          	<td align="center">Term Value</td>
	          	<td align="center">UOM</td>-->
	          	<#--<td align="center">Description</td>
	    </tr>
	    <#if orderId?exists && orderShipTermInfo?has_content>
	    	<#assign rowCount = 0>
	    	<#list orderShipTermInfo as eachShipTerm>
	    	<tr>
	    	<input type="hidden"  name="deliveryTermTypeId_o_${rowCount}" value="${eachShipTerm.get("termTypeId")?if_exists}"/>
	        	<td>
	          	<input type="text"  name="deliveryTermDesc" value="${eachShipTerm.get("termTypeDescription")?if_exists}" size="40"/>
	        	</td>
	            	<#--<td>
	                	<input type="text" name="deliveryTermDays_o_${rowCount}" value="${eachShipTerm.get("termDays")?if_exists}" size="10"/>
	            	</td>
	            	<td>
	                	<input type="text" name="deliveryTermValue_o_${rowCount}" value="${eachShipTerm.get("termValue")?if_exists}" size="10"/>
	            	</td>
	            	<td>
	            	<select name="deliveryTermUom_o_${rowCount}">
	            	<#if eachShipTerm.get("uomId") == "INR">
	            	<option value="INR" selected>Rupees</option>
	            	<option value="PERCENT">Percent</option>
	            	<#else>
	            	<option value="INR">Rupees</option>
	            	<option value="PERCENT" selected>Percent</option>
	            	</#if>
	            	
	            	</select>
	            	</td>-->
	            	<#--<td>
	                	<input type="textarea" name="deliveryTermDescription_o_${rowCount}" value="${eachShipTerm.get("description")?if_exists}" maxlength="255"/>
	            	</td>
	    	</tr>-->
	    	<#--<#assign rowCount = rowCount+1>
	    	</#list>
	    <#else>
	    <tr>
	        <td>
	        	<select id='delTermDes' name="deliveryTermTypeId_o_0" class='flexselect' ></select>
	        </td>-->
	            <#--<td>
	                <input type="text" name="deliveryTermDays_o_0" value="" size="10"/>
	            </td>
	            <td>
	                <input type="text" name="deliveryTermValue_o_0" value="" size="10"/>
	            </td>
	            	<td>
	            	<select name="deliveryTermUom_o_0">
	            	<option value="INR">Rupees</option>
	            	<option value="PERCENT">Percent</option>
	            	</select>
	            	</td>-->
	            	<#--><td>
	                	<input type="textarea" name="deliveryTermDescription_o_0" value="" maxlength="255"/>
	            	</td>
	    </tr>
	 </#if> -->
	</table>
          	     </td>
	        </tr>
	  </table>
	  </fieldset>
                  </section>
            </form>
                
 <script type="application/javascript">
 
 $(document).ready(function(){
    
    getPayTermDes();
    getDelTermDes();
    
    <#if orderId?exists && termExists?exists && termExists == "Y">
    	prepareApplicableOptions();
    	setupGrid2();
    <#else>
    	$("#titleScreen").hide();
    </#if>
    <#if orderId?exists>
    	calculatePOValue();
    </#if>
    
    $('#addPaymentTerm').click(function () {
        getPayTermDes();
	    var table = $("#paymentTermsTable");
	    var rows = document.getElementById("paymentTermsTable").getElementsByTagName("tr").length;
	    if (rows > 0) {
        var rowCount = rows-4;
        table.append('<tr><td> <select id ="payTermDes" name="paymentTermTypeId_o_'+rowCount+'" class = "flexselect" /></td><#--<td> <input type="text" size="10" name="paymentTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" size="10" name="paymentTermValue_o_'+rowCount+'" value="" /> </td><td><select name="paymentTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td>--><td><input type="textarea" name="paymentTermDescription_o_'+rowCount+'" value="" maxlength="255"/>	</td></tr>');
    }
  	$.each(paymentTerms, function(key, val){
    	$("select[name*='paymentTermTypeId']").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
  	});
    $("select[name*='paymentTermTypeId']").flexselect({
	preSelection: false,
	allowClear: false,
	allowMismatch:false,
	    allowEmpty:true,
	    allowNewElements:false,
	    placeholder: "select",
	    hideDropdownOnEmptyInput: false});	
});
	$('#delPaymentTerm').click(function () {
	     var table = $("#paymentTermsTable");
	     alert(table.find('input:text').length);
	    if (table.find('input:text').length >= 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});

	
	 $('#addDeliveryTerm').click(function () {
	 getDelTermDes();
    var table = $("#deliveryTermsTable");
     var rows = document.getElementById("deliveryTermsTable").getElementsByTagName("tr").length;
    if (rows >0) {
        var rowCount = rows-3;
        table.append('<tr><td> <select id ="delTermDes" name="deliveryTermTypeId_o_'+rowCount+'" class = "flexselect" /></td><#--<td> <input type="text" size="10" name="deliveryTermDays_o_'+rowCount+'" value="" /> </td><td> <input type="text" size="10" name="deliveryTermValue_o_'+rowCount+'" value="" /> </td><td><select name="deliveryTermUom_o_'+rowCount+'"><option value="INR">Rupees</option><option value="PERCENT">Percent</option></select></td>--><td><input type="textarea" name="deliveryTermDescription_o_'+rowCount+'" value="" maxlength="255"/></td></tr>');
    }
    $.each(deliveryTerms, function(key, val){
    	$("select[name*='deliveryTermTypeId']").append('<option value="' + val.value + '">' + val.label + " [" + val.value + "]" + '</option>');
  	});
    $("select[name*='deliveryTermTypeId']").flexselect({
	preSelection: false,
	allowClear: false,
	allowMismatch:false,
	    allowEmpty:true,
	    allowNewElements:false,
	    placeholder: "select",
	    hideDropdownOnEmptyInput: false});
	});
	
	$('#delDeliveryTerm').click(function () {
	     var table = $("#deliveryTermsTable");
	    if (table.find('input:text').length >= 1) {
	        table.find('input:text').last().closest('tr').remove();
	    }
	});
	
});  
	         
 </script>               
	