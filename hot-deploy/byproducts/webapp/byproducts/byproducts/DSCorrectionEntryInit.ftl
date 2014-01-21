
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
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	var dataView;
	var dataView2;
	
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var data = ${StringUtil.wrapString(dataJSON)!'[]'};
	var boothAutoJson = ${StringUtil.wrapString(facilityJSON)!'[]'};	
	var routeAutoJson = ${StringUtil.wrapString(routeJSON)!'[]'};
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
		
		var dataString = $("#indententryinit").serializeArray();
		$.each(dataString , function(i, fd) {
   			if(fd.name === "routeId"){
   				var route = jQuery("<input>").attr("type", "hidden").attr("name", "routeId").val(fd.value);
   				jQuery(formId).append(jQuery(route));
   			 }
		});
		
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	<#assign editClickHandlerAction =''>	
	<#if changeFlag?exists && changeFlag=='supplDeliverySchedule'>
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
      	return {valid: false, msg: "Invalid Product " + value};
      }
      return {valid: true, msg: null};
    }    

	var mainGrid;		
	function setupGrid1() {

		var grid;
		var columns = [
			{id:"product", name:"Product Code", field:"productId", validator: productValidator, width:200, minWidth:200, cssClass:"cell-title", availableTags: availableTags, formatter: productFormatter, editor: AutoCompleteEditor, sortable:false},
			{id:"Qty", name:"<#if screenFlag?exists && screenFlag == "DSCorrection">Correction(Qty)<#else>Quantity</#if>", field:"quantity", width:100, minWidth:100, cssClass:"cell-title", editor:FloatCellEditor, sortable:false},
			{id:"amount", name:"Amount", field:"amount", width:100, minWidth:100, cssClass:"readOnlyColumnClass", sortable:false, focusable :false}	
			<#-- {id:"LastQty", name:"<#if screenFlag?exists && screenFlag == "DSCorrection">Original Order(Qty)<#else>Last Qty</#if>", field:"lastQuantity", width:90, minWidth:90, cssClass:"readOnlyColumnClass", sortable:false , focusable :false ,toolTip:"<#if screenFlag?exists && screenFlag == "DSCorrection">Original Order(Qty)[${lastIndentDate?if_exists}]<#else>Last Indent(Qty)[${lastIndentDate?if_exists}]</#if>" }-->
		];
		
		var options = {
			editable: true,		
			forceFitColumns: false,
			<#if partyCode?exists>
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
				if(isNaN(price)){
					data[args.row]["amount"] = 0;
				}
				else{
					var roundedAmount = Math.round((qty*price) * 100) / 100
					data[args.row]["amount"] = (roundedAmount);
				}
				grid.updateRow(args.row);
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
			
		}); 
		updateProductTotalAmount();
		function updateProductTotalAmount() {
			for(var i=0;i<data.length;i++){
				var qty = parseFloat(data[i]["quantity"]);
				var prod = data[i]["productId"];
				var price = parseFloat(priceTags[prod]);
				if(isNaN(price) || isNaN(qty)){
					data[i]["amount"] = 0;
				}
				else{
					data[i]["amount"] = Math.round((qty*price) * 100)/100;
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
			$("#boothId").focus();      
		}  		
	});
	
	
// to show special related fields in form			
	function showSoInputfields(value){	
	
		jQuery("#sOFieldsDiv").hide();
		jQuery("#replacementDiv").hide();
		
		$("#boothId").autocomplete({ disabled: true });		
		
		var $inputs = $('#sOFieldsDiv :input');
		var $reDivInputs = $('#replacementDiv :input');
		$inputs.each(function() {
        	$(this).removeClass("required");
    	});	
    	$reDivInputs.each(function() {
	        $(this).removeClass("required");
	    });				
		if(value == "SPECIAL_ORDER_BYPROD" || value == "GIFT_BYPROD" || value == "REPLACEMENT_BYPROD"){
			
			if(value == "REPLACEMENT_BYPROD"){
				jQuery("#replacementDiv").show();
				
				$reDivInputs.each(function() {
	        		$(this).addClass("required");
	    		});	
			}
						
			$inputs.each(function() {
	        	$(this).addClass("required");
	    	});	
	    	jQuery('[name = address2]').removeClass("required");
				
		     $(function() {
				$( "#indententryinit" ).validate();
			});	
			$("#boothId").autocomplete({ disabled: false });	
			jQuery("#sOFieldsDiv").show();			
			$("#boothId").autocomplete({					
      				source: boothAutoJson
   			 });
			$("#routeId").autocomplete({					
      				source: routeAutoJson
   			 });
   			 
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
			$('#pinNumber').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
			});		  		
   		// lets get address and phone number for SO Depo
   		 $('#boothId').keyup(function (e) {	
	    			getSoFacilityDetails();  			
		});   			 
			
		}		
		
	}
	
	$(document).ready(function(){
		jQuery("#sOFieldsDiv").hide();
		var $inputs = $('#sOFieldsDiv :input');
		$inputs.each(function() {
        	$(this).removeClass("required");
    	});	
		$('#boothId').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
		});
		
		showSoInputfields(jQuery('select[name=productSubscriptionTypeId] option:selected').val());
		if(jQuery('input[name=productSubscriptionTypeId]').val() == "SPECIAL_ORDER_BYPROD" ||  jQuery('input[name=productSubscriptionTypeId]').val() == "GIFT_BYPROD" || jQuery('input[name=productSubscriptionTypeId]').val() == "REPLACEMENT_BYPROD"){
			jQuery("#sOFieldsDiv").show();
		}
		if( jQuery('input[name=productSubscriptionTypeId]').val() == "REPLACEMENT_BYPROD" ){
			jQuery("#replacementDiv").show();
		}
	/*	$('#indententryinit').submit(function (){
			if(!$("#indententryinit").validate({messages:{
    		   firstName :"" , lastName:"" , Address1:"" ,
    		   address2:"" , pinNumber:"" , routeId:""
    	   }}).form()) return false;		
			
		}); */
		
			
	});	
	 
	
	function getSoFacilityDetails(){
		jQuery("#name").val('');				
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
            	   populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	   var contactInfo = result["contactInfo"];            	             	   
            	   populateContactDetails(contactInfo);            	   
               }
               
             } 
               }); 			
	
	}
	
	function populateContactDetails(contactInfo){		
		jQuery("#name").val(contactInfo["name"]);		
		//jQuery("#routeId").val(contactInfo["byProdRouteId"]);
		jQuery("#address1").val(contactInfo["address1"]);		
		jQuery("#address2").val(contactInfo["address2"]);		
		jQuery("#contactNumber").val(contactInfo["contactNumber"]); 
		jQuery("#pinNumber").val(contactInfo["postalCode"]);
	}
	
</script>			