
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
	var total = 0;   

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
			var boothId = data[rowCount]["boothId"];
			var qty = parseFloat(data[rowCount]["quantity"]);
			var route = data[rowCount]["route"];
			var productSubscriptionId = data[rowCount]["productSubscriptionTypeId"];
	 		if (!isNaN(qty)) {	 		
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "boothId_o_" + rowCount).val(boothId);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);
				var inputRoute = jQuery("<input>").attr("type", "hidden").attr("name", "route_o_" + rowCount).val(route);
				var inputProdSubscription = jQuery("<input>").attr("type", "hidden").attr("name", "productSubscription_o_" + rowCount).val(productSubscriptionId);	
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));
				jQuery(formId).append(jQuery(inputRoute));
				jQuery(formId).append(jQuery(inputProdSubscription));   
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
	
	 
		
	function setupGrid1() {
		var grid;

		var columns = [
			{id:"title", name:"", field:"title", width:10, minWidth:10, cssClass:"cell-title", sortable:false},	
			{id:"boothId", name:"Party Code", field:"boothId", width:200, minWidth:200, cssClass:"cell-title", sortable:false, fieldIdx: 0},
			{id:"route", name:"Route", field:"route", width:90, minWidth:90, cssClass:"readOnlyColumnClass", sortable:false, fieldIdx: 1 ,focusable :false,},			
			{id:"LastQty", name:"Last Indent(Qty)", field:"lastQuantity", width:90, minWidth:90, cssClass:"readOnlyColumnClass", sortable:false, fieldIdx: 2 ,focusable :false ,toolTip : "Last Indent(Qty)[${lastIndentDate?if_exists}]"},
			{id:"Qty", name:"Today's Indent(Qty)", field:"quantity", width:200, minWidth:200, cssClass:"cell-title", editor:FloatCellEditor, sortable:false, fieldIdx: 3}
				
		];

	 <#--<#if productSubscriptionTypeId?exists && productSubscriptionTypeId == "SPECIAL_ORDER"> 
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
		</#if> -->
		
		var options = {
			editable: true,		
			forceFitColumns: false,
			
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25,           
        };
				
		grid = new Slick.Grid("#myGrid1", data, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());        
        grid.setActiveCell(0, 3);
        grid.onCellChange.subscribe(function(e, args){		 	  
		  total = 0;		  
		  for(var i = 0 ; i < data.length ; i++ ) {      
		      total = total + data[i].quantity;	        
		    }  
		     $("#totalQty").html("<b> Total:   "+ total +"</b>");     
		});
	
		grid.onBeforeEditCell.subscribe(function(e, args) { 
			var currItem = data[args.row];			
		
//			if (currItem.title == "Quota") return false;
//			if (currItem.title == "Dispatched") return false; 
		});
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid

		grid.onKeyDown.subscribe(function(e) {		
			var cell = grid.getCellFromEvent(e);			
			if (e.which == $.ui.keyCode.RIGHT &&
				cell && cell.cell == grid.getColumns().length - 1 && 
				cell.row != data.length) {
				grid.getEditController().commitCurrentEdit();				
				grid.setActiveCell(cell.row + 1, 0);
				e.stopPropagation();
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
      
      // do the total quqntity here
	for(var i = 0 ; i < data.length ; i++ ) {      
		      total = total + data[i].quantity;	        
		    }  
	$("#totalQty").html("<b> Total Quantity:"+ total +"</b>"); 
	$("#totalIndents").html("<b> Total Indents:"+ data.length +"</b>");   

	}
	
	function setupGrid2() {
		var grid;
		var data = [
			<#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>	
				{"id":"1", "productId":"${lastChangeSubProdMap.productId}" 					
				<#if facilityList?exists>
					<#list facilityList as fac>				
					 , "${fac.facilityId}":"${lastChangeSubProdMap[fac.facilityId]}"
					</#list>
				</#if>				
				}				
			</#if>
		];

		var columns = [
			{id:"productId", name:"Product Code", field:"productId", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
        	<#if facilityList?exists>
				<#list facilityList as fac>				
					{id:"${fac.facilityId}", name:"${fac.facilityId?if_exists}", field:"${fac.facilityId}", width:75, minWidth:75, editor:FloatCellEditor}<#if fac_has_next>,</#if>
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
	
	
// to show special related fields in form			
	function showSoInputfields(value){	
	
		jQuery("#sOFieldsDiv").hide();
		$("#boothId").autocomplete({ disabled: true });		
		
		var $inputs = $('#sOFieldsDiv :input');
		$inputs.each(function() {
        	$(this).removeClass("required");
    	});				
		if(value == "SPECIAL_ORDER"){
			
			$inputs.each(function() {
	        	$(this).addClass("required");
	    	});	
				
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
		if(jQuery('input[name=productSubscriptionTypeId]').val() == "SPECIAL_ORDER"){
			jQuery("#sOFieldsDiv").show();
		}
		
	/*	$('#indententryinit').submit(function (){
			if(!$("#indententryinit").validate({messages:{
    		   firstName :"" , lastName:"" , Address1:"" ,
    		   address2:"" , pinNumber:"" , routeId:""
    	   }}).form()) return false;		
			
		}); */
		
			
	});	
	 
	
	function getSoFacilityDetails(){	
		jQuery("#firstName").val('');
		jQuery("#lastName").val('');			
		jQuery("#routeId").val('');
		jQuery("#address1").val('');
		jQuery("#address2").val('');
		jQuery("#contactNumber").val('');					
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
            	   //alert(contactNumber[""]);            	   
            	   populateContactDetails(contactInfo);            	   
               }
               
             } 
               }); 			
	
	}
	
	function populateContactDetails(contactInfo){
		jQuery("#firstName").val(contactInfo["firstName"]);
		jQuery("#lastName").val(contactInfo["lastName"]);			
		jQuery("#routeId").val(contactInfo["byProdRouteId"]);
		jQuery("#address1").val(contactInfo["address1"]);
		jQuery("#address2").val(contactInfo["address2"]);
		jQuery("#contactNumber").val(contactInfo["contactNumber"]);
	}
	
</script>			