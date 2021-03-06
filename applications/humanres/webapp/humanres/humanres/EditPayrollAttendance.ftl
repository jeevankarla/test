
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
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellexternalcopymanager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	
  var undoRedoBuffer = {
      commandQueue : [],
      commandCtr : 0,

      queueAndExecuteCommand : function(editCommand) {
        this.commandQueue[this.commandCtr] = editCommand;
        this.commandCtr++;
        editCommand.execute();
      },

      undo : function() {
        if (this.commandCtr == 0)
          return;

        this.commandCtr--;
        var command = this.commandQueue[this.commandCtr];

        if (command && Slick.GlobalEditorLock.cancelCurrentEdit()) {
          command.undo();
        }
      },
      redo : function() {
        if (this.commandCtr >= this.commandQueue.length)
          return;
        var command = this.commandQueue[this.commandCtr];
        this.commandCtr++;
        if (command && Slick.GlobalEditorLock.cancelCurrentEdit()) {
          command.execute();
        }
      }
  }


  var pluginOptions = {
    clipboardCommandHandler: function(editCommand){ undoRedoBuffer.queueAndExecuteCommand.call(undoRedoBuffer,editCommand); },
    includeHeaderWhenCopying : false
  };
function comparer(a, b) {
  var x = a[sortcol], y = b[sortcol];
  return (x == y ? 0 : (x > y ? 1 : -1));
}
	var recentChnage;
	var type;
	type="${parameters.type?if_exists}";
	var data = ${StringUtil.wrapString(payrollJson)!'[]'};
	var benefitTypeId;
	benefitTypeId="${parameters.benefitTypeId?if_exists}";	
	function setUpItemList() {
			//recentChnage = recentChnage;
			var grid;		
			<#if payrollJson?has_content>
			 	data = ${StringUtil.wrapString(payrollJson)!'[]'};
			 <#else>
			 	 data =[];
			</#if>
			var columns = [		
					{id:"id", name:"Employee Id", field:"id", width:300, minWidth:150, cssClass:"cell-title", sortable:true},
					{id:"noOfCalenderDays", name:"Calender Days", field:"noOfCalenderDays", width:120, minWidth:100, cssClass:"cell-title", sortable:true},
					{id:"noOfPayableDays", name:"Payable Days", field:"noOfPayableDays", width:120, minWidth:100, cssClass:"cell-title", sortable:true},
					{id:"lossOfPayDays", name:"Loss Of Pay Days", field:"lossOfPayDays", width:120, minWidth:100, cssClass:"cell-title", sortable:true, editor:FloatCellEditor},
				];
	
			columns.push({id:"button", name:"SAVE", field:"button", width:60, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) { 
			 				if (dataView2.getItem(row).title != "New") {
        						return '<a href="#" class="button" onclick="editClickHandler('+row+')">Save</a>'; 
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
				asyncEditorLoading: false   
	            
			};
			
	        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
			dataView2 = new Slick.Data.DataView({
	        	groupItemMetadataProvider: groupItemMetadataProvider
	        });
			grid = new Slick.Grid("#itemGrid2", dataView2, columns, options);
	        grid.setSelectionModel(new Slick.CellSelectionModel());
			grid.onBeforeEditCell.subscribe(function(e, args) { 
				
			});
    grid.registerPlugin(new Slick.CellExternalCopyManager(pluginOptions));
			
  grid.onSort.subscribe(function (e, args) {
    sortdir = args.sortAsc ? 1 : -1;
    sortcol = args.sortCol.field;

    if ($.browser.msie && $.browser.version <= 8) {
      // using temporary Object.prototype.toString override
      // more limited and does lexicographic sort only by default, but can be much faste

      // use numeric sort of % and lexicographic for everything else
      dataView2.fastSort(args.sortAsc);
    } else {
      // using native sort with comparer
      // preferred method but can be very slow in IE with huge datasets
      dataView2.sort(comparer, args.sortAsc);
    }
  });			
			
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
			
	        grid.onKeyDown.subscribe(function(e){
	        	var cell = grid.getCellFromEvent(e);
				if (e.which == $.ui.keyCode.ENTER) {
					      editClickHandler(cell.row);
					      if(data.length>1){
					      	$(grid.getCellNode(cell.row +1, 2)).click();
					      	 grid.getEditController().commitCurrentEdit();
					      }else{
					      	$(grid.getCellNode(cell.row , 2)).click();
					      	 grid.getEditController().commitCurrentEdit();
					      }
				}
				
				if (e.which == 90 && (e.ctrlKey || e.metaKey)) {    // CTRL + (shift) + Z
      				if (e.shiftKey){
        				undoRedoBuffer.redo();
      				} else {
        			undoRedoBuffer.undo();
      			}
      			
    }
				
			});          
			
			 grid.onCellChange.subscribe(function(e,args) {        	
				var weeklyOff = parseInt(data[args.row]["noOfAttendedHoliDays"]);
				if(isNaN(weeklyOff)){
					weeklyOff=0;
				}
				var physicalPresence = parseFloat(data[args.row]["noOfAttendedDays"]);
				if(isNaN(physicalPresence)){
					physicalPresence=0;
				}
				var casualLeaves = parseFloat(data[args.row]["casualLeaveDays"]);
				if(isNaN(casualLeaves)){
					casualLeaves=0;
				}
				var earnedLeaves = parseFloat(data[args.row]["earnedLeaveDays"]);
				if(isNaN(earnedLeaves)){
					earnedLeaves=0;
				}
				var halfpayLeaves = parseFloat(data[args.row]["noOfHalfPayDays"]);
				if(isNaN(halfpayLeaves)){
					halfpayLeaves=0;
				}else{
					halfpayLeaves=halfpayLeaves/2;
				}
				var commutedLeaves = parseFloat(data[args.row]["commutedLeaveDays"]);
				if(isNaN(commutedLeaves)){
					commutedLeaves=0;
				}
				var disabilityLeaves = parseFloat(data[args.row]["disabilityLeaveDays"]);
				if(isNaN(disabilityLeaves)){
					disabilityLeaves=0;
				}
				var noOfPayableDays = parseFloat(data[args.row]["noOfPayableDays"]);
				if(isNaN(noOfPayableDays)){
					noOfPayableDays=0;
				}
				var days = physicalPresence+weeklyOff+casualLeaves+earnedLeaves+commutedLeaves+halfpayLeaves+disabilityLeaves;
				if(isNaN(days)){
					days = 0;
				}		
				var noOfCalenderDays = parseFloat(data[args.row]["noOfCalenderDays"]);
				var cell = grid.getCellFromEvent(e);
				if(days > noOfCalenderDays){
					alert("Payable Days exceeds CalenderDays");
					$(grid.getCellNode(cell.row +1, 2)).click();
					return false;
				}
				//data[args.row]["noOfPayableDays"] = days;				
				grid.updateRow(args.row);				
				jQuery("#noOfPayableDays").html(days);			
			});		
			
			
			grid.onCellChange.subscribe(function(e,args) {
			  	if (args.cell == 2) {
			  		var noOfCalenderDays = parseFloat(data[args.row]["noOfCalenderDays"]);
					if(isNaN(noOfCalenderDays)){
						noOfCalenderDays=0;
					}
			  		var noOfPayableDays = parseFloat(data[args.row]["noOfPayableDays"]);
					if(isNaN(noOfPayableDays)){
						noOfPayableDays=0;
					}
			  		if( noOfCalenderDays >= noOfPayableDays){
			  			var lossOfPayDays = noOfCalenderDays-noOfPayableDays;
			  			data[args.row]["lossOfPayDays"] = lossOfPayDays;
			  			data[args.row]["noOfPayableDays"] = noOfPayableDays;
			  			grid.updateRow(args.row);
			  		}
			  		//jQuery("#excessAmount").html(amount);
			  	}
			  	
			  	if (args.cell == 3) {
						  var lossOfPayDays = parseFloat(data[args.row]["lossOfPayDays"]);
						  if(isNaN(lossOfPayDays)){
						     lossOfPayDays=0;
						   }
						  var noOfCalenderDays = parseFloat(data[args.row]["noOfCalenderDays"]);
						  if(isNaN(noOfCalenderDays)){
							 noOfCalenderDays=0;
						   }
						  if( noOfCalenderDays >= lossOfPayDays){ 
						  var noOfPayableDays = noOfCalenderDays-lossOfPayDays;
						  data[args.row]["lossOfPayDays"] = lossOfPayDays;						  
						  data[args.row]["noOfPayableDays"] = noOfPayableDays;
						  grid.updateRow(args.row);
						  }	
			  	}
			  	
		  	});
			
			
			// initialize the model after all the events have been hooked up
			dataView2.beginUpdate();
			dataView2.setItems(data);
			dataView2.endUpdate();
			
	
	}
	$(document).ready(function() {			
		  	setUpItemList();
		  	setEmplsCount();
	});
	
	function processInputEntry(formName, action) {
	jQuery("#changeSave").attr( "disabled", "disabled");
	processInputEntryInternal(formName, action);
}
function processInputEntryInternal(formName, action) {
	if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
		return false;		
	}
	var formId = "#" + formName;
	var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
	jQuery(formId).append(jQuery(inputRowSubmit));	
	
	for (var rowCount=0; rowCount < data.length; ++rowCount){
		var partyId = (data[rowCount]["partyId"]);
		var deptId = (data[rowCount]["deptId"]);
		var timePeriodId = (data[rowCount]["timePeriodId"]);
		var customTimePeriodId = (data[rowCount]["customTimePeriodId"]);
		var noOfCalenderDays = parseFloat(data[rowCount]["noOfCalenderDays"]);
		var lossOfPayDays = parseFloat(data[rowCount]["lossOfPayDays"]);
		var noOfPayableDays = parseFloat(data[rowCount]["noOfPayableDays"]);
		var noOfAttendedHoliDays = parseFloat(data[rowCount]["noOfAttendedHoliDays"]);
		
		var partyId = jQuery("<input>").attr("type", "hidden").attr("name", "partyId_o_" + rowCount).val(partyId);
		var deptId = jQuery("<input>").attr("type", "hidden").attr("name", "deptId_o_" + rowCount).val(deptId);
		var timePeriodId = jQuery("<input>").attr("type", "hidden").attr("name", "timePeriodId_o_" + rowCount).val(timePeriodId);
		var customTimePeriodId = jQuery("<input>").attr("type", "hidden").attr("name", "customTimePeriodId_o_" + rowCount).val(customTimePeriodId);
		var noOfCalenderDays = jQuery("<input>").attr("type", "hidden").attr("name", "noOfCalenderDays_o_" + rowCount).val(noOfCalenderDays);
		var lossOfPayDays = jQuery("<input>").attr("type", "hidden").attr("name", "lossOfPayDays_o_" + rowCount).val(lossOfPayDays);
		var noOfPayableDays = jQuery("<input>").attr("type", "hidden").attr("name", "noOfPayableDays_o_" + rowCount).val(noOfPayableDays);
		var noOfAttendedHoliDays = jQuery("<input>").attr("type", "hidden").attr("name", "noOfAttendedHoliDays_o_" + rowCount).val(noOfAttendedHoliDays);
		jQuery(formId).append(jQuery(partyId));
		jQuery(formId).append(jQuery(deptId));
		jQuery(formId).append(jQuery(timePeriodId));
		jQuery(formId).append(jQuery(customTimePeriodId));
		jQuery(formId).append(jQuery(noOfCalenderDays));
		jQuery(formId).append(jQuery(lossOfPayDays));
		jQuery(formId).append(jQuery(noOfPayableDays));
		jQuery(formId).append(jQuery(noOfAttendedHoliDays));

	}
		
		// lets make the ajaxform submit
		$('div#updateEntryMsg')
    		  .html('<img src="/images/ajax-loader64.gif">');
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
	            	    $('div#updateEntryMsg').html('<label>'+ result["_ERROR_MESSAGE_"]+'</label>');
	            	     
	               }else{
	               		$("div#updateEntryMsg").fadeIn();               	         	   
	            	    $('div#updateEntryMsg').html(); 
	            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
	            	    $('div#updateEntryMsg').addClass("messageStr");
	            	    $('div#updateEntryMsg').html('<label><h3>succesfully updated.</h3></label>'); 
	            	    $('div#updateEntryMsg').delay(10000).fadeOut('slow');  
	            	    $($('.grid-canvas').children()[row]).css('background-color','#FAAC58'); 
	            	    
	               }
               
               },
             error: function() {
            	 	//alert("record not updated");
            	 }
           });
			
	}


