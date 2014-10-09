
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
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
<script type="application/javascript">
	var dataView4;
	var data1;
	function fatFormatter(row, cell, value, columnDef, dataContext) { 
		var qtyValue = value;
		var test = value;
		if(qtyValue!=""){
		var intVal = 0;
		var decimal = 0
			if(qtyValue>10){
				 alert('excess fat entered');
				 intVal =qtyValue;  
				 $('#changeSave').attr("disabled", "disabled");
				 
			}else{
				intVal = qtyValue;
				$('#changeSave').removeAttr("disabled");
			}
			return parseFloat(intVal).toFixed(1);
		}else{
			return '';
		}
	}
	function setUpFirstCell(data) {
			var grid;
			data1=data;			
			var columns = [
				{id:"id", name:"Cell", field:"id", width:60, minWidth:100, sortable:false},
				{id:"quantityLtrs", name:"QuantityLtrs", field:"quantityLtrs", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"fat", name:"Fat",maxWidth:4,formatter:fatFormatter,  field:"fat", width:50, minWidth:40, sortable:false , editor:FloatCellEditor},
				{id:"snf", name:"Snf", field:"snf", width:50,  minWidth:40, sortable:false , editor:FloatCellEditor},		
				{id:"clr", name:"CLR", field:"clr", width:50, minWidth:40, sortable:false , editor:FloatCellEditor}	,		
				{id:"acid", name:"Acidity",  field:"acid", width:60, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"temp", name:"temp", field:"temp", width:60, minWidth:40, sortable:false , editor:FloatCellEditor}															
			];
	
			var options = {
				editable: true,		
				forceFitColumns: false,
				enableCellNavigation: true,
				autoEdit: true,
				asyncEditorLoading: false,			
	            secondaryHeaderRowHeight: 25
			};
			
	        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
			dataView4 = new Slick.Data.DataView({
	        	groupItemMetadataProvider: groupItemMetadataProvider
	        });
			grid = new Slick.Grid("#myGrid1", dataView4, columns, options);
	        grid.setSelectionModel(new Slick.CellSelectionModel());
			grid.onBeforeEditCell.subscribe(function(e, args) { 
				
			});
			var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
			
			// wire up model events to drive the grid
			dataView4.onRowCountChanged.subscribe(function(e,args) {
				grid.updateRowCount();
	            grid.render();
			});
			dataView4.onRowsChanged.subscribe(function(e,args) {
			    
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
					      approveClickHandler(cell.row);
					      $(grid.getCellNode(cell.row +1, 6)).click();
					      grid.getEditController().commitCurrentEdit();
				     }
				     
				}
				
			});
			
			// initialize the model after all the events have been hooked up
			dataView4.beginUpdate();
			dataView4.setItems(data1);
			dataView4.endUpdate();	
	
	}	
	$(document).ready(function() {		
		<#if dataJSON?has_content>
		 dataJsonValue = ${StringUtil.wrapString(dataJSON)!'[]'}
		 setUpFirstCell(dataJsonValue);
		</#if>  
	 	makeDatePicker("selectThruDate","thruDate");       
		$('#ui-datepicker-div').css('clip', 'auto');
		
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)}
	$("input").keyup(function(e){
	  		if(e.target.name == "mccCode" ){
				var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
	  			if(tempUnitJson){
	  				$('span#unitToolTip').addClass("tooltip");
	  				$('span#unitToolTip').removeClass("tooltipWarning");
	  				unitName = tempUnitJson["name"];
	  				unitId = tempUnitJson["facilityId"];
	  				$('span#unitToolTip').html(unitName);
	  				$('[name=facilityId]').val(unitId);
	  			}else{
	  				$('span#unitToolTip').removeClass("tooltip");
	  				$('span#unitToolTip').addClass("tooltipWarning");
	  				$('span#unitToolTip').html('Code not found');
	  			}	  			
	  		}
	  		
	}); 
		
	});
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}	
	function processMilkReceiptIndent(formName, action) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		}
		var formId = "#" + formName;
		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));	
							
		var i =	dataView4.getLength();		
		for(j=0;j< i;j++){					
			var changeItem = dataView4.getItem(j);	
			var rowCount = 0;
			var dayId = changeItem["id"];	
			for (key in changeItem){
				var qty =changeItem[key];
				if (key != "id") {
					var inputKey = jQuery("<input>").attr("type", "hidden").attr("name", dayId+"_"+key).val(qty);
					jQuery(formId).append(jQuery(inputKey));  
				} 								 				
    			rowCount++; 				
			}
		
		}
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	}
</script>
<div class="lefthalf">
<div class="screenlet" style="width: 100%; height:130%">
	<div class="screenlet-title-bar">
      <h3>Dispatch  Milk</h3>
    </div>
	<div class="screenlet-body">
		<form method="post" name="milkReceiptEntry" id="milkReceiptEntry" action="<@ofbizUrl>milkReceiptEntryCreation</@ofbizUrl>">  
   
      	<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table>
                         	<tr>
                         		<td><span class='h3'>From:</span></td><td>
                         		<input type="hidden" size="6" id="ACKfacilityId" maxlength="6" name="facilityId" id="facilityId" autocomplete="off" value="" />
                         		<input type="text" size="6" maxlength="6" name="mccCode" id="mccCode" autocomplete="off"/><span class="tooltip" id ="unitToolTip">none</span></td>
                   			</tr>
                   			<tr>
                   				<td><span class='h3'>To:</span></td><td> MPF,HYD<input type="hidden" size="6" id="ACKfacilityIdTo" maxlength="6" name="facilityIdTo" id="facilityIdTo" autocomplete="off" value="MAIN_PLANT" /></td>
                   			</tr>
                   			<tr>
	        					<td align='left' ><span class='h3'>Send Date</span></td><td><input  type="text" size="15pt" id="selectThruDate" name="fromDate"/></td>
	        				</tr>
	        				<tr>
						        	<td align='left' >Despatch Time </td><td><input  name="sendTime" size="10pt" type="text" id="sendTime"/></td>
						        </tr>
	        					<tr>	        	
						        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Milk Type</td><td>
							        	<select name="productId" class='h4'>
							  				<#if milkReceiptProductList?has_content>	
							            		<#list milkReceiptProductList as milkType>    
							                  	    <option value='${milkType.productId}' >
							                    		${milkType.brandName}
							                  		 </option>		                  	 
							            		</#list>        
							            	</#if>	    
										</select>
									</td>
	        					</tr> 
						         <tr>
						        	<td align='left' >Tanker No </td><td><input  name="tankerNo" size="10pt" type="text" id="tankerNo" /></td>
						        </tr> 
						         <#--<tr>
						        	<td align='left' >Capacity</td><td>
						        		<select name="capacity" class='h4'>
							  				<option></option>
							            	<option>Y</option><option>N</option>		                  	 
										</select>
									</td>
						        </tr>-->
								<tr>
						        	<td align='left' >COB</td><td>
							        	<select name="cob" class='h4'>
							  				<option>N</option>
							            	<option>Y</option>		                  	 
										</select>
									</td>
						        </tr>       					
	        					 <tr>
						        	<td align='left' >C/P/U</td><td>
							        	<select name="milkCondition" class='h4'>
							  				<option>C</option>
							            	<option>P</option>
							            	<option>U</option>		                  	 
										</select>
									</td>
						        </tr>
						        <tr>
						        	<td align='left' >SODA</td><td>
							        	<select name="soda" class='h4'>
							  				<option>N</option>
							            	<option>P</option>							            	                  	 
										</select>
									</td>
						        </tr>    
                      		</table>
	          			</tr>
	          		</table>
	         	 </td>  
	        </tr>          
      </table>
      <div class="grid-header" style="width:100%">
			<label>Cells Information</label>
	  </div>
	  <div id="myGrid1" style="width:100%;height:190px;""></div>	
	  <div align="center">
    	<input type="submit" style="padding:.3em" id="changeSave" value="Save" onclick="javascript:processMilkReceiptIndent('milkReceiptEntry','<@ofbizUrl>milkReceiptEntryCreation</@ofbizUrl>');"/>
      </div>
   </form>
  </div>
 </div>	
</div>