jQuery(document).ready(function () {
	
	change();
	selectDept();
});

function change(){
	var date = jQuery("#AdminPunch_punchdate").val();
	var type=jQuery("#AdminPunch_PunchType").val();
	var bool=document.getElementById('Permission').value;
	if((type=="Ood") && (bool!="true")){
		jQuery("#AdminPunch_punchdate").datepicker({dateFormat:'yy-mm-dd'}).datepicker("option", { setDate:'0',maxDate:'+1y', minDate:'-0d' } );
	}else{
		jQuery("#AdminPunch_punchdate").datepicker({dateFormat:'yy-mm-dd'}).datepicker("option", { setDate:'0',maxDate:'-0d', minDate:'-1y' } );
		
	}
	
}

function selectDept(){	
	var partyIdTo=jQuery("#editPayrollAttendance_partyIdTo").val();
	var data = "partyIdTo="+partyIdTo;
	$.ajax({
        type: "POST",
        url: "getEmployeeOrgId",
        data: data,
        dataType: 'json',
        success: function(result) {
       	   partyId=result["partyId"];
       	$("#editPayrollAttendance_partyId").val(partyId);
       	 },
       error: function() {
       	 	alert(result["_ERROR_MESSAGE_"]);
       	 }
});
}


function selectDeptForBenfitDed(){	
	var partyIdTo=jQuery("#FindBenefitsOrDeductions_partyIdTo").val();
	var data = "partyIdTo="+partyIdTo;
	$.ajax({
        type: "POST",
        url: "getEmployeeOrgId",
        data: data,
        dataType: 'json',
        success: function(result) {
       	   partyId=result["partyId"];
       	$("#FindBenefitsOrDeductions_partyId").val(partyId);
       	 },
       error: function() {
       	 	alert(result["_ERROR_MESSAGE_"]);
       	 }
});
}
