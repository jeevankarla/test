<script type="text/javascript">

$(document).ready(function(){
		$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
					
				date = $(this).datepicker('getDate');
	        	var maxDate = new Date(date.getTime());
	        	maxDate.setDate(maxDate.getDate() + 31);
				$( "#thruDate" ).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');
				
	});
</script>
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Period</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="dashboardDates" action="<@ofbizUrl>dashboard</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="left" width="3%"><span class='h3'>From: </span></td>
            	<td align="left" width="15%"><input class='h2' type="text" id="fromDate" name="fromDate" value = "${fromDateStr?if_exists}"/></td>
				<td align="left" width="3%"><span class='h3'>To: </span></td>
				<td align="left" width="15%"><input class='h2' type="text" id="thruDate" name="thruDate" value="${thruDateStr?if_exists}"/></td>
				<td width="10%"><span class='h3'>Product: </span></td>
				<td align="left" width="10%"><@htmlTemplate.lookupField value="${productId?if_exists}" formName="dashboardDates" name="productId" id="productId" fieldFormName="LookupProduct"/></td>
			</tr>
			<tr>
				<td width="10%"><span class='h3'>Ticket Type: </span></td>
				<td width="15%">
					<select name="custRequestTypeId" id="custRequestTypeId" class='h3'>
						<option value="All">All</option>
		            	<#list complaintTypeList as eachComplaint>    
		            	   	<option value='${eachComplaint.custRequestTypeId}'>	${eachComplaint.description}</option>
		                </#list>            
					</select>
			    </td>
				<td width="10%"><span class='h3'>Status: </span></td>
				<td width="15%">
					<select name="statusId" id="statusId" class='h3'>
						<option value="All">All</option>
		            	<#list statusList as eachStatus>    
		            	   	<option value='${eachStatus.statusId}'>	${eachStatus.description}</option>
		                </#list>            
					</select>
			    </td>
			    <td width="10%"><span class='h3'>Severity: </span></td>
				<td width="15%">
					<select name="severity" id="severity" class='h3'>
						<option value="All">All</option>
		            	<#list severityList as severity>    
		            	   	<option value='${severity.statusId}'>	${severity.description}</option>
		                </#list>            
					</select>
			    </td>
			    <td width="10%"><span class='h3'>Project: </span></td>
				<td width="15%">
					<select name="project" id="project" class='h3'>
						<option value="All">All</option>
		            	<#list projectDetails as eachProject>    
		            	   	<option value='${eachProject.enumId}'>	${eachProject.description}</option>
		                </#list>            
					</select>
			    </td>
			    <td><input type="submit" value="Submit" id="getCharts" class="smallSubmit"/></td>
			</tr>
    	</table> 
	</form>
	</div>
</div>

<#if (totalQuantity > 0)>
<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-title-right {
		font-weight: normal;
		text-align: right;		
	}	
	.cell-effort-driven {
		text-align: center;
	}
</style>	
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tickrotor.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tooltip.js"</@ofbizContentUrl>></script>
	
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />
	
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
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

<script type="application/javascript">  

