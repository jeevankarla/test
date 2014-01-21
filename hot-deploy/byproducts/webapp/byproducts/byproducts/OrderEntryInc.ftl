
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

<script type="application/javascript">
	var dataView;
	var dataView2;
	
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var inventoryCB = ${StringUtil.wrapString(inventoryCBJSON)!'[]'};
	var inventorySale = ${StringUtil.wrapString(inventorySaleJSON)!'[]'};
	var invDisplay = ${StringUtil.wrapString(invDisplayJSON)!'[]'};
	
	var data = ${StringUtil.wrapString(dataJSON)!'[]'};
		
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
			var productId = data[rowCount]["productId"];
			var prodId = productId.toUpperCase();
			var qty = parseFloat(data[rowCount]["quantity"]);	
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
   			}
		}			
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	function editClickHandler(row) {
		if(enableSubmit){
			enableSubmit = false;
			processChangeIndentInternal('indententry', '<@ofbizUrl>processIndentEntryNew</@ofbizUrl>', row);
		}	
	}
	
	function processIndentEntry(formName, action) {
		jQuery("#changeSave").attr( "disabled", "disabled");
		processIndentEntryInternal(formName, action);
	}
	
	
    function productFormatter(row, cell, value, columnDef, dataContext) {
        return productIdLabelMap[value];
    }
        
    function productValidator(value) {

    	var currProdCnt = 1;
	  	for (var rowCount=0; rowCount < data.length; ++rowCount)
	  	{  
			if (value == data[rowCount]["productId"]) {
				++currProdCnt;
			}
	  	}
	  	var invalidProdCheck = 0;
	  	for (var rowCount=0; rowCount < availableTags.length; ++rowCount)
	  	{  
			if (value == availableTags[rowCount]["value"]) {
				invalidProdCheck = 1;
			}
	  	}    
      	if (currProdCnt > 1) {
        	return {valid: false, msg: "Duplicate Product " + value};      				
      	}
      	if(invalidProdCheck == 0){
      		return {valid: false, msg: "Invalid Product" + value};
      	}
      	return {valid: true, msg: null};
    }    

	var mainGrid;
	
	function setupGrid1() {
		var grid;

		var columns = [
			{id:"product", name:"Product Code", field:"productId", validator: productValidator, width:200, minWidth:200, cssClass:"cell-title", availableTags: availableTags, formatter: productFormatter, editor: AutoCompleteEditor, sortable:false},	
			{id:"Qty", name:"Quantity", field:"quantity", width:100, minWidth:100, cssClass:"cell-title", editor:FloatCellEditor, sortable:false},
			{id:"amount", name:"Amount", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, focusable :false},
			{id:"inventory", name:"Closing Balance", field:"invSummary", width:150, minWidth:150, cssClass:"readOnlyColumnClass", editor:FloatCellEditor, focusable :false, sortable:false}
				
		];
		
		var options = {
			editable: true,		
			forceFitColumns: false,
			<#if booth?exists>
    			enableAddRow: true,	
    		</#if>		
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
		
    
		grid = new Slick.Grid("#myGrid1", data, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid

		grid.onKeyDown.subscribe(function(e) {		
			var cell = grid.getCellFromEvent(e);			
			if (e.which == $.ui.keyCode.RIGHT &&
				cell && cell.cell == 1 && 
				cell.row != data.length) {
				grid.getEditController().commitCurrentEdit();	
				$(grid.getCellNode(cell.row +1, 0)).click();
				e.stopPropogation();		
			}
        	else if (e.which == $.ui.keyCode.ENTER) {
				jQuery("#changeSave").click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            else {
            	return false;
            }
        });
        
       grid.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
        
        grid.onCellChange.subscribe(function(e,args) {
        	if (args.cell == 0 || args.cell == 1) {		
				var qty = parseFloat(data[args.row]["quantity"]);
				var prod = data[args.row]["productId"];
				var price = parseFloat(priceTags[prod]);
				var invCloseBal = parseFloat(inventoryCB[prod]);
				var display = invDisplay[prod];
				var invSale = parseFloat(inventorySale[prod]);
				if(isNaN(price)){
					data[args.row]["amount"] = 0;
				}
				else{
					data[args.row]["amount"] = (qty*price);
				}
				if(display == 'N' || isNaN(invCloseBal)){
					data[args.row]["invSummary"] = '';
				}
				else{
					data[args.row]["invSummary"] = (invCloseBal-qty+invSale);
				}
				grid.updateRow(args.row);
				var totalAmount = 0;
				for (i = 0; i < data.length; i++) {
					totalAmount += data[i]["amount"];
				}
				var amt = parseInt(totalAmount);
				if(amt > 0 ){
					var dispText = "<b> [Total: Rs" +  totalAmount + "]</b>";
				}
				else{
					var dispText = "<b> [Total: Rs 0 ]</b>";
				}
				jQuery("#totalAmount").html(dispText);
			}
			
		});
		updateProductTotalAmount();
		function updateProductTotalAmount() {
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["productId"];
				var price = parseFloat(priceTags[prod]);
				var inv = parseFloat(inventoryCB[prod]);
				var display = invDisplay[prod];
				if(isNaN(price)){
					data[i]["amount"] = 0;
				}
				else{
					data[i]["amount"] = (qty*price);
				}
				if(display == 'N' || isNaN(inv)){
					data[i]["invSummary"] = '';
				}
				else{
					data[i]["invSummary"] = inv;
				}
						//::TODO:: fetch the correct price here		
				grid.updateRow(i);
				
			}
			var totalAmount = 0;
			for (i = 0; i < data.length; i++) {
				totalAmount += data[i]["amount"];
			}
			var amt = parseInt(totalAmount);
			if(amt > 0 ){
				var dispText = "<b> [Total: Rs" +  totalAmount + "]</b>";
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
			{id:"boothId", name:"Party Code", field:"boothId", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
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
		setupGrid1();
		setupGrid2();		
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
  			jQuery("#boothId").focus();    
		}  		
	});		

</script>			