

<#if (channelSize>0 || prodSize>0)>
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jquery-1.6.1.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tooltip.js"</@ofbizContentUrl>></script>
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
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
        <#if channelReportList?exists> 
	        <#list channelReportList as channelReport>				
				{id:"${channelReport.name}" , name:"${channelReport.name}", revenue:"${channelReport.revenue?string("#0")}"},
			</#list>
			{id:"Total" , name:"Total", revenue:"${totalRevenue?string("#0")}" }			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Channel", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Revenue (Rs)", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
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
        <#if productReportList?exists> 
	        <#list productReportList as prodReport>	
	        	{id:"${prodReport.name}" , name:"${prodReport.name}", quantity:"${prodReport.quantity?string("#0")}" , revenue:"${prodReport.revenue?string("#0")}"} ,
			</#list>
			{id:"Total" , name:"Total", quantity:"" ,revenue:"${totalRevenue?string("#0")}"}
		</#if>		
		];
		var dataView;
		
		var columns = [
			{id:"product", name:"PRODUCT", field:"name", width:100, minWidth:100, cssClass:"cell-title", sortable:false},	
			{id:"quantity", name:"QUANTITY (Ltr/Kgs)", field:"quantity", width:140, minWidth:140, cssClass:"cell-title-right", sortable:false},
			{id:"revenue", name:"Revenue (Rs)", field:"revenue", width:140, minWidth:1400, cssClass:"cell-title-right", sortable:false}	
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
        <#if productCatReportList?exists> 
	        <#list productCatReportList as productCatReport>	
	        	{id:"${productCatReport.name}" , name:"${productCatReport.name}", quantity:"${productCatReport.quantity?string("#0")}" , revenue:"${productCatReport.revenue?string("#0")}"} ,
			</#list>
			{id:"Total" , name:"Total", quantity:"" ,revenue:"${totalRevenue?string("#0")}"}
		</#if>		
		];
		var dataView;
		
		var columns = [
			{id:"productCat", name:"Product Category", field:"name", width:100, minWidth:100, cssClass:"cell-title", sortable:false},	
			{id:"quantity", name:"QUANTITY (Ltr/Kgs)", field:"quantity", width:140, minWidth:140, cssClass:"cell-title-right", sortable:false},			
			{id:"revenue", name:"Revenue (Rs)", field:"revenue", width:140, minWidth:140, cssClass:"cell-title-right", sortable:false}	
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
   
   //Region chart 
   
  function setupGrid4() {
		var grid;
		var data = [
        <#if regionReportList?exists> 
	        <#list regionReportList as regionReport>	
	        	{id:"${regionReport.name}" , name:"${regionReport.name}", revenue:"${regionReport.revenue?string("#0")}"} ,
			</#list>
			{id:"Total" , name:"Total" ,revenue:"${totalRevenue?string("#0")}"}
		</#if>		
		];
		var dataView;
		
		var columns = [
			{id:"regionName", name:"Region Name", field:"name", width:100, minWidth:100, cssClass:"cell-title", sortable:false},	
			{id:"revenue", name:"Revenue (Rs)", field:"revenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
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
<#list channelReportList as channelReport>
    {label: "${StringUtil.wrapString(channelReport.name?default(""))}", data: ${channelReport.revenue?if_exists}}<#if channelReport_has_next>,</#if>
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
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
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
			content: "%s %p.2%  (Rs %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-220, 120]
		}
	});
	
		setupGrid1();
		
		
	var data2 = [
<#list productReportList as prodReport>
    {label: "${StringUtil.wrapString(prodReport.name?default(""))}", data: ${prodReport.revenue?if_exists}}<#if prodReport_has_next>,</#if>
</#list>    
  ];            
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
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%   (Rs %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},			
		legend: {
			show: true,
			noColumns: 3,
			margin: [-240, -10]
		}
	});
	
		setupGrid2();

	var data3 = [
<#list productCatReportList as prodCatReport>
    {label: "${StringUtil.wrapString(prodCatReport.name?default(""))}", data: ${prodCatReport.revenue?if_exists}}<#if prodCatReport_has_next>,</#if>
</#list>    
  ];            
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
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%   (Rs %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},			
		legend: {
			show: true,
			noColumns: 1,
			margin: [-200, 5]
		}
	});
	
		setupGrid3();
	

// Region Chart 
	var data4 = [
<#list regionReportList as regionReport>
    {label: "${StringUtil.wrapString(regionReport.name?default(""))}", data: ${regionReport.revenue?if_exists}}<#if regionReport_has_next>,</#if>
</#list>    
  ];            
	jQuery.plot($("#chart4"), data4, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%   (Rs %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},			
		legend: {
			show: true,
			noColumns: 1,
			margin: [-200, 5]
		}
	});
	
		setupGrid4();


        jQuery(".grid-header .ui-icon")
            .addClass("ui-state-default ui-corner-all")
            .mouseover(function(e) {
                jQuery(e.target).addClass("ui-state-hover")
            })
            .mouseout(function(e) {
                jQuery(e.target).removeClass("ui-state-hover")
            });	 
                       
});
</script>
	<style type="text/css">
		div.graph
		{
			width: 300px;
			height: 300px;
		}
		label
		{
			display: block;
			margin-left: 400px;
			padding-left: 1em;
		}
		
	</style>

<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><#if chartType="SalesMix">Channel-wise Sales (${froDate?date} - ${toDate?date}) <#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if><#else>Parlour Despatch Totals (${froDate?date} - ${toDate?date}) <#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if></#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid1" style="width:400px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><#if chartType="SalesMix">Product-wise Sales (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if><#else>Product-wise Parlour Despatch (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if></#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart2" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid2" style="width:400px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><#if chartType="SalesMix">Product Category-wise Sales (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if><#else>Product Category-wise Parlour Despatch (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if></#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart3" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid3" style="width:400px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3><#if chartType="SalesMix">Region-wise Sales (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if><#else>Region-wise Parlour Despatch (${froDate?date} - ${toDate?date})<#if facilityId?exists> For Facility : ${facilityId} ,</#if><#if productId?exists> For Product : ${productId} </#if></#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart4" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid4" style="width:400px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>
</div>
<#else>
  <h3>No orders found for ${froDate?date} - ${toDate?date}!</h3>
</#if>
