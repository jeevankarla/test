
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
	
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var data = ${StringUtil.wrapString(orderItemsJSON)!'[]'};
	var productUOMMap = ${StringUtil.wrapString(productUOMJSON)!'{}'};
	var uomLabelMap = ${StringUtil.wrapString(uomLabelJSON)!'{}'};
	
	var partyAutoJson = ${StringUtil.wrapString(partyJSON)!'[]'};
	var partyNameObj = ${StringUtil.wrapString(partyNameObj)!'[]'};
	var paymentTermsJSON = ${StringUtil.wrapString(paymentTermsJSON)!'[]'};
	var deliveryTermsJSON = ${StringUtil.wrapString(deliveryTermsJSON)!'[]'};	
	var otherTermsJSON = ${StringUtil.wrapString(otherTermsJSON)!'[]'};	
	
	var cstlableTags = ${StringUtil.wrapString(cstJSON)!'[]'};
	var exclableTags = ${StringUtil.wrapString(excJSON)!'[]'};
	var vatlableTags = ${StringUtil.wrapString(vatJSON)!'[]'};
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
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
			var qty = parseFloat(data[rowCount]["quantity"]);
			var unitPrice = data[rowCount]["unitPrice"];
			var vatPercent = data[rowCount]["vatPercent"];
			var cstPercent = data[rowCount]["cstPercent"];
			var bedPercent = data[rowCount]["bedPercent"];
			
		
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				
				var inputPrice = jQuery("<input>").attr("type", "hidden").attr("name", "unitPrice_o_" + rowCount).val(unitPrice);
				jQuery(formId).append(jQuery(inputPrice));
				var inputVATPer = jQuery("<input>").attr("type", "hidden").attr("name", "vatPercent_o_" + rowCount).val(vatPercent);
				jQuery(formId).append(jQuery(inputVATPer));
				
				var inputCSTPer = jQuery("<input>").attr("type", "hidden").attr("name", "cstPercent_o_" + rowCount).val(cstPercent);
				jQuery(formId).append(jQuery(inputCSTPer));
				
				var inputExcisePer = jQuery("<input>").attr("type", "hidden").attr("name", "bedPercent_o_" + rowCount).val(bedPercent);
				jQuery(formId).append(jQuery(inputExcisePer));
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
		var formatValue = parseFloat(value).toFixed(4);
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
		var amt = parseFloat(Math.round((totalAmt) * 100) / 100);
			
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
    
        var columns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, <#if orderId?exists>cssClass:"readOnlyColumnClass", focusable :false,<#else>cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains", editor: AutoCompleteEditor, validator: productValidator,</#if> sortable:false ,toolTip:""},
			{id:"quantity", name:"Qty(Pkt)", field:"quantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			{id:"uomDescription", name:"UOM", field:"uomDescription", width:70, minWidth:70, cssClass:"readOnlyColumnClass", sortable:false, focusable :false, align:"right", toolTip:"Unit of Measure"},
			{id:"unitPrice", name:"Basic Unit Price", field:"unitPrice", width:130, minWidth:130, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"UD Price"},
			{id:"amount", name:"Amount(Rs)", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false},
			{id:"bedPercent", name:"Excise(%)", field:"bedPercent", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"Excise Percent", availableTags: exclableTags, editor: AutoCompleteEditor,validator:excValidator},
			{id:"vatPercent", name:"VAT(%)", field:"vatPercent", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"VAT Percent", availableTags: vatlableTags, editor: AutoCompleteEditor,validator:vatValidator},
			{id:"cstPercent", name:"CST (%)", field:"cstPercent", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right", toolTip:"CST Percentage", availableTags: cstlableTags, editor: AutoCompleteEditor,validator:cstValidator},
			
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
    	    var totalValue=0;
        	
        	var isIncTax = $('#incTax').is(':checked');
        	if (args.cell == 0 || args.cell == 1 || args.cell == 2 || args.cell == 3 || args.cell == 5 || args.cell == 6 || args.cell == 7) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var uomId = productUOMMap[prod];
				var uomLabel = uomLabelMap[uomId];
				data[args.row]['uomDescription'] = uomLabel;     		 		
	      		
				var bedPercent = parseFloat(data[args.row]["bedPercent"]);
				var vatPercent = parseFloat(data[args.row]["vatPercent"]);
				var cstPercent = parseFloat(data[args.row]["cstPercent"]);
				var price = data[args.row]['unitPrice'];
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
			
				var roundedAmount = qty*price;
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				
				if(!isIncTax){
					if(isNaN(bedPercent)){
						bedPercent = 0;
					}
					var amtBED = Math.round((roundedAmount*bedPercent)/100);
					var entryValue = roundedAmount+amtBED;
					if(isNaN(cstPercent)){
						cstPercent = 0;
					}
					var amtCST = Math.round((entryValue*cstPercent)/100);
					if(isNaN(vatPercent)){
						vatPercent = 0;
					}
					var amtVAT = Math.round((entryValue*vatPercent)/100);
					
					roundedAmount = roundedAmount+amtBED+amtCST+amtVAT;
				}
				totalValue=roundedAmount;
				data[args.row]["unitPrice"] = price;
				data[args.row]["amount"] = roundedAmount;
				grid.updateRow(args.row);
				updateInvoiceTotalAmount();
			}
			
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
	
	// update when  discunt/fright/insurence changes
	function addToInvoiceAmount(){
            var totalAmount = 0;
           // alert("==data.length==inUpdate=>"+data.length);
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
				
				 	var freightCharges=$("#freightCharges").val();
			         var discount=$("#discount").val();
			         var insurence = $("#insurence").val();
			          var packAndFowdg = $("#packAndFowdg").val();
			          var otherCharges = $("#otherCharges").val();
			         //alert("<==totalAmount===>"+totalAmount+"=freightCharges="+freightCharges+"=discount="+discount+"=insurence="+insurence);
			         if(freightCharges !=="" && (!isNaN(freightCharges))){
			          freightCharges = parseFloat(freightCharges);
			          //alert("<==totalAmount===>"+totalAmount+"==freightCharges=="+freightCharges);
			         totalAmount +=freightCharges;
			         }
			         if(insurence !=="" && (!isNaN(insurence))){
			         insurence = parseFloat(insurence);
			         // alert("<==totalAmount===>"+totalAmount+"==insurence=="+insurence);
			         totalAmount +=insurence;
			         }
			         if(discount !=="" && (!isNaN(discount))){
			        discount = parseFloat(discount);
			         totalAmount -=discount;
			         }
			         if(packAndFowdg !=="" && (!isNaN(packAndFowdg))){
			           packAndFowdg = parseFloat(packAndFowdg);
			         totalAmount +=packAndFowdg;
			         }
			         if(otherCharges !=="" && (!isNaN(otherCharges))){
			        otherCharges = parseFloat(otherCharges);
			         totalAmount +=otherCharges;
			         }
			         
			    //var amt = parseFloat(Math.round((totalAmount))); for total rounding
				var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
			
				if(amt > 0 ){
					var dispText = "<b>  [Total PO Amt: Rs " +  amt + "]</b>";
				}
				else{
					var dispText = "<b>  [Total PO Amt: Rs 0 ]</b>";
				}
				//alert("==amt="+amt);
				jQuery("#totalAmount").html(dispText);
    }
    
	jQuery(function(){
	     // only setupGrid when BoothId exists
	     var boothId=$('[name=boothId]').val();
	     var partyId=$('[name=supplierId]').val();
		 if(boothId || partyId){
		    gridShowCall();
		 	//setupGrid1();
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
    	else { 
			$("#boothId").focus();      
		}  		
	});
//
function newGrid(){
	//var data = []; 
        grid.setData(data);
        grid.render();
}
	
// to show special related fields in form			

	function gridHideCall() {
           $('#FieldsDIV').hide();
          
    }
     function gridShowCall() {
           $('#FieldsDIV').show();
    }
    
    
</script>			