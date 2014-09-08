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
	         sUpdateURL: "emplPunchJson",
	         sRowParamId: "employeePunchId",
             sAddURL: "emplPunchJson",
             sAddHttpMethod: "POST",
             sDeleteHttpMethod: "POST",
			 sDeleteURL: "deletePunchJson",
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
				oAddNewRowCancelButtonOptions: { label: "Cancel" },					
			    oDeleteRowButtonOptions: {	label: "Remove", 
													icons: {primary:'ui-icon-trash'}
									},

			    oAddNewRowFormOptions: { 	
                                        title: 'Add a new Punch',
										show: "blind",
										hide: "explode",
                                        modal: true
									},
			 fnOnDeleting: function (tr, id, fnDeleteRow) {

                               var r = confirm('Please confirm that you want to delete this Punch row? ' + id);

                               if (r) {

                                       deleteParameters= {"employeePunchId" :id};

                                       fnDeleteRow(id, null, deleteParameters);                                                

                               }
                               return false;
                       }, 						
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
      <form id='formAddNewRow'  method='post'>
     <table cellspacing=10  cellpadding=20> 		
		<tr class='h3'>
			<td align='left' class='h3' width='30%'>PartyId:</td>
			<td align='left' class='h3' width='30%'><input type='text' value="" name='partyId' size='5' /></td>
		</tr>
	    <tr class='h3'>
	       <td align='left' class='h3' width='40%'>Punch Type:</td>
	       <td align='left' class='h3' width='40%'>
	        <select name='PunchType'  allow-empty='false' id='PunchType' class='h4'>
          		<#list punchTypeList as ptl>
          		<option value='${ptl.enumId}' >${ptl.enumId?if_exists}</option>
          		</#list>          
			 </select>
			 </td></tr>
			<tr class='h3'>
			<td align='left' class='h3' width='40%'>IN/OUT:</td>
			<td align='left' class='h3' width='40%'>
			  <select name='InOut'  allow-empty='false' id='InOut' class='h4'>
          		<option value='IN' >IN</option>
          		<option value='OUT' >OUT</option>  
			 </select>
			</td>
			</tr>
			
			<tr class='h3'>
				<td align='left' class='h3' width='40%'>Shift Type:</td>
				<td align='left' class='h3' width='40%'><select name='shiftType'  allow-empty='false' id='shiftType' class='h4'>
	              		<#list shiftTypeList as stl>
	              		    <option value='${stl.shiftTypeId}' >${stl.shiftTypeId?if_exists}</option>
	              		</#list>          
			</select></td></tr>
			<tr class='h3'>
				<td align='left' class='h3' width='40%'>Date:</td><td align='left' class='h3' width='40%'><input type='text'  name='punchdate' id='punchdate' size='10' /></td></tr>
			<tr class='h3'>
				<td align='left' class='h3' width='40%'>Punch Time:</td><td align='left' class='h3' width='40%'><input type='text'  value="" name='punchtime' size='10' required /></tr>
			
			<tr class='h3'>
				<td align='left' class='h3' width='40%'>Note:</td><td align='left' class='h3' width='40%'><input  type='textarea' value='' name='Note'></td>
			</tr>
			<tr class='h3'>
				<td align='left' class='h3' width='40%'></td>
				<td align='left' class='h3' width='40%'><input  type='button' value='ok' id="btnAddNewRowOk">&nbsp;&nbsp;&nbsp;&nbsp;<input  type='button' value='cancel' id="btnAddNewRowCancel"></td>
			</tr>
		</table>
		
 </form>
    <div id="attendanceTable"> 
            
    	</div>
    </div>
</div>    
