
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
		//animation: blinker 1.7s cubic-bezier(.5, 0, 1, 1) infinite alternate; 
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
	    font:8em Futura, �Century Gothic�, AppleGothic, sans-serif;
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
	var savedOrderId;
	var partyUsedQuotaJSON = ${StringUtil.wrapString(partyUsedQuotaJSON)!'{}'};
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
	var availableIndCustTags = ${StringUtil.wrapString(indcustomerJson)!'{}'};
    var partyPsbNumber = ${StringUtil.wrapString(indcustomerPsbNumJson)!'{}'};
    var indcustomerLabelPsbNumMap = ${StringUtil.wrapString(indcustomerLabelPsbNumJson)!'{}'};
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
	var savedresult=false;
	function processTempIndentEntryInternal(formName, action) {
	 $('div#saveIndentEntry_spinner').show();
	    var saveButtonJson=[];
	    var rowCount=data.length;
	    for (var rowCount=0; rowCount < data.length; ++rowCount)
		{
		     saveButtonJson.push({"custId" : data[rowCount]["customerId"],
				                  "productId" : data[rowCount]["cProductId"],
				                  "remarks" : data[rowCount]["remarks"],
				                  "baleQuantity" : data[rowCount]["baleQuantity"],
				                  "cottonUom" : data[rowCount]["cottonUom"],
				                  "bundleWeight" : data[rowCount]["bundleWeight"],
				                  "unitPrice" : data[rowCount]["KgunitPrice"],
				                  "quantity" : data[rowCount]["quantity"],
				                  "bundleUnitPrice" : data[rowCount]["unitPrice"],
				                  "amount" : data[rowCount]["amount"]
				                }); 
		}
	 	var dataString = {"ItemJson": saveButtonJson,
	 					  "rowCount":rowCount,
						  "schemeCategory":$("#schemeCategory").val(),
						  "effectiveDate":$("#effectiveDate").val(),
						  "partyId": $("#partyId").val(),
						  "orderId" : savedOrderId,
						  "suplierPartyId" : $("#suplierPartyId").val(),
						  "societyPartyId" : $("#societyPartyId").val(),
			 			  "partyGeoId" : $("#partyGeoId").val(), 	
						  "billingType" : $("#billingType").val(),
						  "orderTaxType": $("#orderTaxType").val(),
						  "poNumber" : $("#PONumber").val(),
						  "acctgFlag" : $("#disableAcctgFlag").val(),
						  "promoAdj" : $("#promotionAdj").val(),
						  "productStoreId" : $("#productStoreId").val(),
						  "cfcId" : $("#cfcs").val(),
						  "orderMessage" : $("#orderMessage").val(),
			 			  "schemeCategory" : $("#schemeCategory").val(),
						  "contactMechId" : $("#contactMechId").val(),
			 			  "transporterId" : $("#transporterId").val(),
			              "tallyReferenceNo" : $("#tallyReferenceNo").val(),
			 			  "ediTallyRefNo" : $("#ediTallyRefNo").val(),
						  "orderMessage" : $("#orderMessage").val(),		 		
						};
	 
			 $.ajax({
					     type: "POST",
					     url: "saveBranchSaleOrder",
					     data: dataString ,
					     dataType: 'json',
					     async: false,
					     success: function(result) {
					               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
					            	   alert(result["_ERROR_MESSAGE_"]);
					               }else{  
					                	savedOrderId=result["orderId"];
					                	$('div#saveIndentEntry_spinner').hide();
					                	
					                	if(savedOrderId){
					                	savedresult=true;
					                	jQuery('div#saveIndentEntry_spinner').after("<div id='FNLabel' class='center' font-weight='bold'><font color='green' font-weight='bold'><p style='font-size:15px'>Saved Successfully..!</p></font></div>");
								       	setTimeout(function () {
								        $('#FNLabel').remove();
								       	}, 3000);
								       }	
					               }
					             } ,
					             error: function(result) {
				            	 	alert(result["_ERROR_MESSAGE_"]);
				            	 }
			}); 	
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
			var customerId = data[rowCount]["customerId"];
			var balqty = parseFloat(data[rowCount]["baleQuantity"]);
			var yarnUOM = data[rowCount]["cottonUom"];
			var bundleWeight = data[rowCount]["bundleWeight"];
			var batchNo = data[rowCount]["batchNo"];
			var days = data[rowCount]["daysToStore"];
			var bundleUnitPrice = data[rowCount]["unitPrice"];
			var remarks = data[rowCount]["remarks"];
			var unitPrice = data[rowCount]["KgunitPrice"];			
			var taxAmt = data[rowCount]["taxAmt"];
			var serviceCharge = data[rowCount]["SERVICE_CHARGE"];
			var serviceChargeAmt = data[rowCount]["SERVICE_CHARGE_AMT"];
			
			var checkE2Form = data[rowCount]["checkE2Form"];
			var applicableTaxType = data[rowCount]["applicableTaxType"];
			var checkCForm = data[rowCount]["checkCForm"];
			var usedQuota = data[rowCount]["usedQuota"];
			
			$("#orderTaxType").val(applicableTaxType);
			
			<#if changeFlag?exists && changeFlag != "EditDepotSales">
			 if(qty>0){
			</#if>
	 		if (!isNaN(prodId)) {	 		
				var inputcustomerId = jQuery("<input>").attr("type", "hidden").attr("name", "customerId_o_" + rowCount).val(customerId); 			
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputBaleQty = jQuery("<input>").attr("type", "hidden").attr("name", "baleQuantity_o_" + rowCount).val(balqty);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				var inputYarnUOM = jQuery("<input>").attr("type", "hidden").attr("name", "yarnUOM_o_" + rowCount).val(yarnUOM);
				var inputBundleWeight = jQuery("<input>").attr("type", "hidden").attr("name", "bundleWeight_o_" + rowCount).val(bundleWeight);
				var inputUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "unitPrice_o_" + rowCount).val(unitPrice);
				var inputBundleUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "bundleUnitPrice_o_" + rowCount).val(bundleUnitPrice);
				var inputRemarks = jQuery("<input>").attr("type", "hidden").attr("name", "remarks_o_" + rowCount).val(remarks);
				var inputServChgAmt = jQuery("<input>").attr("type", "hidden").attr("name", "serviceChargeAmt_o_" + rowCount).val(serviceChargeAmt);
				var inputServChg = jQuery("<input>").attr("type", "hidden").attr("name", "serviceCharge_o_" + rowCount).val(serviceCharge);
				
				var inputCheckE2Form = jQuery("<input>").attr("type", "hidden").attr("name", "checkE2Form_o_" + rowCount).val(checkE2Form);
				var inputApplicableTaxType = jQuery("<input>").attr("type", "hidden").attr("name", "applicableTaxType_o_" + rowCount).val(applicableTaxType);
				var inputCheckCForm = jQuery("<input>").attr("type", "hidden").attr("name", "checkCForm_o_" + rowCount).val(checkCForm);
				var inputUsedQuota = jQuery("<input>").attr("type", "hidden").attr("name", "usedQuota_o_" + rowCount).val(usedQuota);
				
				jQuery(formId).append(jQuery(inputRemarks));
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputcustomerId));				
				jQuery(formId).append(jQuery(inputBundleUnitPrice));				
				jQuery(formId).append(jQuery(inputBaleQty));
				jQuery(formId).append(jQuery(inputYarnUOM));
				jQuery(formId).append(jQuery(inputBundleWeight));
				jQuery(formId).append(jQuery(inputQty));
				jQuery(formId).append(jQuery(inputUnitPrice));
				
				jQuery(formId).append(jQuery(inputServChgAmt));
				jQuery(formId).append(jQuery(inputServChg));
				
				jQuery(formId).append(jQuery(inputCheckE2Form));
				jQuery(formId).append(jQuery(inputApplicableTaxType));
				jQuery(formId).append(jQuery(inputCheckCForm));
				jQuery(formId).append(jQuery(inputUsedQuota));
				
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
		
		var dataString = $("#indententryinit").serializeArray();
		
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
			var orderMessage = $("#orderMessage").val();
			var schemeCategory = $("#schemeCategory").val();
			var contactMechId = $("#contactMechId").val();
			var transporterId = $("#transporterId").val();
			var tallyReferenceNo = $("#tallyReferenceNo").val();
			var ediTallyRefNo = $("#ediTallyRefNo").val();
			
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
			var tallyReferenceNo = jQuery("<input>").attr("type", "hidden").attr("name", "tallyReferenceNo").val(tallyReferenceNo);
			var ediTallyRefNo = jQuery("<input>").attr("type", "hidden").attr("name", "ediTallyRefNo").val(ediTallyRefNo);
			
			if(savedOrderId &&  savedOrderId!=undefined){
			   var savedorder = savedOrderId; 
			   var extOrder = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(savedorder);		
				jQuery(formId).append(jQuery(extOrder));
			}
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
			jQuery(formId).append(jQuery(tallyReferenceNo));
			jQuery(formId).append(jQuery(ediTallyRefNo));
			
			
			
		</#if>
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	<#assign editClickHandlerAction =''>	
	
	function editClickHandlerEvent(row){
		showUDPPriceToolTip(data[row], row, userDefPriceObj);
	}
	function clickCustomerDetails(row){
		showCustomerDetailsToolTip(data[row], row, userDefPriceObj);
	}
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
			if (data[rowCount]['cProductName'] != null && data[rowCount]['cProductName'] != undefined ) {
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
			{id:"customerName", name:"Customer", field:"customerName", width:250, minWidth:250, cssClass:"cell-title", url: "LookupIndividualPartyName?branchId=${parameters.branchId}", regexMatcher:"contains" ,editor: AutoCompleteEditorAjax, sortable:false ,toolTip:""},
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
 			{id:"button", name:"View Customer Details", field:"button", width:60, minWidth:60, cssClass:"cell-title", focusable :false,
 				formatter: function (row, cell, id, def, datactx) { 
					return '<a href="#" class="button" onclick="clickCustomerDetails('+row+')" value="View">View</a>'; 
 				}
 			},
			{id:"quotaAvbl", name:"AvailableQuota(In Kgs)", field:"quota", width:50, minWidth:50, sortable:false, cssClass:"readOnlyColumnClass", focusable :false},

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
		

		grid = new Slick.Grid("#myGrid1", data, columns, options);
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
      		setCustomerId(args.item);
    	});
    	grid.onBeforeEditCell.subscribe(function(e,args) {
    	    setCustomerIdOnCustomerChange(args.item,args);
	    });
        grid.onCellChange.subscribe(function(e,args) {
		  
	     	setUndefinedValues(data[args.row]);
		 	grid.updateRow(args.row);
			var prod = data[args.row]["cProductId"];
			var quantity = parseFloat(data[args.row]["quantity"]);
			var kgUnitPrice = data[args.row]['KgunitPrice'];
			var uom = data[args.row]["cottonUom"];
			var balQuty = parseFloat(data[args.row]["baleQuantity"]);
			var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);	
			var price = parseFloat(data[args.row]["unitPrice"]);
			var amt = parseFloat(data[args.row]["amount"]);
			var roundedAmount=0;
			if( uom == "KGs" ){
			 	roundedAmount =calculateBundlePrice(balQuty,uom,kgUnitPrice);
			}else{
			 	roundedAmount =calculateBundlePrice(balQuty,uom,price);
			}

			if (args.cell == 3) {

		  		if(isNaN(roundedAmount)){
					roundedAmount = 0;
		  		}
		  		if(isNaN(kgUnitPrice)){
					kgUnitPrice = 0;
		  		}	
		  
		  		quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
		  		data[args.row]["quantity"]=quantity;
		  		data[args.row]["KgunitPrice"] = kgUnitPrice;
		  		data[args.row]["amount"] = roundedAmount;
		  		data[args.row]["totPayable"] = roundedAmount;
		  
			}
		else if(args.cell == 4){
			quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
			
			data[args.row]["KgunitPrice"] = kgUnitPrice;
			data[args.row]["quantity"] = quantity;
			data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
		
		}
		else if(args.cell == 5){
		  quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
		  kgUnitPrice=reverseCalculationBundleToKG(bundleWeight,price);
		  data[args.row]["KgunitPrice"] = kgUnitPrice;
		  data[args.row]["quantity"] = quantity;
		  data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
		  data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
		}
		else if(args.cell == 6){
		  kgUnitPrice=reverseCalculationBundleToKG(bundleWeight,price);
		  data[args.row]["KgunitPrice"] = kgUnitPrice;
		  data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
		  data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
		
		}
		else if(args.cell == 7){
		  baleQty=calculateKgs(quantity,uom,bundleWeight);
		  data[args.row]["baleQuantity"]=baleQty;
		  data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
		  data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
		}
		else if(args.cell == 8){
		  var upb=0;
		  var roundedAmount=0;
	      upb=reverseCalculationKGToBundle(bundleWeight,kgUnitPrice);
	      if( uom == "KGs" ){
		   		roundedAmount=calculateBundlePrice(balQuty,uom,kgUnitPrice);
			}else{
			 	roundedAmount=calculateBundlePrice(balQuty,uom,upb);
			}
		  data[args.row]["amount"] = roundedAmount;
		  data[args.row]["unitPrice"] = upb;
		}
		else if(args.cell == 9){
		    var upb=0;
			var bundle=0;
				if(uom == "Bale"){
				bundle=balQuty*40;
				}
				if(uom == "Half-Bale"){
				bundle=balQuty*20;
				}
		if(uom == "Bundle" || uom == "KGs" ){
				bundle=balQuty;
				}
				upb=amt/bundle;
				kgprice=amt/quantity;
			data[args.row]["KgunitPrice"] = kgprice;
			data[args.row]["unitPrice"] = upb;
		}
	  if(args.cell != 1 && args.cell != 2){
		addServiceCharge(args.row);
				
		var taxAmt = data[args.row]["taxAmt"];
		roundedAmount=data[args.row]["amount"];
		var servChg = data[args.row]['SERVICE_CHARGE_AMT'];
		if(servChg){
			roundedAmount = roundedAmount + servChg;
		}
				
		if(taxAmt != undefined && taxAmt != 0){
			updateTax(args.row, roundedAmount);
		}
		else{
			getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, args.row, roundedAmount, $("#schemeCategory").val(), $("#orderTaxType").val());
		}
		grid.updateRow(args.row);
		updatePayablePrice(args.row);
		updateTotalIndentAmount();
		updateCurrentQuota(args);
	 } 
	 });
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
			if (args.cell == 1 ) {
   				var currentrow=args.row;
   				if(data[currentrow-1] != undefined && data[currentrow-1]["cProductId"] != undefined){
		       		var prod=data[currentrow-1]["cProductId"];
		       		data[args.row]['cProductId'] = data[currentrow-1]["cProductId"];
		       		data[args.row]['cProductName'] = data[currentrow-1]["cProductName"];
		       		data[args.row]['remarks'] = data[currentrow-1]["remarks"];
		       		data[args.row]['amount'] = data[currentrow-1]["amount"];
		       		data[args.row]['unitPrice'] = data[currentrow-1]["unitPrice"];
				   	data[args.row]['KgunitPrice'] = data[currentrow-1]["KgunitPrice"];
				   	
	      		 	grid.updateRow(args.row);
				   	
  		 		}

   			}
   			//if (args.cell == 1 ) {
   			 //  if(data[currentrow] != undefined && data[currentrow]["customerId"] != undefined){
  		 	//	  getCustomerDetails(args);
  		 	//	}
   			//}
   			
   			if (args.cell == 2 ) {
   				var currentrow=args.row;
   				if(data[currentrow] != undefined && data[currentrow]["cProductId"] != undefined){
		       		var prod=data[currentrow]["cProductId"];
		       		var custId = data[args.row]['customerId']; 
				   	var qut=0;
				   	if(data[args.row]['customerId'] != "undefined"){
				   		var dataString = {"partyId": data[args.row]['customerId'],"productId":data[args.row]['cProductId'],
								   		"schemeCategory":$("#schemeCategory").val()
								 		};
					     $.ajax({
					             type: "POST",
					             url: "getPartyQuotaList",
					             data: dataString ,
					             dataType: 'json',
					             async: false,
					         	success: function(result) {
					               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
					            	   alert(result["_ERROR_MESSAGE_"]);
					               }else{  
					                	productsQuotaList=result['productQuotaJSON'];
					                	if(productsQuotaList[prod] != "undefined" && productsQuotaList[prod] != null){
					                		qut=productsQuotaList[prod];
					                	}
					                	if(isNaN(qut)){
											qut = 0;
										}
										var pervUsedQuota = 0;
										var productCategory = productCategoryJSON[prod];
										if(productCategory != 'undefined' && productCategory != null){
											if(partyUsedQuotaJSON[custId] != 'undefined' && partyUsedQuotaJSON[custId] != null){
												var schemeUsedQuotaObj = partyUsedQuotaJSON[custId];
												if(schemeUsedQuotaObj[productCategory] != 'undefined' && schemeUsedQuotaObj[productCategory] != null){
													pervUsedQuota = schemeUsedQuotaObj[productCategory]["orderUsedQuota"];
												}
												
											}
										}
										
										qut = qut + pervUsedQuota;
										
					                	data[args.row]["quota"] = 0;
						       			data[args.row]['quantity'] =qut;
						       			data[args.row]['usedQuota'] =qut;
						       			data[args.row]['baleQuantity'] =qut;
						       			utprice=data[currentrow]["unitPrice"];
						       			amount=qut*utprice;
						       			data[args.row]['amount'] = amount;
						      		 	grid.updateRow(args.row);
						      		 	//data[args.row]["remarks"].gotoCell();
						      		 	
						      		 	var row = args.row;
										getProductTaxDetails("VAT_SALE", $("#branchGeoId").val(), prod, row, amount, $("#schemeCategory").val(), $("#orderTaxType").val());
				   	
					               }
					             } ,
					             error: function() {
				            	 	alert(result["_ERROR_MESSAGE_"]);
				            	 }
				            	
					        }); 				
							
							
			      	 }
  		 		}
   			}
			
			
			if (args.cell == 12 && data[args.row] != null) {
        		grid.invalidateRow(data.length);
	      		grid.updateRow(args.row+1);
	      		grid.updateRowCount();
	      		grid.render();
	      		$(grid.getCellNode(args.row+1, 1)).click();
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
		//(function blink() { 
		//    $('.readOnlyColumnAndWarningClass').fadeOut(500).fadeIn(500, blink); 
		//})();
		$(function() {
			$( "#indententryinit" ).validate();
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
		var totalDiscount = 0;
		var totalPayable = 0;
		var totalQuantity = 0;
		for (i = 0; i < data.length; i++) {
			totalAmount += data[i]["totPayable"];
			var quotaApplicable = data[i]["usedQuota"];
			var exMillPrice = data[i]["unitPrice"];
			totalQuantity += data[i]["quantity"];
			if(isNaN(quotaApplicable)){
				quotaApplicable = 0;
			}
			if(isNaN(exMillPrice)){
				exMillPrice = 0;
			}
			totalDiscount = totalDiscount + (quotaApplicable*exMillPrice/10);
		}
		var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
		var dispText = "";
		if(amt > 0 ){
			dispText = "<b>&nbsp;  Value: Rs " +  amt + "&nbsp;</b>";
		}
		else{
			dispText = "<b>&nbsp;  Value: Rs 0 &nbsp;</b>";
		}
		
		totalPayable = totalAmount - totalDiscount;
		
		jQuery("#totalAmount").html(dispText);
		jQuery("#itemsSelected").html("<b> &nbsp; Selected: "+data.length+" &nbsp; </b>");
		jQuery("#totalDiscount").html("<b> &nbsp; Subsidy: Rs "+totalDiscount+" &nbsp; </b>");
		jQuery("#totalPayable").html("<b> &nbsp; Payable: Rs "+totalPayable+" &nbsp; </b>");
		jQuery("#totalQtyKgs").html("<b> &nbsp; Quantity(Kgs): "+totalQuantity+" &nbsp; </b>");
		
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
						
						data[row]["CST_SURCHARGE"] = 0;
						data[row]["CST_SURCHARGE_AMT"] = 0;
	   	  				
	   	  				var totalTaxAmt = 0;
	   	  				
	   	  				var cstSurchargeList = [];
	   	  				var vatSurchargeList = [];
	   	  				var taxList = [];
	   	  				
	   	  				cstSurchargeList.push("CST_SURCHARGE");
	   	  				
	   	  				vatSurchargeList.push("VAT_SURCHARGE");
	   	  				taxList.push("VAT_SURCHARGE");
	   	  				taxList.push("CST_SURCHARGE");
	   	  				
	   	  				for(var i=0 ; i<vatSurcharges.length ; i++){
	   	  					var taxItem = vatSurcharges[i];
							var surchargeAmt = 0;
							surchargeAmt = (taxItem.taxPercentage) * ( (vatPercent) * totalAmt/100)/100;
							data[row][taxItem.taxAuthorityRateTypeId] = taxItem.taxPercentage;
							data[row][taxItem.taxAuthorityRateTypeId  + "_AMT"] = surchargeAmt;
							
							totalTaxAmt += surchargeAmt;
	   	  				}
	   	  				
	   	  				for(var i=0 ; i<cstSurcharges.length ; i++){
	   	  					var taxItem = cstSurcharges[i];
							var surchargeAmt = 0;
							surchargeAmt = (taxItem.taxPercentage) * ( (cstPercent) * totalAmt/100)/100;
							data[row][taxItem.taxAuthorityRateTypeId] = taxItem.taxPercentage;
							data[row][taxItem.taxAuthorityRateTypeId  + "_AMT"] = surchargeAmt;
							
							totalTaxAmt += surchargeAmt;
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
	   	  				data[row]["cstSurchargeList"] = cstSurchargeList;
	   	  				data[row]["taxAmt"] = totalTaxAmt;
	   	  				
	   	  				updatePayableAmount(row);
	   	  				//data[row]["totPayable"] = totalAmt + totalTaxAmt;
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
			
			data[row]["CST_SURCHARGE"] = 0;
			data[row]["CST_SURCHARGE_AMT"] = 0;
			
			data[row]["CST_SALE"] = 0;
	   	  	data[row]["CST_SALE_AMT"] = 0;
	   	  	
	   	  	data[row]["VAT_SALE"] = 0;
	   	  	data[row]["VAT_SALE_AMT"] = 0;
	   	  	
	   	  	//data[row]["totPayable"] = totalAmt;
	   	  	
			var taxList = [];
			taxList.push("VAT_SALE");
	   	  	taxList.push("CST_SALE");
	   	  	taxList.push("VAT_SURCHARGE");
	   	  	taxList.push("CST_SURCHARGE");
	   	  	
	   	  	var vatSurchargeList = [];		
			vatSurchargeList.push("VAT_SURCHARGE");
			
			var cstSurchargeList = [];		
			cstSurchargeList.push("CST_SURCHARGE");
			
			data[row]["taxList"] = taxList;
			data[row]["vatSurchargeList"] = vatSurchargeList;
			data[row]["cstSurchargeList"] = cstSurchargeList;
			data[row]["taxAmt"] = 0;
	   	  		
	   	  		
	   	  	$("#orderTaxType").val("Intra-State");
	   	  	
	   	  	updatePayableAmount(row);			
			//addServiceCharge(row);
	   	  	grid.updateRow(row);
	   	  	
	   	  	updateTotalIndentAmount();
	   	  	
	   	  	
	    }					
	} 
	
	function getCustomerDetails(args){
	    var currentrow=args.row;
	    var partyId=data[args.row]['customerId']
	 	if(data[args.row]['customerId'] != "undefined"){
			var dataString="partyId=" + partyId+"&effectiveDate="+$("#effectiveDate").val() ;
	      	$.ajax({
	             type: "POST",
	             url: "getpartyContactDetails",
	           	 data: dataString ,
	           	 dataType: 'json',
	           	 async: false,
	        	 success: function(result) {
	             	if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	       	  		 	alert(result["_ERROR_MESSAGE_"]);
	          		 }else{
	       	  			contactDetails =result["partyJSON"];
	       	  			if( contactDetails != undefined && contactDetails != ""){
	       	  				customerContactList[partyId]=contactDetails;
						}
						      		 	
				 	}
				} ,
				error: function() {
					alert(result["_ERROR_MESSAGE_"]);
				}
				            	
			}); 				
		}
	}
	function updateCurrentQuota(args){
	
			if(args.cell == 3 || args.cell == 4 || args.cell == 5 || args.cell == 7){
			var totalQuota=data[args.row]["quota"]+data[args.row]["usedQuota"];
			   if(!isNaN(totalQuota)){
				    var qty1= data[args.row]["quantity"];
				    if(qty1<=totalQuota){
				       data[args.row]["quota"]=totalQuota-qty1;
				       data[args.row]["usedQuota"]=qty1;
				       data[args.row]["warning"] = '';
				    }
				    else{
				       data[args.row]["usedQuota"]=totalQuota;
				       data[args.row]["quota"]=0;
				       data[args.row]["warning"] = 'Quota Exceeded';
				    }
				    grid.updateRow(args.row);
				}
			}
	}
	
	 function calculateBundlePrice(balQuty,uom,org2){
	  var result=0;
	    if(uom == "Bale"){
			result = Math.round(balQuty*org2*40);
		}
		else if(uom == "Half-Bale"){
		    result = Math.round(balQuty*org2*20);
		}
		else if(uom == "Bundle"||uom == "KGs"){				
		    result = Math.round(balQuty*org2);
		}
		
		return result;
	 }
	 function reverseCalculationBundleToKG(bundleWeight,bundlePrice){
	  var result=0;
		result=bundlePrice/bundleWeight;
		return result;
	 }
	 function reverseCalculationKGToBundle(bundleWeight,kgPrice){
	  var result=0;
		result=kgPrice*bundleWeight;
		return result;
	 }
	 
	function calculateBundleWeight(balQuty,uom,org2){
	  var result=0;
	    if(uom == "Bale"){
			result = balQuty*org2*40;
		}
		else if(uom == "Half-Bale"){
		    result = balQuty*org2*20;
		}
		else if(uom == "Bundle"){				
		    result = balQuty*org2;
		}
		else if(uom == "KGs"){				
		    result = balQuty;
		}
		return result;
	 }
	function calculateKgs(quantity,uom,org2){
	  var result=0;
	    if(uom == "Bale"){
			 result=quantity/(org2*40);
			}
		else if(uom == "Half-Bale"){
				result=quantity/(org2*20);
			}
		else if(uom == "Bundle"){
				result = quantity/org2;
			}
		else if(uom == "KGs"){				
			result = quantity;
		    }
		return result;
	 }
	 function setUndefinedValues(row){
	 if ((row["bundleWeight"]) == undefined   ) {
			row["bundleWeight"] = 4.54;
		}
        if ((row["cottonUom"]) == undefined   ) {
			row["cottonUom"] = "KGs";
		}
		if(isNaN(row['unitPrice'])){
			row['unitPrice'] = 0;
		 }
		if(isNaN(row['KgunitPrice'])){
			row['KgunitPrice'] = 0;
		 }
		if(isNaN(row["quantity"])){
			row["quantity"] = 0;
		 }
		 if(isNaN(row["amount"])){
			row["amount"] = 0;
		 }
		  if(isNaN(row["totPayable"])){
			row["totPayable"] = 0;
		 }
		 if(isNaN(row["taxAmt"])){
			row["taxAmt"] = 0;
		 }
		 if(isNaN(row["SERVICE_CHARGE_AMT"])){
			row["SERVICE_CHARGE_AMT"] = 0;
		 }
	 }
	function setCustomerId(item){
      	var custId= item['customerName'];
      	var splited = (((custId.split("["))[1]).split("]"))[0];
      	var productLabel = item['cProductName']; 
      	item['productNameStr'] = productLabel;
      	var custmerID=indcustomerLabelPsbNumMap[custId];
      	item['customerId'] = splited;
      	item['psbNumber'] = partyPsbNumber[custmerID];
      	item['cProductId'] = productLabelIdMap[productLabel];  
      	grid.invalidateRow(data.length);
      	data.push(item);
      	grid.updateRowCount();
      	grid.render();
	}

 	function setCustomerIdOnCustomerChange(item,orgs){
		if(orgs.cell==1 && item!= undefined){
 			var custId= item['customerName'];
      		var splited = (((custId.split("["))[1]).split("]"))[0];
      		data[orgs.row]['customerId'] = splited;
 		}
    }
	
</script>			