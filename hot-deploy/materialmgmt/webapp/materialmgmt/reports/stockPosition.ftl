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
<style type="text/css">
		div.graph
		{
			width: 600px;
			height: 200px;
		}
		label
		{
			display: block;
			margin-left: 100px;
			padding-left: 5em;
		}
		
	</style>

<script type="application/javascript">   

  
$(document).ready(function(){

<#if custRequestsByStatusList?exists>		
	
            
	jQuery.plot($("#chart3"), [{data: ${StringUtil.wrapString(productDataListJSON)},
		points: { show: false },
									bars: {show: true,
                   						   	barWidth: 0.4,
                   							points: { show: false }, 
                   							align: 'center'}},
                   							
								  ],
				{ 
					series: {
                   		points: { show: true } 
                	}, 
               		grid: { hoverable: true},                 	 
                  	yaxes: [
                      { position: 'left',
                      	min: 0,
 						axisLabel : 'Qty' }
                  	],                	 
     				xaxis: {
         				min: 0,
         				max : ${productDataListJSON.size()} + 1,
         				ticks:${StringUtil.wrapString(labelsJSON)},
         				rotateTicks: 140,
         			    mode: "categories"
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
    $("#chart").bind("plothover", function (event, pos, item) {
        $("#x").text(pos.x.toFixed(2));
        $("#y").text(pos.y.toFixed(2));
        alert("item=" + item);
        if (item) {
            if (previousPoint != item.dataIndex) {
                previousPoint = item.dataIndex;
                
                $("#tooltip").remove();
                var x = item.datapoint[0].toFixed(2),
                    y = item.datapoint[1].toFixed(2);
                var content ="<br> " + y;
                showTooltip(item.pageX, item.pageY,
                            content);
            }
        }
        else {
            $("#tooltip").remove();
            previousPoint = null;            
        }
    });       	
	
	
</#if>

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
	
<div class="screenlet-body">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	      <h3>For Product : ${productId}</h3>
	    </div>
	    <div class="screenlet-body">
	   		<div id="chart3" class="graph">ll-${productDataListJSON.size()}</div>
	   		
	    </div>
	     </div>
</div>	

<!--<div class="screenlet-body">
<div class="righthalf">
	<div class="screenlet">
	    <div class="screenlet-title-bar">
	      <h3>Requirement (${froDate?date} - ${toDate?date})<#if productId?exists> For Product : ${productId} </#if></h3>
	    </div>
	    <div class="screenlet-body">
	   		<div id="chart" class="graph" style="margin-left:10px;margin-top:10px;"></div>
	   		<br><br>
	  		
	    </div>
	</div>
</div>

<div class="lefthalf">
   
	<div class="screenlet">
	  
	    <div class="screenlet-title-bar">
	      <h3> Indents (${froDate?date} - ${toDate?date})<#if productId?exists> For Product : ${productId} </#if></h3>
	    </div>
	    <div class="screenlet-body">
	   		<div id="chart2" class="graph" style="margin-left:10px;margin-top:10px;"></div>
	   		<br><br>
	  		
	    </div>
	    
	</div>
	
</div>-->

<div class="clear"></div>
</div>