function setupGrid1() {
		var grid;
		var data = [
        <#if custRequestList?exists> 
	        <#list custRequestList as custRequest>				
				{id:"${custRequest.name}" , name:"${custRequest.name}", revenue:"${custRequest.total?string("#0")}"},
			</#list>
			{id:"Total" , name:"Total", revenue:"${totalQuantity?string("#0")}" }			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Ticket Type", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Quantity", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
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
	function setupGrid2() {
		var grid;
		var data = [
        <#if custRequestProdList?exists> 
	        <#list custRequestProdList as custRequestProd>				
				{id:"${custRequestProd.name}" , name:"${custRequestProd.name}", revenue:"${custRequestProd.total?string("#0")}"},
			</#list>
			{id:"Total" , name:"Total", revenue:"${totalQuantity?string("#0")}" }			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Product", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Quantity", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid2", dataView, columns, options);
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

function setupGrid3() {
		var grid;
		var data = [
        <#if custRequestStatusList?exists> 
	        <#list custRequestStatusList as custRequestStatus>				
				{id:"${custRequestStatus.name}" , name:"${custRequestStatus.name}", revenue:"${custRequestStatus.total?string("#0")}"},
			</#list>
			{id:"Total" , name:"Total", revenue:"${totalQuantity?string("#0")}" }			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Status", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Quantity", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid3", dataView, columns, options);
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
	
	function setupGrid4() {
		var grid;
		var data = [
        <#if custRequestStatusList?exists> 
	        <#list custRequestSeverityList as custRequestSeverity>				
				{id:"${custRequestSeverity.name}" , name:"${custRequestSeverity.name}", revenue:"${custRequestSeverity.total?string("#0")}"},
			</#list>
			{id:"Total" , name:"Total", revenue:"${totalQuantity?string("#0")}" }			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Severity", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Quantity", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid4", dataView, columns, options);
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
  var data = [
<#list custRequestList as custRequest>
    {label: "${StringUtil.wrapString(custRequest.name?default(""))}", data: ${custRequest.total?if_exists}}<#if custRequest_has_next>,</#if>
</#list>    
  ];
  var data1 = [
<#list custRequestProdList as custProdRequest>
    {label: "${StringUtil.wrapString(custProdRequest.name?default(""))}", data: ${custProdRequest.total?if_exists}}<#if custProdRequest_has_next>,</#if>
</#list>    
  ];
  var data2 = [
<#list custRequestStatusList as custRequestStatus>
    {label: "${StringUtil.wrapString(custRequestStatus.name?default(""))}", data: ${custRequestStatus.total?if_exists}}<#if custRequestStatus_has_next>,</#if>
</#list>    
  ];
  var data3 = [
<#list custRequestSeverityList as custRequestSeverity>
    {label: "${StringUtil.wrapString(custRequestSeverity.name?default(""))}", data: ${custRequestSeverity.total?if_exists}}<#if custRequestSeverity_has_next>,</#if>
</#list>    
  ];
  
	jQuery.plot($("#chart"), data, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent) +'% (' + series.data[0][1] + ')</div>';
                    },
                    threshold: 0.1
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2% (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-50, 5]
		}
	});  
	jQuery.plot($("#chart1"), data1, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent) +'% (' + series.data[0][1] + ')</div>';
                    },
                    threshold: 0.1
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2% (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-50, 5]
		}
	});
	jQuery.plot($("#chart2"), data2, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent) +'% (' + series.data[0][1] + ')</div>';
                    },
                    threshold: 0.1
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2% (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-50, 5]
		}
	});
	jQuery.plot($("#chart3"), data3, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent) +'% (' + series.data[0][1] + ')</div>';
                    },
                    threshold: 0.1
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2% (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-50, 5]
		}
	});
	setupGrid1();
	setupGrid2();
	setupGrid3();
	setupGrid4();
	
	var data = ${StringUtil.wrapString(statusTypeDataListJSON)};
	
	var options = { 
					series: {
						bars: {
							show: true,
                   			barWidth: 0.2,							
							align: 'center'
						}
					},
					legend: {
    					position: "nw"
  					},
               		grid: { hoverable: true},                 	 
                  	yaxes: [
                      { position: 'left',
                      	min: 0,
 						axisLabel : 'Count' }
                  	],                	 
     				xaxis: {
         				min: 0,
         				max: 10,
         				ticks:${StringUtil.wrapString(labelsJSON)},
         				rotateTicks: 140
     				},
     				tooltip: true,
     				tooltipOpts: {
						content: "%s - %y", // show percentages, rounding to 2 decimal places
						shifts: {
							x: 20,
							y: 0
						},
						defaultTheme: false
					},
     				
     			};
	
	jQuery.plot($("#bar"), data, options);               
});
</script>
	<style type="text/css">
		div.graph
		{
			width: 500px;
			height: 300px;
		}	
		div.graph2
		{
			width: 400px;
			height: 300px;
		}
		div.graph3
		{
			width: 400px;
			height: 300px;
		}
		div.graph4
		{
			width: 400px;
			height: 300px;
		}
		div.graph5
		{
			width: 400px;
			height: 300px;
		}			
		label
		{
			display: block;
			margin-left: 400px;
			padding-left: 1em;
		}
		#flotTip 
		{
			padding: 3px 5px;
			background-color: #000;
			z-index: 100;
			color: #fff;
			box-shadow: 0 0 10px #555;
			opacity: .7;
			filter: alpha(opacity=70);
			border: 2px solid #fff;
			-webkit-border-radius: 4px;
			-moz-border-radius: 4px;
			border-radius: 4px;
		}		
	</style>

<#--<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <#if fromDate?has_content>
      	<h3>Complaints Status Type-wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMM dd, yyyy")}]</h3>
       <#else>
       	<h3>Complaints Status Type-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd?if_exists, "MMM dd, yyyy")}</h3>
      </#if>
    </div>
    <div class="screenlet-body">
   		<div id="bar" class="graph"></div>
   		<br><br>  
    </div>
</div>
</div>-->
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
       <#if fromDate?has_content>
      		<h3>Tickets Type-wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMM dd, yyyy")}]</h3>
       	<#else>
       		<h3>Tickets Type-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd?if_exists, "MMM dd, yyyy")}</h3>
      </#if> 
    </div>
    <div class="screenlet-body">
   		<div id="chart" class="graph2"></div>
   		<br><br>  
   		<div id="myGrid1" style="width:400px;height:250px;margin-left:20px;"></div>
    </div>
</div>
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <#if fromDate?has_content>
      	<h3>Product-wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMM dd, yyyy")}]</h3>
       <#else>
       	<h3>Product-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd?if_exists, "MMM dd, yyyy")}</h3>
      </#if>
    </div>
    <div class="screenlet-body">
   		<div id="chart1" class="graph3"></div>
   		<br><br>  
   		<div id="myGrid2" style="width:400px;height:250px;margin-left:20px;"></div>
    </div>
</div>
</div>
<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <#if fromDate?has_content>
      	<h3>Status wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMM dd, yyyy")}]</h3>
       <#else>
       	<h3>Status-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd?if_exists, "MMM dd, yyyy")}</h3>
      </#if>
    </div>
    <div class="screenlet-body">
   		<div id="chart2" class="graph4"></div>
   		<br><br>  
   		<div id="myGrid3" style="width:400px;height:250px;margin-left:20px;"></div>
    </div>
</div>
</div>	
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <#if fromDate?has_content>
      	<h3>Severity wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate?if_exists, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate?if_exists, "MMM dd, yyyy")}]</h3>
       <#else>
       	<h3>Severity-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateEnd?if_exists, "MMM dd, yyyy")}</h3>
      </#if>
    </div>
    <div class="screenlet-body">
   		<div id="chart3" class="graph5"></div>
   		<br><br>  
   		<div id="myGrid4" style="width:400px;height:250px;margin-left:20px;"></div>
    </div>
</div>
</div>
<#else>
      <p align="center" style="font-size: large;">
        <b>No Tickets Found!</b>
      </p>  
</#if> 		   		


