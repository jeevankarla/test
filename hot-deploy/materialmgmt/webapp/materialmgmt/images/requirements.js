jQuery(document).ready(function() {
	
});

function raiseRequirement(element){
	var curreElem = $(element);
    var form = curreElem.parent().parent();
    var custmQuantity = $(form).find( "[name='"+"custmQuantity"+"']");
    var custmQuantity = $(custmQuantity).val();
}
function approveRequestByHOD(element){
	var curreElem = $(element);
    var form = curreElem.parent().parent();
    var quantity = $(form).find( "[name='"+"quantity"+"']");
    var quantity = $(quantity).val();
}