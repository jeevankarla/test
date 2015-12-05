
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
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
	
	.btn {
	    color:#08233e;
	    font:8em Futura, ‘Century Gothic’, AppleGothic, sans-serif;
	    font-size:100%;
	    font-weight: bold;
	    padding:14px;
	    background:url(overlay.png) repeat-x center #ffcc00;
	    background-color:rgba(255,204,0,1);
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
    	background-color:rgba(255,204,0,0.8);
	}
	.btn {
	    background-color:orange;
	    cursor:pointer;
	}
	
</style>			
			
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
			var batchNo = data[rowCount]["batchNo"];
			var days = data[rowCount]["daysToStore"];
			var unitPrice = data[rowCount]["unitPrice"];
			<#if changeFlag?exists && changeFlag != "EditDepotSales">
			 if(qty>0){
			</#if>
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				var inputUnitPrice = jQuery("<input>").attr("type", "hidden").attr("name", "unitPrice_o_" + rowCount).val(unitPrice);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				jQuery(formId).append(jQuery(inputUnitPrice));
				<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
					var batchNum = jQuery("<input>").attr("type", "hidden").attr("name", "batchNo_o_" + rowCount).val(batchNo);
					jQuery(formId).append(jQuery(batchNum));
					var days = jQuery("<input>").attr("type", "hidden").attr("name", "daysToStore_o_" + rowCount).val(days);
					jQuery(formId).append(jQuery(days));
				</#if>
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
		<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
			var partyId = $("#partyId").val();
			var suplierPartyId = $("#suplierPartyId").val();
			var societyPartyId="";
			 	societyPartyId = $("#societyPartyId").val();
			var billingType = $("#billingType").val();
			var  orderTaxType= $("#orderTaxType").val();
			var poNumber = $("#PONumber").val();
			var acctgFlag = $("#disableAcctgFlag").val();
			var promoAdj = $("#promotionAdj").val();
			var productStoreId = $("#productStoreId").val();
			var orderMessage = $("#orderMessage").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
			var suplierParty = jQuery("<input>").attr("type", "hidden").attr("name", "suplierPartyId").val(suplierPartyId);
			var societyParty = jQuery("<input>").attr("type", "hidden").attr("name", "societyPartyId").val(societyPartyId);
			var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
			var promoField = jQuery("<input>").attr("type", "hidden").attr("name", "promotionAdjAmt").val(promoAdj);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			var tax = jQuery("<input>").attr("type", "hidden").attr("name", "orderTaxType").val(orderTaxType);
			var bilngType = jQuery("<input>").attr("type", "hidden").attr("name", "billingType").val(billingType);
			var orderMessageInPut = jQuery("<input>").attr("type", "hidden").attr("name", "orderMessage").val(orderMessage);
			var disableAcctgFlag = jQuery("<input>").attr("type", "hidden").attr("name", "disableAcctgFlag").val(acctgFlag);
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
			jQuery(formId).append(jQuery(orderMessageInPut));
			jQuery(formId).append(jQuery(disableAcctgFlag));
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
	<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "EditDepotSales">
		function editClickHandlerEvent(row){
			showUDPPriceToolTip(data[row], row, userDefPriceObj);
			 <#--updateUDPLabel();-->
			
			
		}
	</#if>
	
	function editClickHandler(row) {
		if(enableSubmit){						
			enableSubmit = false;
			processChangeIndentInternal('indententry', '<@ofbizUrl>${editClickHandlerAction}</@ofbizUrl>', row);		
		}
		
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
		
		var columns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:350, minWidth:350, cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains" ,editor: AutoCompleteEditor, validator: productValidator, sortable:false ,toolTip:""},
			<#--{id:"productFeature", name:"Feature", field:"productFeature", width:80, minWidth:80, cssClass:"cell-title", availableTags: featureAvailableTags, regexMatcher:"contains" ,editor: AutoCompleteEditor, sortable:false ,toolTip:""},-->
			{id:"baleQuantity", name:"Quantity", field:"baleQuantity", width:80, minWidth:80, sortable:false, editor:FloatCellEditor},
			{id:"cottonUom", name:"Uom", field:"cottonUom", width:150, minWidth:150, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "Bale,Half-Bale"},
			{id:"bundleWeight", name:"Bundle Wt(Kgs)", field:"bundleWeight", width:110, minWidth:110, sortable:false, editor:FloatCellEditor},
			{id:"quantity", name:"Qty Kgs", field:"quantity", width:80, minWidth:80, sortable:false, editor:FloatCellEditor},
			{id:"unitPrice", name:"Unit Price", field:"unitPrice", width:75, minWidth:75, sortable:false, formatter: rateFormatter, align:"right", editor:FloatCellEditor},
			<#--{id:"schemeApplicability", name:"10% Scheme", field:"schemeApplicability", width:150, minWidth:150, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "Applicable,Not-Applicable"},-->
			{id:"amount", name:"Total Amount(Rs)", field:"amount", width:130, minWidth:130, sortable:false, formatter: rateFormatter},	
			{id:"quotaAvbl", name:"Quota Available", field:"quota", width:80, minWidth:80, sortable:false, cssClass:"readOnlyColumnClass", focusable :false}
			
			<#--
			{id:"productFeature", name:"Count", field:"productFeature", width:70, minWidth:70, cssClass:"cell-title",editor: SelectCellEditor, sortable:false, options: "INR,PERCENT,asdf"},
			-->	
			<#--
			<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary" || changeFlag == "ICPTransferSale">
				{id:"crQuantity", name:"Qty(Crt)", field:"crQuantity", width:60, minWidth:60, cssClass:"cell-title",editor:FloatCellEditor, sortable:false, formatter: quantityFormatter},
				{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
				<#if changeFlag?exists && changeFlag != "ICPTransferSale">
					{id:"batchNo", name:"Batch Number", field:"batchNo", width:65, minWidth:65, sortable:false, editor:TextCellEditor},
				</#if>
			<#elseif changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "InterUnitTransferSale"  || changeFlag == "EditDepotSales">
		       <#if changeFlag?exists && changeFlag == "EditDepotSales">
					{id:"prevQuantity", name:"Prev-Qty(Pkt)", field:"prevQuantity", width:70, minWidth:70, cssClass:"readOnlyColumnClass", sortable:false , formatter: rateFormatter},
				<#else>
					{id:"ltrQuantity", name:"Ltr/KG Qty", field:"ltrQuantity", width:65, minWidth:65, sortable:false, editor:FloatCellEditor},
				</#if>
				{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
				
			</#if>
			-->
			
			<#--
			<#if changeFlag?exists && changeFlag != "EditDepotSales">
			{id:"unitCost", name:"Unit Price(Rs)", field:"unitPrice", width:65, minWidth:65, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"ltrPrice", name:"Ltr/Kg Price", field:"ltrPrice", width:80, minWidth:80, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"amount", name:"Total Amount(Rs)", field:"amount", width:100, minWidth:100, sortable:false, formatter: rateFormatter},
			{id:"UOM", name:"UOM", field:"uomDescription", width:100, minWidth:100, sortable:false, focusable :false}
			<#else>
			{id:"unitCost", name:"Unit Price(Rs)", field:"unitPrice", width:65, minWidth:65, sortable:false, formatter: rateFormatter , align:"right"},
			{id:"ltrPrice", name:"Ltr/Kg Price", field:"ltrPrice", width:80, minWidth:80, sortable:false, formatter: rateFormatter , align:"right"},
			{id:"amount", name:"Total Amount(Rs)", field:"amount", width:100, minWidth:100, sortable:false, formatter: rateFormatter}
			</#if>
			-->
		];
		<#--
		<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "EditDepotSales">
			columns.push({id:"button", name:"Edit Price", field:"button", width:70, minWidth:70, cssClass:"cell-title", focusable :false,
 				formatter: function (row, cell, id, def, datactx) { 
						return '<a href="#" class="button" onclick="editClickHandlerEvent('+row+')" value="Edit">Edit</a>'; 
 				}
 			});
 		</#if>
 		-->
		
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
	      	if ( (args.cell == 1) || (args.cell == 2) || ((args.cell == 3)) ) {
	      		
	      		var productName = data[args.row]["productNameStr"];
      			
      			if (productName.toLowerCase().indexOf("cotton") <= 0){
      				grid.updateRow(args.row);
      				return false;
      			}
      			
      			
	      	}
	      	
	      	
	      	
	    });
        grid.onCellChange.subscribe(function(e,args) {
        	if (args.cell == 2) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var uomId = productUOMMap[prod];
				var uomLabel = uomLabelMap[uomId];
				data[args.row]['uomDescription'] = uomLabel;
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary" || changeFlag == "ICPTransferSale">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" >
					convValue = prodConversionData['LtrKg'];
				</#if>
				var udp = data[args.row]['basicPrice'];
				var price = 0;
				if(udp){
					var basic_price = data[args.row]['basicPrice'];
					var vat_price = data[args.row]['vatPrice'];
					var cst_price = data[args.row]['cstPrice'];
					var bed_price = data[args.row]['bedPrice'];
					var serviceTax_price = data[args.row]['serviceTaxPrice'];
					var totalPrice = basic_price+vat_price+cst_price+bed_price+serviceTax_price;
					price = totalPrice;
				}
				else{
					price = parseFloat(priceTags[prod]);
				}
				var literPrice = parseFloat(priceTags[prod]);
				var crVal = 0;
				if(convValue != 'undefined' || convValue != null || convValue > 0){
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[args.row]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
						crVal = parseFloat(Math.round((qty*convValue)*100)/100);
						data[args.row]["ltrQuantity"] = crVal;
					</#if>
					
				}
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
				var roundedAmount;
					roundedAmount = Math.round(qty*price);
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				data[args.row]["unitPrice"] = price;
				data[args.row]["ltrPrice"] = parseFloat(literPrice/parseFloat(productQtyInc[prod]));
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				var totalAmount = 0;
				var totalCrates = 0;
				for (i = 0; i < data.length; i++) {
					//totalAmount += data[i]["amount"];
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
						totalCrates += data[i]["crQuantity"];
					</#if>
					totalAmount += data[i]["amount"];
				}
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
				var dispText = "";
				if(amt > 0 ){
					dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
				}
				else{
					dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
				}
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					if(totalCrates > 0 ){
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: " +  totalCrates + "]</b>";
					}
					else{
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: Rs 0 ]</b>";
					}
				</#if>
				jQuery("#totalAmount").html(dispText);
			}
			
			if (args.cell == 0 || args.cell == 1) {
				var prod = data[args.row]["cProductId"];
				
				var calcQty = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					calcQty = parseFloat(data[args.row]["crQuantity"]);
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					calcQty = parseFloat(data[args.row]["ltrQuantity"]);
				</#if>
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				var udp = data[args.row]['basicPrice'];
				var price = 0;
				if(udp){
					var basic_price = data[args.row]['basicPrice'];
					var vat_price = data[args.row]['vatPrice'];
					var cst_price = data[args.row]['cstPrice'];
					var bed_price = data[args.row]['bedPrice'];
					var serviceTax_price = data[args.row]['serviceTaxPrice'];
					var totalPrice = basic_price+vat_price+cst_price+bed_price+serviceTax_price;
					price = totalPrice;
				}
				else{
					price = parseFloat(priceTags[prod]);
				}
				var literPrice = parseFloat(priceTags[prod]);
				var calculateQty = 0;
				if(convValue != 'undefined' && convValue != null && calcQty>0){

					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
						calculateQty = parseFloat(Math.round((calcQty*convValue)*100)/100);
						data[args.row]["quantity"] = calculateQty;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
						calculateQty = parseFloat(Math.round((calcQty/convValue)*10000)/10000);
						data[args.row]["quantity"] = calculateQty;
					</#if>
				}
				
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(calculateQty)){
					calculateQty = 0;
				}
				var roundedAmount;
					roundedAmount = Math.round(calculateQty*price);
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				data[args.row]["unitPrice"] = price;
				data[args.row]["amount"] = roundedAmount;
				data[args.row]["ltrPrice"] = parseFloat(literPrice/parseFloat(productQtyInc[prod]));
				grid.updateRow(args.row);
				var totalAmount = 0;
				var totalCrates = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
						totalCrates += data[i]["crQuantity"];
					</#if>
				}
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
				var dispText = "";
				if(amt > 0 ){
					dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
				}
				else{
					dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
				}
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					if(totalCrates > 0 ){
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: " +  totalCrates + "]</b>";
					}
					else{
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: Rs 0 ]</b>";
					}
				</#if>
				jQuery("#totalAmount").html(dispText);
			}
			if (args.cell == 3) {
				var prod = data[args.row]["cProductId"];
				var baleQty = parseFloat(data[args.row]["baleQuantity"]);
				var uom = data[args.row]["cottonUom"];
				var bundleWeight = parseFloat(data[args.row]["bundleWeight"]);
				var unitPrice = parseFloat(data[args.row]["unitPrice"]);
				
				
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
					quantity = Math.round(baleQty*bundleWeight*40);
				}
				if(uom == "Half-Bale"){
					quantity = Math.round(baleQty*bundleWeight*20);
				}
				data[args.row]["quantity"] = quantity;
				data[args.row]["amount"] = Math.round(quantity*unitPrice);
				
				grid.updateRow(args.row);
				
				//jQuery("#totalAmount").html(dispText);
			}
			if (args.cell == 5) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var udp = data[args.row]['unitPrice'];
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
					roundedAmount = Math.round(qty*price);
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				
				var totalAmount = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
				}
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
				var dispText = "";
				if(amt > 0 ){
					dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
				}
				else{
					dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
				}
				
				jQuery("#totalAmount").html(dispText);
			}
			
			
		}); 
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
				if (args.cell == 1 && data[args.row] != null) {
        		var item = data[args.row];   
				var prod = data[args.row]["cProductId"];
				var uomId = productUOMMap[prod];
				var uomLabel = uomLabelMap[uomId];
				item['uomDescription'] = uomLabel;     		 		
	      		grid.invalidateRow(data.length);
	      		grid.updateRow(args.row);
	      		grid.updateRowCount();
	      		grid.render();
	      		$(grid.getCellNode(args.row, 1)).click();
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
    updateInlineTotalAmount();
		updateProductTotalAmount();
		
		mainGrid = grid;
	}
	
	//adding new Grid for adjustments
		function setupGrid2() {
    
        withAdjColumns = [
			{id:"orderAdjTypeId", name:"Adjustment Type", field:"orderAdjTypeId", width:205, minWidth:205, cssClass:"cell-title", availableTags: availableAdjTags, regexMatcher:"contains",editor: AutoCompleteEditor, validator: orderAdjFormatter,formatter: adustmentFormatter,sortable:false ,toolTip:""},
			{id:"adjAmount", name:"Amount", field:"adjAmount", width:100, minWidth:100, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Amount"},
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
			var cellNav = 2;
			
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
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data2.length) {
				grid2.getEditController().commitCurrentEdit();	
				$(grid2.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid2.getEditController().commitCurrentEdit();
				if(cell.cell == 1 || cell.cell == 2){
					jQuery("#changeSave").click();
				}
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
            //here ESC to Save grid2
        		if (cell && cell.cell == 0) {
        			$(grid2.getCellNode(cell.row - 1, cellNav)).click();
        			return false;
        		}  
        		grid2.getEditController().commitCurrentEdit();
				   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            
            else {
            	return false;
            }
        });
         
                
    	grid2.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var itemLabel = item['orderAdjTypeId'];
      		item['orderAdjTypeId'] = orderAdjLabelIdMap[itemLabel];
      		//showUDPPriceToolTip();     		 		
      		grid2.invalidateRow(data2.length);
      		data2.push(item);
      		grid2.updateRowCount();
      		grid2.render();
    	});
        
        grid2.onCellChange.subscribe(function(e,args) {
        		if (args.cell == 1) {
        		updateProductTotalAmount();
        		}
		}); 
		
		grid2.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data2[args.row] != null) {
				var itemType = data2[args.row]["orderAdjTypeId"];
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
	
	
	//onLoad  inline row update Total Amount
	function updateInlineTotalAmount() {
			
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["cProductId"];
				var uomLabel='';
				var literPrice = parseFloat(priceTags[prod]);
				if(data[i]["orderId"]){
					 literPrice = data[i]["ltrPrice"];
				}
				var uomId = productUOMMap[prod];
				if(uomId != 'undefined' || uomId != null ){
				 uomLabel = uomLabelMap[uomId];
				}
				data[i]["uomDescription"] = uomLabel;
				
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				
				var udp = data[i]['basicPrice'];
				var price = parseFloat(data[i]['unitPrice']);
				if(!price){
					//price = 0;
					price = parseFloat(priceTags[prod]);
				}
				/*
				else{
					price = parseFloat(priceTags[prod]);
				}*/
				
				if(isNaN(price) || isNaN(qty)){
					data[i]["amount"] = 0;
					data[i]["unitPrice"] = 0;
				}
				else{
					data[i]["unitPrice"] = price;
					data[i]["amount"] = Math.round((qty*price) * 100)/100;
				}
				if(productQtyInc!="" && typeof productQtyInc != "undefined" && productQtyInc[prod]!=""&& typeof productQtyInc[prod] != "undefined" && parseFloat(productQtyInc[prod]) != 0){
					data[i]["ltrPrice"] = parseFloat(literPrice/parseFloat(productQtyInc[prod]));
				}
				var crVal = 0;
				if(convValue != 'undefined' || convValue != null || convValue > 0){
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[i]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
						crVal = parseFloat(Math.round((qty*convValue)*10000)/10000);
						data[i]["ltrQuantity"] = crVal;
					</#if>
				}
				grid.updateRow(i);
			}
			
		}
	//update total amount
	function updateProductTotalAmount() {
			<#--updateUDPLabel();-->
			var totalAmount = 0;
			var totalCrates = 0;
			for (i = 0; i < data.length; i++) {
				totalAmount += data[i]["amount"];
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
					totalCrates += data[i]["crQuantity"];
				</#if>
			}
			// update AdustmentValues
			for (i = 0; i < data2.length; i++) {
			   if(!isNaN(data2[i]["adjAmount"])){
				totalAmount += data2[i]["adjAmount"];
			   }
			}
			var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			var dispText = "";
			if(amt > 0 ){
				dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
			}
			else{
				dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
			}
			<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary"  || changeFlag == "ICPTransferSale">
				if(totalCrates > 0 ){
					dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: " +  totalCrates + "]</b>";
				}
				else{
					dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: Rs 0 ]</b>";
				}
			</#if>
			
			jQuery("#totalAmount").html(dispText);
		}
	
	jQuery(function(){
	     // only setupGrid when BoothId exists
	     var boothId=$('[name=boothId]').val();
	     var partyId=$('[name=partyId]').val();
	    // alert("====partyId==="+partyId);
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
   		// lets get address and phone number for SO Depo
   		/* $('#boothId').keyup(function (e) {	
	    			getSoFacilityDetails();  			
		});   */			 
		
		
	});	
	 
	
	function getSoFacilityDetails(){
		jQuery("#name").val('');jQuery("#address2").val('');				
		//jQuery("#routeId").val('');
		jQuery("#address1").val('');
		jQuery("#address2").val('');
		jQuery("#contactNumber").val('');
		jQuery("#pinNumber").val('');					
		var action = "getBoothOwnerContactInfo";
               //var dataString = $('#boothId').val();        
               $.ajax({
             type: "POST",
             url: action,
             data: {facilityId : jQuery("#boothId").val()},
             dataType: 'json',
             success: function(result) {
             
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	 //  alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{   
            	   var contactInfo = result["contactInfo"]; 
            	  	if(typeof(contactInfo)!= "undefined"){ 	   
            	   populateContactDetails(contactInfo); 
            	   }           	   
               }
               
             } 
               }); 			
	
	}
	
	function populateContactDetails(contactInfo){
		jQuery("#name").val(contactInfo["name"]);		
		jQuery("#address1").val(contactInfo["address1"]);		
		jQuery("#address2").val(contactInfo["address2"]);		
		jQuery("#contactNumber").val(contactInfo["contactNumber"]); 
		jQuery("#pinNumber").val(contactInfo["postalCode"]);
	}
	
	<#--function updateUDPLabel() {
		for(var i=0;i<data.length;i++){
			var udpPrice = data[i]["basicPrice"];
			if(udpPrice && udpPrice>0){
				data[i]["button"] = "Update";
			}
			grid.updateRow(index);
		}
		grid.setData(data);
		grid.render();    
		
	}-->
</script>			