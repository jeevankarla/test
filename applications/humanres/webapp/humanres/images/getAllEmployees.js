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

