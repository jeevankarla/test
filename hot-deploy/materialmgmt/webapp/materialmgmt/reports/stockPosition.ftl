


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

  
$(document).ready(function(){
<#if requirmentByStatusList?exists>
  var data = [
<#list requirmentByStatusList as requirment>
    {label: "${StringUtil.wrapString(requirment.name?default(""))}", data: ${requirment.count?if_exists}}<#if requirment_has_next>,</#if>
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
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
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
			content: "%s %p.2%  (Qty %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-220, 120]
		}
	});
//setupGrid1();	

</#if>		
		
<#if custRequestsByStatusList?exists>		
	var data2 = [
<#list custRequestsByStatusList as custRequest>
    {label: "${StringUtil.wrapString(custRequest.name?default(""))}", data: ${custRequest.count?if_exists}}<#if custRequest_has_next>,</#if>
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
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%   (Qty %y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},			
		legend: {
			show: true,
			noColumns: 3,
			margin: [-240, -10]
		}
	});
	
	//	setupGrid2();
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
	<style type="text/css">
		div.graph
		{
			width: 250px;
			height: 250px;
		}
		label
		{
			display: block;
			margin-left: 100px;
			padding-left: 1em;
		}
		
	</style>
<div class="screenlet-body">
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
	
</div>

<div class="clear"></div>
</div>

