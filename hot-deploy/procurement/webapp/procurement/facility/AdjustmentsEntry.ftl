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

<script type="application/javascript">
	var shedJson = ${StringUtil.wrapString(shedJson)}	
	var shedUnitTimePeriodsJson = ${StringUtil.wrapString(shedUnitTimePeriodsJson)}
	var unitsList ;
	var timePeriodList;
	var shedTimePeriodList;
	
function getTimePeriodsByUnitValue(shedId,unitId){
		var unitWiseTimePeriods = shedUnitTimePeriodsJson[shedId];
		var unitTimePeriods = unitWiseTimePeriods[unitId];
		var shedTimePeriods =shedUnitTimePeriodsJson[shedId+'_timePeriods'];		
		var unitOptionList = '';
		var shedOptionList = '';
		for(var i=0;i<shedTimePeriods.length;i++){
				shedTimePeriodList += shedTimePeriods[i];
				shedOptionList += "<option value = " + shedTimePeriods[i]['customTimePeriodId'] + " >" + shedTimePeriods[i]['fromDate']+"-"+shedTimePeriods[i]['thruDate']+ "</option>";
			}
		if(unitTimePeriods.length>0){
			for(var i=0; i<unitTimePeriods.length;i++){
				timePeriodList += unitTimePeriods[i];
				unitOptionList += "<option value = " + unitTimePeriods[i]['customTimePeriodId'] + " >" + unitTimePeriods[i]['fromDate']+"-"+unitTimePeriods[i]['thruDate']+ "</option>";
			}
			
		}else{
			timePeriodList = shedTimePeriodList;
			unitOptionList = shedOptionList;
		}
		
		jQuery("[name='"+"customTimePeriodId"+"']").html(unitOptionList);
		<#if timePeriodId?exists>
			jQuery("[name='"+"customTimePeriodId"+"']").val(${timePeriodId});
		</#if>
	}
	function setUnitAndTimePeriod(){
		$('[name=unitCode]').val('');
		$('[name=centerCode]').val('');
		var optionsList={};
		jQuery("[name='"+"customTimePeriodId"+"']").html(optionsList);
		$('span#unitToolTip').html('none');
		$('span#centerToolTip').html('none');
	}
$(document).ready(function() {		
		 if($('[name=unitCode]').val()){
		 		shedValue = $('[name=shedCode]').val();
	  			unitValue = $('[name=unitCode]').val();
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			var tempUnitJson = unitJson[unitValue];
  				getTimePeriodsByUnitValue(shedValue,unitValue);
	  			if(tempUnitJson){
	  				$('span#unitToolTip').addClass("tooltip");
	  				$('span#unitToolTip').removeClass("tooltipWarning");
	  				unitName = tempUnitJson["name"];
	  				$('span#unitToolTip').html(unitName);
	  			}else{
	  				$('span#unitToolTip').removeClass("tooltip");
	  				$('span#unitToolTip').addClass("tooltipWarning");
	  				$('span#unitToolTip').html('Code not found');
	  			}
		 } 	
		$("input").keyup(function(e){
	  		if(e.target.name == "unitCode" ){
	  			shedValue = $('[name=shedCode]').val();
	  			unitValue = e.target.value;
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			var tempUnitJson = unitJson[e.target.value];
	  			
	  			  			
	  			if(tempUnitJson){
	  				$('span#unitToolTip').addClass("tooltip");
	  				$('span#unitToolTip').removeClass("tooltipWarning");
	  				unitName = tempUnitJson["name"];
	  				$('span#unitToolTip').html(unitName);
	  			}else{
	  				$('span#unitToolTip').removeClass("tooltip");
	  				$('span#unitToolTip').addClass("tooltipWarning");
	  				$('span#unitToolTip').html('Code not found');
	  			}
	  			if(!unitValue){
	  				var optionsList = {};
	  				jQuery("[name='"+"customTimePeriodId"+"']").html(optionsList); 
	  			}else{
	  				getTimePeriodsByUnitValue(shedValue,unitValue);
	  			}
	  			
	  				  			
	  		}
	  		
	  		if((e.target.name) == "centerCode"){
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			var tempCenterJson = (unitJson[ $('[name=unitCode]').val() ] )["centers"];
	  			var centerName = tempCenterJson [$('[name=centerCode]').val()];
	  						
	  			if(centerName){
	  				$('span#centerToolTip').removeClass("tooltipWarning");
	  				$('span#centerToolTip').addClass("tooltip");
	  				$('span#centerToolTip').html(centerName);
	  			}else{
	  				$('span#centerToolTip').removeClass("tooltip");
	  				$('span#centerToolTip').addClass("tooltipWarning");
	  				$('span#centerToolTip').html('Code not found');	  
	  			}				
	  		}
	  		
	}); 
	
	
		
	});
	
