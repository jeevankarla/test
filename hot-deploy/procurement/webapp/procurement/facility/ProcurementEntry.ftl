
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
	//Decimal formating
	jQuery(function($) {	
		var dateFormat = $('#orderDate').datepicker( "option", "dateFormat" );
		$('#orderDate').datepicker( "option", "dateFormat", "dd-mm-yy" );
		//$('input.money').autoNumeric({aNeg: '-'}).trigger('focusout'); 
		//$('#fat').autoNumeric('init',{aSep: '.',  mDec:functionName});
       <#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableQtyDecimalEntry?has_content) && (tenantConfigCondition.enableQtyDecimalEntry = 'Y')>
			$('#quantity').autoNumeric({mDec: 1 ,mRound: 'A' , mDecNum:'0|5' ,	aSep:'' , autoTab : true}).trigger('focusout');
		<#else>
		    $('#quantity').autoNumeric({mDec: 1 ,mRound: 'A' ,	aSep:'' , autoTab : true}).trigger('focusout');
		</#if>
		$('#qtyLtrs').autoNumeric({mDec: 2 , autoTab : true}).trigger('focusout');
		$('#fat').autoNumeric({mNum: 2,mDec: 1 , autoTab : true}).trigger('focusout');
		$('#snf').autoNumeric({mNum: 2,mDec: 2}).trigger('focusout');
		$('#sFat').autoNumeric({mNum: 2,mDec: 1, autoTab : true}).trigger('focusout');
		$('#sQuantity').autoNumeric({mDec: 1 , autoTab : true}).trigger('focusout');
		$('#cQuantity').autoNumeric({mDec: 1 , autoTab : true}).trigger('focusout');
		$('#ptcQuantity').autoNumeric({mDec: 1 , autoTab : true}).trigger('focusout');
		$('#lactoReading').autoNumeric({mNum: 2,mDec: 1}).trigger('focusout');
		
			
	});




	$(document).ready(function() {
	
		/*$(':input').keypress(function(e) {
		var focusables = $(":focusable");
		var current = focusables.index(this);
		var curentElName = focusables.eq(current).attr("name");
		var activeFlag = $("#accordion" ).accordion("option","active");
		if (e.which == 13) {			
			if(curentElName != "submitButton"){	
					next = focusables.eq(current+1).length ? focusables.eq(current+1) : focusables.eq(0);
					next.focus();					 		
			}
			
			
		}
	 }); */
	 
	 
    $(':input').autotab_magic();	
	
	$('#snf').autotab({ target: 'submitEntry', previous: 'fat'});
	
    // OR

   // $('#area_code, #number1, #number2').autotab_magic();

});
	var shedJson = ${StringUtil.wrapString(shedJson)}
	var productBrandNameJson = ${StringUtil.wrapString(productBrandNameJson)}	
	var recentChnage;
	function setUpRecentList(recentChnage) {
			recentChnage = recentChnage;
			orderDate = (new Date((recentChnage["estimatedDeliveryDate"])["time"])).toString('yyyy-MM-dd');	
			var grid;		
			var data = [			
					{"id":"1","orderDate":orderDate ,"purchaseTime":recentChnage['purchaseTime'], "unit":recentChnage['unitCode'], 
						"center":recentChnage['centerCode'], "productId":productBrandNameJson[recentChnage['productId']] ,
					 	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableQuantityInLtrs?has_content) && (tenantConfigCondition.enableQuantityInLtrs != 'N')>
					 		"qtyLtrs":(recentChnage['qtyLtrs'])
					 	<#else>
							"quantity":recentChnage['quantity']											 	 
					 	</#if>
					 	,"fat":recentChnage['fat'], 
					 	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableLR?has_content)&& (tenantConfigCondition.enableLR != 'N')>
	       			   		"lactoReading":recentChnage['lactoReading'],
	       				<#else>
	       					"snf":recentChnage['snf'], 
	        			</#if>
					 	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableSQuantityInKgs?has_content) && (tenantConfigCondition.enableSQuantityInKgs != 'N')>
					 		"sQuantity":recentChnage['sQuantityLtrs']
					 	<#else>
					 			"sQtyKgs":(recentChnage['sQtyKgs'])
					 	</#if>
					 	
					 	
					 	, "sFat":recentChnage['sFat'], "cQuantity":recentChnage['cQuantityLtrs'], "ptcQuantity":recentChnage['ptcQuantity'] ,"orderId":recentChnage['orderId'] ,
					 	"orderItemSeqId":recentChnage['orderItemSeqId']						
					}				
				
			];		
			var columns = [
				{id:"orderDate", name:"Proc Date", field:"orderDate", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"purchaseTime", name:"Purchase Time", field:"purchaseTime", width:70, minWidth:70, cssClass:"cell-title", sortable:false},
				{id:"unit", name:"Unit Code", field:"unit", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
				{id:"center", name:"Center Code", field:"center", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
	        	{id:"productId", name:"Milk Type", field:"productId", width:70, minWidth:70, cssClass:"cell-title", sortable:false},
	        	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableQuantityInLtrs?has_content)&&(tenantConfigCondition.enableQuantityInLtrs != 'N')>
	        	{id:"qtyLtrs", name:"Quantity-Ltrs", field:"qtyLtrs", width:100, minWidth:100, sortable:false , editor:FloatCellEditor},
	        	<#else>
				{id:"quantity", name:"Quantity", field:"quantity", width:100, minWidth:100, sortable:false , editor:FloatCellEditor},	        		
	        	</#if>
	        	{id:"fat", name:"Fat", field:"fat", width:70, minWidth:70, sortable:false , editor:FloatCellEditor},
	        	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableLR?has_content)&& (tenantConfigCondition.enableLR != 'N')>
	       			{id:"lactoReading", name:"lactoReading", field:"lactoReading", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor},
	       			<#else>
	       			{id:"snf", name:"Snf", field:"snf", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor},
	        	</#if>
	        	<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableSQuantityInKgs?has_content) && (tenantConfigCondition.enableSQuantityInKgs != 'N')>
	        		{id:"sQuantity", name:"sQuantity", field:"sQuantity", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor}
	        	<#else>
	        		{id:"sQtyKgs", name:"sQty-Kgs", field:"sQtyKgs", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor}
	        	</#if>
	        	,
	        	{id:"sFat", name:"sFat", field:"sFat", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor},
	        	{id:"cQuantity", name:"cQuantity", field:"cQuantity", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor},
	        	{id:"ptcQuantity", name:"ptcQuantity", field:"ptcQuantity", width:70, minWidth:70,  sortable:false ,editor:FloatCellEditor}
			];
	
			columns.push({id:"button", name:"", field:"button", width:70, minWidth:70, cssClass:"cell-title",
			 			formatter: function (row, cell, id, def, datactx) { 
			 				if (dataView2.getItem(row).title != "New") {
        						return '<a href="#" class="button" onclick="editClickHandler('+row+')">Edit</a>'; 
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
			
		changeDate = (new Date((recentChnage["changeDatetime"])["time"])).toString('dd-MM-yyyy HH:mm:ss');
		
		$('div.grid-header')
	  .html('<label>Last Change [made by  ' + recentChnage["changeByUserLoginId"] + '   at    ' + changeDate + ' ]</label>');
	
	}
		
	
	
	
	$(document).ready(function() {
		$('#ptcQuantity').parent().parent().parent().hide();
		$('[name = ptcMilkType]').parent().parent().parent().hide();
		//populate Last change on load
		  var recentChangeJson = ${StringUtil.wrapString(recentChangeJson)}
		  if((recentChangeJson["orderDate"])){
		  	setUpRecentList(recentChangeJson);
		  }	 
		  
		  	
		$("input").keyup(function(e){
	  		if(e.target.name == "unitCode" ){
				var unitJson = shedJson[$('[name=shedCode]').val()];
	  			$('[name=unitCode]').val(e.target.value.toUpperCase());  
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
	  		}
	  		
	  		if((e.target.name) == "centerCode"){
	  			var unitJson = shedJson[$('[name=shedCode]').val()];
	  			$('input[name=centerCode]').val(e.target.value.toUpperCase());
	  			var tempCenterJson = (unitJson[ $('[name=unitCode]').val()] )["centers"];
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
	  		if($('#updateFlag').val()=='update'){
	  		  if(((e.target.name) == "shedCode")||((e.target.name) == "centerCode")||((e.target.name) == "unitCode")||((e.target.name) == "purchaseTime")||((e.target.name) == "productId")||((e.target.name) == "orderDate")){
	  			var fetchRecord = 'false';
	  			if($('#editRecord').val()=='true'){
	  				if((e.which == 27)){
	  					clearEditEntryFields();	  			
		  				fetchProurementEntry();
	  				}
	  			}else{
	  				clearEditEntryFields();	  			
		  			fetchProurementEntry();
	  			}
	  		}
	  		  
	  	}
	  		
	}); 
	
	
		
	});
	
function validateFatSnfValue(prodId,fat,snf){	
	var validateRule = ${StringUtil.wrapString(prodJson)}
	if((fat>validateRule[prodId]['maxFat'])||(fat<=0)){
		alert(validateRule[prodId]['brandName']+' fat should be between'+validateRule[prodId]['minFat']+'-'+validateRule[prodId]['maxFat']);		
		return false;
	}
	if((snf>validateRule[prodId]['maxSnf'])||(snf<=0)){
		alert(validateRule[prodId]['brandName']+' snf should be between'+validateRule[prodId]['minSnf']+'-'+validateRule[prodId]['maxSnf']);
		return false;
	}
	return true;	
}	
function prepareFechRecentChangeParameters(){
		var dataJson;
		var tempShedId;
		var tempUnitId;
		<#if shedCode?has_content>
			tempShedId = '${shedId}';
		</#if>
		<#if unitCode?has_content>
			tempUnitId = '${unitId}';
		</#if>
		<#if shedCode?has_content && unitCode?has_content>
			dataJson  = {"shedId":tempShedId,
							"unitId":tempUnitId,
						   };
		<#else>
	 		<#if shedCode?has_content>
				dataJson  = {"shedId":tempShedId,
						   };
			</#if>
		</#if>
		fetchRecentChange(dataJson);
		
}
$(window).load(function() 
{  
	 prepareFechRecentChangeParameters();
 	
});  
function clearEditEntryFields(){
	$('[name=orderId]').val('');
	$('[name=orderItemSeqId]').val('');
	$('[name=fat]').val('');
	$('[name=snf]').val('');
	$('[name=quantity]').val('');
	$('[name=cQuantity]').val('');
	$('[name=sQuantity]').val('');
	$('[name=sQtyKgs]').val('');
	$('[name=sFat]').val('');
	$('[name=ptcQuantity]').val('');
	$('[name=lactoReading]').val('');
	$('#sourMilk').attr('checked', false);
	$('#curdMilk').attr('checked', false);
}
	
	
function editClickHandler(row) {
		updateProcurementEntryInternal('updateProcurementEntry', '<@ofbizUrl>updateProcurementEntryAjax</@ofbizUrl>', row);
	}	
	


function updateProcurementEntryInternal(formName, action, row) {
		if (Slick.GlobalEditorLock.isActive() && !Slick.GlobalEditorLock.commitCurrentEdit()) {
			return false;		
		} 
		
		var formId = "#" + formName;
									
		var changeItem = dataView2.getItem(row);			
		var rowCount = 0;
		for (key in changeItem){			
			var value = changeItem[key];	
	 		if (key != "id") {
	 			var tempValue = value;
	 			if (key == "lactoReading") {
			   		if((Number(tempValue)>30)){
				  		 alert('LR  should be less than 31');
				  		 return false;				
   						}
   					}	 			 		
   			}
   			 
		}
		for (key in changeItem){			
			var value = changeItem[key];	
	 		if (key != "id") {
				var inputParam = jQuery("<input>").attr("type", "hidden").attr("name", key).val(value);										
				jQuery(formId).append(jQuery(inputParam));				
				 				
   			}
		}
		// lets make the ajaxform submit
			
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
            	   $('div#updateEntryMsg').html('<label>'+ result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]+'</label>');
            	     
               }else{
               		$("div#updateEntryMsg").fadeIn();               	         	   
            	   $('div#updateEntryMsg').html(); 
            	    $('div#updateEntryMsg').removeClass("errorMessage");           	 
            	   $('div#updateEntryMsg').addClass("messageStr");
            	   $('div#updateEntryMsg').html('<label>succesfully updated.</label>'); 
            	   $('div#updateEntryMsg').delay(5000).fadeOut('slow');  
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
	
 <div style="float: left;width: 30%; background:transparent;border: #F97103 solid 0.1em; valign:middle">
 	<div class="screenlet">
 	<div class="screenlet-title-bar">
        <ul>
   			<li>
   				<a href="<@ofbizUrl>procurementCheckList.pdf?userLoginId=${userLogin.get("userLoginId")}&&all=Y&&shedId=<#if shedId?has_content>${shedId}</#if>&&unitId=<#if unitId?has_content>${unitId}</#if></@ofbizUrl>" >All Check List</a>
            </li>  
   			<li>
   				<a href="<@ofbizUrl>procurementCheckList.pdf?userLoginId=${userLogin.get("userLoginId")}&&shedId=<#if shedId?has_content>${shedId}</#if>&&unitId=<#if unitId?has_content>${unitId}</#if></@ofbizUrl>">My Check List</a>
            </li>                      
         </ul>
     </div>
   </div>  
 	<form method="post" name="ProcurementEntry" id="ProcurementEntry">
 	  <input type="hidden" name="retainCenterCode" id="retainCenterCode" value="<#if (tenantConfigCondition?has_content)> ${(tenantConfigCondition.enableCenterCodeRetain)?if_exists} </#if>">
 	  <input type="hidden" name="updateFlag" id="updateFlag" value="${updateFlag}">
      <input type="hidden" name="editRecord" id="editRecord" value="${editRecord}">
      <input type="hidden" size="10" maxlength="15" name="orderId" id="orderId" autocomplete="off" />
      <input type="hidden" size="10" maxlength="15" name="orderItemSeqId" id="orderItemSeqId" autocomplete="off"/>
      <table width="35%" border="0" cellspacing="0" cellpadding="0">
      	<tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>shed Code:</div></td>
          <#if shedId?has_content>
          	<td  align='left'> <div class='h2'> ${shedName}</div></td>	
          	<input type="hidden" name="shedCode" id="shedCode" value="${shedCode}">
          <#else>
          <td  align='left'> 
      		<select name="shedCode" class='h2'>
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
            	<#if unitCode?has_content>
             		<h1>${unitCode}     <span class="tooltip">${unitName}</span></h1>
             		<input type="hidden" size="6" maxlength="6" name="unitCode" id="unitCode" autocomplete="off" value="${unitCode}" />
             	<#else>
             		<#if unitMapsList?has_content>
		      		<select name="unitCode" id="unitCode" class="h3">
		                <#list unitMapsList as unit>    
								<#assign isDefault = false>
								<option value='${unit.facilityCode}' <#if isDefault> selected="selected"</#if>>
		                    	${unit.facilityCode}	${unit.facilityName}
		                  	</option>                  	
		      			</#list>            
					</select>
             		<#else>
             			<input type="text" size="6" maxlength="6" name="unitCode" id="unitCode" autocomplete="off" required /><em>*</em><span class="tooltip" id ="unitToolTip">none</span>
             		</#if>
             	</#if>              	   	
            </div>     
          </td>
         <td>&nbsp;</td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Procurement Date:</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="text" size="10" maxlength="15" name="orderDate" id="orderDate" autocomplete="off" required/><em>*</em>          	
            </div>
          </td>
         <td>&nbsp;</td></tr>            
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Purchase Time:</div></td>
          
          <td valign='middle'> 
      		<select name="purchaseTime" class='h2'>
                <#list purchaseTimeList as purchaseTime>    
                  	<#assign isDefault = false>
					<option value='${purchaseTime.enumId}'<#if isDefault> selected="selected"</#if>>
                    	${purchaseTime.description}
                  	</option>                  	
      			</#list>            
			</select>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Milk Type:</div></td>
          
          <td valign='middle'> 
      		<select name="productId" class='h2' id="productId">
                <#list productList as product>    
                  	<#assign isDefault = false>
					<option value='${product.productId}'<#if isDefault> selected="selected"</#if>>
                    	${product.productName}
                  	</option>                  	
      			</#list>            
			</select>
          </td>
        </tr>
         <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.centerCode}:</div></td>
          
          <td valign='middle' nowrap="nowrap">
            <div class='tabletext h2'>            
             	<input type="text" size="8" maxlength="6" name="centerCode" id="centerCode" autocomplete="off" required/><em>*</em><span class="tooltip" id ="centerToolTip">none</span><#if (editRecord?has_content) && (editRecord=='true')><span class="tooltip" size="8"><b>Press ESC to Get the Record</b></span></#if>             	            	
            </div>
          </td>
        </tr>
        <div>
         <tr>
          <td>&nbsp;</td>
          <#if  (tenantConfigCondition?has_content) && (tenantConfigCondition.enableQuantityInLtrs?has_content)&& (tenantConfigCondition.enableQuantityInLtrs != 'N')>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Good Milk Qty(Ltrs):</div></td>
	          <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="8" maxlength="6" name="qtyLtrs" id="qtyLtrs" autocomplete="off" required/><em>*</em>           	
	            </div>
	          </td>
          <#else>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Good Milk Qty(Kgs):</div></td>
	          <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="8" maxlength="9" name="quantity" id="quantity" autocomplete="off" required/><em>*</em>           	
	            </div>
	          </td>
          </#if>
         <td>&nbsp;</td></tr>
        </div>
         <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Good Milk FAT:</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="text" size="4" maxlength="4" name="fat" id="fat" autocomplete="off" required/><em>*</em>           	
            </div>
          </td>
         <td>&nbsp;</td>
         </tr>
         <tr>
		<td>&nbsp;</td>
		<#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableLR?has_content)&& tenantConfigCondition.enableLR != 'N'>
			<td align='left' valign='middle' nowrap="nowrap" ><div class='h2'>MILK LR:</div></td>
	        <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="6" maxlength="5" name="lactoReading" id="lactoReading" autocomplete="off" required/><em>*</em>           	
	            </div>
	        </td>          
        <#else>
	       	<td align='left' valign='middle' nowrap="nowrap" ><div class='h2'>Good Milk SNF:</div></td>
	        <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="5" maxlength="5" name="snf" id="snf" autocomplete="off" required/><em>*</em>           	
	            </div>
	        </td>
        </#if>
         <td>&nbsp;</td>
         </tr>   
      </table>
      <!-- Sour/PTC Quantity  accordion  --> 
      <div id="accordion">      	
      	<h3><p style="font-weight: bold;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sour/PTC Quantity</p></h3>
    	<div>
      	<table width="35%" border="0" cellspacing="0" cellpadding="0">
      	
        <tr>
          <td>&nbsp;</td>
          <#if (tenantConfigCondition?has_content) && (tenantConfigCondition.enableSQuantityInKgs?has_content) && (tenantConfigCondition.enableSQuantityInKgs != 'N')>
	          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Sour Milk Qty(Ltrs):</div></td>
	          
	          <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="6" maxlength="5" name="sQuantity" id="sQuantity" autocomplete="off"/>           	
	            </div>
	          </td>
          <#else>
          	  <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Sour Milk Qty(Kgs):</div></td>
	          <td valign='middle'>
	            <div class='tabletext h2'>            
	             	<input type="text" size="6" maxlength="5" name="sQtyKgs" id="sQtyKgs" autocomplete="off"/>           	
	            </div>
	          </td>
          </#if>
         <td>&nbsp;</td></tr>
       
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Sour Milk FAT:</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="text" size="4" maxlength="4" name="sFat" id="sFat" autocomplete="off"/>           	
            </div>
          </td>
         <td>&nbsp;</td></tr>
        
       <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Curd Quantity(Ltrs):</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="text" size="6" maxlength="5" name="cQuantity" id="cQuantity" autocomplete="off"/>           	
            </div>
          </td>
         <td>&nbsp;</td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>PTC Recover Qty(Kgs):</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="text" size="6" maxlength="5" name="ptcQuantity" id="ptcQuantity" autocomplete="off"/>           	
            </div>
          </td>
        </tr>
       
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'> Sour/Curd :</div></td>
          
          <td valign='middle'>
            <div class='tabletext h2'>            
             	<input type="radio" name="ptcMilkType" id="sourMilk" value="S"/> sour milk   <input type="radio" name="ptcMilkType" id="curdMilk" value="C"/> curd milk        	
            </div>
          </td>
         <td>&nbsp;</td>          
       </tr>       
      </table>
      </div> 
      </div>
      <br/>
      <!-- Submit Button -->
      <div align="center">
      	 <table width="35%" border="0" cellspacing="0" cellpadding="0">
        <tr>
       
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'> &nbsp;</div></td>
          <td valign='middle'>
            
           <div class='tabletext h2'>            
             	<input type="submit" class="button" name="submitButton" id="submitEntry" <#if updateFlag !="update">value="Add"<#else>value="Update"</#if> >        	
            </div>
            
            
          </td>
          <td valign='middle'>
          
          </td>
         <td>&nbsp;</td></tr>             
        <tr><td><br/></td></tr>
      </table>
      </div>    
      <div name ="displayMsg" id="ProcurementEntry_spinner">      
      </div>
 </form>

</div>	
 <form method="post" name="updateProcurementEntry" id="updateProcurementEntry"> 
 </form>
</div>
       
<div id="div2" style="float: right;width: 65%;align:right; border: #F97103 solid 0.1em;" >
    <div>    	
 		<div class="grid-header" style="width:100%">
			<label>Last Change </label>
		</div>    
		<div id="myGrid2" style="width:100%;height:75px;">
			
		</div>
		<div name ="updateEntryMsg" id="updateEntryMsg">      
      </div>		
    </div>
</div>     
 
</div>