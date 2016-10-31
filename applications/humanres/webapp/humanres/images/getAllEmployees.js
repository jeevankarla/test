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