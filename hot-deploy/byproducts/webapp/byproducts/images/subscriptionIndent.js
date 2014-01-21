
function indentFieldsOnchange(){
	var str=jQuery("select[name='orderTypeId']").val();	
	if(str.search(/(CASH)+/g) >= 0){
		
		jQuery("input[name='route']").parent().parent().hide();
		jQuery("input[name='route']").removeClass("required");
	}else{
		jQuery("input[name='route']").parent().parent().show();
		jQuery("input[name='route']").addClass("required");
	}
}

jQuery(document).ready(function() {		
	indentFieldsOnchange();
    return false;
});	

var counter = 0;
function addFieldsOnclick(prodSize){
	var ids = counter +prodSize;
	counter = counter +1;

	var facility = jQuery("input[name='facilityId']")[0].value;
	var orderType = jQuery("#subscriptionIndent_orderTypeId")[0].value;
	if(facility != ""){
		var newRow = document.createElement('tr');
		var newCell1 = document.createElement('td');
		var newCell2 = document.createElement('td');
		var newCell3 = document.createElement('td');
		var newCell4 = document.createElement('td');
		var inputProduct = document.createElement('input');
		inputProduct.type = "input";
		inputProduct.size = 40;		
		inputProduct.name = "product_o_"+ids;
		inputProduct.value = "";
		newCell1.appendChild(inputProduct);
		
		var input1 = document.createElement('input');
		input1.type = "hidden";
		input1.name = "productId_o_"+ids;
		input1.value = "";

        $(inputProduct).autocomplete({
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
        };
    

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
		var input4 = document.createElement('input');
		input4.type = "hidden";
		input4.name = "orderTypeId_o_"+ids;
		input4.value = orderType;
		newCell4.appendChild(input4);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		newRow.appendChild(newCell3);
		newRow.appendChild(newCell4);
		var tbody = $("tbody")[1];
		tbody.appendChild(newRow);
	}
	return false;
}

function copyIndentOnclick(indentSize){
	var isChecked = $("input[name='copy']").attr('checked')?true:false;
	if(isChecked)
	{
		for (var i=0;i<indentSize;i++)
		{ 
			var lastQuantity = "lastQuantity_o_"+i;
			var actualQuantity ="quantity_o_"+i;
			var lastValue = $("input[name="+lastQuantity+"]").val();
			$("input[name="+actualQuantity+"]").val(lastValue);
		}
		document.getElementById("copy").checked=true;
	}
	else{
		for (var i=0;i<indentSize;i++)
		{ 
			var actualQuantity ="quantity_o_"+i;
			$("input[name="+actualQuantity+"]").val("");
		}
		document.getElementByName("copy").checked=false;
	}
	return false;
}