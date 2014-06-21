<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">


<style type="text/css">
		div.graph
		{
			width: 300px;
			height: 400px;
		}
		label
		{
			display: block;
			margin-left: 400px;
			padding-left: 1em;
		}
		
	.dataTables_filter input {
  		border-style: groove;    
	}
	 #datatable1_filter,  #datatable1_length, #datatable1_info, #datatable1_paginate { 
	 	display: none; 
	 }
	 #datatable2_filter,  #datatable2_length, #datatable2_info, #datatable2_paginate { 
	 	display: none; 
	 }	 
	 .myRightAlignClass{ text-align: right;font-weight:bold } 
 
</style>	
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>
	
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tooltip.js"</@ofbizContentUrl>></script>


	<script type="text/javascript" language="javascript" class="init">	

$(document).ready(function() {

  var benefitsData = ${StringUtil.wrapString(benefitsPieDataJSON!'[]')};
  
	$.plot($("#chart"), benefitsData, 
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
			content: "%s %p.2%  (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			position: "ne",
			show: true,
			margin: [-220, 20]
		}
	});


	var salaryTableData = ${StringUtil.wrapString(benefitsTableJSON!'[]')};
	$('#salaryAnalysisTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable1"><tfoot><tr><td></td><td></td></tr></tfoot></table>' );

	var datatable1 = $('#datatable1').dataTable( {
		"data": salaryTableData,
		"columns": [
			{ "title": "Benefit" },			
			{ "title": "Amount (Rs)", sClass: "myRightAlignClass" }],
		"iDisplayLength" : 50,
        "footerCallback": function ( row, data, start, end, display ) {
			var api = this.api(), data;
            // Remove the formatting to get integer data for summation
            var intVal = function ( i ) {
                return typeof i === 'string' ?
                    i.replace(/[\$,]/g, '')*1 :
                    typeof i === 'number' ?
                        i : 0;
            }; 
            // Total over all pages
            data = api.column( 1 ).data();
            total = data.length ?
                data.reduce( function (a, b) {
                        return intVal(a) + intVal(b);
                } ) : 0;
            // Update footer
            $( api.column( 0).footer() ).html("Total");            
            $( api.column( 1).footer() ).html(total);            
        }		
	 });
	 
	 datatable1.fnSort( [ [1,'desc'] ] );	 
	 
	 // By Department Analysis
	 
  var benefitsByDeptData = ${StringUtil.wrapString(benefitsByDeptPieDataJSON!'[]')};
  
	$.plot($("#salaryAnalysisByDeptChart"), benefitsByDeptData, 
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
			content: "%s %p.2%  (%y)", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			position: "ne",
			show: true,
			margin: [-250, 20]
		}
	});


	var salaryByDeptTableData = ${StringUtil.wrapString(benefitsByDeptTableJSON!'[]')};
	$('#salaryAnalysisByDeptTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"><tfoot><tr><td></td><td></td></tr></tfoot></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"data": salaryByDeptTableData,
		"columns": [
			{ "title": "Department" },			
			{ "title": "Amount (Rs)", sClass: "myRightAlignClass" }],
		"iDisplayLength" : 50,
        "footerCallback": function ( row, data, start, end, display ) {
			var api = this.api(), data;
            // Remove the formatting to get integer data for summation
            var intVal = function ( i ) {
                return typeof i === 'string' ?
                    i.replace(/[\$,]/g, '')*1 :
                    typeof i === 'number' ?
                        i : 0;
            }; 
            // Total over all pages
            data = api.column( 1 ).data();
            total = data.length ?
                data.reduce( function (a, b) {
                        return intVal(a) + intVal(b);
                } ) : 0;
            // Update footer
            $( api.column( 0).footer() ).html("Total");            
            $( api.column( 1).footer() ).html(total);            
        }		
	 });
	 
	 datatable2.fnSort( [ [1,'desc'] ] );	 	 
});
	</script>
		
<div class="container">
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Salary Benefits Analysis</h3>	
     </div>
    <div class="screenlet-body">
       	<div id="chart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
    </div>
	<div class="screenlet-title-bar">
      	<h3>Salary Benefits Analysis</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="salaryAnalysisTable"/>
    </div>    
</div>    
</div>
<div class="righthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Salary Benefits By Department Analysis</h3>	
     </div>
    <div class="screenlet-body">
       	<div id="salaryAnalysisByDeptChart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
    </div>
	<div class="screenlet-title-bar">
      	<h3>Salary Benefits By Department Analysis</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="salaryAnalysisByDeptTable"/>
    </div> 
</div>    
</div>
</div>