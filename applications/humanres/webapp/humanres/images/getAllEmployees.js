function getAllEmployees(){
	var isChecked = $("input[name='allEmployees']").is(':checked');
	if(isChecked == true){
		window.location.href = "/humanres/control/main?allEmployees="+isChecked;
	}
}
