
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
    

    
	var table = $('#example').DataTable( {

		"data": dataSet,
		"columns": [
			{ "title": "Employee Name" },
			{ "title": "Employee Id" },
			{ "title": "Gender"},
			{ "title": "Designation" },	
			{ "title": "Department" },	
			{ "title": "Place of Posting" },
			{ "title": "Blood Group" },		
			{ "title": "Mobile" },	
			{ "title": "Email Id" },
			
			],
			
			"columnDefs": [
				{ type: 'date-eu', targets: 4 }
				],			
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
                	"sFileName": "EmployeeList.csv"                 	
                },
                { 
                	"sExtends": "pdf", 
                	"oSelectorOpts": { filter: 'applied', order: 'current' },
                	"sFileName": "EmployeeList.pdf"   
                } 
                ]
            },
		"iDisplayLength" : 25,		            
     	"fnRowCallback": function(nRow, aData, iDisplayIndex ) {
     		<#if security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
		    $('td:eq(0)', nRow).html('<a href="EmployeeProfile?partyId=' + aData[1] + '">' +
                aData[0] + '</a>');
            </#if> 
            //if(aData[1]==${userLogin.partyId}){
            	//$('td:eq(0)', nRow).html('<a href="/humanres/control/EmployeeProfile?partyId=' + aData[1] + '">' +
                //aData[0] + '</a>');
            //}   
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

		if (colIdx == 6 || colIdx == 7) {        
			table
    		.column(colIdx)
    		.nodes()
    		.to$()      // Convert to a jQuery object
    		.css('text-align', 'center');
    	}
    } );
	
} );


	</script>
		<#assign allEmployees = "false">
		<#if parameters.allEmployees?has_content >
			<#assign allEmployees = parameters.allEmployees>
		</#if>
		<#if security.hasEntityPermission("HUMANRES", "_ADMIN", session)>
		<div id="demo">
		<div align = "right">
        <ul><li class="h3">Fetch All<input type="checkbox" name="allEmployees"  <#if allEmployees == 'true'> checked="checked" <#elseif allEmployees == 'false'> onclick="javascript:getAllEmployees();"</#if></li></li>
         </ul>
        <br class="clear"/>
    	</div>
    	</#if>
		<table id="example" class="display" cellspacing="0" width="100%" >
        <thead>
            <tr>
            	<th style='width: 20%;'>Employee</th>
                <th style='width: 10%;'>Employee Id</th>
                <th style='width: 5%; '>Gender</th>
                <th style='width: 20%;'>Designation</th>
                <th style='width: 15%;'>Deparment</th>
                <th style='width: 10%;'>Place of Posting</th>
                <th style='width: 5%;'>Blood Group</th>
                <th style='width: 10%;'>Mobile</th>
                <th style='width: 10%;'>Email Id</th>
                 
                       
            </tr>
        </thead>
 		<tbody></tbody>
        <tfoot>
            <tr>
            	<th><input type="text" placeholder="<Name>" /></th>
            	<th><input type="text" style="width: 65px;" placeholder="<Id>" /></th>
               	<th><input type="text" style="width: 65px;" placeholder="<Gender>" /></th>
               	<th><input type="text" placeholder="<Position>" /></th>
               	<th><input type="text" style="width: 110px;" placeholder="<Department>" /></th>
                <th><input type="text" style="width: 100px;" placeholder="<Place of posting>" /></th>
                 <th><input type="text" style="width: 65px;" placeholder="<Blood Group>" /></th>
                <th><input type="text" style="width: 80px;" placeholder="<Mobile>" /></th>
                <th><input type="text" style="width: 110px;" placeholder="<Email>" /></th>
                
               
                          
            </tr>
        </tfoot>
		</table>
		</div>