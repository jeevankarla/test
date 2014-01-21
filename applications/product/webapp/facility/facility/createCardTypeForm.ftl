<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
 

 <#assign changeRowTitle = "Card Sale">                
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
</style>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
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
<script type="application/javascript">
	var dataView;
	var dataView2;
		
	function requiredFieldValidator(value) {
		if (value == null || value == undefined || !value.length)
			return {valid:false, msg:"This is a required field"};
		else
			return {valid:true, msg:null};
	}
	
	function appendParamsToProcessBulkCardSale(formName, action){	
	
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
				return false;		
			}
		var formId = "#" + formName;		
		var inputBoothId = jQuery("<input>").attr("type", "hidden").attr("name", "boothId").val(jQuery('input[name=originFacilityId]').val());
		jQuery(formId).append(jQuery(inputBoothId));
				
		var inputCounterNumber = jQuery("<input>").attr("type", "hidden").attr("name", "counterNumber").val(jQuery('input[name=counterNumber]').val());
		jQuery(formId).append(jQuery(inputCounterNumber));	
				
		var inputCustomTimePeriodId = jQuery("<input>").attr("type", "hidden").attr("name", "customTimePeriodId").val(jQuery('#customTimePeriodId').val());
		jQuery(formId).append(jQuery(inputCustomTimePeriodId));
		
		var inputCustomerName = jQuery("<input>").attr("type", "hidden").attr("name", "customerName").val(jQuery('input[name=customerName]').val());
		jQuery(formId).append(jQuery(inputCustomerName));
		
		var inputCustomerContactNumber = jQuery("<input>").attr("type", "hidden").attr("name", "customerContactNumber").val(jQuery('input[name=customerContactNumber]').val());
		jQuery(formId).append(jQuery(inputCustomerContactNumber));
		
		var inputPaymentTypeId = jQuery("<input>").attr("type", "hidden").attr("name", "paymentTypeId").val(jQuery('#paymentTypeId').val());
		jQuery(formId).append(jQuery(inputPaymentTypeId));	

		var inputRowSubmit = jQuery("<input>").attr("type", "hidden").attr("name", "_useRowSubmit").val("Y");
		jQuery(formId).append(jQuery(inputRowSubmit));	
									
		var changeItem = dataView.getItem(dataView.getLength() - 1);			
		var rowCount = 0;
		for (key in changeItem)
		{
			var qty = parseFloat(changeItem[key]);								
	 		if (key != "id" && key != "title" && key != "New" && !isNaN(qty)) {
				var inputProd = jQuery("<input>").attr("type", "hidden").attr("name", "milkCardTypeId_o_" + rowCount).val(key);
				var inputQty = jQuery("<input>").attr("type", "hidden").attr("name", "quantity_o_" + rowCount).val(qty);										
				jQuery(formId).append(jQuery(inputProd));				
				jQuery(formId).append(jQuery(inputQty));			 								 				
    			rowCount++; 				
   			}
		}
				
		jQuery(formId).attr("action", action);	
		jQuery(formId).submit();
	   
}
		
	function setupGrid1() {
		var grid;
		var data = ${StringUtil.wrapString(dataJSON)!'[]'};

		var columns = [
			{id:"title", name:"", field:"title", width:100, minWidth:100, cssClass:"cell-title", sortable:false},	
		<#if milkCardTypeList?exists> 
	        <#list milkCardTypeList as milkCardType>	
				{id:"${milkCardType.milkCardTypeId}", name:"${milkCardType.name}", field:"${milkCardType.milkCardTypeId}", width:80, minWidth:80, editor:FloatCellEditor}<#if milkCardType_has_next>,</#if>
			</#list>
		</#if>			
		];
		
		var options = {
			editable: true,		
			forceFitColumns: false,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: true,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid1", dataView, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
		grid.onBeforeEditCell.subscribe(function(e, args) { 
			var currItem = dataView.getItem(args.row);			
		
			if (currItem.title == "Quota") return false;
			if (currItem.title == "Dispatched") return false; 
		});
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

		grid.onKeyDown.subscribe(function(e) {
        	if (e.which == $.ui.keyCode.ENTER) {
				jQuery("#changeSave").click();   
            	e.stopPropagation();
            	e.preventDefault();        	
            }
            else {
            	return false;
            }
        });
        
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
		dataView.endUpdate();

	}
	
	function setupGrid2() {
		var grid;
		var data = [
			<#if lastChangeSubProdMap?exists>	
				{"id":"1", "boothId":"${lastChangeSubProdMap.boothId}","supplyType":"${lastChangeSubProdMap.supplyType}" 					
				<#if milkCardTypeList?exists>
					<#list milkCardTypeList as milkCardType>				
					, "${milkCardType.milkCardTypeId}":"${lastChangeSubProdMap[StringUtil.wrapString(milkCardType.milkCardTypeId)]}"
					</#list>
				</#if>			
				}				
			</#if>
		];

		var columns = [
			{id:"boothId", name:"Booth (S. Type)", field:"boothId", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
        	<#if milkCardTypeList?exists>
				<#list milkCardTypeList as milkCardType>				
					{id:"${milkCardType.milkCardTypeId}", name:"${milkCardType.name}", field:"${milkCardType.milkCardTypeId}", width:80, minWidth:80, editor:FloatCellEditor}<#if milkCardType_has_next>,</#if>
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

</script>	

<div class="screenlet">
	<div class="screenlet-title-bar">
         <ul>
   			<li>
            	<a href="<@ofbizUrl>CardSaleCheckList.csv?userLoginId=${userLogin.get("userLoginId")}&&all=Y</@ofbizUrl>">Card Sale Check List</a>
            </li>
            <li>
   				<a href="<@ofbizUrl>CardSaleCheckList.csv?userLoginId=${userLogin.get("userLoginId")}</@ofbizUrl>"> My Card Sale Check List</a>
            </li>
            <#--<li>
   				<a href="<@ofbizUrl>cardSaleReport?userLoginId=${userLogin.get("userLoginId")}&&checkListType=cardsale</@ofbizUrl>" target="_blank">Card Sale Report</a>
            </li>-->
         </ul>
     </div>
    <div class="screenlet-body"> 
    <div>      
      <table width="100%" border="0" cellspacing="0" cellpadding="0">     
      	<tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Sale Location</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext h2'>
             <input type="hidden" size="22" maxlength="60" name="saleLocation" id="saleLocation" value="${saleLocation?if_exists}"/>
            	${saleLocation?if_exists}
            </div>
          </td>
        </tr> 
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Card Period</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <select name="customTimePeriodId" id="customTimePeriodId">
                 <#list timePeriodList as timePeriod>
                    <option value="${timePeriod.customTimePeriodId}"> <#if "${timePeriod.periodName}${timePeriod.fromDate}${timePeriod.thruDate}" == customTimePeriodId?if_exists>selected="selected"></#if>${(timePeriod.get("periodName",locale))?if_exists}:${(timePeriod.get("fromDate",locale))?if_exists}-${(timePeriod.get("thruDate",locale))?if_exists}</option>
                 </#list>
            </select>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.IssuedDate}</div></td>
          <td>&nbsp;</td>
			<td><@htmlTemplate.renderDateTimeField name="orderDate" event="" action="" value="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="orderDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
        </tr>
        
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Counter Number</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
           <div class='tabletext'>
               <input type="text" size="17" maxlength="60" name="counterNumber" id="counterNumber"/>
            </div>
          </td>
        </tr>
        
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Booth}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
           <div class='tabletext'>
               <input type="text" size="22" maxlength="60" name="originFacilityId" id="originFacilityId"/>
            </div>
          </td>
        </tr> 
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Payment Type</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <select name="paymentTypeId" id="paymentTypeId">
                 <#list paymentTypeList as paymentType>
                     <option value="${paymentType.enumId}">${paymentType.description}</option>
                 </#list>
            </select>
          </td>
        </tr>
      </table>
 </div>
<br/>
<form method="post" id="CreateCardSaleForm" action="<@ofbizUrl>processBulkCardSale</@ofbizUrl>">	
	<div class="grid-header" style="width:100%">
		<label>Card Sale</label>
	</div>
		<div id="myGrid1" style="width:100%;height:100px;"></div>
		<br>					
	    <div align="center">
	    	<input type="submit" style="padding:.3em" id="changeSave" value="Save" onclick="javascript:appendParamsToProcessBulkCardSale('CreateCardSaleForm','<@ofbizUrl>processBulkCardSale</@ofbizUrl>');"/>
	    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    	<a href="BulkCardSale" ><input type="button" style="padding:.3em"  value="Cancel"/></a>
	    </div>     
</form>
    </div>
</div> 
<div class="screenlet">
    <div class="screenlet-body">
 		<div class="grid-header" style="width:100%">
			<label>Last Card Entry<#if lastChangeSubProdMap?exists && lastChangeSubProdMap?has_content>[made by ${lastChangeSubProdMap.modifiedBy?if_exists} at ${lastChangeSubProdMap.modificationTime?if_exists}] </#if></label>
		</div>    
		<div id="myGrid2" style="width:100%;height:80px;"></div>		
    </div>
</div>     
 	

 

