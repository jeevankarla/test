
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<style type="text/css">
	.cell-title {
		font-weight: normal;
		font-color: green;
	}
	.cell-effort-driven {
		text-align: center;
	}
	.readOnlyColumnClass {
		font-weight: normal;
		background: mistyrose;
	}
	.readOnlyColumnAndWarningClass {
		font-weight: bold;
		color: red;
		background: white;
		animation: blinker 1.7s cubic-bezier(.5, 0, 1, 1) infinite alternate; 
	}
	@keyframes blinker {  
	  from { opacity: 1; }
	  to { opacity: 0; }
	}
	
});
	
	
	.righthalf {
	    float: right;
	    height: 1%;
	    margin: 0 0 1% 1%;
	    right: 0;
	    width: 69%;
	}
	
	.lefthalf {
	    float: left;
	    height: 1%;
	    left: 0;
	    margin: 0% 1% 1% 0%;
	    width: 29%;
	}
	
	.btn {
	    color:#08233e;
	    font:8em Futura, ‘Century Gothic’, AppleGothic, sans-serif;
	    font-size:100%;
	    font-weight: bold;
	    padding:14px;
	    background:url(overlay.png) repeat-x center #ffcc00;
	    background-color:rgba(900,204,0,1);
	    border:1px solid #ffcc00;
	    -moz-border-radius:10px;
	    -webkit-border-radius:10px;
	    border-radius:10px;
	    border-bottom:1px solid #9f9f9f;
	    -moz-box-shadow:inset 0 1px 0 rgba(255,255,255,0.5);
	    -webkit-box-shadow:inset 0 1px 0 rgba(255,255,255,0.5);
	    box-shadow:inset 0 1px 0 rgba(255,255,255,0.5);
	    cursor:pointer;
	    display:inline;
	}
	
	
	.btn:hover {
    	background-color:rgba(900,204,0,0.8);
	}
	.btn {
	    background-color:green;
	    cursor:pointer;
	}
	
</style>			
	
