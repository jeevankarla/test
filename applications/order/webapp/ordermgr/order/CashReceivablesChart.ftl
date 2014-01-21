
<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>

<script type="application/javascript">   
$.plot(
   $("#graph"),
   [
{
        data: [
            [0, 0],
            [1, ${cashReceivables}],
            [2, ${pastDues}],
            [3,0]
        ],
        label: "Cash Receivables"
    },
	{
        data: [
            [0, 0],
            [1, ${cashReceived}],
            [2, 0]
        ],
        label: "Cash Received"
    }
 ],
 {
        xaxis: {
        	ticks: [[0, ''], [1, "Day's Dues"], [2, 'Past Dues'], [3,'']],
            minTickSize: 1
        },
        yaxis: {
        	min: 0,
 			axisLabel : 'Receivables (Lakhs)',
 			axisLabelPadding: 5
        },        
        series: {
            bars: {
                show: true,
                barWidth: .5,
                align: "center"
            },
            stack: true
        },
        grid: { hoverable: true}                	 
        
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
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                
                showTooltip(item.pageX, item.pageY,
                            y);
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;            
        }
    });
</script>
	<style type="text/css">
		div.graph
		{
			width: 600px;
			height: 400px;
			margin-left: 100px;		
			margin-bottom: 30px;							
		}
		
	</style>


<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Day Cash Receivables for ${salesDate?date} (in lakhs)</h3>
    </div>
    <div class="screenlet-body">
   		<div id="graph" class="graph"></div>
    </div>
</div>

<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>