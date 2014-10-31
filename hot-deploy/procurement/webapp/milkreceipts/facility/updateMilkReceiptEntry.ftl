
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

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoNumeric/autoNumeric-1.6.2.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/validate/jquery.validate.js</@ofbizContentUrl>"></script>

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/autoTab/jquery.autotab-1.1b.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	var dataView2;
	//Decimal formating
jQuery(function($) {
		var sendDateFormat = $('#selectFromDate').datepicker( "option", "dateFormat" );
		$('#selectFromDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		var recDateFormat = $('#selectThruDate').datepicker( "option", "dateFormat" );
		$('#selectThruDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		
		
		$('#fcQtyLtrs').autoNumeric({mDec: 1 , autoTab : true,aSep:'', autoTabTarget : 'fcAckQtyLtrs' }).trigger('focusout');
		$('#fcAckQtyLtrs').autoNumeric({mDec: 1 , autoTab : true ,aSep:'',autoTabTarget : 'fcFat'}).trigger('focusout');
		$('#fcFat').autoNumeric({mNum: 2,mDec: 2 ,autoTab : true , autoTabTarget : 'fcAckFat'}).trigger('focusout');
		$('#fcAckFat').autoNumeric({mNum: 2,mDec: 2,autoTab : true ,autoTabTarget : 'fcSnf'}).trigger('focusout');
		$('#fcSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true, autoTabTarget : 'fcAckSnf' }).trigger('focusout');
		$('#fcAckSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true ,autoTabTarget : 'fcClr'}).trigger('focusout');
		$('#fcClr').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'fcAckClr'}).trigger('focusout');
		$('#fcAckClr').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'fcAcid'}).trigger('focusout');
		$('#fcAcid').autoNumeric({mNum: 3,mDec: 1 ,autoTab : true , autoTabTarget : 'fcAckAcid'}).trigger('focusout');
		$('#fcAckAcid').autoNumeric({mNum: 3,mDec: 1,autoTab : true ,autoTabTarget : 'fcTemp'}).trigger('focusout');
		$('#fcTemp').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'fcAckTemp'}).trigger('focusout');
		$('#fcAckTemp').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'bcQtyLtrs'}).trigger('focusout');
		
		
		$('#bcQtyLtrs').autoNumeric({mDec: 1 , autoTab : true,aSep:'', autoTabTarget : 'bcAckQtyLtrs' }).trigger('focusout');
		$('#bcAckQtyLtrs').autoNumeric({mDec: 1 , autoTab : true ,aSep:'',autoTabTarget : 'bcFat'}).trigger('focusout');
		$('#bcFat').autoNumeric({mNum: 2,mDec: 2 ,autoTab : true , autoTabTarget : 'bcAckFat'}).trigger('focusout');
		$('#bcAckFat').autoNumeric({mNum: 2,mDec: 2,autoTab : true ,autoTabTarget : 'bcSnf'}).trigger('focusout');
		$('#bcSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true, autoTabTarget : 'bcAckSnf' }).trigger('focusout');
		$('#bcAckSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true ,autoTabTarget : 'bcClr'}).trigger('focusout');
		$('#bcClr').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'bcAckClr'}).trigger('focusout');
		$('#bcAckClr').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'bcAcid'}).trigger('focusout');
		$('#bcAcid').autoNumeric({mNum: 3,mDec: 1 ,autoTab : true , autoTabTarget : 'bcAckAcid'}).trigger('focusout');
		$('#bcAckAcid').autoNumeric({mNum: 3,mDec: 1,autoTab : true ,autoTabTarget : 'bcTemp'}).trigger('focusout');
		$('#bcTemp').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'bcAckTemp'}).trigger('focusout');
		$('#bcAckTemp').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'mcQtyLtrs'}).trigger('focusout');
		
		
		$('#mcQtyLtrs').autoNumeric({mDec: 1 , autoTab : true,aSep:'', autoTabTarget : 'mcAckQtyLtrs' }).trigger('focusout');
		$('#mcAckQtyLtrs').autoNumeric({mDec: 1 , autoTab : true ,aSep:'',autoTabTarget : 'mcFat'}).trigger('focusout');
		$('#mcFat').autoNumeric({mNum: 2,mDec: 2 ,autoTab : true , autoTabTarget : 'mcAckFat'}).trigger('focusout');
		$('#mcAckFat').autoNumeric({mNum: 2,mDec: 2,autoTab : true ,autoTabTarget : 'mcSnf'}).trigger('focusout');
		$('#mcSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true, autoTabTarget : 'mcAckSnf' }).trigger('focusout');
		$('#mcAckSnf').autoNumeric({mNum: 2,mDec: 2 , autoTab : true ,autoTabTarget : 'mcClr'}).trigger('focusout');
		$('#mcClr').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'mcAckClr'}).trigger('focusout');
		$('#mcAckClr').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'mcAcid'}).trigger('focusout');
		$('#mcAcid').autoNumeric({mNum: 3,mDec: 1 ,autoTab : true , autoTabTarget : 'mcAckAcid'}).trigger('focusout');
		$('#mcAckAcid').autoNumeric({mNum: 3,mDec: 1,autoTab : true ,autoTabTarget : 'mcTemp'}).trigger('focusout');
		$('#mcTemp').autoNumeric({mNum: 2,mDec: 1 ,autoTab : true , autoTabTarget : 'mcAckTemp'}).trigger('focusout');
		$('#mcAckTemp').autoNumeric({mNum: 2,mDec: 1,autoTab : true ,autoTabTarget : 'submitEntry'}).trigger('focusout');
	});
	$(document).ready(function() {	
	   //$(':input').autotab_magic();	
		
		$('#fcQtyLtrs').autotab({ target: 'fcAckQtyLtrs'});
		//$('#fcAckQtyLtrs').autotab({ target: 'fcFat',previous: 'fcQtyLtrs'});
		//$('#fcFat').autotab({ target: 'fcAckFat', previous: 'fcAckQtyLtrs'});
	
		
	 	makeDatePicker("selectFromDate","fromDate");
	 	makeDatePicker("selectThruDate","thruDate");       
		$('#ui-datepicker-div').css('clip', 'auto');
		
		var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)}
		var productJson = ${StringUtil.wrapString(productJson)}
		$("input").keyup(function(e){
		  		if(e.target.name == "milkTransferId" ){
		  			var tranId = $('[name=milkTransferId]').val();
					if(tranId){
						fetchMilkReceiptItems(tranId);
					}
		  		}
		  		if(e.target.name == "mccCode" ){
					var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
		  			if(tempUnitJson){
		  				$('span#unitToolTip').addClass("tooltip");
		  				$('span#unitToolTip').removeClass("tooltipWarning");
		  				unitName = tempUnitJson["name"];
		  				unitId = tempUnitJson["facilityId"];
		  				showQtyKgs = tempUnitJson["showQtyKgs"];
		  				$('span#unitToolTip').html(unitName);
		  				$('[name=facilityId]').val(unitId);
		  				$('[name=qtyKgsFlag]').val(showQtyKgs);
		  			}else{
		  				$('[name=facilityId]').val('');
		  				$('span#unitToolTip').removeClass("tooltip");
		  				$('span#unitToolTip').addClass("tooltipWarning");
		  				$('span#unitToolTip').html('Code not found');
		  			}	  			
		  		}
		  		
		  		if(e.target.name == "product" ){
					var tempProductJson = productJson[$('[name=product]').val()];
		  			if(tempProductJson){
		  				$('span#productToolTip').addClass("tooltip");
		  				$('span#productToolTip').removeClass("tooltipWarning");
		  				productName = tempProductJson["name"];
		  				$('[name=productId]').val($('[name=product]').val());
		  				$('span#productToolTip').html(productName);
		  			}else{
		  				$('[name=productId]').val('');	
		  				$('span#productToolTip').removeClass("tooltip");
		  				$('span#productToolTip').addClass("tooltipWarning");
		  				$('span#productToolTip').html('product not found');
		  			}	  			
		  		}
		  		
		  		
		}); 
		
			
	});