var recentChange;
	function setUpRecentList(recentChange) {
			recentChange = recentChange;			
			var grid;		
			var data = [			
					{"id":"1", 
						"shed":recentChange['shedCode'],
						"unit":recentChange['unitCode'], 
						"center":recentChange['centerCode'],
						"orderDate":recentChange['orderDate'] ,
					 	"adjustmentType":(recentChange['adjustmentType']),
					 	"amount":recentChange['amount']					
					}				
				
			];		
			var columns = [
				
				{id:"shed", name:"Shed Code", field:"shed", width:50, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"unit", name:"Unit Code", field:"unit", width:50, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"center", name:"Center Code", field:"center", width:70, minWidth:100, cssClass:"cell-title", sortable:false},				
				{id:"orderDate", name:"Order Date", field:"orderDate", width:120, minWidth:100, cssClass:"cell-title", sortable:false},
	        	{id:"adjustmentType", name:"Adjustment Type", field:"adjustmentType", width:150, minWidth:70, cssClass:"cell-title", sortable:false},
	        	{id:"amount", name:"amount", field:"amount", width:80, minWidth:100, sortable:false , editor:FloatCellEditor}	        	
			];	
	
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
			var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid1", dataView, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
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
        
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
		dataView.endUpdate();	
	}	
	
$(document).ready(function(){
		//populate Last change on load
		  var lasAdjChangeJson = ${StringUtil.wrapString(lasAdjChangeJson)}
		   if((lasAdjChangeJson["orderDate"])){
		  	setUpRecentList(lasAdjChangeJson);	
		  }	
	});
	
</script>

