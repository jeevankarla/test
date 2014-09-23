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
       $(':input').autotab_magic();
     
     
	 var attendanceTable = ${StringUtil.wrapString(punchListEditJSON!'[]')};
	 var shiftTypeData = ${StringUtil.wrapString(shiftJSON!'[]')};
	 	 
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
			{ "title": "Punch Type"},
			{ "title": "Note"},
			{ "title": "shiftType"}],
		"aoColumns": [
			{ "sTitle": "employeePunchId"},
			{ "sTitle": "Punch Date" },
			{ "sTitle": "Employee Id" },			
			{ "sTitle": "Employee Name" },
			{ "sTitle": "Time" },			
			{ "sTitle": "InOut" },
			{ "sTitle": "PunchType"},
			{ "sTitle": "Note"},
			{ "sTitle": "shiftType"}],
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
             sDeleteRowButtonId: "deletePunchRow",
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
	        					data: "{'IN':'IN','OUT':'OUT'}",
	        					submit:'Save changes'
	    						},
	    				   {
	        					indicator: 'Saving PunchType...',
	        					tooltip: 'Click to select PunchType',
	        					loadtext: 'loading...',
	                        	type: 'select',
	        					data: "{'Normal':'Normal','Ood':'Ood'}",
	        					submit:'Save changes'
	    						},
	    				    {
	        					indicator: 'Saving Note...',
	        					tooltip: 'Click to add note',
	                        	type: 'textarea',
	        					submit:'Save changes'
	    				  },
	    				  {
	    				    indicator: 'Saving ShiftType...',
        					tooltip: 'Click to select ShiftType',
        					loadtext: 'loading...',
                        	type: 'select',
        					data: "{'SHIFT_01':'SHIFT_01','SHIFT_02':'SHIFT_02','SHIFT_GEN':'SHIFT_GEN' ,'SHIFT_NIGHT':'SHIFT_NIGHT'}",
        					submit:'Save changes'
	    				  }
				],
				oAddNewRowButtonOptions: {	label: "Add",
									       icons: {primary:'ui-icon-plus'} 
									},
				oAddNewRowCancelButtonOptions: { label: "Cancel" },					
			    oDeleteRowButtonOptions: {	label: "Remove", 
													icons: {primary:'ui-icon-trash'}
									},
                sAddNewRowFormId : "editAdminPunch",
			    oAddNewRowFormOptions: { 	
                                        title: 'Add a new Punch',
										show: "blind",
										hide: "explode",
                                        modal: true,
                                        draggable: false,
                                        resizable: false,
                                        width: 400,
                                        height: 350
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
    datatable2.fnSort([[4,'desc']]);
    
    $('#btnAddNewRow').click(function () {
			var parts = $("#punchDate").val().split("/");
			var punchDate = new Date(parseInt(parts[2], 10),
                  parseInt(parts[1], 10) - 1,
                  parseInt(parts[0], 10));         
  			$("#punchdate").val(punchDate.getFullYear() + "-" + (punchDate.getMonth() + 1) + "-"+punchDate.getDate());
  			$("#punchdate_dis").val(punchDate.getDate() + "/" + (punchDate.getMonth() + 1) + "/" +punchDate.getFullYear());
  			
            $("#partyId").val($("select[name=employeeId]").val());
            $("#emplName").val($("#employeeId option:selected").text());
            $('#timehh').focus();
            
      });
      
    $("input[name=employeeId]").attr("required",true);
    $("#deletePunchRow").hide();
   <#if (security.hasEntityPermission("EMP_PUNCH_EDIT", "_REMOVE", session))>  
     	$("#deletePunchRow").show();
   </#if> 
   //$('#timehh').autoNumeric({mNum: 2,mDec: 0, autoTab : true ,autoTabTarget : 'timemm'}).trigger('focusout');
   //$('#timemm').autoNumeric({mNum: 2,mDec: 0, aDec :'' ,autoTab : true ,autoTabTarget : 'Note'}).trigger('focusout');
   
   
});

$('#timemm').keyup(function(){
       
         var timehh = $('#timehh').val();
         var timemm = $('#timemm').val();
        
         $('#punchtime').val(timehh+":"+timemm+":00")
     
   });
	