function setQtyFlag(){
	var mccCodeJson = ${StringUtil.wrapString(mccCodeJson)}
	var tempUnitJson = mccCodeJson[$('[name=mccCode]').val()];
	if(tempUnitJson){
		var showQtyKgs = tempUnitJson["showQtyKgs"];
		$('[name=qtyKgsFlag]').val(showQtyKgs);
	}
}
	
function makeDatePicker(fromDateId ,thruDateId){
	$( "#"+fromDateId ).datepicker({
			dateFormat:'dd-mm-yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
}	
function setUpRecentList(recentChnage) {
			recentChnage = recentChnage;
			receiveDate = (new Date((recentChnage["receiveDate"])["time"])).toString('yyyy-MM-dd');
			var cellQtyList = recentChnage["cellQtyList"];
			var grid;
			var data = [			
					{"id":"1","Date":receiveDate , "unit":recentChnage['mccCode'], 
						"qty":recentChnage['recdQtyLtrs']			
					}				
			];		
			
			
			
			var columns = [
				{id:"Date", name:"Date", field:"Date", width:80, minWidth:90, cssClass:"cell-title", sortable:false},
				{id:"unit", name:"unit", field:"unit", width:50, minWidth:50, cssClass:"cell-title", sortable:false},
				{id:"qty", name:"Tot Qty Ltrs", field:"qty", width:80, minWidth:50, cssClass:"cell-title", sortable:false}
			];
			$.each(cellQtyList, function(idx,obj){
				$.each(obj, function(key,value){
					item = {};
        		    item [key] = value;
					data.push(item);
					columnItem = {};
					columnItem["id"]=key;
					columnItem["name"]=key+" Qty";
					columnItem["field"]=key;
					columnItem["width"]=60;
					columnItem["minWidth"]=50;
					columnItem["cssClass"]="cell-title";
					columnItem["sortable"]=false;
					columns.push(columnItem);
				});	
			});
			var tempJson={};
	          $.each( data,function(index,obj){
	          			$.each( obj,function(key,val){
	          				tempJson[key]=val;
		         		 });
		          }); 
	        data =[];
	        data.push(tempJson);
			
			
			var options = {
				editable: false,		
				forceFitColumns: false,
				enableCellNavigation: true,
				autoEdit: false,
				asyncEditorLoading: false,			
	            secondaryHeaderRowHeight: 25
			};
	        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
			dataView2 = new Slick.Data.DataView({
	        	groupItemMetadataProvider: groupItemMetadataProvider
	        });
			grid = new Slick.Grid("#myGrid2", dataView2, columns, options);
	        grid.setSelectionModel(new Slick.CellSelectionModel());
			grid.onBeforeEditCell.subscribe(function(e, args) { 
				
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
	            
			// initialize the model after all the events have been hooked up
			dataView2.beginUpdate();
			dataView2.setItems(data);
			dataView2.endUpdate();
			
		//changeDate = (new Date((recentChnage["changeDatetime"])["time"])).toString('dd-MM-yyyy HH:mm:ss');
		
		//$('#lastChange').html('<label>Last Change [made by  ' + recentChnage["lastModifiedByUserLogin"] +' ]</label>');
	
	}
	
	 $(window).load(function() 
	{  
		<#if milkTransferId?has_content>
		if(milkTransferId){
			fetchMilkReceiptItems(milkTransferId);
		 }
		 </#if>
	}); 

</script>
</div>
<div id="wrapper" style="width: 90%; height:100%">

<div style="float: left;width: 90%; background:transparent;border: #F97103 solid 0.1em; valign:middle">

	<div class="screenlet" background:transparent;border: #F97103 solid 0.1em;>
      <h3>Update Dispatch And Acknowledgement of Milk</h3>
    </div>
	<div name ="displayMsg" id="ReceiptEntry_spinner">      
      </div>    
	<div class="screenlet-body">
		<form method="post" name="milkReceiptEntry" id="milkReceiptEntry" action="<@ofbizUrl>updateReceipt</@ofbizUrl>" >  
      	<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;" border="1">     
	        <tr>
			  <td>&nbsp;</td>
	          <td align='left' valign='middle' nowrap="nowrap">
	          <table>
	          		<tr>
	          			<table>
	          					<tr>
						        	<td><span class='h3'>Record Number:</span>
						        		<td><input type="text" size="8" maxlength="7" name="milkTransferId" id="milkTransferId" autocomplete="off" value="${milkTransferId?if_exists}" />
						        	</td></td>
						        </tr>
                         	<tr>
	        					<td align='left' ><span class='h3'>Dispatch Date</span></td><td><input  type="text" size="15pt" id="selectFromDate" name="sendDate" autocomplete="off"  /></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td>
	        					<td></td><td></td><td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td>
	        					<td></td><td></td>
	        					<td align='right' ><span class='h3'>Ack Date</span></td><td><input  type="text" autocomplete="off" size="15pt" id="selectThruDate" name="receiveDate"  /></td>
	        				</tr>
	        				<tr><td align='left' >Despatch Time </td><td><input  name="sendTime" size="10" maxlength="4" type="text" id="sendTime" autocomplete="off"  /></td>
						        	
						        	<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td>
	        					<td></td><td></td><td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td><td></td><td></td>
	        					<td></td><td></td>
	        					<td></td><td></td>
	        					
	        					<td align='right' >Ack Time </td><td><input  name="ackTime" maxlength="4" size="10" type="text" id="ackTime" autocomplete="off"  /></td>
						        </tr>
                         	<tr>
                         		<td><span class='h3'>From:</span></td><td>
                         		<input type="hidden" size="6" id="qtyKgsFlag" maxlength="6" name="qtyKgsFlag"  autocomplete="off"  />
                         		<input type="hidden" size="6" id="ACKfacilityId" maxlength="6" name="facilityId"  autocomplete="off"  />
                         		<input type="text" size="6" maxlength="6" name="mccCode" id="mccCode" autocomplete="off"  /><span class="tooltip" id ="unitToolTip">none</span></td>
                   			</tr>
                   			<tr>
                   				<td><span class='h3'>To:</span></td><td> MPF,HYD<input type="hidden" size="6" id="ACKfacilityIdTo" maxlength="6" name="facilityIdTo" id="facilityIdTo" autocomplete="off" value="MAIN_PLANT" /></td>
                   			</tr>
	        					<tr>	        	
						        	<td align='left' valign='middle' nowrap="nowrap"><div class='h3'>Milk Type</td><td>
							        	<input type="hidden" size="6" id="productId" maxlength="6" name="productId" autocomplete="off" value="${productId?if_exists}" />
                         				<input type="text" size="6" maxlength="6" name="product" id="product" autocomplete="off"  /><span class="tooltip" id ="productToolTip"> none</span></td>
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
							        	<select name="cob"  id ="cob" class='h4'>
							  				<option>N</option>
							            	<option>Y</option>		                  	 
										</select>
									</td>
						        </tr>       					
	        					 <tr>
						        	<td align='left' >C/P/U</td><td>
							        	<select name="milkCondition" id="milkCondition" class='h4' >
							  				<option value='C'>C</option>
							  				<option value='P'>P</option>
							  				<option value='U'>U</option>
										</select>
									</td>
						        </tr>
						        <tr>
						        	<td align='left' >SODA</td><td>
							        	<select name="soda" id="soda" class='h4'>
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
	        <tr>
	        <div class="lefthalf">
	        <div class="grid-header h2" style="width:98%">
				<label>Dispatch Cells Information</label>
	  		</div>
	        	<td>
					 <table class="basic-table hover-bar h2" cellspacing="0">
						<th>CELL</th>
						<th>QTY LTRS</th>
						<th></th>
						<th>FAT</th>
						<th>SNF</th>
						<th>CLR</th>
						<th>ACIDITY</th>
						<th>TEMP</th>
						<tr>
							<td>FC</td>
							<td><input type="text" size="8" maxlength="7" name="fcQtyLtrs" id="fcQtyLtrs" autocomplete="off" value="${fcMap.get("sendQtyLtrs")?if_exists}" /></td>
							<td><input type="hidden" size="8" maxlength="7" name="fcSequenceNum" id="fcSequenceNum" autocomplete="off" value="${fcMap.get("sequenceNum")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="fcFat" id="fcFat" autocomplete="off" value="${fcMap.get("sendFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="fcSnf" id="fcSnf" autocomplete="off" value="${fcMap.get("sendSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="fcClr" id="fcClr" autocomplete="off" value="${fcMap.get("sendClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="fcAcid" id="fcAcid" autocomplete="off" value="${fcMap.get("sendAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="fcTemp" id="fcTemp" autocomplete="off" value="${fcMap.get("sendTemp")?if_exists}" /></td> 
						</tr>
						<tr><td>BC</td>
							<td><input type="text" size="8" maxlength="7" name="bcQtyLtrs" id="bcQtyLtrs" autocomplete="off" value="${bcMap.get("sendQtyLtrs")?if_exists}" /></td>
							<td><input type="hidden" size="8" maxlength="7" name="bcSequenceNum" id="bcSequenceNum" autocomplete="off" value="${bcMap.get("sequenceNum")?if_exists}" /></td>	
							<td><input type="text" size="5" maxlength="6" name="bcFat" id="bcFat" autocomplete="off" value="${bcMap.get("sendFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="bcSnf" id="bcSnf" autocomplete="off" value="${bcMap.get("sendSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="bcClr" id="bcClr" autocomplete="off" value="${bcMap.get("sendClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="bcAcid" id="bcAcid" autocomplete="off" value="${bcMap.get("sendAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="bcTemp" id="bcTemp" autocomplete="off" value="${bcMap.get("sendTemp")?if_exists}" /></td>
						</tr>
						<tr><td>MC</td>
							<td><input type="text" size="8" maxlength="7" name="mcQtyLtrs" id="mcQtyLtrs" autocomplete="off" value="${mcMap.get("sendQtyLtrs")?if_exists}" /></td>
							<td><input type="hidden" size="8" maxlength="7" name="mcSequenceNum" id="mcSequenceNum" autocomplete="off" value="${mcMap.get("sequenceNum")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="mcFat" id="mcFat" autocomplete="off" value="${mcMap.get("sendFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="mcSnf" id="mcSnf" autocomplete="off" value="${mcMap.get("sendSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="mcClr" id="mcClr" autocomplete="off" value="${mcMap.get("sendClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="mcAcid" id="mcAcid" autocomplete="off" value="${mcMap.get("sendAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="mcTemp" id="mcTemp" autocomplete="off" value="${mcMap.get("sendTemp")?if_exists}" /></td>
						</tr>
					</table>	        	
	        	</td>
	        	</div>
	        </tr>
	        <tr>
	        <div class="righthalf">
	        <div class="grid-header h2" style="width:98%">
				<label>Acknowledgement Cells Information</label>
	  		</div>
	        	<td>
					 <table class="basic-table hover-bar h2" cellspacing="0">
						<th>CELL</th>
						<th>QTY LTRS</th>
						<th>FAT</th>
						<th>SNF</th>
						<th>CLR</th>
						<th>ACIDITY</th>
						<th>TEMP</th>
						<tr>
							<td nowrap="nowrap">FC ACK</td>
							<td><input type="text" size="8" maxlength="7" name="fcAckQtyLtrs" id="fcAckQtyLtrs" autocomplete="off" value="${fcMap.get("recdQtyLtrs")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="fcAckFat" id="fcAckFat" autocomplete="off" value="${fcMap.get("recdFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="fcAckSnf" id="fcAckSnf" autocomplete="off" value="${fcMap.get("recdSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="fcAckClr" id="fcAckClr" autocomplete="off" value="${fcMap.get("recdClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="fcAckAcid" id="fcAckAcid" autocomplete="off" value="${fcMap.get("recdAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="fcAckTemp" id="fcAckTemp" autocomplete="off" value="${fcMap.get("recdTemp")?if_exists}" /></td> 
						</tr>
						<tr><td nowrap="nowrap">BC ACK</td>
							<td><input type="text" size="8" maxlength="7" name="bcAckQtyLtrs" id="bcAckQtyLtrs" autocomplete="off" value="${bcMap.get("recdQtyLtrs")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="bcAckFat" id="bcAckFat" autocomplete="off" value="${bcMap.get("recdFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="bcAckSnf" id="bcAckSnf" autocomplete="off" value="${bcMap.get("recdSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="bcAckClr" id="bcAckClr" autocomplete="off" value="${bcMap.get("recdClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="bcAckAcid" id="bcAckAcid" autocomplete="off" value="${bcMap.get("recdAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="bcAckTemp" id="bcAckTemp" autocomplete="off" value="${bcMap.get("recdTemp")?if_exists}" /></td> 
						</tr>
						<tr><td nowrap="nowrap">MC ACK</td>
							<td><input type="text" size="8" maxlength="7" name="mcAckQtyLtrs" id="mcAckQtyLtrs" autocomplete="off" value="${mcMap.get("recdQtyLtrs")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="mcAckFat" id="mcAckFat" autocomplete="off" value="${mcMap.get("recdFat")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="mcAckSnf" id="mcAckSnf" autocomplete="off" value="${mcMap.get("recdSnf")?if_exists}" /></td> 
							<td><input type="text" size="5" maxlength="6" name="mcAckClr" id="mcAckClr" autocomplete="off" value="${mcMap.get("recdClr")?if_exists}" /></td>
							<td><input type="text" size="5" maxlength="6" name="mcAckAcid" id="mcAckAcid" autocomplete="off" value="${mcMap.get("recdAcid")?if_exists}" /></td>
							<td><input type="text" size="4" maxlength="6" name="mcAckTemp" id="mcAckTemp" autocomplete="off" value="${mcMap.get("recdTemp")?if_exists}" /></td> 
						</tr>
					</table>	        	
	        	</td>
	        	</div>
	        </tr>
      </table>
      <table>
      	<tr>
      		<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>
	      	<td valign = "middle" align="center">
	      	<div class='tabletext h1'>
	 <input type="submit" align="right"  class="button" name="submitButton" id="updateEntry"  value="UPDATE"/>      
	      		</div>
	      	</td>
      	</tr>
      </table>
	       
   </form>
  </div>
 </div>
</div>