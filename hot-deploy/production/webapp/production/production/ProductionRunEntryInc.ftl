
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
<#--
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
-->
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
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datetimepicker/jquery-ui-timepicker-addon-0.9.3.min.js</@ofbizContentUrl>"></script>
<script type="application/javascript">
    
    	
	var dataView;
	var dataView2; 
	var dataView3;
	var grid;
	var grid2;
	var grid3;
	var data2 = ${StringUtil.wrapString(additionalChargesJSON)!'[]'};
	var data3;
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var addChargesIdLabelJSON = ${StringUtil.wrapString(addChargesIdLabelJSON)!'{}'};
	var addChargesLabelIdJSON = ${StringUtil.wrapString(addChargesLabelIdJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var conversionData = ${StringUtil.wrapString(conversionJSON)!'{}'};
	var data;
	var taskEffortId;
	var gridEditable;
	var gridReturnEditable;
	var gridDeclareEditable;
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	function prepareIssueGrid(issueProdItem, workEffortId, displayBtn){
		data = issueProdItem;
		taskEffortId = workEffortId;
		gridEditable = displayBtn;
		setupGrid1();
	}
	
	function prepareReturnGrid(returnProdItem, workEffortId, displayBtn){
		data3 = returnProdItem;
		taskEffortId = workEffortId;
		gridReturnEditable = displayBtn;
		setupGrid3();
	}
	
	function prepareDeclareGrid(declareProdItem, workEffortId, displayBtn){
		data2 = declareProdItem;
		taskEffortId = workEffortId;
		gridDeclareEditable = displayBtn;
		setupGrid2();
	}
	
	
	var enableSubmit = true;
	function processDeclareComponentEntry(){
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var dataJson = {};
		var index = 0;		
		for (var rowCount=0; rowCount < data2.length; ++rowCount)
		{ 
			var productId = data2[rowCount]["cDeclareProductId"];
			var qty = parseFloat(data2[rowCount]["declareQuantity"]);
	 		if (!isNaN(qty) && qty>0) {
	 			dataJson['productId_o_'+index] = productId;
	 			dataJson['quantity_o_'+index] = qty;
				index += 1;
   			}
		}
		dataJson['workEffortId'] = taskEffortId;
		var action = "declareRoutingTaskMaterial";
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
			success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					msg = result["_ERROR_MESSAGE_"];
					if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
						msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
					}
				}else{
					$('#declareMaterialDiv').hide();
				}					
			},
			error: function (xhr, textStatus, thrownError){
				alert("record not found :: Error code:-  "+xhr.status);
			}							
		});
	}
	function processReturnComponentEntry(){
		
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var dataJson = {};
		var index = 0;		
		for (var rowCount=0; rowCount < data3.length; ++rowCount)
		{ 
			var productId = data3[rowCount]["cReturnProductId"];
			var qty = parseFloat(data3[rowCount]["returnQuantity"]);
			//var facilityId = data3[rowCount]["returnFacilityId"];
	 		if (!isNaN(qty) && qty>0) {
	 			dataJson['productId_o_'+index] = productId;
	 			dataJson['quantity_o_'+index] = qty;
	 			//dataJson['facilityId_o_'+index] = facilityId;
				index += 1;
   			}
		}
		dataJson['workEffortId'] = taskEffortId;
		var action = "returnUnusedMaterialOfRoutingTask";
		
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
			success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					msg = result["_ERROR_MESSAGE_"];
					if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
						msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
					}
				}else{
					$('#returnMaterialDiv').hide();
					
				}					
			},
			error: function (xhr, textStatus, thrownError){
				alert("record not found :: Error code:-  "+xhr.status);
			}							
		});
	}
	
	function processIssueComponentEntry() {
		
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var dataJson = {};
		var index = 0;		
		for (var rowCount=0; rowCount < data.length; ++rowCount)
		{ 
			var productId = data[rowCount]["cIssueProductId"];
			var qty = parseFloat(data[rowCount]["issueQuantity"]);
			var facilityId = data[rowCount]["issueFacilityId"];
	 		if (!isNaN(qty) && qty>0 && facilityId) {
	 			dataJson['productId_o_'+index] = productId;
	 			dataJson['quantity_o_'+index] = qty;
	 			dataJson['facilityId_o_'+index] = facilityId;
				index += 1;
   			}
		}
		dataJson['workEffortId'] = taskEffortId;
		var action = "issueRoutingTaskNeededMaterial";
		
		$.ajax({
			 type: "POST",
             url: action,
             data: dataJson,
             dataType: 'json',
			success:function(result){
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					msg = result["_ERROR_MESSAGE_"];
					if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
						msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
					}
				}else{
					$('#addMaterialDiv').hide();
				}					
			},
			error: function (xhr, textStatus, thrownError){
				alert("record not found :: Error code:-  "+xhr.status);
			}							
		});
	}
	
    function productFormatter(row, cell, value, columnDef, dataContext) {   
        return productIdLabelMap[value];
    }
    function termFormatter(row, cell, value, columnDef, dataContext) {   
        return addChargesIdLabelJSON[value];
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
     //deliveryChallanQty validator
	function deliveryChallanQtyFormatter(row, cell, value, columnDef, dataContext) { 
		if(value == null){
			return "";
		}
        return  value;
    }
	
	function rateFormatter(row, cell, value, columnDef, dataContext) { 
		var formatValue = parseFloat(value).toFixed(2);
        return formatValue;
    }
	
	function returnQuantityValidator(value ,item) {
			for (var rowCount=0; rowCount < data3.length; ++rowCount)
	  		{ 
				if (data3[rowCount]['returnQuantity'] != null && data3[rowCount]['returnQuantity'] != undefined) {
					if(value && value >= data3[rowCount]['returnQuantity']){
        				return {valid: false, msg: "Cannot Return greater than the quantity issued " + value};      				
					}
				}
	  		}
		return {valid: true, msg: null};
	}
	
	
	function quantityValidator(value ,item) {
		var quarterVal = value*4;
		var floorValue = Math.floor(quarterVal);
		var remainder = quarterVal - floorValue;
		var remainderVal =  Math.floor(value) - value;
		 if(parseInt(value) <0 ){
			return {valid: false, msg: "required quantity Should not be less than or equals to zero" + value};
		 }
	     
      return {valid: true, msg: null};
    }
    function deliveryChallanQtyValidator(value ,item) {
		var quarterVal = value*4;
		var floorValue = Math.floor(quarterVal);
		var remainder = quarterVal - floorValue;
		var remainderVal =  Math.floor(value) - value;
	     
      return {valid: true, msg: null};
    }
    
	var mainGrid;		
	function setupGrid1() {
		    var columns = [
					{id:"cIssueProductName", name:"Material", field:"cIssueProductName", width:240, minWidth:240, cssClass:"readOnlyColumnClass", focusable :false,editor:FloatCellEditor, sortable:false, toolTip:""},
					{id:"issueQuantity", name:"Quantity", field:"issueQuantity", width:80, minWidth:80, editor:FloatCellEditor,<#if gridEditable?exists && gridEditable=='Y'>cssClass:"cell-title", formatter: quantityFormatter, validator: quantityValidator, <#else>cssClass:"readOnlyColumnClass", focusable :false,</#if>sortable:false},
					{id:"UOM", name:"UOM", field:"uomDescription", width:80, minWidth:80, cssClass:"readOnlyColumnClass", focusable :false,editor:FloatCellEditor, sortable:false},
					{id:"issueFacilityId", name:"From Store", field:"issueFacilityId", width:140, minWidth:140, cssClass:"cell-title",editor:FloatCellEditor, <#if gridEditable?exists && gridEditable=='Y'>cssClass:"cell-title", formatter: quantityFormatter, validator: quantityValidator, <#else>cssClass:"readOnlyColumnClass", focusable :false,</#if>sortable:false}
			];
		
			var options = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableColumnReorder: false,
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
			var cellNav = 2;
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
			if (args.cell == 0 || args.cell == 1) {
				var prod = data[args.row]["cProductId"];
				grid.updateRow(args.row);
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
	
	function setupGrid2() {
		   var columns = [
		   			{id:"cDeclareProductName", name:"Product", field:"cDeclareProductName", width:240, minWidth:240, cssClass:"readOnlyColumnClass", focusable :false, editor:FloatCellEditor, sortable:false, toolTip:""},
					{id:"declareQuantity", name:"Quantity", field:"declareQuantity", width:80, minWidth:80, editor:FloatCellEditor, cssClass:"cell-title", formatter: quantityFormatter, validator: quantityValidator, sortable:false},
					{id:"declareUom", name:"UOM", field:"declareUom", width:80, minWidth:80, cssClass:"readOnlyColumnClass", focusable :false,editor:FloatCellEditor, sortable:false}
			];
		
			var options = {
				editable: true,		
				forceFitColumns: false,			
				enableCellNavigation: true,
				enableColumnReorder: false,
				enableAddRow: true,
				asyncEditorLoading: false,			
				autoEdit: true,
	            secondaryHeaderRowHeight: 25
			};
		

		grid2 = new Slick.Grid("#myGrid2", data2, columns, options);
        grid2.setSelectionModel(new Slick.CellSelectionModel());        
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid2, options);
		
		// wire up model events to drive the grid
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
				&& cell.row == data.length && cell.cell == cellNav){
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
				$(grid.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid2.getEditController().commitCurrentEdit();
				if(cell.cell == 1 || cell.cell == 2){
					jQuery("#changeSave").click();
				}
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
            //here ESC to Save grid
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
      		grid2.invalidateRow(data2.length);
      		data2.push(item);
      		grid2.updateRowCount();
      		grid2.render();
    	});
        grid2.onCellChange.subscribe(function(e,args) {
			
		}); 
		
		grid2.onActiveCellChanged.subscribe(function(e,args) {
			
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
		    var columns = [
					{id:"cReturnProductName", name:"Material", field:"cReturnProductName", width:240, minWidth:240, cssClass:"readOnlyColumnClass", focusable :false,editor:FloatCellEditor, sortable:false, toolTip:""},
					{id:"returnQuantity", name:"Quantity", field:"returnQuantity", width:80, minWidth:80, editor:FloatCellEditor, <#if gridReturnEditable?exists && gridReturnEditable=='Y'>cssClass:"cell-title", formatter: quantityFormatter, validator: returnQuantityValidator, <#else>cssClass:"readOnlyColumnClass", focusable :false,</#if>sortable:false},
					{id:"returnUOM", name:"UOM", field:"returnUom", width:80, minWidth:80, cssClass:"readOnlyColumnClass", focusable :false,editor:FloatCellEditor, sortable:false},
					{id:"returnFacilityId", name:"From Store", field:"returnFacilityId", width:140, minWidth:140, cssClass:"cell-title",editor:FloatCellEditor, <#if gridEditable?exists && gridEditable=='Y'>cssClass:"cell-title", formatter: quantityFormatter, validator: quantityValidator, <#else>cssClass:"readOnlyColumnClass", focusable :false,</#if>sortable:false}
			];
		
			var options = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableColumnReorder: false,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
            
		};
		

		grid3 = new Slick.Grid("#myGrid3", data3, columns, options);
        grid3.setSelectionModel(new Slick.CellSelectionModel());        
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid3, options);
		
		// wire up model events to drive the grid
        if (data3.length > 0) {			
			$(grid3.getCellNode(0, 1)).click();
		}else{
			$(grid3.getCellNode(0,0)).click();
		}
         grid3.onKeyDown.subscribe(function(e) {
			var cellNav = 2;
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
				&& cell.row == data.length && cell.cell == cellNav){
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
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data3.length) {
				grid3.getEditController().commitCurrentEdit();	
				$(grid3.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		grid3.getEditController().commitCurrentEdit();
				if(cell.cell == 1 || cell.cell == 2){
					jQuery("#changeSave").click();
				}
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
            //here ESC to Save grid
        		if (cell && cell.cell == 0) {
        			$(grid3.getCellNode(cell.row - 1, cellNav)).click();
        			return false;
        		}  
        		grid3.getEditController().commitCurrentEdit();
				   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            
            else {
            	return false;
            }
        });
        
                
    	grid3.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var productLabel = item['cProductName']; 
      		item['cProductId'] = productLabelIdMap[productLabel];     		 		
      		grid3.invalidateRow(data3.length);
      		data3.push(item);
      		grid3.updateRowCount();
      		grid3.render();
    	});
        grid3.onCellChange.subscribe(function(e,args) {
			if (args.cell == 0 || args.cell == 1) {
				//var prod = data3[args.row]["cProductId"];
				grid3.updateRow(args.row);
			}
			
		}); 
		
		grid3.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data3[args.row] != null) {
				var prod = data3[args.row]["cProductId"];
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
		
		mainGrid = grid3;
	}
// to show special related fields in form			
		 
</script>			