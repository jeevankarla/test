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
