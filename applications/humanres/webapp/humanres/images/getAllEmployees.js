function getAllEmployees(){
	var isChecked = $("input[name='allEmployees']").is(':checked');
	if(isChecked == true){
		window.location.href = "/humanres/control/main?allEmployees="+isChecked;
	}
}
function addSpouseName(){
	var maritalStatus = $("select[name='maritalStatus']").val();
	if(maritalStatus=="M"){
		jQuery("#spouseName").parent().parent().show();
	}else{
		jQuery("#spouseName").parent().parent().hide();
	}
}

function copyAddress(){
	var address1 = $("input[name='address1']").val();
	var address2 = $("input[name='address2']").val();
	var city = $("input[name='city']").val();
	var state = $("select[name='state']").val();
	var postalCode = $("input[name='postalCode']").val();
	var country = $("select[name='country']").val();
	$("#prsAddress1").val(address1);
	$("#prsAddress2").val(address2);
	$("#prsCity").val(city);
	$("#prsState").val(state);
	$("#prsPostalCode").val(postalCode);
	$("#prsCountry").val(country);
}

function checkPartyExistance(element){
	var employeeId =  jQuery("[name='employeeId']").val();
	$.ajax({
        type: "POST",
        url: 'checkEmployeeExistance',
        data: {employeeId : employeeId},
        dataType: 'json',
        success: function(result) {
        	 if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
        		 
        	 }else{
        		 partyId = result["partyId"];
        		 var empId = partyId;
        		 if (empId) {	
        			 alert("Given EmployeeId is Already Existing, Please enter new employee");
        		 }
        	 }
        }
	 });
	
}