<#include "EditUDPPriceDepot.ftl"/>		
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.js</@ofbizContentUrl>"></script>
<script type="application/javascript">
	var dataView;
	var dataView2;
	var grid;
	var data2 = [];
	var grid2;
	var withAdjColumns;
	
	var productQuotaJSON = ${StringUtil.wrapString(productQuotaJSON)!'{}'};
	var productCategoryJSON = ${StringUtil.wrapString(productCategoryJSON)!'{}'};
	
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var featureAvailableTags = ${StringUtil.wrapString(featuresJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var conversionData = ${StringUtil.wrapString(conversionJSON)!'{}'};
	var data = ${StringUtil.wrapString(dataJSON)!'[]'};
	data2=${StringUtil.wrapString(data2JSON)!'[]'};
	var userDefPriceObj = ${StringUtil.wrapString(userDefPriceObj)!'[]'};
    var productQtyInc = ${StringUtil.wrapString(productQtyIncJSON)!'{}'};
	var boothAutoJson = ${StringUtil.wrapString(boothsJSON)!'[]'};
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};	
	var branchAutoJson = ${StringUtil.wrapString(branchJSON)!'[]'};	
	var partyNameObj = ${StringUtil.wrapString(partyNameObj)!'[]'};
	var routeAutoJson = ${StringUtil.wrapString(routesJSON)!'[]'};
	var prodIndentQtyCat=${StringUtil.wrapString(prodIndentQtyCat)!'[]'};
	var qtyInPieces=${StringUtil.wrapString(qtyInPieces)!'[]'};
	
	var availableAdjTags = ${StringUtil.wrapString(orderAdjItemsJSON)!'[]'};
	var orderAdjLabelJSON = ${StringUtil.wrapString(orderAdjLabelJSON)!'{}'};
	var orderAdjLabelIdMap = ${StringUtil.wrapString(orderAdjLabelIdJSON)!'{}'};
	
	var productUOMMap = ${StringUtil.wrapString(productUOMJSON)!'{}'};
	var uomLabelMap = ${StringUtil.wrapString(uomLabelJSON)!'{}'};
	
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	
	function processIndentEntryInternal(formName, action) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var formId = "#" + formName;
		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));
		
		for (var rowCount=0; rowCount < data.length; ++rowCount)
		{ 
			var productId = data[rowCount]["cProductId"];
			var prodId="";
			if(typeof(productId)!= "undefined"){ 	  
				var prodId = productId.toUpperCase();
			}
			var qty = parseFloat(data[rowCount]["quantity"]);
			var balqty = parseFloat(data[rowCount]["baleQuantity"]);
			var yarnUOM = data[rowCount]["cottonUom"];
			var bundleWeight = data[rowCount]["bundleWeight"];
			var batchNo = data[rowCount]["batchNo"];
			var days = data[rowCount]["daysToStore"];
			var bundleUnitPrice = data[rowCount]["unitPrice"];
			var unitPrice = data[rowCount]["KgunitPrice"];	
			var remarks = data[rowCount]["remarks"];		
			var serviceCharge = data[rowCount]["SERVICE_CHARGE"];
			var serviceChargeAmt = data[rowCount]["SERVICE_CHARGE_AMT"];			
			
			var taxAmt = data[rowCount]["taxAmt"];
			var checkE2Form = data[rowCount]["checkE2Form"];
			var applicableTaxType = data[rowCount]["applicableTaxType"];
			var checkCForm = data[rowCount]["checkCForm"];
			
			$("#orderTaxType").val(applicableTaxType);
			var usedQuota = data[rowCount]["usedQuota"];
			<#if changeFlag?exists && changeFlag != "EditDepotSales">
			 if(qty>0){
			</#if>
	 		if (!isNaN(prodId)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputBaleQty = jQuery("<input>").attr("type", "hidden").attr("name", "baleQuantity_o_" + rowCount).val(balqty);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				var inputYarnUOM = jQuery("<input>").attr("type", "hidden").attr("name", "yarnUOM_o_" + rowCount).val(yarnUOM);
				var inputBundleWeight = jQuery("<input>").attr("type", "hidden").attr("name", "bundleWeight_o_" + rowCount).val(bundleWeight);
				var inputUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "unitPrice_o_" + rowCount).val(unitPrice);
				var inputbundleUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "bundleUnitPrice_o_" + rowCount).val(bundleUnitPrice);			
				var inputRemarks = jQuery("<input>").attr("type", "hidden").attr("name", "remarks_o_" + rowCount).val(remarks);
				var inputServChgAmt = jQuery("<input>").attr("type", "hidden").attr("name", "serviceChargeAmt_o_" + rowCount).val(serviceChargeAmt);
				var inputServChg = jQuery("<input>").attr("type", "hidden").attr("name", "serviceCharge_o_" + rowCount).val(serviceCharge);
				
				var inputCheckE2Form = jQuery("<input>").attr("type", "hidden").attr("name", "checkE2Form_o_" + rowCount).val(checkE2Form);
				var inputApplicableTaxType = jQuery("<input>").attr("type", "hidden").attr("name", "applicableTaxType_o_" + rowCount).val(applicableTaxType);
				var inputCheckCForm = jQuery("<input>").attr("type", "hidden").attr("name", "checkCForm_o_" + rowCount).val(checkCForm);
				var inputUsedQuota = jQuery("<input>").attr("type", "hidden").attr("name", "usedQuota_o_" + rowCount).val(usedQuota);
				jQuery(formId).append(jQuery(inputRemarks));
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputBaleQty));
				jQuery(formId).append(jQuery(inputYarnUOM));
				jQuery(formId).append(jQuery(inputBundleWeight));
				jQuery(formId).append(jQuery(inputQty));
				jQuery(formId).append(jQuery(inputUnitPrice));
				jQuery(formId).append(jQuery(inputbundleUnitPrice));			
				
				jQuery(formId).append(jQuery(inputServChgAmt));
				jQuery(formId).append(jQuery(inputServChg));
				jQuery(formId).append(jQuery(inputUsedQuota));
				jQuery(formId).append(jQuery(inputCheckE2Form));
				jQuery(formId).append(jQuery(inputApplicableTaxType));
				jQuery(formId).append(jQuery(inputCheckCForm));
				
				<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
					var batchNum = jQuery("<input>").attr("type", "hidden").attr("name", "batchNo_o_" + rowCount).val(batchNo);
					jQuery(formId).append(jQuery(batchNum));
					var days = jQuery("<input>").attr("type", "hidden").attr("name", "daysToStore_o_" + rowCount).val(days);
					jQuery(formId).append(jQuery(days));
				</#if>
				var taxList = [];
				taxList = data[rowCount]["taxList"]
				
				var taxListItem = jQuery("<input>").attr("type", "hidden").attr("name", "taxList_o_" + rowCount).val(taxList);
				jQuery(formId).append(jQuery(taxListItem));	
				if(taxList != undefined){
					for(var i=0;i<taxList.length;i++){
						var taxType = taxList[i];
						var taxPercentage = data[rowCount][taxType];
						var taxValue = data[rowCount][taxType + "_AMT"];
						
						var inputTaxTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_o_" + rowCount).val(taxPercentage);
						var inputTaxTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_AMT_o_"+ rowCount).val(taxValue);
						jQuery(formId).append(jQuery(inputTaxTypePerc));
						jQuery(formId).append(jQuery(inputTaxTypeValue));
					}
				}
				
				var taxList = [];
				taxList = data[rowCount]["taxList"];
   			}
			
   			<#if changeFlag?exists && changeFlag != "EditDepotSales">
   			 }
   			</#if>
		}
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{ 
			var ordetAdjTypeId = data2[rowCount]["orderAdjTypeId"];
			var adjAmt = parseFloat(data2[rowCount]["adjAmount"]);
	 		if (!isNaN(adjAmt)) {	 		
				var inputInv = jQuery("<input>").attr("type", "hidden").attr("name", "orderAdjTypeId_o_" + rowCount).val(ordetAdjTypeId);
				var inputAmt = jQuery("<input>").attr("type", "hidden").attr("name", "adjAmt_o_" + rowCount).val(adjAmt);
				jQuery(formId).append(jQuery(inputInv));				
				jQuery(formId).append(jQuery(inputAmt));
			}
		}
		var dataString = $("#indententryinit").serializeArray();
		$.each(dataString , function(i, fd) {
   			if(fd.name === "routeId"){
   				var route = jQuery("<input>").attr("type", "hidden").attr("name", "routeId").val(fd.value);
   				jQuery(formId).append(jQuery(route));
   			 }
		});
		
		//calculateTaxApplicability();
		
		<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
			var partyId = $("#partyId").val();
			var suplierPartyId = $("#suplierPartyId").val();
			var societyPartyId="";
			 	societyPartyId = $("#societyPartyId").val();
			 	
			var partyGeoId = $("#partyGeoId").val(); 	
			var billingType = $("#billingType").val();
			var orderTaxType= $("#orderTaxType").val();
			var poNumber = $("#PONumber").val();
			var acctgFlag = $("#disableAcctgFlag").val();
			var promoAdj = $("#promotionAdj").val();
			var productStoreId = $("#productStoreId").val();
			var cfcId = $("#cfcs").val();
			var schemeCategory = $("#schemeCategory").val();
			var contactMechId = $("#contactMechId").val();
			var transporterId = $("#transporterId").val();
			
			
			
			var orderMessage = $("#orderMessage").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
			var suplierParty = jQuery("<input>").attr("type", "hidden").attr("name", "suplierPartyId").val(suplierPartyId);
			var societyParty = jQuery("<input>").attr("type", "hidden").attr("name", "societyPartyId").val(societyPartyId);
			var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
			var promoField = jQuery("<input>").attr("type", "hidden").attr("name", "promotionAdjAmt").val(promoAdj);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			var cfc = jQuery("<input>").attr("type", "hidden").attr("name", "cfcId").val(cfcId);
			
			var tax = jQuery("<input>").attr("type", "hidden").attr("name", "orderTaxType").val(orderTaxType);
			var bilngType = jQuery("<input>").attr("type", "hidden").attr("name", "billingType").val(billingType);
			var orderMessageInPut = jQuery("<input>").attr("type", "hidden").attr("name", "orderMessage").val(orderMessage);
			var disableAcctgFlag = jQuery("<input>").attr("type", "hidden").attr("name", "disableAcctgFlag").val(acctgFlag);
			var schemeCategoryObj = jQuery("<input>").attr("type", "hidden").attr("name", "schemeCategory").val(schemeCategory);
			var partyGeo = jQuery("<input>").attr("type", "hidden").attr("name", "partyGeoId").val(partyGeoId);
			var contactMechId = jQuery("<input>").attr("type", "hidden").attr("name", "belowContactMechId").val(contactMechId);
			var transporterId = jQuery("<input>").attr("type", "hidden").attr("name", "transporterId").val(transporterId);
			<#if orderId?exists>
				var order = '${orderId?if_exists}';
				var extOrder = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(order);		
				jQuery(formId).append(jQuery(extOrder));
			</#if>
			
			jQuery(formId).append(jQuery(party));
			jQuery(formId).append(jQuery(suplierParty));
			jQuery(formId).append(jQuery(societyParty));
			jQuery(formId).append(jQuery(bilngType));
			jQuery(formId).append(jQuery(POField));
			jQuery(formId).append(jQuery(promoField));
			jQuery(formId).append(jQuery(tax));
			jQuery(formId).append(jQuery(productStore));
			jQuery(formId).append(jQuery(cfc));
			jQuery(formId).append(jQuery(orderMessageInPut));
			jQuery(formId).append(jQuery(disableAcctgFlag));
			jQuery(formId).append(jQuery(schemeCategoryObj));
			jQuery(formId).append(jQuery(partyGeo));
			jQuery(formId).append(jQuery(contactMechId));
			jQuery(formId).append(jQuery(transporterId));
		</#if>
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	<#assign editClickHandlerAction =''>	
	<#--<#if changeFlag?exists && changeFlag=='supplDeliverySchedule'>
		 <#assign editClickHandlerAction='processSupplDeleverySchdule'>
	<#elseif changeFlag?exists && changeFlag=='ByProdGatePass'>
	      <#assign editClickHandlerAction='processSupplDeleverySchdule'>
	<#else>
		 <#assign editClickHandlerAction='processIndentEntryNew'>		 	
	</#if>-->
		function editClickHandlerEvent(row){
			showUDPPriceToolTip(data[row], row, userDefPriceObj);
			
		}
	
	<#--
	function editClickHandler(row) {
		if(enableSubmit){						
			enableSubmit = false;
			processChangeIndentInternal('indententry', '<@ofbizUrl>${editClickHandlerAction}</@ofbizUrl>', row);		
		}
		
	}
	-->
	
	function processIndentEntry(formName, action) {
		jQuery("#changeSave").attr( "disabled", "disabled");
		processIndentEntryInternal(formName, action);
		
	}
	
    function productFormatter(row, cell, value, columnDef, dataContext) {   
        return productIdLabelMap[value];
    }
    function productValidator(value,item) {
      
    	var currProdCnt = 1;
	  	for (var rowCount=0; rowCount < data.length; ++rowCount)
	  	{ 
			if (data[rowCount]['cProductName'] != null && data[rowCount]['cProductName'] != undefined && value == data[rowCount]['cProductName']) {
				++currProdCnt;
			}
	  	}
	  
	  	var invalidProdCheck = 0;
	  	for (var rowCount=0; rowCount < availableTags.length; ++rowCount)
	  	{  
			if (value == availableTags[rowCount]["label"]) {
				invalidProdCheck = 1;
			}
	  	}
      	//if (currProdCnt > 1) {
        	//return {valid: false, msg: "Duplicate Product " + value};      				
      	//}
      	if(invalidProdCheck == 0){
      		return {valid: false, msg: "Invalid Product " + value};
      	}
      
      	if (item != null && item != undefined ) {
      		item['cProductId'] = productLabelIdMap[value];
	  	}      
      	return {valid: true, msg: null};
    }
    
    //quantity validator
	function quantityFormatter(row, cell, value, columnDef, dataContext) { 
		if(value == null){
			return "";
		}
        return  value;
    }
	
	function rateFormatter(row, cell, value, columnDef, dataContext) { 
		var formatValue = parseFloat(value).toFixed(2);
        return formatValue;
    }
	
	 function adustmentFormatter(row, cell, value, columnDef, dataContext) {
        return orderAdjLabelJSON[value];
    }
	function orderAdjFormatter(value,item) {
      
      	var valueId = orderAdjLabelIdMap[value];
    	var currItemCnt = 1;
	  	for (var rowCount=0; rowCount < data2.length; ++rowCount)
	  	{ 
			if (data2[rowCount]['orderAdjTypeId'] != null && data2[rowCount]['orderAdjTypeId'] != undefined && valueId == data2[rowCount]['orderAdjTypeId']) {
				++currItemCnt;
			}
	  	}
	  	
	  	var invalidItemCheck = 0;
	  	for (var rowCount=0; rowCount < availableAdjTags.length; ++rowCount)
	  	{  
			if (valueId == availableAdjTags[rowCount]["value"]) {
				invalidItemCheck = 1;
			}
	  	}
      	if (currItemCnt > 1) {
        	return {valid: false, msg: "Duplicate Item " + value};      				
      	}
      	if(invalidItemCheck == 0){
      		return {valid: false, msg: "Invalid Item " + value};
      	}
      
      	if (item != null && item != undefined ) {
      		item['invoiceItemTypeId'] = invoiceAdjLabelIdMap[value];
	  	}      
      	return {valid: true, msg: null};
    }
	
	
	function quantityValidator(value ,item) {
		var quarterVal = value*4;
		var floorValue = Math.floor(quarterVal);
		var remainder = quarterVal - floorValue;
		var remainderVal =  Math.floor(value) - value;
	     if(remainder !=0 ){
			return {valid: false, msg: "packets should not be in decimals " + value};
		}
      return {valid: true, msg: null};
    }
	var mainGrid;		
	function setupGrid1() {
		var columns = [];
		var columns2 =null;
		var columns1 = [
			{id:"cProductName", name:"${uiLabelMap.Product}", field:"cProductName", width:300, minWidth:300, cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains" ,editor: AutoCompleteEditor, validator: productValidator, sortable:false ,toolTip:""},
			{id:"remarks", name:"Specifications", field:"remarks", width:150, minWidth:150, sortable:false, cssClass:"cell-title", focusable :true,editor:TextCellEditor},
			{id:"baleQuantity", name:"Qty(Nos)", field:"baleQuantity", width:50, minWidth:50, sortable:false, editor:FloatCellEditor},
			{id:"cottonUom", name:"${uiLabelMap.cottonUom}", field:"cottonUom", width:50, minWidth:50, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "KGs,Bale,Half-Bale,Bundle"},
			{id:"bundleWeight", name:"${uiLabelMap.BundleWtKgs}", field:"bundleWeight", width:110, minWidth:110, sortable:false, editor:FloatCellEditor},
			{id:"unitPrice", name:"${uiLabelMap.UnitPrice} (Bundle)", field:"unitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			{id:"quantity", name:"Qty(Kgs)", field:"quantity", width:80, minWidth:80, sortable:false, editor:FloatCellEditor},
			{id:"KgunitPrice", name:"${uiLabelMap.UnitPrice} (KGs)", field:"KgunitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			<#--{id:"schemeApplicability", name:"10% Scheme", field:"schemeApplicability", width:150, minWidth:150, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "Applicable,Not-Applicable"},-->
			{id:"amount", name:"Amt(Rs)", field:"amount", width:75, minWidth:75, sortable:false, formatter: rateFormatter,editor:FloatCellEditor},	
			<#--{id:"warning", name:"Warning", field:"warning", width:230, minWidth:230, sortable:false, cssClass:"readOnlyColumnAndWarningClass", focusable :false},-->
			{id:"taxAmt", name:"VAT/CST", field:"taxAmt", width:65, minWidth:65, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"SERVICE_CHARGE_AMT", name:"Serv Chgs", field:"SERVICE_CHARGE_AMT", width:65, minWidth:65, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"totPayable", name:"Total Payable", field:"totPayable", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"button", name:"Edit Tax", field:"button", width:60, minWidth:60, cssClass:"cell-title", focusable :false,
 				formatter: function (row, cell, id, def, datactx) { 
					return '<a href="#" class="button" onclick="editClickHandlerEvent('+row+')" value="Edit">Edit</a>'; 
 				}
 			},
 			{id:"quotaAvbl", name:"AvailableQuota(In Kgs)", field:"quota", width:50, minWidth:50, sortable:false, cssClass:"readOnlyColumnClass", focusable :false}
		];
		var selectedDate= $('#effectiveDate').val();
		var effDate=Date.parse(selectedDate);
		var targetDate=Date.parse("04/01/2016");
		if(effDate<targetDate && $('#schemeCategory').val()=="MGPS_10Pecent"){
		columns2=[{id:"usedQuota", name:"Quota(In Kgs)", field:"usedQuota", width:50, minWidth:50, sortable:false, cssClass:"cell-title", focusable :true,editor:TextCellEditor},
		    {id:"warning", name:"Warning", field:"warning", width:130, minWidth:130, sortable:false, cssClass:"readOnlyColumnAndWarningClass", focusable :false}];
		}else{
		  columns2=[{id:"usedQuota", name:"Quota(In Kgs)", field:"usedQuota", width:50, minWidth:50, sortable:false, cssClass:"readOnlyColumnClass", focusable :false},
		  {id:"warning", name:"Warning", field:"warning", width:130, minWidth:130, sortable:false, cssClass:"readOnlyColumnAndWarningClass", focusable :false}];
		}
		columns= columns1.concat(columns2);
		var data_view = new Slick.Data.DataView();
		grid = new Slick.Grid("#myGrid1", data, columns,options);
		var columnPicker= new Slick.Controls.ColumnPicker(columns, grid,options);
		
		var options = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
		

		grid = new Slick.Grid("#myGrid1", data,columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());        
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);

		
		// wire up model events to drive the grid
        if (data.length > 0) {			
			$(grid.getCellNode(0, 1)).click();
		}else{
			$(grid.getCellNode(0,0)).click();
		}
         grid.onKeyDown.subscribe(function(e) {
			var cellNav = 0;
			<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
				cellNav = 3;
			<#else>
				cellNav = 2;
			</#if>
			var cell = grid.getCellFromEvent(e);		
			if(e.which == $.ui.keyCode.UP && cell.row == 0){
				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();
			}
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == data.length && cell.cell == cellNav){
				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(0, 2)).click();
				e.stopPropagation();
			}else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == (data.length-1) && cell.cell == cellNav){
				grid.getEditController().commitCurrentEdit();
				grid.gotoCell(data.length, 0, true);
				$(grid.getCellNode(data.length, 0)).edit();
				
				e.stopPropagation();
			}
			
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.RIGHT) && cell 
				&& cell.row == data.length && cell.cell == cellNav){
  				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(cell.row, 0)).click();
				e.stopPropagation();
			
			}else if (e.which == $.ui.keyCode.RIGHT &&
				cell && (cell.cell == cellNav) && 
				cell.row != data.length) {
				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data.length) {
				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid.getEditController().commitCurrentEdit();
				if(cell.cell == 1 || cell.cell == 2){
					jQuery("#changeSave").click();
				}
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
            //here ESC to Save grid
        		if (cell && cell.cell == 0) {
        			$(grid.getCellNode(cell.row - 1, cellNav)).click();
        			return false;
        		}  
        		grid.getEditController().commitCurrentEdit();
				   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            
            else {
            	return false;
            }
        });
        
                
    	grid.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var productLabel = item['cProductName']; 
      		item['productNameStr'] = productLabel;
      		item['cProductId'] = productLabelIdMap[productLabel];  
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
    	grid.onBeforeEditCell.subscribe(function(e,args) {
	      	
	      	if (args.cell == 1) {
				var row = args.row;
				updateCurrentQuota(row);
			}
	      	
	    });
        grid.onCellChange.subscribe(function(e,args) {
        	
			if (args.cell == 2) {
   				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var udp = data[args.row]['KgunitPrice'];
				var uom = data[args.row]["cottonUom"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var price = 0;
				if(udp){
					var totalPrice = udp;
					price = totalPrice;
				}
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
				var roundedAmount;
				var kgUnitPrice;
				if(uom == "Bale"){
					roundedAmount = Math.round(baleQty*price*40);
				}
				if(uom == "Half-Bale"){
					roundedAmount = Math.round(baleQty*price*20);
				}
				if(uom == "KGs" ||uom == "Bundle"){				
					roundedAmount = Math.round(baleQty*price);
				}
				kgUnitPrice=price/bundleWeight;				
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				if(isNaN(kgUnitPrice)){
					kgUnitPrice = 0;
				}	
				
				quantityq = 0;
				if(uom == "Bale"){
					bundleWeight=4.54;
					quantityq = baleQty*bundleWeight*40;
				}
				if(uom == "Half-Bale"){
					bundleWeight=4.54;
					quantityq = baleQty*bundleWeight*20;
				}
				if(uom == "Bundle"){
					bundleWeight=4.54;
					quantityq = baleQty*bundleWeight;
				}
				
				if(uom == "KGs"){				
					quantityq = baleQty;
					bundleWeight=0;
				}
				
				data[args.row]["unitPrice"] = kgUnitPrice;
				data[args.row]["amount"] = roundedAmount;
				data[args.row]["totPayable"] = roundedAmount;
				data[args.row]["quantity"]=quantityq;
				
				var row = args.row;
				addServiceCharge(row);
				
				var taxAmt = data[row]["taxAmt"];
				
				var servChg = data[args.row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					roundedAmount = roundedAmount + servChg;
				}
				
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(row, roundedAmount);
				}
				else{
					getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, roundedAmount, $("#schemeCategory").val(), $("#orderTaxType").val());
				}
				
				grid.updateRow(args.row);
				updatePayableAmount(row);
				updateTotalIndentAmount();
        	       	
        	   	
        	}
			if (args.cell == 2) {
				var row = args.row;
				updateCurrentQuota(row);
			}
			if (args.cell == 3) {
				var row = args.row;
				var prod = data[args.row]["cProductId"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var uom = data[args.row]["cottonUom"];
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var unitPrice = parseFloat(data[args.row]["unitPrice"]);
				var udp = data[args.row]['unitPrice'];
				var kgprice = data[args.row]['KgunitPrice'];
				var kgUnitPrice;
				if(udp){
					var totalPrice = udp;
					price = totalPrice;
				}
				if(isNaN(price)){
					price = 0;
				}
				
				if(isNaN(baleQty)){
					baleQty = 1;
				}
				if(isNaN(bundleWeight)){
					qty = 0;
				}
				if(isNaN(unitPrice)){
					unitPrice = 0;
				}
				
				quantity = 0;
				if(uom == "Bale"){
					bundleWeight=4.54;
					quantity = baleQty*bundleWeight*40;
				}
				if(uom == "Half-Bale"){
					bundleWeight=4.54;
					quantity = baleQty*bundleWeight*20;
				}
				if(uom == "Bundle"){
					bundleWeight=4.54;
					quantity = baleQty*bundleWeight;
				}
				
				if(uom == "KGs"){				
					quantity = baleQty;
					bundleWeight=0;
				}
				if(uom == "Bale" ||uom == "Half-Bale" || uom == "Bundle"){
				kgUnitPrice=price/bundleWeight;
				
				}
				if(uom == "KGs" ){
				kgUnitPrice=price;
				}
				if(isNaN(kgUnitPrice)){
					kgUnitPrice = 0;
				}	
				data[args.row]["KgunitPrice"] = kgUnitPrice;
				data[args.row]["quantity"] = quantity;
				data[args.row]["baleQuantity"] = baleQty;
				data[args.row]["cottonUom"] = uom;
				data[args.row]["bundleWeight"] = bundleWeight;
				data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
				data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
				
				var row = args.row;
				addServiceCharge(row);
				
				var roundedAmount = quantity*kgUnitPrice;
				var servChg = data[args.row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					roundedAmount = roundedAmount + servChg;
				}
				
				
				var taxAmt = data[row]["taxAmt"];
				
				var servChg = data[row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					roundedAmount = roundedAmount + servChg;
				}
				
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(row, roundedAmount);
				}
				else{
					getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, roundedAmount, $("#schemeCategory").val(), $("#orderTaxType").val());
				}
				
				
				//getProductTaxDetails("VAT_SALE", $("#partyGeoId").val(), prod, row, (quantity*kgUnitPrice), $("#schemeCategory").val(), $("#orderTaxType").val());
				updatePayableAmount(row);
				grid.updateRow(args.row);
				updateCurrentQuota(row);
				
			}
			 if(args.cell == 4 || args.cell == 5 || args.cell == 6 ) {
				var row = args.row;
				var prod = data[args.row]["cProductId"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var uom = data[args.row]["cottonUom"];
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var unitPrice = parseFloat(data[args.row]["unitPrice"]);
				var udp = data[args.row]['unitPrice'];
				var kgprice = data[args.row]['KgunitPrice'];
				var kgUnitPrice;
				if(udp){
					var totalPrice = udp;
					price = totalPrice;
				}
				if(isNaN(price)){
					price = 0;
				}
				
				if(isNaN(baleQty)){
					baleQty = 1;
				}
				if(isNaN(bundleWeight)){
					qty = 0;
				}
				if(isNaN(unitPrice)){
					unitPrice = 0;
				}
				
				quantity = 0;
				if(uom == "Bale"){
					quantity = baleQty*bundleWeight*40;
				}
				if(uom == "Half-Bale"){
					quantity = baleQty*bundleWeight*20;
				}
				if(uom == "Bundle"){
					quantity = baleQty*bundleWeight;
				}
				
				if(uom == "KGs"){				
					quantity = baleQty;
					bundleWeight=0;
				}
				if(uom == "Bale" ||uom == "Half-Bale" || uom == "Bundle"){
				kgUnitPrice=price/bundleWeight;
				
				}
				if(uom == "KGs" ){
				kgUnitPrice=price;
				}
				if(isNaN(kgUnitPrice)){
					kgUnitPrice = 0;
				}	
				data[args.row]["KgunitPrice"] = kgUnitPrice;
				data[args.row]["quantity"] = quantity;
				data[args.row]["baleQuantity"] = baleQty;
				data[args.row]["cottonUom"] = uom;
				data[args.row]["bundleWeight"] = bundleWeight;
				data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
				data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
				
				var row = args.row;
				addServiceCharge(row);
				
				var roundedAmount = quantity*kgUnitPrice;
				
				var taxAmt = data[row]["taxAmt"];
				
				var servChg = data[row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					roundedAmount = roundedAmount + servChg;
				}
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(row, roundedAmount);
				}
				else{
					getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, roundedAmount, $("#schemeCategory").val(), $("#orderTaxType").val());
				}
				
				//getProductTaxDetails("VAT_SALE", $("#partyGeoId").val(), prod, row, (quantity*kgUnitPrice), $("#schemeCategory").val(), $("#orderTaxType").val());
				grid.updateRow(args.row);
				updatePayableAmount(row);
				updateCurrentQuota(row);
				
			}
			if(args.cell == 7){
				var kgprice = data[args.row]['KgunitPrice'];
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var uom = data[args.row]["cottonUom"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var upb=0;
				if(isNaN(kgprice)){
					kgprice = 0;
				}
				if(isNaN(bundleWeight)){
					bundleWeight = 0;
				}
				if(uom == "Bale" ||uom == "Half-Bale" || uom == "Bundle"){
				upb=kgprice*bundleWeight;
				
				}
				if(uom == "KGs" ){
				upb=kgprice;
				}
				var roundedAmount;
				if(uom == "Bale"){
					roundedAmount = Math.round(baleQty*upb*40);
				}
				if(uom == "Half-Bale"){
					roundedAmount = Math.round(baleQty*upb*20);
				}
				if(uom == "KGs" ||uom == "Bundle" ){				
					roundedAmount = Math.round(baleQty*upb);
				}
				
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				data[args.row]["amount"] = roundedAmount;
				data[args.row]["unitPrice"] = upb;
				
				
				var row = args.row;
				//updatePayablePrice(row);
				addServiceCharge(row);
				
				var taxAmt = data[row]["taxAmt"];
				
				var servChg = data[row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					roundedAmount = roundedAmount + servChg;
				}
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(row, roundedAmount);
				}
				else{
					getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, roundedAmount, $("#schemeCategory").val(), $("#orderTaxType").val());
				}
				
				//getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, price, $("#schemeCategory").val(), $("#orderTaxType").val());
				
				grid.updateRow(args.row);
				updatePayableAmount(row);
				updateTotalIndentAmount();
				
				
			
			
			}
			if(args.cell == 8){
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var uom = data[args.row]["cottonUom"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var qty = parseFloat(data[args.row]["quantity"]);
				var amt = parseFloat(data[args.row]["amount"]);
				var upb=0;
				
				var kgprice = 0;
				
				if(isNaN(bundleWeight)){
					bundleWeight = 0;
				}
				var bunble=0;
				if(uom == "Bale"){
				
				bunble=baleQty*40;
				}
				if(uom == "Half-Bale"){
				bunble=baleQty*20;
				}
				if(uom == "Bundle" || uom == "KGs" ){
				bunble=baleQty;
				}
				
				
				upb=amt/bunble;
				kgprice=amt/qty;
				
				data[args.row]["KgunitPrice"] = kgprice;
				data[args.row]["unitPrice"] = upb;
				
				
				var row = args.row;
				//updatePayablePrice(row);
				
				addServiceCharge(row);
				
				var taxAmt = data[row]["taxAmt"];
				
				var servChg = data[row]['SERVICE_CHARGE_AMT'];
				if(servChg){
					amt = amt + servChg;
				}
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(row, amt);
				}
				else{
					getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, amt, $("#schemeCategory").val(), $("#orderTaxType").val());
				}
				
				//getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, price, $("#schemeCategory").val(), $("#orderTaxType").val());
				
				grid.updateRow(args.row);
				updatePayableAmount(row);
				updateTotalIndentAmount();
				
				
			
			
			}
		
			
		}); 
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
				if (args.cell == 2 && data[args.row] != null) {
        		var item = data[args.row];   
				var prod = data[args.row]["cProductId"];
				var uomId = productUOMMap[prod];
				var uomLabel = uomLabelMap[uomId];
				item['uomDescription'] = uomLabel;     		 		
	      		grid.invalidateRow(data.length);
	      		grid.updateRow(args.row);
	      		grid.updateRowCount();
	      		grid.render();
	      		$(grid.getCellNode(args.row, 2)).click();
			}
			
		});
		
		grid.onValidationError.subscribe(function(e, args) {
        var validationResult = args.validationResults;
        var activeCellNode = args.cellNode;
        var editor = args.editor;
        var errorMessage = validationResult.msg;
        var valid_result = validationResult.valid;
        
        if (!valid_result) {
           $(activeCellNode).attr("tittle", errorMessage);
            }else {
           $(activeCellNode).attr("tittle", "");
        }

    });
    	//updateInlineTotalAmount();
		//updateProductTotalAmount();
		
		mainGrid = grid;
		for (var rowCount=0; rowCount < data.length; ++rowCount)
		{
		       updateCurrentQuota(rowCount);
		}
	}
		
	jQuery(function(){
	     // only setupGrid when BoothId exists
	     var boothId=$('[name=boothId]').val();
	     var partyId=$('[name=partyId]').val();
		 if(boothId || partyId){
		 	setupGrid1();
		 	//setupGrid2();
	     }
	    
			//  alert("=After==Setup==partyId==="+partyId+"==boothId=="+boothId);	
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
    	else { 
			$("#boothId").focus();      
		}  		
	});
	
	
