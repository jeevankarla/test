<script type="text/javascript">

$(document).ready(function(){
		$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			showCurrentAtPos: 1,
			onSelect: function( selectedDate ) {
				$( "#thruDate" ).datepicker( "option", "minDate", selectedDate );
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			showCurrentAtPos: 1,			
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
            	<td align="left" width="15%"><input class='h2' type="text" id="fromDate" name="fromDate"/></td>
				<td align="left" width="3%"><span class='h3'>To: </span></td>
				<td align="left" width="15%"><input class='h2' type="text" id="thruDate" name="thruDate"/></td>
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
		
			
<script type="application/javascript">  


$(document).ready(function(){
  var data = [
<#list custRequestList as custRequest>
    {label: "${StringUtil.wrapString(custRequest.name?default(""))}", data: ${custRequest.total?if_exists}}<#if custRequest_has_next>,</#if>
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
      <#if fromDate?has_content>
      	<h3>Complaints Status Type-wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.fromDisplayDate, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.thruDisplayDate, "MMM dd, yyyy")}]</h3>
       <#else>
       	<h3>Complaints Status Type-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.thruDisplayDate, "MMM dd, yyyy")}</h3>
      </#if>
    </div>
    <div class="screenlet-body">
   		<div id="bar" class="graph"></div>
   		<br><br>  
    </div>
</div>
</div>	
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
       <#if fromDate?has_content>
      		<h3>Complaints Type-wise [${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.fromDisplayDate, "MMM dd, yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.thruDisplayDate, "MMM dd, yyyy")}]</h3>
       	<#else>
       		<h3>Complaints Type-wise upto ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parameters.thruDisplayDate, "MMM dd, yyyy")}</h3>
      </#if> 
    </div>
    <div class="screenlet-body">
   		<div id="chart" class="graph2"></div>
   		<br><br>  
    </div>
</div>
</div>	
<#else>
      <p align="center" style="font-size: large;">
        <b>No Complaints Found!</b>
      </p>  
</#if> 		   		


