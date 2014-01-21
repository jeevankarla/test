var count = 0;
function addItemsOnclick(){
	var ids = count;
	count = count +1;

	var facility = jQuery("input[name='facilityId']")[0].value;
	if(facility != ""){
		var newRow = document.createElement('tr');
		var newCell1 = document.createElement('td');
		var newCell2 = document.createElement('td');
		var newCell3 = document.createElement('td');
		/*var inputProduct = document.createElement('input');
		inputProduct.type = "input";
		inputProduct.size = 30;		
		inputProduct.name = "product_o_"+ids;
		inputProduct.value = "";*/
		/*newCell1.appendChild(inputProduct);*/
		var input1 = document.createElement('input');
		input1.type = "input";
		input1.name = "productId_o_"+ids;
		input1.value = "";
		
        /*$(inputProduct).autocomplete({
			source: productItems,
			focus: function( event, ui ) {
				$(inputProduct ).val( ui.item.label );
				return false;
			},
			select: function( event, ui ) {
				var display = ui.item.label;
				$( inputProduct ).val( display );
				$( input1 ).val( ui.item.value );
				return false;
			}        	
        })
        .data( "autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li>" )
                .data( "item.autocomplete", item )
                .append( "<a>"  + item.label + "</a>" )
                .appendTo( ul );
        };*/
    

		newCell1.appendChild(input1);
		var input2 = document.createElement('input');
		input2.type = "input";
		input2.name = "quantity_o_"+ids;
		input2.size = "4"
		input2.value = "";
		
		newCell2.appendChild(input2);
		var input3 = document.createElement('input');
		input3.type = "hidden";
		input3.name = "facilityId_o_"+ids;
		input3.value = facility;
		newCell3.appendChild(input3);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		newRow.appendChild(newCell3);
		var tbody = $("tbody")[1];
		tbody.appendChild(newRow);
	}
	return false;
}