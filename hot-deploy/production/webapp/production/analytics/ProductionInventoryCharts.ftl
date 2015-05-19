
<link rel="stylesheet" href="<@ofbizContentUrl>/images/amcharts/style.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
        		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/amcharts/amcharts.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/amcharts/serial.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
    
            var chart;
			var data = ${StringUtil.wrapString(categorySiloJSON)!'[]'};
			var chartData = data["PASTURIZED_MILK"];
			var chartData1 = data["RAW_MILK"];
			var chartData2 = data["BUTTER"];
			var chartData3 = data["CREAM"];
			var chartData4 = data["CURD"];
			var chartData5 = data["POWDER"];
						
            AmCharts.ready(function () {
                // SERIAL CHART
                chart = new AmCharts.AmSerialChart();
                chart.dataProvider = chartData;
                chart.categoryField = "facility";
                chart.startDuration = 1;
                chart.depth3D = 50;
                chart.angle = 30;
                chart.marginRight = -45;
                
                chart1 = new AmCharts.AmSerialChart();
                chart1.dataProvider = chartData1;
                chart1.categoryField = "facility";
                chart1.startDuration = 1;
                chart1.depth3D = 50;
                chart1.angle = 30;
                chart1.marginRight = -45;
                
                chart2 = new AmCharts.AmSerialChart();
                chart2.dataProvider = chartData2;
                chart2.categoryField = "facility";
                chart2.startDuration = 1;
                chart2.depth3D = 50;
                chart2.angle = 30;
                chart2.marginRight = -45;
                
                chart3 = new AmCharts.AmSerialChart();
                chart3.dataProvider = chartData3;
                chart3.categoryField = "facility";
                chart3.startDuration = 1;
                chart3.depth3D = 50;
                chart3.angle = 30;
                chart3.marginRight = -45;
                
                chart4 = new AmCharts.AmSerialChart();
                chart4.dataProvider = chartData4;
                chart4.categoryField = "facility";
                chart4.startDuration = 1;
                chart4.depth3D = 50;
                chart4.angle = 30;
                chart4.marginRight = -45;
                
                chart5 = new AmCharts.AmSerialChart();
                chart5.dataProvider = chartData5;
                chart5.categoryField = "facility";
                chart5.startDuration = 1;
                chart5.depth3D = 50;
                chart5.angle = 30;
                chart5.marginRight = -45;
                
                // AXES
                // category
                var categoryAxis = chart.categoryAxis;
                categoryAxis.gridAlpha = 0;
                categoryAxis.axisAlpha = 0;
                categoryAxis.gridPosition = "start";
				
				var categoryAxis1 = chart1.categoryAxis;
                categoryAxis1.gridAlpha = 0;
                categoryAxis1.axisAlpha = 0;
                categoryAxis1.gridPosition = "start";
				
				var categoryAxis2 = chart2.categoryAxis;
                categoryAxis2.gridAlpha = 0;
                categoryAxis2.axisAlpha = 0;
                categoryAxis2.gridPosition = "start";
				
				var categoryAxis3 = chart3.categoryAxis;
                categoryAxis3.gridAlpha = 0;
                categoryAxis3.axisAlpha = 0;
                categoryAxis3.gridPosition = "start";
				
				var categoryAxis4 = chart4.categoryAxis;
                categoryAxis4.gridAlpha = 0;
                categoryAxis4.axisAlpha = 0;
                categoryAxis4.gridPosition = "start";
				
				var categoryAxis5 = chart5.categoryAxis;
                categoryAxis5.gridAlpha = 0;
                categoryAxis5.axisAlpha = 0;
                categoryAxis5.gridPosition = "start";
				
                // value
                var valueAxis = new AmCharts.ValueAxis();
                valueAxis.axisAlpha = 0;
                valueAxis.gridAlpha = 0;
                chart.addValueAxis(valueAxis);
				
				var valueAxis1 = new AmCharts.ValueAxis();
                valueAxis1.axisAlpha = 0;
                valueAxis1.gridAlpha = 0;
                chart1.addValueAxis(valueAxis1);
				
				var valueAxis2 = new AmCharts.ValueAxis();
                valueAxis2.axisAlpha = 0;
                valueAxis2.gridAlpha = 0;
                chart2.addValueAxis(valueAxis2);
				
				var valueAxis3 = new AmCharts.ValueAxis();
                valueAxis3.axisAlpha = 0;
                valueAxis3.gridAlpha = 0;
                chart3.addValueAxis(valueAxis3);
				
				var valueAxis4 = new AmCharts.ValueAxis();
                valueAxis4.axisAlpha = 0;
                valueAxis4.gridAlpha = 0;
                chart4.addValueAxis(valueAxis4);
				
				var valueAxis5 = new AmCharts.ValueAxis();
                valueAxis5.axisAlpha = 0;
                valueAxis5.gridAlpha = 0;
                chart5.addValueAxis(valueAxis5);
				
                // GRAPH
                var graph = new AmCharts.AmGraph();
                graph.valueField = "quantity";
                graph.colorField = "color";
                graph.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph.type = "column";
                graph.lineAlpha = 0.5;
                graph.lineColor = "#FFFFFF";
                graph.topRadius = 1;
                graph.fillAlphas = 0.9;
                chart.addGraph(graph);
				
				var graph1 = new AmCharts.AmGraph();
                graph1.valueField = "quantity";
                graph1.colorField = "color";
                graph1.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph1.type = "column";
                graph1.lineAlpha = 0.5;
                graph1.lineColor = "#FFFFFF";
                graph1.topRadius = 1;
                graph1.fillAlphas = 0.9;
                chart1.addGraph(graph1);
				
				var graph2 = new AmCharts.AmGraph();
                graph2.valueField = "quantity";
                graph2.colorField = "color";
                graph2.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph2.type = "column";
                graph2.lineAlpha = 0.5;
                graph2.lineColor = "#FFFFFF";
                graph2.topRadius = 1;
                graph2.fillAlphas = 0.9;
                chart2.addGraph(graph2);
				
				var graph3 = new AmCharts.AmGraph();
                graph3.valueField = "quantity";
                graph3.colorField = "color";
                graph3.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph3.type = "column";
                graph3.lineAlpha = 0.5;
                graph3.lineColor = "#FFFFFF";
                graph3.topRadius = 1;
                graph3.fillAlphas = 0.9;
                chart3.addGraph(graph3);
				
				var graph4 = new AmCharts.AmGraph();
                graph4.valueField = "quantity";
                graph4.colorField = "color";
                graph4.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph4.type = "column";
                graph4.lineAlpha = 0.5;
                graph4.lineColor = "#FFFFFF";
                graph4.topRadius = 1;
                graph4.fillAlphas = 0.9;
                chart4.addGraph(graph4);
				
				var graph5 = new AmCharts.AmGraph();
                graph5.valueField = "quantity";
                graph5.colorField = "color";
                graph5.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[product]] </br>Capacity: [[capacity]] </b>";
                graph5.type = "column";
                graph5.lineAlpha = 0.5;
                graph5.lineColor = "#FFFFFF";
                graph5.topRadius = 1;
                graph5.fillAlphas = 0.9;
                chart5.addGraph(graph5);
				
                // CURSOR
                var chartCursor = new AmCharts.ChartCursor();
                chartCursor.cursorAlpha = 0;
                chartCursor.zoomable = false;
                chartCursor.categoryBalloonEnabled = false;
                chartCursor.valueLineEnabled = false;
                chartCursor.valueLineBalloonEnabled = false;
                chartCursor.valueLineAlpha = 1;
                chart.addChartCursor(chartCursor);
				
                chart.creditsPosition = "top-right";

				var chartCursor1 = new AmCharts.ChartCursor();
                chartCursor1.cursorAlpha = 0;
                chartCursor1.zoomable = false;
                chartCursor1.categoryBalloonEnabled = false;
                chartCursor1.valueLineEnabled = false;
                chartCursor1.valueLineBalloonEnabled = false;
                chartCursor1.valueLineAlpha = 1;
                chart1.addChartCursor(chartCursor1);
				
                chart1.creditsPosition = "top-right";
                
                var chartCursor2 = new AmCharts.ChartCursor();
                chartCursor2.cursorAlpha = 0;
                chartCursor2.zoomable = false;
                chartCursor2.categoryBalloonEnabled = false;
                chartCursor2.valueLineEnabled = false;
                chartCursor2.valueLineBalloonEnabled = false;
                chartCursor2.valueLineAlpha = 1;
                chart2.addChartCursor(chartCursor2);
				
                chart2.creditsPosition = "top-right";
				
				var chartCursor3 = new AmCharts.ChartCursor();
                chartCursor3.cursorAlpha = 0;
                chartCursor3.zoomable = false;
                chartCursor3.categoryBalloonEnabled = false;
                chartCursor3.valueLineEnabled = false;
                chartCursor3.valueLineBalloonEnabled = false;
                chartCursor3.valueLineAlpha = 1;
                chart3.addChartCursor(chartCursor3);
				
                chart3.creditsPosition = "top-right";
				
				var chartCursor4 = new AmCharts.ChartCursor();
                chartCursor4.cursorAlpha = 0;
                chartCursor4.zoomable = false;
                chartCursor4.categoryBalloonEnabled = false;
                chartCursor4.valueLineEnabled = false;
                chartCursor4.valueLineBalloonEnabled = false;
                chartCursor4.valueLineAlpha = 1;
                chart4.addChartCursor(chartCursor4);
				
                chart4.creditsPosition = "top-right";
				
				var chartCursor5 = new AmCharts.ChartCursor();
                chartCursor5.cursorAlpha = 0;
                chartCursor5.zoomable = false;
                chartCursor5.categoryBalloonEnabled = false;
                chartCursor5.valueLineEnabled = false;
                chartCursor5.valueLineBalloonEnabled = false;
                chartCursor5.valueLineAlpha = 1;
                chart5.addChartCursor(chartCursor5);
				
                chart5.creditsPosition = "top-right";
					
                // WRITE
                chart.write("chartdiv1");
                chart1.write("chartdiv2");
                chart2.write("chartdiv3");
                chart3.write("chartdiv4");
                chart4.write("chartdiv5");
                chart5.write("chartdiv6");
            });
	
