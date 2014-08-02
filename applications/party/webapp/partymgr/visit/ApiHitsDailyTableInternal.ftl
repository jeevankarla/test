<style type="text/css">
		
	.dataTables_filter input {
  		border-style: groove;    
	}
	 #datatable1_filter,  #datatable1_length, #datatable1_info, #datatable1_paginate { 
	 	display: none; 
	 }
	 #datatable2_filter { 
	 }	 
</style>	


<script type="text/javascript" language="javascript" class="init">	

$(document).ready(function() {

	 
	var hitsTable = ${StringUtil.wrapString(hitsListJSON!'[]')};	 
	$('#hitsTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"data": hitsTable,
		"columns": [
			{ "title": "Hits Time" },
			{ "title": "User" },			
			{ "title": "Content Id" }],	
		"columnDefs": [{ type: 'date-eu', targets: [0] }],
       	"iDisplayLength" : 100
	} );	
	datatable2.fnSort( [ [0,'desc'] ] );		 	
} );

</script>
		
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Mobile Hits Details for ${hitsDate?date}</h3>	
     </div>
    <div class="screenlet-body">
    	<div id="hitsTable"/>
    </div>
</div>    
