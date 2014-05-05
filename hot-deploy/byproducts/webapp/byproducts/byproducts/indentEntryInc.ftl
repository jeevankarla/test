
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

screenFlag = '${screenFlag}';
function setDateRange(){
<#if screenFlag?exists && screenFlag == 'DSCorrection'>
         var startDate=new Date("01 May, 2014");
	    	$("#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: false,
			numberOfMonths: 1,
			minDate:startDate,
			maxDate:1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
		<#else>
		 $( "#effectiveDate" ).datepicker({
			dateFormat:'d MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			minDate: 0,
			maxDate:1,
			onSelect: function( selectedDate ) {
				$( "#effectiveDate" ).datepicker("option", selectedDate );
			}
	  });
	$('#ui-datepicker-div').css('clip', 'auto');
</#if>
}
function cleanUpGrid(value){
	$('[name=boothId]').val(value);
	$('[name=tempRouteId]').val('');
	$('[name=routeId]').val('');
    $('span#boothTooltip').html('<label></label>');
	$('span#routeTooltip').html('<label></label>');
	$('span#boothDepositTip').html('<label></label>');
	//$('span#routeCrateTotal').html('<label></label>');
	var emptyJson= [];
	$('div#tempRouteDiv').css("display","none");
	updateGrid1(emptyJson);
	if(screenFlag != "DSCorrection"){
		setupGrid2(emptyJson);
	}
	
}

function updateGrid(){
 	
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
						"screenFlag":screenFlag,
						"routeId" : $('[name=routeId]').val()
						 <#if screenFlag?exists && screenFlag == 'DSCorrection'>,
						     "isNoShipment" : $('[name=isNoShipment]').is(':checked')
						 </#if>							
					};
	 $('div#changeIndentEntry_message').removeClass("errorMessage");
     $('div#changeIndentEntry_message').html('');
	 $('div#changeIndentEntry_spinner').show();
	 			

		$.ajax({
				 type: "POST",
	             url: action,
	             data: dataJson,
	             dataType: 'json',
	            
				success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
					    $('div#changeIndentEntry_spinner').hide();
						 $("div#changeIndentEntry_message").fadeIn();
						msg = result["_ERROR_MESSAGE_"];
						if(result["_ERROR_MESSAGE_LIST_"] =! undefined){
							msg =msg+result["_ERROR_MESSAGE_LIST_"] ;
						}
						  $('div#changeIndentEntry_message').html('<span style="color:red; font-size:10pt; font-stlye:bold">"'+msg+'"</span>');
                	   $('div#changeIndentEntry_message').delay(7000).fadeOut('slow');
						gridHideCall();
					}else{
					    $('div#changeIndentEntry_spinner').hide();
					    
						var changeIndentProductList = result["changeIndentProductList"];
						//var tripId = result["tripId"];
							prodIndentQtyCat = result["prodIndentQtyCat"];
							priceTags = result["productPrice"];
							qtyInPieces = result["qtyInPieces"];
						if(screenFlag != 'DSCorrection'){
							var categoryTot = result["categoryTotals"];
							var tempRouteId = result["tempRouteId"];
							permanentRouteId = result["routeId"];
							//var  tripNo = tripId.charAt(tripId.length-1);
							var routeCrateTotal = result["routeCrateTotal"];
						
							routeTotals = result["routeTotalsList"];
							if(routeCapacity != undefined){
								if(routeCapacity < routeCrateTotal){
									alert("Route capacity exceeded");
								}
							}
						}
						priceTags = result["productPrice"];
						prodIndentQtyCat = result["prodIndentQtyCat"];
						var boothName = result["boothName"];
						var routeId = result["routeId"];
						var routeName = result["routeName"];
						var securityDeposit = result["securityDeposit"];
						var routeCapacity = result["routeCapacity"];
						
						//to show Grid on success
						gridShowCall();
						updateGrid1(changeIndentProductList);
						if(screenFlag != "DSCorrection"){
							updateGrid2(routeTotals);	
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
						//$('span#tripName').html('<b>'+tripNo +'</b>');
						$('span#routeCrateTotal').html('<b>'+routeCrateTotal +'</b>');
						
					}								 
				},
				error: function (xhr, textStatus, thrownError){
					alert("record not found :: Error code:-  "+xhr.status);
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
	var permanentRouteId;
	var qtyInPieces;
	var prodIndentQtyCat;
	var routeIdLabelMap;
	var routeLabelIdMap;
	var routeTags;
	var routeTotals;
	<#if screenFlag?exists && screenFlag == 'indentAlt'>
		routeIdLabelMap = ${StringUtil.wrapString(routeIdLabelJSON)!'{}'};
		routeLabelIdMap = ${StringUtil.wrapString(routeLabelIdJSON)!'{}'};
		routeTags = ${StringUtil.wrapString(routeItemsJSON)!'[]'};
	</#if>
		
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
    
    function routeValidator(value,item) {
      var currProdCnt = 1;
	  for (var rowCount=0; rowCount < data.length; ++rowCount)
	  { 
	  	 if (data[rowCount]['seqRouteId'] != null && data[rowCount]['seqRouteId'] != undefined && value == data[rowCount]['seqRouteId']) {
			if (item['cProductName'] == data[rowCount]['cProductName']) {
				++currProdCnt;
			}	
		 }
	  }
	  
      if (currProdCnt > 1) {
        return {valid: false, msg: "Duplicate Product " + value};      				
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
			
			if ((data[row]['uomId']) == "CR") {
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
		if (indentCat == "CR") {
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
			<#if screenFlag?exists && screenFlag == 'indentAlt'>
				{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, cssClass:"cell-title", availableTags: availableTags, editor: AutoCompleteEditor, sortable:false ,toolTip:""},
			<#else>
				{id:"cProductName", name:"Product", field:"cProductName", width:180, minWidth:180, cssClass:"cell-title", availableTags: availableTags, editor: AutoCompleteEditor, validator: productValidator,sortable:false ,toolTip:""},
			</#if>
			
			{id:"cQuantity", name:"Qty(Pkt)", field:"cQuantity", width:70, minWidth:70, cssClass:"cell-title",editor:FloatCellEditor, sortable:false , formatter: quantityFormatter,  validator: quantityValidator},
			{id:"quantity", name:"Qty(Cr/Can)", field:"quantity", width:60, minWidth:60, cssClass:"cell-title",editor:FloatCellEditor, sortable:false, formatter: quantityFormatter},
			<#if screenFlag?exists && screenFlag != 'DSCorrection'>
				<#--{id:"supply", name:"C/P/B ", field:"uomId", width:35, minWidth:35, cssClass:"readOnlyColumnClass", sortable:false, focusable :false},
				{id:"LtrKgs", name:"Ltr/Kgs", field:"LtrKgs", width:65, minWidth:65, cssClass:"readOnlyColumnClass", sortable:false, focusable :false , align:"right"},-->
			</#if>
			<#if screenFlag?exists && screenFlag == 'indentAlt'>
				{id:"seqRouteId", name:"Route", field:"seqRouteId", width:65, minWidth:65, cssClass:"cell-title", availableTags: routeTags, validator: routeValidator, editor: AutoCompleteEditor, sortable:false, align:"right"},
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
			var cellNav = 2;
			<#if screenFlag?exists && screenFlag == 'indentAlt'>
				cellNav = 3;
			</#if>
			var cell = _grid.getCellFromEvent(e);		
			if(e.which == $.ui.keyCode.UP && cell.row == 0){
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();
			}
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == data.length && cell.cell == cellNav){
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(0, 2)).click();
				e.stopPropagation();
			}else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.ENTER) && cell.row == (data.length-1) && cell.cell == cellNav){
				_grid.getEditController().commitCurrentEdit();
				_grid.gotoCell(data.length, 0, true);
				$(_grid.getCellNode(data.length, 0)).edit();
				
				e.stopPropagation();
			}
			
			else if((e.which == $.ui.keyCode.DOWN || e.which == $.ui.keyCode.RIGHT) && cell 
				&& cell.row == data.length && cell.cell == cellNav){
  				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row, 0)).click();
				e.stopPropagation();
			
			}else if (e.which == $.ui.keyCode.RIGHT &&
				cell && (cell.cell == cellNav) && 
				cell.row != data.length) {
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row+1, 0)).click();
				e.stopPropagation();	
			}
			else if (e.which == $.ui.keyCode.LEFT &&
				cell && (cell.cell == 0) && 
				cell.row != data.length) {
				_grid.getEditController().commitCurrentEdit();	
				$(_grid.getCellNode(cell.row, cellNav)).click();
				e.stopPropagation();	
			}else if (e.which == $.ui.keyCode.ENTER) {
        	  /*	if (cell && cell.cell == 0) {
					_grid.getEditController().commitCurrentEdit();	
					if (cell.row == 0) {
						_grid.navigateRight();    
						_grid.navigateUp(); 
					} else {
        				$(_grid.getCellNode(cell.row - 1, cellNav)).click();
        			}
        			e.stopPropagation();	
        			return false;
        		}  */
        		_grid.getEditController().commitCurrentEdit();
				if(cell.cell == 1 || cell.cell == 2){
					jQuery("#changeSave").click();
				}
				
				
				/*if(cell.row == data.length){
					$(_grid.getCellNode(cell.row, cellNav)).click();
				}else{
					$(_grid.getCellNode(cell.row, cellNav)).click();
				} */
				
				//$(_grid.getCellNode(cell.row, cellNav)).click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }else if (e.keyCode == 27) {
             //here ESC to Save grid
        		if (cell && cell.cell == 0) {
        			$(_grid.getCellNode(cell.row - 1, cellNav)).click();
        			return false;
        		}  
        		_grid.getEditController().commitCurrentEdit();
				   
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
      		productLabelIdMap[productLabel]
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
				var indentCat = prodIndentQtyCat[prod];
				var qtyInBulk = qtyInPieces[prod];
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qty)){
					qty = 0;
				}
				
				if(indentCat == null && indentCat == undefined){
					data[args.row]["quantity"] = "";
				}
				else{
					if(!isNaN(qty) && qtyInBulk != 0){
						data[args.row]["quantity"] = parseFloat(Math.round((qty/qtyInBulk)*100)/100);
					}
					
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
				if(screenFlag == 'indentAlt'){
					if(data[args.row]["seqRouteId"] == null || data[args.row]["seqRouteId"] == undefined){
						data[args.row]["seqRouteId"] = permanentRouteId;
						_grid.getEditController().commitCurrentEdit();
					}
					
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
			
			if (args.cell == 2) {
				var prod = data[args.row]["cProductId"];
				var qty = parseFloat(data[args.row]["quantity"]);
				var indentCat = prodIndentQtyCat[prod];
				var qtyInBulk = qtyInPieces[prod];
				var price = parseFloat(priceTags[prod]);
				var qtyPieces;
				if(indentCat == null && indentCat == undefined){
					data[args.row]["cQuantity"] = qty;
				}
				else{
					if(!isNaN(qty)){
						data[args.row]["cQuantity"] = parseFloat(Math.round((qty*qtyInBulk)*100)/100);
						qtyPieces = parseFloat(Math.round((qty*qtyInBulk)*100)/100);
					}
					
				}
				if(isNaN(price)){
					price = 0;
				}
				if(isNaN(qtyPieces)){
					qtyPieces = 0;
				}
				
				
				var roundedAmount = Math.round(qtyPieces*price);
				
				if(isNaN(roundedAmount)){
					roundedAmount = 0;
				}
				if(screenFlag == 'indentAlt'){
					if(data[args.row]["seqRouteId"] == null || data[args.row]["seqRouteId"] == undefined){
						data[args.row]["seqRouteId"] = permanentRouteId;
						_grid.getEditController().commitCurrentEdit();
					}
					
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
						//data[args.row]["uomId"] = supType.charAt(0);
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
			{id:"routeId", name:"Route", field:"routeId", width:60, minWidth:60, cssClass:"cell-title", sortable:false},
			{id:"retailerIndentCrate", name:"Retailer Indent", field:"retailerIndentCrate", width:60, minWidth:60, cssClass:"cell-title", sortable:false},
			{id:"routeLoad", name:"Load", field:"routeLoad", width:60, minWidth:60, cssClass:"cell-title", sortable:false},
			{id:"routeCapacity", name:"Capacity", field:"routeCapacity", width:60, minWidth:60, cssClass:"cell-title", sortable:false}
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
		if(screenFlag == 'indentAlt'){
			jQuery("#boothId").focus();
		}
		else{
			jQuery("#routeId").focus();
		}
		
		 //$("select#subscriptionTypeId").change();
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
			var indentCat = prodIndentQtyCat[prod];
			var qtyInBulk = qtyInPieces[prod];
			if(isNaN(price)){
				price = 0;
			}
			if(isNaN(qty)){
				qty = 0;
			}
			/*if(indentCat == "CRATE"){
				data[i]["uomId"] = "CR";
			}
			else if(indentCat == "CAN"){
				data[i]["uomId"] = "CN";
			}
			else{
				data[i]["uomId"] = "P";
			}*/
			
			if(indentCat == null && indentCat == undefined){
				data[i]["quantity"] = "";
			}
			else{
				if(!isNaN(qty) && qtyInBulk != 0){
					data[i]["quantity"] = parseFloat(Math.round((qty/qtyInBulk)*100)/100);
				}
			}
				
			data[i]["unitPrice"] = price;
			var amount = Math.round(qty*price);
			if(isNaN(amount)){
				amount = 0;
			}
			
			data[i]["amount"] = amount; //onload update amount
			if(screenFlag == 'indentAlt'){
				//data[i]["seqRouteId"] = data[i]["cProductId"];
			}
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
    function gridHideCall() {
           $('#myGrid1Hdr').hide();
           $('#myGrid1').hide();
           $('#GridSaveId').hide();
    }
     function gridShowCall() {
           $('#myGrid1Hdr').show();
           $('#myGrid1').show();
           $('#GridSaveId').show();
    }
     $('#routeId').keypress(function (e) {
			$("#subscriptionTypeId").trigger("onchange");
	 });
      
     
	var boothsList =  ${StringUtil.wrapString(boothsJSON)}
	var routesList =  ${StringUtil.wrapString(routesJSON)}
	var supplyRouteList =  ${StringUtil.wrapString(supplyRouteItemsJSON)}
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N') && screenFlag?exists && (screenFlag == 'indent' || screenFlag == 'DSCorrection')>
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			
			$("#subscriptionTypeId").trigger("onchange");
			function setRouteDropDown(selection){	
				setRouteDropDownByValue(selection.value);
				setSupplyDate(selection);
								
			}
			
				
				
			function setRouteBoothsDropDown(selection){
			  <#if screenFlag?exists && screenFlag != 'DSCorrection'>
					if(typeof selection.value == "undefined"){
					   selection.value = $("#routeId").val();
					}
					boothsList = routeBoothsData[selection.value];
					if(selection.value =="" || typeof selection.value == "undefined"){
						boothsList =  ${StringUtil.wrapString(boothsJSON)}
					}
			   </#if>
			}
			<#if subscriptionTypeId?exists && subscriptionTypeId?has_content> 
				routesList =  supplyRouteList["${subscriptionTypeId}"]; 
			</#if>		
			<#if routeId?exists && routeId?has_content> 
				 boothsList = routeBoothsData["${routeId}"];
			</#if>	
						
	</#if>
	function setRouteDropDownByValue(selectionValue){
	
			if(supplyRouteList != "" || supplyRouteList != "undefined"){
			
				routesList = supplyRouteList[selectionValue];
				if(selectionValue =="" || selectionValue == "undefined"){
					routesList =  ${StringUtil.wrapString(routesJSON)}
				}
				if(screenFlag == 'indentAlt'){
            	   $("#boothId").focus();
               }else{
               	$("#routeId").focus();
               }
								
			}
			gridHideCall();
	}
	function setSupplyDate(selection){
		var type = selection.value;
		var dateStr = $("#effectiveDate").val();
		var today = new Date();
		if(type == 'PM'){
			var date = new Date(dateStr);
			if(today >= date){
			  return false;
			}
			date.setDate(date.getDate()-1);
			var newDate = new Date(date);
			$("#effectiveDate").val($.datepicker.formatDate('dd MM, yy', newDate));
			
		}else if(type == 'AM'){
			var date = new Date(dateStr);
			if(today < date){
			  return false;
			}
			date.setDate(date.getDate()+1);
			var newDate = new Date(date);
			$("#effectiveDate").val($.datepicker.formatDate('dd MM, yy', newDate));
			//$("#effectiveDate").val($.datepicker.formatDate('dd MM, yy', date));
		}
	}
$(document).ready(function(){	
	$('div#changeIndentEntry_spinner').hide();
	});
	
</script>			