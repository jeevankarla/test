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


<script type="text/javascript" language="javascript" class="init" charset="utf-8">	

$(document).ready(function() {

	 var attendanceTable = ${StringUtil.wrapString(punchListEditJSON!'[]')};	 
	 $('#attendanceTable').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="datatable2"><thead><tr></tr></thead><tbody></tbody></table>' );

	var datatable2 = $('#datatable2').dataTable( {
		"aaData": attendanceTable,
		"columns": [
		    { "title": "Empl Punch Id" },
			{ "title": "Punch Date" },
			{ "title": "Employee Id" },			
			{ "title": "Employee Name" },
			{ "title": "Time" },			
			{ "title": "In Out" },
			{ "title": "Punch Type"}],
		"aoColumns": [
			{ "sTitle": "Empl Punch Id" },
			{ "sTitle": "Punch Date" },
			{ "sTitle": "Employee Id" },			
			{ "sTitle": "Employee Name" },
			{ "sTitle": "Time" },			
			{ "sTitle": "InOut" },
			{ "sTitle": "PunchType"}],
	 "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull) {
           $(nRow).attr("id", aData[0]);
          return nRow;
       }	,
		"columnDefs": [{ type: 'date-eu', targets: [0] }],
       	"iDisplayLength" : 100,
       	"asSorting": [[0, 'asc']],
       	bJQueryUI: true,
		"sPaginationType": "full_numbers"
	} ).makeEditable({
	         sUpdateURL: "emplPunch",
             sAddURL: "emplPunch",
             sAddHttpMethod: "post",
             sDeleteHttpMethod: "post",
			 sDeleteURL: "deletePunch",
             "aoColumns": [ { },
             				{ 	cssclass: "required" },
                            { },
							{},
							{ },
							{
	        					indicator: 'Saving IN/OUT...',
	        					tooltip: 'Click to select IN/OUT',
	        					loadtext: 'loading...',
	                        	type: 'select',
	            				onblur: 'submit',
	        					data: "{'IN':'IN','OUT':'OUT'}"
	    						},
	    				   {
	        					indicator: 'Saving PunchType...',
	        					tooltip: 'Click to select PunchType',
	        					loadtext: 'loading...',
	                        	type: 'select',
	            				onblur: 'submit',
	        					data: "{'Normal':'Normal','Ood':'Ood'}"
	    						}		
	    						
				],
				oAddNewRowButtonOptions: {	label: "Add",
									       icons: {primary:'ui-icon-plus'} 
									},
			    oDeleteRowButtonOptions: {	label: "Remove", 
													icons: {primary:'ui-icon-trash'}
									},

			    oAddNewRowFormOptions: { 	
                                        title: 'Add a new Punch',
										show: "blind",
										hide: "explode",
                                        modal: true
									},
			 sAddNewRowFormId: "formAddNewRow",
			 oDeleteParameters : { 'employeePunchId': function _fnGetRowIDFromAttribute(nRow) {
           							return $(nRow).attr("id");
        						} },						
			sAddDeleteToolbarSelector: ".dataTables_length"
	        
	});	
    datatable2.fnSort([[3,'desc']]);
});	
</script>
		
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Attendance Punch-In Details for ${punchDate?date}</h3>	
     </div>
    <div class="screenlet-body">
     <form id="formAddNewRow" action="#" title="Add a new browser" style="width:600px;min-width:600px">
        <label for="engine">Rendering engine</label><br />
	    <input type="text" name="engine" id="name" rel="0" />
        <br />
        <label for="browser">Browser</label><br />
	<input type="text" name="browser" id="browser" rel="1" />
        <input type="hidden" name="platform" rel="2" />
        <br />
        <label for="version">Engine version</label><br />
	<select name="version" id="version" rel="3">
                <option>1.5</option>
                <option>1.7</option>
                <option>1.8</option>
        </select>
        <br />
        
        <br />
</form>
    <div id="attendanceTable"> 
            
    	</div>
    </div>
</div>    
