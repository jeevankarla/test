
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
	
	.tooltip { /* tooltip style */
    background-color: #ffffbb;
    border: 0.1em solid #999999;
    color: #000000;
    font-style: arial;
    font-size: 80%;
    font-weight: normal;
    margin: 0.4em;
    padding: 0.1em;
}
.tooltipWarning { /* tooltipWarning style */
    background-color: #ffffff;
    border: 0.1em solid #FF0000;
    color: #FF0000;
    font-style: arial;
    font-size: 80%;
    font-weight: bold;
    margin: 0.4em;
    padding: 0.1em;
}	

.messageStr {
    background:#e5f7e3;
    background-position:7px 7px;
    border:4px solid #c5e1c8;
    font-weight:700;
    color:#005e20;    
    text-transform:uppercase;
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

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<script type="application/javascript">

	var data3;
	function setUpOutLierList(outLierJson) {
			var grid;
			data3=outLierJson
			
			var columns = [
				{id:"shedId", name:"Shed", field:"shedName", width:50, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"unitCode", name:"unit Code", field:"unitCode", width:60, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"centerCode", name:"center Code", field:"centerCode", width:60, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"orderDate", name:"proc Date", field:"orderDate", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"supplyTypeEnumId", name:"Time", field:"purchaseTime", width:40, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"productName", name:"Milk", field:"productName", width:70, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"quantity", name:"quantity", field:"quantity", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"fat", name:"fat", field:"fat", width:40, minWidth:40, sortable:false , editor:FloatCellEditor},
				<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableLR?has_content)&& tenantConfigCondition.enableLR != 'N'>
					{id:"lactoReading", name:"lactoReading", field:"lactoReading", width:40, minWidth:40, sortable:false , editor:FloatCellEditor}
				<#else>
					{id:"snf", name:"snf", field:"snf", width:40, minWidth:40, sortable:false , editor:FloatCellEditor}
				</#if>,				
				{id:"sQuantity", name:"sQuantity", field:"sQuantity", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"sFat", name:"sFat", field:"sFat", width:40, minWidth:40, sortable:false , editor:FloatCellEditor},
				{id:"cQuantity", name:"cQuantity", field:"cQuantity", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"ptcQuantity", name:"ptcQuantity", field:"ptcQuantity", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"statusId", name:"status", field:"statusId", width:80, minWidth:100, sortable:false}								
			];
	
			columns.push({id:"button", name:"Approve", field:"button", width:70, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) { 
			 				if (dataView3.getItem(row)["statusId"] != "Approved") {
        						return '<a href="#" class="button" onclick="approveClickOutlierHandler('+row+')">Approve</a>'; 
        					}
        					else {
        					return '';
        					}
        	 			}
 					   });
 			columns.push({id:"button", name:"Edit", field:"button", width:70, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) { 
			 				if (dataView3.getItem(row)["statusId"] != "Approved") {
        						return '<a href="#" class="button" onclick="editClickOutlierHandler('+row+')">Edit</a>'; 
        					}
        					else {
        					return '';
        					}
        	 			}
 					   });		   
	
	
			var options = {
				editable: true,		
				forceFitColumns: false,
				enableCellNavigation: true,
				autoEdit: true,
				asyncEditorLoading: false,			
	            secondaryHeaderRowHeight: 25
			};
			
	        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
			dataView3 = new Slick.Data.DataView({
	        	groupItemMetadataProvider: groupItemMetadataProvider
	        });
			grid = new Slick.Grid("#itemGrid5", dataView3, columns, options);
	        grid.setSelectionModel(new Slick.CellSelectionModel());
			grid.onBeforeEditCell.subscribe(function(e, args) { 
				
			});
			var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
			
			// wire up model events to drive the grid
			dataView3.onRowCountChanged.subscribe(function(e,args) {
				grid.updateRowCount();
	            grid.render();
			});
			dataView3.onRowsChanged.subscribe(function(e,args) {
			    
				grid.invalidateRows(args.rows);
				grid.render();
			});
			
	        grid.onKeyDown.subscribe(function(e){
	        	var cell = grid.getCellFromEvent(e);
				if (e.which == $.ui.keyCode.ENTER) {
				     if(cell && cell.cell == 6){
				          $(grid.getCellNode((cell.row),7)).click();
				          grid.getEditController().commitCurrentEdit();
				     
				     }else{
					      editClickOutlierHandler(cell.row);
					      approveClickOutlierHandler(cell.row);
					      $(grid.getCellNode(cell.row +1, 6)).click();
					      grid.getEditController().commitCurrentEdit();
				     }
				     
				}
				
			});
	          
	         
			// initialize the model after all the events have been hooked up
			dataView3.beginUpdate();
			dataView3.setItems(data3);
			dataView3.endUpdate();	
	
	}	
	
	$(document).ready(function() {
		
		//populate Last change on load
		var outLierJason;
		<#if outLierItemsJson?has_content>
		 outLierJason = ${StringUtil.wrapString(outLierItemsJson)!'[]'}
		 setUpOutLierList(outLierJason);
		</#if>  
		  	
	});	
	
function editClickOutlierHandler(row) {
		outLierQtyEntries('OutLierQtyEntry', '<@ofbizUrl>updateProcurementEntryAjax</@ofbizUrl>', row);
	}	
function approveClickOutlierHandler(row) {
		outLierQtyEntries('OutLierQtyEntry', '<@ofbizUrl>approveValidationEntryAjax</@ofbizUrl>', row);
		data3[row]["statusId"]="Approved";
		setUpOutLierList(data3);
	}	


function outLierQtyEntries(formName, action, row) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		} 
		
		var formId = "#" + formName;
									
		var changeItem = dataView3.getItem(row);			
		var rowCount = 0;
		for (key in changeItem){			
			var value = changeItem[key];	
	 		if (key != "id") {	 			 		
				var inputParam = jQuery("<input>").attr("type", "hidden").attr("name", key).val(value);										
				jQuery(formId).append(jQuery(inputParam));				
				 				
   			}
		}
		// lets make the ajaxform submit
			 $('div#qtyOutLierMsg')
    		  .html('<img src="/images/ajax-loader64.gif">'); 
		var dataString = $(formId).serialize();		
		$.ajax({
             type: "POST",
             url: action,
             data: dataString,
             dataType: 'json',
             success: function(result) { 
             	$(formId+' input').remove()            	
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){               	   
            	  alert("Error Occured");
            	     
               }else{
               		alert("Successfully Updated")
               }
               
             } ,
             error: function() {
            	 	populateError(result["_ERROR_MESSAGE_"]);
            	 }
               });
		
	}//end of updateProcurementEntryInternal
	
	
    $(function() {
	 	 	 	
        $( "#accordion" ).accordion({ collapsible: true , active : true});
        $( "#accordion" ).accordion({ icons: { "header": "ui-icon-plus", "headerSelected": "ui-icon-minus" } }); 
        
        
              
   });

</script>
	
<div id="wrapper" style="width: 95%; height:100%">

 <form method="post" name="OutLierQtyEntry" id="OutLierQtyEntry"> 
 	
 </form>
</div>
 
