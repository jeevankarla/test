
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
	var grid;
	var grid2;
	var withOutBedcolumns;
	var withAdjColumns;
	var withBedcolumns;
	var data2 = [];
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var availableAdjTags = ${StringUtil.wrapString(invoiceAdjItemsJSON)!'[]'};
	var invoiceAdjLabelJSON = ${StringUtil.wrapString(invoiceAdjLabelJSON)!'{}'};
	var invoiceAdjLabelIdMap = ${StringUtil.wrapString(invoiceAdjLabelIdJSON)!'{}'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var conversionData = ${StringUtil.wrapString(conversionJSON)!'{}'};
	//var data = ${StringUtil.wrapString(dataJSON)!'[]'};
	var data = ${StringUtil.wrapString(invoiceItemsJSON)!'[]'};
	var data2 = ${StringUtil.wrapString(adjustmentJSON)!'[]'};
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};	
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
			
			var VATPer = data[rowCount]["VatPercent"];
			var CSTPer = data[rowCount]["CSTPercent"];
			var ExcisePer = data[rowCount]["ExcisePercent"];
			var bedCessPer = data[rowCount]["bedCessPercent"];
			var bedSecCessPer = data[rowCount]["bedSecCessPercent"];
			
		
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
				
				//percentages
				
				var inputVATPer = jQuery("<input>").attr("type", "hidden").attr("name", "VatPercent_o_" + rowCount).val(VATPer);
				jQuery(formId).append(jQuery(inputVATPer));
				
				var inputCSTPer = jQuery("<input>").attr("type", "hidden").attr("name", "CSTPercent_o_" + rowCount).val(CSTPer);
				jQuery(formId).append(jQuery(inputCSTPer));
				
				var inputExcisePer = jQuery("<input>").attr("type", "hidden").attr("name", "ExcisePercent_o_" + rowCount).val(ExcisePer);
				jQuery(formId).append(jQuery(inputExcisePer));
				
				var inputbedCessPer = jQuery("<input>").attr("type", "hidden").attr("name", "bedCessPercent_o_" + rowCount).val(bedCessPer);
				jQuery(formId).append(jQuery(inputbedCessPer));
				
				var inputbedSecCessPer = jQuery("<input>").attr("type", "hidden").attr("name", "bedSecCessPercent_o_" + rowCount).val(bedSecCessPer);
				jQuery(formId).append(jQuery(inputbedSecCessPer));
				
				
   			}
		}
		
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{ 
			var invItemTypeId = data2[rowCount]["invoiceItemTypeId"];
			var adjAmt = parseFloat(data2[rowCount]["adjAmount"]);
	 		if (!isNaN(adjAmt)) {	 		
				var inputInv = jQuery("<input>").attr("type", "hidden").attr("name", "invoiceItemTypeId_o_" + rowCount).val(invItemTypeId);
				var inputAmt = jQuery("<input>").attr("type", "hidden").attr("name", "adjAmt_o_" + rowCount).val(adjAmt);
				jQuery(formId).append(jQuery(inputInv));				
				jQuery(formId).append(jQuery(inputAmt));
			}
		}
		
		var dataString = $("#indententryinit").serializeArray();
		/*$.each(dataString , function(i, fd) {
   			if(fd.name === "routeId"){
   				var route = jQuery("<input>").attr("type", "hidden").attr("name", "routeId").val(fd.value);
   				jQuery(formId).append(jQuery(route));
   			 }
		});*/
		var invoiceDate = $("#effectiveDate").val();
		var invoiceDateField=jQuery("<input>").attr("type", "hidden").attr("name", "invoiceDate").val(invoiceDate);
		jQuery(formId).append(jQuery(invoiceDateField));
		
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
    
    function invoiceItemFormatter(row, cell, value, columnDef, dataContext) {
        if(value != undefined){
			if(invoiceAdjLabelJSON[value]){
				return invoiceAdjLabelJSON[value];
			}
			else{
				var adjTermId = invoiceAdjLabelIdMap[value]; 
				data2[row]['invoiceItemTypeId'] = adjTermId;
				return invoiceAdjLabelJSON[adjTermId]; 
			}
		}
        
    }
	function invoiceTypeValidator(value,item) {
      
      	var valueId = invoiceAdjLabelIdMap[value];
    	var currItemCnt = 1;
	  	for (var rowCount=0; rowCount < data2.length; ++rowCount)
	  	{ 
			if (data2[rowCount]['invoiceItemTypeId'] != null && data2[rowCount]['invoiceItemTypeId'] != undefined && valueId == data2[rowCount]['invoiceItemTypeId']) {
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
		if(isNaN(value)){
			value = 0;
		}		
		var formatValue = parseFloat(value).toFixed(4);
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
    
             withOutBedcolumns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, cssClass:"readOnlyColumnClass", focusable :false, sortable:false ,toolTip:""},
			{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"readOnlyColumnClass",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter, focusable :false},
			{id:"UPrice", name:"Unit Price", field:"UPrice", width:130, minWidth:130, cssClass:"readOnlyColumnClass", editor:FloatCellEditor, sortable:false, focusable :false, align:"right", toolTip:"UD Price"},
			
			{id:"amount", name:"Total Basic Amount", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false},
			{id:"VatPercent", name:"VAT(%)", field:"VatPercent", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Vat Price"},
			{id:"VAT", name:"VAT-Amount", field:"VAT", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Vat Percent"},
			
			{id:"CSTPercent", name:"CST (%)", field:"CSTPercent", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"CST Percentage"},
			{id:"CST", name:"CST-Amount", field:"CST", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"CST Percentage"},
			
		];
		  
		  var columns=withOutBedcolumns;
		  
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
        	var totalValue=0;
        
        	if (args.cell == 2 || args.cell == 1) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
			
				var price = data[args.row]['UPrice'];
		
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
				var roundedAmount;
				roundedAmount = qty*price;
				//roundedAmount = Math.round(qty*price);
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				totalValue=roundedAmount;
			//alert("=prod="+prod+"=qty="+qty+"=price="+price);
				data[args.row]["UPrice"] = price;
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				updateInvoiceTotalAmount();
			}
			
			 
            if(args.cell == 4) {
            	var amountAftereExcise = 0;
              	if(!isNaN(data[args.row]['amount'])){
					amountAftereExcise += data[args.row]['amount'];
			   	}
				var vat_percent= data[args.row]['VatPercent'];
				if(isNaN(vat_percent)){
					vat_percent = 0;
				}
				var vatUnit = vat_percent/100;
				var vatAmount=amountAftereExcise*vatUnit;
				if(isNaN(vatAmount)){
					vatAmount = 0;
				}
				data[args.row]["VAT"] = vatAmount;
				updateInvoiceTotalAmount();
			}
		  	if(args.cell == 5) {
		  		var vat_percent= data[args.row]['VatPercent'];
				if(isNaN(vat_percent)){
					vat_percent = 0;
				}
				var vat_Amnt= data[args.row]['VAT'];
				if(isNaN(data[args.row]['VAT'])){
					vat_Amnt = 0;
			    }
			    if(vat_Amnt>0 && vat_percent<=0 ){
					alert("'vat Percent' can not be empty if 'vat Amount' is grater than ZERO");
					 data[args.row]["VAT"] = 0;
				 	grid.updateRow(args.row);
				}
		   		updateInvoiceTotalAmount();
	       	}
            if(args.cell==6) {
              	var amountAftereExcise = 0;
              	if(!isNaN(data[args.row]['amount'])){
					amountAftereExcise += data[args.row]['amount'];
			   	}
				var cst_percent= data[args.row]['CSTPercent'];
				if(isNaN(cst_percent)){
					cst_percent = 0;
				}
				var cstUnit = cst_percent/100;
				var cstAmount=amountAftereExcise*cstUnit;
				if(isNaN(cstAmount)){
					cstAmount = 0;
				}
				data[args.row]["CST"] = cstAmount;
				updateInvoiceTotalAmount();
			}
			if(args.cell == 7) {
				var cst_percent= data[args.row]['CSTPercent'];
				if(isNaN(cst_percent)){
					cst_percent = 0;
				}
				var cst_Amnt= data[args.row]['CST'];
				if(isNaN(data[args.row]['CST'])){
					cst_Amnt = 0;
			    }
		      	if(cst_Amnt>0 && cst_percent<=0 ){
					alert("'cst Percent' can not be empty if 'cst Amount' is grater than ZERO");
				 	data[args.row]["CST"] = 0;
				 	grid.updateRow(args.row);
				}
				updateInvoiceTotalAmount();
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
    
	mainGrid = grid;
	}
	
	//update invoice total amount
	
	function updateInvoiceTotalAmount(){
        var totalAmount = 0;
		for (i = 0; i < data.length; i++) {
		   if(!isNaN(data[i]["amount"])){
			totalAmount += data[i]["amount"];
		   }
		   if(!isNaN(data[i]["Excise"])){
			totalAmount += data[i]["Excise"];
		   }
		   if(!isNaN(data[i]["bedCessAmount"])){
			totalAmount += data[i]["bedCessAmount"];
		   }
		   if(!isNaN(data[i]["bedSecCessAmount"])){
			totalAmount += data[i]["bedSecCessAmount"];
		   }
		  
		   if(!isNaN(data[i]["VAT"])){
			totalAmount += data[i]["VAT"];
		   }
		   if(!isNaN(data[i]["CST"])){
			totalAmount += data[i]["CST"];
		   }
		   
		}
		
		// update AdustmentValues
		for (i = 0; i < data2.length; i++) {
		   if(!isNaN(data2[i]["adjAmount"])){
		   		var termType = data2[i]["invoiceItemTypeId"];
		   		if(termType == "COGS_DISC" || termType == "COGS_DISC_ATR"){
		   			totalAmount -= data2[i]["adjAmount"];
		   		}
		   		else{
		   			totalAmount += data2[i]["adjAmount"];
		   		}
				
		   }
		}
		var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
	
		if(amt > 0 ){
			var dispText = "<b>  [Invoice Amt: Rs " +  amt + "]</b>";
		}
		else{
			var dispText = "<b>  [Invoice Amt: Rs 0 ]</b>";
		}
		//alert("==amt="+amt);
		jQuery("#totalAmount").html(dispText);
    }
	
	
	function setupGrid2() {
    
        withAdjColumns = [
			{id:"invoiceItemTypeId", name:"Adjustment Type", field:"invoiceItemTypeId", width:205, minWidth:205, cssClass:"cell-title", availableTags: availableAdjTags, regexMatcher:"contains",editor: AutoCompleteEditor, validator: invoiceTypeValidator,formatter: invoiceItemFormatter,sortable:false ,toolTip:""},
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
      		var itemLabel = item['invoiceItemTypeId'];
      		item['invoiceItemTypeId'] = invoiceAdjLabelIdMap[itemLabel];     		 		
      		grid2.invalidateRow(data2.length);
      		data2.push(item);
      		grid2.updateRowCount();
      		grid2.render();
    	});
        
        grid2.onCellChange.subscribe(function(e,args) {
        		if (args.cell == 1) {
        			updateInvoiceTotalAmount();
        		}
		}); 
		
		grid2.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data2[args.row] != null) {
				var itemType = data2[args.row]["invoiceItemTypeId"];
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
	    
	//update ExciseElementValues
    function updateGridColumnsValues(){
		for (i = 0; i < data.length; i++) {
		   if(!isNaN(data[i]["Excise"])){
		   data[args.row]["CST"] = cstAmount;
		   data[i]["Excise"]=0;
			totalAmount += data[i]["Excise"];
		   }
		   if(!isNaN(data[i]["bedCessAmount"])){
			data[i]["bedCessAmount"]=0;
		   }
		   if(!isNaN(data[i]["bedSecCessAmount"])){
			data[i]["bedSecCessAmount"]=0;
		   }
		   if(!isNaN(data[i]["VAT"])){
			data[i]["VAT"]=0;
		   }
		   if(!isNaN(data[i]["CST"])){
			data[i]["CST"]=0;
		   }
		   updateInvoiceTotalAmount();
		}		
     }
	
	jQuery(function(){
	     var partyId=$('[name=partyId]').val();
		 if(partyId){
		    gridShowCall();
		 	setupGrid1();
		 	setupGrid2();
		 	updateInvoiceTotalAmount();
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
	
	$(document).ready(function(){
	   $(function() {
			$( "#indententryinit" ).validate();
		});	
	});	
	 
	function gridHideCall() {
           $('#FieldsDIV').hide();
          
    }
     function gridShowCall() {
           $('#FieldsDIV').show();
    }
</script>			