
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
			var batchNo = data[rowCount]["batchNo"];
			var days = data[rowCount]["daysToStore"];
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				<#if changeFlag?exists && changeFlag != "AdhocSaleNew">
					var batchNum = jQuery("<input>").attr("type", "hidden").attr("name", "batchNo_o_" + rowCount).val(batchNo);
					jQuery(formId).append(jQuery(batchNum));
					var days = jQuery("<input>").attr("type", "hidden").attr("name", "daysToStore_o_" + rowCount).val(days);
					jQuery(formId).append(jQuery(days));
				</#if>
   			}
			
   			<#if changeFlag?exists && changeFlag != "IcpSales" && changeFlag != "IcpSalesAmul" && changeFlag != "IcpSalesBellary">
				var bPrice = data[rowCount]["basicPrice"];
				var vatPrice = data[rowCount]["vatPrice"];
				var cstPrice = data[rowCount]["cstPrice"];
				var bedPrice = data[rowCount]["bedPrice"];
				var serviceTaxPrice = data[rowCount]["serviceTaxPrice"];
				if(bPrice && bPrice>0){
					var bPriceField = jQuery("<input>").attr("type", "hidden").attr("name", "basicPrice_o_"+ rowCount).val(bPrice);
					jQuery(formId).append(jQuery(bPriceField));
					var vatPriceField = jQuery("<input>").attr("type", "hidden").attr("name", "vatPrice_o_"+ rowCount).val(vatPrice);
					jQuery(formId).append(jQuery(vatPriceField));
					var cstPriceField = jQuery("<input>").attr("type", "hidden").attr("name", "cstPrice_o_"+ rowCount).val(cstPrice);
					jQuery(formId).append(jQuery(cstPriceField));
					var bedPriceField = jQuery("<input>").attr("type", "hidden").attr("name", "bedPrice_o_"+ rowCount).val(bedPrice);
					jQuery(formId).append(jQuery(bedPriceField));
					var serviceTaxPriceField = jQuery("<input>").attr("type", "hidden").attr("name", "serviceTaxPrice_o_"+ rowCount).val(serviceTaxPrice);
					jQuery(formId).append(jQuery(serviceTaxPriceField));
				}
			</#if>
   			
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
			var promoAdj = $("#promotionAdj").val();
			var productStoreId = $("#productStoreId").val();
			var party = jQuery("<input>").attr("type", "hidden").attr("name", "partyId").val(partyId);
			var POField = jQuery("<input>").attr("type", "hidden").attr("name", "PONumber").val(poNumber);
			var promoField = jQuery("<input>").attr("type", "hidden").attr("name", "promotionAdjAmt").val(promoAdj);
			var productStore = jQuery("<input>").attr("type", "hidden").attr("name", "productStoreId").val(productStoreId);
			var tax = jQuery("<input>").attr("type", "hidden").attr("name", "orderTaxType").val(orderTaxType);
			<#if orderId?exists>
				var order = '${orderId}';
				var extOrder = jQuery("<input>").attr("type", "hidden").attr("name", "orderId").val(order);		
				jQuery(formId).append(jQuery(extOrder));
			</#if>
			
			jQuery(formId).append(jQuery(party));
			jQuery(formId).append(jQuery(POField));
			jQuery(formId).append(jQuery(promoField));
			jQuery(formId).append(jQuery(tax));
			jQuery(formId).append(jQuery(productStore));
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
	<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
		function editClickHandlerEvent(row){
			showUDPPriceToolTip(data[row], row);
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
			{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains" ,editor: AutoCompleteEditor, validator: productValidator, sortable:false ,toolTip:""},
			<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
				{id:"crQuantity", name:"Qty(Crt)", field:"crQuantity", width:60, minWidth:60, cssClass:"cell-title",editor:FloatCellEditor, sortable:false, formatter: quantityFormatter},
				{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
				{id:"batchNo", name:"Batch Number", field:"batchNo", width:65, minWidth:65, sortable:false, editor:TextCellEditor},
			<#elseif changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
				{id:"ltrQuantity", name:"Ltr/KG Qty", field:"ltrQuantity", width:65, minWidth:65, sortable:false, editor:FloatCellEditor},
				{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
				<#if changeFlag?exists &&  changeFlag != "ICPTransferSale">
					{id:"daysToStore", name:"Days", field:"daysToStore", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator , toolTip:"Days To Preserve(For Storage Charges)"},
				</#if>
			</#if>
			
			{id:"unitCost", name:"Unit Price(Rs)", field:"unitPrice", width:65, minWidth:65, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false , align:"right"},
			{id:"amount", name:"Total Amount(Rs)", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false}
				
		];
		<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale">
			columns.push({id:"button", name:"Edit Price", field:"button", width:70, minWidth:70, cssClass:"cell-title",
 				formatter: function (row, cell, id, def, datactx) { 
						return '<a href="#" class="button" onclick="editClickHandlerEvent('+row+')" value="Edit">Edit</a>'; 
 				}
 			});
 		</#if>
		
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
        	if (args.cell == 2) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale" >
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
				var crVal = 0;
				if(convValue != 'undefined' || convValue != null || convValue > 0){
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[args.row]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
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
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				var totalAmount = 0;
				var totalCrates = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
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
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
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
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
					calcQty = parseFloat(data[args.row]["crQuantity"]);
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
					calcQty = parseFloat(data[args.row]["ltrQuantity"]);
				</#if>
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
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
				var calculateQty = 0;
				if(convValue != 'undefined' && convValue != null && calcQty>0){

					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
						calculateQty = parseFloat(Math.round((calcQty*convValue)*100)/100);
						data[args.row]["quantity"] = calculateQty;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
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
				grid.updateRow(args.row);
				var totalAmount = 0;
				var totalCrates = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
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
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
					if(totalCrates > 0 ){
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: " +  totalCrates + "]</b>";
					}
					else{
						dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: Rs 0 ]</b>";
					}
				</#if>
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
			<#--updateUDPLabel();-->
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["cProductId"];
				var prodConversionData = conversionData[prod];
				var convValue = 0;
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
					convValue = prodConversionData['CRATE'];
				</#if>
				<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
					convValue = prodConversionData['LtrKg'];
				</#if>
				
				var udp = data[i]['basicPrice'];
				var price = parseFloat(data[i]['unitPrice']);
				if(!price){
					price = 0;
				}
				else{
					price = parseFloat(priceTags[prod]);
				}
				
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
					<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
						crVal = parseFloat(Math.round((qty/convValue)*100)/100);
						data[i]["crQuantity"] = crVal;
					</#if>
					<#if changeFlag?exists && changeFlag == "DepotSales" || changeFlag == "FgsSales" || changeFlag == "InterUnitTransferSale" || changeFlag == "ICPTransferSale">
						crVal = parseFloat(Math.round((qty*convValue)*10000)/10000);
						data[i]["ltrQuantity"] = crVal;
					</#if>
				}
				
				
				grid.updateRow(i);
			}
			var totalAmount = 0;
			var totalCrates = 0;
			for (i = 0; i < data.length; i++) {
				totalAmount += data[i]["amount"];
				<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
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
			<#if changeFlag?exists && changeFlag == "IcpSales" || changeFlag == "IcpSalesAmul" || changeFlag == "IcpSalesBellary">
				if(totalCrates > 0 ){
					dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: " +  totalCrates + "]</b>";
				}
				else{
					dispText += "&emsp;&emsp;&emsp;&emsp;&emsp;<b>  [Total Crates: Rs 0 ]</b>";
				}
			</#if>
			
			jQuery("#totalAmount").html(dispText);
		}
		mainGrid = grid;
	}
	
	
	
	function setupGrid2() {
		
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