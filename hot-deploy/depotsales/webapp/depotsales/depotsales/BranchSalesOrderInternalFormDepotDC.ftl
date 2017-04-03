
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
	
<#include "EditUDPPriceDepotTestDepot.ftl"/>		
			
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
			var taxAmt = data[rowCount]["taxAmt"];
			var serviceCharge = 0;
			var serviceChargeAmt = 0;
			
			if(data[rowCount]["SERVICE_CHARGE_SALE"]){
			 serviceCharge = data[rowCount]["SERVICE_CHARGE_SALE"];
			 serviceChargeAmt = data[rowCount]["SERVICE_CHARGE_SALE_AMT"];
		    }else{
		     serviceCharge = data[rowCount]["SERVICE_CHARGE"];
			 serviceChargeAmt = data[rowCount]["SERVICE_CHARGE_AMT"];
		     }	 
		     
		     
		     var changeTenPercentage = 0;
		     var changeTenPercentageAmout = 0;
		     if(data[rowCount]["TEN_PERCENT_SUBSIDY_SALE"]){
		      changeTenPercentage = data[rowCount]["TEN_PERCENT_SUBSIDY_SALE"];
		      changeTenPercentageAmout = data[rowCount]["TEN_PERCENT_SUBSIDY_SALE_AMT"];
		      }
			
			var checkE2Form = data[rowCount]["checkE2Form"];
			var applicableTaxType = data[rowCount]["applicableTaxType"];
			var checkCForm = data[rowCount]["checkCForm"];
			var usedQuota = data[rowCount]["usedQuota"];
			
			var Packaging = data[rowCount]["Packaging"];
			var packets = data[rowCount]["packets"];
			
			$("#orderTaxType").val(applicableTaxType);
			
			<#if changeFlag?exists && changeFlag != "EditDepotSales">
			 if(qty>0){
			</#if>
	 		if ((prodId)) {	 		
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
				
				var inputChangeTenPercentage = jQuery("<input>").attr("type", "hidden").attr("name", "changeTenPercentage_o_" + rowCount).val(changeTenPercentage);
				var inputChangeTenPercentageAmout = jQuery("<input>").attr("type", "hidden").attr("name", "changeTenPercentageAmout_o_" + rowCount).val(changeTenPercentageAmout);
				
				var inputPackaging = jQuery("<input>").attr("type", "hidden").attr("name", "Packaging_o_" + rowCount).val(Packaging);
			    var inputpackets = jQuery("<input>").attr("type", "hidden").attr("name", "packets_o_" + rowCount).val(packets);
			    
			    var purposeTypeId = jQuery("<input>").attr("type", "hidden").attr("name", "purposetypeId").val("DC_DEPOT_SALES");
                
                jQuery(formId).append(jQuery(inputPackaging));
                jQuery(formId).append(jQuery(inputpackets)); 
				
				
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
				jQuery(formId).append(jQuery(inputCheckE2Form));
				jQuery(formId).append(jQuery(inputApplicableTaxType));
				jQuery(formId).append(jQuery(inputCheckCForm));
				jQuery(formId).append(jQuery(inputUsedQuota));
				jQuery(formId).append(jQuery(inputChangeTenPercentage));
				jQuery(formId).append(jQuery(inputChangeTenPercentageAmout));
				jQuery(formId).append(jQuery(purposeTypeId));
				
				
				<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
					var batchNum = jQuery("<input>").attr("type", "hidden").attr("name", "batchNo_o_" + rowCount).val(batchNo);
					jQuery(formId).append(jQuery(batchNum));
					var days = jQuery("<input>").attr("type", "hidden").attr("name", "daysToStore_o_" + rowCount).val(days);
					jQuery(formId).append(jQuery(days));
				</#if>
				var taxList = [];
				//taxList = data[rowCount]["taxList"]
				
				taxList.push("VAT_SALE");
				taxList.push("CST_SALE");
				taxList.push("VAT_SURCHARGE");
				taxList.push("CST_SURCHARGE");
				
				var taxListItem = jQuery("<input>").attr("type", "hidden").attr("name", "taxList_o_" + rowCount).val(taxList);
				jQuery(formId).append(jQuery(taxListItem));	
				if(taxList != undefined){
					for(var i=0;i<taxList.length;i++){
						var taxType = taxList[i];
						
						var taxPercentage = 0;
						var taxValue = 0;
						
						if(taxType != "VAT_SURCHARGE" && taxType != "CST_SURCHARGE"){
						    taxPercentage = data[rowCount][taxType];
						    taxValue = data[rowCount][taxType + "_AMT"];
						}else{
						    taxPercentage = data[rowCount][taxType+ "_SALE"];
						    taxValue = data[rowCount][taxType + "_SALE_AMT"];
						}
						
						var inputTaxTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_o_" + rowCount).val(taxPercentage);
						var inputTaxTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_AMT_o_"+ rowCount).val(Math.round(taxValue));
						jQuery(formId).append(jQuery(inputTaxTypePerc));
						jQuery(formId).append(jQuery(inputTaxTypeValue));
					}
				}
				
				// Sale order Adjustments
				var orderAdjustmentsList = [];
				//orderAdjustmentsList = data[rowCount]["orderAdjustmentTypeList"]
				
				
				
				orderAdjustmentsList.push("CESS");
				orderAdjustmentsList.push("INSURANCE_CHGS");
				orderAdjustmentsList.push("OTHER_CHARGES");
				orderAdjustmentsList.push("PACKING_FORWARDIG");
				
				
				
				var orderAdjustmentItem = jQuery("<input>").attr("type", "hidden").attr("name", "orderAdjustmentsList_o_" + rowCount).val(orderAdjustmentsList);
				jQuery(formId).append(jQuery(orderAdjustmentItem));	
				if(orderAdjustmentsList != undefined){
					for(var i=0;i<orderAdjustmentsList.length;i++){
						var orderAdjType = orderAdjustmentsList[i];
						var adjPercentage = data[rowCount][orderAdjType+"_SALE"];
						var adjValue = data[rowCount][orderAdjType + "_SALE_AMT"];
						var isAssessableValue = data[rowCount][orderAdjType + "_INC_BASIC"];
						
						var inputOrderAdjTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_o_" + rowCount).val(adjPercentage);
						var inputOrderAdjTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_AMT_o_"+ rowCount).val(Math.round(adjValue));
						var inputOrderAdjTypeAssessable = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_INC_BASIC_o_"+ rowCount).val(isAssessableValue);
						
						jQuery(formId).append(jQuery(inputOrderAdjTypePerc));
						jQuery(formId).append(jQuery(inputOrderAdjTypeValue));
						jQuery(formId).append(jQuery(inputOrderAdjTypeAssessable));
					}
				}
				
				
				// For General Scheme, capturing purchase order details
				
				var purchaseBasicAmount = 0;
				if(data[rowCount]["purchaseBasicAmount"]){
					purchaseBasicAmount = data[rowCount]["purchaseBasicAmount"];
				}
				var inputPurchaseBasicAmount = jQuery("<input>").attr("type", "hidden").attr("name", "purchaseBasicAmount_o_"+ rowCount).val(purchaseBasicAmount);
				jQuery(formId).append(jQuery(inputPurchaseBasicAmount));		
				
				// Purchase taxes
				var purTaxList = [];
				var purTaxList1= [];
				
			//	purTaxList = data[rowCount]["purTaxList"];
				
				purTaxList.push("VAT_PUR");
				purTaxList.push("CST_PUR");
				purTaxList.push("VAT_SURCHARGE");
				purTaxList.push("CST_SURCHARGE");
				
				purTaxList1.push("VAT_SALE");
				purTaxList1.push("CST_SALE");
				purTaxList1.push("VAT_SURCHARGE");
				purTaxList1.push("CST_SURCHARGE");
				
				
				var purTaxListItem = jQuery("<input>").attr("type", "hidden").attr("name", "purTaxList_o_" + rowCount).val(purTaxList1);
				jQuery(formId).append(jQuery(purTaxListItem));	
				if(purTaxList != undefined){
					for(var i=0;i<purTaxList.length;i++){
						var taxType = purTaxList[i];
						
						var taxPercentage = 0;
						var taxValue = 0;
						
						if(taxType != "VAT_SURCHARGE" && taxType != "CST_SURCHARGE"){
						    taxPercentage = data[rowCount][taxType];
						    taxValue = data[rowCount][taxType + "_AMT"];
						}else{
						
						    taxPercentage = data[rowCount][taxType +"_PUR"];
						    taxValue = data[rowCount][taxType + "_PUR_AMT"];
						
						}
						
						taxType = taxType.replace("_PUR","_SALE");
						
						var purInputTaxTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_PUR_o_" + rowCount).val(taxPercentage);
						var purInputTaxTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_PUR_AMT_o_"+ rowCount).val(Math.round(taxValue));
						jQuery(formId).append(jQuery(purInputTaxTypePerc));
						jQuery(formId).append(jQuery(purInputTaxTypeValue));
					}
				}
				
				
				// Purchase Order Adjustments list
				var purOrderAdjustmentsList = [];
				//purOrderAdjustmentsList = data[rowCount]["purOrderAdjustmentTypeList"];
				
				
				purOrderAdjustmentsList.push("CESS");
				purOrderAdjustmentsList.push("INSURANCE_CHGS");
				purOrderAdjustmentsList.push("OTHER_CHARGES");
				purOrderAdjustmentsList.push("PACKING_FORWARDIG");
				
				
				var purOrderAdjustmentItem = jQuery("<input>").attr("type", "hidden").attr("name", "purOrderAdjustmentsList_o_" + rowCount).val(purOrderAdjustmentsList);
				jQuery(formId).append(jQuery(purOrderAdjustmentItem));	
				if(purOrderAdjustmentsList != undefined){
					for(var i=0;i<purOrderAdjustmentsList.length;i++){
						var orderAdjType = purOrderAdjustmentsList[i];
						var adjPercentage = data[rowCount][orderAdjType + "_PUR"];
						var adjValue = data[rowCount][orderAdjType + "_PUR_AMT"];
						var isAssessableValue = data[rowCount][orderAdjType + "_PUR_INC_BASIC"];
						
						var inputOrderAdjTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_PUR_o_" + rowCount).val(adjPercentage);
						var inputOrderAdjTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_PUR_AMT_o_"+ rowCount).val(Math.round(adjValue));
						var inputOrderAdjTypeAssessable = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_PUR_INC_BASIC_o_"+ rowCount).val(isAssessableValue);
						
						jQuery(formId).append(jQuery(inputOrderAdjTypePerc));
						jQuery(formId).append(jQuery(inputOrderAdjTypeValue));
						jQuery(formId).append(jQuery(inputOrderAdjTypeAssessable));
					}
				}
				
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
			var tallyReferenceNo = $("#tallyReferenceNo").val();
			var ediTallyRefNo = $("#ediTallyRefNo").val();
			var inventoryItemId = $("#inventoryItemId").val();
			
			var depotSalesFlag = "DepotSaleOrder";
			
			var onbehalfOff = $("#onbehalfOff").val();
			
			var editDestination = $("#editDestination").val();
			if($("#changeDesti").val())
			 editDestination = $("#changeDesti").val();
			
			
			
			var orderMessage = $("#orderMessage").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
			var suplierParty = jQuery("<input>").attr("type", "hidden").attr("name", "suplierPartyId").val(suplierPartyId);
			var societyParty = jQuery("<input>").attr("type", "hidden").attr("name", "societyPartyId").val(societyPartyId);
			var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
			var promoField = jQuery("<input>").attr("type", "hidden").attr("name", "promotionAdjAmt").val(promoAdj);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			var cfc = jQuery("<input>").attr("type", "hidden").attr("name", "cfcId").val(cfcId);
			
			var depotSalesFlagObj = jQuery("<input>").attr("type", "hidden").attr("name", "depotSalesFlag").val(depotSalesFlag);
			
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
			var inventoryItemId = jQuery("<input>").attr("type", "hidden").attr("name", "inventoryItemId").val(inventoryItemId);
			var onbehalfOfff = jQuery("<input>").attr("type", "hidden").attr("name", "onbehalfOff").val(onbehalfOff);
			var editDestinatioN = jQuery("<input>").attr("type", "hidden").attr("name", "editDestination").val(editDestination);
			
			var purchaseTitleTransferEnum = jQuery("<input>").attr("type", "hidden").attr("name", "purchaseTitleTransferEnumId").val($("#purchaseTitleTransferEnumId").val());
			var saleTitleTransferEnum = jQuery("<input>").attr("type", "hidden").attr("name", "saleTitleTransferEnumId").val($("#saleTitleTransferEnumId").val());
			var saleTaxType = jQuery("<input>").attr("type", "hidden").attr("name", "saleTaxType").val($("#saleTaxType").val());
			var purchaseTaxType = jQuery("<input>").attr("type", "hidden").attr("name", "purchaseTaxType").val($("#purchaseTaxType").val());
			
			
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
			jQuery(formId).append(jQuery(inventoryItemId));
			jQuery(formId).append(jQuery(depotSalesFlagObj));
			jQuery(formId).append(jQuery(onbehalfOfff));
			 jQuery(formId).append(jQuery(editDestinatioN));
			
			
			jQuery(formId).append(jQuery(purchaseTitleTransferEnum));
			jQuery(formId).append(jQuery(saleTitleTransferEnum));
			jQuery(formId).append(jQuery(saleTaxType));
			jQuery(formId).append(jQuery(purchaseTaxType));
			jQuery(formId).append(jQuery(purposeTypeId));
			
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
			showItemAdjustmentsAndTaxes(data[row], row, userDefPriceObj);
			
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
        //	return {valid: false, msg: "Duplicate Product " + value};      				
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
			{id:"cProductName", name:"${uiLabelMap.Product}", field:"cProductName", width:350, minWidth:350, cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains" ,editor: AutoCompleteEditor,cssClass:"readOnlyColumnClass" ,focusable :false, validator: productValidator, sortable:false ,toolTip:""},
			{id:"remarks", name:"Specifications", field:"remarks", width:150, minWidth:150, sortable:false, cssClass:"cell-title", focusable :true,editor:TextCellEditor},
			{id:"quantityOnHandTotal", name:"Stock", field:"quantityOnHandTotal", width:110, minWidth:110, sortable:false,cssClass:"readOnlyColumnClass" ,focusable :false,cssClass:"readOnlyColumnClass" ,focusable :false, editor:FloatCellEditor},
			<#-- {id:"bookedQuantity", name:"Booked Quantity", field:"bookedQuantity", width:110, minWidth:110, sortable:false,cssClass:"readOnlyColumnClass" ,focusable :false,cssClass:"readOnlyColumnClass" ,focusable :false, editor:FloatCellEditor}, -->
			{id:"availableQuantity", name:"Available Quantity", field:"availableQuantity", width:110, minWidth:110, sortable:false,cssClass:"readOnlyColumnClass" ,focusable :false,cssClass:"readOnlyColumnClass" ,focusable :false, editor:FloatCellEditor},
			{id:"baleQuantity", name:"Qty(Nos)", field:"baleQuantity", width:50, minWidth:50, sortable:false, editor:FloatCellEditor},
			{id:"cottonUom", name:"${uiLabelMap.cottonUom}", field:"cottonUom", width:50, minWidth:50, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "KGs"},
			<#--
			{id:"bundleWeight", name:"${uiLabelMap.BundleWtKgs}", field:"bundleWeight", width:110, minWidth:110, sortable:false,focusable :false,cssClass:"readOnlyColumnClass" , editor:FloatCellEditor},
			{id:"unitPrice", name:"${uiLabelMap.UnitPrice} (Bundle)", field:"unitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter,cssClass:"readOnlyColumnClass" ,focusable :false,  align:"right", editor:FloatCellEditor},
			-->
			{id:"package", name:"Packaging(KGS)", field:"Packaging", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			{id:"packets", name:"packets", field:"packets", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			
			{id:"quantity", name:"Wt.(Kgs)", field:"quantity", width:60, minWidth:60, sortable:false,focusable :false,cssClass:"readOnlyColumnClass" , editor:FloatCellEditor},
			{id:"KgunitPrice", name:"${uiLabelMap.UnitPrice} (KGs)", field:"KgunitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			<#--{id:"schemeApplicability", name:"10% Scheme", field:"schemeApplicability", width:150, minWidth:150, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "Applicable,Not-Applicable"},-->
			{id:"amount", name:"Amt.(Rs)", field:"amount", width:130, minWidth:130, sortable:false, formatter: rateFormatter,editor:FloatCellEditor,focusable :false,cssClass:"readOnlyColumnClass" },	
			{id:"taxAmt", name:"Tax", field:"taxAmt", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"SERVICE_CHARGE_AMT", name:"Serv Chgs", field:"SERVICE_CHARGE_AMT", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"OTH_CHARGES_AMT", name:"Oth Chgs", field:"OTH_CHARGES_AMT", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"SUBSIDY", name:"Subsidy", field:"SUBSIDY", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"totPayable", name:"Total Payable", field:"totPayable", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"button", name:"Edit Tax", field:"button", width:60, minWidth:60, cssClass:"cell-title", focusable :false,
 				formatter: function (row, cell, id, def, datactx) { 
					return '<a href="#" class="button" onclick="editClickHandlerEvent('+row+')" value="Edit">Edit</a>'; 
 				}
 			},
 			{id:"quotaAvbl", name:"AvailableQuota(In Kgs)", field:"quota", width:50, minWidth:50, sortable:false, cssClass:"readOnlyColumnClass" , focusable :false},
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
			var availableQuantity = parseFloat(data[args.row]["availableQuantity"]);
			var roundedAmount =calculateBundlePrice(balQuty,uom,price);
			
			/*
			if(args.cell != 7){
		  		if(uom!="KGs"){
		   			kgUnitPrice=price/bundleWeight;
		  		}
				else{
				    kgUnitPrice=price;
				}
		 	}
		 	*/

			if (args.cell == 2) {
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
			
			if (args.cell == 5) {
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
			else if(args.cell == 3){
				quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
				
				data[args.row]["KgunitPrice"] = kgUnitPrice;
				data[args.row]["quantity"] = quantity;
				data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
				data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			
			}
			else if(args.cell == 4){
			  	quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
			  	data[args.row]["quantity"] = quantity;
			 	data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			  	
			  	
			  	if(quantity > availableQuantity){
			
			   alert("Quantity is greater than stock available...!!");
			
			  data[args.row]["baleQuantity"] = "";
			  data[args.row]["quantity"] = "";
			}else{
			
			
			if(!isNaN(data[args.row]['Packaging']) && data[args.row]['Packaging']!=0){
			
			var Packaging = data[args.row]['Packaging'];
		    var quantity = data[args.row]['quantity'];
		    data[args.row]['packets'] = quantity/Packaging;
			}
			}
			  	
			}
			else if(args.cell == 5){
		    
			  	data[args.row]["KgunitPrice"] = kgUnitPrice;
			  	data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			}
			else if(args.cell == 6){
			
			  var Packaging = data[args.row]['Packaging'];
		    var quantity = data[args.row]['quantity'];
		    data[args.row]['packets'] = quantity/Packaging;
			
			  //	baleQty=calculateKgs(quantity,uom,bundleWeight);
			  	//data[args.row]["baleQuantity"]=baleQty;
			  	data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			}
			else if(args.cell == 7){
			  	var upb=0;
			  	if(uom == "Bale" ||uom == "Half-Bale" || uom == "Bundle"){
					upb=kgUnitPrice*bundleWeight;
				}
			  	else if(uom == "KGs" ){
					upb=kgUnitPrice;
			 	}
			  	var roundedAmount=calculateBundlePrice(balQuty,uom,upb);
			  	data[args.row]["amount"] = roundedAmount;
			  	//data[args.row]["unitPrice"] = upb;
			}
			else if(args.cell == 8){
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
			else if(args.cell == 9){
			
				data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			
			}
			if(args.cell != 1){
				addServiceCharge(args.row);
					
				updateOtherCharges(args.row);
				updateSaleBaseAmount(args.row);
				updateSubsidyAmt(args.row);	
						
				// Un comment this if we want to include service charge to taxes
								
				var saleBaseAmt = roundedAmount;
				if(data[args.row]["saleBaseAmt"]){
					saleBaseAmt = data[args.row]["saleBaseAmt"];
				}
					
				var taxAmt = data[args.row]["taxAmt"];
				
				/*
				if(taxAmt != undefined && taxAmt != 0){
					updateTax(args.row);
				}
				else{
					getProductPurchaseTaxDetails($("#supplierGeoId").val(), prod, args.row, saleBaseAmt, $("#purchaseTaxType").val());
					getProductTaxDetails($("#branchGeoId").val(), prod, args.row, saleBaseAmt, $("#saleTaxType").val());
				}
				*/
				updatePayableAmount(args.row);
				updateTotalIndentAmount();
				updateCurrentQuota(args.row);
				if($("#schemeCategory").val() == "General")
				updateServiceChargeAmounts();
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
			
			     if(args.row){
			    updateOtherCharges(args.row);
				updateSaleBaseAmount(args.row);
				updateSubsidyAmt(args.row);	
				updatePayableAmount(args.row);
				updateTotalIndentAmount();
				updateCurrentQuota(args.row);
				
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
	
	function updateSaleBaseAmount(row){
		var amount = 0;
		if(data[row]["amount"]){
			amount = data[row]["amount"];
		}
		var saleBaseAmt = amount;
		orderAdjustmentsList = data[row]["orderAdjustmentsList"]
		if(orderAdjustmentsList != undefined){
			for(var i=0;i<orderAdjustmentsList.length;i++){
				var adjType = orderAdjustmentsList[i]["orderAdjustmentTypeId"];
				var adjPercentage = data[row][adjType];
				var adjValue = data[row][adjType + "_AMT"];
				var adjIncBasic = data[row][adjType + "_INC_BASIC"];
				
				if(adjIncBasic && adjValue && adjIncBasic == "TRUE"){
					saleBaseAmt = saleBaseAmt + adjValue;
				}
			}
		}
		data[row]["saleBaseAmt"] = saleBaseAmt;
	}
	
	
	function updateOtherCharges(row){
		
		var amount = 0;
		if(data[row]["amount"]){
			amount = data[row]["amount"];
		}
				
		totAdjValue = 0;
		orderAdjustmentsList = data[row]["orderAdjustmentsList"]
		if(orderAdjustmentsList != undefined){
			for(var i=0;i<orderAdjustmentsList.length;i++){
				var adjType = orderAdjustmentsList[i]["orderAdjustmentTypeId"];
				var adjPercentage = data[row][adjType];
				var adjValue = data[row][adjType + "_AMT"];
				if(adjPercentage){
					if(amount){
						adjValue = amount*(adjPercentage/100);
					}
				}
				if(adjValue){
					totAdjValue = totAdjValue + adjValue;
					data[row][adjType + "_AMT"] = adjValue;
				}
			}
		}
		data[row]["OTH_CHARGES_AMT"] = totAdjValue;
	}
	
	function updateSubsidyAmt(row){
		
		var usedQuota = 0;
		if(data[row]["usedQuota"]){
			usedQuota = data[row]["usedQuota"];
		}
		var amount = 0;
		if(data[row]["amount"]){
			amount = data[row]["amount"];
		}
		var saleBaseAmt = amount;
		if(data[row]["saleBaseAmt"]){
			saleBaseAmt = data[row]["saleBaseAmt"];
		}
		var indentQty = 0;
		if(data[row]["quantity"]){
			indentQty = data[row]["quantity"];
		}
		
		var subsidy = (saleBaseAmt/indentQty)*usedQuota*.1;
		data[row]["SUBSIDY"]=subsidy;
	}
	
	
	function updateTotalIndentAmount(){
		var totalAmount = 0;
		var totalDiscount = 0;
		var totalPayable = 0;
		var totalQuantity = 0;
		for (i = 0; i < data.length; i++) {
			totalPayable += parseFloat(data[i]["totPayable"]);
			totalDiscount += parseFloat(data[i]["SUBSIDY"]);
			totalQuantity += data[i]["quantity"];
		}
		var totalAmount = parseFloat(Math.round((totalPayable) * 100) / 100) + parseFloat(Math.round((totalDiscount) * 100) / 100);
		
		jQuery("#totalAmount").html("<b> &nbsp; Selected: "+totalAmount+" &nbsp; </b>");
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
					data[i]["usedQuota"]=finalUsedQuota;
					if(finalUsedQuota<0){
						data[i]["usedQuota"]=0;
					 	data[i]["SUBSIDY"]=0;
					}
					if(schemeCategory == "MGPS_10Pecent"){
						data[i]["warning"] = 'Quota Exceeded';
					}
				}
				else{
					data[i]["quota"] = lineQuota;
					data[i]["usedQuota"] = takenQty;
					
					updateSubsidyAmt(i);
					
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
		var serviceChargePercent = 2;
		if(serviceChargePercent != 'undefined' && serviceChargePercent != null){
			for (i = 0; i < data.length; i++) {
				var basicAmt = data[i]["amount"];
				var serviceChargeAmt = (serviceChargePercent/100) * basicAmt;
				data[i]["SERVICE_CHARGE"] = serviceChargePercent;
				data[i]["SERVICE_CHARGE_AMT"] = serviceChargeAmt;
				
				updatePayableAmount(i);
				//data[i]["totPayable"] = basicAmt + data[i]["taxAmt"] + serviceChargeAmt;
				
				grid.updateRow(i);
			}
		}
		
		updateTotalIndentAmount();
	}
	
	function updateTax(row){
		
		var totTaxValue = 0;
		var saleBaseAmt = 0;
		if(data[row]["saleBaseAmt"]){
			saleBaseAmt = data[row]["saleBaseAmt"];
		}
		
		if(data[row]["defaultTaxMap"]){
			var defaultTaxMap = data[row]["defaultTaxMap"];
			var saleTitleTransferEnumId = $("#saleTitleTransferEnumId").val();
			var saleTaxList = transactionTypeTaxMap[saleTitleTransferEnumId];
			
			for(var i=0;i<saleTaxList.length;i++){
				var salesTax = saleTaxList[i];
				var saleTaxPercent = data[row][salesTax];
				var saleTaxAmount = 0;
				if(saleTaxPercent){
					saleTaxAmount = saleTaxPercent*(saleBaseAmt)*0.01;
					data[row][salesTax + "_AMT"] = saleTaxAmount;
					totTaxValue = totTaxValue + saleTaxAmount;
				}
				
				var surchargeList = defaultTaxMap[salesTax]["surchargeList"];
				for(var j=0;j<surchargeList.length;j++){
					var surchargeDetails = surchargeList[j];
					var surchargePercent = data[row][surchargeDetails.taxAuthorityRateTypeId];
					var surchargeAmount = 0;
					
					if(surchargePercent){
						surchargeAmount = surchargePercent*saleTaxAmount*0.01;
						data[row][surchargeDetails.taxAuthorityRateTypeId + "_AMT"] = surchargeAmount;
						totTaxValue = totTaxValue + surchargeAmount;
					}
				}	
			}	
		}
		
		data[row]["taxAmt"] = totTaxValue;
	}
	
	function updatePayableAmount(row){
		
		var basicAmtVal = 0;
		var taxAmtVal = 0;
		var servChgVal = 0;
		var otherChgs = 0;
		var subsidy = 0;
		
		var basicAmt = data[row]["amount"];
		var taxAmt = data[row]["taxAmt"];
		var servChg = data[row]["SERVICE_CHARGE_AMT"];
		
		if(data[row]["OTH_CHARGES_AMT"]){
			otherChgs = data[row]["OTH_CHARGES_AMT"];
		}
		if(data[row]["SUBSIDY"]){
			subsidy = data[row]["SUBSIDY"];
		}
		if(basicAmt){
			basicAmtVal = basicAmt;
		}
		if(taxAmt){
			taxAmtVal = taxAmt;
		}
		if(servChg){
			servChgVal = servChg;
		}
		
		data[row]["totPayable"] = parseFloat(basicAmtVal) + parseFloat(taxAmtVal) + parseFloat(otherChgs) + parseFloat(servChgVal) - parseFloat(subsidy);
		grid.updateRow(row);
	}
	
	function getProductPurchaseTaxDetails(taxAuthGeoId, productId, row, totalAmt, taxType){
         if( taxAuthGeoId != undefined && taxAuthGeoId != "" &&  taxType != undefined && taxType != "" ){	
	         $.ajax({
	        	type: "POST",
	         	url: "calculateTaxesByGeoIdTest",
	       	 	data: {taxAuthGeoId: taxAuthGeoId, productId: productId } ,
	       	 	dataType: 'json',
	       	 	async: true,
	    	 	success: function(result) {
	          		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	   	  				alert(result["_ERROR_MESSAGE_"]);
	      			}else{
	   	  				
	   	  				var defaultTaxMapPur =result["defaultTaxMap"];
	   	  				var taxValueMapPur =result["taxValueMap"];
	   	  				
	   	  				var purOrderAdjustmentsList = result["orderAdjustmentsList"];
	   	  				data[row]["purOrderAdjustmentsList"] = purOrderAdjustmentsList;
	   	  				
	   	  				var purOrderAdjustmentTypeList = [];
	   	  				for(var i=0;i<purOrderAdjustmentsList.length;i++){
	   	  					var purOrderAdjustmentType = purOrderAdjustmentsList[i];
	   	  					purOrderAdjustmentTypeList.push(purOrderAdjustmentType["orderAdjustmentTypeId"]);
	   	  				}
	   	  				data[row]["purOrderAdjustmentTypeList"] = purOrderAdjustmentTypeList;
	   	  				
	   	  				data[row]["defaultTaxMapPur"] = defaultTaxMapPur;
	   	  				data[row]["taxValueMapPur"] = taxValueMapPur;
	   	  				
	   	  				var count = 0;
						$.each(taxValueMapPur, function(key, value) {
						    data[row]["DEFAULT_PUR_"+key] = value;
						    data[row]["DEFAULT_PUR_"+key+"_AMT"] = (value) * totalAmt/100;
						    
						    data[row][key+"_PUR"] = value;
						    data[row][key+"_PUR_AMT"] = (value) * totalAmt/100;
						    
						    count++;
						});
	   	  				
	   	  				var purTaxList = [];
						purTaxList.push("VAT_SALE");
						purTaxList.push("CST_SALE");
						purTaxList.push("VAT_SURCHARGE");
						purTaxList.push("CST_SURCHARGE");
	   	  				
	   	  				data[row]["purTaxList"] = purTaxList;
	   	  				
	   	  				grid.updateRow(row);
	   	  				
	   	  				//updateTotalIndentAmount();
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
	    	
			var purTaxList = [];
			purTaxList.push("VAT_SALE");
			purTaxList.push("CST_SALE");
			purTaxList.push("VAT_SURCHARGE");
			purTaxList.push("CST_SURCHARGE");
	   	  				
	   	  	
	   	  	data[row]["purTaxList"] = purTaxList;
	   	  	
	   	  	updatePayableAmount(row);			
			//addServiceCharge(row);
	   	  	grid.updateRow(row);
	   	  	
	   	  	//updateTotalIndentAmount();
	   	  	
	   	  	
	    }			
	}
	
	function getProductTaxDetails(taxAuthGeoId, productId, row, totalAmt, taxType){
         if( taxAuthGeoId != undefined && taxAuthGeoId != "" &&  taxType != undefined && taxType != "" ){	
	         $.ajax({
	        	type: "POST",
	         	url: "calculateTaxesByGeoIdTest",
	       	 	data: {taxAuthGeoId: taxAuthGeoId, productId: productId } ,
	       	 	dataType: 'json',
	       	 	async: true,
	    	 	success: function(result) {
	          		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	   	  				alert(result["_ERROR_MESSAGE_"]);
	      			}else{
	   	  				
	   	  				var defaultTaxMap =result["defaultTaxMap"];
	   	  				var taxValueMap =result["taxValueMap"];
	   	  				
	   	  				var orderAdjustmentsList =result["orderAdjustmentsList"];
	   	  				data[row]["orderAdjustmentsList"] = orderAdjustmentsList;
	   	  				
	   	  				var totalOtherCharges = 0;
	   	  				var orderAdjustmentTypeList = [];
	   	  				for(var i=0;i<orderAdjustmentsList.length;i++){
	   	  					var orderAdjustmentType = orderAdjustmentsList[i];
	   	  					orderAdjustmentTypeList.push(orderAdjustmentType["orderAdjustmentTypeId"]);
	   	  					
	   	  					if(data[row][orderAdjustmentType["orderAdjustmentTypeId"]]){
	   	  						totalOtherCharges = totalOtherCharges + data[row][orderAdjustmentType["orderAdjustmentTypeId"]];
	   	  					}
	   	  					
	   	  				}
	   	  				data[row]["OTH_CHARGES_AMT"] = totalOtherCharges;
	   	  				
	   	  				data[row]["orderAdjustmentTypeList"] = orderAdjustmentTypeList;
	   	  				
	   	  				data[row]["defaultTaxMap"] = defaultTaxMap;
	   	  				data[row]["taxValueMap"] = taxValueMap;
	   	  				
	   	  				//var taxList = [];
	   	  				
	   	  				var saleTitleTransferEnumId = $("#saleTitleTransferEnumId").val();
						var validSaleTaxList = transactionTypeTaxMap[saleTitleTransferEnumId];
						
						var totalTaxAmt = 0;
						for(var i=0;i<validSaleTaxList.length;i++){
							var salesTax = validSaleTaxList[i];
							var saleTaxValue = taxValueMap[salesTax];
							var saleTaxAmount = saleTaxValue*(totalAmt)*0.01;
							totalTaxAmt = totalTaxAmt + saleTaxAmount;
							if(defaultTaxMap[salesTax] != 'undefined' || defaultTaxMap[salesTax] != null){
								var surchargeList = defaultTaxMap[salesTax]["surchargeList"];
								
								for(var j=0;j<surchargeList.length;j++){
									var surchargeDetails = surchargeList[j];
									var surchargeValue = taxValueMap[surchargeDetails.taxAuthorityRateTypeId];
									var surchargeAmount = surchargeValue*saleTaxAmount*0.01;
									totalTaxAmt = totalTaxAmt + surchargeAmount;
								}
							}
						}
						
	   	  				var count = 0;
						$.each(taxValueMap, function(key, value) {
						    data[row]["DEFAULT_"+key] = value;
						    data[row]["DEFAULT_"+key+"_AMT"] = (value) * totalAmt/100;
						    count++;
						});
	   	  				
	   	  				var taxList = [];
						taxList.push("VAT_SALE");
	   	  				taxList.push("CST_SALE");
	   	  				taxList.push("VAT_SURCHARGE");
	   	  				taxList.push("CST_SURCHARGE");
	   	  				
	   	  				data[row]["taxAmt"] = totalTaxAmt;
	   	  				data[row]["taxList"] = taxList;
	   	  				
	   	  				grid.updateRow(row);
	   	  				
	   	  				return false; 
	  				}
	           
	      		} ,
	     	 	error: function() {
	      	 		alert(result["_ERROR_MESSAGE_"]);
	     	 	}
	    	});
	    }	
	    else{
	    	
			var taxList = [];
			taxList.push("VAT_SALE");
	   	  	taxList.push("CST_SALE");
	   	  	taxList.push("VAT_SURCHARGE");
	   	  	taxList.push("CST_SURCHARGE");
	   	  	
	   	  	data[row]["taxList"] = taxList;
	   	  	updatePayableAmount(row);			
	   	  	grid.updateRow(row);
	   	  	
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
		  if(isNaN(row["Packaging"])){
			row["Packaging"] = 0;
		 }
		  if(isNaN(row["packets"])){
			row["packets"] = 0;
		 }
	 }
</script>			