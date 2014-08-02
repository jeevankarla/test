

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
      <h3>Mobile Hits</h3>
    </div>
    <div class="screenlet-body">
   		<div id="graph" class="graph"></div>
   		<div id="overview" style="margin-left:50px;margin-top:20px;width:400px;height:50px"></div>
    </div>
</div>


