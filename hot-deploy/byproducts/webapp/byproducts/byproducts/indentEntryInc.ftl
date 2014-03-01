
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<style type="text/css">
	.cell-title {
		font-weight: bold;
	}
	.cell-effort-driven {
		text-align: center;
	}
	.readOnlyColumnClass {
		font-weight: normal;
		background: mistyrose;
		text-align: right;
	}
	
	.tooltipbold { /* tooltip style */
    background-color: #ffffbb;
    border: 0.1em solid #999999;
    color: #000000;
    font-style: italic;
    font-weight: bold;
    font-size: 110%;
    margin: 0.4em;
    padding: 0.1em;
	}
	
	.toolTipNotice { /* tooltip style */
    	background-color: #ffffbb;
    	border: 0.1em;
    	color: #f97103;
    	font-size: 100%;
    	margin: 0.4em;
    	padding: 0.1em;
	}	
</style>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-1.4.3.min.js</@ofbizContentUrl>"></script>
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
<script type="application/javascript">
function cleanUpGrid(value){
	$('[name=boothId]').val(value);
	$('[name=tempRouteId]').val('');
    $('span#boothTooltip').html('<label></label>');
	//$('span#routeTooltip').html('<label></label>');
	$('span#boothDepositTip').html('<label></label>');
	//$('span#routeCrateTotal').html('<label></label>');
	var emptyJson= [];
	$('div#tempRouteDiv').css("display","none");
	updateGrid1(emptyJson);
	//updateGrid2(emptyJson);
}
function updateGrid(){
 	screenFlag = '${screenFlag}';
	if(!$('[name=boothId]').val()){
		return;
	}
	var action;
	if(screenFlag == "DSCorrection"){
		action = "getBoothOrderDetailsJson";
	}
	else{
		action = "getBoothChandentIndentJson";
	}
	var dataJson = {"boothId":$('[name=boothId]').val(),
						"supplyDate":$('[name=effectiveDate]').val(),
						"subscriptionTypeId": $('[name=subscriptionTypeId]').val(),
						"productSubscriptionTypeId" : $('[name=productSubscriptionTypeId]').val(),
						"tripId" : $('[name=tripId]').val(),
						"routeId" : $('[name=routeId]').val()							
					};
	 $('div#changeIndentEntry_spinner').removeClass("errorMessage");
    	   $('div#changeIndentEntry_spinner')
    		  .html('<img src="/images/ajax-loader64.gif">');				

		$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
	            
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
						$('div#changeIndentEntry_spinner').html('');
						msg = result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"];
						  $('div#errorMsg').html('<span style="color:red; font-size:10pt; font-stlye:bold">"'+msg+'"</span>');
                	   $('div#errorMsg').delay(7000).fadeOut('slow');
                	    $('#myGrid1').hide();
						//$('div#errorMsg').html('<label>'+msg +'</label>');
						
					}else{
					    $('div#changeIndentEntry_spinner').html('');
						var changeIndentProductList = result["changeIndentProductList"];
						var tripId = result["tripId"];
						if(screenFlag != 'DSCorrection'){
							var categoryTot = result["categoryTotals"];
							var tempRouteId = result["tempRouteId"];
							var  tripNo = tripId.charAt(tripId.length-1);
							var routeCrateTotal = result["routeCrateTotal"];
							prodIndentQtyCat = result["prodIndentQtyCat"];
							priceTags = result["productPrice"];
							qtyInPieces = result["qtyInPieces"];
							if(routeCapacity != undefined){
								if(routeCapacity < routeCrateTotal){
									alert("Route capacity exceeded");
								}
							}
						}
						priceTags = result["productPrice"];
						var boothName = result["boothName"];
						var routeId = result["routeId"];
						var routeName = result["routeName"];
						var securityDeposit = result["securityDeposit"];
						var routeCapacity = result["routeCapacity"];
						
						
						updateGrid1(changeIndentProductList);
						if(screenFlag != "DSCorrection"){
							updateGrid2(categoryTot);	
						}
						$('span#boothTooltip').html('<label>'+boothName +'</label>');
						$('span#boothDepositTip').html('<label> Security Deposit:: Rs. '+securityDeposit +'</label>');
						$('div#tempRouteDiv').css("display","block");
						$('span#tempRouteDis').html('<b>'+tempRouteId +'</b>');
              			$('[name=tempRouteId]').val(tempRouteId);
              			$('[name=routeId]').val(routeId);
              			$('span#routeTooltip').html('<label>'+routeName+'</label>');
						$('span#routeCapacity').html('<b>'+routeCapacity +'</b>');
						$('span#routeName').html('<b>'+routeName +'</b>');
						$('span#tripName').html('<b>'+tripNo +'</b>');
						$('span#routeCrateTotal').html('<b>'+routeCrateTotal +'</b>');
						
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
	
	
}
//end of update grid
	var priceTags; 
	var prodIndentQtyCat;
	var qtyInPieces;
	var data = [];
	var data1=[];
	var dataView;
	var dataView2;
	var dataView3;
	var screenFlag = '${screenFlag?if_exists}';
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	//var uomMap = ${StringUtil.wrapString(uomMapJSON)!'{}'};
	var productQtyInc = ${StringUtil.wrapString(productQtyIncJSON)!'{}'};
	var priceTags;
	//var productCrates = ${StringUtil.wrapString(productCratesJSON)!'{}'};
		
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}

	
	function productFormatter(row, cell, value, columnDef, dataContext) { 
		if(productIdLabelMap[value] == null){
			return "";
		}		
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
		/*var qty = data[row]['cQuantity'];
		var floorValue = Math.floor(qty);
		if(qty != null){
			
			if ((data[row]['uomId']) == "C") {
				var quarterVal = floorValue*4;
			   	var nearestVal = quarterVal/4;	
			   	data[row]['cQuantity'] = nearestVal;
				return  nearestVal;
			}else{
				data[row]['cQuantity'] = floorValue;
				return  floorValue;
			}
		}	*/				     
        return  value;
    }
	
	function rateFormatter(row, cell, value, columnDef, dataContext) { 
		var formatValue = parseFloat(value).toFixed(2);
        return formatValue;
    }
	
	
	
	function quantityValidator(value ,item) {
		var indentCat = item['uomId'];
		var quarterVal = value*4;
		var floorValue = Math.floor(quarterVal);
		var remainder = quarterVal - floorValue;
		var remainderVal =  Math.floor(value) - value;
		if (indentCat == "C") {
				if(remainder !=0 ){
					 	return {valid: false, msg: "packets should not be in decimals " + value};
				}
			}else if( remainderVal !=0){
				return {valid: false, msg: "qty not be in decimals " + value};
			}
	  
      return {valid: true, msg: null};
    }
	
	
	
	var _grid;
	 function setupGrid1(ajaxJson) {
		var grid;
		data = ${StringUtil.wrapString(dataJSON)!'[]'};
		if(ajaxJson != ''){
			data = ajaxJson;
		}
		var columns = [
			{id:"cProductName", name:"Product", field:"cProductName", width:160, minWidth:160, cssClass:"cell-title", availableTags: availableTags, editor: AutoCompleteEditor, validator: productValidator,sortable:false ,toolTip:""},
			{id:"cQuantity", name:"Quantity", field:"cQuantity", width:80, minWidth:80, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			<#if screenFlag?exists && screenFlag != 'DSCorrection'>
				{id:"supply", name:"C/P/B ", field:"uomId", width:35, minWidth:35, cssClass:"readOnlyColumnClass", sortable:false, focusable :false},
				{id:"LtrKgs", name:"Ltr/Kgs", field:"LtrKgs", width:65, minWidth:65, cssClass:"readOnlyColumnClass", sortable:false, focusable :false , align:"right"},
			</#if>
			{id:"unitCost", name:"Unit Price(Rs)", field:"unitPrice", width:65, minWidth:65, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false , align:"right"},
			{id:"amount", name:"Total Amount(Rs)", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, formatter: rateFormatter, focusable :false}	
		];
		//var enableNewRow = ($('[name=boothId]').val() != null);
		var options = {
			editable: true,		
			forceFitColumns: false,			
			enableCellNavigation: true,
			enableAddRow: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
		

		_grid = new Slick.Grid("#myGrid1", data, columns, options);
        _grid.setSelectionModel(new Slick.CellSelectionModel());
        
         var columnpicker = new Slick.Controls.ColumnPicker(columns, _grid, options);
		
		if (data.length > 0) {			
			$(_grid.getCellNode(0, 1)).click();
		}else{
			$(_grid.getCellNode(0,0)).click();
		}
		_grid.onKeyDown.subscribe(function(e) {
			var cell = _grid.getCellFromEvent(e);		
			if(e.which == $.ui.keyCode.UP && cell.row == 0){
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();
			}
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == data.length && cell.cell == 1){
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(0, 1)).click();
				e.stopPropagation();
			}else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == (data.length-1) && cell.cell == 1){
				_grid.getEditController().commitCurrentEdit();
				_grid.gotoCell(data.length, 0, true);
				$(_grid.getCellNode(data.length, 0)).edit();
				
				e.stopPropagation();
			}
			
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.RIGHT) && cell 
				&& cell.row == data.length && cell.cell == 1){
  				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row, 0)).click();
				e.stopPropagation();
			
			}else if (e.which == $.ui.keyCode.RIGHT &&
				cell && (cell.cell == 1) && 
				cell.row != data.length) {
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data.length) {
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row, 1)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        		if (cell && cell.cell == 0) {
					_grid.getEditController().commitCurrentEdit();	
					if (cell.row == 0) {
						_grid.navigateRight();    
						_grid.navigateUp(); 
					} else {
        				$(_grid.getCellNode(cell.row - 1, 1)).click();
        			}
        			e.stopPropagation();	
        			return false;
        		}  
        		_grid.getEditController().commitCurrentEdit();
				//jQuery("#changeSave").click();
				if(cell.row == data.length){
					$(_grid.getCellNode(cell.row, 1)).click();
				}else{
					$(_grid.getCellNode(cell.row, 1)).click();
				}
				//$(_grid.getCellNode(cell.row, 1)).click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
        		if (cell && cell.cell == 0) {
        			$(_grid.getCellNode(cell.row - 1, 1)).click();
        			return false;
        		}  
        		_grid.getEditController().commitCurrentEdit();
				jQuery("#changeSave").click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            
            else {
            	return false;
            }
        });
         
        _grid.onAddNewRow.subscribe(function (e, args) {
      		var item = args.item;   
      		var productLabel = item['cProductName']; 
      		item['cProductId'] = productLabelIdMap[productLabel];     		 		
      		_grid.invalidateRow(data.length);
      		data.push(item);
      		_grid.updateRowCount();
      		_grid.render();
    	});
    	      
        _grid.onCellChange.subscribe(function(e,args) {
        	if (args.cell == 0 || args.cell == 1) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["cQuantity"]);
				var price = parseFloat(priceTags[prod]);
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
				var roundedAmount;
				if(screenFlag != 'DSCorrection'){
					var crateQty = parseFloat(qtyInPieces[prod]);
					var supType = prodIndentQtyCat[prod];
					//data[args.row]["uomId"] = supType.charAt(0);
					//data[args.row]["LtrKgs"] = parseFloat(productQtyInc[prod])*qty*crateQty;
					roundedAmount = Math.round(qty*price);
				}
				else{
					roundedAmount = Math.round(qty*price);
				}
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				data[args.row]["unitPrice"] = price;
				data[args.row]["amount"] = roundedAmount;
				_grid.updateRow(args.row);
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
		
		_grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data[args.row] != null) {
				var prod = data[args.row]["cProductId"];
				if(prod != null && screenFlag != 'DSCorrection' ){
					var supType = prodIndentQtyCat[prod];
					if(supType !=null){
						data[args.row]["uomId"] = supType.charAt(0);
						_grid.updateRow(args.row);
					}
				}
			}
			
		});
		
		_grid.onValidationError.subscribe(function(e, args) {
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

    })
		 
		
	}
	var absGrid;
	function setupGrid2(ajaxJson) {
		
		if(ajaxJson != ''){
			data1 = ajaxJson;
		}

		var columns = [
			{id:"categoryId", name:"Category", field:"categoryId", width:200, minWidth:200, cssClass:"cell-title", sortable:false},
			{id:"totalLtr", name:"Total in Ltrs/Kgs", field:"totalLtr", width:150, minWidth:150, cssClass:"cell-title", sortable:false}
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
		absGrid = new Slick.Grid("#myGrid2", dataView2, columns, options);
        absGrid.setSelectionModel(new Slick.CellSelectionModel());

		var columnpicker = new Slick.Controls.ColumnPicker(columns, absGrid, options);
		
		// wire up model events to drive the grid
		dataView2.onRowCountChanged.subscribe(function(e,args) {
			absGrid.updateRowCount();
            absGrid.render();
		});
		dataView2.onRowsChanged.subscribe(function(e,args) {
			absGrid.invalidateRows(args.rows);
			absGrid.render();
		});
            
		// initialize the model after all the events have been hooked up
		dataView2.beginUpdate();
		dataView2.setItems(data1);
		dataView2.endUpdate();
	}

	jQuery(function(){
		
		setupGrid1([]);
		jQuery("#routeId").focus();
		 $("select#subscriptionTypeId").change();
		if(screenFlag !="DSCorrection"){
			setupGrid2([]);
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
	
	});		
	
		
	function updateProductInfo() {
		for(var i=0;i<data.length;i++){
			var prod = data[i]["cProductId"];
			var qty = parseFloat(data[i]["cQuantity"]);
			var price = parseFloat(priceTags[prod]);
			if(isNaN(price)){
				price = 0;
			}
			if(isNaN(qty)){
				qty = 0;
			}
			data[i]["unitPrice"] = price;
			var amount = Math.round(qty*price);
			if(isNaN(amount)){
				amount = 0;
			}
			if(screenFlag != 'DSCorrection'){
				//var crateQty = parseFloat(qtyInPieces[prod]);
				//data[i]["LtrKgs"] = parseFloat(productQtyInc[prod])*qty*crateQty;
				//var supType = prodIndentQtyCat[prod];
				//data[i]["uomId"] = supType.charAt(0);
				//data[i]["amount"] = Math.round((qty*price) * 100)/100;
			}
			//else{
				data[i]["amount"] = amount; //onload update amount
			//}
			_grid.updateRow(i);
		}
		var totalAmount = 0;
		for (i = 0; i < data.length; i++) {
			totalAmount += data[i]["amount"];
		}
		var amt = parseFloat(Math.round((totalAmount) * 100) / 100);
		if(amt > 0 ){
			var dispText = "<b> [Invoice Amt: Rs " +  amt + "]</b>";
		}
		else{
			var dispText = "<b> [Invoice Amt: Rs 0 ]</b>";
		}
		jQuery("#totalAmount").html(dispText);
	}
	
	 function updateGrid1(ajaxJson) {
             data = ajaxJson;
             _grid.setData(data);
             _grid.invalidate();
             _grid.render();
              var pluginOptions = {
      		 enableForCells: true,
		      enableForHeaderCells: false,
		      maxToolTipLength: null
		    };
             plugin = new Slick.AutoTooltips(pluginOptions);
			_grid.registerPlugin(plugin);
           if(data.length >0){
			$(_grid.getCellNode(0,1)).click();
			}else{
				$(_grid.getCellNode(0,0)).click();
			}
			updateProductInfo();
    }
    
    
    function updateGrid2(ajaxJson) {
             data1 = ajaxJson;
             absGrid.setData(data1);
             absGrid.invalidate();
             absGrid.render();
           
    }
     
	var boothsList =  ${StringUtil.wrapString(boothsJSON)}
	var routesList =  ${StringUtil.wrapString(routesJSON)}
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N') && screenFlag?exists && screenFlag == 'indent'>
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var supplyRouteList =  ${StringUtil.wrapString(supplyRouteItemsJSON)}
			function setRouteDropDown(selection){	
				//routesList = routesList;
				
				routesList = supplyRouteList[selection.value];
				
				if(selection.value =="" || typeof selection.value == "undefined"){
					routesList =  ${StringUtil.wrapString(routesJSON)}
				}				
			}	
			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value];
				
				if(selection.value =="" || typeof selection.value == "undefined"){
					boothsList =  ${StringUtil.wrapString(boothsJSON)}
				}				
			}
			<#if subscriptionTypeId?exists && subscriptionTypeId?has_content> 
				routesList =  supplyRouteList["${subscriptionTypeId}"]; 
			</#if>		
			<#if routeId?exists && routeId?has_content> 
				 boothsList = routeBoothsData["${routeId}"];
			</#if>	
						
	</#if>
</script>			