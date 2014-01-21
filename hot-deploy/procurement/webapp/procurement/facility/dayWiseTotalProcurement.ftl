

<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jquery-1.6.1.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>

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
	jQuery.plot($("#graph"), [${StringUtil.wrapString(listJSON)}], 
				{ 
					series: {
                   		lines: { show: true },
                   		points: { show: true }
               		},
               		grid: { hoverable: true},
					xaxis: { mode: "time" },
        			yaxis: {
            			min: 0,
						axisLabel : 'Qty (Kgs)',
           			    position: 'left'            			
        			}, 					 
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
                var x = item.datapoint[0],
                    y = item.datapoint[1].toFixed(2);
                
                showTooltip(item.pageX, item.pageY,
                            timeConverter(x)+ "<br>" + y + " Kgs");
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
      <h3>Daily Procurement Quantity (in Kgs)</h3>
    </div>
    <div class="screenlet-body">
   		<div id="graph" class="graph"></div>
    </div>
</div>

<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