// to show special related fields in form			
		 
</script>			

<div class="full">
	<div class="screenlet">
			<div class="screenlet-title-bar">
         		<div class="grid-header" style="width:100%">
					<center><label>Pasteurized Milk Silos</label><center>
				</div>
		     </div>
		     
		     <div class="screenlet-body">
		     		<div id="chartdiv1" style="width: 100%; height: 250px;"></div>
		     </div>
	</div>
</div> 
<div class="full">
	<div class="lefthalf">
		<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<center><label>Raw Milk Silos</label><center>
					</div>
			     </div>
			     
			     <div class="screenlet-body">
			     		<div id="chartdiv2" style="width: 100%; height: 250px;"></div>
			     </div>
		</div>
	</div>
	<div class="righthalf">
		<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<center><label>Butter Silos</label><center>
					</div>
			     </div>
			     
			     <div class="screenlet-body">
			     		<div id="chartdiv3" style="width: 100%; height: 250px;"></div>
			     </div>
		</div>
	</div>
</div>
<div class="full">
	<div class="lefthalf">
		<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<center><label>Cream Silos</label><center>
					</div>
			     </div>
			     
			     <div class="screenlet-body">
			     		<div id="chartdiv4" style="width: 100%; height: 250px;"></div>
			     </div>
		</div>
	</div>
	<div class="righthalf">
		<div class="screenlet">
				<div class="screenlet-title-bar">
	         		<div class="grid-header" style="width:100%">
						<center><label>Curd Silos</label><center>
					</div>
			     </div>
			     
			     <div class="screenlet-body">
			     		<div id="chartdiv5" style="width: 100%; height: 250px;"></div>
			     </div>
		</div>
	</div>
</div>
<div class="clear"></div>
<div class="full">
	<div class="screenlet">
		<div class="screenlet-title-bar">
     		<div class="grid-header" style="width:100%">
				<center><label>Powder Silos</label><center>
			</div>
	     </div>
	     
	     <div class="screenlet-body">
	     		<div id="chartdiv6" style="width: 100%; height: 250px;"></div>
	     </div>
	</div>
</div>