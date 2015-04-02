

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


<script type="text/javascript" language="javascript" class="init">	

$(document).ready(function() {

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
		

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Upcoming Retirements</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="upcomingRetireesTable"/>
    </div>
</div>    
