
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
		
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}

	function processChangeIndentInternal(formName, action, row) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var formId = "#" + formName;
		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));	
									
		var changeItem = dataView.getItem(row);			
		var rowCount = 0;
		for (key in changeItem)
		{
			var qty = parseFloat(changeItem[key]);	
	 		if (key != "id" && key != "title" && key != "productSubscriptionTypeId" && key !="sequenceNum" && !isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(key);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);	
				var inputSeq = jQuery("<input>").attr("type", "hidden").attr("name", "sequenceNum_o_" + rowCount).val(changeItem.sequenceNum);							
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));   
				jQuery(formId).append(jQuery(inputSeq));    								 				
    			rowCount++; 				
   			}
		}			
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
	
	function editClickHandler(row) {
		processChangeIndentInternal('changeindent', '<@ofbizUrl>processChangeIndentMIS</@ofbizUrl>', row);
	}
	
	function processChangeIndent(formName, action) {
		processChangeIndentInternal(formName, action, dataView.getLength() - 1);
	}
	
	
	function appendParamsToProcessBulkCardSale(formName, action){	
	
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
				return false;		
			}
		var formId = "#" + formName;		
		var inputBoothId = jQuery("<input>").attr("type", "hidden").attr("name", "boothId").val(jQuery('input[name=originFacilityId]').val());
		jQuery(formId).append(jQuery(inputBoothId));
				
		var inputCounterNumber = jQuery("<input>").attr("type", "hidden").attr("name", "counterNumber").val(jQuery('input[name=counterNumber]').val());
		jQuery(formId).append(jQuery(inputCounterNumber));	
				
		var inputCustomTimePeriodId = jQuery("<input>").attr("type", "hidden").attr("name", "customTimePeriodId").val(jQuery('#customTimePeriodId').val());
		jQuery(formId).append(jQuery(inputCustomTimePeriodId));
		
		var inputCustomerName = jQuery("<input>").attr("type", "hidden").attr("name", "customerName").val(jQuery('input[name=customerName]').val());
		jQuery(formId).append(jQuery(inputCustomerName));
		
		var inputCustomerContactNumber = jQuery("<input>").attr("type", "hidden").attr("name", "customerContactNumber").val(jQuery('input[name=customerContactNumber]').val());
		jQuery(formId).append(jQuery(inputCustomerContactNumber));
		
		var inputPaymentTypeId = jQuery("<input>").attr("type", "hidden").attr("name", "paymentTypeId").val(jQuery('#paymentTypeId').val());
		jQuery(formId).append(jQuery(inputPaymentTypeId));	

		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));	
									
		var changeItem = dataView.getItem(dataView.getLength() - 1);			
		var rowCount = 0;
		for (key in changeItem)
		{
			var qty = parseFloat(changeItem[key]);				
	 		if (key != "id" && key != "title" && key != "New" && !isNaN(qty)) {
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "milkCardTypeId_o_" + rowCount).val(key);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);										
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));			 								 				
    			rowCount++; 				
   			}
		}
				
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	   
}
		
	function setupGrid1() {
		var grid;
		var data = ${StringUtil.wrapString(dataJSON)!'[]'};

		var columns = [
			{id:"title", name:"", field:"title", width:100, minWidth:100, cssClass:"cell-title", sortable:false},	
        <#if productList?exists> 
	        <#list productList as product>				
				{id:"${product.productId}", name:"${product.productName}", field:"${product.productId}", width:75, minWidth:75, editor:FloatCellEditor}<#if product_has_next>,</#if>
			</#list>
		</#if>
		<#if milkCardTypeList?exists> 
	        <#list milkCardTypeList as milkCardType>	
				{id:"${milkCardType.milkCardTypeId}", name:"${milkCardType.name}", field:"${milkCardType.milkCardTypeId}", width:80, minWidth:80, editor:FloatCellEditor}<#if milkCardType_has_next>,</#if>
			</#list>
		</#if>			
		];

		<#if productSubscriptionTypeId?exists && productSubscriptionTypeId == "SPECIAL_ORDER"> 
			columns.push({id:"button", name:"", field:"button", width:70, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) { 
			 				if (dataView.getItem(row).title != "New") {
        						return '<a href="#" class="button" onclick="editClickHandler('+row+')">Edit</a>'; 
        					}
        					else {
        					return '';
        					}
        	 			}
 					   });
		</#if>
		
		var options = {
			editable: true,		
			forceFitColumns: false,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid1", dataView, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
		grid.onBeforeEditCell.subscribe(function(e, args) { 
			var currItem = dataView.getItem(args.row);			
		
			if (currItem.title == "Quota") return false;
			if (currItem.title == "Dispatched") return false; 
		});
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

		grid.onKeyDown.subscribe(function(e) {
        	if (e.which == $.ui.keyCode.ENTER) {
				jQuery("#changeSave").click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            else {
            	return false;
            }
        });
        
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
		dataView.endUpdate();

	}
	
	function setupGrid2() {
		var grid;
		var data = [
			<#if lastChangeSubProdMap?exists>	
				{"id":"1", "boothId":"${lastChangeSubProdMap.boothId}","supplyType":"${lastChangeSubProdMap.supplyType}" 					
				<#if milkCardTypeList?exists>
					<#list milkCardTypeList as milkCardType>				
					, "${milkCardType.milkCardTypeId}":"${lastChangeSubProdMap[StringUtil.wrapString(milkCardType.milkCardTypeId)]}"
					</#list>
				</#if>
				<#if productList?exists>
					<#list productList as product>				
					, "${product.productId}":"${lastChangeSubProdMap[product.productId]}"
					</#list> 
				</#if>				
				}				
			</#if>
		];

		var columns = [
			{id:"boothId", name:"Booth (S. Type)", field:"boothId", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
        	<#if milkCardTypeList?exists>
				<#list milkCardTypeList as milkCardType>				
					{id:"${milkCardType.milkCardTypeId}", name:"${milkCardType.name}", field:"${milkCardType.milkCardTypeId}", width:80, minWidth:80, editor:FloatCellEditor}<#if milkCardType_has_next>,</#if>
				</#list>
			</#if>
        	<#if productList?exists>
				<#list productList as product>				
					{id:"${product.productId}", name:"${product.productName}", field:"${product.productId}", width:75, minWidth:75, editor:FloatCellEditor}<#if product_has_next>,</#if>
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

<#if changeFlag?exists && changeFlag="CardSale">  
     	jQuery('#myGrid1 .slick-row:last-child > .slick-cell:nth-child(2)').click()
</#if>     			
    	var tabindex = 1;
    	jQuery('input,select').each(function() {
        	if (this.type != "hidden") {
            	var $input = $(this);
            	$input.attr("tabindex", tabindex);
            	tabindex++;
        	}
    	});
 <#if changeFlag?exists && changeFlag="CardSale">  
   		jQuery("#originFacilityId").focus();   	
<#else>
    	var rowCount = jQuery('#myGrid1 .slick-row').length;
		if (rowCount > 0) {			    
    		jQuery('#myGrid1 .slick-row:last-child > .slick-cell:nth-child(2)').click()
    	}
    	else {
  			jQuery("#boothId").focus();    
		}  
</#if>			
	});		
	
	<#if (showBoothAutoSuggest?has_content) && (showBoothAutoSuggest != 'N')>
			var routeBoothsData = ${StringUtil.wrapString(facilityItemsJSON)}
			var boothsList ;
			function setRouteBoothsDropDown(selection){	
				boothsList = routeBoothsData[selection.value]; 
			}	
			<#if routeId?exists && routeId?has_content> 
				boothsList = routeBoothsData["${routeId}"]; 
			</#if>
			$(function() {
				$('#boothId').keypress(function (e) { 
					$("#boothId").autocomplete({ source: boothsList });	
				});
			});
	</#if>	
	
</script>			