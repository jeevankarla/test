
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/steps/jqueryVST.steps.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datetimepicker/jquery-ui-timepicker-addon-0.9.3.js</@ofbizContentUrl>"></script>
<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-effort-driven {
		text-align: center ;
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
	.cell-title {
      font-weight: bold;
    }
    .slick-headerrow-column {
      background: #87ceeb;
      text-overflow: clip;
      -moz-box-sizing: border-box;
      box-sizing: border-box;
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
   
   function processPrePackEntryInternal(formName, action) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var formId = "#" + formName;
		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));
		
		for (var rowCount=0; rowCount < data.length; ++rowCount)
		{ 
			var productId = data[rowCount]["productId"];
			var prodId="";
			if(typeof(productId)!= "undefined"){ 	  
			var prodId = productId.toUpperCase();
			}
			var noOfCrates=0;
			var noOfPackets=0;
			var leakageQuantity=0;
			var startTime="";
			var endTime="";
			var remarks1="";
			var remarks2="";
			noOfCrates = parseFloat(data[rowCount]["noOfCrates"]);
			noOfPackets = parseFloat(data[rowCount]["noOfPackets"]);
			leakageQuantity = parseFloat(data[rowCount]["leakageQuantity"]);
			startTime = data[rowCount]["startTime"]; 
			endTime = data[rowCount]["endTime"];
			remarks1 = data[rowCount]["remarks1"];
			remarks2 = data[rowCount]["remarks2"];
	 		if(isNaN(prodId)){	 	
	 		   	alert("Please enter Product..!"); 
   			 	//window.location.reload(true);
   				 return false;
   			}else{
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "productId_o_" + rowCount).val(prodId);
				var noOfCrates = jQuery("<input>").attr("type", "hidden").attr("name", "noOfCrates_o_" + rowCount).val(noOfCrates);
				var noOfPackets = jQuery("<input>").attr("type", "hidden").attr("name", "noOfPackets_o_" + rowCount).val(noOfPackets);
				var startTime = jQuery("<input>").attr("type", "hidden").attr("name", "startTime_o_" + rowCount).val(startTime);
				var endTime = jQuery("<input>").attr("type", "hidden").attr("name", "endTime_o_" + rowCount).val(endTime);
				var remarks1 = jQuery("<input>").attr("type", "hidden").attr("name", "remarks1_o_" + rowCount).val(remarks1);
				var remarks2 = jQuery("<input>").attr("type", "hidden").attr("name", "remarks2_o_" + rowCount).val(remarks2);
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(noOfCrates));
				jQuery(formId).append(jQuery(noOfPackets));
				jQuery(formId).append(jQuery(startTime));
				jQuery(formId).append(jQuery(endTime));
				jQuery(formId).append(jQuery(remarks1));
				jQuery(formId).append(jQuery(remarks2));
				
   			}
		}
		jQuery(formId).attr("action", action);
		jQuery(formId).submit();
	}
	var enableSubmit = true;
	
	
	function processPrePackEntry(formName, action) {
		
			processPrePackEntryInternal(formName, action);
	}
 
  function requiredFieldValidator(value) {
    if (value == null || value == undefined || !value.length) {
      return {valid: false, msg: "This is a required field"};
    } else {
      return {valid: true, msg: null};
    }
  }
   function selectShift(){
     	var shiftId = $("#shiftId").val();
		if(shiftId == null || shiftId=="undefined" || shiftId==""){
         alert("Please select the Shift");
        }
    }     
  var grid;
  var data = [];
function setUpGrid(){
  
  var columns = [
    {id: "startTime", name: "Start Time", field: "startTime", width: 150, cssClass: "cell-title",editor : TimestampCellEditor, validator: requiredFieldValidator},
    {id: "endTime", name: "End Time", cssClass: "cell-title",field: "endTime",editor : TimestampCellEditor, width: 150},
    {id: "productId", name: "Product", field: "productId",width:250,editor: AutoCompleteEditor},
    {id: "noOfCrates", name: "No Of Crates", field: "noOfCrates", width: 120,editor:FloatCellEditor},
    {id: "noOfPackets", name: "No Of Packets", field: "noOfPackets", width: 120,editor:FloatCellEditor},
    {id: "leakageQuantity", name: "Leakage Quantity", width: 120, cssClass: "cell-effort-driven", field: "leakageQuantity",editor:FloatCellEditor},
    {id: "remarks1", name: "Start Time Remarks", width: 170, cssClass: "cell-effort-driven", field: "remarks1",editor:LongTextCellEditor, sortable:false},
    {id: "remarks2", name: "End Time Remarks", width: 170, cssClass: "cell-effort-driven", field: "remarks2",editor:LongTextCellEditor, sortable:false}
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
	
		grid = new Slick.Grid("#myGrid", data, columns, options);
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
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
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
    	grid.onActiveCellChanged.subscribe(function(e,args) {
    	    if(args.cell==1){
    	      selectShift();
    	    }
    	    if(args.cell){
	    	     var productId = data[args.row]["productId"];
	    	     if(productId){
	    	      var hideSubmit = document.getElementById('submitDiv');
	        		hideSubmit.setAttribute('class', 'visible');
	    	     }else{
	    	     	var hideSubmit = document.getElementById('submitDiv');
	        		hideSubmit.setAttribute('class', 'hidden');
	    	     }
    	    }
    	});
   
    }

$(document).ready(function() {			
		  	setUpGrid();
	});
</script>			