function editClickHandler(row) {
		updatePayrollAttendanceInternal('updatePayrollAttendance', '<@ofbizUrl>EditPayrollAttendance</@ofbizUrl>', row);
		getEmplDetails(row);
	}
function updatePayrollAttendanceInternal(formName, action, row) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}		
		var formId = "#" + formName;									
		var changeItem = dataView2.getItem(row);			
		var rowCount = 0;
		for (key in changeItem){			
			var value = changeItem[key];
	 		if (key != "id") {	 			 		
				var inputParam = jQuery("<input>").attr("type", "hidden").attr("name", key).val(value);	
				jQuery(formId).append(jQuery(inputParam));				
				 				
   			}
		}
		// lets make the ajaxform submit
			 $('div#updateEntryMsg')
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
	function setEmplsCount(){
			var  dataEmpValues;
			<#if emplsCountJson?has_content>
			 	dataEmpValues = ${StringUtil.wrapString(emplsCountJson)};
			</#if>
			totalEmpls=dataEmpValues['totalEmpls'];
			enteredEmpls=dataEmpValues['enteredEmpls'];
			remainingEmpls=dataEmpValues['remainingEmpls'];
			jQuery("#totalEmpls").html(totalEmpls);
        	jQuery("#enteredEmpls").html(enteredEmpls);
        	jQuery("#remainingEmpls").html(remainingEmpls);
	}
 function getEmplDetails(row){
 	var changeItem = dataView2.getItem(row);
 		var customTimePeriodId=changeItem["customTimePeriodId"];
 		var noOfAttendedDays=changeItem["noOfAttendedDays"];
 		var deptId=changeItem["deptId"];
 		var partyId=changeItem["partyId"];
 		var timePeriodId=changeItem["timePeriodId"];
 		var data= "partyId="+partyId+"&customTimePeriodId="+customTimePeriodId+"&deptId="+deptId+"&noOfAttendedDays="+noOfAttendedDays+"&timePeriodId="+timePeriodId;
 		
	$.ajax({
        type: "POST",
        url: "getEmplsCount",
        data: data,
        dataType: 'json',
        success: function(result) {
        totalEmpls=result["totalEmpls"];
        enteredEmpls=result["enteredEmpls"];
        remainingEmpls=result["remainingEmpls"];
        jQuery("#totalEmpls").html(totalEmpls);
        jQuery("#enteredEmpls").html(enteredEmpls);
        jQuery("#remainingEmpls").html(remainingEmpls);
       	 },
       error: function() {
       	 	alert(result["_ERROR_MESSAGE_"]);
       	 }
	});
 
 }
</script>
	
<div id="wrapper" style="width: 95%; height:100%">
	<form method="post" action="<@ofbizUrl>updatePayrollAttendance</@ofbizUrl>" name="updatePayrollAttendance" id="updatePayrollAttendance"></form>
<div name ="updateEntryMsg" id="updateEntryMsg"></div>
<div id="div2" style="float: left;width: 100%;align:right; border: #F97103 solid 0.1em;">
    <div>    	
 		<div class="grid-header" style="width:100%">
			<font size="15" color="#22047F"><b>Employee Payroll Attendance<b/></font>:<font size="15" color="red"><b><#if timePeriodStart?has_content>[${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart?if_exists, "MMMMM-yyyy"))}]</#if></b></font>  <font size="15" color="#22047F"><b>Total Employees :<b/></font><font size="15" color="red"><b><span id="totalEmpls"></span></b></font><font size="15" color="#22047F"><b>Completed Employees :<b/></font><font size="15" color="red"><b><span id="enteredEmpls"></span></b></font><font size="15" color="#22047F"><b>Remaining Employees :<b/></font><font size="15" color="red"><b><span id="remainingEmpls"></span></b></font>  
		</div>    
		<div id="itemGrid2" style="width:100%;height:350px;"></div>
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