</script>
 
 
 	
<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Attendance Punch-In Details for ${punchDate?date}</h3>	
     </div>
    <div class="screenlet-body">
 <#if (security.hasEntityPermission("HUMANRES", "_ADMIN", session) || security.hasEntityPermission("EMP_PUNCH_EDIT", "_UPDATE", session))>
    <form id='editAdminPunch' action='adminPunch' method='post'>
     <table cellspacing=10  cellpadding=20>
        <input type='hidden' value="" name='employeePunchId'  rel="0"/>
        <input type='hidden' value="Y" name='emplName' id="emplName" rel="3"/>
        <input type='hidden'  name='punchdate' id='punchdate' size='10' />
        <input type='hidden' value="Y" name='isManual'/> 		
		<tr class='h3'>
			<td align='left' class='h3' width='20%'>Employee Id:</td>
			<td align='left' class='h3' width='30%'><input type='text' value="" name='partyId' size='5' rel="2" id='partyId' readonly/></td>
		</tr>
	    <tr class='h3'>
	       <td align='left' class='h3' width='20%'>Punch Type:</td>
	       <td align='left' class='h3' width='40%'>
	        <select name='PunchType'  allow-empty='false' id='PunchType' class='h4' rel="6">
          		<#list punchTypeList as ptl>
          		<option value='${ptl.enumId}' >${ptl.enumId?if_exists}</option>
          		</#list>          
			 </select>
			 </td></tr>
			<tr class='h3'>
			<td align='left' class='h3' width='20%'>IN/OUT:</td>
			<td align='left' class='h3' width='40%'>
			  <select name='InOut'  allow-empty='false' id='InOut' class='h4' rel="5">
          		<option value='IN' >IN</option>
          		<option value='OUT' >OUT</option>  
			 </select>
			</td>
			</tr>
			
			<tr class='h3'>
				<td align='left' class='h3' width='20%'>Shift Type:</td>
				<td align='left' class='h3' width='40%'><select name='shiftType'  allow-empty='false' id='shiftType' class='h4' rel="8">
	              		<#list shiftTypeList as stl>
	              		     <#if stl.shiftTypeId=="SHIFT_GEN">
	              		            <option value='${stl.shiftTypeId}' selected>${stl.description?if_exists}</option>
	              		         <#else>
	              		           <option value='${stl.shiftTypeId}' >${stl.description?if_exists}</option>
	              		     </#if>
	              		</#list>          
			</select></td></tr>
			<tr class='h3'>
				<td align='left' class='h3' width='20%'>Date:</td><td align='left' class='h3' width='40%'>
					<input type='text'  name='punchdate_dis' id='punchdate_dis' size='10' rel="1" readonly />
				</td></tr>
			<tr class='h3'>
				<td align='left' class='h3' width='20%'>Punch Time:</td>
				<td align='left' class='h3' width='40%'>
				<input type='hidden'  value="" name='punchtime' id='punchtime' size='10'   rel="4" />
				HH:
				<input type='text'  value="" name='timehh' id='timehh' size='4' maxlength="2" autocomplete="off" required />
				MM:
				<input type='text'  value="" name='timemm' id='timemm' size='4' maxlength="2" autocomplete="off" required />
				<em  style="font-size: large;color:red;">
				
				<b>*</b></em></td></tr>
			
			<tr class='h3'>
				<td align='left' class='h3' width='20%'>Note:</td><td align='left' class='h3' width='40%'><input  type='textarea' value='' id='Note' name='Note' rel="7" required><em  style="font-size: large;color:red;"><b>*</b></em></td>
			</tr>
			<tr class='h3'>
				<td align='left' class='h3' width='20%'></td>
				<td align='left' class='h3' width='40%'><input  type='submit' value='ok' id="btnAddNewRowOk">&nbsp;&nbsp;&nbsp;&nbsp;<input  type='button' value='cancel' id="btnAddNewRowCancel"></td>
			</tr>
		</table>
		
 </form>
 </#if>
    <div id="attendanceTable"> 
            
    	</div>
    </div>
</div>    