<div id="wrapper" style="width: 50%; height:100%;">
		<#if entryAdjType == "Deductions">
		<form method="post" name="AdjustmentsEntry" id="AdjustmentsEntry" action="<@ofbizUrl>adjustmentsDeductionEntry</@ofbizUrl>">  
		<#else>
		<form method="post" name="AdjustmentsEntry" id="AdjustmentsEntry" action="<@ofbizUrl>adjustmentsAdditionEntry</@ofbizUrl>">
		</#if>   
      		<table width="35%" border="0" cellspacing="0" cellpadding="0">
      			<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>shed Code:</div></td>
          			<#if shedId?has_content>
		          		<td  align='left'> <div class='h2'> ${shedName}</div></td>	
		          		<input type="hidden" name="shedCode" id="shedCode" value="${shedCode}">
		          	<#else>
	          			<td  align='left'> 
				      		<select name="shedCode" class='h2' onchange="javascript:setUnitAndTimePeriod();" >
				                <#list shedList as shed>    
				                  	<#assign isDefault = false>
									<option value='${shed.facilityCode}'<#if isDefault> selected="selected"</#if>>
				                    	${shed.facilityName}
				                  	</option>                  	
				      			</#list>            
							</select>
	          			</td>
	          		</#if>	
           			<td>&nbsp;</td>
        		</tr>  
         		<tr>
		        	<td>&nbsp;</td>
		        	<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.unitCode}:</div></td>
		          
		          	<td valign='middle' nowrap="nowrap">
		            	<div class='tabletext h2'>            
		             		<#if unitName?has_content>
             					<h2>${unitCode}     <span class="tooltip">${unitName}</span></h2>
             					<input type="hidden" size="6" maxlength="6" name="unitCode" id="unitCode" autocomplete="off" value="${unitCode}" />
             				<#else>
             					<#if unitMapsList?has_content>
		      						<select name="unitCode" id="unitCode" class='h3'>
		               				 <#list unitMapsList as unit>    
										<#assign isDefault = false>
										<option value='${unit.facilityCode}' <#if isDefault> selected="selected"</#if>>
		                    				${unit.facilityCode} ${unit.facilityName}
		                  				</option>                  	
		      						</#list>            
								</select>
		             		<#else>
		             			<input type="text" size="10" maxlength="15" name="unitCode" id="unitCode" value="${parameters.unitCode?if_exists}" autocomplete="off" required /><em>*</em><span class="tooltip" id ="unitToolTip">none</span>
		             		</#if>
		             	</#if>		              	   	
		           		</div>     
		          	</td>
		         	<td>&nbsp;</td>
		        </tr>
		        <tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Custom Time Period:</div></td>
          
		           	<td  align='left'> 
		      			<select name="customTimePeriodId" class='h2'>      			     			
			                <#list timePeriodList as timePeriod>
			                	<#if !timePeriodId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if timePeriodId?exists>
			                    	<#if timePeriodId == timePeriod.customTimePeriodId>
			      						<option  value="${timePeriodId}" selected="selected">${timePeriod.periodName}</option>
			      						<#else>
			      						<option value='${timePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
			                    			${timePeriod.periodName}
			      					</#if>	
			      					<#else>
			      						<option value='${timePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
			                    			${timePeriod.periodName}
			                  		</option>
			      				</#if>
			      			</#list>            
						</select>
		          	</td>
         			<td>&nbsp;</td>
         		</tr>   
        		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.centerCode}:</div></td>
          
          			<td valign='middle' nowrap="nowrap">
            			<div class='tabletext h2'>            
             				<input type="text" size="10" maxlength="15" name="centerCode" id="centerCode" onfocus="javascript:this.value;"  autocomplete="off" required/><em>*</em><span class="tooltip" id ="centerToolTip">none</span>             	            	
            			</div>
          			</td>
        		</tr>   
         		<#assign orderAdjustmentDesc=adjDescription.get("shedAdjustmentDescriptionMap")>
         		<#list adjustmentTypeList as adjustment>         			
         		<tr>
	         		<td>&nbsp;</td>
	         		<td>
	         			<div class='h2'>${orderAdjustmentDesc[adjustment.orderAdjustmentTypeId]?if_exists} :</div><input name="adjustmentTypeId_o_${adjustment_index}" value="${adjustment.orderAdjustmentTypeId}" type="hidden" size="10"/></input>
	         		</td>
	         		<td>
	         			<input type="text" size="10" maxlength="15" name="amount_o_${adjustment_index}"/></input>
	         		</td>
         		</tr> 
         		</#list>
         			 
        		<#--<tr>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Adjustment Type:</div></td>          
          
          			<td  align='left'> 
      					<select name="adjustmentTypeId" class='h2'>
                			<#list adjustmentTypeList as adjustment>
                				<#if !adjustmentTypeId?exists>    
				                  	<#assign isDefault = false>                  
			                    </#if>
			                    <#if adjustmentTypeId?exists>
			                    	<#if adjustmentTypeId == adjustment.orderAdjustmentTypeId>
			      						<option  value="${adjustmentTypeId}" selected="selected">${adjustment.description}</option>
			      						<#else>
				      					<option value='${adjustment.orderAdjustmentTypeId}'<#if isDefault> selected="selected"</#if>>
	                    					${adjustment.description}
	                  					</option>
			      					</#if>	
			      						<#else>
				      					<option value='${adjustment.orderAdjustmentTypeId}'<#if isDefault> selected="selected"</#if>>
	                    					${adjustment.description}
	                  					</option>
			      				</#if>        				
									                  	
      						</#list>            
						</select>
          			</td>
         			<td>&nbsp;</td>
         		</tr>     
         	     		
         		<tr>
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Amount:</div></td>          
          
          			<td valign='middle' nowrap="nowrap">
            			<div class='tabletext h2'>            
             				<input type="text" size="10" maxlength="15" name="amount" id="amountValue" autocomplete="off" required/>             	            	
            			</div>
          			</td>
        		</tr>       -->     
      		</table>
      		<br/>
      <!-- Submit Button -->
      	<div align="center">
      	 	<table width="35%" border="0" cellspacing="0" cellpadding="0">
        		<tr>       
          			<td>&nbsp;</td>
          			<td align='left' valign='middle' nowrap="nowrap"><div class='h2'> &nbsp;</div></td>
          			<td valign='middle'> 
           				<div class='tabletext h2'>            
             				<#--<input type="submit" class="button" name="submitButton" value="Add" onclick="javascript:adjustmentEntry('AdjustmentsEntry')"> -->  
             				<input type="submit" class="button" name="submitButton" id="submitEntry" value="Add">     	
            			</div>
          			</td>
          			<td valign='middle'>          
          			</td>
         			<td>&nbsp;</td>
        		</tr>             
        		<tr><td><br/></td></tr>
      		</table> 
      	</div>	 
      	 <div name ="displayMsg" id="AdjustmentsEntry_spinner">      
      </div>
 	</form>
</div>
<div id="div2" style="float: left;width: 100%;align:left; border: #F97103 solid 0.1em;" >
    <div>    	
 		<div class="grid-header" style="width:100%">
			<label>Last Change </label>
		</div>    
		<div id="myGrid1" style="width:100%;height:75px;">			
		</div>		
		<div name ="updateEntryMsg" id="updateEntryMsg">      
      </div>		
    </div>
</div>
<script type="text/javascript">
	<#if unitId?has_content>
		getTimePeriodsByUnitValue(${shedCode},${unitId});
	</#if>
</script>

