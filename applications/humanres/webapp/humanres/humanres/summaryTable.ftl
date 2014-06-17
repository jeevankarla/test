
	<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
	<style type="text/css" class="init">

	</style>
<style type="text/css">
.dataTables_filter input {
  border-style: groove;
}
</style>	
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" class="init">	

var dataSet = ${StringUtil.wrapString(employeesJSON!'[]')};

$(document).ready(function() {
	$('#demo').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>' );

	$('#example').dataTable( {
		"data": dataSet,
		"columns": [
			{ "title": "Employee" },
			{ "title": "Employee Id" },
			{ "title": "Department" },
			{ "title": "Position" },			
			{ "title": "Join Date" },			
			{ "title": "Phone" }],
		"iDisplayLength" : 25,
		"fnRowCallback": function(nRow, aData, iDisplayIndex ) {
		    $('td:eq(0)', nRow).html('<a href="EmployeeProfile?partyId=' + aData[1] + '">' +
                aData[0] + '</a>');
            return nRow;
			//$(nRow).click(function () {
        		//document.location.href = "viewprofile?partyId=" + aData[1];
    		//});
		}
	} );	
} );


	</script>
		
		<div id="demo"></div>