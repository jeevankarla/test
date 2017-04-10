<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/steps/jqueryVST.steps.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-effort-driven {
		text-align: center;
	}
	.readOnlyColumnClass {
		font-weight: normal;
		background: mistyrose;
	}
	
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
</style>		
<#include "EditDepoPurchaseOrderPrice.ftl"/>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.autotooltips.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>


<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/steps/jquery.steps.min.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	var dataView;
	var dataView2;
	var grid;
	var grid2;
	var dropDownOption = "ALL";
	var optList = [];
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdsJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdItemLabelsJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemJSON)!'[]'};
	var data = ${StringUtil.wrapString(orderItemsJSON)!'[]'};
	var data2 = ${StringUtil.wrapString(orderAdjustmentJSON)!'[]'};
	var productUOMMap = ${StringUtil.wrapString(productUOMJSON)!'{}'};
	var uomLabelMap = ${StringUtil.wrapString(uomLabelJSON)!'{}'};
	
	var otherTermsLabelJSON = ${StringUtil.wrapString(otherTermsLabelJSON)!'{}'};
	var otherTermsLabelIdJSON = ${StringUtil.wrapString(otherTermsLabelIdJSON)!'{}'};
	
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};
	var supplierJSON = ${StringUtil.wrapString(supplierJSON)!'[]'};
	var partyNameObj = ${StringUtil.wrapString(partyNameObj)!'[]'};
	var partyGeoObj = ${StringUtil.wrapString(partyGeoObj)!'[]'};
	var paymentTermsJSON = ${StringUtil.wrapString(paymentTermsJSON)!'[]'};
	var deliveryTermsJSON = ${StringUtil.wrapString(deliveryTermsJSON)!'[]'};	
	var otherTermsTags = ${StringUtil.wrapString(otherTermsJSON)!'[]'};	
	
	var cstlableTags = ${StringUtil.wrapString(cstJSON)!'[]'};
	var exclableTags = ${StringUtil.wrapString(excJSON)!'[]'};
	var vatlableTags = ${StringUtil.wrapString(vatJSON)!'[]'};
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	function editClickHandlerEvent(row){
		getProductPurchaseTaxDetails($("#supplierGeoId").val(), row, $("#purchaseTaxType").val());
		showItemAdjustmentsAndTaxes(data[row], row);
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
	function calculatePOValue(){
		var isIncTax = $('#incTax').is(':checked');
		var dataMap = {};
		var totAmt = 0;
		var totAmt2 = 0;	
		 for (var rowCount=0; rowCount < data.length; ++rowCount)
		 { 
			var temp =parseInt(data[rowCount]["totPayable"]);
			totAmt=totAmt+temp;
			totAmt2= parseFloat(totAmt).toFixed(2)
			var productId = data[rowCount]["cProductId"];
			var prodId="";
			if(typeof(productId)!= "undefined"){ 	  
				var prodId = productId.toUpperCase();
			}
			dataMap["productId_o_"+rowCount] = prodId;
			var qty = parseFloat(data[rowCount]["quantity"]);
			dataMap["quantity_o_"+rowCount] = qty;
			var unitPrice = data[rowCount]["unitPrice"];
			dataMap["unitPrice_o_"+rowCount] = unitPrice;
			var vatPercent = data[rowCount]["vatPercent"];
			dataMap["vatPercent_o_"+rowCount] = vatPercent;
			var cstPercent = data[rowCount]["cstPercent"];
			dataMap["cstPercent_o_"+rowCount] = cstPercent;
			var bedPercent = data[rowCount]["bedPercent"];
			dataMap["bedPercent_o_"+rowCount] = bedPercent;
			
		}
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{
		
			var otherTermId = data2[rowCount]["adjustmentTypeId"];
			dataMap["otherTermId_o_"+rowCount] = otherTermId;
			var applicableToLabel = data2[rowCount]["applicableTo"];
			var applicableTo = "ALL";
			if(applicableToLabel && applicableToLabel != "ALL"){
				applicableTo = productLabelIdMap[applicableToLabel];
			}
			dataMap["applicableTo_o_"+rowCount] = applicableTo;
			var adjValue = data2[rowCount]["adjValue"];
			if(isNaN(adjValue)){
				adjValue = 0;
			}
			dataMap["adjustmentValue_o_"+rowCount] = adjValue;
			var uomId = "INR";
			if(data2[rowCount]["uomId"]){
				uomId = data2[rowCount]["uomId"];
			}
			dataMap["uomId_o_"+rowCount] = uomId;
			var termDays = 0;
			if(data2[rowCount]["termDays"]){
				termDays = data2[rowCount]["termDays"];
			}
			dataMap["termDays_o_"+rowCount] = termDays;
			var description = "";
			if(data2[rowCount]["description"]){
				description = data2[rowCount]["description"];
			}
			dataMap["description_o_"+rowCount] = description;
		}
		
		
		if(isIncTax){
			dataMap["incTax"] = "Y";
		}
		else{
			dataMap["incTax"] = "";
		}
		jQuery.ajax({
            url: 'getMaterialPOValue',
            async: false,
            type: 'POST',
            data: dataMap,
            dataType: 'json',
            success: function(result) {
               var grandTotal = result["grandTotal"];
               var dspTotal = Math.round(grandTotal);
               var dspMsg = "Rs. "+dspTotal;
               $("#totalPOAmount").html(totAmt);
            },
            error: function (xhr, textStatus, thrownError){
				alert("record not found :: Error code:-  "+xhr.status);
			}
        });
        grid.updateRowCount();
        grid.render();
        
	}     //calculate po value func closed here 
	
	function displayChargesGrid(){
		$("#titleScreen").show();
		prepareApplicableOptions();
		setupGrid2();
	}
	function prepareApplicableOptions(){
		if(data){
			dropDownOption = "ALL";
			for (i = 0; i < data.length; i++) {
    			var product = data[i]["cProductName"];
    			dropDownOption += ","+product;
    		}
		}
	}
	
	function processPOEntryInternal(formName, action) {
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
			//var qty = parseFloat(data[rowCount]["quantity"]);
			//var unitPrice = data[rowCount]["unitPrice"];
			var vatPercent = data[rowCount]["vatPercent"];
			var cstPercent = data[rowCount]["cstPercent"];
			var bedPercent = data[rowCount]["bedPercent"];

			var taxAmt = data[rowCount]["taxAmt"];
			
			var balqty = parseFloat(data[rowCount]["baleQuantity"]);
			var yarnUOM = data[rowCount]["cottonUom"];
			var bundleWeight = data[rowCount]["bundleWeight"];			
			var bundleUnitPrice = data[rowCount]["unitPrice"];			
			var remarks = data[rowCount]["remarks"];
			var qty = parseFloat(data[rowCount]["quantity"]);
			var unitPrice = data[rowCount]["KgunitPrice"];			
			var orderItemSeqId = data[rowCount]["orderItemSeqId"];
			
			var vatPercent = data[rowCount]["vatPercent"];
			var cstPercent = data[rowCount]["cstPercent"];
			var cessPercent = data[rowCount]["cessPercent"];
			//var bedPercent = data[rowCount]["bedPercent"];
			
		
	 		if (!isNaN(qty)) {	
	 			var inputSeqId = jQuery("<input>").attr("type", "hidden").attr("name", "orderItemSeqId_o_" + rowCount).val(orderItemSeqId); 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				var inputBaleQty = jQuery("<input>").attr("type", "hidden").attr("name", "baleQuantity_o_" + rowCount).val(balqty);
				var inputYarnUOM = jQuery("<input>").attr("type", "hidden").attr("name", "yarnUOM_o_" + rowCount).val(yarnUOM);
				var inputBundleWeight = jQuery("<input>").attr("type", "hidden").attr("name", "bundleWeight_o_" + rowCount).val(bundleWeight);
				var inputbundleUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "bundleUnitPrice_o_" + rowCount).val(bundleUnitPrice);			
				var inputRemarks = jQuery("<input>").attr("type", "hidden").attr("name", "remarks_o_" + rowCount).val(remarks);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				jQuery(formId).append(jQuery(inputRemarks));
				jQuery(formId).append(jQuery(inputBaleQty));
				jQuery(formId).append(jQuery(inputYarnUOM));
				jQuery(formId).append(jQuery(inputBundleWeight));
				jQuery(formId).append(jQuery(inputbundleUnitPrice));			
				jQuery(formId).append(jQuery(inputSeqId));
				
				var inputPrice = jQuery("<input>").attr("type", "hidden").attr("name", "unitPrice_o_" + rowCount).val(unitPrice);				
				jQuery(formId).append(jQuery(inputPrice));
				var inputTaxAmt = jQuery("<input>").attr("type", "hidden").attr("name", "taxAmt_o_" + rowCount).val(taxAmt);				
				jQuery(formId).append(jQuery(inputTaxAmt));				
				var inputVATPer = jQuery("<input>").attr("type", "hidden").attr("name", "vatPercent_o_" + rowCount).val(vatPercent);
				jQuery(formId).append(jQuery(inputVATPer));
				var inputCSTPer = jQuery("<input>").attr("type", "hidden").attr("name", "cstPercent_o_" + rowCount).val(cstPercent);				
				jQuery(formId).append(jQuery(inputCSTPer));				
				var inputCESSPer = jQuery("<input>").attr("type", "hidden").attr("name", "cessPercent_o_" + rowCount).val(cessPercent);
				jQuery(formId).append(jQuery(inputCESSPer));				
				//var remarks = jQuery("<input>").attr("type", "hidden").attr("name", "remarks_o_" + rowCount).val(remarks);
				
				//jQuery(formId).append(jQuery(remarks));
                 
                
                // Purchase taxes
				
				var purTaxList = [];
			//	purTaxList = data[rowCount]["purTaxList"];
				
				purTaxList.push("VAT_PUR");
				purTaxList.push("CST_PUR");
				purTaxList.push("VAT_SURCHARGE");
				purTaxList.push("CST_SURCHARGE");
				
				//alert("purTaxList============"+JSON.stringify(purTaxList));
				
				var purTaxListItem = jQuery("<input>").attr("type", "hidden").attr("name", "purTaxList_o_" + rowCount).val(purTaxList);
				jQuery(formId).append(jQuery(purTaxListItem));	
				if(purTaxList != undefined){
					for(var i=0;i<purTaxList.length;i++){
						var taxType = purTaxList[i];
						var taxPercentage = 0;
						var taxValue = 0;
						var taxValue = 0;
						
						if(taxType != "VAT_SURCHARGE" && taxType != "CST_SURCHARGE")
						{
						  taxPercentage = data[rowCount][taxType];
						  taxValue = data[rowCount][taxType + "_AMT"];
						}else{
						   taxPercentage = data[rowCount][taxType+"_PUR"];
					       taxValue = data[rowCount][taxType + "_PUR_AMT"];
						}
						
						
						
						var purInputTaxTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_PUR_o_" + rowCount).val(taxPercentage);
						var purInputTaxTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", taxType + "_PUR_AMT_o_"+ rowCount).val(taxValue);
						jQuery(formId).append(jQuery(purInputTaxTypePerc));
						jQuery(formId).append(jQuery(purInputTaxTypeValue));
					}
				}
                
                // Purchase order Adjustments
				var orderAdjustmentsList = [];
				
				orderAdjustmentsList.push("CESS");
				orderAdjustmentsList.push("INSURANCE_CHGS");
				orderAdjustmentsList.push("OTHER_CHARGES");
				orderAdjustmentsList.push("PACKING_FORWARDIG");
				
				//orderAdjustmentsList = data[rowCount]["purOrderAdjustmentTypeList"];
				data[rowCount]["itemAdjustments"] = orderAdjustmentsList;				
				var orderAdjustmentItem = jQuery("<input>").attr("type", "hidden").attr("name", "orderAdjustmentsList_o_" + rowCount).val(orderAdjustmentsList);				
				jQuery(formId).append(jQuery(orderAdjustmentItem));	
				if(orderAdjustmentsList != undefined){
					for(var i=0;i<orderAdjustmentsList.length;i++){
						var orderAdjType = orderAdjustmentsList[i];
						var orderAdjType = orderAdjustmentsList[i];
						var adjPercentage = data[rowCount][orderAdjType+ "_PUR"];
						var adjValue = data[rowCount][orderAdjType + "_PUR_AMT"];
						var isAssessableValue = data[rowCount][orderAdjType + "_INC_BASIC"];
						
						
						var inputOrderAdjTypePerc = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_o_" + rowCount).val(adjPercentage);
						var inputOrderAdjTypeValue = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_AMT_o_"+ rowCount).val(adjValue);
						var inputOrderAdjTypeAssessable = jQuery("<input>").attr("type", "hidden").attr("name", orderAdjType + "_INC_BASIC_o_"+ rowCount).val(isAssessableValue);
						
						jQuery(formId).append(jQuery(inputOrderAdjTypePerc));
						jQuery(formId).append(jQuery(inputOrderAdjTypeValue));
						jQuery(formId).append(jQuery(inputOrderAdjTypeAssessable));
					}
				} 
				//var inputExcisePer = jQuery("<input>").attr("type", "hidden").attr("name", "bedPercent_o_" + rowCount).val(bedPercent);
				//jQuery(formId).append(jQuery(inputExcisePer));
   			}
		}
		
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{
			var otherTermId = data2[rowCount]["adjustmentTypeId"];
			var applicableToLabel = data2[rowCount]["applicableTo"];
			var applicableTo = "ALL";
			if(applicableToLabel && applicableToLabel != "ALL"){
				applicableTo = productLabelIdMap[applicableToLabel];
			}
			
			var adjValue = data2[rowCount]["adjValue"];
			if(isNaN(adjValue)){
				adjValue = 0;
			}
			var uomId = "INR";
			if(data2[rowCount]["uomId"]){
				uomId = data2[rowCount]["uomId"];
			}
			var termDays = 0;
			if(data2[rowCount]["termDays"]){
				termDays = data2[rowCount]["termDays"];
			}
			
			var assessableValue = "";
			if(data2[rowCount]["assessableValue"]){
				assessableValue = data2[rowCount]["assessableValue"];
			}
			
			var description = "";
			
			if(data2[rowCount]["description"]){
				description = data2[rowCount]["description"];
			}
			
			if(adjValue != 0 && otherTermId){
				var inputTermId = jQuery("<input>").attr("type", "hidden").attr("name", "otherTermId_o_" + rowCount).val(otherTermId);
				var inputApplicable = jQuery("<input>").attr("type", "hidden").attr("name", "applicableTo_o_" + rowCount).val(applicableTo);
				var inputAdjVal = jQuery("<input>").attr("type", "hidden").attr("name", "adjustmentValue_o_" + rowCount).val(adjValue);
				var inputUom = jQuery("<input>").attr("type", "hidden").attr("name", "uomId_o_" + rowCount).val(uomId);
				var inputDays = jQuery("<input>").attr("type", "hidden").attr("name", "termDays_o_" + rowCount).val(termDays);
				var inputDescription = jQuery("<input>").attr("type", "hidden").attr("name", "description_o_" + rowCount).val(description);
				var inputAssessableVal = jQuery("<input>").attr("type", "hidden").attr("name", "assessableValue_o_" + rowCount).val(assessableValue);
				
				jQuery(formId).append(jQuery(inputTermId));				
				jQuery(formId).append(jQuery(inputApplicable));
				jQuery(formId).append(jQuery(inputAdjVal));				
				jQuery(formId).append(jQuery(inputUom));
				jQuery(formId).append(jQuery(inputDays));				
				jQuery(formId).append(jQuery(inputDescription));
				jQuery(formId).append(jQuery(inputAssessableVal));
			}
		}
		
		<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
			var supplierId = $("#supplierId").val();
			var poNumber = $("#PONumber").val();
			 var isIncTax = $('#incTax').is(':checked');
			var orderName = $("#orderName").val();
			var orderId = $("#orderId").val();
			var productStoreId = $("#productStoreId").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "supplierId").val(supplierId);
			var order = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(orderId);
			var orderDesc = jQuery("<input>").attr("type", "hidden").attr("name", "orderName").val(orderName);
		    var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
		    var refNo = $("#refNo").val();
			var quotationNo = $("#quotationNo").val();			
		    var refNum= jQuery("<input>").attr("type", "hidden").attr("name", "refNo").val(refNo);
		    var quotationNum = jQuery("<input>").attr("type", "hidden").attr("name", "quotationNo").val(quotationNo);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			if(isIncTax){
			    var incTaxEl = jQuery("<input>").attr("type", "hidden").attr("name", "incTax").val(isIncTax);
			    jQuery(formId).append(jQuery(incTaxEl));
			}
			
			<#if orderId?exists>
				var order = '${orderId}';
				var extOrder = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(order);		
				jQuery(formId).append(jQuery(extOrder));
			</#if>
			jQuery(formId).append(jQuery(orderDesc));
			jQuery(formId).append(jQuery(order));
			jQuery(formId).append(jQuery(party));
			jQuery(formId).append(jQuery(POField));
            jQuery(formId).append(jQuery(refNum));
            jQuery(formId).append(jQuery(quotationNum)); 
			jQuery(formId).append(jQuery(productStore));            
		</#if>
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	
	function processIndentEntry(formName, action) {
		jQuery("#changeSave").attr( "disabled", "disabled");
		processPOEntryInternal(formName, action);
		
	}
	
    function productFormatter(row, cell, value, columnDef, dataContext) {
        return productIdLabelMap[value];
    }
	
	function adjustmentTermFormatter(row, cell, value, columnDef, dataContext) {
		
		if(value != undefined){
			if(otherTermsLabelJSON[value]){
				return otherTermsLabelJSON[value];
			}
			else{
				var adjTermId = otherTermsLabelIdJSON[value]; 
				data2[row]['adjustmentTypeId'] = adjTermId;
				return otherTermsLabelJSON[adjTermId]; 
			}
		}
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
      	if (currProdCnt > 1) {
        	return {valid: false, msg: "Duplicate Product " + value};      				
      	}
      	if(invalidProdCheck == 0){
      		return {valid: false, msg: "Invalid Product " + value};
      	}
      
      	if (item != null && item != undefined ) {
      		item['cProductId'] = productLabelIdMap[value];
	  	}      
      	return {valid: true, msg: null};
    }
    //persent Validation
	function excValidator(value){
		var invalidValue = 0;
	  	for (var rowCount=0; rowCount < exclableTags.length; ++rowCount)
	  	{  
			if (value == exclableTags[rowCount]["label"] || value==0) {
				invalidValue = 1;
			}
	  	}
		if(invalidValue == 0){
      		return {valid: false, msg: "Invalid Product " + value};
      	}
		return {valid: true, msg: null};
	}
	function vatValidator(value,item){
		var invalidValue = 0;
	  	for (var rowCount=0; rowCount < vatlableTags.length; ++rowCount)
	  	{  
			if (value == vatlableTags[rowCount]["label"] || value==0) {
				invalidValue = 1;
			}
			
	  	}
	  	var cstPrecent =item['cstPercent'];
	  	
	  	if( cstPrecent>0){
	  		invalidValue=0;
	  	}
		if(invalidValue == 0){
      		return {valid: false, msg: "Invalid Product " + value};
      	}
		return {valid: true, msg: null};
	}
	function cstValidator(value,item){
		var invalidValue = 0;
	  	for (var rowCount=0; rowCount < cstlableTags.length; ++rowCount)
	  	{  
			if (value == cstlableTags[rowCount]["label"] || value==0) {
				invalidValue = 1;
			}
	  	}
	  	var vatPercent =item['vatPercent'];
	  	if(vatPercent>0){
	  		invalidValue=0;
	  	}
		if(invalidValue == 0){
      		return {valid: false, msg: "Invalid Product " + value};
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
		if(isNaN(value)){
			value = 0;
		}		
		var formatValue = parseFloat(value).toFixed(2);
        return formatValue;
    }
    
    function updateGridAmount(){
    	var totalAmt = 0;
    	if(data){
    		var isIncTax = $('#incTax').is(':checked');
    		
    		for (i = 0; i < data.length; i++) {
    			
    			var itemTotal = 0
    			var unitPrice = 0;
    			var qty = 0;
    			var bedPercent = 0;
    			var vatPercent = 0;
    			var cstPercent = 0;
    			
    			if(!isNaN(data[i]["unitPrice"])){
					unitPrice = data[i]["unitPrice"];
				}
				if(!isNaN(data[i]["quantity"])){
					qty = data[i]["quantity"];
				}
				
				if(!isNaN(data[i]["bedPercent"])){
					bedPercent = data[i]["bedPercent"];
			   	}
					  
				if(!isNaN(data[i]["vatPercent"])){
					vatPercent = data[i]["vatPercent"];
				}
				if(!isNaN(data[i]["cstPercent"])){
					cstPercent = data[i]["cstPercent"];
				}
				
    			if(!isIncTax){
    			
					var basicPrice = parseFloat(Math.round( ((qty*unitPrice) * 100) / 100 ));
					var bedAmt = parseFloat(Math.round(((basicPrice*(bedPercent/100)) * 100) / 100));
					var basePrice = basicPrice+bedAmt;
					var vatAmt = parseFloat(Math.round(((basePrice*(vatPercent/100)) * 100) / 100));
					var cstAmt = parseFloat(Math.round(((basePrice*(cstPercent/100)) * 100) / 100));
					
					itemTotal = basePrice+vatAmt+cstAmt;
	    		}
    			else{
    			
    				var newUnitPrice = 0;
    				
    				var itemTotal = parseFloat(Math.round(((qty*unitPrice) * 100) / 100));
    				
    				var vatAmt = parseFloat(Math.round(((unitPrice*(vatPercent/100)) * 100) / 100));
    				var baseVatStrpAmt = unitPrice-vatAmt;
    				
    				var cstAmt = parseFloat(Math.round(((unitPrice*(cstPercent/100)) * 100) / 100));
    				var baseCstStrpAmt = unitPrice-cstAmt;
    				
    				var taxStrpAmt = 0;
    				
    				if(baseVatStrpAmt && baseVatStrpAmt>0){
    					taxStrpAmt = baseVatStrpAmt;
    				}
    				
    				if(baseCstStrpAmt && baseCstStrpAmt>0){
    					taxStrpAmt = baseCstStrpAmt;
    				}
    				
    				bedAmt = parseFloat(Math.round((taxStrpAmt*(bedPercent/100)) * 100) / 100);
    				newUnitPrice = taxStrpAmt-bedAmt;
    				//data[i]["unitPrice"] = newUnitPrice;
    					
    			}
				totalAmt += itemTotal;
				data[i]["amount"] = itemTotal;
				grid.updateRow(i);
    		}
    		 
		}
		//var amt = parseFloat(Math.round((totalAmt) * 100) / 100);
		
		var amt = parseFloat(Math.round(totalAmt));
		
		alert(amt);
			
		if(amt > 0 ){
			var dispText = "<b>  [Total PO Amt: Rs " +  amt + "]</b>";
		}
		else{
			var dispText = "<b>  [Total PO Amt: Rs 0 ]</b>";
		}
		jQuery("#totalAmount").html(dispText);
    }
	
	function quantityValidator(value ,item) {
		/*var quarterVal = value*4;
		var floorValue = Math.floor(quarterVal);
		var remainder = quarterVal - floorValue;
		var remainderVal =  Math.floor(value) - value;
	     if(remainder !=0 ){
			return {valid: false, msg: "packets should not be in decimals " + value};
		}*/
      return {valid: true, msg: null};
    }
	var mainGrid;		
	function setupGrid1() {
        if(mainGrid){
           return false;
        }
        var columns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:270, minWidth:270, <#if orderId?exists>cssClass:"readOnlyColumnClass", focusable :false,<#else>cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains", editor: AutoCompleteEditor,</#if> sortable:false ,toolTip:""},
			{id:"remarks", name:"Specifications", field:"remarks", width:150, minWidth:150, sortable:false, cssClass:"cell-title", focusable :true,editor:TextCellEditor},
			{id:"baleQuantity", name:"Qty(Nos)", field:"baleQuantity", width:50, minWidth:50, sortable:false, editor:FloatCellEditor},
			{id:"cottonUom", name:"${uiLabelMap.cottonUom}", field:"cottonUom", width:50, minWidth:50, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "KGs,Bale,Half-Bale,Bundle"},
			{id:"bundleWeight", name:"${uiLabelMap.BundleWtKgs}", field:"bundleWeight", width:110, minWidth:110, sortable:false, editor:FloatCellEditor},
			{id:"unitPrice", name:"${uiLabelMap.UnitPrice} (Bundle)", field:"unitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			//{id:"package", name:"Packaging(KGS)", field:"Packaging", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			//{id:"packets", name:"packets", field:"packets", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			{id:"quantity", name:"Qty(Kgs)", field:"quantity", width:80, minWidth:80, sortable:false, editor:FloatCellEditor},
			{id:"KgunitPrice", name:"${uiLabelMap.UnitPrice} (KGs)", field:"KgunitPrice", width:110, minWidth:110, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			{id:"amount", name:"Basic Amount(Rs)", field:"amount", width:100, minWidth:100,sortable:false, formatter: rateFormatter, editor:FloatCellEditor},
			{id:"taxAmt", name:"Tax", field:"taxAmt", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"OTH_CHARGES_AMT", name:"Oth Chgs", field:"OTH_CHARGES_AMT", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"totPayable", name:"Total Payable", field:"totPayable", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", cssClass:"readOnlyColumnClass" , focusable :false},
			{id:"button", name:"Edit Tax", field:"button", width:60, minWidth:60, cssClass:"cell-title", focusable :false,
 				formatter: function (row, cell, id, def, datactx) { 
					return '<a href="#" class="button" onclick="editClickHandlerEvent('+row+')" value="Edit">Edit</a>'; 
 				}
 			},
		];
            
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
      		var productIdItemLabelMap = ${StringUtil.wrapString(productLabelIdsJSON)!'{}'};
      		item['cProductId'] = productLabelIdMap[productLabel];
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
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
			var roundedAmount =calculateBundlePrice(balQuty,uom,price);
			if(args.cell != 7){
		  		if(uom!="KGs"){
		   			kgUnitPrice=price/bundleWeight;		   			
		  		}
				else{
				    kgUnitPrice=price;				    
				}				
		 	}
		 	
		 	if(args.cell == 2){
		    $.ajax({
	        	type: "POST",
	         	url: "getProductPrice",
	       	 	data: {productId: prod, fromDc: "DC" },
	       	 	dataType: 'json',
	       	 	async: true,
	    	 	success: function(result) {
	          		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	   	  				alert(result["_ERROR_MESSAGE_"]);
	      			}else{
	      		     var lastPrice =result["lastPrice"];
	      		     data[args.row]['KgunitPrice'] = lastPrice;
	      		}
	      		}
	    	});
	    	
	    	}
	    	
	    	if(args.cell > 2){
		    
		    var Packaging = data[args.row]['Packaging'];
		    var quantity = data[args.row]['quantity'];
		    data[args.row]['packets'] = quantity/Packaging;
		    var kgUnitPrice = data[args.row]['KgunitPrice'];
		    data[args.row]['amount'] = kgUnitPrice*quantity;	
		    
		    }
			
			//var kgUnitPrice = data[args.row]['KgunitPrice'];
			var quantity = parseFloat(data[args.row]["quantity"]);
							
						
	    	

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
		else if(args.cell == 3){
				quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
				
				data[args.row]["KgunitPrice"] = kgUnitPrice;
				data[args.row]["quantity"] = quantity;
				data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
				data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			
			}
			else if(args.cell == 4){
			var Packaging = parseFloat(data[args.row]["Packaging"]);
				
				data[args.row]["packets"] = quantity/Packaging;
			  	quantity=calculateBundleWeight(balQuty,uom,bundleWeight);
			  	data[args.row]["quantity"] = quantity;
			 	data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			  	
			  	
			}
			else if(args.cell == 5){
			   var quantity = data[args.row]['quantity'];
			  	var kgUnitPrice = data[args.row]['kgUnitPrice'];
			  	data[args.row]["KgunitPrice"] = kgUnitPrice;
			  	data[args.row]["amount"] = Math.round(quantity*kgUnitPrice);
			  	data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
			}
			else if(args.cell == 6){
			  	baleQty=calculateKgs(quantity,uom,bundleWeight);
			  	data[args.row]["baleQuantity"]=baleQty;
			  	
			  	var quantity = data[args.row]['quantity'];
			  	var kgUnitPrice = data[args.row]['kgUnitPrice'];
			  	
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
			  	data[args.row]["unitPrice"] = upb;
				data[args.row]["totPayable"] = Math.round(quantity*kgUnitPrice);
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
		if(args.cell != 0 && args.cell != 1){
						
				roundedAmount=data[args.row]["amount"];
				grid.updateRow(args.row);
				//updatePayablePrice(args.row);
				//updateTotalIndentAmount();
			}
			$("#calculateBtn").click();
	}); 
		
		
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data[args.row] != null) {
				var prod = data[args.row]["cProductId"];
			}
			
			if (args.cell == 0 && data[args.row] != null) {
				var prod = data[args.row]["cProductId"];
				var uomId = productUOMMap[prod];
				var uomLabel = uomLabelMap[uomId];
				data[args.row]['uomDescription'] = uomLabel;     		 		
	      		
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
	
	    
    
    function updateInvoiceTotalAmount(){
   		updateGridAmount();
   	}
    
 
		//updateProductTotalAmount();
		function updateProductTotalAmount() {
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["cProductId"];
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				var price = parseFloat(priceTags[prod]);
				var crVal = 0;
				grid.updateRow(i);
			}
			var totalAmount = 0;
			for (i = 0; i < data.length; i++) {
				totalAmount += data[i]["amount"];
			}
			//var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			
			var amt = parseFloat(Math.round(totalAmount));
			if(amt > 0 ){
				var dispText = "<b> [Total PO Value: Rs" +  amt + "]</b>";
			}
			else{
				var dispText = "<b>[Total PO Value: Rs 0 ]</b>";
			}
			
			jQuery("#totalAmount").html(dispText);
		}
		mainGrid = grid;
	}
	
	function setupGrid2() {
        withAdjColumns = [
			{id:"adjustmentTypeId", name:"Term Type", field:"adjustmentTypeId", width:250, minWidth:250, cssClass:"cell-title", regexMatcher:"contains",availableTags: otherTermsTags, editor: AutoCompleteEditor,sortable:false , formatter: adjustmentTermFormatter, toolTip:"Other Adjustment Type"},
			{id:"applicableTo", name:"Applicable To", field:"applicableTo", width:150, minWidth:150, cssClass:"cell-title",options: dropDownOption, editor: SelectCellEditor,sortable:false ,toolTip:""},
			{id:"adjValue", name:"Value", field:"adjValue", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Value"},
			{id:"uomId", name:"Rs/Percent", field:"uomId", width:80, minWidth:80, options: "INR,PERCENT", editor: SelectCellEditor, sortable:false, align:"right", toolTip:"Unit Of Measure"},
			{id:"termDays", name:"Term Days", field:"termDays", width:80, minWidth:80, editor:IntegerCellEditor, sortable:false, align:"right", toolTip:"Term Days"},
			{id:"description", name:"Description", field:"description", width:200, minWidth:200, editor:LongTextCellEditor, sortable:false, align:"right", toolTip:"Term Description"},
		];
		
		var options2 = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
			  
		grid2 = new Slick.Grid("#myGrid2", data2, withAdjColumns, options2);
        grid2.setSelectionModel(new Slick.CellSelectionModel()); 
     
		var columnpicker = new Slick.Controls.ColumnPicker(withAdjColumns, grid2, options2);
        if (data2.length > 0) {			
			$(grid2.getCellNode(0, 1)).click();
		}else{
			$(grid2.getCellNode(0,0)).click();
		}
        
        grid2.onKeyDown.subscribe(function(e) {
			var cellNav = 5;
			var cell = grid2.getCellFromEvent(e);		
			if(e.which == $.ui.keyCode.UP && cell.row == 0){
				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();
			}
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == data2.length && cell.cell == cellNav){
				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(0, 2)).click();
				e.stopPropagation();
			}else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == (data2.length-1) && cell.cell == cellNav){
				grid2.getEditController().commitCurrentEdit();
				grid2.gotoCell(data2.length, 0, true);
				$(grid2.getCellNode(data2.length, 0)).edit();
				
				e.stopPropagation();
			}
			
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.RIGHT) && cell 
				&& cell.row == data2.length && cell.cell == cellNav){
  				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(cell.row, 0)).click();
				e.stopPropagation();
			
			}else if (e.which == $.ui.keyCode.RIGHT &&
				cell && (cell.cell == cellNav) && 
				cell.row != data2.length) {
				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.DOWN && cell && (cell.cell == 3)) {
				grid2.getEditController().commitCurrentEdit();
				$(grid2.getCellNode(cell.row, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data2.length) {
				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid2.getEditController().commitCurrentEdit();
				e.stopPropagation();
            	e.preventDefault();        	
            }else {
            	return false;
            }
        });
         
    	grid2.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var itemLabel = item['adjustmentTypeId'];
      		
      		
      		if (item != null && item != undefined ) {
      			item['adjustmentTypeId'] = otherTermsLabelIdJSON[itemLabel];
      		}     		 		
      		grid2.invalidateRow(data2.length);
      		data2.push(item);
      		grid2.updateRowCount();
      		grid2.render();
    	});
        
        grid2.onCellChange.subscribe(function(e,args) {

		}); 
		
		grid2.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data2[args.row] != null) {
				var itemType = data2[args.row]["adjustmentTypeId"];
			}
			if (args.cell == 0 && data[args.row] != null) {
				var adjType = data[args.row]["adjustmentTypeId"];
				var adjLabel = otherTermsLabelJSON[adjType];
				var uomLabel = otherTermsLabelIdJSON[adjLabel];
				data[args.row]['adjustmentTypeId'] = uomLabel;     		 		
	      		
			}
		});
		
		grid2.onValidationError.subscribe(function(e, args) {
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
    }
    
	jQuery(function(){
	     var partyId=$('[name=supplierId]').val();
		 if(partyId){
		    gridShowCall();
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
    	  		
	});
	
	
// to show special related fields in form			

	function gridHideCall() {
           $('#FieldsDIV').hide();
          
    }
     function gridShowCall() {
           $('#FieldsDIV').show();
    }
    
    function getProductPurchaseTaxDetails(taxAuthGeoId, row, taxType){
    
    	var productId = data[row]["cProductId"];
    	var totalAmt = data[row]["amount"]; 
         if( taxAuthGeoId != undefined && taxAuthGeoId != "" &&  taxType != undefined && taxType != "" ){	         
	         $.ajax({
	        	type: "POST",
	         	url: "calculateTaxesByGeoIdTest",
	       	 	data: {taxAuthGeoId: taxAuthGeoId, productId: productId } ,
	       	 	dataType: 'json',
	       	 	async: false,
	    	 	success: function(result) {
	          		if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
	   	  				alert(result["_ERROR_MESSAGE_"]);
	      			}else{
	   	  				
	   	  				var defaultTaxMapPur =result["defaultTaxMap"];
	   	  				var taxValueMapPur =result["taxValueMap"];	   	  				
	   	  				var purOrderAdjustmentsList = result["orderAdjustmentsList"];	   	  				
	   	  				data[row]["itemAdjustments"] = purOrderAdjustmentsList;
	   	  				
	   	  				var purOrderAdjustmentTypeList = [];
	   	  				for(var i=0;i<purOrderAdjustmentsList.length;i++){
	   	  					var purOrderAdjustmentType = purOrderAdjustmentsList[i];
	   	  					purOrderAdjustmentTypeList.push(purOrderAdjustmentType["orderAdjustmentTypeId"]);
	   	  				}
	   	  				data[row]["purOrderAdjustmentTypeList"] = purOrderAdjustmentTypeList;
	   	  				
	   	  				data[row]["defaultTaxMapPur"] = defaultTaxMapPur;
	   	  				data[row]["taxValueMapPur"] = taxValueMapPur;
	   	  				
	   	  				<#--
	   	  				var purTaxList = [];
	   	  				
	   	  				var purchaseTitleTransferEnumId = $("#purchaseTitleTransferEnumId").val();
						var validPurchaseTaxList = transactionTypeTaxMap[purchaseTitleTransferEnumId];
						
						for(var i=0;i<validPurchaseTaxList.length;i++){
							var purchaseTax = validPurchaseTaxList[i];
							purTaxList.push(purchaseTax);
							if(defaultTaxMapPur[purchaseTax] != 'undefined' || defaultTaxMapPur[purchaseTax] != null){
								var surchargeList = defaultTaxMapPur[purchaseTax]["surchargeList"];
								
								for(var j=0;j<surchargeList.length;j++){
									var surchargeDetails = surchargeList[j];
									purTaxList.push(surchargeDetails.taxAuthorityRateTypeId);
								}
							}
						}
						-->
						
	   	  				var count = 0;
						$.each(taxValueMapPur, function(key, value) {
						    data[row]["DEFAULT_PUR_"+key] = value;
						    data[row]["DEFAULT_PUR_"+key+"_AMT"] = (value) * totalAmt/100;
						    
						    //data[row][key+"_PUR"] = value;
						    //data[row][key+"_PUR_AMT"] = (value) * totalAmt/100;
						    
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
	   	  	
	   	  	//updatePayableAmount(row);			
			//addServiceCharge(row);
	   	  	grid.updateRow(row);
	   	  	
	   	  	//updateTotalIndentAmount();
	   	  	
	   	  	
	    }			
	}
    
    
    
</script>			