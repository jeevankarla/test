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
.reallyHidden { display: none }

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




<script type="text/javascript">
	var emplIdLabelMap = ${StringUtil.wrapString(emplIdLabelJSON)!'{}'};
	var emplLabelIdMap = ${StringUtil.wrapString(emplLabelIdJSON)!'{}'};
	//var periodLabelIdMap = ${StringUtil.wrapString(periodLabelIdJSON)!'{}'};
	
	var availableTags = ${StringUtil.wrapString(employeesJSON)!'[]'};
	var data = ${StringUtil.wrapString(employeeArrearDaysJSON)!'[]'};
	var employeeId;
	//var dropDownOption;
	employeeId="${parameters.partyId?if_exists}";
	var data2 = ${StringUtil.wrapString(periodJSON)!'[]'};
	$(document).ready(function(){
		$( "#basicSalDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#basicSalDate" ).datepicker(selectedDate);
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
			
});
	
	
	/*function prepareApplicableOptions(){
		if(data2){
			dropDownOption = " ";
			for (i = 0; i < data2.length; i++) {
    			var period = data2[i]["label"];
    			dropDownOption += ","+period;
    		}
		}
	}*/
	
	function setupGrid1() {
		var grid;		
				var columns = [
					{id:"EmployeeId", name:"Employee ID", field:"employeeId", width:250, minWidth:220, cssClass:"cell-title", editor: AutoCompleteEditor,availableTags:availableTags,regexMatcher:"contains", sortable:false, toolTip:""},
					{id:"noOfArrearDays", name:"No Of Arrear Days", field:"noOfArrearDays", width:140, minWidth:100, cssClass:"cell-title", sortable:false, editor:FloatCellEditor},
					{id:"basicSalDate", name:"Arrears Date", field:"basicSalDate", width:150, minWidth:100, cssClass:"cell-title", sortable:true,editor:DateCellEditor},
					{id:"actualBasic", name:"Actual Basic", field:"actualBasic", width:150, minWidth:100, cssClass:"cell-title", sortable:true}
				];
				columns.push({id:"button", name:"Save", field:"button", width:60, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) {
        						return '<a href="#" class="button" onclick="editClickHandler('+row+')">Save</a>'; 
        	 			}
 					   }); 
		    
			
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
		var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		

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
    		if (grid.getActiveCell()) {
		        var cell = grid.getActiveCell().cell;
		        if(cell == 0){
		        }
		    }
      		var item = args.item;   
      		var productLabel = item['EmployeeId']; 
      		item['id'] = emplIdLabelMap[productLabel];     		 		
      		grid.invalidateRow(data.length);
      		data.push(item);
      		grid.updateRowCount();
      		grid.render();
    	});
    	
        grid.onCellChange.subscribe(function(e,args) {
		  	if (args.cell == 2) {
		  		var basicSalDate = (data[args.row]["basicSalDate"]);
		  		var employeeId = (data[args.row]["employeeId"]);
		  		partyId = emplLabelIdMap[employeeId];
		  		var basicPay;
		  		
		  		$.ajax({
					type: "POST",
		          	url: 'getBasicOfSelectedMonth',
		          	data: {basicSalDate : basicSalDate, employeeId : partyId},
		          	dataType: 'json',
		     		
			   		success: function(result) {
		            	if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
		           			alert(result["_ERROR_MESSAGE_"]);
		            	}else{
							basicPay = result["basicPay"];
					  		data[args.row]["actualBasic"] = basicPay;
					  		grid.updateRow(args.row);
		               	}
		           	}
				}); 
		  		
		  		//jQuery("#excessAmount").html(amount);
		  	}
	  	});
        
        
		
		grid.onActiveCellChanged.subscribe(function(e,args) {
        	if (args.cell == 0 && data[args.row] != null) {
				var prod = data[args.row]["EmployeeId"];
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
		//prepareApplicableOptions();
		setupGrid1();
				
	});
	
	function editClickHandler(row) {
		updatePayrollAttendanceArrearsInternal('EmplArrearDays', '<@ofbizUrl>updatePayrollAttendanceArrears</@ofbizUrl>', row);
	}
	
	function updatePayrollAttendanceArrearsInternal(formName, action, row) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}		
		var formId = "#" + formName;	
		var changeItem = data[row];	
		
		var rowCount = 0;
		for (key in changeItem){
			 var dataString1;
			var customTimePeriodId = "${parameters.customTimePeriodId?if_exists}";
			if(key == "employeeId"){
			
				partyId = emplLabelIdMap[changeItem[key]];
				dataString1 = "partyId="+partyId;
			}
			/*if(key == "periodId"){
				periodId = periodLabelIdMap[changeItem[key]];
				dataString1 = dataString1+"&customTimePeriodId="+periodId;
			}*/
			if(key == "basicSalDate"){
				dataString1 = dataString1+"&basicSalDate="+changeItem[key];
			}
			if(key == "noOfArrearDays"){
				dataString1 = dataString1+"&noOfArrearDays="+changeItem[key];
			}
			
			var value = changeItem[key];
	 		if (key != "id") {	 			 		
				var inputParam = jQuery("<input>").attr("type", "hidden").attr("name", key).val(value);	
				jQuery(formId).append(jQuery(inputParam));				
				 				
   			}
		}
		dataString1 = dataString1+"&customTimePeriodId="+customTimePeriodId;
		// lets make the ajaxform submit
			 $('div#updateEntryMsg')
    		  .html('<img src="/images/ajax-loader64.gif">'); 
		var dataString = $(formId).serialize();		
		$.ajax({
             type: "POST",
             url: action,
             data: dataString1,
             dataType: 'json',
             success: function(result) { 
             	//$(formId+' input').remove()            	
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){               	   
            	   $('div#updateEntryMsg').html();
            	   $('div#updateEntryMsg').removeClass("messageStr");            	 
            	   $('div#updateEntryMsg').addClass("errorMessage");
            	   $('div#updateEntryMsg').html('<label>'+ result["_ERROR_MESSAGE_"]+'</label>');
            	     
               }else{
               		$("div#updateEntryMsg").fadeIn();               	         	   
            	   $('div#updateEntryMsg').html(); 
            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
            	   $('div#updateEntryMsg').addClass("messageStr");
            	   $('div#updateEntryMsg').html('<label>Succesfully Updated.</label>'); 
            	   $('div#updateEntryMsg').delay(5000).fadeOut('slow'); 
            	   $($('.grid-canvas').children()[row]).css('background-color','#FAAC58'); 
            	 
               }
               
             } ,
             error: function() {
            	 	populateError(result["_ERROR_MESSAGE_"]);
            	 }
               });
	}//end of updatePayrollAttendanceInternal
</script>


<div id="wrapper" style="width: 95%; height:100%">
	<form method="post" action="<@ofbizUrl>updatePayrollAttendanceArrears</@ofbizUrl>" name="EmplArrearDays" id="EmplArrearDays"></form>
<div name ="updateEntryMsg" id="updateEntryMsg"></div>
<div id="div2" style="float: left;width: 100%;align:right; border: #F97103 solid 0.1em;">
    <div>    	
 		<div class="grid-header" style="width:100%">
			<font size="15" color="#22047F"><b>Edit Employee Arrear Days for the Month : ${fromDateMonth?if_exists}<b/></font>:
		</div>    
		<div id="myGrid1" style="width:100%;height:350px;"></div>
    </div>
</div>
<div align="left">
 	<table width="60%" border="0" cellspacing="0" cellpadding="0">  
    	<tr><td></td><td></td></tr>
    	<tr><td></td><td></td></tr>
    	<tr><td> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" style="padding:.3em" name="changeSave" id="changeSave" value="Submit" onclick="javascript:processInputEntry('updatePayrollAttendance','<@ofbizUrl>updatePayrollAttendance</@ofbizUrl>');" /></td>
 	</table>
</div>
</form>
</div>

