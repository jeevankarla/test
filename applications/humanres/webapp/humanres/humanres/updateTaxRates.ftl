
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
		
<#include "updateTaxRatesInc.ftl"/> 	
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
<script type="application/javascript">
    
    	
	var dataView;
	var dataView2;
	var grid;
	var productLabelIdMap = ${StringUtil.wrapString(productLabelIdJSON)!'{}'};
	var productIdLabelMap = ${StringUtil.wrapString(productIdLabelJSON)!'{}'};
	var availableTags = ${StringUtil.wrapString(productItemsJSON)!'[]'};
	var priceTags = ${StringUtil.wrapString(productCostJSON)!'[]'};
	var conversionData = ${StringUtil.wrapString(conversionJSON)!'{}'};
	var data = ${StringUtil.wrapString(dataJSON)!'[]'};
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	
	var mainGrid;		
	function setupGrid1() {
		<#if headItemsJson?has_content>
		 	data = ${StringUtil.wrapString(headItemsJson)!'[]'};
		 	<#else>
		 	 data =[];
		</#if>
		var columns = [
					{id:"totalIncomeFrom", name:"Total Income From", field:"totalIncomeFrom", width:150, minWidth:100, cssClass:"cell-title", sortable:true,editor:FloatCellEditor},
					{id:"totalIncomeTo", name:"Total Income To", field:"totalIncomeTo", width:150, minWidth:100, cssClass:"cell-title", sortable:true,editor:FloatCellEditor},		
					{id:"taxPercentage", name:"Percentage Rate of Tax", field:"taxPercentage", width:150, minWidth:100, cssClass:"cell-title", sortable:true,editor:FloatCellEditor},	
					{id:"refundAmount", name:"Minimum Slab Amount", field:"refundAmount", width:150, minWidth:100, cssClass:"cell-title", sortable:true,editor:FloatCellEditor}		
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
      		var productLabel = item['inputName']; 
      		item['inputTypeId'] = productLabelIdMap[productLabel];     		 		
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
        grid.onCellChange.subscribe(function(e,args) {
			if (args.cell == 0 || args.cell == 1) {
				var prod = data[args.row]["inputTypeId"];
				grid.updateRow(args.row);
			}
			
		}); 
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 1 && data[args.row] != null) {
				var prod = data[args.row]["inputTypeId"];
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
	
	jQuery(function(){
	    
		setupGrid1();
				
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
			$("#orderId").focus();      
		}  		
	});
	
	
// to show special related fields in form			
	
	$(document).ready(function(){
	 
		$('#suppInvoiceId').keypress(function (e) {
	  			if (e.which == $.ui.keyCode.ENTER) {
	    			$('#indententryinit').submit();
	    			return false;   
	  			}
		});
		     $(function() {
				$( "#indententryinit" ).validate();
			});	
			$("#orderId").autocomplete({ disabled: false });
			$("#suppInvoiceId").autocomplete({ disabled: false });	
		
	});	
	
	function updateGrid2(ajaxJson) {
             data = ajaxJson;
             grid2.setData(data);
             grid2.invalidate();
             grid2.render();
              var pluginOptions = {
      		 enableForCells: true,
		      enableForHeaderCells: false,
		      maxToolTipLength: null
		    };
             plugin = new Slick.AutoTooltips(pluginOptions);
			grid2.registerPlugin(plugin);
           if(data.length >0){
			$(grid2.getCellNode(0,1)).click();
			}else{
				$(grid2.getCellNode(0,0)).click();
			}
			updateProductInfo();
    }
    
    
	 function processIndentEntry(formName, action) {
		jQuery("#changeSave").attr( "disabled", "disabled");
		processIndentEntryInternal(formName, action);
		
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
			var totalIncomeFrom = parseFloat(data[rowCount]["totalIncomeFrom"]);
			var totalIncomeTo = parseFloat(data[rowCount]["totalIncomeTo"]);
			var taxPercentage = parseFloat(data[rowCount]["taxPercentage"]);
			var refundAmount = parseFloat(data[rowCount]["refundAmount"]);
				var totalIncomeFrom = jQuery("<input>").attr("type", "hidden").attr("name", "totalIncomeFrom_o_" + rowCount).val(totalIncomeFrom);
				var totalIncomeTo = jQuery("<input>").attr("type", "hidden").attr("name", "totalIncomeTo_o_" + rowCount).val(totalIncomeTo);
				var taxPercentage = jQuery("<input>").attr("type", "hidden").attr("name", "taxPercentage_o_" + rowCount).val(taxPercentage);
				var refundAmount = jQuery("<input>").attr("type", "hidden").attr("name", "refundAmount_o_" + rowCount).val(refundAmount);
				jQuery(formId).append(jQuery(totalIncomeFrom));
				jQuery(formId).append(jQuery(totalIncomeTo));
				jQuery(formId).append(jQuery(taxPercentage)); 
				jQuery(formId).append(jQuery(refundAmount));
		}
		var dataString = $(formId).serializeArray();	
		$.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) { 
             	//$(formId+' input').remove()            	
	               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){               	   
	            	    $('div#updateEntryMsg').html();
	            	    $('div#updateEntryMsg').removeClass("messageStr");            	 
	            	    $('div#updateEntryMsg').addClass("errorMessage");
	            	    $('div#updateEntryMsg').html('<label style="float: left;width: 51%;align:right; border: #F97103 solid 0.1em;">'+ result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]+'</label>');
	            	     
	               }else{
	               		var taxDetailsList = result["taxDetailsList"];
	               		updateGrid2(taxDetailsList);
	               		$("div#updateEntryMsg").fadeIn();               	         	   
	            	    $('div#updateEntryMsg').html(); 
	            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
	            	    $('div#updateEntryMsg').addClass("messageStr");
	            	    $('div#updateEntryMsg').html('<label><h2>succesfully updated.</h2></label>'); 
	            	    $('div#updateEntryMsg').delay(5000).fadeOut('slow');  
	               }
               
               },
             error: function() {
            	 	//alert("record not updated");
            	 }
           });
			
	}