// to show special related fields in form			
	
	$(document).ready(function(){
	
		(function blink() { 
		    $('.readOnlyColumnAndWarningClass').fadeOut(500).fadeIn(500, blink); 
		})();
		$('#boothId').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
		});
		     $(function() {
				$( "#indententryinit" ).validate();
			});	
			$("#boothId").autocomplete({ disabled: false });	
		
   			 $('#boothId').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
		});
		
				
		  $('#contactNumber').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});
			$('#name').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});
			$('#address1').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});
			$('#address2').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});
			
			
			$('#pinNumber').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});		  		
   				 
		
		
	});	
	 
	
	
	
	function populateContactDetails(contactInfo){
		jQuery("#name").val(contactInfo["name"]);		
		jQuery("#address1").val(contactInfo["address1"]);		
		jQuery("#address2").val(contactInfo["address2"]);		
		jQuery("#contactNumber").val(contactInfo["contactNumber"]); 
		jQuery("#pinNumber").val(contactInfo["postalCode"]);
	}
	
	function updatePayablePrice(row){
		var amount = data[row]['amount'];
		var taxAmt = data[row]['taxAmt'];
		if(isNaN(amount)){
			amount = 0;
		}
		if(isNaN(taxAmt)){
			taxAmt = 0;
		}
		
        data[row]["totPayable"] = amount + taxAmt; 
        grid.updateRow(row);
        updateTotalIndentAmount();
	}
	
	function updateTotalIndentAmount(){
		var totalAmount = 0;
		for (i = 0; i < data.length; i++) {
			totalAmount += data[i]["totPayable"];
		}
		var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
		var dispText = "";
		if(amt > 0 ){
			dispText = "<b>  [Indent Amt: Rs " +  amt + "]</b>";
		}
		else{
			dispText = "<b>  [Indent Amt: Rs 0 ]</b>";
		}
		
		jQuery("#totalAmount").html(dispText);
		
	}
	
	function updateCurrentQuota(row){
		var prod = data[row]["cProductId"];
		var schemeCategory = $("#schemeCategory").val();
		productCategory = productCategoryJSON[prod];
		data[row]["productCategory"] = productCategory;
		
		quota = parseFloat(productQuotaJSON[prod]);
		if(isNaN(quota)){
			quota = 0;
		}
		
		var usedQuota = 0
		for(var i=0;i<data.length;i++){
			var existingCategory = data[i]["productCategory"];
			if(productCategory == existingCategory){
				var takenQty = parseFloat(data[i]["quantity"]);
				if(isNaN(takenQty)){
					takenQty = 0;
				}
				usedQuota += takenQty;
				
				var lineQuota = quota - usedQuota;
				
				if(lineQuota < 0){
					data[i]["quota"] = 0;
					var finalUsedQuota = takenQty+lineQuota;
					if(finalUsedQuota<0){
					 data[i]["usedQuota"]=0;
					}
					
					if(schemeCategory == "MGPS_10Pecent"){
						data[i]["warning"] = 'Quota Exceeded';
					}
				}
				else{
					data[i]["quota"] = lineQuota;
					data[i]["usedQuota"] = takenQty;
					data[i]["warning"] = '';
				}
				
				grid.updateRow(i);
			}
		}
	}
	
	function addServiceCharge(row){
		var serviceChargePercent = $("#serviceChargePercent").val();
		var serviceChargeAmt = 0;
		
		if(serviceChargePercent != 'undefined' && serviceChargePercent != null){
	 		serviceChargeAmt = (serviceChargePercent/100) * data[row]["amount"];
	 	}
		data[row]["SERVICE_CHARGE"] = serviceChargePercent;
		data[row]["SERVICE_CHARGE_AMT"] = serviceChargeAmt;
		
		//data[row]["totPayable"] = data[row]["totPayable"] + serviceChargeAmt;
	}
	
	function updateServiceChargeAmounts(){
		var serviceChargePercent = $("#serviceChargePercent").val();
		if(serviceChargePercent != 'undefined' && serviceChargePercent != null){
			for (i = 0; i < data.length; i++) {
				var basicAmt = data[i]["amount"];
				var serviceChargeAmt = (serviceChargePercent/100) * basicAmt;
				data[i]["SERVICE_CHARGE"] = serviceChargePercent;
				data[i]["SERVICE_CHARGE_AMT"] = serviceChargeAmt;
				
				updateTax(i, (basicAmt + serviceChargeAmt));
				data[i]["totPayable"] = basicAmt + data[i]["taxAmt"] + serviceChargeAmt;
				grid.updateRow(i);
			}
		}
		
		updateTotalIndentAmount();
	}
	
	function updateTax(rowCount, baseAmt){
		
		
		var totTaxValue = 0;
		var taxList = [];
		taxList = data[rowCount]["taxList"]
		if(taxList != undefined){
			for(var i=0;i<taxList.length;i++){
				var taxType = taxList[i];
				var taxPercentage = data[rowCount][taxType];
				var taxValue = data[rowCount][taxType + "_AMT"];
				if(taxPercentage){
					if(baseAmt){
						taxValue = baseAmt*(taxPercentage/100);
					}
				}
				totTaxValue = totTaxValue + taxValue;
				
				data[rowCount][taxType + "_AMT"] = taxValue;
			}
		}
		data[rowCount]["taxAmt"] = totTaxValue;
	}
	
	function updatePayableAmount(row){
		
		var basicAmtVal = 0;
		var taxAmtVal = 0;
		var servChgVal = 0;
		
		var basicAmt = data[row]["amount"];
		var taxAmt = data[row]["taxAmt"];
		var servChg = data[row]["SERVICE_CHARGE_AMT"];
		
		if(basicAmt){
			basicAmtVal = basicAmt;
		}
		if(taxAmt){
			taxAmtVal = taxAmt;
		}
		if(servChg){
			servChgVal = servChg;
		}
		
		data[row]["totPayable"] = basicAmtVal + taxAmtVal + servChgVal;
		grid.updateRow(row);
	}
	
	function getProductTaxDetails(taxAuthorityRateTypeId, taxAuthGeoId, productId, row, totalAmt, schemeCategory, taxType){
         if( taxAuthGeoId != undefined && taxAuthGeoId != "" &&  taxType != undefined && taxType != "" ){	
	         $.ajax({
	        	type: "POST",
	         	url: "calculateTaxesByGeoId",
	       	 	data: {taxAuthGeoId: taxAuthGeoId, productId: productId } ,
	       	 	dataType: 'json',
	       	 	async: true,
	    	 	success: function(result) {
	          		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	   	  				alert(result["_ERROR_MESSAGE_"]);
	      			}else{
	   	  				//var taxPercentage =result["taxPercentage"];
	   	  				//alert("taxPercentage = "+taxPercentage);
	   	  				var taxAuthProdCatList =result["taxAuthProdCatList"];
	   	  				//data[row]["taxPercent"] = (taxAuthProdCatList[0]).taxPercentage;
	   	  				
	   	  				var vatSurcharges =result["vatSurcharges"];
	   	  				var cstSurcharges =result["cstSurcharges"];
	   	  				var vatPercent =result["vatPercent"];
	   	  				var cstPercent =result["cstPercent"];
	   	  				
	   	  				data[row]["DEFAULT_VAT"] = vatPercent;
	   	  				data[row]["DEFAULT_CST"] = cstPercent;
	   	  				data[row]["DEFAULT_VAT_AMT"] = (vatPercent) * totalAmt/100;
	   	  				data[row]["DEFAULT_CST_AMT"] = (cstPercent) * totalAmt/100;
	   	  				
	   	  				data[row]["VAT_SURCHARGE"] = 0;
						data[row]["VAT_SURCHARGE_AMT"] = 0;
	   	  				
	   	  				var totalTaxAmt = 0;
	   	  				var vatSurchargeList = [];
	   	  				var taxList = [];
	   	  				vatSurchargeList.push("VAT_SURCHARGE");
	   	  				taxList.push("VAT_SURCHARGE");
	   	  				
	   	  				for(var i=0 ; i<vatSurcharges.length ; i++){
	   	  					var taxItem = vatSurcharges[i];
							var surchargeAmt = 0;
							surchargeAmt = (taxItem.taxPercentage) * ( (vatPercent) * totalAmt/100)/100;
							data[row][taxItem.taxAuthorityRateTypeId] = taxItem.taxPercentage;
							data[row][taxItem.taxAuthorityRateTypeId  + "_AMT"] = surchargeAmt;
							
							//vatSurchargeList.push(taxItem.taxAuthorityRateTypeId);
							
							totalTaxAmt += surchargeAmt;
							
							//taxList.push(taxItem.taxAuthorityRateTypeId);
	   	  				}
	   	  				
	   	  				var totalAmount = 0;
						for (i = 0; i < data.length; i++) {
							totalAmount += data[i]["amount"];
						}
						var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
	   	  				
	   	  				
	   	  				//var taxList = [];
	   	  				taxList.push("VAT_SALE");
	   	  				taxList.push("CST_SALE");
	   	  				if(taxType == "Intra-State"){
	   	  					data[row]["VAT_SALE"] = vatPercent;
	   	  					data[row]["VAT_SALE_AMT"] = (vatPercent) * totalAmt/100;
	   	  					totalTaxAmt += (vatPercent) * totalAmt/100;
	   	  					
	   	  					data[row]["CST_SALE"] = 0;
	   	  					data[row]["CST_SALE_AMT"] = 0;
	   	  					
	   	  				}
	   	  				if(taxType == "Inter-State"){
	   	  					data[row]["CST_SALE"] = cstPercent;
	   	  					data[row]["CST_SALE_AMT"] = (cstPercent) * totalAmt/100;
	   	  					totalTaxAmt += (cstPercent) * totalAmt/100;
	   	  					
	   	  					data[row]["VAT_SALE"] = 0;
	   	  					data[row]["VAT_SALE_AMT"] = 0;
	   	  				}
	   	  				
	   	  				<#--
	   	  				for(var i=0 ; i<taxAuthProdCatList.length ; i++){
							var taxItem = taxAuthProdCatList[i];
							
							totalTaxAmt += (taxItem.taxPercentage) * totalAmt/100;
							
							
							//item['cProductId'] = productLabelIdMap[value];
							data[row][taxItem.taxAuthorityRateTypeId] = taxItem.taxPercentage;
							data[row][taxItem.taxAuthorityRateTypeId  + "_AMT"] = (taxItem.taxPercentage) * totalAmt/100;
							
							taxList.push(taxItem.taxAuthorityRateTypeId);
						}
						-->
	   	  				
	   	  				data[row]["taxList"] = taxList;
	   	  				data[row]["vatSurchargeList"] = vatSurchargeList;
	   	  				data[row]["taxAmt"] = totalTaxAmt;
	   	  				
	   	  				//data[row]["totPayable"] = totalAmt + totalTaxAmt;
	   	  				//addServiceCharge(row);
	   	  				
	   	  				updatePayableAmount(row);	
	   	  				
	   	  				grid.updateRow(row);
	   	  				
	   	  				updateTotalIndentAmount();
	   	  				//data[row]["remarks"].setActiveCell();
	   	  				return false; 
	  				}
	           
	      		} ,
	     	 	error: function() {
	      	 		alert(result["_ERROR_MESSAGE_"]);
	     	 	}
	    	});
	    }	
	    else{
	    	data[row]["VAT_SURCHARGE"] = 0;
			data[row]["VAT_SURCHARGE_AMT"] = 0;
			
			data[row]["CST_SALE"] = 0;
	   	  	data[row]["CST_SALE_AMT"] = 0;
	   	  	
	   	  	data[row]["VAT_SALE"] = 0;
	   	  	data[row]["VAT_SALE_AMT"] = 0;
	   	  	
	   	  	//data[row]["totPayable"] = totalAmt;
	   	  	
			var taxList = [];
			taxList.push("VAT_SALE");
	   	  	taxList.push("CST_SALE");
	   	  	taxList.push("VAT_SURCHARGE");
	   	  	
	   	  	var vatSurchargeList = [];		
			vatSurchargeList.push("VAT_SURCHARGE");
			
			data[row]["taxList"] = taxList;
			data[row]["vatSurchargeList"] = vatSurchargeList;
			data[row]["taxAmt"] = 0;
	   	  		
	   	  		
	   	  	$("#orderTaxType").val("Intra-State");
	   	  	
	   	  	updatePayableAmount(row);	
	   	  				
			//addServiceCharge(row);
	   	  	grid.updateRow(row);
	   	  	
	   	  	updateTotalIndentAmount();
	    }			
	}
	
</script>			