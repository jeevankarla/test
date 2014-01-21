function autofillProdNames(){
	$('[name="internalName"]')[0].value = $('[name="productName"]')[0].value;
	$('[name="brandName"]')[0].value = $('[name="productName"]')[0].value;
	$('[name="description"]')[0].value = $('[name="productName"]')[0].value;
}