
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
	function setUpFirstCell(data) {
			var grid;			
			data1=data;			
			var columns = [
				{id:"id", name:"Cell", field:"id", width:60, minWidth:100, sortable:false},
				{id:"quantity", name:"Quantity", field:"quantity", width:70, minWidth:100, sortable:false},
				{id:"quantityLtrs", name:"QuantityLtrs", field:"quantityLtrs", width:80, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"fat", name:"Fat",  field:"fat", width:50, minWidth:40, sortable:false , editor:FloatCellEditor},
				{id:"snf", name:"Snf", field:"snf", width:50,  minWidth:40, sortable:false , editor:FloatCellEditor},		
				{id:"clr", name:"CLR", field:"clr", width:50, minWidth:40, sortable:false , editor:FloatCellEditor}	,		
				{id:"acid", name:"Acidity",  field:"acid", width:60, minWidth:100, sortable:false , editor:FloatCellEditor},
				{id:"temp", name:"temp", field:"temp", width:90, minWidth:40, sortable:false , editor:FloatCellEditor}
															
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
				var qty = changeItem[key];
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
<div class="screenlet" style="width: 100%; height:100%">
	<div class="screenlet-title-bar">
      <h3>Acknowledgement Milk </h3>
    </div>
	<div class="screenlet-body">
		<form method="post" name="approveMilkReceiptEntry" id="approveMilkReceiptEntry" action="<@ofbizUrl>approveMilkReceiptEntryCreation</@ofbizUrl>">   
      		<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">    
      		 	<input  type="hidden" size="10pt" id="milkTransferId" name="milkTransferId" value="${cellWiseDetailsMap.get("milkTransferId")?if_exists}"/> 
	        	<tr>
			  		<td>&nbsp;</td>
	          		<td align='left' valign='middle' nowrap="nowrap">
	          	 		<table>
		          			<tr>
		          				<table>
		          					<tr>
		          						<#assign fromFacilityDetails = delegator.findOne("Facility", {"facilityId" : cellWiseDetailsMap.get("facilityId")}, true)>	
                         				<td><span class='h3'>From :</span></td><td><input  type="hidden" size="10pt" id="facilityId" name="facilityId" value="${cellWiseDetailsMap.get("facilityId")?if_exists}"/>${fromFacilityDetails.get("facilityName")?if_exists}</td>
                   					</tr>
                   			 		<tr>
                   			 			<#assign toFacilityDetails = delegator.findOne("Facility", {"facilityId" : cellWiseDetailsMap.get("facilityIdTo")}, true)>	
                   			 			<td><span class='h3'>To   :</span></td><td><input  type="hidden" size="10pt" id="facilityIdTo" name="facilityIdTo" value="${cellWiseDetailsMap.get("facilityIdTo")?if_exists}"/>${toFacilityDetails.get("facilityName")?if_exists}</td>
					        		</tr>
					        		<tr>					        	
					        			<td><span class='h3'>Send Date :</span></td><td><input  type="hidden" size="10pt" id="fromDate" name="fromDate" value="${cellWiseDetailsMap.get("sendDate")}"/>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cellWiseDetailsMap.get("sendDate"),"dd-MM-yyyy")}</td>
					        		</tr>
					        		<tr>					        	
					        			<td><span class='h3'>Despatch Time:</span></td><td><input  name="sendTime" size="10pt" type="hidden" id="sendTime" value="${cellWiseDetailsMap.get("sendTime")?if_exists}"/>${cellWiseDetailsMap.get("sendTime")?if_exists}</td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>Milk Type       :</span></td><td> <select name="productId" class='h4'>
					        				<#assign productDetails = delegator.findOne("Product", {"productId" : cellWiseDetailsMap.get("productId")}, true)>
					        				<option value='${cellWiseDetailsMap.get("productId")}'>${productDetails.get("brandName")}</option>
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
					        			<td><span class='h3'>Tanker No :</span></td><td><input  name="tankerNo" size="10pt" type="hidden" id="tankerNo" value="${cellWiseDetailsMap.get("vehicleId")?if_exists}"/>${cellWiseDetailsMap.get("vehicleId")?if_exists}</td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>Capacity :</span></td><td><input  name="capacity" size="10pt" type="hidden" id="capacity" value="${cellWiseDetailsMap.get("capacity")?if_exists}"/>${cellWiseDetailsMap.get("capacity")?if_exists}<td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>COB  :</span></td><td><input  name="cob" size="10pt" type="hidden" id="cob" value="${cellWiseDetailsMap.get("cob")?if_exists}"/>${cellWiseDetailsMap.get("cob")?if_exists}</td>
					        		</tr>
					        		<tr>					        	
					        			<td><span class='h3'>C/P  :</span></td><td><input  name="milkCondition" size="10pt" type="hidden" id="milkCondition" value="${cellWiseDetailsMap.get("milkCondition")?if_exists}"/>${cellWiseDetailsMap.get("milkCondition")?if_exists}</td>
					        		</tr>					        		
					        		<tr>					        	
					        			<td><span class='h3'>SODA:</span></td><td>
					        				<select name="soda" class='h4'>
					  							<option>${cellWiseDetailsMap.get("soda")?if_exists}</option>
					            				<option>N</option><option>P</option>		                  	 
											</select>
										</td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>Received Date</span></td><td><input  type="text" size="15pt" id="selectThruDate" name="receivedDate" required/><em>*</em></td>
					        		</tr>
					        		<tr>
						        		<td align='left' >Ack Time </td><td><input  name="ackTime" size="10pt" type="text" id="ackTime" /></td>
						        	</tr>
					        		<#--<tr>					        	
					        			<td><span class='h3'>Sour/Curd  :</span></td><td>
					        				<select name="milkType" class='h4' onchange="javascript:milkTypeChangeHandler(this);">
						  						<option></option>
						            			<option>C</option><option>S</option>		                  	 
											</select>
										</td>
					        		</tr>-->
					         		<tr>					        	
					        			<td><span class='h3'>Sour Qty(Lts)</span></td><td><input  type="text" size="15pt" id="sQuantityLtrs" name="sQuantityLtrs"/></td>
					        		</tr>
					        		<tr>					        	
					        			<td><span class='h3'>Sour Fat</span></td><td><input  type="text" size="15pt" id="sFat" name="sFat"/></td>
					        		</tr>
					        		<tr>					        	
					        			<td><span class='h3'>Sour Snf</span></td><td><input  type="text" size="15pt" id="sSnf" name="sSnf"/></td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>ghee Yeild(Kgs)</span></td><td><input  type="text" size="15pt" id="gheeYield" name="gheeYield"/></td>
					        		</tr>
					         		<tr>					        	
					        			<td><span class='h3'>Curd Qty(Lts)</span></td><td><input  type="text" size="15pt" id="cQuantityLtrs" name="cQuantityLtrs"/></td>
					        		</tr>
					     		</table>
					  		</tr>
						<table>      
	          		</td>    
	        	</tr>       
      		</table>
      	<div class="grid-header" style="width:100%">
			<label>Cells Information</label>
		</div>
		<div id="myGrid1" style="width:100%;height:250px;"></div>	
		<div align="center">
    		<input type="submit" style="padding:.3em" id="changeSave" value="Approve" onclick="javascript:processMilkReceiptIndent('approveMilkReceiptEntry','<@ofbizUrl>approveMilkReceiptEntryCreation</@ofbizUrl>');"/>
    	</div>
	</form>
	</div>
</div>	
</div>