

<#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jqplot/jquery.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jqplot/jquery.jqplot.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/jqplot/plugins/jqplot.pieRenderer.min.js"</@ofbizContentUrl>></script>

<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/jqplot/jquery.jqplot.min.css</@ofbizContentUrl>" />

<script type="application/javascript">   
$(document).ready(function(){
  var data = [
<#list productReportList as productReport>
    ["${StringUtil.wrapString(productReport.internalName?default(""))}", ${productReport.unitPrice?if_exists * productReport.quantityOrdered?if_exists}]<#if productReport_has_next>,</#if>
</#list>    
  ];
  var plot1 = jQuery.jqplot ('chart', [data], 
    { 
      seriesDefaults: {
        // Make this a pie chart.
        renderer: jQuery.jqplot.PieRenderer, 
        rendererOptions: {
          // Put data labels on the pie slices.
          // By default, labels show the percentage of the slice.
          showDataLabels: true
        }
      }, 
      legend: { show:true, location: 'e' }
    }
  );
});
</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.OrderOrderChartsPage}</h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart"></div>
    </div>
</div>

<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
