<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">


<style type="text/css">
		div.graph
		{
			width: 300px;
			height: 300px;
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
	 #datatable2_filter { 
	 	display: none; 
	 }	 
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

  var data =${StringUtil.wrapString(deptPieDataJSON!'[]')};
  
	$.plot($("#chart"), data, 
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


	var deptableData = ${StringUtil.wrapString(deptEmployeesJSON!'[]')};
	$('#departmentEmployeesTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable1"><tfoot><tr><td></td><td></td></tr></tfoot></table>' );

	$('#datatable1').dataTable( {
		"data": deptableData,
		"columns": [
			{ "title": "Department" },			
			{ "title": "Number of Employees" }],
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
	 
	 // populate the upcoming retirees table
	var upcomingRetireesTable = ${StringUtil.wrapString(employeesJSON!'[]')};	 
	$('#upcomingRetireesTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"data": upcomingRetireesTable,
		"columns": [
			{ "title": "Employee" },
			{ "title": "Employee Id" },			
			{ "title": "Department" },
			{ "title": "Position" },			
			{ "title": "Join Date" },			
			{ "title": "Retirement Date" }],
		"columnDefs": [{ type: 'date-eu', targets: [4,5] },
					   { "visible": false, targets: 1 }],
       	"iDisplayLength" : 25,		
     	"fnRowCallback": function(nRow, aData, iDisplayIndex ) {
		    $('td:eq(0)', nRow).html('<a href="EmployeeProfile?partyId=' + aData[1] + '">' +
                aData[0] + '</a>');
            return nRow;
		}
	} );	
	
	datatable2.fnSort( [ [5,'asc'] ] );	 
	 
	 	
} );

	</script>
		
<div class="container"></div>
<div class="lefthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Employees Department Analysis</h3>	
     </div>
    <div class="screenlet-body">
       	<div id="chart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
    	<div id="departmentEmployeesTable"/>
    </div>
</div>    
</div>
<div class="righthalf">
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Upcoming Retirements</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="upcomingRetireesTable"/>
    </div>
</div>    
</div>
</div>