</script>			
	
<div id="wrapper" style="width: 95%; height:100%">
  	<form method="post" action="<@ofbizUrl>updateTaxRatesDetails</@ofbizUrl>" name="updateTaxRatesDetails" id="updateTaxRatesDetails"></form>
</div>

<#assign customTimePeriodIdsList	= delegator.findByAnd("CustomTimePeriod", {"periodTypeId" : "FISCAL_YEAR"})>
 <#--  <div class="lefthalf">
<table border="0" cellspacing="10" cellpadding="0">  
	<tr>
		 <td width="40%"><b>Period Id :</b></td>
	     <td width="60%">
		     <select name="periodId" id="periodId">
	            <#list customTimePeriodIdsList as periodId>
	             <option value='${periodId.customTimePeriodId?if_exists}' >${periodId.periodName} : ${periodId.fromDate} - ${periodId.thruDate?if_exists}</option>
	            </#list>
	        </select>
	    </td>
	</tr>
	<tr>
		 <td width="40%"><b>Gender :</b></td>
	     <td><input type="radio" id="gender" name="gender" value="Female" >Female</input>
	     	<input type="radio" id="gender" name="gender" value="Male">Male</input>
		     	
	    </td>
	</tr>
    </table>
 </div> -->
 <div class="full">
 	<div class="lefthalf">
		<div id="div2" style="float: left;width: 100%;align:right; border: #F97103 solid 0.1em;">
		    <div>    	
		 		<div class="grid-header" style="width:100%">
					<label>Tax Rates Input Form</label>
				</div>    
				<div id="myGrid1" style="width:100%;height:200px;"></div>
			</div> 
		</div> 	
		<div name ="updateEntryMsg" id="updateEntryMsg" style="width:100%"> </div> 	
		<div align="center">
			 <table width="60%" border="0" cellspacing="0" cellpadding="0">  
			    <tr><td></td><td></td></tr>
			    <tr><td></td><td></td></tr>
			    <tr><td> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" style="padding:.3em" name="changeSave" id="changeSave" value="Submit" onclick="javascript:processIndentEntry('updateTaxRatesDetails','<@ofbizUrl>updateTaxRatesDetails</@ofbizUrl>');" /></td>
			 </table>
		</div>
	</div>
	<div class="righthalf">
		<div class="screenlet">
    		<div class="screenlet-body">
    			<div class="grid-header" style="width:100%">
					<font size="15" color="#22047F"><b>Tax Slabs <b/></font>:<font size="15" color="red"><b><#if parameters.age?has_content>[Age : ${parameters.age} 60] </#if> <#if parameters.gender?has_content><#if (parameters.gender) == "M">[Gender : Male] </#if><#if (parameters.gender) == "F">[Gender : Female] </#if></#if>  </b></font>  
				</div>
		 		
				<div id="myGrid2" style="width:100%;height:200px;"></div>
			  
			</div>
		</div>     
	</div>
</div>    
    
