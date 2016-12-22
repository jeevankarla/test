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
			{ "title": "Hit Date" },
			{ "title": "Hit Time" },			
			{ "title": "User Login" },
			{ "title": "Party Id" },	
			{ "title": "Role" },														
			{ "title": "Content Id" },
			{ "title": "Time (ms)"}],	
		"columnDefs": [{ type: 'date-eu', targets: [0] }],
       	"iDisplayLength" : 100,
       	"sDom": 'lfTrtip',
       	"tableTools": {
                "sSwfPath": "<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/swf/copy_csv_xls_pdf.swf</@ofbizContentUrl>",
                "aButtons": [ 
                { 
                	"sExtends": "copy", 
                	"oSelectorOpts": { filter: 'applied', order: 'current' }
                },
                { 
                	"sExtends": "csv", 
                	"oSelectorOpts": { filter: 'applied', order: 'current' },
                	"sFileName": "MobileHits.csv"                 	
                },
                { 
                	"sExtends": "pdf", 
                	"oSelectorOpts": { filter: 'applied', order: 'current' },
                	"sFileName": "MobileHits.pdf"   
                } 
                ]
            }
	} );	
	datatable2.fnSort( [ [1,'desc'] ] );		 	
} );

</script>
		
<div class="screenlet">
	<div class="screenlet-title-bar">
		<#if fromDate == thruDate>
      		<h3>Mobile Hits Details for ${fromDate?date} </h3>
      	<#else>
      		<h3>Mobile Hits Details from ${fromDate?date} to ${thruDate?date}</h3>
      	</#if>	
     </div>
    <div class="screenlet-body">
    	<div id="hitsTable"/>
    </div>
</div>    
