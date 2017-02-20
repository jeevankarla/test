jQuery(document).ready(function() {
	//setDateType();
});

function dateFormatType(){
	//var periodTypeId=jQuery("[name='periodTypeId']").val();
	var periodTypeId = jQuery("[name='periodTypeId']").val();
	if(periodTypeId == 'HR_MONTH' ){
		$(".groupLabel").remove();
		jQuery("[name='periodTypeId']").after("<div class='groupLabel'>Date Ex: Jan 1 to Jan 31</div>");	
	}else{
		$(".groupLabel").remove(); 
		jQuery("[name='periodTypeId']").after("<div class='groupLabel'>Date Ex: Jan 1 to Jan 31</div>"); 
	}
}

function supplyDateFormatType(){
	//var periodTypeId=jQuery("[name='periodTypeId']").val();
	var periodTypeId = jQuery("[name='periodTypeId']").val();
	if(periodTypeId == 'HR_INCARREARS' ){
		$(".groupLabel").remove();
		jQuery("[name='periodTypeId']").after("<div class='groupLabel'>Selection Dates should be in between April to March</div>");	
	}else{
		$(".groupLabel").remove(); 
	}
}