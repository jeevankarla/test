
	<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/css/jquery.dataTables.css</@ofbizContentUrl>">
	<link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/css/dataTables.tableTools.css</@ofbizContentUrl>">

	<style type="text/css" class="init">

	</style>
<style type="text/css">
.dataTables_filter input {
  border-style: groove;    
}
</style>	
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/jquery.dataTables.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/js/dataTables.tableTools.js</@ofbizContentUrl>"></script>
	<script type="text/javascript" language="javascript" src="<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/media/js/dataTables.plugins.js</@ofbizContentUrl>"></script>


	<script type="text/javascript" language="javascript" class="init">	

var dataSet = ${StringUtil.wrapString(employeesJSON!'[]')};

$(document).ready(function() {
    
    $('#example tfoot th').each( function () {
        var title = $('#example thead th').eq( $(this).index() ).text();
        $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
    } );
    
	var table = $('#example').DataTable( {
		"data": dataSet,
		"columns": [
			{ "title": "Employee" },
			{ "title": "Employee Id" },
			{ "title": "Department" },
			{ "title": "Position" },			
			{ "title": "Join Date" },			
			{ "title": "Phone" }],
			"columnDefs": [{ type: 'date-eu', targets: 4 }],			
            "sDom": 'lfTrtip',
            "tableTools": {
                "sSwfPath": "<@ofbizContentUrl>/images/jquery/plugins/datatables/1.10.0/extensions/TableTools/swf/copy_csv_xls_pdf.swf</@ofbizContentUrl>",
                "aButtons": [ "copy", "csv", "pdf"  ]
            },
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
	
	table.columns().eq( 0 ).each( function ( colIdx ) {
        $( 'input', table.column( colIdx ).footer() ).on( 'keyup change', function () {
            table
                .column( colIdx )
                .search( this.value )
                .draw();
        } );
    } );
	
} );


	</script>
		
		<div id="demo">
		
		<table id="example" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Employee</th>
                <th>Employee Id</th>
                <th>Department</th>
                <th>Position</th>
                <th>Join Date date</th>
                <th>Phone</th>
            </tr>
        </thead>
 		<tbody></tbody>
        <tfoot>
            <tr>
                <th>Employee</th>
                <th>Employee Id</th>
                <th>Department</th>
                <th>Position</th>
                <th>Join Date date</th>
                <th>Phone</th>
            </tr>
        </tfoot>
		</table>
		</div>