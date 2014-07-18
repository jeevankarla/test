<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">

<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>


<style type="text/css">
		
	.dataTables_filter input {
  		border-style: groove;    
	}
	 #datatable1_filter,  #datatable1_length, #datatable1_info, #datatable1_paginate { 
	 	display: none; 
	 }
	 #datatable2_filter { 
	 }	 
	.myRightAlignClass{ text-align: right; } 
</style>	


<script type="text/javascript" language="javascript" class="init">	

$(document).ready(function() {

	 
	var transportersTable = ${StringUtil.wrapString(transportersJSON!'[]')};	 
	$('#transportersTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"data": transportersTable,
		"columns": [
			{ "title": "Route" },
			{ "title": "Transporter Id" },			
			{ "title": "Transporter Name" },
			{ "title": "Phone" },			
			{ "title": "Route Length (Km)", sClass: "myRightAlignClass" },	
			{ "title": "Unit Rate (Rs)", sClass: "myRightAlignClass" },								
			{ "title": "Contract Start" },			
			{ "title": "Contract End" }],
		"columnDefs": [{ type: 'date-eu', targets: [6,7] }],
       	"iDisplayLength" : 100,
       	"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
       		if (aData[7] && aData[7].length > 0) {
       			var parts =aData[7].split('/');  // dd/MM/yyyy
				var thruDate = new Date(parts[2],parts[1]-1,parts[0]);
				var currentDate = new Date();
				var interval = thruDate.getTime() - currentDate.getTime();
				interval = interval/(1000*60*60*24);
				alert("interval="+interval);
        		if (interval < 45) {
                	$(nRow).css('color', 'red')
            	}
            }
         }
	} );	
	datatable2.fnSort( [ [7,'asc'] ] );	 	 	
} );

</script>
		
<div class="container"></div>
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Transporters</h3>	
     </div>
    <div class="screenlet-body">
       	<div id="chart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
    	<div id="transportersTable"/>
    </div>
</div>    
</div>
