
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

<script type="application/javascript">
	var dataView;
	var dataView2;
	
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var conversionData = ${StringUtil.wrapString(conversionJSON)!'{}'};
	var data = ${StringUtil.wrapString(dataJSON)!'[]'};
	var boothAutoJson = ${StringUtil.wrapString(boothsJSON)!'[]'};
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};	
	var routeAutoJson = ${StringUtil.wrapString(routesJSON)!'[]'};
	var prodIndentQtyCat=${StringUtil.wrapString(prodIndentQtyCat)!'[]'};
	var qtyInPieces=${StringUtil.wrapString(qtyInPieces)!'[]'};
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
			var UPrice = data[rowCount]["UPrice"];
			var VAT = data[rowCount]["VAT"];
			var CST = data[rowCount]["CST"];
			var Excise = data[rowCount]["Excise"];
			var bedCess = data[rowCount]["bedCessAmount"];
			var bedSecCess = data[rowCount]["bedSecCessAmount"];
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				
				var inputPrice = jQuery("<input>").attr("type", "hidden").attr("name", "UPrice_o_" + rowCount).val(UPrice);
				jQuery(formId).append(jQuery(inputPrice));
				var inputVat = jQuery("<input>").attr("type", "hidden").attr("name", "VAT_o_" + rowCount).val(VAT);
				jQuery(formId).append(jQuery(inputVat));
				var inputCst = jQuery("<input>").attr("type", "hidden").attr("name", "CST_o_" + rowCount).val(CST);
				jQuery(formId).append(jQuery(inputCst));
				var inputExcise = jQuery("<input>").attr("type", "hidden").attr("name", "excise_o_" + rowCount).val(Excise);
				jQuery(formId).append(jQuery(inputExcise));
				var inputBedCess = jQuery("<input>").attr("type", "hidden").attr("name", "bedCess_o_" + rowCount).val(bedCess);
				jQuery(formId).append(jQuery(inputBedCess));
				var inputBedSecCess = jQuery("<input>").attr("type", "hidden").attr("name", "bedSecCess_o_" + rowCount).val(bedSecCess);
				jQuery(formId).append(jQuery(inputBedSecCess));
				
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
			var orderTaxType = $("#orderTaxType").val();
			var poNumber = $("#PONumber").val();
			var sInvNumber = $("#SInvNumber").val();
			var packingType = $("#packingType").val();
			var taxInc = $("#UDP :checked");
			var insurence = $("#insurence").val();
			var SInvoiceDate = $("#SInvoiceDate").val();
			
			
			var mrnNumber=$("#mrnNumber").val();
			var freightCharges=$("#freightCharges").val();
			var discount=$("#discount").val();
			//alert("=mrnNumber="+mrnNumber+"=freightCharges="+freightCharges+"=discount="+discount+"=sInvNumber="+sInvNumber+"=poNumber="+poNumber+"=insurence="+insurence);
			var productStoreId = $("#productStoreId").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
		    var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
			var tax = jQuery("<input>").attr("type", "hidden").attr("name", "orderTaxType").val(orderTaxType);
			var pack = jQuery("<input>").attr("type", "hidden").attr("name", "packingType").val(packingType);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			var frightField = jQuery("<input>").attr("type", "hidden").attr("name", "freightCharges").val(freightCharges);
			var dicountField = jQuery("<input>").attr("type", "hidden").attr("name", "discount").val(discount);
			var mrnNumberField=jQuery("<input>").attr("type", "hidden").attr("name", "mrnNumber").val(mrnNumber);
			var sInvNumberField=jQuery("<input>").attr("type", "hidden").attr("name", "SInvNumber").val(sInvNumber);
			var insurenceField=jQuery("<input>").attr("type", "hidden").attr("name", "insurence").val(insurence);
			var SInvoiceDateField=jQuery("<input>").attr("type", "hidden").attr("name", "SInvoiceDate").val(SInvoiceDate);
			<#if orderId?exists>
				var order = '${orderId}';
				var extOrder = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(order);		
				jQuery(formId).append(jQuery(extOrder));
			</#if>
			jQuery(formId).append(jQuery(party));
			jQuery(formId).append(jQuery(POField));
			jQuery(formId).append(jQuery(pack));
			jQuery(formId).append(jQuery(tax));
			jQuery(formId).append(jQuery(productStore));
			jQuery(formId).append(jQuery(frightField));
			jQuery(formId).append(jQuery(dicountField));
			jQuery(formId).append(jQuery(mrnNumberField));
			jQuery(formId).append(jQuery(sInvNumberField));
			jQuery(formId).append(jQuery(insurenceField));
			jQuery(formId).append(jQuery(SInvoiceDateField));
		</#if>
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	<#assign editClickHandlerAction =''>	
	<#if changeFlag?exists && changeFlag=='supplDeliverySchedule'>
		 <#assign editClickHandlerAction='processSupplDeleverySchdule'>
	<#elseif changeFlag?exists && changeFlag=='ByProdGatePass'>
	      <#assign editClickHandlerAction='processSupplDeleverySchdule'>
	<#else>
		 <#assign editClickHandlerAction='processIndentEntryNew'>		 	
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

		var grid;
		
		var columns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, cssClass:"cell-title", availableTags: availableTags, editor: AutoCompleteEditor, validator: productValidator, sortable:false ,toolTip:""},
			{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			{id:"UPrice", name:"Price", field:"UPrice", width:130, minWidth:130, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"UD Price"},
			{id:"VAT", name:"VAT-Amount", field:"VAT", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Vat Price"},
			{id:"Excise", name:"Bed-Amount", field:"Excise", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Bed Amount"},
			{id:"bedCessAmount", name:"Bed-CessAmount", field:"bedCessAmount", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Bed Cess Amount"},
			{id:"bedSecCessAmount", name:"Bed-SecCessAmount", field:"bedSecCessAmount", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Bed Sec Cess Amount"},
			{id:"CST", name:"CST-Amount", field:"CST", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"CST Percentage"},
			
			//{id:"amount", name:"Total Amount(Rs)", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false}
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
      		item['cProductId'] = productLabelIdMap[productLabel];     		 		
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
        grid.onCellChange.subscribe(function(e,args) {
        	if (args.cell == 2	) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				var price = parseFloat(priceTags[prod]);
				var crVal = 0;
				if(convValue != 'undefined' || convValue != null || convValue > 0){
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[args.row]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
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
			//alert("=prod="+prod+"=qty="+qty+"=price="+price);
				data[args.row]["unitPrice"] = price;
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				var totalAmount = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
				}
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			
				if(amt > 0 ){
					var dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
				}
				else{
					var dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
				}
				jQuery("#totalAmount").html(dispText);
			}
			
			if (args.cell == 2) {
				var prod = data[args.row]["cProductId"];
				var calcQty = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
					calcQty = parseFloat(data[args.row]["crQuantity"]);
				</#if>
				<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					calcQty = parseFloat(data[args.row]["ltrQuantity"]);
				</#if>
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				var price = parseFloat(priceTags[prod]);
				var calculateQty = 0;
				if(convValue != 'undefined' && convValue != null && calcQty>0){

					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
						calculateQty = parseFloat(Math.round((calcQty*convValue)*100)/100);
						data[args.row]["quantity"] = calculateQty;
					</#if>
					<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
						calculateQty = parseFloat(Math.round((calcQty/convValue)*100)/100);
						data[args.row]["quantity"] = calculateQty;
					</#if>
				}
				   var basic_price = data[args.row]['UPrice'];
					var vat_price = data[args.row]['VAT'];
					var cst_price = data[args.row]['CST'];
					var bed_price = data[args.row]['Excise'];
					
					var totalPrice = basic_price+vat_price+cst_price+bed_price;
					price = totalPrice;
					
				
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
				grid.updateRow(args.row);
				var totalAmount = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
				}
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			
				if(amt > 0 ){
					var dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
				}
				else{
					var dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
				}
				jQuery("#totalAmount").html(dispText);
			}
			
		
		}); 
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data[args.row] != null) {
				var prod = data[args.row]["cProductId"];
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
		updateProductTotalAmount();
		function updateProductTotalAmount() {
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["cProductId"];
				
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				
				var price = parseFloat(priceTags[prod]);
				if(isNaN(price) || isNaN(qty)){
					data[i]["amount"] = 0;
					data[i]["unitPrice"] = 0;
				}
				else{
					data[i]["unitPrice"] = price;
					data[i]["amount"] = Math.round((qty*price) * 100)/100;
				}
				var crVal = 0;
				if(convValue != 'undefined' || convValue != null || convValue > 0){
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[i]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "PowderSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
						crVal = parseFloat(Math.round((qty*convValue)*100)/100);
						data[i]["ltrQuantity"] = crVal;
					</#if>
				}
				
				grid.updateRow(i);
			}
			var totalAmount = 0;
			for (i = 0; i < data.length; i++) {
				totalAmount += data[i]["amount"];
			}
			var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			if(amt > 0 ){
				var dispText = "<b> [Total: Rs" +  amt + "]</b>";
			}
			else{
				var dispText = "<b> [Total: Rs 0 ]</b>";
			}
			jQuery("#totalAmount").html(dispText);
		}
		mainGrid = grid;
	}
	
	function setupGrid2() {
		var grid;
		var data = [
			<#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>	
				{"id":"1", "boothId":"${lastChangeSubProdMap.boothId}" 					
				<#if prodList?exists>
					<#list prodList as product>				
					, "${product.productId}":"${lastChangeSubProdMap[product.productId]}"
					</#list> 
				</#if>				
				}				
			</#if>
		];

		var columns = [
			{id:"boothId", name:"RetailerCode[Name]", field:"boothId", width:180, minWidth:120, cssClass:"cell-title", sortable:false},
        	<#if prodList?exists>
				<#list prodList as product>				
					{id:"${product.productId}", name:"${product.productName?if_exists}", field:"${product.productId}", width:75, minWidth:75, editor:FloatCellEditor}<#if product_has_next>,</#if>
				</#list>
			</#if>		
		];

		var options = {
			editable: false,		
			forceFitColumns: false,
			enableCellNavigation: false,		
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView2 = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid2", dataView2, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());

		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid
		dataView2.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView2.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});
            
		// initialize the model after all the events have been hooked up
		dataView2.beginUpdate();
		dataView2.setItems(data);
		dataView2.endUpdate();

	}
	
	
	jQuery(function(){
	     // only setupGrid when BoothId exists
	     var boothId=$('[name=boothId]').val();
	     var partyId=$('[name=partyId]').val();
		 if(boothId || partyId){
		 	setupGrid1();
	     }
	     <#if screenFlag?exists && screenFlag == "AdhocSaleNew">
	     	setupGrid2();
	     </#if>
				
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
	
</script>			