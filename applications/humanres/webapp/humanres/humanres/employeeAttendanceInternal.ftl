<style type="text/css">
		
	.dataTables_filter input {
  		border-style: groove;    
	}
	 #datatable1_filter,  #datatable1_length, #datatable1_info, #datatable1_paginate { 
	 }
	 #datatable2_filter,  #datatable2_length, #datatable2_info, #datatable2_paginate { 
	 }
</style>	


<script type="text/javascript" language="javascript" class="init">	

$(document).ready(function() {

	 
	var attendanceTable = ${StringUtil.wrapString(punchListJSON!'[]')};	 
	$('#attendanceTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable1"></table>' );

	var datatable1 = $('#datatable1').dataTable( {
		"data": attendanceTable,
		"columns": [
			{ "title": "In Time" },			
			{ "title": "Out Time" },
			{ "title": "Duration (hrs)"}],	
		"columnDefs": [{ type: 'date-eu', targets: [0,1] }],
       	"iDisplayLength" : 100
	} );
	
	var oodTable = ${StringUtil.wrapString(oodPunchListJSON!'[]')};	 
	$('#oodTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"data": oodTable,
		"columns": [
			{ "title": "Out Time" },			
			{ "title": "In Time" },
			{ "title": "Duration (hrs)"}],	
		"columnDefs": [{ type: 'date-eu', targets: [0,1] }],
       	"iDisplayLength" : 100
	} );		

} );

</script>

<div style="width:800px; margin:0 auto; padding:20px">
<h3>Employee Attendance Details for ${employeeId}: ${employeeName} [${fromDate?date} - ${thruDate?date}]</h3>	
</div>

<div class="container">
<div class="lefthalf">		
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Daily Punch</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="attendanceTable"/>
    </div>
</div>
</div>
<div class="righthalf">  
    <div class="screenlet">  
    	<div class="screenlet-title-bar">
      		<h3>OOD</h3>	
     	</div>
	    <div class="screenlet-body">
    		<div id="oodTable"/>  
    	</div>  	
    </div>
</div>
</div>    
