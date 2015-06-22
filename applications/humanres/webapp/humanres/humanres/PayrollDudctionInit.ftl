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
<#--<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellexternalcopymanager.js</@ofbizContentUrl>"></script>-->
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
 var data; 

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
 
	
    
function setUpItemList(emptyData) {
			//recentChnage = recentChnage;
			
			var grid;		
			<#if employeesJSON?has_content>
			 	data = ${StringUtil.wrapString(employeesJSON)!'[]'};
			<#else>
			 	 data =[];
			</#if>
			if(typeof emptyData !== "undefined"){
				data =[];
			}
			var columns = [	
			    	{id:"payrollHeaderId", name:"Payroll Header Id", field:"payrollHeaderId", width:150, minWidth:100, cssClass:"cell-title",sortable:true},
					{id:"employeeId", name:"Employee Id", field:"employeeId", width:150, minWidth:100, cssClass:"cell-title",sortable:true},
					{id:"name", name:"Employee Name", field:"name", width:300, minWidth:100, cssClass:"cell-title",sortable:true},
					{id:"check", name:"Select Employee", field:"check", width:300, minWidth:100, cssClass:"cell-title",editor:YesNoCheckboxCellEditor, sortable:true},
					{id:"comment", name:"Comment", field:"comment", width:300, minWidth:100, cssClass:"cell-title",editor:TextCellEditor, sortable:false},
			];
			
			
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
			grid = new Slick.Grid("#myGrid1", data, columns, options);
	        grid.setSelectionModel(new Slick.CellSelectionModel());
			grid.onBeforeEditCell.subscribe(function(e, args) { 
				
			});
			//grid.registerPlugin(new Slick.CellExternalCopyManager(pluginOptions));
			
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
				     if(cell && cell.cell == 6){
				          $(grid.getCellNode((cell.row),7)).click();
				          grid.getEditController().commitCurrentEdit();
				     
				     }else{
					      editClickHandler(cell.row);
					      $(grid.getCellNode(cell.row +1, 6)).click();
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
	         	             
	        // initialize the model after all the events have been hooked up
			dataView2.beginUpdate();
			dataView2.setItems(data);
			dataView2.endUpdate();
	}
	$(document).ready(function() {			
		  	setUpItemList();
		  	
	});
	
	function cleanUpGrid(value){
		$('[name=EmpSalaryDisbursement]').val(value);
		var emptyJson= [];
		setUpItemList("cleanGrid") ;
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
			var payrollHeaderId = data[rowCount]["payrollHeaderId"];
			var employeeId = data[rowCount]["employeeId"];
			var check = data[rowCount]["check"];
			var comment = data[rowCount]["comment"];
			
			if(comment == ""){
			   comment = "NA";
			}
			
			if(check){
			
				var inputPayrollHeaderId = jQuery("<input>").attr("type", "hidden").attr("name", "payrollHeaderId_o_" + rowCount).val(payrollHeaderId);
				var inputEmployeeId = jQuery("<input>").attr("type", "hidden").attr("name", "employeeId_o_" + rowCount).val(employeeId);
				var comment = jQuery("<input>").attr("type", "hidden").attr("name", "comment_o_" + rowCount).val(comment);
				jQuery(formId).append(jQuery(inputPayrollHeaderId));
				jQuery(formId).append(jQuery(inputEmployeeId));
				jQuery(formId).append(jQuery(comment));
			
			}
			
			
				    
		}
		
		// lets make the ajaxform submit
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
	            	    $('div#updateEntryMsg').html('<label>'+ result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]+'</label>');
	            	     
	               }else{
	               		
	               		$("div#updateEntryMsg").fadeIn();               	         	   
	            	    $('div#updateEntryMsg').html(); 
	            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
	            	    $('div#updateEntryMsg').addClass("messageStr");
	            	    $('div#updateEntryMsg').html('<label>succesfully updated.</label>'); 
	            	    $('div#updateEntryMsg').delay(5000).fadeOut('slow');  
	            	    cleanUpGrid(); 
	               }
               
               },
             error: function() {
            	 	//alert("record not updated");
            	 }
           });
			
	}
</script>
			
	<div id="wrapper" style="width: 95%; height:100%">
	  <form method="post" name="EmpSalaryDisbursement " id="EmpSalaryDisbursement"> 
	  </form>
	</div>
	<div name ="updateEntryMsg" id="updateEntryMsg">      
	</div>
 	<div class="grid-header" style="width:100%">
			<label>EnterExcluded Employee Salary Disbursement  </label>
	</div>    
	<div id="myGrid1"  style="width:100%;height:250px;"></div>
	<div align="center">
		 <table width="60%" border="0" cellspacing="0" cellpadding="0">  
		    <tr><td></td><td></td></tr>
		    <tr><td></td><td></td></tr>
		    <tr><td>  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" style="padding:.3em" name="changeSave" id="changeSave" value="Save" onclick="javascript:processIndentEntry('EmpSalaryDisbursement','<@ofbizUrl>updateExcludeSalaryDisbursement</@ofbizUrl>');" /></td>
		    <td><input type="button" style="padding:.3em" id="changeCancel" value="Cancel" onclick="javascript:cleanUpGrid('EmpSalaryDisbursement');"/>  &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;</td></tr>
		 </table>
	</div>    
	
