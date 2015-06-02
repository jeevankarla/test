
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
			
			var chartData1;
			var chartData2;
			var chartData3;
			var chartData4;
			var chartData5;
			var chartData6;
			var chartData7;
			var chartData8;
			<#assign i = 1>
			<#list categoryList as eachCategory>
				<#assign chartData = "chartData"+i>
				<#assign catId = eachCategory.siloType />
				${chartData} = data["${catId}"];
				<#assign i = i+1>
			</#list>			
			AmCharts.ready(function () {
				<#if categoryList?exists && categoryList?has_content>
					
					// serial chart
					<#assign i = 1>	
					<#list categoryList as eachCategory>
						<#assign catId = eachCategory.siloType />
						<#assign chartData = "chartData"+i>
						<#assign chart = "chart"+i>
						var ${chart} = new AmCharts.AmSerialChart();
		                ${chart}.dataProvider = ${chartData};
		                ${chart}.categoryField = "facility";
		                ${chart}.startDuration = 1;
		                ${chart}.depth3D = 50;
		                ${chart}.angle = 30;
		                ${chart}.marginRight = -45;
		                <#assign i = i+1>
		            </#list>    
		        		// AXES
                		// category
		            <#assign i = 1>	
					<#list categoryList as eachCategory>
					    <#assign chart = "chart"+i>
					    <#assign categoryAxis = "categoryAxis"+i>
		                var ${categoryAxis} = ${chart}.categoryAxis;
		                ${categoryAxis}.gridAlpha = 0;
		                ${categoryAxis}.axisAlpha = 0;
		                ${categoryAxis}.gridPosition = "start";
						<#assign i = i+1>
		            </#list>
					
						// value
					<#assign i = 1>	
					<#list categoryList as eachCategory>
					    <#assign chart = "chart"+i>
		                <#assign valueAxis = "valueAxis"+i>
		                var ${valueAxis} = new AmCharts.ValueAxis();
		                ${valueAxis}.axisAlpha = 0;
		                ${valueAxis}.gridAlpha = 0;
		                ${chart}.addValueAxis(${valueAxis});
						<#assign i = i+1>
		            </#list>
						
						// GRAPH
					<#assign i = 1>	
					<#list categoryList as eachCategory>
					    <#assign chart = "chart"+i>
		            	<#assign graph = "graph"+i>
		                var ${graph} = new AmCharts.AmGraph();
		                ${graph}.valueField = "quantity";
		                ${graph}.colorField = "color";
		                ${graph}.balloonText = "<b>Silo: [[category]] </br>Qty: [[value]]</br> Product: [[productName]] </br>Capacity: [[capacity]] </b>";
		                ${graph}.type = "column";
		                ${graph}.lineAlpha = 0.5;
		                ${graph}.lineColor = "#FFFFFF";
		                ${graph}.columnWidth = 0.5;
		                ${graph}.topRadius = 1;
		                ${graph}.fillAlphas = 0.8;
		                ${chart}.addGraph(${graph});
						<#assign i = i+1>
		            </#list>
		            
						// CURSOR
					<#assign i = 1>	
					<#list categoryList as eachCategory>
					    <#assign chart = "chart"+i>
		                <#assign chartCursor = "chartCursor"+i>
		                var ${chartCursor} = new AmCharts.ChartCursor();
		                ${chartCursor}.cursorAlpha = 0;
		                ${chartCursor}.zoomable = false;
		                ${chartCursor}.categoryBalloonEnabled = false;
		                ${chartCursor}.valueLineEnabled = false;
		                ${chartCursor}.valueLineBalloonEnabled = false;
		                ${chartCursor}.valueLineAlpha = 1;
		                ${chart}.addChartCursor(${chartCursor});
		                <#assign i = i+1>
					</#list>
					<#assign i = 1>
					<#list categoryList as eachCategory>
						<#assign chartdiv = "chartdiv"+i>
						<#assign chart = "chart"+i>
						${chart}.write("${chartdiv}");
						<#assign i = i+1>
					</#list>
				</#if>
            });
	
// to show special related fields in form			
		 
</script>			
<div class="full">
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Search Field</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="siloInventoryForm" method="post" action="productionDashboardMain">
		<table class="basic-table" cellspacing="0" width="30%">
			<tr>
        		<td align="right" width="10%"><span class='h3'>Plant/Unit: </span></td>
				<td align="left" width="10%">
					<select name="facilityId" class='h3'>
						<#list facilityList  as eachFac>
							<option value='${eachFac.facilityId}'>${eachFac.facilityName?if_exists}</option>
						</#list>
					</select>
				</td>
				<td align="center" width="10%"><input type="submit" value="Submit" id="getCharts" class="smallSubmit" /></td>
			</tr>
    	</table> 
	</form>
	</div>
</div>
</div>
<div class="full">
	<#if categoryList?has_content>
		<#assign index = 1>
 		 <#list categoryList as eachSilo>
 		 		<div class="full">
 		 		
					<div class="screenlet">
						<div class="screenlet-title-bar">
			         		<div class="grid-header" style="width:100%">
								<center><label>${eachSilo.description?if_exists}</label><center>
							</div>
					     </div>
					     
					     <div class="screenlet-body">
					     	<#assign idx= "chartdiv"+index>
					     		<div id="${idx}" style="width: 100%; height: 250px;"></div>
					     </div>
					  </div>
				</div>
				<#assign index = index+1>
         </#list>    
	</#if>
</div>