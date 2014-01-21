

<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jquery-1.6.1.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.selection.js</@ofbizContentUrl>"></script>

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>

<script type="application/javascript">  
function timeConverter(timestamp){
 	var a = new Date(timestamp);
 	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var year = a.getFullYear();
    var month = months[a.getMonth()];
    var date = a.getDate();
    var time = month + ' ' + date+', '+year;
    return time;
 }
  
$(document).ready(function(){  
	var data = ${StringUtil.wrapString(listJSON)};
	var options = { 
					series: {
                   		lines: { show: true },
                   		points: { show: true }
               		},
               		grid: { hoverable: true},
					xaxis: { mode: "time" },
					selection: { mode: "x" } 
				};
	
	var plot = jQuery.plot($("#graph"), [data], options);
				
    var overview = $.plot($("#overview"), [data], {
        xaxis: { ticks: [], mode: "time" },
        yaxis: { ticks: [], min: 0, autoscaleMargin: 0.1 },
        selection: { mode: "x" }
    });

    // now connect the two
    
    $("#graph").bind("plotselected", function (event, ranges) {
        // do the zooming
        plot = $.plot($("#graph"), [data],
                      $.extend(true, {}, options, {
                          xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
                      }));

        // don't fire event on the overview to prevent eternal loop
        overview.setSelection(ranges, true);
    });
    
    $("#overview").bind("plotselected", function (event, ranges) {
        plot.setSelection(ranges);
    });				

    function showTooltip(x, y, contents) {
        $('<div id="tooltip">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 5,
            left: x + 5,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var previousPoint = null;
    $("#graph").bind("plothover", function (event, pos, item) {   
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));
        if (item) {
            if (previousPoint != item.dataIndex) {
                previousPoint = item.dataIndex;
                
                $("#tooltip").remove();
                var x = item.datapoint[0];
                var y = item.datapoint[1].toFixed(2);
                showTooltip(item.pageX, item.pageY,
                            timeConverter(x)+ "<br>" + y + " ltrs");
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;            
        }
    });
    
    // 2nd graph
    
	var data2 = ${StringUtil.wrapString(listRevJSON)} ;
	var options2 = { 
					series: {
                   		lines: { show: true },
                   		points: { show: true }
               		},
               		grid: { hoverable: true},
					xaxis: { mode: "time" },
					selection: { mode: "x" } 
				};		   
	var plot2 = jQuery.plot($("#graph2"), [data2], options2);
			
    var overview2 = $.plot($("#overview2"), [data2], {
        xaxis: { ticks: [], mode: "time" },
        yaxis: { ticks: [], min: 0, autoscaleMargin: 0.1 },
        selection: { mode: "x" }
    });

    // now connect the two
    
    $("#graph2").bind("plotselected", function (event, ranges) {
        // do the zooming
        plot = $.plot($("#graph2"), [data],
                      $.extend(true, {}, options2, {
                          xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
                      }));

        // don't fire event on the overview to prevent eternal loop
        overview2.setSelection(ranges, true);
    });
    
    $("#overview2").bind("plotselected", function (event, ranges) {
        plot2.setSelection(ranges);
    });	
    	
    function showTooltip2(x, y, contents) {
        $('<div id="tooltip2">' + contents + '</div>').css( {
            position: 'absolute',
            display: 'none',
            top: y + 5,
            left: x + 5,
            border: '1px solid #fdd',
            padding: '2px',
            'background-color': '#fee',
            opacity: 0.80
        }).appendTo("body").fadeIn(200);
    }

    var previousPoint2 = null;
    $("#graph2").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));
        if (item) {
            if (previousPoint2 != item.dataIndex) {
                previousPoint2 = item.dataIndex;
                
                $("#tooltip2").remove();
                var x = item.datapoint[0];
                var y = item.datapoint[1].toFixed(2);
                
                showTooltip2(item.pageX, item.pageY,
                            timeConverter(x)+ "<br>" + y + " ");
            }
        }
        else {
            $("#tooltip2").remove();
            previousPoint2 = null;            
        }
    });    
});    
</script>
	<style type="text/css">
		div.graph
		{
			width: 800px;
			height: 400px;
		}
		
	</style>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Daily Sales Quantity (in litres)</h3>
    </div>
    <div class="screenlet-body">
   		<div id="graph" class="graph"></div>
   		<div id="overview" style="margin-left:50px;margin-top:20px;width:400px;height:50px"></div>
    </div>
</div>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Daily Sales Revenue (in lakh Rupees)</h3>
    </div>
    <div class="screenlet-body">
   		<div id="graph2" class="graph"></div>
   		<div id="overview2" style="margin-left:50px;margin-top:20px;width:400px;height:50px"></div>   		
    </div>
</div>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
