

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
			
	

<script type="application/javascript">   


	function setupGrid1() {
		var grid;
		var data = [
        <#if productReportList?exists> 
	        <#list productReportList as productReport>				
				{id:"${productReport.name}" , name:"${productReport.name}", totalFat:"${productReport.totalFat}", 
				 totalSnf:"${productReport.totalSnf}", total:"${productReport.total}", 
				 totalRevenue:"${productReport.totalRevenue}"},
			</#list>
			{id:"total" , name:"Total", totalFat:"${totalFat}", totalSnf:"${totalSnf}", total:"${totalQuantity}", totalRevenue:"${totalRevenue}"}			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"product", name:"Product", field:"name", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"fat", name:"Fat (Kgs)", field:"totalFat", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
			{id:"snf", name:"SNF (Kgs)", field:"totalSnf", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
			{id:"quantity", name:"Quantity (litres)", field:"total", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: false,
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
        <#if zoneReportList?exists> 
	        <#list zoneReportList as zoneReport>				
				{id:"${zoneReport.name}" , name:"${zoneReport.name}", total:"${zoneReport.total}",
				 totalRevenue:"${zoneReport.totalRevenue}"},
			</#list>
			{id:"total" , name:"Total", total:"${totalQuantity}", totalRevenue:"${totalRevenue}"},			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"zone", name:"Zone", field:"name", width:150, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"quantity", name:"Quantity (litres)", field:"total", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
			{id:"revenue", name:"Revenue (Rs)", field:"totalRevenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: false,
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
        <#if supplyTypeReportList?exists> 
	        <#list supplyTypeReportList as supplyTypeReport>				
				{id:"${supplyTypeReport.name}" , name:"${supplyTypeReport.name}", total:"${supplyTypeReport.total}",
				 totalRevenue:"${supplyTypeReport.totalRevenue}"},
			</#list>
			{id:"total" , name:"Total", total:"${totalQuantity}", totalRevenue:"${totalRevenue}"},			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"supplyType", name:"Supply Type", field:"name", width:150, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"quantity", name:"Quantity (litres)", field:"total", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
			{id:"revenue", name:"Revenue (Rs)", field:"totalRevenue", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false},	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: false,
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


  var data = [
<#list productReportList as productReport>
    {label: "${StringUtil.wrapString(productReport.name?default(""))}", data: ${productReport.total?if_exists}}<#if productReport_has_next>,</#if>
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
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
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
			content: "%s %p.2%", // show percentages, rounding to 2 decimal places
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
	
 
  var data2 = [
<#list zoneReportList as zoneReport>
    {label: "${StringUtil.wrapString(zoneReport.name?default(""))}", data: ${zoneReport.total?if_exists}}<#if zoneReport_has_next>,</#if>
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
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                    background: { opacity: 0.3 }
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},		
		legend: {
			show: true,
			noColumns: 1,
			margin: [-150, 5]
		}
	});
	
		setupGrid2();
		
            
  var data3 = [
<#list supplyTypeReportList as supplyTypeReport>
    {label: "${StringUtil.wrapString(supplyTypeReport.name?default(""))}", data: ${supplyTypeReport.total?if_exists}}<#if supplyTypeReport_has_next>,</#if>
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
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                    background: { opacity: 0.3 }
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},		
		legend: {
			show: true,
			noColumns: 1,
			margin: [-50, 5]
		}
	});   
	setupGrid3();         

        jQuery(".grid-header .ui-icon")
            .addClass("ui-state-default ui-corner-all")
            .mouseover(function(e) {
                jQuery(e.target).addClass("ui-state-hover")
            })
            .mouseout(function(e) {
                jQuery(e.target).removeClass("ui-state-hover")
            });	
                        
</script>
	<style type="text/css">
		div.graph
		{
			width: 400px;
			height: 300px;
		}
		div.graph2
		{
			width: 450px;
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

<div class="lefthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Product-wise Sales Volume for ${salesDate?date}</h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart" class="graph"></div>
   		<br><br>
  
		<div id="myGrid1" style="width:400px;height:400px"></div>   		
    </div>
</div>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Supply Type-wise Sales Volume for ${salesDate?date}</h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart3" class="graph2"></div>
   		<br><br>
		<div id="myGrid3" style="width:350px;height:300px"></div>   		  
    </div>
</div>
</div>

<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Zone-wise Sales Volume for ${salesDate?date}</h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart2" class="graph"></div>
   		<br><br>
   
		<div id="myGrid2" style="width:400px;height:400px"></div>     		
    </div>
</div>
</div>		
<#else>
      <p align="center" style="font-size: large;">
        <b>No Sales Orders Found!</b>
      </p>  
</#if>
