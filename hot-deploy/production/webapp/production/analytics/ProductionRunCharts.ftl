
<link rel="stylesheet" href="<@ofbizContentUrl>/images/amcharts/style.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
        		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/amcharts/amcharts.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/amcharts/pie.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
    var chart;
            var legend;
			var chartData = ${StringUtil.wrapString(productionRunData)!'[]'};

            AmCharts.ready(function () {
                // PIE CHART
                chart = new AmCharts.AmPieChart();
                chart.dataProvider = chartData;
                chart.titleField = "product";
                chart.valueField = "quantity";
				chart.depth3D = 20;
            	chart.angle = 20;
                
                // LEGEND
                legend = new AmCharts.AmLegend();
                legend.align = "center";
                legend.markerType = "circle";
                chart.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
                chart.addLegend(legend);

                // WRITE
                chart.write("chartdiv");
            });
</script>			

<div class="full">
	<div id="chartdiv" style="width: 100%; height: 400px;"></div>
	
</div> 
