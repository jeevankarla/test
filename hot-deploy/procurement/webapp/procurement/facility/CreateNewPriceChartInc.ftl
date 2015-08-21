
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
	var grid2;
	var grid3;
	var dropDownOption = "ALL";
	var optList = [];
	
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var snfLabelIdMap = ${StringUtil.wrapString(snfLabelIdJSON)!'{}'};
	var fatLabelIdMap = ${StringUtil.wrapString(fatLabelIdJSON)!'{}'};
	
	var availableTags = ${StringUtil.wrapString(slabTermsJSON)!'[]'};
	var availableTags2 = ${StringUtil.wrapString(snfTermsJSON)!'[]'};
	var availableTags3 = ${StringUtil.wrapString(fatTermsJSON)!'[]'};
	
	var data = [];
	var data2 = [];
	var data3 = [];
	
	<#if existSlabBasedJSON?has_content>
	 	data = ${StringUtil.wrapString(existSlabBasedJSON)!'[]'};
	<#else>
	 	data =[];
	</#if>
	<#if existSnfJSON?has_content>
	 	data2 = ${StringUtil.wrapString(existSnfJSON)!'[]'};
	<#else>
	 	data2 =[];
	</#if>
	<#if existFatJSON?has_content>
	 	data3 = ${StringUtil.wrapString(existFatJSON)!'[]'};
	<#else>
	 	data3 =[];
	</#if>
	
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	
	function displayChargesGrid(){
		setupGrid2();
		setupGrid3();
	}
	
	function processPriceChartEntryInternal(formName, action) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var formId = "#" + formName;
		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));
		
		
		for (var rowCount=0; rowCount < data.length; ++rowCount)
		{ 
		
			var procurementPriceTypeId = data[rowCount]["procurementPriceTypeId"];
			var prodId="";
			if(typeof(procurementPriceTypeId)!= "undefined"){ 	  
				prodId = procurementPriceTypeId.toUpperCase();
			}
			
			var snfPercent = parseFloat(data[rowCount]["snfPercent"]);
			var fatPercent = parseFloat(data[rowCount]["fatPercent"]);
			var amount = parseFloat(data[rowCount]["amount"]);
			var useTotalSolids = data[rowCount]["useTotalSolids"];
			var fromDate = data[rowCount]["fromDate"];
		
	 		if (procurementPriceTypeId) {
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "procurementPriceTypeId_o_" + rowCount).val(prodId);
				var inputSnfPercent = jQuery("<input>").attr("type", "hidden").attr("name", "snfPercent_o_" + rowCount).val(snfPercent);
				var inputFatPercent = jQuery("<input>").attr("type", "hidden").attr("name", "fatPercent_o_" + rowCount).val(fatPercent);
				var inputAmount = jQuery("<input>").attr("type", "hidden").attr("name", "amount_o_" + rowCount).val(amount);
				
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputSnfPercent));
				jQuery(formId).append(jQuery(inputFatPercent));				
				jQuery(formId).append(jQuery(inputAmount));
				
   			}
		}
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{ 
			var snfProcurementPriceTypeId = data2[rowCount]["snfProcurementPriceTypeId"];
			var prodId="";
			if(typeof(snfProcurementPriceTypeId)!= "undefined"){ 	  
				prodId = snfProcurementPriceTypeId.toUpperCase();
			}
			var snfSnf = parseFloat(data2[rowCount]["snfSnf"]);
			var snfFat = parseFloat(data2[rowCount]["snfFat"]);
			var snfPrice = parseFloat(data2[rowCount]["snfPrice"]);
	
			var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "snfProcurementPriceTypeId_o_" + rowCount).val(prodId);
			var inputSnfPercent = jQuery("<input>").attr("type", "hidden").attr("name", "snfSnf_o_" + rowCount).val(snfSnf);
			var inputFatPercent = jQuery("<input>").attr("type", "hidden").attr("name", "snfFat_o_" + rowCount).val(snfFat);
			var inputAmount = jQuery("<input>").attr("type", "hidden").attr("name", "snfPrice_o_" + rowCount).val(snfPrice);
				
			jQuery(formId).append(jQuery(inputProd));				
			jQuery(formId).append(jQuery(inputSnfPercent));
			jQuery(formId).append(jQuery(inputFatPercent));				
			jQuery(formId).append(jQuery(inputAmount));
				
		}
		
		for (var rowCount=0; rowCount < data3.length; ++rowCount)
		{ 
			var fatProcurementPriceTypeId = data3[rowCount]["fatProcurementPriceTypeId"];
			var prodId="";
			if(typeof(fatProcurementPriceTypeId)!= "undefined"){ 	  
				prodId = fatProcurementPriceTypeId.toUpperCase();
			}
			var fatSnf = parseFloat(data3[rowCount]["fatSnf"]);
			var fatFat = parseFloat(data3[rowCount]["fatFat"]);
			var fatPrice = parseFloat(data3[rowCount]["fatPrice"]);
		
			var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "fatProcurementPriceTypeId_o_" + rowCount).val(prodId);
			var inputSnfPercent = jQuery("<input>").attr("type", "hidden").attr("name", "fatSnf_o_" + rowCount).val(fatSnf);
			var inputFatPercent = jQuery("<input>").attr("type", "hidden").attr("name", "fatFat_o_" + rowCount).val(fatFat);
			var inputAmount = jQuery("<input>").attr("type", "hidden").attr("name", "fatPrice_o_" + rowCount).val(fatPrice);
			
			jQuery(formId).append(jQuery(inputProd));				
			jQuery(formId).append(jQuery(inputSnfPercent));
			jQuery(formId).append(jQuery(inputFatPercent));				
			jQuery(formId).append(jQuery(inputAmount));
				
		}
		
		alert($(formId).html());
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	
	function processIndentEntry(formName, action) {
		jQuery("#changeSave").attr( "disabled", "disabled");
		processPriceChartEntryInternal(formName, action);
		
	}
	
    function productFormatter(row, cell, value, columnDef, dataContext) {
        return productIdLabelMap[value];
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
		
      return {valid: true, msg: null};
    }
	var mainGrid;		
	function setupGrid1() {
        if(mainGrid){
           return false;
        }
        var columns = [
			{id:"procurementPriceTypeId", name:"ProcurementPriceTypeId", field:"procurementPriceTypeId", width:270, minWidth:270,cssClass:"cell-title", availableTags: availableTags, regexMatcher:"contains", editor: AutoCompleteEditor,sortable:false ,toolTip:""},
			{id:"snfPercent", name:"SnfPercent", field:"snfPercent", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			{id:"fatPercent", name:"FatPercent", field:"fatPercent", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			{id:"amount", name:"Rate", field:"amount", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			
		];
            
		var options = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 50
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
      		var productLabel = item['procurementPriceTypeId']; 
      		
      		item['procurementPriceTypeId'] = productLabelIdMap[productLabel];
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
    
    
        grid.onCellChange.subscribe(function(e,args) {
			
		}); 
		
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data[args.row] != null) {
			}
			
			if (args.cell == 0 && data[args.row] != null) {
				
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
	
	function setupGrid2() {
	
        withAdjColumns = [
        	{id:"snfProcurementPriceTypeId", name:"ProcurementPriceTypeId", field:"snfProcurementPriceTypeId", width:270, minWidth:270, cssClass:"cell-title", availableTags: availableTags2, regexMatcher:"contains", editor: AutoCompleteEditor, sortable:false ,toolTip:""},
			{id:"snfSnf", name:"SNF", field:"snfSnf", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"snfFat", name:"FAT", field:"snfFat", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"snfPrice", name:"Rate", field:"snfPrice", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, align:"right"},
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
      		var productLabel = item['snfProcurementPriceTypeId']; 
      		
      		item['snfProcurementPriceTypeId'] = snfLabelIdMap[productLabel];
      		grid2.invalidateRow(data2.length);
      		data2.push(item);
      		grid2.updateRowCount();
      		grid2.render();
    	});
    	
        
        grid2.onCellChange.subscribe(function(e,args) {

		}); 
		
		grid2.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data2[args.row] != null) {
        	
			}
			if (args.cell == 0 && data[args.row] != null) {
	      		
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
    
    function setupGrid3() {
        withAdjColumns = [
        	{id:"fatProcurementPriceTypeId", name:"ProcurementPriceTypeId", field:"fatProcurementPriceTypeId", width:270, minWidth:270, cssClass:"cell-title", availableTags: availableTags3, regexMatcher:"contains", editor: AutoCompleteEditor,sortable:false ,toolTip:""},
			{id:"fatSnf", name:"SNF", field:"fatSnf", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"fatFat", name:"FAT", field:"fatFat", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, formatter: rateFormatter, align:"right"},
			{id:"fatPrice", name:"Rate", field:"fatPrice", width:80, minWidth:80, editor:FloatCellEditor, sortable:false, align:"right"},
		];
		
		var options3 = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
			  
		grid3 = new Slick.Grid("#myGrid3", data3, withAdjColumns, options3);
        grid3.setSelectionModel(new Slick.CellSelectionModel()); 
     
		var columnpicker = new Slick.Controls.ColumnPicker(withAdjColumns, grid3, options3);
        if (data3.length > 0) {			
			$(grid3.getCellNode(0, 1)).click();
		}else{
			$(grid3.getCellNode(0,0)).click();
		}
        
        grid3.onKeyDown.subscribe(function(e) {
			var cellNav = 5;
			var cell = grid3.getCellFromEvent(e);		
			if(e.which == $.ui.keyCode.UP && cell.row == 0){
				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();
			}
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == data3.length && cell.cell == cellNav){
				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(0, 2)).click();
				e.stopPropagation();
			}else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == (data3.length-1) && cell.cell == cellNav){
				grid3.getEditController().commitCurrentEdit();
				grid3.gotoCell(data3.length, 0, true);
				$(grid3.getCellNode(data3.length, 0)).edit();
				
				e.stopPropagation();
			}
			
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.RIGHT) && cell 
				&& cell.row == data3.length && cell.cell == cellNav){
  				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(cell.row, 0)).click();
				e.stopPropagation();
			
			}else if (e.which == $.ui.keyCode.RIGHT &&
				cell && (cell.cell == cellNav) && 
				cell.row != data3.length) {
				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.DOWN && cell && (cell.cell == 3)) {
				grid3.getEditController().commitCurrentEdit();
				$(grid3.getCellNode(cell.row, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data3.length) {
				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid3.getEditController().commitCurrentEdit();
				e.stopPropagation();
            	e.preventDefault();        	
            }else {
            	return false;
            }
        });
         
    	grid3.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var productLabel = item['fatProcurementPriceTypeId']; 
      		item['fatProcurementPriceTypeId'] = fatLabelIdMap[productLabel];
      		grid3.invalidateRow(data2.length);
      		data3.push(item);
      		grid3.updateRowCount();
      		grid3.render();
    	});
    	
    	       
        grid3.onCellChange.subscribe(function(e,args) {

		}); 
		
		grid3.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data3[args.row] != null) {
			}
			if (args.cell == 0 && data3[args.row] != null) {
	      		
			}
		});
		
		grid3.onValidationError.subscribe(function(e, args) {
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
    
    
</script>			