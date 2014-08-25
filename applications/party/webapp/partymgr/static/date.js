jQuery(document).ready(function () {
	
	change();
});

function change(){
	var date = jQuery("#AdminPunch_punchdate").val();
	var type=jQuery("#AdminPunch_PunchType").val();
	var bool=document.getElementById('Permission').value;
	if((type=="Ood") && (bool!="true")){
		jQuery("#AdminPunch_punchdate").datepicker({dateFormat:'yy-mm-dd'}).datepicker("option", { setDate:'0',maxDate:'+1y', minDate:'-0d' } );
	}else{
		jQuery("#AdminPunch_punchdate").datepicker({dateFormat:'yy-mm-dd'}).datepicker("option", { setDate:'0',maxDate:'-0d', minDate:'-1y' } );
		
	}
	
}
